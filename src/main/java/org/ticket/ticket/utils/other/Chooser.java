package org.ticket.ticket.utils.other;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.http.client.utils.DateUtils;

/**
 * 选择器
 * 设置日历展示板式
 *
 * @author YanZhen
 * 2018-12-07 10:05:30
 * Chooser
 */
public class Chooser extends JPanel {
	private static final long serialVersionUID = 1L;

	private SimpleDateFormat sdf;
	private Calendar calendar;
	private Calendar now = Calendar.getInstance();
	private JPanel calendarPanel;
	private TitlePanel titlePanel;
	private BodyPanel bodyPanel;
	private FooterPanel footerPanel;
	private Popup pop;
	private final LabelManager lm = new LabelManager();
	
	private JComponent showDate;
	private boolean isShow = false;
	private final String[] showTEXT = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
	private final WeekLabel[] weekLabels = new WeekLabel[7];// 一周的展示顺序
	private static int defaultStartDAY = 0;// Sunday
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd";
	private final Color hoverColor = Color.red;
	
	public static Chooser getInstance(Date date, String format) {
		return new Chooser(date, format, defaultStartDAY);
	}

	public static Chooser getInstance(Date date) {
		return getInstance(date, DEFAULT_FORMAT);
	}

	public static Chooser getInstance(String format) {
		return getInstance(new Date(), format);
	}

	public static Chooser getInstance() {
		return getInstance(new Date(), DEFAULT_FORMAT);
	}
	
	private Chooser(Date date, String format, int startDAY) {
		// 是否有指定的开始时间
		if (startDAY > -1 && startDAY < 7) defaultStartDAY = startDAY;
		int dayIndex = defaultStartDAY;
		
		// 遍历一周的展示顺序
		for (int i = 0; i < weekLabels.length; i++) {
			if (dayIndex > 6) dayIndex = 0;// 遍历到周六，在从周六开始遍历
			weekLabels[i] = new WeekLabel(showTEXT[dayIndex]);
			dayIndex ++ ;
		}
		
		// 日期格式化
		sdf = new SimpleDateFormat(format);
		calendar = Calendar.getInstance();
		calendar.setTime(date);
		initCalendarPanel();
	}
	
	/**
	 * 自定义日历表
	 *
	 * 2018-12-07 11:37:58 void
	 */
	private void initCalendarPanel() {
		calendarPanel = new JPanel(new BorderLayout());
		calendarPanel.setBorder(BorderFactory.createLineBorder(new Color(0xAA, 0xAA, 0xAA)));
		
		// 表头的位置
		titlePanel = new TitlePanel();
		calendarPanel.add(titlePanel, BorderLayout.NORTH);
		
		// body位置
		bodyPanel = new BodyPanel();
		calendarPanel.add(bodyPanel, BorderLayout.CENTER);
		
		// footer位置
		footerPanel = new FooterPanel();
		calendarPanel.add(footerPanel, BorderLayout.SOUTH);
		
		this.addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {hidePanel();}
			
			// hide pop when move component
			@Override
			public void ancestorAdded(AncestorEvent event) {hidePanel();}
		});
	}
	
	
	public void register(final JComponent showComponent) {
		this.showDate = showComponent;
		showComponent.setRequestFocusEnabled(true);
		showComponent.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showComponent.requestFocusInWindow();
			}
		});
		this.add(showComponent, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(90, 25));
		this.setBorder(BorderFactory.createLineBorder(Color.gray));
		showComponent.addMouseListener(new MouseAdapter() {
			// 鼠标进入组件
			@Override
			public void mouseEntered(MouseEvent e) {
				if (showComponent.isEnabled()) {
					showComponent.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
			// 鼠标退出组件的时候
			@Override
			public void mouseExited(MouseEvent e) {
				if (showComponent.isEnabled()) {
					showComponent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					showComponent.setForeground(Color.black);
				}
			}
			// 鼠标按下的时候
			@Override
			public void mousePressed(MouseEvent e) {
				if (showComponent.isEnabled()) {
					showComponent.setForeground(hoverColor);
					if (isShow) {
						hidePanel();
					} else {
						showPanel(showComponent);
					}
				}
			}
			// 鼠标释放的时候
			@Override
			public void mouseReleased(MouseEvent e) {
				if (showComponent.isEnabled()) {
					showComponent.setForeground(Color.black);
				}
			}
		});
		
		// 监听器
		showComponent.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {hidePanel();}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
	}
	
	/**
	 *  show the main panel
	 *
	 * 2018-12-11 11:15:45
	 * @param owner void
	 */
	private void showPanel(Component owner) {
		if (pop != null) pop.hide();
		Point point = new Point(0, showDate.getHeight());
		SwingUtilities.convertPointToScreen(point, showDate);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int x = point.x;
		int y = point.y;
		if (x < 0) x = 0;
		if (x > size.width - 212) x = size.width -212;
		if (y > size.height - 167) y -= 165;
		pop = PopupFactory.getSharedInstance().getPopup(owner, calendarPanel, x, y);
		pop.show();
		isShow = true;
	}
	
	/**
	 * 表头
	 *
	 * @author YanZhen
	 * 2018-12-07 14:58:25
	 * TitlePanel
	 */
	private class TitlePanel extends JPanel {
		private static final long serialVersionUID = -1224601053669944606L;
		private JLabel lastYear,lastMonth,center,centerContainer,nextMonth,nextYear;
		
		public TitlePanel() {
			super(new BorderLayout());
			this.setBackground(new Color(0xBE, 0xC8, 0xC8));
			initTitlePanel();
		}

		/**
		 * 初始化表头
		 *
		 * 2018-12-07 15:18:33 void
		 */
		private void initTitlePanel() {
			// 图标
			lastYear = new JLabel("<<", JLabel.CENTER);
			lastMonth = new JLabel("<", JLabel.CENTER);
			center = new JLabel("", JLabel.CENTER);
			centerContainer = new JLabel("", JLabel.CENTER);
			nextMonth = new JLabel(">", JLabel.CENTER);
			nextYear = new JLabel(">>", JLabel.CENTER);
			
			// 表题
			lastYear.setToolTipText("Last year");
			lastMonth.setToolTipText("Last month");
			nextMonth.setToolTipText("Next month");
			nextYear.setToolTipText("Next year");
			
			// 大小范围
			lastYear.setBorder(BorderFactory.createEmptyBorder(2, 10, 0, 0));
			lastMonth.setBorder(BorderFactory.createEmptyBorder(2, 15, 0, 0));
			nextMonth.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 15));
			nextYear.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 10));
			
			// 月份添加进容器
			centerContainer.setLayout(new BorderLayout());
			centerContainer.add(lastMonth, BorderLayout.WEST);
			centerContainer.add(center, BorderLayout.CENTER);
			centerContainer.add(nextMonth, BorderLayout.EAST);
			
			// 年添加进去
			this.add(lastYear, BorderLayout.WEST);
			this.add(centerContainer, BorderLayout.CENTER);
			this.add(nextYear, BorderLayout.EAST);
			this.setPreferredSize(new Dimension(210, 25));
			
			updateDate();
			
			lastYear.addMouseListener(new MyMouseAdapter(lastYear, Calendar.YEAR, -1));
			lastMonth.addMouseListener(new MyMouseAdapter(lastMonth, Calendar.MONTH, -1));
			nextMonth.addMouseListener(new MyMouseAdapter(nextMonth, Calendar.MONTH, 1));
			nextYear.addMouseListener(new MyMouseAdapter(nextYear, Calendar.YEAR, 1));
		}

		/**
		 * 时间格式，更新
		 *
		 * 2018-12-07 16:05:07 void
		 */
		private void updateDate() {
			center.setText(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH));
		}
		
		private class MyMouseAdapter extends MouseAdapter {
			private JLabel label;
			private int type, value;
			
			public MyMouseAdapter(final JLabel label, final int type, final int value) {
				this.label = label;
				this.type = type;
				this.value = value;
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// 当光标进入组件时调用
				label.setCursor(new Cursor(Cursor.HAND_CURSOR));
				label.setForeground(hoverColor);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// 当光标退出组件
				label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				label.setForeground(Color.black);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// 点击时
				calendar.add(type, value);
				label.setForeground(Color.white);
				refresh();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// 释放光标时
				label.setForeground(Color.black);
			}
		}
	}
	
	/**
	 * body面板
	 *
	 * @author YanZhen
	 * 2018-12-10 10:12:47
	 * BodyPanel
	 */
	private class BodyPanel extends JPanel {
		private static final long serialVersionUID = 73423970762726776L;

		public BodyPanel() {
			super(new GridLayout(7, 7));
			this.setPreferredSize(new Dimension(350, 140));
			initMonthPanel();
		}

		/**
		 * 初始化body部分
		 *
		 * 2018-12-10 10:21:07 void
		 */
		private void initMonthPanel() {
			updateDate();
		}

		/**
		 * 变更时间
		 *
		 * 2018-12-10 10:22:08 void
		 */
		private void updateDate() {
			this.removeAll();
			lm.clear();
			Date time = calendar.getTime();
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			c.add(Calendar.DAY_OF_MONTH, 1);
			
			int dayWeek = c.get(Calendar.DAY_OF_WEEK);
			
			// 从1号向前移动
			if (dayWeek > defaultStartDAY) {
				c.set(Calendar.DAY_OF_MONTH, -dayWeek + defaultStartDAY);
			} else {
				c.set(Calendar.DAY_OF_MONTH, -dayWeek + defaultStartDAY - 7);
			}
			
			for (WeekLabel wl : weekLabels) {
				this.add(wl);
			}
			
			// 面板有42格
			for (int i = 0; i < 42; i++) {
				c.add(Calendar.DAY_OF_MONTH, 1);
				lm.addDls(new DayLabel(c));
			}
			
			for (DayLabel dl : lm.getDls()) {
				this.add(dl);
			}
		}
	}
	
	private class FooterPanel extends JPanel {
		private static final long serialVersionUID = -7394074839750118260L;
		private JButton todayButton;
		
		public FooterPanel() {
			super(new BorderLayout());
			initFooterPanel();
		}

		private void initFooterPanel() {
			
			todayButton = new JButton("今天：" + sdf.format(new Date()));
			todayButton.setBounds((this.getWidth() - todayButton.getWidth()) / 2,
					(this.getHeight() - todayButton.getHeight()) / 2, todayButton.getWidth(),
					todayButton.getHeight());
			todayButton.addMouseListener(new MouseListener() {
				
				// 鼠标释放
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				// 鼠标按下
				@Override
				public void mousePressed(MouseEvent e) {
					calendar.setTime(new Date());
					refresh();
					commit();
				}
				
				// 鼠标退出
				@Override
				public void mouseExited(MouseEvent e) {
					todayButton.setForeground(Color.black);
				}
				
				// 鼠标悬于上空
				@Override
				public void mouseEntered(MouseEvent e) {
					todayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
					todayButton.setForeground(hoverColor);
				}
				
				// 点击时
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
			
			// 组件的大小
			this.setPreferredSize(new Dimension(250, 30));
			this.add(todayButton);
		}
	}
	
	private void commit() {
		if (showDate instanceof JTextField) {
			((JTextField) showDate).setText(sdf.format(calendar.getTime()));
		} else if (showDate instanceof JLabel) {
			((JLabel) showDate).setText(sdf.format(calendar.getTime()));
		}
		hidePanel();
	}
	
	/**
	 * Hide the main panel
	 *
	 * 2018-12-10 16:50:48 void
	 */
	private void hidePanel() {
		if (pop != null) {
			isShow = false;
			pop.hide();
			pop = null;
		}
	}

	private class LabelManager {
		private List<DayLabel> dls;
		
		public LabelManager() {
			dls = new ArrayList<>();
		}
		
		public List<DayLabel> getDls() {
			return dls;
		}
		
		public void addDls(DayLabel dl) {
			dls.add(dl);
		}
		
		public void clear() {
			dls.clear();
		}
		
		public void setSelect(Point p, boolean b) {
			if (b) {
				boolean findLast = false, findNext = false;
				for (DayLabel dl : dls) {
					if (dl.contains(p)) {
						findNext = true;
						if (dl.getIsSelected()) {
							findLast = true;
						} else {
							dl.setIsSelected(true, b);
						}
					} else if (dl.getIsSelected()) {
						findLast = true;
						dl.setIsSelected(false, b);
					}
					
					if (findLast && findNext) return;
				}
			} else {
				DayLabel dayLabel = null;
				for (DayLabel dl : dls) {
					if (dl.contains(p)) {
						dayLabel = dl;
					} else if (dl.getIsSelected()) {
						dl.setIsSelected(false, b);
					}
				}
				
				if (dayLabel != null) {
					dayLabel.setIsSelected(true, b);
				}
			}
		}
	}
	
	private class DayLabel extends JLabel implements Comparator<DayLabel>, MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 1L;
		
		private boolean isSelected;
		private int year, month, day;

		public DayLabel(Calendar c) {
			super(c.get(Calendar.DAY_OF_MONTH) + "", JLabel.CENTER);
			this.year = c.get(Calendar.YEAR);
			this.month = c.get(Calendar.MONTH);
			this.day = c.get(Calendar.DAY_OF_MONTH);
			
			this.setFont(new Font("Times", Font.PLAIN, 12));
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			if (month == c.get(Calendar.MONTH)) {
				this.setForeground(Color.black);
			} else {
				this.setForeground(Color.lightGray);
			}
		}
		
		public void setIsSelected(boolean b, boolean isDrag) {
			isSelected = b;
			if (b && !isDrag) {
				int temp = calendar.get(Calendar.MONTH);
				calendar.set(year, month, day);
				if (temp == month) {
					SwingUtilities.updateComponentTreeUI(bodyPanel);
				} else {
					refresh();
				}
				this.repaint();
			}
		}

		public boolean getIsSelected() {
			return isSelected;
		}
		
		@Override
		public boolean contains(Point p) {
			return this.getBounds().contains(p);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			// set current select day's background
			if (day == calendar.get(Calendar.DAY_OF_MONTH) && month == calendar.get(Calendar.MONTH)) {
				g.setColor(new Color(0xBB, 0xBF, 0xDA));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			
			// set current day's border
			if (year == now.get(Calendar.YEAR) && month == now.get(Calendar.MONTH) && day == now.get(Calendar.DAY_OF_MONTH)) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(new Color(0x55, 0x55, 0x88));
				Polygon p = new Polygon();
				p.addPoint(0, 0);
				p.addPoint(getWidth() - 1, 0);
				p.addPoint(getWidth() - 1, getHeight() - 1);
				p.addPoint(0, getHeight() - 1);
				g2d.drawPolygon(p);
			}
			
			if (isSelected) {
				Stroke s = new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1f, new float[] {2f, 2f}, 1f);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(s);
				g2d.setColor(Color.black);
				Polygon p = new Polygon();
				p.addPoint(0, 0);
				p.addPoint(getWidth() - 1, 0);
				p.addPoint(getWidth() - 1, getHeight() - 1);
				p.addPoint(0, getHeight() - 1);
				g2d.drawPolygon(p);
			}
			
			super.paintComponent(g);
		}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			isSelected = true;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point p = SwingUtilities.convertPoint(this, e.getPoint(), bodyPanel);
			this.setForeground(Color.black);
			lm.setSelect(p, false);
			commit();
		}

		// Change color when mouse over
		@Override
		public void mouseEntered(MouseEvent e) {
			this.setForeground(hoverColor);
			this.repaint();
		}

		// Change color when mouse exit
		@Override
		public void mouseExited(MouseEvent e) {
			if (month == calendar.get(Calendar.MONTH)) this.setForeground(Color.black);
			else this.setForeground(Color.lightGray);
			this.repaint();
		}

		@Override
		public int compare(DayLabel o1, DayLabel o2) {
			Calendar c1 = Calendar.getInstance();
			c1.set(o1.year, o1.month, o1.day);
			Calendar c2 = Calendar.getInstance();
			c2.set(o2.year, o2.month, o2.day);
			return c1.compareTo(c2);
		}
	}
	
	/**
	 * 刷新面板
	 *
	 * 2018-12-07 17:29:25 void
	 */
	private void refresh() {
		// 更新头时间
		titlePanel.updateDate();
		
		// 更新body时间
		bodyPanel.updateDate();
		
		// 更新底部时间
		
		
		SwingUtilities.updateComponentTreeUI(this);
	}

	/**
	 * 星期标签
	 *
	 * @author YanZhen
	 * 2018-12-07 10:33:54
	 * WeekLabel
	 */
	private class WeekLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		
		private String name;
		
		public WeekLabel(String name) {
			super(name, JLabel.CENTER);
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 测试框
	 *
	 * 2018-12-11 15:51:26
	 * @param args void
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame("Date picker test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(null);
		f.setBounds(400, 200, 300, 300);
		
		// 输入框
		Chooser c = getInstance();
		JTextField text = new JTextField();
		text.setBounds(10, 10, 200, 30);
		text.setText(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		c.register(text);
		
		// 提示语
		Chooser c2 = getInstance("yyyy年MM月dd日");
		JLabel label = new JLabel("please click me.");
		label.setBounds(10, 50, 200, 30);
		c2.register(label);
		
		// 日期面板
		f.add(text);
		f.add(label);
		f.setVisible(true);
	}
}
