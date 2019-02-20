package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.CarInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface CarInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(CarInfo record);

	List<CarInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") CarInfo record, @Param("example") QueryExample example);

}