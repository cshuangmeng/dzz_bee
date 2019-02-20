package com.yixiang.api.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.yixiang.api.user.pojo.UserDevice;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserDeviceComponent;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;

public class LoginInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserInfoComponent userInfoComponent;
	@Autowired
	private UserDeviceComponent userDeviceComponent;

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 拼装请求参数
		Map<String,Object> param=(Map<String,Object>)ThreadCache.getData(Constants.HTTP_PARAM);
		String phone=!DataUtil.isEmpty(param.get(Constants.PHONE))?param.get(Constants.PHONE).toString():null;
		String imei=!DataUtil.isEmpty(param.get(Constants.IMEI))?param.get(Constants.IMEI).toString():null;
		String system=!DataUtil.isEmpty(param.get(Constants.SYSTEM))?param.get(Constants.SYSTEM).toString():null;
		String openId=!DataUtil.isEmpty(param.get(Constants.WXOPENID))?param.get(Constants.WXOPENID).toString():null;
		boolean flag = false;
		UserInfo user = null;
		UserDevice device = null;
		if (StringUtils.isNotEmpty(phone)) {// APP登录
			if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(system)) {
				user = userInfoComponent.getUserInfoByPhone(phone);
				if (null != user) {
					device = userDeviceComponent.getUserDeviceByImeiAndSystem(user.getId(), imei, system);
					flag = null != device && user.getDeviceId().equals(device.getId());
				}
			} else if (StringUtils.isNotEmpty(openId)) {// 微信登录
				user = userInfoComponent.getUserInfoByPhone(phone);
				if (null != user) {
					device = userDeviceComponent.getUserDeviceByOpenId(user.getId(), openId);
					flag = null != device && user.getDeviceId().equals(device.getId());
				}
			} else {
				Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
			}
		} else {
			Result.putValue(ResponseCode.CodeEnum.REQUIRED_PARAM_NULL);
		}
		if (Result.noError()) {
			if (!flag) {// 用户信息不存在
				Result.putValue(ResponseCode.CodeEnum.USER_AUTH_FAIL);
			} else if (user.getState().equals(UserInfo.USER_STATE_ENUM.YIDONGJIE.getState())) {// 黑名单用户
				Result.putValue(ResponseCode.CodeEnum.USER_PHONE_BLACK);
				flag = false;
			}
		}
		if (flag) {
			ThreadCache.setData(Constants.USER, user);
		} else {
			response.setContentType("text/json;charset=UTF-8");
			response.getWriter().print(JSONObject.toJSONString(Result.getThreadObject()));
		}
		return flag;
	}

}
