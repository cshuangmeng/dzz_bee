package com.yixiang.api.order.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.charging.service.ChargingStationComponent;
import com.yixiang.api.order.mapper.OrderInfoMapper;
import com.yixiang.api.order.pojo.CouponInfo;
import com.yixiang.api.order.pojo.OrderInfo;
import com.yixiang.api.order.pojo.TradeHistory;
import com.yixiang.api.refund.pojo.RefundInfo;
import com.yixiang.api.refund.pojo.RefundSummary;
import com.yixiang.api.refund.service.RefundInfoComponent;
import com.yixiang.api.refund.service.RefundSummaryComponent;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class OrderInfoComponent {
	
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private TradeHistoryComponent tradeHistoryComponent;
	@Autowired
	private RefundInfoComponent refundInfoComponent;
	@Autowired
	private RefundSummaryComponent refundSummaryComponent;
	@Autowired
	private ChargingStationComponent chargingStationComponent;
	@Autowired
	private CouponInfoComponent couponInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//加载订单列表
	public Map<String,Object> queryOrders(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		//筛选用户
		List<Integer> ids=null;
		if(!DataUtil.isEmpty(param.get("areaId"))||!DataUtil.isEmpty(param.get("phone"))){
			example.and().andEqualTo("area_id", param.get("areaId")).andEqualTo("phone", param.get("phone"));
			List<UserInfo> users=userInfoComponent.selectByExample(example);
			if(users.size()<=0){
				return DataUtil.mapOf("total",users.size(),"dataset",users);
			}
			ids=users.stream().map(o->o.getId()).collect(Collectors.toList());
			example.clear();
		}
		example.and().andIn("user_id", ids);
		if(!DataUtil.isEmpty(param.get("startDate"))){
			Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
			example.and().andGreaterThanOrEqualTo("create_time", startDate);
		}
		if(!DataUtil.isEmpty(param.get("endDate"))){
			Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
			endDate=DateUtils.addDays(endDate, 1);
			example.and().andLessThan("create_time", endDate);
		}
		//获取数据总条数
		Long total=countByExample(example);
		example.setOffset(limit*(page>0?page-1:0));
		example.setLimit(limit);
		example.setOrderByClause("create_time desc");
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(i->{
			UserInfo user=userInfoComponent.getUserInfo(i.getUserId(),false);
			ChargingStation station=chargingStationComponent.getChargingStation(i.getStationId());
			RefundInfo refund=null;
			if(i.getState().equals(OrderInfo.ORDER_STATE_ENUM.REFUND.getState())){
				refund=refundInfoComponent.getRefundInfo(i.getId(),RefundSummary.ORDER_TYPE_ENUM.CHARGING.getType());
			}
			return DataUtil.mapOf("id",i.getId(),"userId",user.getId(),"createTime",i.getCreateTime(),"payTime",i.getPayTime()
					,"userName",user.getUserName(),"phone",user.getPhone(),"totalPrice",i.getTotalPrice()
					,"state",i.getState(),"remark",i.getRemark(),"payPrice",i.getPayPrice(),"refundPrice",null!=refund?refund.getTotalPrice():null
					,"stationName",null!=station?station.getTitle():null,"stationProvider",null!=station?station.getProvider():null
					,"chargeState",i.getChargeState());
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//订单退款
	@Transactional
	public void refundOrder(Integer id,Float discount){
		OrderInfo order=getOrderInfo(id, true);
		//常规检查
		if(null==order){
			Result.putValue(ResponseCode.CodeEnum.ORDER_NOT_EXISTS);
			return;
		}
		if(!OrderInfo.END_CHARGING__STATES.contains(order.getState())){
			Result.putValue(ResponseCode.CodeEnum.ORDER_STATE_INCORRECT);
			return;
		}
		discount=0<=discount&&discount<=1?discount:0;
		BigDecimal refundPrice=new BigDecimal(order.getPayPrice()*(1-discount)).setScale(2, BigDecimal.ROUND_HALF_UP);
		if(refundPrice.floatValue()<=0){
			Result.putValue(ResponseCode.CodeEnum.NOT_REFUNDABLE_PRICE);
			return;
		}
		//退款到余额
		userInfoComponent.addBalance(order.getUserId(), refundPrice);
		//记录流水
		Integer tradeType=TradeHistory.TRADE_TYPE_ENUM.CHARGE_REFUND.getType();
		Integer orderType=RefundSummary.ORDER_TYPE_ENUM.CHARGING.getType();
		Integer state=TradeHistory.TRADE_STATE_ENUM.YICHULI.getState();
		TradeHistory history=tradeHistoryComponent.saveTradeHistory(order.getUserId(), order.getId(), tradeType
				, refundPrice.floatValue(), state, null);
		refundInfoComponent.saveRefundInfo(order.getUserId(), order.getId(), orderType, 0F
				, refundPrice.floatValue(), null, order.getPayWay(), null, state, history.getId());
		RefundSummary summary=refundSummaryComponent.getRefundSummary(order.getId(), orderType);
		if(null!=summary){
			summary.setBalanceRefund(summary.getBalanceRefund()+refundPrice.floatValue());
			summary.setUpdateTime(new Date());
			refundSummaryComponent.updateRefundSummary(summary);
		}
		//取消订单
		updateOrderInfoState(order.getId(), null, OrderInfo.ORDER_STATE_ENUM.REFUND.getState());
	}
	
	//操作用户充电订单状态
	@Transactional
	public void endOrderInfo(Integer orderId,Integer couponId){
		//检查订单是否可操作
		OrderInfo order=getOrderInfo(orderId, true);
		if(null==order){
			log.info("订单信息不存在,orderId="+orderId);
			Result.putValue(ResponseCode.CodeEnum.ORDER_NOT_EXISTS);
			return;
		}
		if(order.getState().equals(OrderInfo.ORDER_STATE_ENUM.NO_PAY.getState())){
			payOrderInfo(orderId, couponId);
		}else if(order.getState()<OrderInfo.ORDER_STATE_ENUM.NO_PAY.getState()){
			updateOrderInfoState(orderId, null, OrderInfo.ORDER_STATE_ENUM.CANCEL.getState());
		}
	}
	
	//订单支付
	@Transactional
	public void payOrderInfo(Integer orderId,Integer couponId){
		//检查订单是否可操作
		Map<String,Object> param=ThreadCache.getHttpData();
		OrderInfo order=getOrderInfo(orderId, true);
		if(null==order){
			log.info("订单信息不存在,orderId="+orderId);
			Result.putValue(ResponseCode.CodeEnum.ORDER_NOT_EXISTS);
			return;
		}
		UserInfo user=userInfoComponent.getUserInfo(order.getUserId(), true);
		if(!order.getState().equals(OrderInfo.ORDER_STATE_ENUM.NO_PAY.getState())){
			log.info("订单在此状态下不支持支付,orderId="+order.getId()+",state="+order.getState());
			Result.putValue(ResponseCode.CodeEnum.ORDER_STATE_INCORRECT);
			return;
		}
		//校验优惠券是否可用
		CouponInfo coupon=couponInfoComponent.getCouponInfo(couponId,true);
		if(null!=coupon){
			//重设订单总金额
			param.put("price", order.getTotalPrice());
			if(!couponInfoComponent.isCouponAvailable(user.getId(), param, coupon)){
				Result.putValue(ResponseCode.CodeEnum.COUPON_NOT_MATCH);
				return;
			}
			//计算减免后的应付金额
			float payPrice=0;
			Float maxDiscount=coupon.getMaxDiscount();
			if(coupon.getCategory().equals(CouponInfo.COUPON_CATEGORY_ENUM.CHARGING.getCategory())){
				if(coupon.getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())){
					if(null!=maxDiscount&&order.getTotalPowerPrice()*(1-coupon.getAmount())>maxDiscount.floatValue()){
						payPrice=order.getTotalPowerPrice()-maxDiscount;
					}else{
						payPrice=order.getTotalPowerPrice()*coupon.getAmount();
					}
				}else if(coupon.getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.REDUCE.getType())){
					payPrice=order.getTotalPowerPrice()-coupon.getAmount();
				}
				payPrice=payPrice>0?payPrice:0;
				payPrice+=order.getTotalServiceFee();
			}else if(coupon.getCategory().equals(CouponInfo.COUPON_CATEGORY_ENUM.SERVICEFEE.getCategory())){
				if(coupon.getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())){
					if(null!=maxDiscount&&order.getTotalServiceFee()*(1-coupon.getAmount())>maxDiscount.floatValue()){
						payPrice=order.getTotalServiceFee()-maxDiscount;
					}else{
						payPrice=order.getTotalServiceFee()*coupon.getAmount();
					}
				}else if(coupon.getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.REDUCE.getType())){
					payPrice=order.getTotalServiceFee()-coupon.getAmount();
				}
				payPrice=payPrice>0?payPrice:0;
				payPrice+=order.getTotalPowerPrice();
			}
			//四舍五入
			payPrice=new BigDecimal(payPrice).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			order.setTotalPrice(payPrice);
		}
		log.info("订单应付金额为:price="+order.getTotalPrice());
		Float balance=user.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		if(balance.floatValue()<order.getTotalPrice()){
			log.info("用户余额不足,user.balance="+balance+",order.totalPrice="+order.getTotalPrice());
			Result.putValue(ResponseCode.CodeEnum.BALANCE_NOT_ENOUGH);
			return;
		}
		//扣除账户金额
		BigDecimal interval=new BigDecimal(-order.getTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
		userInfoComponent.addBalance(user.getId(), interval);
		tradeHistoryComponent.saveTradeHistory(user.getId(), order.getId(), TradeHistory.TRADE_TYPE_ENUM.CHARGE_PAY.getType()
				, interval.floatValue(), TradeHistory.TRADE_STATE_ENUM.YICHULI.getState(), null);
		refundSummaryComponent.saveRefundSummary(user.getId(), order.getId()
				, RefundSummary.ORDER_TYPE_ENUM.CHARGING.getType(), 0F, order.getTotalPrice());
		//修改订单状态
		OrderInfo update=new OrderInfo();
		update.setId(order.getId());
		update.setState(OrderInfo.ORDER_STATE_ENUM.NO_EVALUATE.getState());
		update.setPayWay(Constants.BALANCEPAY);
		update.setPayPrice(order.getTotalPrice());
		update.setTotalBalance(update.getPayPrice());
		update.setCouponId(null!=coupon?coupon.getId():0);
		update.setPayTime(new Date());
		updateOrderInfo(update);
		//修改优惠券状态
		if(null!=coupon){
			couponInfoComponent.updateCouponState(coupon.getId(), CouponInfo.COUPON_STATE_ENUM.USED.getState(), order.getId());
		}
	}

	//查询订单
	public OrderInfo getOrderByBillId(String billId){
		if(StringUtils.isNotEmpty(billId)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("bill_id", billId);
			List<OrderInfo> result=selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//查询订单
	public OrderInfo getOrderInfo(Integer orderId,boolean lock){
		if(null!=orderId&&orderId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", orderId);
			example.setLock(lock);
			List<OrderInfo> result=selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//更新订单状态
	@Transactional
	public void updateOrderInfoState(Integer orderId,String chargeState,Integer state){
		if(null!=orderId&&orderId>0){
			if(null!=chargeState||null!=state){
				QueryExample example=new QueryExample();
				example.and().andEqualTo("id", orderId);
				OrderInfo update=new OrderInfo();
				update.setChargeState(chargeState);
				update.setState(state);
				if(null!=state&&state.equals(OrderInfo.ORDER_STATE_ENUM.REFUND.getState())){
					update.setRefundTime(new Date());
				}
				updateByExampleSelective(update, example);
			}
		}
	}
	
	//更新订单
	@Transactional
	public void updateOrderInfo(OrderInfo order){
		if(null!=order.getId()&&order.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", order.getId());
			updateByExampleSelective(order, example);
		}
	}

	//获取结果集大小
	public long countByExample(QueryExample example) {
		return orderInfoMapper.countByExample(example);
	}
	
	//统计快慢充订购
	public List<Map<String,Object>> statFastSlowOrder(Map<String,Object> param) {
		return orderInfoMapper.statFastSlowOrder(param);
	}

	//保存
	public int insertSelective(OrderInfo record) {
		return orderInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<OrderInfo> selectByExample(QueryExample example) {
		return orderInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(OrderInfo record, QueryExample example) {
		return orderInfoMapper.updateByExampleSelective(record, example);
	}
	
}
