package com.yixiang.api.brand.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.brand.mapper.BrandCarMapper;
import com.yixiang.api.brand.pojo.BrandCar;
import com.yixiang.api.brand.pojo.BrandInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.AreaInfoComponent;

@Service
public class BrandCarComponent {

	@Autowired
	private BrandCarMapper brandCarMapper;
	@Autowired
	private BrandInfoComponent brandInfoComponent;
	@Autowired
	private AreaInfoComponent areaInfoComponent;
	
	//获取车型列表
	public Map<String,Object> queryBrandCar(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		List<Integer> states=Arrays.asList(BrandCar.CAR_STATE_ENUM.ENABLED.getState(),BrandCar.CAR_STATE_ENUM.DISABLED.getState());
		example.and().andIn("state", states);
		if(!DataUtil.isEmpty(param.get("startDate"))){
			Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
			example.and().andGreaterThanOrEqualTo("create_time", startDate);
		}
		if(!DataUtil.isEmpty(param.get("endDate"))){
			Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
			endDate=DateUtils.addDays(endDate, 1);
			example.and().andLessThan("create_time", endDate);
		}
		if(!DataUtil.isEmpty(param.get("brandId"))){
			example.and().andEqualTo("brand_id", Integer.valueOf(param.get("brandId").toString()));
		}
		if(!DataUtil.isEmpty(param.get("areaId"))){
			example.and().andEqualTo("area_id", Integer.valueOf(param.get("areaId").toString()));
		}
		//获取数据总条数
		Long total=countByExample(example);
		example.setOffset(limit*(page>0?page-1:0));
		example.setLimit(limit);
		example.setOrderByClause("create_time desc");
		JSONObject oss=JSONObject.parseObject(Redis.use().get("brand_oss_config"));
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(o->{
			//品牌
			BrandInfo brand=brandInfoComponent.getBrandInfo(o.getBrandId());
			return DataUtil.mapOf("id",o.getId(),"brand",brand.getBrand(),"icon",OSSUtil.joinOSSFileUrl(oss, o.getIcon())
					,"car",o.getCar(),"batteryLife",o.getBatteryLife(),"price",o.getPrice(),"groupPrice",o.getGroupPrice()
					,"shopPrice",o.getShopPrice(),"category",o.getCategory(),"color",o.getColor(),"state",o.getState()
					,"detailImgs",OSSUtil.joinOSSFileUrl(oss,o.getDetailImgs().split(",")),"remark",o.getRemark()
					,"paramImgs",OSSUtil.joinOSSFileUrl(oss,o.getParamImgs().split(",")));
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//统计车型访问情况
	public Map<String,Object> statBrandCar(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		List<Integer> states=Arrays.asList(BrandCar.CAR_STATE_ENUM.ENABLED.getState(),BrandCar.CAR_STATE_ENUM.DISABLED.getState());
		example.and().andIn("state", states).andEqualTo("source", param.get("source")).andEqualTo("area_id", param.get("areaId"))
			.andEqualTo("brand_id", param.get("brandId"));
		param.put("states", states);
		if(!DataUtil.isEmpty(param.get("startDate"))){
			Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
			param.put("startDate", startDate);
		}
		if(!DataUtil.isEmpty(param.get("endDate"))){
			Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
			endDate=DateUtils.addDays(endDate, 1);
			param.put("endDate", endDate);
		}
		//获取数据总条数
		Long total=countByExample(example);
		param.put("offset",limit*(page>0?page-1:0));
		param.put("limit",limit);
		List<Map<String,Object>> dataset=statBrandCar(param);
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//编辑车型信息
	@Transactional
	public void editBrandCar(BrandCar car){
		if(null!=car.getId()&&car.getId()>0){//更新
			car.setCreateTime(null);
			updateBrandCar(car);
		}else{//保存
			car.setState(BrandCar.CAR_STATE_ENUM.ENABLED.getState());
			car.setCreateTime(new Date());
			insertSelective(car);
		}
	}
	
	//置顶车型
	public void topBrandCar(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			BrandCar update=new BrandCar();
			update.setTopTime(new Date());
			updateByExampleSelective(update, example);
		}
	}
	
	//获取车型列表
	public JSONArray queryCarType(){
		return JSONObject.parseObject(Redis.use().get("brand_car_query_items")).getJSONArray("cars");
	}
	
	//获取车型详情
	public Map<String,Object> getBrandCarDetail(Integer id){
		BrandCar car=getBrandCar(id);
		Map<String,Object> result=null;
		if(null!=car){
			JSONObject oss=JSONObject.parseObject(Redis.use().get("brand_oss_config"));
			result=DataUtil.objectToMap(car);
			result.put("detailImgs", OSSUtil.joinOSSFileUrl(oss,car.getDetailImgs().split(",")));
			result.put("paramImgs",OSSUtil.joinOSSFileUrl(oss,car.getParamImgs().split(",")));
			result.put("banner",OSSUtil.joinOSSFileUrl(oss,car.getBanner().split(",")));
			result.put("icon",OSSUtil.joinOSSFileUrl(oss, car.getIcon()));
			//补充地区信息
			AreaInfo area=areaInfoComponent.getAreaInfo(car.getAreaId());
			result.put("parentId", null!=area?area.getParentId():null);
		}
		return result;
	}
	
	//获取所有车型
	public List<Map<Object,Object>> queryAllCars(Integer brandId){
		QueryExample example=new QueryExample();
		example.and().andEqualTo("brand_id", brandId).andEqualTo("state", BrandCar.CAR_STATE_ENUM.ENABLED.getState());
		example.setOrderByClause("sort,id");
		JSONObject json=JSONObject.parseObject(Redis.use().get("brand_oss_config"));
		List<Map<Object,Object>> result=selectByExample(example).stream().map(c->{
			return DataUtil.mapOf("id",c.getId(),"car",c.getCar(),"icon",OSSUtil.joinOSSFileUrl(json, c.getIcon()));
		}).collect(Collectors.toList());
		return result;
	}
	
	//更新车型状态
	@Transactional
	public void updateBrandCarState(Integer id,Integer state){
		BrandCar car=new BrandCar();
		car.setId(id);
		car.setState(state);
		updateBrandCar(car);
	}
	
	//获取汽车信息
	public BrandCar getBrandCar(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<BrandCar> cars=selectByExample(example);
			return cars.size()>0?cars.get(0):null;
		}
		return null;
	}
	
	//更新汽车信息
	@Transactional
	public int updateBrandCar(BrandCar car){
		if(null!=car&&null!=car.getId()&&car.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", car.getId());
			return updateByExampleSelective(car, example);
		}
		return 0;
	}
	
	//统计车型访问情况
	public List<Map<String,Object>> statBrandCar(Map<String,Object> param){
		return brandCarMapper.statBrandCar(param);
	}
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return brandCarMapper.countByExample(example);
	}

	//保存
	@Transactional
	public int insertSelective(BrandCar car) {
		return brandCarMapper.insertSelective(car);
	}

	//获取结果集
	public List<BrandCar> selectByExample(QueryExample example) {
		return brandCarMapper.selectByExample(example);
	}

	//更新
	@Transactional
	public int updateByExampleSelective(BrandCar car, QueryExample example) {
		return brandCarMapper.updateByExampleSelective(car, example);
	}

}
