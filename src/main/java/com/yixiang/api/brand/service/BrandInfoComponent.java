package com.yixiang.api.brand.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.brand.mapper.BrandInfoMapper;
import com.yixiang.api.brand.pojo.BrandInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class BrandInfoComponent {

	@Autowired
	private BrandInfoMapper brandInfoMapper;
	
	//获取热门品牌
	public List<Map<Object,Object>> queryHotBrands(){
		JSONObject json=JSONObject.parseObject(Redis.use().get("hot_brand_info_config"));
		QueryExample example=new QueryExample();
		example.and().andEqualTo("is_hot", Constants.YES).andEqualTo("state", BrandInfo.BRAND_STATE_ENUM.ENABLED.getState());
		example.setOrderByClause("sort,id");
		example.setLimit(json.getInteger("size"));
		json=JSONObject.parseObject(Redis.use().get("brand_oss_config"));
		String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
		List<Map<Object,Object>> result=selectByExample(example).stream().map(b->{
			return DataUtil.mapOf("id",b.getId(),"brand",b.getBrand()
					,"icon",StringUtils.isNotEmpty(b.getIcon())?domain+b.getIcon():b.getIcon());
		}).collect(Collectors.toList());
		return result;
	}
	
	//获取所有品牌
	public List<Map<Object,Object>> queryAllBrands(){
		QueryExample example=new QueryExample();
		example.and().andEqualTo("state", BrandInfo.BRAND_STATE_ENUM.ENABLED.getState());
		example.setOrderByClause("sort,id");
		JSONObject json=JSONObject.parseObject(Redis.use().get("brand_oss_config"));
		String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
		List<Map<Object,Object>> result=selectByExample(example).stream().map(b->{
			return DataUtil.mapOf("id",b.getId(),"brand",b.getBrand()
					,"icon",StringUtils.isNotEmpty(b.getIcon())?domain+b.getIcon():b.getIcon());
		}).collect(Collectors.toList());
		return result;
	}
	
	//获取品牌信息
	public BrandInfo getBrandInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<BrandInfo> brands=selectByExample(example);
			return brands.size()>0?brands.get(0):null;
		}
		return null;
	}
	
	//更新品牌信息
	public int updateBrandInfo(BrandInfo brand){
		if(null!=brand.getId()&&brand.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", brand.getId());
			return updateByExampleSelective(brand, example);
		}
		return 0;
	}
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return brandInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(BrandInfo record) {
		return brandInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<BrandInfo> selectByExample(QueryExample example) {
		return brandInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(BrandInfo record, QueryExample example) {
		return brandInfoMapper.updateByExampleSelective(record, example);
	}

}
