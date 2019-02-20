package com.yixiang.api.order.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class TradeHistory {

	private Integer id;
	private Integer userId;
	private String title;
	private Integer tradeId;
	private String tradeNo;
	private Integer tradeType;
	private BigDecimal amount;
	private BigDecimal balance;
	private String remark;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo == null ? null : tradeNo.trim();
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	// 状态
	public static enum TRADE_STATE_ENUM {
		DAICHULI(0), CHULIZHONG(1), YICHULI(2), YISHANCHU(3);
		private Integer state;

		private TRADE_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

	// 交易类型
	public static enum TRADE_TYPE_ENUM {
		RECHARGE(1), RECHARGE_REFUND(2), WITHDRAW(3), CHARGE_PAY(4), CHARGE_REFUND(5)
		, CHARGE_INCOME(6), CHARGE_REVOKE(7), SYSTEM_GIVE(8), RECHARGE_GIVE(9), JFQ_RECHARGE(10);
		private Integer type;

		private TRADE_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

}