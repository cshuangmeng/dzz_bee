package com.yixiang.api.charging.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yixiang.api.charging.pojo.ChargingComment;
import com.yixiang.api.charging.service.ChargingCommentComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/charging/comment")
public class ChargingCommentController {
	
	@Autowired
	private ChargingCommentComponent chargingCommentComponent;

	//加载充电桩评论列表
	@RequestMapping("/list")
	public Result queryChargingComments(@RequestParam String uuid,@RequestParam(defaultValue="1")Integer page){
		Map<String,Object> result=chargingCommentComponent.getCommentsOfCharging(uuid, page);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//发布充电桩评论
	@RequestMapping("/save")
	public Result publishNewChargingComment(@RequestParam String uuid,@ModelAttribute ChargingComment comment
			,@RequestParam(required=false)MultipartFile[] files){
		chargingCommentComponent.saveChargingComment(uuid, comment, files);
		return Result.getThreadObject();
	}
	
}
