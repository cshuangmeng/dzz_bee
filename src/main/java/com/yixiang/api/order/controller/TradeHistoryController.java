package com.yixiang.api.order.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.order.service.TradeHistoryComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/trade")
public class TradeHistoryController {
	
	@Autowired
	private TradeHistoryComponent tradeHistoryComponent;

	//获取充值请求列表
	@RequestMapping("/recharge/list")
	public Result queryOrders(){
		Map<String,Object> result=tradeHistoryComponent.queryRechargeTradeHistory();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
}
