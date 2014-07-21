package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.HomeAppListAdapter;
import com.ranger.bmaterials.adapter.HomeAppListAdapter.CheckPackagesCallBack;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.mode.HomeAppListInfoArray;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListItemInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.HomeAppResult;
import com.ranger.bmaterials.netresponse.HomePageDataResult;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.ExpandablePullUpListView;
import com.ranger.bmaterials.view.ScrollEventScrollerView.ScrollListener;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class HomeAppListFragment extends Fragment implements ScrollListener {
	private ExpandablePullUpListView pullUpLv;

	public HomeAppListAdapter adapter;

	public PackageStatusListener packageStatusListener = new PackageStatusListener();

	private ScrollView scrollView;

	public void setScrollView(ScrollView scrollView) {
		this.scrollView = scrollView;
	}

	private View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);
			}
			return root;
		}

		root = inflater.inflate(R.layout.home_app_list_fragment_layout, null);
		root.setVisibility(View.GONE);
		pullUpLv = (ExpandablePullUpListView) root.findViewById(R.id.home_app_listview);
		pullUpLv.setFooter(createLoadingFooter());
		pullUpLv.showFooter();
		adapter = new HomeAppListAdapter(getActivity());
		adapter.setListView(pullUpLv);
		pullUpLv.setAdapter(adapter);
		HomePageDataResult dataResult = ((HomeFragment) getParentFragment()).mHomePageDataResult;
		if (dataResult != null) {
			showListTitle(dataResult);
			refreshListData(dataResult.getGamesList(), false, null);
		}
		return root;
	}

	public void showListTitle(HomePageDataResult responseData) {
		if (responseData == null)
			return;
		if (root != null) {
			// 列表标题
			String listTitle = responseData.getGameListTitle();
			View titleLayout = root.findViewById(R.id.home_app_list_title_layout);
			if (listTitle == null || listTitle.equalsIgnoreCase("")) {
				titleLayout.setVisibility(View.GONE);
			} else {
				titleLayout.setVisibility(View.VISIBLE);
				TextView title = (TextView) root.findViewById(R.id.home_app_list_title);
				title.setText("热门推荐");
			}
		}
	}

	public void refreshListData(ArrayList<HomeAppListInfoArray> infos, boolean isMore, final HomeAppResult result) {
		aysnCheckPackages(infos, new CheckPackagesCallBack() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				if (!root.isShown())
					root.setVisibility(View.VISIBLE);

				if (adapter == null) {
					return;
				}
				adapter.notifyDataSetChanged();
				if (result != null) {
					isDataEnd = adapter.getList().size() >= result.gamescount;
					if (isDataEnd)
						showNoDataFooter(false);
					else
						current_page_index.incrementAndGet();
				}

				if (pullUpLv != null)
					pullUpLv.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (getActivity() == null)
								return;

							HomeFragment act = (HomeFragment) getParentFragment();
							if (!act.isRegisterPackage.get()) {
								PackageHelper.registerPackageStatusChangeObserver(act.packageCallBack);
								act.isRegisterPackage.set(true);
							}
						}
					});
			}
		}, isMore);
	}

	private View createNoDataFooter() {
		if (getActivity() == null)
			return null;
		View v = View.inflate(getActivity(), R.layout.home_footer_layout, null);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			}
		});
		return v;
	}

	private View createLoadingFooter() {
		View footer = View.inflate(getActivity(), R.layout.loading_layout, null);
		TextView subView = (TextView) footer.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		return footer;
	}

	@Override
	public void OnScrollBottom() {
		if (getActivity() == null) {
			return;
		}
		if (DeviceUtil.isNetworkAvailable(getActivity())) {
			if (!isDataEnd)
				loadMoreData(new LoadMoreRequestHandler());
		} else
			requestMoreError();
	}

	private AtomicInteger current_page_index = new AtomicInteger(1);

	private void loadMoreData(final IRequestListener l) {
		LoadingTask requestDataTask = new LoadingTask(getActivity(), new ILoading() {

			@Override
			public void loading(IRequestListener listener) {
				// TODO Auto-generated method stub
				NetUtil.getInstance().requestHomeMoreData(current_page_index.get(), listener);
			}

			@Override
			public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
				pullUpLv.hideFooter();
				pullUpLv.setFooter(createLoadingFooter());
				pullUpLv.showFooter();
			}

			@Override
			public boolean isShowNoNetWorkView() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public IRequestListener getRequestListener() {
				// TODO Auto-generated method stub
				return l;
			}

			@Override
			public boolean isAsync() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		requestDataTask.loading();
	}

	private boolean isDataEnd;

	private class LoadMoreRequestHandler implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			// TODO Auto-generated method stub
			HomeAppResult result = (HomeAppResult) responseData;
			ArrayList<HomeAppListInfoArray> list = result.getGamesList();

			if (list == null || list.isEmpty()) {
				showNoDataFooter(list != null && list.isEmpty());
				isDataEnd = true;
			} else
				refreshListData(list, true, result);
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			// TODO Auto-generated method stub
			requestMoreError();
		}
	}

	private void requestMoreError() {
		showNoDataFooter(true);
		CustomToast.showToast(getActivity(), getString(R.string.network_error_hint));
	}

	private void showNoDataFooter(boolean error) {

		if (pullUpLv == null || adapter == null) {
			return;
		}
		pullUpLv.hideFooter();
		pullUpLv.setFooter(createNoDataFooter());
		pullUpLv.showFooter();
		adapter.notifyDataSetChanged();
		pullUpLv.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		if (!error) {
			if (getActivity() != null)
				GeneralStatistics.addHomeLoadAllDevicesStatistics(getActivity());
		}
	}

	public class PackageStatusListener implements PackageCallback {

		@Override
		public void onPackageStatusChanged(final PackageMode packageMode) {
			// TODO Auto-generated method stub
			// 当删除所有安装包的时候会变成重置 需要重查状态
			if (packageMode.status == PackageMode.RESET_STATUS) {
				checkPackages(adapter.getList(), new CheckPackagesCallBack() {

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						if (adapter != null) {
							adapter.notifyDataSetChanged();
						}
					}
				}, false);
				return;
			} else if (adapter != null)
				adapter.partRefresh(packageMode);
		}

	}

	private void checkPackages(ArrayList<HomeAppListInfoArray> sourcelist, final CheckPackagesCallBack callBack, final boolean isAdd) {
		final ArrayList<HomeAppListInfoArray> info_list = new ArrayList<HomeAppListInfoArray>(sourcelist);
		for (HomeAppListInfoArray homeInfoArray : info_list) {

			ArrayList<QueryInput> qiList = new ArrayList<QueryInput>();
			for (HomeAppListItemInfo homeAppListInfos : homeInfoArray.homeAppListInfos) {
				QueryInput qi = new QueryInput(homeAppListInfos.pkgname, homeAppListInfos.versionname, homeAppListInfos.versioncode == null || homeAppListInfos.versioncode.equals("") ? -1
						: Integer.valueOf(homeAppListInfos.versioncode), homeAppListInfos.downloadurl, homeAppListInfos.gameid);

				qiList.add(qi);
			}

			Map<QueryInput, PackageMode> status = PackageHelper.queryPackageStatus(qiList);

			if (status == null)
				return;
			for (QueryInput qi : status.keySet()) {
				int pos = qiList.indexOf(qi);
				HomeAppListItemInfo homeInfo = homeInfoArray.homeAppListInfos.get(pos);
				homeInfo.packageMode = status.get(qi);
				homeInfo.downloadId = homeInfo.packageMode.downloadId;
			}

			if (homeInfoArray.bannerInfo == null)
				continue;
			QueryInput banner_qi = new QueryInput(homeInfoArray.bannerInfo.pkgname, homeInfoArray.bannerInfo.versionname, homeInfoArray.bannerInfo.versioncode == null
					|| homeInfoArray.bannerInfo.versioncode.equals("") ? -1 : Integer.valueOf(homeInfoArray.bannerInfo.versioncode), homeInfoArray.bannerInfo.downloadurl,
					homeInfoArray.bannerInfo.gameid);

			Map<QueryInput, PackageMode> banner_status = PackageHelper.queryPackageStatus(banner_qi);
			if (banner_status == null) {
				return;
			}
			homeInfoArray.bannerInfo.packageMode = banner_status.get(banner_qi);
			homeInfoArray.bannerInfo.downloadId = homeInfoArray.bannerInfo.packageMode.downloadId;

		}

		if (getActivity() != null)
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isAdd)
						adapter.addList(info_list);
					else
						adapter.setList(info_list);
					callBack.onFinish();
				}
			});
	}

	private void aysnCheckPackages(final ArrayList<HomeAppListInfoArray> info_list, final CheckPackagesCallBack callBack, final boolean isAdd) {
		if (info_list == null || info_list.isEmpty())
			return;
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (adapter != null && adapter.getList() != null) {
					synchronized (adapter.getList()) {
						checkPackages(info_list, callBack, isAdd);
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();

	}

	@Override
	public void onDestroy() {
		if (pullUpLv != null) {
			pullUpLv.setAdapter(null);
			pullUpLv = null;
		}
		super.onDestroy();
	}

}
