package com.ranger.bmaterials.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class BindPhoneVerifyActivity extends StatActivity implements OnClickListener, IRequestListener,
		OnCancelListener {

	private CustomProgressDialog progressDialog;
	// private ProgressDialog progressDialog;
	private int requestId;

	private TextView verifyCodeButton;
	private Handler handler;;
	TimerTask timertask;;
	private Timer timer;
	private int counter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.bind_phone_verify_activity);
		((TextView) findViewById(R.id.label_title)).setText(getResources().getString(
				R.string.change_bind_phone_title));
		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.btn_bindphone_next).setOnClickListener(this);
		findViewById(R.id.btn_get_verifycode).setOnClickListener(this);

		verifyCodeButton = (TextView) findViewById(R.id.btn_get_verifycode);
		((TextView) findViewById(R.id.label_phonenum)).setText(MineProfile.getInstance()
				.getPhonenum());
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();

		if (viewID == R.id.img_back) {
			this.finish();
		} else if (viewID == R.id.btn_bindphone_next) {

			String verifyCode = ((EditText) findViewById(R.id.edit_verifycode)).getText()
					.toString();

			if (!StringUtil.checkValidVerifyCode(verifyCode)) {
				CustomToast.showLoginRegistErrorToast(this, DcError.DC_VERIFYCODE_ERROR);
				return;
			}

			requestId = NetUtil.getInstance().requestBindPhone(
					MineProfile.getInstance().getPhonenum(), verifyCode,
					BindPhoneActivity.BIND_VERIFY_OLD_PHONE, MineProfile.getInstance().getUserID(),
					MineProfile.getInstance().getSessionID(), this);
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.show();
		} else if (viewID == R.id.btn_get_verifycode) {

			String phonenum = MineProfile.getInstance().getPhonenum();
			NetUtil.getInstance().requestPhoneVerifyCode(phonenum, 0, this);
			// startTimer();

			verifyCodeButton.setEnabled(false);
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		if (StringUtil.parseInt(responseData.getTag()) == Constants.NET_TAG_GET_PHONE_VERIFYCODE) {
			startTimer();
			return;
		}

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		Intent intent = new Intent(this, BindPhoneActivity.class);
		startActivity(intent);
		this.finish();
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		if (requestTag == Constants.NET_TAG_GET_PHONE_VERIFYCODE) {
			endTimer();
		}

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		switch (errorCode) {
		case DcError.DC_NEEDLOGIN:// 需要登录
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			CustomToast.showToast(this, getResources().getString(R.string.need_login_tip));

			break;
		default:
			break;
		}

		CustomToast.showLoginRegistErrorToast(this, errorCode);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		NetUtil.getInstance().cancelRequestById(requestId);
	}

	private void startTimer() {
		timer = new Timer(false);
		this.handler = new Handler() {
			public void handleMessage(Message msg) {
				counter--;
				if (counter < 0) {
					endTimer();
				} else {

					verifyCodeButton.setText(getResources().getString(R.string.get_verifycode_left)
							+ counter + getResources().getString(R.string.get_verifycode_right));
				}
				super.handleMessage(msg);
			}
		};

		timertask = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};

		counter = 60;
		timer.schedule(timertask, 1000, 1000);

		verifyCodeButton.setEnabled(false);
		verifyCodeButton.setText(getResources().getString(R.string.get_verifycode_left) + counter
				+ getResources().getString(R.string.get_verifycode_right));
		verifyCodeButton.setTextColor(Color.GRAY);
		verifyCodeButton.setBackgroundResource(R.drawable.btn_register_bg);
	}

	private void endTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		verifyCodeButton.setEnabled(true);
		verifyCodeButton.setText(getResources().getString(R.string.get_verifycode));
		verifyCodeButton.setTextColor(Color.WHITE);
		verifyCodeButton.setBackgroundResource(R.drawable.mine_btn_get_verifycode_bg_selector);
	}
}
