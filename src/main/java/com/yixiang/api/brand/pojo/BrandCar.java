package com.yixiang.api.brand.pojo;

import java.util.Date;

public class BrandCar {

	private Integer id;
	private String sid;
	private Integer carType;
	private Integer source;
	private Integer fuelType;
	private Integer brandId;
	private Integer areaId;
	private String address;
	private String category;
	private String color;
	private String car;
	private String price;
	private String groupPrice;
	private String shopPrice;
	private String batteryLife;
	private String icon;
	private String banner;
	private String detailImgs;
	private String paramImgs;
	private Integer isSpecial;
	private Integer sort;
	private Integer state;
	private Integer label;
	private String remark;
	private Date createTime;
	private Date topTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid == null ? null : sid.trim();
	}

	public Integer getCarType() {
		return carType;
	}

	public void setCarType(Integer carType) {
		this.carType = carType;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getFuelType() {
		return fuelType;
	}

	public void setFuelType(Integer fuelType) {
		this.fuelType = fuelType;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category == null ? null : category.trim();
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color == null ? null : color.trim();
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car == null ? null : car.trim();
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price == null ? null : price.trim();
	}

	public String getGroupPrice() {
		return groupPrice;
	}

	public void setGroupPrice(String groupPrice) {
		this.groupPrice = groupPrice == null ? null : groupPrice.trim();
	}

	public String getShopPrice() {
		return shopPrice;
	}

	public void setShopPrice(String shopPrice) {
		this.shopPrice = shopPrice == null ? null : shopPrice.trim();
	}

	public String getBatteryLife() {
		return batteryLife;
	}

	public void setBatteryLife(String batteryLife) {
		this.batteryLife = batteryLife == null ? null : batteryLife.trim();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon == null ? null : icon.trim();
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner == null ? null : banner.trim();
	}

	public String getDetailImgs() {
		return detailImgs;
	}

	public void setDetailImgs(String detailImgs) {
		this.detailImgs = detailImgs == null ? null : detailImgs.trim();
	}

	public String getParamImgs() {
		return paramImgs;
	}

	public void setParamImgs(String paramImgs) {
		this.paramImgs = paramImgs == null ? null : paramImgs.trim();
	}

	public Integer getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(Integer isSpecial) {
		this.isSpecial = isSpecial;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getLabel() {
		return label;
	}

	public void setLabel(Integer label) {
		this.label = label;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getTopTime() {
		return topTime;
	}

	public void setTopTime(Date topTime) {
		this.topTime = topTime;
	}

	// 状态
	public static enum CAR_STATE_ENUM {
		ENABLED(1), DISABLED(2), DELETED(3);
		private Integer state;

		private CAR_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

	// 车型来源
	public static enum CAR_SOURCE_ENUM {
		NEW_CAR(1), OLD_CAR(2);
		private Integer source;

		private CAR_SOURCE_ENUM(Integer source) {
			this.source = source;
		}

		public Integer getSource() {
			return source;
		}
	}

}