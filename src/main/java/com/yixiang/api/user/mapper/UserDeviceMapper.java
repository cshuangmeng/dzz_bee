package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.UserDevice;
import com.yixiang.api.util.pojo.QueryExample;

public interface UserDeviceMapper {

	long countByExample(QueryExample example);

	int insertSelective(UserDevice record);

	List<UserDevice> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") UserDevice record, @Param("example") QueryExample example);

}