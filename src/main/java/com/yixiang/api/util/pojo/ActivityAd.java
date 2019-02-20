package com.yixiang.api.util.pojo;

import java.util.Date;

public class ActivityAd {

	private Integer id;
	private Integer category;
	private String title;
	private String img;
	private Integer display;
	private String destination;
	private String parameters;
	private Integer state;
	private Integer sort;
	private String remark;
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Integer getDisplay() {
		return display;
	}

	public void setDisplay(Integer display) {
		this.display = display;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination == null ? null : destination.trim();
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters == null ? null : parameters.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	// 状态
	public static enum AD_STATE_ENUM {
		DISABLED(0), ENABLED(1), DELETED(2);
		private Integer state;

		private AD_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

	// 广告分类
	public static enum CATEGORY_TYPE_ENUM {
		HOME(1), HOT_HOME_TOP(2), CAR_HOME_MIDDLE(3);
		private Integer type;

		private CATEGORY_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

	// 展示方式
	public static enum DISPLAY_TYPE_ENUM {
		NATIVE(1), H5(2);
		private Integer type;

		private DISPLAY_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

}