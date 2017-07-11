package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	
	public static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	
	
	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	public static String partner = "2088911876712776";
	
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	public static String seller_id = partner;

	//商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	public static String private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK7HFwMCVy50Ct3roFkcxpsg5+oHdB2cZB5Anae3+M0tYGuraqSnX5J3mQZZKUBuBdlhHBTr+kkvYC1fLjaNC5VdH+zQ7QHUwCGh0LDpXxLA0d+19TjRxRolxXu1XFOnda/34D4uXZHoPYxApHk4KjdBFM+ThBCGF1N98k4SdOkZAgMBAAECgYA1kw+Ju5jvw2BlYEnZpwl2GeMdCMXeIcdKwrHQ7ILWmfc5aeUZrbiNFB4TJReRCWi/lpXeD+u7rUsE/W0UCa4ny8/yQpunphphCnhacjKISv3Z29x/5uWDyYYN5DZpY5bxCQLdGTZkOTuaanbhaKt9zVzMrT0ueqp9f5ZW0XPaUQJBAOMGme5WvV2f4ijEwlBRXV0WwnxV8QT2i5tte8Egxn2Jt0fCnisfBHH3MXvW3Qel2G8t+7pRCfeoHuLkaw9hMl0CQQDFFW+nA19LBWVfkVe11ANvgP5w4qG8tO8fD1uWcCkqyLpwBKwa5wNrMLvSIP24PAIJsmawW98XpE4Ah/mfC93tAkAicJ/tbi8wAs82TBMGSDGXq5o5bngvvo8TNxqtA6HY7YlmU3nXJM9x0oQpO0EwpdIdm6Yyb5TJgzGcTfaH0MDpAkEAq/YjQMepew0PQc7QiB+gi8cyu46TbtM0nZZJ5L6ivOj9+8Wn4aVWD04rY5JTr0/0uz9FaHYQ91DumnoKbSLulQJAA1eP4YCcjlPZSM86tmNjrX/AZppzdTaV+U+0WRCbQX1RJbSJ66IAUWJauIEjNrkqQpWLFrNuXKfhWsM54427Sg==";
	
	// 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
	public static String alipay_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://121.42.157.19:8080/payservice/pay_notify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://121.42.157.19:8080/payservice/pay_return";

	// 签名方式
	public static String sign_type = "RSA";
	
	// 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
	public static String log_path = "C:\\";
		
	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
		
	// 支付类型 ，无需修改
	public static String payment_type = "1";
		
	// 调用的接口名，无需修改
	public static String service = "create_direct_pay_by_user";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	
//↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	// 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
	public static String anti_phishing_key = "";
	
	// 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
	public static String exter_invoke_ip = "";
	
     
	
		
 
	
	public static String[] must_params_out={"trade_no","subject","total_fee","body"};
	
	public static String[] must_params_in={"trade_no","trade_status" };

}

