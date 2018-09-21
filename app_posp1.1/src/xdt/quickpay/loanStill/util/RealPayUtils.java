package xdt.quickpay.loanStill.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 下游处理工具类
 */
public class RealPayUtils {

	/**
	 * 默认报文处理字符集UTF-8
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * socket报文数据域长度
	 */
	public static final int LENGTH_FIELD_LENGTH = 2;
	public static final int SERVICE_CODE_FIELD_LENGTH = 4;
	public static final int COMPANY_ID_LENGTH = 7;
	public static final int MERCHANT_CODE_LENGTH = 15;
	public static final int MD5_FIELD_LENGTH = 32;
	public static final int SET_TYPE = 1;

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes(Charset.forName("UTF-8"));
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
			return new String(str).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static int count(String str) {
		/** 中文字符 */
		int chCharacter = 0;

		/** 英文字符 */
		int enCharacter = 0;

		/** 空格 */
		int spaceCharacter = 0;

		/** 数字 */
		int numberCharacter = 0;

		/** 其他字符 */
		int otherCharacter = 0;

		/***
		 * 统计字符串中中文，英文，数字，空格等字符个数
		 * 
		 * @param str
		 *            需要统计的字符串
		 */
		if (null == str || str.equals("")) {
			// System.out.println("字符串为空");
			return 0;
		}

		for (int i = 0; i < str.length(); i++) {
			char tmp = str.charAt(i);
			if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
				enCharacter++;
			} else if ((tmp >= '0') && (tmp <= '9')) {
				numberCharacter++;
			} else if (tmp == ' ') {
				spaceCharacter++;
			} else if (isChinese(tmp)) {
				chCharacter++;
			} else {
				otherCharacter++;
			}
		}
		/*
		 * System.out.println("字符串:" + str + ""); System.out.println("中文字符有:" +
		 * chCharacter); System.out.println("英文字符有:" + enCharacter);
		 * System.out.println("数字有:" + numberCharacter);
		 * System.out.println("空格有:" + spaceCharacter);
		 * System.out.println("其他字符有:" + otherCharacter);
		 */
		// System.out.println("中文字符有:" + chCharacter);
		return chCharacter;
	}

	private static boolean isChinese(char ch) {
		// 获取此字符的UniCodeBlock
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
		// GENERAL_PUNCTUATION 判断中文的“号
		// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
		// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			// System.out.println(ch + " 是中文");
			return true;
		}
		return false;
	}
	

}
