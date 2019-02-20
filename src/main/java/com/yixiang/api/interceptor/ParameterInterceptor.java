package com.yixiang.api.interceptor;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.feilong.core.net.ParamUtil;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;

import net.sf.json.JSONObject;

public class ParameterInterceptor extends HandlerInterceptorAdapter{
	
	private Logger log=LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
		//拼装请求参数
		Enumeration<?> pNames = request.getParameterNames();
        Map<String, String> params = new HashMap<String, String>();
        while (pNames.hasMoreElements()) {
            String pName = (String) pNames.nextElement();
            params.put(pName, request.getParameter(pName));
        }
        String queryString=ParamUtil.toQueryStringUseSingleValueMap(params);
        //读取请求体数据
        int b=0;
        byte[] bytes=new byte[1024];
        StringBuilder body=new StringBuilder();
        InputStream is=request.getInputStream();
        while((b=is.read(bytes))!=-1){
        	body.append(new String(bytes,0,b,"UTF-8"));
        }
        if(body.length()>0){
        	ThreadCache.setData(Constants.REQUEST_BODY, body.toString());
        }
        //保存至线程中
        ThreadCache.setData(Constants.HTTP_PARAM, params);
        ThreadCache.setData(Constants.IP, DataUtil.getIpAddr(request));
        log.info(Thread.currentThread().getName()+" --- "
				+request.getRequestURI()+(queryString.length()>0?"?"+queryString:"")+",body="+body.toString());
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info(Thread.currentThread().getName()+" --- "+JSONObject.fromObject(Result.getThreadObject()));
		Result.clear();
		super.afterCompletion(request, response, handler, ex);
	}
	
}
