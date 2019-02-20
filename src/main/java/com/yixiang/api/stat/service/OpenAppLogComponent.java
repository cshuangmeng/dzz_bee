package com.yixiang.api.stat.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.yixiang.api.order.service.TradeHistoryComponent;
import com.yixiang.api.stat.mapper.OpenAppLogMapper;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;

@Service
public class OpenAppLogComponent {

	@Autowired
	private OpenAppLogMapper openAppLogMapper;
	@Autowired
	private NaviLogComponent naviLogComponent;
	@Autowired
	private TradeHistoryComponent tradeHistoryComponent;
	
	//活跃用户统计
	public Map<String,Object> queryOpenAppAmount(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		if(DataUtil.isEmpty(param.get("startDate"))||DataUtil.isEmpty(param.get("endDate"))){
			Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
			return null;
		}
		//获取数据总条数
		Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
		Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
		if(startDate.compareTo(endDate)>0){
			Result.putValue(ResponseCode.CodeEnum.PARAM_INCORRECT);
			return null;
		}
		endDate=DateUtils.addDays(endDate, 1);
		Integer total=(int)((DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH).getTime()
				-DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH).getTime())/(1000*3600*24));
		startDate=DateUtils.addDays(startDate, limit*(page>0?page-1:0));
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		List<Map<String,Object>> dataset=new ArrayList<>();
		//统计打开每天打开APP的用户
		List<Map<String,Object>> users=statOpenAppAmountWithDate(param);
		//统计每天导航的用户
		List<Map<String,Object>> navis=naviLogComponent.statNaviAmountWithDate(param);
		//统计每天充值、充电的用户
		List<Map<String,Object>> recharges=tradeHistoryComponent.statRechargeAndChargeAmountWithDate(param);
		for(int i=0;i<limit&&!DateUtils.isSameDay(startDate, endDate);i++){
			String date=DateUtil.toString(startDate, DatePattern.COMMON_DATE);
			Optional<Map<String,Object>> uop=users.stream().filter(o->date.equals(o.get("ot").toString())).findAny();
			Optional<Map<String,Object>> nop=navis.stream().filter(o->date.equals(o.get("ot").toString())).findAny();
			Optional<Map<String,Object>> rop=recharges.stream().filter(o->date.equals(o.get("ot").toString())).findAny();
			dataset.add(DataUtil.mapOf("date",date,"openUser",uop.isPresent()?uop.get().get("uv"):0
					,"naviUser",nop.isPresent()?nop.get().get("pv"):0,"rechargeUser",rop.isPresent()?rop.get().get("rechargeUV"):0
					,"chargeUser",rop.isPresent()?rop.get().get("chargeUV"):0));
			startDate=DateUtils.addDays(startDate, 1);
		}
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//按天统计打开APP次数
	public List<Map<String, Object>> statOpenAppAmountWithDate(Map<String, Object> param) {
		return openAppLogMapper.statOpenAppAmountWithDate(param);
	}
	
	//统计打开APP次数
	public Map<String, Object> statOpenAppAmount(Map<String, Object> param) {
		return openAppLogMapper.statOpenAppAmount(param);
	}
	
}
