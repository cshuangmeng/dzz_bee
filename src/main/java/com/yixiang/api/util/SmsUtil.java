package com.yixiang.api.util;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SmsUtil {

	public final static String APPKEY = PropertiesUtil.getProperty("jiguang_app_key");;
	public final static String APPSECRET = PropertiesUtil.getProperty("jiguang_app_secret");;

	// 依据短信模板发送验证码
	private static String sendSmsByTemplate(String url, Map<String, Object> params) {
		String result = null;
		try {
			byte[] auth = new String(APPKEY + ":" + APPSECRET).getBytes();
			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
					.writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
			RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSONString(params));
			Request request = new Request.Builder().url(url)
					.addHeader("Authorization", "Basic " + Base64.encodeBase64String(auth)).post(body).build();
			Response response = client.newCall(request).execute();
			result = response.body().string();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 发送单条模板短信
	public static boolean sendVerifyCode(String code, String phone) {
		String url = PropertiesUtil.getProperty("jiguang_sms_url");
		Integer templateId = 1;
		Map<String, Object> params = DataUtil.mapOf("mobile", phone, "temp_id", templateId, "temp_para",
				DataUtil.mapOf("code", code));
		String response = sendSmsByTemplate(url, params);
		return !JSONObject.parseObject(response).containsKey("error");
	}

}
