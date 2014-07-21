package com.ranger.bmaterials.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class FeedbackActivity extends StatActivity implements OnClickListener,
		TextWatcher, IRequestListener, OnCancelListener {

	private EditText content;// 文本输入框
	private EditText contact;// 文本输入框
	private TextView hasnum;// 用来显示剩余字数
	private int num = 140;// 限制的最大字数
	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;

	private CustomProgressDialog progressDialog = null;
	// private ProgressDialog progressDialog;
	private int requestId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback_activity);
		((TextView) findViewById(R.id.label_title)).setText(getResources()
				.getString(R.string.feedback_title));

		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.btn_feedback_commit).setOnClickListener(this);

		content = (EditText) findViewById(R.id.edit_feedback_content);
		contact = (EditText) findViewById(R.id.edit_feedback_phoneormail);
		hasnum = (TextView) findViewById(R.id.label_feedback_num);
		hasnum.setText("" + num);

		content.addTextChangedListener(this);

		findViewById(R.id.btn_feedback_commit).setBackgroundResource(
				R.drawable.btn_register_bg);
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();

		if (viewID == R.id.img_back) {
			this.finish();
		} else if (viewID == R.id.btn_feedback_commit) {

			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();
			String content = this.content.getText().toString();
			String contact = this.contact.getText().toString();

			if (!StringUtil.checkValidFeedbackContent(content)) {
				CustomToast.showLoginRegistErrorToast(this,
						CustomToast.DC_ERR_INVALID_FEEDBACK_CONTENT);
				return;
			}

			if (contact.length() > 0 && !StringUtil.checkValidPhoneNum(contact)
					&& !StringUtil.checkValidMailaddress(contact)) {
				CustomToast.showLoginRegistErrorToast(this,
						CustomToast.DC_ERR_INVALID_CONTACT);
				return;
			}

			requestId = NetUtil.getInstance().requestFeedback(userid,
					sessionid, content, contact, this);

			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}

			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(
					R.string.committing_tip));
			progressDialog.show();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;

		if (temp.length() > 0) {
			((TextView) findViewById(R.id.btn_feedback_commit))
					.setEnabled(true);
			((TextView) findViewById(R.id.btn_feedback_commit))
					.setTextColor(Color.WHITE);
			findViewById(R.id.btn_feedback_commit).setBackgroundResource(
					R.drawable.mine_btn_login_register_ect_bg_selector);
		} else {
			((TextView) findViewById(R.id.btn_feedback_commit))
					.setEnabled(false);
			((TextView) findViewById(R.id.btn_feedback_commit))
					.setTextColor(StringUtil.getColor("787878"));
			findViewById(R.id.btn_feedback_commit).setBackgroundResource(
					R.drawable.btn_register_bg);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

		try {
			int number = num - s.length();
			if (number < 0) {
				number = 0;
			}

			hasnum.setText("" + number);
			selectionStart = content.getSelectionStart();
			selectionEnd = content.getSelectionEnd();
			if (temp.length() > num) {
				s.delete(selectionStart - (temp.length() - num), selectionEnd);
				int tempSelection = selectionEnd;
				content.setText(s);
				content.setSelection(tempSelection);
			}

			if (number > 0) {
				hasnum.setTextColor(StringUtil.getColor("81B537"));
			} else {
				hasnum.setTextColor(StringUtil.getColor("FFAC1B"));
			}

		} catch (Exception e) {
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		NetUtil.getInstance().cancelRequestById(requestId);
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		if (progressDialog != null)
			progressDialog.dismiss();
		progressDialog = null;
		CustomToast.showLoginRegistSuccessToast(this,
				CustomToast.DC_OK_FEEDBACK);
		this.finish();
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode,
			String msg) {
		if (progressDialog != null)
			progressDialog.dismiss();
		progressDialog = null;

		switch (errorCode) {
		case DcError.DC_NEEDLOGIN:
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, SapiLoginActivity.class);
			startActivity(intent);
			CustomToast.showToast(this,
					getResources().getString(R.string.need_login_tip));
			break;
		default:
			break;
		}
		CustomToast.showLoginRegistErrorToast(this, errorCode);
	}
}
