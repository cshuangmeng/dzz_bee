package com.yixiang.api.user.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.user.mapper.UserDeviceMapper;
import com.yixiang.api.user.pojo.UserDevice;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class UserDeviceComponent {

	@Autowired
	private UserDeviceMapper userDeviceMapper;
	
	//获取用户的设备信息
	public UserDevice getUserDevice(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<UserDevice> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取用户的设备信息
	public UserDevice getUserDeviceByImeiAndSystem(Integer userId,String imei,String system){
		if(null!=userId&&userId>0&&StringUtils.isNotEmpty(imei)&&StringUtils.isNotEmpty(system)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", userId).andEqualTo("imei", imei).andEqualTo("system", system);
			List<UserDevice> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取用户的设备信息
	public UserDevice getUserDeviceByOpenId(Integer userId,String openId){
		if(null!=userId&&userId>0&&StringUtils.isNotEmpty(openId)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", userId).andEqualTo("wx_open_id", openId);
			List<UserDevice> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新用户的设备信息
	public int updateUserDevice(UserDevice device){
		if(null!=device.getId()&&device.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", device.getId());
			return updateByExampleSelective(device, example);
		}
		return 0;
	}
	
	//获取结果集大小
	public long countByExample(QueryExample example) {
		return userDeviceMapper.countByExample(example);
	}

	//保存
	public int insertSelective(UserDevice record) {
		return userDeviceMapper.insertSelective(record);
	}

	//获取结果集
	public List<UserDevice> selectByExample(QueryExample example) {
		return userDeviceMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(UserDevice record, QueryExample example) {
		return userDeviceMapper.updateByExampleSelective(record, example);
	}

}
