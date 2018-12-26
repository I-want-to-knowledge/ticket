package org.ticket.ticket.utils.other;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * 弹出框
 *
 * @author YanZhen
 * 2018-12-19 17:24:29
 * PopUp
 */
public class PopUp {
	
	static boolean isShow = false;
	static Popup mypop = null;
	
	/** 上 */
	final static int UP = 1;
	/** 下 */
	final static int DOWN = 2;
	/** 左 */
	final static int LEFT = 3;
	/** 右 */
	final static int RIGHT = 4;

	/**
	 * 输入框弹出提示信息
	 *
	 * 2018-12-19 17:24:50
	 * @param componentName 组件名，点击什么弹出信息，就传什么组件；例：button、label...
	 * @param type 类型信息
	 * @param listModel 要弹出的信息
	 */
	public static void popUpMessage(Component componentName, JList<Object> type, DefaultListModel<Object> listModel) {
		popUpMessage(componentName, type, listModel, UP);
	}

	/**
	 * 输入框弹出提示信息
	 *
	 * 2018-12-19 17:33:51
	 * @param componentName 组件名，点击什么弹出信息，就传什么组件；例：button、label...
	 * @param type 类型信息
	 * @param listModel 要弹出的信息
	 * @param direction 展示位置，四个值：UP=1、DOWN=2、LEFT=3、RIGHT=4
	 */
	public static void popUpMessage(Component componentName, JList<Object> type, DefaultListModel<Object> listModel,
			final int direction) {
		JList<Object> popUpList = new JList<>(listModel);
		popUpList.setSize(new Dimension(componentName.getWidth(), listModel.getSize() * 18));
		popUpList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 选择单个
		popUpList.setForeground(new Color(240, 240, 240));
		popUpList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 鼠标点击事件
				ListModel<Object> seatTypeModel = type.getModel();
				if (seatTypeModel.getSize() >= 5) {
					return;
				}
				
				// 添加展示信息
				DefaultListModel<Object> messageModel = new DefaultListModel<>();
				for (int i = 0; i < seatTypeModel.getSize(); i++) {
					messageModel.addElement(seatTypeModel.getElementAt(i));
				}
				
				if (!messageModel.contains(popUpList.getSelectedValue())) {
					messageModel.addElement(popUpList.getSelectedValue());
				}
				
				// 放入弹出框
				type.setModel(messageModel);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// 鼠标退出
				if (isShow) {
					if (mypop != null) {
						mypop.hide();
						isShow = false;
					}
				}
			}
		});
		
		popUpList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// 鼠标悬于控件上
				popUpList.setSelectedIndex(popUpList.locationToIndex(e.getPoint()));
			}
		});
		
		componentName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 鼠标点击事件
				if (isShow) {
					if (mypop != null) {
						mypop.hide();
						isShow = false;
					}
				} else {
					if (mypop != null) {
						mypop.hide();
					}
					
					Point locationOnScreen = componentName.getLocationOnScreen();// 组件的位置
					int width = componentName.getWidth();
					int height = componentName.getHeight();
					
					// 展示信息的面板
					JPanel p = new JPanel();
					p.setBorder(BorderFactory.createLineBorder(new Color(0xAA, 0xAA, 0xAA)));
					p.setPreferredSize(new Dimension(width, popUpList.getHeight() + 20));
					p.add(popUpList);
					
					int x = 0;
					int y = 0;
					switch (direction) {
						case UP:
							x = (int) locationOnScreen.getX();
							y = (int) (locationOnScreen.getY() - popUpList.getHeight() - 20);
							break;
						case DOWN:
							x = (int) locationOnScreen.getX();
							y = (int) (locationOnScreen.getY() + height);
							break;
						case LEFT:
							x = (int) (locationOnScreen.getX() - width);
							y = (int) locationOnScreen.getY();
							break;
						case RIGHT:
							x = (int) (locationOnScreen.getX() + width);
							y = (int) locationOnScreen.getY();
							break;

						default:
							break;
					}
					
					mypop = PopupFactory.getSharedInstance().getPopup(componentName, p, x, y);
					mypop.show();
					isShow = true;
				}
			}
		});
	}

}
