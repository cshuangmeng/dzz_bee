package com.yixiang.api.order.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.order.mapper.TradeHistoryMapper;
import com.yixiang.api.order.pojo.TradeHistory;
import com.yixiang.api.recharge.pojo.RechargeInfo;
import com.yixiang.api.recharge.service.RechargeInfoComponent;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.AreaInfoComponent;

@Service
public class TradeHistoryComponent {
	
	@Autowired
	private TradeHistoryMapper tradeHistoryMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private RechargeInfoComponent rechargeInfoComponent;
	@Autowired
	private AreaInfoComponent areaInfoComponent;
	
	//保存交易记录
	@Transactional
	public TradeHistory saveTradeHistory(Integer userId,Integer tradeId,Integer tradeType
			,Float amount,Integer state,String remark){
		UserInfo user=userInfoComponent.getUserInfo(userId, false);
		JSONObject json=JSONObject.parseObject(Redis.use().get("trade_type_config")).getJSONObject("trade_type_"+tradeType);
		TradeHistory trade=new TradeHistory();
		trade.setAmount(new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
		trade.setBalance(user.getBalance());
		trade.setCreateTime(new Date());
		trade.setRemark(remark);
		trade.setState(state);
		trade.setTitle(json.getString("title"));
		trade.setTradeId(tradeId);
		trade.setTradeNo(DateUtil.toString(new Date(), DatePattern.TIMESTAMP_WITH_MILLISECOND)+DataUtil.createNums(3));
		trade.setTradeType(tradeType);
		trade.setUserId(user.getId());
		tradeHistoryMapper.insertSelective(trade);
		return trade;
	}
	
	//获取用户充值记录列表
	public Map<String,Object> queryRechargeTradeHistory(){
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
		example.and().andIn("trade_type", Arrays.asList(TradeHistory.TRADE_TYPE_ENUM.RECHARGE.getType()
			,TradeHistory.TRADE_TYPE_ENUM.RECHARGE_REFUND.getType(),TradeHistory.TRADE_TYPE_ENUM.JFQ_RECHARGE.getType()))
			.andNotEqualTo("state", TradeHistory.TRADE_STATE_ENUM.YISHANCHU.getState()).andIn("user_id", ids);
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
		JSONObject oss=JSONObject.parseObject(Redis.use().get("user_oss_config"));
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(o->{
			//用户信息
			UserInfo user=userInfoComponent.getUserInfo(o.getUserId(), false);
			//充值请求信息
			RechargeInfo recharge=rechargeInfoComponent.getRechargeInfo(o.getTradeId());
			//用户所在地区信息
			AreaInfo city=areaInfoComponent.getAreaInfo(user.getAreaId());
			AreaInfo province=null;
			if(null!=city){
				province=areaInfoComponent.getAreaInfo(city.getParentId());
			}
			return DataUtil.mapOf("id",o.getId(),"userId",o.getUserId(),"phone",user.getPhone(),"userName",user.getUserName()
					,"headImg",OSSUtil.joinOSSFileUrl(oss, user.getHeadImg()),"amount",o.getAmount(),"balance",o.getBalance()
					,"outTradeNo",null!=recharge?recharge.getOutTradeNo():null,"tradeNo",null!=recharge?recharge.getTradeNo():null
					,"payWay",null!=recharge?recharge.getPayWay():null,"province",null!=province?province.getAreaName():null
					,"city",null!=city?city.getAreaName():null,"createTime",o.getCreateTime(),"tradeType",o.getTradeType()
					,"state",o.getState(),"tradeId",o.getTradeId(),"remark",o.getRemark());
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//按天统计充值、充电数据
	public List<Map<String,Object>> statRechargeAndChargeAmountWithDate(Map<String,Object> param){
		return tradeHistoryMapper.statRechargeAndChargeAmountWithDate(param);
	}
	
	//统计充值、充电次数
	public Map<String,Object> statRechargeAndChargeAmount(Map<String,Object> param){
		return tradeHistoryMapper.statRechargeAndChargeAmount(param);
	}
	
	//获取交易交易记录信息
	public TradeHistory getTradeHistory(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<TradeHistory> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新交易交易记录信息
	public int updateTradeHistory(TradeHistory history){
		if(null!=history.getId()&&history.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", history.getId());
			return updateByExampleSelective(history, example);
		}
		return 0;
	}

	//获取结果集大小
	public long countByExample(QueryExample example) {
		return tradeHistoryMapper.countByExample(example);
	}

	//保存
	public int insertSelective(TradeHistory record) {
		return tradeHistoryMapper.insertSelective(record);
	}

	//获取结果集
	public List<TradeHistory> selectByExample(QueryExample example) {
		return tradeHistoryMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(TradeHistory record, QueryExample example) {
		return tradeHistoryMapper.updateByExampleSelective(record, example);
	}

}
