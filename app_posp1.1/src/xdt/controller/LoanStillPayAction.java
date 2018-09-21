package xdt.controller;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;
import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.jbb.util.Base64;
import xdt.quickpay.loanStill.entity.CallbackEntity;
import xdt.quickpay.loanStill.entity.PayRequestEntity;
import xdt.quickpay.loanStill.entity.PayResponseEntity;
import xdt.quickpay.loanStill.entity.RegisterRequestEntity;
import xdt.quickpay.loanStill.entity.RegisterResponseEntity;
import xdt.quickpay.loanStill.util.HttpClientUtil;
import xdt.quickpay.loanStill.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.ILoanStillPayService;
import xdt.service.OriginalOrderInfoService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.UtilDate;

@Controller
@RequestMapping("loanStill")
public class LoanStillPayAction extends BaseAction {
	
	private Logger logger = Logger.getLogger(LoanStillPayAction.class);
	
	@Resource
	private ILoanStillPayService loanStillPayService;
	@Resource
	private OriginalOrderInfoService Origi; // 原始信息Service
	
	/**
	 * 代还注册签名接口
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody	
	@RequestMapping(value = "register/ls/sign")
	  public void registerSign(RegisterRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=utf-8");
	    logger.info("下游上送的参数:" + entity);
	    
	    ChannleMerchantConfigKey keyinfo =loanStillPayService.getChannelConfigKey(entity.getV_mid());
	    String merchantKey = keyinfo.getMerchantkey();
	    logger.info("下游商户密钥:" + keyinfo);
	    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
	    entity.setV_sign(sign);
	    
	    request.setCharacterEncoding("UTF-8");
	    request.setAttribute("temp", entity);
	    request.getRequestDispatcher("/quick/loanStill/loanStill_register_submit.jsp").forward(request, response);
	  }
	/**
	 *代还注册接口
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "register/ls/submit")
	public void registerSubmit(RegisterRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();

		logger.info("下游上送参数:{}"+ param);
		if (!StringUtils.isEmpty(param.getV_mid())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = loanStillPayService.getChannelConfigKey(param.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey)) {
				logger.info("对比签名成功");
				result = loanStillPayService.lsRegister(param);
				logger.info("短信响应信息:" + result);
				RegisterResponseEntity registerResponse = (RegisterResponseEntity) BeanToMapUtil.convertMap(RegisterResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(registerResponse), merchantKey);
				result.put("v_sign", sign);
				outString(response, gson.toJson(result));
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, gson.toJson(result));
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, gson.toJson(result));
		}

	}
	/**
	 * 代还绑卡签名接口
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody	
	@RequestMapping(value = "tieCard/ls/sign")
	  public void tieCardScan(PayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=utf-8");
	    logger.info("下游上送的参数:" + entity);
	    
	    ChannleMerchantConfigKey keyinfo =loanStillPayService.getChannelConfigKey(entity.getV_mid());
	    String merchantKey = keyinfo.getMerchantkey();
	    logger.info("下游商户密钥:" + keyinfo);
	    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
	    entity.setV_sign(sign);
	    
	    request.setCharacterEncoding("UTF-8");
	    request.setAttribute("temp", entity);
	    request.getRequestDispatcher("/quick/loanStill/loanStill_tieCard_submit.jsp").forward(request, response);
	  }
	/**
	 * 代还绑卡请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "tieCard/ls/submit")
	public void tieCardSubmit(PayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();

		logger.info("下游上送参数:{}"+ param);
		if (!StringUtils.isEmpty(param.getV_mid())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = loanStillPayService.getChannelConfigKey(param.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey)) {
				logger.info("对比签名成功");
				result = loanStillPayService.tieCardHandle(param);
				if ("00".equals(result.get("v_code"))) {
						String url = result.get("html");
						outString(response, url.replaceAll("", ""));
				}
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, gson.toJson(result));
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, gson.toJson(result));
		}

	}
	
	/**
	 * 代还支付签名接口
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody	
	@RequestMapping(value = "pay/ls/sign")
	  public void payScan(PayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=utf-8");
	    logger.info("下游上送的参数:" + entity);
	    
	    ChannleMerchantConfigKey keyinfo =loanStillPayService.getChannelConfigKey(entity.getV_mid());
	    String merchantKey = keyinfo.getMerchantkey();
	    logger.info("下游商户密钥:" + keyinfo);
	    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
	    entity.setV_sign(sign);
	    
	    request.setCharacterEncoding("UTF-8");
	    request.setAttribute("temp", entity);
	    request.getRequestDispatcher("/quick/loanStill/loanStill_pay_submit.jsp").forward(request, response);
	  }
	/**
	 * 代还支付请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "pay/ls/submit")
	public void paySubmit(PayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();

		logger.info("下游上送参数:{}"+ param);
		if (!StringUtils.isEmpty(param.getV_mid())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = loanStillPayService.getChannelConfigKey(param.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey)) {
				logger.info("对比签名成功");
				result = loanStillPayService.payHandle(param);
				outString(response, gson.toJson(result));
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, gson.toJson(result));
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, gson.toJson(result));
		}

	}
	/**
	 * 通联绑卡同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ls/tieCard/returnUrl")
	public void tieCardReturnUrl(HttpServletResponse response, HttpServletRequest request) {

		try {
			logger.info("##########通联绑卡同步数据返回参数##############");			
			String orderId=request.getParameter("orderId");
			
			OriginalOrderInfo originalInfo = null;
			if (orderId != null && orderId != "") {
				originalInfo = Origi.get(orderId);
			}
			logger.info("通联绑卡订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("通联绑卡下游的同步地址" + originalInfo.getTieCardPageUrl());
			TreeMap<String, String> result = new TreeMap<String, String>();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = loanStillPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_mid", originalInfo.getPid());
				PayResponseEntity consume = (PayResponseEntity) BeanToMapUtil
						.convertMap(PayResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(consume), key);
				result.put("v_sign", sign);
				params = HttpURLConection.parseParams(result);
				logger.info("通联绑卡给下游同步的数据:" + params);
				request.getSession();
				try {
					// 给下游手动返回支付结果
					if (originalInfo.getTieCardPageUrl().indexOf("?") == -1) {

						String path = originalInfo.getTieCardPageUrl() + "?" + params;
						logger.info("通联绑卡页面重定向地址：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						logger.info("通联绑卡页面重定向地址：" + originalInfo.getTieCardPageUrl());
						String path = originalInfo.getTieCardPageUrl() + "&" + params;
						logger.info("通联绑卡页面重定向地址：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			} else {
				logger.info("没有收到通联绑卡的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	/**
	 * 通联绑卡异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ls/tieCard/notifyUrl")
	public void tieCardnotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		try {
			logger.info("##############通联绑卡异步通知##############");
			String str = request.getParameter("params");
			
			String orderId="";
			
			String retCode="";
			
			String retMsg="";
			
			String token="";

			logger.info("通联绑卡上游返回的异步信息"+str);
			JSONObject jsonObject = JSONObject.fromObject(str);
			Iterator it = jsonObject.keys();
			while (it.hasNext()) {
				
				String key = (String) it.next();
				if (key.equals("retCode")) {
					retCode=jsonObject.getString(key);
					if ("00".equals(jsonObject.getString(key))) {						
						while (it.hasNext()) {
							String keys = (String) it.next();
							if (keys.equals("info")) {								
								Map maps = (Map) JSON.parse(jsonObject.getString(keys));
								orderId=maps.get("orderNo").toString();
							}

						}
					}
				}
				if(key.equals("retMsg"))
				{
					retMsg=jsonObject.getString(key);
				}
			}
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap<String, String>();
			if (!StringUtils.isEmpty(retCode)) {
				response.getWriter().write("SUCCESS");
				// 查询原始订单信息
				if (orderId != null && orderId != "") {
					originalInfo = Origi.get(orderId);
				}
				logger.info("通联绑卡异步订单数据:" + JSON.toJSON(originalInfo));
						result.put("v_oid", originalInfo.getOrderId());
						result.put("v_code", "00");
						result.put("v_msg", "请求成功");
						result.put("v_mid", originalInfo.getPid());
						if ("00".equals(retCode)) {
							result.put("v_payStatus", "0000");
							result.put("v_payMsg", "绑卡成功");

						} else {
							result.put("v_payStatus", "1001");
							result.put("v_payMsg", "绑卡失败");
							logger.info("交易错误码:" +retMsg);
						}
						ChannleMerchantConfigKey keyinfo = loanStillPayService.getChannelConfigKey(originalInfo.getPid());
						// 获取商户秘钥
						String key = keyinfo.getMerchantkey();
						CallbackEntity callback = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class, result);
						OriginalOrderInfo orInfo=new OriginalOrderInfo();
						// 修改绑卡订单信息
						orInfo.setByUser(token);
						orInfo.setOrderId(orderId);
						orInfo.setStatus("0000");
						loanStillPayService.otherInvoke(orInfo);

						// 生成签名
						String sign = SignatureUtil.getSign(beanToMap(callback), key);
						result.put("v_sign", sign);

						logger.info("通联绑卡异步之前的参数：" + result);
						ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
								.convertMap(ConsumeResponseEntity.class, result);
						Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
						logger.info("通联绑卡异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));
						String html = xdt.quickpay.conformityQucikPay.util.HttpClientUtil.post(originalInfo.getTieCardUrl(),
								bean2Util.bean2QueryStr(consumeResponseEntity));
						logger.info("通联绑卡下游返回状态" + html);
						JSONObject ob = JSONObject.fromObject(html);
						Iterator its = ob.keys();
						Map<String, String> map = new HashMap<>();
						while (its.hasNext()) {
							String keys = (String) its.next();
							if (keys.equals("success")) {
								String value = ob.getString(keys);
								logger.info("通联绑卡异步回馈的结果:" + "\t" + value);
								map.put("success", value);
							}
						}
						if (!map.get("success").equals("true")) {

							logger.info("通联绑卡启动线程进行异步通知");
							// 启线程进行异步通知
							ThreadPool.executor(new MbUtilThread(originalInfo.getTieCardUrl(),
									bean2Util.bean2QueryStr(consumeResponseEntity)));
						}

						logger.info("通联绑卡向下游 发送数据成功");

					} else {
						logger.error("回调的参数为空!");
						result.put("v_code", "15");
						result.put("v_msg", "请求失败");
					}
					outString(response, gson.toJson(result));

		} catch (

		Exception e) {
			logger.info("通联绑卡异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	/**
	 * 通联支付同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ls/pay/returnUrl")
	public void payReturnUrl(HttpServletResponse response, HttpServletRequest request) {

		try {
			logger.info("##########通联支付同步数据返回参数##############");
			logger.info("##########通联绑卡同步数据返回参数##############");			
			String orderId=request.getParameter("orderId");
			
			OriginalOrderInfo originalInfo = null;
			if (orderId != null && orderId != "") {
				originalInfo = Origi.get(orderId);
			}
			logger.info("通联支付订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("通联支付下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap<String, String>();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				request.getSession();
				try {
					// 给下游手动返回支付结果
					if (originalInfo.getPageUrl().indexOf("?") == -1) {

						String path = originalInfo.getPageUrl() + "?" + params;
						logger.info("通联支付页面重定向地址：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						logger.info("通联支付页面重定向地址：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						logger.info("通联支付页面重定向地址：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			} else {
				logger.info("没有收到通联支付的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	/**
	 * 通联支付异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ls/pay/notifyUrl")
	public void paynotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		try {
			logger.info("##############通联支付异步通知##############");
			String str = request.getParameter("params");
			
			String orderId="";
			
			String retCode="";
			
			String retMsg="";

			logger.info("通联支付上游返回的同步信息"+str);
			JSONObject jsonObject = JSONObject.fromObject(str);
			Iterator it = jsonObject.keys();
			while (it.hasNext()) {
				
				String key = (String) it.next();
				if (key.equals("retCode")) {
					retCode=jsonObject.getString(key);
					if ("00".equals(jsonObject.getString(key))) {						
						while (it.hasNext()) {
							String keys = (String) it.next();
							if (keys.equals("info")) {								
								Map maps = (Map) JSON.parse(jsonObject.getString(keys));
								orderId=maps.get("orderNo").toString();
							}

						}
					}
				}
				if(key.equals("retMsg"))
				{
					retMsg=jsonObject.getString(key);
				}
			}
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap<String, String>();
			if (!StringUtils.isEmpty(retCode)) {
				response.getWriter().write("success");
				// 查询原始订单信息
				if (orderId != null && orderId != "") {
					originalInfo = loanStillPayService.getOriginOrderInfo(orderId);
				}
				logger.info("通联支付异步订单数据:" + JSON.toJSON(originalInfo));
						result.put("v_oid", originalInfo.getOrderId());
						result.put("v_code", "00");
						result.put("v_msg", "请求成功");
						result.put("v_mid", originalInfo.getPid());
						if ("00".equals(retCode)) {
							result.put("v_payStatus", "0000");
							result.put("v_payMsg", "支付成功");
							// 修改订单状态
							loanStillPayService.otherInvokes(orderId,"0000");

						} else {
							result.put("v_payStatus", "1001");
							result.put("v_payMsg", "支付失败");
							logger.info("交易错误码:" +retMsg);
						}
						ChannleMerchantConfigKey keyinfo = loanStillPayService.getChannelConfigKey(originalInfo.getPid());
						// 获取商户秘钥
						String key = keyinfo.getMerchantkey();
						CallbackEntity callback = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class, result);
						// 生成签名
						String sign = SignatureUtil.getSign(beanToMap(callback), key);
						result.put("v_sign", sign);

						logger.info("通联支付异步之前的参数：" + result);
						ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
								.convertMap(ConsumeResponseEntity.class, result);
						
						Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
						logger.info("通联支付异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));
						String html = xdt.quickpay.conformityQucikPay.util.HttpClientUtil.post(originalInfo.getBgUrl(),
								bean2Util.bean2QueryStr(consumeResponseEntity));
						logger.info("通联支付下游返回状态" + html);
						JSONObject ob = JSONObject.fromObject(html);
						Iterator its = ob.keys();
						Map<String, String> map = new HashMap<>();
						while (its.hasNext()) {
							String keys = (String) its.next();
							if (keys.equals("success")) {
								String value = ob.getString(keys);
								logger.info("通联支付异步回馈的结果:" + "\t" + value);
								map.put("success", value);
							}
						}
						if (!map.get("success").equals("true")) {

							logger.info("通联支付启动线程进行异步通知");
							// 启线程进行异步通知
							ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),
									bean2Util.bean2QueryStr(consumeResponseEntity)));
						}

						logger.info("通联支付向下游 发送数据成功");

					} else {
						logger.error("回调的参数为空!");
						result.put("v_code", "15");
						result.put("v_msg", "请求失败");
					}
					outString(response, gson.toJson(result));

		} catch (

		Exception e) {
			logger.info("通联支付异步回调异常:" + e);
			e.printStackTrace();
		}
	}


}
