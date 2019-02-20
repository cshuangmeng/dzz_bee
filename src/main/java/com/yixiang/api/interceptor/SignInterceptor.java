package com.yixiang.api.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.SignUtil;
import com.yixiang.api.util.ThreadCache;

public class SignInterceptor extends HandlerInterceptorAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 校验签名是否正确
		Map<String, Object> httpParam = (Map<String, Object>) ThreadCache.getData(Constants.HTTP_PARAM);
		if (!DataUtil.isEmpty(httpParam.get("system")) && !DataUtil.isEmpty(httpParam.get("imei"))) {
			httpParam.put("key", Redis.use().get("app_api_key"));
		} else if (!DataUtil.isEmpty(httpParam.get("wxOpenId"))) {
			httpParam.put("key", Redis.use().get("js_api_key"));
		}
		String reqSign = !DataUtil.isEmpty(httpParam.get("sign")) ? httpParam.get("sign").toString() : "";
		httpParam.remove("sign");
		String sign = SignUtil.sign(httpParam, "MD5");
		boolean flag = sign.equals(reqSign);
		if (!flag) {
			Result.putValue(ResponseCode.CodeEnum.PARAM_SIGN_INCORRECT);
			response.setContentType("text/json;charset=UTF-8");
			response.getWriter().print(JSONObject.toJSONString(Result.getThreadObject()));
		}
		return flag;
	}

}
