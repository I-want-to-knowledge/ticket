package org.ticket.ticket.utils;

/**
 * 常量
 *
 * @author YanZhen
 * 2018-11-19 16:50:20
 * XConstant
 */
public interface XConstant {

    String UTF_8 = "UTF-8";

    String AUTHOR_EMAIL = "1031881200@qq.com";

    String PROMPT_MESSAGE = "通知作者：" + AUTHOR_EMAIL;

    /**
     * 请求
     *
     * @author YanZhen
     * 2018-11-20 17:24:26
     * Http
     */
    interface Http {
        String GET = "GET";
        String POST = "POST";
        String QUESTIONMARK = "?";
        String HTTPS = "https";
        String HTTP = "http";
    }

    /**
     * 用户信息常量
     *
     * @author YanZhen
     * 2018-12-12 13:14:26
     * User
     */
    interface User {
        // String USERNAME = "username";
        // String PWD = "pwd";
        String KEY = "key";
        String VALUE = "value";
    }

    /**
     * 符号
     *
     * @author YanZhen
     * 2018-12-12 13:44:55
     * Symbol
     */
    interface Symbol {
        String LINK_ = "—";
        String _WU_ = "--";
        String WU = "无";
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
        String SEAT_ALL = "全部席别";
        String SEAT_SWZ = "商务座";
        String SEAT_TDZ = "特等座";
        String SEAT_YDZ = "一等座";
        String SEAT_EDZ = "二等座";
        String SEAT_GJRW = "高级软卧";
        String SEAT_RW = "软卧";
        String SEAT_YW = "硬卧";
        String SEAT_RZ = "软座";
        String SEAT_YZ = "硬座";
        String SEAT_WZ = "无座";
        String SEAT_QT = "其他";
    }

    /**
     * 火车类型，首字母
     *
     * @author YanZhen
     * 2018-12-14 16:07:44
     * TrainType
     */
    interface TrainType {
        /**
         * 高铁
         */
        String TRAIN_G = "G";
        /**
         * 普通火车
         */
        String TRAIN_C = "C";
        /**
         * 动车
         */
        String TRAIN_D = "D";
        /**
         * 直达
         */
        String TRAIN_Z = "Z";
        /**
         * 特快
         */
        String TRAIN_T = "T";
        /**
         * 快
         */
        String TRAIN_K = "K";
        /**
         *
         */
        String TRAIN_STAR = "*";

        // 车次
        String TRAIN_ALL = "全部车次";
        String TRAIN_GT = "高铁-G";
        String TRAIN_CT = "城铁-C";
        String TRAIN_DC = "动车-D";
        String TRAIN_TK = "特快-T";
        String TRAIN_ZD = "直达-Z";
        String TRAIN_KC = "快车-K";
        String TRAIN_PK = "普客";
        String TRAIN_LK = "临客";
        String TRAIN_QT = "其它";
    }

    /**
     * 请求链接
     *
     * @author YanZhen
     * 2018-12-13 17:14:33
     * Url
     */
    interface Url {
        /**
         * 登录
         */
        String LOGIN_INIT = "https://kyfw.12306.cn/otn/login/init";
        // /** 验证码图片 */
        // static String CAPTCHA_IMAGE = "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&";
        /**
         * 登录界面验证码
         */
        String GET_PASS_CODE_NEW = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&";
        /**
         * 登录信息提交
         */
        String LOGIN_AYSN_SUGGEST = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
        /**
         * 用户二次登录
         */
        String USER_LOGIN = "https://kyfw.12306.cn/otn/login/userLogin";

        /**
         * 刷票链接
         */
        String OTN_QUERY = "https://kyfw.12306.cn/otn/leftTicket/query?";
        /**
         * 提交订单
         */
        String SUBMIT_ORDER_REQUEST = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
        /**
         * 预定车票
         */
        String INIT_DC = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
        /**
         * 验证码拉取
         */
        String PASS_CODE_NEW = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=passenger&rand=randp&";
        /**
         * 验证验证码
         */
        String CHECK_RAND_CODE_ANSYN = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
        /**
         * 检查订单（提交的订票订单）
         */
        String CHECK_ORDER_INFO = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
        /**
         * 获取余票数量
         */
        String GET_QUEUE_COUNT = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
        /**
         * 确认订单并提交
         */
        String CONFIRM_SINGLE_FOR_QUEUE = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
        /**
         * 查询订单信息
         */
        String QUERY_ORDER_WAIT_TIME = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?";
        /**
         * 获取乘客列表
         */
        String GET_PASSENGER_DTO_S = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
        /**
         * 获取订单列表
         */
        String QUERY_MY_ORDER_NO_COMPLETE = "https://kyfw.12306.cn/otn/queryOrder/queryMyOrderNoComplete";
        /**
         * 取消订单
         */
        String CANCEL_NO_COMPLETE_MY_ORDER = "https://kyfw.12306.cn/otn/queryOrder/cancelNoCompleteMyOrder";
    }

    /**
     * 12306常量
     *
     * @author YanZhen
     * 2018-12-14 15:40:08
     * Params12306
     */
    interface Constant12306 {
        /**
         * 火车编号
         */
        String STATION_TRAIN_CODE = "station_train_code";
        /**
         * 乘客姓名
         */
        String PASSENGER_NAME = "passenger_name";
        /**
         * 乘客ID号
         */
        String PASSENGER_ID_NO = "passenger_id_no";
        /**
         * 手机号
         */
        String MOBILE_NO = "mobile_no";
    }

    /**
     * 图片文件
     *
     * @author YanZhen
     * 2018-12-20 17:25:45
     * Jpg
     */
    interface Jpg {
        /**
         * 12306图标
         */
        String JPG_12306_URL = PathUtils.jpg12306Path();

        /**
         * 箭头图标
         */
        String ARROW = "arrow.png";
    }

    /**
     * unicode 字符串常量
     *
     * @author YanZhen
     * 2018-12-24 14:27:18
     * UnicodeString
     */
    interface UnicodeString {
        /**
         * 车次
         */
        String UNICODE_CC = "\u8F66\u6B21";// "车次";
        /**
         * 订单号
         */
        String UNICODE_DDH = "\u8BA2\u5355\u53F7";// "订单号";
        /**
         * 乘客姓名
         */
        String UNICODE_CKXM = "\u4E58\u5BA2\u59D3\u540D";// "乘客姓名";
        /**
         * 发车时间
         */
        String UNICODE_FCSJ = "\u53D1\u8F66\u65F6\u95F4";// "发车时间";
        /**
         * 出发地
         */
        String UNICODE_CFD = "\u51FA\u53D1\u5730";// "出发地";
        /**
         * 目的地
         */
        String UNICODE_MDD = "\u76EE\u7684\u5730";// "目的地";
        /**
         * 票种
         */
        String UNICODE_PZ = "\u7968\u79CD";// "票种";
        /**
         * 席别
         */
        String UNICODE_XB = "\u5E2D\u522B";// "席别";
        /**
         * 车厢
         */
        String UNICODE_CX = "\u8F66\u53A2";// "车厢";
        /**
         * 座位
         */
        String UNICODE_ZW = "\u5EA7\u4F4D";// "座位";
        /**
         * 票价
         */
        String UNICODE_PJ = "\u7968\u4EF7";// "票价";
        /**
         * 状态
         */
        String UNICODE_ZT = "\u72B6\u6001";// "状态";
    }

    /**
     * @author YanZhen
     * 2018-12-24 14:29:01
     * panelString
     */
    interface PanelString {
        String PANEL_SPJM = "刷票界面";
        String PANEL_DDJM = "订单界面";
        String PANEL_XXTX = "消息提醒";
        String PANEL_QTGN = "其它功能";
    }
}
