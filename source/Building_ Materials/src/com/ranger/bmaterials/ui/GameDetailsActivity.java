package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.adapter.GameDetailSummaryAdapter;
import com.ranger.bmaterials.adapter.HomeGuideViewPagerAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.GameCommentInfo;
import com.ranger.bmaterials.mode.GameGuideInfo;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameDetailCommentResult;
import com.ranger.bmaterials.netresponse.GameDetailGuideResult;
import com.ranger.bmaterials.netresponse.GameDetailSummaryResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.enumeration.StatusLoading;
import com.ranger.bmaterials.view.AdsPoints;
import com.ranger.bmaterials.view.GameDetailWorkspace;
import com.ranger.bmaterials.view.ImageViewForList;
import com.ranger.bmaterials.view.IndicatorWorkspace;
import com.ranger.bmaterials.view.MyScrollView;
import com.ranger.bmaterials.view.SimpleListView;
import com.ranger.bmaterials.view.SlowScrollViewpager;
import com.ranger.bmaterials.view.SimpleListView.BindViewCallBack;
import com.ranger.bmaterials.view.SimpleListView.SimpleHolder;
import com.ranger.bmaterials.view.SimpleListView.loadMoreDataCallBack;

public class GameDetailsActivity extends GameDetailsBaseActivity {
	private boolean openRecommendAndCloseThis; // 点击启动底部推荐游戏时是否需要关闭当前activity
	private View mViewNoGuide;
	private View mViewGuideGameNotFound;
	private GameDetailHolder mHolderGuide;
	private View mViewSummaryGameNotFound;
	private GameDetailHolder mHolderSummary;
	private View mViewSummaryLoading;
	private View mViewNoComment;
	private View mViewCommentGameNotFound;
	private GameDetailHolder mHolderComment;
	private View pic_game_detail_preview_container;// @author zhangxiaofeng
	private GridView gv_game_detail_summary;// recommend games
	private int displayWidth;
	private int mVisibleWidth;
	private int density;
	private GameDetailSummaryResult mGameDetailSummaryResult;
	private String gameid;
	private String pkgname;
	private String vername;
	private String vercode;
	private View mPage = null;
	private boolean auto_download;
	boolean isOpen = false;
	LinearLayout ll_open_description_game_detail_summary;
	TextView tv_open_description_game_detail_summary;
	ImageView iv_open_description_game_detail_summary;
	TextView tv_description_game_detail_summary;
	HashMap<String, PackageCallback> listeners = new HashMap<String, PackageCallback>();
	private boolean isDescpSpread;
	private boolean isNetConnecting;//当前网络连接状态
	private boolean isfromrec = false;

	/**
	 * @author liushuohui
	 */
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action) && (!isNetConnecting)) {
				ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				boolean refresh = false;

				if (null != netInfo && (netInfo.isConnected() || netInfo.isAvailable())) {
					refresh = true;
				}

				if (!refresh && null != wifiInfo && (wifiInfo.isConnected() || wifiInfo.isAvailable())) {
					refresh = true;
				}

				if (refresh) {
					if (null == gameInfo) {
						requestGameSummary();

						if (mGameDetailWorkspace.current_screen == 2) {
							requestGuidePage();
						} else if (mGameDetailWorkspace.current_screen == 3) {
							requestCommentPage();
						}
					}
				}
			}
			isNetConnecting=ConnectManager.isNetworkConnected(context);
		}
	};

	// private HeaderCoinAnimationTask headerCoinTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		GameDetailConstants.gamepicsmoving = false;
		isNetConnecting=ConnectManager.isNetworkConnected(this);
		measureScreen();
		initPage();
		// 展开
		ll_open_description_game_detail_summary = (LinearLayout) findViewById(R.id.ll_open_description_game_detail_summary);
		tv_open_description_game_detail_summary = (TextView) findViewById(R.id.tv_open_description_game_detail_summary);
		iv_open_description_game_detail_summary = (ImageView) findViewById(R.id.iv_open_description_game_detail_summary);
		// tv_description_game_detail_summary = (MyTextView2)
		// findViewById(R.id.tv_description_game_detail_summary);
		tv_description_game_detail_summary = (TextView) findViewById(R.id.tv_description_game_detail_summary_new);
		isDescpSpread = false;

		// 简介：START
		View viewParent = findViewById(R.id.view_game_summary);

		mViewSummaryGameNotFound = viewParent.findViewById(R.id.view_game_not_found);
		mViewSummaryLoading = viewParent.findViewById(R.id.loading);
		viewParent = viewParent.findViewById(R.id.view_loading);
		mHolderSummary = new GameDetailHolder(viewParent);
		mHolderSummary.setViewGameNotFound(mViewSummaryGameNotFound);
		// 简介：END

		// 攻略: START
		viewParent = findViewById(R.id.view_game_guide);

		mViewNoGuide = viewParent.findViewById(R.id.view_content_none);
		mViewGuideGameNotFound = viewParent.findViewById(R.id.view_game_not_found);

		viewParent = viewParent.findViewById(R.id.loading);
		mHolderGuide = new GameDetailHolder(viewParent);
		mHolderGuide.setViewContentNone(mViewNoGuide);
		mHolderGuide.setViewGameNotFound(mViewGuideGameNotFound);
		// 攻略: END

		// 评论: START
		viewParent = findViewById(R.id.view_game_comment);

		mViewNoComment = viewParent.findViewById(R.id.view_no_comment);
		mViewCommentGameNotFound = viewParent.findViewById(R.id.view_game_not_found);

		viewParent = viewParent.findViewById(R.id.loading);
		mHolderComment = new GameDetailHolder(viewParent);
		mHolderComment.setViewContentNone(mViewNoComment);
		mHolderComment.setViewGameNotFound(mViewCommentGameNotFound);
		// 评论: END

		View v_none_game_detail = findViewById(R.id.v_none_game_detail);
		v_none_game_detail.requestFocus();

		GeneralStatistics.addGameDetailViewStatistics(this, gamename);

		mMyInstalledReceiver = new MyInstalledReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PACKAGE_ADDED");

		filter.addDataScheme("package");
		GameDetailsActivity.this.registerReceiver(mMyInstalledReceiver, filter);

		mMyPublishCommentReceiver = new MyPublishCommentReceiver();
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("com.duoku.action.comment.publish.success");
		filter2.addAction(BroadcaseSender.ACTION_DOWNLOAD_START);
		GameDetailsActivity.this.registerReceiver(mMyPublishCommentReceiver, filter2);

		/**
		 * @author liushuohui
		 */
		IntentFilter filter3 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

		registerReceiver(mConnectionReceiver, filter3);

		// headerCoinTask = new HeaderCoinAnimationTask(this);
		// headerCoinTask.initCoinImp();
		loadData();
	}

	private void measureScreen() {
		WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		density = dm.densityDpi;
	}

	private void parseIntent() {
		Intent s_intent = getIntent();
		openRecommendAndCloseThis = s_intent.getBooleanExtra("openRecommendAndCloseThis", false);
		gameid = s_intent.getStringExtra(GameDetailConstants.KEY_GAME_ID);
		isfromrec = s_intent.getBooleanExtra("isfromrec", false);
		if (gameid == null)
			gameid = "";
		// 存gameid 当打开为我推荐的时候上传gameid list 来确定返回的下行参数\

		SharedPreferences sPreferences = getSharedPreferences("startdata", 0);
		String gameIdPre = sPreferences.getString("gameid", "");
		String[] arr = gameIdPre.split(",");
		List<String> list = java.util.Arrays.asList(arr);
		List<String> arraylist = new ArrayList<String>(list);
		Editor editor = sPreferences.edit();
		if (arraylist.size() == 0 || arraylist.size() < 5) {
			if (!arraylist.contains(gameid)) {
				gameIdPre = gameIdPre + gameid + ",";
				editor.putString("gameid", gameIdPre);
			}
		} else if (arraylist.size() == 5) {
			if (!arraylist.contains(gameid)) {
				arraylist.remove(0);
				String newGameIds = "";
				for (int i = 0; i < arraylist.size(); i++) {
					newGameIds = newGameIds + arraylist.get(i) + ",";
				}
				gameIdPre = newGameIds + gameid;
				editor.putString("gameid", gameIdPre);
			}
		}

		editor.commit();

		pkgname = s_intent.getStringExtra(GameDetailConstants.KEY_GAME_PACKAGE_NAME);
		if (pkgname == null)
			pkgname = "";

		vercode = s_intent.getStringExtra(GameDetailConstants.KEY_GAME_VERSION_CODE);
		vername = s_intent.getStringExtra(GameDetailConstants.KEY_GAME_VERSION_NAME);

		auto_download = s_intent.getBooleanExtra(GameDetailConstants.KEY_GAME_AUTO_DOWNLOAD, false);
	}

	private void requestGameSummary() {
		NetUtil.getInstance().requestGameDetailSummary(MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), gameid, pkgname, vercode, vername, new IRequestListener() {

			@Override
			public void onRequestSuccess(BaseResult responseData) {
				mGameDetailSummaryResult = (GameDetailSummaryResult) responseData;
				new Thread(new Runnable() {

					@Override
					public void run() {
						GameInfo gIn = mGameDetailSummaryResult.getGameInfo();
						QueryInput qin = new QueryInput();
						qin.gameId = gIn.getGameId();
						qin.packageName = gIn.getPkgname();
						qin.version = gIn.getGameversion();
						qin.versionCode = gIn.getGameversioncode();
						qin.downloadUrl = gIn.getDownloadurl();
						gIn.qin = qin;

						gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								try {
									initSummaryPage();
								} catch (OutOfMemoryError e) {
								}
							}
						});

					}
				}) {
				}.start();
			}

			@Override
			public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
				if (DcError.DC_GAME_OUTOFSTOCK == errorCode || DcError.DC_GAME_NOT_FOUND == errorCode) {
					CustomToast.showToast(GameDetailsActivity.this, "暂无此游戏");

					mHolderSummary.refreshStatus(StatusLoading.NOT_FOUND);
				} else {
					// 加载失败
					mHolderSummary.refreshStatus(StatusLoading.FAILED);
					mHolderSummary.setReloadListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							requestGameSummary();
							mHolderSummary.refreshStatus(StatusLoading.LOADING);
						}
					});
				}
			}
		});
	}

	public void loadData() {
		requestGameSummary();

		Intent s_intent = getIntent();
		if (s_intent.hasExtra(GameDetailConstants.KEY_GAME_TAB_ID)) {
			int id = s_intent.getIntExtra(GameDetailConstants.KEY_GAME_TAB_ID, 0);
			if (id == 1) {
				requestGuidePage();
				ll_tab_guide_game_detail.performClick();
			} else if (id == 2) {
				requestCommentPage();
				ll_tab_comment_game_detail.performClick();
			}
			s_intent.removeExtra(GameDetailConstants.KEY_GAME_TAB_ID);
		} else {
			if (mGameDetailWorkspace.current_screen == 2) {
				requestGuidePage();
				ll_tab_guide_game_detail.performClick();
			} else if (mGameDetailWorkspace.current_screen == 3) {
				requestCommentPage();
				ll_tab_comment_game_detail.performClick();
			}
		}

	}

	@Override
	protected void onDestroy() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Collection<PackageCallback> callbacks = listeners.values();
				for (PackageCallback callback : callbacks) {
					// DownloadUtil.removeDownloadItemListener(this, key,
					// listeners.get(key));
					PackageHelper.unregisterPackageStatusChangeObserver(callback);
				}

			}
		}) {
		}.start();

		if (mMyInstalledReceiver != null) {
			this.unregisterReceiver(mMyInstalledReceiver);
		}

		if (mMyPublishCommentReceiver != null)
			this.unregisterReceiver(mMyPublishCommentReceiver);

		/**
		 * @author liushuohui
		 */
		unregisterReceiver(mConnectionReceiver);

		// headerCoinTask.onDestroy();
		unbindDrawables(findViewById(R.id.ll_pics_game_summary_game_details));
		super.onDestroy();
	}

	private void unbindDrawables(View view) {

		if (view == null)
			return;

		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		// headerCoinTask.onWindowFocusChanged(hasFocus);
	};

	GameDetailWorkspace mGameDetailWorkspace;

	LinearLayout ll_tab_summary_game_detail;
	LinearLayout ll_tab_guide_game_detail;
	LinearLayout ll_tab_comment_game_detail;

	// ImageView iv_icon_tab_summary_game_detail;
	// ImageView iv_icon_tab_guide_game_detail;
	// ImageView iv_icon_tab_comment_game_detail;

	TextView tv_tab_summary_game_detail;
	TextView tv_tab_guide_game_detail;
	TextView tv_tab_comment_game_detail;

	/*
	 * ImageView iv_01_current_arrow_tab_game_detail; ImageView
	 * iv_02_current_arrow_tab_game_detail; ImageView
	 * iv_03_current_arrow_tab_game_detail;
	 */



	IndicatorWorkspace ws_indicator_game_detail;

	private void initPage() {
		mGameDetailWorkspace = (GameDetailWorkspace) findViewById(R.id.ws_game_detail_activity);

		mGameDetailWorkspace.page_count = 3;
		if (isfromrec) {
			current_page = 2;
			mGameDetailWorkspace.current_screen = 2;
		} else {
			current_page=1;
			mGameDetailWorkspace.current_screen = 1;
		}
		mGameDetailWorkspace.diff = (mGameDetailWorkspace.current_screen - 1) * displayWidth;
		mGameDetailWorkspace.scrollTo(mGameDetailWorkspace.diff, 0);
		mGameDetailWorkspace.requestLayout();
		mGameDetailWorkspace.invalidate();

		mGameDetailWorkspace.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// enableTabFocus();
				if (GameDetailWorkspace.SCREEN_STATE == GameDetailWorkspace.SCREEN_IS_MOVING)
					return true;

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mGameDetailWorkspace.x_down = event.getX();
					mGameDetailWorkspace.x_move_first = event.getX();
					mGameDetailWorkspace.time_x_down = System.currentTimeMillis();

					break;
				case MotionEvent.ACTION_MOVE:
					if (GameDetailWorkspace.mTouchState != GameDetailWorkspace.TOUCH_STATE_STOPED) {
						if (ws_indicator_game_detail.x_move_first == 0) {
							// indicator part
							ws_indicator_game_detail.x_down = mGameDetailWorkspace.x;
							ws_indicator_game_detail.x_move_first = mGameDetailWorkspace.x;
							ws_indicator_game_detail.time_x_down = mGameDetailWorkspace.time_x_down;
							//
						}

						mGameDetailWorkspace.x_move_second = event.getX();

						mGameDetailWorkspace.diff += (mGameDetailWorkspace.x_move_first - mGameDetailWorkspace.x_move_second);
						mGameDetailWorkspace.x_move_first = mGameDetailWorkspace.x_move_second;

						if (mGameDetailWorkspace.diff < 0) {
							mGameDetailWorkspace.diff = 0;
						}

						if (mGameDetailWorkspace.diff > (mGameDetailWorkspace.page_count - 1) * displayWidth) {
							mGameDetailWorkspace.diff = (mGameDetailWorkspace.page_count - 1) * displayWidth;
						}

						mGameDetailWorkspace.old_diff = mGameDetailWorkspace.diff;

						mGameDetailWorkspace.scrollTo(mGameDetailWorkspace.diff, 0);

						mGameDetailWorkspace.requestLayout();
						mGameDetailWorkspace.invalidate();

						//

						// indicator part
						ws_indicator_game_detail.x_move_second = event.getRawX();
						// Log.i("WWWWW",
						// "x:"+ws_indicator_game_detail.x_move_first+"....y:"+ws_indicator_game_detail.x_move_second+"...d:"+ws_indicator_game_detail.diff);
						ws_indicator_game_detail.diff += -(ws_indicator_game_detail.x_move_first - ws_indicator_game_detail.x_move_second) / 3;
						ws_indicator_game_detail.x_move_first = ws_indicator_game_detail.x_move_second;

						if (ws_indicator_game_detail.diff > (displayWidth / 3)) {
							// Log.i("WWWWW",
							// "x 1111111 diff:"+ws_indicator_game_detail.diff+"......."+(2
							// - current_page) * (displayWidth/3));
							ws_indicator_game_detail.diff = (displayWidth / 3);
						}

						if (ws_indicator_game_detail.diff < -(displayWidth / 3)) {

							ws_indicator_game_detail.diff = -(displayWidth / 3);
						}

						ws_indicator_game_detail.old_diff = ws_indicator_game_detail.diff;

						ws_indicator_game_detail.scrollTo(ws_indicator_game_detail.diff, 0);

						ws_indicator_game_detail.requestLayout();
						ws_indicator_game_detail.invalidate();

						// end indicator part
						//

					}

					break;
				case MotionEvent.ACTION_UP:
					mGameDetailWorkspace.time_x_up = System.currentTimeMillis();
					mGameDetailWorkspace.x_up = event.getX();

					// indicator part
					ws_indicator_game_detail.x_move_first = 0;
					// end indicator part

					// Log.i("whb", ""+(Math.abs(mGameDetailWorkspace.x_down
					// -
					// mGameDetailWorkspace.x_up))/((mGameDetailWorkspace.time_x_up
					// - mGameDetailWorkspace.time_x_down)));
					if ((Math.abs(mGameDetailWorkspace.x_down - mGameDetailWorkspace.x_up)) / ((mGameDetailWorkspace.time_x_up - mGameDetailWorkspace.time_x_down)) > 0.35f) {
						// to next page
						if (mGameDetailWorkspace.x_down - mGameDetailWorkspace.x_up > 0) {
							// ++
							if (mGameDetailWorkspace.current_screen < mGameDetailWorkspace.page_count) {
								mGameDetailWorkspace.diff = displayWidth * mGameDetailWorkspace.current_screen;
								mGameDetailWorkspace.current_screen = mGameDetailWorkspace.current_screen + 1;
								//
								/*
								 * need_turn_page = false;
								 * turn2Tab(mGameDetailWorkspace
								 * .current_screen);
								 */
								// current_page =
								// mGameDetailWorkspace.current_screen;
								// turnTab(mGameDetailWorkspace.current_screen);

								ClickNumStatistics.addGameDetailSlideStatistics(GameDetailsActivity.this);
								// Log.i("WWWWW", "...p1...");

							} else if (mGameDetailWorkspace.current_screen == mGameDetailWorkspace.page_count) {
								mGameDetailWorkspace.diff = displayWidth * (mGameDetailWorkspace.current_screen - 1);
								// Log.i("WWWWW", "...p2...");
							}

						} else {
							// --
							if (mGameDetailWorkspace.current_screen > 1) {
								mGameDetailWorkspace.diff = displayWidth * (mGameDetailWorkspace.current_screen - 2);
								mGameDetailWorkspace.current_screen = mGameDetailWorkspace.current_screen - 1;
								//
								/*
								 * need_turn_page = false;
								 * turn2Tab(mGameDetailWorkspace
								 * .current_screen);
								 */
								// current_page =
								// mGameDetailWorkspace.current_screen;
								// turnTab(mGameDetailWorkspace.current_screen);

								ClickNumStatistics.addGameDetailSlideStatistics(GameDetailsActivity.this);
								// Log.i("WWWWW", "...p3...");

							} else if (mGameDetailWorkspace.current_screen == 1) {
								mGameDetailWorkspace.diff = 0;
								mGameDetailWorkspace.current_screen = 1;
								// Log.i("WWWWW", "...p4...");
							}
						}

						turnTab(mGameDetailWorkspace.current_screen);

						mGameDetailWorkspace.scrollTo(mGameDetailWorkspace.diff, 0, true);

						mGameDetailWorkspace.requestLayout();
						mGameDetailWorkspace.invalidate();

						GameDetailWorkspace.mTouchState = GameDetailWorkspace.TOUCH_STATE_STOPED;

						//
						ws_indicator_game_detail.diff = (2 - current_page) * (displayWidth / 3);
						ws_indicator_game_detail.scrollTo(ws_indicator_game_detail.diff, 0, true);
						ws_indicator_game_detail.requestLayout();
						ws_indicator_game_detail.invalidate();

						return true;
					}

					if (GameDetailWorkspace.mTouchState != GameDetailWorkspace.TOUCH_STATE_STOPED) {

						if (mGameDetailWorkspace.x_down - mGameDetailWorkspace.x_up > (displayWidth / 2)) {
							if (mGameDetailWorkspace.current_screen < mGameDetailWorkspace.page_count) {
								mGameDetailWorkspace.diff = displayWidth * mGameDetailWorkspace.current_screen;
								mGameDetailWorkspace.current_screen = mGameDetailWorkspace.current_screen + 1;
								//
								/*
								 * need_turn_page = false;
								 * turn2Tab(mGameDetailWorkspace
								 * .current_screen);
								 */
								// current_page =
								// mGameDetailWorkspace.current_screen;
								// turnTab(mGameDetailWorkspace.current_screen);

								ClickNumStatistics.addGameDetailSlideStatistics(GameDetailsActivity.this);
								// Log.i("WWWWW", "...p5...");

							} else if (mGameDetailWorkspace.current_screen == mGameDetailWorkspace.page_count) {
								mGameDetailWorkspace.diff = displayWidth * (mGameDetailWorkspace.current_screen - 1);
								// mGameDetailWorkspace.current_screen =
								// mGameDetailWorkspace.current_screen;
								// Log.i("WWWWW", "...p6...");
							}

						} else if (mGameDetailWorkspace.x_down - mGameDetailWorkspace.x_up < -(displayWidth / 2)) {
							if (mGameDetailWorkspace.current_screen > 1) {
								mGameDetailWorkspace.diff = displayWidth * (mGameDetailWorkspace.current_screen - 2);
								mGameDetailWorkspace.current_screen = mGameDetailWorkspace.current_screen - 1;
								//
								/*
								 * need_turn_page = false;
								 * turn2Tab(mGameDetailWorkspace
								 * .current_screen);
								 */
								// current_page =
								// mGameDetailWorkspace.current_screen;
								// turnTab(mGameDetailWorkspace.current_screen);

								ClickNumStatistics.addGameDetailSlideStatistics(GameDetailsActivity.this);
								// Log.i("WWWWW", "...p7...");
							} else if (mGameDetailWorkspace.current_screen == 1) {
								mGameDetailWorkspace.diff = 0;
								mGameDetailWorkspace.current_screen = 1;
								// Log.i("WWWWW", "...p8...");
							}

						} else {
							if (mGameDetailWorkspace.current_screen == 1) {
								// mGameDetailWorkspace.bg_offset = 0;
								mGameDetailWorkspace.diff = 0;
								mGameDetailWorkspace.old_diff = mGameDetailWorkspace.diff;
								// Log.i("WWWWW", "...p9...");
							} else if (mGameDetailWorkspace.current_screen == mGameDetailWorkspace.page_count) {
								// mGameDetailWorkspace.bg_offset =
								// displayWidth/2;
								mGameDetailWorkspace.diff = displayWidth * (mGameDetailWorkspace.current_screen - 1);
								// Log.i("WWWWW", "...p10...");
								mGameDetailWorkspace.old_diff = mGameDetailWorkspace.diff;
							} else {
								// mGameDetailWorkspace.bg_offset =
								// displayWidth/2*2;
								mGameDetailWorkspace.diff = displayWidth * (mGameDetailWorkspace.current_screen - 1);
								// Log.i("WWWWW", "...p11...");
								mGameDetailWorkspace.old_diff = mGameDetailWorkspace.diff;
							}
						}

						turnTab(mGameDetailWorkspace.current_screen);

						mGameDetailWorkspace.scrollTo(mGameDetailWorkspace.diff, 0, true);

						mGameDetailWorkspace.requestLayout();
						mGameDetailWorkspace.invalidate();

						GameDetailWorkspace.mTouchState = GameDetailWorkspace.TOUCH_STATE_STOPED;

						// indicator part
						ws_indicator_game_detail.diff = (2 - current_page) * (displayWidth / 3);
						ws_indicator_game_detail.old_diff = ws_indicator_game_detail.diff;
						ws_indicator_game_detail.scrollTo(ws_indicator_game_detail.diff, 0, true);
						ws_indicator_game_detail.requestLayout();
						ws_indicator_game_detail.invalidate();
						// end indicator part
					}

					//

					break;
				case MotionEvent.ACTION_CANCEL:
					ws_indicator_game_detail.x_move_first = 0;
					break;
				}
				return true;
			}
		});

		// init indicator
		ws_indicator_game_detail = (IndicatorWorkspace) findViewById(R.id.ws_indicator_game_detail);
		/*
		 * RelativeLayout.LayoutParams lp = new
		 * RelativeLayout.LayoutParams(displayWidth/3, 8);
		 * ws_indicator_game_detail.setLayoutParams(lp);
		 */

		ws_indicator_game_detail.page_count = 1;
		if (isfromrec) {
			ws_indicator_game_detail.current_screen = 2;
		} else {
			ws_indicator_game_detail.current_screen = 1;
		}

		ws_indicator_game_detail.diff = (2 - current_page) * (displayWidth / 3);
		ws_indicator_game_detail.scrollTo(ws_indicator_game_detail.diff, 0);
		ws_indicator_game_detail.requestLayout();
		ws_indicator_game_detail.invalidate();

		// end init indicator

		ll_tab_summary_game_detail = (LinearLayout) findViewById(R.id.ll_tab_summary_game_detail);
		ll_tab_guide_game_detail = (LinearLayout) findViewById(R.id.ll_tab_guide_game_detail);
		ll_tab_comment_game_detail = (LinearLayout) findViewById(R.id.ll_tab_comment_game_detail);

		// 去掉导航的图片，所以把这里注释掉 陈贺强 2013-10-15
		// iv_icon_tab_summary_game_detail = (ImageView)
		// findViewById(R.id.iv_icon_tab_summary_game_detail);
		// iv_icon_tab_guide_game_detail = (ImageView)
		// findViewById(R.id.iv_icon_tab_guide_game_detail);
		// iv_icon_tab_comment_game_detail = (ImageView)
		// findViewById(R.id.iv_icon_tab_comment_game_detail);

		tv_tab_summary_game_detail = (TextView) findViewById(R.id.tv_tab_summary_game_detail);
		tv_tab_guide_game_detail = (TextView) findViewById(R.id.tv_tab_guide_game_detail);
		tv_tab_comment_game_detail = (TextView) findViewById(R.id.tv_tab_comment_game_detail);

		ll_tab_summary_game_detail.setOnClickListener(GameDetailsActivity.this);
		ll_tab_guide_game_detail.setOnClickListener(GameDetailsActivity.this);
		ll_tab_comment_game_detail.setOnClickListener(GameDetailsActivity.this);

		/*
		 * iv_01_current_arrow_tab_game_detail = (ImageView)
		 * findViewById(R.id.iv_01_current_arrow_tab_game_detail);
		 * iv_02_current_arrow_tab_game_detail = (ImageView)
		 * findViewById(R.id.iv_02_current_arrow_tab_game_detail);
		 * iv_03_current_arrow_tab_game_detail = (ImageView)
		 * findViewById(R.id.iv_03_current_arrow_tab_game_detail);
		 */

		// 安装loading
		// pb_loading_install_bottom_game_detail = (ProgressBar)
		// findViewById(R.id.pb_loading_install_bottom_game_detail);


		ll_bar_download_bottom_game_detail = (LinearLayout) findViewById(R.id.ll_bar_download_bottom_game_detail);
	}

	// GameDetailPicsWorkspace mGameDetailPicsWorkspace;
	MyScrollView sv_out_game_summary_game_detail;
	HorizontalScrollView sv_game_pics_game_detail_summary;

	int ad_page_count = 1;
	boolean page_moved;
	// 安装loading
	// ProgressBar pb_loading_install_bottom_game_detail;
	LinearLayout ll_bar_download_bottom_game_detail;

	// List<String> installed_apps;

	// boolean isPicsMoving;
	float x_down_pic;
	float x_move_pic;
	float y_down_pic;
	float y_move_pic;

	LinearLayout.LayoutParams lp_des_whole_height;
	LinearLayout.LayoutParams lp_des_custom_height;


	private void initSummaryPage() {
		sv_out_game_summary_game_detail = (MyScrollView) findViewById(R.id.sv_out_game_summary_game_detail);
		sv_game_pics_game_detail_summary = (HorizontalScrollView) findViewById(R.id.sv_game_pics_game_detail_summary);

		sv_out_game_summary_game_detail.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sv_out_game_summary_game_detail.eventover = false;
					MyScrollView.ismoving = false;
					GameDetailConstants.gamepicsmoving = false;
					break;
				case MotionEvent.ACTION_UP:
					sv_out_game_summary_game_detail.eventover = true;
					MyScrollView.ismoving = false;
					break;
				case MotionEvent.ACTION_MOVE:
					sv_out_game_summary_game_detail.eventover = false;
					GameDetailConstants.gamepicsmoving = false;
					break;
				case MotionEvent.ACTION_CANCEL:
					sv_out_game_summary_game_detail.eventover = true;
					MyScrollView.ismoving = false;
					break;

				}
				return false;
			}
		});

		if (mGameDetailSummaryResult != null) {
			gameInfo = mGameDetailSummaryResult.getGameInfo();
			gameid = gameInfo.getGameId();
			updateHeaderTitle(gameInfo.getGameName());
			// game icon
			ImageViewForList iv_game_icon_game_detail_summary = (ImageViewForList) findViewById(R.id.iv_game_icon_game_detail_summary);
			DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.game_icon_game_detail_default);

			iv_game_icon_game_detail_summary.displayImage(gameInfo.getIconUrl(), options);
			/*
			 * mageLoaderHelper.displayImage(gameInfo.getIconUrl(),
			 * iv_game_icon_game_detail_summary, options);
			 */

			ImageView iv_under_shade_game_icon_game_detail_summary = (ImageView) findViewById(R.id.iv_under_shade_game_icon_game_detail_summary);
			iv_under_shade_game_icon_game_detail_summary.setImageResource(R.drawable.icon_under_shade_game_detail);
			ImageView iv_front_shade_game_icon_game_detail_summary = (ImageView) findViewById(R.id.iv_front_shade_game_icon_game_detail_summary);
			iv_front_shade_game_icon_game_detail_summary.setImageResource(R.drawable.icon_front_shade_game_detail);
			// game name
			TextView tv_game_name_game_detail_summary = (TextView) findViewById(R.id.tv_game_name_game_detail_summary);
			tv_game_name_game_detail_summary.setText(gameInfo.getGameName());
			// game star
			RatingBar rb_game_detail_summary = (RatingBar) findViewById(R.id.rb_game_detail_summary);
			rb_game_detail_summary.setProgress((int) (gameInfo.getStar()));
			// game size
			TextView tv_game_size_game_detail_summary = (TextView) findViewById(R.id.tv_game_size_game_detail_summary);
			try {
				Long size = Long.parseLong(gameInfo.getSize());

				tv_game_size_game_detail_summary.setText(getResources().getString(R.string.label_game_size_game_detail_summary) + StringUtil.getDisplaySize(size.toString()));
			} catch (Exception e) {

			}
			// game downloaded times
			TextView tv_download_times_game_detail_summary = (TextView) findViewById(R.id.tv_download_times_game_detail_summary);
			tv_download_times_game_detail_summary.setText(getResources().getString(R.string.label_game_download_times_game_detail_summary) + gameInfo.getDisplaydownloadtimes());
			// game update time
			TextView tv_update_time_game_detail_summary = (TextView) findViewById(R.id.tv_update_time_game_detail_summary);
			tv_update_time_game_detail_summary.setText(getResources().getString(R.string.label_game_update_time_game_detail_summary) + gameInfo.getUpdatetimedate());

			// game type
			TextView tv_game_type_game_detail_summary = (TextView) findViewById(R.id.tv_game_type_game_detail_summary);
			tv_game_type_game_detail_summary.setText("类型：" + gameInfo.getGametypename());

			// game version
			TextView tv_version_game_detail_summary = (TextView) findViewById(R.id.tv_version_game_detail_summary);
			tv_version_game_detail_summary.setText(getResources().getString(R.string.label_game_version_game_detail_summary) + gameInfo.getGameversion());
			// game description
			TextView tv_description_game_detail_summary = (TextView) findViewById(R.id.tv_description_game_detail_summary_new);
			if (gameInfo.getDescription() != null) {
				String s = checkspace(gameInfo.getDescription().trim());
				tv_description_game_detail_summary.setText("\u3000\u3000" + s);

				mVisibleWidth = displayWidth - tv_description_game_detail_summary.getPaddingLeft() - tv_description_game_detail_summary.getPaddingRight();

				int height = tv_description_game_detail_summary.getLineHeight() * 4 + tv_description_game_detail_summary.getPaddingTop() + tv_description_game_detail_summary.getPaddingBottom();

				lp_des_whole_height = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp_des_custom_height = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height + density / 64);// 因为底部显示不全的问题高度添加density/64

				if (isDescpSpread) {
					tv_description_game_detail_summary.setLayoutParams(lp_des_whole_height);
				} else {
					tv_description_game_detail_summary.setLayoutParams(lp_des_custom_height);
				}
				tv_description_game_detail_summary.invalidate();
			}
			if (tv_description_game_detail_summary.getLineCount() <= 4) {
				ll_open_description_game_detail_summary.setVisibility(View.GONE);
			} else {
				ll_open_description_game_detail_summary.setOnClickListener(this);
				tv_description_game_detail_summary.setOnClickListener(this);
			}

			if (gameInfo.isIscollected())
				iv_collect_game_detail.setImageResource(R.drawable.bt_collected_game_detail_selector);
			else
				iv_collect_game_detail.setImageResource(R.drawable.bt_collect_game_detail_selector);

			// pics
			final ArrayList<String> list_small_pics = mGameDetailSummaryResult.getList_gameSmallPics();
			if (list_small_pics.size() > 0) {
				final int width = 180 * density / 240;
				int height = width * 300 / 180;

				int bg_width = (list_small_pics.size() + 1) * 12 * density / 240 + width * list_small_pics.size();
				if (bg_width < displayWidth)
					bg_width = displayWidth;

				if (null == mPage) {
					mPage = View.inflate(GameDetailsActivity.this, R.layout.game_pic_page_game_detail_, null);

					View page = mPage;
					LinearLayout ll_pics_game_summary_game_details = (LinearLayout) page.findViewById(R.id.ll_pics_game_summary_game_details);

					DisplayImageOptions options2 = null;
					for (int i = 0; i < list_small_pics.size(); i++) {
						View v_pic = View.inflate(GameDetailsActivity.this, R.layout.pic_item_game_detail, null);
						ImageViewForList iv_game_pic = (ImageViewForList) v_pic.findViewById(R.id.iv_game_pic_page_game_detail);

						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
						iv_game_pic.setLayoutParams(lp);
						if (options2 == null) {
							options2 = ImageLoaderHelper.getCustomOption(R.drawable.game_pic_game_detail_default);
							// options2 = new DisplayImageOptions.Builder()
							// .showImageForEmptyUri(
							// R.drawable.game_pic_game_detail_default)
							// .showImageOnFail(
							// R.drawable.game_pic_game_detail_default)
							// .bitmapConfig(Bitmap.Config.RGB_565)
							// // 减少内存占用
							// // 每像素站2byte
							// // 默认888占4byte
							// .imageScaleType(ImageScaleType.EXACTLY)
							// .build();
						}

						iv_game_pic.displayImage(list_small_pics.get(i), options2);

						ll_pics_game_summary_game_details.addView(v_pic);
					}
					View v_pic = View.inflate(GameDetailsActivity.this, R.layout.pic_item_game_detail, null);
					ImageView iv_game_pic = (ImageView) v_pic.findViewById(R.id.iv_game_pic_page_game_detail);
					iv_game_pic.setVisibility(View.GONE);
					ll_pics_game_summary_game_details.addView(v_pic);

					if (mPage.getParent() == null) {
						sv_game_pics_game_detail_summary.addView(page);
					}
					sv_game_pics_game_detail_summary.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								x_down_pic = event.getRawX();
								y_down_pic = event.getRawY();
								sv_game_pics_game_detail_summary.requestDisallowInterceptTouchEvent(true);
								GameDetailConstants.gamepicsmoving = true;
								break;
							case MotionEvent.ACTION_UP:
								if (Math.abs(event.getRawX() - x_down_pic) < 10 && Math.abs(event.getRawY() - y_down_pic) < 10) {
									int index = (int) ((sv_game_pics_game_detail_summary.getScrollX() + event.getX()) / (width + 8));
									iv_game_pic_PreviewPager(index);
								}
								sv_out_game_summary_game_detail.eventover = true;
								GameDetailConstants.gamepicsmoving = false;
								MyScrollView.ismoving = false;
								break;
							case MotionEvent.ACTION_MOVE:
								x_move_pic = event.getRawX();
								y_move_pic = event.getRawY();
								if (Math.abs(y_move_pic - y_down_pic) > 80) {
									sv_game_pics_game_detail_summary.requestDisallowInterceptTouchEvent(false);
								}
								GameDetailConstants.gamepicsmoving = true;
								break;
							case MotionEvent.ACTION_CANCEL:
								break;

							}
							return false;
						}
					});
				}
			}

			// recommend games if exist not load.when onresume() run will load
			// it,cause when back from click recommend game,it will flicker for
			// once
			if (null == gv_game_detail_summary) {
				gv_game_detail_summary = (GridView) findViewById(R.id.gv_game_detail_summary);
				gv_game_detail_summary.setAdapter(new GameDetailSummaryAdapter(GameDetailsActivity.this, mGameDetailSummaryResult.getList_rd_games(), density, openRecommendAndCloseThis));
			}
			// download part
			ll_bar_download_bottom_game_detail.setVisibility(null == mGameDetailSummaryResult ? View.GONE : View.VISIBLE);

			if ("2".equals(gameInfo.getComingsoon())) {
				refreshCommingSoon();
			} else {
				checkGameInfoStatus();
			}

			/**
			 * @author liushuohui
			 */
			if (auto_download && gameInfo.download_status != null && (gameInfo.download_status.status == PackageMode.UNDOWNLOAD || PackageMode.UPDATABLE == gameInfo.download_status.status)) {
				auto_download = false;// 调用一次自动下载
				ll_bt_download_game_detail.performClick();
			}
		}

		mViewSummaryLoading.setVisibility(View.GONE);
	}

	private void checkGameInfoStatus() {
		if (null == gameInfo) {
			return;
		}
		if (iv_icon_download_bottom_game_detail != null)
			iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
		if (tv_download_status_bottom_game_detail != null)
			tv_download_status_bottom_game_detail.setVisibility(View.VISIBLE);

		if (!listeners.keySet().contains(gameInfo.getGameId())) {
			PackageCallback download_listener = new PackageCallback() {

				@Override
				public void onPackageStatusChanged(PackageMode mode) {
					if (mode.gameId != null && mode.gameId.equals(gameInfo.getGameId())) {
						gameInfo.download_status = mode;

						if (Constants.DEBUG) {
							Log.d("DOWNLOAD_RUNNING", "onPackageStatusChanged >> name: " + gameInfo.getGameName());
							Log.d("DOWNLOAD_RUNNING", "onPackageStatusChanged >> current: " + mode.currentSize);
							Log.d("DOWNLOAD_RUNNING", "onPackageStatusChanged >> total--: " + mode.totalSize);
							Log.d("DOWNLOAD_RUNNING", "onPackageStatusChanged >> pkgsize: " + Long.valueOf(gameInfo.getSize()).longValue());
						}

						Message msg = new Message();
						msg.what = DOWNLOAD_NOTIFY;
						msg.obj = gameInfo;
						mHandler.sendMessage(msg);
					}

				}
			};

			listeners.put(gameInfo.getGameId(), download_listener);
			PackageHelper.registerPackageStatusChangeObserver(download_listener);
		}

		checkDownloadBtnState();

	}

	private void turn2Page(int page) {
		mGameDetailWorkspace.current_screen = page;
		current_page = page;
		mGameDetailWorkspace.diff = (mGameDetailWorkspace.current_screen - 1) * displayWidth;
		mGameDetailWorkspace.scrollTo(mGameDetailWorkspace.diff, 0);
		mGameDetailWorkspace.requestLayout();
		mGameDetailWorkspace.invalidate();
	}

	private void turn2Indicator() {
		ws_indicator_game_detail.diff = (2 - current_page) * (displayWidth / 3);
		ws_indicator_game_detail.scrollTo(ws_indicator_game_detail.diff, 0);
		ws_indicator_game_detail.requestLayout();
		ws_indicator_game_detail.invalidate();
	}

	private void turnTab(int pos) {
		if (current_page == pos) {
			return;
		}

		current_page = pos;

		switch (pos) {
		case 1:
			refreshTabs(R.id.ll_tab_summary_game_detail);
			refreshOptionsLine(R.id.ll_tab_summary_game_detail);

			break;
		case 2:
			refreshTabs(R.id.ll_tab_guide_game_detail);
			requestGuidePage();
			refreshOptionsLine(R.id.ll_tab_guide_game_detail);

			break;
		case 3:
			refreshTabs(R.id.ll_tab_comment_game_detail);
			requestCommentPage();
			refreshOptionsLine(R.id.ll_tab_comment_game_detail);
			break;
		default:
			break;
		}
	}

	boolean isGuidePageInit;
	int guide_page = 1;
	GameDetailGuideResult mGameDetailGuideResult;

	private void requestGuidePage() {
		if (!isGuidePageInit) {
			NetUtil.getInstance().requestGameDetailGuide(gameid, MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), pkgname, vername, vercode, guide_page, 20,
					new IRequestListener() {

						@Override
						public void onRequestSuccess(BaseResult responseData) {
							mGameDetailGuideResult = (GameDetailGuideResult) responseData;
							isGuidePageInit = true;
							if (mGameDetailGuideResult.getList_game_guide().size() > 0) {
								guide_page++;

								initGuidePage();

								// 攻略加载成功
								mHolderGuide.refreshStatus(StatusLoading.SUCCEED);
							} else {
								// 暂无攻略
								mHolderGuide.refreshStatus(StatusLoading.NONE);
							}
						}

						@Override
						public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
							// 加载失败
							mHolderGuide.refreshStatus(StatusLoading.FAILED);
							mHolderGuide.setReloadListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									requestGuidePage();

									mHolderGuide.refreshStatus(StatusLoading.LOADING);
								}
							});
						}
					});
		}
	}

	boolean isCommentPageInit;
	int comment_page = 1;
	GameDetailCommentResult mGameDetailCommentResult;

	private void requestCommentPage() {
		if (!isCommentPageInit) {
			if (TextUtils.isEmpty(gameid)) {
				mHolderComment.refreshStatus(StatusLoading.NONE);
			} else {
				NetUtil.getInstance().requestGameDetailComment(gameid, pkgname, vername, vercode, comment_page, 20, new IRequestListener() {

					@Override
					public void onRequestSuccess(BaseResult responseData) {
						mGameDetailCommentResult = (GameDetailCommentResult) responseData;
						isCommentPageInit = true;
						if (mGameDetailCommentResult.getList_comment().size() > 0) {
							comment_page++;

							initCommentPage();

							mHolderComment.refreshStatus(StatusLoading.SUCCEED);
						} else {
							// 暂无评论
							mHolderComment.refreshStatus(StatusLoading.NONE);
						}
					}

					@Override
					public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
						// 加载失败
						mHolderComment.refreshStatus(StatusLoading.FAILED);
						mHolderComment.setReloadListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								requestCommentPage();

								mHolderComment.refreshStatus(StatusLoading.LOADING);
							}
						});
					}
				});
			}
		}
	}

	SimpleListView<GameGuideInfo> lv_game_guide_game_detail;
	ArrayList<GameGuideInfo> list_guide = new ArrayList<GameGuideInfo>();

	private void initGuidePage() {
		lv_game_guide_game_detail = (SimpleListView<GameGuideInfo>) findViewById(R.id.lv_game_guide_game_detail);
		if (mGameDetailGuideResult != null) {
			list_guide.addAll(mGameDetailGuideResult.getList_game_guide());
			lv_game_guide_game_detail.setList(R.layout.item_game_guide_game_detail, list_guide,new BindViewCallBack<GameGuideInfo>() {

				@Override
				public void onBindViewCallBack(View convertView, GameGuideInfo item, SimpleHolder tag) {
					((TextView) tag.v1).setText(item.getGuidetitle());
					((TextView) tag.v2).setText(item.getGuidetime());
				}

				@Override
				public SimpleHolder onBindViewTag(View convertView) {
					SimpleHolder holder = new SimpleHolder();
					holder.v1 = convertView.findViewById(R.id.tv_guide_title_item_game_guide_game_detail);
					holder.v2 = convertView.findViewById(R.id.tv_guide_time_item_game_guide_game_detail);
					return holder;
				}
			});
			ll_bar_download_bottom_game_detail.setVisibility(View.VISIBLE);

			lv_game_guide_game_detail.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					// TODO 攻略详情
					if (position < list_guide.size() && position >= 0) {
						Intent in = new Intent(GameDetailsActivity.this, GameGuideDetailActivity2.class);
						in.putExtra("guideid", list_guide.get(position).getGuideid());
						GameDetailsActivity.this.startActivity(in);
					}
				}
			});
			lv_game_guide_game_detail.setScrollBottomCallBack(new loadMoreDataCallBack() {

				@Override
				public void onScrollBottomCallBack() {
					loadMoreGuide();
				}
			});
		}
	}

	SimpleListView<GameCommentInfo> lv_game_comment_game_detail;
	ArrayList<GameCommentInfo> list_comment = new ArrayList<GameCommentInfo>();

	private void initCommentPage() {

		lv_game_comment_game_detail = (SimpleListView<GameCommentInfo>) findViewById(R.id.lv_game_comment_game_detail);
		if (mGameDetailCommentResult != null) {
			list_comment.addAll(mGameDetailCommentResult.getList_comment());
			lv_game_comment_game_detail.setList(R.layout.item_game_comment_game_detail, list_comment, new BindViewCallBack<GameCommentInfo>() {
				@Override
				public void onBindViewCallBack(View convertView, GameCommentInfo item, SimpleHolder tag) {
					((TextView) tag.v1).setText(item.getCmtusername());
					((TextView) tag.v2).setText(item.getCommenttime());
					((TextView) tag.v3).setText(item.getCmtcotent());
				}

				@Override
				public SimpleHolder onBindViewTag(View convertView) {
					SimpleHolder holder = new SimpleHolder();
					holder.v1 = convertView.findViewById(R.id.tv_username_item_game_comment_game_detail);
					holder.v2 = convertView.findViewById(R.id.tv_time_item_game_comment_game_detail);
					holder.v3 = convertView.findViewById(R.id.tv_content_item_game_comment_game_detail);
					return holder;
				}
			});
			lv_game_comment_game_detail.setScrollBottomCallBack(new loadMoreDataCallBack() {
				@Override
				public void onScrollBottomCallBack() {
					loadMoreComment();
				}
			});
			ll_bar_download_bottom_game_detail.setVisibility(null == mGameDetailSummaryResult ? View.GONE : View.VISIBLE);
		}

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.ll_tab_summary_game_detail:
			if (current_page == 1)
				return;

			refreshTabs(id);

			turn2Page(1);
			turn2Indicator();

			refreshOptionsLine(id);

			break;
		case R.id.ll_tab_guide_game_detail:
			if (current_page == 2)
				return;

			refreshTabs(id);
			turn2Page(2);
			requestGuidePage();
			setiv_share_game_guide_detail_Hide(View.VISIBLE);
			iv_collect_game_detail.setVisibility(View.VISIBLE);

			turn2Indicator();

			refreshOptionsLine(id);

			ClickNumStatistics.addGameDetailRaidersClickStatistis(getApplicationContext(), gamename);

			break;
		case R.id.ll_tab_comment_game_detail:
			if (current_page == 3)
				return;

			ClickNumStatistics.addGameDetailCommentsClickStatistis(getApplicationContext(), gamename);

			refreshTabs(id);
			turn2Page(3);
			requestCommentPage();
			setiv_share_game_guide_detail_Hide(View.INVISIBLE);
			iv_collect_game_detail.setVisibility(View.INVISIBLE);

			// iv_icon_download_bottom_game_detail.setVisibility(View.GONE);

			turn2Indicator();

			refreshOptionsLine(id);
			break;
		case R.id.ll_open_description_game_detail_summary:
			// case R.id.tv_description_game_detail_summary:
		case R.id.tv_description_game_detail_summary_new:
		// if(lp_des_whole_height != null && lp_des_custom_height != null)
		{

			if (!isOpen) {
				// tv_description_game_detail_summary.setMaxLines(Integer.MAX_VALUE);
				// tv_description_game_detail_summary.isOpen = true;
				// tv_description_game_detail_summary.invalidate();
				// tv_description_game_detail_summary.setLines(1000000);
				tv_description_game_detail_summary.setLayoutParams(lp_des_whole_height);
				tv_description_game_detail_summary.invalidate();
				tv_open_description_game_detail_summary.setText("收起");
				iv_open_description_game_detail_summary.setImageResource(R.drawable.icon_close_description_game_detail_summary);
				isDescpSpread = true;
			} else {
				// tv_description_game_detail_summary.setMaxLines(4);
				// tv_description_game_detail_summary.setLayoutParams(lp_des_custom_height);
				// tv_description_game_detail_summary.isOpen = false;
				// tv_description_game_detail_summary.invalidate();
				tv_description_game_detail_summary.setLayoutParams(lp_des_custom_height);
				// tv_description_game_detail_summary.setLines(4);
				tv_description_game_detail_summary.invalidate();
				tv_open_description_game_detail_summary.setText("展开");
				iv_open_description_game_detail_summary.setImageResource(R.drawable.icon_open_description_game_detail_summary);
				isDescpSpread = false;
			}
			isOpen = !isOpen;
		}
			break;
		default:
			super.onClick(v);
			break;
		}

	}

	MyInstalledReceiver mMyInstalledReceiver;

	class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
				String data = intent.getDataString();
				String packageName = null;
				if (data != null)
					packageName = data.substring(data.indexOf(":") + 1);
				// Log.i("WWWWW",
				// "iiiiiiiii pkg:"+packageName+"...."+gameInfo.getPkgname());
				if (gameInfo != null) {
					if (packageName != null && packageName.equals(gameInfo.getPkgname())) {
						gameInfo.game_status = GameInfo.GAME_INSTALLED;
						mHandler.sendEmptyMessage(DOWNLOAD_NOTIFY);
					}

				}
			}

		}

	}

	MyPublishCommentReceiver mMyPublishCommentReceiver;

	class MyPublishCommentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.i("WWWWW", "flash........................11111:");
			if (intent.getAction().equals("com.duoku.action.comment.publish.success")) {
				// Log.i("WWWWW", "flash........................:");
				isCommentPageInit = false;
				comment_page = 1;
				requestCommentPage();
			} else if (BroadcaseSender.ACTION_DOWNLOAD_START.equals(intent.getAction())) {
				// Log.i("WWWWW", "flash.............3333...........:");
				checkGameInfoStatus();	
			}

		}

	}

	HashMap<String, GameInfo> wait2download_map = new HashMap<String, GameInfo>();
	// HashMap<String,DownloadItemOutput> continueOrRetry_map = new
	// HashMap<String,DownloadItemOutput>();
	private final static int REQUEST_DOWNLOAD_IN_WAP_NETWORK = 1000;




	private String checkspace(String content) {
		Pattern p = Pattern.compile("　{1,10}");
		Matcher m = p.matcher(content);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		boolean result = m.find();
		while (result) {
			i++;
			m.appendReplacement(sb, "");

			result = m.find();
		}
		m.appendTail(sb);

		Pattern p2 = Pattern.compile("[ | |　]{0,140}\n{1,140}[ | |　]{0,140}");
		Matcher m2 = p2.matcher(sb.toString());
		StringBuffer sb2 = new StringBuffer();
		int i2 = 0;
		boolean result2 = m2.find();
		while (result2) {
			i2++;
			m2.appendReplacement(sb2, "\n\u3000\u3000");

			result2 = m2.find();
		}
		m2.appendTail(sb2);
		return sb2.toString();
	}

	private void refreshCommingSoon() {
		tv_download_status_bottom_game_detail.setText("即将上线");
		tv_download_status_bottom_game_detail.setTextColor(Color.parseColor("#FFFFFF"));

		// ll_bt_download_game_detail.setBackgroundResource(R.drawable.bg_prg_img_game_detail4);
		ll_bt_download_game_detail.setBackgroundResource(R.drawable.bt_download_game_detail_selector);

		iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_comingsoon_game_detail);

		tv_download_status_bottom_game_detail.setVisibility(View.VISIBLE);
		iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
	}

	private void refreshOptionsLine(int id) {
		if (null != gameInfo) {
			int visible = View.VISIBLE;

			if (id == R.id.ll_tab_comment_game_detail) {
				visible = View.INVISIBLE;
			}

			if ("2".equals(gameInfo.getComingsoon())) {
				refreshCommingSoon();
			} else {
				checkDownloadBtnState();
			}

			setiv_share_game_guide_detail_Hide(visible);
			iv_collect_game_detail.setVisibility(visible);
		}
	}

	private void refreshTabs(int id) {
		int clrSummary = StringUtil.getColor("6b6b6b");
		int clrGuide = StringUtil.getColor("6b6b6b");
		int clrComment = StringUtil.getColor("6b6b6b");
		int imgSummary = R.drawable.icon_summary_tab_game_detail_normal;
		int imgGuide = R.drawable.icon_guide_tab_game_detail_normal;
		int imgComment = R.drawable.icon_comment_tab_game_detail_normal;

		if (id == R.id.ll_tab_summary_game_detail) {
			clrSummary = StringUtil.getColor("448BDC");
			imgSummary = R.drawable.icon_summary_tab_game_detail_click;
		} else if (id == R.id.ll_tab_guide_game_detail) {
			clrGuide = StringUtil.getColor("448BDC");
			imgGuide = R.drawable.icon_guide_tab_game_detail_click;
		} else if (id == R.id.ll_tab_comment_game_detail) {
			clrComment = StringUtil.getColor("448BDC");
			imgComment = R.drawable.icon_comment_tab_game_detail_click;
		}

		// iv_icon_tab_summary_game_detail.setImageResource(imgSummary);
		tv_tab_summary_game_detail.setTextColor(clrSummary);
		// iv_icon_tab_guide_game_detail.setImageResource(imgGuide);
		tv_tab_guide_game_detail.setTextColor(clrGuide);
		// iv_icon_tab_comment_game_detail.setImageResource(imgComment);
		tv_tab_comment_game_detail.setTextColor(clrComment);
	}

	/**
	 * 大图预览
	 */
	private void iv_game_pic_PreviewPager(int index) {

		pic_game_detail_preview_container = View.inflate(this, R.layout.pic_game_detail_preview, null);

		SlowScrollViewpager viewPager = (SlowScrollViewpager) pic_game_detail_preview_container.findViewById(R.id.pics_preview_slowscrollviewpager);

		final AdsPoints home_ad_points = (AdsPoints) pic_game_detail_preview_container.findViewById(R.id.home_ad_points);

		ArrayList<String> list_small_pics = mGameDetailSummaryResult.getList_gameSmallPics();

		DisplayImageOptions options2 = ImageLoaderHelper.getCustomOption(R.drawable.game_pic_game_detail_default);

		ArrayList<View> ll_pics_game_summary_game_details = new ArrayList<View>();

		int count = list_small_pics.size();

		for (int i = 0; i < count; i++) {

			View v_pic = View.inflate(GameDetailsActivity.this, R.layout.pic_item_game_detail_preview, null);
			ImageViewForList iv_game_pic = (ImageViewForList) v_pic.findViewById(R.id.iv_game_pic_page_game_detail);

			iv_game_pic.displayImage(list_small_pics.get(i), options2);

			ll_pics_game_summary_game_details.add(v_pic);

			v_pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					((ViewGroup) GameDetailsActivity.this.getWindow().getDecorView()).removeView(pic_game_detail_preview_container);
					pic_game_detail_preview_container = null;
				}
			});

		}

		home_ad_points.setChildCount(count);
		home_ad_points.setSelectBgRes(R.drawable.icon_point_ads_home_activity2);
		home_ad_points.setNormalBgRes(R.drawable.icon_point_ads_home_activity2_current);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				home_ad_points.change(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		viewPager.setAdapter(new HomeGuideViewPagerAdapter(ll_pics_game_summary_game_details));
		viewPager.setCurrentItem(index);
		// 大图预览
		((ViewGroup) this.getWindow().getDecorView()).addView(pic_game_detail_preview_container);
	}

	/**
	 * 处理返回事件，判断是否正在显示大图预览，先结束掉大图预览效果。
	 */
	@Override
	public void onBackPressed() {
		if (pic_game_detail_preview_container != null) {
			int index = ((ViewGroup) this.getWindow().getDecorView()).indexOfChild(pic_game_detail_preview_container);
			if (index != -1) {
				((ViewGroup) GameDetailsActivity.this.getWindow().getDecorView()).removeView(pic_game_detail_preview_container);
				pic_game_detail_preview_container = null;
				return;
			}
		}
		super.onBackPressed();
	}

	@Override
	public int getLayout() {
		return R.layout.game_detail_activity;
	}

	private void loadMoreComment() {
		if (list_comment.size() < 20) {

			// mHandler.sendEmptyMessage(GAME_COMMENT_LIST_SET_LOAD_NUM_EMPTY_NOTIFY);
			lv_game_comment_game_detail.setFooterStatus(SimpleListView.STATUS_NO_GONE);
		} else {

			NetUtil.getInstance().requestGameDetailComment(gameid, pkgname, vername, vercode, comment_page, 20, new IRequestListener() {

				@Override
				public void onRequestSuccess(BaseResult responseData) {
					mGameDetailCommentResult = (GameDetailCommentResult) responseData;

					if (mGameDetailCommentResult != null) {
						if (mGameDetailCommentResult.getList_comment().size() > 0) {
							comment_page++;

							list_comment.addAll(mGameDetailCommentResult.getList_comment());
							// mHandler.sendEmptyMessage(GAME_COMMENT_LIST_NOTIFY);
							lv_game_comment_game_detail.notifyDataSetChanged();
							lv_game_comment_game_detail.setFooterStatus(SimpleListView.STATUS_NO_DATA);
						} else {
							// mHandler.sendEmptyMessage(GAME_COMMENT_LIST_SET_LOAD_NUM_EMPTY_NOTIFY);
							lv_game_comment_game_detail.setFooterStatus(SimpleListView.STATUS_NO_MORE);
						}

					}
				}

				@Override
				public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
					// Log.i("whb",
					// "requestTag:"+requestTag+"...errorCode:"+errorCode);
					// LOADING_STATE = LOADING_STATE_WAIT;
					CustomToast.showToast(GameDetailsActivity.this, getString(R.string.alert_network_inavailble));
					lv_game_comment_game_detail.setFooterStatus(SimpleListView.STATUS_NO_DATA);
				}
			});

		}
	}

	protected void loadMoreGuide() {
		if (list_guide.size() < 20) {
			lv_game_guide_game_detail.setFooterStatus(SimpleListView.STATUS_NO_GONE);

		} else {
			NetUtil.getInstance().requestGameDetailGuide(gameid, MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), pkgname, vername, vercode, guide_page, 20,
					new IRequestListener() {

						@Override
						public void onRequestSuccess(BaseResult responseData) {
							mGameDetailGuideResult = (GameDetailGuideResult) responseData;

							if (mGameDetailGuideResult != null) {
								if (mGameDetailGuideResult.getList_game_guide().size() > 0) {
									guide_page++;

									list_guide.addAll(mGameDetailGuideResult.getList_game_guide());
									lv_game_guide_game_detail.notifyDataSetChanged();
									lv_game_guide_game_detail.setFooterStatus(SimpleListView.STATUS_NO_DATA);
								} else {
									lv_game_guide_game_detail.setFooterStatus(SimpleListView.STATUS_NO_MORE);
								}

							}
						}

						@Override
						public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
							CustomToast.showToast(GameDetailsActivity.this, getString(R.string.alert_network_inavailble));
							lv_game_guide_game_detail.setFooterStatus(SimpleListView.STATUS_NO_DATA);
						}
					});

		}
	}
}
