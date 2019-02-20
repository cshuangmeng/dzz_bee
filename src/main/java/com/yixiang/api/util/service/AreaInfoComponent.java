package com.yixiang.api.util.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.util.mapper.AreaInfoMapper;
import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class AreaInfoComponent {

	@Autowired
	private AreaInfoMapper areaInfoMapper;
	
	//查询地区信息
	public List<AreaInfo> queryAreaInfos(Integer parentId){
		if(null!=parentId){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("parent_id", parentId);
			return areaInfoMapper.selectByExample(example);
		}
		return null;
	}
	
	//查询地区信息
	public AreaInfo getAreaInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<AreaInfo> areas=areaInfoMapper.selectByExample(example);
			return areas.size()>0?areas.get(0):null;
		}
		return null;
	}
	
	//获取结果集
	public List<AreaInfo> selectByExample(QueryExample example){
		return areaInfoMapper.selectByExample(example);
	}
	
}
