package com.ranger.bmaterials.updateservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ranger.bmaterials.tools.MyLogger;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "BootBroadcastReceiver";
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private MyLogger logger = MyLogger.getLogger(TAG);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			logger.d("start service--");
		}
	}
}
