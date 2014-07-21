package com.ranger.bmaterials.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.SearchRecommedAdapter;
import com.ranger.bmaterials.adapter.SearchResultAdapter;
import com.ranger.bmaterials.adapter.AbstractListAdapter.OnListItemClickListener;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.AppManager.GameStatus;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemListener;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.*;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.tools.install.InstallPacket;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

public class SearchResultActivity extends Activity implements
/* IRequestListener, */OnClickListener, OnItemClickListener{

	private static final String TAG = "SearchResultActivity";

	protected static final boolean DEBUG = true;

	public static final String ARG_KEYWORD = "keywords";

	private PullToRefreshListView searchResultLayout;
	private ViewGroup searchNoResultLayout;
	private View loadingView;

	private SearchResultAdapter searchResultAdapter;
	private int requestId;
	private View searchRecomendHintView;

	/** 推荐的GridView */
	private GridView recomendGv;

	private View errorView;
	private SearchRecommedAdapter recommedAdapter;

	private View footer;

	/** 分页的页码 */
	private int currentPage = 0;
	
	private Dialog rootDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search_result_activity);
		
		Intent intent = getIntent();

		
		restore(savedInstanceState);

		initTitleBar();
		initView();

		showLoadingProgressView();

		registerListener();

		// commented by wangliang
		// registerReceiver();
		search(currentPage + 1, false);

	}

	private void restore(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null
					&& savedInstanceState.containsKey(PAGE)) {
				currentPage = savedInstanceState.getInt(PAGE);
			}
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

	OnItemDownloadListener onItemDownloadListener = new OnItemDownloadListener();
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_PROGRESS:
				if (Constants.DEBUG)
					Log.i("wangliang",
							"refreshDownloadPregress "
									+ Thread.currentThread().getName() + ":"
									+ System.currentTimeMillis() / 1000);
				String gameId = (String) msg.obj;
				refreshDownloadProgress(gameId);
				break;
			case MSG_REFRESH_TITLE_COUNT:
				break;
			}
		}
	};

	private static final int MSG_REFRESH_PROGRESS = 300;
	private static final int MSG_REFRESH_TITLE_COUNT = 301;

	class OnItemDownloadListener implements DownloadItemListener {

		@Override
		public void onDownloadProcessing(DownloadItemOutput o) {
			// if (true) {
			// long now = System.currentTimeMillis();
			// // if (Constants.DEBUG)Log.i("wangliang",
			// // "onDownloadProcessing now:"+now+" last:"+last+" 间隔："+(now -
			// // last));
			// last = now;
			// // if (Constants.DEBUG)Log.i("SearchResultAdapter",
			// // "onDownloadProcessing "+Thread.currentThread().getName()+":"
			// // +System.currentTimeMillis()/1000);
			// }
			if (searchResultAdapter == null) {
				return;
			}
			List<SearchItem> data = searchResultAdapter.getData();
			if (data == null)
				return;
			// searchResultAdapter.setNotifyOnChange(false);
			for (SearchItem item : data) {
				String downloadUrl = item.getDownloadUrl();
				if (downloadUrl != null && downloadUrl.equals(o.getUrl())) {
					long currentBytes = o.getCurrentBytes();
					long totalBytes = o.getTotalBytes();
					item.setCurrentBytes(currentBytes);
					item.setTotalBytes(totalBytes);
					DownloadStatus status = o.getStatus();
					item.setDownloadStatus(status);
					item.setDownloadReason(o.getReason());
					switch (status) {
					case STATUS_FAILED:
					case STATUS_PAUSED:
					case STATUS_RUNNING:
					case STATUS_PENDING:
						item.setStatus(GameStatus.DOWNLOADING);
						break;
					case STATUS_SUCCESSFUL:
						item.setStatus(GameStatus.DONWLOADED);
						addDownloadedData(item.getGameId(), o.getDest());
						break;
					}
					Message msg = new Message();
					msg.what = MSG_REFRESH_PROGRESS;
					msg.obj = item.getGameId();
					handler.sendMessage(msg);
					if (Constants.DEBUG)
						log();
					if (Constants.DEBUG)
						Log.i("SearchResultAdapter", "onDownloadProcessing "
								+ "" + o.getTitle() + "," + o.getStatus());
				}
			}
		}

		private void log() {
			List<SearchItem> data = searchResultAdapter.getData();
			for (SearchItem searchItem : data) {

				Object[] arrayOfObject = new Object[5];
				arrayOfObject[0] = searchItem.getGameId();
				arrayOfObject[1] = searchItem.getGameName();
				arrayOfObject[2] = searchItem.getStatus();
				arrayOfObject[3] = searchItem.getDownloadStatus();
				arrayOfObject[4] = searchItem.getInstalleStatus();

				Log.v("SearchResultAdapter",
						String.format(
								"[SearchList List item:id=%s,title=%s,status=%s,downloadStatus=%s,InstalleStatus=%s]",
								arrayOfObject));
			}
		}

	}

	private void refreshDownloadProgress(final String gameId) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {

					ListView refreshableView = searchResultLayout
							.getRefreshableView();
					// searchResultAdapter.notifyDataSetChanged();
					// 这样也可以，不用刷新整个ListView
					((SearchResultAdapter) searchResultAdapter).updateItemView(
							refreshableView, gameId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * no use
	 * 
	 * @author wangliang
	 * 
	 */
	/*
	 * class AllDownloadListener implements DownloadListener {
	 * 
	 * @Override public void onDownloadProcessing(List<DownloadItemOutput>
	 * items) { List<SearchItem> data = searchResultAdapter.getData(); if (data
	 * != null && items != null) { for (SearchItem item : data) { String
	 * downloadUrl = item.getDownloadUrl(); for (DownloadItemOutput s : items) {
	 * if (downloadUrl.equals(s.getUrl())) { long currentBytes =
	 * s.getCurrentBytes(); long totalBytes = s.getTotalBytes();
	 * item.setCurrentBytes(currentBytes); item.setTotalBytes(totalBytes);
	 * DownloadStatus status = s.getStatus(); //
	 * app.setSaveDest(file.getDest()); switch (status) { case STATUS_FAILED:
	 * break; case STATUS_PAUSED: break; case STATUS_RUNNING: case
	 * STATUS_PENDING: break; case STATUS_SUCCESSFUL: break; }
	 * 
	 * } }
	 * 
	 * } } }
	 * 
	 * }
	 */

	private void initTitleBar() {
		View backView = findViewById(R.id.btn_back);
		backView.setOnClickListener(this);
//		titleLeftText = (TextView) findViewById(R.id.label_title);
//		titleLeftText.setText("搜索结果");
	}

	boolean isLoadingMore = false;
	boolean hasMore = true;

	OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if (Constants.DEBUG)
				Log.i(TAG, "onLastItemVisible");
			if (DeviceUtil.isNetworkAvailable(getApplicationContext())
					&& !getLoadlingMoreState() && hasMore()) {
				setFooterVisible(true);
				setLoadlingMoreState(true);
				search(currentPage + 1, true);
			} else {
				// searchResultLayout.onRefreshComplete();
			}
		}
	};

	private View createFooter() {
		View view = View.inflate(this, R.layout.loading_layout, null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}
	
	private ForegroundColorSpan fcc_keyword = new ForegroundColorSpan(Color.parseColor("#478ddc"));
	private ForegroundColorSpan fcc_no_data = new ForegroundColorSpan(Color.parseColor("#989898"));
	
	private View footer_view;
	
	private View createNoMoreDataFooter(final String key){
		
		if(footer_view == null){
			footer_view = View.inflate(getApplicationContext(), R.layout.layout_listview_footer_nomoredata, null);
		}
		final String default_keyword = getString(R.string.search_result_no_more_data);
		
		SpannableString ss_default = new SpannableString(default_keyword + "  \"" + key+ "\"");
		ss_default.setSpan(fcc_no_data, 0,default_keyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		ss_default.setSpan(fcc_keyword, default_keyword.length(), ss_default.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		TextView tvFooter = (TextView) footer_view.findViewById(R.id.tv_footer_no_data);
		tvFooter.setText(ss_default);

        footer_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                et_search.setText(key);
                showLoadingProgressView();
                search();
            }
        });
		
		return footer_view;
	}


	private void initView() {
		searchResultLayout = (PullToRefreshListView) findViewById(R.id.layout_search_result_list);
		searchResultLayout.setOnRefreshListener(new MyOnRefreshListener2());
		searchResultLayout.setOnItemClickListener(this);
		searchResultLayout
				.setOnLastItemVisibleListener(OnLastItemVisibleListener);
		footer = createFooter();
		searchResultLayout.getRefreshableView().addFooterView(footer);

		searchNoResultLayout = (ViewGroup) findViewById(R.id.layout_search_subview_no_result);
		searchRecomendHintView = searchNoResultLayout
				.findViewById(R.id.label_recomend_hint);
		recomendGv = (GridView) searchNoResultLayout
				.findViewById(R.id.search_recomend_gv);
		// recomendGv.setScrollContainer(false);
		loadingView = findViewById(R.id.progress_bar);

		errorView = findViewById(R.id.error_hint);
		errorView.setVisibility(View.GONE);
		errorView.setOnClickListener(this);
	}

	class MyOnRefreshListener2 implements OnRefreshListener2<ListView> {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (DeviceUtil.isNetworkAvailable(getApplicationContext())) {
				search(currentPage + 1, true);
			} else {
				CustomToast.showToast(getApplicationContext(),
						getString(R.string.alert_network_inavailble));
				searchResultLayout.onRefreshComplete();
			}
		}
	}

	private void showLoadingProgressView() {
		loadingView.setVisibility(View.VISIBLE);
		searchResultLayout.setVisibility(View.GONE);
		searchNoResultLayout.setVisibility(View.GONE);
		errorView.setVisibility(View.GONE);
	}

	private void showErrorView() {
		loadingView.setVisibility(View.GONE);
		searchResultLayout.setVisibility(View.GONE);
		searchNoResultLayout.setVisibility(View.GONE);
		errorView.setVisibility(View.VISIBLE);
	}

    private void showLoadingMoreFooter(){
        if(footer != null){
            try{
                searchResultLayout.getRefreshableView().removeFooterView(footer);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(footer_view != null){
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer_view);
            } catch (Exception e) {
            }
        }

        searchResultLayout.getRefreshableView().addFooterView(footer);
    }

	//显示没有更多的提示
	private void showNoMoreView(){
		if(footer != null){
			try{
				searchResultLayout.getRefreshableView().removeFooterView(footer);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		if(footer_view != null){
			try {
				searchResultLayout.getRefreshableView().removeFooterView(footer_view);
			} catch (Exception e) {
			}
		}
		
		searchResultLayout.getRefreshableView().addFooterView(createNoMoreDataFooter(KeywordsList.getInstance().getRandomRecomKeyword()));
		
	}

	private void showSearchResultView() {
		loadingView.setVisibility(View.GONE);
		searchNoResultLayout.setVisibility(View.GONE);
		errorView.setVisibility(View.GONE);
		searchResultLayout.setVisibility(View.VISIBLE);

	}

	private void showSearchNoResultView() {
		loadingView.setVisibility(View.GONE);
		errorView.setVisibility(View.GONE);
		searchResultLayout.setVisibility(View.GONE);
		searchNoResultLayout.setVisibility(View.VISIBLE);

        TextView tv_noresult_hint = (TextView) findViewById(R.id.tv_no_searchresult_hint);
        String default_keyword = getString(R.string.no_keywords);
        String no_data_hint = getString(R.string.string_with_quote);
        SpannableString ss = new SpannableString(default_keyword + no_data_hint);
        ss.setSpan(fcc_no_data, 0,default_keyword.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(fcc_keyword, default_keyword.length(), (default_keyword + no_data_hint).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_noresult_hint.setText(ss);

        tv_noresult_hint.setOnClickListener(this);
	}

	static final String PAGE = "page";
	static final String KEYWORD = "keyword";

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(PAGE, currentPage);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		boolean containsKey = savedInstanceState.containsKey(PAGE);
		if (containsKey) {
			currentPage = savedInstanceState.getInt(PAGE);
		}
	}

	private static final int PAGE_SIZE = 20;

	private void search(int targetPage, boolean loadMore) {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		if (DeviceUtil.isNetworkAvailable(this)) {
//			if (loadMore) {
//				requestId = NetUtil.getInstance().requestForSearch(keyword,
//						targetPage, PAGE_SIZE,
//						new LoadMoreContentListener(this));
//			} else {
//				requestId = NetUtil.getInstance()
//						.requestForSearch(keyword, targetPage, PAGE_SIZE,
//								new RequestContentListener(this));
//			}

		} else {
			if (!loadMore)
				showErrorView();
		}
	}

	private class RequestContentListener implements IRequestListener {

		WeakReference<SearchResultActivity> hostref;

		public RequestContentListener(SearchResultActivity host) {
			hostref = new WeakReference<SearchResultActivity>(host);
		}

		private boolean check() {
            SearchResultActivity host = hostref.get();
			if (host == null) {
				return false;
			}
			if (host.isFinishing()) {
				return false;
			}
			return true;
		}

		@Override
		public void onRequestSuccess(BaseResult responsedata) {
			if (!check()) {
				return;
			}
            SearchResultActivity host = hostref.get();
			if (responsedata.getErrorCode() == DcError.DC_OK) {
				// 搜索结果
				SearchResult searchresult = (SearchResult) responsedata;
				List<SearchItem> searchdata = searchresult.getData();
				if (searchdata == null || searchdata.size() == 0) {
					host.doFillSearchNoResult(null);
				} else {
					if (searchresult.isSearch()) {
                        if(searchresult.getData().size() < 20){
                            host.setHasMore(false);
                            host.showNoMoreView();
                        }else{
                            host.setHasMore(true);
                            host.showLoadingMoreFooter();
                            host.setFooterVisible(false);
                        }
						host.checkAndFillSearchResult(searchdata);
					} else {
						host.doFillSearchNoResult(searchdata);
					}
				}
				if (searchresult.getTotalCount() > 0) {
					Message msg = new Message();
					msg.what = MSG_REFRESH_TITLE_COUNT;
					msg.obj = searchresult.getTotalCount();
					handler.sendMessage(msg);
				}

				if(searchresult.getTotalCount() < 20 && searchresult.getTotalCount() != 0){
					host.showNoMoreView();
				}

			} else {
				host.showErrorView();
			}
		}

		@Override
		public void onRequestError(int requesttag, int requestid,
				int errorcode, String msg) {
			if (!check()) {
				return;
			}
			SearchResultActivity host = hostref.get();
			host.showErrorView();

		}

	}

	static class LoadMoreContentListener implements IRequestListener {

		WeakReference<SearchResultActivity> hostRef;

		public LoadMoreContentListener(SearchResultActivity host) {
			hostRef = new WeakReference<SearchResultActivity>(host);
		}

		private boolean check() {
			SearchResultActivity host = hostRef.get();
			if (host == null) {
				return false;
			}
			if (host.isFinishing()) {
				return false;
			}
			return true;
		}

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (!check()) {
				return;
			}
			SearchResultActivity host = hostRef.get();
			host.setLoadlingMoreState(false);
			host.setFooterVisible(false);
			host.searchResultLayout.onRefreshComplete();
			if (responseData.getErrorCode() == DcError.DC_OK) {
				// 搜索结果
				SearchResult searchResult = (SearchResult) responseData;
				List<SearchItem> searchData = searchResult.getData();
				if (searchData == null || searchData.size() == 0) {
					host.setHasMore(false);
					host.setFooterVisible(false);
					// Toast.makeText(host.getApplicationContext(), "没有更多内容",
					// Toast.LENGTH_LONG).show();
					host.showNoMoreView();
				} else {
					if (searchResult.isSearch()) {
						host.checkAndFillSearchResult(searchData);
                        host.showLoadingMoreFooter();
                        host.setFooterVisible(false);
					} else {
						host.setHasMore(false);
						host.showNoMoreView();
					}

                    if(searchResult.getData().size() < 20){
                        host.setHasMore(false);
                        host.showNoMoreView();
                    }else{
                        host.setHasMore(true);
                        host.showLoadingMoreFooter();
                        host.setFooterVisible(false);
                    }
				}

			} else {
				// host.showErrorView();
				CustomToast.showToast(host.getApplicationContext(),
						host.getString(R.string.get_more_data_failed));
			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId,
				int errorCode, String msg) {
			if (!check()) {
				return;
			}

			SearchResultActivity host = hostRef.get();
			host.setLoadlingMoreState(false);
			host.setFooterVisible(false);
			host.searchResultLayout.onRefreshComplete();
			CustomToast.showToast(host.getApplicationContext(),
					host.getString(R.string.get_more_data_failed));
			// Toast.makeText(host.getApplicationContext(), "获取更多内容失败",
			// Toast.LENGTH_LONG).show();

		}

	}

	private void setLoadlingMoreState(boolean loading) {
		this.isLoadingMore = loading;
	}

	private void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	private boolean hasMore() {
		return this.hasMore;
	}

	private boolean getLoadlingMoreState() {
		return this.isLoadingMore;
	}

	private void setFooterVisible(boolean visible) {
		footer.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	/**
	 * Intent d_intent = new Intent("com.duoku.action.download.begin");
	 * d_intent.putExtra("pkgname", gameInfo.getPkgname());
	 * GameDetailsActivity.this.sendBroadcast(d_intent);
	 * 
	 * @author wangliang
	 * 
	 */
	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			// if (Constants.DEBUG)Log.i(TAG, "MyReceiver receive "+action);
			/**
			 * 程序安装或者卸载的通知（好像只有跳到ManagerActivity中才会出现）
			 */
			if (action.equals(BroadcaseSender.ACTION_PRE_PACKAGE_EVENT)) {
				String originalAction = intent
						.getStringExtra(BroadcaseSender.ARG_ORIGIANL_ACTION);
				if (originalAction.equals(Intent.ACTION_PACKAGE_ADDED)) {
					onPackageAdded(context, intent);
				} else if (originalAction.equals(Intent.ACTION_PACKAGE_REMOVED)) {
					onPackageRemoved(context, intent);
				}

			}
			/**
			 * 添加或者删除下载的通知（好像只有跳到ManagerActivity中才会出现）
			 */
			if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_CHANGED)) {
				boolean downloadOrOtherwise = intent.getBooleanExtra(
						BroadcaseSender.DOWNLOAD_CHANGED_ARG, false);
				if (downloadOrOtherwise) {
					if (searchResultAdapter != null
							&& searchResultAdapter.getData() != null) {
						// 删除下载需要更新状态
						// checkAndFillSearchResult2(searchResultAdapter.getData());
					}

				}
			}
			/**
			 * 静默安装的通知
			 */
			if (action.equals(BroadcaseSender.ACTION_INSTALL_CHANGED)) {
				// TODO
				onInstallChanged();
			}

			if ("com.duoku.action.download.begin".equals(action)) {
				try {
					// d_intent.putExtra("fromown", true);
					boolean fromOwn = intent.getBooleanExtra("fromown", false);
					String pkgName = intent.getStringExtra("pkgname");
					String gameId = intent.getStringExtra("gameid");
					long downloadId = intent.getLongExtra("downloadid", -1);

					if (downloadId > 0 && gameId != null && !fromOwn) {

						List<SearchItem> data = searchResultAdapter.getData();
						if (data == null)
							return;
						// searchResultAdapter.setNotifyOnChange(false);
						for (SearchItem item : data) {
							String tmp = item.getGameId();
							if (gameId != null && tmp.equals(gameId)) {
								item.setCurrentBytes(0);
								item.setTotalBytes(item.getPackageSize());
								item.setDownloadStatus(DownloadStatus.STATUS_PENDING);
								item.setStatus(GameStatus.DOWNLOADING);
								Message msg = new Message();
								msg.what = MSG_REFRESH_PROGRESS;
								msg.obj = item.getGameId();
								handler.sendMessage(msg);
								break;
							}
						}

						addDownloadListener(gameId, downloadId);
						if (searchResultAdapter != null) {
							searchResultAdapter.notifyDataSetChanged();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

	private void onInstallChanged() {
		if (searchResultAdapter != null) {
			List<SearchItem> data = searchResultAdapter.getData();
			if (data == null) {
				return;
			}
			searchResultAdapter.setNotifyOnChange(false);
			AppManager manager = AppManager
					.getInstance(getApplicationContext());
			Set<InstallPacket> silentInstallList = manager
					.getSilentInstallList();
			for (InstallPacket installPacket : silentInstallList) {
				String packageName = installPacket.getPackageName();
				for (SearchItem searchItem : data) {
					// TODO 不能唯一确定
					if (packageName.equals(searchItem.getPackageName())
							&& searchItem.getStatus() == GameStatus.DONWLOADED) {
						searchItem.setInstalleStatus(installPacket.getStatus());
						searchItem.setInstallErrorReason(installPacket
								.getErrorReason());
					}
				}
			}
			searchResultAdapter.setNotifyOnChange(true);
			searchResultAdapter.notifyDataSetChanged();
		}
	}

	private void onPackageAdded(Context context, Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		Boolean systemPackage = AppUtil.isSystemPackage(
				context.getPackageManager(), packageName);
		if (systemPackage == null || systemPackage) {
			return;
		}
		if (searchResultAdapter != null) {
			List<SearchItem> data = searchResultAdapter.getData();
			if (data == null) {
				return;
			}
			for (SearchItem searchItem : data) {
				if (packageName.equals(searchItem.getPackageName())
						&& searchItem.getStatus() == GameStatus.DONWLOADED) {
					searchItem.setStatus(GameStatus.INSTALLED);
					searchItem.setInstalleStatus(InstallStatus.INSTALLED);
				}
			}
			searchResultAdapter.notifyDataSetChanged();
		}
	}

	private void onPackageRemoved(Context context, Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		if (searchResultAdapter != null) {
			List<SearchItem> data = searchResultAdapter.getData();
			if (data == null) {
				return;
			}
			for (SearchItem searchItem : data) {
				if (packageName.equals(searchItem.getPackageName())) {
					if (searchItem.getStatus() == GameStatus.DONWLOADED
					/* || searchItem.getInstalleStatus() != null */) {
						searchItem.setStatus(GameStatus.DONWLOADED);
						// searchItem.setInstalleStatus(null)
					} else {
						searchItem.setStatus(GameStatus.UNDOWNLOAD);
					}

				}
			}
			searchResultAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * BroadcastReceiver
	 */
	private MyReceiver myReceiver;

	/**
	 * 当app安装或者删除时刷新列表
	 */
	private void registerReceiver() {
		if (myReceiver == null) {
			myReceiver = new MyReceiver();
			//
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BroadcaseSender.ACTION_PRE_PACKAGE_EVENT);
			intentFilter.addDataScheme("package");
			registerReceiver(myReceiver, intentFilter);

			IntentFilter intentFilter2 = new IntentFilter();
			intentFilter2.addAction(BroadcaseSender.ACTION_DOWNLOAD_CHANGED);
			registerReceiver(myReceiver, intentFilter2);

			IntentFilter installFilter = new IntentFilter();
			installFilter.addAction(BroadcaseSender.ACTION_INSTALL_CHANGED);
			registerReceiver(myReceiver, installFilter);

			IntentFilter downloadFilter = new IntentFilter();
			downloadFilter.addAction("com.duoku.action.download.begin");
			registerReceiver(myReceiver, downloadFilter);
		}
	}

	private void addDownloadListener(String gameId, long downloadId) {
		DownloadUtil.addDownloadItemListener(getApplicationContext(),
				downloadId, onItemDownloadListener);
		observersIds.put(gameId, downloadId);
	}

	private void addDownloadedData(String gameId, String dest) {
		downloadedIds.put(gameId, dest);
	}

	private void unregisterReceiver() {
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
			myReceiver = null;
		}
	}

	private Map<String, Long> observersIds = new HashMap<String, Long>();
	private Map<String, String> downloadedIds = new HashMap<String, String>();

	private void checkAndFillSearchResult(final List<SearchItem> data) {

		// 将搜索设置下载以及安装的状态
		new AsyncTask<Void, Void, Map<String, GameStatus>>() {

			protected void onPreExecute() {
				// registerListener();
			};

			@Override
			protected Map<String, GameStatus> doInBackground(Void... params) {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(
					java.util.Map<String, GameStatus> result) {
				doFillSearchResult(data);
			};
		}.execute();
	}

	private void doFillSearchResult(List<SearchItem> data) {
		if (data == null) {
			Log.e(TAG, "Fatal Error!");
			return;
		}
		showSearchResultView();
		if (searchResultAdapter == null) {
			searchResultAdapter = new SearchResultAdapter(this);
			searchResultLayout.setAdapter(searchResultAdapter);
			searchResultLayout.setVisibility(View.VISIBLE);
			searchResultAdapter
					.setOnListItemClickListener(searchResultItemClickListener);
		}

		searchResultAdapter.addAll(data);
//		searchResultAdapter.setData(data);
		currentPage += 1;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
	};

	private void handleRestartDownload(Intent data) {
		/*
		 * DuokuDialog.showNetworkAlertDialog( SearchResultActivity.this,
		 * REQ_CODE_RESTART, item.getPackageName(), item.getDownloadUrl(),
		 * downloadId);
		 */

		int intExtra = data.getIntExtra(DownloadDialogActivity.ARG_EXTRA, -1);
		if (intExtra == -1) {
			return;
		}
		SearchItem item = searchResultAdapter.getItem(intExtra);
		PackageHelper.restartDownload(item.getDownloadId(), myDownloadCallback);

		// DownloadUtil.restartDownload(getApplicationContext(),
		// intExtra);
		//
		// Long downloadId = observersIds.get(item.getGameId());
		// //下载发送广播
		// Intent d_intent = new Intent("com.duoku.action.download.begin");
		// d_intent.putExtra("pkgname", item.getPackageName());
		// d_intent.putExtra("gameid", item.getGameId());
		// d_intent.putExtra("versionname", item.getVersion());
		// d_intent.putExtra("versioncode", item.getVersionInt());
		// d_intent.putExtra("downloadid", downloadId);
		// d_intent.putExtra("fromown", true);
		// sendBroadcast(d_intent);

		// 统计下载
		// DownloadStatistics.addDownloadGameStatistics(
		// getApplicationContext(), item.getGameName());
	}

	private void handleResumeDownload(Intent data) {

		int intExtra = data.getIntExtra(DownloadDialogActivity.ARG_EXTRA, -1);
		if (intExtra == -1) {
			return;
		}
		SearchItem item = searchResultAdapter.getItem(intExtra);
		PackageHelper.resumeDownload(item.getDownloadId(), myDownloadCallback);
		DownloadStatistics.addResumeDownloadGameStatistics(
				getApplicationContext(), item.getGameName());

		// DownloadReason downloadReason = item.getDownloadReason();
		// Long downloadId = observersIds.get(item.getGameId());
		// if (downloadReason == DownloadReason.PAUSED_BY_APP) {
		// DownloadUtil.resumeDownload(getApplicationContext(),
		// downloadId);
		// DownloadStatistics.addResumeDownloadGameStatistics(
		// getApplicationContext(), item.getGameName());
		// } else {
		// boolean networkAvailable2 = DeviceUtil
		// .isNetworkAvailable(getApplicationContext());
		// if (!networkAvailable2) {
		// LoginRegisterToast
		// .showToast(
		// getApplicationContext(),
		// getString(R.string.alert_network_inavailble));
		// // Toast.makeText(getApplicationContext(), "请检查网络",
		// // Toast.LENGTH_SHORT).show();
		// } else {
		// DownloadUtil.resumeDownload(
		// getApplicationContext(), downloadId);
		// DownloadStatistics
		// .addResumeDownloadGameStatistics(
		// getApplicationContext(),
		// item.getGameName());
		// }
		// }
	}

	/**
	 * 下载、继续下载或者重试的回调方法
	 * 
	 * @author wangliang
	 * 
	 */
	class MyDownloadCallback implements DownloadCallback {

		private SearchItem findTarget(String url) {
			if (searchResultAdapter == null
					|| searchResultAdapter.getData() == null) {
				if (DEBUG) {
					Log.d(TAG,
							String.format(
									"DownloadCallback.findTarget return null searchResultAdapter is null or data is null for:%s ",
									url));
				}
				return null;
			}

			List<SearchItem> data = searchResultAdapter.getData();
			int size = data.size();
			SearchItem target = null;
			for (int i = 0; i < size; i++) {
				SearchItem item = data.get(i);
				if (url.equals(item.getDownloadUrl())) {
					target = item;
				}
			}
			if (DEBUG) {
				Log.d(TAG, String.format(
						"DownloadCallback.findTarget return null for %s ", url));
			}
			return target;
		}

		@Override
		public void onDownloadResult(String downloadUrl, boolean successful,
				long downloadId, String saveDest, Integer reason) {
			SearchItem target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
			String gameName = target.getGameName();
			if (successful) {
				target.setDownloadId(downloadId);
				target.setSaveDest(saveDest);
			}
			if (DEBUG) {
				if (successful) {
					Log.d(TAG,
							String.format(
									"[onDownloadResult]target:%s download successful,downloadId:%s",
									gameName, downloadId));
				} else {
					Log.d(TAG,
							String.format(
									"[onDownloadResult]target:%s download error,reason:%s",
									gameName, reason));
				}
			}

		}

		@Override
		public void onResumeDownloadResult(String url, boolean successful,
				Integer reason) {
			SearchItem target = findTarget(url);
			if (target == null) {
				return;
			}
			String gameName = target.getGameName();
			if (DEBUG) {
				if (successful) {
					Log.d(TAG,
							String.format(
									"[onResumeDownloadResult]target:%s resume/restart successful",
									gameName));
				} else {
					Log.d(TAG,
							String.format(
									"[onResumeDownloadResult]target:%s resume/restart error,reason:%s",
									gameName, reason));
				}
			}

		}

		@Override
		public void onRestartDownloadResult(String downloadUrl,
				String saveDest, boolean successful, Integer reason) {
			// TODO Auto-generated method stub
			SearchItem target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
		}

	}

	MyDownloadCallback myDownloadCallback = new MyDownloadCallback();

	private void logForDownload(SearchItem item) {
		String fmt = "Download for %s,is diff update? %s,apk status:%s";
		Log.i(TAG, String.format(fmt, item.getGameName(),
				item.isDiffDownload(),
				PackageMode.getStatusString(item.getApkStatus())));

	}


	OnListItemClickListener searchResultItemClickListener = new OnListItemClickListener() {

		@Override
		public void onItemIconClick(View view, int position) {
		}

		@Override
		public void onItemButtonClick(final View view, int position) {

			final SearchItem item = searchResultAdapter.getItem(position);

			int apkStatus = item.getApkStatus();


			ClickNumStatistics.addSearchResultItemButtonClickStatis(
					getApplicationContext(), position + "");
		}

	};

	private void doFillSearchNoResult(List<SearchItem> data) {
		showSearchNoResultView();

		if (data != null && data.size() > 0) {
			// 没有结果
			if (data.size() > 8) {
				List<SearchItem> tmpList = new ArrayList<SearchResult.SearchItem>();
				for (int i = 0; i < 8; i++) {
					tmpList.add(data.get(i));
				}
				data = tmpList;
			}

			searchRecomendHintView.setVisibility(View.VISIBLE);
			recommedAdapter = new SearchRecommedAdapter(this);
			recommedAdapter.setData(data);
			recomendGv.setAdapter(recommedAdapter);
			recomendGv.setOnItemClickListener(this);

		} else {
			// 没有结果，也没有推荐结果
			searchRecomendHintView.setVisibility(View.GONE);


		}
	}

	/*
	 * void fakeRecommed(){ ArrayList<SearchItem> list = new
	 * ArrayList<SearchItem>(); Random random = new Random(); for (int i = 0; i
	 * < 8; i++) { SearchItem item = new SearchItem(1909+"", "游戏" + i, 3.0f, 77
	 * * i + i * i + 88, "abc", SquareSubPageLoader.imageThumbUrls[i], "", 233 i
	 * * i * i - i * i + 1000,"",7,90L,"extra",false); list.add(item);
	 * 
	 * } searchRecomendHintView.setVisibility(View.VISIBLE);
	 * SearchRecommedAdapter recommedAdapter = new SearchRecommedAdapter(this);
	 * recommedAdapter.setData(list); recomendGv.setAdapter(recommedAdapter);
	 * 
	 * }
	 */

	// //////////////////////////////////////////////////////////////////////

	/*
	 * @Override public void onRequestSuccess(BaseResult responseData) { //搜索结果
	 * SearchResult searchResult = (SearchResult) responseData; List<SearchItem>
	 * searchData = searchResult.getData(); if (searchData == null ||
	 * searchData.size() == 0) { doFillSearchNoResult(null); } else { if
	 * (searchResult.isSearch()) { checkAndFillSearchResult(searchData); } else
	 * { doFillSearchNoResult(searchData); } } }
	 * 
	 * @Override public void onRequestError(int requestTag, int requestId, int
	 * errorCode, String msg) { showSearchNoResultView();
	 * searchRecomendHintView.setVisibility(View.GONE); }
	 */

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.error_hint:
			if (!DeviceUtil.isNetworkAvailable(getApplicationContext())) {
				CustomToast.showToast(getApplicationContext(),
						getString(R.string.alert_network_inavailble));
				return;
			} else {
				showLoadingProgressView();
				search(1, false);
			}
        break;
        case R.id.tv_no_searchresult_hint:
            if (!DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                CustomToast.showToast(getApplicationContext(),
                        getString(R.string.alert_network_inavailble));
                return;
            } else {
                showLoadingProgressView();

                search();
            }

        break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			Set<String> keySet = observersIds.keySet();
			for (String string : keySet) {
				Long id = observersIds.get(string);
				DownloadUtil.removeDownloadItemListener(
						getApplicationContext(), id, onItemDownloadListener);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// unregisterReceiver();
		unregisterListener();
		if (rootDialog != null) {
			rootDialog.dismiss();
			rootDialog = null;
		}
		NetUtil.getInstance().cancelRequestById(requestId);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1,
			int position, long id) {
		SearchItem item = null;
		final AppManager manager = AppManager
				.getInstance(getApplicationContext());
		// final SearchItem item = null ;
		if (adapterView instanceof ListView) {
			Object item2 = adapterView.getAdapter().getItem(position);
			if (item2 != null) {
				item = (SearchItem) item2;
				if (item2 == item) {
					// Toast.makeText(getApplicationContext(), "same",
					// 0).show();
				}
			}
		} else if (adapterView instanceof GridView) {
			if (recommedAdapter != null)
				item = recommedAdapter.getItem(position);
		}
		if (item != null)
            manager.jumpToDetail(SearchResultActivity.this, item.getGameId(), item.getGameName(),
                    item.getPackageName(), false);

		ClickNumStatistics.addSearchResultItemClickStatis(
				getApplicationContext(), position + "");

	}

	// //////////////////////////////////////////////////////////////////////////////////////
	private boolean needRequery = false;
	private PackageCallback packageCallback;

	private void registerListener() {
		if (packageCallback == null) {
			packageCallback = new MyPackageCallback();
			PackageHelper.registerPackageStatusChangeObserver(packageCallback);
		}
	}

	private void unregisterListener() {
		if (packageCallback == null) {
			PackageHelper
                    .unregisterPackageStatusChangeObserver(packageCallback);
		}

	}

	class MyPackageCallback implements PackageCallback {

		@Override
		public void onPackageStatusChanged(PackageMode mode) {

			if (searchResultAdapter == null) {
				needRequery = true;
			} else {
				List<SearchItem> data = searchResultAdapter.getData();
				if (data == null) {
					return;
				}
				SearchItem target = null;
				for (SearchItem item : data) {
					if (item.getPackageName()
							.equalsIgnoreCase(mode.packageName)
							&& item.getVersionInt() == mode.versionCode
							&& item.getVersion().equalsIgnoreCase(mode.version)) {
						target = item;
						break;
					}
					/*
					 * else if(mode.downloadUrl != null &&
					 * mode.downloadUrl.equals(item.getDownloadUrl())){ target =
					 * item ; break; }
					 */
				}
				if (target == null) {
					return;
				}

				switch (mode.status) {
				case PackageMode.DOWNLOAD_PENDING:
					// Progress maybe not set?
					// TODO
					target.setApkStatus(PackageMode.DOWNLOAD_PENDING);
					if (mode.totalSize > 0) {
						target.setCurrentBytes(mode.currentSize);
						target.setTotalBytes(mode.totalSize);
					}
					if (mode.downloadDest != null) {
						target.setSaveDest(mode.downloadDest);
					}

					break;
				case PackageMode.DOWNLOAD_RUNNING:
					target.setApkStatus(PackageMode.DOWNLOAD_RUNNING);
					target.setCurrentBytes(mode.currentSize);
					target.setTotalBytes(mode.totalSize);
					break;
				case PackageMode.DOWNLOAD_PAUSED:
					target.setApkStatus(PackageMode.DOWNLOAD_PAUSED);
					target.setApkReason(mode.reason);
					if (DEBUG) {
						Log.i(TAG, "Download Paused,reason " + mode.reason);
					}
					break;
				case PackageMode.DOWNLOAD_FAILED:
					target.setApkStatus(PackageMode.DOWNLOAD_FAILED);
					target.setApkReason(mode.reason);
					if (DEBUG) {
						Log.i(TAG, "Download Failed,reason " + mode.reason);
					}
					break;
				case PackageMode.DOWNLOADED:
					if (mode.isDiffDownload) {
						target.setApkStatus(PackageMode.DOWNLOADED);
					} else {
						target.setApkStatus(PackageMode.DOWNLOADED);
					}
					if (DEBUG) {
						Log.i(TAG, "Download finished,mode " + mode);
						Log.i(TAG, "Download finished,item " + target);
					}
					break;
				case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
				case PackageMode.MERGED_DIFFERENT_SIGN:
					break;
				case PackageMode.MERGING:
					target.setApkStatus(PackageMode.MERGING);
					break;
				case PackageMode.MERGE_FAILED:
					target.setApkStatus(PackageMode.MERGE_FAILED);
					target.setApkReason(mode.reason);
					break;
				case PackageMode.MERGED:
					target.setApkStatus(PackageMode.MERGED);
					break;

				case PackageMode.INSTALLING:
					target.setApkStatus(PackageMode.INSTALLING);
					break;
				case PackageMode.INSTALL_FAILED:
					target.setApkStatus(PackageMode.INSTALL_FAILED);
					target.setApkReason(mode.reason);
					break;
				case PackageMode.INSTALLED:
					target.setApkStatus(PackageMode.INSTALLED);
					break;
				case PackageMode.CHECKING:
				case PackageMode.CHECKING_FINISHED:
					target.setApkStatus(mode.status);
					break;
				case PackageMode.UNDOWNLOAD:
				case PackageMode.UPDATABLE:
				case PackageMode.UPDATABLE_DIFF:
					target.setApkStatus(mode.status);
					return;
				default:
					return;
				}

				target.setCurrentBytes(mode.currentSize);
				target.setTotalBytes(mode.totalSize);
				target.setApkStatus(mode.status);
				target.setApkReason(mode.reason);
				// refreshList(mode);

				refreshDownloadProgress(target.getGameId());
				if (DEBUG) {
					Log.i(TAG,
							String.format(
									"[refreshDownloadProgress]current:%s,total:%s for :%s",
									target.getCurrentBytes(),
									target.getTotalBytes(),
									target.getGameName()));
				}
			}

		}
	}

	public void search() {
        if(searchResultAdapter != null){
            searchResultAdapter.clear();
        }
        if(footer_view != null){
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer_view);
            } catch (Exception e) {
            }
        }

		search(0, false);
	}

}
