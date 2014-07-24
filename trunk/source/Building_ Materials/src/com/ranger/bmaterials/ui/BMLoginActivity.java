package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BMUserLoginResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.Logger;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.view.MyPopupWindows;

public class BMLoginActivity extends Activity implements OnClickListener,
		NetUtil.IRequestListener, OnCancelListener, TextWatcher, OnDismissListener,
		OnItemClickListener {

	private static final String TAG = "LoginActivity";

	private CustomProgressDialog progressDialog = null;
	private boolean hasProgressDlg = false;
	// private ProgressDialog progressDialog;
	private int requestId;

	private boolean flag = false;
	private static boolean opening = false;
	private MyPopupWindows accountMenu;
	private String message;
	private EditText edit_login_username;
    private EditText edit_password;
	
	/**是否应该忽略一键注册结果，如果请求已成功或者请求被用户取消、网络超时等，则不应忽略。
	 * 如果请求未被取消，则暂时忽略以前的请求结果，初始状态为false*/
	boolean ignoreRegResult;
	/**一键注册请求是否被cancel，用户在按下cancel键时表示请求被cancel，另外被cancel的情况
	 * 还有sms发送失败、网络超时、设定的重试时间结束*/
	private boolean requestHasCancel;

	ArrayList<Integer> fastRegReqList = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (opening) {
			this.finish();
		}

		opening = true;
		setContentView(R.layout.bm_layout_login_view);

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_commit_login).setOnClickListener(this);
		findViewById(R.id.btn_register).setOnClickListener(this);

		edit_login_username = (EditText) findViewById(R.id.edit_login_username);
		edit_login_username.addTextChangedListener(this);

		resetUsernameEditText();
	}

	@Override
	protected void onStop() {
		super.onStop();
		MineProfile.getInstance().Save();
//		unregisterReceiver(deliveryReceiver);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		enableFastRegBtn();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (MineProfile.getInstance().getIsLogin()) {
			this.finish();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		opening = false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.btn_back) {
//			this.finish();
			InjectKeys(KeyEvent.KEYCODE_BACK);
		} else if (id == R.id.btn_commit_login) {
			if (!ConnectManager.isNetworkConnected(this)) {
				CustomToast.showLoginRegistErrorToast(this, DcError.DC_NET_GENER_ERROR);
				return;
			}

			String username = ((EditText) findViewById(R.id.edit_login_username)).getText()
					.toString();
			String password = ((EditText) findViewById(R.id.edit_login_pwd)).getText().toString();

			if (!StringUtil.checkValidUserName(username)) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_INVALID_USERNAME);
				findViewById(R.id.edit_login_username).requestFocus();
				return;
			}

			if (!StringUtil.checkValidPassword(password)) {
				CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_INVALID_PWD);
				findViewById(R.id.edit_login_pwd).requestFocus();
				return;
			}

            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
            }
            progressDialog = CustomProgressDialog.createDialog(this);
            progressDialog.setMessage(getResources().getString(R.string.committing_tip));
            progressDialog.show();

            NetUtil.getInstance().requestUserLogin(username,password,this);


		} else if (id == R.id.btn_register) {

            Intent regisIntent = new Intent(this,BMRegisterActivity.class);
            startActivityForResult(regisIntent,RESULT_REGISTER);
		}
	}

    public static final int RESULT_REGISTER = 0x2;

	private void loginSucceed() {
		disMissProgressDialog();
		this.finish();
	}

    private void loginFailed(){
        disMissProgressDialog();
    }

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		BMUserLoginResult result = (BMUserLoginResult) responseData;

        if(result.getSuccess() == 1){
            MineProfile.getInstance().setSessionID(result.getToken());
            MineProfile.getInstance().setNickName(result.getNickname());
            MineProfile.getInstance().setStrUserHead(result.getPhoto());
            MineProfile.getInstance().setIsLogin(true);
            MineProfile.getInstance().Save();
            loginSucceed();
        }else{
            Toast.makeText(getApplicationContext(),result.getMessage(), Toast.LENGTH_LONG).show();
            MineProfile.getInstance().setIsLogin(false);
            loginFailed();
        }


	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

        Toast.makeText(getApplicationContext(),"登录失败", Toast.LENGTH_LONG).show();
        loginFailed();
		MineProfile.getInstance().setIsLogin(false);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		NetUtil.getInstance().cancelRequestById(requestId);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	public void resetUsernameEditText() {
		CharSequence text = edit_login_username.getText();
		List<String> accountList = MineProfile.getInstance().getAccountsList(); 
		if (accountList.size() > 0) {
			String nextName = accountList.get(0);
			if (TextUtils.isEmpty(text) && nextName != null) {
				edit_login_username.setText(nextName);
				text = edit_login_username.getText();
			}
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}	
		}
	}

	@Override
	public void onDismiss() {
		accountMenu = null;
	}

	private void disMissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
			hasProgressDlg = false;
		}
	}

	private void enableFastRegBtn() {
		findViewById(R.id.btn_register).setEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		String accountName = MineProfile.getInstance().getAccountsList().get(position);
		EditText userNameText = (EditText) findViewById(R.id.edit_login_username);
		userNameText.setText(accountName);
		userNameText.setSelection(accountName.length());
	}
	
	class SendReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			int resultCode = getResultCode();


			if (resultCode == Activity.RESULT_OK) {
				return;
			} else {
				cancelRequest();
				Toast.makeText(getApplicationContext(), R.string.login_sms_send_failed, Toast.LENGTH_SHORT).show();
				CustomToast.showLoginRegistErrorToast(context,
						CustomToast.DC_Err_NEED_REGISTER_MANUALLY);
				
				Intent intentManual = new Intent(getApplicationContext(), BMRegisterActivity.class);
				startActivity(intentManual);
			}

		}

	}

	protected void cancelRequest() {
		resetFastRegStatus("cancel");
		Logger.d(TAG, "cancel RequestList,size="+fastRegReqList.size());
		for (int requestId : fastRegReqList) {
			Logger.d(TAG, "cancel requestId= "+requestId);
			NetUtil.getInstance().cancelRequestById(requestId);	
		}
		fastRegReqList.clear();
		enableFastRegBtn();
		disMissProgressDialog();
	}

	/**
	 * 重置一键注册时状态为初始状态
	 */
	private void resetFastRegStatus(String action) {
		if (action.equals("init")) {
			requestHasCancel = false;
			ignoreRegResult = false;
		} else if (action.equals("cancel")) {
			requestHasCancel = true;
			ignoreRegResult = true;
		}
	}
	
	private void jump2ManualReg() {
		Intent intent = new Intent(this, BMRegisterActivity.class);
		intent.putExtra("flag", flag);
		startActivity(intent);
		CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_Err_NEED_REGISTER_MANUALLY);
	}

	private final static int MSG_CANCEL_TO_MANUAL = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_CANCEL_TO_MANUAL) {
				if (!requestHasCancel) {
					jump2ManualReg();					
				}
				cancelRequest();
			}
		};
	};
/**
 * BUG #10209 【游戏大厅_Android】2.3.2.4_抢号：未登录时点击“抢号”进入登录页面，按登录页面左上角返回到抢号页面出现异常【返回到抢号页面，抢号页面上下闪着跳动2下，之后恢复正常】
 * 实际测试发现：从抢号页SquareFragment进入登录页后，以及从MineFragment进入登录页面后，在点击登录页左上角的返回按钮返回到上一个页面时，都会导致上一个页面上下跳动几次
 * 目前判断是跟登录页弹出的输入法有关，所以这里的解决办法是：第一次按返回按钮，关掉输入法，再次点击返回按钮，再返回到上一个页面，最后测试发现此方法可避免该问题。
 */
	private void InjectKeys(final int keyEventCode) {
		Thread T=new Thread(new Runnable() {
			@Override
			public void run() {
				new Instrumentation().sendKeyDownUpSync(keyEventCode);
			}
		});
		T.setDaemon(true);
		T.start();
	}
}