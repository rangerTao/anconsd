package com.ranger.phonerecorder.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ranger.phonerecorder.sms.SMS;
import com.ranger.phonerecorder.sms.SMSObserver;
import com.ranger.phonerecorder.sms.SmsHandler;

public class SystemMessageObserverService extends Service {

	public static final String TAG = "BootService";

	private ContentObserver mObserver;

	@Override
	public void onCreate()

	{

		Log.i(TAG, "onCreate().");

		super.onCreate();

		addSMSObserver();

	}

	public void addSMSObserver()

	{

		Log.i(TAG, "add a SMS observer. ");

		ContentResolver resolver = getContentResolver();

		Handler handler = new SmsHandler(this);

		mObserver = new SMSObserver(resolver, handler);

		resolver.registerContentObserver(SMS.CONTENT_URI, true, mObserver);

	}

	@Override
	public IBinder onBind(Intent intent)

	{

		return null;

	}
	
	@Override
	public void onDestroy(){
		
	}

}
