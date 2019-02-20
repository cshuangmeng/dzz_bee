package com.yixiang.api.article.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.jfinal.plugin.redis.Redis;
import com.yixiang.api.article.mapper.ArticleInfoMapper;
import com.yixiang.api.article.pojo.ArticleInfo;
import com.yixiang.api.util.Constants;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.OSSUtil;
import com.yixiang.api.util.ResponseCode;
import com.yixiang.api.util.Result;
import com.yixiang.api.util.ThreadCache;
import com.yixiang.api.util.pojo.Config;
import com.yixiang.api.util.pojo.QueryExample;
import com.yixiang.api.util.service.ConfigComponent;

@Service
public class ArticleInfoComponent {

	@Autowired
	private ArticleInfoMapper articleInfoMapper;
	@Autowired
	private ConfigComponent configComponent;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	//编辑帖子
	@Transactional
	public void editArticle(ArticleInfo article){
		if(null!=article.getId()&&article.getId()>0){//更新
			article.setUuid(null);
			article.setUpdateTime(new Date());
			updateArticleInfo(article);
		}else{//保存
			article.setSource(ArticleInfo.ARTICLE_SOURCE_ENUM.SYSTEM.getSource());
			article.setUuid(DataUtil.buildUUID());
			article.setState(ArticleInfo.ARTICLE_STATE_ENUM.TONGGUO.getState());
			article.setCreateTime(new Date());
			insertSelective(article);
		}
	}
	
	//帖子分类列表
	public JSONArray queryArticleCategory(){
		return JSONArray.parseArray(Redis.use().get("article_category_list"));
	}
	
	//添加帖子分类
	public JSONArray editArticleCategory(String category){
		Config config=configComponent.getConfig("article_category_list");
		JSONArray array=JSONArray.parseArray(config.getContent());
		//已存在一样的分类则不添加
		Optional<Object> ip=array.stream().filter(i->JSONObject.parseObject(i.toString()).getString("category").equals(category)).findAny();
		if(!ip.isPresent()){
			Integer nextId=JSONObject.parseObject(array.stream()
					.max(Comparator.comparing(i->JSONObject.parseObject(i.toString()).getInteger("id"))).get().toString()).getInteger("id");
			array.add(JSONObject.parseObject(JSONObject.toJSONString(DataUtil.mapOf("id",nextId+1,"category",category,"state",0))));
			//更新配置
			config.setContent(array.toJSONString());
			configComponent.updateConfig(config);
			//刷新缓存
			Redis.use().set(config.getTitle(), config.getContent());
		}
		return queryArticleCategory();
	}
	
	//获取帖子列表
	public Map<String,Object> queryArticles(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		example.and().andNotIn("state", Arrays.asList(ArticleInfo.ARTICLE_STATE_ENUM.XITONGSHANCHU.getState()
				,ArticleInfo.ARTICLE_STATE_ENUM.GERENSHANCHU.getState())).andEqualTo("source", param.get("source"));
		if(!DataUtil.isEmpty(param.get("startDate"))){
			Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
			example.and().andGreaterThanOrEqualTo("create_time", startDate);
		}
		if(!DataUtil.isEmpty(param.get("endDate"))){
			Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
			endDate=DateUtils.addDays(endDate, 1);
			example.and().andLessThan("create_time", endDate);
		}
		//获取数据总条数
		Long total=countByExample(example);
		example.setOffset(limit*(page>0?page-1:0));
		example.setLimit(limit);
		example.setOrderByClause("create_time desc");
		JSONArray articleTypes=JSONArray.parseArray(Redis.use().get("article_category_list"));
		JSONObject oss=JSONObject.parseObject(Redis.use().get("article_oss_config"));
		List<Map<Object,Object>> dataset=selectByExample(example).stream().map(i->{
			String createTime=DateUtil.toString(i.getCreateTime(), DatePattern.COMMON_DATE);
			Optional<Object> atop=articleTypes.stream()
					.filter(o->JSONObject.parseObject(o.toString()).getInteger("id").equals(i.getCategory())).findAny();
			return DataUtil.mapOf("id",i.getId(),"category",atop.isPresent()?JSONObject.parseObject(atop.get().toString()).get("category"):null
					,"title",i.getTitle(),"content",i.getContent(),"icon",OSSUtil.joinOSSFileUrl(oss, i.getIcon())
					,"state",i.getState(),"createTime",createTime,"source",i.getSource(),"remark",i.getRemark());
		}).collect(Collectors.toList());
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//置顶帖子
	public void topArticle(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			ArticleInfo update=new ArticleInfo();
			update.setTopTime(new Date());
			updateByExampleSelective(update, example);
		}
	}
	
	//统计帖子访问情况
	public Map<String,Object> statArticles(){
		Map<String,Object> param=ThreadCache.getHttpData();
		Integer page=Integer.valueOf(param.getOrDefault("page", 0).toString());
		Integer limit=Integer.valueOf(param.getOrDefault("limit", Constants.DEFAULT_PAGE_SIZE).toString());
		QueryExample example=new QueryExample();
		List<Integer> states=Arrays.asList(ArticleInfo.ARTICLE_STATE_ENUM.DAISHENHE.getState()
				,ArticleInfo.ARTICLE_STATE_ENUM.TONGGUO.getState(),ArticleInfo.ARTICLE_STATE_ENUM.BUTONGGUO.getState());
		example.and().andIn("state", states);
		param.put("states", states);
		if(!DataUtil.isEmpty(param.get("startDate"))){
			Date startDate=DateUtil.toDate(param.get("startDate").toString(), DatePattern.COMMON_DATE);
			param.put("startDate", startDate);
		}
		if(!DataUtil.isEmpty(param.get("endDate"))){
			Date endDate=DateUtil.toDate(param.get("endDate").toString(), DatePattern.COMMON_DATE);
			endDate=DateUtils.addDays(endDate, 1);
			param.put("endDate", endDate);
		}
		//获取数据总条数
		Long total=countByExample(example);
		param.put("offset",limit*(page>0?page-1:0));
		param.put("limit",limit);
		JSONArray articleTypes=JSONArray.parseArray(Redis.use().get("article_category_list"));
		List<Map<String,Object>> dataset=statArticleBrowseData(param);
		dataset.stream().forEach(i->{
			Date topTime=(Date)i.get("topTime");
			Optional<Object> atop=articleTypes.stream().filter(o->JSONObject.parseObject(o.toString()).getInteger("id")
					.equals(Integer.valueOf(i.get("category").toString()))).findAny();
			i.put("category",atop.isPresent()?JSONObject.parseObject(atop.get().toString()).get("category"):null);
			if(topTime.compareTo(Constants.DEFAULT_DATE_TIME)<=0){
				i.remove("topTime");
			}
		});
		return DataUtil.mapOf("total",total,"dataset",dataset);
	}
	
	//加载帖子详情
	public Map<String,Object> getArticleDetail(Integer id){
		ArticleInfo article=getArticleInfo(id);
		List<Integer> states=Arrays.asList(ArticleInfo.ARTICLE_STATE_ENUM.XITONGSHANCHU.getState()
				,ArticleInfo.ARTICLE_STATE_ENUM.GERENSHANCHU.getState());
		if(null==article||states.contains(article.getState())){
			log.info("文章信息不存在或状态不正确,id="+id+",state="+(null!=article?article.getState():null));
			Result.putValue(ResponseCode.CodeEnum.ARTICLE_NOT_EXISTS);
			return null;
		}
		//组装文章信息
		Map<String,Object> result=DataUtil.objectToMap(article);
		JSONObject oss=JSONObject.parseObject(Redis.use().get("article_oss_config"));
		result.put("media", OSSUtil.joinOSSFileUrl(oss, article.getMedia().split(",")));
		result.put("icon", OSSUtil.joinOSSFileUrl(oss, article.getIcon()));
		result.put("shareIcon", OSSUtil.joinOSSFileUrl(oss, article.getShareIcon()));
		return result;
	}
	
	//获取文章信息
	public ArticleInfo getArticleInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<ArticleInfo> articles=selectByExample(example);
			return articles.size()>0?articles.get(0):null;
		}
		return null;
	}
	
	//获取文章信息
	public ArticleInfo getArticleInfoByUUID(String uuid){
		if(StringUtils.isNotEmpty(uuid)){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("uuid", uuid);
			List<ArticleInfo> articles=selectByExample(example);
			return articles.size()>0?articles.get(0):null;
		}
		return null;
	}
	
	//更新文章状态
	@Transactional
	public void updateArticleState(Integer id,Integer state){
		ArticleInfo article=new ArticleInfo();
		article.setId(id);
		article.setState(state);
		updateArticleInfo(article);
	}
	
	//更新文章信息
	@Transactional
	public int updateArticleInfo(ArticleInfo article){
		if(null!=article&&null!=article.getId()&&article.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", article.getId());
			return updateByExampleSelective(article, example);
		}
		return 0;
	}
	
	//加载文章列表
	public List<Map<String,Object>> queryArticles(Map<String,Object> param){
		return articleInfoMapper.queryArticles(param);
	}
	
	//统计文章访问情况
	public List<Map<String,Object>> statArticleBrowseData(Map<String,Object> param){
		return articleInfoMapper.statArticleBrowseData(param);
	}
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return articleInfoMapper.countByExample(example);
	}

	//保存
	@Transactional
	public int insertSelective(ArticleInfo record) {
		return articleInfoMapper.insertSelective(record);
	}
	
	//获取结果集
	public List<ArticleInfo> selectByExample(QueryExample example) {
		return articleInfoMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(ArticleInfo record, QueryExample example) {
		return articleInfoMapper.updateByExampleSelective(record, example);
	}

}
