package com.yixiang.api.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.order.pojo.OrderInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface OrderInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(OrderInfo record);

	List<OrderInfo> selectByExample(QueryExample example);
	
	List<Map<String,Object>> statFastSlowOrder(@Param("param")Map<String,Object> param);

	int updateByExampleSelective(@Param("record") OrderInfo record, @Param("example") QueryExample example);

}