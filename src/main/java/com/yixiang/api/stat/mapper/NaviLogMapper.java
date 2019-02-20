package com.yixiang.api.stat.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface NaviLogMapper {

	List<Map<String,Object>> statNaviAmountWithDate(@Param("param")Map<String,Object> param);
	Map<String,Object> statNaviAmount(@Param("param")Map<String,Object> param);
	
}
