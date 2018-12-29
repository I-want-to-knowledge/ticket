package org.ticket.ticket.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.page.method.AutoBrushTicketMethod;
import org.ticket.ticket.page.method.OrderMethod;
import org.ticket.ticket.page.method.TicketOrderMethods;
import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.ComBoTextField;
import org.ticket.ticket.utils.http.HttpUtils;
import org.ticket.ticket.utils.http.XBrowser;
import org.ticket.ticket.utils.http.XHttpUtils;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.method.impl.XHttpPost;
import org.ticket.ticket.utils.other.Chooser;
import org.ticket.ticket.utils.other.JTextAreaExt;
import org.ticket.ticket.utils.other.PopUp;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

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
	public List<JSONObject> trainTypeDatas = new ArrayList<>();
	public JTextField departTextField;// 出发地
	public JTextField dateTextField;
	public JTextField destinationTextField;
	public JTable table;// 车次信息表
	public JButton queryTicketButton;
	public int ticketType = 0;// 查票类型（1自动刷票，0及其他为手动）
	public boolean isRun = false;// 自动刷票是否正在运行
	public boolean result = true;// 是否自动刷票
	public JTextAreaExt textArea;
	public DateFormat HMS_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public DateFormat YMD_FORMAT = new SimpleDateFormat("yyyy:MM:dd");
	public JPanel row_panel;// 获取行数据
	public JPanel column_panel;// 获取列数据
	public JList<Object> passengers;// 乘车人
	public JList<Object> seatTypes;// 座位类型
	private JList<Object> checkTicketPattern;// 查票模式
	public String purchaseTicketURLParams;// 购票链接参数
	public int[] seatCodes;// 席别编号
	public DefaultListModel<Object> trainsModel = new DefaultListModel<>();// 火车车次
	public Map<String, JSONObject> userInfoMap = new HashMap<>();// 购票人信息，key：用户名
	public String globalRepeatSubmitToken = "";// 全局重复提交令牌
	public String keyCheckIsChange = "";// 检查key是否变化
	private JPanel carTypePanel;// 车型面板
	private JPanel seatPanel;// 席别面板
	public JTable orderTable;// 订单界面-订单列表

	public int[] mouseLocation = new int[2];// 鼠标的位置
	
	/** 开车时间 */
	private String START_TIME = "start_time";
	
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
			LOG.error("赋值‘系统外观类名’报错！", e);
		}
		
		// 创建我的首页
		frame = new JFrame("My home page");
		frame.setBounds(100, 100, 1110, 720);// 窗口大小
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 关闭窗口
		Container frameContentPane = frame.getContentPane();// frame content pane
		frameContentPane.setLayout(null);
		
		JLabel carTypeLabel = new JLabel("车型");
		carTypeLabel.setBounds(43, 89, 41, 18);
		JLabel seatTypeLabel = new JLabel("席别");
		seatTypeLabel.setBounds(43, 123, 41, 18);
		
		// 出发地时间年龄选择控件
		basicInfo();
		
		// 查刷票模式选择控件
		ticketModelSelected();
		
		// 车次展示表格，滑动框
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(43, 164, 1001, 237);
		
		// 车次信息表
		table = new JTable();
		table.setRowHeight(20);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int[] rows = table.getSelectedRows();
					for (int row : rows) {
						if (!trainsModel.contains(table.getValueAt(row, 0))) {
							trainsModel.addElement(table.getValueAt(row, 0));
						}
					}
				}
				
				if (e.getClickCount() == 2) {
					new TicketOrderMethods(mypage).start();
				}
			}
		});
		table.setFillsViewportHeight(true);// 表格填充控件
		table.setSurrendersFocusOnKeystroke(true);
		table.setFont(new Font("宋体", Font.PLAIN, 12));
		scrollPane.setViewportView(table);// 表格放入控件
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);// 设置选择区间
        table.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{XConstant.UnicodeString.UNICODE_CC, XConstant.UnicodeString.UNICODE_CFD,
                        XConstant.UnicodeString.UNICODE_MDD, XConstant.UnicodeString.UNICODE_LS,
                        XConstant.UnicodeString.UNICODE_FCSJ, XConstant.UnicodeString.UNICODE_DDSJ,
                        XConstant.UnicodeString.UNICODE_SWZ, XConstant.UnicodeString.UNICODE_TDZ,
                        XConstant.UnicodeString.UNICODE_YDZ, XConstant.UnicodeString.UNICODE_EDZ,
                        XConstant.UnicodeString.UNICODE_GJRW, XConstant.UnicodeString.UNICODE_RW,
                        XConstant.UnicodeString.UNICODE_YW, XConstant.UnicodeString.UNICODE_RZ,
                        XConstant.UnicodeString.UNICODE_YZ, XConstant.UnicodeString.UNICODE_WZ,
                        XConstant.UnicodeString.UNICODE_QT, XConstant.UnicodeString.UNICODE_BZ}));
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < 17; i++) {
			switch (i) {
				case 0:
					columnModel.getColumn(i).setPreferredWidth(55);
					break;
				case 1:
				case 2:
				case 4:
				case 5:
					columnModel.getColumn(i).setPreferredWidth(70);
					break;

				default:
					columnModel.getColumn(i).setPreferredWidth(65);
					break;
			}
		}
		
		// 设置表格样式
		setTableStyle();
		
		// 分页选项卡
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// 刷票窗
		brushTicketPane(tabbedPane);
		
		// 订单窗
		orderPane(tabbedPane);
		
		// 消息提醒
		messageRemindingPane(tabbedPane);
		
		// 其它功能
		otherPane(tabbedPane);
		
		// 车型面板1
		carTypePane();
		
		// 席别面板
		seatPane();
		
		// 互换 图标信息
		JLabel arrowLabel = new JLabel(new ImageIcon(XConstant.Jpg.ARROW));
		arrowLabel.setBounds(198, 54, 30, 13);
		arrowLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String depart = departTextField.getText();// 出发地
				String destination = destinationTextField.getText();// 目的地
				
				// 互换信息
				departTextField.setText(destination);
				destinationTextField.setText(depart);
			}
		});
		arrowLabel.setText("☜☞");
		
		// 主面板添加控件
		frameContentPane.setLayout(null);
		frameContentPane.add(tabbedPane);
		frameContentPane.add(scrollPane);
		frameContentPane.add(carTypeLabel);
		frameContentPane.add(seatTypeLabel);
		frameContentPane.add(arrowLabel);
		
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {// 鼠标点击完成事件
				// 判断按鼠标的那个键
				if (e.getButton() == MouseEvent.BUTTON1) {// 1左键，3右键
					// 鼠标的坐标
					mouseLocation[0] = e.getX();
					mouseLocation[1] = e.getY();
				}
			}
		});
	}
	
	/**
	 * 查刷票选择
	 *
	 * 2018-12-26 11:39:20 void
	 */
	private void ticketModelSelected() {

		queryTicketButton = new JButton("手动查票");
		queryTicketButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 自动和手动切换
				if (ticketType == 1) {
					if (isRun) {
						isRun = false;
						result = false;
						printLog("已停止刷票");
						queryTicketButton.setText("自动刷票");
					} else {
						isRun = true;
						queryTicketButton.setText("停止刷票");
						checkAllColumnRow();
						checkBrushTicketInfo();
					}
				} else {
					checkBrushTicketInfo();
				}
			}
		});
		queryTicketButton.setBounds(953, 85, 86, 67);
		
		JCheckBox brushTicketCheckBox = new JCheckBox("刷票模式");
		brushTicketCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (brushTicketCheckBox.isSelected()) {
					queryTicketButton.setText("自动刷票");
					ticketType = 1;
				} else {
					if (isRun) {
						printLog("请先停止刷票");
						brushTicketCheckBox.setSelected(true);
					} else {
						queryTicketButton.setText("手动刷票");
						ticketType = 0;
					}
				}
			}
		});
		brushTicketCheckBox.setBounds(945, 53, 100, 21);
		frame.getContentPane().add(brushTicketCheckBox);
		frame.getContentPane().add(queryTicketButton);
	}

	/**
	 * 基本信息选择
	 * 出发地、目的地、日期、人类型
	 *
	 * 2018-12-25 17:14:03 void
	 */
	private void basicInfo() {
		// 出发地
		JLabel labelStart = new JLabel("出发地");
		labelStart.setBounds(43, 52, 55, 18);
		// 输入框信息
		// 拓展：ComBoTextField 带下拉功能的text field
		// 使用方法：ComBoTextField.setupAutoComplete(普通输入框, 下拉数据);
		// textField.setColumns(number);
		departTextField = new JTextField();
		departTextField.setLocation(104, 51);
		departTextField.setSize(90, 23);
		// 获取位置名
		List<String> stationNames = new ArrayList<>(map.keySet());
		/*for (String key : map.keySet()) {
			stationNames.add(key);
		}*/
		ComBoTextField.setUpAutoComplete(departTextField, stationNames, map);
		departTextField.setColumns(30);
		departTextField.setColumns(10);
		
		// 目的地
		JLabel labelEnd = new JLabel("目的地");
		labelEnd.setBounds(236, 54, 55, 18);
		// 目的地输入框
		destinationTextField = new JTextField();
		destinationTextField.setLocation(281, 53);
		destinationTextField.setSize(90, 23);
		ComBoTextField.setUpAutoComplete(destinationTextField, stationNames, map);
		destinationTextField.setColumns(10);
		
		// 日期选择
		JLabel labelDate = new JLabel("日期");
		labelDate.setBounds(382, 54, 41, 21);
		// 日期输入框
		dateTextField = new JTextField();
		dateTextField.setBounds(431, 54, 86, 23);
		// 日期格式调整
		Chooser chooser = Chooser.getInstance();
		chooser.register(dateTextField);
		dateTextField.setColumns(10);
		
		JLabel departLabel = new JLabel("发车时间");
		departLabel.setBounds(529, 55, 65, 18);
		
		JComboBox<Object> comboBox = new JComboBox<>();
		comboBox.setBounds(600, 53, 105, 21);
		comboBox.addActionListener(e -> {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);

			// 解析选择的时间范围
			String[] timeArr = Objects.requireNonNull(comboBox.getSelectedItem()).toString().split(XConstant.Symbol.LINK_);
			// 没有时间区间，搜索全天
			if (timeArr.length != 2) {
				timeArr = new String[] {"00:00", "24:00"};
			}

			// 所有车型的数据
			for (JSONObject json : trainTypeDatas) {
				String departTime = json.getString(START_TIME);
				if (departTime.compareTo(timeArr[0]) >= 0
						&& departTime.compareTo(timeArr[1]) < 0) {
					addRow(new String[]{}, json);
				}
			}
		});
		
		comboBox.setFont(new Font(null, Font.PLAIN, 12));
		comboBox.setModel(new DefaultComboBoxModel<>(
				new String[] { "00:00—24:00", "00:00—08:00", "08:00—12:00", "12:00—20:00", "20:00—24:00" }));
		
		// 选择框，位置
		JRadioButton adult = new JRadioButton("成人");
		adult.setLocation(714, 52);
		adult.setSize(60, 21);
		
		// 选择框，位置
		JRadioButton student = new JRadioButton("学生");
		student.setLocation(771, 52);
		student.setSize(60, 21);
		
		// 选择框，位置
		JRadioButton children = new JRadioButton("儿童");
		children.setLocation(827, 52);
		children.setSize(60, 21);
		
		// 选择框，位置
		JRadioButton soldier_handicapped = new JRadioButton("军残");
		soldier_handicapped.setBounds(885, 52, 60, 21);
		
		// 添进组
		ButtonGroup group = new ButtonGroup();
		group.add(adult);
		group.add(student);
		group.add(children);
		group.add(soldier_handicapped);
		
		// 添加主面板
		Container contentPane = frame.getContentPane();
		contentPane.add(labelStart);
		contentPane.add(departTextField);
		contentPane.add(labelEnd);
		contentPane.add(destinationTextField);
		contentPane.add(labelDate);
		contentPane.add(dateTextField);
		contentPane.add(departLabel);
		contentPane.add(comboBox);
		contentPane.add(adult);
		contentPane.add(student);
		contentPane.add(children);
		contentPane.add(soldier_handicapped);
	}

	/**
	 * 席别面板填充
	 *
	 * 2018-12-25 13:56:51 void
	 */
	private void seatPane() {
		// 席别面板
		seatPanel = new JPanel();
		seatPanel.setBounds(104, 117, 837, 31);
		seatPanel.setLayout(null);
		
		// 全部席别
		JCheckBox allCheckBox = new JCheckBox(XConstant.SeatType.SEAT_ALL);
		allCheckBox.setBounds(17, 4, 86, 23);
		allCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component[] components = seatPanel.getComponents();
				for (Component c : components) {
					if (c instanceof JCheckBox) {
						JCheckBox checkBox = ((JCheckBox) c);
						// 全部席别选中，则所有席别选中，否则...
						checkBox.setSelected(allCheckBox.isSelected());
						
						// 列表中展示该席别的信息
						if (!XConstant.SeatType.SEAT_ALL.equals(checkBox.getText())) {
							// 列表中的总列数
							int columnCount = table.getColumnModel().getColumnCount();
							// 改 Check Box 在几列
							int columnNum = 0;
							for (int i = 0; i < columnCount; i++) {
								if (checkBox.getText().equals(table.getColumnModel().getColumn(i).getHeaderValue())) {
									columnNum = i;
									break;
								}
							}
							seatTypeSelected(checkBox, columnNum, checkBox.getWidth(), seatPanel, allCheckBox);
						}
					}
				}
			}
		});
		allCheckBox.setSelected(true);// 默认选中
		seatPanel.add(allCheckBox);
		
		// 商务
		JCheckBox swzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_SWZ);
		swzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(swzCheckBox, 6, 65, seatPanel, allCheckBox);
			}
		});
		swzCheckBox.setBounds(107, 4, 62, 23);
		swzCheckBox.setSelected(true);// 默认选中
		seatPanel.add(swzCheckBox);
		
		// 特等
		JCheckBox tdzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_TDZ);
		tdzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(tdzCheckBox, 7, 65, seatPanel, allCheckBox);
			}
		});
		tdzCheckBox.setBounds(173, 4, 62, 23);
		tdzCheckBox.setSelected(true);// 默认选中
		seatPanel.add(tdzCheckBox);
		
		// 一等
		JCheckBox ydzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_YDZ);
		ydzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(ydzCheckBox, 8, 65, seatPanel, allCheckBox);
			}
		});
		ydzCheckBox.setBounds(239, 4, 62, 23);
		ydzCheckBox.setSelected(true);// 默认选中
		seatPanel.add(ydzCheckBox);
		
		// 二等
		JCheckBox edzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_EDZ);
		edzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(edzCheckBox, 9, 65, seatPanel, allCheckBox);
			}
		});
		edzCheckBox.setBounds(305, 4, 62, 23);
		edzCheckBox.setSelected(true);// 默认选中
		seatPanel.add(edzCheckBox);
		
		// 高软
		JCheckBox gjrwCheckBox = new JCheckBox(XConstant.SeatType.SEAT_GJRW);
		gjrwCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(gjrwCheckBox, 10, 65, seatPanel, allCheckBox);
			}
		});
		gjrwCheckBox.setBounds(371, 4, 62, 23);
		gjrwCheckBox.setSelected(true);
		seatPanel.add(gjrwCheckBox);
		
		// 软卧
		JCheckBox rwCheckBox = new JCheckBox(XConstant.SeatType.SEAT_RW);
		rwCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(rwCheckBox, 11, 65, seatPanel, allCheckBox);
			}
		});
		rwCheckBox.setBounds(437, 4, 62, 23);
		rwCheckBox.setSelected(true);
		seatPanel.add(rwCheckBox);
		
		// 硬卧
		JCheckBox ywCheckBox = new JCheckBox(XConstant.SeatType.SEAT_YW);
		ywCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(ywCheckBox, 12, 65, seatPanel, allCheckBox);
			}
		});
		ywCheckBox.setBounds(503, 4, 62, 23);
		ywCheckBox.setSelected(true);
		seatPanel.add(ywCheckBox);
		
		// 软座
		JCheckBox rzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_RZ);
		rzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(rzCheckBox, 13, 65, seatPanel, allCheckBox);
			}
		});
		rzCheckBox.setBounds(569, 4, 62, 23);
		rzCheckBox.setSelected(true);
		seatPanel.add(rzCheckBox);
		
		// 硬座
		JCheckBox yzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_YZ);
		yzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(yzCheckBox, 14, 65, seatPanel, allCheckBox);
			}
		});
		yzCheckBox.setBounds(635, 4, 62, 23);
		yzCheckBox.setSelected(true);
		seatPanel.add(yzCheckBox);
		
		// 无座
		JCheckBox wzCheckBox = new JCheckBox(XConstant.SeatType.SEAT_WZ);
		wzCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(wzCheckBox, 15, 65, seatPanel, allCheckBox);
			}
		});
		wzCheckBox.setBounds(701, 4, 62, 23);
		wzCheckBox.setSelected(true);
		seatPanel.add(wzCheckBox);
		
		// 其他
		JCheckBox qtCheckBox = new JCheckBox(XConstant.SeatType.SEAT_QT);
		qtCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seatTypeSelected(qtCheckBox, 16, 65, seatPanel, allCheckBox);
			}
		});
		qtCheckBox.setBounds(767, 4, 62, 23);
		qtCheckBox.setSelected(true);
		seatPanel.add(qtCheckBox);
		
		frame.getContentPane().add(seatPanel);
	}

	/**
	 * 车型面板填充
	 *
	 * 2018-12-25 13:55:28 void
	 */
	private void carTypePane() {
		carTypePanel = new JPanel();
		carTypePanel.setBounds(104, 80, 837, 31);
		carTypePanel.setLayout(null);
		
		JCheckBox trainsAllCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_ALL);
		trainsAllCheckBox.setBounds(14, 5, 92, 23);
		trainsAllCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component[] components = carTypePanel.getComponents();
				if (trainsAllCheckBox.isSelected()) {
					for (Component c : components) {
						if (c instanceof JCheckBox) {
							((JCheckBox) c).setSelected(true);
						}
					}
					
					// 显示全部车次
					for (JSONObject timeJson : trainTypeDatas) {
						addRow(new String[]{}, timeJson);
					}
				} else {
					for (Component c : components) {
						if (c instanceof JCheckBox) {
							((JCheckBox) c).setSelected(false);
						}
					}
					
					// 隐藏全部车次
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setRowCount(0);
				}
			}
		});
		trainsAllCheckBox.setSelected(true);
		carTypePanel.add(trainsAllCheckBox);
		
		// 高铁
		JCheckBox gtCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_GT);
		gtCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(gtCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_G);
			}
		});
		gtCheckBox.setBounds(110, 5, 80, 23);
		gtCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(gtCheckBox);
		
		// 城铁
		JCheckBox ctCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_CT);
		ctCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(ctCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_C);
			}
		});
		ctCheckBox.setBounds(194, 5, 80, 23);
		ctCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(ctCheckBox);
		
		// 动车
		JCheckBox dcCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_DC);
		dcCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(dcCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_D);
			}
		});
		dcCheckBox.setBounds(278, 5, 80, 23);
		dcCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(dcCheckBox);
		
		// 特快
		JCheckBox tkCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_TK);
		tkCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(tkCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_T);
			}
		});
		tkCheckBox.setBounds(362, 5, 80, 23);
		tkCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(tkCheckBox);
		
		// 直达
		JCheckBox zdCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_ZD);
		zdCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(zdCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_Z);
			}
		});
		zdCheckBox.setBounds(446, 5, 80, 23);
		zdCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(zdCheckBox);
		
		// 快车
		JCheckBox kcCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_KC);
		kcCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(kcCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_K);
			}
		});
		kcCheckBox.setBounds(530, 5, 80, 23);
		kcCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(kcCheckBox);
		
		// 普客
		JCheckBox pkCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_PK);
		pkCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(pkCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_STAR);
			}
		});
		pkCheckBox.setBounds(614, 5, 58, 23);
		pkCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(pkCheckBox);
		
		// 临客
		JCheckBox lkCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_LK);
		lkCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(lkCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_STAR);
			}
		});
		lkCheckBox.setBounds(686, 5, 58, 23);
		lkCheckBox.setSelected(true);
		carTypePanel.add(lkCheckBox);
		
		// 其它
		JCheckBox qtCheckBox = new JCheckBox(XConstant.TrainType.TRAIN_QT);
		qtCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainNumSelected(qtCheckBox, carTypePanel, trainsAllCheckBox, XConstant.TrainType.TRAIN_STAR);
			}
		});
		qtCheckBox.setBounds(758, 5, 58, 23);
		qtCheckBox.setSelected(true);// 默认选中
		carTypePanel.add(qtCheckBox);
		
		frame.getContentPane().add(carTypePanel);
	}

	/**
	 * 是否全部选中，选中则勾选 ‘全部车次’ 选择框
	 *
	 * 2018-12-24 17:39:10 void
	 * @param checkBox 车型选择框
	 * @param carTypePanel 车型选择面板
	 * @param trainsAllCheckBox ‘全部车次’选择框
	 * @param train 车型首字母
	 */
	private void trainNumSelected(JCheckBox checkBox, JPanel carTypePanel, JCheckBox trainsAllCheckBox, String train) {
		// 查看该车型是否选中
		if (checkBox.isSelected()) {
			// 选中时
			Component[] components = carTypePanel.getComponents();
			boolean checked = true;// 选中的状态
			for (Component c : components) {
				if (c instanceof JCheckBox) {
					JCheckBox cb = (JCheckBox) c;
					
					// 全部车型不判断
					if (XConstant.TrainType.TRAIN_ALL.equals(cb.getText())) {
						continue;
					}
					if (!cb.isSelected()) {
						checked = false;// 不选中
						break;
					}
				}
			}
			
			// 查看车型的选中状态
			trainsAllCheckBox.setSelected(checked);
			
			// 列表显示该车次
			for (JSONObject trainDataJson : trainTypeDatas) {
				String trainCode = trainDataJson.getString("station_train_code");
				
				// 判断是否为该车型
				if (trainCode.toUpperCase().startsWith(train)) {
					// 添加列表中
					addRow(new String[] {}, trainDataJson);
				}
			}
		} else {
			// 未选中时，全部车次选择框取消勾选
			trainsAllCheckBox.setSelected(false);
			
			// 列表删除该车次
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			int rowCount = model.getRowCount();
			for (int i = rowCount - 1; i >= 0; i++) {
				String cellValue = table.getValueAt(i, 0).toString();
				
				// 是该车型则移除
				if (cellValue.toUpperCase().startsWith(train)) {
					model.removeRow(i);
				}
			}
		}
	}

	/**
	 * 其它功能面板
	 *
	 * 2018-12-24 14:08:18
	 * @param tabbedPane void
	 */
	private void otherPane(JTabbedPane tabbedPane) {
		// 其它功能面板
		JPanel otherPanel = new JPanel();
		tabbedPane.add(otherPanel, XConstant.PanelString.PANEL_QTGN);
		
		// 面板布局
		GroupLayout otherGroup = new GroupLayout(otherPanel);
		otherGroup.setHorizontalGroup(otherGroup.createParallelGroup(Alignment.LEADING).addGap(0, 996, Short.MAX_VALUE));
		otherGroup.setVerticalGroup(otherGroup.createParallelGroup(Alignment.LEADING).addGap(0, 205, Short.MAX_VALUE));
		otherPanel.setLayout(otherGroup);
	}

	/**
	 * 消息提醒面板
	 *
	 * 2018-12-24 14:08:46
	 * @param tabbedPane void
	 */
	private void messageRemindingPane(JTabbedPane tabbedPane) {
		JPanel messageRemindingPanel = new JPanel();
		tabbedPane.add(messageRemindingPanel, XConstant.PanelString.PANEL_XXTX);
	}

	/**
	 * 订单界面
	 *
	 * 2018-12-20 16:39:58 void
	 * @param tabbedPane 分页容器
	 */
	private void orderPane(JTabbedPane tabbedPane) {
		JPanel orderPanel = new JPanel();
		orderPanel.setLayout(null);
		tabbedPane.add(orderPanel, XConstant.PanelString.PANEL_DDJM);
		
		JButton brushOrderButton = new JButton("刷新订单列表");
		brushOrderButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 按钮禁用
				brushOrderButton.setEnabled(false);
				
				// 启动线程，不影响ui
				new Thread(() -> OrderMethod.getOrderList(brushOrderButton, mypage)).start();
			}
		});
		brushOrderButton.setBounds(20, 19, 105, 32);
		orderPanel.add(brushOrderButton);
		
		// 订单表
		orderTable = new JTable();
		orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		orderTable.setFillsViewportHeight(true);
		orderTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { XConstant.UnicodeString.UNICODE_CC,
				XConstant.UnicodeString.UNICODE_DDH, XConstant.UnicodeString.UNICODE_CKXM, XConstant.UnicodeString.UNICODE_FCSJ,
				XConstant.UnicodeString.UNICODE_CFD, XConstant.UnicodeString.UNICODE_MDD, XConstant.UnicodeString.UNICODE_PZ,
				XConstant.UnicodeString.UNICODE_XB, XConstant.UnicodeString.UNICODE_CX, XConstant.UnicodeString.UNICODE_ZW,
				XConstant.UnicodeString.UNICODE_PJ, XConstant.UnicodeString.UNICODE_ZT }));
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(124);
		
		// 订单表，滑动面板
		JScrollPane orderTableScroll = new JScrollPane(orderTable);
		orderTableScroll.setBounds(10, 42, 976, 140);
		orderPanel.add(orderTableScroll);
		
		// 订单操作面板
		JPanel orderOperationPanel = new JPanel();
		orderOperationPanel.setBounds(10, 0, 465, 60);
		orderOperationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"订单操作：", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.controlText));
		orderOperationPanel.setLayout(null);
		orderPanel.add(orderOperationPanel);
		
		JButton payButton = new JButton("继续支付");
		payButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LOG.info("继续支付！");
			}
		});
		payButton.setBounds(205, 19, 81, 32);
		orderOperationPanel.add(payButton);
		
		JButton cancelButton = new JButton("取消订单");
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 订单行数
				if (orderTable.getSelectedRows().length > 0) {
					Object orderNo = orderTable.getValueAt(orderTable.getSelectedRow(), 1);
					if (orderNo != null) {
						int showConfirmDialog = JOptionPane.showConfirmDialog(null, "是否取消订单？", "确认框", JOptionPane.YES_NO_OPTION);
						if (showConfirmDialog == 0) {
							cancelButton.setEnabled(false);
							new Thread(() -> OrderMethod.cancelOrder(orderNo.toString(), cancelButton, mypage)).start();
						}
					}
				}
			}
		});
		cancelButton.setBounds(120, 19, 81, 32);
		orderOperationPanel.add(cancelButton);
		
		JButton endorseTicketButton = new JButton("改签");
		endorseTicketButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LOG.info("改签！");
			}
		});
		endorseTicketButton.setBounds(290, 19, 81, 32);
		orderOperationPanel.add(endorseTicketButton);
		
		JButton refundTicketButton = new JButton("退票");
		refundTicketButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LOG.info("退票！");
			}
		});
		refundTicketButton.setBounds(375, 19, 81, 32);
		orderOperationPanel.add(refundTicketButton);
	}

	/**
	 * 刷票界面
	 *
	 * 2018-12-19 15:18:40 void
	 * @param tabbedPane 分页容器
	 */
	private void brushTicketPane(JTabbedPane tabbedPane) {
		// 刷票界面
		JPanel brushTicketPanel = new JPanel();
		tabbedPane.add(brushTicketPanel, XConstant.PanelString.PANEL_SPJM);
		
		tabbedPane.setBounds(43, 411, 1001, 242);
		brushTicketPanel.setLayout(null);
		
		textArea = new JTextAreaExt();
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					textArea.setText("");
					printLog("信息输出区输出完毕！");
				}
			}
		});
		textArea.setEnabled(false);// 是否启用组件
		textArea.setEditable(false);// 是否可编辑
		textArea.setLineWrap(true);// 换行策略
		textArea.setBorder(BorderFactory.createTitledBorder("信息输出："));
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 不显示水平滚动条
		textScrollPane.setBounds(0, 0, 413, 205);
		brushTicketPanel.add(textScrollPane);
		
		// 乘车信息设置
		JPanel ridePanel = new JPanel();
		ridePanel.setBorder(BorderFactory.createTitledBorder("乘车信息设置："));
		ridePanel.setBounds(423, 5, 563, 200);
		ridePanel.setLayout(null);
		brushTicketPanel.add(ridePanel);
		
		JLabel brushTicketHz = new JLabel("刷票频率：");
		brushTicketHz.setBounds(8, 32, 80, 15);
		ridePanel.add(brushTicketHz);
		
		// 刷票频率选择框
		JSpinner brushTicketSpinner = new JSpinner();
		brushTicketSpinner.setBounds(91, 29, 78, 22);
		brushTicketSpinner.setModel(new SpinnerNumberModel(1000, 0, null, 100));
		ridePanel.add(brushTicketSpinner);
		
		JCheckBox WZCheckBox = new JCheckBox("不要无座");
		WZCheckBox.setBounds(180, 28, 110, 23);
		WZCheckBox.setSelected(true);// 默认选中
		ridePanel.add(WZCheckBox);
		
		JCheckBox submitPartCheckBox = new JCheckBox("部分提交");
		submitPartCheckBox.setBounds(300, 28, 90, 23);
		ridePanel.add(submitPartCheckBox);
		
		// 标签部分
		JLabel trainNumLabel = new JLabel("车次：");
		trainNumLabel.setBounds(10, 74, 69, 15);
		ridePanel.add(trainNumLabel);
		
		JLabel prioritySeatLabel = new JLabel("优先席别：");
		prioritySeatLabel.setBounds(10, 119, 80, 15);
		ridePanel.add(prioritySeatLabel);
		
		JLabel passengerLabel = new JLabel("乘客：");
		passengerLabel.setBounds(10, 167, 69, 15);
		ridePanel.add(passengerLabel);
		
		// 按钮部分
		JButton clearButton = new JButton("清空");
		clearButton.setBounds(480, 70, 73, 23);
		ridePanel.add(clearButton);
		
		JButton seatTypeButton = new JButton("席别");
		seatTypeButton.setBounds(480, 115, 73, 23);
		ridePanel.add(seatTypeButton);
		
		JButton passengerButton = new JButton("乘车人");
		passengerButton.setBounds(480, 163, 73, 23);
		ridePanel.add(passengerButton);
		
		// 输入框部分
		// 车次输入框
		JScrollPane trainNumScroll = new JScrollPane();
		trainNumScroll.setBounds(86, 70, 384, 25);
		checkTicketPattern = new JList<>(trainsModel);
		checkTicketPattern.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (checkTicketPattern.getSelectedIndex() > -1) {
					trainsModel.remove(checkTicketPattern.getSelectedIndex());
				}
			}
		});
		checkTicketPattern.setVisibleRowCount(1);
		checkTicketPattern.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 设置选择域，单个
		checkTicketPattern.setLayoutOrientation(JList.HORIZONTAL_WRAP);// 设置布局
		trainNumScroll.setViewportView(checkTicketPattern);
		ridePanel.add(trainNumScroll);
		
		// 席别输入框
		DefaultListModel<Object> seatModel = new DefaultListModel<>();
		seatModel.addElement(XConstant.SeatType.SEAT_SWZ);
		seatModel.addElement(XConstant.SeatType.SEAT_TDZ);
		seatModel.addElement(XConstant.SeatType.SEAT_YDZ);
		seatModel.addElement(XConstant.SeatType.SEAT_EDZ);
		seatModel.addElement(XConstant.SeatType.SEAT_GJRW);
		seatModel.addElement(XConstant.SeatType.SEAT_RW);
		seatModel.addElement(XConstant.SeatType.SEAT_YW);
		seatModel.addElement(XConstant.SeatType.SEAT_RZ);
		seatModel.addElement(XConstant.SeatType.SEAT_YZ);
		seatModel.addElement(XConstant.SeatType.SEAT_WZ);
		seatModel.addElement(XConstant.SeatType.SEAT_QT);
		
		// 席别类型
		seatTypes = new JList<>();
		
		// 席别的滚动面板
		JScrollPane seatScrollPane = new JScrollPane();
		seatScrollPane.setBounds(86, 117, 384, 25);
		seatScrollPane.setViewportView(seatTypes);
		ridePanel.add(seatScrollPane);
		
		// 鼠标按键事件处理
		PopUp.popUpMessage(seatTypeButton, seatTypes, seatModel);
		seatTypes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					if (seatTypes.getSelectedIndex() > -1) {
						DefaultListModel<Object> seatsModel = new DefaultListModel<>();
						for (int i = 0; i < seatTypes.getModel().getSize(); i++) {
							if (i != seatTypes.getSelectedIndex()) {
								seatsModel.addElement(seatTypes.getModel().getElementAt(i));
							}
						}
						seatTypes.setModel(seatsModel);
					}
				}
			}
		});
		seatTypes.setVisibleRowCount(1);
		seatTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 设置为单选
		seatTypes.setLayoutOrientation(JList.HORIZONTAL_WRAP);// 表格换行排列

		// 乘客输入框
		DefaultListModel<Object> passengerList = getPassengerList();
		passengers = new JList<>();
		
		// 滚动框
		JScrollPane passengerScroll = new JScrollPane();
		passengerScroll.setBounds(86, 163, 384, 25);
		passengerScroll.setViewportView(passengers);
		ridePanel.add(passengerScroll);
		
		PopUp.popUpMessage(passengerButton, passengers, passengerList);
		passengers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					if (passengers.getSelectedIndex() > -1) {
						DefaultListModel<Object> passengerModel = new DefaultListModel<>();
						for (int i = 0; i < passengers.getModel().getSize(); i++) {
							if (i != passengers.getSelectedIndex()) {
								passengerModel.addElement(passengers.getModel().getElementAt(i));// 用户姓名
							}
						}
						passengers.setModel(passengerModel);
					}
				}
			}
		});
		passengers.setVisibleRowCount(1);// 设置要显示的行数
		passengers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 设置为单选
		passengers.setLayoutOrientation(JList.HORIZONTAL_WRAP);// 表格换行排列
		
		// 清空按钮事件
		clearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				trainsModel.removeAllElements();
			}
		});
	}

	/**
	 * 获取乘客列表
	 *
	 * 2018-12-19 14:49:53
	 * @return DefaultListModel<Object>
	 */
	private DefaultListModel<Object> getPassengerList() {
		DefaultListModel<Object> seatsModel = new DefaultListModel<>();
		XHttpResponse resp = XBrowser.execute(new XHttpPost(XConstant.Url.GET_PASSENGER_DTO_S));
		String body = XHttpUtils.outHtml(resp.getBody());
		JSONObject bodyJson = JSONObject.parseObject(body);
		JSONObject dataJson = bodyJson.getJSONObject("data");
		if (dataJson.size() < 1) {
			textArea.append(HMS_FORMAT.format(new Date()) + "：" + bodyJson.getString("messages") + "\r\n");
		} else {
			// 所有乘客信息
			JSONArray userJsonArray = dataJson.getJSONArray("normal_passengers");
			for (Object userObj : userJsonArray) {
				// 单个乘客处理
				JSONObject userJson = (JSONObject) userObj;
				seatsModel.addElement(userJson.get("passenger_name"));
				userInfoMap.put(userJson.getString("passenger_name"), userJson);
			}
		}
		
		return seatsModel;
	}

	/**
	 * 效验刷票信息是否填写完毕
	 * 出发地、目的地、日期、乘车人、席别
	 *
	 * 2018-12-13 11:30:23 void
	 */
	private void checkBrushTicketInfo() {
		// 检查标识
		boolean check = true;
		
		// 出发地
		String departText = departTextField.getText();
		if (departText == null || departText.isEmpty()) {
			check = false;
			textArea.append(HMS_FORMAT.format(new Date()) + "：请输入出发地\r\n");
		}
		
		// 目的地
		String destinationText = destinationTextField.getText();
		if (destinationText == null || destinationText.isEmpty()) {
			check = false;
			textArea.append(HMS_FORMAT.format(new Date()) + "：请输入目的地\r\n");
		}
		
		// 日期
		String dateText = dateTextField.getText();
		if (dateText == null || dateText.isEmpty()) {
			check = false;
			textArea.append(HMS_FORMAT.format(new Date()) + "：请选择日期\r\n");
		}
		
		// 乘车人
		ListModel<Object> model = passengers.getModel();
		if (model.getSize() <= 0) {
			check = false;
			textArea.append(HMS_FORMAT.format(new Date()) + "：请选择乘车人\r\n");
		}
		
		// 席别
		if (seatTypes.getModel().getSize() <= 0) {
			check = false;
			textArea.append(HMS_FORMAT.format(new Date()) + "：请选择席别\r\n");
		}
		
		// 刷票的使用方法
		if (ticketType == 1) {
			if (checkTicketPattern.getModel().getSize() <= 0) {
				check = false;
				textArea.append(HMS_FORMAT.format(new Date()) + "：请先手动查询出车次，鼠标右键添加到车次列表，在进行自动刷票模式！");
			}
		}
		
		// 查看是否都满足情况
		if (!check) {
			return;
		}
		
		// 乘车人的信息
		StringJoiner str = new StringJoiner(",");
		for (int i = 0; i < model.getSize(); i++) {
			str.add(model.getElementAt(i).toString());
		}
		
		// 购买链接参数
		purchaseTicketURLParams = autoTicketPurchaseInfo();
		seatCodes = autoSeatCodeInfo();
		
		// 开始刷票
		new AutoBrushTicketMethod(mypage).start();
	}

	/**
	 * 组装座位类型的参数
	 *
	 * 2018-12-13 15:42:54
	 * @return int[]
	 */
	private int[] autoSeatCodeInfo() {
		// 席别
		ListModel<Object> model = seatTypes.getModel();
		int size = model.getSize();// 席别个数
		int[] seatCodes = new int[size];
		result = true;
		if (ticketType == 1) {
			for (int i = 0; i < size; i++) {
				switch (model.getElementAt(i).toString().trim()) {
					case XConstant.SeatType.SEAT_SWZ:
						seatCodes[i] = 32;
						break;
					case XConstant.SeatType.SEAT_TDZ:
						seatCodes[i] = 25;
						break;
					case XConstant.SeatType.SEAT_YDZ:
						seatCodes[i] = 31;
						break;
					case XConstant.SeatType.SEAT_EDZ:
						seatCodes[i] = 30;
						break;
					case XConstant.SeatType.SEAT_GJRW:
						seatCodes[i] = 21;
						break;
					case XConstant.SeatType.SEAT_RW:
						seatCodes[i] = 23;
						break;
					case XConstant.SeatType.SEAT_YW:
						seatCodes[i] = 28;
						break;
					case XConstant.SeatType.SEAT_RZ:
						seatCodes[i] = 24;
						break;
					case XConstant.SeatType.SEAT_YZ:
						seatCodes[i] = 29;
						break;
					case XConstant.SeatType.SEAT_WZ:
						seatCodes[i] = 26;
						break;
					case XConstant.SeatType.SEAT_QT:
						seatCodes[i] = 22;
						break;

					default:
						// 其它席别不处理
						LOG.warn("有其它席别存在，席别={}", model.getElementAt(i));
						break;
				}
			}
		}
		return seatCodes;
	}

	/**
	 * 购票参数拼接
	 *
	 * 2018-12-13 15:13:34
	 * @return String
	 */
	private String autoTicketPurchaseInfo() {
		StringJoiner sj = new StringJoiner("&");
		// 日期
		sj.add("leftTicketDTO.train_date=" + dateTextField.getText());
		// 出发地
		String[] departs = map.get(departTextField.getText());
		String depart = "";
		if (departs != null && departs.length > 1) {
			depart = departs[2];
		}
		sj.add("leftTicketDTO.from_station=" + depart);
		// 目的地
		String[] destinations = map.get(destinationTextField.getText());
		String destination = "";
		if (destinations != null && destinations.length > 1) {
			destination = destinations[2];
		}
		sj.add("leftTicketDTO.to_station=" + destination);
		// 
		sj.add("purpose_codes=ADULT");
		return sj.toString();
	}

	/**
	 * 选中所有行和列
	 *
	 * 2018-12-12 17:07:33 void
	 */
	private void checkAllColumnRow() {
		// row
		Component[] rows = row_panel.getComponents();
		for (Component row : rows) {
			// 选择框
			if (row instanceof JCheckBox) {
				((JCheckBox) row).setSelected(true);
			}
		}
		
		// 所有车型数据展示在列表
		for (JSONObject jsonObj : trainTypeDatas) {
			addRow(new String[]{}, jsonObj);
		}
		
		// column
		Component[] columns = column_panel.getComponents();
		for (Component column : columns) {
			if (column instanceof JCheckBox) {
				JCheckBox box = (JCheckBox) column;
				// 设置成选中状态
				box.setSelected(true);
				
				// 对选择框处理
				String title = box.getText();
				if (title != null && !XConstant.SeatType.SEAT_ALL.equals(title)) {
					// 表总列数
					TableColumnModel columnModel = table.getColumnModel();
					int columnCount = columnModel.getColumnCount();
					
					// 各个标题所在列
					int columnNum = 0;
					for (int i = 0; i < columnCount; i++) {
						if (title.equals(columnModel.getColumn(i).getHeaderValue())) {
							columnNum = i;
							break;
						}
					}
					
					seatTypeSelected(box, columnNum, box.getWidth(), column_panel, (JCheckBox) columns[0]);
				}
			}
		}
		
	}

	/**
	 * 席别选择，只展示选中的席别列的信息，没有全选时，取消‘全部席别’选中状态
	 *
	 * 2018-12-13 10:43:21
	 * @param checkBox 操作的选择框
	 * @param columnNum 列数
	 * @param width 列宽
	 * @param seatPanel 席别面板
	 * @param allCheckBox ‘所有席别’选择框
	 */
	private void seatTypeSelected(JCheckBox checkBox, int columnNum, int width, JPanel seatPanel, JCheckBox allCheckBox) {
		// 获取表的指定列
		TableColumn column = table.getColumnModel().getColumn(columnNum);
		// 选中时
		if (checkBox.isSelected()) {
			// 列宽
			column.setMinWidth(width);
			column.setMaxWidth(width);
			column.setPreferredWidth(width);
		} else {
			// 列宽
			column.setMinWidth(0);
			column.setMaxWidth(0);
			column.setPreferredWidth(0);
		}
		
		boolean check = true;// 选中状态
		
		// 查看是否有没选中的选择框
		Component[] components = seatPanel.getComponents();
		for (Component component : components) {
			if (component instanceof JCheckBox) {
				JCheckBox box = (JCheckBox) component;
				if (!XConstant.SeatType.SEAT_ALL.equals(box.getText())) {
					if (!box.isSelected()) {
						check = false;// 没有选中
						break;
					}
				}
			}
		}
		
		// ‘所有席别’选择框是否选中
		allCheckBox.setSelected(check);
	}

	/**
	 * 打印信息
	 *
	 * 2018-12-12 17:00:47
	 * @param data void
	 */
	public void printLog(String data) {
		textArea.append(HMS_FORMAT.format(new Date()) + ":" + data + "\r\n");
	}

	/**
	 * 添加车次（每行）
	 *
	 * 2018-12-12 14:06:36
	 * @param rows 多行
	 * @param jsonObj 添加车次数据
	 */
	public void addRow(String[] rows, JSONObject jsonObj) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		Vector<String> vector = new Vector<>();
		if (rows == null || rows.length == 0) {
			return;
		}
		
		// 解析车次
		if (rows.length > 32) {
			String sw = "--";
			String swDate = rows[32];
			if (swDate != null && !swDate.trim().isEmpty()) {
				sw = swDate;
			}
			
			// 车次
			vector.add(rows[3]);
			// 出发地
			vector.add(jsonObj.getString(rows[6]));
			// 目的地
			vector.add(jsonObj.getString(rows[7]));
			// 历时
			vector.add(rows[10]);
			// 发车时间
			vector.add(rows[8]);
			// 到达时间
			vector.add(rows[9]);
			// 商务
			vector.add(sw);
			// 特等
			vector.add(rows[25]);
			// 一等
			vector.add(rows[31]);
			// 二等
			vector.add(rows[30]);
			// 高软
			vector.add(rows[21]);
			// 软卧
			vector.add(rows[23]);
			// 硬卧
			vector.add(rows[28]);
			// 软座
			vector.add(rows[24]);
			// 硬座
			vector.add(rows[29]);
			// 无座
			vector.add(rows[26]);
			// 其他
			vector.add(rows[22]);
			// 备注
			if ("Y".equalsIgnoreCase(rows[11])) {
				vector.add("可预订");
			} else {
				vector.add("不可预订");
			}
		}
		
		// 添加该行
		model.addRow(vector);
	}

	
	/**
	 * 设置表格样式
	 *
	 * 2018-12-19 10:30:07 void
	 */
	public void setTableStyle() {
		// 表格样式，居中
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		
		// 设置表格的样式到页面
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setPreferredWidth(65);
			columnModel.getColumn(i).setCellRenderer(tcr);
		}
		
		for (int i = 4; i < 6; i++) {
			columnModel.getColumn(i).setPreferredWidth(90);
			columnModel.getColumn(i).setCellRenderer(tcr);
		}
	}
}
