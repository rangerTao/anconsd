package com.ranger.bmaterials.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.RecomdAppAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.mode.RecommendAppItemInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.RecommendAppResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.ApkUtil;
import com.ranger.bmaterials.tools.FileHelper;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UpdateHelper;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class MineSettingsActivity extends StatActivity implements OnClickListener, IRequestListener, 
OnCancelListener, OnCheckedChangeListener, OnItemClickListener {

	private ViewGroup loginViewContainer;
	private ViewGroup loginSubview;
	private ViewGroup unloginSubview;

//	private ViewGroup switchUserViewContainer;
//	private TextView switchUserLabel;

	private SettingsSwitchButton switchButtonWiFiDownload;
//	private SettingsSwitchButton switchButtonNoPic;
	private SettingsSwitchButton switchButtonDeletePkgAfterinstalling;
	private SettingsSwitchButton switchButtonShowInstallTip;
	private SettingsSwitchButton switchButtonAutoInstall;

	int maxDownloadNum;
	private Dialog alertDialog;
	private AlertDialog delPkgDialog = null;

	private CustomProgressDialog progressDialog;
	private int requestId;
	private String nickname;

	private Handler mHandler;
	private boolean rootResult = false;;
	private boolean autoInstall;
	private boolean hasClickShareBtn = false;
	private GridView recomdappGview;
	private List<RecommendAppItemInfo> recomdAppList;
	private int icon_width = 160;
	private int icon_HSpace = 15;

	UpdateResultReceiver updateResultReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine_activity_settings);
		((TextView) findViewById(R.id.label_title)).setText("设置");


//		findViewById(R.id.layout_settings_unlogin_subview).setOnClickListener(this);
//		findViewById(R.id.settings_changenickname).setOnClickListener(this);
//		findViewById(R.id.settings_changepwd).setOnClickListener(this);
//		findViewById(R.id.settings_bind_phone).setOnClickListener(this);
//		findViewById(R.id.settings_totaldownloadnum).setOnClickListener(this);
//		findViewById(R.id.settings_deletealldownloadedpkg).setOnClickListener(this);
//		findViewById(R.id.settings_contactus).setOnClickListener(this);

		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.settings_clearcache).setOnClickListener(this);
		findViewById(R.id.settings_recommandtofriend).setOnClickListener(this);
		findViewById(R.id.settings_feedback).setOnClickListener(this);
		findViewById(R.id.settings_checkupdate).setOnClickListener(this);
		findViewById(R.id.settings_about).setOnClickListener(this);

//		switchUserViewContainer = (ViewGroup) findViewById(R.id.layout_settings_switch_user_view_container);
//		switchUserLabel = (TextView) findViewById(R.id.label_settings_switch_user_subview);
//		switchUserLabel.setOnClickListener(this);
		
//		switchButtonNoPic = (SettingsSwitchButton) findViewById(R.id.settings_switch_nopic);
//		switchButtonNoPic.setOnClickListener(this);
		
		switchButtonWiFiDownload = (SettingsSwitchButton) findViewById(R.id.settings_switch_wifi);
		switchButtonWiFiDownload.setOnClickListener(this);
		switchButtonDeletePkgAfterinstalling = (SettingsSwitchButton) findViewById(R.id.settings_switch_deletepkgafterinstalling);
		switchButtonDeletePkgAfterinstalling.setOnClickListener(this);
		switchButtonShowInstallTip = (SettingsSwitchButton) findViewById(R.id.settings_switch_showinstalltip);
		switchButtonShowInstallTip.setOnClickListener(this);
		switchButtonAutoInstall = (SettingsSwitchButton) findViewById(R.id.settings_switch_installautomaticly);
		switchButtonAutoInstall.setOnClickListener(this);

//		loginViewContainer = (ViewGroup) findViewById(R.id.layout_settings_login_view_container);
//		loginSubview = (ViewGroup) findViewById(R.id.layout_settings_login_subview);
//		unloginSubview = (ViewGroup) findViewById(R.id.layout_settings_unlogin_subview);
		
		initRecommendAppView();
	}

	private void initRecommendAppView() {
		TextView title = (TextView)findViewById(R.id.recommend_subtitle);
		title.setText(R.string.down_more_duoku_app);
		
		recomdappGview = (GridView) findViewById(R.id.recomdapp_gridview);
		recomdAppList = new ArrayList<RecommendAppItemInfo>();
		Resources resources = getResources();
		RecommendAppItemInfo defaultRecmApp = new RecommendAppItemInfo(
				resources.getString(R.string.defalt_recomapp_name), R.drawable.icon_reader, 
				resources.getString(R.string.default_recomapp_url));
		recomdAppList.add(defaultRecmApp);
		RecomdAppAdapter adapter = new RecomdAppAdapter(this, recomdAppList);
		int count = adapter.getCount();
		android.widget.LinearLayout.LayoutParams params = 
				new android.widget.LinearLayout.LayoutParams((icon_width+2*icon_HSpace),
				LayoutParams.MATCH_PARENT);
		recomdappGview.setLayoutParams(params);
		recomdappGview.setColumnWidth(icon_width);
		recomdappGview.setHorizontalSpacing(icon_HSpace);
		recomdappGview.setStretchMode(GridView.NO_STRETCH);
		recomdappGview.setNumColumns(count);
		recomdappGview.setAdapter(adapter);
		recomdappGview.setOnItemClickListener(this);
		
		//get the recommend applist when the activity create.
		requestId = NetUtil.getInstance().requestRecommendApp(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if (MineProfile.getInstance().isUpdateAvailable()) {
			findViewById(R.id.label_new_version).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.label_new_version).setVisibility(View.INVISIBLE);
		}
		hasClickShareBtn = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateResultReceiver = new UpdateResultReceiver();
		registerReceiver(updateResultReceiver, new IntentFilter(Constants.UPDATE_AVIABLE));
		
//		if (MineProfile.getInstance().getIsLogin()) {
//			// 已经登录
//			unloginSubview.setVisibility(View.GONE);
//			loginSubview.setVisibility(View.VISIBLE);
//			switchUserLabel.setVisibility(View.VISIBLE);
//
//			((TextView) findViewById(R.id.label_settings_username)).setText(MineProfile.getInstance().getUserName());
//			String nickName = MineProfile.getInstance().getNickName();
//			((TextView) findViewById(R.id.label_settings_nickname)).setText(nickName);
//
//			if (MineProfile.getInstance().getUserType() != MineProfile.USERTYPE_UNBINDINGPHONE)
//				((TextView) findViewById(R.id.label_settings_bindphone)).setText(MineProfile.getInstance().getPhonenum());
//			else
//				((TextView) findViewById(R.id.label_settings_bindphone)).setText("");
//		} else {
//			// 未登录
//			unloginSubview.setVisibility(View.VISIBLE);
//			loginSubview.setVisibility(View.GONE);
//			switchUserLabel.setVisibility(View.GONE);
//		}

//		((TextView) findViewById(R.id.label_totaldownloadnum)).setText(String.valueOf(MineProfile.getInstance().getSimultaneousDownloadNum()));
//		switchButtonNoPic.setOn(MineProfile.getInstance().isNoPicture());

		switchButtonWiFiDownload.setOn(MineProfile.getInstance().isDownloadOnlyWithWiFi());
		switchButtonDeletePkgAfterinstalling.setOn(MineProfile.getInstance().isDeletePkgAfterInstallation());
		switchButtonShowInstallTip.setOn(MineProfile.getInstance().isShowInstallTipAfterDownloading());
		switchButtonAutoInstall.setOn(MineProfile.getInstance().isInstallAutomaticllyAfterDownloading());
	}

	@Override
	protected void onStop() {
		super.onStop();
		MineProfile.getInstance().Save();

		unregisterReceiver(updateResultReceiver);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewID = v.getId();

		switch (viewID) {
		case R.id.img_back: {
			this.finish();
		}
			break;

//		case R.id.layout_settings_unlogin_subview: {
//			Intent intent = new Intent();
//			intent.setClass(this, LoginActivity.class);
//			startActivity(intent);
//		}
//			break;
//
//		case R.id.settings_changenickname: {
//
//			LayoutInflater factory = LayoutInflater.from(this);
//			View dialogView = factory.inflate(R.layout.mine_change_nickname, null);
//
//			((EditText) dialogView.findViewById(R.id.edit_change_nickname)).setText(MineProfile.getInstance().getNickName());
//			CharSequence text = ((EditText) dialogView.findViewById(R.id.edit_change_nickname)).getText();
//			if (text instanceof Spannable) {
//				Spannable spanText = (Spannable) text;
//				Selection.setSelection(spanText, text.length());
//			}
//
//			dialogView.findViewById(R.id.btn_change_nickname_cancel).setOnClickListener(this);
//			dialogView.findViewById(R.id.btn_change_nickname_commit).setOnClickListener(this);
//			alertDialog = new Dialog(MineSettingsActivity.this, R.style.dialog);
//
//			DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
//			int width = dm.widthPixels - PhoneHelper.dip2px(this, 13) * 2;
//			alertDialog.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
//			alertDialog.setCancelable(true);
//			alertDialog.show();
//		}
//			break;
//		case R.id.btn_max_downloadnum_commit: {
//			alertDialog.dismiss();
//			alertDialog = null;
//			MineProfile.getInstance().setSimultaneousDownloadNum(maxDownloadNum);
//			((TextView) findViewById(R.id.label_totaldownloadnum)).setText(String.valueOf(MineProfile.getInstance().getSimultaneousDownloadNum()));
//		}
//			break;
//		case R.id.settings_changepwd: {
//			Intent intent = new Intent(this, ChangePwdActivity.class);
//			startActivity(intent);
//		}
//			break;
//
//		case R.id.settings_bind_phone: {
//			Intent intent;
//			if (MineProfile.getInstance().getPhonenum().length() <= 0) {
//				intent = new Intent(this, BindPhoneActivity.class);
//			} else {
//				intent = new Intent(this, BindPhoneVerifyActivity.class);
//			}
//			startActivity(intent);
//		}
//			break;
		case R.id.btn_max_downloadnum_cancel: {
			alertDialog.dismiss();
			alertDialog = null;
		}
			break;

		case R.id.btn_change_nickname_cancel: {
			alertDialog.dismiss();
			alertDialog = null;
		}
			break;
		case R.id.btn_change_nickname_commit: {
			EditText editText = (EditText) alertDialog.findViewById(R.id.edit_change_nickname);
			nickname = editText.getText().toString();

			if (!StringUtil.checkValidNickName(nickname)) {
				CustomToast.showToast(this, getResources().getString(R.string.valid_nickname_tip));
				return;
			}

			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();

			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(alertDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(getResources().getString(R.string.committing_tip));
			progressDialog.show();

			requestId = NetUtil.getInstance().requestChangeNickname(userid, sessionid, nickname, this);
		}
			break;

		case R.id.settings_clearcache: {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("清除中...");
			progressDialog.show();
			// progressDialog = ProgressDialog.show(this, "", "清除中...", false, true, this);
			mHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;						
					}
					CustomToast.showLoginRegistSuccessToast(MineSettingsActivity.this, CustomToast.DC_OK_CLEAR_PIC_CACHE);
				}

			};

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						final File cachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.IMAGE_CACHE);
						FileHelper.removeFile(cachePath);
					} catch (Exception e) {
					}

					mHandler.sendMessage(mHandler.obtainMessage());
				}
			}).start();
		}
			break;

//		case R.id.settings_totaldownloadnum: {
//			if (alertDialog == null) {
//				alertDialog = new Dialog(MineSettingsActivity.this, R.style.dialog);
//
//				LayoutInflater factory = LayoutInflater.from(this);
//				View dialogView = factory.inflate(R.layout.mine_max_downloadnum_dialog, null);
//
//				dialogView.findViewById(R.id.btn_max_downloadnum_cancel).setOnClickListener(this);
//				dialogView.findViewById(R.id.btn_max_downloadnum_commit).setOnClickListener(this);
//
//				((RadioGroup) dialogView.findViewById(R.id.radioGroup_max_downloadnum)).setOnCheckedChangeListener(this);
//
//				switch (MineProfile.getInstance().getSimultaneousDownloadNum()) {
//				case 1:
//					((RadioGroup) dialogView.findViewById(R.id.radioGroup_max_downloadnum)).check(R.id.radio01);
//					break;
//				case 2:
//					((RadioGroup) dialogView.findViewById(R.id.radioGroup_max_downloadnum)).check(R.id.radio02);
//					break;
//				case 3:
//					((RadioGroup) dialogView.findViewById(R.id.radioGroup_max_downloadnum)).check(R.id.radio03);
//					break;
//				case 4:
//					((RadioGroup) dialogView.findViewById(R.id.radioGroup_max_downloadnum)).check(R.id.radio04);
//					break;
//				case 5:
//					((RadioGroup) dialogView.findViewById(R.id.radioGroup_max_downloadnum)).check(R.id.radio05);
//					break;
//				default:
//					break;
//				}
//
//				DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
//				int width = dm.widthPixels - PhoneHelper.dip2px(this, 13) * 2;
//				alertDialog.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
//				alertDialog.setCancelable(true);
//				alertDialog.show();
//			}
//		}
//			break;
//		case R.id.settings_deletealldownloadedpkg: {
//
//			if (delPkgDialog == null) {
//				delPkgDialog = new AlertDialog.Builder(MineSettingsActivity.this).setTitle("删除下载的安装包").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						delPkgDialog = null;
//						progressDialog = CustomProgressDialog.createDialog(MineSettingsActivity.this);
//						progressDialog.setMessage("删除中...");
//						progressDialog.show();
//						// progressDialog =
//						// ProgressDialog.show(MineSettingsActivity.this,
//						// "", "删除中...");
//						mHandler = new Handler() {
//
//							@Override
//							public void handleMessage(Message msg) {
//								super.handleMessage(msg);
//
//								progressDialog.dismiss();
//								progressDialog = null;
//								CustomToast.showLoginRegistSuccessToast(MineSettingsActivity.this, CustomToast.DC_OK_REMOVE_PKG);
//								BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance().getApplicationContext());
//								sender.notifyDeleteDownloadedPkg();
//							}
//
//						};
//
//						new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//
//								try {
//									final File cachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.DOWNLOAD_FOLDER);
//									// 不能删除全部文件（只有下载完成的才删除，在removeFinishedDownload查询下载状态后再删除）
//									// FileHelper.removeFile(cachePath);
//									/**
//									 * 删除下载记录
//									 */
//									PackageHelper.removeFinishedDownload();
//									// AppManager.getInstance(getApplicationContext()).removeFinishedDownload();
//								} catch (Exception e) {
//								}
//
//								mHandler.sendMessage(mHandler.obtainMessage());
//							}
//						}).start();
//					}
//				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						delPkgDialog = null;
//					}
//				}).create();
//				delPkgDialog.setCanceledOnTouchOutside(false);
//				delPkgDialog.show();
//			}
//		}
//			break;
//			case R.id.settings_switch_nopic: {
//				boolean noPic = !MineProfile.getInstance().isNoPicture();
//				MineProfile.getInstance().setNoPicture(noPic);
//				switchButtonNoPic.setOn(noPic);
//			}
//				break;
//			case R.id.label_settings_switch_user_subview: {
//			MineProfile.getInstance().setIsLogin(false);
//
//			String userid = MineProfile.getInstance().getUserID();
//			String sessionid = MineProfile.getInstance().getSessionID();
//			NetUtil.getInstance().requestUserUnlogin(userid, sessionid, null);
//
//			Intent intent = new Intent(this, LoginActivity.class);
//			intent.putExtra("flag", true);
//			startActivity(intent);
//		}
		case R.id.settings_recommandtofriend: {
			if (!hasClickShareBtn) {
				hasClickShareBtn = true;
				String share_content = this.getResources().getString(R.string.share_content);
				ApkUtil.shareApp(this, share_content);
			}
		}
			break;
		case R.id.settings_feedback: {
			Intent intent = new Intent();
			intent.setClass(this, FeedbackActivity.class);
			startActivity(intent);
		}
			break;

		case R.id.settings_checkupdate: {
			UpdateHelper updateHelper = new UpdateHelper(this, false);
			updateHelper.checkGameTingUpdate(false);
		}
			break;

		case R.id.settings_about: {
			Intent intent = new Intent(this, AboutUsActivity.class);
			startActivity(intent);
		}
			break;

		case R.id.settings_switch_wifi: {
			boolean downloadbywifi = !MineProfile.getInstance().isDownloadOnlyWithWiFi();
			MineProfile.getInstance().setDownloadOnlyWithWiFi(downloadbywifi);
			switchButtonWiFiDownload.setOn(downloadbywifi);
		}
			break;

		case R.id.settings_switch_deletepkgafterinstalling: {
			boolean deletePkgAfterInstalling = !MineProfile.getInstance().isDeletePkgAfterInstallation();
			MineProfile.getInstance().setDeletePkgAfterInstallation(deletePkgAfterInstalling);
			switchButtonDeletePkgAfterinstalling.setOn(deletePkgAfterInstalling);
		}
			break;

		case R.id.settings_switch_showinstalltip: {
			boolean showInstallTip = !MineProfile.getInstance().isShowInstallTipAfterDownloading();
			MineProfile.getInstance().setShowInstallTipAfterDownloading(showInstallTip);
			switchButtonShowInstallTip.setOn(showInstallTip);
		}
			break;

		case R.id.settings_switch_installautomaticly: {

			autoInstall = !MineProfile.getInstance().isInstallAutomaticllyAfterDownloading();
			if (autoInstall) {

				progressDialog = CustomProgressDialog.createDialog(MineSettingsActivity.this);
				progressDialog.setMessage("获取root权限...");
				progressDialog.show();

				mHandler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);

						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;							
						}

						if (rootResult) {
							MineProfile.getInstance().setInstallAutomaticllyAfterDownloading(autoInstall);
							switchButtonAutoInstall.setOn(autoInstall);
						} else {
							CustomToast.showLoginRegistErrorToast(MineSettingsActivity.this, CustomToast.DC_ERR_GET_ROOT_FAILED);
						}
					}
				};

				new Thread(new Runnable() {

					@Override
					public void run() {

						rootResult = new ShellCommand().canSU(true);
						mHandler.sendMessage(mHandler.obtainMessage());
					}
				}).start();

//				try{
//					if (RootUtil.requestRoot()) {
//						MineProfile.getInstance().setInstallAutomaticllyAfterDownloading(autoInstall);
//						switchButtonAutoInstall.setOn(autoInstall);
//					} else {
//						CustomToast.showLoginRegistErrorToast(MineSettingsActivity.this, CustomToast.DC_ERR_GET_ROOT_FAILED);					
//					}
//				}catch(Exception e){
////					e.printStackTrace();
//					CustomToast.showLoginRegistErrorToast(MineSettingsActivity.this, CustomToast.DC_ERR_GET_ROOT_FAILED);
//				}
			} else {
				MineProfile.getInstance().setInstallAutomaticllyAfterDownloading(false);
				switchButtonAutoInstall.setOn(false);
			}
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {

		String requestTag = responseData.getTag();
		if (requestTag.equals(String.valueOf(Constants.NET_TAG_GET_RECOMMEND_APP))) {
			RecommendAppResult result = (RecommendAppResult) responseData;
			recomdAppList = result.getAppList();
			TextView title = (TextView)findViewById(R.id.recommend_subtitle);
			title.setText(result.getTitle());
			
			RecomdAppAdapter adapter = new RecomdAppAdapter(this, recomdAppList);
			int count = adapter.getCount();
			android.widget.LinearLayout.LayoutParams params = 
					new android.widget.LinearLayout.LayoutParams(count * (icon_width+icon_HSpace)+icon_HSpace,
					LayoutParams.MATCH_PARENT);
			recomdappGview.setLayoutParams(params);
			recomdappGview.setNumColumns(count);
			recomdappGview.setAdapter(adapter);
			recomdappGview.setOnItemClickListener(this);
			return;
		}
		
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (alertDialog != null) {
			alertDialog.dismiss();
			alertDialog = null;
		}
		BaseResult result = (BaseResult) responseData;

//		if (StringUtil.parseInt(result.getTag()) == Constants.NET_TAG_CHANGE_NICKNAME) {
//			MineProfile.getInstance().setNickName(nickname);
//			CustomToast.showLoginRegistSuccessToast(this, CustomToast.DC_OK_CHANGED_NICKNAME);
//			((TextView) findViewById(R.id.label_settings_nickname)).setText(MineProfile.getInstance().getNickName());
//		}
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		
		if (requestTag == Constants.NET_TAG_GET_RECOMMEND_APP) {
//			Toast.makeText(this, "获取推荐app出错", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		if (requestTag == Constants.NET_TAG_CHANGE_NICKNAME) {

			if (errorCode == DcError.DC_ONCE_IN_SEVEN_DAYS) {
				if (alertDialog != null) {
					alertDialog.dismiss();
					alertDialog = null;
				}
			}
			switch (errorCode) {
			case DcError.DC_EXIST_NICKNAME:
				if (MineProfile.getInstance().getNickName().equals(nickname)) {
					CustomToast.showLoginRegistErrorToast(this, CustomToast.DC_ERR_NICKNAME_NOT_CHANGED);
					if (alertDialog != null) {
						alertDialog.dismiss();
						alertDialog = null;
					}
				} else {
					CustomToast.showLoginRegistErrorToast(this, errorCode);
				}
				break;
			case DcError.DC_NEEDLOGIN:
				CustomToast.showLoginRegistErrorToast(this, DcError.DC_NEEDLOGIN);
				MineProfile.getInstance().setIsLogin(false);
				Intent intent = new Intent(this, SapiLoginActivity.class);
				startActivity(intent);
				break;
			default:
				CustomToast.showLoginRegistErrorToast(this, errorCode);
				break;
			}

		} else {
			CustomToast.showLoginRegistErrorToast(this, errorCode);
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (progressDialog != null) {
			NetUtil.getInstance().cancelRequestById(requestId);
			progressDialog = null;
		} else {
			dialog = null;
		}

		WindowManager.LayoutParams lp;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio01:
			maxDownloadNum = 1;
			break;
		case R.id.radio02:
			maxDownloadNum = 2;
			break;
		case R.id.radio03:
			maxDownloadNum = 3;
			break;
		case R.id.radio04:
			maxDownloadNum = 4;
			break;
		case R.id.radio05:
			maxDownloadNum = 5;
			break;

		default:
			break;
		}
	}

	class UpdateResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Constants.UPDATE_AVIABLE)) {
				if (MineProfile.getInstance().isUpdateAvailable()) {
					findViewById(R.id.label_new_version).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.label_new_version).setVisibility(View.INVISIBLE);
				}
			}

		}

	}

	/**
	 * recommend app gridview. If click item, will jump to the detail html page which has download link.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ClickNumStatistics.addSettingRecomdAppClickStatis(this);
		RecommendAppItemInfo itemInfo = recomdAppList.get(position);
		Intent intent = new Intent();
		intent.setClass(MineSettingsActivity.this, WebviewActivity.class);
		intent.putExtra("title", getResources().getString(R.string.down_more_duoku_app));
		intent.putExtra("url", itemInfo.getRecommendUrl());
        startActivity(intent);
	}
}
