package com.yixiang.api.charging.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.charging.pojo.ChargingComment;
import com.yixiang.api.util.pojo.QueryExample;

public interface ChargingCommentMapper {

	long countByExample(QueryExample example);

	int insertSelective(ChargingComment record);

	List<ChargingComment> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") ChargingComment record, @Param("example") QueryExample example);

}