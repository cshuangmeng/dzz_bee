package com.yixiang.api.util;

import java.util.HashMap;
import java.util.Map;

public class ThreadCache {

	private final static ThreadLocal<Map<Object, Object>> tl = new ThreadLocal<Map<Object, Object>>();

	public static Map<Object, Object> getDataMap() {
		Map<Object, Object> c = tl.get();
		if (null == c) {
			tl.set(new HashMap<Object, Object>());
		}
		return tl.get();
	}

	public static void setData(String key, Object value) {
		getDataMap().put(key, value);
	}

	public static Object getData(String key) {
		return getDataMap().get(key);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getHttpData() {
		return (Map<String, Object>) getDataMap().get(Constants.HTTP_PARAM);
	}

}
