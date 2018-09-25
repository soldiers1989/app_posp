package xdt.quickpay.conformityQucikPay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import sun.misc.BASE64Encoder;

public class SecurityUtil {
    public static PublicKey epaylinksPublicKey;
    public static PrivateKey privateKey;
    public static String certId;
    

    public static String getinit(String keyStoreFilePath,String keyStorePassword,String keyPassword,String epaylinksCertPath) {
    	try{
				KeyStore keyStore = SunSecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, BaseSecurity.KEY_STORE_TYPE_PKCS12);
				
				Enumeration<String> aliasSet = keyStore.aliases();
				String alias = null;
				while(aliasSet.hasMoreElements()){
					alias = aliasSet.nextElement();
				}
				
				//加载商户的私钥
				privateKey = SunSecurityUtil.getPrivateKey(keyStore, alias, keyPassword.toCharArray());
				X509Certificate cert = (X509Certificate)SunSecurityUtil.getCertificate(keyStore, alias);
				certId = String.valueOf(cert.getSerialNumber());   
				//加载易票联的公钥
				epaylinksPublicKey = SunSecurityUtil.getPublicKeyByCertFile(epaylinksCertPath);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
    	return certId;
	    }
	
	
	/**
	 * 数字签名，使用SHA256withRSA签名算法
	 * @param signData 待签名数据
	 * @return byte[]返回签名后的字节数组
	 * @throws Exception
	 */
	public static byte[]sign(byte[]signData) throws Exception{
		return xdt.quickpay.conformityQucikPay.signature.SignatureUtil.sign(signData, BaseSecurity.SIGNATURE_ALGORITHM_SHA256withRSA, privateKey);
	}
	
	/**
	 * 数字签名，使用SHA256withRSA签名算法
	 * @param signData 待签名数据
	 * @return String 签名结果以Base64编码格式返回
	 * @throws Exception
	 */
	public static String signForBase64(byte[]signData) throws Exception{
		byte[]sign = sign(signData);
		String result = new BASE64Encoder().encode(sign);
		result = result.replaceAll("[\\s*\t\n\r]", "");
		return result;
	}
	
	

	/**
	 * 数字验签，使用SHA256withRSA签名算法
	 * @param signData 待签名数据
	 * @param sign 签名字符串
	 * @param publicKey 公钥
	 * @return boolean 返回数字验签结果
	 * @throws Exception
	 */
	public static boolean verify(byte[]signData ,byte[]sign) throws Exception{
		return xdt.quickpay.conformityQucikPay.signature.SignatureUtil.verify(signData, BaseSecurity.SIGNATURE_ALGORITHM_SHA256withRSA, epaylinksPublicKey, sign);
	}
	
	
	
}
