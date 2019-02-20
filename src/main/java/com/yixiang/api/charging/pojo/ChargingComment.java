package com.yixiang.api.charging.pojo;

import java.util.Date;

public class ChargingComment {

	private Integer id;
	private Integer userId;
	private Integer chargingId;
	private String title;
	private String content;
	private String media;
	private String tags;
	private Integer state;
	private Date createTime;

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

	public Integer getChargingId() {
		return chargingId;
	}

	public void setChargingId(Integer chargingId) {
		this.chargingId = chargingId;
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

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media == null ? null : media.trim();
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
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

	// 状态
	public static enum COMMENT_STATE_ENUM {
		DAISHENHE(0), TONGGUO(1), BUTONGGUO(2), XITONGSHANCHU(3), GERENSHANCHU(4);
		private Integer state;

		private COMMENT_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

}