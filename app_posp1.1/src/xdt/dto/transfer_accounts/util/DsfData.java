package xdt.dto.transfer_accounts.util;

import com.epaylinks.common.EpayDataException;
import com.epaylinks.common.Parameters;
import com.epaylinks.common.Tool;
import com.epaylinks.common.XMLUtil;
import java.util.SortedMap;
import java.util.TreeMap;

public class DsfData {
	
	
	private String termNo ;
	private String terminalPwd ;
	private String partner;
	private String md5Key;
	private String wsdl;
	private String nameSpace ;
	private SortedMap<String, Object> requestHeaderParams = new TreeMap();
	private SortedMap<String, Object> requestBodyParams = new TreeMap();
	private SortedMap<String, Object> responseHeaderParams = new TreeMap();
	private SortedMap<String, Object> responseBodyParams = new TreeMap();

	
	public DsfData(String termNo, String terminalPwd, String partner, String md5Key, String wsdl, String nameSpace) {
		super();
		this.termNo = termNo;
		this.terminalPwd = terminalPwd;
		this.partner = partner;
		this.md5Key = md5Key;
		this.wsdl = wsdl;
		this.nameSpace = nameSpace;
	}

	public String toXml(String root) {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<").append(root).append(">");
		sb.append(XMLUtil.SortedMapToXml("header", this.getRequestHeaderParams()));
		sb.append(XMLUtil.SortedMapToXml("dataBody", this.getRequestBodyParams()));
		sb.append("</").append(root).append(">");
		return sb.toString();
	}

	public String makeSign(String xml) {
		xml = xml.replaceAll("<sign>[0-9a-zA-Z]*</sign>", "");
		xml = xml + this.md5Key + this.terminalPwd;
		String sign = Tool.MD5(xml);
		return sign;
	}

	/*public static void main(String[] args) {
		DsfData data = new DsfData(md5Key, md5Key, md5Key, md5Key, md5Key, md5Key);
		data.setMd5Key("yuiwbdueu8394939896481kfweievmjf20509");
		data.setTerminalPwd("21218cca77804d2ba1922c33e0151105");
		String sign = data.makeSign(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><bindBankAcc><header><tranCode>8017</tranCode><termNo>36183842</termNo><merNo>EC080820X01</merNo><trackNo>1234561231</trackNo><reqTime>20160315185058</reqTime></header><dataBody><cardNo>5387380000302909</cardNo><signFlag>1</signFlag><notifyUrl>http://127.0.0.1:8080/aaa</notifyUrl></dataBody></bindBankAcc>");
		System.out.println("sign:" + sign);
	}*/

	public void fromXml(String responseXml) throws Exception {
		SortedMap<String, Object> map = XMLUtil.doXMLParse(responseXml);
		String dataBodyXml;
		if (map.containsKey("header")) {
			dataBodyXml = "<header>" + map.get("header") + "</header>";
			this.setResponseHeaderParams(XMLUtil.doXMLParse(dataBodyXml));
		}

		if (map.containsKey("dataBody")) {
			dataBodyXml = "<dataBody>" + map.get("dataBody") + "</dataBody>";
			this.setResponseBodyParams(XMLUtil.doXMLParse(dataBodyXml));
		}

		if ("0000".equals(this.getRequestHeaderParam("respCode"))) {
			this.checkSign(responseXml);
		}

	}

	public boolean checkSign(String responseXml) {
		if (!this.getResponseHeaderParams().containsKey("sign")) {
			throw new EpayDataException("收到报文中，签名不存在");
		} else if (this.makeSign(responseXml).equals(this.getResponseHeaderParam("sign"))) {
			return true;
		} else {
			throw new EpayDataException("签名验证错误");
		}
	}

	public String getTermNo() {
		return this.termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public String getTerminalPwd() {
		return this.terminalPwd;
	}

	public void setTerminalPwd(String terminalPwd) {
		this.terminalPwd = terminalPwd;
	}

	public String getPartner() {
		return this.partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getMd5Key() {
		return this.md5Key;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

	public String getWsdl() {
		return this.wsdl;
	}

	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}

	public String getNameSpace() {
		return this.nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public SortedMap<String, Object> getRequestHeaderParams() {
		return this.requestHeaderParams;
	}

	public void setRequestHeaderParams(SortedMap<String, Object> requestHeaderParams) {
		this.requestHeaderParams = requestHeaderParams;
	}

	public SortedMap<String, Object> getRequestBodyParams() {
		return this.requestBodyParams;
	}

	public void setRequestBodyParams(SortedMap<String, Object> requestBodyParams) {
		this.requestBodyParams = requestBodyParams;
	}

	public SortedMap<String, Object> getResponseHeaderParams() {
		return this.responseHeaderParams;
	}

	public void setResponseHeaderParams(SortedMap<String, Object> responseHeaderParams) {
		this.responseHeaderParams = responseHeaderParams;
	}

	public SortedMap<String, Object> getResponseBodyParams() {
		return this.responseBodyParams;
	}

	public void setResponseBodyParams(SortedMap<String, Object> responseBodyParams) {
		this.responseBodyParams = responseBodyParams;
	}

	public void setRequestHeaderParam(String key, Object value) {
		this.requestHeaderParams.put(key, value);
	}

	public Object getRequestHeaderParam(String key) {
		Object ret = "";
		if (this.requestHeaderParams.containsKey(key)) {
			ret = this.requestHeaderParams.get(key);
		}

		return ret;
	}

	public void setRequestBodyParam(String key, Object value) {
		this.requestBodyParams.put(key, value);
	}

	public Object getRequestBodyParam(String key) {
		Object ret = "";
		if (this.requestBodyParams.containsKey(key)) {
			ret = this.requestBodyParams.get(key);
		}

		return ret;
	}

	public void setResponseHeaderParam(String key, Object value) {
		this.responseHeaderParams.put(key, value);
	}

	public Object getResponseHeaderParam(String key) {
		Object ret = "";
		if (this.responseHeaderParams.containsKey(key)) {
			ret = this.responseHeaderParams.get(key);
		}

		return ret;
	}

	public void setResponseBodyParam(String key, Object value) {
		this.responseBodyParams.put(key, value);
	}

	public Object getResponseBodyParam(String key) {
		Object ret = "";
		if (this.responseBodyParams.containsKey(key)) {
			ret = this.responseBodyParams.get(key);
		}

		return ret;
	}
}