package xdt.quickpay.loanStill.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;



@SuppressWarnings({"unused"})
public class MakeMacUtil {


	/**
	 * 计算macKey值
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static String makeMac(String json, String macKey) {
		Map<String, String> contentData = (Map<String, String>) Tools.parserToMap(json);
		String macStr = "";
		Object[] key_arr = contentData.keySet().toArray();
		Arrays.sort(key_arr);
		for (Object key : key_arr) {
			Object value = contentData.get(key);
			if (value != null) {
				if (!key.equals("mac")) {
					macStr += value.toString();
				}
			}
		}
		System.out.println("macStr"+macStr);
		String rMac = DESUtil.mac(macStr, macKey);
		return rMac;
	}
	
	 public static String md5MakeMac(String json, String macKey) throws Exception{
	    	Map<String, String> contentData = (Map<String, String>) Tools.parserToMap(json);
			String macStr="";
			Object[] key_arr = contentData.keySet().toArray();
			Arrays.sort(key_arr);
			for (Object key : key_arr) {
				Object value = contentData.get(key);
				if (value != null ) {
					if (!key.equals("sign")) {
						macStr+= value.toString();
					}
				}
			}
			System.out.println("macStr"+macStr);
			String rMac = MD5Util.mac(macStr, macKey);
			return rMac;
	    }

}
