package com.yixiang.api.stat.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.stat.mapper.NaviLogMapper;

@Service
public class NaviLogComponent {
	
	@Autowired
	private NaviLogMapper naviLogMapper;

	//按天统计导航次数
	public List<Map<String, Object>> statNaviAmountWithDate(Map<String, Object> param) {
		return naviLogMapper.statNaviAmountWithDate(param);
	}

	//统计导航次数
	public Map<String, Object> statNaviAmount(Map<String, Object> param) {
		return naviLogMapper.statNaviAmount(param);
	}

}
