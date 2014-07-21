package com.ranger.bmaterials.work;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ranger.bmaterials.broadcast.DailyTimeResetReceiver;

//显示首页金币更新气泡
public final class HeaderCoinAlarmTask {
	private HeaderCoinAlarmTask() {
	}

	public static final String SHOW_HOME_COIN_TIP_SP = "show_home_coin_tip";

	public static void initAlarm(Context cx) {

		Intent intent = new Intent(DailyTimeResetReceiver.DAILY_TIME_REST);
		PendingIntent sender = PendingIntent.getBroadcast(cx, 0, intent, 0);
		// 设置警报时间
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		AlarmManager am = (AlarmManager) cx
				.getSystemService(Context.ALARM_SERVICE);
		// 只会警报一次
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, sender);
	}

	public static boolean checkShowTip(Context cx) {
		Calendar c = Calendar.getInstance();
		// 每天五点启动 则提示 每日凌晨重置
		if (c.get(Calendar.HOUR_OF_DAY) >= 5) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(cx);
			if (sp.getBoolean(SHOW_HOME_COIN_TIP_SP, false)) {
				sp.edit().putBoolean(SHOW_HOME_COIN_TIP_SP, false).commit();
				return true;
			}
		}
		return false;
	}
}
