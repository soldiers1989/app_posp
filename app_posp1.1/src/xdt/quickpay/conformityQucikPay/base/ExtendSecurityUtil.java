package xdt.quickpay.conformityQucikPay.base;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ExtendSecurityUtil {
	
	static{
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 读取密钥库文件，并返回对应的密钥库对象KeyStore
	 * @param keyStoreFilePath   密钥库文件路径
	 * @param keyStorePassword   密钥库的管理密码
	 * @param keyStoreType       密钥库类型，仅支持Java本身支持的密钥库类型，JKS, JCEKS, PKCS12三种类型的密钥库
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String keyStoreFilePath ,String keyStorePassword ,String keyStoreType) throws Exception{
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
	 * @param alias 公钥证书在密钥管理中的存储别名
	 * @return PublicKey返回公钥对象
	 */
	public static PublicKey getPublicKey(KeyStore keystore ,String alias) throws Exception{
		Certificate certificate = keystore.getCertificate(alias);
		return certificate.getPublicKey();
	}
	
	
	/**
	 * 从公钥证书文件中读取公钥
	 * @param certFilePath 公钥证书文件路径
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
	 * 根据公钥字符串生成公钥对象
	 * @param certString 公钥字符串
	 * @return PublicKey返回公钥对象
	 */
	public static PublicKey generatePublicKeyByCertString(String certString) throws Exception{
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(certString.getBytes()); 
		return keyfactory.generatePublic(keySpec);
	}
	
	
	

	/**
	 * 数字签名
	 * @param signData  待签名数据
	 * @param algorithm 签名算法，这里仅支持由Java实现的一些签名算法，MD2withRSA\MD5withRSA\SHA1withRSA\SHA1withDSA\SHA224withRSA\SHA256withRSA
	 * @param privateKey 私钥对象实例
	 * @return byte[]数字签名结果
	 * @throws Exception
	 */
	public static byte[] sign(byte[] signData, String algorithm ,PrivateKey privateKey) throws Exception{
		Signature signature = Signature.getInstance(algorithm);
    	signature.initSign(privateKey);
    	signature.update(signData);
		return signature.sign();
	}
	
	
	/**
	 * 数字签名验证
	 * @param signData 待签名数据
	 * @param algorithm 签名算法，这里仅支持由Java实现的一些签名算法，MD2withRSA\MD5withRSA\SHA1withRSA\SHA1withDSA\SHA224withRSA\SHA256withRSA
	 * @param publicKey 公钥对象
	 * @param sign 待校验的签名
	 * @return boolean返回签名验证结果 true或false
	 * @throws Exception
	 */
	public static boolean verify(byte[]signData ,String algorithm ,PublicKey publicKey ,byte[] sign) throws Exception{
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(signData);
		return signature.verify(sign);
	}
}
