package org.ticket.ticket.page.method;

import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.page.MyHomePage;
import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.XBrowser;
import org.ticket.ticket.utils.http.XHttpUtils;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.method.impl.XHttpPost;
import org.ticket.ticket.utils.http.params.XParams;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 订单方法
 *
 * @author YanZhen
 * 2018-12-20 17:02:29
 * OrderMethod
 */
public class OrderMethod {
	private static Logger LOG = LoggerFactory.getLogger(OrderMethod.class);

	/**
	 * 获取订单列表
	 *
	 * 2018-12-20 17:02:48
	 * @param brushOrderButton 刷新订单按钮
	 * @param mypage 主页
	 * @return Object
	 */
	public static void getOrderList(JButton brushOrderButton, MyHomePage mypage) {
		try {
			// 参数
			XParams params = new XParams();
			params.put("_json_att", "");// 空字符串
			
			XHttpPost post = new XHttpPost(XConstant.Url.QUERY_MY_ORDER_NO_COMPLETE);
			post.setParams(params);
			XHttpResponse resp = XBrowser.execute(post);
			String body = XHttpUtils.outHtml(resp.getBody());
			JSONObject bodyJson = JSONObject.parseObject(body);
			
			// 处理订单
			disposeOrder(bodyJson, brushOrderButton, mypage);
		} finally {
			brushOrderButton.setEnabled(true);
		}
	}

	/**
	 * 处理订单
	 *
	 * 2018-12-20 17:17:49
	 * @param bodyJson 查询的订单信息
	 * @param brushOrderButton 刷新订单按钮
	 * @param mypage 主页
	 */
	private static void disposeOrder(JSONObject bodyJson, JButton brushOrderButton, MyHomePage mypage) {
		mypage.orderTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				XConstant.UnicodeString.UNICODE_CC, XConstant.UnicodeString.UNICODE_DDH, XConstant.UnicodeString.UNICODE_CKXM,
				XConstant.UnicodeString.UNICODE_FCSJ, XConstant.UnicodeString.UNICODE_CFD, XConstant.UnicodeString.UNICODE_MDD,
				XConstant.UnicodeString.UNICODE_PZ, XConstant.UnicodeString.UNICODE_XB, XConstant.UnicodeString.UNICODE_CX,
				XConstant.UnicodeString.UNICODE_ZW, XConstant.UnicodeString.UNICODE_PJ, XConstant.UnicodeString.UNICODE_ZT }) {
			private static final long serialVersionUID = 6320198627286309066L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		
		mypage.orderTable.getColumnModel().getColumn(3).setPreferredWidth(124);
		DefaultTableModel orderModel = (DefaultTableModel) mypage.orderTable.getModel();
		orderModel.setRowCount(0);
		if (!bodyJson.containsKey("data")) {
			return;
		}
		
		JSONObject dataJson = bodyJson.getJSONObject("data");
		JSONArray orderDBArray = dataJson.getJSONArray("orderDBList");
		for (int i = 0; i < orderDBArray.size(); i++) {
			JSONObject orderDBJson = orderDBArray.getJSONObject(i);
			JSONArray tickets = orderDBJson.getJSONArray("tickets");
			JSONObject ticketJson = tickets.getJSONObject(0);
			JSONObject stationTrainDTOJson = ticketJson.getJSONObject("stationTrainDTO");
			JSONObject passengerDTOJson = ticketJson.getJSONObject("passengerDTO");
			
			Vector<String> orderVector = new Vector<>();
			orderVector.add(orderDBJson.getString("train_code_page"));
			orderVector.add(orderDBJson.getString("sequence_no"));
			orderVector.add(passengerDTOJson.getString("passenger_name"));
			orderVector.add(orderDBJson.getString("start_train_date_page"));
			orderVector.add(stationTrainDTOJson.getString("from_station_name"));
			orderVector.add(stationTrainDTOJson.getString("to_station_name"));
			orderVector.add(ticketJson.getString("ticket_type_name"));
			orderVector.add(ticketJson.getString("seat_type_name"));
			orderVector.add(ticketJson.getString("coach_name"));
			orderVector.add(ticketJson.getString("seat_name"));
			orderVector.add(ticketJson.getString("str_ticket_price_page"));
			orderVector.add(ticketJson.getString("ticket_status_name"));
			orderModel.addRow(orderVector);
		}
	}

	/**
	 * 取消订单
	 *
	 * 2018-12-21 14:40:02
	 * @param orderNo 订单编号
	 * @param cancelButton 取消按钮
	 * @param mypage 主页
	 * @return Object
	 */
	public static void cancelOrder(String orderNo, JButton cancelButton, MyHomePage mypage) {
		// 请求参数
		XParams params = new XParams();
		params.put("_json_att", "");
		params.put("cancel_flag", "cancel_order");
		params.put("sequence_no", orderNo);
		
		// 请求
		XHttpPost post = new XHttpPost(XConstant.Url.CANCEL_NO_COMPLETE_MY_ORDER);
		post.setParams(params);
		XHttpResponse resp = XBrowser.execute(post);
		
		// 返回值解析
		JSONObject bodyJson = JSONObject.parseObject(XHttpUtils.outHtml(resp.getBody()));
		JSONObject dataJson = bodyJson.getJSONObject("data");
		
		// 查看是否为取消状态
		if ("N".equals(dataJson.get("existError"))) {
			mypage.textArea.append(mypage.HMS_FORMAT.format(new Date()) + "：取消订单成功！\r\n");
		}
		
		// 操作完成，激活按钮
		if (cancelButton != null) {
			cancelButton.setEnabled(true);
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			LOG.error("异常：", e);
		}
		
		getOrderList(null, mypage);
	}
	
}
