/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ranger.bmaterials.download;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.download.DownloadConfiguration.OnNotifierClickListener;
import com.ranger.bmaterials.speeddownload.SpeedDonwloadService;
import com.ranger.bmaterials.tools.MyLogger;
import com.ranger.bmaterials.updateservice.CheckGameUpdateService;

/*import com.ranger.bmaterials.R;
 import com.duoku.gamesearch.myapp.MyAppConstants;
 import com.duoku.gamesearch.tools.AppUtils;*/

/**
 * Receives system broadcasts (boot, network connectivity)
 */
public class DownloadReceiver extends BroadcastReceiver {
	/** SystemFacade. */
	SystemFacade mSystemFacade = null;
	/** DownloadManager */
	DownloadManager mDownloadManager = null;

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (mSystemFacade == null) {
			mSystemFacade = new RealSystemFacade(context);
		}
		DownloadConfiguration config = DownloadConfiguration
				.getInstance(context);
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			if (config.isAutoResume()) {
				startService(context);
			} else {
				startIfNecessary(context);
			}

			Intent i = new Intent(context, CheckGameUpdateService.class);
			context.startService(i);

			if (com.ranger.bmaterials.app.Constants.speed_download_enable) {
				// 开机启动监听高速下载请求的service
				Intent speedDownIntent = new Intent(context,
						SpeedDonwloadService.class);
				context.startService(speedDownIntent);
			}
		} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			if (Constants.LOGVV) {
				Log.v(Constants.TAG, "Received broadcast intent for "
						+ Intent.ACTION_MEDIA_MOUNTED);
			}
			if (config.isAutoResume())
				startService(context);
		} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			/*
			 * @SuppressWarnings("deprecation") NetworkInfo info = (NetworkInfo)
			 * intent
			 * .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO); if
			 * (info != null && info.isConnected()) { startService(context); }
			 */

			final ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo info = connManager.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				// if(config.isAutoResume()){
				startService(context);
				// }
				new Thread() {
					public void run() {
						DownloadUtil.resumeLastPausedDownload(context);
					};
				}.start();

			}
			/*
			 * if(!info.isAvailable()) if(isGameSearchAppTop(context)){
			 * Toast.makeText(context,
			 * context.getResources().getString(R.string.net_disconnect),
			 * Toast.LENGTH_LONG).show(); }
			 */
		} else if (action.equals(Constants.ACTION_RETRY)) {
			startService(context);
		} else if (action.equals(Constants.ACTION_OPEN)
				|| action.equals(Constants.ACTION_LIST)
				|| action.equals(Constants.ACTION_HIDE)) {
			MyLogger logger = MyLogger.getLogger(this.getClass()
					.getSimpleName());
			logger.i("DownloadReceiver receive intent " + action);
			handleNotificationBroadcast(context, intent);
		} else if (action.equals(Constants.ACTION_REDOWNLOAD)) {
			if (mDownloadManager == null) {

				mDownloadManager = DownloadManager.getInstance(context);
			}
			long id = intent.getLongExtra(Constants.DOWNLOAD_ID, -1);
			// ����ʱ�����һ�����ؼ�¼
			// StatisticProcessor.getInstance(context).addOneStartDownloadStatisticData();
			mDownloadManager.restartDownload(context, id);

			// �������ʧ�ܵ�notification.
			/*
			 * String appKey =
			 * intent.getStringExtra(MyAppConstants.EXTRA_APP_KEY); if
			 * (!TextUtils.isEmpty(appKey)) {
			 * AppUtils.cancelSilentInstallingNotification(context, appKey); }
			 */
		}
	}

	private void startIfNecessary(final Context context) {
		new Thread() {
			@Override
			public void run() {
				boolean flag = checkAppRunning(context);
				if (flag) {
					startService(context);
				}
			}
		}.start();

	}

	private boolean checkAppRunning(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = am.getRunningTasks(30);
		String myPkg = context.getPackageName();
		if (!runningTasks.isEmpty()) {
			int size = runningTasks.size();
			for (int i = 0; i < size; i++) {
				RunningTaskInfo task = runningTasks.get(i);
				ComponentName ta = task.topActivity;
				String pkg = ta.getPackageName();
				if (pkg.equals(myPkg)
						&& !ta.getClassName().equals(this.getClass().getName())) {
					// Logger.d(TAG,"Our app running in backgroud:"+task.topActivity.getClassName());
					return true;

				}
			}
		}
		// Logger.d(TAG,"Our app not start.");
		return false;
	}

	/**
	 * Handle any broadcast related to a system notification.
	 * 
	 * @param context
	 *            context
	 * @param intent
	 *            intent
	 */
	private void handleNotificationBroadcast(Context context, Intent intent) {
		Uri uri = intent.getData();
		String action = intent.getAction();
		if (Constants.LOGVV) {
			if (action.equals(Constants.ACTION_OPEN)) {
				Log.v(Constants.TAG, "Receiver open for " + uri);
			} else if (action.equals(Constants.ACTION_LIST)) {
				Log.v(Constants.TAG, "Receiver list for " + uri);
			} else { // ACTION_HIDE
				Log.v(Constants.TAG, "Receiver hide for " + uri);
			}
		}

		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		if (cursor == null) {
			return;
		}
		try {
			if (!cursor.moveToFirst()) {
				return;
			}
			if (action.equals(Constants.ACTION_OPEN)) {
				DownloadConfiguration config = DownloadConfiguration
						.getInstance(context);
				OnNotifierClickListener onNotifierClickListener = config
						.getOnNotifierClickListener();
				boolean autoOpenDownloadedFile = config
						.isOpenOnClickSuccessfulDownload();
				if (autoOpenDownloadedFile) {
					openDownload(context, cursor);
				}
				if (onNotifierClickListener != null) {
					/*
					 * DownloadConfiguration.DownloadItem item = new
					 * DownloadConfiguration.DownloadItem();
					 * item.setId(Long.parseLong(uri.getPathSegments().get(1)));
					 * String filename =
					 * cursor.getString(cursor.getColumnIndexOrThrow
					 * (Downloads.Impl.DATA)); String mimetype =
					 * cursor.getString
					 * (cursor.getColumnIndexOrThrow(Downloads.Impl
					 * .COLUMN_MIME_TYPE)); item.setFileName(filename);
					 * item.setMimeType(mimetype);
					 */
					int downloadId = cursor.getInt(cursor
							.getColumnIndex(Downloads.Impl._ID));
					onNotifierClickListener.onDownloadCompleted(true,
							downloadId);
				}
				hideNotification(context, uri, cursor);
			} else if (action.equals(Constants.ACTION_LIST)) {
				sendNotificationClickedIntent(intent, cursor);

				// �������ʧ��Ҳ��Ҫɾ��֪ͨ��
				int statusColumn = cursor
						.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
				int status = cursor.getInt(statusColumn);

				/*
				 * if (Downloads.Impl.isStatusCompleted(status)) {
				 * hideNotification(context, uri, cursor); }
				 */
			} else { // ACTION_HIDE
				hideNotification(context, uri, cursor);
			}
		} finally {
			cursor.close();
		}
	}

	/**
	 * Hide a system notification for a download.
	 * 
	 * @param context
	 *            context
	 * @param uri
	 *            URI to update the download
	 * @param cursor
	 *            Cursor for reading the download's fields
	 */
	private void hideNotification(Context context, Uri uri, Cursor cursor) {
		mSystemFacade.cancelNotification(ContentUris.parseId(uri));

		int statusColumn = cursor
				.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
		int status = cursor.getInt(statusColumn);
		int visibilityColumn = cursor
				.getColumnIndexOrThrow(Downloads.Impl.COLUMN_VISIBILITY);
		int visibility = cursor.getInt(visibilityColumn);
		if (Downloads.Impl.isStatusCompleted(status)
				&& visibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
			ContentValues values = new ContentValues();
			values.put(Downloads.Impl.COLUMN_VISIBILITY,
					Downloads.Impl.VISIBILITY_VISIBLE);
			context.getContentResolver().update(uri, values, null, null);
		}
	}

	/**
	 * Open the download that cursor is currently pointing to, since it's
	 * completed notification has been clicked.
	 * 
	 * @param context
	 *            context
	 * @param cursor
	 *            cursor
	 */
	private void openDownload(Context context, Cursor cursor) {
		String filename = cursor.getString(cursor
				.getColumnIndexOrThrow(Downloads.Impl.DATA));
		String mimetype = cursor.getString(cursor
				.getColumnIndexOrThrow(Downloads.Impl.COLUMN_MIME_TYPE));
		Uri path = Uri.parse(filename);
		// If there is no scheme, then it must be a file
		if (path.getScheme() == null) {
			path = Uri.fromFile(new File(filename));
		}

		Intent activityIntent = new Intent(Intent.ACTION_VIEW);
		activityIntent.setDataAndType(path, mimetype);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(activityIntent);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(context, R.string.download_no_application_title,
					Toast.LENGTH_LONG).show();
			Log.d(Constants.TAG, "no activity for " + mimetype, ex);
		}
	}

	/**
	 * Notify the owner of a running download that its notification was clicked.
	 * 
	 * @param intent
	 *            the broadcast intent sent by the notification manager
	 * @param cursor
	 *            Cursor for reading the download's fields
	 */
	private void sendNotificationClickedIntent(Intent intent, Cursor cursor) {
		String pckg = cursor
				.getString(cursor
						.getColumnIndexOrThrow(Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE));
		if (pckg == null) {
			return;
		}

		String clazz = cursor
				.getString(cursor
						.getColumnIndexOrThrow(Downloads.Impl.COLUMN_NOTIFICATION_CLASS));
		boolean isPublicApi = cursor.getInt(cursor
				.getColumnIndex(Downloads.Impl.COLUMN_IS_PUBLIC_API)) != 0;

		Intent appIntent = null;
		if (isPublicApi) {
			appIntent = new Intent(DownloadManager.ACTION_NOTIFICATION_CLICKED);
			appIntent.setPackage(pckg);
		} else { // legacy behavior
			if (clazz == null) {
				return;
			}
			appIntent = new Intent(Downloads.Impl.ACTION_NOTIFICATION_CLICKED);
			appIntent.setClassName(pckg, clazz);
			if (intent.getBooleanExtra("multiple", true)) {
				appIntent.setData(Downloads.Impl.CONTENT_URI);
			} else {
				long downloadId = cursor.getLong(cursor
						.getColumnIndexOrThrow(Downloads.Impl._ID));
				appIntent.setData(ContentUris.withAppendedId(
						Downloads.Impl.CONTENT_URI, downloadId));
			}
		}

		mSystemFacade.sendBroadcast(appIntent);
	}

	/**
	 * start download service.
	 * 
	 * @param context
	 *            context.
	 */
	private void startService(Context context) {
		context.startService(new Intent(context, DownloadService.class));
	}

	private boolean isGameSearchAppTop(Context context) {
		String cpPackageName = context.getPackageName();
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String classNameTemp = "";
		String packageNameTemp = "";
		if (null != runningTaskInfos) {
			for (int i = 0, n = runningTaskInfos.size(); i < n; i++) {
				packageNameTemp = (runningTaskInfos.get(i).topActivity)
						.getPackageName();
				classNameTemp = (runningTaskInfos.get(i).topActivity)
						.getClassName();

				if (null == classNameTemp) {
					return false;
				} else {
					if ((cpPackageName.equals(packageNameTemp))
							&& isAppContainsAc(context, classNameTemp)) {
						return true;
					} else {
						continue;
					}
				}
			}

		}
		return false;
	}

	private HashSet<String> getActivitys(Context context) {
		HashSet<String> activitys = new HashSet<String>();
		String packageName = context.getPackageName();
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (packageInfo != null) {
			for (int i = 0; i < packageInfo.activities.length; i++) {
				String activityName = packageInfo.activities[i].name;
				activitys.add(activityName);
			}
		}

		return activitys;
	}

	private boolean isAppContainsAc(Context context, String activityName) {
		HashSet<String> containActivitys = getActivitys(context);
		return containActivitys.contains(activityName);
	}
}
