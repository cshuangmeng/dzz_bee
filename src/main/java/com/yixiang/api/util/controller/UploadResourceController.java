package com.yixiang.api.util.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yixiang.api.util.Result;
import com.yixiang.api.util.service.UploadResourceComponent;

@RestController
@RequestMapping("/upload")
public class UploadResourceController {
	
	@Autowired
	private UploadResourceComponent uploadResourceComponent;

	//上传帖子相关多媒体资源
	@RequestMapping("/media")
	public Result uploadStaticMedia(@RequestParam String model,@RequestParam MultipartFile[] file){
		List<String> result=uploadResourceComponent.uploadStaticMedia(model,file);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
