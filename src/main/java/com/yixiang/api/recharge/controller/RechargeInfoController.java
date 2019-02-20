package com.yixiang.api.recharge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yixiang.api.recharge.service.RechargeInfoComponent;
import com.yixiang.api.util.Result;

@RestController
@RequestMapping("/recharge/request")
public class RechargeInfoController {
	
	@Autowired
	private RechargeInfoComponent rechargeInfoComponent;

	//充值退款
	@RequestMapping("/refund")
	public Result refundRecharge(@RequestParam Integer id,@RequestParam(required=false) String reason){
		rechargeInfoComponent.refundRecharge(id,reason);
		return Result.getThreadObject();
	}
	
}
