package com.yixiang.api.user.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String uuid;
	private Integer deviceId;
	private String userName;
	private String pwd;
	private String phone;
	private String headImg;
	private String wxHeadImg;
	private Integer levelId;
	private Integer stars;
	private Integer coins;
	private Integer times;
	private Integer areaId;
	private BigDecimal lat;
	private BigDecimal lng;
	private BigDecimal balance;
	private String remark;
	private Integer state;
	private Date createTime;
	private Date activeTime;
	private Date loginTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd == null ? null : pwd.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg == null ? null : headImg.trim();
	}

	public String getWxHeadImg() {
		return wxHeadImg;
	}

	public void setWxHeadImg(String wxHeadImg) {
		this.wxHeadImg = wxHeadImg == null ? null : wxHeadImg.trim();
	}

	public Integer getLevelId() {
		return levelId;
	}

	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}

	public Integer getStars() {
		return stars;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	public Integer getCoins() {
		return coins;
	}

	public void setCoins(Integer coins) {
		this.coins = coins;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	public BigDecimal getLng() {
		return lng;
	}

	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
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

	public Date getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	// 状态
	public static enum USER_STATE_ENUM {
		DAIJIHUO(0), YIJIHUO(1), YIDONGJIE(2);
		private Integer state;

		private USER_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

}