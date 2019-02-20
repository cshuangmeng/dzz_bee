package com.yixiang.api.refund.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yixiang.api.refund.mapper.RefundSummaryMapper;
import com.yixiang.api.refund.pojo.RefundSummary;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class RefundSummaryComponent {

	@Autowired
	private RefundSummaryMapper refundSummaryMapper;
	
	//保存退款汇总信息
	@Transactional
	public void saveRefundSummary(Integer userId,Integer orderId,Integer orderType,Float third,Float balance){
		RefundSummary summary=new RefundSummary();
		summary.setBalance(null!=balance?balance:0);
		summary.setCreateTime(new Date());
		summary.setOrderId(orderId);
		summary.setOrderType(orderType);
		summary.setThird(null!=third?third:0);
		summary.setTotal(summary.getBalance()+summary.getThird());
		summary.setUserId(userId);
		refundSummaryMapper.insertSelective(summary);
	}
	
	//获取退款汇总信息
	public RefundSummary getRefundSummary(Integer orderId,Integer orderType){
		if(null!=orderId&&null!=orderType){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("order_id", orderId).andEqualTo("order_type", orderType);
			List<RefundSummary> result=refundSummaryMapper.selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//更新退款汇总信息
	@Transactional
	public void updateRefundSummary(RefundSummary summary){
		if(null!=summary.getId()&&summary.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", summary.getId());
			refundSummaryMapper.updateByExampleSelective(summary, example);
		}
	}
	
}
