package com.ranger.phonerecorder.app;

import java.text.SimpleDateFormat;

import com.ranger.phonerecorder.pojos.EmailServer;

import android.os.Environment;

public class Constants {

	public static final boolean AUTO_UPLOAD = true;
	
	public static final String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "PhoneRecorder";
	public static final String PHONE_REMIND_DIR = CACHE_DIR + "/" + "remind";
	public static final String PHONE_REMIND_FILE = PHONE_REMIND_DIR + "/" + "incall_remind.3gp";
	public static final String PHONE_MESSAGE_DIR = CACHE_DIR + "/" + "message/";

	private static final String date_format = "yyyy-MM-dd_HH_mm_ss";
	private static final String time_format_email_subject = "yyyyMMddHHmmss";
	private static final String FORMATTER_DATE_STRING = "yyyy-MM-dd";
	public static final SimpleDateFormat EMAIL_SUBJECT_TIME_FORMATTER = new SimpleDateFormat(time_format_email_subject);
	public static final SimpleDateFormat date_formater = new SimpleDateFormat(date_format);
	public static final SimpleDateFormat FORMATER_DATE_FORMAT = new SimpleDateFormat(FORMATTER_DATE_STRING);
	
	public static String REMINDER_PATH = "";
	
	public static EmailServer emailServerInfo;
}
