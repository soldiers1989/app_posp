package xdt.dto.transfer_accounts.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.util.Signature;
import cfca.sadk.x509.certificate.X509Cert;

public class sqUtil {
	private static final String deviceName = JCrypto.JSOFT_LIB;
	private static cfca.sadk.lib.crypto.Session session = null;
	
	static {
		try {
			JCrypto jCrypto = JCrypto.getInstance();
			jCrypto.initialize(deviceName, null);
			session = jCrypto.openSession(deviceName);
		} catch (PKIException e) {
			e.printStackTrace();
		}
	}
		
	
	/******* p1 *********/
	/**
	 * p1消息签名
	 * @param 待签名消息：message
	 * @return
	 * @throws Exception
	 */
	public static String signMessageByP1(String message, String pfxPath, String passWord) throws Exception{
		PrivateKey userPriKey = KeyUtil.getPrivateKeyFromPFX(new FileInputStream(pfxPath), passWord);
		Signature signature = new Signature();
		byte[] base64P7SignedData = signature.p1SignMessage(Mechanism.SHA256_RSA, message.getBytes("UTF-8"), userPriKey, session);
		return new String(base64P7SignedData);
	}
	
	/**
	 * p1消息校验(公钥证书验签)
	 * @param 签名原文：beforeSignedData
	 * @param base64签名结果：afterSignedData
	 * @param 公钥证书路径:certPath
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyMessageByP1(String beforeSignedData, String afterSignedData, String certPath) throws Exception{
		X509Cert cert = new X509Cert(new FileInputStream(certPath));
		PublicKey publicKey = cert.getPublicKey();
		Signature signature = new Signature();
		return signature.p1VerifyMessage(Mechanism.SHA256_RSA, beforeSignedData.getBytes("UTF-8"), afterSignedData.getBytes("UTF-8"), publicKey, session);
	}
	
	/**
	 * p1消息校验(公钥字符串验签)
	 * @param 签名原文：beforeSignedData
	 * @param base64签名结果：afterSignedData
	 * @param 公钥字符串:publicKeyStr
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyMessageByP1AndPubKey(String beforeSignedData, String afterSignedData, String publicKeyStr) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(publicKeyStr));  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
		Signature signature = new Signature();
		return signature.p1VerifyMessage(Mechanism.SHA256_RSA, beforeSignedData.getBytes("UTF-8"), afterSignedData.getBytes("UTF-8"), publicKey, session);
	}
	
/*	*//********* RSA_PKCS ***********//*
	*//**
	 *  RSA证书加密消息
	 *  RSA_PKCS公钥加密
	 * @param 待加密数据：message
	 * @throws UnsupportedEncodingException 
	 *//*
	public static String encryptMessageByRSA_PKCS(String message, String certPath) throws Exception{
		X509Cert cert = new X509Cert(new FileInputStream(certPath));
		PublicKey userPubKey = cert.getPublicKey();
		Mechanism mechanism = new Mechanism(Mechanism.RSA_PKCS);
		byte[] encryptedData = EncryptUtil.encrypt(mechanism, userPubKey, message.getBytes("UTF-8"), session);
		return new String(encryptedData);
	}

	*//**
	 * RSA证书解密消息
	 * RSA_PKCS私钥解密
	 * @param 指定算法配对的私钥对象:key
	 * @param 待解密数据：message
	 * @throws Exception 
	 *//*
	public static String decryptMessageByRSA_PKCS(String message, String pfxPath, String passWord) throws Exception{
		PrivateKey userPriKey = KeyUtil.getPrivateKeyFromPFX(new FileInputStream(pfxPath), passWord);
		Mechanism mechanism = new Mechanism(Mechanism.RSA_PKCS);
		byte[] dataBytes = message.getBytes("UTF-8");
		byte[] encryptedData = EncryptUtil.decrypt(mechanism, userPriKey, dataBytes, session);
		return new String(encryptedData);
	}
	*/
}
