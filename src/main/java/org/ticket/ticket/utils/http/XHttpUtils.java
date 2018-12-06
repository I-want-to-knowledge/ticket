package org.ticket.ticket.utils.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.ticket.ticket.utils.XConstant;

/**
 * 
 *
 * @author YanZhen
 * 2018-11-21 18:52:40
 * XHttpUtils
 */
public class XHttpUtils {

	/**
	 * inputStream 转 byte
	 *
	 * 2018-11-21 18:59:18
	 * @param inputStream
	 * @return byte[]
	 * @throws IOException 
	 */
	public static byte[] inputStreamToByte(InputStream inputStream) throws IOException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				// 用输入流往buffer里写数据，0代表写入的位置，len代表读取的长度
				baos.write(buffer, 0, len);
			}
			baos.flush();
			
			return baos.toByteArray();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	/**
	 * 将网页返回的结果封装成字符串
	 *
	 * 2018-11-26 17:47:56
	 * @param in
	 * @return String
	 */
	public static String outHtml(InputStream in) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(inr);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			result = new String(sb.toString().getBytes(), XConstant.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
