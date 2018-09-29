package xdt.dto.sd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class HttpClientUtils {
	
	public static final int CONN_TIMEOUT = 10*1000; //请求超时
	public static final int SO_TIMEOUT = 20*1000;   //读取超时

	public static void doGet(String url) {
		HttpClient client = new DefaultHttpClient();
		
		try {
			long start = System.currentTimeMillis();
			
			HttpGet httpGet = new HttpGet(url);
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONN_TIMEOUT); //请求超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT); //读取超时
			HttpResponse response = client.execute(httpGet);
			// response.getEntity();
			if (response.getEntity() != null){
				//Log.debug(EntityUtils.toString(response.getEntity()));
				System.out.println(EntityUtils.toString(response.getEntity()));
			}else{
				System.out.println(response.getEntity());
			}
			System.out.println("time:"+(System.currentTimeMillis()-start));
			httpGet.abort();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}
	
	public static String doPost(String url, HashMap<String, String> params) {
		return doPost(url,params,HTTP.ISO_8859_1); //HTTP.ISO_8859_1,HTTP.UTF_8
	}

	public static String doPost(String url, HashMap<String, String> params,String encoding) {
		
		//处理参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		return doPost(url,nvps,encoding);
	}
	
	public static String doPost(String url,List<NameValuePair> nvps,String encoding) {
		String strResp = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			
			//整理参数
			HttpPost httpost = new HttpPost(url);
			httpost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

			//post提交
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONN_TIMEOUT); //请求超时
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT); //读取超时
			HttpResponse response = httpclient.execute(httpost);
			
			//返回处理
			if (response.getEntity() != null){
				//strResp = EntityUtils.toString(response.getEntity());
				strResp = EntityUtils.toString(response.getEntity(),encoding);
				//Log.print("http response: " + strResp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//AppException.raiseApp(e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return strResp;
	}
	
	public static String doPost(String url, String jsonStr,String encoding) {
		String strResp = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			
			//整理参数
			StringEntity se = new StringEntity(jsonStr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            HttpPost httpost = new HttpPost(url);
			httpost.setEntity(se);

			//post提交
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONN_TIMEOUT); //请求超时
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT); //读取超时
			HttpResponse response = httpclient.execute(httpost);
			
			//返回处理
			if (response.getEntity() != null){
				strResp = EntityUtils.toString(response.getEntity(),encoding);
				//Log.print("http response: " + strResp);
			}
		} catch (Exception e) {
			System.out.println("http1 Exception: " + e);
			//AppException.raiseApp(e.getMessage());
		} finally {
			try{
				httpclient.getConnectionManager().shutdown();
			}catch (Exception e){
				System.out.println("http0 shutdown Exception: " + e);
			}
		}
		return strResp;
	}
	
	public static void cashNotifyAgain(){
		
		String url = "http://112.74.195.214:9010/notify/newpaycash.do";
		String strPost = "{\"partner_id\":\"0755466\",\"m\":\"1\",\"orders\":[{\"order_no\":\"201709191756324587\",\"partner_order_no\":\"11035\",\"status\":\"S\",\"amount\":\"14\",\"transfer_fee_amt\":\"0\"}],\"sign\":\"2a06440e50f4aea64058c6e93bcb59ed\"}";
		System.out.println("post url: " + url);
		System.out.println("post json: " + strPost);
		HashMap<String, String> ps = new HashMap<String, String>();
		ps.put(strPost, "");
		String ret = HttpClientUtils.doPost(url,ps,HTTP.UTF_8);
		System.out.println("post ret: " + ret);
	}
	
	public static void main(String[] args) {
		
		cashNotifyAgain();
		
//		//String url = "http://127.0.0.1:9010/notify/newpaycash.do";
//		String url = "http://112.74.195.214:9010/notify/newpaycash.do";
//		
//		String json = "{\"partner_id\":\"0755466\",\"orders\":[{\"order_no\":\"201708311710526116\",\"partner_order_no\":\"10064\",\"status\":\"S\",\"amount\":\"12\",\"transfer_fee_amt\":\"0\"}],\"sign\":\"325c8605f3f1558445a0f740dba5337a\"}";
//		String ret = doPost(url,json,HTTP.UTF_8);
//		System.out.println("ret: " + ret);
	}
	
}
