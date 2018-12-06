package org.ticket.ticket.utils.http.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * header内容
 *
 * @author YanZhen
 * 2018-11-19 17:11:52
 * XHeader
 */
public class XHeader {
	private Logger LOG = LoggerFactory.getLogger(XHeader.class);
	
	private Map<String, List<String>> headerMap = new HashMap<>();
	
	public Map<String, List<String>> getHeaderMap() {
		return headerMap;
	}
	
	public void setHeaderMap(Map<String, List<String>> headerMap) {
		this.headerMap = headerMap;
	}
	
	public XHeader() {}
	
	public XHeader(Map<String, List<String>> headerMap) {
		setHeaderMap(headerMap);
	}
	
	/**
	 * 获取header的对应value
	 *
	 * 2018-11-19 17:21:04
	 * @param key
	 * @return String
	 */
	public String getValue(String key) {
		return headerMap.get(key).toString();
	}
	
	/**
	 * 请求header的name获取
	 *
	 * 2018-11-19 17:37:33
	 * @return String[]
	 */
	public String[] getKeys() {
		Set<String> keys = headerMap.keySet();
		String[] strArray = new String[]{};
		if (keys != null) {
			return keys.toArray(strArray);
		}
		return strArray;
	}
	
	/**
	 * 储存name和value
	 *
	 * 2018-11-19 17:42:09
	 * @param key
	 * @param value void
	 */
	public void put(String key, List<String> value) {
		headerMap.put(key, value);
	}
	
	/**
	 * 打印请求头
	 *
	 * 2018-11-19 18:02:11 void
	 */
	public void printlnAllHeaderInfo() {
		LOG.info("===================开始打印头信息=================");
		for (String key : headerMap.keySet()) {
			LOG.info("{} == {}", key, headerMap.get(key).toString());
		}
		LOG.info("===================打印头信息结束=================");
	}
	
}
