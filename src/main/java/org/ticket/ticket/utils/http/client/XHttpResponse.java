package org.ticket.ticket.utils.http.client;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.List;

import org.ticket.ticket.utils.http.client.entity.XHttpEntity;
import org.ticket.ticket.utils.http.header.XHeader;

/**
 * http 返回数据
 *
 * @author YanZhen
 * 2018-11-19 16:55:13
 * XHttpResponse
 */
public interface XHttpResponse {

	/**
	 * 获取cookie
	 *
	 * 2018-11-19 16:58:43
	 * @return List<HttpCookie>
	 */
	abstract List<HttpCookie> getCookie();
	
	/**
	 * 获取header
	 *
	 * 2018-11-19 17:09:04
	 * @param key
	 * @return String
	 */
	abstract String getHeader(String key);
	
	/**
	 * 获取所有的header
	 *
	 * 2018-11-19 17:10:57
	 * @return XHeader
	 */
	abstract XHeader getAllHeader();
	
	/**
	 * 获取请求体
	 *
	 * 2018-11-19 18:04:14
	 * @return InputStream
	 */
	abstract InputStream getBody();
	
	/**
	 * 获取请求位置
	 *
	 * 2018-11-19 18:05:15
	 * @return String
	 */
	abstract String getLocation();
	
	/**
	 * 请求信息
	 *
	 * 2018-11-19 19:52:45
	 * @return XHttpEntity
	 */
	abstract XHttpEntity getEntity();
	
	/**
	 * 请求信息录入
	 *
	 * 2018-11-19 19:54:55
	 * @param entity void
	 */
	abstract void setEntity(XHttpEntity entity);
}
