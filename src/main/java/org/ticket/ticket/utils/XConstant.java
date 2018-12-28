package org.ticket.ticket.utils;

/**
 * 常量
 *
 * @author YanZhen
 * 2018-11-19 16:50:20
 * XConstant
 */
public interface XConstant {

	final String UTF_8 = "UTF-8";
	
	final String AUTHOR_EMAIL = "1031881200@qq.com";
	
	final String PROMPT_MESSAGE = "通知作者：" + AUTHOR_EMAIL;
	
	/**
	 * 请求
	 *
	 * @author YanZhen
	 * 2018-11-20 17:24:26
	 * Http
	 */
	interface Http {
		final String GET = "GET";
		final String POST = "POST";
		final String QUESTIONMARK = "?";
		final String HTTPS = "https";
		final String HTTP = "http";
	}
	
	/**
	 * 用户信息常量
	 *
	 * @author YanZhen
	 * 2018-12-12 13:14:26
	 * User
	 */
	interface User {
		final String USERNAME = "username";
		final String PWD = "pwd";
		final String KEY = "key";
		final String VALUE = "value";
	}
	
	/**
	 * 符号
	 *
	 * @author YanZhen
	 * 2018-12-12 13:44:55
	 * Symbol
	 */
	interface Symbol {
		final String LINK_ = "—";
		final String _WU_ = "--";
		final String WU = "无";
	}
	
	/**
	 * 席别
	 *
	 * @author YanZhen
	 * 2018-12-13 10:10:18
	 * SeatType
	 */
	interface SeatType {
		// 席别
		final String SEAT_ALL = "全部席别";
		final String SEAT_SWZ = "商务座";
		final String SEAT_TDZ = "特等座";
		final String SEAT_YDZ = "一等座";
		final String SEAT_EDZ = "二等座";
		final String SEAT_GJRW = "高级软卧";
		final String SEAT_RW = "软卧";
		final String SEAT_YW = "硬卧";
		final String SEAT_RZ = "软座";
		final String SEAT_YZ = "硬座";
		final String SEAT_WZ = "无座";
		final String SEAT_QT = "其他";
	}
		
	/**
	 * 火车类型，首字母
	 *
	 * @author YanZhen
	 * 2018-12-14 16:07:44
	 * TrainType
	 */
	interface TrainType {
		/** 高铁 */
		final String TRAIN_G = "G";
		/** 普通火车 */
		final String TRAIN_C = "C";
		/** 动车 */
		final String TRAIN_D = "D";
		/** 直达 */
		final String TRAIN_Z = "Z";
		/** 特快 */
		final String TRAIN_T = "T";
		/** 快 */
		final String TRAIN_K = "K";
		/** * */
		final String TRAIN_STAR = "*";
		
		// 车次
		final String TRAIN_ALL = "全部车次";
		final String TRAIN_GT = "高铁-G";
		final String TRAIN_CT = "城铁-C";
		final String TRAIN_DC = "动车-D";
		final String TRAIN_TK = "特快-T";
		final String TRAIN_ZD = "直达-Z";
		final String TRAIN_KC = "快车-K";
		final String TRAIN_PK = "普客";
		final String TRAIN_LK = "临客";
		final String TRAIN_QT = "其它";
	}
	
	/**
	 * 请求链接
	 *
	 * @author YanZhen
	 * 2018-12-13 17:14:33
	 * Url
	 */
	interface Url {
		/** 登录 */
		final static String LOGIN_INIT = "https://kyfw.12306.cn/otn/login/init";
		/** 验证码图片 */
		// final static String CAPTCHA_IMAGE = "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&";
		/** 登录界面验证码 */
		final String GET_PASS_CODE_NEW = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&";
		/** 登录信息提交 */
		final String LOGIN_AYSN_SUGGEST = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
		/** 用户二次登录 */
		final String USER_LOGIN = "https://kyfw.12306.cn/otn/login/userLogin";
		
		/** 刷票链接 */
		final String OTN_QUERY = "https://kyfw.12306.cn/otn/leftTicket/query?";
		/** 提交订单 */
		final String SUBMIT_ORDER_REQUEST = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
		/** 预定车票 */
		final String INIT_DC = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
		/** 验证码拉取 */
		final String PASS_CODE_NEW = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=passenger&rand=randp&";
		/** 验证验证码 */
		final String CHECK_RAND_CODE_ANSYN = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
		/** 检查订单（提交的订票订单） */
		final String CHECK_ORDER_INFO = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
		/** 获取余票数量 */
		final String GET_QUEUE_COUNT = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
		/** 确认订单并提交 */
		final String CONFIRM_SINGLE_FOR_QUEUE = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
		/** 查询订单信息 */
		final String QUERY_ORDER_WAIT_TIME = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?";
		/** 获取乘客列表 */
		final String GET_PASSENGER_DTO_S = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
		/** 获取订单列表 */
		final String QUERY_MY_ORDER_NO_COMPLETE = "https://kyfw.12306.cn/otn/queryOrder/queryMyOrderNoComplete";
		/** 取消订单 */
		final String CANCEL_NO_COMPLETE_MY_ORDER = "https://kyfw.12306.cn/otn/queryOrder/cancelNoCompleteMyOrder";
	}
	
	/**
	 * 12306常量
	 *
	 * @author YanZhen
	 * 2018-12-14 15:40:08
	 * Params12306
	 */
	interface Constant12306 {
		/** 火车编号 */
		final String STATION_TRAIN_CODE = "station_train_code";
		/** 乘客姓名 */
		final String PASSENGER_NAME = "passenger_name";
		/** 乘客ID号 */
		final String PASSENGER_ID_NO = "passenger_id_no";
		/** 手机号 */
		final String MOBILE_NO = "mobile_no";
	}
	
	/**
	 * 图片文件
	 *
	 * @author YanZhen
	 * 2018-12-20 17:25:45
	 * Jpg
	 */
	interface Jpg {
		/** 12306图标 */
		final String JPG_12306_URL = System.getProperty("user.dir").concat("\\src\\main\\resources\\image\\12306.jpg");
		
		/** 箭头图标 */
		final String ARROW = "arrow.png";
	}
	
	/**
	 * unicode 字符串常量
	 *
	 * @author YanZhen
	 * 2018-12-24 14:27:18
	 * UnicodeString
	 */
	interface UnicodeString {
		/** 车次 */
		final String UNICODE_CC = "\u8F66\u6B21";// "车次";
		/** 订单号 */
		final String UNICODE_DDH = "\u8BA2\u5355\u53F7";// "订单号";
		/** 乘客姓名 */
		final String UNICODE_CKXM = "\u4E58\u5BA2\u59D3\u540D";// "乘客姓名";
		/** 发车时间 */
		final String UNICODE_FCSJ = "\u53D1\u8F66\u65F6\u95F4";// "发车时间";
		/** 出发地 */
		final String UNICODE_CFD = "\u51FA\u53D1\u5730";// "出发地";
		/** 目的地 */
		final String UNICODE_MDD = "\u76EE\u7684\u5730";// "目的地";
		/** 票种 */
		final String UNICODE_PZ = "\u7968\u79CD";// "票种";
		/** 席别 */
		final String UNICODE_XB = "\u5E2D\u522B";// "席别";
		/** 车厢 */
		final String UNICODE_CX = "\u8F66\u53A2";// "车厢";
		/** 座位 */
		final String UNICODE_ZW = "\u5EA7\u4F4D";// "座位";
		/** 票价 */
		final String UNICODE_PJ = "\u7968\u4EF7";// "票价";
		/** 状态 */
		final String UNICODE_ZT = "\u72B6\u6001";// "状态";
	}
	
	/**
	 * 
	 *
	 * @author YanZhen
	 * 2018-12-24 14:29:01
	 * panelString
	 */
	interface PanelString {
		final String PANEL_SPJM = "刷票界面";
		final String PANEL_DDJM = "订单界面";
		final String PANEL_XXTX = "消息提醒";
		final String PANEL_QTGN = "其它功能";
	}
}
