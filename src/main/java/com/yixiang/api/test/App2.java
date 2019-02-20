package com.yixiang.api.test;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;

import com.feilong.core.Alphabet;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.feilong.core.net.ParamUtil;
import com.feilong.core.util.MapUtil;
import com.feilong.core.util.RandomUtil;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.cache.RedisAccessTokenCache;

public class App2 {

	public static void main(String[] args) {
		//设置公众号账户
		ApiConfig ac=new ApiConfig();
		ac.setAppId("wx1be0f2a1f512729a1");
		ac.setAppSecret("b2f856a84ebec1f4878e832400f185a2");
		ApiConfigKit.putApiConfig(ac);
		//设置accessToken存储方式
		RedisPlugin rp=new RedisPlugin("mainRedis", "39.107.126.2", 6379, "thetsredis");
		rp.start();
		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache());
		//调用API
		String token=AccessTokenApi.getAccessTokenStr();
		System.out.println(token);
		ApiResult ar=UserApi.getUserInfo("oePMUv0BlXr2WjRcZuf4tHSrCfak");
		System.out.println(ar.getJson());
		//微信公众号支付
		Map<String, String> param=MapUtil.newHashMap();
		param.put("appid", ac.getAppId());
		param.put("mch_id", "1458821002");
		param.put("body", "jfinal");
		param.put("total_fee", "1");
		param.put("nonce_str", RandomUtil.createRandomFromString(Alphabet.DECIMAL_AND_LETTERS, 32));
		param.put("out_trade_no", RandomUtil.createRandomFromString(Alphabet.DECIMAL, 32));
		param.put("spbill_create_ip", "127.0.0.1");
		param.put("timeStamp", String.valueOf(String.valueOf(new Date().getTime()/1000)));
		param.put("time_start", DateUtil.toString(new Date(), DatePattern.TIMESTAMP));
		param.put("trade_type", "JSAPI");
		param.put("openid","oePMUv0BlXr2WjRcZuf4tHSrCfak");
		param.put("notify_url", "http://api.tangseng.shop/shop/pay/notify");
		param.put("sign", signValue(param, "b2bab676568fcda3d7d319704a89d0bf").toUpperCase());
		String json=PaymentApi.pushOrder(param);
		System.out.println(json);
	}
	
	public static String signValue(Map<String,String> params,String key){
		String string=ParamUtil.toNaturalOrderingQueryString(params);
		string+=string.length()>0?"&key="+key:"key="+key;
		String sign="";
		try {
			sign = toHexValue(encryptMD5(string.getBytes(Charset.forName("utf-8"))));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sha1 error");
		}
		return sign;
	}
	
	private static byte[] encryptMD5(byte[] data)throws Exception{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}
	
	private static String toHexValue(byte[] messageDigest) {
		if (messageDigest == null)
			return "";
		StringBuilder hexValue = new StringBuilder();
		for (byte aMessageDigest : messageDigest) {
			int val = 0xFF & aMessageDigest;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	
}
