package xdt.service;

import java.util.Map;

import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.loanStill.entity.PayRequestEntity;
import xdt.quickpay.loanStill.entity.RegisterRequestEntity;

public interface ILoanStillPayService {
	
	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception;

	/**
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;

	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;
	
	/**
	 * 处理代还注册接口
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> lsRegister(RegisterRequestEntity register) throws Exception;
	/**
	 * 处理代还绑卡接口
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> tieCardHandle(PayRequestEntity tieCard) throws Exception;
	/**
	 * 处理代还支付接口
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> payHandle(PayRequestEntity tieCard) throws Exception;
	/**
	 * 绑卡请求返回处理订单状态
	 * 
	 * @param result
	 *            绑卡响应信息
	 * @throws Exception
	 */
	void otherInvoke(OriginalOrderInfo originalOrderInfo) throws Exception;
	
	/**
	 * 支付请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvokes(String orderId, String status) throws Exception;
	
	

}
