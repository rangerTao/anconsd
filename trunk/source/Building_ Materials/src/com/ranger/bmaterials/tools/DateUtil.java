package com.ranger.bmaterials.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	// "2013-05-10 17:36:51"
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT1 = "yyyy-MM-dd-HH-mm-ss";
	public static final String FORMAT2 = "MM/dd/yyyy HH:mm";
	public static final String FORMAT3 = "yyyy-MM-dd:HH-mm-ss";

	public static final String DEFAULT_DISPLAY_FORMAT = "yyyy.MM.dd";

	// public static final String DEFAULT_DISPLAY_FORMAT = "yyyy.MM.dd HH:mm";

	public static Date pareseDate(String format, String date) {
		DateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static Date pareseDate(String date) {
		DateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDate(long date, String format) {
		try {
			DateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(new Date(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDate(Date date, String format) {
		try {
			DateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDate(Date date) {
		return formatDate(date, DEFAULT_DISPLAY_FORMAT);
	}

	/**
	 * 判断两个日期是否是同一天
	 * 
	 * @param date1
	 *            date1
	 * @param date2
	 *            date2
	 * @return
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

}
