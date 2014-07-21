package com.ranger.bmaterials.speeddownload;

import com.ranger.bmaterials.app.Constants;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SpeedDonwloadService extends Service {

	private LocalServer server = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		server = new LocalServer(this, "SpeedDownloadServer");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (Constants.speedServiceStarted) {
			return START_STICKY;
		}
		Constants.speedServiceStarted = true;
		server.setDaemon(true);
		server.start();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		server.stopServer();
		super.onDestroy();
		Constants.speedServiceStarted = false;
		Intent intent = new Intent();
		//重启service，保持一直运行
		intent.setClass(this, SpeedDonwloadService.class);
		this.startService(intent);
	}
}