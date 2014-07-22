package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.HomeAppGridAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.InternalGames.InternalInstalledGames;
import com.ranger.bmaterials.app.InternalGames.InternalStartGames;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.listener.AdOnClickListener;
import com.ranger.bmaterials.mode.ADInfo;
import com.ranger.bmaterials.mode.HomeAppGridInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.HomePageDataResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.ui.gametopic.BMProductDetailActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.AdsPoints;
import com.ranger.bmaterials.view.AdsViewPager;
import com.ranger.bmaterials.view.ExpandableHeightGridView;
import com.ranger.bmaterials.view.MyPopupWindows;
import com.ranger.bmaterials.view.SectionScrollView;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.SpeedDownloadInfoTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class HomeFragment extends HeaderHallBaseFragment implements OnClickListener {

	public HomePageDataResult mHomePageDataResult;

	private HomeAppGridAdapter adapter;

	private HomeAppListFragment appListFragment;

	private long startTime;

	private UnInstalledGameRefreshReceiver unInstalledGameRefreshReceiver;

	private WhilteListGamesRefreshReceiver whiteListAndUninstalledReceiver;

	protected Bundle savedBundle;
	private View iv_home_rec_enter;
	public static HomeFragment instance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		savedBundle = savedInstanceState;

		if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);
			}
			return root;
		}

		startTime = System.currentTimeMillis();

		if (Constants.speed_download_enable)
			new SpeedDownloadInfoTask(getActivity()).start();

		root = inflater.inflate(R.layout.home_activity, null);

		// fix scrollview and abslistview compat bug
		View v_none_space = root.findViewById(R.id.v_none_space);
		v_none_space.requestFocus();

		sectionScrollView = (SectionScrollView) root.findViewById(R.id.home_section_scrollView);

		initHeader();
		initAdViews();
		initAppGridView();
		initSectionLayout();
		initAppListFragment();
		refreshLocalGamesView();

		loadData();

		instance = this;
		return root;
	}

	private void initAppListFragment() {
		FragmentManager fm = getChildFragmentManager();
		appListFragment = new HomeAppListFragment();
		appListFragment.setScrollView(sectionScrollView);
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.home_app_list_fragment, appListFragment);
		ft.commitAllowingStateLoss();

		sectionScrollView.setScrollListener(appListFragment);
	}

	// public void initLocalGameAnimationTask(boolean startAnimation) {
	// if (myLocalGameAnimationTask == null) {
	// myLocalGameAnimationTask = new HomeMyLocalGameAnimationTask(
	// getActivity(), root);
	// myLocalGameAnimationTask.startAnimationDelay = System
	// .currentTimeMillis() - startTime;
	// myLocalGameAnimationTask.init();
	// }
	// if (startAnimation)
	// // 首次启动则在引导页结束后才展示动画
	// myLocalGameAnimationTask.refreshMyLocalGames(true);
	// }

	private void refreshLocalGamesView() {

		// initLocalGameAnimationTask(!SplashTask.isShowGuide());

		// 监听白名单更新
		whiteListAndUninstalledReceiver = new WhilteListGamesRefreshReceiver();
		IntentFilter i = new IntentFilter(BroadcaseSender.ACTION_WHITELIST_INITIALIZED);
		getActivity().registerReceiver(whiteListAndUninstalledReceiver, i);

		// 监听应用卸载
		unInstalledGameRefreshReceiver = new UnInstalledGameRefreshReceiver();
		IntentFilter i2 = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		i2.addDataScheme("package");
		getActivity().registerReceiver(unInstalledGameRefreshReceiver, i2);
	}

	private ExpandableHeightGridView pull2gv;

	// private HomeMyLocalGameAnimationTask myLocalGameAnimationTask;

	private void initAppGridView() {
		pull2gv = (ExpandableHeightGridView) root.findViewById(R.id.gv_home_activity);
		pull2gv.setParentScrollView(sectionScrollView);
		adapter = new HomeAppGridAdapter(getActivity());
		pull2gv.setAdapter(adapter);
	}

	private void initAdViews() {
		mWorkspace = (AdsViewPager) root.findViewById(R.id.home_ad_ws);
		RelativeLayout rl = (RelativeLayout) root.findViewById(R.id.home_ad_layout);
		LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) rl.getLayoutParams();
		int[] wh = UIUtil.getScreenPx(getActivity());
		int displayWidth = wh[0];
		lp2.height = displayWidth / 3 + 20;
		rl.setLayoutParams(lp2);
	}

	private class RequestListenerImpl implements IRequestListener {
		@Override
		public void onRequestSuccess(BaseResult responseData) {
			// TODO Auto-generated method stub
			mHomePageDataResult = (HomePageDataResult) responseData;
			String jsonRes =null;
			if (mHomePageDataResult != null && (jsonRes = mHomePageDataResult.getJsonRes()) != null && getActivity() != null) {
				if (!jsonRes.equalsIgnoreCase(data_cache_json)) {
					showContent();
					data_cache_sp.edit().putString(HOME_DATA_LOCAL_CACHE, jsonRes).commit();

					appListFragment.refreshListData(mHomePageDataResult.getGamesList(), false, null);
				}
			}

			requestDataTask.isLoading.set(false);
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			// TODO Auto-generated method stub
			requestDataTask.isLoading.set(false);
		}

	}

	private synchronized void showContent() {
		showADsWorkspace();
		showAppGrid();
		appListFragment.showListTitle(mHomePageDataResult);
	}

	private void showAppGrid() {
		if (mHomePageDataResult != null) {
			requestDataTask.network_loading_layout.setVisibility(View.VISIBLE);
			filterGridInstalledGames();
		}
	}

	public AtomicBoolean isRegisterPackage = new AtomicBoolean();

	public PackageCallback packageCallBack = new PackageCallback() {

		@Override
		public void onPackageStatusChanged(PackageMode mode) {
			// TODO Auto-generated method stub
			if (mode.status == PackageMode.INSTALLED) {
				// 更新九宫格
				filterGridInstalledGames();
				// 更新我的游戏
				// if (myLocalGameAnimationTask != null)
				// myLocalGameAnimationTask.refreshMyLocalGames(false);

				InternalInstalledGames.addInternalInstalledGames(getActivity(), mode.packageName);
			}

			appListFragment.packageStatusListener.onPackageStatusChanged(mode);
		}
	};

	// 九宫格过滤已安装游戏
	private void filterGridInstalledGames() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				if (getActivity() == null || mHomePageDataResult == null) {
					return null;
				}

				CopyOnWriteArrayList<HomeAppGridInfo> showApplist = new CopyOnWriteArrayList<HomeAppGridInfo>(mHomePageDataResult.getGamesGrid());

				PackageManager pm = getActivity().getPackageManager();

				for (HomeAppGridInfo info : showApplist) {
					try {
						pm.getPackageInfo(info.pkgName, 0);
						showApplist.remove(info);
					} catch (Exception e) {
					}
				}

				if (adapter != null)
					adapter.showAppList = showApplist;

				return null;

			};

			protected void onPostExecute(Void v) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
					if (requestDataTask != null && requestDataTask.network_loading_layout != null) {
						requestDataTask.network_loading_layout.setVisibility(View.GONE);
					}
				}
			}
		}.execute();

	}

	private class WhilteListGamesRefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 已安装应用列表刷新
			// if (myLocalGameAnimationTask != null)
			// myLocalGameAnimationTask.refreshMyLocalGames(false);

			if (appListFragment != null && appListFragment.adapter != null)
				appListFragment.refreshListData(appListFragment.adapter.getList(), false, null);
		}

	}

	private class UnInstalledGameRefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 有应用被卸载
			String packageName = intent.getData().getSchemeSpecificPart();
			InternalInstalledGames.deleteInternalInstalledGames(context, packageName);
			InternalStartGames.deleteStartGame(context, packageName);

			// if (myLocalGameAnimationTask != null)
			// myLocalGameAnimationTask.refreshMyLocalGames(false);
		}

	}

	private class NetWorkStatusChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (ConnectManager.isNetworkConnected(context)) {
				requestDataTask.loading();
			}
		}
	}

	private SharedPreferences data_cache_sp;

	private String data_cache_json;

	private LoadingTask requestDataTask;

	private NetWorkStatusChangedReceiver networkStatusChangedReceiver;

	private SectionScrollView sectionScrollView;

	private static final String HOME_CACHE_SP_NAME = "home_data_cache";

	private static final String HOME_DATA_LOCAL_CACHE = "home_data_local_cache";

	private void loadData() {
		networkStatusChangedReceiver = new NetWorkStatusChangedReceiver();
		// 监听网络状态改变
		IntentFilter i = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		getActivity().registerReceiver(networkStatusChangedReceiver, i);

		requestDataTask = new LoadingTask(getActivity(), new ILoading() {

			@Override
			public void loading(IRequestListener listener) {
				// TODO Auto-generated method stub
				if (requestDataTask != null && requestDataTask.isLoading.get())
					return;
				NetUtil.getInstance().requestHomePageData(listener);
			}

			@Override
			public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
				// TODO Auto-generated method stub
				if (getActivity() == null)
					return;

				// load local data
				data_cache_sp = getActivity().getSharedPreferences(HOME_CACHE_SP_NAME, Context.MODE_PRIVATE);
				data_cache_json = data_cache_sp.getString(HOME_DATA_LOCAL_CACHE, "");

				if (!data_cache_json.equalsIgnoreCase("")) {
					mHomePageDataResult = (HomePageDataResult) JSONParser.parseHomePageData(data_cache_json);

					showContent();
				}

			}

			@Override
			public IRequestListener getRequestListener() {
				// TODO Auto-generated method stub
				return new RequestListenerImpl();
			}

			@Override
			public boolean isShowNoNetWorkView() {
				// TODO Auto-generated method stub
				return TextUtils.isEmpty(data_cache_json);
			}

			@Override
			public boolean isAsync() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		requestDataTask.setRootView(root);

		requestDataTask.loading();
	}

	private void initSectionLayout() {

		View home_section_newgames = root.findViewById(R.id.home_section_newgames);
		View home_tab_mustplay = root.findViewById(R.id.home_section_mustplay);
		View home_tab_competition = root.findViewById(R.id.home_section_competition);
		View home_tab_class = root.findViewById(R.id.home_section_classgames);
		home_section_newgames.setOnClickListener(this);
		home_tab_mustplay.setOnClickListener(this);
		home_tab_competition.setOnClickListener(this);
		home_tab_class.setOnClickListener(this);

//		View section_layout = root.findViewById(R.id.home_section_buttons_include);
//		sectionScrollView.setSection_layout(section_layout);
//		View gloss_section_layout = root.findViewById(R.id.gloss_home_section_buttons_include);
//		sectionScrollView.setGlossSectionLayout(gloss_section_layout);
//		sectionScrollView.setButtonOnClickListener(this);
//		sectionScrollView.setTopOffset(getResources().getDimensionPixelSize(R.dimen.header_title_height));
		setNewAddedCount();

	}

	public void setNewAddedCount() {
		// 新品游戏显示
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

			@Override
			public void run() {
				if (null != root && null != getActivity()) {
					TextView tv_new_games_count = (TextView) root.findViewById(R.id.tv_new_games_count);
					SharedPreferences sp = getActivity().getSharedPreferences("startdata", Context.MODE_PRIVATE);
					String adCount = sp.getString("addedcount", "0");
					if (!adCount.equals("0") && !TextUtils.isEmpty(adCount)) {
						tv_new_games_count.setText(adCount);
						tv_new_games_count.setVisibility(View.VISIBLE);
					} else {
						tv_new_games_count.setVisibility(View.INVISIBLE);
					}
				}
			}
		}, 4000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		turn2TabFourTag = false;
		try {
			PackageHelper.unregisterPackageStatusChangeObserver(packageCallBack);
			getActivity().unregisterReceiver(networkStatusChangedReceiver);
			getActivity().unregisterReceiver(whiteListAndUninstalledReceiver);
			getActivity().unregisterReceiver(unInstalledGameRefreshReceiver);
		} catch (Exception e) {
		}

		mWorkspace.recycle();
		mWorkspace = null;
		pull2gv = null;
		adapter = null;
		root = null;
		appListFragment = null;

		System.gc();

		super.onDestroy();
	}

	@Override
	public void onResume() {
		if (mWorkspace != null) {
			if (mWorkspace.ad_page_count > 1) {
				mWorkspace.startAD();
			}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (mWorkspace != null) {
			mWorkspace.stopAD();
		}
		super.onPause();
	}

	private AdsViewPager mWorkspace;

	private void showADsWorkspace() {
		if (getActivity() == null) {
			return;
		}
		if (mHomePageDataResult != null) {
			ArrayList<ADInfo> ad_list = mHomePageDataResult.getAdsList();
			int ad_page_count = ad_list.size();
			if (ad_page_count == 0)
				return;

			if (ad_page_count == 2) {
				ad_list.add(ad_list.get(0));
				ad_list.add(ad_list.get(1));
			}

			ArrayList<View> ad_ivs = new ArrayList<View>();
			mADOnClickListener = new HomeAdOnClickListener(getActivity());
			for (int i = 0; i < ad_list.size(); i++) {
				ADInfo adInfo = ad_list.get(i);
				RoundCornerImageView iv_ad_view = new RoundCornerImageView(getActivity());
				iv_ad_view.setScaleType(ScaleType.FIT_XY);
				iv_ad_view.setOnClickListener(mADOnClickListener);
				iv_ad_view.setImageResource(R.drawable.ad_default);
				iv_ad_view.setScaleType(ScaleType.FIT_XY);
				iv_ad_view.setTag(adInfo);
				ad_ivs.add(iv_ad_view);
			}

			mWorkspace.init(ad_ivs, ad_list.size());
			mWorkspace.setBackgroundResource(0);
			showADPoints(ad_page_count);
			if (ad_ivs.size() < 2) {
				mWorkspace.stopAD();
			} else {
				mWorkspace.startAD();
			}
		}

	}

	private void showADPoints(int ad_page_count) {
		AdsPoints ad_points_layout = (AdsPoints) root.findViewById(R.id.home_ad_points);
		ad_points_layout.setChildCount(ad_page_count);
		ad_points_layout.setSelectBgRes(R.drawable.icon_point_ads_home_activity2);
		ad_points_layout.setNormalBgRes(R.drawable.icon_point_ads_home_activity2_current);
		if (ad_page_count == 1) {
			ad_points_layout.setVisibility(View.INVISIBLE);
		} else {
			ad_points_layout.setVisibility(View.VISIBLE);
		}

		mWorkspace.setAdsPointsView(ad_points_layout);
	}

	public static boolean turn2TabFourTag;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_section_newgames:
			Intent in = new Intent(getActivity(), NewGamesActivity.class);
			in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(in);
			ClickNumStatistics.addHomeTabNewGames(getActivity());
			break;
		case R.id.home_section_competition:
			Intent intent = new Intent(getActivity(), CompetitionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			ClickNumStatistics.addHomeCompetitionClickStatistics(getActivity());
			break;
		case R.id.home_section_classgames:
			Intent intentTopic = new Intent(getActivity(), BMProductDetailActivity.class);
			intentTopic.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentTopic);
			ClickNumStatistics.addHomeTabClassGames(getActivity());
			break;
		}

		super.onClick(v);
	}

	private HomeAdOnClickListener mADOnClickListener;

	private class HomeAdOnClickListener extends AdOnClickListener {

		public HomeAdOnClickListener(Activity act) {
			super(act);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			super.onClick(v);
			ClickNumStatistics.addHomeBannerStatistics(act);
		}
	}

	private MyPopupWindows guide_pop2, guide_pop;

	public void dismissGuidePop() {
		if (!getActivity().isFinishing()) {
			if (guide_pop != null) {
				guide_pop.dismiss();
			}
			if (guide_pop2 != null) {
				guide_pop2.dismiss();
			}
		}
	}

	public void showGuidePopTip() {
		int[] wh = UIUtil.getScreenPx(getActivity());
		int displayWidth = wh[0];
		View pop2_layout = View.inflate(getActivity(), R.layout.home_guide_pop_layout, null);
		TextView pop2_tv = (TextView) pop2_layout.findViewById(R.id.home_guide_pop_tv);
		pop2_tv.measure(0, 0);
		guide_pop2 = new MyPopupWindows(getActivity(), pop2_layout);
		// View v = root.findViewById(R.id.hall_title);
		// View manager_v = root.findViewById(R.id.rl_iv_manager_home_activity);
		// View menu_v = root.findViewById(R.id.hall_header_coin);
		// int device_dp = UIUtil.getDeviceDpi(getActivity());
		// guide_pop2.showAtBottom(v, displayWidth - menu_v.getWidth() -
		// (manager_v.getWidth() >> 1) - (pop2_tv.getMeasuredWidth() * 2 / 3),
		// -(int) (7 * ((device_dp == 0 ? 240 : device_dp)) / 160.0f),
		// R.style.home_guide_popup_animation);

	}

	// public void showHeaderCoinTip(int delay_time) {
	// // TODO Auto-generated method stub
	// // if (headerCoinTask != null)
	// // headerCoinTask.showHeaderCoinTip(delay_time);
	// }

}
