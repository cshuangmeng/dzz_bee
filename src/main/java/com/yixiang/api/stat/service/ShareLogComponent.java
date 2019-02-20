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
import com.yixiang.api.stat.mapper.ShareLogMapper;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;

@Service
public class ShareLogComponent {

	@Autowired
	private ShareLogMapper shareLogMapper;
	
	//统计指定时间每天的分享次数
	public Map<String,Object> queryShareAmount(){
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
		List<Map<String,Object>> result=statShareAmountWithDate(param);
		for(int i=0;i<limit&&!DateUtils.isSameDay(startDate, endDate);i++){
			String date=DateUtil.toString(startDate, DatePattern.COMMON_DATE);
			Optional<Map<String,Object>> op=result.stream().filter(o->date.equals(o.get("ot").toString())).findAny();
			dataset.add(DataUtil.mapOf("date",date,"station",op.isPresent()?op.get().get("station"):0
					,"article",op.isPresent()?op.get().get("article"):0,"newCar",op.isPresent()?op.get().get("newCar"):0
					,"oldCar",op.isPresent()?op.get().get("oldCar"):0));
			startDate=DateUtils.addDays(startDate, 1);
		}
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}

	//按天统计分享次数
	public List<Map<String, Object>> statShareAmountWithDate(Map<String, Object> param) {
		return shareLogMapper.statShareAmountWithDate(param);
	}

	//统计分享次数
	public Map<String, Object> statShareAmount(Map<String, Object> param) {
		return shareLogMapper.statShareAmount(param);
	}
	
}
