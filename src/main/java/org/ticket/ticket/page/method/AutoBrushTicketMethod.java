package org.ticket.ticket.page.method;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

import org.ticket.ticket.page.MyHomePage;
import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.XBrowser;
import org.ticket.ticket.utils.http.XHttpUtils;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.method.impl.XHttpGet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 自动刷票
 *
 * @author YanZhen
 * 2018-12-13 16:22:20
 * AutoTicketingMethod
 */
public class AutoBrushTicketMethod extends Thread {
	
	private MyHomePage myPage;
	
	public AutoBrushTicketMethod(MyHomePage myPage) {
		this.myPage = myPage;
	}

	@Override
	public void run() {
		int num = 0;// 运行次数
		Thread currentThread = Thread.currentThread();
		currentThread.setName("3");
		while (myPage.result) {
			if (myPage.ticketType == 1) {
				num++;
				myPage.printLog("正在为您进行第"+ num + "次查询！");
				// 自动刷票
				autoBrushTicket();
			}
		}
	}

	/**
	 * 自动刷票
	 *
	 * 2018-12-13 17:08:23 void
	 */
	private void autoBrushTicket() {
		XHttpResponse resp = XBrowser.execute(new XHttpGet(XConstant.Url.OTN_QUERY + myPage.purchaseTicketURLParams));
		String body = XHttpUtils.outHtml(resp.getBody());
		// analysis body
		analysisBody(body, myPage.seatCodes);
	}

	/**
	 * 解析刷票的请求结果
	 *
	 * 2018-12-13 17:21:02
	 * @param body
	 * @param seatCodes void
	 */
	private boolean analysisBody(String body, int[] seatCodes) {
		if (body == null) {
			return false;
		}
		
		// 解析body数据
		JSONObject bodyJson = JSONObject.parseObject(body);
		JSONObject data = bodyJson.getJSONObject("data");
		if (data == null) {
			return false;
		}
		JSONArray result = data.getJSONArray("result");
		JSONObject respMap = data.getJSONObject("map");
		
		// unicode编码
		myPage.table.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "\u8F66\u6B21", "\u51FA\u53D1\u5730", "\u76EE\u7684\u5730", "\u5386\u65F6",
						"\u53D1\u8F66\u65F6\u95F4", "\u5230\u8FBE\u65F6\u95F4", "\u5546\u52A1", "\u7279\u7B49", "\u4E00\u7B49",
						"\u4E8C\u7B49", "\u9AD8\u8F6F", "\u8F6F\u5367", "\u786C\u5367", "\u8F6F\u5EA7", "\u786C\u5EA7",
						"\u65E0\u5EA7", "\u5176\u5B83", "\u5907\u6CE8" }) {
			private static final long serialVersionUID = -8115899429661615045L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		
		// 所有车型数据删除
		myPage.trainTypeDatas.clear();
		
		boolean brush = true;
		for (Object object : result) {
			String[] tickets = object.toString().split("\\|");
			if (tickets.length <= 0) {
				continue;
			}

			flag: if (brush && myPage.ticketType == 1) {

				// 循环选择的车次
				DefaultListModel<Object> trainsModel = myPage.trainsModel;
				for (int i = 0; i < trainsModel.size(); i++) {
					String modelTrain = trainsModel.get(i).toString();
					// 如果是该车次
					if (tickets[3].trim().equalsIgnoreCase(modelTrain.trim())) {
						// 判断该车次是否邮票
						for (int seatCode : seatCodes) {
							String seat = tickets[seatCode].trim();
							if (!XConstant.Symbol._WU_.equals(seat) && !XConstant.Symbol.WU.equals(seat)) {
								brush = false;// 结束刷票结果判断
								myPage.result = false;// 结束自动刷票
								myPage.isRun = false;// 结束运行
								myPage.queryTicketButton.setText("自动刷票");
								new TicketOrderMethods(myPage.mypage, data).start();
								break flag;
							}
						}
					}
				}
			}
			myPage.addRow(tickets, respMap);
		}
		
		// 关闭自动刷票
		if (myPage.ticketType == 0) {
			myPage.result = false;
		}
		myPage.setTableStyle();
		return myPage.result;
	}

}
