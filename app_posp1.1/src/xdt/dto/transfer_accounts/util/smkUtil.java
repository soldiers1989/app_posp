package xdt.dto.transfer_accounts.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class smkUtil {
	public static final Logger logger=Logger.getLogger(smkUtil.class);	
	
	/**
	 * @Title: getSignMsg
	 * @Description: 获取签名字符串
	 * @param map
	 * @return
	 */
	public static String getSignMsg(Map<String, Object> map) {
		List<String> signFields = getSignFields((String) map.get("tradeCode"));
		if (signFields.size() < 1) {
			return null;
		}
		StringBuffer signMsg = new StringBuffer();
		for (String field : signFields) {
			if (signMsg.length() == 0) {
				signMsg.append(field).append("=").append(map.get(field) == null ? "" : map.get(field));
			} else {
				signMsg.append("&").append(field).append("=").append(map.get(field) == null ? "" : map.get(field));
			}
		}
		logger.info("签名字符串：" + signMsg);
		return signMsg.toString();
	}

	/**
	 * @Title: getSignFields
	 * @Description: 获取签名字段
	 * @param tradeCode
	 * @return
	 */
	private static List<String> getSignFields(String tradeCode) {
		List<String> resultList = new ArrayList<String>();
		resultList.add("version");
		resultList.add("tradeCode");
		resultList.add("reqSeq");
		resultList.add("merCode");
		resultList.add("channelNo");
		resultList.add("tradeDate");
		resultList.add("tradeTime");
		if ("AP0001".equals(tradeCode)) {
			resultList.add("orderNo");
			resultList.add("busType");
			resultList.add("toibkn");
			resultList.add("actacn");
			resultList.add("toname");
			resultList.add("toaddr");
			resultList.add("tobknm");
			resultList.add("amount");
			resultList.add("currency");
			resultList.add("remark");
		} else if ("AP0003".equals(tradeCode)) {
			resultList.remove("reqSeq");
			resultList.add("orderNo");
			resultList.add("orgSeq");
		} else {
			logger.error("该交易码[" + tradeCode + "]未配置验签字段");
			return new ArrayList<String>();
		}
		return resultList;
	}
	
	
	/**
	 * 签名
	 * 
	 * @param sign
	 * @param strPfx
	 * @param strPassword
	 * @return
	 * @throws Exception
	 */
	public static String sign(String sign, String strPfx, String strPassword) throws Exception {
		// 获取证书
		X509Certificate x509 = (X509Certificate) getCertformPfx(strPfx, strPassword);
		// 构建签名,由证书指定签名算法
		Signature sa = Signature.getInstance(x509.getSigAlgName());
		// 获取私匙
		PrivateKey privateKey = getPvkformPfx(strPfx, strPassword);
		sa.initSign(privateKey);
		sa.update(sign.getBytes("GBK"));
		return Base64.encodeBase64String(sa.sign());
	}
	
	private static Certificate getCertformPfx(String strPfx, String strPassword) {
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			FileInputStream fis = new FileInputStream(strPfx);
			// If the keystore password is empty(""), then we have to set
			// to null, otherwise it won't work!!!
			char[] nPassword = null;
			if ((strPassword == null) || strPassword.trim().equals("")) {
				nPassword = null;
			} else {
				nPassword = strPassword.toCharArray();
			}
			ks.load(fis, nPassword);
			fis.close();
			// Now we loop all the aliases, we need the alias to get keys.
			// It seems that this value is the "Friendly name" field in the
			// detals tab <-- Certificate window <-- view <-- Certificate
			// Button <-- Content tab <-- Internet Options <-- Tools menu
			// In MS IE 6.
			@SuppressWarnings("rawtypes")
			Enumeration enumas = ks.aliases();
			String keyAlias = null;
			if (enumas.hasMoreElements())// we are readin just one certificate.
			{
				keyAlias = (String) enumas.nextElement();
			}
			// Now once we know the alias, we could get the keys.
			Certificate cert = ks.getCertificate(keyAlias);
			return cert;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static PrivateKey getPvkformPfx(String strPfx, String strPassword) {
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			FileInputStream fis = new FileInputStream(strPfx);
			// If the keystore password is empty(""), then we have to set
			// to null, otherwise it won't work!!!
			char[] nPassword = null;
			if ((strPassword == null) || strPassword.trim().equals("")) {
				nPassword = null;
			} else {
				nPassword = strPassword.toCharArray();
			}
			ks.load(fis, nPassword);
			fis.close();
			// Now we loop all the aliases, we need the alias to get keys.
			// It seems that this value is the "Friendly name" field in the
			// detals tab <-- Certificate window <-- view <-- Certificate
			// Button <-- Content tab <-- Internet Options <-- Tools menu
			// In MS IE 6.
			@SuppressWarnings("rawtypes")
			Enumeration enumas = ks.aliases();
			String keyAlias = null;
			if (enumas.hasMoreElements())// we are readin just one certificate.
			{
				keyAlias = (String) enumas.nextElement();
				// System.out.println("alias=[" + keyAlias + "]");
			}
			// Now once we know the alias, we could get the keys.
			PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
			return prikey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * @Title: sendMsgHTTP
	 * @Description: HTTP形式发送报文
	 * @param sendMsg
	 *            发送报文信息 必输
	 * @param sendURL
	 *            外系统URL 必输
	 * @param sendDataType
	 *            非必输 数据类型 （默认 application/json）
	 * @return
	 * @throws IOException
	 */
	public static String sendMsgHTTP(String sendMsg, String sendURL, String sendDataType) {
		URL url = null;
		String result = "";
		if (StringUtils.isEmpty(sendDataType)) {
			sendDataType = "application/json;Charset=UTF-8";
		}
		HttpURLConnection httpConn = null;
		OutputStream output = null;
		OutputStreamWriter outr = null;
		int returnCode = 0;
		try {
			url = new URL(sendURL);
			httpConn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			httpConn.setConnectTimeout(60000);
			httpConn.setReadTimeout(60000);
			httpConn.setDoInput(true); // 设置输入流采用字节流
			httpConn.setDoOutput(true);
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Accept", "application/json;"); // 设置接收数据的格式
			httpConn.setRequestProperty("Content-Type", sendDataType); // 设置发送数据的格式
			httpConn.connect();
			output = httpConn.getOutputStream();
			outr = new OutputStreamWriter(output, "utf-8");
			// 写入请求参数
			outr.write(sendMsg.toCharArray(), 0, sendMsg.length());
			outr.flush();
			outr.close();
			returnCode = httpConn.getResponseCode();

		} catch (ConnectException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		logger.error("调用外系统返回码：" + returnCode);
		if (returnCode == 200) {
			InputStream is = null;
			BufferedReader reader = null;
			try {
				is = httpConn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				// 读取响应内容
				String readLine = "";
				StringBuffer buffer = new StringBuffer();
				while ((readLine = reader.readLine()) != null) {
					if (readLine.length() > 0)
						buffer.append(readLine.trim()).append("\n");
				}
				result = buffer.toString();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
