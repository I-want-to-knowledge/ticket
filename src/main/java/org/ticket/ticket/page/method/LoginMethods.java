package org.ticket.ticket.page.method;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.StringJoiner;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.config.ConfigUtils;
import org.ticket.ticket.page.Login;
import org.ticket.ticket.utils.http.XBrowser;
import org.ticket.ticket.utils.http.XHttpUtils;
import org.ticket.ticket.utils.http.client.XHttpResponse;
import org.ticket.ticket.utils.http.method.XHttpMethods;
import org.ticket.ticket.utils.http.method.impl.XHttpGet;
import org.ticket.ticket.utils.http.method.impl.XHttpPost;
import org.ticket.ticket.utils.http.params.XParams;

import com.alibaba.fastjson.JSONObject;

/**
 * 登录页方法
 *
 * @author YanZhen
 * 2018-11-20 15:42:28
 * LoginMethod
 */
public class LoginMethods {
	private Logger LOG = LoggerFactory.getLogger(LoginMethods.class);
	
	private Login page;
	private StringJoiner newVerificationCode = new StringJoiner(",");
	private final static String LOGIN_INIT = "https://kyfw.12306.cn/otn/login/init";
	private final static String VERIFICATION_CODE = "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&";
	private final String CHECK_RAND_CODE_ANSYN = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
	private final String LOGIN_AYSN_SUGGEST = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
	private final String USER_LOGIN = "https://kyfw.12306.cn/otn/login/userLogin";

	public LoginMethods(Login page) {
		this.page = page;
	}
	
	/**
	 * 登录界面用于获取cookie
	 *
	 * 2018-11-26 16:26:43 void
	 */
	public static void ticket_init() {
		XHttpGet httpGet = new XHttpGet(LOGIN_INIT);
		XBrowser.execute(httpGet).getEntity().disconnect();
	}

	/**
	 * 获取验证码
	 *
	 * 2018-11-20 15:59:25
	 * @return byte[]
	 */
	public static byte[] getVerificationCode() {
		// https://kyfw.12306.cn/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&1542706422151&callback=jQuery19106652336679869479_1542706407795&_=1542706407797
		// https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.6551712691897946
		XHttpMethods verificationCode = new XHttpGet(VERIFICATION_CODE + Math.random());
		XHttpResponse response = XBrowser.execute(verificationCode);
		try {
			return XHttpUtils.inputStreamToByte(response.getBody());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 检查验证码
	 *
	 * 2018-11-22 10:56:57 void
	 */
	public void checkVerificationCode() {
		if (!IsChoiceCode()) {
			return;
		}
		
		Component[] components = page.frame.getLayeredPane().getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof JLabel) {
				JLabel lb = (JLabel) component;
				Integer x = lb.getX() - 64 + (lb.getIcon().getIconWidth()/2);
				Integer y = lb.getY() - 179 + (lb.getIcon().getIconHeight()/2);
				newVerificationCode.add(x.toString());
				newVerificationCode.add(y.toString());
			}
		}
		page.hintLabel.setText("当前的验证码是：" + newVerificationCode.toString());
		
		XHttpPost post = new XHttpPost(CHECK_RAND_CODE_ANSYN);
		XParams params = new XParams();
		params.put("rand", "sjrand");
		params.put("randCode", newVerificationCode.toString());
		post.setParams(params);// 组装数据提交form
		XHttpResponse resp = XBrowser.execute(post);
		String body = XHttpUtils.outHtml(resp.getBody());
		JSONObject jsonBody = JSONObject.parseObject(body);
		JSONObject jsonData = JSONObject.parseObject(jsonBody.getString("data"));
		if ("1".equals(jsonData.get(jsonData.getInteger("result")))) {
			page.hintLabel.setText("验证码正确，开始提交表单！");
			resp.getEntity().disconnect();
			login();
		} else {
			page.hintLabel.setText("验证码错误！");
			page.hintLabel.setForeground(Color.red);
			resp.getEntity().disconnect();
			page.hintLabel.setIcon(new ImageIcon(getVerificationCode()));
			clearCode();
		}
	}

	/**
	 * 清除历史验证码
	 *
	 * 2018-11-26 19:56:31 void
	 */
	private void clearCode() {
		// 清空历史记录
		newVerificationCode = new StringJoiner(",");
		
		// 删除组件中的验证码
		JComponent jCom = page.frame.getLayeredPane();
		Component[] components = jCom.getComponents();
		for (Component component : components) {
			if (component instanceof JLabel) {
				jCom.remove(component);
			}
		}
		
		// 重绘
		page.frame.repaint();
	}

	/**
	 * 登录
	 *
	 * 2018-11-26 17:56:09 void
	 */
	private void login() {
		// 开始提交信息
		XHttpPost post = new XHttpPost(LOGIN_AYSN_SUGGEST);
		XParams params = new XParams();
		params.put("loginUserDTO.user_name", page.jTextField.getText());
		params.put("randCode", newVerificationCode.toString());
		params.put("userDTO.password", new String(page.pwdField.getPassword()));
		
		// 组装数据
		post.setParams(params);
		
		// 请求获取结果
		XHttpResponse resp = XBrowser.execute(post);
		// 获取body
		String body = XHttpUtils.outHtml(resp.getBody());
		
		// 解析返回数据
		JSONObject jsonBody = JSONObject.parseObject(body);
		// 查看请求是否成功
		if (jsonBody.getBooleanValue("status")) {
			JSONObject jsonData = JSONObject.parseObject(jsonBody.getString("data"));
			if (jsonData != null && "Y".equals(jsonData.getString("loginCheck"))) {
				page.hintLabel.setText("登录成功，正在跳转到主页！");
			} else {
				LOG.error(jsonBody.getString("messages"));
			}
		} else {
			LOG.error(jsonBody.getString("messages"));
			System.exit(0);
		}
		resp.getEntity().disconnect();
		
		// 验证成功后，进行第二次登录
		post = new XHttpPost(USER_LOGIN);
		XParams params2 = new XParams();
		params2.put("_json_att", "");
		post.setParams(params2);
		resp = XBrowser.execute(post);
		if (resp.getEntity().getStatus() == 200) {
			if (XHttpUtils.outHtml(resp.getBody()).contains("欢迎您登录")) {
				page.hintLabel.setText("登录成功！");

				// 记住密码
				boolean selected = page.jCheckBox.isSelected();
				try {
					if (selected) {
						String[] userInfo = new String[] { page.jTextField.getText(), new String(page.pwdField.getPassword()) };
						ConfigUtils.getInstance().rememberUser(userInfo);
					} else {
						ConfigUtils.getInstance().removeUser(page.jTextField.getText());
					}
				} catch (Exception e) {
					LOG.error("记住密码错误：{}", e);
				}
			}
			page.frame.dispose();
			
			// 登录成功后首页
			// TODO:待完善...	HomePage
			
		} else {
			page.hintLabel.setText("登录失败！");
			page.hintLabel.setForeground(Color.red);
		}
		resp.getEntity().disconnect();
	}

	/**
	 * 先检查表单信息
	 *
	 * 2018-11-22 11:06:28
	 * @return boolean
	 */
	private boolean IsChoiceCode() {
		// 验证登录界面所产生的信息，输入的格式是否正确
		// 用户名
		String text = page.jTextField.getText();
		if (text == null || text.isEmpty()) {
			page.hintLabel.setText("请填写用户名！");
			page.hintLabel.setForeground(Color.red);
			return false;
		}
		
		// 密码
		char[] password = page.pwdField.getPassword();
		if (password.length < 6) {
			page.pwdField.setText((password.length == 0 ? "请填写密码！" : "密码至少填写6位！"));
			page.pwdField.setForeground(Color.red);
			return false;
		}
		
		// 验证码
		if (page.frame.getLayeredPane().getComponents().length <= 0) {
			page.hintLabel.setText("请选择验证码！");
			page.hintLabel.setForeground(Color.red);
			return false;
		}
		
		return true;
	}
}
