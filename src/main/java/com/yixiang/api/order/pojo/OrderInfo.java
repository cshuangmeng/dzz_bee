package com.yixiang.api.order.pojo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderInfo {

	private Integer id;
	private Integer userId;
	private String chargeId;
	private String billId;
	private Integer connectorId;
	private Integer couponId;
	private Integer stationId;
	private String chargeState;
	private Date startTime;
	private Date endTime;
	private Integer totalTime;
	private Float totalPower;
	private Float totalPowerPrice;
	private Float totalServiceFee;
	private Float totalPrice;
	private Float payPrice;
	private Float totalBalance;
    private Integer payWay;
    private String tradeNo;
    private String outTradeNo;
	private String endCode;
	private Float current;
	private Float power;
	private String soc;
	private Integer provider;
	private String providerName;
	private Date createTime;
	private Date payTime;
	private Date refundTime;
	private Integer state;
	private String remark;

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

	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId == null ? null : chargeId.trim();
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId == null ? null : billId.trim();
	}

	public Integer getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(Integer connectorId) {
		this.connectorId = connectorId;
	}

	public Integer getCouponId() {
		return couponId;
	}

	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	public Integer getStationId() {
		return stationId;
	}

	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}

	public String getChargeState() {
		return chargeState;
	}

	public void setChargeState(String chargeState) {
		this.chargeState = chargeState == null ? null : chargeState.trim();
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Integer totalTime) {
		this.totalTime = totalTime;
	}

	public Float getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(Float totalPower) {
		this.totalPower = totalPower;
	}

	public Float getTotalPowerPrice() {
		return totalPowerPrice;
	}

	public void setTotalPowerPrice(Float totalPowerPrice) {
		this.totalPowerPrice = totalPowerPrice;
	}

	public Float getTotalServiceFee() {
		return totalServiceFee;
	}

	public void setTotalServiceFee(Float totalServiceFee) {
		this.totalServiceFee = totalServiceFee;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Float getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(Float payPrice) {
		this.payPrice = payPrice;
	}

	public Float getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Float totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Integer getPayWay() {
        return payWay;
    }

    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }
	
	public String getEndCode() {
		return endCode;
	}

	public void setEndCode(String endCode) {
		this.endCode = endCode == null ? null : endCode.trim();
	}

	public Float getCurrent() {
		return current;
	}

	public void setCurrent(Float current) {
		this.current = current;
	}

	public Float getPower() {
		return power;
	}

	public void setPower(Float power) {
		this.power = power;
	}

	public String getSoc() {
		return soc;
	}

	public void setSoc(String soc) {
		this.soc = soc == null ? null : soc.trim();
	}

	public Integer getProvider() {
		return provider;
	}

	public void setProvider(Integer provider) {
		this.provider = provider;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}
	
	// 充电状态
	public static enum CHARGE_STATE_ENUM {
		CHARGE_AUTH("chargeAuth"), CHARGE_START("chargeStart"), CHARGE_END("chargeEnd"), CHARGE_COMPLETE("chargeComplete");
		private String state;

		private CHARGE_STATE_ENUM(String state) {
			this.state = state;
		}

		public String getState() {
			return state;
		}
	}

	// 订单状态
	public static enum ORDER_STATE_ENUM {
		PENDING(0), CHARGING(1), SETTLEMENT(2), NO_PAY(3), NO_EVALUATE(4), COMPLETED(5), CANCEL(6), REFUND(7);
		private Integer state;

		private ORDER_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}
	
	// 进行中的订单状态
	public static final List<Integer> IN_PROGRESS_STATES = Arrays.asList(ORDER_STATE_ENUM.CHARGING.getState(),
			ORDER_STATE_ENUM.SETTLEMENT.getState(), ORDER_STATE_ENUM.NO_PAY.getState());
	// 已完成的订单状态
	public static final List<Integer> END_CHARGING__STATES = Arrays.asList(ORDER_STATE_ENUM.NO_EVALUATE.getState(),
			ORDER_STATE_ENUM.COMPLETED.getState());

}