package com.yixiang.api.util.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.pojo.ActivityAd;
import com.yixiang.api.util.service.ActivityAdComponent;

@RestController
@RequestMapping("/util/ad")
public class ActivityAdController {

	@Autowired
	private ActivityAdComponent activityAdComponent;
	
	//下发banner
	@RequestMapping("/list")
	public Result getActivityAds(){
		Map<String,Object> result=activityAdComponent.queryActivityAds();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//下发banner详情
	@RequestMapping("/info")
	public Result getActivityAds(@RequestParam Integer id){
		ActivityAd result=activityAdComponent.getActivityAdDetail(id);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//编辑banner
	@RequestMapping("/edit")
	public Result editActivityAd(@ModelAttribute ActivityAd ad){
		activityAdComponent.editActivityAd(ad);
		return Result.getThreadObject();
	}
	
	//编辑banner状态
	@RequestMapping("/state")
	public Result updateActivityAdState(@RequestParam Integer id,@RequestParam Integer state){
		activityAdComponent.updateActivityAdState(id, state);
		return Result.getThreadObject();
	}
	
	//获取banner分类
	@RequestMapping("/category/list")
	public Result queryActivityCategory(){
		JSONArray result=activityAdComponent.queryActivityCategory();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
