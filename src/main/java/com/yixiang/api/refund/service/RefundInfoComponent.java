package com.yixiang.api.refund.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feilong.core.DatePattern;
import com.feilong.core.date.DateUtil;
import com.yixiang.api.refund.mapper.RefundInfoMapper;
import com.yixiang.api.refund.pojo.RefundInfo;
import com.yixiang.api.util.DataUtil;
import com.yixiang.api.util.pojo.QueryExample;

@Service
public class RefundInfoComponent {

	@Autowired
	private RefundInfoMapper refundInfoMapper;
	
	//保存退款信息
	@Transactional
	public void saveRefundInfo(Integer account,Integer source,Integer userId,Integer orderId,Integer orderType
			,Float third,Float balance,String outTradeNo,Integer payWay,String reason,Integer state,Integer tradeHistoryId){
		RefundInfo refund=new RefundInfo();
		refund.setAccount(account);
		refund.setSource(source);
		refund.setBalancePrice(null!=balance?balance:0);
		refund.setCreateTime(new Date());
		refund.setOrderId(orderId);
		refund.setOrderType(orderType);
		refund.setOutTradeNo(outTradeNo);
		refund.setPayWay(payWay);
		refund.setReason(reason);
		refund.setState(state);
		refund.setThirdPrice(null!=third?third:0);
		refund.setTotalPrice(refund.getBalancePrice()+refund.getThirdPrice());
		refund.setTradeHistoryId(tradeHistoryId);
		refund.setTradeNo(DateUtil.toString(new Date(), DatePattern.TIMESTAMP_WITH_MILLISECOND)+DataUtil.createNums(3));
		refund.setUserId(userId);
		refundInfoMapper.insertSelective(refund);
	}
	
	//获取退款信息
	public List<RefundInfo> queryRefundInfos(Integer state){
		if(null!=state){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("state", state);
			return refundInfoMapper.selectByExample(example);
		}
		return null;
	}

	//获取退款信息
	public RefundInfo getRefundInfo(Integer id){
		if(null!=id&&id>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", id);
			List<RefundInfo> result=refundInfoMapper.selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//获取退款信息
	public RefundInfo getRefundInfo(Integer orderId,Integer orderType){
		if(null!=orderId&&orderId>0&&null!=orderType&&orderType>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("order_id", orderId).andEqualTo("order_type", orderType);
			List<RefundInfo> result=refundInfoMapper.selectByExample(example);
			return result.size()>0?result.get(0):null;
		}
		return null;
	}
	
	//更新退款信息
	@Transactional
	public void updateRefundInfo(RefundInfo info){
		if(null!=info.getId()&&info.getId()>0){
			QueryExample example=new QueryExample();
			example.and().andEqualTo("id", info.getId());
			refundInfoMapper.updateByExampleSelective(info, example);
		}
	}
	
}
