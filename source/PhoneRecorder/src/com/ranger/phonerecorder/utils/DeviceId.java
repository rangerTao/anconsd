package com.ranger.phonerecorder.utils;

import android.content.Context;
import android.text.TextUtils;

public class DeviceId {

	public static String getAndroidId(Context context) {
		String s = "";
		s = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
		if (TextUtils.isEmpty(s))
			s = "";
		return s;
	}
}
