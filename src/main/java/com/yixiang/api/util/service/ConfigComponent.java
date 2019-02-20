package com.yixiang.api.util.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.util.mapper.ConfigMapper;
import com.yixiang.api.util.pojo.Config;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ConfigComponent {
	
	@Autowired
	private ConfigMapper configMapper;
	
	//读取配置信息至Redis
	public void syncConfigToRedis(){
		List<Config> configs=selectByExample(new QueryExample());
		configs.stream().forEach(c->{
			if(c.getState().equals(Config.CONFIG_STATE_ENUM.DISABLED.getState())){//删除不可用的配置
				Redis.use().del(c.getTitle());
			}else if(c.getState().equals(Config.CONFIG_STATE_ENUM.ENABLED.getState())){//更新可用的配置
				Redis.use().set(c.getTitle(), c.getContent());
			}
		});
	}
	
	//获取系统配置信息
	public Config getConfig(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<Config> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取系统配置信息
	public Config getConfig(String name){
		if(StringUtils.isNotEmpty(name)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("title", name);
			List<Config> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新系统配置信息
	public int updateConfig(Config config){
		if(null!=config.getId()&&config.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", config.getId());
			return updateByExampleSelective(config, example);
		}
		return 0;
	}

	//获取结果集大小
	public long countByExample(QueryExample example) {
		return configMapper.countByExample(example);
	}

	//保存
	public int insertSelective(Config record) {
		return configMapper.insertSelective(record);
	}

	//获取结果集
	public List<Config> selectByExample(QueryExample example) {
		return configMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(Config record, QueryExample example) {
		return configMapper.updateByExampleSelective(record, example);
	}

}
