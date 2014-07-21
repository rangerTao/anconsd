package com.ranger.bmaterials.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.tools.AppUtil;

public class NotificaionReceiver extends BroadcastReceiver {
	static String TAG = "NotificaionReceiver" ;
	public static final String ACTION_CANCLE_NOTIFICATION = "duoku.gamesearch.intent.action.CANCLE_NOTIFICATION" ;
	public static final String ARG_NOTIFICATION_ID = "notification_id" ;
	public static final String ARG_NOTIFICATION_UPDATE = "notification_update" ;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(ACTION_CANCLE_NOTIFICATION.equals(intent.getAction())){
			 NotificationManager nm = (NotificationManager)
		                context.getSystemService(Context.NOTIFICATION_SERVICE);
			 int id = intent.getIntExtra(ARG_NOTIFICATION_ID, -1);
			 if (Constants.DEBUG) Log.i(TAG, ARG_NOTIFICATION_ID+" "+id);
		     //nm.cancelAll();
		     nm.cancel(id);
		}
		
	}
	
	
	
	
	
	

}
