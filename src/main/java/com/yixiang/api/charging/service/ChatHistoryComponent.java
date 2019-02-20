package com.yixiang.api.charging.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.charging.mapper.ChatHistoryMapper;
import com.yixiang.api.charging.pojo.ChatHistory;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ChatHistoryComponent {
	
	@Autowired
	private ChatHistoryMapper chatHistoryMapper;
	
	//获取充电站聊天记录信息
	public ChatHistory getChatHistory(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<ChatHistory> historys=selectByExample(example);
			return historys.size()>0?historys.get(0):null;
		}
		return null;
	}
	
	//更新充电站聊天记录信息
	public int updateChatHistory(ChatHistory history){
		if(null!=history.getId()&&history.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", history.getId());
			return updateByExampleSelective(history, example);
		}
		return 0;
	}

	//计算结果集大小
	public long countByExample(QueryExample example) {
		return chatHistoryMapper.countByExample(example);
	}

	//保存
	public int insertSelective(ChatHistory record) {
		return chatHistoryMapper.insertSelective(record);
	}

	//获取结果集
	public List<ChatHistory> selectByExample(QueryExample example) {
		return chatHistoryMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(ChatHistory record, QueryExample example) {
		return chatHistoryMapper.updateByExampleSelective(record, example);
	}

}
