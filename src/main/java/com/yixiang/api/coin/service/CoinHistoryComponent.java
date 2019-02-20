package com.yixiang.api.coin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.coin.mapper.CoinHistoryMapper;
import com.yixiang.api.coin.pojo.CoinHistory;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class CoinHistoryComponent {
	
	@Autowired
	private CoinHistoryMapper coinHistoryMapper;
	
	//获取虚拟币交易记录信息
	public CoinHistory getCoinHistory(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<CoinHistory> list=selectByExample(example);
			return list.size()>0?list.get(0):null;
		}
		return null;
	}
	
	//更新虚拟币交易记录信息
	public int updateCoinHistory(CoinHistory history){
		if(null!=history.getId()&&history.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", history.getId());
			return updateByExampleSelective(history, example);
		}
		return 0;
	}

	//计算结果集大小
	public long countByExample(QueryExample example) {
		return coinHistoryMapper.countByExample(example);
	}

	//保存
	public int insertSelective(CoinHistory record) {
		return coinHistoryMapper.insertSelective(record);
	}

	//获取结果集
	public List<CoinHistory> selectByExample(QueryExample example) {
		return coinHistoryMapper.selectByExample(example);
	}

	//更新
	public int updateByExampleSelective(CoinHistory record, QueryExample example) {
		return coinHistoryMapper.updateByExampleSelective(record, example);
	}

}
