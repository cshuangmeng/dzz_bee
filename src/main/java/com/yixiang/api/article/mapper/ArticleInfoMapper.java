package com.yixiang.api.article.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.article.pojo.ArticleInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface ArticleInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(ArticleInfo record);

	List<ArticleInfo> selectByExample(QueryExample example);
	
	List<Map<String,Object>> queryArticles(@Param("param")Map<String,Object> param);
	
	List<Map<String,Object>> statArticleBrowseData(@Param("param")Map<String,Object> param);

	int updateByExampleSelective(@Param("record") ArticleInfo record, @Param("example") QueryExample example);
	

}