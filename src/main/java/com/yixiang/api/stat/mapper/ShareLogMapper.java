package com.yixiang.api.stat.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ShareLogMapper {
	
	List<Map<String,Object>> statShareAmountWithDate(@Param("param")Map<String,Object> param);
	Map<String,Object> statShareAmount(@Param("param")Map<String,Object> param);
	
}
