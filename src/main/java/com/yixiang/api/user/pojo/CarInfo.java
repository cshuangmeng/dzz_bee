package com.yixiang.api.user.pojo;

import java.util.Date;

public class CarInfo {

	private Integer id;
	private Integer userId;
	private Integer brandId;
	private Integer carId;
	private String brand;
	private String car;
	private String number;
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

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public Integer getCarId() {
		return carId;
	}

	public void setCarId(Integer carId) {
		this.carId = carId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number == null ? null : number.trim();
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
	public static enum CAR_STATE_ENUM {
		NORMAL(1), SYSTEM_DEL(2), PERSON_DEL(3);
		private Integer state;

		private CAR_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

}