package xdt.quickpay.conformityQucikPay.signature;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;

import xdt.quickpay.conformityQucikPay.base.BaseSecurity;
import xdt.quickpay.conformityQucikPay.base.ExtendSecurityUtil;
import xdt.quickpay.conformityQucikPay.base.SunSecurityUtil;


/**
 * 数字签名工具类，该工具类用来对签名数据进行加签以及对签名数据进行验签
 * java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
 * @author xuguolong
 * @date 20160901
 */
public class SignatureUtil {	
	
	/**
	 * 数字签名，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm 签名算法
	 * @param keystore 密钥库对象
	 * @param keyPassword 私钥密码
	 * @return byte[]数字签名后的结果
	 * @throws Exception
	 */
	public static byte[] sign(byte[] signData, String algorithm ,KeyStore keystore ,String keyPassword) throws Exception{
		Enumeration<String> aliasSet = keystore.aliases();
		String alias = null;
		while(aliasSet.hasMoreElements()){
			alias = aliasSet.nextElement();
		}
		return sign(signData ,algorithm ,keystore ,alias ,keyPassword);
	}
	
	
	/**
	 * 数字签名，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm 签名算法
	 * @param keystore 密钥库对象
	 * @param alias 私钥在密钥库中的存储别名
	 * @param keyPassword 私钥密码
	 * @return byte[]数字签名后的结果
	 * @throws Exception
	 */
    public static byte[] sign(byte[] signData, String algorithm ,KeyStore keystore ,String alias ,String keyPassword) throws Exception{
		PrivateKey privateKey = SunSecurityUtil.getPrivateKey(keystore ,alias ,keyPassword.toCharArray());
		return sign(signData ,algorithm ,privateKey);
	}
    
    
    
    
    /**
	 * 数字签名，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm 签名算法
	 * @param privateKey 私钥
	 * @return byte[]数字签名后的结果
	 * @throws Exception
	 */
    public static byte[]sign(byte[] signData, String algorithm ,PrivateKey privateKey) throws Exception{
    	if(BaseSecurity.SIGNATURE_ALGORITHM_MD2withRSA.equals(algorithm)
				||BaseSecurity.SIGNATURE_ALGORITHM_MD5withRSA.equals(algorithm)
				||BaseSecurity.SIGNATURE_ALGORITHM_SHA1withRSA.equals(algorithm)
			    ||BaseSecurity.SIGNATURE_ALGORITHM_SHA256withRSA.equals(algorithm)
			    ||BaseSecurity.SIGNATURE_ALGORITHM_SHA1withDSA.equals(algorithm)){
			return SunSecurityUtil.sign(signData ,algorithm ,privateKey);
		}
		
		return ExtendSecurityUtil.sign(signData, algorithm, privateKey);
    }
    
    
	
	
    
    /**
     * 数字签名，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
     * @param signData  待签名数据
     * @param algorithm 签名算法
     * @param keyStoreFilePath 密钥管理库库文件路径
     * @param keyStorePassword 密钥管理库管理密码
     * @param keyStoreType 密钥管理库类型，仅支持Java本身支持的密钥库类型，JKS, JCEKS, PKCS12三种类型的密钥库
     * @param keyPassword 私钥密码
     * @return
     * @throws Exception
     */
	public static byte[] sign(byte[]signData ,
			String algorithm ,
			String keyStoreFilePath ,
			String keyStorePassword ,
			String keyStoreType ,
			String keyPassword) throws Exception{
		KeyStore keyStore = SunSecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, keyStoreType);
		return sign(signData ,algorithm ,keyStore ,keyPassword);
	}
	
	
	/**
     * 数字签名，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
     * @param signData  待签名数据
     * @param algorithm 签名算法
     * @param keyStoreFilePath 密钥管理库库文件路径
     * @param keyStorePassword 密钥管理库管理密码
     * @param keyStoreType 密钥管理库类型，仅支持Java本身支持的密钥库类型，JKS, JCEKS, PKCS12三种类型的密钥库
     * @param alias 私钥在密钥库中的存储别名
     * @param keyPassword 私钥密码
     * @return
     * @throws Exception
     */
	public static byte[] sign(byte[]signData ,
			String algorithm ,
			String keyStoreFilePath ,
			String keyStorePassword ,
			String keyStoreType ,
			String alias ,
			String keyPassword) throws Exception{
		KeyStore keyStore = SunSecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, keyStoreType);
		return sign(signData ,algorithm ,keyStore ,alias ,keyPassword);
	}
	
	
	/**
	 * 签名验证，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm  签名算法
	 * @param certFilePath 公钥证书文件路径
	 * @param sign 待验证的签名
	 * @return boolean 签名验证结果
	 * @throws Exception
	 */
	public static boolean verifyByCertFile(byte[]signData ,String algorithm ,String certFilePath ,byte[]sign) throws Exception{
		PublicKey publicKey = SunSecurityUtil.getPublicKeyByCertFile(certFilePath);
		return verify(signData, algorithm, publicKey, sign);
	}
	
	
	
	/**
	 * 签名验证，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm  签名算法
	 * @param certString 公钥证书字符串
	 * @param sign 待验证的签名
	 * @return boolean 签名验证结果
	 * @throws Exception
	 */
	public static boolean verifyByCertByte(byte[]signData ,String algorithm ,byte[]cert ,byte[]sign) throws Exception{
		PublicKey publicKey = SunSecurityUtil.generatePublicKeyByCertByte(cert);
		return verify(signData, algorithm, publicKey, sign);
	}
	
	
	/**
	 * 签名验证，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm  签名算法
     * @param keyStoreFilePath 密钥管理库库文件路径
     * @param keyStorePassword 密钥管理库管理密码
     * @param keyStoreType 密钥管理库类型，仅支持Java本身支持的密钥库类型，JKS, JCEKS, PKCS12三种类型的密钥库
	 * @param sign 待验证的签名
	 * @return boolean 签名验证结果
	 * @throws Exception
	 */
	public static boolean verifyByKeyStore(byte[]signData ,
			String algorithm ,
			String keyStoreFilePath ,
			String keyStorePassword ,
			String keyStoreType ,
			String alias ,
			byte[]sign) throws Exception{
		KeyStore keystore = SunSecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, keyStoreType);
		PublicKey publicKey = SunSecurityUtil.getPublicKey(keystore, alias);
		return verify(signData, algorithm, publicKey, sign);
	}
	
	
	
	
	/**
	 * 签名验证，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm  签名算法
     * @param Base64X509String 公钥证书的Base64编码X509字符串 
	 * @param sign 待验证的签名
	 * @return boolean 签名验证结果
	 * @throws Exception
	 */
	public static boolean verifyByBase64X509(byte[]signData , String algorithm , String Base64X509String , byte[]sign) throws Exception{
		PublicKey publicKey = SunSecurityUtil.generatePublicKeyByX509Base64String(Base64X509String);
		return verify(signData, algorithm, publicKey, sign);
	}
	
	
	
	
	
	/**
	 * 签名验证，java 6仅支持MD2withRSA\MD5withRSA\SHA1withRSA\SHA256withRSA\SHA1withDSA，当使用其他算法，如SHA224withRSA时，需要使用第三方开发包BouncyCastle
	 * @param signData 待签名数据
	 * @param algorithm  签名算法
     * @param publicKey  公钥
	 * @param sign 待验证的签名
	 * @return boolean 签名验证结果
	 * @throws Exception
	 */
	public static boolean verify(byte[]signData , String algorithm , PublicKey publicKey, byte[]sign) throws Exception{
		if(BaseSecurity.SIGNATURE_ALGORITHM_MD2withRSA.equals(algorithm)
				||BaseSecurity.SIGNATURE_ALGORITHM_MD5withRSA.equals(algorithm)
				||BaseSecurity.SIGNATURE_ALGORITHM_SHA1withRSA.equals(algorithm)
			    ||BaseSecurity.SIGNATURE_ALGORITHM_SHA256withRSA.equals(algorithm)
			    ||BaseSecurity.SIGNATURE_ALGORITHM_SHA1withDSA.equals(algorithm)){
			return SunSecurityUtil.verify(signData, algorithm, publicKey, sign);
		}
		return ExtendSecurityUtil.verify(signData, algorithm, publicKey, sign);
	}
	

	
	
	/*public static void main(String[] args) throws Exception{
		//Security.addProvider(new BouncyCastleProvider());
		
		String keyStorePath = "D:\\certs\\yzt.pfx";
		String certFilePath = "D:\\certs\\yzt_pfx.cer";
		String storePassword = "yzt20160728";
		String privatePassword = "yzt20160728";
		
		String signData = "hello ，中国！";
		
		//签名
		byte[]signByteData = sign(signData.getBytes(), "SHA256withRSA" , keyStorePath, storePassword, BaseSecurity.KEY_STORE_TYPE_PKCS12, privatePassword);
		
		System.out.println(signByteData.length);
		System.out.println("数字签名结果:" + HexUtil.bytes2HexString(signByteData));
		
		
		
		boolean result = verifyByCertFile(signData.getBytes(), "SHA256withRSA", certFilePath, signByteData);
		String certString = readCertBase64String("D:\\Users\\Administrator\\Desktop\\dddd.cer");
		System.out.println(certString);
		byte[]certByte = new BASE64Decoder().decodeBuffer(certString);
		result = verifyByCertByte(signData.getBytes(), "SHA256withRSA", certByte, signByteData);
		System.out.println("验证签名结果：" + result);
		
		
		
		
		
		// 实例化密钥对生成器
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器
		keyPairGen.initialize(2048);
		// 生成密钥对
		KeyPair keyPair = keyPairGen.generateKeyPair();
		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		
		System.out.println(">>>>>>>>>>>>"+new BASE64Encoder().encode(publicKey.getEncoded()));;
		
		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 封装密钥
		Map<String, Object> keyMap = new HashMap<String, Object>();
		
		
		
	}
	*/
}
