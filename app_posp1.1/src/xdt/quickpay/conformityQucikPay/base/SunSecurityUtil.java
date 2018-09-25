package xdt.quickpay.conformityQucikPay.base;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;




/**
 * 安全加密之基础公共工具类，该类不依赖于其他非sun实现的第三方类库,该类库仅支持jdk1.6支持的数字签名算法，其他签名算法的封装实现，可调用ExtendSecurityUtil类的相关方法
 * @author xuguolong
 * @date 20160816
 * @version 1.0.0
 */
public class SunSecurityUtil {
	
	/**
	 * 读取密钥库文件，并返回对应的密钥库对象KeyStore
	 * @param keyStoreFilePath   密钥库文件路径
	 * @param keyStorePassword   密钥库的管理密码
	 * @param keyStoreType       密钥库类型，仅支持Java本身支持的密钥库类型，JKS, JCEKS, PKCS12三种类型的密钥库
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String keyStoreFilePath ,String keyStorePassword ,String keyStoreType) throws Exception{
		if(!BaseSecurity.KEY_STORE_TYPE_JKS.equals(keyStoreType)
				&&!BaseSecurity.KEY_STORE_TYPE_JCEKS.equals(keyStoreType)
				&&!BaseSecurity.KEY_STORE_TYPE_PKCS12.equals(keyStoreType)){
			throw new Exception("不支持的keystore type");
		}
	    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
	    keyStore.load(new FileInputStream(keyStoreFilePath), keyStorePassword.toCharArray());
	    return keyStore;
	}
	
	
	/**
	 * 从密钥管理库中获取私钥对象
	 * @param keystore  密钥库对象
	 * @param alias 私钥在密钥管理库中的存储别名
	 * @param password 私钥密码
	 * @return PrivateKey 私钥对象实例
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(KeyStore keystore ,String alias ,char[]password) throws Exception{
		PrivateKey privateKey =(PrivateKey)keystore.getKey(alias, password);
		return privateKey;
	}
	
	
	
	/**
	 * 从密钥管理库中获取公钥
	 * @param keystore 密钥管理库对象
	 * @param alias 公钥在密钥管理中的存储别名
	 * @return PublicKey返回公钥对象
	 */
	public static PublicKey getPublicKey(KeyStore keystore ,String alias) throws Exception{
		Certificate certificate = keystore.getCertificate(alias);
		return certificate.getPublicKey();
	}
	
	
	/**
	 * 从公钥证书文件中读取公钥
	 * @param certFilePath 公钥证书文件路径，如：d:\\certs\\test.cer
	 * @return PublicKey返回公钥对象
	 * @throws Exception
	 */
	public static PublicKey getPublicKeyByCertFile(String certFilePath) throws Exception{
		FileInputStream in  = new FileInputStream(certFilePath);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(in);
        PublicKey publicKey = cert.getPublicKey();
        return publicKey;
	}
	
	
	
	/**
	 * 根据公钥字节信息生成公钥对象
	 * @param cert 公钥字节数组，即publicKey.getEncoded()获得的字节数组
	 * 一般来说，保存时候，通过Base64.encoded(publicKey.getEncoded())，在使用的时候，在解码使用，
	 * 即通过Base64.encoded(publicKeyBase64String)即可得到PublicKey的公钥字节数组
	 * 
	 * @return PublicKey返回公钥对象
	 */
	public static PublicKey generatePublicKeyByCertByte(byte[] cert) throws Exception{
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cert); 
		return keyfactory.generatePublic(keySpec);
	}
	
	
	
	 /**
     * 根据公钥的 Base64 X.509格式的字符串来生成公钥对象
     * 公钥的 Base64编码X.509格式的字符串，即从浏览器导出的.cer证书文件中的内容。
     * 例如：
     * -----BEGIN CERTIFICATE-----
     * MIIEHjCCAwagAwIBAgIUOtZmoN484tQsbqkW5EhMYC/gikUwDQYJKoZIhvcNAQEFBQAwgZYxCzAJBgNVBAYTAkNOMTkwNwYDVQQKDDDljJfkuqzlpKnlqIHor5rkv6HnlLXlrZDllYbliqHmnI3liqHmnInpmZDlhazlj7gxFTATBgNVBAsMDOS8geS4muivgeS5pjE1MDMGA1UEAwws5aSp5aiB6K+a5L+h5pWw5a2X6K6k6K+B5Lit5b+D5LyB5Lia6K+B5LmmQ0EwHhcNMTYwODA3MTA0NzA0WhcNMTcwODA3MTA0NzA0WjB1MScwJQYDVQQKDB7lpKnlqIHor5rkv6HmlbDlrZforqTor4HkuK3lv4MxGDAWBgNVBAsMD+eUteWtkOWVhuWKoemDqDEwMC4GA1UEAwwn5bm/5bee5LiA5oyH6YCa5L+h5oGv56eR5oqA5pyJ6ZmQ5YWs5Y+4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZ4styCNSPROFEoThM8ry2+RhLxTA3B9wkbFBlXWlKNX/4tJpOo0H8yEv1fYZPUVc39PCPGbQ3YTU90jDUxmaBlLyvfS4/Zfb3hoPUArVKjUDjO3rI7Om1UF+wLkapKeyLgsZZJ+cO8tHfvb+rqlrFLtWYZuUOgj/jTNNq08yogFomhgW36xSWJGFAMzArebPf6/hOAgQyxUJc5lMqPHs/X/GzAQKugGMMwEUP9Or4JjiGdemsfRO4PoY5Ybya3uXWW46qCZa8YPjFDJf5daAs67A26YEvzHOr03Whpre345GJnkh0aj9ottwvyrPI+hEsCgz8gFiT9pSGGqmubXNQIDAQABo4GDMIGAMAkGA1UdEwQCMAAwCwYDVR0PBAQDAgWgMGYGA1UdHwRfMF0wW6BZoFeGVWh0dHA6Ly90b3BjYS5pdHJ1cy5jb20uY24vcHVibGljL2l0cnVzY3JsP0NBPTdFMURDOEQ0QjA5OEQ2RTNBRkMzMUFBQjA0MjY3REExNUFBRTdCNUUwDQYJKoZIhvcNAQEFBQADggEBACsEQ3wulcsUjd6GDw7dINCAO3qfsVNXlUXe7l6iUgNet2ry41+lsz4arsdi8D0n4eRygHtNbNSEYTxyP2Ah+uWhSuhv8O2dWeHsm78bmSeF7Oy3QQQvL/Rer+/7Zv6qq2DkGIwisXJ5cc7LkALI9XrEu+0wZ9MmFcWR6TTwty3F9UlGw8CdJnB4HGWSsFOEVM0R68v9mFX3yZELiv4WEqwdxvaAep12QLOKJ8t+I0AFmxyJUhTZB+Clzu3jNg8anOyJwSnhMJNEEh+q7E1e+LgH5CLnP/sx5o5xlNbIyIcXZaF42KBY5heuQZVgS66ugColythiiYsYeX8Uycxie1M=
     * ----END CERTIFICATE-----
     * 
     * @param Base64X509String 公钥的 Base64编码X.509格式的字符串
     * @return
     * @throws Exception
     */
    public static PublicKey generatePublicKeyByX509Base64String(String Base64X509String) throws Exception{
        byte[]buffer = Base64X509String.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(in);
        PublicKey key = cert.getPublicKey();
        return key;
    }  
    
    
    
    
    
    /**
	 * 从密钥管理库中获取获取证书对象
	 * @param keystore 密钥管理库对象
	 * @param alias 公钥在密钥管理中的存储别名
	 * @return Certificate返回证书对象
	 */
	public static Certificate getCertificate(KeyStore keystore ,String alias) throws Exception{
		Certificate certificate = keystore.getCertificate(alias);
		return certificate;
	}
    
    
    
    /**
	 * 从公钥证书文件中获取证书对象
	 * @param certFilePath 公钥证书文件路径，如：d:\\certs\\test.cer
	 * @return Certificate返回证书对象
	 * @throws Exception
	 */
	public static Certificate getCertificateByCertFile(String certFilePath) throws Exception{
		FileInputStream in  = new FileInputStream(certFilePath);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(in);
        return cert;
	}
	
	
	
	
	
	/**
     * 根据公钥的 Base64 X.509格式的字符串来获取证书对象
     * 公钥的 Base64编码X.509格式的字符串，即从浏览器导出的.cer公钥证书文件中的内容。
     * 例如：
     * -----BEGIN CERTIFICATE-----
     * MIIEHjCCAwagAwIBAgIUOtZmoN484tQsbqkW5EhMYC/gikUwDQYJKoZIhvcNAQEFBQAwgZYxCzAJBgNVBAYTAkNOMTkwNwYDVQQKDDDljJfkuqzlpKnlqIHor5rkv6HnlLXlrZDllYbliqHmnI3liqHmnInpmZDlhazlj7gxFTATBgNVBAsMDOS8geS4muivgeS5pjE1MDMGA1UEAwws5aSp5aiB6K+a5L+h5pWw5a2X6K6k6K+B5Lit5b+D5LyB5Lia6K+B5LmmQ0EwHhcNMTYwODA3MTA0NzA0WhcNMTcwODA3MTA0NzA0WjB1MScwJQYDVQQKDB7lpKnlqIHor5rkv6HmlbDlrZforqTor4HkuK3lv4MxGDAWBgNVBAsMD+eUteWtkOWVhuWKoemDqDEwMC4GA1UEAwwn5bm/5bee5LiA5oyH6YCa5L+h5oGv56eR5oqA5pyJ6ZmQ5YWs5Y+4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZ4styCNSPROFEoThM8ry2+RhLxTA3B9wkbFBlXWlKNX/4tJpOo0H8yEv1fYZPUVc39PCPGbQ3YTU90jDUxmaBlLyvfS4/Zfb3hoPUArVKjUDjO3rI7Om1UF+wLkapKeyLgsZZJ+cO8tHfvb+rqlrFLtWYZuUOgj/jTNNq08yogFomhgW36xSWJGFAMzArebPf6/hOAgQyxUJc5lMqPHs/X/GzAQKugGMMwEUP9Or4JjiGdemsfRO4PoY5Ybya3uXWW46qCZa8YPjFDJf5daAs67A26YEvzHOr03Whpre345GJnkh0aj9ottwvyrPI+hEsCgz8gFiT9pSGGqmubXNQIDAQABo4GDMIGAMAkGA1UdEwQCMAAwCwYDVR0PBAQDAgWgMGYGA1UdHwRfMF0wW6BZoFeGVWh0dHA6Ly90b3BjYS5pdHJ1cy5jb20uY24vcHVibGljL2l0cnVzY3JsP0NBPTdFMURDOEQ0QjA5OEQ2RTNBRkMzMUFBQjA0MjY3REExNUFBRTdCNUUwDQYJKoZIhvcNAQEFBQADggEBACsEQ3wulcsUjd6GDw7dINCAO3qfsVNXlUXe7l6iUgNet2ry41+lsz4arsdi8D0n4eRygHtNbNSEYTxyP2Ah+uWhSuhv8O2dWeHsm78bmSeF7Oy3QQQvL/Rer+/7Zv6qq2DkGIwisXJ5cc7LkALI9XrEu+0wZ9MmFcWR6TTwty3F9UlGw8CdJnB4HGWSsFOEVM0R68v9mFX3yZELiv4WEqwdxvaAep12QLOKJ8t+I0AFmxyJUhTZB+Clzu3jNg8anOyJwSnhMJNEEh+q7E1e+LgH5CLnP/sx5o5xlNbIyIcXZaF42KBY5heuQZVgS66ugColythiiYsYeX8Uycxie1M=
     * ----END CERTIFICATE-----
     * 
     * @param Base64X509String 公钥的 Base64编码X.509格式的字符串
     * @return
     * @throws Exception
     */
    public static Certificate generateCertificateByX509Base64String(String Base64X509String) throws Exception{
        byte[]buffer = Base64X509String.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(in);
        return cert;
    }  
	
	
	

	/**
	 * 数字签名
	 * @param signData  待签名数据
	 * @param algorithm 签名算法，这里仅支持由Java实现的一些签名算法，MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA
	 * @param privateKey 私钥对象实例
	 * @return byte[]数字签名结果
	 * @throws Exception
	 */
	public static byte[] sign(byte[] signData, String algorithm ,PrivateKey privateKey) throws Exception{
		
		if(!BaseSecurity.SIGNATURE_ALGORITHM_MD2withRSA.equals(algorithm)
				&&!BaseSecurity.SIGNATURE_ALGORITHM_MD5withRSA.equals(algorithm)
				&&!BaseSecurity.SIGNATURE_ALGORITHM_SHA1withRSA.equals(algorithm)
			    &&!BaseSecurity.SIGNATURE_ALGORITHM_SHA256withRSA.equals(algorithm)
			&&!BaseSecurity.SIGNATURE_ALGORITHM_SHA1withDSA.equals(algorithm)){
			throw new Exception("不支持的signature algorithm");
		}
		
		Signature signature = Signature.getInstance(algorithm);
    	signature.initSign(privateKey);
    	signature.update(signData);
		return signature.sign();
	}
	
	
	/**
	 * 数字签名验证
	 * @param signData 待签名数据
	 * @param algorithm 签名算法，这里仅支持由Java实现的一些签名算法，MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA
	 * @param publicKey 公钥对象
	 * @param sign 待校验的签名
	 * @return boolean返回签名验证结果 true或false
	 * @throws Exception
	 */
	public static boolean verify(byte[]signData ,String algorithm ,PublicKey publicKey ,byte[] sign) throws Exception{
		if(!BaseSecurity.SIGNATURE_ALGORITHM_MD2withRSA.equals(algorithm)
				&&!BaseSecurity.SIGNATURE_ALGORITHM_MD5withRSA.equals(algorithm)
				&&!BaseSecurity.SIGNATURE_ALGORITHM_SHA1withRSA.equals(algorithm)
			    &&!BaseSecurity.SIGNATURE_ALGORITHM_SHA256withRSA.equals(algorithm)
			&&!BaseSecurity.SIGNATURE_ALGORITHM_SHA1withDSA.equals(algorithm)){
			throw new Exception("不支持的signature algorithm");
		}
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(signData);
		return signature.verify(sign);
	}
	
	
	
	public static void main(String[] args) throws Exception{
		/*String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQWiDVZ7XYxa4CQsZoB3n7bfxLDkeGKjyQPt2FUtm4TWX9OYrd523iw6UUqnQ+Evfw88JgRnhyXadp+vnPKP7unormYQAfsM/CxzrfMoVdtwSiGtIJB4pfyRXjA+KL8nIa2hdQy5nLfgPVGZN4WidfUY/QpkddCVXnZ4bAUaQjXQIDAQAB";
		
		byte[]cert = new BASE64Decoder().decodeBuffer(pubKey);
		System.out.println(cert.length);
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cert); 
		PublicKey pk = keyfactory.generatePublic(keySpec);*/
		
		
		
//		String signData = "base64_memo=aaa&certId=335902764374016746163578510918738695929360058949&currency_type=RMB&is_raw=1&notify_url=http://v2.iyangtao-hk.com/api.php/api/plugin/execute/_plugin/Epay/_controller/Callback/_action/pay_notify&out_trade_no=IYT20180507174838634&partner=130&pay_id=wxpay&return_url=http://v2.iyangtao-hk.com/api.php/api/plugin/execute/_plugin/Epay/_controller/Callback/_action/pay_return&sign_type=SHA256withRSA&store_oi_type=0&sub_appid=wx92fec07768afb005&sub_openid=oQD3Xw4ZqJhhyGaT4yeioJaz8zi0&submission_type=00&total_fee=123.00&version=4.0";
//		String algorithm = "SHA256withRSA";
//		PublicKey publicKey = getPublicKeyByCertFile("D:\\certs\\yzt_pfx.cer");
//		String sign = "r0FvxQf5XTLVIq9bohy46o07aA56VuK7ozCwvjo2AC58u9mlXjIaIg08+78LJMGxie90iILSnB3qDu7SNg/tVs98r//g3dM33HH4x9yJFpxCx8IAs0bU9fIs6vhtDpGZ8Frxj+noKhFjmTzITyR4tqdSy613CY0ByWKSkzOWpF+Y+ZnfsED3Vxkzuc6/yw4QjhrETqhsQv+fO/Wjk3UwobGCZBJEq7+bZJDKWUL3K0KXHtANfGDJRw4cZe6fZQBw/OOwdBZP+R5m1+kkwNmIOrNTTCHRR3h5gBHTB0ZE1TQlWIIMhoIVGMa5w25sPhrc41nZBpSytr4lhwkqmxOazg==";
//		boolean is = SunSecurityUtil.verify(signData,algorithm,publicKey,sign);
//		
//		System.out.println(is);
		
		
		
		/*String pubKey ="-----BEGIN CERTIFICATE-----\nMIIEHjCCAwagAwIBAgIUOtZmoN484tQsbqkW5EhMYC/gikUwDQYJKoZIhvcNAQEFBQAwgZYxCzAJBgNVBAYTAkNOMTkwNwYDVQQKDDDljJfkuqzlpKnlqIHor5rkv6HnlLXlrZDllYbliqHmnI3liqHmnInpmZDlhazlj7gxFTATBgNVBAsMDOS8geS4muivgeS5pjE1MDMGA1UEAwws5aSp5aiB6K+a5L+h5pWw5a2X6K6k6K+B5Lit5b+D5LyB5Lia6K+B5LmmQ0EwHhcNMTYwODA3MTA0NzA0WhcNMTcwODA3MTA0NzA0WjB1MScwJQYDVQQKDB7lpKnlqIHor5rkv6HmlbDlrZforqTor4HkuK3lv4MxGDAWBgNVBAsMD+eUteWtkOWVhuWKoemDqDEwMC4GA1UEAwwn5bm/5bee5LiA5oyH6YCa5L+h5oGv56eR5oqA5pyJ6ZmQ5YWs5Y+4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZ4styCNSPROFEoThM8ry2+RhLxTA3B9wkbFBlXWlKNX/4tJpOo0H8yEv1fYZPUVc39PCPGbQ3YTU90jDUxmaBlLyvfS4/Zfb3hoPUArVKjUDjO3rI7Om1UF+wLkapKeyLgsZZJ+cO8tHfvb+rqlrFLtWYZuUOgj/jTNNq08yogFomhgW36xSWJGFAMzArebPf6/hOAgQyxUJc5lMqPHs/X/GzAQKugGMMwEUP9Or4JjiGdemsfRO4PoY5Ybya3uXWW46qCZa8YPjFDJf5daAs67A26YEvzHOr03Whpre345GJnkh0aj9ottwvyrPI+hEsCgz8gFiT9pSGGqmubXNQIDAQABo4GDMIGAMAkGA1UdEwQCMAAwCwYDVR0PBAQDAgWgMGYGA1UdHwRfMF0wW6BZoFeGVWh0dHA6Ly90b3BjYS5pdHJ1cy5jb20uY24vcHVibGljL2l0cnVzY3JsP0NBPTdFMURDOEQ0QjA5OEQ2RTNBRkMzMUFBQjA0MjY3REExNUFBRTdCNUUwDQYJKoZIhvcNAQEFBQADggEBACsEQ3wulcsUjd6GDw7dINCAO3qfsVNXlUXe7l6iUgNet2ry41+lsz4arsdi8D0n4eRygHtNbNSEYTxyP2Ah+uWhSuhv8O2dWeHsm78bmSeF7Oy3QQQvL/Rer+/7Zv6qq2DkGIwisXJ5cc7LkALI9XrEu+0wZ9MmFcWR6TTwty3F9UlGw8CdJnB4HGWSsFOEVM0R68v9mFX3yZELiv4WEqwdxvaAep12QLOKJ8t+I0AFmxyJUhTZB+Clzu3jNg8anOyJwSnhMJNEEh+q7E1e+LgH5CLnP/sx5o5xlNbIyIcXZaF42KBY5heuQZVgS66ugColythiiYsYeX8Uycxie1M=\n-----END CERTIFICATE-----\n";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new ByteArrayInputStream(pubKey.getBytes()));
        
        X509Certificate xcert = (X509Certificate)cert;
        System.out.println(xcert.getNotAfter());
        System.out.println(xcert.getIssuerDN());
		
		X509Certificate cert = (X509Certificate)getCertificateByCertFile("f:\\yzt.cer");
		System.out.println("证书序列号1：" + cert.getSerialNumber());
		System.out.println(cert.getIssuerDN());
		System.out.println(cert.getSerialNumber());
		System.out.println(cert.getSubjectDN());
		
		KeyStore keystore = getKeyStore("D:\\certs\\yzt.pfx", "yzt20160728", BaseSecurity.KEY_STORE_TYPE_PKCS12);
		X509Certificate cert2 = (X509Certificate)getCertificate(keystore, "99915746-bc50-4307-8168-85caf4094d65");
		System.out.println("证书序列号2：" + cert2.getSerialNumber());*/
        
	}
	

}
