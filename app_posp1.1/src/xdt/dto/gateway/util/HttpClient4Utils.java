package xdt.dto.gateway.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * httpClient4 工具�?
 * 
 * @author: jiangyun
 * @since : 2016-2-17 16:17:20
 */

public class HttpClient4Utils {

	// 设置默认超时时间�?60s
	public static final int DEFAULT_TIME_OUT = 60 * 1000;

	// http请求
	public static String sendHttpRequest(String url, Map<String, Object> paramMap, String charset, boolean isPost) {
		return sendHttpRequest(url, paramMap, charset, isPost, DEFAULT_TIME_OUT);
	}

	// http请求
	public static String sendHttpRequest(String url, Map<String, Object> paramMap, String charset, boolean isPost,
			int timeout) {
		if (isPost) {
			return httpPost(url, paramMap, charset, timeout);
		}

		return httpGet(url, paramMap, charset, timeout);
	}

	// post请求
	public static String httpPost(String url, Map<String, Object> params, String charset, int timeout) {

		if (url == null || url.equals("")) {
			return null;
		}

		String result = null;

		// 超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout)
				.build();

		// 参数组装
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = (String) entry.getValue();
			pairs.add(new BasicNameValuePair(key, formatStr(value)));
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = null;
		String responseBody = null;
		CloseableHttpResponse response = null;

		try {
			httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			//增加post请求表单方式
			/*StringEntity stringEntity = new StringEntity("UTF-8");
			stringEntity.setContentType("application/json;charset=utf-8");
			httpPost.setEntity(stringEntity);*/
			//
			httpPost.setEntity( new UrlEncodedFormEntity(pairs,charset));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
			System.out.println("post请求信息："+urlEncodedFormEntity+"|"+url);
			response = httpClient.execute(httpPost);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}

			HttpEntity entity = response.getEntity();
			responseBody = EntityUtils.toString(entity, charset);
			result = responseBody;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	// get请求
	public static String httpGet(String url, Map<String, Object> params, String charset, int timeout) {

		if (url == null || url.equals("")) {
			return null;
		}

		String result = null;

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = null;
		String responseBody = null;
		CloseableHttpResponse response = null;

		try {

			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				for (Entry<String, Object> entry : params.entrySet()) {
					String key = entry.getKey();
					String value = (String) entry.getValue();
					pairs.add(new BasicNameValuePair(key, formatStr(value)));
				}
				url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			System.out.println("请求url:" + url);
			httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}

			HttpEntity entity = response.getEntity();
			responseBody = EntityUtils.toString(entity, charset);
			result = responseBody;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static String formatStr(String text) {
		return (text == null ? "" : text.trim());
	}
	
	//传化白名单生成签名方法
	public static String createSign(String characterEncoding, String[] a){  
    	Arrays.sort(a);
    	for(int i=0;i<a.length;i++){//ѭ�����飬

 	       System.out.println(a[i]);//�������Ԫ��

 	    }
    	String sbString= StringUtils.join(a);
    //	System.out.println("======"+sbString);
    	String sign = DigestUtils.shaHex(sbString);
    	System.out.println("白名单生成的签名="+sign);
		return sign;
       
    }  
	
	//传化白名单post请求方法
	  public static String sendPost(String url, String param) {
	    	System.out.println("白名单post请求进来了");
	        OutputStreamWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	    //    param = param.replaceAll("\\+", "%2B");
	        try {
	            URL realUrl = new URL(url);
	            // �򿪺�URL֮�������
	            URLConnection conn = realUrl.openConnection();
	            // ����ͨ�õ���������
	            conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // ����POST�������������������
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // ��ȡURLConnection�����Ӧ�������
	            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
	            // �����������
	            out.write(param);
	            // flush������Ļ���
	            out.flush();
	            // ����BufferedReader����������ȡURL����Ӧ
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	                System.out.println(result);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        //ʹ��finally�����ر��������������
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	    }    

	  /**
	   * 本机外网IP  传化白名单用
	   * @return
	   * @throws SocketException
	   */
	  public static String getRealIp() throws SocketException {
			String localip = null;// 本地IP，如果没有配置外网IP则返回它
			String netip = null;// 外网IP
	 
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
	 
			if (netip != null && !"".equals(netip)) {
				return netip;
			} else {
				return localip;
			}
		}

}
