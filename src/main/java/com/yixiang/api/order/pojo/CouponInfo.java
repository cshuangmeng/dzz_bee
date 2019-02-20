package com.yixiang.api.order.pojo;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CouponInfo {

	private Integer id;
	private Integer userId;
	private String title;
	private Integer category;
	private Integer reduceType;
	private String code;
	private String jfqUuid;
	private Float amount;
	private Float maxDiscount;
	private String pattern;
	private Date startTime;
	private Date endTime;
	private String description;
	private String cond;
	private String remark;
	private Integer state;
	private Integer tradeId;
	private Integer typeId;
	private Date useTime;
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

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getReduceType() {
		return reduceType;
	}

	public void setReduceType(Integer reduceType) {
		this.reduceType = reduceType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getJfqUuid() {
		return jfqUuid;
	}

	public void setJfqUuid(String jfqUuid) {
		this.jfqUuid = jfqUuid;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Float getMaxDiscount() {
		if(getReduceType().equals(CouponInfo.REDUCE_TYPE_ENUM.DISCOUNT.getType())){
			Pattern p=Pattern.compile("((\\d+\\.\\d{0,2})|([1-9]+))");
			Matcher m=p.matcher(getDescription());
			maxDiscount=m.find()?Float.valueOf(m.group(1)):null;
		}
		return maxDiscount;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern == null ? null : pattern.trim();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond == null ? null : cond.trim();
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

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Date getUseTime() {
		return useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	// 分类
	public static enum COUPON_CATEGORY_ENUM {
		CHARGING(1), SERVICEFEE(2), RECHARGE(3);
		private Integer category;

		private COUPON_CATEGORY_ENUM(Integer category) {
			this.category = category;
		}

		public Integer getCategory() {
			return category;
		}
	}

	// 减免类型
	public static enum REDUCE_TYPE_ENUM {
		REDUCE(1), DISCOUNT(2);
		private Integer type;

		private REDUCE_TYPE_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}

	// 优惠券状态
	public static enum COUPON_STATE_ENUM {
		NO_USE(0), USED(1), EXPIRED(2);
		private Integer state;

		private COUPON_STATE_ENUM(Integer state) {
			this.state = state;
		}

		public Integer getState() {
			return state;
		}
	}

}
