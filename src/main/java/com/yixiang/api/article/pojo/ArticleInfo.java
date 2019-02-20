package com.yixiang.api.article.pojo;

import java.util.Date;

public class ArticleInfo {

	private Integer id;
	private Integer userId;
	private Integer carId;
	private Integer category;
	private String uuid;
	private String icon;
	private String shareIcon;
	private Integer visitors;
	private Integer comments;
	private Integer praises;
	private String title;
	private String content;
	private Integer source;
	private String media;
	private String remark;
	private Integer state;
	private Date createTime;
	private Date updateTime;
	private Date topTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCarId() {
		return carId;
	}

	public void setCarId(Integer carId) {
		this.carId = carId;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid == null ? null : uuid.trim();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon == null ? null : icon.trim();
	}

	public String getShareIcon() {
		return shareIcon;
	}

	public void setShareIcon(String shareIcon) {
		this.shareIcon = shareIcon == null ? null : shareIcon.trim();
	}

	public Integer getVisitors() {
		return visitors;
	}

	public void setVisitors(Integer visitors) {
		this.visitors = visitors;
	}

	public Integer getComments() {
		return comments;
	}

	public void setComments(Integer comments) {
		this.comments = comments;
	}

	public Integer getPraises() {
		return praises;
	}

	public void setPraises(Integer praises) {
		this.praises = praises;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getTopTime() {
		return topTime;
	}

	public void setTopTime(Date topTime) {
		this.topTime = topTime;
	}

	// 状态
	public static enum ARTICLE_STATE_ENUM {
		DAISHENHE(0), TONGGUO(1), BUTONGGUO(2), XITONGSHANCHU(3), GERENSHANCHU(4);
		private Integer state;

		private ARTICLE_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

	// 来源
	public static enum ARTICLE_SOURCE_ENUM {
		SYSTEM(1), PERSONAL(2);
		private Integer source;

		private ARTICLE_SOURCE_ENUM(Integer source) {
			this.source = source;
		}

		public Integer getSource() {
			return source;
		}
	}

	// 分类
	public static enum ARTICLE_CATEGORY_ENUM {
		NORMAL(1), PROBLEM(2);
		private Integer category;

		private ARTICLE_CATEGORY_ENUM(Integer category) {
			this.category = category;
		}

		public Integer getCategory() {
			return category;
		}
	}

}