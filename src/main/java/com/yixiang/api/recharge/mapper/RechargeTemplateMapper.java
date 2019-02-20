package com.yixiang.api.recharge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.recharge.pojo.RechargeTemplate;
import com.yixiang.api.util.pojo.QueryExample;

public interface RechargeTemplateMapper {

	long countByExample(QueryExample example);

	int insertSelective(RechargeTemplate record);

	List<RechargeTemplate> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") RechargeTemplate record, @Param("example") QueryExample example);

}