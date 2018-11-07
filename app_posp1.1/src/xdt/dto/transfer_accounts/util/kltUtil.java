package xdt.dto.transfer_accounts.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import com.alibaba.fastjson.JSONObject;

public class kltUtil {
    /**
     * @param orignStr 签名原串
     * @param md5Key   商户md5Key
     * @return 加签串
     */
    public static String addSign(String orignStr, String md5Key) {
        JSONObject jsonObject = JSONObject.parseObject(orignStr);
        JSONObject headJson = jsonObject.getJSONObject("head");
        JSONObject contentJson = jsonObject.getJSONObject("content");

        //放入TreeMap后,按照Key值自然排序！
        Map signMap = new TreeMap();
        //报文头
        for (Map.Entry entry : headJson.entrySet()) {
            signMap.put(entry.getKey(), entry.getValue());
        }
        //报文体
        for (Map.Entry entry : contentJson.entrySet()) {
            signMap.put(entry.getKey(), entry.getValue());
        }

        StringBuilder stringBuilder = new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = signMap.entrySet();
        for (Map.Entry entry : entrySet) {
            if (entry.getValue() != null && !((String) entry.getValue()).trim().equals(""))
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        //删除最后一个&号
        String signOrgin = stringBuilder.substring(0, stringBuilder.length() - 1);
        //签名值
        String tSign = getSign(signOrgin, md5Key);
        //添加签名
        jsonObject.getJSONObject("head").put("sign", tSign);
        return jsonObject.toJSONString();
    }
    

    public static String getSign(String string, String posKey) {

        String result = string;
        // key是双方约定的加密秘钥
        result += "&key=" + posKey;
        result = MD5Encode(result).toUpperCase();
        return result;
    }
    
    /**
     * MD5编码
     *
     * @param origin 原始字符串
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
    
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
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
     * @param b 要转换的byte
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
    

    /**
     * 配置忽略SSL认证
     *
     * @param clientBuilder
     */
    public static void configureHttpClient(HttpClientBuilder clientBuilder) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
                return true;
            }
        }).build();
        //NoopHostNameVerifer 接受任何有效的SSL会话来匹配目标主机
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        clientBuilder.setSSLSocketFactory(sslsf);
    }

    /**
     * @param url         请求地址
     * @param reqeust     请求参数
     * @param isVerifySSL 是否开启SSl认证
     * @return
     * @throws IOException
     */
    public static String sendHttpPostRequest(String url, String reqeust, boolean isVerifySSL) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (!isVerifySSL)
            configureHttpClient(httpClientBuilder);
        HttpClient httpClient = httpClientBuilder.build();

        StringEntity requestEntity = new StringEntity(reqeust, "utf-8");
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("content-type", "application/json;charset=UTF-8");
        httpPost.addHeader("Accept", "application/json");
        httpPost.setEntity(requestEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        InputStream inputStream = httpEntity.getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        StringBuilder stringBuilder = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

}
