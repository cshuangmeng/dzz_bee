package com.yixiang.api.article.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.yixiang.api.article.pojo.ArticleInfo;
import com.yixiang.api.article.service.ArticleInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/article")
public class ArticleInfoController {

	@Autowired
	private ArticleInfoComponent articleInfoComponent;
	
	//加载帖子详情
	@RequestMapping("/info")
	public Result getArticleDetail(@RequestParam Integer id){
		Map<String,Object> result=articleInfoComponent.getArticleDetail(id);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//加载帖子列表
	@RequestMapping("/list")
	public Result queryArticles(){
		Map<String,Object> result=articleInfoComponent.queryArticles();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//加载帖子列表
	@RequestMapping("/stat")
	public Result statArticles(){
		Map<String,Object> result=articleInfoComponent.statArticles();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//置顶帖子
	@RequestMapping("/top")
	public Result topArticle(@RequestParam Integer id){
		articleInfoComponent.topArticle(id);
		return Result.getThreadObject();
	}
	
	//编辑帖子
	@RequestMapping("/edit")
	public Result editArticle(@ModelAttribute ArticleInfo article){
		articleInfoComponent.editArticle(article);
		return Result.getThreadObject();
	}
	
	//修改帖子状态
	@RequestMapping("/state")
	public Result editArticle(@RequestParam Integer id,@RequestParam Integer state){
		articleInfoComponent.updateArticleState(id, state);
		return Result.getThreadObject();
	}
	
	//获取帖子分类
	@RequestMapping("/category/list")
	public Result queryArticleCategory(){
		JSONArray result=articleInfoComponent.queryArticleCategory();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//添加帖子分类
	@RequestMapping("/category/edit")
	public Result editArticleCategory(@RequestParam String category){
		JSONArray result=articleInfoComponent.editArticleCategory(category);
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
