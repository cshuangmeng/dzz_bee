package com.yixiang.api.stat.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.stat.service.OpenAppLogComponent;
import com.yixiang.api.stat.service.ShareLogComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/stat")
public class DataStatController {
	
	@Autowired
	private ShareLogComponent shareLogComponent;
	@Autowired
	private OpenAppLogComponent openAppLogComponent;

	//分享统计
	@RequestMapping("/share")
	public Result queryShareAmount(){
		Map<String,Object> result=shareLogComponent.queryShareAmount();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//活跃用户统计
	@RequestMapping("/active/user")
	public Result queryOpenAppAmount(){
		Map<String,Object> result=openAppLogComponent.queryOpenAppAmount();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
