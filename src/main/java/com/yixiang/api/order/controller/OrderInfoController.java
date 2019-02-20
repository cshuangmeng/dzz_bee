package com.yixiang.api.order.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.order.service.OrderInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/order/charge")
public class OrderInfoController {

	@Autowired
	private OrderInfoComponent orderInfoComponent;
	
	//获取订单列表
	@RequestMapping("/list")
	public Result queryOrders(){
		Map<String,Object> result=orderInfoComponent.queryOrders();
		if(Result.noError()){
			Result.putValue(result);
		}
		return Result.getThreadObject();
	}
	
	//退款
	@RequestMapping("/refund")
	public Result refundOrder(@RequestParam Integer id,@RequestParam(defaultValue="0")Float discount){
		orderInfoComponent.refundOrder(id, discount);
		return Result.getThreadObject();
	}
	
	//更改订单状态
	@RequestMapping("/end")
	public Result endOrderInfo(@RequestParam Integer orderId,@RequestParam(required=false)Integer couponId){
		orderInfoComponent.endOrderInfo(orderId, couponId);
		return Result.getThreadObject();
	}
	
}
