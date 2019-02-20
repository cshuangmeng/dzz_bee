package com.yixiang.api.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	static Properties prop = new Properties();
	private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

	static {
		try {
			prop.load(PropertiesUtil.class.getResourceAsStream("/config.properties"));
			log.info("config.properties loaded!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读取属性
	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

}
