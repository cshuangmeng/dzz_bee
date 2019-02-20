package com.yixiang.api.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.feilong.core.CharsetType;
import com.feilong.core.DatePattern;
import com.feilong.core.Validator;
import com.feilong.core.date.DateUtil;
import com.feilong.core.util.RegexUtil;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.yixiang.api.article.pojo.ArticleInfo;
import com.yixiang.api.article.service.ArticleInfoComponent;
import com.yixiang.api.brand.pojo.BrandCar;
import com.yixiang.api.brand.pojo.BrandInfo;
import com.yixiang.api.brand.service.BrandCarComponent;
import com.yixiang.api.brand.service.BrandInfoComponent;
import com.yixiang.api.charging.pojo.ChargingStation;
import com.yixiang.api.charging.service.ChargingStationComponent;
import com.yixiang.api.main.Application;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.pojo.AreaInfo;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.AreaInfoComponent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ArticleImporter {
	
	@Autowired
	private BrandCarComponent brandCarComponent;
	@Autowired
	private ArticleInfoComponent articleInfoComponent;
	@Autowired
	private BrandInfoComponent brandInfoComponent;
	@Autowired
	private ChargingStationComponent chargingStationComponent;
	@Autowired
	private AreaInfoComponent areaInfoComponent;
	
	@Test
	public void test1()throws Exception{
		//readCars();
		//readArticles();
		//readBrand();
		//updateCarBrand();
		readChargingStation();
	}
	
	public void readChargingStation(){
		QueryExample example=new QueryExample();
		example.and().andGreaterThan("lng", 0).andGreaterThan("lat", 0);
		ChargingStation update=new ChargingStation();
		String url="http://api.map.baidu.com/geocoder/v2/?ak=Yw2f6FG6SOcq0MoBfeuG0rihcaFNQkBE&output=json&pois=0&location=";
		chargingStationComponent.selectByExample(example).stream().forEach(i->{
			JSONObject json=JSONObject.parseObject(HttpUtils.get(url+i.getLat()+","+i.getLng()));
			Integer areaId=json.getJSONObject("result").getJSONObject("addressComponent").getInteger("adcode");
			example.clear();
			example.and().andEqualTo("area_code", areaId);
			List<AreaInfo> areas=areaInfoComponent.selectByExample(example);
			if(areas.size()==1){
				update.setAreaId(areas.get(0).getId());
				example.clear();
				example.and().andEqualTo("id", i.getId());
				chargingStationComponent.updateByExampleSelective(update, example);
			}else if(areas.size()>1){
				System.out.println(i.getId()+",找到一个以上的结果"+json.getJSONObject("result").getJSONObject("addressComponent"));
			}else{
				System.out.println(i.getId()+",未找到");
			}
		});
		
	}
	
	public void updateCarBrand(){
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream("/Users/huangmeng/Downloads/易享充电/data.txt"), CharsetType.UTF8));
			QueryExample example=null;
			String row=reader.readLine();
			while(Validator.isNotNullOrEmpty(row)){
				String car=row.split("	")[0];
				String brand=row.split("	")[1];
				example=new QueryExample();
				example.and().andEqualTo("car", car);
				BrandCar brandCar=brandCarComponent.selectByExample(example).get(0);
				example=new QueryExample();
				example.and().andEqualTo("brand", brand);
				List<BrandInfo> brandInfos=brandInfoComponent.selectByExample(example);
				if(brandInfos.size()>0){
					BrandInfo brandInfo=brandInfos.get(0);
					brandCar.setBrandId(brandInfo.getId());
					brandCarComponent.updateBrandCar(brandCar);
				}else{
					System.out.println(car+" 未找到品牌 "+brand);
				}
				row=reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readBrand(){
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream("/Users/huangmeng/Downloads/易享充电/cars.html"), CharsetType.UTF8));
			StringBuilder builder=new StringBuilder();
			String row=reader.readLine();
			while(null!=row){
				if(Validator.isNotNullOrEmpty(row)){
					builder.append(row.trim());
				}
				row=reader.readLine();
			}
			reader.close();
			Document doc=Jsoup.parse(builder.toString());
			Elements elements=doc.select("div.content div");
			for(int i=0;i<elements.size();i++){
				String firstWord=elements.get(i).attr("data-key");
				Elements a=elements.get(i+1).select("a");
				for(int j=0;j<a.size();j++){
					String img=a.get(j).select("img").get(0).attr("src");
					String brand=a.get(j).select("span").get(1).text().trim();
					/*BrandInfo info=new BrandInfo();
					info.setBrand(brand);
					info.setIcon(saveImgToOSS(img));
					info.setCreateTime(new Date());
					brandInfoComponent.insertSelective(info);*/
					QueryExample example=new QueryExample();
					example.and().andEqualTo("brand", brand);
					BrandInfo info=brandInfoComponent.selectByExample(example).get(0);
					info.setFirstWord(firstWord);
					brandInfoComponent.updateBrandInfo(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readArticles(){
		try {
			String url="http://db.auto.sina.cn/search/api/inside/SinaSearch/getExtendedReadingList.json";
			Map<String,String> params=ParaMap.create("page", "1").put("limit", "1000").getData();
			List<BrandCar> cars=brandCarComponent.selectByExample(new QueryExample());
			for(int c=22;c<cars.size();c++){
				BrandCar car=cars.get(c);
				params.put("serial_id", String.valueOf(car.getSid()));
				String response=HttpUtils.get(url, params);
				JSONObject json=JSONObject.parseObject(response);
				JSONArray data=json.getJSONObject("data").getJSONArray("data");
				System.out.println(car.getCar()+","+c+","+car.getId()+" begin......");
				for(int i=0;i<data.size();i++){
					JSONObject obj=data.getJSONObject(i);
					if(!obj.getString("wap_url").startsWith("http://k.sina.cn/article_")){
						continue;
					}
					ArticleInfo info=new ArticleInfo();
					String icon=obj.getString("mainPic");
					info.setIcon(saveImgToOSS(icon));
					info.setTitle(obj.getString("title"));
					info.setCreateTime(DateUtil.toDate(obj.getString("cTime"), DatePattern.COMMON_DATE_AND_TIME));
					String html=HttpUtils.get(obj.getString("wap_url"));
					Document parser=Jsoup.parse(html);
					Elements nodes=parser.select("div.art_content");
					Elements imgs=nodes.select("img");
					for(Element img:imgs){
						String saveName=saveImgToOSS(!img.attr("src").startsWith("http:")?"http:"+img.attr("src"):img.attr("src"));
						img.attr("src", "http://img.sayiyinxiang.com/api/brand/imgs/"+saveName);
					}
					info.setCarId(car.getId());
					info.setContent(nodes.html());
					info.setUuid(DataUtil.buildUUID());
					try {
						articleInfoComponent.insertSelective(info);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readCars()throws Exception{
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new InputStreamReader(new FileInputStream("/Users/huangmeng/Downloads/易享充电/cars.html"), CharsetType.UTF8));
			StringBuilder builder=new StringBuilder();
			String row=reader.readLine();
			while(null!=row){
				if(Validator.isNotNullOrEmpty(row)){
					builder.append(row.trim());
				}
				row=reader.readLine();
			}
			Document parser=Jsoup.parse(builder.toString()); 
			Elements nodes=parser.select("li");
			String reg=".*/(\\d+)/.*";
			System.out.println(nodes.size());
			for(int i=0;i<nodes.size();i++){
				Element node=nodes.get(i);
				Integer sid=Integer.valueOf(RegexUtil.group(reg, node.child(0).attr("href"), 1));
				String icon=node.child(0).child(0).attr("src");
				String title=node.child(0).child(1).text().trim();
				String price=node.child(0).child(2).text().trim();
				//保存
				BrandCar car=new BrandCar();
				car.setSid(String.valueOf(sid));
				car.setIcon(saveImgToOSS(icon));
				car.setCar(title);
				car.setPrice(price);
				car.setCreateTime(new Date());
				brandCarComponent.insertSelective(car);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null!=reader){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//上传图片
	public static String saveImgToOSS(String img){
		try {
			InputStream is=new URL(img).openStream();
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			int b=0;
			byte[] bytes=new byte[1024];
			while((b=is.read(bytes))!=-1){
				bos.write(bytes, 0, b);
			}
			bos.flush();
			ByteArrayInputStream bis=new ByteArrayInputStream(bos.toByteArray());
			String saveName=new Date().getTime()+DataUtil.createNums(7);
			saveName+=img.substring(img.lastIndexOf("."));
			uploadFileToOSS(bis, saveName);
			bis.close();
			bos.close();
			is.close();
			return saveName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 上传本地文件至OSS存储
	public static void uploadFileToOSS(InputStream is, String saveName) throws Exception {
		String oss="{'endpoint':'http://oss-cn-beijing.aliyuncs.com','accesskeyid':'LTAImEn86VbcYUu6'"
				+ ",'secretaccesskey':'iUmW0uxlwUkSyhuW0OQ1FVhXPdElTk','bucketname':'sayi-static-product'"
				+ ",'imgDir':'api/brand/imgs','videoDir':'api/brand/videos'"
				+ ",'domain':'http://img.sayiyinxiang.com'"
				+ ",'img_domain':'http://sayi-static-product.img-cn-beijing.aliyuncs.com','style':'@500w'}";
		JSONObject json = JSONObject.parseObject(oss);
		OSSClient client = new OSSClient(json.getString("endpoint"), json.getString("accesskeyid"),json.getString("secretaccesskey"));
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(is.available());
		if (DataUtil.isImg(saveName)) {
			client.putObject(json.getString("bucketname"), json.getString("imgDir") + "/" + saveName, is, meta);
		} else if (DataUtil.isVideo(saveName)) {
			client.putObject(json.getString("bucketname"), json.getString("videoDir") + "/" + saveName, is, meta);
		}
	}

}
