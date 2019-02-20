package com.yixiang.api.recharge.pojo;

import java.util.Date;

public class RechargeInfo {

	private Integer id;
	private Integer userId;
	private Integer templateId;
	private Float price;
	private Float bonus;
	private Integer payWay;
	private String outTradeNo;
	private String tradeNo;
	private Integer state;
	private Date createTime;
	private Date payTime;
	private Date refundTime;

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

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getBonus() {
		return bonus;
	}

	public void setBonus(Float bonus) {
		this.bonus = bonus;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	// 状态类型
	public static enum STATE_TYPE_ENUM {
		OVERTIME(-1), NOPAY(0), PAYED(1), REFUNDED(2);
		private Integer state;

		private STATE_TYPE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

}