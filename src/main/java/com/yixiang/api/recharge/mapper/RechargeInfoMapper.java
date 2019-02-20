package com.yixiang.api.recharge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.recharge.pojo.RechargeInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface RechargeInfoMapper {
	
	long countByExample(QueryExample example);

	int insertSelective(RechargeInfo record);

	List<RechargeInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") RechargeInfo record, @Param("example") QueryExample example);

}