package com.yixiang.api.charging.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yixiang.api.charging.mapper.ReportStationInfoMapper;
import com.yixiang.api.charging.pojo.ReportStationInfo;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class ReportStationInfoComponent {

	@Autowired
	private ReportStationInfoMapper reportStationInfoMapper;
	
	//计算结果集大小
	public long countByExample(QueryExample example) {
		return reportStationInfoMapper.countByExample(example);
	}

	//保存
	public int insertSelective(ReportStationInfo record) {
		return reportStationInfoMapper.insertSelective(record);
	}

	//计算结果集
	public List<ReportStationInfo> selectByExample(QueryExample example) {
		return reportStationInfoMapper.selectByExample(example);
	}

	//统计充电桩故障率
	public List<Map<String, Object>> statStationReport(Map<String, Object> param) {
		return reportStationInfoMapper.statStationReport(param);
	}

	//更新
	public int updateByExampleSelective(ReportStationInfo record, QueryExample example) {
		return reportStationInfoMapper.updateByExampleSelective(record, example);
	}

}
