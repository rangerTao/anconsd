package com.ranger.bmaterials.broadcast;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.download.Downloads;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.ui.ManagerActivity;

public class Notifier {
	public static final int UPDATENOTIFYID = 100;
	private static final int COMMON_DOWNLOAD_ID = 200;
	private static final int COMMON_SUCCESSFUL_ID = 201;
	private static final int COMMON_FAILED_ID = 202;

	private static int showCOunt = 0;
	/**
	 * 更新总的下载通知
	 */
	public static void updateNotificationForDownload() {
		
		int runningSize = 0;
		int waittingSize = 0;
		
		Map<DownloadStatus, Integer> ret = PackageHelper.loadDownloadTasks();
		
		runningSize = ret.get(DownloadStatus.STATUS_RUNNING);
		waittingSize = ret.get(DownloadStatus.STATUS_PENDING);

		if (runningSize + waittingSize == 0) {
			cancleNotification(COMMON_DOWNLOAD_ID);
		} else {
			StringBuffer textSb = new StringBuffer();
			// cancleNotification(COMMON_ID);
			// TODO
			if (runningSize > 0) {
				textSb.append(String.format("%d个下载中", runningSize));
			}
			if (waittingSize > 0) {
				if (textSb.length() > 0) {
					textSb.append(",");
				}
				textSb.append(String.format("%d个下载等待", waittingSize));
			}
			
			if (textSb.length() > 0) {
				textSb.append("，点击查看");
			}
			
			String title = String.format("%d个下载任务",
					(runningSize + waittingSize));
			String ticker = null;// title;
			showNotification(COMMON_DOWNLOAD_ID, ticker, title,
					textSb.toString(), false, ManagerActivity.class);
		}
	}
	/**
	 * TODO
	 */
	public static void updateNotificationForFailedDownload() {
		Map<DownloadStatus, Integer> tasks = PackageHelper.loadDownloadTasks();
		Integer failedSize = tasks.get(DownloadStatus.STATUS_FAILED);
		if(failedSize != null  && failedSize >0){
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_notifier;
			long id = COMMON_FAILED_ID ;
			String ticker = null ;
			String title = String.format("%d个任务下载失败", failedSize);
			String caption = "点击查看";
			showNotification(id, ticker, title, caption, true,
					ManagerActivity.class);

			updateNotificationForDownload();
		}else{
			cancleNotification(COMMON_FAILED_ID);
		}
		
	}
	
	public static void updateDownloadFinishedNotification() {
		Map<DownloadStatus, Integer> tasks = PackageHelper.loadDownloadTasks();
		Integer successfulSize = tasks.get(DownloadStatus.STATUS_SUCCESSFUL);
		if(successfulSize != null && successfulSize > 0){
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_notifier;

			long id = COMMON_SUCCESSFUL_ID ;
			String ticker = null ;
			String title = String.format("%d个任务已完成", successfulSize);
			String caption = "点击进入下载管理";
			showNotification(id, ticker, title, caption, true,
					ManagerActivity.class);

			updateNotificationForDownload();
		}else{
			cancleNotification(COMMON_SUCCESSFUL_ID);
		}
		

	}


	public static void showDownloadFinishedNotification(
			DownloadItemOutput output) {

		Map<DownloadStatus, Integer> tasks = PackageHelper.loadDownloadTasks();
		Integer successfulSize = tasks.get(DownloadStatus.STATUS_SUCCESSFUL);
		if(successfulSize != null && successfulSize >0){
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_notifier;

			long id = COMMON_SUCCESSFUL_ID ;
			String ticker = String.format("%s下载完成", output.getTitle());
			String title = String.format(Locale.getDefault(),"%d个任务已完成", successfulSize);
			String caption = "点击进入下载管理";
			showNotification(id, ticker, title, caption, true,
					ManagerActivity.class);

			updateNotificationForDownload();
		}else{
			cancleNotification(COMMON_SUCCESSFUL_ID);
		}
		

	}
	
	public static void showDownloadFailedNotification(
			DownloadItemOutput output) {

		Map<DownloadStatus, Integer> tasks = PackageHelper.loadDownloadTasks();
		Integer failedSize = tasks.get(DownloadStatus.STATUS_FAILED);
		if(failedSize != null  && failedSize >0){
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_notifier;
			long id = COMMON_FAILED_ID ;
			String ticker = String.format("%s下载失败", output.getTitle());
			String title = String.format("%d个任务下载失败", failedSize);
			String caption = "点击查看";
			showNotification(id, ticker, title, caption, true,
					ManagerActivity.class);

			updateNotificationForDownload();
		}else{
			cancleNotification(COMMON_FAILED_ID);
		}
		

	}

	public static void showDownloadStartNotification(DownloadItemOutput out) {
		
		int runningSize = 0;
		int waittingSize = 0;
		Map<DownloadStatus, Integer> ret = PackageHelper.loadDownloadTasks();
		runningSize = ret.get(DownloadStatus.STATUS_RUNNING);
		waittingSize = ret.get(DownloadStatus.STATUS_PENDING);
		if (runningSize + waittingSize == 0) {
			cancleNotification(COMMON_DOWNLOAD_ID);
		} else {
			StringBuffer textSb = new StringBuffer();
			// cancleNotification(COMMON_ID);
			// TODO
			if (runningSize > 0) {
				textSb.append(String.format("%d个下载中", runningSize));
			}
			if (waittingSize > 0) {
				if (textSb.length() > 0) {
					textSb.append(",");
				}
				textSb.append(String.format("%d个下载等待", waittingSize));
			}
			if (textSb.length() > 0) {
				textSb.append("，点击查看");
			}
			String title = String.format("%d个下载任务",
					(runningSize + waittingSize));
			String ticker = String.format("%s开始下载", out.getTitle());
			cancleNotification(COMMON_DOWNLOAD_ID);
			showNotification(COMMON_DOWNLOAD_ID, ticker, title,
					textSb.toString(), false, ManagerActivity.class);
		}
	}

	/**
	 * 取消下载后取消此单条通知()
	 * 
	 * @param downloadUrl
	 * @deprecated
	 */
	public static void removeNotificationForDelete(String downloadUrl) {
		cancleNotification(downloadUrl.hashCode());
	}

	private static void cancleNotification(int id) {
		NotificationManager nm = (NotificationManager) GameTingApplication
				.getAppInstance()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(id);
	}

	@SuppressWarnings("deprecation")
	private static void showNotification(long id, String ticker, String title,
			String text, boolean cancleable, Class<?> clazz) {

		Context context = GameTingApplication.getAppInstance();

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_notifier;
		Intent contentIntent = new Intent(context, clazz);
		contentIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID, id);
		contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		if (ticker != null) {
			notification.tickerText = ticker;
		}
		
		contentIntent.putExtra("from", "notification");
		
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(context, title, text,
				PendingIntent.getActivity(context, 0, contentIntent, 0));

		Intent delIntent = new Intent(
				NotificaionReceiver.ACTION_CANCLE_NOTIFICATION);
		delIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID, id);

		notification.deleteIntent = PendingIntent.getBroadcast(context, 0,
				delIntent, 0);

		if (!cancleable) {
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE
					| Notification.FLAG_ONGOING_EVENT
					| Notification.FLAG_NO_CLEAR;
		} else {
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify((int) id, notification);

	}

	public static void notifyUpdatableList() {

		if (AppUtil.isGameSearchForeground()) {
			return;
		}

		if (hasShowNotification()) {
			return;
		}

		AppManager manager = AppManager.getInstance(GameTingApplication
				.getAppInstance());
		List<UpdatableAppInfo> updatableGames = manager.getUpdatableGames(true);
		if (updatableGames != null && updatableGames.size() > 0) {
			String tricker = "更新通知";
			String title = "您有" + updatableGames.size() + "个游戏可更新";
			String content = "";
			switch (updatableGames.size()) {
			case 1:
				content += updatableGames.get(0).getName() + "游戏可更新; 点击查看";
				break;

			case 2:
				content += updatableGames.get(0).getName() + "、"
						+ updatableGames.get(1).getName() + "游戏可更新; 点击查看";
				break;

			default:
				content += updatableGames.get(0).getName() + "、"
						+ updatableGames.get(1).getName() + "等游戏可更新; 点击查看";
				break;
			}

			Context context = GameTingApplication.getAppInstance();
			Intent intent = new Intent(context, ManagerActivity.class);
			intent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_UPDATE, true);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pt = PendingIntent.getActivity(context, 0, intent, 0);

			Intent delIntent = new Intent(
					NotificaionReceiver.ACTION_CANCLE_NOTIFICATION);
			delIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID,
					UPDATENOTIFYID);
			PendingIntent delPendingIntent = PendingIntent.getBroadcast(
					context, 0, delIntent, 0);

			int flags = 0;
			if (AppUtil.isGameSearchForeground()) {
				flags = Notification.DEFAULT_SOUND;
			} else {
				flags = Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE;
			}
			
			SharedPreferences sp = GameTingApplication.getAppInstance().getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
			
			long last_show = sp.getLong(Constants.SP_UPDATE_NOTIFICATION_LAST_SHOW, 0);
			
			if((System.currentTimeMillis() - last_show) > Constants.UPDATE_NOTIFICATION_WAIT_DURATION){
				showUpdateNotification(UPDATENOTIFYID, tricker, title, content,
						pt, delPendingIntent, flags, true);
				
				Editor editor = sp.edit();
				editor.putLong(Constants.SP_UPDATE_NOTIFICATION_LAST_SHOW, System.currentTimeMillis());
				editor.commit();
			}
			
			
		}
	}

	public static void cancleNotifyUpdatableList() {
		try {
			NotificationManager nm = (NotificationManager) GameTingApplication
					.getAppInstance().getSystemService(
							Context.NOTIFICATION_SERVICE);
			nm.cancel(UPDATENOTIFYID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean hasShowNotification() {
		Time t = new Time();
		t.setToNow();
		int hour = t.hour;
		boolean ret = false;

		if (hour >= 7 && hour <= 23) {
			if (MineProfile.getInstance().isHasShowNotification()) {
				ret = true;
			} else {
				MineProfile.getInstance().setHasShowNotification(true);
				ret = false;
			}
		} else {
			ret = true;
			MineProfile.getInstance().setHasShowNotification(false);
		}

		return ret;
	}

	private static void showUpdateNotification(int notifyID, String tickertext,
			String title, String content,
			PendingIntent intent, PendingIntent delIntent, int flags,
			boolean cancleable) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				GameTingApplication.getAppInstance());
		builder.setSmallIcon(R.drawable.ic_notifier);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());
		builder.setDeleteIntent(delIntent);
		/*RemoteViews contentViews = new RemoteViews(GameTingApplication
				.getAppInstance().getPackageName(),
				R.layout.update_notification_layout);
		contentViews
				.setImageViewResource(R.id.img_icon, R.drawable.ic_notifier);
		contentViews.setTextViewText(R.id.label_content, content);
		contentViews.setTextViewText(R.id.label_subcontent, subcontent);
		builder.setContent(contentViews);*/

		if (!cancleable) {
			flags |= Notification.FLAG_ONLY_ALERT_ONCE
					| Notification.FLAG_ONGOING_EVENT;
		} else {
			flags |= Notification.FLAG_AUTO_CANCEL;
		}

		builder.setDefaults(flags);

		/*
		 * if (AppUtil.isGameSearchForeground()) {
		 * builder.setDefaults(Notification.DEFAULT_SOUND); } else {
		 * builder.setDefaults(Notification.DEFAULT_SOUND |
		 * Notification.DEFAULT_VIBRATE); }
		 */

		NotificationManager nm = (NotificationManager) GameTingApplication
				.getAppInstance()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());
		
	}

	private static void show(int id, String title, String content,
			Class<?> target, boolean cancleable) {
		try {

			Notification notification = new Notification();
			notification.icon = R.drawable.ic_notifier;
			String caption = content;
			Application context = GameTingApplication.getAppInstance();
			Intent contentIntent = new Intent(context, target);
			contentIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_UPDATE,
					true);
			contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			notification.when = System.currentTimeMillis(); // download.mLastMod;
			notification.setLatestEventInfo(context, title, caption,
					PendingIntent.getActivity(context, 0, contentIntent, 0));

			Intent delIntent = new Intent(
					NotificaionReceiver.ACTION_CANCLE_NOTIFICATION);
			// delIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID,
			// app.getDownloadId());
			notification.deleteIntent = PendingIntent.getBroadcast(context, 0,
					delIntent, 0);
			notification.tickerText = title;

			if (!cancleable) {
				notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE
						| Notification.FLAG_ONGOING_EVENT;
			} else {
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
			}

			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(id, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
