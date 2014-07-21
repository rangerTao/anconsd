package com.ranger.bmaterials.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.SnapNumberAdapter;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.SnapNumber;
import com.ranger.bmaterials.mode.SnapNumberDetail;
import com.ranger.bmaterials.mode.SnappedNumber;
import com.ranger.bmaterials.mode.SnapNumber.SnapNumberStatus;
import com.ranger.bmaterials.mode.SnapNumberDetail.SnapNumberItem;
import com.ranger.bmaterials.mode.SnappedNumber.ResCode;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.DateUtil;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.CustomDialog;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.CustomDialog.ICustomDialog;
import com.ranger.bmaterials.work.DBTaskManager;
import com.ranger.bmaterials.work.HtmlGetter;

public class SnapNumberDetailActivity extends StatActivity implements OnClickListener/*
																					 * ,
																					 * IRequestListener
																					 */{

	public static final String ARG_USERID = "uesr_id";
	public static final String ARG_SESSIONID = "session_id";
	public static final String ARG_GAMEID = "game_id";
	public static final String ARG_GRABID = "grab_id";

	public static final String ARG_NUMBER = "number";
	public static final String ARG_STATUS = "status";

	private String userId;
	private String sessionId;
	private String gameId;
	private String grabId;

	private TextView detailLayout;
	private CustomDialog resultDialog;
	private View loadingView;
	private View contentLayout;

	private TextView percentText;
	private ProgressBar snapNumberProgressBar;
	private ImageView snapButton;
	private Dialog progressDialog;
	private View errorHint;
	private boolean isfromrec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.square_activity_snapnumber_detail);
		if (!getArgs()) {
			return;
		}
		initTitleBar();
		initView();
		loadContentData(false);
		observer = new PackageIntentReceiver();
	}

	private PackageIntentReceiver observer;
	private View pbView;

	private class PackageIntentReceiver extends BroadcastReceiver {

		public PackageIntentReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(BroadcaseSender.ACTION_USER_LOGIN);
			filter.addAction(BroadcaseSender.ACTION_USER_LOGOUT);
			registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (BroadcaseSender.ACTION_USER_LOGIN.equals(action)) {
				// Toast.makeText(SnapNumberDetailActivity.this, "用户登陆",
				// 1).show();
				setArgs(null);
				// 刷新数据
				loadContentData(true);
			} else if (BroadcaseSender.ACTION_USER_LOGOUT.equals(action)) {
				// Toast.makeText(SnapNumberDetailActivity.this, "用户登出",
				// 1).show();
				setArgs(SnapNumberStatus.NOT_LOGIN);
				// 刷新数据
				if (contentData != null)
					refreshNumberButtonState(contentData.getStatus(), contentData.getLeftCount());
				refreshNumberResultView();
			}
		}
	}

	private void setArgs(SnapNumberStatus status) {
		MineProfile profile = MineProfile.getInstance();
		this.userId = profile.getUserID();
		this.sessionId = profile.getSessionID();

		if (this.contentData != null && status != null) {
			contentData.setStatus(status);
		}

	}

	private void initTitleBar() {
		View backView = findViewById(R.id.img_back);

		TextView titleText = (TextView) findViewById(R.id.header_title);
		titleText.setText("抢号详情");

		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (observer != null) {
			unregisterReceiver(observer);
			observer = null;
		}
	}

	private boolean getArgs() {

		Intent intent = getIntent();
		this.userId = intent.getStringExtra(ARG_USERID);
		this.sessionId = intent.getStringExtra(ARG_SESSIONID);
		this.gameId = intent.getStringExtra(ARG_GAMEID);
		this.grabId = intent.getStringExtra(ARG_GRABID);
		isfromrec = intent.getBooleanExtra("isfromrec", false);
		if (isfromrec) {
			MainHallActivity.JUMP_TO_TAB_EXTRA = 3;
		}
		if (grabId == null) {
			finish();
			return false;
		}
		return true;
	}

	private boolean global_refresh;
	private ImageView iconIv;
	private TextView titleText;
	private SnapNumber snapNumber;

	private void initView() {
		snapNumber = (SnapNumber) getIntent().getSerializableExtra(ARG_NUMBER);
		global_refresh = snapNumber == null;

		loadingView = findViewById(R.id.square_detail_base_network);
		contentLayout = findViewById(R.id.content_global_layout);

		pbView = loadingView.findViewById(R.id.network_loading_pb);
		errorHint = loadingView.findViewById(R.id.loading_error_layout);
		errorHint.setOnClickListener(this);

		snapButton = (ImageView) findViewById(R.id.snapnumber_detail_snapnumber_button);
		snapButton.setOnClickListener(this);

		percentText = (TextView) findViewById(R.id.percent_text);
		snapNumberProgressBar = (ProgressBar) findViewById(R.id.snapnumber_progress_bar);

		detailLayout = (TextView) findViewById(R.id.square_detail_content);

		iconIv = (ImageView) findViewById(R.id.game_icon);
		titleText = (TextView) findViewById(R.id.title_text);
		time_text = (TextView) findViewById(R.id.time_text);
		game_title_text = (TextView) findViewById(R.id.game_title_text);
		showLoadingProgressView();

		if (!global_refresh)
			initTopView(snapNumber);
	}

	private void showLoadingProgressView() {
		contentLayout.setVisibility(View.GONE);
		loadingView.setVisibility(View.VISIBLE);
		errorHint.setVisibility(View.GONE);
	}

	private void showContentView() {
		contentLayout.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.GONE);
		errorHint.setVisibility(View.GONE);
	}

	private void showErrorView() {
		iconIv.setOnClickListener(null);
		contentLayout.setVisibility(View.GONE);
		errorHint.setVisibility(View.VISIBLE);
		pbView.setVisibility(View.GONE);
	}

	private void loadContentData(boolean refresh) {
		boolean networkAvailable = DeviceUtil.isNetworkAvailable(this);
		if (!networkAvailable) {
			if (!refresh)
				showErrorView();
			return;
		}

		NetUtil netUtil = NetUtil.getInstance();
		netUtil.requestForSnapNumberDetail(userId, sessionId, gameId, grabId, new RequestContentListener());
	}

	public void refreshNumberButtonState(SnapNumberStatus status, int leftCount) {
		if (status == SnapNumberStatus.SNAPPED) {
			// snapButton.setText("已抢");
			snapButton.setEnabled(false);
			snapButton.setImageResource(R.drawable.snapnumber_already_dark_color);
		} else if (status == null || status == SnapNumberStatus.NOT_LOGIN || status == SnapNumberStatus.NOT_SNAPPED) {
			// snapButton.setText("抢号");
			snapButton.setEnabled(true);
			snapButton.setImageResource(R.drawable.square_snap_num);
		} else if (status == SnapNumberStatus.OVER) {
			// snapButton.setText("已结束");
			// snapButton.setText("");
			snapButton.setEnabled(false);
			snapButton.setImageResource(R.drawable.snapnumber_over_dark_color);
		}
		if (leftCount <= 0) {
			if (status == SnapNumberStatus.SNAPPED) {
				// snapButton.setText("已抢");
				snapButton.setEnabled(false);
				snapButton.setImageResource(R.drawable.snapnumber_already_dark_color);
			} else if (status == SnapNumberStatus.OVER) {
				// snapButton.setText("已结束");
				// snapButton.setText("");
				snapButton.setEnabled(false);
				snapButton.setImageResource(R.drawable.snapnumber_over_dark_color);
			} else {
				// snapButton.setText("抢光了");
				// snapButton.setText("");
				snapButton.setEnabled(false);
				snapButton.setImageResource(R.drawable.snapnumber_null_dark_color);
			}

		}
	}

	private void refreshNumberResultView(String... number) {
		if (number == null || number.length == 0 || TextUtils.isEmpty(number[0])) {
		} else {
//			showSnapNumberView(number[0]);
		}
	}

	private SnapNumberDetail contentData;

	// private Button viewGameButton;

	private void updateSnapProgress(int left_count, int totalCount) {
		int progressValue = SnapNumberAdapter.getProgressValue(totalCount, left_count);
		if (progressValue > 0) {
			percentText.setText("号码剩余量:" + progressValue + "%");
		} else {
			percentText.setText("号码剩余量:无");
		}
		snapNumberProgressBar.setProgress(progressValue);
	}

	private void initTopView(SnapNumber number) {
		ImageLoaderHelper.displayImage(number.getIconUrl(), iconIv);

		titleText.setText(StringUtil.convertEscapeString(number.getTitle()));

		updateSnapProgress(number.getLeftCount(), number.getTotalCount());

		// 抢号状态
		refreshNumberButtonState(number.getStatus(), number.getLeftCount());

		// 抢号号码
		refreshNumberResultView(number.getNumber());
	}

	/**
	 * 填充content数据
	 * 
	 * @param data
	 */
	private void fillContentData(SnapNumberDetail data) {
		try {
			this.contentData = data;
            iconIv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AppManager manager = AppManager.getInstance(getApplicationContext());
                    manager.jumpToDetail(SnapNumberDetailActivity.this, contentData.getGameId(), contentData.getGameName(), contentData.getPackageName(), false);
                }
            });

			if (global_refresh) {
				String iconUrl = data.getIconUrl();
				ImageLoaderHelper.displayImage(iconUrl, iconIv);

				String title = data.getTitle();
				titleText.setText(StringUtil.convertEscapeString(title));
			}

			updateSnapProgress(data.getLeftCount(), data.getTotalCount());
			refreshNumberButtonState(data.getStatus(), data.getLeftCount());

			refreshNumberResultView(data.getNumber());

			List<SnapNumberItem> list = data.getData();
			int size = list.size();

			String html = "";
			for (int i = 0; i < size; i++) {

				SnapNumberItem item = list.get(i);
				html += StringUtil.convertEscapeString(item.getContent());

			}

			detailLayout.setText(Html.fromHtml(html, new HtmlGetter(this, detailLayout), null));
			time_text.setText(DateUtil.formatDate(new Date(data.getTime())));
			game_title_text.setText(data.getGameName());
			if (!TextUtils.isEmpty(gameId)) {
				View game_detail_button_container = findViewById(R.id.snapnumber_detail_viewdetail_button);
				game_detail_button_container.setVisibility(View.VISIBLE);
				TranslateAnimation tAnimation = new TranslateAnimation(0.0f, 0.0f, 100.f, 0.0f);
				tAnimation.setDuration(200);
				tAnimation.setInterpolator(new DecelerateInterpolator());
				game_detail_button_container.setAnimation(tAnimation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showResultDialog(final String number, final String... title) {
		final SnapNumber snapNum = contentData.getSnapNumber();
		if (snapNumber.mode == null) {
			new AsyncTask<Void, Void, SnapNumber>() {

				@Override
				protected SnapNumber doInBackground(Void... params) {
					QueryInput qi = new QueryInput(snapNumber.getPackageName(), snapNumber.version, snapNumber.verCode, snapNumber.downloadUrl, snapNumber.getGameId());

					Map<QueryInput, PackageMode> status = PackageHelper.queryPackageStatus(qi);
					snapNumber.mode = snapNum.mode = status.get(qi);

					return snapNumber;
				}

				@Override
				protected void onPostExecute(SnapNumber snapNumber) {
					if (snapNumber != null && snapNumber.mode != null) {
						doShowResultDialog(snapNumber, number, title);
					}

				};

			}.execute();
		} else {
			snapNum.mode = snapNumber.mode;
			doShowResultDialog(snapNumber, number, title);
		}
	}

	private TextView buttonLeft;
	private TextView time_text;
	private TextView game_title_text;

	private void doShowResultDialog(final SnapNumber snapNumber, final String number, String... title) {
		resultDialog = new CustomDialog(this);
		if (title != null && title.length > 0) {
			resultDialog.setTitle(title[0]);
		} else
			resultDialog.setTitle(getString(R.string.square_snap_num_success));

		ICustomDialog impl = new ICustomDialog() {

			@Override
			public void preAddView(CustomDialog dialog) {
				// TODO Auto-generated method stub
				PackageMode mode = snapNumber.mode;
				if (mode.status == PackageMode.UNDOWNLOAD) {
					buttonLeft.setText(R.string.download_game);
				} else if (mode.status == PackageMode.INSTALLED) {
					buttonLeft.setText(R.string.open_game);
				} else {
					buttonLeft.setText(R.string.view_detail);
				}

				buttonLeft.setTag(snapNumber);
			}

			@Override
			public View initOtherView(CustomDialog dialog) {
				// TODO Auto-generated method stub
				buttonLeft = dialog.buttonLeft;
				dialog.buttonRight.setText("关闭");

				View contentView = View.inflate(SnapNumberDetailActivity.this, R.layout.snap_num_dialog_layout, null);

				TextView tvBody = (TextView) contentView.findViewById(R.id.custom_dialog_body);
				tvBody.setText(number);
				Button copyButton = (Button) contentView.findViewById(R.id.custom_dialog_body_button);
				copyButton.setTag(number);
				copyButton.setOnClickListener(dialog.getClickListner());
				return contentView;
			}
		};
		resultDialog.setImpl(impl).setClickListner(this).createView().show();
	}

	private void dismissResultDialog() {
		if (resultDialog != null && resultDialog.isShowing()) {
			resultDialog.dismiss();
		}
	}

	private void checkGame(final PackageMode mode) {

		DBTaskManager.submitTask(new Runnable() {

			@Override
			public void run() {

				String gameId = mode.gameId;

				if (mode.status == PackageMode.INSTALLED) {
					InstalledAppInfo info = AppManager.getInstance(SnapNumberDetailActivity.this).getInstalledGame(mode.packageName);
					StartGame isg = new StartGame(SnapNumberDetailActivity.this, mode.packageName, info.getExtra(), gameId, info.isNeedLogin());
					isg.startGame();
				} else {
					AppManager manager = AppManager.getInstance(SnapNumberDetailActivity.this);
                    manager.jumpToDetail(SnapNumberDetailActivity.this, gameId, "", null, false);
                }
			}
		});
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void copyText(String plainText) {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				clipboard.setText(plainText);
			}
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				android.content.ClipData clip = android.content.ClipData.newPlainText("text", plainText);
				clipboard.setPrimaryClip(clip);
			}
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public String pasteText() {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				return (String) clipboard.getText();
			}
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null && clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
				return (String) clipboard.getPrimaryClip().getItemAt(0).getText();
			}
		}
		return null;
	}

	private void showNumberProgressDialog() {
		progressDialog = DuokuDialog.showProgressDialog(this, true, null);
	}

	private void dismissNumberProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void tryToSnapNumber() {
		MineProfile profile = MineProfile.getInstance();
		boolean isLogin = profile.getIsLogin();
		if (!isLogin) {
			CustomToast.showToast(this, getString(R.string.login_to_snap));
			Intent intent = new Intent(this, SapiLoginActivity.class);
			startActivity(intent);
			return;
		}
		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(sessionId)) {
			// Toast.makeText(this, "请登陆", Toast.LENGTH_LONG).show();
			CustomToast.showToast(this, getString(R.string.login_to_snap));
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, SapiLoginActivity.class);
			startActivity(intent);
		} else {
			if (DeviceUtil.isNetworkAvailable(this)) {
				showNumberProgressDialog();
				NetUtil.getInstance().requestForSnapNumber(userId, sessionId, gameId, grabId, new RequestNumberListener());
			} else {
				CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
				// Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
			}

		}
	}

	private class RequestNumberListener implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			dismissNumberProgressDialog();
			if (responseData.getErrorCode() == DcError.DC_OK) {
				SnappedNumber number = (SnappedNumber) responseData;
				int resCode = number.getResCode();
				switch (resCode) {
				case ResCode.SUCCESS:
					showResultDialog(number.getNumber());
					int leftCount = contentData.getLeftCount();
					if (leftCount > 1) {
						contentData.setLeftCount(leftCount - 1);
					}
					updateSnapProgress(contentData.getLeftCount(), contentData.getTotalCount());
					setArgs(SnapNumberStatus.SNAPPED);
					refreshNumberButtonState(contentData.getStatus(), contentData.getLeftCount());
					refreshNumberResultView(number.getNumber());
					sendSuccessResult(number.getGrabId());
					break;
				case ResCode.MULTIPLE_ACTION:
					// Toast.makeText(fragment.SnapNumberDetailActivity.this,
					// "您已经抢过了！",
					// Toast.LENGTH_LONG).show();
					showResultDialog(number.getNumber(), getString(R.string.snapped));
					setArgs(SnapNumberStatus.SNAPPED);
					refreshNumberButtonState(contentData.getStatus(), contentData.getLeftCount());
					refreshNumberResultView(number.getNumber());
					sendMutilResult(number.getGrabId());
					break;
				case ResCode.BAD_LUCK:
					CustomToast.showToast(getApplicationContext(), getString(R.string.snap_bad_luck));
					// Toast.makeText(host, "对不起，您未抢中，您可以重新尝试！",
					// Toast.LENGTH_LONG).show();
					break;
				case ResCode.NONE:
					CustomToast.showToast(getApplicationContext(), getString(R.string.snap_null));
					// Toast.makeText(host, "对不起，号码已被抢光！",
					// Toast.LENGTH_LONG).show();
					// refreshState(ResCode.NONE,null);
					// refreshNumberButtonState(SnapNumberStatus.OVER);
					contentData.setLeftCount(0);
					updateSnapProgress(contentData.getLeftCount(), contentData.getTotalCount());
					refreshNumberButtonState(contentData.getStatus(), contentData.getLeftCount());
					sendOverResult(number.getGrabId());
					break;
				case ResCode.OVER:
					CustomToast.showToast(getApplicationContext(), getString(R.string.snap_over));
					// Toast.makeText(host, "对不起，活动已结束！",
					// Toast.LENGTH_LONG).show();
					setArgs(SnapNumberStatus.OVER);
					refreshNumberButtonState(contentData.getStatus(), contentData.getLeftCount());
					sendNoneResult(number.getGrabId());
					break;
				case ResCode.PENDING:
					CustomToast.showToast(getApplicationContext(), getString(R.string.snap_pending));
					// Toast.makeText(host, "对不起，活动还没开始！",
					// Toast.LENGTH_LONG).show();
					// fragment.refreshState(ResCode.OVER,number);
					break;
				default:
					CustomToast.showToast(getApplicationContext(), getString(R.string.snap_failed));
					// Toast.makeText(host, "抢号失败", Toast.LENGTH_LONG).show();
					break;
				}

			} else if (responseData.getErrorCode() == DcError.DC_NEEDLOGIN) {
				CustomToast.showToast(getApplicationContext(), getString(R.string.need_login_tip));

				setArgs(SnapNumberStatus.NOT_LOGIN);
				refreshNumberButtonState(contentData.getStatus(), contentData.getLeftCount());
				// session失效
				// Toast.makeText(host, "session失效请重新登录",
				// Toast.LENGTH_LONG).show();
				MineProfile profile = MineProfile.getInstance();
				profile.setIsLogin(false);
				Intent intent = new Intent(SnapNumberDetailActivity.this, SapiLoginActivity.class);
				startActivity(intent);

			} else {
				CustomToast.showToast(getApplicationContext(), getString(R.string.snap_failed));
			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			dismissNumberProgressDialog();

			if (errorCode == DcError.DC_NEEDLOGIN) {
				CustomToast.showToast(getApplicationContext(), getString(R.string.need_login_tip));

				// session失效
				// Toast.makeText(getApplicationContext(), "请重新登录",
				// Toast.LENGTH_LONG).show();
				MineProfile profile = MineProfile.getInstance();
				profile.setIsLogin(false);
				Intent intent = new Intent(SnapNumberDetailActivity.this, SapiLoginActivity.class);
				startActivity(intent);
			} else {
				CustomToast.showToast(getApplicationContext(), getString(R.string.snap_failed));
			}

		}

	}

	private class RequestContentListener implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (responseData.getErrorCode() == DcError.DC_OK) {
				SnapNumberDetail data = (SnapNumberDetail) responseData;
				if(TextUtils.isEmpty(data.getId())){
					fillContentWithNoneData();
				}else{
					gameId = data.getGameId();
					fillContentData(data);
					showContentView();
					sendDetailResult();
				}
			} else {
				showErrorView();
				// Toast.makeText(this, "获取详细内容失败", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			showErrorView();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.snapnumber_detail_snapnumber_button:
			tryToSnapNumber();
			ClickNumStatistics.addSquareSnapNumDetailActionStatistics(this);
			break;
		case R.id.custom_dialog_body_button:
			copyText((String) v.getTag());
			CustomToast.showToast(getApplicationContext(), getString(R.string.copy_successfully));
			break;
		case R.id.dialog_button_left:
			SnapNumber snapNumber = (SnapNumber) v.getTag();
			PackageMode mode = snapNumber.mode;

			if (mode.status != PackageMode.UNDOWNLOAD) {
				dismissResultDialog();
				checkGame(mode);
			} else {
				// 变成查看详情 弹出toast 并开始下载任务

				DownloadItemInput dInfo = new DownloadItemInput();
				dInfo.setGameId(snapNumber.getGameId());
				dInfo.setDownloadUrl(snapNumber.downloadUrl);
				dInfo.setDisplayName(snapNumber.gameName);
				dInfo.setPackageName(snapNumber.getPackageName());
				dInfo.setIconUrl(snapNumber.getIconUrl());
				dInfo.setAction(snapNumber.startAction);
				dInfo.setNeedLogin(snapNumber.isNeedLogin);
				dInfo.setVersion(snapNumber.version);
				dInfo.setVersionInt(snapNumber.verCode);
				dInfo.setSize(Long.valueOf(snapNumber.pkgSize));

				PackageHelper.download(dInfo, null);

				buttonLeft.setText(R.string.view_detail);
				CustomToast.showToast(this, snapNumber.gameName + "开始下载");
				// DownloadStatistics.addDownloadGameStatistics(this,
				// snapNumber.gameName);
				mode.status = PackageMode.DOWNLOAD_RUNNING;
			}

			break;
		case R.id.dialog_button_right:
			dismissResultDialog();
			break;
		case R.id.snapnumber_detail_viewdetail_button:
			AppManager manager = AppManager.getInstance(getApplicationContext());
            manager.jumpToDetail(this, contentData.getGameId(), contentData.getGameName(), contentData.getPackageName(), false);
            break;

		case R.id.loading_error_layout:
			if (DeviceUtil.isNetworkAvailable(getApplicationContext())) {
				loadContentData(false);
				errorHint.setVisibility(View.GONE);
				pbView.setVisibility(View.VISIBLE);
			} else {
				CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
			}
			break;

		default:
			break;
		}
	}

	private void sendSuccessResult(String snapId) {
		BroadcaseSender sender = BroadcaseSender.getInstance(this);
		sender.notifySnapNumber(snapId);
	}

	private void sendDetailResult() {
		if (contentData != null) {
			int leftCount = contentData.getLeftCount();
			int totalCount = contentData.getTotalCount();
			SnapNumberStatus status = contentData.getStatus();
			if (contentData.getId() != null && leftCount <= totalCount && totalCount > 0 && leftCount >= 0 && status != null) {
				BroadcaseSender sender = BroadcaseSender.getInstance(this);
				sender.notifySnapNumberDetail(contentData.getId(), status, leftCount);
			}

		}

	}

	private void sendMutilResult(String snapId) {
		BroadcaseSender sender = BroadcaseSender.getInstance(this);
		sender.notifySnapNumberMutilple(snapId);
	}

	private void sendOverResult(String snapId) {
		BroadcaseSender sender = BroadcaseSender.getInstance(this);
		sender.notifySnapNumberOver(snapId);
	}

	private void sendNoneResult(String snapId) {
		BroadcaseSender sender = BroadcaseSender.getInstance(this);
		sender.notifySnapNumberNone(snapId);
	}
	private void fillContentWithNoneData() {
		findViewById(R.id.no_data_hint_container).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.no_data_hint_textview)).setText(R.string.none_snapnumber_data);
		CustomToast.showToast(SnapNumberDetailActivity.this, getString(R.string.none_snapnumber_data));
	}
}
