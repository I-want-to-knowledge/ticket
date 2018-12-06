package org.ticket.ticket.utils.http;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ticket.ticket.utils.http.client.XHttpClient;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.client.impl.XHttpClientImpl;
import org.ticket.ticket.utils.http.method.impl.XHttpGet;

/**
 * http 工具类
 *
 * @author YanZhen
 * 2018-11-26 20:50:46
 * HttpUtils
 */
public class HttpUtils {
	private final static String STATION_NAME_JS = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js";

	/**
	 * 获取城市信息
	 *
	 * 2018-11-26 20:54:17
	 * @return Map<String,String[]>
	 */
	public static Map<String, String[]> getCityInfo() {
		// TODO:重构城市
		Map<String, String[]> map = new LinkedHashMap<>();
		XHttpClient client = new XHttpClientImpl();
		XHttpGet get = new XHttpGet(STATION_NAME_JS);
		XHttpResponse resp = client.execute(get);
		String data = XHttpUtils.outHtml(resp.getBody()).split("=")[1];
		resp.getEntity().disconnect();
		// var station_names ='@bjb|北京北|VAP|beijingbei|bjb|0@bjd|北京东|BOP|beijingdong|bjd|1'
		String[] stationNames = data.replace("'", "").split("@");
		for (String value : stationNames) {
			String[] split = value.split("\\|");
			if (split.length > 1) {
				map.put(split[1], split);
			}
		}
		return map;
	}
	
}

