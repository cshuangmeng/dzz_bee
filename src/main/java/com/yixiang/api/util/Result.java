package com.yixiang.api.util;

import com.mysql.jdbc.StringUtils;
import com.yixiang.api.util.ResponseCode.CodeEnum;

public class Result {

	private Integer code = ResponseCode.CodeEnum.SUCCESS.getValue();
	private String msg = "";
	private Object data;

	private static ThreadLocal<Result> threadResult = new ThreadLocal<Result>();

	// 每个线程一个Result对象
	public static synchronized Result getThreadObject() {
		if (null == threadResult.get()) {
			threadResult.set(new Result());
		}
		return threadResult.get();
	}

	public static void clear() {
		threadResult.remove();
	}

	public static void putValue(Integer code) {
		putValue(code, null);
	}

	public static void putValue(Object data) {
		putValue(ResponseCode.CodeEnum.SUCCESS.getValue(), data);
	}

	public static void putValue(CodeEnum data) {
		putValue(data.getValue(), null);
	}

	public static void putValue(Integer code, Object data) {
		Result result = getThreadObject();
		result.code = code;
		result.msg = ResponseCode.getNameByValue(code);
		result.data = data;
	}

	public static void putValue(Integer code, String msg, Object data) {
		Result result = getThreadObject();
		result.code = code;
		result.msg = msg;
		result.data = data;
	}

	public static void putValue(Integer code, Object data, Object... params) {
		Result result = getThreadObject();
		result.code = code;
		result.msg = ResponseCode.getNameByValue(code);
		result.data = data;
		if (!StringUtils.isNullOrEmpty(result.msg)) {
			for (int i = 1; i <= params.length; i++) {
				result.msg = result.msg.replace("${" + (i) + "}", params[i - 1].toString());
			}
		}
	}

	public static boolean noError() {
		return getThreadObject().getCode().equals(ResponseCode.CodeEnum.SUCCESS.getValue());
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return StringUtils.isNullOrEmpty(msg) ? ResponseCode.getNameByValue(code) : msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
