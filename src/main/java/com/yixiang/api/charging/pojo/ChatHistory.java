package com.yixiang.api.charging.pojo;

import java.util.Date;

public class ChatHistory {

	private Integer id;
	private Integer fromUserId;
	private Integer toUserId;
	private Integer chargingId;
	private Integer contentType;
	private String content;
	private String media;
	private Integer isRead;
	private Integer state;
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Integer fromUserId) {
		this.fromUserId = fromUserId;
	}

	public Integer getToUserId() {
		return toUserId;
	}

	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}

	public Integer getChargingId() {
		return chargingId;
	}

	public void setChargingId(Integer chargingId) {
		this.chargingId = chargingId;
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
}