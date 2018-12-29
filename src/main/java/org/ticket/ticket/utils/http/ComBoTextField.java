package org.ticket.ticket.utils.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ticket.ticket.config.ConfigUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author YanZhen 2018-11-26 11:14:27 ComBoTextField
 */
public class ComBoTextField {
    private final static Logger LOG = LoggerFactory.getLogger(ComBoTextField.class);

    private final static String IS_ADJUSTING = "is_adjusting";

    /**
     * 输入框控制
     * 2018-11-26 11:19:45
     *
     * @param jTextField
     * @param pwdField
     * @param arrayList  void
     */
    public static void setUpAutoComplete(final JTextField jTextField, final JPasswordField pwdField,
                                         final List <String> arrayList) {
        final DefaultComboBoxModel <String> model = new DefaultComboBoxModel <>();
        final JComboBox <String> cbInput = new JComboBox <String>(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };

        // TODO:无用
        for (String value : arrayList) {
            model.addElement(value);
        }

        setAdjusting(cbInput, false);
        cbInput.setSelectedItem(null);

        // 选择记住的用户，自动填写密码
        cbInput.addActionListener(e -> {
            if (isAdjusting(cbInput)) {
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
            }
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
                        jTextField.setText(Objects.requireNonNull(cbInput.getSelectedItem()).toString());
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
     * 出发地、目的地输入框
     * 2018-12-11 16:31:45
     *
     * @param txtInput
     * @param lists
     * @param map      void
     */
    public static void setUpAutoComplete(final JTextField txtInput, final List <String> lists,
                                         final Map <String, String[]> map) {
        final DefaultComboBoxModel <String> model = new DefaultComboBoxModel <>();
        final JComboBox <String> cb = new JComboBox <String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };
        setAdjusting(cb, false);
        for (String string : lists) {
            model.addElement(string);
        }
        cb.setSelectedItem(null);

        cb.addActionListener(e -> {
            if (isAdjusting(cb)) {
                if (cb.getSelectedItem() != null) {
                    txtInput.setText(cb.getSelectedItem().toString());
                }
            }
        });

        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setAdjusting(cb, true);
                // 空格键
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (cb.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }

                // Enter,Up,Down
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_UP
                        || keyCode == KeyEvent.VK_DOWN) {
                    e.setSource(cb);
                    cb.dispatchEvent(e);

                    // Enter
                    if (keyCode == KeyEvent.VK_ENTER) {
                        if (cb.getSelectedItem() != null) {
                            txtInput.setText(cb.getSelectedItem().toString());
                        }
                        cb.setPopupVisible(false);
                    }
                }

                // Esc
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cb.setPopupVisible(false);
                }

                setAdjusting(cb, false);
            }
        });

        txtInput.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDocument();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDocument();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDocument();
            }

            /**
             * 修改文档
             *
             * 2018-12-11 17:28:42 void
             */
            private void updateDocument() {
                setAdjusting(cb, true);
                model.removeAllElements();
                String text = txtInput.getText();
                if (text != null && !text.isEmpty()) {
                    for (String key : map.keySet()) {
                        String[] strs = map.get(key);
                        String lowerCase = text.toLowerCase();
                        if (key.startsWith(lowerCase)) {
                            model.addElement(key);
                        } else {
                            if (strs.length > 4 && strs[4].toLowerCase().startsWith(lowerCase)) {
                                model.addElement(key);
                            }
                        }
                    }
                }
                cb.setPopupVisible(model.getSize() > 0);
                setAdjusting(cb, false);
            }
        });
        txtInput.setLayout(new BorderLayout());
        txtInput.add(cb, BorderLayout.SOUTH);
    }

    /**
     * 是否可以调整
     * 2018-11-26 14:09:31
     * @param cbInput
     * @return boolean
     */
    private static boolean isAdjusting(JComboBox <String> cbInput) {
        Object clientProperty = cbInput.getClientProperty(IS_ADJUSTING);
        if (clientProperty instanceof Boolean) {
            return !((boolean) clientProperty);
        }
        return true;
    }

    /**
     * 设置是否可调整
     * 2018-11-26 14:09:51
     * @param cbInput
     * @param b       void
     */
    private static void setAdjusting(JComboBox <String> cbInput, boolean b) {
        cbInput.putClientProperty(IS_ADJUSTING, b);
    }

    /**
     * 按钮
     * 2018-11-27 11:13:22
     * @param button
     * @param list
     */
    private static void setDestination(final JButton button, final List <String> list) {
        final DefaultComboBoxModel <String> model = new DefaultComboBoxModel <>();
        final JComboBox <String> cbInput = new JComboBox <String>(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };

        // 放入地理位置名
        for (String l : list) {
            model.addElement(l);
        }

        // 禁止调整
        setAdjusting(cbInput, false);

        cbInput.setSelectedItem(null);
        cbInput.addActionListener(e -> {
            if (isAdjusting(cbInput)) {
                if (cbInput.getSelectedItem() == null) {
                    LOG.info("选择下拉内容后！");
                }
            }
        });

        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setAdjusting(cbInput, true);
                // SPACE
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (cbInput.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }

                // ENTER,UP,DOWN
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.setSource(cbInput);
                    cbInput.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        LOG.info("选择下拉内容按回车键！");
                        cbInput.setPopupVisible(false);
                    }
                }

                // ESC
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cbInput.setPopupVisible(false);
                }

                setAdjusting(cbInput, false);
            }
        });

        button.setLayout(new BorderLayout());
        button.add(cbInput, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        setDestination(new JButton(), new ArrayList <>());
    }
}
