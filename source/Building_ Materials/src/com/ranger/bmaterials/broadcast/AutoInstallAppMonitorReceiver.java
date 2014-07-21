package com.ranger.bmaterials.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.tools.AppUtil;

public class AutoInstallAppMonitorReceiver extends BroadcastReceiver {
	static String TAG = "AutoInstallAppMonitorReceiver" ;
	
	
	public static class AutoInstall{
		/**
		 * silentinstallservice安装某个apk结束
		 */
		public static final String ACTION_PACKAGE_ADDED_AUTO = "duoku.gamesearch.intent.action.PACKAGE_ADDED_AUTO";
		/**
		 * silentinstallservice结束
		 */
		public static final String ACTION_INSTALL_SERVICE_FINISHED = "duoku.gamesearch.intent.action.INSTALL_SERVICE_FINISHED";
		public static final String EXTRA_PACKAGE_AUTO = "package_AUTO";
		public static final String EXTRA_FILE_AUTO = "file_AUTO";
		public static final String EXTRA_STATUE_AUTO = "status_AUTO";
		public static final String EXTRA_ERROR_REASON = "error_reason";
		public static final String EXTRA_ID_AUTO = "id_AUTO";
		public static final String EXTRA_GAME_ID_AUTO = "game_id_AUTO";
		public static final String EXTRA_DOWNLOAD_URL_AUTO = "download_url_AUTO";
		public static final String EXTRA_ITEM_AUTO = "item_AUTO";
		
	}
	public static class AutoUninstall{
		public static final String ACTION_PACKAGE_REMOVED_AUTO = "duoku.gamesearch.intent.action.PACKAGE_REMOVED_AUTO";
	}
	
	
	private Context context ;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context ;
		updateDataIfNecessary(intent);
	}
	
	
	
	private void updateDataIfNecessary(final Intent intent){
		new Thread(){
			public void run() {
				String action = intent.getAction();
			}
			;
		}.start();
	}
	
	
	
	

}
