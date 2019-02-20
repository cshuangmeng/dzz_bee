package com.yixiang.api.refund.pojo;

import java.util.Date;

public class RefundInfo {

	private Integer id;
	private Integer userId;
	private Integer orderId;
	private Integer orderType;
	private Integer tradeHistoryId;
	private Float totalPrice;
	private Float thirdPrice;
	private Float balancePrice;
	private Integer payWay;
	private String outTradeNo;
	private String tradeNo;
	private Integer state;
	private String reason;
	private String response;
	private String remark;
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

	public Integer getTradeHistoryId() {
		return tradeHistoryId;
	}

	public void setTradeHistoryId(Integer tradeHistoryId) {
		this.tradeHistoryId = tradeHistoryId;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Float getThirdPrice() {
		return thirdPrice;
	}

	public void setThirdPrice(Float thirdPrice) {
		this.thirdPrice = thirdPrice;
	}

	public Float getBalancePrice() {
		return balancePrice;
	}

	public void setBalancePrice(Float balancePrice) {
		this.balancePrice = balancePrice;
	}

	public Integer getPayWay() {
		return payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo == null ? null : tradeNo.trim();
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	// 状态
	public static enum REFUND_STATE_ENUM {
		DAICHULI(0), CHULIZHONG(1), YICHULI(2), YISHANCHU(3);
		private Integer state;

		private REFUND_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

}