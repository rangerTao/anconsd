package com.ranger.bmaterials.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.PublishCommentStarResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.PhoneHelper;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class PublishCommentActivity extends StatActivity implements OnClickListener, IRequestListener {

	View ll_iv_back_publish_comment_activity;
	RatingBar rb_publish_comment_activity;
	EditText et_publish_comment_activity;
	TextView tv_words_count_publish_comment_activity;
	LinearLayout ll_bt_publish_comment_activity;
	TextView tv_bt_publish_comment_activity;

	String gameid;
	String gamename;
	String userid;
	// String sessionid;
	String cmtcontent;
	int star;

	int textColor1 = StringUtil.getColor("FFAC1B");
	int textColor2 = StringUtil.getColor("81B537");

//	boolean text_change_flag;

	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.publish_comment_activity);

		gameid = getIntent().getStringExtra("gameid");
		gamename = getIntent().getStringExtra("gamename");
		userid = getIntent().getStringExtra("userid");
		// sessionid = getIntent().getStringExtra("sessionid");

		ll_iv_back_publish_comment_activity = findViewById(R.id.ll_iv_back_publish_comment_activity);
		ll_iv_back_publish_comment_activity.setOnClickListener(this);

		rb_publish_comment_activity = (RatingBar) findViewById(R.id.rb_publish_comment_activity);
		et_publish_comment_activity = (EditText) findViewById(R.id.et_publish_comment_activity);
		tv_words_count_publish_comment_activity = (TextView) findViewById(R.id.tv_words_count_publish_comment_activity);
		ll_bt_publish_comment_activity = (LinearLayout) findViewById(R.id.ll_bt_publish_comment_activity);
		tv_bt_publish_comment_activity = (TextView) findViewById(R.id.tv_bt_publish_comment_activity);

		ll_bt_publish_comment_activity.setOnClickListener(this);

		et_publish_comment_activity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				// Log.i("WWWWW",
				// "onTextChanged s:"+s+"...start:"+start+"...before:"+before+"...count:"+count);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				// Log.i("WWWWW",
				// "beforeTextChanged s:"+s+"...start:"+start+"...after:"+after+"...count:"+count);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.toString().length() >= 140) {
					// et_publish_comment_activity.setEnabled(false);
					tv_words_count_publish_comment_activity.setText("0");
					tv_words_count_publish_comment_activity.setTextColor(textColor1);
					if (s.toString().length() > 140)
						s.delete(140, s.toString().length());
				} else {
					// et_publish_comment_activity.setEnabled(true);
					tv_words_count_publish_comment_activity.setText(String.valueOf(140 - s.toString().length()));
					tv_words_count_publish_comment_activity.setTextColor(textColor2);
				}

//				if (s.toString().length() == 0) {
//					text_change_flag = false;
//					tv_bt_publish_comment_activity.setTextColor(Color.BLACK);
//					ll_bt_publish_comment_activity.setClickable(false);
//					ll_bt_publish_comment_activity.setBackgroundResource(R.drawable.bg_bt_coming_soon_game_detail);
//				} else if (s.toString().length() > 0 && !text_change_flag) {
//					text_change_flag = true;
//					tv_bt_publish_comment_activity.setTextColor(Color.WHITE);
//					ll_bt_publish_comment_activity.setClickable(true);
//					ll_bt_publish_comment_activity.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
//				}
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		MineProfile.getInstance().Save();
	}

	PublishCommentStarResult mPublishCommentStarResult;
	private Dialog dialogChangeNickname;
	private int requestId;
	private String nickname;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_iv_back_publish_comment_activity:
			PublishCommentActivity.this.finish();
			break;
		case R.id.ll_bt_publish_comment_activity:
			if (MineProfile.getInstance().getNickName() == null || "".equals(MineProfile.getInstance().getNickName())) {
				callNickNameDialog();
				return;
			}

			cmtcontent = et_publish_comment_activity.getEditableText().toString();
			cmtcontent = checkContent(cmtcontent.trim());
			if (cmtcontent.length() < 2) {
				// Toast.makeText(PublishCommentActivity.this, "请至少输入两个非空字符",
				// 1).show();
				CustomToast.showToast(PublishCommentActivity.this, "请至少输入两个非空字符");
				return;
			}

//			ll_bt_publish_comment_activity.setClickable(false);
			star = rb_publish_comment_activity.getProgress();
			NetUtil.getInstance().requestPublishCommentStar(gameid, userid, MineProfile.getInstance().getSessionID(), cmtcontent, star, new IRequestListener() {

				@Override
				public void onRequestSuccess(BaseResult responseData) {
					// Toast.makeText(PublishCommentActivity.this,
					// "发表成功", 1).show();
					CustomToast.showToast(PublishCommentActivity.this, "发表成功");
//					ll_bt_publish_comment_activity.setClickable(true);
					Intent intent = new Intent("com.duoku.action.comment.publish.success");

					PublishCommentActivity.this.sendBroadcast(intent);

					progressDialog.dismiss();

					PublishCommentActivity.this.finish();
					PublishCommentStarResult result = (PublishCommentStarResult) responseData;

					MineProfile.getInstance().addCoinnum(result.getCoinnum());

					GeneralStatistics.addCommentSucceedStatistics(PublishCommentActivity.this, gamename);
				}

				@Override
				public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
					switch (errorCode) {
					case DcError.DC_NEEDLOGIN:
						CustomToast.showToast(PublishCommentActivity.this, PublishCommentActivity.this.getResources().getString(R.string.need_login_tip));

						MineProfile.getInstance().setIsLogin(false);
						Intent login_in = new Intent(PublishCommentActivity.this, SapiLoginActivity.class);
						startActivity(login_in);
						break;
					case DcError.DC_HAVE_SENSITIVE_WORD:
						// Toast.makeText(PublishCommentActivity.this,
						// "您输入的内容含有敏感词汇", 1).show();
						CustomToast.showToast(PublishCommentActivity.this, "您输入的内容含有敏感词汇");
						break;
					case DcError.DC_NEED_NICK_NAME:
						callNickNameDialog();
						break;
					default:
						// Toast.makeText(PublishCommentActivity.this,
						// "发表失败"+errorCode, 1).show();
						CustomToast.showToast(PublishCommentActivity.this, "发表失败");
					}
//					ll_bt_publish_comment_activity.setClickable(true);
					progressDialog.dismiss();

				}
			});

			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.setCancelable(false);
			progressDialog.show();
			break;
		case R.id.btn_change_nickname_cancel:
			dialogChangeNickname.dismiss();
			dialogChangeNickname = null;
			break;
		case R.id.btn_change_nickname_commit:
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
			break;

		}

	}

	private String checkContent(String content) {
		Pattern p = Pattern.compile("\n{2,140}");
		Matcher m = p.matcher(content);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		boolean result = m.find();
		while (result) {
			i++;
			m.appendReplacement(sb, "\n");

			result = m.find();
		}
		m.appendTail(sb);

		Pattern p2 = Pattern.compile(" {2,140}");
		Matcher m2 = p2.matcher(sb.toString());
		StringBuffer sb2 = new StringBuffer();
		int i2 = 0;
		boolean result2 = m2.find();
		while (result2) {
			i2++;
			m2.appendReplacement(sb2, " ");

			result2 = m2.find();
		}
		m2.appendTail(sb2);

		return sb2.toString();
	}

	private void callNickNameDialog() {
		LayoutInflater factory = LayoutInflater.from(PublishCommentActivity.this);
		View dialogView = factory.inflate(R.layout.mine_change_nickname, null);

		((EditText) dialogView.findViewById(R.id.edit_change_nickname)).setText(MineProfile.getInstance().getNickName());
		CharSequence text = ((EditText) dialogView.findViewById(R.id.edit_change_nickname)).getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
		dialogView.findViewById(R.id.btn_change_nickname_cancel).setOnClickListener(PublishCommentActivity.this);
		dialogView.findViewById(R.id.btn_change_nickname_commit).setOnClickListener(PublishCommentActivity.this);
		dialogChangeNickname = new Dialog(PublishCommentActivity.this, R.style.dialog);

		DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
		int width = dm.widthPixels - PhoneHelper.dip2px(PublishCommentActivity.this, 13) * 2;
		dialogChangeNickname.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
		dialogChangeNickname.setCancelable(true);
		dialogChangeNickname.show();
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

}
