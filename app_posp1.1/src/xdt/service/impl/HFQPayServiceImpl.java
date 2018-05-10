/**
 * 
 */
package xdt.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import xdt.common.RetAppMessage;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsGoodsDao;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsOrderHelpDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IQuickpayPreRecordDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.quickpay.hf.util.PlatKeyGenerator;
import xdt.quickpay.hf.util.PreSignUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.service.HfQPayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

/**
 * @ClassName: HFQuickPayService
 * @Description: 第三方 恒丰快捷支付
 * @author LiShiwen
 * @date 2016年6月16日 上午10:51:28
 * 
 */
@Component
public class HFQPayServiceImpl extends BaseServiceImpl implements HfQPayService {

	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(HFQPayServiceImpl.class);

	private Logger logger = Logger.getLogger(HFQPayServiceImpl.class);

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	IPmsGoodsDao pmsGoodsDao;

	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsGoodsOrderDao pmsGoodsOrderDao;
	@Resource
	private IPmsAddressDao pmsAddressDao;
	@Resource
	private IPmsOrderHelpDao pmsOrderHelpDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	IPayCmmtufitDao iPayCmmtufitDao;
	@Resource
	MobaoPayHandel mobaoPayHandel;
	@Resource
	IQuickpayRecordDao quickpayRecordDao;
	@Resource
	IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;

	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#payHandle(xdt.quickpay.hengfeng.entity
	 * .PayRequestEntity, javax.servlet.http.HttpSession)
	 */
	// public synchronized String payHandle(PayRequestEntity originalinfo)
	// throws Exception {
	//
	// String message = "0:initialize";
	//
	// String jsonString = null;
	//
	// SubmitOrderNoCardPayResponseDTO responseDTO = new
	// SubmitOrderNoCardPayResponseDTO();
	//
	//
	// // 验证签名
	// ChannleMerchantConfigKey keyinfo = this
	// .getChannelConfigKey(originalinfo.getMerchantId());
	//
	// String merchantKey = keyinfo.getMerchantkey();
	// HFSignUtil signUtil = new HFSignUtil();
	// if (!signUtil.verify(PreSignUtil.hfpaySigiString(originalinfo),
	// originalinfo.getSignmsg(), merchantKey)) {
	// responseDTO.setRetCode(11);
	// responseDTO.setRetMessage("签名错误");
	// jsonString = createJsonString(responseDTO);
	// log.info("签名错误");
	// return jsonString;
	// }
	// // 验证当前是否已经下单
	// OriginalOrderInfo orig = new OriginalOrderInfo();
	// orig.setMerchantOrderId(originalinfo.getOrderId());
	// orig.setPid(originalinfo.getMerchantId());
	//
	// if (originalDao.selectByOriginal(orig) != null) {
	// responseDTO.setRetCode(12);
	// responseDTO.setRetMessage("下单重复");
	// jsonString = createJsonString(responseDTO);
	// log.info("下单重复");
	// return jsonString;
	// }
	//
	// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
	// // 业务号（2位）+业务细分（1位）+时间戳（13位）
	// // 总共16位
	// /**
	// * 插入原始数据信息
	// */
	// OriginalOrderInfo original = new OriginalOrderInfo();
	// original.setMerchantOrderId(originalinfo.getOrderId());// 原始数据的订单编号
	// original.setOrderId(orderNumber); // 为主键
	// original.setPid(originalinfo.getMerchantId());
	// original.setOrderTime(originalinfo.getTxnTime());
	// original.setOrderAmount(originalinfo.getTxnAmt());
	// original.setPageUrl(originalinfo.getFrontUrl());
	// original.setBgUrl(originalinfo.getBackUrl());
	// original.setBankNo(originalinfo.getAccNo());
	// original.setBankId(originalinfo.getCard());
	// original.setPayType(originalinfo.getTranTp());
	// originalDao.insert(original);
	//
	// // 根据商户号查询
	// String mercId = originalinfo.getMerchantId();
	//
	// PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
	// merchantinfo.setMercId(mercId);
	//
	// // o单编号
	// String oAgentNo = "";
	//
	// // 查询当前商户信息
	// List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
	// .searchList(merchantinfo);
	// if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
	//
	// merchantinfo = merchantList.get(0);
	// // merchantinfo.setCustomertype("3");
	//
	// oAgentNo = merchantinfo.getoAgentNo();//
	//
	// if (StringUtils.isBlank(oAgentNo)) {
	// // 如果没有欧单编号，直接返回错误
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("参数错误");
	// jsonString = createJsonString(responseDTO);
	// log.info("参数错误,没有欧单编号");
	// return jsonString;
	// }
	// // 判断是否为正式商户
	// if ("60".equals(merchantinfo.getMercSts())) {
	//
	// // 组装订单数据
	// PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
	// // 写入欧单编号
	// pmsAppTransInfo.setoAgentNo(oAgentNo);
	// pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());//
	// 订单初始化状态
	// pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay
	// .getTypeName());// 业务功能模块名称
	// // ：网购
	// pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
	// pmsAppTransInfo.setMercid(merchantinfo.getMercId());
	// pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay
	// .getTypeCode());// 业务功能模块编号
	// // ：17
	// pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
	// pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay
	// .getTypeName());
	// pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay
	// .getTypeCode());
	// pmsAppTransInfo.setFactamount("1");
	// pmsAppTransInfo.setPoundage("1");
	// pmsAppTransInfo.setPayamount("1");
	// pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
	// if (originalinfo.getTranTp().equals("0")) {
	// pmsAppTransInfo.setSettlementPeriod(originalinfo.getTranTp());// 结算周期
	// // pmsAppTransInfo.setSettlementState(reqeustInfo.getTranTp());//结算状态
	// } else if (originalinfo.getTranTp().equals("1")) {
	// pmsAppTransInfo.setSettlementPeriod(originalinfo.getTranTp());// 结算周期
	// // pmsAppTransInfo.setSettlementState("");//结算状态
	// }
	//
	// // 插入订单信息
	// Integer insertAppTrans = pmsAppTransInfoDao
	// .insert(pmsAppTransInfo);
	// if (insertAppTrans == 1) {
	//
	// log.info("插入订单成功");
	// }
	// } else {
	// // 请求参数为空
	// logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
	// jsonString = createJsonString(responseDTO);
	// return jsonString;
	// }
	// } else {
	// message = RetAppMessage.MERCHANTDOESNOTEXIST;
	// }
	//
	// // 解析要返回的信息
	// int retCode = Integer.parseInt(message.split(":")[0]);
	// String retMessage = message.split(":")[1];
	// if (retMessage.equals("initialize")) {
	// retMessage = "系统初始化";
	// } else if (retMessage.equals("merchantDoesNotExist")) {
	// retMessage = "商户信息不存在";
	// }
	// responseDTO.setRetCode(retCode);
	// responseDTO.setRetMessage(retMessage);
	// jsonString = createJsonString(responseDTO);
	//
	// return jsonString;
	//
	// }
	// public String pay(PayRequestEntity originalinfo) throws Exception {
	// // TODO Auto-generated method stub
	// String message = "0:initialize";
	//
	// String jsonString = null;
	//
	// SubmitOrderNoCardPayResponseDTO responseDTO = new
	// SubmitOrderNoCardPayResponseDTO();
	//
	//
	// // 验证签名
	// ChannleMerchantConfigKey keyinfo = this
	// .getChannelConfigKey(originalinfo.getMerchantId());
	//
	// String merchantKey = keyinfo.getMerchantkey();
	//
	// HFSignUtil signUtil = new HFSignUtil();
	// if (!signUtil.verify(PreSignUtil.hfmessageSignString(originalinfo),
	// originalinfo.getSignmsg(), merchantKey)) {
	// responseDTO.setRetCode(11);
	// responseDTO.setRetMessage("签名错误");
	// jsonString = createJsonString(responseDTO);
	// log.info("签名错误");
	// return jsonString;
	// }
	// // 验证当前是否已经下单
	// OriginalOrderInfo orig = new OriginalOrderInfo();
	// orig.setMerchantOrderId(originalinfo.getOrderId());
	// orig.setPid(originalinfo.getMerchantId());
	//
	// if (originalDao.selectByOriginal(orig) != null) {
	// responseDTO.setRetCode(12);
	// responseDTO.setRetMessage("下单重复");
	// jsonString = createJsonString(responseDTO);
	// log.info("下单重复");
	// return jsonString;
	// }
	//
	// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
	// // 业务号（2位）+业务细分（1位）+时间戳（13位）
	// // 总共16位
	// /**
	// * 插入原始数据信息
	// */
	// OriginalOrderInfo original = new OriginalOrderInfo();
	// original.setMerchantOrderId(originalinfo.getOrderId());// 原始数据的订单编号
	// original.setOrderId(orderNumber); // 为主键
	// original.setPid(originalinfo.getMerchantId());
	// original.setOrderTime(originalinfo.getTxnTime());
	// original.setOrderAmount(originalinfo.getTxnAmt());
	// if(originalinfo.getTranTp().equals("0"))
	// {
	// original.setBankNo(originalinfo.getAccNo());
	// }
	// originalDao.insert(original);
	//
	// // 根据商户号查询
	// String mercId = originalinfo.getMerchantId();
	//
	// PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
	// merchantinfo.setMercId(mercId);
	//
	// // o单编号
	// String oAgentNo = "";
	//
	// // 查询当前商户信息
	// List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
	// .searchList(merchantinfo);
	// if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
	//
	// merchantinfo = merchantList.get(0);
	// // merchantinfo.setCustomertype("3");
	//
	// oAgentNo = merchantinfo.getoAgentNo();//
	//
	// if (StringUtils.isBlank(oAgentNo)) {
	// // 如果没有欧单编号，直接返回错误
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("参数错误");
	// jsonString = createJsonString(responseDTO);
	// log.info("参数错误,没有欧单编号");
	// return jsonString;
	// }
	// // 判断是否为正式商户
	// if ("60".equals(merchantinfo.getMercSts())) {
	// // 实际金额
	// String factAmount = originalinfo.getTxnAmt();
	// // 校验欧单金额限制
	// ResultInfo payCheckResult = iPublicTradeVerifyService
	// .amountVerifyOagent(
	// (int) Double.parseDouble(factAmount),
	// TradeTypeEnum.onlinePay, oAgentNo);
	// if (!payCheckResult.getErrCode().equals("0")) {
	// // 交易不支持
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage(payCheckResult.getMsg());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
	// + PaymentCodeEnum.hengFengQuickPay.getTypeCode());
	// return jsonString;
	// }
	//
	// // 校验欧单模块是否开启
	// ResultInfo payCheckResult1 = iPublicTradeVerifyService
	// .moduleVerifyOagent(TradeTypeEnum.onlinePay, oAgentNo);
	// if (!payCheckResult1.getErrCode().equals("0")) {
	// // 交易不支持
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage(payCheckResult1.getMsg());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// logger.info("欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
	// + PaymentCodeEnum.hengFengQuickPay.getTypeCode());
	// return jsonString;
	// }
	// // 校验商户模块是否开启
	// ResultInfo payCheckResult3 = iPublicTradeVerifyService
	// .moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
	// if (!payCheckResult3.getErrCode().equals("0")) {
	// // 交易不支持
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage(payCheckResult3.getMsg());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
	// + PaymentCodeEnum.hengFengQuickPay.getTypeCode());
	// return jsonString;
	// }
	// // 校验商户金额限制
	// Map<String, String> paramMap = new HashMap<String, String>();
	// paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
	// paramMap.put("businesscode",
	// TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
	// paramMap.put("oAgentNo", oAgentNo);
	// //商户 网购 业务信息
	// Map<String, String> resultMap = merchantMineDao
	// .queryBusinessInfo(paramMap);
	//
	// if (resultMap == null || resultMap.size() == 0) {
	// // 若查到的是空值，直接返回错误
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
	// logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// return jsonString;
	// }
	//
	// String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
	// String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
	// String paymentAmount = factAmount;// 交易金额
	//
	// if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(
	// maxTransMoney)) == 1) {
	// // 金额超过最大金额
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("金额超过最大交易金额");
	// logger.info("交易金额大于最打金额");
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// return jsonString;
	// } else if (new BigDecimal(paymentAmount)
	// .compareTo(new BigDecimal(minTransMoney)) == -1) {
	// // 金额小于最小金额
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("金额小于最小交易金额");
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// logger.info("交易金额小于最小金额");
	// return jsonString;
	// }
	//
	// // 组装订单数据
	// PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
	// // 写入欧单编号
	// pmsAppTransInfo.setoAgentNo(oAgentNo);
	// pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());//
	// 订单初始化状态
	// pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay
	// .getTypeName());// 业务功能模块名称
	// // ：网购
	// pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
	// pmsAppTransInfo.setMercid(merchantinfo.getMercId());
	// pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay
	// .getTypeCode());// 业务功能模块编号
	// // ：17
	// pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
	// pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay
	// .getTypeName());
	// pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay
	// .getTypeCode());
	// pmsAppTransInfo.setPrepaidphonenumber(originalinfo.getPhoneNo());//手机号码
	// pmsAppTransInfo.setToken(originalinfo.getToken()); //令牌
	// BigDecimal factBigDecimal = new BigDecimal(factAmount);
	// BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
	//
	// pmsAppTransInfo.setFactamount(factBigDecimal
	// .stripTrailingZeros().toPlainString());// 实际金额
	// pmsAppTransInfo.setOrderamount(orderAmountBigDecimal
	// .stripTrailingZeros().toPlainString());// 订单金额
	// pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
	// if(originalinfo.getTranTp().equals("0"))
	// {
	// pmsAppTransInfo.setBankno(originalinfo.getAccNo());
	// pmsAppTransInfo.setMercname(originalinfo.getName());
	// pmsAppTransInfo.setCertNo(originalinfo.getCertNo());
	// }
	//
	// // 插入订单信息
	// Integer insertAppTrans = pmsAppTransInfoDao
	// .insert(pmsAppTransInfo);
	// if (insertAppTrans == 1) {
	//
	// //查询订单信息
	// pmsAppTransInfo =
	// pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
	//
	// String quickRateType =
	// resultMap.get("QUICKRATETYPE").toString();//快捷支付费率类型
	//
	// // 获取o单第三方支付的费率
	// AppRateConfig appRate = new AppRateConfig();
	// appRate.setRateType(quickRateType);
	// appRate.setoAgentNo(oAgentNo);
	// AppRateConfig appRateConfig =
	// appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
	//
	// if (appRateConfig == null) {
	// // 若查到的是空值，直接返回错误
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
	// logger.info("没有查到相关费率配置："
	// + merchantinfo.getMobilephone());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// return jsonString;
	// }
	//
	// String isTop = appRateConfig.getIsTop();
	// String rate = appRateConfig.getRate();
	// String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
	// paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
	// String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
	// Double minPoundage = 0.0; // 附加费
	//
	// if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
	// && appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
	// if (StringUtils.isNotBlank(minPoundageStr)) {
	// minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
	// } else {
	// // 若查到的是空值，直接返回错误
	// responseDTO.setRetCode(1);
	// responseDTO
	// .setRetMessage("没有查到相关费率配置（附加费），请联系客服人员");
	// logger.info("没有查到相关费率附加费（最低手续费）："
	// + merchantinfo.getMobilephone());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// return jsonString;
	// }
	// }
	//
	// BigDecimal payAmount = null;
	// BigDecimal dfactAmount = new BigDecimal(
	// pmsAppTransInfo.getFactamount());
	// // 费率
	// BigDecimal fee = new BigDecimal(0);
	// String rateStr = "";
	// Double payfee=null;
	// // 计算结算金额
	// if ("1".equals(isTop)) {
	//
	// rateStr = rate + "-" + topPoundage;
	// // 是封顶费率类型
	// fee = new BigDecimal(rate).multiply(dfactAmount);
	//
	// if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
	// // 手续费大于封顶金额，按封顶金额处理
	// payAmount = dfactAmount.subtract(new BigDecimal(
	// topPoundage).subtract(new BigDecimal(
	// minPoundage)));
	// fee = new BigDecimal(topPoundage)
	// .add(new BigDecimal(minPoundage));
	// } else {
	// // 按当前费率处理
	// rateStr = rate;
	// fee.add(new BigDecimal(minPoundage));
	// payAmount = dfactAmount.subtract(fee);
	// }
	//
	// } else {
	// // 按当前费率处理
	// rateStr = rate;
	// if(originalinfo.getTranTp().equals("0"))
	// {
	//
	// //最低手续费
	// double minfee=Double.parseDouble(merchantinfo.getCounter())*100;
	// Double
	// rr=Double.parseDouble(originalinfo.getUserfee())/Double.parseDouble(originalinfo.getTxnAmt());
	// log.info("下游上送的费率:"+rr);
	// if(rr>=Double.parseDouble(rate.toString())){
	//
	// if(Double.parseDouble(originalinfo.getUserfee())>=minfee)
	// {
	// fee = new BigDecimal(originalinfo.getUserfee()).add(
	// new BigDecimal(minPoundage));
	// payAmount = dfactAmount.subtract(fee);
	// payfee=fee.doubleValue();
	// }else
	// {
	// payfee=minfee;
	// BigDecimal bd=new BigDecimal(payfee);
	// payAmount = dfactAmount.subtract(bd);
	// }
	//
	// }
	// }else if(originalinfo.getTranTp().equals("1"))
	// {
	// fee = new BigDecimal(rate).multiply(dfactAmount).add(
	// new BigDecimal(minPoundage));
	// payfee=fee.doubleValue();
	// payAmount = dfactAmount.subtract(fee);
	// }
	// }
	// // 设置结算金额
	// pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
	// pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
	// log.info("手续费为:"+payfee);
	// pmsAppTransInfo.setPoundage(payfee.toString());
	// pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
	// // 转换double为int
	// Integer paymentAmountInt = (int) Double
	// .parseDouble(paymentAmount);
	//
	// // 验证支付方式是否开启
	// payCheckResult = iPublicTradeVerifyService.totalVerify(
	// paymentAmountInt, TradeTypeEnum.onlinePay,
	// PaymentCodeEnum.hengFengQuickPay, oAgentNo,
	// merchantinfo.getMercId());
	// if (!payCheckResult.getErrCode().equals("0")) {
	// // 交易不支持
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage(payCheckResult.getMsg());
	// try {
	// jsonString = createJsonString(responseDTO);
	// } catch (Exception em) {
	// em.printStackTrace();
	// }
	// logger.info("不支持的支付方式，oAagentNo:"
	// + oAgentNo
	// + ",payType:"
	// + PaymentCodeEnum.hengFengQuickPay
	// .getTypeCode());
	// return jsonString;
	// }
	// ViewKyChannelInfo channelInfo = AppPospContext.context
	// .get(HENGFENGPAY + HENGFENGCHANNELNUM);
	//
	// // 设置通道信息
	// pmsAppTransInfo
	// .setBusinessNum(channelInfo.getBusinessnum());
	// pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);
	//
	// // 查看当前交易是否已经生成了流水表
	// PospTransInfo pospTransInfo = null;
	// // 流水表是否需要更新的标记 0 insert，1：update
	// int insertOrUpdateFlag = 0;
	// // 生成上送流水号
	// String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
	// PaymentCodeEnum.hengFengQuickPay);
	// if ((pospTransInfo = pospTransInfoDAO
	// .searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
	// // 已经存在，修改流水号，设置pospsn为空
	// logger.info("订单号：" + pmsAppTransInfo.getOrderid()
	// + ",生成上送通道的流水号：" + transOrderId);
	// pospTransInfo.setTransOrderId(originalinfo.getOrderId());
	// pospTransInfo.setResponsecode("99");
	// pospTransInfo.setPospsn("");
	// insertOrUpdateFlag = 1;
	// } else {
	// // 不存在流水，生成一个流水
	// pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
	// // 设置上送流水号
	// pospTransInfo.setTransOrderId(originalinfo.getOrderId());
	// insertOrUpdateFlag = 0;
	// }
	// // 插入流水表信息
	// if (insertOrUpdateFlag == 0) {
	// // 插入一条流水
	// pospTransInfoDAO.insert(pospTransInfo);
	// } else if (insertOrUpdateFlag == 1) {
	// // 更新一条流水
	// pospTransInfoDAO.updateByOrderId(pospTransInfo);
	// }
	// log.info("修改订单信息");
	// log.info(pmsAppTransInfo);
	// pmsAppTransInfoDao.update(pmsAppTransInfo);
	// }
	// } else {
	// // 请求参数为空
	// logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
	// jsonString = createJsonString(responseDTO);
	// return jsonString;
	// }
	// } else {
	// message = RetAppMessage.MERCHANTDOESNOTEXIST;
	// }
	//
	// // 解析要返回的信息
	// int retCode = Integer.parseInt(message.split(":")[0]);
	// String retMessage = message.split(":")[1];
	// if (retMessage.equals("initialize")) {
	// retMessage = "系统初始化";
	// } else if (retMessage.equals("merchantDoesNotExist")) {
	// retMessage = "商户信息不存在";
	// }
	// responseDTO.setRetCode(retCode);
	// responseDTO.setRetMessage(retMessage);
	// jsonString = createJsonString(responseDTO);
	// return jsonString;
	// }
	public String consumepayHandle(PayRequestEntity originalinfo) throws Exception {
		
		String message = "0:initialize";
		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();

		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSignUtil.hfconsumeSignString(originalinfo), originalinfo.getSignmsg(), merchantKey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonString = createJsonString(responseDTO);
			log.info("签名错误");
			return jsonString;
		}
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getOrderId());
		orig.setPid(originalinfo.getMerchantId());

		if (originalDao.selectByOriginal(orig) != null) {
			responseDTO.setRetCode(12);
			responseDTO.setRetMessage("下单重复");
			jsonString = createJsonString(responseDTO);
			log.info("下单重复");
			return jsonString;
		}

		String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
															// 业务号（2位）+业务细分（1位）+时间戳（13位）
															// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getOrderId());// 原始数据的订单编号
		original.setOrderId(orderNumber); // 为主键
		original.setPid(originalinfo.getMerchantId());
		original.setOrderTime(originalinfo.getTxnTime());
		original.setOrderAmount(originalinfo.getTxnAmt());
		if (originalinfo.getPayType().equals("10")) {
			original.setBankNo(originalinfo.getAccNo());
			original.setByUser(originalinfo.getName());
		}
		original.setBgUrl(originalinfo.getBackUrl());
		original.setPayType(originalinfo.getPayType());
		original.setPageUrl(originalinfo.getFrontUrl());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getMerchantId();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("参数错误");
				jsonString = createJsonString(responseDTO);
				log.info("参数错误,没有欧单编号");
				return jsonString;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				// 实际金额
				String factAmount = originalinfo.getTxnAmt();
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage(payCheckResult.getMsg());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return jsonString;
				}

				// 校验欧单模块是否开启
				ResultInfo payCheckResult1 = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.onlinePay,
						oAgentNo);
				if (!payCheckResult1.getErrCode().equals("0")) {
					// 交易不支持
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage(payCheckResult1.getMsg());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return jsonString;
				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage(payCheckResult3.getMsg());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return jsonString;
				}
				// 校验商户金额限制
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
				paramMap.put("businesscode", TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
				paramMap.put("oAgentNo", oAgentNo);
				// 商户 网购 业务信息
				Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

				if (resultMap == null || resultMap.size() == 0) {
					// 若查到的是空值，直接返回错误
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
					logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					return jsonString;
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					// 金额超过最大金额
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("金额超过最大交易金额");
					logger.info("交易金额大于最打金额");
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					return jsonString;
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("金额小于最小交易金额");
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("交易金额小于最小金额");
					return jsonString;
				}

				// 组装订单数据
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
				// 写入欧单编号
				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
				pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay.getTypeName());// 业务功能模块名称
																					// ：网购
				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay.getTypeCode());// 业务功能模块编号
																						// ：17
				pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
				if (originalinfo.getPayType().equals("10")) {
					pmsAppTransInfo.setBankno(originalinfo.getAccNo());
					pmsAppTransInfo.setMercname(originalinfo.getName());
					pmsAppTransInfo.setCertNo(originalinfo.getCertNo());
					pmsAppTransInfo.setSettlementState("D0");
				}
				if (originalinfo.getPayType().equals("20")) {
					
					pmsAppTransInfo.setSettlementState("T1");
				}
				

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
				if (insertAppTrans == 1) {

					// 查询订单信息
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					if (appRateConfig == null) {
						// 若查到的是空值，直接返回错误
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						try {
							jsonString = createJsonString(responseDTO);
						} catch (Exception em) {
							em.printStackTrace();
						}
						return jsonString;
					}

					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
					Double minPoundage = 0.0; // 附加费

					if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
							&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
						} else {
							// 若查到的是空值，直接返回错误
							responseDTO.setRetCode(1);
							responseDTO.setRetMessage("没有查到相关费率配置（附加费），请联系客服人员");
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							try {
								jsonString = createJsonString(responseDTO);
							} catch (Exception em) {
								em.printStackTrace();
							}
							return jsonString;
						}
					}

					BigDecimal payAmount = null;
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					String rateStr = "";
					Double payfee = null;
					// 计算结算金额
					if ("1".equals(isTop)) {

						rateStr = rate + "-" + topPoundage;
						// 是封顶费率类型
						fee = new BigDecimal(rate).multiply(dfactAmount);

						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
							// 手续费大于封顶金额，按封顶金额处理
							payAmount = dfactAmount
									.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
							fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
						} else {
							// 按当前费率处理
							rateStr = rate;
							fee.add(new BigDecimal(minPoundage));
							payAmount = dfactAmount.subtract(fee);
						}

					} else {
						 double dfpag=Double.parseDouble(merchantinfo.getPoundage());
						// 按当前费率处理
						if (originalinfo.getPayType().equals("10")) {
							 rateStr = rate;
							   
							    if(Double.parseDouble(rateStr)>=Double.parseDouble(originalinfo.getUserfee())/100)
							    {
							    	if(Double.parseDouble(originalinfo.getPounage())>=dfpag)
							    	{
							    		fee = new BigDecimal(rate).multiply(dfactAmount).add(new BigDecimal(minPoundage).add(new BigDecimal(originalinfo.getPounage()).multiply(new BigDecimal(100))));
										payfee = fee.doubleValue();
										payAmount = dfactAmount.subtract(fee);	
							    	}
							    	
							    }else
							    {
							    	responseDTO.setRetCode(1);
									responseDTO.setRetMessage("费率低于成本费率");
									logger.info("费率低于成本费率：" + merchantinfo.getMercId());
							    }
						}
						if (originalinfo.getPayType().equals("20")) {
							
							// 按当前费率处理
							rateStr = rate;
							fee = new BigDecimal(rate).multiply(dfactAmount).add(new BigDecimal(minPoundage));
							payfee=fee.doubleValue();
							if(dfpag*100>=payfee)
					    	{
								payfee=dfpag*100;
					    	}
							payAmount = dfactAmount.subtract(new BigDecimal(payfee));
						}
						   
							
					}
					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
					log.info("手续费为:" + payfee);
					pmsAppTransInfo.setPoundage(payfee.toString());
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay, oAgentNo, merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage(payCheckResult.getMsg());
						try {
							jsonString = createJsonString(responseDTO);
						} catch (Exception em) {
							em.printStackTrace();
						}
						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return jsonString;
					}
					ViewKyChannelInfo channelInfo = AppPospContext.context.get(HENGFENGPAY + HENGFENGCHANNELNUM);

					// 设置通道信息
					pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
					pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);

					// 查看当前交易是否已经生成了流水表
					PospTransInfo pospTransInfo = null;
					// 流水表是否需要更新的标记 0 insert，1：update
					int insertOrUpdateFlag = 0;
					// 生成上送流水号
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getOrderId());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getOrderId());
						insertOrUpdateFlag = 0;
					}
					// 插入流水表信息
					if (insertOrUpdateFlag == 0) {
						// 插入一条流水
						pospTransInfoDAO.insert(pospTransInfo);
					} else if (insertOrUpdateFlag == 1) {
						// 更新一条流水
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					log.info("修改订单信息");
					log.info(pmsAppTransInfo);
					pmsAppTransInfoDao.update(pmsAppTransInfo);
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
				jsonString = createJsonString(responseDTO);
				return jsonString;
			}
		} else {
			message = RetAppMessage.MERCHANTDOESNOTEXIST;
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("merchantDoesNotExist")) {
			retMessage = "商户信息不存在";
		}
		responseDTO.setRetCode(retCode);
		responseDTO.setRetMessage(retMessage);
		jsonString = createJsonString(responseDTO);
		return jsonString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#otherInvoke(xdt.quickpay.hengfeng.
	 * entity.PayResponseEntity)
	 */
	@Override
	public synchronized void otherInvoke(PayResponseEntity result) throws Exception {

		// 流水表transOrderId
		String transOrderId = result.getOrderId();

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);

		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());

		// 查询结果成功
		if ("00".equals(result.getRespCode())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getRespMsg());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				log.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getRespMsg());
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("支付失败");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn("支付失败");
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.impl.HfQuickPayService#updateMerchantBanlance(xdt.model.
	 * PmsAppTransInfo)
	 */
	@Override
	public synchronized int updateMerchantBanlance(PmsAppTransInfo pmsAppTransInfo) {
		int result = 0;
		try {
			result = merchantCollectMoneyService.updateMerchantBalance(pmsAppTransInfo);
		} catch (Exception e) {
			logger.info("修改余额的时候出错(" + pmsAppTransInfo.getOrderid() + ")：" + e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#getOriginOrderInfo(java.lang.String)
	 */
	@Override
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.impl.HfQuickPayService#getTransInfo(java.lang.String)
	 */
	@Override
	public PospTransInfo getTransInfo(String orderId) {
		PospTransInfo transinfo = pospTransInfoDAO.searchByOrderId(orderId);
		return transinfo;
	}

	// 查询手续费
	public PmsAppTransInfo getFeeInfo(String orderId) throws Exception {
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(orderId);
		return pmsAppTransInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#getoriginInfoByMerchantOrderId(java.
	 * lang.String)
	 */
	@Override
	public synchronized OriginalOrderInfo getoriginInfoByMerchantOrderId(String originalOrderId) {
		log.info("根据商户订单id  获取商户上送原始信息");
		return originalDao.getOriginalOrderInfoByMerchanOrderId(originalOrderId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.HfQuickPayService#getChannelConfigKey(java.lang.String)
	 */
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public ViewKyChannelInfo getChannelInfo() {
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(HENGFENGPAY + HENGFENGCHANNELNUM);
		return channelInfo;
	}

	public OriginalOrderInfo selectByOriginal(OriginalOrderInfo queryWhere) {
		log.info("查询上送原始信息   下游订单id  商户号联合查询");
		return originalDao.selectByOriginal(queryWhere);
	}

	public OriginalOrderInfo getOriginalOrderInfoByMerchanOrderId(String merchantOrderId) {

		return originalDao.getOriginalOrderInfoByMerchanOrderId(merchantOrderId);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#payHandle(xdt.quickpay.hengfeng.entity
	 * .PayRequestEntity, javax.servlet.http.HttpSession)
	 */
	// public synchronized String payHandle1(PayRequestEntity originalinfo)
	// throws Exception {
	//
	// String message = "0:initialize";
	//
	// String jsonString = null;
	//
	// SubmitOrderNoCardPayResponseDTO responseDTO = new
	// SubmitOrderNoCardPayResponseDTO();
	//
	//
	// // 验证签名
	// ChannleMerchantConfigKey keyinfo = this
	// .getChannelConfigKey(originalinfo.getMerchantId());
	//
	// String merchantKey = keyinfo.getMerchantkey();
	// HFSignUtil signUtil = new HFSignUtil();
	/// * if (!signUtil.verify(PreSignUtil.hfpaySigiString(originalinfo),
	// originalinfo.getSignmsg(), merchantKey)) {
	// responseDTO.setRetCode(11);
	// responseDTO.setRetMessage("签名错误");
	// jsonString = createJsonString(responseDTO);
	// log.info("签名错误");
	// return jsonString;
	// }*/
	// // 验证当前是否已经下单
	// OriginalOrderInfo orig = new OriginalOrderInfo();
	// orig.setMerchantOrderId(originalinfo.getOrderId());
	// orig.setPid(originalinfo.getMerchantId());
	//
	// if (originalDao.selectByOriginal(orig) != null) {
	// responseDTO.setRetCode(12);
	// responseDTO.setRetMessage("下单重复");
	// jsonString = createJsonString(responseDTO);
	// log.info("下单重复");
	// return jsonString;
	// }
	//
	// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
	// // 业务号（2位）+业务细分（1位）+时间戳（13位）
	// // 总共16位
	// /**
	// * 插入原始数据信息
	// */
	// OriginalOrderInfo original = new OriginalOrderInfo();
	// original.setMerchantOrderId(originalinfo.getOrderId());// 原始数据的订单编号
	// original.setOrderId(orderNumber); // 为主键
	// original.setPid(originalinfo.getMerchantId());
	// original.setOrderTime(originalinfo.getTxnTime());
	// original.setOrderAmount(originalinfo.getTxnAmt());
	// original.setPageUrl(originalinfo.getFrontUrl());
	// original.setBgUrl(originalinfo.getBackUrl());
	// original.setBankNo(originalinfo.getAccNo());
	// original.setBankId(originalinfo.getCard());
	// original.setPayType(originalinfo.getTranTp());
	// originalDao.insert(original);
	//
	// // 根据商户号查询
	// String mercId = originalinfo.getMerchantId();
	//
	// PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
	// merchantinfo.setMercId(mercId);
	//
	// // o单编号
	// String oAgentNo = "";
	//
	// // 查询当前商户信息
	// List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
	// .searchList(merchantinfo);
	// if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
	//
	// merchantinfo = merchantList.get(0);
	// // merchantinfo.setCustomertype("3");
	//
	// oAgentNo = merchantinfo.getoAgentNo();//
	//
	// if (StringUtils.isBlank(oAgentNo)) {
	// // 如果没有欧单编号，直接返回错误
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("参数错误");
	// jsonString = createJsonString(responseDTO);
	// log.info("参数错误,没有欧单编号");
	// return jsonString;
	// }
	// // 判断是否为正式商户
	// if ("60".equals(merchantinfo.getMercSts())) {
	//
	// // 组装订单数据
	// PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
	// // 写入欧单编号
	// pmsAppTransInfo.setoAgentNo(oAgentNo);
	// pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());//
	// 订单初始化状态
	// pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay
	// .getTypeName());// 业务功能模块名称
	// // ：网购
	// pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
	// pmsAppTransInfo.setMercid(merchantinfo.getMercId());
	// pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay
	// .getTypeCode());// 业务功能模块编号
	// // ：17
	// pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
	// pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay
	// .getTypeName());
	// pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay
	// .getTypeCode());
	// pmsAppTransInfo.setFactamount("1");
	// pmsAppTransInfo.setPoundage("1");
	// pmsAppTransInfo.setPayamount("1");
	// pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
	// if (originalinfo.getTranTp().equals("0")) {
	// pmsAppTransInfo.setSettlementPeriod(originalinfo.getTranTp());// 结算周期
	// // pmsAppTransInfo.setSettlementState(reqeustInfo.getTranTp());//结算状态
	// } else if (originalinfo.getTranTp().equals("1")) {
	// pmsAppTransInfo.setSettlementPeriod(originalinfo.getTranTp());// 结算周期
	// // pmsAppTransInfo.setSettlementState("");//结算状态
	// }
	//
	// // 插入订单信息
	// Integer insertAppTrans = pmsAppTransInfoDao
	// .insert(pmsAppTransInfo);
	// if (insertAppTrans == 1) {
	//
	// log.info("插入订单成功");
	// }
	// } else {
	// // 请求参数为空
	// logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
	// responseDTO.setRetCode(1);
	// responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
	// jsonString = createJsonString(responseDTO);
	// return jsonString;
	// }
	// } else {
	// message = RetAppMessage.MERCHANTDOESNOTEXIST;
	// }
	//
	// // 解析要返回的信息
	// int retCode = Integer.parseInt(message.split(":")[0]);
	// String retMessage = message.split(":")[1];
	// if (retMessage.equals("initialize")) {
	// retMessage = "系统初始化";
	// } else if (retMessage.equals("merchantDoesNotExist")) {
	// retMessage = "商户信息不存在";
	// }
	// responseDTO.setRetCode(retCode);
	// responseDTO.setRetMessage(retMessage);
	// jsonString = createJsonString(responseDTO);
	//
	// return jsonString;
	//
	// }

}
