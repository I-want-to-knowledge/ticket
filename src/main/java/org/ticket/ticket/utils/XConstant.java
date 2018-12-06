package org.ticket.ticket.utils;

/**
 * 常量
 *
 * @author YanZhen
 * 2018-11-19 16:50:20
 * XConstant
 */
public interface XConstant {

	final String UTF_8 = "UTF_8";
	
	/**
	 * 请求
	 *
	 * @author YanZhen
	 * 2018-11-20 17:24:26
	 * Http
	 */
	interface Http {
		final String GET = "GET";
		final String POST = "POST";
		final String QUESTIONMARK = "?";
		final String HTTPS = "https";
		final String HTTP = "http";
	}
	
	interface User {
		final String USERNAME = "username";
		final String PWD = "pwd";
		final String KEY = "key";
		final String VALUE = "value";
	}
}
