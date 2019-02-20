package com.yixiang.api.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.user.service.UserChargingComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/user/charging")
public class UserChargingController {

	@Autowired
	private UserChargingComponent userChargingComponent;
	
	//加载用户收藏的站点信息
	@RequestMapping("/list")
	public Result getFavoriteChargings(){
		List<Map<Object,Object>> result=userChargingComponent.getFavoriteChargings();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//收藏充电桩
	@RequestMapping("/follow")
	public Result follow(@RequestParam String uuid){
		userChargingComponent.followCharging(uuid);
		return Result.getThreadObject();
	}
	
	//取消收藏充电桩
	@RequestMapping("/cancel")
	public Result cancel(@RequestParam String uuid){
		userChargingComponent.cancelFollowCharging(uuid);
		return Result.getThreadObject();
	}
	
}
