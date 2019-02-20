package com.yixiang.api.charging.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class ChargingStation {

	private Integer id;
	private String uuid;
	private String stationId;
	private Integer areaId;
	private Integer userId;
	private Integer source;
	private Integer construction;
	private Integer supportOrder;
	private String matchCars;
	private String telephone;
	private String servicePhone;
	private String providerId;
	private String provider;
	private String payWay;
	private String payTip;
	private String electricityPrice;
	private Integer fastNum;
	private Integer slowNum;
	private Integer times;
	private Integer isUnderground;
	private Integer isStandard;
	private Integer isPrivate;
	private BigDecimal lat;
	private BigDecimal lng;
	private String title;
	private String address;
	private String siteGuide;
	private String openTime;
	private String parkingPrice;
	private Integer parkNums;
	private String parkInfo;
	private String headImg;
	private String detailImgs;
	private String serviceFee;
	private String remark;
	private Integer state;
	private Date createTime;
	private Date updateTime;
	private BigDecimal distance;

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
		this.uuid = uuid == null ? null : uuid.trim();
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId == null ? null : stationId.trim();
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getConstruction() {
		return construction;
	}

	public void setConstruction(Integer construction) {
		this.construction = construction;
	}

	public Integer getSupportOrder() {
		return supportOrder;
	}

	public void setSupportOrder(Integer supportOrder) {
		this.supportOrder = supportOrder;
	}

	public String getMatchCars() {
		return matchCars;
	}

	public void setMatchCars(String matchCars) {
		this.matchCars = matchCars == null ? null : matchCars.trim();
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone == null ? null : telephone.trim();
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone == null ? null : servicePhone.trim();
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId == null ? null : providerId.trim();
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider == null ? null : provider.trim();
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay == null ? null : payWay.trim();
	}

	public String getPayTip() {
		return payTip;
	}

	public void setPayTip(String payTip) {
		this.payTip = payTip == null ? null : payTip.trim();
	}

	public String getElectricityPrice() {
		return electricityPrice;
	}

	public void setElectricityPrice(String electricityPrice) {
		this.electricityPrice = electricityPrice == null ? null : electricityPrice.trim();
	}

	public Integer getFastNum() {
		return fastNum;
	}

	public void setFastNum(Integer fastNum) {
		this.fastNum = fastNum;
	}

	public Integer getSlowNum() {
		return slowNum;
	}

	public void setSlowNum(Integer slowNum) {
		this.slowNum = slowNum;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Integer getIsUnderground() {
		return isUnderground;
	}

	public void setIsUnderground(Integer isUnderground) {
		this.isUnderground = isUnderground;
	}

	public Integer getIsStandard() {
		return isStandard;
	}

	public void setIsStandard(Integer isStandard) {
		this.isStandard = isStandard;
	}

	public Integer getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Integer isPrivate) {
		this.isPrivate = isPrivate;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public String getSiteGuide() {
		return siteGuide;
	}

	public void setSiteGuide(String siteGuide) {
		this.siteGuide = siteGuide == null ? null : siteGuide.trim();
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime == null ? null : openTime.trim();
	}

	public String getParkingPrice() {
		return parkingPrice;
	}

	public void setParkingPrice(String parkingPrice) {
		this.parkingPrice = parkingPrice == null ? null : parkingPrice.trim();
	}

	public Integer getParkNums() {
		return parkNums;
	}

	public void setParkNums(Integer parkNums) {
		this.parkNums = parkNums;
	}

	public String getParkInfo() {
		return parkInfo;
	}

	public void setParkInfo(String parkInfo) {
		this.parkInfo = parkInfo == null ? null : parkInfo.trim();
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg == null ? null : headImg.trim();
	}

	public String getDetailImgs() {
		return detailImgs;
	}

	public void setDetailImgs(String detailImgs) {
		this.detailImgs = detailImgs == null ? null : detailImgs.trim();
	}

	public String getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(String serviceFee) {
		this.serviceFee = serviceFee == null ? null : serviceFee.trim();
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

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	// 状态
	public static enum STATION_STATE_ENUM {
		UNKNOWN(0), BUILDING(1), CLOSED(5), CHECKING(6), ENABLED(50);
		private Integer state;

		private STATION_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

	// 快慢充类型
	public static enum CONNECTOR_TYPE_ENUM {
		SLOW(3), FAST(4);
		private Integer type;

		private CONNECTOR_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

}