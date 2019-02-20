package com.yixiang.api.brand.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.yixiang.api.brand.pojo.BrandCar;
import com.yixiang.api.brand.service.BrandCarComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/brand/car")
public class BrandCarController {

	@Autowired
	private BrandCarComponent brandCarComponent;
	
	//车型详情
	@RequestMapping("/info")
	public Result getBrandCarInfo(@RequestParam Integer id){
		Map<String,Object> result=brandCarComponent.getBrandCarDetail(id);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//获取车型列表
	@RequestMapping("/list")
	public Result queryBrandCar(){
		Map<String,Object> result=brandCarComponent.queryBrandCar();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//统计车型访问情况
	@RequestMapping("/stat")
	public Result statBrandCar(){
		Map<String,Object> result=brandCarComponent.statBrandCar();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//获取车型列表
	@RequestMapping("/all")
	public Result queryAllCars(@RequestParam Integer brandId){
		List<Map<Object,Object>> result=brandCarComponent.queryAllCars(brandId);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//编辑车型
	@RequestMapping("/edit")
	public Result queryAllCars(@ModelAttribute BrandCar car){
		brandCarComponent.editBrandCar(car);
		return Result.getThreadObject();
	}
	
	//置顶车型
	@RequestMapping("/top")
	public Result topBrandCar(@RequestParam Integer id){
		brandCarComponent.topBrandCar(id);
		return Result.getThreadObject();
	}
	
	//更新车型状态
	@RequestMapping("/state")
	public Result updateBrandCarState(@RequestParam Integer id,@RequestParam Integer state){
		brandCarComponent.updateBrandCarState(id, state);
		return Result.getThreadObject();
	}
	
	//获取车型类别
	@RequestMapping("/category/list")
	public Result queryCarType(){
		JSONArray result=brandCarComponent.queryCarType();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
