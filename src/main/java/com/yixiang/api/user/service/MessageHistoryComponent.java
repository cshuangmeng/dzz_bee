package com.yixiang.api.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.user.mapper.MessageHistoryMapper;
import com.yixiang.api.user.pojo.MessageHistory;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.UploadResourceComponent;

@Service
public class MessageHistoryComponent {
	
	@Autowired
	private MessageHistoryMapper messageHistoryMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private UploadResourceComponent uploadResourceComponent;
	
	//发布留言
	public void saveMessageHistory(MessageHistory message,MultipartFile[] files){
		message.setMedia(uploadResourceComponent.uploadMedia("user", files));
		message.setCreateTime(new Date());
		message.setSource(MessageHistory.MESSAGE_SOURCE_ENUM.KEFU.getSource());
		message.setTopId(message.getRefId());
		message.setUserId(getMessageHistory(message.getRefId()).getUserId());
		insertSelective(message);
	}
	
	//加载留言历史列表
	public Map<String,Object> queryMessageHistorys(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		//筛选用户
		List<Integer> ids=null;
		if(!DataUtil.isEmpty(param.get("areaId"))||!DataUtil.isEmpty(param.get("phone"))){
			example.and().andEqualTo("area_id", param.get("areaId")).andEqualTo("phone", param.get("phone"));
			List<UserInfo> users=userInfoComponent.selectByExample(example);
			if(users.size()<=0){
				return DataUtil.mapOf("total",users.size(),"dataset",users);
			}
			ids=users.stream().map(o->o.getId()).collect(Collectors.toList());
			example.clear();
		}
		example.and().andIn("user_id", ids).andNotIn("state", Arrays.asList(MessageHistory.MESSAGE_STATE_ENUM.XITONGSHANCHU.getState()
				,MessageHistory.MESSAGE_STATE_ENUM.GERENSHANCHU.getState())).andEqualTo("source", MessageHistory.MESSAGE_SOURCE_ENUM.USER.getSource());
		if(!DataUtil.isEmpty(param.get("startDate"))){
			Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
			example.and().andGreaterThanOrEqualTo("create_time", startDate);
		}
		if(!DataUtil.isEmpty(param.get("endDate"))){
			Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
			endDate=DateUtils.addDays(endDate, 1);
			example.and().andLessThan("create_time", endDate);
		}
		//获取数据总条数
		Long total=countByExample(example);
		example.setOffset(limit*(page>0?page-1:0));
		example.setLimit(limit);
		example.setOrderByClause("create_time desc");
		JSONObject oss=JSONObject.parseObject(Redis.use().get("user_oss_config"));
		List<Integer> noReadMessageIds=new ArrayList<>();
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(o->{
			//用户信息
			UserInfo user=userInfoComponent.getUserInfo(o.getUserId(), false);
			//客服回复
			List<Map<Object,Object>> replys=getMessageHistoryByRefId(o.getId(), MessageHistory.MESSAGE_SOURCE_ENUM.KEFU.getSource())
					.stream().map(i->DataUtil.mapOf("content",i.getContent(),"media",OSSUtil.joinOSSFileUrl(oss, o.getMedia().split(","))))
					.collect(Collectors.toList());
			if(o.getIsRead().equals(Constants.NO)){
				noReadMessageIds.add(o.getId());
			}
			return DataUtil.mapOf("createTime",o.getCreateTime(),"isRead",o.getIsRead(),"state",o.getState(),"content",o.getContent()
					,"media",OSSUtil.joinOSSFileUrl(oss, o.getMedia().split(",")),"userName",user.getUserName(),"phone",user.getPhone()
					,"userId",user.getId(),"id",o.getId(),"headImg",OSSUtil.joinOSSFileUrl(oss, user.getHeadImg()),"replys",replys);
		}).collect(Collectors.toList());
		//将本页未读消息重置为已读
		if(noReadMessageIds.size()>0){
			example.clear();
			example.and().andIn("id", noReadMessageIds);
			MessageHistory update=new MessageHistory();
			update.setIsRead(Constants.YES);
			updateByExampleSelective(update, example);
		}
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//获取管家留言信息
	public MessageHistory getMessageHistory(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<MessageHistory> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取管家回复信息
	public List<MessageHistory> getMessageHistoryByRefId(Integer refId,Integer source){
		if(null!=refId&&refId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("ref_id", refId).andEqualTo("source", source);
			return selectByExample(example);
		}
		return null;
	}
	
	//更新管家留言信息
	public int updateMessageHistory(MessageHistory info){
		if(null!=info.getId()&&info.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", info.getId());
			return updateByExampleSelective(info, example);
		}
		return 0;
	}

	//计算结果集大小
	public long countByExample(QueryExample example) {
		return messageHistoryMapper.countByExample(example);
	}

	//保存
	public int insertSelective(MessageHistory record) {
		return messageHistoryMapper.insertSelective(record);
	}

	//获取结果集
	public List<MessageHistory> selectByExample(QueryExample example) {
		return messageHistoryMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(MessageHistory record, QueryExample example) {
		return messageHistoryMapper.updateByExampleSelective(record, example);
	}

}
