package com.yixiang.api.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.user.service.FansInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/user/idol")
public class FansInfoController {

	@Autowired
	private FansInfoComponent fansInfoComponent;
	
	//获取关注的人
	@RequestMapping("/list")
	public Result queryMyIdols(){
		List<Map<Object,Object>> result=fansInfoComponent.queryMyIdols();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//关注用户
	@RequestMapping("/follow")
	public Result follow(@RequestParam String uuid){
		fansInfoComponent.followUser(uuid);
		return Result.getThreadObject();
	}
	
	//取注用户
	@RequestMapping("/cancel")
	public Result cancel(@RequestParam String uuid){
		fansInfoComponent.cancelFollowUser(uuid);
		return Result.getThreadObject();
	}
	
}
