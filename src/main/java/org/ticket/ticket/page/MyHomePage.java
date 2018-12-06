package org.ticket.ticket.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.utils.http.ComBoTextField;
import org.ticket.ticket.utils.http.HttpUtils;
import org.ticket.ticket.utils.http.XBrowser;

/**
 * 购票界面
 *
 * @author YanZhen
 * 2018-11-26 20:10:53
 * MyHomePage
 */
public class MyHomePage {
	private Logger LOG = LoggerFactory.getLogger(MyHomePage.class);

	public MyHomePage mypage;
	private JFrame frame;
	public Map<String, String[]> map = HttpUtils.getCityInfo();
	public JTextField textField;
	public JLabel labelStart;
	
	public void show(MyHomePage mypage) {
		this.mypage = mypage;
		mypage.frame.setVisible(true);
	}
	
	/**
	 * Create the application.
	 */
	public MyHomePage() {
		XBrowser.getInstance();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 *
	 * 2018-11-26 20:25:04 void
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.error("赋值‘系统外观类名’报错：{}", e);
		}
		
		// 创建我的首页
		frame = new JFrame("My home page");
		frame.setBounds(100, 100, 1110, 720);// 窗口大小
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 关闭窗口
		
		// 输入框信息
		// 拓展：ComBoTextField 带下拉功能的text field
		// 使用方法：ComBoTextField.setupAutoComplete(普通输入框, 下拉数据);
		// textField.setColumns(number);
		textField = new JTextField();
		textField.setLocation(104, 51);
		textField.setSize(90, 23);
		
		// 获取位置名
		List<String> stationNames = new ArrayList<>();
		for (String key : map.keySet()) {
			stationNames.add(key);
		}
		ComBoTextField.setDestination(textField, stationNames, map);
		textField.setColumns(30);
		textField.setColumns(10);
		
		labelStart = new JLabel("出发地");
		labelStart.setBounds(43, 52, 55, 18);
		
		JLabel jLabel = new JLabel("目的地");
		
	}
}
