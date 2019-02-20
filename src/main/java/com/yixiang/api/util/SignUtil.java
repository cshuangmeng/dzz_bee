package com.yixiang.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.feilong.core.CharsetType;

public class SignUtil {

	public static String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean signVerify(String appSecret, Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appSecret", appSecret);

		for (String key : params.keySet()) {
			if (!key.equals("sign")) {
				map.put(key, params.get(key));
			}
		}

		String sign = sign(map, "MD5");
		if (sign.equals(params.get("sign"))) {
			return true;
		}
		return false;
	}

	private static String toHexValue(byte[] messageDigest) {
		if (messageDigest == null)
			return "";
		StringBuilder hexValue = new StringBuilder();
		for (byte aMessageDigest : messageDigest) {
			int val = 0xFF & aMessageDigest;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	public static String sign(Map<String, Object> params, String encrypt) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String string = "";
		for (String s : keys) {
			if(!DataUtil.isEmpty(params.get(s))){
				string += params.get(s);
			}
		}
		String sign = "";
		try {
			if (encrypt.equals("MD5")) {
				sign = toHexValue(encryptMD5(string.getBytes(Charset.forName("utf-8"))));
			} else if (encrypt.equals("SHA1")) {
				sign = toHexValue(encryptSHA1(string.getBytes(Charset.forName("utf-8"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("md5 error");
		}
		return sign;
	}

	private static byte[] encryptMD5(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	public static String signValue(Map<String, String> params, String encrypt) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String string = "";
		for (String s : keys) {
			string += string.length() > 0 ? "&" + s + "=" + params.get(s) : s + "=" + params.get(s);
		}
		String sign = "";
		try {
			if (encrypt.equals("MD5")) {
				sign = toHexValue(encryptMD5(string.getBytes(Charset.forName("utf-8"))));
			} else if (encrypt.equals("SHA1")) {
				sign = toHexValue(encryptSHA1(string.getBytes(Charset.forName("utf-8"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sha1 error");
		}
		return sign;
	}

	public static String getSignString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String string = "";
		for (String s : keys) {
			string += string.length() > 0 ? "&" + s + "=" + params.get(s) : s + "=" + params.get(s);
		}
		return string;
	}

	public static String getSignEncodeUrl(Map<String, String> map, boolean isEncode) {
		String sign = map.get("sign");
		String encodedSign = "";
		if (null != map) {
			map.remove("sign");
			List<String> keys = new ArrayList<String>(map.keySet());
			// key排序
			Collections.sort(keys);
			StringBuilder authInfo = new StringBuilder();
			boolean first = true;// 是否第一个
			for (String key : keys) {
				if (first) {
					first = false;
				} else {
					authInfo.append("&");
				}
				authInfo.append(key).append("=");
				if (isEncode) {
					try {
						authInfo.append(URLEncoder.encode(map.get(key), CharsetType.UTF8));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					authInfo.append(map.get(key));
				}
			}
			try {
				encodedSign = authInfo.toString() + "&sign=" + URLEncoder.encode(sign, CharsetType.UTF8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return encodedSign.replaceAll("\\+", "%20");
	}

	public static String signValue(Map<String, Object> params, String encrypt, String key) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String string = "";
		for (String s : keys) {
			if (null != params.get(s) && StringUtils.isNotEmpty(params.get(s).toString())) {
				string += string.length() > 0 ? "&" + s + "=" + params.get(s) : s + "=" + params.get(s);
			}
		}
		string += string.length() > 0 ? "&key=" + key : "key=" + key;
		String sign = "";
		try {
			if (encrypt.equals("MD5")) {
				sign = toHexValue(encryptMD5(string.getBytes(Charset.forName("utf-8"))));
			} else if (encrypt.equals("SHA1")) {
				sign = toHexValue(encryptSHA1(string.getBytes(Charset.forName("utf-8"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sha1 error");
		}
		return sign;
	}

	private static byte[] encryptSHA1(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("SHA1");
		md5.update(data);
		return md5.digest();
	}

}
