package com.ranger.bmaterials.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.PhoneHelper;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class MineEditActivity extends StatActivity implements OnClickListener, IRequestListener, OnCancelListener {

	private View img_back;
	private View btnChangePwd;
	private View btnBindPhone;
	private TextView btnSwitchUser;

	private Dialog dialogChangeNickname;
	private CustomProgressDialog progressDialog;
	private int requestId;
	private String nickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine_activity_edit);
		((TextView) findViewById(R.id.label_title)).setText(getResources().getString(R.string.mine_edit_title));

		img_back = (View) findViewById(R.id.img_back);
		btnChangePwd = (View) findViewById(R.id.btn_changepwd);
		btnBindPhone = (View) findViewById(R.id.btn_bindphone);
		btnSwitchUser = (TextView) findViewById(R.id.btn_switchuser);

		img_back.setOnClickListener(this);
		btnChangePwd.setOnClickListener(this);
		btnBindPhone.setOnClickListener(this);
		btnSwitchUser.setOnClickListener(this);
		findViewById(R.id.btn_edit).setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MineProfile.getInstance().Save();
	}

	@Override
	protected void onStart() {
		super.onStart();

		String nickName = MineProfile.getInstance().getNickName();
		((TextView) findViewById(R.id.label_username)).setText(nickName);

		if (MineProfile.getInstance().getUserType() == MineProfile.USERTYPE_UNBINDINGPHONE) {
			((TextView) findViewById(R.id.label_phonenum)).setText("");
		} else {
			((TextView) findViewById(R.id.label_phonenum)).setText(MineProfile.getInstance().getPhonenum());
		}
	}

	@Override
	public void onClick(View v) {
		if (v == img_back) {
			this.finish();
		} else if (v == btnChangePwd) {

			Intent intent = new Intent(this, ChangePwdActivity.class);
			startActivity(intent);
		} else if (v == btnBindPhone) {

			Intent intent;
			if (MineProfile.getInstance().getPhonenum().length() <= 0) {
				intent = new Intent(this, BindPhoneActivity.class);
			} else {
				intent = new Intent(this, BindPhoneVerifyActivity.class);
			}
			startActivity(intent);
		} else if (v == btnSwitchUser) {
			
			ClickNumStatistics.addMineSwithUserClickNumStatistics(this);
			MineProfile.getInstance().setIsLogin(false);
			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();
			NetUtil.getInstance().requestUserUnlogin(userid, sessionid, null);
			
			Intent intent = new Intent(this, SapiLoginActivity.class);
			startActivity(intent);
			this.finish();
		} else if (v.getId() == R.id.btn_edit) {
			// 修改昵称

			LayoutInflater factory = LayoutInflater.from(this);
			View dialogView = factory.inflate(R.layout.mine_change_nickname, null);

			((EditText) dialogView.findViewById(R.id.edit_change_nickname)).setText(MineProfile.getInstance().getNickName());
			CharSequence text = ((EditText) dialogView.findViewById(R.id.edit_change_nickname)).getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}
			dialogView.findViewById(R.id.btn_change_nickname_cancel).setOnClickListener(this);
			dialogView.findViewById(R.id.btn_change_nickname_commit).setOnClickListener(this);
			dialogChangeNickname = new Dialog(MineEditActivity.this, R.style.dialog);

			DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
			int width = dm.widthPixels - PhoneHelper.dip2px(this, 13) * 2;
			dialogChangeNickname.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
			dialogChangeNickname.setCancelable(true);
			dialogChangeNickname.show();
		} else if (v.getId() == R.id.btn_change_nickname_cancel) {
			dialogChangeNickname.dismiss();
			dialogChangeNickname = null;
		} else if (v.getId() == R.id.btn_change_nickname_commit) {
			EditText editText = (EditText) dialogChangeNickname.findViewById(R.id.edit_change_nickname);
			nickname = editText.getText().toString();

			if (!StringUtil.checkValidNickName(nickname)) {
				CustomToast.showToast(this, getResources().getString(R.string.valid_nickname_tip));
				return;
			}

			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();

			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(dialogChangeNickname.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}

			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.show();

			requestId = NetUtil.getInstance().requestChangeNickname(userid, sessionid, nickname, this);
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (dialogChangeNickname != null) {
			dialogChangeNickname.dismiss();
			dialogChangeNickname = null;
		}
		BaseResult result = (BaseResult) responseData;

		if (StringUtil.parseInt(result.getTag()) == Constants.NET_TAG_CHANGE_NICKNAME) {
			MineProfile.getInstance().setNickName(nickname);
			String nickName = MineProfile.getInstance().getNickName();
			((TextView) findViewById(R.id.label_username)).setText(nickName);
			CustomToast.showLoginRegistSuccessToast(this, CustomToast.DC_OK_CHANGED_NICKNAME);
		} else {
		}
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		
		if (errorCode == DcError.DC_ONCE_IN_SEVEN_DAYS) {
			if (dialogChangeNickname != null) {
				dialogChangeNickname.dismiss();
				dialogChangeNickname = null;
			}
		}
		
		switch (errorCode) {
		case DcError.DC_EXIST_NICKNAME:
			if (MineProfile.getInstance().getNickName().equals(nickname)) {
				errorCode = CustomToast.DC_ERR_NICKNAME_NOT_CHANGED;
				
				if (dialogChangeNickname != null) {
					dialogChangeNickname.dismiss();
					dialogChangeNickname = null;
				}
			}
			break;

		case DcError.DC_NEEDLOGIN:
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, SapiLoginActivity.class);
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
		if (progressDialog != null) {
			NetUtil.getInstance().cancelRequestById(requestId);
			progressDialog = null;
		} else {
			dialogChangeNickname = null;
		}
	}
}
