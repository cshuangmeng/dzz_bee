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

import com.alibaba.fastjson.JSONArray;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.charging.service.ChargingStationComponent;
import com.yixiang.api.user.mapper.UserChargingMapper;
import com.yixiang.api.user.pojo.UserCharging;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class UserChargingComponent {

	@Autowired
	private UserChargingMapper userChargingMapper;
	@Autowired
	private ChargingStationComponent chargingStationComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//获取用户收藏的站点信息
	public List<Map<Object,Object>> getFavoriteChargings(){
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		QueryExample example=new QueryExample();
		example.and().andEqualTo("user_id", user.getId()).andEqualTo("state", Constants.YES);
		List<Map<Object,Object>> result=selectByExample(example).stream().map(c->{
			ChargingStation station=chargingStationComponent.getChargingStation(c.getChargingId());
			String price=null;
			if(StringUtils.isNotEmpty(station.getElectricityPrice())){
				JSONArray array=JSONArray.parseArray(station.getElectricityPrice());
				if(array.size()>0){
					price=array.getJSONObject(0).getString("price");
				}
			}
			return DataUtil.mapOf("uuid",station.getUuid(),"title",station.getTitle(),"headImg",station.getHeadImg()
					,"price",price,"serviceFee",station.getServiceFee());
		}).collect(Collectors.toList());
		return result;
	}
	
	//收藏充电桩
	public void followCharging(String uuid){
		ChargingStation followed=chargingStationComponent.getChargingStationByUUID(uuid);
		if(null==followed){
			log.info("充电桩信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.STATION_NOT_EXISTS);
			return;
		}
		Integer chargingId=followed.getId();
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		QueryExample example=new QueryExample();
		example.and().andEqualTo("charging_id", chargingId).andEqualTo("user_id", user.getId());
		List<UserCharging> fans=selectByExample(example);
		if(fans.size()>0){
			UserCharging info=fans.get(0);
			if(info.getState().equals(Constants.YES)){
				log.info("不可重复收藏,用户："+user.getId()+",被收藏充电桩："+uuid);
				Result.putValue(ResponseCode.CodeEnum.ALREADY_COLLECTED_CHARGING);
				return;
			}
			info.setState(Constants.YES);
			info.setFollowTime(new Date());
			updateUserCharging(info);
		}else{
			UserCharging info=new UserCharging();
			info.setChargingId(chargingId);
			info.setUserId(user.getId());
			info.setState(Constants.YES);
			info.setFollowTime(new Date());
			insertSelective(info);
		}
		
	}
	
	//取注收藏充电桩
	public void cancelFollowCharging(String uuid){
		ChargingStation followed=chargingStationComponent.getChargingStationByUUID(uuid);
		if(null==followed){
			log.info("充电桩信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.STATION_NOT_EXISTS);
			return;
		}
		Integer chargingId=followed.getId();
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		QueryExample example=new QueryExample();
		example.and().andEqualTo("charging_id", chargingId).andEqualTo("user_id", user.getId());
		List<UserCharging> fans=selectByExample(example);
		if(fans.size()<=0){
			log.info("尚未收藏该充电桩,用户："+user.getId()+",被收藏充电桩："+uuid);
			Result.putValue(ResponseCode.CodeEnum.COLLECT_NOT_YET);
			return;
		}
		UserCharging info=fans.get(0);
		if(info.getState().equals(Constants.NO)){
			log.info("不可重复取消收藏,用户："+user.getId()+",被收藏充电桩："+uuid);
			Result.putValue(ResponseCode.CodeEnum.ALREADY_CANCELED_CHARGING);
			return;
		}
		info.setState(Constants.NO);
		info.setCancelTime(new Date());
		updateUserCharging(info);
	}
	
	//获取用户收藏的站点信息
	public UserCharging getUserCharging(Integer userId,Integer chargingId){
		QueryExample example=new QueryExample();
		example.and().andEqualTo("user_id", userId).andEqualTo("charging_id", chargingId).andEqualTo("state", Constants.YES);
		List<UserCharging> stations=selectByExample(example);
		return stations.size()>0?stations.get(0):null;
	}
	
	//获取用户的收藏站点信息
	public UserCharging getUserCharging(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<UserCharging> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新用户的收藏站点信息
	public int updateUserCharging(UserCharging info){
		if(null!=info.getId()&&info.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", info.getId());
			return updateByExampleSelective(info, example);
		}
		return 0;
	}
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return userChargingMapper.countByExample(example);
	}

	//保存
	public int insertSelective(UserCharging record) {
		return userChargingMapper.insertSelective(record);
	}

	//获取结果集
	public List<UserCharging> selectByExample(QueryExample example) {
		return userChargingMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(UserCharging record, QueryExample example) {
		return userChargingMapper.updateByExampleSelective(record, example);
	}

}
