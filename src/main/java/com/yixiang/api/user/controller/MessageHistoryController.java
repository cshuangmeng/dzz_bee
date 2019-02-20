package com.yixiang.api.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yixiang.api.user.pojo.MessageHistory;
import com.yixiang.api.user.service.MessageHistoryComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/message/history")
public class MessageHistoryController {

	@Autowired
	private MessageHistoryComponent messageHistoryComponent;
	
	//获取历史留言列表
	@RequestMapping("/list")
	public Result queryMessageHistorys(){
		Map<String,Object> result=messageHistoryComponent.queryMessageHistorys();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//获取历史留言列表
	@RequestMapping("/reply")
	public Result replyMessage(@ModelAttribute MessageHistory message,@RequestParam(required=false)MultipartFile[] files){
		messageHistoryComponent.saveMessageHistory(message, files);
		return Result.getThreadObject();
	}
	
}
