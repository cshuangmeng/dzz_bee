package com.yixiang.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.jfinal.plugin.redis.Redis;

public class OSSUtil {

	// 上传本地文件至OSS存储
	public static boolean uploadFileToOSS(String file, String saveName, String oss) {
		try {
			JSONObject json = JSONObject.parseObject(Redis.use().get(oss));
			OSSClient client = new OSSClient(json.getString("endpoint"), json.getString("accesskeyid"),
					json.getString("secretaccesskey"));
			InputStream is = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(is.available());
			saveName = StringUtils.isNotEmpty(saveName) ? saveName : file.substring(file.lastIndexOf("/") + 1);
			if (DataUtil.isImg(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("imgDir") + "/" + saveName, is, meta);
			} else if (DataUtil.isVideo(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("videoDir") + "/" + saveName, is, meta);
			}
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 上传本地文件至OSS存储
	public static boolean uploadFileToOSS(File file, String saveName, String oss) {
		try {
			JSONObject json = JSONObject.parseObject(Redis.use().get(oss));
			OSSClient client = new OSSClient(json.getString("endpoint"), json.getString("accesskeyid"),
					json.getString("secretaccesskey"));
			InputStream is = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(is.available());
			saveName = StringUtils.isNotEmpty(saveName) ? saveName
					: file.getName().substring(file.getName().lastIndexOf("/") + 1);
			if (DataUtil.isImg(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("imgDir") + "/" + saveName, is, meta);
			} else if (DataUtil.isVideo(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("videoDir") + "/" + saveName, is, meta);
			}
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 上传本地文件至OSS存储
	public static boolean uploadFileToOSS(InputStream is, String saveName, String oss) {
		try {
			JSONObject json = JSONObject.parseObject(Redis.use().get(oss));
			OSSClient client = new OSSClient(json.getString("endpoint"), json.getString("accesskeyid"),
					json.getString("secretaccesskey"));
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(is.available());
			if (DataUtil.isImg(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("imgDir") + "/" + saveName, is, meta);
			} else if (DataUtil.isVideo(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("videoDir") + "/" + saveName, is, meta);
			} else if (DataUtil.isAudio(saveName)) {
				client.putObject(json.getString("bucketname"), json.getString("audioDir") + "/" + saveName, is, meta);
			}
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//拼装全路径
	public static String joinOSSFileUrl(JSONObject oss,String file){
		if(StringUtils.isNotEmpty(file)&&null!=oss){
			if (DataUtil.isImg(file)) {
				String imgDomain=oss.getString("domain")+"/"+oss.get("imgDir");
				return imgDomain+"/"+file;
			} else if (DataUtil.isVideo(file)) {
				String videoDomain=oss.getString("domain")+"/"+oss.get("videoDir");
				return videoDomain+"/"+file;
			} else if(DataUtil.isAudio(file)){
				String audioDomain=oss.getString("domain")+"/"+oss.getString("audioDir");
				return audioDomain+"/"+file;
			}
		}
		return file;
	}
	
	//拼装全路径
	public static List<String> joinOSSFileUrl(JSONObject oss,String... file){
		return Arrays.asList(file).stream().filter(i->StringUtils.isNotEmpty(i))
				.map(i->joinOSSFileUrl(oss,i)).collect(Collectors.toList());
	}

}
