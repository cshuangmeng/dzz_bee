package com.yixiang.api.test;

import java.io.File;
import java.util.Map;

import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class App1 {

	public static void main(String[] args) {
		String url="http://localhost:8080/charging/comment/tags";
		Map<String,String> queryParas=ParaMap.create("key", "bb1ab2aa10af0e61619d7c1a5a349e82")
				.put("keywords", "特斯拉充电站")
				.put("city", "beijing")
				.put("citylimit", "true")
				.put("offset", "25")
				.put("page", "1").getData();;
        //String json = HttpUtils.get(url, queryParas);
        //System.out.println(json);
		//System.out.println(UUID.randomUUID().toString().replace("-", "").length());
		test1();
	}
	
	public static void test1() {
		try {
			RequestBody body=new MultipartBody.Builder()
					.setType(MultipartBody.ALTERNATIVE)
					.addFormDataPart("uuid", "3d95e429efca4369b6b327c1367f3e26")
					.addFormDataPart("content","《战神》今日进😄行版本更新，此前宣布的拍🥛照模式正式上线。作为一款奎爷与儿子😂在北欧的“旅游”大作，没有自拍怎么行？为了让玩家更好地记录沿途风景，官方也是拼了。")
					.addFormDataPart("tags","说的好,非常棒,牛逼")
					.addFormDataPart("files","gamersky_03small_06_2018510103098E.jpg"
							,RequestBody.create(MediaType.parse("application/octet-stream")
							, new File("/Users/huangmeng/Downloads/2017-02-12 084158.jpg")))
					.build();
			Request request=new Request.Builder().url("https://demo.sayiyinxiang.com/api/article/comment/save")
					.addHeader("phone", "18910701047")
					.addHeader("system", "ios_1.0")
					.addHeader("imei", "6620fd0903f2442e9b7996aaf4b119b4")
			        .post(body)
			        .build();
			OkHttpClient client=new OkHttpClient();
			Response response=client.newCall(request).execute();
			System.out.println(response.isSuccessful());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
