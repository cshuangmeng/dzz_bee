package com.yixiang.api.article.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.article.pojo.ArticleComment;
import com.yixiang.api.util.pojo.QueryExample;

public interface ArticleCommentMapper {

	long countByExample(QueryExample example);

	int insertSelective(ArticleComment record);

	List<ArticleComment> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") ArticleComment record, @Param("example") QueryExample example);

}