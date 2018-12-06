package org.ticket.ticket.utils.http.method.impl;

import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.header.XHeader;
import org.ticket.ticket.utils.http.method.XHttpMethods;
import org.ticket.ticket.utils.http.params.XParams;

/**
 * get 请求
 *
 * @author YanZhen 2018-11-20 16:49:56 XHttpGet
 */
public class XHttpGet implements XHttpMethods {
	private final String type = XConstant.Http.GET;
	private String url;
	private XHeader header;
	private XParams params;
	
	public XHttpGet() {}
	public XHttpGet(String url) {this.url = url;}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public XHeader getHeader() {
		return header;
	}

	public void setHeader(XHeader header) {
		this.header = header;
	}

	public XParams getParams() {
		return params;
	}

	public void setParams(XParams params) {
		this.params = params;
	}

	public String getType() {
		return type;
	}

}
