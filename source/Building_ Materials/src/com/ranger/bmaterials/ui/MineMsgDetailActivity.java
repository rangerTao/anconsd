package com.ranger.bmaterials.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.MineMsgDetailResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class MineMsgDetailActivity extends StatActivity implements OnClickListener, IRequestListener, OnCancelListener {

	private String msgID;
	private View layout_loading_guide;
	// private ProgressDialog progressDialog;
	private int requestId = 0;
	private int position = -1;
	private boolean ret = false;
	private boolean msgRequestSend = false;
	private ViewGroup errorContainer;
	private String msgTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine_activity_msg_detail);

		Intent intent = this.getIntent();
		msgID = intent.getStringExtra(Constants.JSON_MSGID);
		String msgTitle = intent.getStringExtra(Constants.JSON_MSGTITLE);
		msgTime = intent.getStringExtra(Constants.JSON_MSGTIME);
		position = intent.getIntExtra("position", -1);

		((TextView) findViewById(R.id.label_title)).setText(msgTitle);
		layout_loading_guide = findViewById(R.id.layout_loading_guide);

		findViewById(R.id.img_back).setOnClickListener(this);
		errorContainer = (ViewGroup) findViewById(R.id.error_hint);
		errorContainer.setOnClickListener(this);
		((WebView) findViewById(R.id.webview_msg_detail_content)).setBackgroundColor(Color.parseColor("#EAEAEA"));
		
		((WebView) findViewById(R.id.webview_msg_detail_content)).setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.indexOf("tel:")<0){
					Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(it);
				} 
				
				return true;
			}
			
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (msgRequestSend) {
			return;
		}
		getMsgDetail();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent result = new Intent();
			result.putExtra("position", position);
			result.putExtra("result", ret);
			setResult(RESULT_OK, result);

			this.finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();
		if (viewID == R.id.error_hint) {
			errorContainer.setVisibility(View.GONE);
			getMsgDetail();		
		} else {
			Intent result = new Intent();
			result.putExtra("position", position);
			result.putExtra("result", ret);
			setResult(RESULT_OK, result);
			
			this.finish();			
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		ret = false;
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		ret = true;
		layout_loading_guide.setVisibility(View.GONE);

		MineMsgDetailResult result = (MineMsgDetailResult) responseData;
		((WebView) findViewById(R.id.webview_msg_detail_content)).getSettings().setDefaultTextEncodingName("UTF-8");
		((WebView) findViewById(R.id.webview_msg_detail_content)).loadData(result.msgText, "text/html; charset=UTF-8", null);
		((TextView) findViewById(R.id.label_msg_detail_time)).setText(msgTime);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		ret = false;
		layout_loading_guide.setVisibility(View.GONE);

		switch (errorCode) {
		case DcError.DC_NEEDLOGIN:// 需要登录
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, SapiLoginActivity.class);
			startActivity(intent);
			CustomToast.showToast(this, getResources().getString(R.string.need_login_tip));
			break;
		default:
			findViewById(R.id.error_hint).setVisibility(View.VISIBLE);
			break;
		}
		CustomToast.showLoginRegistErrorToast(this, errorCode);
	}
	
	private void getMsgDetail() {
		msgRequestSend = true;
		String userid = MineProfile.getInstance().getUserID();
		String sessionid = MineProfile.getInstance().getSessionID();
		layout_loading_guide.setVisibility(View.VISIBLE);

		requestId = NetUtil.getInstance().requestMessageDetail(userid, sessionid, msgID, this);
	}
}
