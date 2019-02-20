package com.yixiang.api.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.order.pojo.CouponInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface CouponInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(CouponInfo record);

	List<CouponInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") CouponInfo record, @Param("example") QueryExample example);

}
