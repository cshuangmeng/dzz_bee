package com.yixiang.api.util.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface AreaInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(AreaInfo record);

	List<AreaInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") AreaInfo record, @Param("example") QueryExample example);

}
