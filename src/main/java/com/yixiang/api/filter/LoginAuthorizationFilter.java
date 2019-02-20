package com.yixiang.api.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.ResponseCode;

public class LoginAuthorizationFilter extends FormAuthenticationFilter {

	//这个方法是未登录需要执行的方法
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		Logger log=LoggerFactory.getLogger(getClass());
		Subject subject = getSubject(request, response);
		log.info("subject.getPrincipal()="+subject.getPrincipal());
		if (DataUtil.isEmpty(subject.getPrincipal())) {
			// 设置响应代码
			httpResponse.setStatus(ResponseCode.CodeEnum.PLEASE_LOGIN_FIRST.getValue());
			// 关闭输出流
			httpResponse.getWriter().close();
		}
		return false;
	}

}
