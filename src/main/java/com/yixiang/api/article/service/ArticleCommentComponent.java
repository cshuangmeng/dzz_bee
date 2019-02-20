package com.yixiang.api.article.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.article.mapper.ArticleCommentMapper;
import com.yixiang.api.article.pojo.ArticleComment;
import com.yixiang.api.article.pojo.ArticleInfo;
import com.yixiang.api.user.pojo.UserInfo;
import com.yixiang.api.user.service.UserInfoComponent;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ArticleCommentComponent {

	@Autowired
	private ArticleCommentMapper articleCommentMapper;
	@Autowired
	private ArticleInfoComponent articleInfoComponent;
	@Autowired
	private UserInfoComponent userInfoComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//发布新评论
	public void saveArticleComment(String uuid,ArticleComment comment,MultipartFile[] files){
		ArticleInfo article=articleInfoComponent.getArticleInfoByUUID(uuid);
		if(null==article||!article.getState().equals(ArticleInfo.ARTICLE_STATE_ENUM.TONGGUO.getState())){
			log.info("文章信息不存在,uuid="+uuid);
			Result.putValue(ResponseCode.CodeEnum.ARTICLE_NOT_EXISTS);
			return;
		}
		UserInfo user=(UserInfo)ThreadCache.getData(Constants.USER);
		StringBuilder names=new StringBuilder();
		//多媒体资源
		if(null!=files){
			for(MultipartFile file:files){
				if(null!=file&&!file.isEmpty()){
					String saveName=new Date().getTime()+DataUtil.createNums(7);
					saveName+=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
					try {
						if(OSSUtil.uploadFileToOSS(file.getInputStream(), saveName, "article_oss_config")){
							names.append(names.length()>0?","+saveName:saveName);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		comment.setUserId(user.getId());
		comment.setArticleId(article.getId());
		comment.setMedia(names.toString());
		comment.setCreateTime(new Date());
		insertSelective(comment);
	}
	
	//加载文章评论列表
	public Map<String,Object> getCommentsOfArticle(String uuid,Integer page){
		ArticleInfo article=articleInfoComponent.getArticleInfoByUUID(uuid);
		List<Map<Object,Object>> comments=null;
		Long total=null;
		if(null!=article&&article.getState().equals(ArticleInfo.ARTICLE_STATE_ENUM.TONGGUO.getState())){
			JSONObject json=JSONObject.parseObject(Redis.use().get("user_oss_config"));
			String domain=json.getString("domain")+"/"+json.getString("imgDir")+"/";
			json=JSONObject.parseObject(Redis.use().get("article_comment_list_config"));
			//计算评论总条数
			QueryExample example=new QueryExample();
			example.and().andEqualTo("article_id", article.getId()).andEqualTo("state", ArticleComment.COMMENT_STATE_ENUM.TONGGUO.getState());
			total=countByExample(example);
			//拼装评论数据
			example.setOffset((page-1)*json.getInteger("size"));
			example.setLimit(json.getInteger("size"));
			example.setOrderByClause("create_time desc");
			json=JSONObject.parseObject(Redis.use().get("article_oss_config"));
			String domain1=json.getString("domain")+"/"+json.getString("imgDir")+"/";
			String domain2=json.getString("domain")+"/"+json.getString("videoDir")+"/";
			comments=selectByExample(example).stream().map(c->{
				UserInfo user=userInfoComponent.getUserInfo(c.getId(),false);
				//标签
				List<String> tags=null;
				if(StringUtils.isNotEmpty(c.getTags())){
					tags=Arrays.asList(c.getTags().split(",")).stream().filter(t->StringUtils.isNotEmpty(t)).collect(Collectors.toList());
				}
				//多媒体
				List<String> media=null;
				if(StringUtils.isNotEmpty(c.getMedia())){
					media=Arrays.asList(c.getMedia().split(",")).stream().filter(m->StringUtils.isNotEmpty(m))
							.map(m->String.valueOf(DataUtil.isImg(m)?domain1+m:DataUtil.isVideo(m)?domain2+m:m)).collect(Collectors.toList());
				}
				return DataUtil.mapOf("headImg",StringUtils.isNotEmpty(user.getHeadImg())?domain+user.getHeadImg():user.getHeadImg()
						,"userName",user.getUserName(),"content",c.getContent(),"tags",tags,"media",media,"createTime"
						,DateUtil.toString(c.getCreateTime(), DatePattern.COMMON_DATE_AND_TIME_WITHOUT_YEAR_AND_SECOND));
			}).collect(Collectors.toList());
		}
		return DataUtil.mapOf("total",total,"comments",comments); 
	}
	
	//获取文章评论信息
	public ArticleComment getArticleComment(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<ArticleComment> comments=selectByExample(example);
			return comments.size()>0?comments.get(0):null;
		}
		return null;
	}
	
	//更新文章评论信息
	public int updateArticleComment(ArticleComment comment){
		if(null!=comment.getId()&&comment.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", comment.getId());
			return updateByExampleSelective(comment, example);
		}
		return 0;
	}
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return articleCommentMapper.countByExample(example);
	}

	//保存
	public int insertSelective(ArticleComment record) {
		return articleCommentMapper.insertSelective(record);
	}
	
	//获取结果集
	public List<ArticleComment> selectByExample(QueryExample example) {
		return articleCommentMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(ArticleComment record, QueryExample example) {
		return articleCommentMapper.updateByExampleSelective(record, example);
	}

}
