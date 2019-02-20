package com.yixiang.api.coin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yixiang.api.coin.pojo.CoinHistory;
import com.yixiang.api.util.pojo.QueryExample;

public interface CoinHistoryMapper {

	long countByExample(QueryExample example);

	int insertSelective(CoinHistory record);

	List<CoinHistory> selectByExample(QueryExample example);

	int updateByExampleSelective(@Param("record") CoinHistory record, @Param("example") QueryExample example);

}