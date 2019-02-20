package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface UserInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(UserInfo record);

	List<UserInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") UserInfo record, @Param("example") QueryExample example);

}