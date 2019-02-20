package com.yixiang.api.user.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.user.mapper.CarInfoMapper;
import com.yixiang.api.user.pojo.CarInfo;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class CarInfoComponent {
	
	@Autowired
	private CarInfoMapper carInfoMapper;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//保存用户车辆信息
	public void saveMyCarInfo(CarInfo car){
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		//检查用户车辆是否超上限
		String value=Redis.use().get("user_car_config");
		if(StringUtils.isNotEmpty(value)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", user.getId()).andEqualTo("state", CarInfo.CAR_STATE_ENUM.NORMAL.getState());
			if(selectByExample(example).size()>=JSONObject.parseObject(value).getInteger("max")){
				log.info("用户个人车辆信息已超限额,userId="+user.getId());
				Result.putValue(ResponseCode.CodeEnum.CAR_EXCEED_MAX);
				return;
			}
		}
		car.setUserId(user.getId());
		car.setCreateTime(new Date());
		insertSelective(car);
	}
	
	//用户的车辆信息
	public List<Map<Object,Object>> getMyCarInfo(){
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		List<Map<Object,Object>> result=queryCarInfosByUserId(user.getId()).stream().map(c->{
			return DataUtil.mapOf("id",c.getId(),"brand",c.getBrand(),"car",c.getCar(),"number",c.getNumber());
		}).collect(Collectors.toList());
		return result;
	}
	
	//获取我的车辆信息
	public CarInfo getCarInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<CarInfo> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取我的车辆信息
	public List<CarInfo> queryCarInfosByUserId(Integer userId){
		if(null!=userId&&userId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", userId).andEqualTo("state", CarInfo.CAR_STATE_ENUM.NORMAL.getState());
			return selectByExample(example);
		}
		return null;
	}
	
	//更新我的车辆信息
	public int updateCarInfo(CarInfo car){
		if(null!=car.getId()&&car.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", car.getId());
			return updateByExampleSelective(car, example);
		}
		return 0;
	}

	//计算结果集大小
	public long countByExample(QueryExample example) {
		return carInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(CarInfo record) {
		return carInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<CarInfo> selectByExample(QueryExample example) {
		return carInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(CarInfo record, QueryExample example) {
		return carInfoMapper.updateByExampleSelective(record, example);
	}

}
