package org.ticket.ticket.utils.http;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.page.method.TicketOrderMethods;
import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.client.XHttpClient;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.client.impl.XHttpClientImpl;
import org.ticket.ticket.utils.http.method.impl.XHttpGet;

import com.alibaba.fastjson.JSONObject;

/**
 * http 工具类
 *
 * @author YanZhen
 * 2018-11-26 20:50:46
 * HttpUtils
 */
public class HttpUtils {
	private static Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
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

	/**
	 * 购票信息
	 *
	 * 2018-12-14 15:57:28
	 * @param userJson 用户的信息
	 * @param seatTypes 座位类型
	 * @param trainNum 火车编号
	 * @return String 组装信息
	 */
	public static String getPassengerTicketStr(JSONObject userJson, String[] seatTypes, String trainNum) {
		String seatTypeCode = "";// 座位类型编号
		// String passengerTicketStr = "";// 购票信息
		
		// 解析车型
		String fristLetter = trainNum.substring(0, 1);// 车型的首字母
		switch (fristLetter.toUpperCase()) {
			case XConstant.TrainType.TRAIN_G:
				for (String seatType : seatTypes) {
					if (seatType.contains(XConstant.SeatType.SEAT_SWZ)) {
						seatTypeCode = "9";
					} else if (seatType.contains(XConstant.SeatType.SEAT_YDZ)) {
						seatTypeCode = "M";
					} else if (seatType.contains(XConstant.SeatType.SEAT_EDZ)) {
						seatTypeCode = "O";
					}
					
					if (!seatType.isEmpty()) {
						break;
					}
				}
				break;
			case XConstant.TrainType.TRAIN_C:
			case XConstant.TrainType.TRAIN_D:
				for (String seatType : seatTypes) {
					if (seatType.contains(XConstant.SeatType.SEAT_YDZ)) {
						seatTypeCode = "M";
					} else if (seatType.contains(XConstant.SeatType.SEAT_EDZ)) {
						seatTypeCode = "O";
					}
					
					if (!seatType.isEmpty()) {
						break;
					}
				}
				break;
			case XConstant.TrainType.TRAIN_Z:
			case XConstant.TrainType.TRAIN_T:
			case XConstant.TrainType.TRAIN_K:
				for (String seatType : seatTypes) {
					if (seatType.contains(XConstant.SeatType.SEAT_RW)) {
						seatTypeCode = "4";
					} else if (seatType.contains(XConstant.SeatType.SEAT_YW)) {
						seatTypeCode = "3";
					} else if (seatType.contains(XConstant.SeatType.SEAT_YZ)) {
						seatTypeCode = "1";
					}
					
					if (!seatType.isEmpty()) {
						break;
					}
				}
				break;

			default:
				LOG.warn("车型首字母不匹配，letter={}", fristLetter);
				break;
		}
		
		// 购票信息，拼接
		StringJoiner sj = new StringJoiner(",");
		sj.add(seatTypeCode);// 座位类型编号
		sj.add("0");
		sj.add("1");
		sj.add(userJson.getString(XConstant.Constant12306.PASSENGER_NAME));// 乘客名
		sj.add("1");
		sj.add(userJson.getString(XConstant.Constant12306.PASSENGER_ID_NO));// 乘客ID号
		sj.add(userJson.getString(XConstant.Constant12306.MOBILE_NO));// 手机号
		sj.add("N");
		// passengerTicketStr = seatTypeCode + 
		
		return sj.toString();
	}

	/**
	 * 验证码（提交订单）
	 *
	 * 2018-12-17 13:54:19
	 * @param body 页面返回body
	 * @param ticketOrderMethods 票订单方法
	 */
	public static void submitVerificationCode(InputStream body, TicketOrderMethods ticketOrderMethods) {
		byte[] b = new byte[]{};
		try {
			b = XHttpUtils.inputStreamToByte(body);
		} catch (IOException e) {
			LOG.error("提交订单异常：{}", e);
		}
		
		// 验证码
		JFrame verification = new JFrame("验证码");
		verification.setSize(new Dimension(387, 455));
		verification.setLocationRelativeTo(null);
		verification.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		verification.getContentPane().setLayout(null);
		
		JLabel l = new JLabel("当前点击坐标是：");
		l.setBounds(43, 266, 284, 23);
		verification.getContentPane().add(l);
		
		JLabel label = new JLabel("");
		label.setBounds(43, 28, 284, 196);
		label.setIcon(new ImageIcon(b));
		
		// 验证码面板
		JComponent verificationPane = verification.getLayeredPane();
		
		// label 处理
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					// 12306 LOGO 放置
					ImageIcon image12306 = new ImageIcon(XConstant.Jpg.JPG_12306_URL);
					JLabel imageLabel = new JLabel(image12306);
					imageLabel.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							verificationPane.remove(imageLabel);
							verificationPane.repaint();
						}
					});
					verificationPane.add(imageLabel, -3);// 将label放置在面板之下
					imageLabel.setSize(image12306.getIconWidth(), image12306.getIconHeight());
					imageLabel.setLocation(e.getX() + 43 - (image12306.getIconWidth()/2), e.getY() + 86 - image12306.getIconHeight());
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					Component[] components = verificationPane.getComponents();
					if (components.length > 0) {
						// 销毁frame
						verification.dispose();
						
						// 拼接验证码坐标
						StringJoiner sj = new StringJoiner(",");
						for (Component c : components) {
							if (c instanceof JLabel) {
								JLabel lb = (JLabel) c;
								sj.add((lb.getX() - 64 + lb.getIcon().getIconWidth()/2) + "");
								sj.add((lb.getY() - 179 + lb.getIcon().getIconHeight()/2) + "");
							}
						}
						
						// 验证验证码
						ticketOrderMethods.checkVerificationCode(sj.toString());
					} else {
						l.setText("没有选择验证码，不提交信息；");
						l.setForeground(Color.red);
					}
				}
			}
		});
		verification.getContentPane().add(label);
		
		JButton submitBut = new JButton("提交");
		submitBut.setBounds(43, 354, 284, 53);
		submitBut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 点击事件
				
				Component[] components = verificationPane.getComponents();
				if (components.length > 0) {
					// 销毁
					verification.dispose();
					
					// 拼接验证码坐标
					StringJoiner sj = new StringJoiner(",");
					for (Component c : components) {
						if (c instanceof JLabel) {
							JLabel lb = (JLabel) c;
							sj.add((lb.getX() - 48 + lb.getIcon().getIconWidth()/2) + "");
							sj.add((lb.getY() - 113 + lb.getIcon().getIconHeight()/2) + "");
						}
					}
					
					// 验证验证码
					ticketOrderMethods.checkVerificationCode(sj.toString());
				} else {
					l.setText("没有选择验证码，不提交信息；");
					l.setForeground(Color.red);
				}
			}
		});
		verification.getContentPane().add(submitBut);
		
		JButton button = new JButton("...BBB");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				XHttpResponse resp = XBrowser.execute(new XHttpGet(XConstant.Url.PASS_CODE_NEW + Math.random()));
				try {
					byte[] body = XHttpUtils.inputStreamToByte(resp.getBody());
					label.setIcon(new ImageIcon(body));
				} catch (IOException e1) {
					ticketOrderMethods.mypage.printLog("发生异常：" + XConstant.PROMPT_MESSAGE + "\n异常信息：" + e1.getMessage());
					LOG.error("异常：", e1);
				}
			}
		});
		button.setBounds(43, 316, 284, 35);
		verification.getContentPane().add(button);
		
		JLabel ticketLabel = new JLabel("车票信息：");
		ticketLabel.setBounds(43, 10, 88, 23);
		ticketLabel.setFont(new Font("Default", Font.BOLD, 15));
		verification.getContentPane().add(ticketLabel);
		
		String username = ticketOrderMethods.mypage.passengers.getModel().getElementAt(0).toString();
		JLabel usernameLabel = new JLabel(username);
		usernameLabel.setBounds(43, 43, 50, 15);
		usernameLabel.setFont(new Font("Default", Font.BOLD, 12));
		verification.getContentPane().add(usernameLabel);
		
		JLabel orderTrainNoLabel = new JLabel(ticketOrderMethods.data.getString("station_train_code"));
		orderTrainNoLabel.setBounds(108, 43, 50, 15);
		orderTrainNoLabel.setFont(new Font("Default", Font.BOLD, 12));
		verification.getContentPane().add(orderTrainNoLabel);
		
		// 出发地 --> 目的地
		JLabel siteLabel = new JLabel(ticketOrderMethods.mypage.departTextField.getText() + "-->" + ticketOrderMethods.mypage.destinationTextField.getText());
		siteLabel.setBounds(168, 43, 88, 15);
		siteLabel.setFont(new Font("Default", Font.BOLD, 12));
		verification.getContentPane().add(siteLabel);
		
		// 座位类型
		JLabel seatLabel = new JLabel(ticketOrderMethods.mypage.seatTypes.getModel().getElementAt(0).toString());
		seatLabel.setBounds(260, 43, 50, 15);
		seatLabel.setFont(new Font("Default", Font.BOLD, 12));
		verification.getContentPane().add(seatLabel);
		
		// 提示
		JLabel hintLabel = new JLabel("验证码选择完成，右键即可提交！");
		hintLabel.setBounds(43, 290, 284, 23);
		hintLabel.setForeground(Color.red);
		verification.getContentPane().add(hintLabel);
		
		// 展示面板
		verification.setVisible(true);
	}

	/**
	 * 获取余票数量
	 *
	 * 2018-12-18 10:23:50
	 * @param ticket 票
	 * @param seatNum 座位号
	 * @return int
	 */
	public static int getRemainingTicket(String ticket, String seatNum) {
		// String rt = "";
		int seat_1 = -1;
		// int seat_2 = -1;
		int i = 0;
		while (i < ticket.length()) {
			String s = ticket.substring(i, i + 10);
			String c_seat = s.substring(0, 1);
			if (c_seat.equals(seatNum)) {
				String count = s.substring(6, 10);
				if (count.length() > 0) {
					int c = Integer.parseInt(count);
					if (c < 3000) {
						seat_1 = c;
					} else {
						// seat_2 = (c - 3000);
						LOG.info("余票数量：{}", c);
					}
				}
			}
			i = i + 10;
		}
		/*if (seat_1 > -1) {
			rt += seat_1;
		}
		if (seat_2 > -1) {
			rt += "," + seat_2;
		}*/
		
		int number = 0;// 张数
		
		/*String[] rts = rt.split(",");
		if (rts.length > 0) {
			number = Integer.parseInt(rts[0]);
		}*/
		if (seat_1 > 0) {
			number = seat_1;
		}
		
		return number;
	}

	/**
	 * 座位编号转座位类型
	 *
	 * 2018-12-18 13:57:11
	 * @param seatNum 座位号
	 * @return String
	 */
	public static String seatNumToSeatType(String seatNum) {
		String seatType = "";
		switch (seatNum.toUpperCase()) {
			case "1":
				seatType = XConstant.SeatType.SEAT_YZ;
				break;
			case "3":
				seatType = XConstant.SeatType.SEAT_YW;
				break;
			case "4":
				seatType = XConstant.SeatType.SEAT_RW;
				break;
			case "O":
				seatType = XConstant.SeatType.SEAT_EDZ;
				break;
			case "M":
				seatType = XConstant.SeatType.SEAT_YDZ;
				break;
			case "9":
				seatType = XConstant.SeatType.SEAT_SWZ;
				break;

			default:
				LOG.warn("座位编号（{}）不存在；", seatNum);
				break;
		}
		return seatType;
	}
	
}

