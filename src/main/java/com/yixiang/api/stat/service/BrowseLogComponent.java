package com.yixiang.api.stat.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.stat.mapper.BrowseLogMapper;

@Service
public class BrowseLogComponent {

	@Autowired
	private BrowseLogMapper browseLogMapper;
	
	//按天统计浏览次数
	public List<Map<String, Object>> statBrowseAmountWithDate(Map<String, Object> param) {
		return browseLogMapper.statBrowseAmountWithDate(param);
	}

	//统计浏览次数
	public Map<String, Object> statBrowseAmount(Map<String, Object> param) {
		return browseLogMapper.statBrowseAmount(param);
	}

}
