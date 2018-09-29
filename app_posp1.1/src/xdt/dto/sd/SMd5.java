package xdt.dto.sd;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SMd5 {
	
	public static byte[] md5(byte[] md5Src) {
		byte[] ret = null;
		try {
			// Log.print("md5Src len: " + md5Src.length);
			// Log.print("md5Src: " + StringUtil.bytes2Hex(md5Src));
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(md5Src);
			ret = md.digest();
		} catch (NoSuchAlgorithmException e) {
		}
		return ret;
	}

	public static boolean md5Verify(byte[] md5Src, byte[] md5) {
		byte[] newMd5 = md5(md5Src);
		return MessageDigest.isEqual(newMd5, md5);
	}
	
	/**
	 * 
	 * @param md5Src 字符串
	 * @return		 Hex字符串
	 */
	public static String md5(String md5Src){
		try {
			byte[] bMd5 = md5(md5Src.getBytes("UTF-8"));
			return ByteUtil.bytes2HexStr(bMd5);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
	
	/**
	 * 
	 * @param md5Src 字符串
	 * @param md5    Hex字符串
	 * @return
	 */
	public static boolean md5Verify(String md5Src, String md5) {
		boolean ret = false;
		try {
			ret = md5Verify(md5Src.getBytes("UTF-8"),ByteUtil.hexStr2Bytes(md5));
		} catch (Exception e) {
			
		}
		return ret;
	}
	
	
	
	public static void main(String[] args) {
		//String payData = "{name:\"张三\",age:99}";
		String payData = "{\"payUrl\":\"https://cash.yeepay.com/cashier/std?sign=U5vn7Bk0zjyP2MhekYLQnX9YQvlHpmBd9I8VnGkvPD_ywTVVj_KdYSa_VovaWTMkkGavnVFqDZtXW1Lv6D7-7o41roFUzox54DB1T_YoT4HZlhR_9gqudF3x14uO8omIjO70y7c2ULNae6rcHS-_pf9in4pWxFfOvNFsQi9yG2sZUK2RI3ECF5TNfRMxt4FoEX90fXV7lmYs5EWAqZdzbAkO0lGAqJxGdqzVIuztuzLUo5eDyJWjVAtk5qIp6Jo0rYtBZ4GYp2XEDLOwSKdwh-bt824ZqtFVZKgmRuM0DZ7G9dszTbh0WiYYdOa9YT6NQ-bi679YpUvEiaMP1ItaNA$SHA256&merchantNo=10018820932&token=2AF55EC948163B8542B188C93E941CCF6CD305B5C77F424005859EDA4637AACC&timestamp=1523602535222&directPayType=&cardType=&userNo=1&userType=USER_ID&ext=null\",\"payHtml\":\"<html>\\r\\n<head>\\r\\n<meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" />\\r\\n<title>pay order</title>\\r\\n</head>\\r\\n<body>\\r\\n<p/><strong>clusterId: 100</strong>\\r\\n<p/><strong>页面直接跳转</strong>\\r\\n<p/><p/><p/><p/><form action=\\\"https://cash.yeepay.com/cashier/std?sign=U5vn7Bk0zjyP2MhekYLQnX9YQvlHpmBd9I8VnGkvPD_ywTVVj_KdYSa_VovaWTMkkGavnVFqDZtXW1Lv6D7-7o41roFUzox54DB1T_YoT4HZlhR_9gqudF3x14uO8omIjO70y7c2ULNae6rcHS-_pf9in4pWxFfOvNFsQi9yG2sZUK2RI3ECF5TNfRMxt4FoEX90fXV7lmYs5EWAqZdzbAkO0lGAqJxGdqzVIuztuzLUo5eDyJWjVAtk5qIp6Jo0rYtBZ4GYp2XEDLOwSKdwh-bt824ZqtFVZKgmRuM0DZ7G9dszTbh0WiYYdOa9YT6NQ-bi679YpUvEiaMP1ItaNA$SHA256&merchantNo=10018820932&token=2AF55EC948163B8542B188C93E941CCF6CD305B5C77F424005859EDA4637AACC&timestamp=1523602535222&directPayType=&cardType=&userNo=1&userType=USER_ID&ext=null\\\" method=\\\"post\\\">\\r\\n\\r\\n<input type=\\\"submit\\\" value=\\\"submit\\\">\\r\\n</form>\\r\\n</body>\\r\\n</html>\"}";
		String key = "12345678-9388-11de-b73f-0f19974d6b20";
		String md5Src = payData + "&" + key;  //md5源串
		try {
			byte[] bPaySign = md5(md5Src.getBytes("UTF-8"));
			String paySign = ByteUtil.bytes2HexStr(bPaySign);  //字节转字符,如0x1234AB -> “1234AB”

			System.out.println("md5Src: " + md5Src);
			System.out.println("paySign: " + paySign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
