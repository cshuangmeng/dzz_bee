package com.yixiang.api.user.controller;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/user")
public class UserInfoController {

	@Autowired
	private UserInfoComponent userInfoComponent;
	
	//获取验证码
	@RequestMapping("/sendVerifyCode")
	public Result sendVerifyCode(){
		userInfoComponent.sendCheckCode();
		return Result.getThreadObject();
	}
	
	//用户登录
	@RequestMapping("/login")
	public Result login(@RequestParam String phone,@RequestParam String pwd){
		Map<String,Object> result=userInfoComponent.login(phone,pwd);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//用户未登录
	@RequestMapping("/logout")
	public Result unauth(){
		SecurityUtils.getSubject().logout();
		SecurityUtils.getSubject().getSession().stop();
		return Result.getThreadObject();
	}
	
	//获取用户信息列表
	@RequestMapping("/list")
	public Result info(){
		Map<String,Object> result=userInfoComponent.queryUserInfo();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//清空用户账户余额
	@RequestMapping("/empty/account")
	public Result emptyAccount(@RequestParam Integer id){
		userInfoComponent.emptyAccount(id);
		return Result.getThreadObject();
	}
	
}
