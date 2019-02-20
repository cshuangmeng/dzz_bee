package com.yixiang.api.brand.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.brand.service.AppointmentInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/brand/appoint")
public class AppointmentInfoController {
	
	@Autowired
	private AppointmentInfoComponent appointmentInfoComponent;

	//用户预约信息列表
	@RequestMapping("/list")
	public Result queryAppointmentInfo(){
		Map<String,Object> result=appointmentInfoComponent.queryAppointmentInfo();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//删除用户预约信息
	@RequestMapping("/delete")
	public Result deleteAppointmentInfo(@RequestParam Integer id){
		appointmentInfoComponent.deleteAppointmentInfo(id);
		return Result.getThreadObject();
	}
	
}
