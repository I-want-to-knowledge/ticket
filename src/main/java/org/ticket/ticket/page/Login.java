package org.ticket.ticket.page;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.ticket.ticket.config.ConfigUtils;
import org.ticket.ticket.page.method.LoginMethods;
import org.ticket.ticket.utils.XConstant;
import org.ticket.ticket.utils.http.ComBoTextField;
import org.ticket.ticket.utils.http.XBrowser;

/**
 * 登录
 *
 * @author YanZhen
 * 2018-11-19 14:52:18
 * Login
 */
public class Login {
	// private Logger LOG = LoggerFactory.getLogger(Login.class);
	
	public JFrame frame;// 页面
	public JLabel imageLabel;// 图片标签
	public JTextField jTextField;// 文本框
	public JPasswordField pwdField;// 密码字段
	public JLabel hintLabel;// 提示标签
	public JCheckBox jCheckBox;// 记住
	private static LoginMethods loginMethods;
	
	/**
	 * 登录页
	 *
	 * 2018-11-21 20:57:39 void
	 */
	public static void start() {
		EventQueue.invokeLater(() -> {
			Login page = new Login();
			loginMethods = new LoginMethods(page);
			page.frame.setVisible(true);// 展示页面
		});
	}

	public Login() {
		XBrowser.getInstance();
		initialize();
	}

	// 初始化登录页面
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 登录对话框布局
		frame = new JFrame("欢迎使用XXXX抢票工具");
		frame.setBounds(100, 100, 440, 465);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// 输入框布局
		jTextField = new JTextField();
		jTextField.setBounds(123, 23, 230, 33);
		frame.getContentPane().add(jTextField);
		jTextField.setColumns(10);
		
		// 标题
		JLabel userLabel = new JLabel("用户名");
		userLabel.setBounds(61, 32, 54, 15);
		frame.getContentPane().add(userLabel);
		
		JLabel pwdLabel = new JLabel("密码");
		pwdLabel.setBounds(61, 76, 54, 15);
		frame.getContentPane().add(pwdLabel);
		
		JLabel verificationLabel = new JLabel("验证码");
		verificationLabel.setBounds(61, 114, 54, 15);
		frame.getContentPane().add(verificationLabel);
		
		// 验证码图片部分
		final JComponent layeredPane = frame.getLayeredPane();
		
		imageLabel = new JLabel(new ImageIcon(LoginMethods.getVerificationCode()));
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					ImageIcon imageIcon = new ImageIcon(XConstant.Jpg.JPG_12306_URL);
					final JLabel jLabel = new JLabel(imageIcon);
					jLabel.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							// 从容器中删除组件
							layeredPane.remove(jLabel);
							// 重绘该组件
							layeredPane.repaint();
						};
					});
					// 将按钮放置在面板之下
					layeredPane.add(jLabel, -3);
					int iconWidth = imageIcon.getIconWidth();
					int iconHeight = imageIcon.getIconHeight();
					jLabel.setSize(iconWidth, iconHeight);
					jLabel.setLocation(e.getX() + 60 - (iconWidth / 2), e.getY() + 139 - (iconHeight / 2));
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					loginMethods.checkVerificationCode();
				}
			}
		});
		imageLabel.setBounds(60, 139, 293, 191);
		frame.getContentPane().add(imageLabel);
		
		// 刷新
		JButton refreshButton = new JButton("刷新");
		refreshButton.addActionListener(e -> {
			// 获取验证码图片
			imageLabel.setIcon(new ImageIcon(LoginMethods.getVerificationCode()));
			// 删除原验证码
			JComponent layeredPane2 = frame.getLayeredPane();
			Component[] components = layeredPane2.getComponents();
			for (Component component : components) {
				if (component instanceof JLabel) {
					layeredPane2.remove(component);
				}
			}
			// 重绘
			frame.repaint();
		});
		// 刷新按钮的位置
		refreshButton.setBounds(123, 110, 104, 23);
		frame.getContentPane().add(refreshButton);
		
		// 密码区
		pwdField = new JPasswordField("");// 请输入密码！
		pwdField.setEchoChar('*');
		pwdField.setToolTipText("you password!");
		pwdField.setBounds(123, 67, 230, 33);
		frame.getContentPane().add(pwdField);
		
		// 提示
		hintLabel = new JLabel("提示：验证码选完，右击即可提交！");
		hintLabel.setBounds(61, 340, 337, 29);
		hintLabel.setForeground(Color.blue);
		frame.getContentPane().add(hintLabel);
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					loginMethods.checkVerificationCode();
				}
			}
		});
		
		// 登录
		JButton loginButton = new JButton("登录");
		loginButton.addActionListener(e -> {
			loginMethods.checkVerificationCode();
		});
		loginButton.setBounds(61, 366, 303, 51);
		frame.getContentPane().add(loginButton);
		
		// 记住用户
		List<String> arrayList = new ArrayList<>();
		Map<String, String> map = null;
		try {
			map = ConfigUtils.getInstance().map;
		} catch (Exception e) {
			hintLabel.setText("记住用户处理错误(" + e.getMessage() + ")");
			hintLabel.setForeground(Color.red);
			// LOG.error("记住用户处理错误，错误信息：{}", e);
			e.printStackTrace();
		}
		for (String key : map.keySet()) {
			arrayList.add(key);
		}
		
		// 按键自动完成
		ComBoTextField.setUpAutoComplete(jTextField, pwdField, arrayList);
		
		jCheckBox = new JCheckBox("记住");
		jCheckBox.setSelected(true);
		jCheckBox.setBounds(354, 29, 116, 21);
		frame.getContentPane().add(jCheckBox);
		LoginMethods.ticket_init();
	}
}
