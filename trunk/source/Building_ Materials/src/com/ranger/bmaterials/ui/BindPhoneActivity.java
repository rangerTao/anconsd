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
import com.ranger.bmaterials.netresponse.BindPhoneResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class BindPhoneActivity extends StatActivity implements OnClickListener, IRequestListener,
		OnCancelListener {

	public static int BIND_NEW_PHONE = 1;
	public static int BIND_VERIFY_OLD_PHONE = 2;
	public static int BIND_CHANGE_PHONE = 3;

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

		setContentView(R.layout.bind_phone_activity);
		((TextView) findViewById(R.id.label_title)).setText(getResources().getString(
				R.string.bind_phonenum_title));
		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.btn_bindphone).setOnClickListener(this);
		findViewById(R.id.btn_get_verifycode).setOnClickListener(this);

		verifyCodeButton = (TextView) findViewById(R.id.btn_get_verifycode);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MineProfile.getInstance().Save();
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();

		if (viewID == R.id.img_back) {
			this.finish();
		} else if (viewID == R.id.btn_bindphone) {

			String phonenum = ((EditText) findViewById(R.id.edit_phonenum)).getText().toString();
			String verifyCode = ((EditText) findViewById(R.id.edit_verifycode)).getText()
					.toString();

			if (!StringUtil.checkValidPhoneNum(phonenum)) {
				CustomToast.showToast(this,
						getResources().getString(R.string.invalid_phonenum_tip));
				((EditText) findViewById(R.id.edit_verifycode)).setText("");
				findViewById(R.id.edit_phonenum).requestFocus();
				return;
			}

			if (!StringUtil.checkValidVerifyCode(verifyCode)) {
				CustomToast.showLoginRegistErrorToast(this, DcError.DC_VERIFYCODE_ERROR);
				((EditText) findViewById(R.id.edit_verifycode)).setText("");
				findViewById(R.id.edit_verifycode).requestFocus();
				return;
			}

			requestId = NetUtil.getInstance().requestBindPhone(phonenum, verifyCode,
					BindPhoneActivity.BIND_NEW_PHONE, MineProfile.getInstance().getUserID(),
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

			String phonenum = ((EditText) findViewById(R.id.edit_phonenum)).getText().toString();

			if (!StringUtil.checkValidPhoneNum(phonenum)) {
				CustomToast.showToast(this,
						getResources().getString(R.string.invalid_phonenum_tip));
				return;
			}

			NetUtil.getInstance().requestPhoneVerifyCode(phonenum, 1, this);
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
		CustomToast.showLoginRegistSuccessToast(this, CustomToast.DC_OK_BIND_PHONENUM);

		// 绑定成功后保存手机号和用户类型
		String phonenum = ((EditText) findViewById(R.id.edit_phonenum)).getText().toString();

		if (MineProfile.getInstance().getUserType() == MineProfile.USERTYPE_PHONEUSER) {
			MineProfile.getInstance().setUserName(phonenum);
		} else {
			MineProfile.getInstance().setUserType(MineProfile.USERTYPE_BINGDINGPHONE);
		}
		MineProfile.getInstance().setPhonenum(phonenum);
		
		if (StringUtil.parseInt(responseData.getTag()) == Constants.NET_TAG_BIND_PHONE) {
		    BindPhoneResult result = (BindPhoneResult)responseData;

            //@author liushuohui -- 金币数量增加至本地 
		    MineProfile.getInstance().addCoinnum(result.getCoinnum());
		}
		
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

		case DcError.DC_VERIFYCODE_ERROR:// 验证码错误
		case DcError.DC_VERIFYCODE_EXPIREED:// 验证码过期
			((EditText) findViewById(R.id.edit_verifycode)).setText("");
			break;

		case DcError.DC_NEEDLOGIN:// 需要登录
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, BMLoginActivity.class);
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
