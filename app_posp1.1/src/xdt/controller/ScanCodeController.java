package xdt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.dto.gateway.entity.GateWayQueryRequestEntity;
import xdt.dto.hj.HJResponse;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.pay.PayRequest;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.dto.scanCode.entity.ResponseMode;
import xdt.dto.scanCode.entity.ScanCodeRequestEntity;
import xdt.dto.scanCode.entity.ScanCodeResponseEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IHJService;
import xdt.service.IScanCodeService;
import xdt.service.ITotalPayService;
import xdt.service.JsdsQrCodeService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月20日 上午9:28:24 
* 类说明 
*/
@Controller
@RequestMapping("/ScanCodeController")
public class ScanCodeController extends BaseAction{

	private Logger log = Logger.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private IScanCodeService service;
	@Resource
	private HfQuickPayService payService;
	@Resource
	private ITotalPayService totalPayService;
	
	@Resource
	private IHJService ihjService;
	@Resource
	private JsdsQrCodeService JsdsService;
	/**
	 * 扫码签名方法
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "paySign")
	public void paySign(ScanCodeRequestEntity payRequest, HttpServletResponse response) {

		log.info("--签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payRequest.getV_mid());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		log.info("签名前数据**********支付:" + beanToMap(payRequest));
		// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		String sign = SignatureUtil.getSign(beanToMap(payRequest), key, log);
		log.info("签名**********支付:" + sign);
		try {
			outString(response, sign);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="scanCode")
	public void scanCode(ScanCodeRequestEntity payRequest, HttpServletResponse response,HttpServletRequest request) {
		log.info("--扫码发来的参数：" + JSON.toJSONString(payRequest));
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(payRequest.getV_mid())&&!StringUtils.isEmpty(payRequest.getV_cardType())) {

			    //检验数据是否合法
				log.info("下游上送签名串{}" + payRequest.getV_sign());
				// 查询商户密钥
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(payRequest.getV_mid());
				// ------------------------需要改签名
				String merchantKey = keyinfo.getMerchantkey();
				SignatureUtil signUtil = new SignatureUtil();

				Map map = BeanToMapUtil.convertBean(payRequest);
				if (signUtil.checkSign(map, merchantKey, log)) {
					
					log.info("对比签名成功");
					result = service.scanCode(payRequest, result);
					ScanCodeResponseEntity codeResponseEntity = (ScanCodeResponseEntity) BeanToMapUtil
							.convertMap(ScanCodeResponseEntity.class, result);
					log.info("---返回数据签名签的数据:" + beanToMap(codeResponseEntity));
					String sign = SignatureUtil.getSign(beanToMap(codeResponseEntity), merchantKey, log);
					log.info("---返回数据签名:" + sign);
					result.put("v_sign", sign);

				} else {
					log.error("签名错误!");
					result.put("v_code", "02");
					result.put("v_msg", "Signature error!");
					log.info("返回的参数:" + JSON.toJSON(result));
				}

		} else {
			log.error("商户号 为 null!");
			result.put("v_code", "01");
			result.put("v_msg", "v_mid is null");
		}
		try {
			if("0000".equals(result.get("v_code"))) {
				outString(response, result.get("v_result"));
			}else {
				
				outString(response, JSON.toJSONString(result));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");
		
	}
	
	@RequestMapping(value="getScanCodeQuick")
	public void getScanCodeQuick(GateWayQueryRequestEntity query,HttpServletResponse response) {
		log.info("扫码查询来了，参数:"+JSON.toJSONString(query));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(query.getV_mid())&&!StringUtils.isEmpty(query.getV_oid())) {

			    //检验数据是否合法
				log.info("下游上送签名串{}" + query.getV_sign());
				// 查询商户密钥
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(query.getV_mid());
				// ------------------------需要改签名
				String merchantKey = keyinfo.getMerchantkey();
				SignatureUtil signUtil = new SignatureUtil();

				Map map = BeanToMapUtil.convertBean(query);
				if (signUtil.checkSign(map, merchantKey, log)) {
					log.info("对比签名成功");
					
					result = service.getScanCodeQuick(query);
					ScanCodeResponseEntity codeResponseEntity = (ScanCodeResponseEntity) BeanToMapUtil
							.convertMap(ScanCodeResponseEntity.class, result);
					log.info("---返回数据签名签的数据:" + beanToMap(codeResponseEntity));
					String sign = SignatureUtil.getSign(beanToMap(codeResponseEntity), merchantKey, log);
					log.info("---返回数据签名:" + sign);
					result.put("v_sign", sign);

				} else {
					log.error("签名错误!");
					result.put("v_code", "02");
					result.put("v_msg", "Signature error!");
					log.info("返回的参数:" + JSON.toJSON(result));
				}

		} else {
			log.error("商户号 为 null!");
			result.put("v_code", "01");
			result.put("v_msg", "v_mid or v_oid is null");
		}
		
	}
	
	
	/**
	 * 漪雷扫码异步
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="YLNotifyUrl")
	public void YLNotifyUrl( HttpServletResponse response,HttpServletRequest request) {
		Map<String, String> maps=new HashMap<>();
		
		BufferedReader br;
		String code="";
		String info= "";
		String p3_uno= "";
		String p3_orderno= "";
		String p3_money= "";
		String p3_type=  "";
		String p3_note= "";
		String p3_sysno= "";
		String sign= "";
		String key ="";
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			logger.info("扫码异步来了：" + appMsg);
			net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(appMsg);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equals("code")) {
					code = ob.getString(key);
					System.out.println(code);
				}
				if (key.equals("info")) {
					info = ob.getString(key);
					System.out.println(info);
				}
				if (key.equals("p3_uno")) {
					p3_uno = ob.getString(key);
					System.out.println(p3_uno);
				}
				if (key.equals("p3_orderno")) {
					p3_orderno = ob.getString(key);
					System.out.println(p3_orderno);
				}
				if (key.equals("p3_money")) {
					p3_money = ob.getString(key);
					System.out.println(p3_money);
				}
				if (key.equals("p3_type")) {
					p3_type = ob.getString(key);
					System.out.println(p3_type);
				}
				if (key.equals("p3_note")) {
					p3_note = ob.getString(key);
					System.out.println(p3_note);
				}
				if (key.equals("p3_sysno")) {
					p3_sysno = ob.getString(key);
					System.out.println(p3_sysno);
				}
				if (key.equals("sign")) {
					sign = ob.getString(key);
					System.out.println(sign);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		/*TreeMap<String, String> map =new TreeMap<>();*/
		if(code!=""&&code!=null&&p3_orderno!=null&&p3_orderno!=""&&p3_uno!=""&&p3_uno!=null&&sign!=""&&sign!=null) {
			try {
				outString(response, "OK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*map.put("code", code);
			map.put("info", info);
			map.put("p3_uno", p3_uno);
			map.put("p3_orderno", p3_orderno);
			map.put("p3_money", p3_money);
			map.put("p3_type", p3_type);
			map.put("p3_note", p3_note);
			map.put("p3_sysno", p3_sysno);*/
			
			//String paramSrc = RequestUtils.getParamSrc(map);
			//log.info("签名前数据**********支付:" + paramSrc);
			//PmsBusinessPos businessPos =totalPayService.selectMer(p3_uno);
			//String md5 = MD5Utils.sign(paramSrc, businessPos.getKek(), "UTF-8").toUpperCase();
			//if(md5.equals(sign)) {
				//log.info("异步验签成功！");
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				OriginalOrderInfo originalInfo=null;
				try {
					originalInfo  = this.payService.getOriginOrderInfo(p3_orderno);
					keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				maps.put("v_mid", originalInfo.getPid());
				maps.put("v_oid", originalInfo.getOrderId());
				maps.put("v_txnAmt", originalInfo.getOrderAmount());
				maps.put("v_attach", originalInfo.getAttach());
				maps.put("v_code", "00");
				maps.put("v_msg", "成功");
				if("0".equals(code)) {
					maps.put("v_status", "0000");
					maps.put("v_status_msg", "支付成功");
					
				}else {
					maps.put("v_status", "1001");
					maps.put("v_status_msg", info);
				}
				ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
						.convertMap(ScanCodeResponseEntity.class, maps);
				try {
					service.otherInvoke(consume);
				} catch (Exception e1) {
					log.info("修改状态失败");
					e1.printStackTrace();
				}
				String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				maps.put("v_sign", signs);
				String params = HttpURLConection.parseParams(maps);
				log.info("给下游同步的数据:" + params);
				String html="";
				try {
					html = HttpClientUtil.post(originalInfo.getBgUrl(),params);
				}  catch (Exception e) {
					
					e.printStackTrace();
				}
			    logger.info("下游返回状态" + html);
			    net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> result = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						result.put("success", value);
					}
				}
				if (result.get("success").equals("false")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
				}
				logger.info("向下游 发送数据成功");
			//}else {
			//	log.info("异步验签失败！");
			//}
		}else {
			try {
				outString(response, "FAIL");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 漪雷扫码同步
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="YLReturnUrl")
	public void YLReturnUrl(HttpServletResponse response,HttpServletRequest request) {
		log.info("扫码同步来了");
		Map<String, String> maps=new HashMap<>();
		/*TreeMap<String, String> map =new TreeMap<>();*/
		BufferedReader br;
		String code="";
		String info= "";
		String p3_uno= "";
		String p3_orderno= "";
		String p3_money= "";
		String p3_type=  "";
		String p3_note= "";
		String p3_sysno= "";
		String sign= "";
		String key ="";
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			logger.info("扫码异步来了：" + appMsg);
			net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(appMsg);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equals("code")) {
					code = ob.getString(key);
					System.out.println(code);
				}
				if (key.equals("info")) {
					info = ob.getString(key);
					System.out.println(info);
				}
				if (key.equals("p3_uno")) {
					p3_uno = ob.getString(key);
					System.out.println(p3_uno);
				}
				if (key.equals("p3_orderno")) {
					p3_orderno = ob.getString(key);
					System.out.println(p3_orderno);
				}
				if (key.equals("p3_money")) {
					p3_money = ob.getString(key);
					System.out.println(p3_money);
				}
				if (key.equals("p3_type")) {
					p3_type = ob.getString(key);
					System.out.println(p3_type);
				}
				if (key.equals("p3_note")) {
					p3_note = ob.getString(key);
					System.out.println(p3_note);
				}
				if (key.equals("p3_sysno")) {
					p3_sysno = ob.getString(key);
					System.out.println(p3_sysno);
				}
				if (key.equals("sign")) {
					sign = ob.getString(key);
					System.out.println(sign);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(code!=""&&code!=null&&p3_orderno!=null&&p3_orderno!=""&&p3_uno!=""&&p3_uno!=null&&sign!=""&&sign!=null) {
			try {
				outString(response, "OK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*map.put("code", code);
			map.put("info", info);
			map.put("p3_uno", p3_uno);
			map.put("p3_orderno", p3_orderno);
			map.put("p3_money", p3_money);
			map.put("p3_type", p3_type);
			map.put("p3_note", p3_note);
			map.put("p3_sysno", p3_sysno);*/
			
			//String paramSrc = RequestUtils.getParamSrc(map);
			//log.info("签名前数据**********支付:" + paramSrc);
			//PmsBusinessPos businessPos =totalPayService.selectMer(p3_uno);
			
			//String md5 = MD5Utils.sign(paramSrc, businessPos.getKek(), "UTF-8").toUpperCase();
			//if(md5.equals(sign)) {
				//log.info("异步验签成功！");
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				OriginalOrderInfo originalInfo=null;
				try {
					originalInfo  = this.payService.getOriginOrderInfo(p3_orderno);
					keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				maps.put("v_mid", originalInfo.getPid());
				maps.put("v_oid", originalInfo.getOrderId());
				maps.put("v_txnAmt", originalInfo.getOrderAmount());
				maps.put("v_attach", originalInfo.getAttach());
				maps.put("v_code", "00");
				if("0".equals(code)) {
					maps.put("v_status", "0000");
					maps.put("v_msg", "支付成功");
				}else {
					maps.put("v_status", "1001");
					maps.put("v_msg", info);
				}
				ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
						.convertMap(ScanCodeResponseEntity.class, maps);
				String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				maps.put("v_sign", signs);
				String params = HttpURLConection.parseParams(maps);
				log.info("给下游同步的数据:" + params);
				try {
					response.sendRedirect(originalInfo.getPageUrl()+"?"+params);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("向下游 发送数据成功");
			//}else {
			//	log.info("异步验签失败！");
			//}
		}else {
			try {
				outString(response, "FAIL");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 九派扫码异步
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="jpNotifyUrl")
	public void jpNotifyUrl(HttpServletRequest request,HttpServletResponse response) {
		log.info("九派扫码异步参数！");
		String orderId=request.getParameter("orderId");
		String charset=request.getParameter("charset");
		String version=request.getParameter("version");
		String merchanId=request.getParameter("merchanId");
		String payType=request.getParameter("payType");
		String signType=request.getParameter("signType");
		String serverCert=request.getParameter("serverCert");
		String serverSign=request.getParameter("serverSign");
		String memberId=request.getParameter("memberId");
		String amount=request.getParameter("amount");
		String orderTime=request.getParameter("orderTime");
		String orderSts=request.getParameter("orderSts");
		String bankAbbr=request.getParameter("bankAbbr");
		String payTime=request.getParameter("payTime");
		String acDate=request.getParameter("acDate");
		String fee=request.getParameter("fee");
		log.info("九派异步返回参数：orderId="+orderId+",charset="+charset+",version="+version+",merchanId="+merchanId
				+",payType="+payType+",signType="+signType+",serverCert="+serverCert+",serverSign="+serverSign
				+",memberId="+memberId+",amount="+amount+",orderTime="+orderTime+",orderSts="+orderSts+",bankAbbr="+bankAbbr
				+",payTime="+payTime+",acDate="+acDate+",fee="+fee);
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps =new HashMap<>();
		if(orderId !=null &&orderId !="") {
			maps.put("result", "SUCCESS");
			ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
			OriginalOrderInfo originalInfo=null;
			try {
				originalInfo  = this.payService.getOriginOrderInfo(orderId);
			} catch (Exception e) {
				log.info("九派扫码查询原始订单信息返回异常");
				e.printStackTrace();
			}
			keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			
			log.info("下游的异步地址" + originalInfo.getBgUrl());
			map.put("v_mid", originalInfo.getPid());
			map.put("v_oid", originalInfo.getOrderId());
			map.put("v_txnAmt", originalInfo.getOrderAmount());
			map.put("v_attach", originalInfo.getAttach());
			map.put("v_code", "00");
			map.put("v_msg", "成功");
			if("PD".equals(orderSts)) {
				map.put("v_status", "0000");
				map.put("v_status_msg", "支付成功");
				
			}else {
				map.put("v_status", "1001");
				map.put("v_status_msg", "支付失败");
			}
			ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
					.convertMap(ScanCodeResponseEntity.class, map);
			try {
				service.otherInvoke(consume);
			} catch (Exception e1) {
				log.info("修改状态失败");
				e1.printStackTrace();
			}
			String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
			map.put("v_sign", signs);
			String params = HttpURLConection.parseParams(map);
			log.info("给下游同步的数据:" + params);
			String html="";
			try {
				html = HttpClientUtil.post(originalInfo.getBgUrl(),params);
			}  catch (Exception e) {
				
				e.printStackTrace();
			}
		    logger.info("下游返回状态" + html);
		    net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(html);
			Iterator it = ob.keys();
			Map<String, String> result = new HashMap<>();
			while (it.hasNext()) {
				String keys = (String) it.next();
				if (keys.equals("success")) {
					String value = ob.getString(keys);
					logger.info("异步回馈的结果:" + "\t" + value);
					result.put("success", value);
				}
			}
			if (result.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
			}
			logger.info("向下游 发送数据成功");
			
		}else {
			maps.put("result", "FAILED");
		}
		
			try {
				outString(response, map);
			} catch (IOException e) {
				log.info("九派扫码返回信息异常");
				e.printStackTrace();
			}
	}
	/**
	 * 汇聚扫码异步通知
	 * @param hjResponse
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="hjNotifyUrl")
	public void hjNotifyUrl(HJResponse hjResponse,HttpServletResponse response,HttpServletRequest request) {
		log.info("汇聚扫码异步来了！"+JSON.toJSONString(hjResponse));
		Map<String, String> map =new HashMap<>();
		String str;
		OriginalOrderInfo originalInfo = null;
		ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
		if(!"".equals(hjResponse.getR2_OrderNo())&&hjResponse.getR2_OrderNo()!=null) {
			str = "success";
			try {
				outString(response, str);
			} catch (IOException e2) {
				log.info("汇聚扫码SUCCESS返回异常");
				e2.printStackTrace();
			}
			
			try {
				originalInfo = this.payService.getOriginOrderInfo(hjResponse.getR2_OrderNo());
			} catch (Exception e2) {
				log.info("汇聚扫码查询原始订单信息返回异常");
				e2.printStackTrace();
			}
			keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());			
			map.put("v_mid", originalInfo.getPid());
			map.put("v_oid", originalInfo.getOrderId());
			map.put("v_txnAmt", originalInfo.getOrderAmount());
			map.put("v_attach", originalInfo.getAttach());
			map.put("v_code", "00");
			map.put("v_msg", "成功");
			if("100".equals(hjResponse.getR6_Status())) {
				map.put("v_status", "0000");
				map.put("v_status_msg", "支付成功");
				if(!"10052270614".equals(originalInfo.getPid())) {
					int ii=0;
					try {
						ii = service.UpdatePmsMerchantInfo(originalInfo);
					} catch (Exception e) {
						log.info("汇聚入金异常！");
						e.printStackTrace();
					}
					System.out.println(ii);
				}
			}else {
				map.put("v_status", "1001");
				map.put("v_status_msg", "支付失败！");
			}
			ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
					.convertMap(ScanCodeResponseEntity.class, map);
			try {
				service.otherInvoke(consume);
			} catch (Exception e1) {
				log.info("修改状态失败");
				e1.printStackTrace();
			}
			String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
			map.put("v_sign", signs);
			String params = HttpURLConection.parseParams(map);
			log.info("九派扫码给下游异步的数据:" + params);
			String html="";
			try {
				html = HttpClientUtil.post(originalInfo.getBgUrl(),params);
			}  catch (Exception e) {
				
				e.printStackTrace();
			}
		    logger.info("下游返回状态" + html);
		    net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(html);
			Iterator it = ob.keys();
			Map<String, String> result = new HashMap<>();
			while (it.hasNext()) {
				String keys = (String) it.next();
				if (keys.equals("success")) {
					String value = ob.getString(keys);
					logger.info("异步回馈的结果:" + "\t" + value);
					result.put("success", value);
				}
			}
			if (result.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
			}
			logger.info("向下游 发送数据成功");
		}else {
			str = "FAIL";
			try {
				outString(response, str);
			} catch (IOException e) {
				log.info("汇聚扫码FAIL返回异常");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 江苏电商扫码异步
	 * @param temp
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="jsdsNotifyUrl")
	public void jsdsNotifyUrl(JsdsResponseDto temp,HttpServletResponse response,HttpServletRequest request) {
		log.info("江苏电商扫码异步返回参数！"+JSON.toJSONString(temp));
		Map<String, String> map =new HashMap<>();
		String str;
		OriginalOrderInfo originalInfo = null;
		ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
		Map<String, String> param =new HashMap<>();
		if(temp!=null) {
			str="SUCCESS";
			try {
				outString(response, str);
				 param = service.handleNofity(temp);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			try {
				originalInfo = this.payService.getOriginOrderInfo(param.get("orderNum"));
			} catch (Exception e2) {
				log.info("江苏电商扫码查询原始订单信息返回异常");
				e2.printStackTrace();
			}
			keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());			
			map.put("v_mid", originalInfo.getPid());
			map.put("v_oid", originalInfo.getOrderId());
			map.put("v_txnAmt", originalInfo.getOrderAmount());
			map.put("v_attach", originalInfo.getAttach());
			map.put("v_code", "00");
			map.put("v_msg", "成功");
			if("4".equals(param.get("pl_payState").toString())) {
				map.put("v_status", "0000");
				map.put("v_status_msg", "支付成功");
				Calendar cal1 = Calendar.getInstance();
				TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
				java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
				try {
					if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("03:00:00").getTime()
							&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
						
						int i=service.UpdatePmsMerchantInfo(originalInfo);
						if(i==1) {
							log.info("江苏电商扫码入金成功");
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if("5".equals(param.get("pl_payState").toString())||"3".equals(param.get("pl_payState").toString())||"5".equals(param.get("pl_payState").toString())) {
				map.put("v_status", "1001");
				map.put("v_status_msg", "支付失败！");
			}
			ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
					.convertMap(ScanCodeResponseEntity.class, map);
			try {
				service.otherInvoke(consume);
			} catch (Exception e1) {
				log.info("修改状态失败");
				e1.printStackTrace();
			}
			String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
			map.put("v_sign", signs);
			String params = HttpURLConection.parseParams(map);
			log.info("江苏电商扫码给下游异步的数据:" + params);
			String html="";
			try {
				html = HttpClientUtil.post(originalInfo.getBgUrl(),params);
			}  catch (Exception e) {
				
				e.printStackTrace();
			}
		    logger.info("下游返回状态" + html);
		    net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(html);
			Iterator it = ob.keys();
			Map<String, String> result = new HashMap<>();
			while (it.hasNext()) {
				String keys = (String) it.next();
				if (keys.equals("success")) {
					String value = ob.getString(keys);
					logger.info("异步回馈的结果:" + "\t" + value);
					result.put("success", value);
				}
			}
			if (result.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
			}
			logger.info("向下游 发送数据成功");
		}else {
			str="FAIL";
		}
		try {
			outPrint(response, str);
		} catch (IOException e) {
			log.info("江苏电商扫码返回异常");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 银生宝扫码异步
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="ysbNotifyUrl")
	public void ysbNotifyUrl(HttpServletRequest request,HttpServletResponse response) {
		log.info("银生宝扫码异步参数！");
		
		BufferedReader br;
		String orderId="";
		String result_code="";
		String result_msg="";
		String amount="";
		String mac="";
		String accountId="";
		String key="";
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			logger.info("扫码异步来了：" + appMsg);
			net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(appMsg);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equals("orderId")) {
					orderId = ob.getString(key);
					System.out.println(orderId);
				}
				if (key.equals("result_code")) {
					result_code = ob.getString(key);
					System.out.println(result_code);
				}
				if (key.equals("result_msg")) {
					result_msg = ob.getString(key);
					System.out.println(result_msg);
				}
				if (key.equals("amount")) {
					amount = ob.getString(key);
					System.out.println(amount);
				}
				if (key.equals("accountId")) {
					accountId = ob.getString(key);
					System.out.println(accountId);
				}
				if (key.equals("mac")) {
					mac = ob.getString(key);
					System.out.println(mac);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		log.info("银生宝异步返回参数：orderId="+orderId+",result_code="+result_code+",result_msg="+result_msg+",amount="+amount
				+",mac="+mac+",accountId="+accountId);
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps =new HashMap<>();
		if(orderId !=null &&orderId !="") {
			maps.put("result", "SUCCESS");
			ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
			OriginalOrderInfo originalInfo=null;
			try {
				originalInfo  = this.payService.getOriginOrderInfo(orderId);
			} catch (Exception e) {
				log.info("银生宝扫码查询原始订单信息返回异常");
				e.printStackTrace();
			}
			keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			
			log.info("下游的异步地址" + originalInfo.getBgUrl());
			map.put("v_mid", originalInfo.getPid());
			map.put("v_oid", originalInfo.getOrderId());
			map.put("v_txnAmt", originalInfo.getOrderAmount());
			map.put("v_attach", originalInfo.getAttach());
			map.put("v_code", "00");
			map.put("v_msg", "成功");
			if("0000".equals(result_code)) {
				map.put("v_status", "0000");
				map.put("v_status_msg", "支付成功");
				
			}else {
				map.put("v_status", "1001");
				map.put("v_status_msg", "支付失败");
			}
			ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
					.convertMap(ScanCodeResponseEntity.class, map);
			try {
				service.otherInvoke(consume);
			} catch (Exception e1) {
				log.info("银生宝修改状态失败");
				e1.printStackTrace();
			}
			String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
			map.put("v_sign", signs);
			String params = HttpURLConection.parseParams(map);
			log.info("银生宝给下游同步的数据:" + params);
			String html="";
			try {
				html = HttpClientUtil.post(originalInfo.getBgUrl(),params);
			}  catch (Exception e) {
				
				e.printStackTrace();
			}
		    logger.info("下游返回状态" + html);
		    net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(html);
			Iterator it = ob.keys();
			Map<String, String> result = new HashMap<>();
			while (it.hasNext()) {
				String keys = (String) it.next();
				if (keys.equals("success")) {
					String value = ob.getString(keys);
					logger.info("银生宝异步回馈的结果:" + "\t" + value);
					result.put("success", value);
				}
			}
			if (result.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
			}
			logger.info("银生宝向下游 发送数据成功");
			
		}else {
			maps.put("result", "FALL");
		}
		
			try {
				outString(response, map);
			} catch (IOException e) {
				log.info("九派扫码返回信息异常");
				e.printStackTrace();
			}
	}
	
	
	/**
	 * OEM接的汇聚银联扫码异步
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="jhjNotifyUrl")
	public void jhjNotifyUrl(HttpServletRequest request,HttpServletResponse response) {
		log.info("OEM接的汇聚银联扫码异步参数！");
		
		BufferedReader br;
		String returnCode="";
		String resultCode="";
		String outChannelNo="";
		String status="";
		String mchId="";
		String channel="";
		String baby="";
		String outTradeNo="";
		String amount="";
		String transTime="";
		String sign="";
		String key="";
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			logger.info("扫码异步来了：" + appMsg);
			net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(appMsg);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equals("returnCode")) {
					returnCode = ob.getString(key);
					System.out.println(returnCode);
				}
				if (key.equals("resultCode")) {
					resultCode = ob.getString(key);
					System.out.println(resultCode);
				}
				if (key.equals("outChannelNo")) {
					outChannelNo = ob.getString(key);
					System.out.println(outChannelNo);
				}
				if (key.equals("status")) {
					status = ob.getString(key);
					System.out.println(status);
				}
				if (key.equals("mchId")) {
					mchId = ob.getString(key);
					System.out.println(mchId);
				}
				if (key.equals("channel")) {
					channel = ob.getString(key);
					System.out.println(channel);
				}
				if (key.equals("baby")) {
					baby = ob.getString(key);
					System.out.println(baby);
				}
				if (key.equals("outTradeNo")) {
					outTradeNo = ob.getString(key);
					System.out.println(outTradeNo);
				}
				if (key.equals("amount")) {
					amount = ob.getString(key);
					System.out.println(amount);
				}
				if (key.equals("transTime")) {
					transTime = ob.getString(key);
					System.out.println(transTime);
				}
				if (key.equals("sign")) {
					sign = ob.getString(key);
					System.out.println(sign);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		log.info("OEM接的汇聚银联扫码异步返回参数：outTradeNo="+outTradeNo+",returnCode="+returnCode+",resultCode="+resultCode+",status="+status
				+",outChannelNo="+outChannelNo+",outTradeNo="+outTradeNo);
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps =new HashMap<>();
		if(outTradeNo !=null &&outTradeNo !="") {
			try {
				outString(response, "SUCCESS");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
			OriginalOrderInfo originalInfo=null;
			try {
				originalInfo  = this.payService.getOriginOrderInfo(outTradeNo);
			} catch (Exception e) {
				log.info("OEM接的汇聚银联扫码查询原始订单信息返回异常");
				e.printStackTrace();
			}
			keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			
			log.info("下游的异步地址" + originalInfo.getBgUrl());
			map.put("v_mid", originalInfo.getPid());
			map.put("v_oid", originalInfo.getOrderId());
			map.put("v_txnAmt", originalInfo.getOrderAmount());
			map.put("v_attach", originalInfo.getAttach());
			map.put("v_code", "00");
			map.put("v_msg", "成功");
			if("0".equals(returnCode)) {
				if("0".equals(resultCode)) {
					if("02".equals(status)) {
						map.put("v_status", "0000");
						map.put("v_status_msg", "支付成功");
						int ii=0;
						try {
							ii = service.UpdatePmsMerchantInfo1(originalInfo);
						} catch (Exception e) {
							log.info("汇聚入金异常！");
							e.printStackTrace();
						}
						System.out.println(ii);
					}else if("03".equals(status)){
						
					}else {
						map.put("v_status", "1001");
						map.put("v_status_msg", "支付失败");
					}
					
				}else {
					map.put("v_status", "1001");
					map.put("v_status_msg", "支付失败");
				}
			}else {
				map.put("v_status", "1001");
				map.put("v_status_msg", "支付失败");
			}
			ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
					.convertMap(ScanCodeResponseEntity.class, map);
			try {
				service.otherInvoke(consume);
			} catch (Exception e1) {
				log.info("OEM接的汇聚银联扫码修改状态失败");
				e1.printStackTrace();
			}
			String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
			map.put("v_sign", signs);
			String params = HttpURLConection.parseParams(map);
			log.info("OEM接的汇聚银联扫码给下游同步的数据:" + params);
			String html="";
			try {
				html = HttpClientUtil.post(originalInfo.getBgUrl(),params);
			}  catch (Exception e) {
				
				e.printStackTrace();
			}
		    logger.info("OEM接的汇聚银联扫码下游返回状态" + html);
		    net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(html);
			Iterator it = ob.keys();
			Map<String, String> result = new HashMap<>();
			while (it.hasNext()) {
				String keys = (String) it.next();
				if (keys.equals("success")) {
					String value = ob.getString(keys);
					logger.info("OEM接的汇聚银联扫码异步回馈的结果:" + "\t" + value);
					result.put("success", value);
				}
			}
			if (result.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
			}
			logger.info("OEM接的汇聚银联扫码向下游 发送数据成功");
			
		}else {
			try {
				outString(response, "FAIL");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
	}
	
}
