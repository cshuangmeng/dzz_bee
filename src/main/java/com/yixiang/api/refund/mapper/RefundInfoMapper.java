package com.yixiang.api.refund.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.refund.pojo.RefundInfo;
import com.yixiang.api.util.pojo.QueryExample;

public interface RefundInfoMapper {

	long countByExample(QueryExample example);

	int insertSelective(RefundInfo record);

	List<RefundInfo> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") RefundInfo record, @Param("example") QueryExample example);

}