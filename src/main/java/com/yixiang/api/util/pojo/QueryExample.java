package com.yixiang.api.util.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class QueryExample {

	protected String orderByClause;
	protected String groupByClause;
	protected boolean distinct;
	protected List<Criteria> oredCriteria;
	protected Integer offset;
	protected Integer limit;
	protected boolean lock;
	protected static final String AND = "and";
	protected static final String OR = "or";

	public QueryExample() {
		offset = 0;
		oredCriteria = new ArrayList<Criteria>();
	}

	public String getGroupByClause() {
		return groupByClause;
	}

	public void setGroupByClause(String groupByClause) {
		this.groupByClause = groupByClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public Criteria or() {
		Criteria criteria = createCriteriaInternal(OR);
		oredCriteria.add(criteria);
		return criteria;
	}

	public Criteria and() {
		Criteria criteria = createCriteriaInternal(AND);
		oredCriteria.add(criteria);
		return criteria;
	}

	protected Criteria createCriteriaInternal(String type) {
		Criteria criteria = new Criteria(type);
		return criteria;
	}

	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	protected abstract static class GeneratedCriteria {
		protected List<Criterion> criteria;
		protected String type;

		protected GeneratedCriteria(String type) {
			super();
			this.type = type;
			criteria = new ArrayList<Criterion>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public boolean isAnd() {
			return type.equals(AND);
		}

		public boolean isOr() {
			return type.equals(OR);
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition, String type) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition, type));
		}

		protected void addCriterion(String condition, Object value, String property, String type) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value, type));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property, String type) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2, type));
		}

		public Criteria andExists(String field) {
			addCriterion("exists(" + field + ")", AND);
			return (Criteria) this;
		}

		public Criteria orExists(String field) {
			addCriterion("exists(" + field + ")", OR);
			return (Criteria) this;
		}

		public Criteria andNotExists(String field) {
			addCriterion("not exists(" + field + ")", AND);
			return (Criteria) this;
		}

		public Criteria orNotExists(String field) {
			addCriterion("not exists(" + field + ")", OR);
			return (Criteria) this;
		}

		public Criteria andIsNull(String field) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " is null", AND);
			return (Criteria) this;
		}

		public Criteria orIsNull(String field) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " is null", OR);
			return (Criteria) this;
		}

		public Criteria andIsNotNull(String field) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " is not null", AND);
			return (Criteria) this;
		}

		public Criteria orIsNotNull(String field) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " is not null", OR);
			return (Criteria) this;
		}

		public Criteria andEqualTo(String field, Object value) {
			if(StringUtils.isNotEmpty(field)&&null!=value){
				addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " =", value, field, AND);
			}
			return (Criteria) this;
		}

		public Criteria orEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " =", value, field, OR);
			return (Criteria) this;
		}

		public Criteria andNotEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " <>", value, field, AND);
			return (Criteria) this;
		}

		public Criteria orNotEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " <>", value, field, OR);
			return (Criteria) this;
		}

		public Criteria andGreaterThan(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " >", value, field, AND);
			return (Criteria) this;
		}

		public Criteria orGreaterThan(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " >", value, field, OR);
			return (Criteria) this;
		}

		public Criteria andGreaterThanOrEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " >=", value, field, AND);
			return (Criteria) this;
		}

		public Criteria orGreaterThanOrEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " >=", value, field, OR);
			return (Criteria) this;
		}

		public Criteria andLessThan(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " <", value, field, AND);
			return (Criteria) this;
		}

		public Criteria orLessThan(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " <", value, field, OR);
			return (Criteria) this;
		}

		public Criteria andLessThanOrEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " <=", value, field, AND);
			return (Criteria) this;
		}

		public Criteria orLessThanOrEqualTo(String field, Object value) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " <=", value, field, OR);
			return (Criteria) this;
		}

		public Criteria andIn(String field, List<?> values) {
			if (null != values && values.size() > 0) {
				addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " in", values, field,
						AND);
			}
			return (Criteria) this;
		}

		public Criteria orIn(String field, List<?> values) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " in", values, field, OR);
			return (Criteria) this;
		}

		public Criteria andNotIn(String field, List<?> values) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " not in", values, field,
					AND);
			return (Criteria) this;
		}

		public Criteria orNotIn(String field, List<?> values) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " not in", values, field,
					OR);
			return (Criteria) this;
		}

		public Criteria andBetween(String field, Object value1, Object value2) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " between", value1, value2,
					field, AND);
			return (Criteria) this;
		}

		public Criteria orBetween(String field, Object value1, Object value2) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " between", value1, value2,
					field, OR);
			return (Criteria) this;
		}

		public Criteria andNotBetween(String field, Object value1, Object value2) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " not between", value1,
					value2, field, AND);
			return (Criteria) this;
		}

		public Criteria orNotBetween(String field, Object value1, Object value2) {
			addCriterion((field.startsWith("t.") ? field.replace("t.", "") : "t." + field) + " not between", value1,
					value2, field, OR);
			return (Criteria) this;
		}
	}

	public static class Criteria extends GeneratedCriteria {

		protected Criteria(String type) {
			super(type);
		}
	}

	public static class Criterion {
		private String type;
		private String condition;

		private Object value;

		private Object secondValue;

		private boolean noValue;

		private boolean singleValue;

		private boolean betweenValue;

		private boolean listValue;

		private String typeHandler;

		public boolean isAnd() {
			return type.equals(AND);
		}

		public boolean isOr() {
			return type.equals(OR);
		}

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition, String type) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
			this.type = type;
		}

		protected Criterion(String condition, Object value, String typeHandler, String type) {
			super();
			this.condition = condition;
			this.value = value;
			this.type = type;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value, String type) {
			this(condition, value, null, type);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler, String type) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
			this.type = type;
		}

		protected Criterion(String condition, Object value, Object secondValue, String type) {
			this(condition, value, secondValue, null, type);
		}
	}
}