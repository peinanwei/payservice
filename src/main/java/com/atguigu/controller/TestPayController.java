package com.atguigu.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestPayController {
	
	
	@RequestMapping("/notifyBusi")
	@ResponseBody
	public String  notifyBusi(@RequestParam Map map){
		
		String tradeNo=(String)map.get("trade_no");
		
		 
		return "success";
	}
 
}
