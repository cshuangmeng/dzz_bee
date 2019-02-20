package com.yixiang.api.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.user.pojo.MessageHistory;
import com.yixiang.api.util.pojo.QueryExample;

public interface MessageHistoryMapper {

	long countByExample(QueryExample example);

	int insertSelective(MessageHistory record);

	List<MessageHistory> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") MessageHistory record, @Param("example") QueryExample example);

}