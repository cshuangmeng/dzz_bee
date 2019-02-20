package com.yixiang.api.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.order.pojo.CouponType;
import com.yixiang.api.util.pojo.QueryExample;

public interface CouponTypeMapper {

	long countByExample(QueryExample example);

	int insertSelective(CouponType record);

	List<CouponType> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") CouponType record, @Param("example") QueryExample example);

}
