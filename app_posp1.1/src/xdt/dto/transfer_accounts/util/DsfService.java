package xdt.dto.transfer_accounts.util;

import com.epaylinks.common.Tool;
import com.epaylinks.common.WsHelper;
import com.epaylinks.common.XMLUtil;
import com.epaylinks.sdk.javabean.dsf.Transfer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DsfService {
	public Map<String, Object> batchPayToEpay(DsfData dsfData, List<Transfer> trans) throws Exception {
		System.out.println("===代收付批量转账到易票联===");
		dsfData.setRequestHeaderParam("tranCode", "8004");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		dsfData.setRequestBodyParam("tradeDate", Tool.dateToString(new Date(), "yyyyMMdd"));
		dsfData.setRequestBodyParam("payWay", "0");
		dsfData.setRequestBodyParam("payType", "0");
		dsfData.setRequestBodyParam("payPass",
				Tool.desEncrypt((String) dsfData.getRequestBodyParam("payPass"), dsfData.getTerminalPwd()));
		StringBuffer sb = new StringBuffer();
		Iterator var5 = trans.iterator();

		while (var5.hasNext()) {
			Transfer transfer = (Transfer) var5.next();
			sb.append(XMLUtil.SortedMapToXml("tran", transfer.getParams()));
		}

		dsfData.setRequestBodyParam("trans", sb.toString());
		String xml = dsfData.toXml("batchPayTo");
		String sign = dsfData.makeSign(xml);
		System.out.println("签名：" + sign);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("batchPayTo");
		System.out.println("发送报文：" + xml);
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("batchPayTo", new Object[]{xml});
		System.out.println("接受报文：" + responseXml);
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> queryDetail(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8004");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("queryDetail");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("queryDetail");
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("queryDetail", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> signUp(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8017");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("bindBankAcc");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("bindBankAcc");
		System.out.println(xml);
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("bindBankAcc", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> daiShou(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8000");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("epayDaiShou");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("epayDaiShou");
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("daiShou", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> signConfirm(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8018");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("signConfirm");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("signConfirm");
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("signConfirm", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> signUpQuery(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8020");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("qrySignStatus");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("qrySignStatus");
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("qrySignStatus", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> queryBankCardInfo(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8020");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("bankCardQueryReq");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("bankCardQueryReq");
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("queryBankCardInfo", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> callWs(DsfData dsfData, String tranCode, String root, String methodName)
			throws Exception {
		dsfData.setRequestHeaderParam("tranCode", tranCode);
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml(root);
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml(root);
		System.out.println("发送请求：" + xml);
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs(methodName, new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> bankAccSignUp(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8017");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("bankAccSignUp");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("bankAccSignUp");
		System.out.println(xml);
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("bankAccSignUp", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}

	public Map<String, Object> signUpConfirm(DsfData dsfData) throws Exception {
		dsfData.setRequestHeaderParam("tranCode", "8018");
		dsfData.setRequestHeaderParam("termNo", dsfData.getTermNo());
		dsfData.setRequestHeaderParam("merNo", dsfData.getPartner());
		dsfData.setRequestHeaderParam("reqTime", Tool.dateToString(new Date(), "yyyyMMddHHmmss"));
		String xml = dsfData.toXml("signConfirm");
		String sign = dsfData.makeSign(xml);
		dsfData.setRequestHeaderParam("sign", sign);
		xml = dsfData.toXml("signConfirm");
		WsHelper helper = new WsHelper(dsfData.getWsdl(), dsfData.getNameSpace());
		String responseXml = helper.callWs("signUpConfirm", new Object[]{xml});
		dsfData.fromXml(responseXml);
		Map<String, Object> map = new HashMap();
		map.put("header", dsfData.getResponseHeaderParams());
		map.put("dataBody", dsfData.getResponseBodyParams());
		return map;
	}
}