package com.yixiang.api.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.user.service.UserEvaluationComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/user/eval")
public class UserEvaluationController {

	@Autowired
	private UserEvaluationComponent userEvaluationComponent;
	
	//评价用户
	@RequestMapping("")
	public Result idol(@RequestParam String uuid,@RequestParam Integer stars){
		userEvaluationComponent.evaluateUser(uuid, stars);
		return Result.getThreadObject();
	}
	
}
