package com.yixiang.api.charging.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.charging.pojo.ReportStationInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface ReportStationInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(ReportStationInfo record);

	List<ReportStationInfo> selectByExample(QueryExample example);
	
	List<Map<String,Object>> statStationReport(@Param("param")Map<String,Object> param);

	int updateByExampleSelective(@Param("record") ReportStationInfo record, @Param("example") QueryExample example);

}