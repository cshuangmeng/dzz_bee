package com.yixiang.api.order.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.order.mapper.CouponInfoMapper;
import com.yixiang.api.order.mapper.CouponTypeMapper;
import com.yixiang.api.order.pojo.CouponInfo;
import com.yixiang.api.order.pojo.CouponType;
import com.yixiang.api.order.pojo.TradeHistory;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OgnlUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class CouponInfoComponent {

	@Autowired
	private CouponInfoMapper couponInfoMapper;
	@Autowired
	private CouponTypeMapper couponTypeMapper;
	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private TradeHistoryComponent tradeHistoryComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//加载我的优惠券列表
	public Map<String,Object> queryMyCoupons(Integer userId,Integer page,Integer state){
		UserInfo user=userInfoComponent.getUserInfo(userId, false);
		List<Integer> states=null;
		if(null!=state){
			if(state.equals(Constants.NO)){
				states=Arrays.asList(CouponInfo.COUPON_STATE_ENUM.USED.getState(),CouponInfo.COUPON_STATE_ENUM.EXPIRED.getState());
			}else if(state.equals(Constants.YES)){
				states=Arrays.asList(CouponInfo.COUPON_STATE_ENUM.NO_USE.getState());
			}
		}
		JSONObject config=JSONObject.parseObject(Redis.use().get("user_coupon_list_config"));
		DecimalFormat yuanFormat=new DecimalFormat(config.getString("yuan_pattern"));
		DecimalFormat zheFormat=new DecimalFormat(config.getString("zhe_pattern"));
		List<Map<Object,Object>> dataset=queryUserCoupons(user.getId(),states,page,config.getInteger("size"))
				.stream().map(o->DataUtil.mapOf("id",o.getId()
				,"title",o.getTitle(),"description",o.getDescription(),"reduceType",o.getReduceType(),"category",o.getCategory()
				,"amount",o.getAmount(),"amountTxt",o.getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())
				?zheFormat.format(o.getAmount()):yuanFormat.format(o.getAmount())
				,"startTime",DateUtil.toString(o.getStartTime(), o.getPattern()),"state",o.getState()
				,"endTime",DateUtil.toString(o.getEndTime(), o.getPattern()))).collect(Collectors.toList());
		return DataUtil.mapOf("dataset",dataset);
	}
	
	//匹配可用优惠券
	public Map<String,Object> matchCoupons(Integer userId,Integer category){
		UserInfo user=userInfoComponent.getUserInfo(userId, false);
		Map<String,Object> param=ThreadCache.getHttpData();
		JSONObject config=JSONObject.parseObject(Redis.use().get("user_coupon_list_config"));
		DecimalFormat yuanFormat=new DecimalFormat(config.getString("yuan_pattern"));
		DecimalFormat zheFormat=new DecimalFormat(config.getString("zhe_pattern"));
		List<Integer> states=Arrays.asList(CouponInfo.COUPON_STATE_ENUM.NO_USE.getState());
		List<Map<Object,Object>> dataset=queryUserCoupons(user.getId(), states, null, null).stream()
				.filter(o->o.getCategory().equals(category)).map(o->{
			//逐个校验ognl表达式是否通过
			return DataUtil.mapOf("id",o.getId(),"isAvali",isCouponAvailable(user.getId(),param,o)?1:0
					,"title",o.getTitle(),"description",o.getDescription(),"reduceType",o.getReduceType(),"category",o.getCategory()
					,"amount",o.getAmount(),"amountTxt",o.getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())
					?zheFormat.format(o.getAmount()*10):yuanFormat.format(o.getAmount())
					,"startTime",DateUtil.toString(o.getStartTime(), o.getPattern())
					,"endTime",DateUtil.toString(o.getEndTime(), o.getPattern()),"maxDiscount",o.getMaxDiscount());
		}).sorted((a,b)->{
			Integer isAvaliA=Integer.valueOf(a.get("isAvali").toString());
			Integer isAvaliB=Integer.valueOf(b.get("isAvali").toString());
			Float priceA=Float.valueOf(a.get("amount").toString());
			Float priceB=Float.valueOf(b.get("amount").toString());
			//如果传入了应付金额,则计算折扣券的实际减免金额
			if(!DataUtil.isEmpty(param.get("price"))){
				Float price=Float.valueOf(param.get("price").toString());
				if(Integer.valueOf(a.get("reduceType").toString()).equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())){
					priceA=price*(1-priceA);
				}
				if(Integer.valueOf(b.get("reduceType").toString()).equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())){
					priceB=price*(1-priceB);
				}
			}
			if(isAvaliA.compareTo(isAvaliB)==0){
				if(priceA.compareTo(priceB)==0){
					return Integer.valueOf(b.get("id").toString()).compareTo(Integer.valueOf(a.get("id").toString()));
				}else{//减免金额大的优先
					return priceB.compareTo(priceA);
				}
			}else{//可用优惠券优先
				return isAvaliB.compareTo(isAvaliA);
			}
		}).collect(Collectors.toList());
		//过滤掉不可用优惠券
		if(!config.getBooleanValue("show_unavailable_coupons")){
			dataset=dataset.stream().filter(o->Integer.valueOf(o.get("isAvali").toString()).equals(Constants.YES)).collect(Collectors.toList());
		}
		//分页
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer pageSize=Integer.valueOf(param.getOrDefault("pageSize", config.get("size")).toString());
		page=pageSize*(page>0?page-1:0);
		if(page<dataset.size()){
			dataset=dataset.subList(page, page+pageSize<dataset.size()?page+pageSize:dataset.size());
		}else{
			dataset.clear();
		}
		return DataUtil.mapOf("dataset",dataset);
	}
	
	//匹配优惠券是否可用
	public boolean isCouponAvailable(Integer userId,Map<String,Object> param,CouponInfo coupon){
		UserInfo user=userInfoComponent.getUserInfo(userId, false);
		//常规检查
		if(null==coupon){
			log.info("优惠券信息不存在,coupon="+coupon);
			return false;
		}
		if(!user.getId().equals(coupon.getUserId())){
			log.info("非本人优惠券,couponId="+coupon.getId()+",UserInfo.id="+user.getId()+",CouponInfo.userId="+coupon.getUserId());
			return false;
		}
		if(!coupon.getState().equals(CouponInfo.COUPON_STATE_ENUM.NO_USE.getState())){
			log.info("优惠券状态不正确,couponId="+coupon.getId()+",CouponInfo.state="+coupon.getState());
			return false;
		}
		String startTime=DateUtil.toString(coupon.getStartTime(), coupon.getPattern());
		String endTime=DateUtil.toString(coupon.getEndTime(), coupon.getPattern());
		String now=DateUtil.toString(new Date(), coupon.getPattern());
		if(!Range.between(startTime, endTime).contains(now)){
			log.info("不在优惠期使用有效期内,couponId="+coupon.getId()+",startTime="+startTime+",endTime="+endTime+",now="+now);
			return false;
		}
		//表达式检查
		if(StringUtils.isNotEmpty(coupon.getCond())&&!OgnlUtil.ognl(coupon.getCond(), param)){
			log.info("ognl表达式校验未通过,couponId="+coupon.getId()+",cond="+coupon.getCond()+",param="+param);
			return false;
		}
		log.info("ognl表达式校验通过,couponId="+coupon.getId()+",cond="+coupon.getCond()+",param="+param);
		return true;
	}
	
	//生成充值兑换码
	@Transactional
	public Map<String,Object> buildRechargeRedeemCode(){
		Map<String,Object> http=ThreadCache.getHttpData();
		String appId=http.getOrDefault("appId", "").toString();
		String uuid=http.getOrDefault("uuid", "").toString();
		Float money=!DataUtil.isEmpty(http.get("money"))?Float.valueOf(http.get("money").toString()):0;
		JSONObject config=JSONObject.parseObject(Redis.use().get("jfq_exchange_config"));
		if(!config.getInteger("enable").equals(Constants.YES)){
			log.info("积分墙兑换功能已关闭,config="+config.toJSONString());
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_NOT_OPEN);
			return null;
		}
		if(!appId.equals(config.getString("appId"))){
			log.info("积分墙兑换appId不正确,appId="+appId+",config.appId="+config.getString("appId"));
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_APPID_INCORRECT);
			return null;
		}
		CouponType type=getCouponTypeById(config.getInteger("typeId"));
		if(null==type||!type.getState().equals(Constants.YES)){
			log.info("积分墙兑换码模板未配置,CouponType="+type);
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_NOT_OPEN);
			return null;
		}
		if(money<config.getFloatValue("min_money")){
			log.info("起兑金额不正确,money="+money+",config.min_money="+config.getFloatValue("min_money"));
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_MONEY_INCORRECT);
			return null;
		}
		//计算兑换金额
		String[] data=config.getString("rate").split(":");
		Float proportion=Float.parseFloat(data[0]);
		Float amount=Float.parseFloat(data[1]);
		amount=new BigDecimal((money/proportion)*amount).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		//生成兑换码
		String code=DataUtil.createNums(config.getIntValue("digit"));
		while(null!=getCouponInfoByCode(code)){
			code=DataUtil.createNums(config.getIntValue("digit"));
		}
		CouponInfo coupon=new CouponInfo();
		coupon.setAmount(amount);
		coupon.setCategory(type.getCategory());
		coupon.setCode(code);
		coupon.setJfqUuid(uuid);
		coupon.setCreateTime(new Date());
		coupon.setDescription(type.getDescription());
		coupon.setPattern(type.getPattern());
		coupon.setStartTime(coupon.getCreateTime());
		coupon.setEndTime(DateUtils.addDays(coupon.getStartTime(), type.getDuration()));
		coupon.setTitle(type.getTitle());
		coupon.setRemark(type.getRemark());
		coupon.setReduceType(type.getReduceType());
		insertSelective(coupon);
		return DataUtil.mapOf("code",code,"price",amount);
	}
	
	//兑换码兑换余额
	@Transactional
	public void useRedeemCode(Integer userId,String code){
		CouponInfo coupon=getCouponInfoByCode(code);
		if(null==coupon){
			log.info("兑换码不正确,code="+code);
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_CODE_INCORRECT);
			return;
		}
		coupon=getCouponInfo(coupon.getId(), true);
		if(!coupon.getState().equals(CouponInfo.COUPON_STATE_ENUM.NO_USE.getState())){
			log.info("兑换码状态不正确,coupon.id="+coupon.getId()+",coupon.state="+coupon.getState()+",code="+code);
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_STATE_INCORRECT);
			return;
		}
		if(!coupon.getCategory().equals(CouponInfo.COUPON_CATEGORY_ENUM.RECHARGE.getCategory())){
			log.info("非现金充值券,coupon.id="+coupon.getId()+",coupon.category="+coupon.getCategory()+",code="+code);
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_COUPON_INCORRECT);
			return;
		}
		String startTime=DateUtil.toString(coupon.getStartTime(), coupon.getPattern());
		String endTime=DateUtil.toString(coupon.getEndTime(), coupon.getPattern());
		String now=DateUtil.toString(new Date(), coupon.getPattern());
		if(!Range.between(startTime, endTime).contains(now)){
			log.info("不在优惠期使用有效期内,couponId="+coupon.getId()+",startTime="+startTime+",endTime="+endTime+",now="+now);
			Result.putValue(ResponseCode.CodeEnum.EXCHANGE_CODE_INVALID);
			return;
		}
		//充值到账户
		UserInfo user=userInfoComponent.getUserInfo(userId, false);
		if(coupon.getAmount()>0){
			userInfoComponent.addBalance(user.getId(), new BigDecimal(coupon.getAmount()));
		}
		//记录流水
		tradeHistoryComponent.saveTradeHistory(user.getId(), coupon.getId(), TradeHistory.TRADE_TYPE_ENUM.JFQ_RECHARGE.getType()
				, coupon.getAmount(), TradeHistory.TRADE_STATE_ENUM.YICHULI.getState(), null);
		//更新兑换码状态
		coupon.setUserId(user.getId());
		coupon.setUseTime(new Date());
		coupon.setState(CouponInfo.COUPON_STATE_ENUM.USED.getState());
		updateCouponInfo(coupon);
		Result.putValue(ResponseCode.CodeEnum.EXCHANGE_COUPON_SUCCESS);
	}
	
	//分页加载用户的优惠券
	public List<CouponInfo> queryUserCoupons(Integer userId,List<Integer> states,Integer page,Integer size){
		QueryExample example=new QueryExample();
		example.and().andEqualTo("user_id", userId).andIn("state", states);
		if(null!=page&&null!=size){
			example.setOffset((page>0?page-1:0)*size);
			example.setLimit(size);
		}
		example.setOrderByClause("create_time desc,id desc");
		return selectByExample(example);
	}
	
	//获取优惠券
	public CouponInfo getCouponInfo(Integer id,boolean lock){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			example.setLock(lock);
			List<CouponInfo> coupons=selectByExample(example);
			return coupons.size()>0?coupons.get(0):null;
		}
		return null;
	}
	
	//获取优惠券
	public List<CouponInfo> queryCouponByIds(List<Integer> ids){
		if(null!=ids&&ids.size()>0){
			QueryExample example=new QueryExample();
			example.and().andIn("id", ids);
			return selectByExample(example);
		}
		return null;
	}
	
	//下发优惠券
	@Transactional
	public CouponInfo grantCoupon(Integer userId,Integer typeId){
		if(null==typeId||typeId<=0){
			log.info("优惠券类型ID为空或不正确,typeId"+typeId);
			return null;
		}
		QueryExample example=new QueryExample();
		example.and().andEqualTo("id", typeId).andEqualTo("state", Constants.YES);
		List<CouponType> types=couponTypeMapper.selectByExample(example);
		CouponInfo coupon=null;
		if(types.size()>0){
			CouponType type=types.get(0);
			coupon=new CouponInfo();
			coupon.setAmount(type.getAmount());
			coupon.setCategory(type.getCategory());
			coupon.setCond(type.getCond());
			coupon.setCreateTime(new Date());
			coupon.setDescription(type.getDescription());
			coupon.setPattern(type.getPattern());
			coupon.setReduceType(type.getReduceType());
			coupon.setRemark(type.getRemark());
			coupon.setStartTime(coupon.getCreateTime());
			coupon.setEndTime(DateUtils.addDays(coupon.getStartTime(), type.getDuration()));
			coupon.setTitle(type.getTitle());
			coupon.setUserId(userId);
			coupon.setTypeId(type.getId());
			insertSelective(coupon);
		}
		return coupon;
	}
	
	//获取用户的优惠券
	public List<CouponInfo> getUserCoupons(Integer uesrId,Integer category){
		if(null!=uesrId&&uesrId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", uesrId).andEqualTo("category", category);
			return couponInfoMapper.selectByExample(example);
		}
		return null;
	}
	
	//获取用户的可用优惠券数量
	public long getUserEnableCouponAmount(Integer uesrId){
		if(null!=uesrId&&uesrId>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("user_id", uesrId).andEqualTo("state", CouponInfo.COUPON_STATE_ENUM.NO_USE.getState())
				.andGreaterThan("end_time", new Date());
			return couponInfoMapper.countByExample(example);
		}
		return 0;
	}
	
	//通过兑换码获取优惠券
	public CouponInfo getCouponInfoByCode(String code){
		if(StringUtils.isNotEmpty(code)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("code", code);
			List<CouponInfo> coupons=selectByExample(example);
			return coupons.size()>0?coupons.get(0):null;
		}
		return null;
	}
	
	//获取优惠券类型
	public CouponType getCouponTypeById(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<CouponType> coupons=couponTypeMapper.selectByExample(example);
			return coupons.size()>0?coupons.get(0):null;
		}
		return null;
	}
	
	//获取结果集大小
	public long countByExample(QueryExample example) {
		return couponInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(CouponInfo record) {
		return couponInfoMapper.insertSelective(record);
	}

	//获取结果集
	public List<CouponInfo> selectByExample(QueryExample example) {
		return couponInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(CouponInfo record, QueryExample example) {
		return couponInfoMapper.updateByExampleSelective(record, example);
	}
	
	//更新优惠券
	public void updateCouponInfo(CouponInfo coupon){
		if(null!=coupon&&null!=coupon.getId()&&coupon.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", coupon.getId());
			updateByExampleSelective(coupon, example);
		}
	}
	
	//更新优惠券
	public void updateCouponState(Integer couponId,Integer state,Integer tradeId){
		if(null!=couponId&&couponId>0){
			if(null!=state||null!=tradeId){
				CouponInfo coupon=new CouponInfo();
				coupon.setId(couponId);
				coupon.setState(state);
				coupon.setTradeId(tradeId);
				if(null!=state&&state.equals(CouponInfo.COUPON_STATE_ENUM.USED.getState())){
					coupon.setUseTime(new Date());
				}
				updateCouponInfo(coupon);
			}
		}
	}

}
