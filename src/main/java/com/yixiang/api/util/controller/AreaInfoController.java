package com.yixiang.api.util.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.util.Result;
import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.service.AreaInfoComponent;

@RestController
@RequestMapping("/util/area")
public class AreaInfoController {

	@Autowired
	private AreaInfoComponent areaInfoComponent;
	
	//获取地区信息
	@RequestMapping("/list")
	public Result get(@RequestParam(defaultValue="0")Integer parentId){
		List<AreaInfo> result=areaInfoComponent.queryAreaInfos(parentId);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
