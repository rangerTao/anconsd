package com.ranger.bmaterials.updateservice;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.FileHelper;
import com.ranger.bmaterials.tools.MyLogger;

public class CheckGameUpdateService extends Service {
	private final String TAG = "CheckGameUpdateService";
	private MyLogger logger = MyLogger.getLogger(TAG);
	private final String gameSearch = "com.duoku.gamesearch";
	private TimerTask timertask = null;
	private Timer timer = null;

	BroadcastReceiver receiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			startTimer();
		}
	};
	

	@Override
	public IBinder onBind(Intent intent) {
		logger.d("CheckGameUpdateService--onBind");
		return null;
	}

	@Override
	public void onCreate() {
		logger.d("CheckGameUpdateService--onCreate");
		super.onCreate();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		registerReceiver(receiver, filter);
		
		startTimer();
	}

	@Override
	public void onDestroy() {
		logger.d("CheckGameUpdateService--onDestroy");
		unregisterReceiver(receiver);
		
		Intent localIntent = new Intent();
		localIntent.setClass(this, CheckGameUpdateService.class); // 销毁时重新启动Service
		this.startService(localIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		logger.d("CheckGameUpdateService--onStartCommand");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	private void showNotification(int icon, String tickertext, String title, String content,
			PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_notifier);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());
		//builder.setContent(new RemoteViews(GameTingApplication.getAppInstance().getPackageName(),
				//R.layout.update_notification_layout));

		if (AppUtil.isGameSearchForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());
	}

	public void startTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		timer = new Timer(false);
		timertask = new TimerTask() {
			public void run() {
				logger.d("check games update------");
				long time = System.currentTimeMillis();
				long lastTime = MineProfile.getInstance().getTimeShowNotification();

				// 3小时check游戏更新一次
				if (Math.abs(time - lastTime) >= 3 * 3600 * 1000) {
					logger.d("check games update------time - lastTime >= 3 * 3600 * 1000");
					try {
						MineProfile.getInstance().setTimeShowNotification(
								System.currentTimeMillis());
						if (!ConnectManager.isNetworkConnected(CheckGameUpdateService.this)) {
							logger.d("No network");
							return;
						}

						if (!AppUtil.isGameSearchForeground()
								&& !ConnectManager.isWifi(CheckGameUpdateService.this)) {
							return;
						}
						
						FileHelper.saveResultToLogFile(SimpleDateFormat.getDateInstance().format(new Time()).toString(), "update_log.txt",true);

					} catch (Exception e) {
					}
				}
			}
		};

		timer.schedule(timertask, 1000, 10 *60*1000);// 1小时
	}

	private void endTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
