package com.ranger.bmaterials.ui;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.ApkUtil;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.DialogFactory;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.DuokuDialog;

/**
 * 这个类处理游戏详情页和攻略详情页底部的分享，下载，收藏逻辑
 * 
 * @author zhangxiaofeng
 * 
 */
public abstract class GameDetailsBaseActivity extends HeaderCoinBackBaseActivity implements OnClickListener {
	private ImageView iv_share_game_guide_detail;// 分享按钮
	protected String gamename;
	protected GameInfo gameInfo;
	protected ImageView iv_collect_game_detail;
	protected View pbra_download_game_detail_container;
	// 下载进度条
	protected ProgressBar pbra_download_game_detail;
	// 下载按钮
	protected LinearLayout ll_bt_download_game_detail;
	// 下载icon
	protected ImageView iv_icon_download_bottom_game_detail;
	// 下载状态
	protected TextView tv_download_status_bottom_game_detail;
	// 下载进度百分比
	protected TextView tv_download_percent_bottom_game_detail;
	protected final int DOWNLOAD_NOTIFY = 1000;
	HashMap<String, GameInfo> wait2download_map = new HashMap<String, GameInfo>();
	protected final static int REQUEST_DOWNLOAD_IN_WAP_NETWORK = 1000;
	protected int current_page = 1;
	private Dialog rootDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		gamename = getHeaderTitle();
	}

	private void initView() {
		iv_share_game_guide_detail = (ImageView) findViewById(R.id.iv_share_game_detail);
		iv_share_game_guide_detail.setOnClickListener(this);
		iv_collect_game_detail = (ImageView) findViewById(R.id.iv_collect_game_detail);
		iv_collect_game_detail.setOnClickListener(this);
		pbra_download_game_detail_container = (View) findViewById(R.id.pbra_download_game_detail_container);
		// 下载进度条
		pbra_download_game_detail = (ProgressBar) findViewById(R.id.pbra_download_game_detail);
		// 下载按钮
		ll_bt_download_game_detail = (LinearLayout) findViewById(R.id.ll_bt_download_game_detail);
		// 下载icon
		iv_icon_download_bottom_game_detail = (ImageView) findViewById(R.id.iv_icon_download_bottom_game_detail);
		// 下载状态
		tv_download_status_bottom_game_detail = (TextView) findViewById(R.id.tv_download_status_bottom_game_detail);
		// 下载进度百分比
		tv_download_percent_bottom_game_detail = (TextView) findViewById(R.id.tv_download_percent_bottom_game_detail);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_share_game_detail:
			String share_content = String.format(getResources().getString(R.string.share_game_text), gamename);
			ApkUtil.shareApp(this, share_content);
			GeneralStatistics.addShareGameStatistics(this, gamename);
			break;
		case R.id.iv_collect_game_detail:
			if (gameInfo != null) {
				boolean is_login = MineProfile.getInstance().getIsLogin();
				if (is_login) {
					if (gameInfo.isIscollected()) {
						// 取消收藏
						NetUtil.getInstance().requestCollectionAction(MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), 0, 1, gameInfo.getGameId(),
								new IRequestListener() {

									@Override
									public void onRequestSuccess(BaseResult responseData) {
										MineProfile.getInstance().decreaseCollectnum();
										gameInfo.setIscollected(false);
										iv_collect_game_detail.setImageResource(R.drawable.bt_collect_game_detail_selector);
										// Toast.makeText(GameDetailsBaseActivity.this,
										// "取消收藏成功", 1).show();
										CustomToast.showToast(GameDetailsBaseActivity.this, "取消收藏成功");
										Intent intent = new Intent(BroadcaseSender.ACTION_COLLECT_GAME_CANCEL);
										intent.putExtra(Constants.JSON_GAMEID, gameInfo.getGameId());
										GameDetailsBaseActivity.this.sendBroadcast(intent);
									}

									@Override
									public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
										if (DcError.DC_NEEDLOGIN == errorCode) {
											MineProfile.getInstance().setIsLogin(false);
											Intent login_in = new Intent(GameDetailsBaseActivity.this, SapiLoginActivity.class);
											startActivity(login_in);
											CustomToast.showToast(GameDetailsBaseActivity.this, GameDetailsBaseActivity.this.getResources().getString(R.string.need_login_tip));

										} else
											// Toast.makeText(GameDetailsBaseActivity.this,
											// "取消收藏失败", 1).show();
											CustomToast.showToast(GameDetailsBaseActivity.this, "取消收藏失败");
									}
								});
					} else {
						// 收藏
						NetUtil.getInstance().requestCollectionAction(MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), 0, 0, gameInfo.getGameId(),
								new IRequestListener() {

									@Override
									public void onRequestSuccess(BaseResult responseData) {
										MineProfile.getInstance().increaseCollectnum();
										gameInfo.setIscollected(true);
										iv_collect_game_detail.setImageResource(R.drawable.bt_collected_game_detail_selector);
										// Toast.makeText(GameDetailsBaseActivity.this,
										// "收藏成功", 1).show();
										CustomToast.showToast(GameDetailsBaseActivity.this, "收藏成功");
										Intent intent = new Intent(BroadcaseSender.ACTION_COLLECT_GAME_SUCCESS);
										intent.putExtra(Constants.JSON_GAMEID, gameInfo.getGameId());
										intent.putExtra(Constants.JSON_GAMENAME, gameInfo.getGameName());
										intent.putExtra(Constants.JSON_PKGNAME, gameInfo.getPkgname());
										intent.putExtra(Constants.JSON_GAMEURL, gameInfo.getIconUrl());
										intent.putExtra(Constants.JSON_STAR, gameInfo.getStar());
										intent.putExtra(Constants.JSON_DOWNLOADTIMES, gameInfo.getDownloadedtimes());
										GameDetailsBaseActivity.this.sendBroadcast(intent);

										GeneralStatistics.addCollectGameStatistics(GameDetailsBaseActivity.this, gamename);
									}

									@Override
									public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
										if (DcError.DC_NEEDLOGIN == errorCode) {
											MineProfile.getInstance().setIsLogin(false);
											Intent login_in = new Intent(GameDetailsBaseActivity.this, SapiLoginActivity.class);
											startActivity(login_in);
											CustomToast.showToast(GameDetailsBaseActivity.this, GameDetailsBaseActivity.this.getResources().getString(R.string.need_login_tip));
										} else if (DcError.DC_REACH_MAX_COLLECTION_NUM == errorCode) {
											// Toast.makeText(GameDetailsBaseActivity.this,
											// "已达到收藏上限", 1).show();
											CustomToast.showToast(GameDetailsBaseActivity.this, "已达到收藏上限");
										} else
											// Toast.makeText(GameDetailsBaseActivity.this,
											// "收藏失败", 1).show();
											CustomToast.showToast(GameDetailsBaseActivity.this, "收藏失败");

									}
								});
					}
				} else {
					MineProfile.getInstance().setIsLogin(false);
					Intent login_in = new Intent(GameDetailsBaseActivity.this, SapiLoginActivity.class);
					startActivity(login_in);
					CustomToast.showToast(GameDetailsBaseActivity.this, "请先登录再收藏");
				}
			}
			break;

		}
	}

	protected void setiv_share_game_guide_detail_Hide(int visibility) {
		iv_share_game_guide_detail.setVisibility(visibility);
	}

	@Override
	public String getHeaderTitle() {
		return getIntent().getStringExtra(GameDetailConstants.KEY_GAME_NAME);
	}
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == DOWNLOAD_NOTIFY) {
				if (gameInfo.download_status != null) {
					if (gameInfo.download_status.mergeFailedCount >= 2) {
						gameInfo.download_status.isDiffDownload = false;
					}

					Log.d("DOWNLOAD_RUNNING", "handle message: " + gameInfo.download_status.status);

					switch (gameInfo.download_status.status) {

					// case GameInfo.GAME_UNINSTALL:
					case PackageMode.UNDOWNLOAD:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_download_bottom_game_detail);
						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						tv_download_status_bottom_game_detail.setText(R.string.label_download);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
						break;
					case PackageMode.UPDATABLE:

						break;
					case PackageMode.UPDATABLE_DIFF:

						break;

					case PackageMode.INSTALLING:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.a_installing_game_detail);
						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						tv_download_status_bottom_game_detail.setText(R.string.label_installing);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
						AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon_download_bottom_game_detail.getDrawable();
						animationDrawable.start();
						break;

					case PackageMode.DOWNLOAD_RUNNING:
						pbra_download_game_detail.setVisibility(View.VISIBLE);
						pbra_download_game_detail_container.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_pause_bottom_game_detail);
						ll_bt_download_game_detail.setBackgroundResource(0);
						tv_download_status_bottom_game_detail.setText(R.string.label_pause);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
						tv_download_percent_bottom_game_detail.setVisibility(View.VISIBLE);
						tv_download_percent_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
						if (!gameInfo.download_status.isDiffDownload) {
							pbra_download_game_detail.setSecondaryProgress((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100));

							tv_download_percent_bottom_game_detail.setText((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100) + "%");
						} else {
							refreshDiffProgress();
						}
						break;

					case PackageMode.DOWNLOAD_PAUSED:
						pbra_download_game_detail.setVisibility(View.VISIBLE);
						pbra_download_game_detail_container.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_continue_bottom_game_detail);
						ll_bt_download_game_detail.setBackgroundResource(0);
						tv_download_status_bottom_game_detail.setText(R.string.label_continue);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
						tv_download_percent_bottom_game_detail.setVisibility(View.VISIBLE);

						if (!gameInfo.download_status.isDiffDownload) {
							pbra_download_game_detail.setSecondaryProgress((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100));

							tv_download_percent_bottom_game_detail.setText((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100) + "%");
						} else {
							refreshDiffProgress();
						}
						break;

					case PackageMode.CHECKING:
					case PackageMode.MERGING:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);

						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

						tv_download_status_bottom_game_detail.setText(R.string.label_checking_diff_update);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_download_bottom_game_detail);

						tv_download_percent_bottom_game_detail.setVisibility(View.GONE);
						break;

					case PackageMode.CHECKING_FINISHED:
					case PackageMode.DOWNLOADED:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);

						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

						if (current_page != 3) {
							tv_download_status_bottom_game_detail.setText(R.string.label_install);
						} else {
							tv_download_status_bottom_game_detail.setText(R.string.label_comment_after_install);
						}
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_install_bottom_game_detail);

						tv_download_percent_bottom_game_detail.setVisibility(View.GONE);
						break;

					case PackageMode.MERGED:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);

						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));

						if (current_page != 3) {
							tv_download_status_bottom_game_detail.setText(R.string.label_install);
						} else {
							tv_download_status_bottom_game_detail.setText(R.string.label_comment_after_install);
						}
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_install_bottom_game_detail);
						break;

					case PackageMode.MERGE_FAILED:
						if (gameInfo.download_status.mergeFailedCount >= 2) {
							if (ConnectManager.isNetworkConnected(GameDetailsBaseActivity.this)) {
								if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
									// Toast.makeText(context, "请检查您的SD卡",
									// 1).show();
									CustomToast.showToast(GameDetailsBaseActivity.this, "请检查您的SD卡");
									break;
								}
								if (ConnectManager.isWifi(GameDetailsBaseActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {

									// DownloadUtil.restartDownload(context,
									// download_info.getDownloadId());
									PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

										@Override
										public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
											// TODO
											// Auto-generated
											// method stub

										}

										@Override
										public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {

										}

										@Override
										public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
											// TODO called in
											// sub thread
											if (!successful) {
												Message msg = new Message();
												msg.what = DOWNLOAD_NOTIFY;
												msg.obj = gameInfo;
												msg.arg1 = reason;
												mHandler.sendMessage(msg);
											}

										}
									});

									DownloadStatistics.addDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName(), false);

								} else {
									wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
									// continueOrRetry_map.put(gameInfo.getDownloadurl(),
									// download_info);
									DuokuDialog.showNetworkAlertDialog(GameDetailsBaseActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
								}

							} else {
								// Toast.makeText(context, "请检查您的网络连接",
								// 1).show();
								// CustomToast.showToast(GameDetailsBaseActivity.this,
								// "请检查您的网络连接");
							}
						} else {
							pbra_download_game_detail.setVisibility(View.GONE);
							pbra_download_game_detail_container.setVisibility(View.GONE);
							iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
							iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_retry_bottom_game_detail);
							ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
							tv_download_status_bottom_game_detail.setText(R.string.label_retry);
							tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

							tv_download_percent_bottom_game_detail.setVisibility(View.GONE);
						}
						break;
					case PackageMode.DOWNLOAD_FAILED:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_retry_bottom_game_detail);
						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						tv_download_status_bottom_game_detail.setText(R.string.label_retry);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

						tv_download_percent_bottom_game_detail.setVisibility(View.GONE);
						break;

					case PackageMode.INSTALLED:
						pbra_download_game_detail.setVisibility(View.GONE);
						pbra_download_game_detail_container.setVisibility(View.GONE);

						ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
						if (current_page != 3) {
							iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
							iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_start_bottom_game_detail);
							tv_download_status_bottom_game_detail.setText(R.string.label_start);
						} else {
							iv_icon_download_bottom_game_detail.setVisibility(View.GONE);
							tv_download_status_bottom_game_detail.setText(R.string.label_comment);
						}
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
						break;

					case PackageMode.DOWNLOAD_PENDING:
						pbra_download_game_detail.setVisibility(View.VISIBLE);
						pbra_download_game_detail_container.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
						iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_pause_bottom_game_detail);
						ll_bt_download_game_detail.setBackgroundResource(0);
						tv_download_status_bottom_game_detail.setText(R.string.label_waiting);
						tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
						tv_download_percent_bottom_game_detail.setVisibility(View.VISIBLE);
						tv_download_percent_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
						// if (!gameInfo.download_status.isDiffDownload)
						// {
						// pbra_download_game_detail.setSecondaryProgress((int)
						// ((gameInfo.download_status.currentSize * 1.0f /
						// gameInfo.download_status.totalSize) * 100));
						// tv_download_percent_bottom_game_detail.setText((int)
						// ((gameInfo.download_status.currentSize * 1.0f /
						// gameInfo.download_status.totalSize) * 100) +
						// "%");
						// }
						// else
						// {
						// refreshDiffProgress();
						// }
						if (TextUtils.isEmpty(tv_download_percent_bottom_game_detail.getText().toString())) {
							pbra_download_game_detail.setSecondaryProgress(0);
							tv_download_percent_bottom_game_detail.setText("0%");
						}
						break;

					}

				}
			}
			super.handleMessage(msg);
		}
	};
	protected void refreshDiffProgress() {
		long current = gameInfo.download_status.currentSize;
		long total = gameInfo.download_status.totalSize;
		long pkgsize = Long.valueOf(gameInfo.getSize()).longValue();
		int secondary = (int) ((pkgsize - total) * 1.0f / pkgsize * 100);
		int first = (int) ((pkgsize - total + current) * 1.0f / pkgsize * 100);

		Log.d("DOWNLOAD_RUNNING", "name: " + gameInfo.getGameName());
		Log.d("DOWNLOAD_RUNNING", "current: " + current);
		Log.d("DOWNLOAD_RUNNING", "total--: " + total);
		Log.d("DOWNLOAD_RUNNING", "pkgsize: " + pkgsize);
		Log.d("DOWNLOAD_RUNNING", "secondary: " + secondary);
		Log.d("DOWNLOAD_RUNNING", "first----: " + first);

		if (total <= 0) {
			pbra_download_game_detail.setProgress(0);
			pbra_download_game_detail.setSecondaryProgress(0);
			tv_download_percent_bottom_game_detail.setText("0%");
		} else {
			pbra_download_game_detail.setProgress(first);
			pbra_download_game_detail.setSecondaryProgress(secondary);

			tv_download_percent_bottom_game_detail.setText((int) (current * 1.0f / total * 100) + "%");
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && REQUEST_DOWNLOAD_IN_WAP_NETWORK == requestCode) {
			String downloadurl = data.getStringExtra(DownloadDialogActivity.ARG2);
			final GameInfo gameInfo = wait2download_map.remove(downloadurl);
			if (gameInfo != null) {
				switch (gameInfo.download_status.status) {
				case PackageMode.UNDOWNLOAD:
					DownloadItemInput dInfo = new DownloadItemInput();
					dInfo.setGameId(gameInfo.getGameId());
					// dInfo.setSaveName(Md5Tools.toMd5(gameInfo.getDownloadurl().getBytes(),
					// true));
					dInfo.setDownloadUrl(gameInfo.getDownloadurl());
					dInfo.setDisplayName(gameInfo.getGameName());
					dInfo.setPackageName(gameInfo.getPkgname());
					dInfo.setIconUrl(gameInfo.getIconUrl());
					dInfo.setAction(gameInfo.getStartaction());
					dInfo.setNeedLogin(gameInfo.isNeedlogin());
					dInfo.setVersion(gameInfo.getGameversion());
					dInfo.setVersionInt(gameInfo.getGameversioncode());
					try {
						dInfo.setSize(Long.parseLong(gameInfo.getSize()));
					} catch (NumberFormatException e) {
						dInfo.setSize(0);
					}

					PackageHelper.download(dInfo, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
							// TODO called in sub thread
							if (!status) {
								Message msg = new Message();
								msg.what = DOWNLOAD_NOTIFY;
								msg.arg1 = reason;
								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

						}
					});

					DownloadStatistics.addDownloadGameInDetailViewStatistics(getApplicationContext(), gameInfo.getGameName());
					break;
				case PackageMode.UPDATABLE_DIFF:
					DownloadItemInput dInfo2 = new DownloadItemInput();
					dInfo2.setGameId(gameInfo.download_status.gameId);
					// dInfo.setSaveName(Md5Tools.toMd5(gameInfo.getDownloadurl().getBytes(),
					// true));
					dInfo2.setDownloadUrl(gameInfo.download_status.downloadUrl);
					dInfo2.setDisplayName(gameInfo.getGameName());
					dInfo2.setPackageName(gameInfo.download_status.packageName);
					dInfo2.setIconUrl(gameInfo.getIconUrl());
					dInfo2.setAction(gameInfo.getStartaction());
					dInfo2.setNeedLogin(gameInfo.isNeedlogin());
					dInfo2.setVersion(gameInfo.download_status.version);
					dInfo2.setVersionInt(gameInfo.download_status.versionCode);

					dInfo2.setSize(gameInfo.download_status.pacthSize);

					// Log.i("whb",
					// "download game name:"+gameInfo.getGameName());
					// long id =
					// AppManager.getInstance(context).downloadGame(dInfo);
					PackageHelper.download(dInfo2, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
							// TODO called in sub thread
							if (!status) {
								Message msg = new Message();
								msg.what = DOWNLOAD_NOTIFY;
								msg.arg1 = reason;
								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

						}
					});
					// Log.i("whb", "dddddd id;"+id);

					/*
					 * Intent d_intent = new
					 * Intent("com.duoku.action.download.begin");
					 * d_intent.putExtra("pkgname", gameInfo.getPkgname());
					 * context.sendBroadcast(d_intent);
					 */

					DownloadStatistics.addDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName(), true);

					AppManager manager = AppManager.getInstance(GameDetailsBaseActivity.this);
					manager.updateIgnoreState(false, gameInfo.getPkgname());

					break;
				case PackageMode.DOWNLOAD_PAUSED:
					// DownloadItemOutput download_info =
					// continueOrRetry_map.remove(downloadurl);
					// if(download_info != null){
					// DownloadUtil.resumeDownload(GameDetailsBaseActivity.this,
					// download_info.getDownloadId());
					PackageHelper.resumeDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
							// TODO called in sub thread
							if (!successful) {
								Message msg = new Message();
								msg.what = DOWNLOAD_NOTIFY;
								msg.arg1 = reason;
								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {

						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

						}
					});
					DownloadStatistics.addResumeDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName());
					// }
					break;
				case PackageMode.DOWNLOAD_FAILED:
					// DownloadItemOutput download_info2 =
					// continueOrRetry_map.remove(downloadurl);
					// if(download_info2 != null){
					// Log.i("whb",
					// "re dddddddd id;"+download_info2.getDownloadId());
					/*
					 * DownloadUtil.restartDownload(GameDetailsBaseActivity.this,
					 * download_info2.getDownloadId());
					 * 
					 * Intent d_intent2 = new
					 * Intent("com.duoku.action.download.begin");
					 * d_intent2.putExtra("pkgname", gameInfo.getPkgname());
					 * d_intent2.putExtra("gameid", gameInfo.getGameId());
					 * d_intent2.putExtra("downloadid",
					 * download_info2.getDownloadId());
					 * d_intent2.putExtra("versionname",
					 * gameInfo.getGameversion());
					 * d_intent2.putExtra("versioncode",
					 * gameInfo.getGameversioncode());
					 * GameDetailsBaseActivity.this.sendBroadcast(d_intent2);
					 */

					PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {

						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
							// TODO called in sub thread
							if (!successful) {
								Message msg = new Message();
								msg.what = DOWNLOAD_NOTIFY;
								msg.arg1 = reason;
								mHandler.sendMessage(msg);
							}

						}
					});

					DownloadStatistics.addDownloadGameInDetailViewStatistics(getApplicationContext(), gameInfo.getGameName());
					// }
					break;

				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	protected void checkDownloadBtnState() {

		if (gameInfo != null && gameInfo.download_status != null) {
			switch (gameInfo.download_status.status) {

			case PackageMode.UNDOWNLOAD:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_download_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				tv_download_status_bottom_game_detail.setText(current_page == 3 ? R.string.label_comment_after_download : R.string.label_download);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
				break;
			case PackageMode.UPDATABLE:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_update_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				tv_download_status_bottom_game_detail.setText(R.string.label_update);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
				break;
			case PackageMode.UPDATABLE_DIFF:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_update_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				tv_download_status_bottom_game_detail.setText(R.string.label_diff_update);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
				break;

			case PackageMode.INSTALLING:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.a_installing_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				tv_download_status_bottom_game_detail.setText(R.string.label_installing);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
				AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon_download_bottom_game_detail.getDrawable();
				animationDrawable.start();
				break;

			case PackageMode.DOWNLOAD_RUNNING:
				pbra_download_game_detail.setVisibility(View.VISIBLE);
				pbra_download_game_detail_container.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_pause_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(0);
				tv_download_status_bottom_game_detail.setText(R.string.label_pause);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
				tv_download_percent_bottom_game_detail.setVisibility(View.VISIBLE);
				tv_download_percent_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));

				if (!gameInfo.download_status.isDiffDownload) {
					pbra_download_game_detail.setSecondaryProgress((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100));
					tv_download_percent_bottom_game_detail.setText((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100) + "%");
				} else {
					refreshDiffProgress();
				}
				break;

			case PackageMode.DOWNLOAD_PAUSED:
				pbra_download_game_detail.setVisibility(View.VISIBLE);
				pbra_download_game_detail_container.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_continue_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(0);
				tv_download_status_bottom_game_detail.setText(R.string.label_continue);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
				tv_download_percent_bottom_game_detail.setVisibility(View.VISIBLE);

				if (!gameInfo.download_status.isDiffDownload) {
					pbra_download_game_detail.setSecondaryProgress((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100));

					tv_download_percent_bottom_game_detail.setText((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100) + "%");
				} else {
					refreshDiffProgress();
				}
				break;
			case PackageMode.CHECKING:
			case PackageMode.MERGING:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

				tv_download_status_bottom_game_detail.setText(R.string.label_checking_diff_update);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_download_bottom_game_detail);

				break;
			case PackageMode.CHECKING_FINISHED:
			case PackageMode.DOWNLOADED:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);

				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

				tv_download_status_bottom_game_detail.setText(current_page == 3 ? R.string.label_comment_after_install : R.string.label_install);

				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_install_bottom_game_detail);

				break;

			case PackageMode.MERGED:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);

				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

				tv_download_status_bottom_game_detail.setText(current_page == 3 ? R.string.label_comment_after_install : R.string.label_install);

				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_install_bottom_game_detail);
				break;

			case PackageMode.MERGE_FAILED:
			case PackageMode.DOWNLOAD_FAILED:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_retry_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				tv_download_status_bottom_game_detail.setText(R.string.label_retry);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
				break;

			case PackageMode.INSTALLED:
				pbra_download_game_detail.setVisibility(View.GONE);
				pbra_download_game_detail_container.setVisibility(View.GONE);

				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);
				if (current_page != 3) {
					iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
					iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_start_bottom_game_detail);
					tv_download_status_bottom_game_detail.setText(R.string.label_start);
				} else {
					iv_icon_download_bottom_game_detail.setVisibility(View.GONE);
					tv_download_status_bottom_game_detail.setText(R.string.label_comment);
				}
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));
				break;

			case PackageMode.DOWNLOAD_PENDING:
				pbra_download_game_detail.setVisibility(View.VISIBLE);
				pbra_download_game_detail_container.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_pause_bottom_game_detail);
				ll_bt_download_game_detail.setBackgroundResource(0);
				tv_download_status_bottom_game_detail.setText(R.string.label_waiting);
				tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));
				tv_download_percent_bottom_game_detail.setVisibility(View.VISIBLE);
				tv_download_percent_bottom_game_detail.setTextColor(Color.parseColor("#4D4D4D"));

				if (TextUtils.isEmpty(tv_download_percent_bottom_game_detail.getText().toString())) {
					if (!gameInfo.download_status.isDiffDownload) {
						pbra_download_game_detail.setSecondaryProgress((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100));

						tv_download_percent_bottom_game_detail.setText((int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalSize) * 100) + "%");
					} else {
						refreshDiffProgress();
					}
				}
				break;
			default:
				if (Constants.DEBUG) {
					// Log.d("DETAILINFO", "updateGameInfo()  -> default: "
					// +
					// gameInfo.download_status.status);
				}
				break;
			}
		} else {
			if (Constants.DEBUG) {
				// Log.d("DETAILINFO", "updateGameInfo() --> gameInfo: " +
				// gameInfo);
				// Log.d("DETAILINFO", "updateGameInfo() --> download_status: "
				// + gameInfo.download_status.status);
			}
		}
		ll_bt_download_game_detail.setOnClickListener(mOptClickListener);
	}
	private long checkDownloadId() {
		AppManager aManager = AppManager.getInstance(GameDetailsBaseActivity.this.getApplicationContext());
		DownloadAppInfo dai = aManager.getDownloadGameForId(gameInfo.getGameId(), false);

		if (null == dai) {
			return 0;
		}

		long donwloadid = dai.getDownloadId();

		return donwloadid;
	}
	private OnClickListener mOptClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (gameInfo == null || gameInfo.download_status == null) {
				return;
			}

			if (current_page != 3 || gameInfo.download_status.status != PackageMode.INSTALLED) {
				switch (gameInfo.download_status.status) {
				case PackageMode.UNDOWNLOAD:
				case PackageMode.UPDATABLE:
					if (ConnectManager.isNetworkConnected(GameDetailsBaseActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(GameDetailsBaseActivity.this, "请检查您的SD卡");
							return;
						}

						if (gameInfo.getDownloadurl() == null || "".equals(gameInfo.getDownloadurl())) {
							CustomToast.showToast(GameDetailsBaseActivity.this, "无下载地址");
							return;
						}

						if (ApkUtil.shouldCheckRootUserDownload()) {
							rootDialog = DialogFactory.createCheckRootDownDialog(GameDetailsBaseActivity.this);
							rootDialog.show();
						}
						if (ConnectManager.isWifi(GameDetailsBaseActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// 直接下载
							DownloadItemInput dInfo = new DownloadItemInput();
							dInfo.setGameId(gameInfo.getGameId());
							dInfo.setDownloadUrl(gameInfo.getDownloadurl());
							dInfo.setDisplayName(gameInfo.getGameName());
							dInfo.setPackageName(gameInfo.getPkgname());
							dInfo.setIconUrl(gameInfo.getIconUrl());
							dInfo.setAction(gameInfo.getStartaction());
							dInfo.setNeedLogin(gameInfo.isNeedlogin());
							dInfo.setVersion(gameInfo.getGameversion());
							dInfo.setVersionInt(gameInfo.getGameversioncode());
							try {
								dInfo.setSize(Long.parseLong(gameInfo.getSize()));
							} catch (NumberFormatException e) {
								dInfo.setSize(0);
							}

							PackageHelper.download(dInfo, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
									if (!status) {
										Message msg = new Message();
										msg.what = DOWNLOAD_NOTIFY;
										mHandler.sendMessage(msg);
									}
								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
								}
							});

							AppManager manager = AppManager.getInstance(GameDetailsBaseActivity.this);
							manager.updateIgnoreState(false, gameInfo.getPkgname());

							DownloadStatistics.addDownloadGameInDetailViewStatistics(getApplicationContext(), gameInfo.getGameName());
						} else {
							// 非wifi下需要用户确认是否下载
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(GameDetailsBaseActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(GameDetailsBaseActivity.this,
						// "请检查您的网络连接");
					}

					break;

				case PackageMode.UPDATABLE_DIFF:
					if (ConnectManager.isNetworkConnected(GameDetailsBaseActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(GameDetailsBaseActivity.this, "请检查您的SD卡");
							return;
						}

						if (gameInfo.getDownloadurl() == null || "".equals(gameInfo.getDownloadurl())) {
							CustomToast.showToast(GameDetailsBaseActivity.this, "无下载地址");
							return;
						}
						if (ConnectManager.isWifi(GameDetailsBaseActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// 直接下载
							DownloadItemInput dInfo = new DownloadItemInput();
							dInfo.setGameId(gameInfo.download_status.gameId);
							dInfo.setDownloadUrl(gameInfo.download_status.downloadUrl);
							dInfo.setDisplayName(gameInfo.getGameName());
							dInfo.setPackageName(gameInfo.download_status.packageName);
							dInfo.setIconUrl(gameInfo.getIconUrl());
							dInfo.setAction(gameInfo.getStartaction());
							dInfo.setDiffDownload(true);
							dInfo.setNeedLogin(gameInfo.isNeedlogin());
							dInfo.setVersion(gameInfo.download_status.version);
							dInfo.setVersionInt(gameInfo.download_status.versionCode);
							dInfo.setSize(gameInfo.download_status.pacthSize);

							PackageHelper.download(dInfo, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
									// TODO called in sub thread
									if (!status) {
										Message msg = new Message();
										msg.what = DOWNLOAD_NOTIFY;
										msg.arg1 = reason;
										mHandler.sendMessage(msg);
									}

								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
								}
							});

							AppManager manager = AppManager.getInstance(GameDetailsBaseActivity.this);
							manager.updateIgnoreState(false, gameInfo.getPkgname());

							DownloadStatistics.addDownloadGameInDetailViewStatistics(getApplicationContext(), gameInfo.getGameName());
						} else {
							// 非wifi下需要用户确认是否下载
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(GameDetailsBaseActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(GameDetailsBaseActivity.this,
						// "请检查您的网络连接");
					}
					break;

				case PackageMode.DOWNLOAD_PENDING:
				case PackageMode.DOWNLOAD_RUNNING:
					// if(download_info != null){
					// DownloadUtil.pauseDownload(GameDetailsBaseActivity.this,
					// download_info.getDownloadId());
					long donwloadid = checkDownloadId();

					if (donwloadid > 0) {
						PackageHelper.pauseDownloadGames(donwloadid);
					} else {
						PackageHelper.pauseDownloadGames(gameInfo.getDownloadurl());
					}
					// PackageHelper.pauseDownloadGames(gameInfo.getDownloadurl());
					DownloadStatistics.addUserPauseDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName());

					break;

				case PackageMode.DOWNLOAD_PAUSED:
					if (ConnectManager.isNetworkConnected(GameDetailsBaseActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							// Toast.makeText(GameDetailsBaseActivity.this,
							// "请检查您的SD卡", 1).show();
							CustomToast.showToast(GameDetailsBaseActivity.this, "请检查您的SD卡");
							return;
						}
						if (ConnectManager.isWifi(GameDetailsBaseActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// if(download_info != null){
							// DownloadUtil.resumeDownload(GameDetailsBaseActivity.this,
							// download_info.getDownloadId());

							PackageHelper.resumeDownload(checkDownloadId(), new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
									// TODO called in sub thread
									if (!successful) {
										Message msg = new Message();
										msg.what = DOWNLOAD_NOTIFY;
										msg.arg1 = reason;
										mHandler.sendMessage(msg);
									}

								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {

								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
									// TODO Auto-generated method stub

								}
							});

							DownloadStatistics.addResumeDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName());
							// }
						} else {
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							// continueOrRetry_map.put(gameInfo.getDownloadurl(),
							// download_info);
							DuokuDialog.showNetworkAlertDialog(GameDetailsBaseActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}
					} else {
						// Toast.makeText(GameDetailsBaseActivity.this,
						// "请检查您的网络连接", 1).show();
						// CustomToast.showToast(GameDetailsBaseActivity.this,
						// "请检查您的网络连接");
					}

					break;

				case PackageMode.CHECKING_FINISHED:
				case PackageMode.DOWNLOADED:
					/*
					 * Intent intent = new Intent();
					 * intent.setAction(android.content.Intent.ACTION_VIEW);
					 * intent.setDataAndType(Uri.parse("file://" +
					 * gameInfo.getApkpath()),
					 * "application/vnd.android.package-archive");
					 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * GameDetailsBaseActivity.this.startActivity(intent);
					 */
					if (!gameInfo.download_status.isDiffDownload) {
						PackageHelper.installApp(GameDetailsBaseActivity.this, gameInfo.getGameId(), gameInfo.getPkgname(), gameInfo.download_status.downloadDest);
					} else {
						PackageHelper.sendMergeRequestFromUI(gameInfo.download_status.downloadId);
					}
					break;
				case PackageMode.MERGED:
					PackageHelper.installApp(GameDetailsBaseActivity.this, gameInfo.getGameId(), gameInfo.getPkgname(), gameInfo.download_status.downloadDest);
					break;
				case PackageMode.MERGE_FAILED:
					if (gameInfo.download_status.mergeFailedCount >= 2) {
						if (ConnectManager.isNetworkConnected(GameDetailsBaseActivity.this)) {
							if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
								// Toast.makeText(GameDetailsBaseActivity.this,
								// "请检查您的SD卡", 1).show();
								CustomToast.showToast(GameDetailsBaseActivity.this, "请检查您的SD卡");
								return;
							}
							if (ConnectManager.isWifi(GameDetailsBaseActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
								// if(download_info != null){
								// Log.i("whb",
								// "re dddddddd id;"+download_info.getDownloadId());
								PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

									@Override
									public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
										// TODO Auto-generated method
										// stub

									}

									@Override
									public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {

									}

									@Override
									public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
										// TODO called in sub thread
										if (!successful) {
											Message msg = new Message();
											msg.what = DOWNLOAD_NOTIFY;
											msg.arg1 = reason;
											mHandler.sendMessage(msg);
										}

									}
								});

								DownloadStatistics.addDownloadGameInDetailViewStatistics(getApplicationContext(), gameInfo.getGameName());
								// }
							} else {
								wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
								// continueOrRetry_map.put(gameInfo.getDownloadurl(),
								// download_info);
								DuokuDialog.showNetworkAlertDialog(GameDetailsBaseActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
							}
						} else {
							// Toast.makeText(GameDetailsBaseActivity.this,
							// "请检查您的网络连接", 1).show();
							// CustomToast.showToast(GameDetailsBaseActivity.this,
							// "请检查您的网络连接");
						}
					} else {
						PackageHelper.sendMergeRequestFromUI(gameInfo.download_status.downloadId);
					}
					break;

				case PackageMode.DOWNLOAD_FAILED:
					if (ConnectManager.isNetworkConnected(GameDetailsBaseActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							// Toast.makeText(GameDetailsBaseActivity.this,
							// "请检查您的SD卡", 1).show();
							CustomToast.showToast(GameDetailsBaseActivity.this, "请检查您的SD卡");
							return;
						}
						if (ConnectManager.isWifi(GameDetailsBaseActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// if(download_info != null){
							// Log.i("whb",
							// "re dddddddd id;"+download_info.getDownloadId());
							PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {

								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
									// TODO called in sub thread
									if (!successful) {
										Message msg = new Message();
										msg.what = DOWNLOAD_NOTIFY;
										msg.arg1 = reason;
										mHandler.sendMessage(msg);
									}

								}
							});

							DownloadStatistics.addDownloadGameInDetailViewStatistics(getApplicationContext(), gameInfo.getGameName());
							// }
						} else {
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							// continueOrRetry_map.put(gameInfo.getDownloadurl(),
							// download_info);
							DuokuDialog.showNetworkAlertDialog(GameDetailsBaseActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}
					} else {
						// Toast.makeText(GameDetailsBaseActivity.this,
						// "请检查您的网络连接", 1).show();
						// CustomToast.showToast(GameDetailsBaseActivity.this,
						// "请检查您的网络连接");
					}

					break;

				case PackageMode.INSTALLED:
					/*
					 * Intent in_game =
					 * getPackageManager().getLaunchIntentForPackage
					 * (gameInfo.getPkgname());
					 * GameDetailsBaseActivity.this.startActivity(in_game);
					 */
					new StartGame(GameDetailsBaseActivity.this, gameInfo.getPkgname(), gameInfo.getStartaction(), gameInfo.getGameId(), gameInfo.isNeedlogin()).startGame();
					break;
				default:
					break;

				}
			} else {
				// TODO 发表评论
				if (gameInfo != null) {
					boolean is_login = MineProfile.getInstance().getIsLogin();
					if (is_login) {
						Intent in = new Intent(GameDetailsBaseActivity.this, PublishCommentActivity.class);
						in.putExtra("gameid", gameInfo.getGameId());
						in.putExtra("gamename", gameInfo.getGameName());
						in.putExtra("userid", MineProfile.getInstance().getUserID());
						in.putExtra("sessionid", MineProfile.getInstance().getSessionID());
						GameDetailsBaseActivity.this.startActivity(in);
					} else {
						MineProfile.getInstance().setIsLogin(false);
						Intent login_in = new Intent(GameDetailsBaseActivity.this, SapiLoginActivity.class);
						startActivity(login_in);
						// Toast.makeText(GameDetailsBaseActivity.this, "请登录后再评论",
						// 1).show();
						CustomToast.showToast(GameDetailsBaseActivity.this, "请登录后再评论");
					}
				}
			}

		}
	};
	protected void onDestroy() {
		System.gc();
		if (rootDialog != null) {
			rootDialog.dismiss();
			rootDialog = null;
		}
		super.onDestroy();
	};
}
