package com.yixiang.api.util;

import java.util.Map;

import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;

public class OgnlUtil {
	
	// 运算表达式
	public static boolean ognl(String expression, Map<String, Object> condition) {
		try {
			return (boolean) Ognl.getValue(expression, condition);
		} catch (OgnlException e) {
			e.printStackTrace();
		}
		return false;
	}

}
