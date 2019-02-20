package com.yixiang.api.recharge.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yixiang.api.order.pojo.TradeHistory;
import com.yixiang.api.order.service.TradeHistoryComponent;
import com.yixiang.api.recharge.mapper.RechargeInfoMapper;
import com.yixiang.api.recharge.pojo.RechargeInfo;
import com.yixiang.api.refund.pojo.RefundSummary;
import com.yixiang.api.refund.service.RefundInfoComponent;
import com.yixiang.api.refund.service.RefundSummaryComponent;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class RechargeInfoComponent {

	@Autowired
	private RechargeInfoMapper rechargeInfoMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private RefundSummaryComponent refundSummaryComponent;
	@Autowired
	private TradeHistoryComponent tradeHistoryComponent;
	@Autowired
	private RefundInfoComponent refundInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//退款充值
	@Transactional
	public void refundRecharge(Integer id,String reason){
		RechargeInfo recharge=getRechargeInfo(id);
		if(null==recharge){
			log.info("充值记录不存在,id="+id);
			Result.putValue(ResponseCode.CodeEnum.RECHARGE_NOT_EXISTS);
			return;
		}
		if(!recharge.getState().equals(RechargeInfo.STATE_TYPE_ENUM.PAYED.getState())){
			log.info("充值记录状态不正确,id="+id+",state="+recharge.getState());
			Result.putValue(ResponseCode.CodeEnum.RECHARGE_STATE_INCORRECT);
			return;
		}
		//锁定用户信息
		UserInfo user=userInfoComponent.getUserInfo(recharge.getUserId(), true);
		//计算应退金额
		float past=recharge.getPrice()+recharge.getBonus()-user.getBalance().floatValue();
		float refundPrice=past>0?recharge.getPrice()-past:recharge.getPrice();
		refundPrice=new BigDecimal(refundPrice).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		log.info("退款用户:"+user.getId()+",应退金额为:"+user.getBalance()+",减完赠送金额后实退金额为:"+refundPrice);
		if(refundPrice<=0){
			log.info("已无可退金额,userId="+user.getId()+",refundPrice="+refundPrice);
			Result.putValue(ResponseCode.CodeEnum.BALANCE_NOT_ENOUGH);
			return;
		}
		//保存退款记录
		Integer orderType=RefundSummary.ORDER_TYPE_ENUM.RECHARGE.getType();
		Integer tradeType=TradeHistory.TRADE_TYPE_ENUM.RECHARGE_REFUND.getType();
		float refundable=0;
		float third=0;
		RefundSummary summary=refundSummaryComponent.getRefundSummary(recharge.getId(), orderType);
		if(null==summary){
			log.info("未找到退款汇总记录:RechargeInfo.id"+recharge.getId()+",已放弃");
			return;
		}
		refundable=summary.getThird()-summary.getThirdRefund();
		if(refundable>0){
			if(refundable>=refundPrice){
				summary.setThirdRefund(summary.getThirdRefund()+refundPrice);
				third=refundPrice;
				refundPrice=0;
			}else{
				summary.setThirdRefund(summary.getThirdRefund()+refundable);
				third=refundable;
				refundPrice-=refundable;
			}
		}
		if(refundPrice>0){
			log.info("充值金额未完全退足:RechargeInfo.id"+recharge.getId()+",还剩refundPrice="+refundPrice+"未退");
		}
		//扣减账户余额
		user.setBalance(user.getBalance().subtract(new BigDecimal(recharge.getPrice()+recharge.getBonus())).setScale(2, BigDecimal.ROUND_HALF_UP));
		if(user.getBalance().floatValue()<0){
			user.setBalance(new BigDecimal(0));
		}
		userInfoComponent.updateUserInfo(user);
		//记录流水
		TradeHistory history=tradeHistoryComponent.saveTradeHistory(user.getId(), recharge.getId(), tradeType, -third, null, null);
		refundInfoComponent.saveRefundInfo(user.getId(), recharge.getId(), orderType, third, 0F, recharge.getOutTradeNo()
				, recharge.getPayWay(), reason, null, history.getId());
		refundSummaryComponent.updateRefundSummary(summary);
		updateRechargeInfoState(recharge.getId(), null, RechargeInfo.STATE_TYPE_ENUM.REFUNDED.getState());
	}
	
	//获取充值请求
	public RechargeInfo getRechargeInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<RechargeInfo> result=rechargeInfoMapper.selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//获取充值请求
	public RechargeInfo getRechargeInfoByTradeNo(String tradeNo){
		if(StringUtils.isNotEmpty(tradeNo)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("trade_no", tradeNo);
			List<RechargeInfo> result=rechargeInfoMapper.selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//获取充值请求
	public List<RechargeInfo> queryRechargeInfos(Integer userId,Integer state){
		if(null!=userId&&userId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", userId).andEqualTo("state", state);
			return rechargeInfoMapper.selectByExample(example);
		}
		return null;
	}
	
	//更新充值请求状态
	@Transactional
	public void updateRechargeInfoState(Integer id,String outTradeNo,Integer state){
		if(null!=id&&id>0){
			if(null!=outTradeNo||null!=state){
				QueryExample example=new QueryExample();
				example.and().andEqualTo("id", id);
				RechargeInfo update=new RechargeInfo();
				update.setOutTradeNo(outTradeNo);
				update.setState(state);
				if(update.getState().equals(RechargeInfo.STATE_TYPE_ENUM.PAYED.getState())){//设置支付时间
					update.setPayTime(new Date());
				}else if(update.getState().equals(RechargeInfo.STATE_TYPE_ENUM.REFUNDED.getState())){//设置退款时间
					update.setRefundTime(new Date());
				}
				rechargeInfoMapper.updateByExampleSelective(update, example);
			}
		}
	}
	
}
