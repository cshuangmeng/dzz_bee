package com.yixiang.api.brand.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.brand.service.BrandInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/brand/info")
public class BrandInfoController {

	@Autowired
	private BrandInfoComponent brandInfoComponent;
	
	//下发热门品牌
	@RequestMapping("/hot")
	public Result queryHotBrands(){
		List<Map<Object,Object>> result=brandInfoComponent.queryHotBrands();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//下发所有品牌
	@RequestMapping("/list")
	public Result queryAllBrands(){
		List<Map<Object,Object>> result=brandInfoComponent.queryAllBrands();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
