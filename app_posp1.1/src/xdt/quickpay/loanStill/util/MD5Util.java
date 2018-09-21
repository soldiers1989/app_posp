package xdt.quickpay.loanStill.util;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

/**
 * MD5加密类
 * 
 * @author 
 * @date 2016年11月11日 下午7:20:09
 * @version v1.0
 */
public class MD5Util {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuilder resultSb = new StringBuilder();
		for (byte aB : b) {
			resultSb.append(byteToHexString(aB));
		}
		return resultSb.toString();
	}

	/**
	 * 转换byte到16进制
	 * 
	 * @param b
	 *            要转换的byte
	 * @return 16进制格式
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	public static String mac(String macStr, String mackey) throws Exception {
		String s = MD5Encode(macStr+mackey);
		System.out.println("MD5Encode:"+s);
		BASE64Encoder base64en = new BASE64Encoder();
        String newstr=base64en.encode(s.getBytes("utf-8"));
        System.out.println("BASE64Encoder:"+newstr);
        return newstr;
	}
	/**
	 * MD5编码
	 * 
	 * @param origin
	 *            原始字符串
	 * @return 经过MD5加密之后的结果
	 */
	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}
	 public final static String md5(String paramSrc) {
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	        try {
	            byte[] btInput = paramSrc.getBytes("GBK");
	            // 获得MD5摘要算法的 MessageDigest 对象
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            // 使用指定的字节更新摘要
	            mdInst.update(btInput);
	            // 获得密文
	            byte[] md = mdInst.digest();
	            // 把密文转换成十六进制的字符串形式
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str).toLowerCase();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 /**  
		* @鐢ㄤ簬鑿滃崟: 鎺ュ彛鎶ヤ欢
		* @鎻忚堪: MD5鍔犲瘑 
		* @鍙傛暟: 
		* @杩斿洖: String   
		* @浣滆??: mogu  
		* @鏃ユ湡: 2018骞?5鏈?10鏃? 
		*/ 
		public final static String MD5s(String s) {
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	        try {
	            byte[] btInput = s.getBytes(Charset.forName("UTF-8"));
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            mdInst.update(btInput);
	            byte[] md = mdInst.digest();
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str).toUpperCase();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "";
	        }
	    }
	public static void main(String[] args) {
		
		String str="merchantId=2120180507170850001&merchantUrl=http://60.28.24.164:8102/app_posp/gateWay/ysb_notifyUrl.action&responseMode=2&orderId=1526348798334&currencyType=CNY&amount=100&assuredPay=false&time=20180515094638&remark=担担面&merchantKey=liangshuai123";
		
		String sign=MD5Encode(str);
		
		System.out.println(sign);
		
		String a="ABC";
		System.out.println(a.toLowerCase());
	}

}
