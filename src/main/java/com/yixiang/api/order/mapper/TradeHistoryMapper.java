package com.yixiang.api.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.order.pojo.TradeHistory;
import com.yixiang.api.util.pojo.QueryExample;

public interface TradeHistoryMapper {

	long countByExample(QueryExample example);

	int insertSelective(TradeHistory record);

	List<TradeHistory> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") TradeHistory record, @Param("example") QueryExample example);
	
	List<Map<String,Object>> statRechargeAndChargeAmountWithDate(@Param("param")Map<String,Object> param);
	
	Map<String,Object> statRechargeAndChargeAmount(@Param("param")Map<String,Object> param);

}