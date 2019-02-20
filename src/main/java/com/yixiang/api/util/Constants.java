package com.yixiang.api.util;

import java.util.Date;

import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;

public class Constants {

	// 跨域访问域名
	public final static String TRUST_CROSS_ORIGINS = "*";
	// 请求body内容存储名称
	public final static String REQUEST_BODY = "request_body_data";
	// 请求参数存储名称
	public final static String HTTP_PARAM = "http_param";
	// 用户信息存储名称
	public final static String USER = "user_data";
	// 请求ip字段
	public final static String IP = "ip";
	// 请求手机号字段
	public final static String PHONE = "phone";
	// 请求软件名称字段
	public final static String SYSTEM = "system";
	// 请求设备字段
	public final static String IMEI = "imei";
	// 请求微信openid字段
	public final static String WXOPENID = "wxOpenId";
	// 微信小程序登录session存储名称
	public final static String WXA_SESSION = "wxa-sessionid";
	// 微信小程序登录session存储前缀
	public final static String WXA_SESSION_PREFIX = "wxa:session:";
	// OSS存储配置名称后缀
	public final static String OSS_CONFIG_SUFFIX = "_oss_config";
	// 列表默认每页显示的数据条数
	public final static Integer DEFAULT_PAGE_SIZE = 10;
	// 日期默认值
	public final static Date DEFAULT_DATE_TIME = DateUtil.toDate("1000-10-10 00:00:00", DatePattern.COMMON_DATE_AND_TIME);
	// 微信支付
	public static final Integer WEIXINPAY = 1;
	// 支付宝支付
	public static final Integer ALIPAY = 2;
	// 余额支付
	public static final Integer BALANCEPAY = 3;
	// 是
	public final static Integer YES = 1;
	// 否
	public final static Integer NO = 0;

}
