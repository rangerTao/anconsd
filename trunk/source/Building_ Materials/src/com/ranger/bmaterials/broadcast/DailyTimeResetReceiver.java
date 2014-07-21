package com.ranger.bmaterials.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class DailyTimeResetReceiver extends BroadcastReceiver {
	public static final String DAILY_TIME_REST = "daily_time_reset";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(DAILY_TIME_REST)) {
			onReceiveHandler(context, intent);
		}
	}

	public abstract void onReceiveHandler(Context context, Intent intent);
}
