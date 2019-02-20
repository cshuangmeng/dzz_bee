package com.yixiang.api.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yixiang.api.user.service.WeiXinComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/wx/user")
public class WxaUserApiController {
	
	@Autowired
	private WeiXinComponent weiXinComponent;

	//用户登录,获取用户openId
	@RequestMapping("/login")
	public Result login(@RequestParam String code) {
		JSONObject result=weiXinComponent.login(code);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//获取用户信息
	@RequestMapping("/info")
	public Result info(@RequestParam String signature,@RequestParam String rawData
			,@RequestParam String encryptedData,@RequestParam String iv) {
		JSONObject result=weiXinComponent.info(signature, rawData, encryptedData, iv);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
