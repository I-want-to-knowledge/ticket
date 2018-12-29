package org.ticket.ticket.config;

import java.util.Base64;

/**
 * 数据的编码解码
 *
 * @author YanZhen
 * 2018-11-22 16:04:41
 * Base64Config
 */
public class Base64Config {
	
	private Base64Config() {}
	
	private static Base64.Encoder encoder = Base64.getEncoder();
	private static Base64.Decoder decoder = Base64.getDecoder();
	
	public static void main(String[] args) {
	}
	
	/**
	 * 编码
	 *
	 * 2018-11-22 16:08:07
	 * @param b
	 * @return String
	 */
	static String encoder(byte[] b) {
		return encoder.encodeToString(b);
	}
	
	/**
	 * 解码
	 *
	 * 2018-11-22 16:08:59
	 * @param str
	 * @return byte[]
	 */
	static byte[] decoder(String str) {
		return decoder.decode(str);
	}
}

