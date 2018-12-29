package org.ticket.ticket.utils;

/**
 * @Auther: YanZhen 作者
 * @Date: 2018-12-29 10:55
 * @Description: 路径工具类
 **/
class PathUtils {
    static String jpg12306Path() {
        return PathUtils.class.getResource("/").getPath().substring(1).replaceAll("%20", " ").concat("image/12306.jpg");
    }

    /*static String pathClass(String fileName) {
        return PathUtils.class.getResource("").getPath().substring(1).replaceAll("%20", " ").concat(fileName);
    }*/
}
