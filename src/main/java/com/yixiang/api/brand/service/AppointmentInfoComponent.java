package com.yixiang.api.brand.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.yixiang.api.brand.mapper.AppointmentInfoMapper;
import com.yixiang.api.brand.pojo.AppointmentInfo;
import com.yixiang.api.brand.pojo.BrandCar;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.AreaInfoComponent;

@Service
public class AppointmentInfoComponent {

	@Autowired
	private AppointmentInfoMapper appointmentInfoMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private BrandCarComponent brandCarComponent;
	@Autowired
	private AreaInfoComponent areaInfoComponent;
	
	//获取预约用户信息列表
	public Map<String,Object> queryAppointmentInfo(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		//筛选用户
		List<Integer> userIds=null;
		if(!DataUtil.isEmpty(param.get("phone"))){
			example.and().andEqualTo("phone", param.get("phone"));
			List<UserInfo> users=userInfoComponent.selectByExample(example);
			if(users.size()<=0){
				return DataUtil.mapOf("total",users.size(),"dataset",users);
			}
			userIds=users.stream().map(o->o.getId()).collect(Collectors.toList());
			example.clear();
		}
		example.and().andIn("user_id", userIds);
		//筛选车型
		List<Integer> ids=null;
		if(!DataUtil.isEmpty(param.get("brandId"))||!DataUtil.isEmpty(param.get("source"))){
			example.and().andEqualTo("brand_id", param.get("brandId")).andEqualTo("source", param.get("source"));
			List<BrandCar> cars=brandCarComponent.selectByExample(example);
			if(cars.size()<=0){
				return DataUtil.mapOf("total",cars.size(),"dataset",cars);
			}
			ids=cars.stream().map(o->o.getId()).collect(Collectors.toList());
			example.clear();
		}
		List<Integer> areaIds=null;
		if(!DataUtil.isEmpty(param.get("areaId"))){
			areaIds=areaInfoComponent.queryAreaInfos(Integer.parseInt(param.get("areaId").toString()))
					.stream().map(i->i.getId()).collect(Collectors.toList());
			areaIds.add(Integer.parseInt(param.get("areaId").toString()));
		}
		example.and().andEqualTo("state", AppointmentInfo.APPOINTMENT_STATE_ENUM.ENABLED.getState())
			.andIn("car_id", ids).andIn("area_id", areaIds);
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
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(i->{
			//用户信息
			UserInfo user=userInfoComponent.getUserInfo(i.getUserId(), false);
			//车型信息
			BrandCar car=brandCarComponent.getBrandCar(i.getCarId());
			return DataUtil.mapOf("id",i.getId(),"createTime",i.getCreateTime(),"car",car.getCar(),"batteryLife",car.getBatteryLife(),"price",car.getPrice()
					,"groupPrice",car.getGroupPrice(),"shopPrice",car.getShopPrice(),"userName",null!=user?user.getUserName():null
					,"phone",null!=user?user.getPhone():null,"province",i.getProvince(),"city",i.getCity(),"address",i.getAddress()
					,"state",i.getState());
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//删除预约信息
	@Transactional
	public void deleteAppointmentInfo(Integer id){
		AppointmentInfo update=new AppointmentInfo();
		update.setId(id);
		update.setState(AppointmentInfo.APPOINTMENT_STATE_ENUM.DELETED.getState());
		updateAppointmentInfo(update);
	}
	
	//更新预约信息
	@Transactional
	public void updateAppointmentInfo(AppointmentInfo update){
		if(null!=update&&null!=update.getId()&&update.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", update.getId());
			updateByExampleSelective(update, example);
		}
	}
	
	//获取结果集大小
	public long countByExample(QueryExample example) {
		return appointmentInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(AppointmentInfo record) {
		return appointmentInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<AppointmentInfo> selectByExample(QueryExample example) {
		return appointmentInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(AppointmentInfo record, QueryExample example) {
		return appointmentInfoMapper.updateByExampleSelective(record, example);
	}
	
}
