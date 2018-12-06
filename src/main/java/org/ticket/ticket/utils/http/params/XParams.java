package org.ticket.ticket.utils.http.params;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数
 *
 * @author YanZhen
 * 2018-11-20 16:37:05
 * XParams
 */
public class XParams {
	// private Logger LOG = LoggerFactory.getLogger(XParams.class);
	
	private Map<String, String> paramsMap = new HashMap<>();

	public Map<String, String> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}
	
	public void put(String key, String value) {
		paramsMap.put(key, value);
	}
	
	public void clear() {
		paramsMap.clear();
	}
	
	public XParams() {}
	
	public XParams(Map<String, String> paramsMap) {this.paramsMap = paramsMap;}
}
