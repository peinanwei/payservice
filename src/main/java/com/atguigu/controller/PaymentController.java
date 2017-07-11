package com.atguigu.controller;
 
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.alipay.util.PaymentStatus;
import com.alipay.util.UtilDate;
import com.atguigu.entity.Payment;
import com.atguigu.service.PaymentService;
import com.sun.org.apache.bcel.internal.generic.NEW;

@Controller
public class PaymentController {
	
	@Autowired
	PaymentService paymentService;
	
 
	//支付模块处理用户发起的支付请求
	@RequestMapping(value="/payment" ,method=RequestMethod.POST,produces="text/html;charset=utf-8")
	@ResponseBody
	public String doPayment(@RequestParam  Map<String,String> paraMap,Model model,@RequestHeader("referer") String origin_url  ){
		 //1、检查必填项
		List<String> errlist=AlipaySubmit.validOutParams(paraMap);
		if (!errlist.isEmpty()){
			model.addAttribute("msg",Arrays.toString(errlist.toArray()));
			System.err.println(origin_url);
			return "error" ;
		}
		Payment originPayment = paymentService.getPaymentByTradeNo(paraMap.get("trade_no"));
		if (originPayment != null&&(originPayment.getStatus()!=PaymentStatus.SUBMITED )) {
			model.addAttribute("msg","该笔订单已发生业务");
			System.err.println(origin_url);
			return "error" ;
		}
		
		 //2、整合默认参数
		StringBuilder url=new StringBuilder(AlipayConfig.ALIPAY_GATEWAY_NEW);
		Map<String, String> submitMap=new HashMap<String, String>() ;
		submitMap.put("out_trade_no", paraMap.get("trade_no"));
		submitMap.put("total_fee", paraMap.get("total_fee"));
		submitMap.put("subject", paraMap.get("subject"));
		submitMap.put("body", paraMap.get("body"));
		submitMap.put("service", AlipayConfig.service);
		submitMap.put("partner", AlipayConfig.partner);
		submitMap.put("seller_id", AlipayConfig.seller_id);
		submitMap.put("_input_charset", AlipayConfig.input_charset);
		submitMap.put("payment_type", AlipayConfig.payment_type);
		submitMap.put("notify_url", AlipayConfig.notify_url);
		submitMap.put("return_url", AlipayConfig.return_url);
		 //3、制作签名 并 生成要提交给支付宝的url参数串
 
		String shtml=  AlipaySubmit.buildRequest(submitMap, "get", "");
		   
	 
		//4、保存业务参数
		if (originPayment == null) {
			Payment payment = new Payment();
			payment.setTradeNo(paraMap.get("trade_no"));
			payment.setTotalFee(new BigDecimal(paraMap.get("total_fee")));
			payment.setBody(paraMap.get("body"));
			payment.setSubject(paraMap.get("subject"));
			payment.setBusiNotifyUrl( paraMap.get("busi_notify_url"));   
			payment.setBusiReturnUrl(paraMap.get("busi_return_url")); 
			payment.setBusiNotifyUrl(paraMap.get("busi_notify_url")); 
			payment.setSubmitUrl(url.toString());  
			payment.setStatus(PaymentStatus.SUBMITED);
			payment.setSubmitTime(UtilDate.getDateFormatter());
			
			paymentService.addPayment(payment);
		}

		
		//model.addAttribute("submitMap", submitMap) ;

        //6、重定向客户端到支付宝
		return shtml;
		
		
	}
	
	//支付后同步回调
	@RequestMapping(value="/pay_return" ,method=RequestMethod.GET)
	public String payReturn(@RequestParam Map<String,String>  paraMap, Model model,HttpServletRequest request  ){
	
		//1、检查参数
		List errlist =AlipayNotify.validInParams(paraMap) ;
		if (!errlist.isEmpty()){
			// 错误流水保存到日志
			System.err.println("err:"+errlist.toString());
			model.addAttribute("msg",Arrays.toString(errlist.toArray()));
			return "error" ;
		}
		paraMap=AlipayNotify.convertMap(paraMap);
		//2、检查签名
		boolean verify_result = AlipayNotify.verify(paraMap);
		// 测试用过签名验证
		if("1".equals(paraMap.get("istest"))){
			verify_result=true;
		}
		if(!verify_result){
			// 错误流水保存到日志
			System.err.println("err:"+errlist.toString());

			model.addAttribute("msg", "验证失败！");
			return "error";
		}
		String trade_status  =  paraMap.get("trade_status") ;
		Payment payment= paymentService.getPaymentByTradeNo(paraMap.get("out_trade_no"));
		if (payment==null) {
			model.addAttribute("msg","该单据不存在");
			return "error" ;
		}
	    if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
				    //判断该笔订单是否在商户网站中已经做过处理,如果该订单已处理则直接跳转提示页面表示处理完成，否则继续处理。
	    	        if(payment!=null&&payment.getStatus()!=PaymentStatus.FINISH){
	    	        	//3、更新订单状态 为PAID ,表示支付成功，但未通知商家
	    	        	Payment paymentReturn=new Payment();
	    	        	paymentReturn.setTradeNo(payment.getTradeNo());
	    	        	paymentReturn.setStatus(PaymentStatus.PAID);
	    	        	paymentReturn.setReturnTime(UtilDate.getDateFormatter());
	    	        	paymentReturn.setReturnParam(paraMap.toString());
	    	        	paymentService.updatePaymentStatus(paymentReturn);
	    	        	//4、通知电商订单系统
	    	        	String result= paymentService.notifyBusi(payment);
	    	        	if(result.equals("success")){
		    	        	//5、 更新订单状态，表示已通知电商系统
		    	        	Payment paymentNotify=new Payment();
		    	        	paymentNotify.setTradeNo(payment.getTradeNo());
		    	        	paymentNotify.setStatus(PaymentStatus.NOTIFIED);
		    	        	paymentNotify.setNotifyTime(UtilDate.getDateFormatter());
		    	        	paymentService.updatePaymentStatus(paymentNotify);
		    	        	//6、跳转到成功页面
		    	        	model.addAttribute("busi_return_url",payment.getBusiReturnUrl()+"?"+"trade_no="+payment.getTradeNo());
		    	        	return "paysuccess";
	    	        	} else{
		    	        	//6.1、跳转到错误页面
 
	    	    			model.addAttribute("msg","电商订单系统更新异常，请联系客服人员！");
		    	        	model.addAttribute("busi_return_url",payment.getBusiReturnUrl()+"?"+"trade_no="+payment.getTradeNo());
		    	        	return "error";
	    	        	}
	    	        	
	    	        }else{
	    	        	//3.1、跳转到成功页面
	    	        	 
    	    			model.addAttribute("msg","该订单已支付完成！");
	    	        	model.addAttribute("busi_return_url",payment.getBusiReturnUrl()+"?"+"trade_no="+payment.getTradeNo());
	    	        	return "paysuccess";
    	        	}
 
		}else{
        	//2.1、跳转到错误页面
       	 
			model.addAttribute("msg","支付未成功！");
        	model.addAttribute("busi_return_url",payment.getBusiReturnUrl()+"?"+"trade_no="+payment.getTradeNo());
        	return "error";
    	}
			
 
	}
	
	//支付成功后 第三方支付异步调用
	@RequestMapping(value="/pay_notify" ,method=RequestMethod.POST,produces="text/plain;charset=utf-8")
	@ResponseBody
	public String payNotify(@RequestParam  Map<String,String>  paraMap , Model model,HttpServletRequest request ){
		        System.out.println("begin to notify" );
		
		       //1、检查参数
				List errlist =AlipayNotify.validInParams(paraMap) ;
				if (!errlist.isEmpty()){
					// 错误流水保存到日志
					System.err.println("err:"+errlist.toString());
					
 
					return "error:"+errlist.toString() ;
				}
				//2、检查签名
				 
				boolean verify_result = AlipayNotify.verify(paraMap);
				// 测试用过签名验证
				if("1".equals(paraMap.get("istest"))){
					verify_result=true;
				}
				if(!verify_result){
					// 错误流水保存到日志
					System.err.println("err:: verify fail！" );

					 return "error: 验证失败！";
				}
				String trade_status  =  paraMap.get("trade_status") ;
				Payment payment= paymentService.getPaymentByTradeNo(paraMap.get("out_trade_no"));
				if(payment==null){
					return "error:单据不存在";
				}
			    if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
						    //判断该笔订单是否在商户网站中已经做过处理,直接告知第三方支付交易已成功
			    			if(payment!=null&& payment.getStatus()==PaymentStatus.FINISH){
			    				return "success";
			    			}else{
			    	        	//3、未通知 情况下 先通知电商订单系统更新订单状态
			    	        	Payment paymentReturn=new Payment();
			    	        	paymentReturn.setTradeNo(payment.getTradeNo());
			    	        	paymentReturn.setStatus(PaymentStatus.PAID);
			    	        	paymentReturn.setReturnTime(UtilDate.getDateFormatter());
			    	        	paymentReturn.setNotifyParam(paraMap.toString());
			    	        	paymentService.updatePaymentStatus(paymentReturn);
                                if( payment.getStatus()==PaymentStatus.SUBMITED||payment.getStatus()==PaymentStatus.PAID){
                                	String result= paymentService.notifyBusi(payment);
    			    	        	if(result.equals("success")){
    				    	        	//5、 更新订单状态
    				    	        	Payment paymentNotify=new Payment();
    				    	        	paymentNotify.setTradeNo(payment.getTradeNo());
    				    	        	paymentNotify.setStatus(PaymentStatus.FINISH);
    				    	        	paymentNotify.setNotifyTime(UtilDate.getDateFormatter());
    				    	        	paymentNotify.setFinishTime(UtilDate.getDateFormatter());
    				    	        	paymentService.updatePaymentStatus(paymentNotify);
	   			    	        		 System.err.println("---tradeno---"+payment.getTradeNo()+"-----notify success------------");
    				    	        	//6、告知支付宝
    				    	        	return "success";
    			    	        	} else{
    				    	        	//6.1、电商订单系统通知失败
	   			    	        		 System.err.println("---tradeno---"+payment.getTradeNo()+"---notify busi fail");
	   			    	    			 return "error";
    			    	        	}
                                }else if (payment.getStatus()==PaymentStatus.NOTIFIED) {
                                	Payment paymentNotify=new Payment();
				    	        	paymentNotify.setTradeNo(payment.getTradeNo());
				    	        	paymentNotify.setStatus(PaymentStatus.FINISH);
				    	        	paymentNotify.setFinishTime(UtilDate.getDateFormatter());
				    	        	paymentService.updatePaymentStatus(paymentNotify);
  			    	        	    System.err.println("---tradeno---"+payment.getTradeNo()+"-----notify success------------");

				    	        	//6、告知支付宝
				    	        	return "success";
								}else{
									return "err";
								}
			    			}
 
				}else{
		        	//2.1、支付失败
		       	 
					System.err.println(payment.getTradeNo()+":支付失败");
		        	return "error";
		    	}
			
 
		
	}
	
 
	
	
	
	 

}
