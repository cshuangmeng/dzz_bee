package com.yixiang.api.brand.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.brand.pojo.BrandCar;
import com.yixiang.api.util.pojo.QueryExample;

public interface BrandCarMapper {

	long countByExample(QueryExample example);

	int insertSelective(BrandCar record);

	List<BrandCar> selectByExample(QueryExample example);
	
	List<Map<String,Object>> statBrandCar(@Param("param")Map<String,Object> param);

	int updateByExampleSelective(@Param("record") BrandCar record, @Param("example") QueryExample example);

}