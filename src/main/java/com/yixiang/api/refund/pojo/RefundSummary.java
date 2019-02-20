package com.yixiang.api.refund.pojo;

import java.util.Date;

public class RefundSummary {

	private Integer id;
	private Integer userId;
	private Integer orderId;
	private Integer orderType;
	private Float total;
	private Float third;
	private Float thirdRefund;
	private Float balance;
	private Float balanceRefund;
	private Date createTime;
	private Date updateTime;

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

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public Float getThird() {
		return third;
	}

	public void setThird(Float third) {
		this.third = third;
	}

	public Float getThirdRefund() {
		return thirdRefund;
	}

	public void setThirdRefund(Float thirdRefund) {
		this.thirdRefund = thirdRefund;
	}

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	public Float getBalanceRefund() {
		return balanceRefund;
	}

	public void setBalanceRefund(Float balanceRefund) {
		this.balanceRefund = balanceRefund;
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

	// 订单类型
	public static enum ORDER_TYPE_ENUM {
		CHARGING(1), RECHARGE(2);
		private Integer type;

		private ORDER_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

}