package com.yixiang.api.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushUtil {

	public final static String APPKEY = PropertiesUtil.getProperty("jiguang_app_key");
	public final static String APPSECRET = PropertiesUtil.getProperty("jiguang_app_secret");
	public final static String PLATFORM_ANDROID = "android";
	public final static String PLATFORM_IOS = "ios";

	// 推送通知
	private static String push(String url, Map<String, Object> params) {
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

	// 推送通知
	public static boolean pushNotification(List<String> registrationIds, Map<String, Object> notification,
			Map<String, Object> options) {
		String url = PropertiesUtil.getProperty("jiguang_push_url");
		if (null != registrationIds && registrationIds.size() > 0) {
			if (null == options) {
				options = DataUtil.mapOf();
			}
			options.put("apns_production", PropertiesUtil.getProperty("jiguang_apns_production"));
			Map<String, Object> params = DataUtil.mapOf("platform", "all", "audience",
					DataUtil.mapOf("registration_id", registrationIds), "notification", notification, "options",
					options);
			String response = push(url, params);
			return !JSONObject.parseObject(response).containsKey("error");
		}
		return false;
	}

	// 推送通知
	public static boolean pushNotification(String registrationId, Map<String, Object> notification) {
		return pushNotification(Arrays.asList(registrationId), notification, null);
	}

	// 推送应用内消息
	public static boolean pushMessage(List<String> registrationIds, Map<String, Object> message,
			Map<String, Object> options) {
		String url = PropertiesUtil.getProperty("jiguang_push_url");
		if (null != registrationIds && registrationIds.size() > 0) {
			if (null == options) {
				options = DataUtil.mapOf();
			}
			options.put("apns_production", PropertiesUtil.getProperty("jiguang_apns_production"));
			Map<String, Object> params = DataUtil.mapOf("platform", "all", "audience",
					DataUtil.mapOf("registration_id", registrationIds), "message", message, "options", options);
			String response = push(url, params);
			return !JSONObject.parseObject(response).containsKey("error");
		}
		return false;
	}

	// 推送应用内消息
	public static boolean pushMessage(String registrationId, Map<String, Object> message) {
		return pushNotification(Arrays.asList(registrationId), message, null);
	}

}
