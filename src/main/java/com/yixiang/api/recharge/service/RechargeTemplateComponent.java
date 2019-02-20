package com.yixiang.api.recharge.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.recharge.mapper.RechargeTemplateMapper;
import com.yixiang.api.recharge.pojo.RechargeTemplate;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class RechargeTemplateComponent {

	@Autowired
	private RechargeTemplateMapper rechargeTemplateMapper;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//获取所有可用充值模板
	public List<RechargeTemplate> getRechargeTemplates(){
		QueryExample example=new QueryExample();
		example.and().andEqualTo("state", Constants.YES);
		example.setOrderByClause("sort");
		return appendImgPrefix(rechargeTemplateMapper.selectByExample(example));
	}
	
	//获取充值模板
	public RechargeTemplate getRechargeTemplate(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<RechargeTemplate> result=rechargeTemplateMapper.selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//组装图片全路径
	public List<RechargeTemplate> appendImgPrefix(List<RechargeTemplate> templates){
		String config=Redis.use().get("recharge_oss_config");
		if(StringUtils.isNotEmpty(config)){
			JSONObject json=JSONObject.parseObject(config);
			templates.stream().forEach(t->{
				if(StringUtils.isNotEmpty(t.getHeadImg())){
					if(DataUtil.isImg(t.getHeadImg())){
						t.setHeadImg(json.getString("domain")+"/"+json.getString("imgDir")+"/"+t.getHeadImg());
					}else if(DataUtil.isVideo(t.getHeadImg())){
						t.setHeadImg(json.getString("domain")+"/"+json.getString("videoDir")+"/"+t.getHeadImg());
					}
				}
			});
		}
		return templates;
	}
	
}
