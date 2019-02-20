package com.yixiang.api.charging.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.charging.service.ChargingStationComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/charging")
public class ChargingStationController {

	@Autowired
	private ChargingStationComponent chargingStationComponent;
	
	//获取个人充电桩
	@RequestMapping("/list")
	public Result queryChargingStation(){
		Map<String,Object> result=chargingStationComponent.queryChargingStation();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//统计充电桩使用情况
	@RequestMapping("/provider")
	public Result queryStationProvider(){
		JSONArray result=chargingStationComponent.queryStationProvider();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//统计充电桩使用情况
	@RequestMapping("/stat")
	public Result statChargingStation(){
		Map<String,Object> result=chargingStationComponent.statChargingStation();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//搜索附近充电桩
	@RequestMapping("/nearby")
	public Result getNearbyChargingStations(@RequestParam(defaultValue="0")BigDecimal lng
			,@RequestParam(defaultValue="0")BigDecimal lat,@RequestParam(defaultValue="1")Integer page){
		List<Map<Object,Object>> result=chargingStationComponent.queryNearbyStations(lng, lat, page);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//新建个人充电桩
	@RequestMapping("/save")
	public Result saveChargingStation(@ModelAttribute ChargingStation station,@RequestParam MultipartFile[] file){
		chargingStationComponent.saveChargingStation(station, file);
		return Result.getThreadObject();
	}
	
	//充电桩详情
	@RequestMapping("/detail")
	public Result getChargingDetail(@RequestParam Integer id,@RequestParam(defaultValue="0")BigDecimal lng
			,@RequestParam(defaultValue="0")BigDecimal lat){
		Map<String,Object> result=chargingStationComponent.getChargingDetail(id, lng, lat);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
