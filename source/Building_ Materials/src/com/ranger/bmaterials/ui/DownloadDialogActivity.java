package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.MineProfile;

public class DownloadDialogActivity extends StatActivity implements OnClickListener {
	private TextView bodyTextView;
	private TextView titleTextView;
	private TextView confirmTextView;
	private TextView cancleTextView;

	public static final String ARG_ALERT_TYPE = "alert_type"; 
	public static final int ALERT_FOR_NETWORK  = 1 ;
	public static final int ALERT_FOR_SDCARD  = 2 ;
	public static final int ALERT_FOR_REMOVE_OLD  = 3 ;
	
	public static final String ARG1 = "arg1";
	public static final String ARG2 = "arg2";
	public static final String ARG_EXTRA = "arg_extra";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!initIntent()){
			finish();
			return ;
		}
		setContentView(R.layout.custom_delete_confirm_dialog_layout);
		initView();
		initData();
	}
	private int type = -1 ;
	
	private boolean initIntent(){
		Intent intent = getIntent();
		int intExtra = intent.getIntExtra(ARG_ALERT_TYPE, -1);
		type = intExtra ;
		return intExtra != -1 ;
	}
	
	private void initView(){
		bodyTextView = (TextView) findViewById(R.id.progress_message_body);
		titleTextView = (TextView) findViewById(R.id.progress_message_title);
		confirmTextView = (TextView) findViewById(R.id.dialog_button_left);
		cancleTextView = (TextView) findViewById(R.id.dialog_button_right);
		
		confirmTextView.setOnClickListener(this);
		cancleTextView.setOnClickListener(this);
		
	}
	
	private void initData(){
		if(type == ALERT_FOR_NETWORK){
			bodyTextView.setText(R.string.no_network_dialog_hint);
			confirmTextView.setText(R.string.no_network_dialog_confirm);
			titleTextView.setText(R.string.no_network_dialog_title);
			cancleTextView.setText(R.string.no_network_dialog_cancel);
		}else if(type == ALERT_FOR_REMOVE_OLD){
			bodyTextView.setText("您已经安装了旧版本游戏，但是签名不一致,需要先卸载旧版本  ");
			confirmTextView.setText("确定");
			cancleTextView.setText("取消");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_button_left:
				if(type == ALERT_FOR_NETWORK){
					changeNetwork();
				}else if(type == ALERT_FOR_REMOVE_OLD){
					Intent intent = getIntent();
					String packageName = intent.getStringExtra(ARG1);
					String name = intent.getStringExtra(ARG2);
					uninstallOld(this, packageName);
					finish();
				}
				break;
			case R.id.dialog_button_right:
				finish();
				break;
			}
		
	}
	
	private static void uninstallOld(Activity context,String packageName){
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivity(uninstallIntent);
	}

	private void changeNetwork() {
		new Thread(){
			public void run() {
				MineProfile profile = MineProfile.getInstance();
				profile.setDownloadOnlyWithWiFi(false);
				profile.Save(getApplicationContext());
			};
		}.start();
		Intent intent = getIntent();
		setResult(Activity.RESULT_OK,new Intent(intent));
		finish();
	}
}
