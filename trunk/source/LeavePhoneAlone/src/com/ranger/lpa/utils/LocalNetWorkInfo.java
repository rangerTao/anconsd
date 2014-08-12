package com.ranger.lpa.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LocalNetWorkInfo {

	/**
	 * 随机获取端口
	 * 
	 * @return
	 */
	public static int getRandPortEx(int min, int max) {
		for (int i = 0; i < 5; i++) {
			int p = 0;
			try {
				p = getRandPort(min, max);
				DatagramSocket server = new DatagramSocket(
						new InetSocketAddress(p));
				server.close();
				return p;

			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("已占用端口" + p);
			}

		}
		System.out.println("获取失败了");
		return 0;

	}

	public static int getRandPort(int min, int max) {
		int port = (int) (Math.random() * 10000 % (max - min + 1) + min);
		return port;
	}

	// TODO 获取广播地址
	public static String getBroadcastAddressEx() {
		String subnet;
		String ip = null;

		subnet = getSubnetMask();
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String[] ips = ip.split("\\.");
		String[] subnets = subnet.split("\\.");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ips.length; i++) {
			ips[i] = String.valueOf((~Integer.parseInt(subnets[i]))
					| (Integer.parseInt(ips[i])));
			sb.append(turnToStr(Integer.parseInt(ips[i])));
			if (i != (ips.length - 1))
				sb.append(".");
		}
		return turnToIp(sb.toString());
	}

	/**
	 * 使用JDK上API获取子网掩码
	 * 
	 * @return
	 */
	public static String getSubnetMask() {

		int prefix = 0;
		int[] ipSplit = new int[4];
		String subnetMask = null;

		InetAddress localHost;
		try {
			localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface;
			networkInterface = NetworkInterface.getByInetAddress(localHost);

			for (InterfaceAddress address : networkInterface
					.getInterfaceAddresses()) {
				// System.out.println(address.getNetworkPrefixLength());
				prefix = address.getNetworkPrefixLength();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		int index = 0;
		int split = 0;
		int remainder = 0;
		split = prefix / 8;
		remainder = prefix % 8;
		while (index < split) {
			ipSplit[index] = 255;
			index++;
		}
		if (remainder == 1)
			ipSplit[index] = 128;
		if (remainder == 2)
			ipSplit[index] = 192;
		if (remainder == 3)
			ipSplit[index] = 224;
		if (remainder == 4)
			ipSplit[index] = 240;
		if (remainder == 5)
			ipSplit[index] = 248;
		if (remainder == 6)
			ipSplit[index] = 252;
		if (remainder == 7)
			ipSplit[index] = 254;
		subnetMask = String.valueOf(ipSplit[0]) + "."
				+ String.valueOf(ipSplit[1]) + "." + String.valueOf(ipSplit[2])
				+ "." + String.valueOf(ipSplit[3]);
		return subnetMask;
	}

	/**
	 * 把带符号整形转换为二进制
	 * 
	 * @param num
	 * @return
	 */
	private static String turnToStr(int num) {
		String str = "";
		str = Integer.toBinaryString(num);
		int len = 8 - str.length();
		// 如果二进制数据少于8位,在前面补零.
		for (int i = 0; i < len; i++) {
			str = "0" + str;
		}
		// 如果num为负数，转为二进制的结果有32位，如1111 1111 1111 1111 1111 1111 1101 1110
		// 则只取最后的8位.
		if (len < 0)
			str = str.substring(24, 32);
		return str;
	}

	/**
	 * 把二进制形式的ip，转换为十进制形式的ip
	 * 
	 * @param str
	 * @return
	 */
	private static String turnToIp(String str) {
		String[] ips = str.split("\\.");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ips.length; i++) {
			sb.append(turnToInt(ips[i]));
			sb.append(".");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 把二进制转换为十进制
	 * 
	 * @param str
	 * @return
	 */
	private static int turnToInt(String str) {
		int total = 0;
		int top = str.length();
		for (int i = 0; i < str.length(); i++) {
			String h = String.valueOf(str.charAt(i));
			top--;
			total += ((int) Math.pow(2, top)) * (Integer.parseInt(h));
		}
		return total;
	}
}