package com.yixiang.api.brand.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.brand.pojo.AppointmentInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface AppointmentInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(AppointmentInfo record);

	List<AppointmentInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") AppointmentInfo record, @Param("example") QueryExample example);

}
