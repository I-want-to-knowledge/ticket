package org.ticket.ticket.utils.http.client.entity;

import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import org.ticket.ticket.utils.http.header.XHeader;

/**
 * http 请求信息
 *
 * @author YanZhen 2018-11-19 19:40:31 XHttpEntity
 */
public class XHttpEntity {

	private XHeader headers;// 请求头
	private InputStream body;// 请求体
	private int status;// 请求状态
	private HttpsURLConnection url;// 链接

	public XHttpEntity() {}
	public XHttpEntity(HttpsURLConnection url) {this.url = url;}
	
	public void setUrl(HttpsURLConnection url) {
		this.url = url;
	}

	/**
	 * 关闭链接
	 *
	 * 2018-11-19 19:50:50 void
	 */
	public void disconnect() {
		url.disconnect();
	}

	public XHeader getHeaders() {
		return headers;
	}

	public void setHeaders(XHeader headers) {
		this.headers = headers;
	}

	public InputStream getBody() {
		return body;
	}

	public void setBody(InputStream body) {
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
