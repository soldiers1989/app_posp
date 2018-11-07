package xdt.controller;

import com.alibaba.fastjson.JSON;
import com.jiupai.paysdk.entity.requestDTO.CapSingleTransferDTO;
import com.jiupai.paysdk.entity.requestDTO.SingleTransferDTO;
import com.jiupai.paysdk.service.impl.BaseJiupayServiceImpl;
import com.jiupai.paysdk.service.interfaces.BaseJiupayService;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.util.Signature;
import cfca.sadk.x509.certificate.X509Cert;
import fosun.sumpay.merchant.integration.core.request.Request;
import fosun.sumpay.merchant.integration.core.request.outer.AgentPayRequest;
import fosun.sumpay.merchant.integration.core.service.SumpayService;
import fosun.sumpay.merchant.integration.core.service.SumpayServiceImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import xdt.dto.BaseUtil;
import xdt.dto.gateway.util.HttpClient4Utils;
import xdt.dto.gateway.util.ParamUtils;
import xdt.dto.jp.MerchantUtil;
import xdt.dto.jp.RSASignUtil;
import xdt.dto.quickPay.util.cjtUtil;
import xdt.dto.sd.ByteUtil;
import xdt.dto.sd.HttpClientUtils;
import xdt.dto.sd.SMd5;
import xdt.dto.transfer_accounts.util.kltUtil;
import xdt.dto.transfer_accounts.util.smkUtil;
import xdt.dto.transfer_accounts.util.sqUtil;
import xdt.dto.yf.StringUtil;
import xdt.model.PmsBusinessPos;
import xdt.service.impl.TotalPayServiceImpl;
import xdt.util.HttpURLConection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import static com.jiupai.paysdk.entity.enums.Service.*;
/**
 *
 * 测试Controller
 * Created by tyh 
 */
@Controller
@RequestMapping("/aa")
public class aaa extends BaseAction{
	private static BaseJiupayService baseJiupayService = new BaseJiupayServiceImpl();
	private static final String String = null;
	Logger log = Logger.getLogger(this.getClass());
	
//  //统统付网关	
//	@RequestMapping(value = "SM_Test")
//	public void SMreturnUrl(HttpServletResponse response, HttpServletRequest request) throws IOException {
//		Map<String, String> result = new HashMap<String, String>();
//		Map<String, String> sParaTemp = new HashMap<String, String>();
//		sParaTemp.put("app_id","s100000040");
//		sParaTemp.put("terminal_type", "web");
//		sParaTemp.put("version", "1.0");
//		sParaTemp.put("service", "fosun.sumpay.cashier.web.trade.order.apply");
//		sParaTemp.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//		//sParaTemp.put("sign_type", "CERT");
//	
//		sParaTemp.put("mer_no", "s100000040");
//		sParaTemp.put("trade_code", "T0002");
//		sParaTemp.put("user_id", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//		sParaTemp.put("order_no", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//		sParaTemp.put("order_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//		sParaTemp.put("order_amount", "0.10");
//		sParaTemp.put("need_notify", "1");
//		sParaTemp.put("notify_url", BaseUtil.url+"/gateWay/SM_notifyUrl.action");
//		sParaTemp.put("need_return", "1");
//		sParaTemp.put("return_url", BaseUtil.url+"/gateWay/SM_returnUrl.action");
//		sParaTemp.put("currency", "CNY");
//		sParaTemp.put("goods_name", "an apple");
//		sParaTemp.put("goods_num", "1");
//		sParaTemp.put("goods_type", "1");
//		/*sParaTemp.put("remark", "备注");
//		sParaTemp.put("extension", "扩展");*/
//		/*sParaTemp.put("realname", "田永寒");
//		sParaTemp.put("id_no", "370323199612093224");*/
//		sParaTemp.put("attach", "附加数据ceshi");
//		
//		//建立请求
//		SumpayService ss = new SumpayServiceImpl();
//		Request request2 = new Request();
//		request2.setCharset("UTF-8");// 取jsp的请求编码
//		request2.setContent(JSON.toJSONString(sParaTemp)); // 业务参数的json字段
//		log.info("业务参数的json串="+JSON.toJSONString(sParaTemp));
//		request2.setPassword("sumpay"); // 
//		log.info("证书路径="+request.getServletContext().getRealPath("") + "/yixuntiankong.pfx");
//		request2.setPrivateKeyPath(request.getServletContext().getRealPath("") + "/yixuntiankong.pfx");
//		request2.setPublicKeyPath(request.getServletContext().getRealPath("") + "/yixun.cer");
//		String Surl ="http://101.71.243.74:8180/entrance/gateway.htm";
//		request2.setUrl(Surl);
//		request2.setDomain(InetAddress.getLocalHost().getHostAddress());//请求方域名"22qp179236.iask.in:41739"
//		
//		Map<String, String> res = ss.fosun.sumpay.merchant.integration.execute(request2);
//		log.info("响应数据="+res);
//		if(res.containsKey(Constant.RESP_CODE) && Constant.SUCCESS_RESP_CODE.equals(res.get(Constant.RESP_CODE))){
//			log.info("跳转的url="+res.get("redirect_url"));
//		    StringBuffer sbHtml = new StringBuffer();
//		    sbHtml.append("<html><head><meta http-equiv=\"refresh\" content=\"5;http://maijie1349.com\"></head><body>");
//		    sbHtml.append(
//	            "<form id=\"sumpaysubmit\" name=\"sumpaysubmit\" action=\""
//	                      + res.get("redirect_url") + "\" method=\"POST\">");
//	        //submit按钮控件请不要含有name属性
//	        sbHtml.append("<input type=\"submit\" value=\"submit\" style=\"display:none;\"></form>");
//	        sbHtml.append("</body>");
//	        sbHtml.append("<script>document.forms['sumpaysubmit'].submit();</script>");
//	        sbHtml.append("</html>");
//	        log.info("跳转的html="+sbHtml.toString());
//	        result.put("html",sbHtml.toString());
//	        outString(response, sbHtml.toString());
//		}else{
//			log.info("网关支付请求失败,"+res);
//			result.put("v_msg","请求失败");
//			result.put("v_code","15");
//			outString(response, JSON.toJSON(result));
//		}
//	}
	
	
	//统统付代付
	@RequestMapping(value = "SMDF_Test")
	public void SMDFreturnUrl(HttpServletResponse response, HttpServletRequest request) throws IOException {
		log.info("进来了==================");
		//Map<String, String> result = new HashMap<String, String>();
		AgentPayRequest sParaTemp = new AgentPayRequest();
		sParaTemp.setApp_id("s100000040");//商户号
		sParaTemp.setBank_branch("邮政储蓄银行");//收方支行名称 编码
		sParaTemp.setBank_code("ICBC");
		sParaTemp.setBank_line_no("403110006007");//收方联行号
		sParaTemp.setCard_no("6212264100011335373");//收方卡号 加密
		sParaTemp.setCard_type("0");//卡类型  0：储蓄卡，1：信用卡
		sParaTemp.setFee_amount("0");//手续费 
		sParaTemp.setFormat("JSON");
		sParaTemp.setId_no("370323199612093224");//收方证件号 加密
		sParaTemp.setIs_private("1");//对公2 对私1
		sParaTemp.setMer_no("s100000040");
		sParaTemp.setNeed_notify("1");
		sParaTemp.setNotify_url(BaseUtil.url+"/totalPayController/TTFNotifyUrl.action");
		sParaTemp.setOpen_city("附加数据ceshi");//收方开户城市 编码
		sParaTemp.setOrder_amount("0.10");//金额
		sParaTemp.setOrder_no(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单号
		sParaTemp.setRealname("田永寒");//收方姓名 加密
		sParaTemp.setService("fosun.sumpay.api.trade.agent.pay");
		sParaTemp.setTerminal_type("PC方式");
		sParaTemp.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		//sParaTemp.setUser_id(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		sParaTemp.setVersion("1.0");
		sParaTemp.setWithhold_sign("0");//扣款标示
		
		//建立请求
		fosun.sumpay.merchant.integration.core.request.Request request2 = new fosun.sumpay.merchant.integration.core.request.Request();
		request2.setCharset("UTF-8");
		request2.setContent(JSON.toJSONString(sParaTemp)); 
		request2.setPassword("sumpay"); //
		System.out.println("公钥路径="+request.getServletContext().getRealPath("") + "/yixuntiankong.pfx");
		request2.setPrivateKeyPath(request.getServletContext().getRealPath("") + "/yixuntiankong.pfx");
		request2.setPublicKeyPath(request.getServletContext().getRealPath("") + "/yixun.cer");
		request2.setUrl("http://101.71.243.74:8180/entrance/gateway.htm");
		request2.setDomain(InetAddress.getLocalHost().getHostAddress());
		request2.setAesEncodedWords(sParaTemp.getAesEncodedWords());
		request2.setBase64EncodedWords(sParaTemp.getBase64EncodedWords());
		request2.setCharsetChangeWords(sParaTemp.getCharsetChangeWords());
		System.out.println("request2="+request2);
		
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request2);
		log.info("代付响应信息="+res);
		if (res != null)
        {	
	        String response2 = res.get("response");
	        net.sf.json.JSONObject jsonResp2 = net.sf.json.JSONObject.fromObject(response2);
	        String status = jsonResp2.getString("status");
	        String remark = jsonResp2.getString("remark");
	        log.info("统统付代付状态码：" + status);
	        log.info("统统付代付状态：" + remark);
	        if (!"000000".equals(res.get("resp_code")))//||status.endsWith("03")
	        {
			log.info("操作成功");
			response.sendRedirect(res.get("redirect_url"));
		}else{
			log.info("操作失败");
			response.setContentType("application/json;charset=UTF-8");
	        response.setCharacterEncoding("UTF-8");
	        response.setHeader("Pragma", "No-cache");
	        response.setHeader("Cache-Control", "no-cache");
	        response.setDateHeader("Expires", 0);
			response.getWriter().println(JSON.toJSONString(res));
		}
        }else {
        	log.info("无响应");
        }
	}
	@RequestMapping(value = "SMYBs")
	public void SMYBs(HttpServletResponse response, HttpServletRequest request) throws IOException {
		request.getSession();
		String params = "v_code=00&v_mid=10011110986&v_msg=请求成功&v_oid=20181101192345471&v_txnAmt=3000.00";
		//params = URLEncoder.encode(params, "UTF-8");
		String path = "http://22qp179236.iask.in:41739/app_posp/aa/SMYB.action" + "?" + params;
		System.out.println("pageUrl 商户页面 重定向：" + path);
		//response.setContentType("text/html;charset=UTF-8");

		response.sendRedirect(path.replace(" ", ""));
	}
	
	//下游异步接收测试
	@RequestMapping(value = "SMYB")
	public void SMYB(HttpServletResponse response, HttpServletRequest request) throws IOException {
		System.out.println("接收异步通知的方法进来了========");
		request.getSession();
//		request.setCharacterEncoding("UTF-8");
		String oid=request.getParameter("v_oid");
		String mid=request.getParameter("v_mid");
		String code=request.getParameter("v_code");
		String status=request.getParameter("v_msg");
//		byte[] b=status.getBytes("ISO-8859-1");
//		String s=new String(b,"UTF-8");
		String v_txnAmt=request.getParameter("v_txnAmt");
		
		/*request.getSession();
		StringBuffer sb = new StringBuffer() ; 
		InputStream is = request.getInputStream(); 
		InputStreamReader isr = new InputStreamReader(is,"UTF-8");
		BufferedReader br = new BufferedReader(isr); 
		String s = "" ; 
		while((s=br.readLine())!=null){ 
		sb.append(s) ; 
		} 
		String str =sb.toString();
		String str1 = str.replace("&", "\",\"");
		String str2 = str1.replace("=", "\":\"");
		String string = "{\""+str2+"\"}";
		System.out.println("aaaaaaaaaa::::"+string);
		JSONObject jsonObject = JSONObject.fromObject(string);
		String oid = (String) jsonObject.get("v_oid");
		String mid = (String) jsonObject.get("v_mid");
		String code = (String) jsonObject.get("v_code");
		String status = (String) jsonObject.get("v_msg");
		String v_txnAmt = (String) jsonObject.get("v_txnAmt");*/
		log.info("订单号="+oid);
		log.info("商户号="+mid);
		log.info("异步通知状态="+code);
		log.info("订单交易状态="+status);
		log.info("订单交易金额="+v_txnAmt);

		Map<String, String> resps = new HashMap<String, String>();
		if(oid!=null&&oid!=""&&code!=null&&code!="") {
			log.info("返回处理成功的标志");
			resps.put("success", "true");
			outString(response, JSON.toJSON(resps));
		}else {
			log.info("异步通知参数为空！");
		}
	}
	
	//给下游发送post请求
	@RequestMapping(value = "SM")
	public String YB(HttpServletRequest request) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://22qp179236.iask.in:41739/app_posp/aa/SMYB.action");
		httpPost.setHeader("Content-Type", "text/json; charset=utf-8");
		// httpPost.setEntity(new StringEntity(URLEncoder.encode(data, "UTF-8")));
		Map<String, String> map=new HashMap<>();
		map.put("v_status_msg", "支付成功");
		map.put("v_code", "00");
		map.put("v_msg", "成功");
		map.put("v_status", "0000");
		map.put("v_mid", "10012014404");
		map.put("v_attach", "大饼夹一切");
		map.put("v_oid", "1539765980000");
		map.put("v_sign", "42637DBF677D83F69E9222A6FCC5DBB8");
		map.put("v_txnAmt", "1");
		String params = HttpURLConection.parseParams(map);
		httpPost.setEntity(new StringEntity(params, "UTF-8"));
		System.out.println("post提交的参数="+httpPost.getEntity());
		HttpResponse response = httpClient.execute(httpPost);
		log.info("response===="+response);
		String httpEntityContent = getHttpEntityContent(response);
		httpPost.abort();
		log.info("httpEntityContent====="+httpEntityContent);
		return httpEntityContent;
	}
	
	private static String getHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			System.out.println("entity!=null");
			InputStream is = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line + "\n");
				line = br.readLine();
			}
			System.out.println("sb.toString()="+sb.toString());
			return sb.toString();
		}
		return "";
	}
	
	
	//传化网关测试
	@RequestMapping(value = "CHWG")
	public void CHWG(HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("#############传化网关支付处理 开始#############");
		Map<String, Object> chMap = new LinkedHashMap<String, Object>();
		chMap.put("appid", "2234001");// appid
		chMap.put("tf_timestamp",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
		chMap.put("service_id", "tf56pay.gateway.bankPay");
		chMap.put("sign_type", "MD5");
		chMap.put("terminal", "PC");
		chMap.put("fronturl", BaseUtil.url+"/gateWay/CH_returnUrl.action");//前台通知地址
		chMap.put("backurl", BaseUtil.url+"/gateWay/CH_notifyUrl.action");//后台通知地址
		chMap.put("subject", "an apple");//商品名称
		chMap.put("businesstype","网关和代付");//业务类型
		chMap.put("kind","购物");//消费场景
		chMap.put("description","一个苹果");//商品描述
		chMap.put("businessnumber",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单号
		chMap.put("transactionamount","0.1");//交易金额 元
		chMap.put("toaccountnumber","8800010267735");//收款方会员账号
		chMap.put("bankcode", "ABC");//银行编号 PSBC ICBC
		chMap.put("bankaccounttype", "储蓄卡");//银行卡类型					
		chMap.put("accountproperty", "对私");//账户属性						
		chMap.put("merchtonline", "0");// 0线上or1线下交易
		chMap.put("clientip", InetAddress.getLocalHost().getHostAddress());//客户端请求IP		
		chMap.put("dog_sk", "b1T0174m49yp6305WRiI");
		chMap.put("tf_sign", ParamUtils.map2MD5(chMap));// 签名
		logger.info("签名="+chMap.get("tf_sign"));
		chMap.remove("dog_sk");		
		//生产
		String curl = "https://openapi.tf56.com/service/api";
		//测试		 
		//String curl = "https://openapitest.tf56.com/service/api";
		logger.info("请求参数="+chMap.toString());
		String resp = HttpClient4Utils.sendHttpRequest(curl,chMap,"UTF-8",true);
		logger.info("传化网关支付响应信息="+resp);
		if(resp!=null&&resp!="") {
			//返回信息处理
			JSONObject jsonResp = JSONObject.fromObject(resp);
			String code = jsonResp.getString("code");
			String msg = jsonResp.getString("msg");
			String biz_code = jsonResp.getString("biz_code");
			String biz_msg = jsonResp.getString("biz_msg");
			
			if(code.equals("GP_00")&&biz_code.equals("GPBIZ_00")) {
				logger.info("传化网关支付请求成功");
				
				String datas = jsonResp.getString("data");
				JSONObject jsonResps = JSONObject.fromObject(datas);
				String htmldata = jsonResps.getString("htmldata");
				String htmldatas = URLDecoder.decode(htmldata, "UTF-8");
				logger.info("htmldatas1==="+htmldatas);
				BASE64Decoder decoder = new BASE64Decoder();
				String html=new String(decoder.decodeBuffer(htmldatas), "UTF-8");
				logger.info("htmldatas2==="+html);
				outString(response, html);
				
			}else {
				logger.info("传化网关支付请求失败");
			}
		}
	}
	
	
	//传化代付测试
	@RequestMapping(value = "DFCH")
	public void DFCH(HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("#############传化代付处理 开始#############");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("service_id", "tf56enterprise.enterprise.payForCustomer");
		map.put("appid", "2234001");
		map.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
		map.put("businessnumber", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("subject", "an apple");
		map.put("transactionamount", "0.1");
		map.put("bankcardnumber", "6217991100013041275");
		map.put("bankcardname", "田永寒");
		map.put("sign_type", "MD5");
		map.put("bankname", "中国邮储银行");
		map.put("bankcardtype", "个人");
		map.put("bankaccounttype", "储蓄卡");
		map.put("fromaccountnumber", "8800010267735");
		map.put("terminal", "PC");
		map.put("backurl", BaseUtil.url+"/totalPayController/CHNotifyUrl.action");
		map.put("dog_sk", "b1T0174m49yp6305WRiI");
		map.put("tf_sign", ParamUtils.map2MD5(map));// 签名
		logger.info("签名="+map.get("tf_sign"));
		map.remove("dog_sk");
		//生产
		String curl = "http://openapi.tf56.com/service/api";
		//测试		 
		//String curl = "https://openapitest.tf56.com/service/api";
		logger.info("请求参数="+map.toString());
		String resp = HttpClient4Utils.sendHttpRequest(curl,map,"UTF-8",true);
		logger.info("传化代付响应信息="+resp);
		/*
		 * {"code":"BC_E0006","count":"","data":"","msg":"账户[partid:10,242,615,accountnumber:8800010267735]余额不足！","result":"error"}
		 */
		if(resp!=null&&resp!="") {
			//返回信息处理
			JSONObject jsonResp = JSONObject.fromObject(resp);
			String result = jsonResp.getString("result");//请求结果标志 success：请求授理成功不代表交易成功;error：业务失败;exception：网络异常失败不代表交易失败
			String code = jsonResp.getString("code");//错误编码
			String msg = jsonResp.getString("msg");//错误描述
			String data = jsonResp.getString("data");//			
			if(result.equals("error")) {
				logger.info("传化代付请求失败!"+code+","+msg);
				/**
				 * 修改订单状态，补款
				 */
				
			}else if(result.equals("success")){
				logger.info("传化代付请求成功");//交易状态异步通知
			}else {
				logger.info("网络异常失败，不代表交易失败。"+code+","+msg);
				/**
				 * 主动查询
				 */
			}
		}else {
			logger.info("传化代付没有响应信息");
			/**
			 * 主动查询
			 */
		}
	}
	
		//传化代付查询
	@RequestMapping(value = "CHS")
	public Map<String, String> chQuick() throws IOException {
	    log.info("传化订单查询来了！");	
	    Map<String, String> map=new HashMap<>();
	  
	    try {
	    	Map<String, Object> maps = new LinkedHashMap<String, Object>();
			maps.put("service_id", "tf56pay.enterprise.queryTradeStatus");
			maps.put("appid", "2234001");
			maps.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
			maps.put("businessnumber", "1539931537259");
			maps.put("dog_sk", "b1T0174m49yp6305WRiI");
			maps.put("tf_sign", ParamUtils.map2MD5(maps));// 签名
			logger.info("签名="+maps.get("tf_sign"));
			maps.remove("dog_sk");
			//生产
			String curl = "http://openapi.tf56.com/service/api";
			//测试		 
			//String curl = "https://openapitest.tf56.com/service/api";
			logger.info("请求参数="+maps.toString());
			String resp = HttpClient4Utils.sendHttpRequest(curl,maps,"UTF-8",true);
			logger.info("传化订单查询响应信息="+resp);
			if(resp!=null&&resp!="") {
				map.put("v_code", "00");
				map.put("v_msg", "请求成功");
				
				JSONObject jsonResps = JSONObject.fromObject(resp);
				String status = jsonResps.getString("result");		
				
				if(status.equals("success")) {
					String backData = jsonResps.getString("data");
					JSONObject jsonResp = JSONObject.fromObject(backData);
					String result = jsonResp.getString("status");
					log.info("订单状态：" + result);
					if(result.equals("成功")) {
						map.put("v_status", "0000");
		        		map.put("v_status_msg", "成功");
					}else if(status.equals("失败")) {
						map.put("v_status", "1001");
		        		map.put("v_status_msg", "失败");
					}else if(status.equals("已退票")) {
						map.put("v_status", "1001");
		        		map.put("v_status_msg", "失败");
					}
				}else {
					map.put("v_code", "01");
					map.put("v_msg", "请求失败");
				}
			}else {
				map.put("v_code", "01");
				map.put("v_msg", "请求失败");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
		
		
	//传化黑名单查询
	@RequestMapping(value = "CHHMD")
	public Map<String, String> CHHMD(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化黑名单查询来了！");	
	    Map<String, String> map=new HashMap<>();
	  
	    try {
	    	String customerCode = "BJ146061023012924482";
	    	//String customerCode=new String(request.getParameter("customerCode").getBytes("iso8859-1"),"utf-8");
    		/*String userName=new String(request.getParameter("userName").getBytes("iso8859-1"),"utf-8");
    		String userCard=new String(request.getParameter("userCard").getBytes("iso8859-1"),"utf-8");
    		String userPhone=new String(request.getParameter("userPhone").getBytes("iso8859-1"),"utf-8");
    		String userBank=new String(request.getParameter("userBank").getBytes("iso8859-1"),"utf-8"); 
    		String otherAccount = new String(request.getParameter("otherAccount").getBytes("iso8859-1"),"utf-8");*/
	    	String userName="";
    		String userCard="";
    		String userPhone="";
    		String userBank=""; 
    		String otherAccount = "";
    		//密钥
    		String secretKey="361F88A117DDD33BA4D02587C5A6FBB8";
    		
    		String[] a = {customerCode,secretKey};
    		
            String characterEncoding ="UTF-8"; 
             //签名
    		String	signature = HttpClient4Utils.createSign(characterEncoding,a);
    		response.setContentType("text/html;charset=UTF-8");  
    
    		//userName=java.net.URLEncoder.encode(userName,"utf-8");
    		String param="customerCode="+customerCode+"&userName="+userName+
            		"&userCard="+userCard+"&userPhone="+userPhone+
            		"&userBank="+userBank+"&otherAccount="+otherAccount+
            		"&signature="+signature;
//	    		String param="customerCode="+customerCode+
//	            		"&signature="+signature;
    		request.setCharacterEncoding("UTF-8"); 
    		System.out.println(param);
    	   
    		String url="http://blacklist.sdshopping.cn/user-api/blacklist";

    	    String sr=HttpClient4Utils.sendPost(url, param);
    	   System.out.println("传化黑名单返回信息="+sr);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
		
		
		
		
		public static String getV4IP(){
			String ip = "";
			String chinaz = "http://ip.chinaz.com";
			
			StringBuilder inputLine = new StringBuilder();
			String read = "";
			URL url = null;
			HttpURLConnection urlConnection = null;
			BufferedReader in = null;
			try {
				url = new URL(chinaz);
				urlConnection = (HttpURLConnection) url.openConnection();
			    in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
				while((read=in.readLine())!=null){
					inputLine.append(read+"\r\n");
				}
				//System.out.println(inputLine.toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(in!=null){
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
			Matcher m = p.matcher(inputLine.toString());
			if(m.find()){
				String ipstr = m.group(1);
				System.out.println(ipstr);
				ip = ipstr;
				//System.out.println(ipstr);
			}
			return ip;
		}
		
		
		private static String getMyIP() throws IOException {
			InputStream ins = null;
			try {
				URL url = new URL("http://iframe.ip138.com/ic.asp");
				URLConnection con = url.openConnection();
				ins = con.getInputStream();
				InputStreamReader isReader = new InputStreamReader(ins, "GB2312");
				BufferedReader bReader = new BufferedReader(isReader);
				StringBuffer webContent = new StringBuffer();
				String str = null;
				while ((str = bReader.readLine()) != null) {
					webContent.append(str);
				}
				int start = webContent.indexOf("[") + 1;
				int end = webContent.indexOf("]");
				return webContent.substring(start, end);
			} finally {
				if (ins != null) {
					ins.close();
				}
			}
		}

		public static String getRealIp() throws SocketException {
			String localip = null;// 本地IP，如果没有配置外网IP则返回它
			String netip = null;// 外网IP
	 
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
	 
			if (netip != null && !"".equals(netip)) {
				return netip;
			} else {
				return localip;
			}
		}

	
	public static void main(String[] args) throws IOException {
//		String aa=getV4IP();
//		String aa=getMyIP();
		String aa=getRealIp();
		System.out.println("本机外网IP="+aa);
	}

	//传化快捷签约回调
	@RequestMapping(value = "CHKJQYHD")
	public Map<String, String> CHKJQYHD(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化快捷签约异步来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	String orderno=request.getParameter("businessrecordnumber");
	    	String certcode=request.getParameter("certcode");
	    	String certificatenumber=request.getParameter("certificatenumber");
	    	String status=request.getParameter("status");
	    	
	    	logger.info("签约订单号="+orderno);
	    	logger.info("签约编号="+certcode);
	    	logger.info("签约证件="+certificatenumber);
	    	logger.info("签约状态="+status);
	    	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	
	//传化快捷签约（无页面跳转）
	@RequestMapping(value = "CHKJQY")
	public Map<String, String> CHKJQY(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化快捷签约来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	Map<String, Object> map = new HashMap<>();
	    	map.put("appid", "2234001");//appID
			map.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
			map.put("service_id","tf56pay.gateway.quickSign" );//服务名称
			map.put("sign_type", "MD5");//签名方式
			map.put("terminal", "PC");//终端类型
			map.put("version", "01");//版本号
			
			map.put("backurl", BaseUtil.url+"/aa/CHKJQYHD.action");//回调地址
			map.put("businessnumber", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单号
			map.put("bankcardnumber", "6217991100013041275");//银行卡号
			map.put("bankcardname","田永寒");//持卡人姓名
			map.put("certificatenumber", "370323199612093224");//证件号
			map.put("certtype", "01");//证件类型
			map.put("bankmobilenumber", "15244166253");//银行预留手机号
			map.put("clientip", getRealIp());//客户端请求IP
			map.put("merchantuserid", "10011018080");//商户平台中用户id
			
			/*map.put("cvv2", "835");//信用卡必填/非信用卡可不填
			map.put("expiredate", "0323");//信用卡必填/非信用卡可不填*/
			map.put("dog_sk", "b1T0174m49yp6305WRiI");
			String curl = "https://openapi.tf56.com/service/api";
			map.put("tf_sign", ParamUtils.map2MD5(map));
			logger.info("签名="+map.get("tf_sign"));
			map.remove("dog_sk");
			logger.info("上送的参数="+map);
			String resp = HttpClient4Utils.sendHttpRequest(curl, map, "UTF-8", true);
			System.out.println("传化快捷签约响应信息:" + resp);
			
			if(resp!=null&&resp!="") {
				//返回信息处理
				JSONObject jsonResp = JSONObject.fromObject(resp);
				String code = jsonResp.getString("code");
				String biz_code = jsonResp.getString("biz_code");
				String biz_msg = jsonResp.getString("biz_msg");
				if(code.equals("GP_00")&&biz_code.equals("GPBIZ_00")) {
					logger.info("传化快捷签约请求成功");								
					String datas = jsonResp.getString("data");
					JSONObject jsonResps = JSONObject.fromObject(datas);
					String businessrecordnumber = jsonResps.getString("businessrecordnumber");//签约订单号
					String certcode = jsonResps.getString("certcode");//签约编号
					String status = jsonResps.getString("status");//签约状态
					logger.info("签约订单号="+businessrecordnumber);
					logger.info("签约编号="+certcode);
					logger.info("签约状态="+status);
				}else {
					logger.info("传化快捷签约请求失败,"+biz_msg);
					remap.put("v_msg","请求失败");
					remap.put("v_code","15");		
				}
			}							
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//传化快捷签约确认
	@RequestMapping(value = "CHKJQYQR")
	public Map<String, String> CHKJQYQR(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化快捷签约确认来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	Map<String, Object> map = new HashMap<>();
	    	map.put("appid", "2234001");//appID
			map.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
			map.put("service_id","tf56pay.gateway.quickSignConfirm" );//服务名称
			map.put("sign_type", "MD5");//签名方式
			map.put("terminal", "PC");//终端类型
			map.put("version", "01");//版本号
		
			map.put("businessrecordnumber", "xxxxxxxxx");//签约订单号
			map.put("clientip", getRealIp());//客户端请求IP
			map.put("verifycode", "xxxxxx");//短信验证码
			
			map.put("dog_sk","b1T0174m49yp6305WRiI");
		
			String curl = "https://openapi.tf56.com/service/api";
			map.put("tf_sign", ParamUtils.map2MD5(map));
			logger.info("签名="+map.get("tf_sign"));
			map.remove("dog_sk");
			String resp = HttpClient4Utils.sendHttpRequest(curl, map, "UTF-8", true);
			System.out.println("传化快捷签约确认响应信息:" + resp);
			
			if(resp!=null&&resp!="") {
				//返回信息处理
				JSONObject jsonResp = JSONObject.fromObject(resp);
				String code = jsonResp.getString("code");
				String biz_code = jsonResp.getString("biz_code");
				String biz_msg = jsonResp.getString("biz_msg");
				if(code.equals("GP_00")&&biz_code.equals("GPBIZ_00")) {
					logger.info("传化快捷签约确认请求成功");								
					String datas = jsonResp.getString("data");
					JSONObject jsonResps = JSONObject.fromObject(datas);
					String businessrecordnumber = jsonResps.getString("businessrecordnumber");//签约订单号
					String certcode = jsonResps.getString("certcode");//签约编号
					String status = jsonResps.getString("status");//签约状态
					logger.info("签约订单号="+businessrecordnumber);
					logger.info("签约编号="+certcode);
					logger.info("签约状态="+status);
				}else {
					logger.info("传化快捷签约确认请求失败,"+biz_msg);
					remap.put("v_msg","请求失败");
					remap.put("v_code","15");		
				}
			}							
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}	
	
	//传化快捷签约查询
	@RequestMapping(value = "CHKJQYCX")
	public Map<String, String> CHKJQYCX(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化快捷签约查询来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	Map<String, Object> map = new HashMap<>();
	    	map.put("appid", "2234001");//appID
			map.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
			map.put("service_id","tf56pay.gateway.signQuery" );//服务名称
			map.put("sign_type", "MD5");//签名方式
			map.put("terminal", "PC");//终端类型
			map.put("version", "01");//版本号
		
			map.put("businessrecordnumber", "xxxxxxxxx");//签约订单号
			
			map.put("dog_sk", "b1T0174m49yp6305WRiI");
		
			String curl = "https://openapi.tf56.com/service/api";
			map.put("tf_sign", ParamUtils.map2MD5(map));
			logger.info("签名="+map.get("tf_sign"));
			map.remove("dog_sk");
			String resp = HttpClient4Utils.sendHttpRequest(curl, map, "UTF-8", true);
			System.out.println("传化快捷签约查询响应信息:" + resp);
			
			if(resp!=null&&resp!="") {
				//返回信息处理
				JSONObject jsonResp = JSONObject.fromObject(resp);
				String code = jsonResp.getString("code");
				String biz_code = jsonResp.getString("biz_code");
				String biz_msg = jsonResp.getString("biz_msg");
				if(code.equals("GP_00")&&biz_code.equals("GPBIZ_00")) {
					logger.info("传化快捷签约查询请求成功");								
					String datas = jsonResp.getString("data");
					JSONObject jsonResps = JSONObject.fromObject(datas);
					String businessrecordnumber = jsonResps.getString("businessrecordnumber");//签约订单号
					String certcode = jsonResps.getString("certcode");//签约编号
					String status = jsonResps.getString("status");//签约状态
					logger.info("签约订单号="+businessrecordnumber);
					logger.info("签约编号="+certcode);
					logger.info("签约状态="+status);
				}else {
					logger.info("传化快捷签约查询请求失败,"+biz_msg);
					remap.put("v_msg","请求失败");
					remap.put("v_code","15");		
				}
			}							
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}		

	//传化快捷支付
	@RequestMapping(value = "CHKJZF")
	public Map<String, String> CHKJZF(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化快捷支付来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	Map<String, Object> map = new HashMap<>();
	    	map.put("appid", "2234001");//appID
			map.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
			map.put("service_id","tf56pay.cashier.quickPayConfirm" );//服务名称
			map.put("sign_type", "MD5");//签名方式
			map.put("terminal", "PC");//终端类型
			map.put("version", "01");//版本号
		
			map.put("backurl", "www.baidu.com");//回调地址			
			map.put("subject", "笔记本");//商品名称
			map.put("businesstype","商家消费");//业务类型
			map.put("kind", "实物_数码家电");//消费场景
			map.put("businessnumber", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单号
			map.put("transactionamount", "1");//交易金额
            map.put("toaccountnumber", "8800010267735");//收款方会员账号			
			map.put("clientip", getRealIp());//客户端请求IP
			map.put("merchantuserid", "xxxxx");//商户平台中用户id
			
			map.put("dog_sk", "b1T0174m49yp6305WRiI");
		
			String curl = "https://openapi.tf56.com/service/api";
			map.put("tf_sign", ParamUtils.map2MD5(map));
			logger.info("签名="+map.get("tf_sign"));
			map.remove("dog_sk");
			String resp = HttpClient4Utils.sendHttpRequest(curl, map, "UTF-8", true);
			System.out.println("传化快捷支付响应信息:" + resp);
			
			if(resp!=null&&resp!="") {
				//返回信息处理
				JSONObject jsonResp = JSONObject.fromObject(resp);
				String code = jsonResp.getString("code");
				String biz_code = jsonResp.getString("biz_code");
				String biz_msg = jsonResp.getString("biz_msg");
				if(code.equals("GP_00")&&biz_code.equals("GPBIZ_00")) {
					logger.info("传化快捷支付请求成功");								
					String datas = jsonResp.getString("data");
					JSONObject jsonResps = JSONObject.fromObject(datas);
					String businessrecordnumber = jsonResps.getString("businessrecordnumber");//支付订单号
					String businessnumber = jsonResps.getString("businessnumber");//业务订单号
					logger.info("支付订单号="+businessrecordnumber);
					logger.info("业务订单号="+businessnumber);
				}else {
					logger.info("传化快捷支付请求失败,"+biz_msg);
					remap.put("v_msg","请求失败");
					remap.put("v_code","15");		
				}
			}							
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//传化快捷支付短信确认
	@RequestMapping(value = "CHKJZFQR")
	public Map<String, String> CHKJZFQR(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("传化快捷支付短信确认来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	Map<String, Object> map = new HashMap<>();
	    	map.put("appid", "2234001");//appID
			map.put("tf_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//时间戳
			map.put("service_id","tf56pay.cashier.quickPayConfirm" );//服务名称
			map.put("sign_type", "MD5");//签名方式
			map.put("terminal", "PC");//终端类型
			map.put("version", "01");//版本号
		
			map.put("businessrecordnumber", "xxxxxxxxx");//签约订单号
			map.put("clientip", getRealIp());//客户端请求IP
			map.put("verifycode", "xxxxxx");//短信验证码
			
			map.put("dog_sk", "b1T0174m49yp6305WRiI");
		
			String curl = "https://openapi.tf56.com/service/api";
			map.put("tf_sign", ParamUtils.map2MD5(map));
			logger.info("签名="+map.get("tf_sign"));
			map.remove("dog_sk");
			String resp = HttpClient4Utils.sendHttpRequest(curl, map, "UTF-8", true);
			System.out.println("传化快捷支付短信确认响应信息:" + resp);
			
			if(resp!=null&&resp!="") {
				//返回信息处理
				JSONObject jsonResp = JSONObject.fromObject(resp);
				String code = jsonResp.getString("code");
				String biz_code = jsonResp.getString("biz_code");
				String biz_msg = jsonResp.getString("biz_msg");
				if(code.equals("GP_00")&&biz_code.equals("GPBIZ_00")) {
					logger.info("传化快捷支付短信确认请求成功");								
					String datas = jsonResp.getString("data");
					JSONObject jsonResps = JSONObject.fromObject(datas);
					String businessrecordnumber = jsonResps.getString("businessrecordnumber");//支付订单号
					String businessnumber = jsonResps.getString("businessnumber");//业务订单号
					logger.info("支付订单号="+businessrecordnumber);
					logger.info("业务订单号="+businessnumber);
				}else {
					logger.info("传化快捷支付短信确认请求失败,"+biz_msg);
					remap.put("v_msg","请求失败");
					remap.put("v_code","15");		
				}
			}							
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	

	//双乾代付
	@RequestMapping(value = "SQDF")
	public Map<String, String> SQDF(HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("#############双乾代付处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		
		String merno="168885";//商户号
		String time=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//发起时间
		String totalAmount="0.1";//总金额,以元为单位,精确到分
		String num="1";//笔数
		String batchNo=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//批次号
		//开户名|开户行代码|卡号|卡类别1:个人（借记卡）2:企业3:信用卡|金额|订单号|地级市代码默认填（000）|备注(非必填)	
		String content="田永寒|ICBC|6222030302000727568|1|0.1|"+batchNo+"|000|";
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("merno", merno);
		map.put("time", time);
		map.put("totalAmount", totalAmount);
		map.put("num", num);
		map.put("batchNo", batchNo);			
		map.put("content", content);
		String signature="";
		//证书生成签名		
        String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/168885_test.pfx";// 商户私钥 
		String certPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/95epay_test_cfca.cer";// 双乾测试环境公钥		
		String passWord = "123123";// 商户私钥证书密码
        try {
        	String beforeSignedData = joinMapValue(map, '&');
        	signature = sqUtil.signMessageByP1(beforeSignedData, pfxPath, passWord);
		} catch (Exception e2) {
			e2.printStackTrace();
		}       
		
//      生产
//		String curl = "https://df.95epay.cn/merchant/numberPaid.action";
//      测试		 
        String curl = "http://218.4.234.150:9600/merchant/numberPaid.action";
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httpPost
        HttpPost httppost = new HttpPost(curl);
        
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("merno", merno));
        formparams.add(new BasicNameValuePair("time", time));
        formparams.add(new BasicNameValuePair("totalAmount", totalAmount));
        formparams.add(new BasicNameValuePair("num", num));
        formparams.add(new BasicNameValuePair("batchNo", batchNo));
        formparams.add(new BasicNameValuePair("content", content));
        formparams.add(new BasicNameValuePair("signature", signature));
		logger.info("请求参数="+formparams.toString());
		
		UrlEncodedFormEntity uefEntity;		
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            logger.info("请求地址=" + httppost.getURI());  
            CloseableHttpResponse resp = httpclient.execute(httppost);
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
            	String entitys = EntityUtils.toString(entity, "UTF-8");
            	logger.info("双乾代付请求响应信息: " + entitys);
                JSONObject json = JSONObject.fromObject(entitys);
                
                //验签
                HashMap<String, Object> data = new LinkedHashMap<String, Object>();
                Iterator<?> it = json.keys();  
                // 遍历jsonObject数据，添加到Map对象  
                while(it.hasNext()){  
                    String key = String.valueOf(it.next());  
                    Object value = json.get(key);
                    if(key.equals("signature")||key.equals("remark")){
                    	continue;
                    }
                    data.put(key, value);  
                }
                String beforeSignedData = joinMapValue(data, '&');
                String s = (String) json.get("signature");
                logger.info(beforeSignedData);
                boolean flag=sqUtil.verifyMessageByP1(beforeSignedData, s, certPath);
                logger.info("Verify result: " +flag);
                if(flag==true) {//验签成功               
	                String result = json.getString("status");//请求结果标志 success 、
	        		String remark = json.getString("remark");//请求结果
	        		
	        		remap.put("v_code", "00");
	        		remap.put("v_msg", "请求成功");
	        		if(result.equals("false")) {
	        			logger.info("双乾代付请求失败!"+remark);
	        			remap.put("v_code", "15");
	        			remap.put("v_msg", "请求失败");
	        			
	        			logger.info("补款");
	        			
	        		}else {
	        			logger.info("主动查询代付结果");
	        			remap=SQDFCX(batchNo,"168885");
	        		}
                }else {
                	remap.put("v_code", "00");
            		remap.put("v_msg", "请求成功");
            		logger.info("验签失败，主动查询代付结果");
        			remap=SQDFCX(batchNo,"168885");
                }
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//双乾代付查询
	public Map<String, String> SQDFCX(String orderId,String merId) throws Exception {
		logger.info("#############双乾代付查询处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("merno", merId);//商户号
		map.put("batchNo", orderId);//批量代付批次号
		map.put("orderNo", orderId);//单笔代付订单号
	
		//证书生成签名
		String signature="";
		String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/168885_test.pfx";// 商户私钥 
		String certPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/95epay_test_cfca.cer";// 双乾测试环境公钥		
		String passWord = "123123";// 商户私钥证书密码
        try {
        	String beforeSignedData = joinMapValue(map, '&');
        	signature = sqUtil.signMessageByP1(beforeSignedData, pfxPath, passWord);
		} catch (Exception e2) {
			e2.printStackTrace();
		}       
		
//      生产
//		String curl = "https://df.95epay.cn/merchant/numberPaidQuery.action";
//      测试		 
        String curl = "http://218.4.234.150:9600/merchant/numberPaidQuery.action";
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httpPost
        HttpPost httppost = new HttpPost(curl);
        
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("merno", merId));
        formparams.add(new BasicNameValuePair("batchNo", orderId));
        formparams.add(new BasicNameValuePair("orderNo", orderId));
        formparams.add(new BasicNameValuePair("signature", signature));
		logger.info("双乾代付查询请求参数="+formparams.toString());
		
		UrlEncodedFormEntity uefEntity;	
        try {
        	uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            logger.info("双乾代付查询请求地址=" + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            HttpEntity entity = response.getEntity();
            if (entity != null) {
            	String entitys = EntityUtils.toString(entity, "UTF-8");
            	logger.info("双乾代付查询响应信息: " + entitys);
                JSONObject json = JSONObject.fromObject(entitys);
                
                /*HashMap<String, Object> data = new LinkedHashMap<String, Object>();                
                Iterator<?> it = json.keys();  
                // 遍历jsonObject数据，添加到Map对象  
                while(it.hasNext()){  
                    String key = String.valueOf(it.next());  
                    Object value = json.get(key);
                    if(key.equals("signature") || key.equals("remark")){
                    	continue;
                    }
                    data.put(key, value);  
                }
                String responseBeforeSignedData = joinMapValue(data, '&');
                String status = (String) json.get("searchResult");
                String s = (String) json.get("signature");
                //status为失败时无验签
                if(!"failure".equals(status)){
                	System.out.println("CFCA Verify result: " + 
                			sqUtil.verifyMessageByP1(responseBeforeSignedData,s, certPath));
                }*/
            	String result = json.getString("searchResult");//请求结果标志 success表示成功,false表示错误,failure表示失败(不用验签)
    			String remark = json.getString("remark");
    			if(result.equals("success")) {
    				remap.put("v_code", "00");
    				remap.put("v_msg", "请求成功");
    	    		
    				logger.info("代付查询请求成功");
    				String contents = json.getString("contents");
    				logger.info("contents===="+contents);
    				JSONArray jsonarray=JSONArray.fromObject(contents);
    				String aa=jsonarray.getString(0);
    				JSONObject jsonResps = JSONObject.fromObject(aa);   		
    				String state = jsonResps.getString("state");//单笔状态（1:已提交,2:已处理,3:已退回,4.已出账）
    				logger.info("state===="+state);
    				if(state.equals("4")) {
    					remap.put("v_status", "0000");
    					remap.put("v_status_msg", "代付成功");
    				}else if(state.equals("3")){
    					remap.put("v_status", "1001");
    					remap.put("v_status_msg", "代付xx");
    				}
    				
    			}else{
    				logger.info("代付查询请求失败，"+remark);
    				remap.put("v_code", "15");
    				remap.put("v_msg", "请求失败");
    			}
            }else {
            	logger.info("代付查询无响应信息");
    			remap.put("v_code", "15");
    			remap.put("v_msg", "请求失败");
            }
               
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	/**
	 * 签名前的字符串
	 * @param map
	 * @param connector
	 * @return
	 */
	public static String joinMapValue(Map<String, Object> map, char connector)	{
		StringBuffer b = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()){
			b.append(entry.getKey());
			b.append('=');
			if (entry.getValue() != null){
				b.append(entry.getValue());
			}
			b.append(connector);
		}
		return b.toString().substring(0, b.length()-1);
	}
	

	
	//开联通代付
	@RequestMapping(value = "KLTDF")
	public Map<String, String> KLTDF(HttpServletResponse response, HttpServletRequest request,CapSingleTransferDTO capSingleTransferDTO) throws Exception {
		logger.info("#############开联通代付处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			net.sf.json.JSONObject json1 = new net.sf.json.JSONObject();
			//json1.put("version", "");
		    json1.put("merchantId", "903110153110001");//商户号
		    //json1.put("transactType", "");
		    json1.put("signType", "1");//签名类型，0：数字证书 1：md5

		    net.sf.json.JSONObject json2 = new net.sf.json.JSONObject();
			json2.put("mchtOrderNo", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单号
		    json2.put("orderDateTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单时间，yyyyMMddHHmmss
		    json2.put("accountNo", "6217991100013041275");//收款方账号
		    json2.put("accountName", "田永寒");//收款方姓名
		    json2.put("accountType", "1");//收款方账户类型1代表个人账户  2代表企业账户
		    json2.put("bankNo", "000000000000");//款方开户行行号（电子联行号）对公需要校验，对私填写000000000000（12个0）
		    json2.put("bankName", "邮储银行");//收款方开户行名称,个人账户只写银行名
		    json2.put("amt", "100");//金额，正整数，单位为分
		    json2.put("purpose", "shopping");//用途
		    json2.put("notifyUrl", BaseUtil.url+"/aa/KLTDFHD.action");//交易结果通知地址
		    
		    map.put("head", json1);
		    map.put("content", json2);
		    
		    JSONObject sendObject = JSONObject.fromObject(map);	
	        String originSign = sendObject.toString();
	        logger.info("参与签名的数据="+originSign);
	        String signStr = kltUtil.addSign(originSign,"742fa3ffd050fb441763bf8fb6c0594f");
	        logger.info("请求的参数="+signStr);
	        String url = "https://ipay.chinasmartpay.cn/openapi/singlePayment/payment";
	        //忽略SSL认证
	        String resp = kltUtil.sendHttpPostRequest(url, signStr, false);
	        logger.info("开联通代付响应信息="+resp);
	        
	        JSONObject reObject = JSONObject.fromObject(resp);
	        String responseCode=reObject.getString("responseCode");//响应码，000000表示接口响应正常，其它表示失败
	        String responseMsg=reObject.getString("responseMsg");//响应信息

	        if(!responseCode.equals("000000")) {
	        	logger.info(responseMsg);
				remap.put("v_code", "15");
				remap.put("v_msg", "请求失败，"+responseMsg);
				
				//补款
				
	        }
	        
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//开联通代付回调测试
	@RequestMapping(value = "KLTDFHD")
	public Map<String, String> KLTDFHD(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("开联通代付异步来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	String orderno=request.getParameter("merchantOrderId");//订单号
	    	String orderStatus=request.getParameter("orderStatus");//订单状态
	    	String errorCode=request.getParameter("errorCode");//交易错误码
	    	String errorMsg=request.getParameter("errorMsg");//交易错误信息
	    	
	    	logger.info("订单号="+orderno);
	    	logger.info("订单状态="+orderStatus);//订单状态,0：处理中，1：成功，2：失败
	    	logger.info("交易错误码="+errorCode);
	    	logger.info("交易错误信息="+errorMsg);
	    	
	    	if(errorCode!=null&&errorCode!=""&&errorCode.equals("000000")) {
	    		outString(response,"success");
	    	}else {
	    		outString(response,"fail");
	    	}
	    	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}

	

	//市民卡代付
	@RequestMapping(value = "SMKDF")
	public Map<String, String> SMKDF(HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("#############市民卡代付处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			map.put("tradeCode", "AP0001");
			map.put("version", "1.0.0");
			map.put("reqSeq", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//请求流水号
			map.put("merCode", "000081");//商户号 
			map.put("channelNo", "004");// 渠道号，默认004
			map.put("tradeDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));// 交易日期YYYY-MM-DD
			map.put("tradeTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));// 交易时间HH:MM:SS		
			map.put("orderNo", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 订单号，不超过32位
			map.put("busType", "TOPUB");// 业务类型，TOPUB：对公，TOPRV：对私
			map.put("toibkn", "");// 收款方联行号，对公业务必输
			map.put("actacn", "xxxxxxxxxxxxx");// 收款方账号
			map.put("toname", "杨单锋");// 收款方姓名
	//		map.put("toaddr", "");// 收款方地址
	//		map.put("tobknm", "");// 收款方开户行名称
			map.put("amount", "0.01");//交易金额
	        map.put("currency", "001");// 币种
			// 签名
	        String pfxPath=new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "/ky/000081.pfx";
	        String passWord = "hzsmk";// 商户私钥证书密码
			map.put("sign",smkUtil.sign(smkUtil.getSignMsg(map), pfxPath, passWord));
			
			String url = "http://115.236.162.166:28081/api/smkpay/";
			JSONObject jsonObject = JSONObject.fromObject(map);			
			String sendMessage = jsonObject.toString();
			logger.info("原始发送报文=" + sendMessage);
			// 转成base64
			String base64 = Base64.encodeBase64String(sendMessage.getBytes("UTF-8"));
			base64 = base64.replace("+", "#");
			logger.info("转成base64后的发送报文="+base64);
			String resp=smkUtil.sendMsgHTTP(base64, url, "");
			remap.put("v_code", "00");
			remap.put("v_msg", "请求成功");
			if(resp!=null&&resp!="") {
				JSONObject reObject = JSONObject.fromObject(resp);	
				String respCode=reObject.getString("respCode");//返回码
				String respDesc=reObject.getString("respDesc");//返回码描述
				
				/*if(respCode.equals("00")||respCode.equals("55")||respCode.equals("9999")||respCode.equals("9990")||respCode.equals("9992"))
				{//请求成功 或失败 或系统繁忙 或操作数据库失败，主动查询
                    logger.info(respDesc);
					
                  //主动查询
					
					
				}else {//请求错误
     				logger.info(respDesc);
					remap.put("v_code", "15");
					remap.put("v_msg", "请求失败，"+respDesc);
					
					//补款
						
				}*/
			}else {
				logger.info("市民卡代付请求无响应信息");
				
				//主动查询
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//市民卡代付查询
	public Map<String, String> SMKDFCX(String orderId,String merId) throws Exception {
		logger.info("#############市民卡代付查询处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("tradeCode", "AP0003");
		map.put("version", "1.0.1");
	
		return remap;
	}
	
    
	//九派代付
	@RequestMapping(value = "JPDF")
	public Map<String, String> JPDF(HttpServletResponse response, HttpServletRequest request,CapSingleTransferDTO capSingleTransferDTO) throws Exception {
		logger.info("#############九派代付处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			/*SingleTransferDTO dataMap = new SingleTransferDTO();
			//公共请求参数
//			dataMap.setCharset("02");// 字符集02：utf-8
//			dataMap.setVersion("1.0");// 版本号
			dataMap.setMerchantId("800000200020043");// 商户号
//			dataMap.setRequestTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//请求时间
//			dataMap.setRequestId(java.lang.String.valueOf(System.currentTimeMillis()));//请求编号，当日唯一
//			dataMap.setService("singleTransfer");//请求类型
//			dataMap.setSignType("RSA256");//签名类型
			//代付请求参数
			dataMap.setMcSequenceNo(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//商户交易流水
			dataMap.setMcTransDateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//商户交易时间
			dataMap.setOrderNo(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单号
			dataMap.setAmount("1");//交易金额,单位分
			dataMap.setCardNo("6217991100013041275");//交易账号
			dataMap.setAccName("田永寒");//账户户名			
			dataMap.setCrdType("00");//银行卡类型
			dataMap.setCallBackUrl(xdt.dto.transfer_accounts.util.PayUtil.jpNotifyUrl);//回调地址
			dataMap.setRemark1("{\"mercUsage\":\"货款结算\"}");//交易备注,json字符串,用途选项有:货款结算、服务费结算、劳务报酬结算
			//可空的
//			dataMap.setAccType("0");//账户类型
//			dataMap.setLBnkNo("123456");//收款人开户行行号
//			dataMap.setLBnkNam("邮政储蓄银行");//收款人开户行名称
//			dataMap.setValidPeriod("1212");//有效期
//			dataMap.setCvv2("123");//CVV2
//			dataMap.setCellPhone("15244166253");//手机号
//			dataMap.setRemark("备注");//订单备注
//			dataMap.setBnkRsv("附言");//银行附言
//			dataMap.setCapUse("用途");//资金用途
//			dataMap.setRemark2("元素2");//填充元素2
//			dataMap.setRemark3("元素3");//填充元素3			
			
			String merchantCert=new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "/ky/800000200020043.p12";//商户证书路径 .p12文件
		    String merchantPass="YyxuDa";//商户证书密码
		    String rootcerPath=new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "/ky/800000200020043.cer";//根证书路径  .cer文件
		    String url="https://jd.jiupaipay.com/paygateway/mpsGate/mpsTransaction";//请求地址
		    logger.info("加密的参数="+dataMap);
		    logger.info("私钥证书路径="+merchantCert);
		    logger.info("公钥证书路径="+rootcerPath);
		    logger.info("证书密码="+merchantPass);
			String resp= baseJiupayService.doSend(SINGLETRANSFER,dataMap,merchantCert,merchantPass,rootcerPath,url);
			logger.info("九派代付响应信息="+resp);*/

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	

	//畅捷通直接支付请求接口
	@RequestMapping(value = "CJKJ1")
	public Map<String, String> CJKJ1(HttpServletResponse response, HttpServletRequest request,CapSingleTransferDTO capSingleTransferDTO) throws Exception {
		logger.info("#############九派代付处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		try {
			Map<String, String> origMap = new HashMap<String, String>();
			//公共参数
			origMap.put("Service", "nmg_zft_api_quick_payment");// 支付接口名称
			origMap.put("Version", "1.0");
			origMap.put("PartnerId", "200000140001");//商户号			
			origMap.put("InputCharset", cjtUtil.charset);// 字符集
			origMap.put("TradeDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));// 商户请求时间
			origMap.put("TradeTime", new SimpleDateFormat("HHmmss").format(new Date()));// 商户请求时间
//			origMap.put("Sign", "签名");// 签名
//			origMap.put("SignType", "RSA");// 签名方式
			//业务参数
			origMap.put("TrxId", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 订单号
			origMap.put("OrdrName", "an apple");// 商品名称
			origMap.put("MerUserId", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 用户标识
			origMap.put("SellerId", "200001160097");// 商户编号
			origMap.put("ExpiredTime", "90m");// 订单有效期
			origMap.put("BkAcctTp", "01");// 卡类型（00 – 银行贷记卡;01 – 银行借记卡;）
			origMap.put("BkAcctNo", cjtUtil.encrypt("6217991100013041275", cjtUtil.MERCHANT_PUBLIC_KEY, cjtUtil.charset));// 卡号
			origMap.put("IDTp", "01");// 证件类型 （目前只支持身份证 01：身份证）
			origMap.put("IDNo", cjtUtil.encrypt("370323199612093224", cjtUtil.MERCHANT_PUBLIC_KEY, cjtUtil.charset));// 证件号
			origMap.put("CstmrNm", cjtUtil.encrypt("田永寒", cjtUtil.MERCHANT_PUBLIC_KEY, cjtUtil.charset));// 持卡人姓名
			origMap.put("MobNo", cjtUtil.encrypt("15244166253", cjtUtil.MERCHANT_PUBLIC_KEY, cjtUtil.charset));// 银行预留手机号
			origMap.put("TrxAmt", "0.1");// 交易金额,元
			origMap.put("TradeType", "11");// 交易类型
			origMap.put("NotifyUrl", BaseUtil.url+"/aa/CEDHD.action");//异步地址
			//origMap.put("SmsFlag", "1");//短信发送标识0：不发送短信1：发送短信
			logger.info("签名的参数="+origMap);
			String resp=cjtUtil.gatewayPost(origMap, cjtUtil.charset, cjtUtil.MERCHANT_PRIVATE_KEY);
			logger.info("返回的信息="+resp);
			JSONObject reObject = JSONObject.fromObject(resp);	
			String respCode=reObject.getString("AcceptStatus");//网关返回码.S接口调用受理成功.F接口调用受理失败
			String RetCode=reObject.getString("RetCode");//业务返回码
			String RetMsg=reObject.getString("RetMsg");//业务返回码描述
			String AppRetcode=reObject.getString("AppRetcode");//应用返回码
			String AppRetMsg=reObject.getString("AppRetMsg");//应用返回码描述
			String Status=reObject.getString("Status");//交易状态
			logger.info("业务返回码描述="+RetMsg);
			logger.info("应用返回码描述="+AppRetMsg);
			logger.info("交易状态="+Status);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//短信验证码重发接口
	@RequestMapping(value = "CJfdx")
	public Map<String, String> CJfdx1(HttpServletResponse response, HttpServletRequest request,CapSingleTransferDTO capSingleTransferDTO) throws Exception {
		logger.info("#############短信验证码重发处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			Map<String, String> origMap = new HashMap<String, String>();
			// 2.1 基本参数
			//origMap = setCommonMap(origMap);
			// 2.2 业务参数
			origMap.put("Service", "nmg_api_quick_payment_resend");
			origMap.put("TrxId", "2017030915102022");// 订单号
			origMap.put("OriTrxId", "20170309131120");// 原业务请求订单号
			origMap.put("TradeType", "auth_order");// 原业务订单类型
			cjtUtil.gatewayPost(origMap, cjtUtil.charset, cjtUtil.MERCHANT_PRIVATE_KEY);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//支付确认接口
	@RequestMapping(value = "CJzfqr")
	public Map<String, String> CJfdx(HttpServletResponse response, HttpServletRequest request,CapSingleTransferDTO capSingleTransferDTO) throws Exception {
		logger.info("#############支付确认接口处理 开始#############");
		Map<String, String> remap = new LinkedHashMap<String, String>();
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			Map<String, String> origMap = new HashMap<String, String>();
			// 2.1 基本参数
//			origMap = setCommonMap(origMap);
//			origMap.put("Service", "nmg_api_quick_payment_smsconfirm");// 请求的接口名称
			// 2.2 业务参数
			String trxId = Long.toString(System.currentTimeMillis());		
			origMap.put("TrxId", trxId);// 订单号

			//origMap.put("TrxId", "101149785980144593760");// 订单号
			origMap.put("OriPayTrxId", "1501123506844");// 原有支付请求订单号
			origMap.put("SmsCode", "695535");// 短信验证码
			cjtUtil.gatewayPost(origMap, cjtUtil.charset, cjtUtil.MERCHANT_PRIVATE_KEY);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
	
	//回调测试
	@RequestMapping(value = "CEDHD")
	public Map<String, String> CEDHD(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    log.info("测试异步来了！");	
	    Map<String, String> remap=new HashMap<>();  
	    try {
	    	String orderno=request.getParameter("inner_trade_no");//订单号
	    	String orderStatus=request.getParameter("trade_status");//订单状态
	    	String errorCode=request.getParameter("trade_amount");//
	    	
	    	logger.info("订单号="+orderno);
	    	logger.info("订单状态="+orderStatus);//订单状态,0：处理中，1：成功，2：失败
	    	logger.info("金额="+errorCode);
	    	
	    	if(errorCode!=null&&errorCode!=""&&errorCode.equals("000000")) {
	    		outString(response,"success");
	    	}else {
	    		outString(response,"fail");
	    	}
	    	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return remap;
	}
}