package org.ticket.ticket.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ticket.ticket.utils.XConstant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 配置类
 *
 * @author YanZhen 2018-11-22 14:03:28 ConfigUtils
 */
public class ConfigUtils {

	public Map<String, String> map;
	private static ConfigUtils configUtils;
	private File file;

	/**
	 * 实例化
	 *
	 * 2018-11-22 14:09:25
	 * 
	 * @return ConfigUtils
	 * @throws Exception 异常
	 */
	public synchronized static ConfigUtils getInstance() throws Exception {
		if (configUtils == null) {
			configUtils = new ConfigUtils();
		}
		return configUtils;
	}

	private ConfigUtils() throws ParserConfigurationException, SAXException, IOException {
		map = new HashMap<>();
		// 解析xml，解析不了%20直接替换成空格
		file = new File(this.getClass().getResource("/").getPath().concat("xconf.xml").replaceAll("%20", " "));
		NodeList nodeList = parseXml().getElementsByTagName(XConstant.User.VALUE);
		if (nodeList == null || nodeList.getLength() <= 0) {
			return;
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			// 提取信息
			String textContent = nodeList.item(i).getTextContent();
			String[] strArray = textContent.split("\\|");

			// 页面展示
			map.put(new String(Base64Config.decoder(strArray[0]), StandardCharsets.UTF_8),
					new String(Base64Config.decoder(strArray[1]), StandardCharsets.UTF_8));
		}
	}

	/**
	 * xml解析
	 *
	 * 2018-11-22 19:37:51
	 * @return 解析结果
	 * @throws SAXException 异常
	 * @throws IOException 异常
	 * @throws ParserConfigurationException Document
	 */
	private Document parseXml() throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	}

	/**
	 * 记住用户
	 *
	 * 2018-11-22 19:34:25
	 * @param users 用户
	 * @throws ParserConfigurationException 异常
	 * @throws IOException 异常
	 * @throws SAXException 异常
	 * @throws TransformerFactoryConfigurationError 异常
	 * @throws TransformerException 异常
	 */
	public void rememberUser(String[] users) throws SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		// 信息不能为空
		if (users == null || users.length <= 0) {
			return;
		}
		
		// 本次登录用户
		String name = Base64Config.encoder(users[0].getBytes());
		String value = name + "|" + Base64Config.encoder(users[1].getBytes());
		
		// 记住的用户
		Document doc = parseXml();
		NodeList nodes = doc.getElementsByTagName(XConstant.User.VALUE);
		Node node = doc.getElementsByTagName(XConstant.User.KEY).item(0);
		
		// 检查登录用户是否被记录过
		if (nodes != null && nodes.getLength() > 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node newUser = nodes.item(i);
				String textContent = newUser.getTextContent();
				// 被记录过就刷新密码
				if (name.equals(textContent.split("\\|")[0])) {
					node.removeChild(newUser);
				}
			}
		}
		
		// 记住登录用户
		Element createElement = doc.createElement(XConstant.User.VALUE);
		createElement.setTextContent(value);
		node.appendChild(createElement);
		
		// 写入本地
		writeLocal(doc);
	}
	
	/**
	 * 取消记住用户信息
	 *
	 * 2018-11-26 19:35:30
	 * @param username 要删除的用户名
	 * @throws SAXException 异常
	 * @throws IOException 异常
	 * @throws ParserConfigurationException 异常
	 * @throws TransformerFactoryConfigurationError 异常
	 * @throws TransformerException 异常
	 */
	public void removeUser(String username) throws SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		// 信息不能为空
		if (username == null || username.isEmpty()) {
			return;
		}
		
		// 编码
		String name = Base64Config.encoder(username.getBytes());
		// String value = name + "\\|" + Base64Config.encoder(users[1].getBytes());
		
		// 拉取历史用户
		Document doc = parseXml();
		NodeList nodes = doc.getElementsByTagName(XConstant.User.VALUE);
		if (nodes == null || nodes.getLength() <= 0) {
			return;
		}
		
		Node node = doc.getElementsByTagName(XConstant.User.KEY).item(0);
		
		// 登录时没有选记住用户，则删除该用户
		for (int i = 0; i < nodes.getLength(); i++) {
			Node newUser = nodes.item(i);
			String textContent = newUser.getTextContent();
			if (name.equals(textContent.split("\\|")[0])) {
				node.removeChild(newUser);
			}
		}
		
		// 刷新本地
		writeLocal(doc);
	}

	/**
	 * 写入数据
	 *
	 * 2018-11-22 20:18:41 void
	 * @param doc 键值对
	 * @throws TransformerFactoryConfigurationError 异常
	 * @throws TransformerException 异常
	 */
	private void writeLocal(Document doc) throws TransformerFactoryConfigurationError, TransformerException {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		DOMSource source = new DOMSource(doc);
		tf.transform(source, new StreamResult(file));
	}
	
	// test接口
	public static void main(String[] args) {
		String[] strArray = {"username2","password2"};
		try {
			
			ConfigUtils.getInstance().rememberUser(strArray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
