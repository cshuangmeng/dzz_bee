package com.yixiang.api.user.pojo;

import java.util.Date;

public class MessageHistory {

	private Integer id;
	private Integer userId;
	private Integer refId;
	private Integer topId;
	private Integer contentType;
	private String content;
	private String media;
	private Integer isRead;
	private Integer source;
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

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public Integer getTopId() {
		return topId;
	}

	public void setTopId(Integer topId) {
		this.topId = topId;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
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

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
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
	public static enum MESSAGE_STATE_ENUM {
		DAISHENHE(0), TONGGUO(1), BUTONGGUO(2), XITONGSHANCHU(3), GERENSHANCHU(4);
		private Integer state;

		private MESSAGE_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

	// 内容类型
	public static enum MESSAGE_TYPE_ENUM {
		WORDS(1), IMGS(2), VIDEOS(3), AUDIOS(4);
		private Integer type;

		private MESSAGE_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

	// 来源
	public static enum MESSAGE_SOURCE_ENUM {
		USER(1), KEFU(2);
		private Integer source;

		private MESSAGE_SOURCE_ENUM(Integer source) {
			this.source = source;
		}

		public Integer getSource() {
			return source;
		}
	}

}