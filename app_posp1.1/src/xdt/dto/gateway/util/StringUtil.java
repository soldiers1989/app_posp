package xdt.dto.gateway.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import java.security.KeyFactory;
/**
 * 字符串工具类
 */
public class StringUtil extends StringUtils {
	 /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
     * 签名方法
     * @param strPfx   私钥文件地址
     * @param message  参与签名的字符串
     * @return
     */
    public static String ApiSign(String strPfx,String message) {

        PrivateKey privateKey = GetPvkformPfx(strPfx,"");
        String signStr = "";
        if(privateKey!=null){
            byte[] privateKeyBytes = privateKey.getEncoded();
            String privateKeyStrs = Base64.encodeBase64String(privateKeyBytes);
            System.out.println("开始签名...");
            //现将报文md5后，在签名
            try {
                String md5Str = md5(message);
                System.out.println("原串 = "+ message);
                System.out.println("md5后 = " + md5Str);
                signStr = sign(md5Str,privateKeyStrs);
                System.out.println("签名串 = " + signStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("商户上行报文服务器证书签名成功!");
        }

        return signStr;
    }
    
    /**
     * 获得私钥
     * @param strPfx        私钥文件地址
     * @param strPassword
     * @return
     */
    public static PrivateKey GetPvkformPfx(String strPfx, String strPassword){
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(strPfx);
            ks.load(fis, strPassword.toCharArray());
            fis.close();
            Enumeration enumas = ks.aliases();
            String keyAlias = null;
            if (enumas.hasMoreElements())// we are readin just one certificate.
            {
                keyAlias = (String)enumas.nextElement();
            }
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, strPassword.toCharArray());
            return prikey;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * md5加密
     * @param message
     * @return
     */
    public static String md5(String message) {
        try {
            return DigestUtils.md5Hex(message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes());
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
