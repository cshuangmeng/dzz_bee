package com.yixiang.api.charging.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.util.pojo.QueryExample;

public interface ChargingStationMapper {

	long countByExample(QueryExample example);

	int insertSelective(ChargingStation record);

	List<ChargingStation> selectByExample(QueryExample example);
	
	Map<String,Object> statFastSlowAmount(@Param("param")Map<String,Object> param);
	
	List<ChargingStation> queryChargingStations(@Param("param")Map<String,Object> param);

	int updateByExampleSelective(@Param("record") ChargingStation record, @Param("example") QueryExample example);

}