package org.ticket.ticket.utils.http.client.impl;

import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.client.XHttpClient;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.client.entity.XHttpEntity;
import org.ticket.ticket.utils.http.header.XHeader;
import org.ticket.ticket.utils.http.method.XHttpMethods;
import org.ticket.ticket.utils.http.params.XParams;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.StringJoiner;

public class XHttpClientImpl implements XHttpClient {
	static CookieManager manager = new CookieManager();
	
	static {
		CookieHandler.setDefault(manager);
	}

	@Override
	public XHttpResponse execute(XHttpMethods methods) {
		XHttpResponse response = new XHttpResponseImpl();
		String urlStr = methods.getUrl();
		if (XConstant.Http.GET.equals(methods.getType())) {
			String paramsStr = preparParams(methods.getParams());
			if (XConstant.Http.QUESTIONMARK.equals(urlStr.substring(urlStr.length() - 1))) {
				urlStr = urlStr + paramsStr;
			} else if (!paramsStr.isEmpty()) {
				urlStr = urlStr + XConstant.Http.QUESTIONMARK + paramsStr;
			}
		}

		URL url;
		HttpsURLConnection openConnection;
		try {
			url = new URL(urlStr);
			openConnection = (HttpsURLConnection) url.openConnection();
			XHttpEntity entity = new XHttpEntity();
			if (urlStr.contains(XConstant.Http.HTTPS)) {
				https(openConnection, methods);
				entity.setBody(openConnection.getInputStream());
				entity.setHeaders(new XHeader(openConnection.getHeaderFields()));
				entity.setStatus(openConnection.getResponseCode());
				entity.setUrl(openConnection);
				response.setEntity(entity);
			} else {
				http(openConnection, methods);
				entity.setBody(openConnection.getInputStream());
				entity.setHeaders(new XHeader(openConnection.getHeaderFields()));
				entity.setStatus(openConnection.getResponseCode());
				entity.setUrl(openConnection);
				response.setEntity(entity);
			}
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * http请求
	 *
	 * 2018-11-21 20:03:33
	 * @param openConnection
	 * @param methods 
	 * @throws IOException
	 */
	private void http(HttpsURLConnection openConnection, XHttpMethods methods) throws IOException {
		openConnection.setInstanceFollowRedirects(false);
		openConnection.setRequestMethod(methods.getType());
		
		String paramsStr = preparParams(methods.getParams());
		if (XConstant.Http.GET.equals(methods.getType())) {
			openConnection.connect();
		} else {
			openConnection.setDoInput(true);
			openConnection.setDoOutput(true);
			OutputStream os = openConnection.getOutputStream();
			os.write(paramsStr.getBytes(Charset.forName("UTF-8")));
			os.close();
		}
	}

	/**
	 * https请求
	 *
	 * 2018-11-20 20:16:15
	 * @param openConnection
	 * @param methods void
	 * @throws KeyManagementException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	private void https(HttpsURLConnection openConnection, XHttpMethods methods) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		// SSLSocket扩展Socket并提供使用SSL或TLS协议的安全套接字
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] {new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {return null;}
			public void checkServerTrusted(X509Certificate[] chain, String authType) {}
			public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		}}, new SecureRandom());
		openConnection.setSSLSocketFactory(sslContext.getSocketFactory());
		
		String type = methods.getType();
		openConnection.setRequestMethod(type);
		String urlStr = preparParams(methods.getParams());
		if (XConstant.Http.GET.equals(type)) {
			openConnection.connect();
		} else {
			openConnection.setDoInput(true);
			openConnection.setDoOutput(true);
			
			OutputStream outputStream = openConnection.getOutputStream();
			outputStream.write(urlStr.getBytes(Charset.forName("UTF-8")));
			outputStream.close();
		}
	}

	/**
	 * 参数准备
	 *
	 * 2018-11-20 19:10:54
	 * 
	 * @param params
	 * @return String
	 */
	private String preparParams(XParams params) {
		if (params == null || params.getParamsMap().isEmpty()) {
			return "";
		}

		StringJoiner str = new StringJoiner("&");
		Map<String, String> p = params.getParamsMap();
		for (String key : p.keySet()) {
			str.add(key + "=" + p.get(key));
		}
		return str.toString();
	}

}
