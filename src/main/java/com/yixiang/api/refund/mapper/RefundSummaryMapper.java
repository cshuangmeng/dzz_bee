package com.yixiang.api.refund.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.refund.pojo.RefundSummary;
import com.yixiang.api.util.pojo.QueryExample;

public interface RefundSummaryMapper {

	long countByExample(QueryExample example);

	int insertSelective(RefundSummary record);

	List<RefundSummary> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") RefundSummary record, @Param("example") QueryExample example);

}