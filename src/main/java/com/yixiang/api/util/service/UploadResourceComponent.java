package com.yixiang.api.util.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;

@Service
public class UploadResourceComponent {

	//上传帖子图片资源
	public List<String> uploadStaticMedia(String type,MultipartFile[] files){
		//上传多媒体资源
		String str=uploadMedia(type,files);
		//组装返回结果
		JSONObject json=JSONObject.parseObject(Redis.use().get(type+Constants.OSS_CONFIG_SUFFIX));
		String imgDomain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
		String videoDomain=json.getString("domain")+"/"+json.getString("videoDir")+"/";
		String audioDomain=json.getString("domain")+"/"+json.getString("audioDir")+"/";
		List<String> media=null;
		if(StringUtils.isNotEmpty(str)){
			media=Arrays.asList(str.split(",")).stream().filter(m->StringUtils.isNotEmpty(m)).map(m->{
				if(DataUtil.isImg(m)){
					return imgDomain+m;
				}else if(DataUtil.isVideo(m)){
					return videoDomain+m;
				}else if(DataUtil.isAudio(m)){
					return audioDomain+m;
				}
				return m;
			}).collect(Collectors.toList());
		}
		return media;
	}
	
	//上传多媒体资源
	public String uploadMedia(String type,MultipartFile[] files){
		StringBuilder names=new StringBuilder();
		//多媒体资源
		if(null!=files){
			for(MultipartFile file:files){
				if(null!=file&&!file.isEmpty()){
					String saveName=new Date().getTime()+DataUtil.createNums(7);
					saveName+=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
					try {
						if(OSSUtil.uploadFileToOSS(file.getInputStream(), saveName, type+Constants.OSS_CONFIG_SUFFIX)){
							names.append(names.length()>0?","+saveName:saveName);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return names.toString();
	}
	
}
