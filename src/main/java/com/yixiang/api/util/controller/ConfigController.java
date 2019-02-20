package com.yixiang.api.util.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.util.Result;
import com.yixiang.api.util.service.ConfigComponent;

@RestController
@RequestMapping("/util/config")
public class ConfigController {

	@Autowired
	private ConfigComponent configComponent;
	
	//刷新配置
	@RequestMapping("/refresh")
	public Result refresh(){
		configComponent.syncConfigToRedis();
		return Result.getThreadObject();
	}
	
}
