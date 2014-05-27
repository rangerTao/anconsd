package com.ranger.phonerecorder.app;

import com.ranger.phonerecorder.pojos.EmailServer;
import com.ranger.phonerecorder.utils.SharePerfenceUtil;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class PhoneRecorderApplication extends Application{

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		SharedPreferences sp = getSharedPreferences("record", MODE_PRIVATE);
		Constants.REMINDER_PATH = sp.getString("filepath", "");
		
		Log.d("TAG", Constants.REMINDER_PATH);
		
		EmailServer es = SharePerfenceUtil.getEmailServerInfo(this);
		if(es != null)
			Constants.emailServerInfo = es;
		
		if(Constants.emailServerInfo != null)
			Log.d("TAG", Constants.emailServerInfo.username);
	}

	
}
