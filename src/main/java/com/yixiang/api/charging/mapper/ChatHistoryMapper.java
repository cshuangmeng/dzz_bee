package com.yixiang.api.charging.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.charging.pojo.ChatHistory;
import com.yixiang.api.util.pojo.QueryExample;

public interface ChatHistoryMapper {

	long countByExample(QueryExample example);

	int insertSelective(ChatHistory record);

	List<ChatHistory> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") ChatHistory record, @Param("example") QueryExample example);

}