package org.ticket.ticket.utils.http;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.config.ConfigUtils;

/**
 * 
 *
 * @author YanZhen 2018-11-26 11:14:27 ComBoTextField
 */
public class ComBoTextField {
	private final static Logger LOG = LoggerFactory.getLogger(ComBoTextField.class);
	
	private final static String IS_ADJUSTING = "is_adjusting";

	/**
	 * 输入框控制
	 *
	 * 2018-11-26 11:19:45
	 * @param jTextField
	 * @param pwdField
	 * @param arrayList void
	 */
	public static void setUpAutoComplete(final JTextField jTextField, final JPasswordField pwdField,
			final List<String> arrayList) {
		final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		final JComboBox<String> cbInput = new JComboBox<String>(model) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 0);
			}
		};
		
		// TODO:无用
		for (String vlaue : arrayList) {
			model.addElement(vlaue);
		}
		
		setAdjusting(cbInput, false);
		cbInput.setSelectedItem(null);
		
		// 选择记住的用户，自动填写密码
		cbInput.addActionListener(e -> {
			if (!isAdjusting(cbInput)) {
				Object selectedItem = cbInput.getSelectedItem();
				if (selectedItem != null) {
					String str = selectedItem.toString();
					jTextField.setText(str);
					try {
						String pass = ConfigUtils.getInstance().map.get(str);
						pwdField.setText(pass);
					} catch (Exception e1) {
						LOG.error("异常信息：{}", e1);
					}
				}
			}
		});
		
		jTextField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setAdjusting(cbInput, true);
				model.removeAllElements();
				for (String value : arrayList) {
					model.addElement(value);
				}
				cbInput.setPopupVisible(model.getSize() > 0);
				setAdjusting(cbInput, false);
			};
		});
		
		// 键盘监听
		jTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				setAdjusting(cbInput, true);
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (cbInput.isPopupVisible()) {
						e.setKeyCode(KeyEvent.VK_ENTER);
					}
				}
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						|| e.getKeyCode() == KeyEvent.VK_UP
						|| e.getKeyCode() == KeyEvent.VK_DOWN) {
					e.setSource(e);
					cbInput.dispatchEvent(e);
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						jTextField.setText(cbInput.getSelectedItem().toString());
						cbInput.setPopupVisible(false);
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cbInput.setPopupVisible(false);
				}
				setAdjusting(cbInput, false);
			}
		});
		
		jTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateList();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateList();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateList();
			}
			
			private void updateList() {
				setAdjusting(cbInput, true);
				model.removeAllElements();
				String text = jTextField.getText();
				if (!text.isEmpty()) {
					for (String value : arrayList) {
						if (value.toLowerCase().startsWith(text.toLowerCase())) {
							model.addElement(value);
						}
					}
				}
				cbInput.setPopupVisible(model.getSize() > 0);
				setAdjusting(cbInput, false);
			}
		});
		jTextField.setLayout(new BorderLayout());
		jTextField.add(cbInput, BorderLayout.SOUTH);
	}

	/**
	 * 是否可以调整
	 *
	 * 2018-11-26 14:09:31
	 * @param cbInput
	 * @return boolean
	 */
	private static boolean isAdjusting(JComboBox<String> cbInput) {
		Object clientProperty = cbInput.getClientProperty(IS_ADJUSTING);
		if (clientProperty instanceof Boolean) {
			return (boolean) clientProperty;
		}
		return false;
	}

	/**
	 * 设置是否可调整
	 *
	 * 2018-11-26 14:09:51
	 * @param cbInput
	 * @param b void
	 */
	private static void setAdjusting(JComboBox<String> cbInput, boolean b) {
		cbInput.putClientProperty(IS_ADJUSTING, b);
	}

	/**
	 * 目的地选择，出发和目的地
	 *
	 * 2018-11-27 11:13:22
	 * @param textField
	 * @param stationNames
	 * @param map void
	 */
	public static void setDestination(final JTextField textField, final List<String> stationNames, final Map<String, String[]> map) {
		final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		final JComboBox<String> cbInput = new JComboBox<String>(model) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 0);
			}
		};
		
		// 放入地理位置名
		for (String stationName : stationNames) {
			model.addElement(stationName);
		}
		
		// 禁止调整
		setAdjusting(cbInput, false);
		
		cbInput.setSelectedItem(null);
		cbInput.addActionListener(e -> {
			if (!isAdjusting(cbInput)) {
				
			}
		});
	}
}
