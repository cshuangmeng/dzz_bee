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
import com.yixiang.api.user.mapper.FansInfoMapper;
import com.yixiang.api.user.pojo.FansInfo;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class FansInfoComponent {

	@Autowired
	private FansInfoMapper fansInfoMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//获取关注的人
	public List<Map<Object,Object>> queryMyIdols(){
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		QueryExample example=new QueryExample();
		example.and().andEqualTo("follower_id", user.getId()).andEqualTo("state", Constants.YES);
		JSONObject json=JSONObject.parseObject(Redis.use().get("user_oss_config"));
		String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
		List<Map<Object,Object>> result=selectByExample(example).stream().map(f->{
			UserInfo idol=userInfoComponent.getUserInfo(f.getUserId(),false);
			return DataUtil.mapOf("headImg",StringUtils.isNotEmpty(idol.getHeadImg())?domain+idol.getHeadImg():idol.getHeadImg()
					,"userName",idol.getUserName(),"stars",idol.getStars(),"coins",idol.getCoins(),"uuid",idol.getUuid());
		}).collect(Collectors.toList());
		return result;
	}
	
	//关注用户
	public void followUser(String uuid){
		UserInfo followed=userInfoComponent.getUserInfoByUUID(uuid);
		if(null==followed){
			log.info("用户信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.USER_NOT_EXISTS);
			return;
		}
		Integer userId=followed.getId();
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		if(userId.equals(user.getId())){
			log.info("不能关注自己,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.CAN_NOT_FOLLOW_MYSELF);
			return;
		}
		QueryExample example=new QueryExample();
		example.and().andEqualTo("follower_id", user.getId()).andEqualTo("user_id", userId);
		List<FansInfo> fans=selectByExample(example);
		if(fans.size()>0){
			FansInfo info=fans.get(0);
			if(info.getState().equals(Constants.YES)){
				log.info("不可重复关注,关注者："+user.getId()+",被关注者："+userId);
				Result.putValue(ResponseCode.CodeEnum.ALREADY_FOLLOWED_USER);
				return;
			}
			info.setState(Constants.YES);
			info.setFollowTime(new Date());
			updateFansInfo(info);
		}else{
			FansInfo info=new FansInfo();
			info.setFollowerId(user.getId());
			info.setUserId(userId);
			info.setState(Constants.YES);
			info.setFollowTime(new Date());
			insertSelective(info);
		}
		
	}
	
	//取注用户
	public void cancelFollowUser(String uuid){
		UserInfo followed=userInfoComponent.getUserInfoByUUID(uuid);
		if(null==followed){
			log.info("用户信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.USER_NOT_EXISTS);
			return;
		}
		Integer userId=followed.getId();
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		QueryExample example=new QueryExample();
		example.and().andEqualTo("follower_id", user.getId()).andEqualTo("user_id", userId);
		List<FansInfo> fans=selectByExample(example);
		if(fans.size()<=0){
			log.info("尚未关注该用户,关注者："+user.getId()+",被关注者："+userId);
			Result.putValue(ResponseCode.CodeEnum.FOLLOW_NOT_YET);
			return;
		}
		FansInfo info=fans.get(0);
		if(info.getState().equals(Constants.NO)){
			log.info("不可重复取关,关注者："+user.getId()+",被关注者："+userId);
			Result.putValue(ResponseCode.CodeEnum.ALREADY_CANCELED_FOLLOW);
			return;
		}
		info.setState(Constants.NO);
		info.setCancelTime(new Date());
		updateFansInfo(info);
	}
	
	//获取关注者信息
	public FansInfo getFansInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<FansInfo> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新关注者信息
	public int updateFansInfo(FansInfo info){
		if(null!=info.getId()&&info.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", info.getId());
			return updateByExampleSelective(info, example);
		}
		return 0;
	}
	
	//获取结果集大小
	public long countByExample(QueryExample example) {
		return fansInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(FansInfo record) {
		return fansInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<FansInfo> selectByExample(QueryExample example) {
		return fansInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(FansInfo record, QueryExample example) {
		return fansInfoMapper.updateByExampleSelective(record, example);
	}

}
