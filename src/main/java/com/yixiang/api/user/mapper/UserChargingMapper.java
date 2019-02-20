package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.UserCharging;
import com.yixiang.api.util.pojo.QueryExample;

public interface UserChargingMapper {

	long countByExample(QueryExample example);

	int insertSelective(UserCharging record);

	List<UserCharging> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") UserCharging record, @Param("example") QueryExample example);

}