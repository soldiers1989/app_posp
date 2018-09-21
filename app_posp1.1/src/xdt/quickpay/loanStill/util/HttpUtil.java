package xdt.quickpay.loanStill.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.http.Consts;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
@SuppressWarnings({"unused"})
public class HttpUtil {

	private static DefaultTrustManager trustManager = new DefaultTrustManager();

	private static ConnectionKeepAliveStrategy myStrategy = null;
	static {
		myStrategy = new ConnectionKeepAliveStrategy() {
			public long getKeepAliveDuration(HttpResponse response,
					HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator("Keep-Alive"));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if ((value != null) && (param.equalsIgnoreCase("timeout"))) {
						return Long.parseLong(value) * 1000L;
					}
				}
				return 5000L;
			}
		};
	}

	private static CloseableHttpClient httpclient = HttpClientBuilder.create()
			.setMaxConnTotal(90).setMaxConnPerRoute(15)
			.setKeepAliveStrategy(myStrategy).build();

	public static JSONObject post(Map<String, String> params, String url)
			throws Exception {
		HttpPost post = new HttpPost(url);

		List<NameValuePair> list = new ArrayList<NameValuePair>();

		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			list.add(new BasicNameValuePair(key, (String) params.get(key)));
		}
		post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
		CloseableHttpResponse response = httpclient.execute(post);
		JSONObject jo = null;
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);
			if (entity != null)
				jo = JSONObject.fromObject(str);
		} else {
			EntityUtils.consume(response.getEntity());
		}

		return jo;
	}

	public static String postAndReturnString(Map<String, String> params,
			String url) throws Exception {
		HttpPost post = new HttpPost(url);

		List<NameValuePair> list = new ArrayList<NameValuePair>();

		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			list.add(new BasicNameValuePair(key, (String) params.get(key)));
		}
		post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
		CloseableHttpResponse response = httpclient.execute(post);
		String jo = null;
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			jo = EntityUtils.toString(entity);
		} else {
			EntityUtils.consume(response.getEntity());
		}

		return jo;
	}

	public static String testPost(Map<String, String> params, String url)
			throws Exception {
		HttpPost post = new HttpPost(url);
		post.addHeader(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C; .NET4.0E)");
		List<NameValuePair> list = new ArrayList<NameValuePair>();

		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			list.add(new BasicNameValuePair(key, (String) params.get(key)));
		}
		post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
		CloseableHttpResponse response = httpclient.execute(post);

		String redirectUrl = null;
		if (response.getStatusLine().getStatusCode() == 302) {
			redirectUrl = response.getFirstHeader("Location").getValue();
		} else if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			redirectUrl = EntityUtils.toString(entity);
		}

		return redirectUrl;

	}

	public static String testGet(String url) throws Exception {
		HttpGet post = new HttpGet(url);

		CloseableHttpResponse response = httpclient.execute(post);
		HttpEntity entity = response.getEntity();

		String str = EntityUtils.toString(entity);
		return str;

	}

	public static String write(String content, String reqUrl) {
		try {
			URL url = new URL(reqUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(100000);
			conn.setReadTimeout(100000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

			conn.connect();
			if (content != null) {
				OutputStream out = conn.getOutputStream();
				out.write(content.getBytes("utf-8"));
			}
			int code = conn.getResponseCode();

			InputStream in = conn.getInputStream();
			byte[] data = new byte[1024];
			StringBuffer resp = new StringBuffer("");
			while (in.read(data) != -1) {
				resp.append(new String(data, "utf-8").trim());
			}

			in.close();
			conn.disconnect();
			return resp.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String writegbk(String content, String reqUrl) {

		HttpPost method = new HttpPost(reqUrl);
		String entity="响应";
		try {
			method.setEntity(new StringEntity(content,ContentType.create("application/json", "utf-8")));
			CloseableHttpResponse r = httpclient.execute(method);
			if (r.getStatusLine().getStatusCode() == 200) {
				 entity = EntityUtils.toString(r.getEntity());
				System.out.println("【URL为：】" + reqUrl + "【包体为:】" + content
						+ "【响应串为:】" + entity);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return entity;

	}

	private static class DefaultTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
	
}
