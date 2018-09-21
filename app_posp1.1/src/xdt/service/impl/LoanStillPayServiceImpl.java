package xdt.service.impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.quickpay.conformityQucikPay.util.OrderStatusEnum;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.loanStill.entity.PayRequestEntity;
import xdt.quickpay.loanStill.entity.RegisterRequestEntity;
import xdt.quickpay.loanStill.util.HttpClientUtil;
import xdt.quickpay.loanStill.util.HttpUtil;
import xdt.quickpay.loanStill.util.MD5Util;
import xdt.quickpay.loanStill.util.MakeMacUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.ILoanStillPayService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.util.BeanToMapUtil;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

@Service
public class LoanStillPayServiceImpl extends BaseServiceImpl implements ILoanStillPayService {

	private Logger logger = Logger.getLogger(LoanStillPayServiceImpl.class);

	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	@Resource
	private OriginalOrderInfoDao originalDao;
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;

	/**
	 * 微信商户信息
	 */
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;

		PospTransInfo transInfo = this.pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		this.logger.info("根据上送订单号  查询商户上送原始信息");
		original = this.originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	public Map<String, String> lsRegister(RegisterRequestEntity register) throws Exception {
		// TODO Auto-generated method stub
		logger.info("请求参数：" + JSON.toJSONString(register));
		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = register.getV_mid();

		// 手机号
		String phone = register.getV_settlePhone();
		// 商户注册信息
		logger.info("******************根据手机号查询");

		// 验证当前商户是否注册过

		if (weixinService.selectByPrimaryKey(phone) != null) {

			Integer status = weixinService.selectByPrimaryKey(phone).getStatus();
			if ("00".equals(status.toString())) {
				logger.info("商户注册信息重复,请重新输入!");
				return setResp("03", "商户注册信息重复,请重新输入!");
			} else {
				// 根据商户号查询
				String mercId = register.getV_mid();

				PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
				merchantinfo.setMercId(mercId);

				// o单编号
				String oAgentNo = "";
				// 查询当前商户信息
				List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
				if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
					merchantinfo = merchantList.get(0);
					oAgentNo = merchantinfo.getoAgentNo();

					if (StringUtils.isBlank(oAgentNo)) {
						// 如果没有欧单编号，直接返回错误
						logger.error("参数错误!");
						retMap.put("v_code", "04");
						retMap.put("v_msg", "参数错误,没有欧单编号");
						return retMap;
					}
					// 判断是否为正式商户
					if ("60".equals(merchantinfo.getMercSts())) {

						logger.info("是正式商户");
						Map<String, String> paramMap = new HashMap();
						paramMap.put("mercid", merchantinfo.getMercId());
						paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
						paramMap.put("oAgentNo", oAgentNo);

						Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);
						if ((resultMap == null) || (resultMap.size() == 0)) {
							logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
						String quickRateType = ((String) resultMap.get("QUICKRATETYPE")).toString();

						AppRateConfig appRate = new AppRateConfig();
						appRate.setRateType(quickRateType);
						appRate.setoAgentNo(oAgentNo);
						AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
						if (appRateConfig == null) {
							logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
						String isTop = appRateConfig.getIsTop();
						String rate = appRateConfig.getRate();
						String topPoundage = appRateConfig.getTopPoundage();
						String minPoundageStr = appRateConfig.getBottomPoundage();
						Double minPoundage = Double.valueOf(0.0D);
						if ((StringUtils.isNotBlank(appRateConfig.getIsBottom()))
								&& (appRateConfig.getIsBottom().equals("1"))) {
							if (StringUtils.isNotBlank(minPoundageStr)) {
								minPoundage = Double.valueOf(Double.parseDouble(minPoundageStr));
							} else {
								logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
								return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
							}
						}
						// 费率
						Double userfee = 0.0;
						String rateStr = "";
						userfee = Double.parseDouble(register.getV_cRate());
						rateStr = rate;
						if (Double.parseDouble(rateStr) <= userfee) {

							// 查询商户路由
							PmsBusinessPos pmsBusinessPos = selectKey(merchId);// 获取上游商户号和秘钥
							String url = "http://sitcustback.daxtech.com.cn/merchantDH/importDHMerchant/openData";
							// String url = "http://localhost:8080/back/importDHMerchant/openData";
							Map<String, String> condition = new HashMap<String, String>();
							DefaultHttpClient httpclient = new DefaultHttpClient();

							JSONObject json = new JSONObject();
							json.put("type", "insTLData"); // 报件固定值
							json.put("agentNo", pmsBusinessPos.getBusinessnum()); // 收单机构(代理商编号)
							json.put("shortName", register.getV_merName()); // 商户简称
							json.put("merName", register.getV_merName()); // 商户全称
							json.put("provCode", "天津市"); // 商户省份
							json.put("cityCode", "市辖区"); // 商户地市
							json.put("areaCode", "红桥区"); // 商户区县
							json.put("settleType", register.getV_settleType()); // 结算卡类型 PERSONAL 对私 ENTERPRISE 对公
							json.put("settleBranchNo", register.getV_settlePmsBankNo()); // 联行号
							json.put("settleName", register.getV_settleName()); // 结算账号
							json.put("settleNo", register.getV_settleCardNo()); // 结算卡号
							json.put("settleMobile", register.getV_settlePhone()); // 结算卡手机号
							json.put("mcc", pmsBusinessPos.getBusinessnum()); // MCC码
							json.put("legalName", register.getV_legalName()); // 法人姓名
							json.put("legalContact", register.getV_legalContact()); // 法人手机号
							json.put("legalId", register.getV_legalId()); // 法人证件号
							json.put("idStartTime", register.getV_idStartTime()); // 身份证生效日期
							json.put("idEndTime", register.getV_idEndTime()); // 身份证失效日期
							json.put("licenseNo", register.getV_licenseNo()); // 营业执照号
							json.put("licenseName", register.getV_licenseName()); // 营业执照名
							json.put("licenseStartTime", register.getV_licenseStartTime()); // 营业执照生效日期
							json.put("licenseEndTime", register.getV_licenseEndTime()); // 营业执照失效日期
							json.put("bizDomain", register.getV_bizDomain()); // 经营范围
							json.put("mainBusiness", register.getV_mainBusiness()); // 主营业务
							json.put("taxId", pmsBusinessPos.getBusinessnum()); // 税务登记
							json.put("orgCode", pmsBusinessPos.getBusinessnum()); // 组织机构代码
							json.put("address", pmsBusinessPos.getBusinessnum()); // 注册地址
							json.put("settfee", "0"); // 提现手续费

							JSONObject temp = new JSONObject();
							temp.put("prodType", "000008");// 产品类型
							temp.put("rate", register.getV_rate());// 费率
							temp.put("cRate", register.getV_cRate());// 费率

							json.put("fee", temp);
							MD5Util md5 = new MD5Util();
							String sync = md5.MD5s(json.toString() + pmsBusinessPos.getKek());// MD5完整性验证加密
							json.put("sync", sync);
							condition.put("data", json.toString());
							logger.info("通联上送的数据:" + condition);
							String result = HttpClientUtil.post(httpclient, url, condition);
							logger.info("通联上游返回的数据:" + result);
							JSONObject jsonObject = JSONObject.fromObject(result);
							Iterator it = jsonObject.keys();
							// Map<String, String> map = new HashMap<>();
							while (it.hasNext()) {

								String key = (String) it.next();
								if (key.equals("data")) {
									Map maps = (Map) JSON.parse(jsonObject.getString(key));

									if ("success".equals(maps.get("status"))) {

										PmsWeixinMerchartInfo weixin = new PmsWeixinMerchartInfo();
										weixin.setSubContractId(maps.get("merNo").toString());
										weixin.setPrivateKey(maps.get("reqKey").toString());
										weixin.setDfSubContractId(maps.get("dfmerNo").toString());
										weixin.setAppId(maps.get("dfcompanyNo").toString());
										weixin.setDfPrivateKey(maps.get("dfreqKey").toString());
										weixin.setRateCode("123");
										weixin.setStatus(1);
										weixin.setAccount(register.getV_settlePhone());
										weixinService.updateByPrimaryKeySelective(weixin);
										retMap.put("v_merNo", maps.get("merNo").toString());
										retMap.put("v_reqKey", maps.get("reqKey").toString());
										retMap.put("v_dfmerNo", maps.get("dfmerNo").toString());
										retMap.put("v_dfreqKey", maps.get("dfreqKey").toString());
										retMap.put("v_code", "00");
										retMap.put("v_msg", "注册成功");
									}
									if ("商户报件：法人身份证号重复".equals(maps.get("msg"))
											|| "商户报件：商户简称已存在".equals(maps.get("msg"))
											|| "商户报件：商户全称已存在".equals(maps.get("msg"))
											|| "渠道-商户信息重复".equals(maps.get("msg"))) {
										retMap.put("v_code", "20");
										retMap.put("v_msg", "已注册：" + maps.get("msg"));
									}

								}

							}

						} else {
							logger.info("费率低于成本费率：" + merchantinfo.getMercId());
							return setResp("12", "费率低于成本费率");
						}

					} else {
						// 请求参数为空
						logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
						retMap.put("v_code", "16");
						retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
						return retMap;
					}
				} else {
					// 请求参数为空
					logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					retMap.put("v_code", "16");
					retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
					return retMap;
				}
			}

		} else {

			// 插入小微商户信息
			PmsWeixinMerchartInfo entity = new PmsWeixinMerchartInfo();
			entity.setMerchartId(merchId);// 商户号
			entity.setAccount(phone);// 账号
			entity.setMerchartName(register.getV_merName());// 商户名称
			entity.setAccountType(register.getV_settleType());// 结算卡类型
			entity.setCardNo(register.getV_settleCardNo());// 结算卡号
			entity.setAccountName(register.getV_settleName());// 结算名
			entity.setPmsBankNo(register.getV_settlePmsBankNo());// 联行号
			entity.setMobile(register.getV_settlePhone());// 结算手机号
			entity.setCertNo(register.getV_legalId());// 法人证件号
			entity.setLicenseNo(register.getV_licenseNo());// 营业执照号
			entity.setWithdrawDepositRate("0");// 提现手续费
			entity.setDebitRate(register.getV_rate());// 借记卡费率
			entity.setCreditRate(register.getV_cRate());// 贷记卡费率
			entity.setoAgentNo("100333");
			entity.setRateCode("123");
			weixinService.updateRegister(entity);
			// 根据商户号查询
			String mercId = register.getV_mid();

			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(mercId);

			// o单编号
			String oAgentNo = "";
			// 查询当前商户信息
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
				merchantinfo = merchantList.get(0);
				oAgentNo = merchantinfo.getoAgentNo();

				if (StringUtils.isBlank(oAgentNo)) {
					// 如果没有欧单编号，直接返回错误
					logger.error("参数错误!");
					retMap.put("v_code", "04");
					retMap.put("v_msg", "参数错误,没有欧单编号");
					return retMap;
				}
				// 判断是否为正式商户
				if ("60".equals(merchantinfo.getMercSts())) {

					logger.info("是正式商户");
					Map<String, String> paramMap = new HashMap();
					paramMap.put("mercid", merchantinfo.getMercId());
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
					paramMap.put("oAgentNo", oAgentNo);

					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);
					if ((resultMap == null) || (resultMap.size() == 0)) {
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
					}
					String quickRateType = ((String) resultMap.get("QUICKRATETYPE")).toString();

					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
					if (appRateConfig == null) {
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
					}
					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();
					String minPoundageStr = appRateConfig.getBottomPoundage();
					Double minPoundage = Double.valueOf(0.0D);
					if ((StringUtils.isNotBlank(appRateConfig.getIsBottom()))
							&& (appRateConfig.getIsBottom().equals("1"))) {
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.valueOf(Double.parseDouble(minPoundageStr));
						} else {
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
					}
					// 费率
					Double userfee = 0.0;
					String rateStr = "";
					userfee = Double.parseDouble(register.getV_cRate());
					rateStr = rate;
					if (Double.parseDouble(rateStr) <= userfee) {
						// 查询商户路由
						PmsBusinessPos pmsBusinessPos = selectKey(merchId);// 获取上游商户号和秘钥
						String url = "http://sitcustback.daxtech.com.cn/merchantDH/importDHMerchant/openData";
						// String url = "http://localhost:8080/back/importDHMerchant/openData";
						Map<String, String> condition = new HashMap<String, String>();
						DefaultHttpClient httpclient = new DefaultHttpClient();

						JSONObject json = new JSONObject();
						json.put("type", "insTLData"); // 报件固定值
						json.put("agentNo", pmsBusinessPos.getBusinessnum()); // 收单机构(代理商编号)
						json.put("shortName", register.getV_merName()); // 商户简称
						json.put("merName", register.getV_merName()); // 商户全称
						json.put("provCode", "天津市"); // 商户省份
						json.put("cityCode", "市辖区"); // 商户地市
						json.put("areaCode", "红桥区"); // 商户区县
						json.put("settleType", register.getV_settleType()); // 结算卡类型 PERSONAL 对私 ENTERPRISE 对公
						json.put("settleBranchNo", register.getV_settlePmsBankNo()); // 联行号
						json.put("settleName", register.getV_settleName()); // 结算账号
						json.put("settleNo", register.getV_settleCardNo()); // 结算卡号
						json.put("settleMobile", register.getV_settlePhone()); // 结算卡手机号
						json.put("mcc", pmsBusinessPos.getBusinessnum()); // MCC码
						json.put("legalName", register.getV_legalName()); // 法人姓名
						json.put("legalContact", register.getV_legalContact()); // 法人手机号
						json.put("legalId", register.getV_legalId()); // 法人证件号
						json.put("idStartTime", register.getV_idStartTime()); // 身份证生效日期
						json.put("idEndTime", register.getV_idEndTime()); // 身份证失效日期
						json.put("licenseNo", register.getV_licenseNo()); // 营业执照号
						json.put("licenseName", register.getV_licenseName()); // 营业执照名
						json.put("licenseStartTime", register.getV_licenseStartTime()); // 营业执照生效日期
						json.put("licenseEndTime", register.getV_licenseEndTime()); // 营业执照失效日期
						json.put("bizDomain", register.getV_bizDomain()); // 经营范围
						json.put("mainBusiness", register.getV_mainBusiness()); // 主营业务
						json.put("taxId", pmsBusinessPos.getBusinessnum()); // 税务登记
						json.put("orgCode", pmsBusinessPos.getBusinessnum()); // 组织机构代码
						json.put("address", pmsBusinessPos.getBusinessnum()); // 注册地址
						json.put("settfee", "0"); // 提现手续费

						JSONObject temp = new JSONObject();
						temp.put("prodType", "000008");// 产品类型
						temp.put("rate", register.getV_rate());// 费率
						temp.put("cRate", register.getV_cRate());// 费率

						json.put("fee", temp);
						MD5Util md5 = new MD5Util();
						String sync = md5.MD5s(json.toString() + pmsBusinessPos.getKek());// MD5完整性验证加密
						json.put("sync", sync);
						condition.put("data", json.toString());
						logger.info("通联上送的数据:" + condition);
						String result = HttpClientUtil.post(httpclient, url, condition);
						logger.info("通联上游返回的数据:" + result);
						JSONObject jsonObject = JSONObject.fromObject(result);
						Iterator it = jsonObject.keys();
						// Map<String, String> map = new HashMap<>();
						while (it.hasNext()) {

							String key = (String) it.next();
							if (key.equals("data")) {
								Map maps = (Map) JSON.parse(jsonObject.getString(key));

								if ("success".equals(maps.get("status"))) {

									PmsWeixinMerchartInfo weixin = new PmsWeixinMerchartInfo();
									weixin.setSubContractId(maps.get("merNo").toString());
									weixin.setPrivateKey(maps.get("reqKey").toString());
									weixin.setDfSubContractId(maps.get("dfmerNo").toString());
									weixin.setAppId(maps.get("dfcompanyNo").toString());
									weixin.setDfPrivateKey(maps.get("dfreqKey").toString());
									weixin.setRateCode("123");
									weixin.setStatus(1);
									weixin.setAccount(register.getV_settlePhone());
									weixinService.updateByPrimaryKeySelective(weixin);
									retMap.put("v_merNo", maps.get("merNo").toString());
									retMap.put("v_reqKey", maps.get("reqKey").toString());
									retMap.put("v_dfmerNo", maps.get("dfmerNo").toString());
									retMap.put("v_dfreqKey", maps.get("dfreqKey").toString());
									retMap.put("v_code", "00");
									retMap.put("v_msg", "注册成功");
								}
								if ("商户报件：法人身份证号重复".equals(maps.get("msg")) || "商户报件：商户简称已存在".equals(maps.get("msg"))
										|| "商户报件：商户全称已存在".equals(maps.get("msg"))
										|| "渠道-商户信息重复".equals(maps.get("msg"))) {
									retMap.put("v_code", "20");
									retMap.put("v_msg", "已注册：" + maps.get("msg"));
								}

							}

						}
					} else {
						logger.info("费率低于成本费率：" + merchantinfo.getMercId());
						return setResp("12", "费率低于成本费率");
					}

				} else {
					// 请求参数为空
					logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					retMap.put("v_code", "16");
					retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
					return retMap;
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}

		}
		return retMap;
	}

	public Map<String, String> tieCardHandle(PayRequestEntity originalinfo) throws Exception {
		logger.info("请求参数：" + JSON.toJSONString(originalinfo));
		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = originalinfo.getV_mid();
		// 金额
		String card = originalinfo.getV_cardNo();
		// 商户订单号
		logger.info("******************根据卡号查询");

		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setCertNo(originalinfo.getV_cert_no());
		orig.setSumCode("1");

		if (originalDao.selectByCode(orig) != null) {
			logger.info("绑卡重复");
			return setResp("21", "绑卡重复");
		}
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getV_oid());// 原始数据的订单编号
		original.setOrderId(originalinfo.getV_oid()); // 为主键
		original.setPid(originalinfo.getV_mid());
		original.setTieCardPageUrl(originalinfo.getV_url());
		original.setTieCardUrl(originalinfo.getV_notify_url());
		original.setBankNo(originalinfo.getV_cardNo());
		original.setRealName(originalinfo.getV_realName());
		original.setCertNo(originalinfo.getV_cert_no());
		original.setPhone(originalinfo.getV_phone());
		original.setCvn2(originalinfo.getV_cvn2());
		original.setProcdutDesc(originalinfo.getV_orderInfo());
		if (originalinfo.getV_attach() != null) {
			original.setAttach(originalinfo.getV_attach());
		}
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getV_mid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询商户路由
		PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());// 获取上游商户号和秘钥
		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				logger.info("是正式商户");
				// 根据手机号查询进件商户
				PmsWeixinMerchartInfo wx = weixinService.selectByPrimaryKey(originalinfo.getV_phone());
				if (wx != null) {
					Map<String, String> condition = new HashMap<String, String>();
					DefaultHttpClient httpclient = new DefaultHttpClient();

					String url = "http://sittrx.daxtech.com.cn/middlepaytrx/gateway/openUnionPay";// 微信主扫

					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssms");
					String sendTime = sf.format(new Date());
					JSONObject json = new JSONObject();
					json.put("accNo", originalinfo.getV_cardNo());
					json.put("body", originalinfo.getV_orderInfo());
					json.put("merNo", wx.getSubContractId());// 商编号
					json.put("phone", originalinfo.getV_phone());//
					json.put("cvn2", originalinfo.getV_cvn2());//
					json.put("callBackUrl",
							"http://60.28.24.164:8102/app_posp/loanStill/ls/tieCard/returnUrl.action?orderId="
									+ originalinfo.getV_oid());//
					json.put("serverCallBackUrl",
							"http://60.28.24.164:8102/app_posp/loanStill/ls/tieCard/notifyUrl.action");//
					json.put("orderNo", originalinfo.getV_oid());
					json.put("organizationId", pmsBusinessPos.getBusinessnum());// 代理商编号
					json.put("trxType", "DH");//
					json.put("version", "1.0.0");

					String sign = MakeMacUtil.md5MakeMac(json.toString(), wx.getPrivateKey());

					json.put("sign", sign);// 报文鉴别码
					condition.put("params", json.toString());
					logger.info("通联上送的参数:" + condition.toString());
					SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ms");
					String sendTime1 = sf1.format(new Date());
					logger.info("通联上送的时间" + sendTime1 + condition);
					String payrequest_result = HttpUtil.postAndReturnString(condition, url);
					logger.info("通联返回的结果" + payrequest_result);
					JSONObject jsonObject = JSONObject.fromObject(payrequest_result);
					Iterator it = jsonObject.keys();
					while (it.hasNext()) {

						String key = (String) it.next();
						if (key.equals("retCode")) {
							if ("00".equals(jsonObject.getString(key))) {
								while (it.hasNext()) {
									String keys = (String) it.next();
									if (keys.equals("info")) {

										Map maps = (Map) JSON.parse(jsonObject.getString(keys));
										retMap.put("html", maps.get("html").toString());
										retMap.put("v_code", "00");
										retMap.put("v_msg", "成功");

									}

								}
							} else {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
								return retMap;
							}
						}

					}
				} else {
					// 请求参数为空
					logger.info("该商户没有注册过，请重新注册" + merchantinfo.getMercId());
					retMap.put("v_code", "22");
					retMap.put("v_msg", "该商户没有注册过，请重新注册");
					return retMap;
				}

			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			// 请求参数为空
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
			return retMap;
		}

		return retMap;
	}

	public Map<String, String> payHandle(PayRequestEntity originalinfo) throws Exception {

		logger.info("代还支付请求参数：" + JSON.toJSONString(originalinfo));
		Map<String, String> retMap = new HashMap();

		String merchId = originalinfo.getV_mid();

		String acount = originalinfo.getV_txnAmt();

		logger.info("******************根据商户号查询################");

		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getV_mid());
		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setOrderId(originalinfo.getV_oid());
		original.setOrderAmount(originalinfo.getV_txnAmt());
		original.setPageUrl(originalinfo.getV_url());
		original.setPid(originalinfo.getV_mid());
		original.setBgUrl(originalinfo.getV_notify_url());
		original.setAttach(originalinfo.getV_attach());

		originalDao.insert(original);

		String mercId = originalinfo.getV_mid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		String oAgentNo = "";

		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if ((merchantList.size() != 0) && (!merchantList.isEmpty())) {
			merchantinfo = (PmsMerchantInfo) merchantList.get(0);

			oAgentNo = merchantinfo.getoAgentNo();
			if (StringUtils.isBlank(oAgentNo)) {
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			if ("60".equals(merchantinfo.getMercSts())) {
				logger.info("是正式商户");

				OriginalOrderInfo ori = originalDao.getOriginalOrderInfoByUserId(originalinfo.getV_userId());

				if (ori != null) {
					// 根据手机号查询进件商户
					PmsWeixinMerchartInfo wx = weixinService.selectByPrimaryKey(ori.getPhone());
					if (wx != null) {
						String factAmount = ""
								+ new BigDecimal(originalinfo.getV_txnAmt()).multiply(new BigDecimal(100));

						PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());

						ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent(
								(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
						if (!payCheckResult.getErrCode().equals("0")) {
							logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
									+ PaymentCodeEnum.moBaoQuickPay.getTypeCode());
							return setResp("05", "欧单金额限制，请重试或联系客服");
						}
						ResultInfo resultInfoForOAgentNo = iPublicTradeVerifyService
								.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
								logger.error("交易关闭，请重试或联系客服");
								return setResp("06", "交易关闭，请重试或联系客服");
							}
							return setResp("07", "系统异常，请重试或联系客服");
						}
						ResultInfo payCheckResult3 = iPublicTradeVerifyService
								.moduelVerifyMer(TradeTypeEnum.merchantCollect, mercId);
						if (!payCheckResult3.getErrCode().equals("0")) {
							logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
									+ PaymentCodeEnum.moBaoQuickPay.getTypeCode());
							return setResp("08", "商户模块限制,请重试或联系客服");
						}
						Map<String, String> paramMap = new HashMap();
						paramMap.put("mercid", merchantinfo.getMercId());
						paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
						paramMap.put("oAgentNo", oAgentNo);

						Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);
						if ((resultMap == null) || (resultMap.size() == 0)) {
							logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
						String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
						String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
						String paymentAmount = factAmount;
						if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
							logger.info("交易金额大于最打金额");
							return setResp("10", "金额超过最大交易金额");
						}
						if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
							logger.info("交易金额小于最小金额");
							return setResp("11", "交易金额小于最小金额");
						}
						PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

						pmsAppTransInfo.setoAgentNo(oAgentNo);
						pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
						pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());

						pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
						pmsAppTransInfo.setMercid(merchantinfo.getMercId());
						pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());

						pmsAppTransInfo.setOrderid(originalinfo.getV_oid());
						pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
						pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
						BigDecimal factBigDecimal = new BigDecimal(factAmount);
						BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
						pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
						pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());
						pmsAppTransInfo.setDrawMoneyType("1");
						pmsAppTransInfo.setSettlementState("D0");
						Integer insertAppTrans = Integer.valueOf(pmsAppTransInfoDao.insert(pmsAppTransInfo));
						if (insertAppTrans.intValue() == 1) {
							pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

							String quickRateType = ((String) resultMap.get("QUICKRATETYPE")).toString();

							AppRateConfig appRate = new AppRateConfig();
							appRate.setRateType(quickRateType);
							appRate.setoAgentNo(oAgentNo);
							AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
							if (appRateConfig == null) {
								logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
								return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
							}
							String isTop = appRateConfig.getIsTop();
							String rate = appRateConfig.getRate();
							String topPoundage = appRateConfig.getTopPoundage();
							paymentAmount = pmsAppTransInfo.getFactamount();
							String minPoundageStr = appRateConfig.getBottomPoundage();
							Double minPoundage = Double.valueOf(0.0D);
							if ((StringUtils.isNotBlank(appRateConfig.getIsBottom()))
									&& (appRateConfig.getIsBottom().equals("1"))) {
								if (StringUtils.isNotBlank(minPoundageStr)) {
									minPoundage = Double.valueOf(Double.parseDouble(minPoundageStr));
								} else {
									logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
									return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
								}
							}
							BigDecimal payAmount = new BigDecimal("0");
							BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());

							BigDecimal fee = new BigDecimal(0);
							String rateStr = wx.getCreditRate();
							fee = new BigDecimal(rateStr).multiply(dfactAmount).add(new BigDecimal(minPoundage));
							payAmount = dfactAmount.subtract(fee);
							logger.info("清算金额:" + paymentAmount);
							if (payAmount.doubleValue() < 0.0D) {
								payAmount = new BigDecimal(0.0D);
							}
							pmsAppTransInfo.setPayamount(payAmount.toString());
							pmsAppTransInfo.setRate(rateStr);
							pmsAppTransInfo.setPoundage(fee.toString());
							pmsAppTransInfo.setDrawMoneyType("1");

							Integer paymentAmountInt = Integer.valueOf((int) Double.parseDouble(paymentAmount));

							payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt.intValue(),
									TradeTypeEnum.merchantCollect, PaymentCodeEnum.moBaoQuickPay, oAgentNo,
									merchantinfo.getMercId());
							if (!payCheckResult.getErrCode().equals("0")) {
								logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.moBaoQuickPay.getTypeCode());
								return setResp("13", "暂不支持该交易方式");
							}
							// 查看当前交易是否已经生成了流水表
							PospTransInfo pospTransInfo = null;
							// 流水表是否需要更新的标记 0 insert，1：update
							int insertOrUpdateFlag = 0;
							// 生成上送流水号
							if ((pospTransInfo = pospTransInfoDAO
									.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
								// 已经存在，修改流水号，设置pospsn为空
								logger.info("订单号：" + pmsAppTransInfo.getOrderid());
								pospTransInfo.setTransOrderId(originalinfo.getV_oid());
								pospTransInfo.setResponsecode("99");
								pospTransInfo.setPospsn("");
								insertOrUpdateFlag = 1;
							} else {
								// 不存在流水，生成一个流水
								pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);

								System.out.println("流水表生成的时间:" + pospTransInfo.getSenddate());
								// 设置上送流水号
								pospTransInfo.setTransOrderId(originalinfo.getV_oid());
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
							logger.info("修改订单信息");
							logger.info(pmsAppTransInfo);

							int num = pmsAppTransInfoDao.update(pmsAppTransInfo);
							if (num > 0) {
								logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
								switch (pmsBusinessPos.getChannelnum()) {
								case "TL":
									logger.info("################通联代还支付开始处理#################");
									Map<String, String> condition = new HashMap<String, String>();
									DefaultHttpClient httpclient = new DefaultHttpClient();
									String url = "http://sittrx.daxtech.com.cn/middlepaytrx/gateway/DHPay";// 微信主扫
									SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssms");
									String sendTime = sf.format(new Date());
									JSONObject json = new JSONObject();
									json.put("trxType", "DH");//
									json.put("merNo", wx.getSubContractId());// 商编号
									json.put("orderNo", originalinfo.getV_oid());
									Integer number = (int) (Double.parseDouble(originalinfo.getV_txnAmt()) * 100);
									json.put("amount", number.toString());
									json.put("body", ori.getProcdutDesc());
									json.put("frontUrl",
											"http://60.28.24.164:8102/app_posp/loanStill/ls/pay/returnUrl.action");// 页面通知地址
									json.put("notifyUrl",
											"http://60.28.24.164:8102/app_posp/loanStill/ls/pay/notifyUrl.action");// 后台通知地址
									json.put("accNo", ori.getBankNo());
									json.put("organizationId", pmsBusinessPos.getBusinessnum());// 代理商编号
									json.put("idCardNo", ori.getCertNo());//
									json.put("payerName", ori.getRealName());//
									json.put("payerPhone", ori.getPhone());//
									json.put("province", "天津市");//
									json.put("city", "天津市");//
									json.put("version", "1.0");
									String sign = MakeMacUtil.md5MakeMac(json.toString(), wx.getPrivateKey());
									json.put("sign", sign);// 报文鉴别码
									condition.put("params", json.toString());
									logger.info("condition:" + condition.toString());
									SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ms");
									String sendTime1 = sf1.format(new Date());
									logger.info("请求时间>>>" + sendTime1 + condition);
									String payrequest_result = HttpUtil.postAndReturnString(condition, url);
									logger.info("响应信息:" + payrequest_result);
									JSONObject jsonObject = JSONObject.fromObject(payrequest_result);
									Iterator it = jsonObject.keys();
									// Map<String, String> map = new HashMap<>();
									while (it.hasNext()) {
										String key = (String) it.next();
										if (key.equals("retCode")) {
											if ("00".equals(jsonObject.getString(key))) {
												retMap.put("v_mid", originalinfo.getV_mid());
												retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
												retMap.put("v_oid", originalinfo.getV_oid());
												retMap.put("v_code", "00");
												retMap.put("v_msg", "请求成功");
											} else {
												retMap.put("v_code", "15");
												retMap.put("v_msg", "请求失败");
												return retMap;
											}
										}
									}

									break;
								default:
									break;
								}
							}
						}
					} else {
						// 请求参数为空
						logger.info("该商户没有注册过，请重新注册" + merchantinfo.getMercId());
						retMap.put("v_code", "22");
						retMap.put("v_msg", "该商户没有注册过，请重新注册");
						return retMap;
					}

				} else {
					// 请求参数为空
					logger.info("该商户没有绑卡，请重新绑卡" + merchantinfo.getMercId());
					retMap.put("v_code", "22");
					retMap.put("v_msg", "该商户没有绑卡，请重新绑卡");
					return retMap;
				}

			} else {
				this.logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			this.logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;

	}

	/**
	 * 
	 * @Description 设置响应信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
	}

	public void otherInvoke(OriginalOrderInfo originalOrderInfo) throws Exception {
		// TODO Auto-generated method stub
		originalDao.update(originalOrderInfo);
	}

	public void otherInvokes(String OrderId, String status) throws Exception {
		this.logger.info("上游返回的订单号" + OrderId);
		this.logger.info("上游返回的状态码" + status);

		String transOrderId = OrderId;

		PospTransInfo pospTransInfo = this.pospTransInfoDAO.searchBytransOrderId(transOrderId);
		this.logger.info("流水表信息" + pospTransInfo);

		PmsAppTransInfo pmsAppTransInfo = this.pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		this.logger.info("订单表信息" + pmsAppTransInfo);
		if ("0000".equals(status)) {
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());

			int updateAppTrans = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				this.logger.info(pmsAppTransInfo);

				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(OrderId);
				this.logger.info("更新流水");
				this.logger.info(pospTransInfo);
				this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("1001".equals(status)) {
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());

			int updateAppTrans = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(OrderId);
				this.logger.info("更新流水");
				this.logger.info(pospTransInfo);
				this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}

}
