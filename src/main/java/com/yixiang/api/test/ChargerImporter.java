package com.yixiang.api.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.CharsetType;
import com.feilong.core.DatePattern;
import com.feilong.core.TimeInterval;
import com.feilong.core.Validator;
import com.feilong.core.date.DateUtil;
import com.feilong.core.util.RegexUtil;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.charging.service.ChargingStationComponent;
import com.yixiang.api.main.Application;
import com.yixiang.api.util.DataUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ChargerImporter {

	@Autowired
	private ChargingStationComponent chargingStationComponent;
	
	@Test
	public void test1()throws Exception{
		//BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/huangmeng/Downloads/error1.log",true)));
		String listUrl="https://www.evyou.cc/appserver/getChargingPileUpdateDataForApp.do";
		Map<String,String> listParams=ParaMap.create("dataVersion", "6").getData();
		String listJson=HttpUtils.get(listUrl, listParams);
		String detailUrl="https://www.evyou.cc/appserver/getChargingStationInfoDetailForApp.do";
		JSONObject body=JSONObject.parseObject(JSONObject.parseObject(listJson).getString("body"));
		JSONArray list=body.getJSONArray("charging_station");
		System.out.println("数据大小："+list.size());
		/*for(int i=0;i>list.size();i++){
			JSONObject charger=list.getJSONObject(i);
			Map<String,String> detailParams=ParaMap.create("chargingStationCode", charger.getString("chargingStationCode")).getData();
			String detailJson=HttpUtils.get(detailUrl, detailParams);
			JSONObject detail=JSONObject.parseObject(detailJson).getJSONObject("body").getJSONObject("charging_station");
			writer.write(detail.toJSONString());
			writer.newLine();
			try {
				if(i%10==0){
					int millis=RandomUtils.nextInt(1, 5);
					System.out.println("线程即将休眠"+millis+"秒");
					Thread.sleep(millis*TimeInterval.MILLISECOND_PER_SECONDS);
				}
				if(i%500==0){
					System.out.println(DateUtil.toString(new Date(), DatePattern.COMMON_TIME)+" 已导入"+i+"条数据");
					writer.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		writer.close();
		*/
	}
	
	@Test
	public void test2()throws Exception{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("/Users/huangmeng/Downloads/error.log"), CharsetType.UTF8));
		String row=reader.readLine();
		while(Validator.isNotNullOrEmpty(row)){
			//saveToDb(JSONObject.parseObject(row));
			row=reader.readLine();
		}
		reader.close();
	}
	
	public void saveToDb(JSONObject detail){
		ChargingStation station=new ChargingStation();
		station.setTelephone(detail.getString("carrPhone"));
		station.setProvider(detail.getString("carrierName"));
		station.setUuid(DataUtil.buildUUID());
		station.setPayTip(detail.getString("createCardProcess"));
		station.setElectricityPrice(detail.getJSONArray("electPrice").toJSONString());
		station.setFastNum(detail.getInteger("fastNum"));
		station.setIsUnderground(detail.getInteger("isGround"));
		station.setIsPrivate(detail.getInteger("isPublic"));
		station.setLat(new BigDecimal(detail.getString("latitude")));
		station.setAddress(detail.getString("location"));
		station.setLng(new BigDecimal(detail.getString("longitude")));
		station.setOpenTime(detail.getString("openTime"));
		List<Map<Object,Object>> packing=Arrays.asList(detail.getString("parkExpense").split(";")).stream()
				.filter(item->Validator.isNotNullOrEmpty(item)).map(item->{
					if(!item.contains(",")){
						return DataUtil.mapOf("during", "全天", "price", item);
					}else{
						return DataUtil.mapOf("during", item.split(",")[0], "price", item.split(",")[1]);
					}
				}).collect(Collectors.toList());
		station.setParkingPrice(JSONArray.toJSONString(packing));
		station.setPayWay(detail.getString("payModelDesc"));
		station.setHeadImg(detail.getString("pics"));
		station.setRemark(detail.getString("remark"));
		station.setServiceFee(detail.getString("servPrice"));
		station.setSlowNum(detail.getInteger("slowNum"));
		station.setTitle(detail.getString("title"));
		chargingStationComponent.insertSelective(station);
	}
	
}
