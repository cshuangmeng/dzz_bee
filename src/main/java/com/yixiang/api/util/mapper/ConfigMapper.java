package com.yixiang.api.util.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.util.pojo.Config;
import com.yixiang.api.util.pojo.QueryExample;

public interface ConfigMapper {

	long countByExample(QueryExample example);

	int insertSelective(Config record);

	List<Config> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") Config record, @Param("example") QueryExample example);

}