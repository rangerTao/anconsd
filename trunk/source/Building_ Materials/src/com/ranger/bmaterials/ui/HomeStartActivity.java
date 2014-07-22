package com.ranger.bmaterials.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.HomeDailyResult;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.ui.gametopic.BMProductDetailActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.enumeration.StatusLoading;
import com.ranger.bmaterials.view.NetImageView;

public class HomeStartActivity extends Activity {
	private ImageView iv_background;
	private View iv_start_close;
	private GridView gv_start_games;
	private GameAdapter adapter;
	private RelativeLayout rl_title_area;
	// 0(图片文字显示)，1(金蛋宝箱等抽奖)，2(游戏推荐)
	private String type = "0";
	private LinearLayout ll_start_content_area;
	private RelativeLayout ll_start_games_content_area;
	private View iv_games_install;
	private ArrayList<GameInfo> gameInfos = new ArrayList<GameInfo>();
	private TextView tv_start_title;
	private TextView tv_games_content;
	private TextView tv_active_content;

	private LinearLayout ll_loading_progress;
	private View mViewLoading;
	private View mViewLoadingFailed;
	private View mViewLoadingOngoing;
	private DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.bg_home_start_text_content);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_start);
		iv_background = (ImageView) findViewById(R.id.iv_background);
		tv_start_title = (TextView) findViewById(R.id.tv_start_title);
		tv_games_content = (TextView) findViewById(R.id.tv_games_content);
		tv_active_content = (TextView) findViewById(R.id.tv_active_content);
		iv_start_close = (ImageView) findViewById(R.id.iv_start_close);
		gv_start_games = (GridView) findViewById(R.id.gv_start_games);
		rl_title_area = (RelativeLayout) findViewById(R.id.rl_title_area);
		ll_start_content_area = (LinearLayout) findViewById(R.id.ll_start_content_area);
		ll_start_games_content_area = (RelativeLayout) findViewById(R.id.ll_start_games_content_area);
		iv_games_install = (View) findViewById(R.id.iv_games_install);

		ll_loading_progress = (LinearLayout) findViewById(R.id.ll_loading_progress);
		mViewLoading = findViewById(R.id.loading);
		mViewLoadingFailed = mViewLoading.findViewById(R.id.loading_error_layout);
		mViewLoadingOngoing = mViewLoading.findViewById(R.id.network_loading_pb);
		mViewLoadingFailed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ConnectManager.isNetworkConnected(HomeStartActivity.this)) {
					request1500();
				} else {
					refreshLoadingStatus(StatusLoading.FAILED);
				}
			}
		});
		request1500();
		iv_start_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		iv_games_install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 下载任务
				startDownload();
			}
		});
		SharedPreferences sPreferences = getSharedPreferences("startdata", 0);
		Editor editor = sPreferences.edit();
		int occuredcount = sPreferences.getInt("occuredcount", 0);
		editor.putInt("occuredcount", occuredcount + 1);
		editor.putLong("occuredtime", System.currentTimeMillis());
		editor.commit();

		gv_start_games.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GameInfo gameInfo = gameInfos.get(position);
				Intent intent = new Intent(HomeStartActivity.this, GameDetailsActivity.class);
				intent.putExtra(GameDetailConstants.KEY_GAME_NAME, gameInfo.getGameName());
				intent.putExtra(GameDetailConstants.KEY_GAME_ID, gameInfo.getGameId());
				intent.putExtra(GameDetailConstants.KEY_GAME_VERSION_CODE, gameInfo.getGameversioncode());
				intent.putExtra(GameDetailConstants.KEY_GAME_VERSION_NAME, gameInfo.getGameversion());
				startActivity(intent);
				finish();
			}
		});
	}

	private void request1500() {
		if (!ConnectManager.isNetworkConnected(HomeStartActivity.this)) {
			refreshLoadingStatus(StatusLoading.FAILED);
			return;
		}
		refreshLoadingStatus(StatusLoading.LOADING);
		new Thread(new Runnable() {

			@Override
			public void run() {
				NetUtil.getInstance().requestHomeStartDialog(new IRequestListener() {

					@Override
					public void onRequestSuccess(BaseResult responseData) {
						final HomeDailyResult startResult = (HomeDailyResult) responseData;
						// gameInfos = startResult.getDailyGameInfos();
						gameInfos.addAll(startResult.getDailyGameInfos());
						String occNumber = startResult.getOccnumber();
						String interval = startResult.getInterval();
						String dialogtype = startResult.getDialogtype();
						// String title = startResult.getTitle();
						final String picurl = startResult.getPicurl();

						// String content = startResult.getContent();
						final String skiptype = startResult.getSkiptype();
						// 每次弹出的时候记录一下弹出的时间，下次要弹出的时候拿当前时间和上次时间比对，如果超出时间间隔了才能弹出
						// 弹出次数逻辑同上
						SharedPreferences sPreferences = getSharedPreferences("startdata", 0);
						Editor editor = sPreferences.edit();
						editor.putString("occNumber", occNumber);
						editor.putString("interval", interval);
						editor.commit();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								type = startResult.getDialogtype();
								if (type.equals("0")) {
									if (!TextUtils.isEmpty(picurl)) {
										ImageLoaderHelper.displayImage(picurl, iv_background, options);
									}
									gv_start_games.setVisibility(View.INVISIBLE);
									ll_start_content_area.setVisibility(View.VISIBLE);
									iv_games_install.setVisibility(View.GONE);
									ll_start_games_content_area.setVisibility(View.GONE);
									tv_active_content.setText(startResult.getContent());
									View contentView = (View) findViewById(R.id.rl_title_area);
									contentView.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											if (skiptype.equals("0")) {
												// 跳转至专题
												Intent intentTopic = new Intent(HomeStartActivity.this, BMProductDetailActivity.class);
												intentTopic.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intentTopic);
											} else {
												// 跳转至活动详情
												Intent intent = new Intent(HomeStartActivity.this, ActivityDetailActivity.class);
												ActivityInfo info = new ActivityInfo();
												info.setGameId(startResult.getGameid());
												info.setId(startResult.getActid());
												intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, info);
												startActivity(intent);
												finish();
											}
										}
									});
								} else if (type.equals("2")) {
									rl_title_area.setBackgroundColor(0xff448bdd);
									ll_start_content_area.setVisibility(View.GONE);
									gv_start_games.setVisibility(View.VISIBLE);
									iv_games_install.setVisibility(View.VISIBLE);
									ll_start_games_content_area.setVisibility(View.VISIBLE);
									tv_games_content.setText(startResult.getContent());
									adapter = new GameAdapter(HomeStartActivity.this, startResult.getDailyGameInfos());
									gv_start_games.setAdapter(adapter);
								}
								tv_start_title.setText(startResult.getTitle());
								ll_loading_progress.setVisibility(View.GONE);
								refreshLoadingStatus(StatusLoading.SUCCEED);
							}
						});
					}

					@Override
					public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								ll_loading_progress.setVisibility(View.GONE);
								refreshLoadingStatus(StatusLoading.FAILED);
							}
						});
					}
				});
			}
		}).start();

	}

	private void refreshLoadingStatus(StatusLoading status) {
		switch (status) {
		case INVALID: {
			// RESERVED
		}
			break;

		case LOADING: {
			mViewLoading.setVisibility(View.VISIBLE);
			mViewLoadingOngoing.setVisibility(View.VISIBLE);
			mViewLoadingFailed.setVisibility(View.GONE);

			mViewLoading.setClickable(false);
		}
			break;

		case SUCCEED: {
			mViewLoading.setVisibility(View.GONE);

			mViewLoading.setClickable(false);
		}
			break;

		case FAILED: {
			mViewLoading.setVisibility(View.VISIBLE);
			mViewLoadingOngoing.setVisibility(View.GONE);
			mViewLoadingFailed.setVisibility(View.VISIBLE);

			mViewLoading.setClickable(true);
		}
			break;

		case NONE: {
			mViewLoading.setVisibility(View.GONE);

			mViewLoading.setClickable(false);
		}
			break;

		default:
			break;
		}
	}

	private void startDownload() {
		if (gameInfos == null || gameInfos.size() == 0) {
			return;
		}
		for (int i = 0; i < gameInfos.size(); i++) {
			GameInfo gameInfo = gameInfos.get(i);
			DownloadItemInput input = new DownloadItemInput();
			input.setGameId(gameInfo.getGameId());
			// dInfo.setSaveName(Md5Tools.toMd5(gameInfo.getDownloadurl().getBytes(),
			// true));
			input.setDownloadUrl(gameInfo.getDownloadurl());
			input.setDisplayName(gameInfo.getGameName());
			input.setPackageName(gameInfo.getPkgname());
			input.setIconUrl(gameInfo.getIconUrl());
			input.setAction(gameInfo.getStartaction());
			input.setNeedLogin(gameInfo.isNeedlogin());
			input.setVersion(gameInfo.getGameversion());
			input.setVersionInt(gameInfo.getGameversioncode());

			input.setSize(Long.valueOf(gameInfo.getSize()));
			PackageHelper.download(input, new DownloadCallback() {

				@Override
				public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {

				}

				@Override
				public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
					if (!status) {
						// Message msg = new Message();
						// msg.what = DOWNLOAD_NOTIFY;
						// msg.arg1 = reason;
						// mHandler.sendMessage(msg);
					}

				}

				@Override
				public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
				}
			});
		}
		finish();
	}

	private class GameAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context context;
		private ArrayList<GameInfo> gameInfos;
		private DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.game_icon_games_default);

		public GameAdapter(Context context, ArrayList<GameInfo> gameInfos) {
			this.gameInfos = gameInfos;
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();

				convertView = mInflater.inflate(R.layout.item_home_start_games, null);

				holder.iv_rec_games_icon = (NetImageView) convertView.findViewById(R.id.iv_rec_games_icon);
				holder.tv_rec_games_name = (TextView) convertView.findViewById(R.id.tv_rec_games_name);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (gameInfos != null) {
				GameInfo gameInfo = gameInfos.get(position);
				ImageLoaderHelper.displayImage(gameInfo.getIconUrl(), holder.iv_rec_games_icon, options);
				holder.tv_rec_games_name.setText(gameInfo.getGameName());
			}
			return convertView;
		}

		class Holder {
			NetImageView iv_rec_games_icon;
			TextView tv_rec_games_name;
		}

	}
}
