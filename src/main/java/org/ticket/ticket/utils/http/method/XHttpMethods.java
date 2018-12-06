package org.ticket.ticket.utils.http.method;

import org.ticket.ticket.utils.http.header.XHeader;
import org.ticket.ticket.utils.http.params.XParams;

/**
 * 请求方法
 *
 * @author YanZhen
 * 2018-11-20 16:32:34
 * XHttpMethods
 */
public interface XHttpMethods {

	abstract String getUrl();
	
	abstract XHeader getHeader();
	
	abstract XParams getParams();
	
	abstract String getType();
}
