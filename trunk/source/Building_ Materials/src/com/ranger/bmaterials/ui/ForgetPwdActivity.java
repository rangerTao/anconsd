package com.ranger.bmaterials.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.ForgetPasswordResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.PhoneHelper;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class ForgetPwdActivity extends StatActivity implements OnClickListener, IRequestListener, OnCancelListener {

	private Dialog dialogForgetpwd;
	private CustomProgressDialog progressDialog;
	// private ProgressDialog progressDialog;
	private int requestId;
	private String username;
	private String servicenum;
	private int flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_password_activity);
		((TextView) findViewById(R.id.label_title)).setText(getResources().getString(R.string.get_pwd_back_title));

		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);

		username = getIntent().getStringExtra("username");

		((EditText) findViewById(R.id.edit_username)).setText(username);
		CharSequence text = ((EditText) findViewById(R.id.edit_username)).getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();

		if (viewID == R.id.img_back) {
			this.finish();
			return;
		} else if (viewID == R.id.btn_next) {
			String username = ((EditText) findViewById(R.id.edit_username)).getText().toString();

			if (!StringUtil.checkValidUserName(username) && !StringUtil.checkValidPhoneNum(username)) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_INVALID_USERNAME_OR_PHONENUM);
				return;
			}

			requestId = NetUtil.getInstance().requestForgetPwd(username, this);
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.show();
		} else if (v.getId() == R.id.btn_change_nickname_commit) {
			if (flag == 2) {
				if (servicenum.trim().length() != 0) {
					//Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + servicenum));
					Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + servicenum));
					startActivity(phoneIntent);
				}
			} else {
				dialogForgetpwd.dismiss();
				dialogForgetpwd = null;
			}
			finish();
		} else if (viewID == R.id.label_forget_pwd_servicenum) {
			if (servicenum.trim().length() != 0) {
				Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + servicenum));
				startActivity(phoneIntent);
			}
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		progressDialog.dismiss();

		ForgetPasswordResult result = (ForgetPasswordResult) responseData;

		flag = result.getFlag();

		LayoutInflater factory = LayoutInflater.from(this);
		View dialogView = factory.inflate(R.layout.mine_forget_password, null);

		if (flag == 1) {
			((TextView) dialogView.findViewById(R.id.label_forget_pwd_title)).setText(getResources().getString(R.string.get_pwd_succeed));
			dialogView.findViewById(R.id.layout_forget_pwd_unbind_phone_container).setVisibility(View.INVISIBLE);
			((TextView) dialogView.findViewById(R.id.label_forget_pwd_phonenum)).setText(result.getPhonenum());
			((TextView) dialogView.findViewById(R.id.btn_change_nickname_commit)).setText(getResources().getString(R.string.btn_ok_tip));
		} else if (flag == 2) {
			((TextView) dialogView.findViewById(R.id.label_forget_pwd_title)).setText(getResources().getString(R.string.btn_get_pwd_tip));
			dialogView.findViewById(R.id.layout_forget_pwd_bind_phone_container).setVisibility(View.INVISIBLE);
			((TextView) dialogView.findViewById(R.id.label_forget_pwd_servicenum)).setText(result.getServicenum());

			((TextView) dialogView.findViewById(R.id.btn_change_nickname_commit)).setText(getResources().getString(R.string.btn_dial_servicenum_tip));
			servicenum = result.getServicenum();
		}

		dialogView.findViewById(R.id.btn_change_nickname_commit).setOnClickListener(this);
		//dialogView.findViewById(R.id.label_forget_pwd_servicenum).setOnClickListener(this);

		dialogForgetpwd = new Dialog(ForgetPwdActivity.this, R.style.dialog);
		DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
		int width = dm.widthPixels - PhoneHelper.dip2px(this, 13) * 2;
		dialogForgetpwd.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
		dialogForgetpwd.setCancelable(true);
		dialogForgetpwd.show();
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		progressDialog.dismiss();

		CustomToast.showLoginRegistErrorToast(this, errorCode);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		NetUtil.getInstance().cancelRequestById(requestId);
		dialogForgetpwd = null;
	}
}
