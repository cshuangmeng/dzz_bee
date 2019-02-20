package com.yixiang.api.user.pojo;

import java.util.Date;

public class UserDevice {

	private Integer id;
	private Integer userId;
	private String wxOpenId;
	private String registrationId;
	private String imei;
	private String system;
	private String platform;
	private Date loginTime;
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

	public String getWxOpenId() {
		return wxOpenId;
	}

	public void setWxOpenId(String wxOpenId) {
		this.wxOpenId = wxOpenId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId == null ? null : registrationId.trim();
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei == null ? null : imei.trim();
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system == null ? null : system.trim();
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}