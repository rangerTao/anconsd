package com.ranger.phonerecorder.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {

	public static String connectHttpGet(String url) {

		StringBuffer sb = new StringBuffer();
		try {
			URL urls = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urls.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			con.setRequestProperty("charset", "UTF-8");
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Linux; U; Android 4.0.4; zh-cn; N90 DUAL CORE Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30");

			con.setRequestProperty("Accept-Language", "zh-CN, en-US");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Charset", "utf-8");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setReadTimeout(10000);
			con.setConnectTimeout(10000);
			OutputStream os = con.getOutputStream();
			os.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			in.close();

			String result = sb.toString();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
