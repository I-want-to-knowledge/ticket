package org.ticket.ticket.page.method;

import java.util.Random;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.page.MyHomePage;
import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.HttpUtils;
import org.ticket.ticket.utils.http.XBrowser;
import org.ticket.ticket.utils.http.XHttpUtils;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.method.impl.XHttpGet;
import org.ticket.ticket.utils.http.method.impl.XHttpPost;
import org.ticket.ticket.utils.http.params.XParams;

import com.alibaba.fastjson.JSONObject;

/**
 * 车票订单
 *
 * @author YanZhen 2018-12-14 11:36:15 TicketOrderMethods
 */
public class TicketOrderMethods extends Thread {
	private Logger LOG = LoggerFactory.getLogger(TicketOrderMethods.class);
	
	public MyHomePage mypage;// 购票主页
	public JSONObject data;// 刷票返回车型数据
	private String verificationCode = "";// 验证码
	private String passengerTicketStr = "";// 顾客购票信息

	public TicketOrderMethods(MyHomePage mypage) {
		if (this.mypage == null)
			this.mypage = mypage;
	}

	TicketOrderMethods(MyHomePage mypage, JSONObject data) {
		if (this.mypage == null)
			this.mypage = mypage;

		if (this.data == null)
			this.data = data;
	}
	
	@Override
	public void run() {
		submitOrder();
	}

	/**
	 * 提交订单
	 *
	 * 2018-12-14 14:37:32 void
	 */
	private void submitOrder() {
		// 赋值车型数据
		if (data == null) {
			data = mypage.trainTypeDatas.get(mypage.table.getSelectedRow());
		}
		
		String username = mypage.passengers.getModel().getElementAt(0).toString();
		JSONObject userJson = mypage.userInfoMap.get(username);// 用户的信息
		DefaultListModel<Object> model = (DefaultListModel<Object>) mypage.seatTypes.getModel();
		int size = model.getSize();
		String[] seatTypes = new String[size];
		for (int i = 0; i < size; i++) {
			seatTypes[i] = model.get(i).toString();
		}
		
		// 客车编号
		String trainNum = data.getString(XConstant.Constant12306.STATION_TRAIN_CODE);
		
		// 获取乘客的购票信息
		passengerTicketStr = HttpUtils.getPassengerTicketStr(userJson, seatTypes, trainNum);
		
		// 预定车票
		mypage.printLog("订单已生成，开始提交订单！");
		// 提交订单
		XHttpPost post = new XHttpPost(XConstant.Url.SUBMIT_ORDER_REQUEST);
		// 组装参数
		XParams params = new XParams();
		params.put("secretStr", data.getString("secretStr"));// 从查票中获得（每个车次对应一个，并且每次都不一样，需要实时解析）
		params.put("train_date", mypage.dateTextField.getText());// 乘车日期
		params.put("back_train_date", mypage.dateTextField.getText());// 返程日期
		params.put("tour_flag", "dc");// 旅行标志
		params.put("purpose_codes", "ADULT");
		params.put("query_from_station_name", data.getString("from_station_name"));// 出发站
		params.put("query_to_station_name", data.getString("to_station_name"));// 目的地
		params.put("undefined", "");// 空字符串
		post.setParams(params);
		XHttpResponse resp = XBrowser.execute(post);
		String body = XHttpUtils.outHtml(resp.getBody());
		JSONObject bodyJson = JSONObject.parseObject(body);
		if (bodyJson.getBooleanValue("status")) {
			mypage.printLog("订单已经成功提交！");
			resp.getEntity().disconnect();
			
			// 预定
			reserveTicket();
			
		} else {
			mypage.printLog(bodyJson.getString("messages"));
			resp.getEntity().disconnect();
		}
	}

	/**
	 * 预定车票
	 *
	 * 2018-12-14 17:37:24 void
	 */
	private void reserveTicket() {
		// 预定请求
		XHttpPost post = new XHttpPost(XConstant.Url.INIT_DC);
		XParams params = new XParams();
		params.put("_json_att", "");
		post.setParams(params);
		
		// 请求返回值
		XHttpResponse resp = XBrowser.execute(post);
		String body = XHttpUtils.outHtml(resp.getBody());
		
		// 解析返回值（html）中的某个值
		Pattern tokenCompile = Pattern.compile("var globalRepeatSubmitToken = '[0-9|a-z]{32}");
		Pattern keyCompile = Pattern.compile("'key_check_isChange':'[0-9|A-Z]{56}");
		Matcher tokenMatcher = tokenCompile.matcher(body);
		Matcher keyMatcher = keyCompile.matcher(body);
		while (tokenMatcher.find()) {
			mypage.globalRepeatSubmitToken = tokenMatcher.group().replaceFirst("var globalRepeatSubmitToken = '", "");
		}
		while (keyMatcher.find()) {
			mypage.keyCheckIsChange = keyMatcher.group().replaceFirst("'key_check_isChange':'", "");
		}
		
		// 关闭
		resp.getEntity().disconnect();
		mypage.printLog("开始拉取验证码...");
		getVerificationCode();
	}

	/**
	 * 获取验证码
	 *
	 * 2018-12-17 13:30:05 void
	 */
	private void getVerificationCode() {
		this.verificationCode = "";
		XHttpResponse resp = XBrowser.execute(new XHttpGet(XConstant.Url.PASS_CODE_NEW + Math.random()));// 获取验证码
		HttpUtils.submitVerificationCode(resp.getBody(), this);
		resp.getEntity().disconnect();
	}

	/**
	 * 检查验证码是否正确
	 *
	 * 2018-12-17 15:59:33
	 * @param verificationCode 验证码
	 */
	public void checkVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
		mypage.printLog("当前验证码：" + verificationCode);
		
		// 请求参数
		XParams params = new XParams();
		params.put("randCode", verificationCode);
		params.put("rand", "randp");
		params.put("_json_att", "");
		params.put("REPEAT_SUBMIT_TOKEN", mypage.globalRepeatSubmitToken);
		
		// 验证请求链接
		XHttpPost post = new XHttpPost(XConstant.Url.CHECK_RAND_CODE_ANSYN);
		post.setParams(params);
		
		// 解析返回参数
		XHttpResponse resp = XBrowser.execute(post);
		String body = XHttpUtils.outHtml(resp.getBody());
		JSONObject bodyJson = JSONObject.parseObject(body);
		JSONObject dataJson = bodyJson.getJSONObject("data");
		if ("1".equals(dataJson.getString("result"))) {
			mypage.printLog("验证码正确，验证提交信息！");
			checkOrderInfo();
		} else {
			mypage.printLog("验证码选择错误！");
			getVerificationCode();
		}
	}

	/**
	 * 检查订单信息，是否可以提交
	 *
	 * 2018-12-17 16:27:39 void
	 */
	private void checkOrderInfo() {
		// 用户的信息获取
		String username = mypage.passengers.getModel().getElementAt(0).toString();
		JSONObject userInfoJson = mypage.userInfoMap.get(username);
		
		XParams params = new XParams();
		params.put("cancel_flag", "2");
		params.put("bed_level_order_num", "000000000000000000000000000000");
		params.put("passengerTicketStr", passengerTicketStr);// 座位类型，0，车票类型，姓名，身份正号，电话，N（多个的话，以逗号分隔）
		params.put("oldPassengerStr", userInfoJson.getString("passenger_name")
				+ ",1," + userInfoJson.getString("passenger_id_no") + ",1_");// 姓名，证件类别，证件号码，用户类型
		params.put("tour_flag", "dc");// 
		params.put("randCode", verificationCode);// 预定验证码
		params.put("_json_att", "");
		params.put("REPEAT_SUBMIT_TOKEN", mypage.globalRepeatSubmitToken);
		
		XHttpPost post = new XHttpPost(XConstant.Url.CHECK_ORDER_INFO);
		post.setParams(params);
		
		// 请求， 返回值
		XHttpResponse resp = XBrowser.execute(post);
		String body = XHttpUtils.outHtml(resp.getBody());
		JSONObject bodyJson = JSONObject.parseObject(body);
		JSONObject dataJson = bodyJson.getJSONObject("data");
		if (dataJson.getBooleanValue("submitStatus")) {
			mypage.printLog("订单信息验证完成，提交订单！");
			submitOrderInfo();
		} else {
			mypage.printLog(dataJson.getString("errMsg"));
			return;
		}
		resp.getEntity().disconnect();
	}

	/**
	 * 提交订单
	 * 先获取余票数量
	 *
	 * 2018-12-17 17:21:39 void
	 */
	private void submitOrderInfo() {
		// 赋值车型数据
		if (data == null) {
			data = mypage.trainTypeDatas.get(mypage.table.getSelectedRow());
		}
		
		// 请求参数
		XParams params = new XParams();
		try {
			params.put("train_date", mypage.YMD_FORMAT.parse(mypage.dateTextField.getText()) + "");
			params.put("train_no", data.getString("train_no"));
			params.put("stationTrainCode", data.getString("station_train_code"));
			params.put("seatType", "3");
			params.put("fromStationTelecode", data.getString("from_station_telecode"));
			params.put("toStationTelecode", data.getString("to_station_telecode"));
			params.put("leftTicket", data.getString("yp_info"));
			params.put("purpose_codes", "00");
			params.put("train_location", data.getString("location_code"));
			params.put("_json_att", "");
			params.put("REPEAT_SUBMIT_TOKEN", mypage.globalRepeatSubmitToken);
			
			// 请求余票数量
			XHttpPost post = new XHttpPost(XConstant.Url.GET_QUEUE_COUNT);
			post.setParams(params);
			XHttpResponse resp = XBrowser.execute(post);
			
			// 解析
			JSONObject bodyJson = JSONObject.parseObject(XHttpUtils.outHtml(resp.getBody()));
			if (bodyJson.getBooleanValue("status")) {
				JSONObject dataJson = bodyJson.getJSONObject("data");
				int remainingTicket = HttpUtils.getRemainingTicket(dataJson.getString("ticket"), passengerTicketStr.substring(0, 1));
				mypage.printLog(data.getString("station_train_code") + ":" + HttpUtils.seatNumToSeatType(passengerTicketStr.substring(0, 1)) + "剩余：" + remainingTicket + "张");
				mypage.printLog("开始提交订单！");
				
				// 确认提交订单
				confirmSubmitOrder();
				
			} else {
				mypage.printLog(bodyJson.getString("messages"));
			}
			resp.getEntity().disconnect();
		} catch (Exception e) {
			mypage.printLog("系统出现异常，" + XConstant.PROMPT_MESSAGE);
			LOG.error("提交订单异常：", e);
		}
	}

	/**
	 * 确认并提交订单
	 *
	 * 2018-12-18 15:29:37 void
	 */
	private void confirmSubmitOrder() {
		// 赋值车型数据
		if (data == null) {
			data = mypage.trainTypeDatas.get(mypage.table.getSelectedRow());
		}
		
		String username = mypage.passengers.getModel().getElementAt(0).toString();
		JSONObject userJson = mypage.userInfoMap.get(username);
		
		// 参数
		XParams params = new XParams();
		params.put("passengerTicketStr", passengerTicketStr);
		params.put("oldPassengerStr", userJson.getString("passenger_name") + ",1," + userJson.getString("passenger_id_no") + ",1_");
		params.put("randCode", verificationCode);
		params.put("purpose_codes", "00");
		params.put("key_check_isChange", mypage.keyCheckIsChange);
		params.put("leftTicketStr", data.getString("yp_info"));
		params.put("train_location", data.getString("location_code"));
		params.put("roomType", "00");
		params.put("dwAll", "N");
		params.put("_json_att", "");
		params.put("REPEAT_SUBMIT_TOKEN", mypage.globalRepeatSubmitToken);
		
		// 请求
		XHttpPost post = new XHttpPost(XConstant.Url.CONFIRM_SINGLE_FOR_QUEUE);
		post.setParams(params);
		XHttpResponse resp = XBrowser.execute(post);
		String body = XHttpUtils.outHtml(resp.getBody());
		JSONObject bodyJson = JSONObject.parseObject(body);
		JSONObject dataJson = bodyJson.getJSONObject("data");
		if (dataJson.getBooleanValue("submitStatus")) {
			mypage.printLog("订单提交成功，正在查询订票结果！");
			queryOrderInfo();
		} else {
			mypage.printLog(body);
		}
		resp.getEntity().disconnect();
	}

	/**
	 * 查询订单信息
	 *
	 * 2018-12-18 16:10:19 void
	 */
	private void queryOrderInfo() {
		String orderId = "";// 订单号
		int flag = 0;// 防止死循环
		while (orderId.isEmpty() && flag < 3) {
			// 参数
			StringJoiner sj = new StringJoiner("&");
			sj.add("random=14772940" + (new Random().nextInt(9000) + 1000));
			sj.add("tourFlag=dc");
			sj.add("_json_att=");
			sj.add("REPEAT_SUBMIT_TOKEN=" + mypage.globalRepeatSubmitToken);
			
			// 订单查询
			XHttpGet get = new XHttpGet(XConstant.Url.QUERY_ORDER_WAIT_TIME + sj.toString());
			XHttpResponse resp = XBrowser.execute(get);
			
			// 返回值解析
			String body = XHttpUtils.outHtml(resp.getBody());
			JSONObject bodyJson = JSONObject.parseObject(body);
			JSONObject dataJson = bodyJson.getJSONObject("data");
			
			// 检查返回信息
			String oId = dataJson.getString("orderId");
			if (oId != null && !oId.isEmpty() && !oId.equalsIgnoreCase("null")) {
				orderId = oId;
			}
			flag++;
		}
		
		// 订票成功
		mypage.printLog("恭喜你，成功订到一张" + data.getString("from_station_name") + "至"
				+ data.getString("end_station_name") + "的" + HttpUtils.seatNumToSeatType(passengerTicketStr.substring(0, 1))
				+ "，（单号为：" + orderId + "），请尽快支付以免错过订单！");
	}
}
