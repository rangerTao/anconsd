package com.ranger.phonerecorder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.gson.Gson;
import com.ranger.phonerecorder.R;
import com.ranger.phonerecorder.app.Constants;
import com.ranger.phonerecorder.pojos.EmailServer;
import com.ranger.phonerecorder.service.PhoneStatusService;
import com.ranger.phonerecorder.service.SystemMessageObserverService;
import com.ranger.phonerecorder.task.UploadFileTask;
import com.ranger.phonerecorder.utils.DeviceId;
import com.ranger.phonerecorder.utils.NetUtil;
import com.ranger.phonerecorder.utils.SharePerfenceUtil;

public class MainActivity extends BaseActivity {

	UploadFileTask uFileTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("电话答录");

		Intent intent = new Intent(this, PhoneStatusService.class);
		startService(intent);

		new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				String result = NetUtil.connectHttpGet("http://www.appvideo.cn/appapi/getid.php?id=" + DeviceId.getAndroidId(getApplicationContext()));
				
				if(!result.equals("")){
					
					Log.d("TAG", result);
					try {
						Gson gson = new Gson();
						EmailServer es = gson.fromJson(result, EmailServer.class);
						if(es.isValid()){
							Constants.emailServerInfo = es;
							SharePerfenceUtil.saveEmaiServerInfo(getApplicationContext(), result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				super.run();
			}

		}.start();
		
		startService(new Intent(this,SystemMessageObserverService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void start_record(View view) {

		switch (view.getId()) {
		case R.id.btn_set_reminder:
			Intent intent = new Intent(this, ReminderRecordActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_view_records:
			Intent intent_view_list = new Intent(this, IncallRecordListActivity.class);
			startActivity(intent_view_list);
			break;
		default:
			break;
		}

	}
	
	class SendUnloadedFile extends AsyncTask{

		@Override
		protected Object doInBackground(Object... params) {
			return null;
		}
		
	}
}
