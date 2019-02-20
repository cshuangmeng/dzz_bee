package com.yixiang.api.user.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.user.mapper.UserEvaluationMapper;
import com.yixiang.api.user.pojo.UserEvaluation;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class UserEvaluationComponent {
	
	@Autowired
	private UserEvaluationMapper userEvaluationMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//评价用户
	public void evaluateUser(String uuid,Integer stars){
		UserInfo followed=userInfoComponent.getUserInfoByUUID(uuid);
		if(null==followed){
			log.info("用户信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.USER_NOT_EXISTS);
			return;
		}
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		Integer toUserId=followed.getId();
		Integer fromUserId=user.getId();
		QueryExample example=new QueryExample();
		example.and().andEqualTo("from_user_id",fromUserId).andEqualTo("to_user_id",toUserId);
		List<UserEvaluation> evaluations=selectByExample(example);
		if(evaluations.size()>0){
			log.info("已评论过该用户,fromUserId="+fromUserId+",toUserId="+toUserId);
			Result.putValue(ResponseCode.CodeEnum.ALREADY_EVALUATED_USER);
			return;
		}
		UserEvaluation evaluation=new UserEvaluation();
		evaluation.setFromUserId(fromUserId);
		evaluation.setToUserId(toUserId);
		evaluation.setStars(stars);
		evaluation.setCreateTime(new Date());
		insertSelective(evaluation);
	}
	
	//获取用户的评价信息
	public UserEvaluation getUserEvaluation(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<UserEvaluation> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新用户的评价信息
	public int updateUserEvaluation(UserEvaluation info){
		if(null!=info.getId()&&info.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", info.getId());
			return updateByExampleSelective(info, example);
		}
		return 0;
	}

	//计算结果集大小
	public long countByExample(QueryExample example) {
		return userEvaluationMapper.countByExample(example);
	}

	//保存
	public int insertSelective(UserEvaluation record) {
		return userEvaluationMapper.insertSelective(record);
	}

	//获取结果集
	public List<UserEvaluation> selectByExample(QueryExample example) {
		return userEvaluationMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(UserEvaluation record, QueryExample example) {
		return userEvaluationMapper.updateByExampleSelective(record, example);
	}

}
