package org.ticket.ticket.utils.http.client.impl;

import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.client.entity.XHttpEntity;
import org.ticket.ticket.utils.http.header.XHeader;

public class XHttpResponseImpl implements XHttpResponse {
	private Logger LOG = LoggerFactory.getLogger(XHttpResponseImpl.class);
	
	private XHttpEntity entity;

	@Override
	public List<HttpCookie> getCookie() {
		CookieManager manager = XHttpClientImpl.manager;
		CookieStore cookieStore = manager.getCookieStore();
		return cookieStore.getCookies();
	}

	@Override
	public String getHeader(String key) {
		return entity.getHeaders().getValue(key);
	}

	@Override
	public XHeader getAllHeader() {
		return entity.getHeaders();
	}

	@Override
	public InputStream getBody() {
		return entity.getBody();
	}

	@Override
	public String getLocation() {
		XHeader headers = entity.getHeaders();
		String location = headers.getValue("Location");
		if (location != null) {
			LOG.info("Location={}", location);
			return location.substring(1, location.length()-1);
		}
		return "";
	}

	@Override
	public XHttpEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(XHttpEntity entity) {
		this.entity = entity;
	}

}
