package com.andconsd.ui;

import java.io.IOException;

import com.andconsd.http.RequestListenerThread;
import com.andconsd.utils.Constants;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


public class WebService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "service stoped", Toast.LENGTH_LONG).show();
	}

}
