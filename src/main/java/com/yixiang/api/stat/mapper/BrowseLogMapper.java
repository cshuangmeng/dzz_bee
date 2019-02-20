package com.yixiang.api.stat.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface BrowseLogMapper {

	List<Map<String,Object>> statBrowseAmountWithDate(@Param("param")Map<String,Object> param);
	Map<String,Object> statBrowseAmount(@Param("param")Map<String,Object> param);
	
}
