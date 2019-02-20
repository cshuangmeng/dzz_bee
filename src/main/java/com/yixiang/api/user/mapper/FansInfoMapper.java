package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.FansInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface FansInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(FansInfo record);

	List<FansInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") FansInfo record, @Param("example") QueryExample example);

}