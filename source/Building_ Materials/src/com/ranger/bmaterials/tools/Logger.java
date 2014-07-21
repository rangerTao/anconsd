package com.ranger.bmaterials.tools;

import android.util.Log;

import com.ranger.bmaterials.app.Constants;

public class Logger {

	public static void v(String tag,String msg) {
		if (Constants.DEBUG) {
			Log.v(tag, msg);
		}
	}
	
	public static void i(String tag,String msg) {
		if (Constants.DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void d(String tag,String msg) {
		if (Constants.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag,String msg) {
		if (Constants.DEBUG) {
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag,String msg) {
		if (Constants.DEBUG) {
			Log.e(tag, msg);
		}
	}
}
