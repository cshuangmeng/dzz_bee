package com.yixiang.api.charging.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.charging.mapper.ChargingStationMapper;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.order.service.OrderInfoComponent;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserChargingComponent;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ChargingStationComponent {

	@Autowired
	private ChargingStationMapper chargingStationMapper;
	@Autowired
	private ChargingCommentComponent chargingCommentComponent;
	@Autowired
	private UserChargingComponent userChargingComponent;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private OrderInfoComponent orderInfoComponent;
	@Autowired
	private ReportStationInfoComponent reportStationInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//获取个人充电桩信息列表
	public Map<String,Object> queryChargingStation(){
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
		example.and().andIn("user_id", ids).andGreaterThan("user_id", 0);
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
		JSONObject oss=JSONObject.parseObject(Redis.use().get("charging_oss_config"));
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(o->{
			//用户信息
			UserInfo user=userInfoComponent.getUserInfo(o.getUserId(), false);
			return DataUtil.mapOf("id",o.getId(),"userName",user.getUserName(),"phone",user.getPhone(),"title",o.getTitle()
					,"address",o.getAddress(),"createTime",o.getCreateTime(),"provider",o.getProvider(),"telephone",o.getTelephone()
					,"fastNum",o.getFastNum(),"slowNum",o.getSlowNum(),"openTime",o.getOpenTime(),"electricityPrice",o.getElectricityPrice()
					,"headImg",OSSUtil.joinOSSFileUrl(oss, o.getHeadImg()),"serviceFee",o.getServiceFee());
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//下发充电桩运营商
	public JSONArray queryStationProvider(){
		return JSONArray.parseArray(Redis.use().get("station_provider_list"));
	}
	
	//统计指定时间充电桩每天的使用情况
	public Map<String,Object> statChargingStation(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		if(DataUtil.isEmpty(param.get("startDate"))||DataUtil.isEmpty(param.get("endDate"))){
			Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
			return null;
		}
		//获取数据总条数
		Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
		Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
		if(startDate.compareTo(endDate)>0){
			Result.putValue(ResponseCode.CodeEnum.PARAM_INCORRECT);
			return null;
		}
		endDate=DateUtils.addDays(endDate, 1);
		Integer total=(int)((DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH).getTime()
				-DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH).getTime())/(1000*3600*24));
		startDate=DateUtils.addDays(startDate, limit*(page>0?page-1:0));
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		List<Map<String,Object>> dataset=new ArrayList<>();
		//快慢充电桩数量
		Map<String,Object> station=statFastSlowAmount(param);
		Integer slowNum=Integer.valueOf(station.get("slowNum").toString());
		Integer fastNum=Integer.valueOf(station.get("fastNum").toString());
		//订单数量
		List<Map<String,Object>> orders=orderInfoComponent.statFastSlowOrder(param);
		//故障数量
		List<Map<String,Object>> reports=reportStationInfoComponent.statStationReport(param);
		Integer connectorType=Integer.valueOf(param.getOrDefault("connectorType", 0).toString());
		DecimalFormat format=new DecimalFormat("#.#");
		for(int i=0;i<limit&&!DateUtils.isSameDay(startDate, endDate);i++){
			String date=DateUtil.toString(startDate, DatePattern.COMMON_DATE);
			Optional<Map<String,Object>> oop=orders.stream().filter(o->date.equals(o.get("ot").toString())).findAny();
			Optional<Map<String,Object>> rop=reports.stream().filter(o->date.equals(o.get("ot").toString())).findAny();
			Integer stationNum=slowNum+fastNum;
			if(connectorType.equals(ChargingStation.CONNECTOR_TYPE_ENUM.SLOW.getType())){
				stationNum=slowNum;
			}else if(connectorType.equals(ChargingStation.CONNECTOR_TYPE_ENUM.FAST.getType())){
				stationNum=fastNum;
			}
			Integer reportAmount=rop.isPresent()?Integer.valueOf(rop.get().get("amount").toString()):0;
			String faultRate=format.format((stationNum>0?(reportAmount.floatValue()/stationNum):0)*100)+"%";
			dataset.add(DataUtil.mapOf("date",date,"stationAmount",stationNum,"useAmount",oop.isPresent()?oop.get().get("useAmount"):0
					,"orderAmount",oop.isPresent()?oop.get().get("orderAmount"):0,"faultRate",faultRate,"reportAmount",reportAmount));
			startDate=DateUtils.addDays(startDate, 1);
		}
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//加载充电桩简介
	public Map<String,Object> getChargingSummary(Integer id,BigDecimal lng,BigDecimal lat){
		ChargingStation station=getChargingStation(id);
		if(null==station){
			log.info("充电桩不存在,id="+id);
			Result.putValue(ResponseCode.CodeEnum.STATION_NOT_EXISTS);
			return null;
		}
		//停车费
		String parkingPrice=station.getParkingPrice();
		//电费
		String electricityPrice=station.getElectricityPrice();
		//评论总数
		Long commentTotal=chargingCommentComponent.getChargingCommentTotal(station.getId());
		//组装返回结果
		return DataUtil.mapOf("isPrivate",station.getIsPrivate(),"title",station.getTitle(),"address",station.getAddress()
				,"fastNum",station.getFastNum(),"slowNum",station.getSlowNum(),"distance",DataUtil.getDistanceFormatText(lat.doubleValue()
				,lng.doubleValue(),station.getLat().doubleValue(),station.getLng().doubleValue()),"provider",station.getProvider()
				,"openTime",station.getOpenTime(),"payWay",station.getPayWay(),"parkingPrice",parkingPrice,"commentTotal",commentTotal
				,"lng",station.getLng(),"lat",station.getLat(),"electricityPrice",electricityPrice,"uuid",station.getUuid()
				,"serviceFee",station.getServiceFee()
				,"headImg",station.getHeadImg(),"times",station.getTimes(),"phone",station.getTelephone()
				,"freeNum",setFreeNum(station.getFastNum(),station.getSlowNum()));
	}
	
	//加载充电桩详情
	public Map<String,Object> getChargingDetail(Integer id,BigDecimal lng,BigDecimal lat){
		Map<String,Object> result=getChargingSummary(id, lng, lat);
		//获取最近充电用户
		if(null!=result){
			//收藏状态
			UserInfo user=userInfoComponent.attemptLogin();
			result.put("is_collect", 0);
			if(null!=user){
				ChargingStation station=getChargingStation(id);
				result.put("is_collect", null!=userChargingComponent.getUserCharging(user.getId(), station.getId())?1:0);
			}
		}
		return result;
	}
	
	//新建个人充电桩
	public void saveChargingStation(ChargingStation station,MultipartFile[] detailImgs){
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		//检查用户充电桩数量是否已超限额
		JSONObject json=JSONObject.parseObject(Redis.use().get("user_charging_config"));
		if(json.containsKey("max")){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", user.getId()).andEqualTo("state", ChargingStation.STATION_STATE_ENUM.ENABLED.getState());
			if(selectByExample(example).size()>=json.getInteger("max")){
				log.info("个人充电桩数量已超限额,userId="+user.getId());
				Result.putValue(ResponseCode.CodeEnum.CHARGING_EXCEED_MAX);
				return;
			}
		}
		//上传详情图片
		if(null!=detailImgs){
			StringBuilder names=new StringBuilder();
			for(MultipartFile img:detailImgs){
				if(null!=img&&!img.isEmpty()){
					String saveName=new Date().getTime()+DataUtil.createNums(7);
					saveName+=img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf("."));
					try {
						if(OSSUtil.uploadFileToOSS(img.getInputStream(), saveName, "charging_oss_config")){
							names.append(names.length()>0?","+saveName:saveName);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			station.setDetailImgs(names.toString());
			station.setHeadImg(station.getDetailImgs().split(",")[0]);
		}
		station.setUserId(user.getId());
		station.setUuid(DataUtil.buildUUID());
		station.setCreateTime(new Date());
		insertSelective(station);
	}
	
	//搜索附近充电桩
	public List<Map<Object,Object>> queryNearbyStations(BigDecimal lng,BigDecimal lat,Integer page){
		JSONObject json=JSONObject.parseObject(Redis.use().get("home_nearby_station_config"));
		List<ChargingStation> stations=queryChargingStations(DataUtil.mapOf("nearby",json.getInteger("distance")
				,"lng",lng.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()
				,"lat",lat.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()
				,"state",ChargingStation.STATION_STATE_ENUM.ENABLED.getState()
				,"offset",(page-1)*json.getInteger("size"),"limit",json.getInteger("size")));
		//组织返回结果
		List<Map<Object,Object>> result=stations.stream().map(s->{
			return DataUtil.mapOf("uuid",s.getUuid(),"title",s.getTitle(),"address",s.getAddress()
					,"distance",DataUtil.getDistanceFormatText(s.getDistance().doubleValue())
					,"openTime",s.getOpenTime(),"fastNum",s.getFastNum(),"slowNum",s.getSlowNum()
					,"lng",s.getLng(),"lat",s.getLat(),"img",s.getHeadImg(),"isPrivate",s.getIsPrivate()
					,"freeNum",setFreeNum(s.getFastNum(),s.getSlowNum()));
		}).collect(Collectors.toList());
		return result;
	}
	
	//计算空闲充电桩数量,因为缺少实时数据,暂用假数据代替
	public Integer setFreeNum(Integer fastNum,Integer slowNum){
		Integer hour=Integer.valueOf(DateUtil.toString(new Date(), "h"));
		Integer total=fastNum+slowNum;
		Integer freeNum=0;
		if(total>0){
			return hour>total?1:Math.round(total/hour.floatValue());
		}
		return freeNum;
	}
	
	//搜索充电桩
	public List<ChargingStation> queryChargingStations(Map<String,Object> param){
		return chargingStationMapper.queryChargingStations(param);
	}
	
	//获取充电站信息
	public ChargingStation getChargingStation(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<ChargingStation> stations=selectByExample(example);
			return stations.size()>0?stations.get(0):null;
		}
		return null;
	}
	
	//获取充电站信息
	public ChargingStation getChargingStationByUUID(String uuid){
		if(StringUtils.isNotEmpty(uuid)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("uuid", uuid);
			List<ChargingStation> stations=selectByExample(example);
			return stations.size()>0?stations.get(0):null;
		}
		return null;
	}
	
	//获取用户充电站信息
	public List<ChargingStation> getChargingStationsByUserId(Integer userId){
		if(null!=userId&&userId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", userId);
			return selectByExample(example);
		}
		return null;
	}
	
	//更新充电站信息
	public int updateChargingStation(ChargingStation station){
		if(null!=station.getId()&&station.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", station.getId());
			return updateByExampleSelective(station, example);
		}
		return 0;
	}
	
	//统计快慢充订购
	public Map<String,Object> statFastSlowAmount(Map<String,Object> param) {
		return chargingStationMapper.statFastSlowAmount(param);
	}
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return chargingStationMapper.countByExample(example);
	}

	//保存
	public int insertSelective(ChargingStation record) {
		return chargingStationMapper.insertSelective(record);
	}

	//获取结果集
	public List<ChargingStation> selectByExample(QueryExample example) {
		return chargingStationMapper.selectByExample(example);
	}
	
	//更新
	public int updateByExampleSelective(ChargingStation record, QueryExample example) {
		return chargingStationMapper.updateByExampleSelective(record, example);
	}

}
