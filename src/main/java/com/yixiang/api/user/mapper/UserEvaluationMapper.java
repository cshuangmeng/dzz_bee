package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.UserEvaluation;
import com.yixiang.api.util.pojo.QueryExample;

public interface UserEvaluationMapper {

	long countByExample(QueryExample example);

	int insertSelective(UserEvaluation record);

	List<UserEvaluation> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") UserEvaluation record, @Param("example") QueryExample example);

}