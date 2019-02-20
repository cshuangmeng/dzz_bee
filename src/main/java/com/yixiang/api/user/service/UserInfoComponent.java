package com.yixiang.api.user.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.charging.service.ChargingStationComponent;
import com.yixiang.api.order.pojo.TradeHistory;
import com.yixiang.api.order.service.TradeHistoryComponent;
import com.yixiang.api.user.mapper.UserInfoMapper;
import com.yixiang.api.user.pojo.CarInfo;
import com.yixiang.api.user.pojo.UserDevice;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.SmsUtil;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.AreaInfoComponent;

@Service
public class UserInfoComponent {
	
	@Autowired
	private UserInfoMapper userInfoMapper;
	@Autowired
	private UserDeviceComponent userDeviceComponent;
	@Autowired
	private ChargingStationComponent chargingStationComponent;
	@Autowired
	private CarInfoComponent carInfoComponent;
	@Autowired
	private AreaInfoComponent areaInfoComponent;
	@Autowired
	private TradeHistoryComponent tradeHistoryComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//尝试登录
	@SuppressWarnings("unchecked")
	public UserInfo attemptLogin(){
		Map<String,Object> param=(Map<String,Object>)ThreadCache.getData(Constants.HTTP_PARAM);
		String phone=!DataUtil.isEmpty(param.get(Constants.PHONE))?param.get(Constants.PHONE).toString():null;
		String imei=!DataUtil.isEmpty(param.get(Constants.IMEI))?param.get(Constants.IMEI).toString():null;
		String system=!DataUtil.isEmpty(param.get(Constants.SYSTEM))?param.get(Constants.SYSTEM).toString():null;
		String openId=!DataUtil.isEmpty(param.get(Constants.WXOPENID))?param.get(Constants.WXOPENID).toString():null;
		if(StringUtils.isNotEmpty(phone)){
			if(StringUtils.isNotEmpty(imei)&&StringUtils.isNotEmpty(system)){//APP登录
				UserInfo user=getUserInfoByPhone(phone);
				if(null!=user&&user.getState().equals(UserInfo.USER_STATE_ENUM.YIJIHUO.getState())){
					UserDevice device=userDeviceComponent.getUserDeviceByImeiAndSystem(user.getId(),imei,system);
					if(null!=device&&user.getDeviceId().equals(device.getId())){
						return user;
					}
				}
			}else if(StringUtils.isNotEmpty(openId)){//微信登录
				UserInfo user=getUserInfoByPhone(phone);
				if(null!=user&&user.getState().equals(UserInfo.USER_STATE_ENUM.YIJIHUO.getState())){
					UserDevice device=userDeviceComponent.getUserDeviceByOpenId(user.getId(),openId);
					if(null!=device&&user.getDeviceId().equals(device.getId())){
						return user;
					}
				}
			}
		}
		return null;
	}
	
	//用户账户信息
	public Map<String,Object> getUserInfo(String uuid){
		UserInfo user=getUserInfoByUUID(uuid);
		Map<String,Object> result=null;
		if(null!=user){
			JSONObject json=JSONObject.parseObject(Redis.use().get("user_oss_config"));
			String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
			result=DataUtil.mapOf("uuid",uuid,"userName",user.getUserName()
					,"headImg",StringUtils.isNotEmpty(user.getHeadImg())?domain+user.getHeadImg():user.getHeadImg()
					,"level",user.getLevelId(),"stars",user.getStars(),"coins",user.getCoins(),"balance",user.getBalance());
		}
		return result;
	}
	
	//获取账户信息列表
	public Map<String,Object> queryUserInfo(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		//筛选用户
		if(!DataUtil.isEmpty(param.get("areaId"))||!DataUtil.isEmpty(param.get("phone"))){
			example.and().andEqualTo("area_id", param.get("areaId")).andEqualTo("phone", param.get("phone"));
		}
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
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(o->{
			AreaInfo city=areaInfoComponent.getAreaInfo(o.getAreaId());
			AreaInfo province=null;
			UserDevice device=userDeviceComponent.getUserDevice(o.getDeviceId());
			if(null!=city){
				province=areaInfoComponent.getAreaInfo(city.getParentId());
			}
			return DataUtil.mapOf("id",o.getId(),"userName",o.getUserName(),"phone",o.getPhone(),"platform",null!=device?device.getPlatform():null
					,"headImg",OSSUtil.joinOSSFileUrl(oss, o.getHeadImg()),"coins",o.getCoins(),"balance",o.getBalance()
					,"province",null!=province?province.getAreaName():null,"city",null!=city?city.getAreaName():null
					,"createTime",o.getCreateTime(),"activeTime",o.getActiveTime(),"loginTime",o.getLoginTime());
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//清空用户账户余额
	@Transactional
	public void emptyAccount(Integer id){
		UserInfo user=getUserInfo(id, true);
		if(null==user){
			log.info("用户信息不存在,id="+id);
			Result.putValue(ResponseCode.CodeEnum.USER_NOT_EXISTS);
			return;
		}
		BigDecimal balance=user.getBalance();
		user.setBalance(new BigDecimal(0));
		updateUserInfo(user);
		tradeHistoryComponent.saveTradeHistory(user.getId(), null
				, TradeHistory.TRADE_TYPE_ENUM.RECHARGE_REFUND.getType(), -balance.floatValue()
				, TradeHistory.TRADE_STATE_ENUM.YICHULI.getState(), "管理平台操作清空用户账户余额");
		log.info("尝试清空用户账户,userId="+user.getId()+",原账户金额:"+balance+",清空后账户金额:"+user.getBalance());
	}
	
	//用户主页
	public Map<String,Object> getUserHomeData(){
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		Map<String,Object> result=getUserInfo(user.getUuid());
		result.put("times", user.getTimes());
		//加载个人充电桩
		JSONObject json=JSONObject.parseObject(Redis.use().get("charging_oss_config"));
		String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
		List<Map<Object,Object>> stations=chargingStationComponent.getChargingStationsByUserId(user.getId()).stream().map(c->{
			String electricityPrice=null;
			if(DataUtil.isJSONArray(c.getElectricityPrice())){
				JSONArray array=JSONArray.parseArray(c.getElectricityPrice());
				electricityPrice=array.size()>0?array.getJSONObject(0).getString("price"):null;
			}
			return DataUtil.mapOf("uuid",c.getUuid(),"headImg",StringUtils.isNotEmpty(c.getHeadImg())?domain+c.getHeadImg():c.getHeadImg()
					,"title",c.getTitle(),"times",c.getTimes(),"source",c.getSource(),"electricityPrice",electricityPrice
					,"serviceFee",c.getServiceFee());
		}).collect(Collectors.toList());
		result.put("stations", stations);
		//加载车辆信息
		List<CarInfo> cars=carInfoComponent.queryCarInfosByUserId(user.getId());
		if(cars.size()>0){
			result.put("car", cars.get(0).getCar());
		}
		//客服电话
		result.put("kf_phone", Redis.use().get("kf_phone"));
		return result;
	}
	
	//用户登录
	@Transactional
	public Map<String,Object> login(String phone,String pwd){
		try {
			Subject subject=SecurityUtils.getSubject();
			UsernamePasswordToken token=new UsernamePasswordToken(phone, pwd);
			subject.login(token);
			Serializable sessionId=subject.getSession().getId();
			if(null!=sessionId){
				return DataUtil.mapOf("token",sessionId);
			}
		} catch (Exception e) {
			Result.putValue(ResponseCode.CodeEnum.USER_AUTH_FAIL);
			e.printStackTrace();
		}
		return null;
	}
	
	//下发验证码
	@SuppressWarnings("unchecked")
	public void sendCheckCode(){
		Map<String,Object> param=(Map<String,Object>)ThreadCache.getData(Constants.HTTP_PARAM);
		String phone=!DataUtil.isEmpty(param.get(Constants.PHONE))?param.get(Constants.PHONE).toString():null;
		if(StringUtils.isEmpty(phone)){
			log.info("用户手机号未输入,phone="+phone);
			Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
			return;
		}
		//检查验证码是否过期
		JSONObject json=JSONObject.parseObject(Redis.use().get("login_verify_code_config"));
		String key=json.getString("prefix")+phone;
		if(StringUtils.isNotEmpty(Redis.use().get(key))){
			log.info("验证码还未过期,不能重复获取");
			Result.putValue(ResponseCode.CodeEnum.VERIFY_CODE_LIVE);
			return;
		}
		String code=DataUtil.createNums(json.getIntValue("length"));
		//发送验证码
		if(!SmsUtil.sendVerifyCode(code, phone)){
			log.info("验证码发送失败,phone="+phone);
			Result.putValue(ResponseCode.CodeEnum.CODE_SEND_FAILED);
			return;
		}
		//将验证码存储到redis
		Redis.use().setex(key, json.getIntValue("live_seconds"), code);
	}
	
	//更新用户星级
	@Transactional
	public void updateStars(Integer userId,Integer stars){
		UserInfo user=getUserInfo(userId, true);
		if(null!=user){
			user.setStars(stars);
			updateUserInfo(user);
		}
	}
	
	//累加用户余额
	@Transactional
	public void addBalance(Integer userId,BigDecimal interval){
		UserInfo user=getUserInfo(userId, true);
		if(null!=user){
			user.setBalance(user.getBalance().add(interval));
			updateUserInfo(user);
		}
	}
	
	//累加用户会员积分
	@Transactional
	public void addCoins(Integer userId,Integer interval){
		UserInfo user=getUserInfo(userId, true);
		if(null!=user){
			user.setCoins(user.getCoins()+interval);
			updateUserInfo(user);
		}
	}
	
	//累加用户充电次数
	@Transactional
	public void addTimes(Integer userId,Integer interval){
		UserInfo user=getUserInfo(userId, true);
		if(null!=user){
			user.setTimes(user.getTimes()+interval);
			updateUserInfo(user);
		}
	}
	
	//获取用户信息
	public UserInfo getUserInfo(Integer id,boolean lock){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			example.setLock(lock);
			List<UserInfo> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取用户信息
	public UserInfo getUserInfoByPhone(String phone){
		if(StringUtils.isNotEmpty(phone)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("phone", phone);
			List<UserInfo> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取用户信息
	public UserInfo getUserInfoByOpenId(String openId){
		if(StringUtils.isNotEmpty(openId)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("wx_open_id", openId);
			List<UserInfo> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//获取用户信息
	public UserInfo getUserInfoByUUID(String uuid){
		if(StringUtils.isNotEmpty(uuid)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("uuid", uuid);
			List<UserInfo> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新用户信息
	public int updateUserInfo(UserInfo info){
		if(null!=info.getId()&&info.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", info.getId());
			return updateByExampleSelective(info, example);
		}
		return 0;
	}

	//计算结果集大小
	public long countByExample(QueryExample example) {
		return userInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(UserInfo record) {
		return userInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<UserInfo> selectByExample(QueryExample example) {
		return userInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(UserInfo record, QueryExample example) {
		return userInfoMapper.updateByExampleSelective(record, example);
	}

}
