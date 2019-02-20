package com.yixiang.api.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.user.pojo.CarInfo;
import com.yixiang.api.user.service.CarInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/user/car")
public class CarInfoController {

	@Autowired
	private CarInfoComponent carInfoComponent;
	
	//保存用户车辆信息
	@RequestMapping("/save")
	public Result save(@ModelAttribute CarInfo car){
		carInfoComponent.saveMyCarInfo(car);
		return Result.getThreadObject();
	}
	
	//用户车辆信息
	@RequestMapping("/my")
	public Result myCars(@ModelAttribute CarInfo car){
		List<Map<Object,Object>> result=carInfoComponent.getMyCarInfo();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
