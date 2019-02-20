package com.yixiang.api.charging.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.charging.mapper.ChargingCommentMapper;
import com.yixiang.api.charging.pojo.ChargingComment;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ChargingCommentComponent {
	
	@Autowired
	private ChargingCommentMapper chargingCommentMapper;
	@Autowired
	private ChargingStationComponent chargingStationComponent;
	@Autowired
	private UserInfoComponent userInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//发布新评论
	public void saveChargingComment(String uuid,ChargingComment comment,MultipartFile[] files){
		ChargingStation station=chargingStationComponent.getChargingStationByUUID(uuid);
		if(null==station||!station.getState().equals(ChargingStation.STATION_STATE_ENUM.ENABLED.getState())){
			log.info("充电桩信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.STATION_NOT_EXISTS);
			return;
		}
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		StringBuilder names=new StringBuilder();
		log.info("获取到多媒体资源个数,files="+files);
		//多媒体资源
		if(null!=files){
			for(MultipartFile file:files){
				if(null!=file&&!file.isEmpty()){
					String saveName=new Date().getTime()+DataUtil.createNums(7);
					saveName+=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
					try {
						if(OSSUtil.uploadFileToOSS(file.getInputStream(), saveName, "charging_oss_config")){
							names.append(names.length()>0?","+saveName:saveName);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		comment.setUserId(user.getId());
		comment.setChargingId(station.getId());
		comment.setMedia(names.toString());
		comment.setCreateTime(new Date());
		insertSelective(comment);
	}
	
	//加载充电桩评论列表
	public Map<String,Object> getCommentsOfCharging(String uuid,Integer page){
		ChargingStation station=chargingStationComponent.getChargingStationByUUID(uuid);
		List<Map<Object,Object>> comments=null;
		Long total=null;
		if(null!=station&&station.getState().equals(ChargingStation.STATION_STATE_ENUM.ENABLED.getState())){
			JSONObject json=JSONObject.parseObject(Redis.use().get("user_oss_config"));
			String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
			json=JSONObject.parseObject(Redis.use().get("charging_comment_list_config"));
			//计算评论总条数
			QueryExample example=new QueryExample();
			example.and().andEqualTo("charging_id", station.getId()).andEqualTo("state", ChargingComment.COMMENT_STATE_ENUM.TONGGUO.getState());
			total=getChargingCommentTotal(station.getId());
			//拼装评论数据
			example.setOffset((page-1)*json.getInteger("size"));
			example.setLimit(json.getInteger("size"));
			example.setOrderByClause("create_time desc");
			json=JSONObject.parseObject(Redis.use().get("charging_oss_config"));
			String domain1=json.getString("domain")+"/"+json.getString("imgDir")+"/";
			String domain2=json.getString("domain")+"/"+json.getString("videoDir")+"/";
			comments=selectByExample(example).stream().map(c->{
				UserInfo user=userInfoComponent.getUserInfo(c.getUserId(),false);
				//标签
				List<String> tags=null;
				if(StringUtils.isNotEmpty(c.getTags())){
					tags=Arrays.asList(c.getTags().split(",")).stream().filter(t->StringUtils.isNotEmpty(t)).collect(Collectors.toList());
				}
				//多媒体
				List<String> media=null;
				if(StringUtils.isNotEmpty(c.getMedia())){
					media=Arrays.asList(c.getMedia().split(",")).stream().filter(m->StringUtils.isNotEmpty(m))
							.map(m->String.valueOf(DataUtil.isImg(m)?domain1+m:DataUtil.isVideo(m)?domain2+m:m)).collect(Collectors.toList());
				}
				return DataUtil.mapOf("headImg",StringUtils.isNotEmpty(user.getHeadImg())?domain+user.getHeadImg():user.getHeadImg()
						,"userName",user.getUserName(),"content",c.getContent(),"tags",tags,"media",media,"createTime"
						,DateUtil.toString(c.getCreateTime(), DatePattern.COMMON_DATE_AND_TIME_WITHOUT_YEAR_AND_SECOND));
			}).collect(Collectors.toList());
		}
		return DataUtil.mapOf("total",total,"comments",comments); 
	}
	
	//获取充电站评论信息
	public ChargingComment getChargingComment(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<ChargingComment> comments=selectByExample(example);
			return comments.size()>0?comments.get(0):null;
		}
		return null;
	}
	
	//更新充电站评论信息
	public int updateChargingComment(ChargingComment comment){
		if(null!=comment.getId()&&comment.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", comment.getId());
			return updateByExampleSelective(comment, example);
		}
		return 0;
	}
	
	//获取充电桩评论总数
	public long getChargingCommentTotal(Integer chargingId) {
		if(null!=chargingId&&chargingId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("charging_id", chargingId).andEqualTo("state", ChargingComment.COMMENT_STATE_ENUM.TONGGUO.getState());
			return countByExample(example);
		}
		return 0;
	}

	//结算结果集大小
	public long countByExample(QueryExample example) {
		return chargingCommentMapper.countByExample(example);
	}

	//保存
	public int insertSelective(ChargingComment record) {
		return chargingCommentMapper.insertSelective(record);
	}

	//获取结果集
	public List<ChargingComment> selectByExample(QueryExample example) {
		return chargingCommentMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(ChargingComment record, QueryExample example) {
		return chargingCommentMapper.updateByExampleSelective(record, example);
	}

}
