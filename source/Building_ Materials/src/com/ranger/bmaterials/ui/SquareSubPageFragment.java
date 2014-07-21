package com.ranger.bmaterials.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.AbstractListAdapter;
import com.ranger.bmaterials.adapter.GameActivityAdapter;
import com.ranger.bmaterials.adapter.GameAppraisalAdapter;
import com.ranger.bmaterials.adapter.GameReleaseAdapter;
import com.ranger.bmaterials.adapter.SnapNumberAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.mode.ActivityInfoList;
import com.ranger.bmaterials.mode.OpenServer;
import com.ranger.bmaterials.mode.OpenServerList;
import com.ranger.bmaterials.mode.SnapNumber;
import com.ranger.bmaterials.mode.SnapNumberList;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

/////////////////////////////////////////////////////////////

public class SquareSubPageFragment extends Fragment implements OnRefreshListener2<ListView>, OnItemClickListener, OnClickListener {
	public static final int PAGE_SNAPNUMBER = 3;
	public static final int PAGE_ADDSERVER = 4;
	public static final int PAGE_ACTIIVTIY = 2;
	public static final int PAGE_APPRAISAL = 0;
	public static final int PAGE_INFORMATION = 1;
	public static final int APPRAISAL_TYPE = 0;
	public static final int INFORMATION_TYPE = 1;

	public static final String ARG_CURRENT_PAGE = "arg_page";
	public static final String ARG_TARGET_DATA_PAGE = "data_page";
	public static final String ARG_HAS_MORE = "data_has_more";
	public static final String ARG_CURRENT_ACTION = "current_action";

	/**
	 * 数据的页数
	 */
	private int dataPage = -1;
	/**
	 * 当前子页面
	 */
	private int currentPage;

	private PullToRefreshListView plv;
	private View loadingLayout;
	protected AbstractListAdapter adapter;

	/**
	 * pager的FramgmentPagerAdapter是可能多次调用onCreateView,onActivityCreated,onStart,
	 * onResume
	 * 
	 */
	private boolean dataAutoRequestFinished = false;

	/**
	 * 只要设置它为false，就一定重新生成view和adapter
	 */
	private boolean reuseView = true;
	private View savedViewParent;

	private boolean isReuseView() {
		return reuseView && savedViewParent != null && savedViewParent != null;
	}

	public static SquareSubPageFragment newInstance(int page) {
		SquareSubPageFragment f = null;
		if (page == 3) {
			f = new SquareSnapNumberFragment();
		} else {
			f = new SquareSubPageFragment();
		}
		Bundle b = new Bundle();
		b.putInt(ARG_CURRENT_PAGE, page);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentPage = getArguments().getInt(ARG_CURRENT_PAGE);
	}

	protected void showLoadingView() {
		plv.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
	}

	private void showErrorView() {
		if (savedViewParent == null) {
			return;
		}
		plv.setVisibility(View.INVISIBLE);
		plv.onRefreshComplete();
		errorView.setVisibility(View.VISIBLE);
		loadingLayout.setVisibility(View.INVISIBLE);
	}

	private void showContentView() {
		if (savedViewParent == null) {
			return;
		}
		plv.setVisibility(View.VISIBLE);
		plv.onRefreshComplete();
		errorView.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			try {
				if (savedInstanceState.containsKey(ARG_CURRENT_PAGE)) {
					currentPage = savedInstanceState.getInt(ARG_CURRENT_PAGE, currentPage);
				}
				if (savedInstanceState.containsKey(ARG_TARGET_DATA_PAGE)) {
					dataPage = savedInstanceState.getInt(ARG_TARGET_DATA_PAGE, 0);
				}
				if (savedInstanceState.containsKey(ARG_HAS_MORE)) {
					hasMore = savedInstanceState.getBoolean(ARG_HAS_MORE);
				}

			} catch (Exception e) {
			}

		}
		if (!isReuseView() || !dataAutoRequestFinished) {
			View viewParent = inflater.inflate(R.layout.square_activity_subpage, null);
			plv = (PullToRefreshListView) viewParent.findViewById(R.id.pull_refresh_list);
			plv.setOnRefreshListener(this);

			// ListView listView = plv.getRefreshableView();
			errorView = viewParent.findViewById(R.id.loading_error_layout);
			errorView.setOnClickListener(this);

			// listView.setEmptyView(errorView);
			// listView.setOnScrollListener(this);

			loadingLayout = viewParent.findViewById(R.id.network_loading_pb);
			this.savedViewParent = viewParent;

			showLoadingView();
		} else {
			ViewParent parent = this.savedViewParent.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.savedViewParent);
			}
		}
		return savedViewParent;
	}

	private boolean createAdapter() {

		if (plv.getRefreshableView().getAdapter() != null) {
			return false;
		}

		if (adapter == null) {
			if (currentPage == PAGE_SNAPNUMBER) {
				adapter = new SnapNumberAdapter(getActivity());
			} else if (currentPage == PAGE_ADDSERVER) {
				adapter = new GameReleaseAdapter(getActivity());
			} else if (currentPage == PAGE_ACTIIVTIY) {
				adapter = new GameActivityAdapter(getActivity());
			} else if (currentPage == PAGE_APPRAISAL) {
				adapter = new GameAppraisalAdapter(getActivity(), R.drawable.appraisal_default_launcher);
			} else if (currentPage == PAGE_INFORMATION) {
				adapter = new GameAppraisalAdapter(getActivity(), R.drawable.information_default_launcher);
			}
		}
		return true;
	}

	private void initView() {
		createAdapter();
		ListView listView = plv.getRefreshableView();

		plv.setOnLastItemVisibleListener(OnLastItemVisibleListener);
		footer = createFooter();
		listView.addFooterView(footer);

		listView.setOnItemClickListener(this);
	}

	private OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if (!isLoadingMore && hasMore) {
				if (DeviceUtil.isNetworkAvailable(getActivity())) {
					setFooterVisible(true);
					isLoadingMore = true;
					loadMore();
				} else {
					CustomToast.showToast(GameTingApplication.getAppInstance(), "网络不给力!");
					setFooterVisible(false);
					isLoadingMore = false;
				}
			}
		}
	};
	private boolean isLoadingMore;
	private boolean hasMore = true;

	private void setFooterVisible(boolean visible) {
		ListView listView = plv.getRefreshableView();
		if (visible) {
			if (listView.getFooterViewsCount() == 0)
				listView.addFooterView(footer);
			footer.setVisibility(View.VISIBLE);
		} else {
			// listView.removeFooterView(footer);
			footer.setVisibility(View.GONE);
		}
	}

	private View createFooter() {
		View view = View.inflate(this.getActivity(), R.layout.loading_layout, null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}

	private View createNoMoreFooter() {
		View view = View.inflate(getActivity(), R.layout.home_footer_layout, null);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToNextActivity(currentPage);
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// setRetainInstance(true);
		if (!isReuseView() || !dataAutoRequestFinished) {
			initView();
			initData();
		}
	}

	private void initData() {
		refreshTriggered();
	}

	// OnRefreshListener2监听
	// 刷新
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (DeviceUtil.isNetworkAvailable(getActivity())) {
			// plv.setVisibility(View.INVISIBLE);
			// showLoadingView();
			refreshTriggered();
		} else {
			CustomToast.showToast(getActivity(), getActivity().getString(R.string.alert_network_inavailble));
			// Toast.makeText(getActivity(), "网络不给力!",
			// Toast.LENGTH_LONG).show();
			plv.onRefreshComplete();
			showErrorView();
		}

	}

	// OnRefreshListener2监听
	// 加载更多
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ARG_CURRENT_PAGE, currentPage);
		outState.putInt(ARG_TARGET_DATA_PAGE, dataPage);
		outState.putBoolean(ARG_HAS_MORE, hasMore);
		// outState.putParcelableArrayList(key, value)
		super.onSaveInstanceState(outState);
	}

	/**
	 * 
	 * @param isRefreshing
	 * @param hasData
	 */
	protected void setDataPage(boolean isRefreshing, boolean hasData) {
		if (!isRefreshing && hasData) {
			dataPage++;
		} else {
			if (hasData) {
				dataPage = 0;
			}

		}
		// Log.d(TAG, "setDataPage "+dataPage);

		if (Constants.DEBUG)
			Log.i("LongTest", "[setDataPage]dataPage? " + dataPage);

	}

	/**
	 * 本地页码从0开始，服务器从1开始
	 */
	private void loadMore() {
		switch (currentPage) {
		case PAGE_SNAPNUMBER:
			loadNumberList((dataPage + 1), loadMoreListener);
			break;
		case PAGE_ADDSERVER:
			loadOpenServersList((dataPage + 1), loadMoreListener);
			break;
		case PAGE_ACTIIVTIY:
			loadActivitiesList((dataPage + 1), loadMoreListener);
			break;
		case PAGE_APPRAISAL:
			loadAppraisalList((dataPage + 1), APPRAISAL_TYPE, loadMoreListener);
			break;
		case PAGE_INFORMATION:
			loadAppraisalList((dataPage + 1), INFORMATION_TYPE, loadMoreListener);
			break;
		default:
			break;
		}

	}

	/**
	 * 代码触发
	 */
	protected void triggerRefresh() {
		showContentView();
		setFooterVisible(false);
		plv.setRefreshing();
	}

	/**
	 * 本地页码从0开始，服务器从1开始
	 */
	protected void refreshTriggered() {

		switch (currentPage) {
		case PAGE_SNAPNUMBER:
			loadNumberList(0, refreshListener);
			break;
		case PAGE_ADDSERVER:
			loadOpenServersList(0, refreshListener);
			break;
		case PAGE_ACTIIVTIY:
			loadActivitiesList(0, refreshListener);
			break;
		case PAGE_APPRAISAL:
			loadAppraisalList(0, APPRAISAL_TYPE, refreshListener);
			break;
		case PAGE_INFORMATION:
			loadAppraisalList(0, INFORMATION_TYPE, refreshListener);
			break;
		default:
			break;
		}
	}

	private int defaultPageSize = 10;

	private void loadNumberList(int targetPage, IRequestListener listener) {
		MineProfile profile = MineProfile.getInstance();
		String sessionId = profile.getSessionID();
		boolean isLogin = profile.getIsLogin();
		String userId = null;
		if (isLogin) {
			userId = profile.getUserID();
		}
		NetUtil.getInstance().requestForSnapNumberList(userId, sessionId, (targetPage + 1), defaultPageSize, listener);
	}

	private void loadOpenServersList(int targetPage, IRequestListener listener) {
		NetUtil.getInstance().requestForOpenServersList((targetPage + 1), defaultPageSize, listener);
	}

	private void loadActivitiesList(int targetPage, IRequestListener listener) {
		NetUtil.getInstance().requestForActivitiesList((targetPage + 1), defaultPageSize, listener);
	}

	private void loadAppraisalList(int targetPage, int appiaisaltype, IRequestListener listener) {
		NetUtil.getInstance().requestForAppraisalList((targetPage + 1), defaultPageSize, appiaisaltype, listener);
	}

	private IRequestListener refreshListener = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			boolean check = check();
			if (!check) {
				return;
			}
			dataAutoRequestFinished = true;
			hasMore = true;
			plv.onRefreshComplete();
			if (responseData.getErrorCode() == DcError.DC_OK) {
				showContentView();
				List data = null;
				if (currentPage == PAGE_ACTIIVTIY) {
					ActivityInfoList activities = (ActivityInfoList) responseData;
					data = activities.getData();
				} else if (currentPage == PAGE_ADDSERVER) {
					OpenServerList serversList = (OpenServerList) responseData;
					data = serversList.getData();
				} else if (currentPage == PAGE_SNAPNUMBER) {
					SnapNumberList numberList = (SnapNumberList) responseData;
					data = numberList.getData();
				} else if (currentPage == PAGE_APPRAISAL || currentPage == PAGE_INFORMATION) {
					ActivityInfoList activities = (ActivityInfoList) responseData;
					data = activities.getData();
				}
				notifyLoadResult(true, data);
			} else {
				// 出错
				if (adapter.getData() != null && adapter.getCount() > 0) {
					FragmentActivity activity = getActivity();
					if (activity != null) {
						CustomToast.showToast(getActivity(), getString(R.string.get_data_failed));
						// Toast.makeText(activity, "获取数据失败!",
						// Toast.LENGTH_LONG).show();
					}
				} else {
					showErrorView();
				}

			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			boolean check = check();
			if (!check) {
				return;
			}
			hasMore = true;
			dataAutoRequestFinished = true;
			plv.onRefreshComplete();
			if (adapter.getData() != null && adapter.getCount() > 0) {
				FragmentActivity activity = getActivity();
				if (activity != null) {
					CustomToast.showToast(getActivity(), getString(R.string.get_data_failed));
				}
			} else {
				showErrorView();
			}

		}
	};

	private boolean check() {
		FragmentActivity activity = getActivity();
		if (activity == null || activity.isFinishing()) {
			return false;
		}
		return true;
	}

	private IRequestListener loadMoreListener = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			boolean check = check();
			if (!check) {
				return;
			}
			showContentView();
			isLoadingMore = false;
			if (responseData.getErrorCode() == DcError.DC_OK) {
				List data = null;
				if (currentPage == PAGE_ACTIIVTIY) {
					ActivityInfoList activities = (ActivityInfoList) responseData;
					data = activities.getData();
				} else if (currentPage == PAGE_ADDSERVER) {
					OpenServerList serversList = (OpenServerList) responseData;
					data = serversList.getData();
				} else if (currentPage == PAGE_SNAPNUMBER) {
					SnapNumberList numberList = (SnapNumberList) responseData;
					data = numberList.getData();
					/*
					 * if(data != null){ data = new CopyOnWriteArrayList(data);
					 * }
					 */
				} else if (currentPage == PAGE_APPRAISAL || currentPage == PAGE_INFORMATION) {
					ActivityInfoList activities = (ActivityInfoList) responseData;
					data = activities.getData();
				}
				notifyLoadResult(false, data);

			} else {
				FragmentActivity activity = getActivity();
				// 出错
				if (activity != null) {
					CustomToast.showToast(getActivity(), getString(R.string.get_more_data_failed));
				}
			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			boolean check = check();
			if (!check) {
				return;
			}
			// showErrorView();
			FragmentActivity activity = getActivity();
			if (activity != null) {
				CustomToast.showToast(getActivity(), getString(R.string.get_more_data_failed));

			}
			isLoadingMore = false;
			setFooterVisible(false);
		}
	};

	private View errorView;

	private View footer;

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (adapter == null)
			return;

		Intent intent = null;
		if (currentPage == PAGE_ACTIIVTIY) {
			ActivityInfo item = (ActivityInfo) adapter.getItem(position);
			if (item == null)
				return;
			intent = new Intent(getActivity(), ActivityDetailActivity.class);
			intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, item);
			ClickNumStatistics.addSquareActivityDetailStatistics(getActivity());
		} else if (currentPage == PAGE_ADDSERVER) {
			OpenServer item = (OpenServer) adapter.getItem(position);
			if (item == null)
				return;
			intent = new Intent(getActivity(), OpenServerDetailActivity.class);

			intent.putExtra(OpenServerDetailActivity.ARG_GAME_ID, item.getGameId());
			intent.putExtra(OpenServerDetailActivity.ARG_OPENSERVER_ID, item.getId());
			ClickNumStatistics.addSquareOpenServiceDetailStatistics(getActivity());
		} else if (currentPage == PAGE_SNAPNUMBER) {
			SnapNumber item = (SnapNumber) adapter.getItem(position);
			if (item == null)
				return;
			intent = new Intent(getActivity(), SnapNumberDetailActivity.class);
			MineProfile profile = MineProfile.getInstance();
			String gameId = item.getGameId();
			String grabId = item.getId();
			String userID = profile.getUserID();
			String sessionID = profile.getSessionID();
			boolean isLogin = profile.getIsLogin();
			if (isLogin) {
				intent.putExtra(SnapNumberDetailActivity.ARG_GAMEID, gameId);
				intent.putExtra(SnapNumberDetailActivity.ARG_GRABID, grabId);
				intent.putExtra(SnapNumberDetailActivity.ARG_USERID, userID);
				intent.putExtra(SnapNumberDetailActivity.ARG_SESSIONID, sessionID);
			} else {
				intent.putExtra(SnapNumberDetailActivity.ARG_GAMEID, gameId);
				intent.putExtra(SnapNumberDetailActivity.ARG_GRABID, grabId);
			}

			intent.putExtra(SnapNumberDetailActivity.ARG_NUMBER, item);
			ClickNumStatistics.addSquareSnapNumDetailStatistics(getActivity());
		} else if (currentPage == PAGE_APPRAISAL || currentPage == PAGE_INFORMATION) {// 测评
			ActivityInfo item = (ActivityInfo) adapter.getItem(position);
			if (item == null)
				return;
			intent = new Intent(getActivity(), AppraisalDetailActivity.class);
			intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, item);
			intent.putExtra(ARG_CURRENT_PAGE, currentPage);

			if (currentPage == PAGE_APPRAISAL) {
				ClickNumStatistics.addSquareAppraisalDetailStatistics(getActivity());
			} else {
				ClickNumStatistics.addSquareInformationDetailStatistics(getActivity());
			}
		}
		if (intent != null)
			getActivity().startActivity(intent);
	}

	private void notifyLoadResult(boolean refresh, List data) {
		if (data == null) {
			setFooterVisible(false);
			if (!refresh) {
				hasMore = false;
			}
			CustomToast.showToast(getActivity(), getString(R.string.get_data_failed));
			// Toast.makeText(getActivity(), "获取数据失败",
			// Toast.LENGTH_LONG).show();
		} else if (data.size() == 0) {
			if (currentPage > 0) {
				ListView listview = plv.getRefreshableView();
				listview.removeFooterView(footer);
				footer = createNoMoreFooter();
				listview.addFooterView(footer);
			} else
				setFooterVisible(false);
			if (!refresh) {
				hasMore = false;
			}
			CustomToast.showToast(getActivity(), getString(R.string.no_new_data));
			// Toast.makeText(getActivity(), "没有新的数据",
			// Toast.LENGTH_LONG).show();
		} else {
			boolean flag = createAdapter();
			if (flag) {
				plv.setAdapter(adapter);
			}
			if (!refresh) {
				// 加载更多
				setDataPage(false, true);
				fillLoadData(false, data);
			} else if (refresh) {
				// 刷新
				if (footer != null) {
					ListView listview = plv.getRefreshableView();
					listview.removeFooterView(footer);
					footer = createFooter();
					listview.addFooterView(footer);
				}
				setDataPage(true, true);
				fillLoadData(true, data);

			}
		}
	}

	private void jumpToNextActivity(int currentPage) {
	}

	private void fillLoadData(boolean isRefresh, List data) {
		if (isRefresh) {
			adapter.clear();
			adapter.addAll(data);
			plv.getRefreshableView().setSelection(0);
		} else if (!isRefresh) {
			adapter.addAll(data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_error_layout:

			if (DeviceUtil.isNetworkAvailable(getActivity())) {
				showLoadingView();
				refreshTriggered();
			} else {
				CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
				showErrorView();
			}
			break;

		default:
			break;
		}
	}

	protected void onNetworkInvalid() {
		plv.onRefreshComplete();
		showErrorView();
		CustomToast.showToast(getActivity(), getString(R.string.exit_app_hint));
	}
}