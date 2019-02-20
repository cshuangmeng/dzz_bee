package com.yixiang.api.brand.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.brand.pojo.BrandInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface BrandInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(BrandInfo record);

	List<BrandInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") BrandInfo record, @Param("example") QueryExample example);

}