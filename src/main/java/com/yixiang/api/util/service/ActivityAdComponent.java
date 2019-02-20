package com.yixiang.api.util.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.mapper.ActivityAdMapper;
import com.yixiang.api.util.pojo.ActivityAd;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ActivityAdComponent {
	
	@Autowired
	private ActivityAdMapper activityAdMapper;
	
	//编辑活动广告
	public void editActivityAd(ActivityAd ad){
		if(null!=ad.getId()&&ad.getId()>0){//更新
			ad.setCreateTime(null);
			updateActivityAd(ad);
		}else{//保存
			ad.setCreateTime(new Date());
			insertSelective(ad);
		}
	}
	
	//编辑活动广告状态
	public void updateActivityAdState(Integer id,Integer state){
		ActivityAd ad=new ActivityAd();
		ad.setId(id);
		ad.setState(state);
		updateActivityAd(ad);
	}
	
	//banner分类列表
	public JSONArray queryActivityCategory(){
		return JSONArray.parseArray(Redis.use().get("activity_category_list"));
	}
	
	//获取活动banner
	public Map<String,Object> queryActivityAds(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		example.and().andIn("state", Arrays.asList(ActivityAd.AD_STATE_ENUM.ENABLED.getState(),ActivityAd.AD_STATE_ENUM.DISABLED.getState()))
			.andEqualTo("category", param.get("category"));
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
		JSONObject json=JSONObject.parseObject(Redis.use().get("activity_oss_config"));
		JSONArray categoryList=JSONArray.parseArray(Redis.use().get("activity_category_list"));
		List<Map<String,Object>> dataset=selectByExample(example).stream().map(i->{
			Map<String,Object> ad=DataUtil.objectToMap(i);
			String createTime=DateUtil.toString(i.getCreateTime(), DatePattern.COMMON_DATE);
			Optional<Object> atop=categoryList.stream()
					.filter(o->JSONObject.parseObject(o.toString()).getInteger("id").equals(i.getCategory())).findAny();
			ad.put("img", OSSUtil.joinOSSFileUrl(json, i.getImg()));
			ad.put("category", atop.isPresent()?JSONObject.parseObject(atop.get().toString()).get("category"):null);
			ad.put("createTime", createTime);
			return ad;
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//活动广告信息
	public ActivityAd getActivityAdDetail(Integer id){
		ActivityAd ad=getActivityAd(id);
		JSONObject json=JSONObject.parseObject(Redis.use().get("activity_oss_config"));
		ad.setImg(OSSUtil.joinOSSFileUrl(json, ad.getImg()));
		return ad;
	}
	
	//获取活动广告
	public ActivityAd getActivityAd(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<ActivityAd> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取活动广告
	public List<ActivityAd> queryActivityAdsByCategory(Integer category){
		QueryExample example=new QueryExample();
		example.and().andEqualTo("category", category).andEqualTo("state", ActivityAd.AD_STATE_ENUM.ENABLED.getState());
		return selectByExample(example);
	}
	
	//更新活动广告
	public int updateActivityAd(ActivityAd ad){
		if(null!=ad.getId()&&ad.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", ad.getId());
			return updateByExampleSelective(ad, example);
		}
		return 0;
	}

	//获取结果集大小
	public long countByExample(QueryExample example) {
		return activityAdMapper.countByExample(example);
	}

	//保存
	public int insertSelective(ActivityAd record) {
		return activityAdMapper.insertSelective(record);
	}

	//获取结果集
	public List<ActivityAd> selectByExample(QueryExample example) {
		return activityAdMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(ActivityAd record, QueryExample example) {
		return activityAdMapper.updateByExampleSelective(record, example);
	}

}
