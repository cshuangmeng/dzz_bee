package com.yixiang.api.util;

public class ResponseCode {

	public static enum CodeEnum {
		// 系统级别
		SUCCESS(0, "操作成功"), 
		FAIL(100001, "操作失败"),
		SYSTEM_EXCEPTION(100002, "系统异常"),
		REQUIRED_PARAM_NULL(100003, "缺少参数"), 
		PARAM_SIGN_INCORRECT(100004, "签名不正确"), 
		PARAM_FORMAT_INCORRECT(100005, "参数格式不正确"),
		PARAM_INCORRECT(100006,"参数不正确"),
		MUCH_DATA_FAIL(100007,"数据量过大"),
		PAY_WAY_INCORRECT(100008,"支付方式不正确"),
		NOT_REFUNDABLE_PRICE(100009,"可退金额不足"),
		PLEASE_LOGIN_FIRST(100010,"请重新登录"),

		// 用户基本属性相关
		USER_PHONE_BLACK(200001, "账户状态不正常"),
		USER_NOT_EXISTS(200002, "用户不存在"), 
		USER_AUTH_FAIL(200003, "用户身份验证失败"),
		USER_PASSWORD_INCORRECT(200004, "密码输入有误"),
		USER_PHONE_INCORRECT(200005, "请输入正确的手机号"),
		VERIFY_CODE_LIVE(200006, "验证码还未过期"),
		CODE_SEND_FAILED(200007, "验证码发送失败"),
		VERIFY_CODE_INCORRECT(200008, "验证码错误"),
		ALREADY_FOLLOWED_USER(200009, "已关注该用户"),
		ALREADY_CANCELED_FOLLOW(200010, "已取关该用户"),
		FOLLOW_NOT_YET(200011, "还未关注该用户"),
		ALREADY_EVALUATED_USER(200012, "已评价该用户"),
		CAR_EXCEED_MAX(200013, "车辆数量已超限额"),
		CHARGING_EXCEED_MAX(200014, "充电桩数量已超限额"),
		CAN_NOT_FOLLOW_MYSELF(200015, "不能关注自己"),
		ALREADY_COLLECTED_CHARGING(200016, "已收藏该充电桩"),
		ALREADY_CANCELED_CHARGING(200017, "已取消收藏该充电桩"),
		COLLECT_NOT_YET(200018, "还未收藏该充电桩"),
		BALANCE_NOT_ENOUGH(200019, "账户余额不足"),
		
		// 充值相关
		TEMPLATE_NOT_EXISTS(210001, "充值模板不存在"),
		TEMPLATE_NOT_ENABLE(210002, "充值模板不可用"),
		RECHARGE_NOT_EXISTS(210003, "充值记录不存在"),
		RECHARGE_STATE_INCORRECT(210004, "充值记录状态不正确"),
		
		// 充电相关
		ORDER_NOT_EXISTS(220001, "订单不存在"),
		ORDER_STATE_INCORRECT(220002, "订单状态不正确"),
		EXISTS_CHARGING_ORDER(220003, "存在进行中的订单"),
		ORDER_NOT_MINE(220004, "不能操作非本人的订单"),
		PAY_PRICE_INCORRECT(220005, "订单应付金额不正确"),
		CHARGE_SERVICE_UNAVAILABLE(220006, "充电服务暂不可用"),
		
		// 文章
		ARTICLE_NOT_EXISTS(300001, "文章信息不存在"),
		ALREADY_PRAISED_ARTICLE(300002, "已点赞该文章"),
		ALREADY_CANCELED_PRAISE(300003, "已取消点赞该文章"),
		PRAISE_NOT_YET(300004, "尚未点赞该文章"),
		ARTICLE_NOT_MINE(300005, "非本人的文章"),
		
		// 充电桩
		STATION_NOT_EXISTS(400001, "充电桩信息不存在"),
		STATION_NOT_MINE(400002, "非本人的充电桩"),
		STATION_STATE_INCORRECT(400003, "充电桩状态不正确"),
		
		// 优惠券
		COUPON_NOT_EXISTS(500001, "优惠券不存在"),
		COUPON_NOT_MINE(500002, "非本人的优惠券"),
		COUPON_STATE_INCORRECT(500003, "优惠券状态不正确"),
		COUPON_TIME_INCORRECT(500004, "不在优惠券可使用有效期内"),
		COUPON_NOT_MATCH(500005, "优惠券不符合使用条件"),
		EXCHANGE_NOT_OPEN(500006, "积分墙兑换功能已关闭"),
		EXCHANGE_APPID_INCORRECT(500007, "appId不正确"),
		EXCHANGE_MONEY_INCORRECT(500008, "兑换金额不正确"),
		EXCHANGE_CODE_INCORRECT(500009, "输入有误，请重新输入"),
		EXCHANGE_STATE_INCORRECT(500010, "兑换码状态不正确"),
		EXCHANGE_CODE_INVALID(500011, "兑换码已失效"),
		EXCHANGE_COUPON_INCORRECT(500012, "非现金充值券"),
		EXCHANGE_COUPON_SUCCESS(500013, "兑换成功！"),
		EXCHANGE_COUPON_FAIL(500014, "兑换失败！");

		int value;
		String name;

		CodeEnum(int value, String name) {
			this.value = value;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}
	
	// 获取key对应显示的字符
	public static String getNameByValue(int value) {
		for (CodeEnum typeEnum : CodeEnum.values()) {
			if (typeEnum.value == value) {
				return typeEnum.name;
			}
		}
		throw new IllegalArgumentException("No element matches " + value);
	}
	
}
