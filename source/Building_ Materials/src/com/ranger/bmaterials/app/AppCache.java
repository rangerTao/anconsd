package com.ranger.bmaterials.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ranger.bmaterials.broadcast.AppMonitorReceiver;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;

/**
 * 暂时没有使用内存缓存（原先考虑的是不用每次都查询数据库，但是觉得内存缓存数据状态较多，比较麻烦）
 * 
 * @author wangliang
 * 
 */
public class AppCache {
	public static final String TAG = "AppCache";
	static AppCache INSTANCE;

	private AppCache() {
		registerReceiver(GameTingApplication.getAppInstance());
	}

	/**
	 * 是否使用cache
	 */
	private boolean useCache = false;

	public static AppCache getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AppCache();
		}
		return INSTANCE;
	}

	private CopyOnWriteArrayList<UpdatableAppInfo> updatableList = new CopyOnWriteArrayList<UpdatableAppInfo>();
	private CopyOnWriteArrayList<DownloadAppInfo> downloadList = new CopyOnWriteArrayList<DownloadAppInfo>();
	private CopyOnWriteArrayList<InstalledAppInfo> installedList = new CopyOnWriteArrayList<InstalledAppInfo>();

	private void setUnpdatable(List<UpdatableAppInfo> list) {
		this.updatableList.clear();
		if (list != null) {
			this.updatableList.addAll(list);
		}

	}

	private void setDownload(List<DownloadAppInfo> list) {
		this.downloadList.clear();
		if (list != null) {
			this.downloadList.addAll(list);
		}
	}

	public List<DownloadAppInfo> getDownloads() {
		return new ArrayList<DownloadAppInfo>(downloadList);
	}

	public List<InstalledAppInfo> getInstalleds() {
		return new ArrayList<InstalledAppInfo>(installedList);
	}

	private volatile boolean initialized = false;

	public boolean isInitialize() {
		if (!useCache) {
			return false;
		}
		return initialized;
	}

	private void setInstall(List<InstalledAppInfo> list) {
		this.installedList.clear();
		if (list != null) {
			this.installedList.addAll(list);
		}
	}

	public List<UpdatableAppInfo> getUnpdatable() {
		return new ArrayList<UpdatableAppInfo>(this.updatableList);
	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.DEBUG)
				Log.i(AppCache.TAG, "[AppCache]onReceive " + action);
			if (BroadcaseSender.ACTION_PACKAGE_ADDED.equals(action)
			/* ||AppMonitorReceiver.ACTION_PACKAGE_REPLACED.equals(action) */) {
				// refreshDownload(context);
				notifyAppsChanged(context);
			} else if (BroadcaseSender.ACTION_PACKAGE_REMOVED.equals(action)) {
				// refreshDownload(context);
				notifyAppsChanged(context);
			} else if (BroadcaseSender.ACTION_INSTALLED_LIST_INITIALIZED
					.equals(action)
					|| BroadcaseSender.ACTION_WHITELIST_INITIALIZED
							.equals(action)) {
				// refreshInstall(context);
				notifyAppsChanged(context);
			} else if (BroadcaseSender.ACTION_UPDATABLE_LIST_INITIALIZED
					.equals(action)) {
				// refreshUpdatable(context);
			} else if (BroadcaseSender.ACTION_DOWNLOAD_CHANGED.equals(action)) {
				// 下载变化时
				notifyAppsChanged(context);
				// refreshDownload(context);
				// refreshUpdatable(context);
			} else if (BroadcaseSender.ACTION_INSTALL_CHANGED.equals(action)) {
				// mLoader.onInstallChanged();
			} else if (BroadcaseSender.ACTION_IGNORED_STATE_CHANGED
					.equals(action)) {
				// 更新状态变化
				notifyAppsChanged(context);
			} else if (action.equals(BroadcaseSender.ACTION_PRE_PACKAGE_EVENT)) {
				notifyAppsChanged(context);

			}
		}
	}

	/**
	 * 通知下载和更新的app数量的变化
	 * 
	 * @param context
	 */
	private void notifyAppsChanged(final Context context) {
		new Thread() {
			public void run() {
				int popNumber = getPopNumber(context);
				if (Constants.DEBUG)
					Log.i("PopNumber",
							"[AppCache#notifyAppsChanged] popNumber:"
									+ popNumber);
				// Log.i(AppCache.TAG, "[AppCache]notifyAppsChanged popNumber "
				// +popNumber);
				BroadcaseSender sender = BroadcaseSender.getInstance(context);
				sender.notifyManagerAppsChanged(popNumber);
			};
		}.start();

	}

	public int getPopNumber(Context context) {
		if (!useCache) {
			AppManager appManager = AppManager.getInstance(context);
			return appManager.getPopNumberFromDB();

		}
		CopyOnWriteArrayList<UpdatableAppInfo> updatable = this.updatableList;
		int count = 0;
		for (UpdatableAppInfo updatableAppInfo : updatable) {
			if (!updatableAppInfo.isIgnoreUpdate()) {
				count++;
			}
		}
		if (Constants.DEBUG)
			Log.i(TAG, "[getPopNumber]updatable but not ignored count:" + count
					+ " all updatable:" + updatable.size());
		CopyOnWriteArrayList<DownloadAppInfo> download = this.downloadList;
		count += download.size();
		if (Constants.DEBUG)
			Log.i("PopNumber", "[AppCache#getPopNumber]download size:"
					+ download.size() + " total count " + count);
		return count;
	}

	private MyBroadcastReceiver myBroadcastReceiver;

	private void registerReceiver(Context context) {
		if (myBroadcastReceiver == null) {
			myBroadcastReceiver = new MyBroadcastReceiver();
			IntentFilter filter = new IntentFilter(
					BroadcaseSender.ACTION_PACKAGE_ADDED);
			filter.addAction(BroadcaseSender.ACTION_PACKAGE_REMOVED);
			filter.addDataScheme("package");
			context.registerReceiver(myBroadcastReceiver, filter);

			IntentFilter downloadFilter = new IntentFilter();
			downloadFilter.addAction(BroadcaseSender.ACTION_DOWNLOAD_CHANGED);
			context.registerReceiver(myBroadcastReceiver, downloadFilter);

			IntentFilter installFilter = new IntentFilter();
			installFilter.addAction(BroadcaseSender.ACTION_INSTALL_CHANGED);
			context.registerReceiver(myBroadcastReceiver, installFilter);

			IntentFilter ignoredFilter = new IntentFilter();
			ignoredFilter
					.addAction(BroadcaseSender.ACTION_IGNORED_STATE_CHANGED);
			context.registerReceiver(myBroadcastReceiver, ignoredFilter);

			IntentFilter initFilter = new IntentFilter();
			initFilter
					.addAction(BroadcaseSender.ACTION_INSTALLED_LIST_INITIALIZED);
			initFilter.addAction(BroadcaseSender.ACTION_WHITELIST_INITIALIZED);
			context.registerReceiver(myBroadcastReceiver, initFilter);

			IntentFilter preFilter = new IntentFilter();
			preFilter.addAction(BroadcaseSender.ACTION_PRE_PACKAGE_EVENT);
			preFilter.addDataScheme("package");
			context.registerReceiver(myBroadcastReceiver, preFilter);

		}

	}

	public void onDestroy() {
		this.downloadList.clear();
		this.installedList.clear();
		this.updatableList.clear();
		unregisterReceiver(GameTingApplication.getAppInstance());
		INSTANCE = null;
	}

	private void unregisterReceiver(Context context) {
		if (myBroadcastReceiver != null) {
			context.unregisterReceiver(myBroadcastReceiver);
			myBroadcastReceiver = null;
		}
	}

	public void refreshUpdatable(Context context) {
		if (!useCache) {
			return;
		}
		if (Constants.DEBUG)
			Log.i(TAG, "[AppCache]before refreshUpdatable:" + updatableList);
		AppManager manager = AppManager.getInstance(context);
		List<UpdatableAppInfo> allUpdatableGames = manager
				.getAllUpdatableGamesFromDB(false);
		setUnpdatable(allUpdatableGames);
		if (Constants.DEBUG)
			Log.i(TAG, "[AppCache]after refreshUpdatable:" + updatableList);
	}

	public void refreshDownload(Context context) {
		if (!useCache) {
			return;
		}
		if (Constants.DEBUG)
			Log.i(TAG, "[AppCache]before refreshDownload:" + downloadList);
		AppManager manager = AppManager.getInstance(context);
		List<DownloadAppInfo> downloadGames = manager
				.getDownloadGamesFromDB(false);
		setDownload(downloadGames);
		if (Constants.DEBUG)
			Log.i(TAG, "[AppCache]after refreshDownload:" + downloadList);
		logDownloadLoad();
	}

	private void logDownloadLoad() {
		int size = downloadList.size();
		for (int i = 0; i < size; i++) {
			DownloadAppInfo d = downloadList.get(i);
			if (Constants.DEBUG)
				Log.i(TAG, "downloadList[" + i + "]>>>" + d.getStatus() + " "
						+ d.getCurrtentSize() + "/" + d.getTotalSize());
		}
	}

	public void refreshInstall(Context context) {
		if (!useCache) {
			return;
		}
		if (Constants.DEBUG)
			Log.i(TAG, "[AppCache]before refreshInstall:" + installedList);
		AppManager manager = AppManager.getInstance(context);
		List<InstalledAppInfo> installedApps = manager
				.getInstalledGamesFromDB();
		setInstall(installedApps);
		if (Constants.DEBUG)
			Log.i(TAG, "[AppCache]after refreshInstall:" + installedList);
	}

	public Runnable onCreate() {
		if (Constants.DEBUG)
			Log.d(AppCache.TAG, "[AppCache]onCreate");
		updatableList.clear();
		downloadList.clear();
		installedList.clear();

		return new Runnable() {
			public void run() {
				Application context = GameTingApplication.getAppInstance();

				refreshInstall(context);
				refreshDownload(context);
				refreshUpdatable(context);
				initialized = true;
			}
		};
	}
}
