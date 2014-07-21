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
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.UserNameRegisterResult;
import com.ranger.bmaterials.statistics.UserStatistics;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class RegisterPhoneNumSubFragment extends Fragment implements OnClickListener, OnCheckedChangeListener, IRequestListener, OnCancelListener,
		TextWatcher {
	private static final int INPUT_ERROR_USERNAME = 0;
	private static final int INPUT_ERROR_NICKNAME = 1;
	private static final int INPUT_ERROR_PASSWORD = 2;
	private static final int INPUT_ERROR_PHONENUM = 3;
	private static final int INPUT_ERROR_VERIFYCODE = 4;
	private static final int INPUT_ERROR_USERNAME_CANNOT_BE_PHONENUM = 5;

	private CustomProgressDialog progressDialog;
	// private ProgressDialog progressDialog;
	private int requestId;

	private TextView verifyCodeButton;
	private Handler handler;;
	private TimerTask timertask;;
	private Timer timer;
	private int counter = 0;

	private boolean flag;// true: 返回到我的编辑界面

	private CheckBox pCheckBox;
	private EditText pEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		flag = this.getArguments().getBoolean("flag");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.register_activity_subpage_phonenum, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView textView = (TextView) getActivity().findViewById(R.id.label_p_agree_protocol);
		//textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		SpannableString content = new SpannableString(getActivity().getResources().getString(R.string.register_protocol));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		textView.setText(content);
		
		textView.setOnClickListener(this);

		getActivity().findViewById(R.id.btn_p_commit_register).setOnClickListener(this);

		getActivity().findViewById(R.id.btn_p_send_verifycode).setOnClickListener(this);
		((CheckBox) getActivity().findViewById(R.id.checkbox_p_agree_protocol)).setOnCheckedChangeListener(this);

		verifyCodeButton = (TextView) getActivity().findViewById(R.id.btn_p_send_verifycode);

		((EditText) getActivity().findViewById(R.id.edit_p_username)).addTextChangedListener(this);

		pCheckBox = (CheckBox) getActivity().findViewById(R.id.checkbox_p_agree_protocol);
		pEditText = (EditText) getActivity().findViewById(R.id.edit_p_username);

		((CheckBox) getActivity().findViewById(R.id.checkbox_p_agree_protocol)).setChecked(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		endTimer();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0 && pCheckBox.isChecked()) {
			getActivity().findViewById(R.id.btn_p_commit_register).setEnabled(true);
			((TextView) getActivity().findViewById(R.id.btn_p_commit_register)).setTextColor(Color.WHITE);
			getActivity().findViewById(R.id.btn_p_commit_register).setBackgroundResource(R.drawable.mine_btn_login_register_ect_bg_selector);
		} else {
			((TextView) getActivity().findViewById(R.id.btn_p_commit_register)).setTextColor(Color.GRAY);
			getActivity().findViewById(R.id.btn_p_commit_register).setEnabled(false);
			getActivity().findViewById(R.id.btn_p_commit_register).setBackgroundResource(R.drawable.btn_register_bg);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		NetUtil.getInstance().cancelRequestById(requestId);
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		if (StringUtil.parseInt(responseData.getTag()) != Constants.NET_TAG_GET_PHONE_VERIFYCODE) {
			if (progressDialog != null) {
			    progressDialog.dismiss();
			    progressDialog = null;
			}

			MineProfile.getInstance().Reset(getActivity());

			UserNameRegisterResult result = (UserNameRegisterResult) responseData;
			MineProfile.getInstance().setUserID(result.getUserid());
			MineProfile.getInstance().setSessionID(result.getSessionid());
			MineProfile.getInstance().setUserName(result.getUsername());
			MineProfile.getInstance().setNickName(result.getNickname());
			MineProfile.getInstance().setUserType(result.getRegistertype());

			if (MineProfile.getInstance().getUserType() == MineProfile.USERTYPE_PHONEUSER) {
				MineProfile.getInstance().setPhonenum(MineProfile.getInstance().getUserName());
			} else {
				MineProfile.getInstance().setPhonenum("");
			}

			MineProfile.getInstance().setIsLogin(true);

			MineProfile.getInstance().setGamenum("0");
			MineProfile.getInstance().setTotalmsgnum("0");
			MineProfile.getInstance().setMessagenum("0");
			MineProfile.getInstance().setCollectnum("0");

			MineProfile.getInstance().addAccount(result.getUsername());
			
			MineProfile.getInstance().Save();
			registerFinished();
		} else {
			startTimer();
		}
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		if (requestTag != Constants.NET_TAG_GET_PHONE_VERIFYCODE) {
			if (progressDialog != null) {
			    progressDialog.dismiss();
			    progressDialog = null;
			}
		} else {
			endTimer();
		}

		switch (errorCode) {
		case DcError.DC_EXIST_USER:
			if (requestTag == Constants.NET_TAG_USERNAME_REGISTER) {
				CustomToast.showLoginRegistErrorToast(getActivity(), errorCode);
			} else {
				CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_EXIST_PHONENUM);
			}
			break;
		case DcError.DC_EXIST_NICKNAME:
			CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_NICKNAME_IS_INUSE);
			break;
		case DcError.DC_NICKNAME_INVALID:
			CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_NICKNAME_BAD_FORMAT);
			break;
		default:
			CustomToast.showLoginRegistErrorToast(getActivity(), errorCode);
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isChecked()) {
			if (pEditText.getText().toString().length() > 0) {

				getActivity().findViewById(R.id.btn_p_commit_register).setEnabled(true);
				((TextView) getActivity().findViewById(R.id.btn_p_commit_register)).setTextColor(Color.WHITE);
				getActivity().findViewById(R.id.btn_p_commit_register).setBackgroundResource(R.drawable.mine_btn_login_register_ect_bg_selector);
			} else {
				((TextView) getActivity().findViewById(R.id.btn_p_commit_register)).setTextColor(Color.GRAY);
				getActivity().findViewById(R.id.btn_p_commit_register).setEnabled(false);
				getActivity().findViewById(R.id.btn_p_commit_register).setBackgroundResource(R.drawable.btn_register_bg);
			}
		} else {
			((TextView) getActivity().findViewById(R.id.btn_p_commit_register)).setTextColor(Color.GRAY);
			getActivity().findViewById(R.id.btn_p_commit_register).setEnabled(false);
			getActivity().findViewById(R.id.btn_p_commit_register).setBackgroundResource(R.drawable.btn_register_bg);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.label_u_agree_protocol || id == R.id.label_p_agree_protocol) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), RegisterProtocolActivity.class);
			startActivity(intent);
		} else if (id == R.id.btn_p_send_verifycode) {

			String phonenum = ((EditText) getActivity().findViewById(R.id.edit_p_username)).getText().toString();

			if (!StringUtil.checkValidPhoneNum(phonenum)) {
				handleInputError(INPUT_ERROR_PHONENUM);
				getActivity().findViewById(R.id.edit_p_username).requestFocus();
				return;
			}

			NetUtil.getInstance().requestPhoneVerifyCode(phonenum, 1, this);
			verifyCodeButton.setEnabled(false);
		} else if (id == R.id.btn_p_commit_register) {

			String phonenum = ((EditText) getActivity().findViewById(R.id.edit_p_username)).getText().toString();
			String password = ((EditText) getActivity().findViewById(R.id.edit_p_pwd)).getText().toString();
			String nickname = ((EditText) getActivity().findViewById(R.id.edit_p_nickname)).getText().toString();
			String verifyCode = ((EditText) getActivity().findViewById(R.id.edit_p_verifycode)).getText().toString();

			if (!StringUtil.checkValidPhoneNum(phonenum)) {
				handleInputError(INPUT_ERROR_PHONENUM);
				getActivity().findViewById(R.id.edit_p_username).requestFocus();
				return;
			}

			if (!StringUtil.checkValidNickName(nickname)) {
				handleInputError(INPUT_ERROR_NICKNAME);
				getActivity().findViewById(R.id.edit_p_nickname).requestFocus();
				return;
			}

			if (!StringUtil.checkValidPassword(password)) {
				handleInputError(INPUT_ERROR_PASSWORD);
				getActivity().findViewById(R.id.edit_p_pwd).requestFocus();
				return;
			}

			if (!StringUtil.checkValidVerifyCode(verifyCode)) {
				handleInputError(INPUT_ERROR_VERIFYCODE);
				getActivity().findViewById(R.id.edit_p_verifycode).requestFocus();
				return;
			}

			requestId = NetUtil.getInstance().requestPhoneumRegister(phonenum, password, nickname, verifyCode, this);

			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			progressDialog = CustomProgressDialog.createDialog(getActivity());
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.show();
		}
	}

	private void startTimer() {
		timer = new Timer(false);
		this.handler = new Handler() {
			public void handleMessage(Message msg) {
				counter--;
				if (counter < 0) {
					endTimer();
				} else {

					verifyCodeButton.setText("再次获取(" + counter + ")");
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
		verifyCodeButton.setText("再次获取(" + counter + ")");
		verifyCodeButton.setTextColor(Color.GRAY);
		verifyCodeButton.setBackgroundResource(R.drawable.btn_register_bg);
	}

	private void endTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		verifyCodeButton.setEnabled(true);
		verifyCodeButton.setText("获取验证码");
		verifyCodeButton.setTextColor(Color.WHITE);
		verifyCodeButton.setBackgroundResource(R.drawable.mine_btn_get_verifycode_bg_selector);
	}

	private void handleInputError(int error) {
		switch (error) {
		case INPUT_ERROR_USERNAME: {
			CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_INVALID_USERNAME);
		}

			break;
		case INPUT_ERROR_NICKNAME: {
			CustomToast.showToast(getActivity(), getResources().getString(R.string.valid_nickname_tip));
		}

			break;
		case INPUT_ERROR_PASSWORD: {
			CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_INVALID_PWD);
		}

			break;
		case INPUT_ERROR_PHONENUM: {
			CustomToast.showToast(getActivity(), getResources().getString(R.string.invalid_phonenum_tip));
		}

			break;
		case INPUT_ERROR_VERIFYCODE: {
			CustomToast.showLoginRegistErrorToast(getActivity(), DcError.DC_VERIFYCODE_ERROR);
		}

			break;
		case INPUT_ERROR_USERNAME_CANNOT_BE_PHONENUM: {
			CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_USERNAME_CANNOT_BE_PHONENUM);
		}

			break;
		default:
			break;
		}
	}

	private void registerFinished() {
		UserStatistics.addRegisterNumStatistics(getActivity());
		MineProfile.getInstance().Print();// for debug
		if (flag) {
			// Intent intent = new Intent(this, MineActivity.class);
			// startActivity(intent);
		}
		getActivity().finish();
	}
}
