package com.ranger.bmaterials.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ranger.bmaterials.work.SpeedDownloadInfoTask;

public class UnInstallReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 应用卸载
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			if (packageName.equals(context.getPackageName())) {
				// 用于高速下载 监听自身应用被卸载
				SharedPreferences speed_sp = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor edit = speed_sp.edit();
				edit.putBoolean(SpeedDownloadInfoTask.SPEED_DOWNLOAD_SP, true);
				edit.commit();
			} 
		}
	}
}
