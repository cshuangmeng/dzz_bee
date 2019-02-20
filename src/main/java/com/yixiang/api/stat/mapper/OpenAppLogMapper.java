package com.yixiang.api.stat.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface OpenAppLogMapper {

	List<Map<String,Object>> statOpenAppAmountWithDate(@Param("param")Map<String,Object> param);
	Map<String,Object> statOpenAppAmount(@Param("param")Map<String,Object> param);
	
}
