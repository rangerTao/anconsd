package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class ChangePwdActivity extends Activity implements OnClickListener, IRequestListener, OnCancelListener {

	private CustomProgressDialog progressDialog;
	//private ProgressDialog progressDialog;
	private int requestId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.change_password_activity);

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_changepwd).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();

		if (viewID == R.id.btn_back) {
			this.finish();
		} else if (viewID == R.id.btn_changepwd) {

			String oldpwd = ((EditText) findViewById(R.id.edit_change_oldpwd)).getText().toString();
			String newpwd = ((EditText) findViewById(R.id.edit_change_newpwd)).getText().toString();
            String confirmPwd = ((EditText) findViewById(R.id.edit_confirm_newpwd)).getText().toString();

			if (oldpwd.length() <= 0) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_OLD_PWD_EMPTY);
				return;
			}
			
			if (newpwd.length() <= 0) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_NEW_PWD_EMPTY);
				return;
			}
			
			if (oldpwd.equals(newpwd)) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_NEW_PWD_CANNOT_BE_OLD_PWD);
				return;
			}
			
			if (!StringUtil.checkValidPassword(oldpwd)) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_INVALID_OLDPWD);
				((EditText) findViewById(R.id.edit_change_oldpwd)).setText("");
				findViewById(R.id.edit_change_oldpwd).requestFocus();
				return;
			}
			
			if (!StringUtil.checkValidPassword(newpwd)) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_INVALID_PWD);
				((EditText) findViewById(R.id.edit_change_newpwd)).setText("");
				findViewById(R.id.edit_change_newpwd).requestFocus();
				return;
			}

            if(!newpwd.equals(confirmPwd)){
                CustomToast.showToast(getApplicationContext(), "两次输入密码不相同");
                return;
            }

			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();

			MineProfile.getInstance().Print();

			requestId = NetUtil.getInstance().requestChangePwd(oldpwd, newpwd, this);

			try {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.show();
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

		CustomToast.showLoginRegistSuccessToast(this, CustomToast.DC_OK_CHNAGE_PWD);
		this.finish();
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

        if(progressDialog != null && progressDialog.isShowing())
		    progressDialog.dismiss();

		switch (errorCode) {
		case DcError.DC_BADPWD:
			((EditText) findViewById(R.id.edit_change_oldpwd)).requestFocus();
			break;
		case DcError.DC_NEEDLOGIN:
			MineProfile.getInstance().setIsLogin(false);
			MineProfile.getInstance().setSessionID("");
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
}
