package com.yixiang.api.user.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Duang;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.wxaapp.api.WxaUserApi;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;

@Service
public class WeiXinComponent {

	// 微信用户接口api
	protected WxaUserApi wxaUserApi = Duang.duang(WxaUserApi.class);
	Logger log=LoggerFactory.getLogger(getClass());

	//登陆接口
	public JSONObject login(String jsCode) {
		if (StringUtils.isEmpty(jsCode)) {
			Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
			log.info("微信code为空,code="+jsCode);
			return null;
		}
		// 获取SessionKey
		ApiResult apiResult = wxaUserApi.getSessionKey(jsCode);
		// 返回{"session_key":"nzoqhc3OnwHzeTxJs+inbQ==","expires_in":2592000,"openid":"oVBkZ0aYgDMDIywRdgPW8-joxXc4"}
		if (!apiResult.isSucceed()) {
			Result.putValue(ResponseCode.CodeEnum.FAIL);
			log.info("请求失败,response="+apiResult.getJson());
			return null;
		}
		// 利用 appId 与 accessToken 建立关联，支持多账户
		IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
		String sessionId = StrKit.getRandomUUID();

		accessTokenCache.set(Constants.WXA_SESSION_PREFIX + sessionId, apiResult.getJson());
		JSONObject json=JSONObject.parseObject(apiResult.getJson());
		json.remove("session_key");
		json.put(Constants.WXA_SESSION, sessionId);
		return json;
	}

	//服务端解密用户信息接口
	@SuppressWarnings("unchecked")
	public JSONObject info(String signature,String rawData,String encryptedData,String iv) {
		// 利用 appId 与 accessToken 建立关联，支持多账户
		Map<String,Object> param=(Map<String,Object>)ThreadCache.getData(Constants.HTTP_PARAM);
		IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
		if(DataUtil.isEmpty(param.get(Constants.WXA_SESSION))){
			Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
			log.info("请求头wxa-sessionid为空,wxa-sessionid="+param.get(Constants.WXA_SESSION));
			return null;
		}
		String sessionId = param.get(Constants.WXA_SESSION).toString();
		String sessionJson = accessTokenCache.get(Constants.WXA_SESSION_PREFIX + sessionId);
		if (StringUtils.isEmpty(sessionJson)) {
			Result.putValue(ResponseCode.CodeEnum.FAIL);
			log.info("用户wxa_session为空sessionId="+sessionId+",sessionJson="+sessionJson);
			return null;
		}
		ApiResult sessionResult = ApiResult.create(sessionJson);
		// 获取sessionKey
		String sessionKey = sessionResult.get("session_key");
		// 用户信息校验
		boolean check = wxaUserApi.checkUserInfo(sessionKey, rawData, signature);
		if (!check) {
			Result.putValue(ResponseCode.CodeEnum.FAIL);
			log.info("用户信息校验失败,sessionId="+sessionId+",sessionJson="+sessionJson);
			return null;
		}
		// 服务端解密用户信息
		ApiResult apiResult = wxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
		if (!apiResult.isSucceed()) {
			Result.putValue(ResponseCode.CodeEnum.FAIL);
			log.info("服务端解密用户信息失败,sessionId="+sessionId+",sessionJson="+sessionJson);
			return null;
		}
		JSONObject json=JSONObject.parseObject(apiResult.getJson());
		return json;
	}

}
