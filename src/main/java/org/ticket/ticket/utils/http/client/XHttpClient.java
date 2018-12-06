package org.ticket.ticket.utils.http.client;

import org.ticket.ticket.utils.http.method.XHttpMethods;

/**
 * 
 *
 * @author YanZhen
 * 2018-11-19 20:20:24
 * XHttpClient
 */
public interface XHttpClient {
	abstract XHttpResponse execute(XHttpMethods methods);
}
