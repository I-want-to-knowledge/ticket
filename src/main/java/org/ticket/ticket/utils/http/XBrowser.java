package org.ticket.ticket.utils.http;

import org.ticket.ticket.utils.http.client.XHttpClient;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.client.impl.XHttpClientImpl;
import org.ticket.ticket.utils.http.method.XHttpMethods;

/**
 * 浏览器
 *
 * @author YanZhen
 * 2018-11-19 16:32:49
 * XBrowser
 */
public class XBrowser {

	private XBrowser() {}
	
	private static XHttpClient client = new XHttpClientImpl();
	
	private static XBrowser xBrowser;
	
	/**
	 *  初始化浏览器
	 *
	 * 2018-11-26 20:23:34
	 * @return XBrowser
	 */
	public static XBrowser getInstance() {
		if (xBrowser != null) {
			return xBrowser;
		}
		return new XBrowser();
	}
	
	/**
	 * 请求链接
	 *
	 * 2018-11-26 20:23:08
	 * @param methods
	 * @return XHttpResponse
	 */
	public static XHttpResponse execute(XHttpMethods methods) {
		return client.execute(methods);
	}
}
