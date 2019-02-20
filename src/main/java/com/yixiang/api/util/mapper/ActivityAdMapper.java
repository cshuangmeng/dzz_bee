package com.yixiang.api.util.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.util.pojo.ActivityAd;
import com.yixiang.api.util.pojo.QueryExample;

public interface ActivityAdMapper {

	long countByExample(QueryExample example);

	int insertSelective(ActivityAd record);

	List<ActivityAd> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") ActivityAd record, @Param("example") QueryExample example);

}