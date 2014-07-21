package com.ranger.bmaterials.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.ExchangeHistoryDetailAdapter;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.ExchangeHistoryDetailResult;
import com.ranger.bmaterials.netresponse.ExchangeHistoryDetailResult.ExchangeItem;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener;

public class ExchangeHistoryAcitivty extends StatActivity implements OnRefreshListener<ListView>, OnClickListener, OnLastItemVisibleListener {

	PullToRefreshListView lvExchangeHistory;
	ExchangeHistoryDetailAdapter mAdapter;

	TextView tvActivityTitle;
	private View loadingView;
	private View errorView;
	private View noRecord;
	View footer;

	ArrayList<ExchangeItem> mResults = new ArrayList<ExchangeItem>();

	MineProfile mineProfile;
	int pageindex = 1;
	boolean isLoadingMore = false;
	boolean hasMore = true;
	
	private int retryCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_history_acitivity_layout);

		tvActivityTitle = (TextView) findViewById(R.id.label_title);

		footer = createFooter();

		lvExchangeHistory = (PullToRefreshListView) findViewById(R.id.exchange_history_list);
		lvExchangeHistory.setOnRefreshListener(this);
		lvExchangeHistory.setOnLastItemVisibleListener(this);
		lvExchangeHistory.getRefreshableView().addFooterView(footer);

		tvActivityTitle.setText(R.string.exchange_history_detail_title);

		loadingView = findViewById(R.id.progress_bar);

		errorView = findViewById(R.id.error_view);
		errorView.setVisibility(View.GONE);
		errorView.setOnClickListener(this);

		noRecord = findViewById(R.id.no_record_hint);

		View backView = findViewById(R.id.img_back);
		backView.setOnClickListener(this);
	}

	@Override
	protected void onStart() {

		showLoadingView();

		mineProfile = MineProfile.getInstance();

		// getBaseExchangeDetailList();
		if (retryCount < 1)
			getExchangeListByPage(pageindex);
		else
			showErrorView();

		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		getExchangeListByPage(1);
	}

	private void getExchangeListByPage(int page) {
	    if (ConnectManager.isNetworkConnected(this) == false)
	    {
	        showErrorView();
	        return ;
	    }
	    
		NetUtil.getInstance().requestExchangeHistoryDetail(mineProfile.getUserID(), mineProfile.getSessionID(), page, new ExchangeHistoryRequestListener(this));
	}

	@Override
	public void onClick(View btn) {

		switch (btn.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.error_hint:
		case R.id.error_view:
			
			if (ConnectManager.isNetworkConnected(this) == false)
		    {
				Toast.makeText(getApplicationContext(), R.string.alert_network_inavailble, 1000).show();
				return ;
		    }
			if (mineProfile.getIsLogin())
				showLoadingView();
				getExchangeListByPage(pageindex);
			break;
		default:
			break;
		}

	}

	private void showErrorView() {
		loadingView.setVisibility(View.GONE);
		noRecord.setVisibility(View.GONE);
		errorView.setVisibility(View.VISIBLE);
	}

	private void showNoRecordView() {
		loadingView.setVisibility(View.GONE);
		errorView.setVisibility(View.GONE);
		noRecord.setVisibility(View.VISIBLE);
	}

	private void showLoadingView() {
		loadingView.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.GONE);
		noRecord.setVisibility(View.GONE);
	}

	private void hideLoadingAndErrorView() {
		loadingView.setVisibility(View.GONE);
		errorView.setVisibility(View.GONE);
	}

	private void refreshWhenDataSetChanged() {
		mAdapter = new ExchangeHistoryDetailAdapter(getApplicationContext(), mResults);
		lvExchangeHistory.setAdapter(mAdapter);
		hideLoadingAndErrorView();
		mAdapter.notifyDataSetChanged();
	}

	private void jumpToLogin() {
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);

		MineProfile.getInstance().setIsLogin(false);
		startActivity(loginIntent);
	}

	class ExchangeHistoryRequestListener implements IRequestListener {

		WeakReference<ExchangeHistoryAcitivty> hostRef;

		public ExchangeHistoryRequestListener(ExchangeHistoryAcitivty host) {
			hostRef = new WeakReference<ExchangeHistoryAcitivty>(host);
		}

		private boolean check() {
			ExchangeHistoryAcitivty host = hostRef.get();
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
			ExchangeHistoryAcitivty host = hostRef.get();
			host.setLoadlingMoreState(false);
			host.setFooterVisible(false);

			if (responseData.getErrorCode() == DcError.DC_OK) {
				// 搜索结果
				ExchangeHistoryDetailResult searchResult = (ExchangeHistoryDetailResult) responseData;
				ArrayList<ExchangeItem> resultData = searchResult.getData();
				if (resultData != null) {

					if (resultData.size() == 0) {
						host.setHasMore(false);
					}

					mResults = new ArrayList<ExchangeItem>();
					mResults.addAll(resultData);
					host.refreshWhenDataSetChanged();
				} else {
					host.setHasMore(false);
					if(null == mAdapter || mAdapter.getCount() < 1)
						host.showNoRecordView();
				}
			} else if (lvExchangeHistory.getChildCount() < 1) {
				host.showNoRecordView();
			} else {
				host.showErrorView();
			}
			lvExchangeHistory.onRefreshComplete();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			ExchangeHistoryAcitivty host = hostRef.get();
			host.setLoadlingMoreState(false);
			host.setFooterVisible(false);

			if (null == lvExchangeHistory || lvExchangeHistory.getChildCount() < 1) {
				host.showErrorView();
			} else if (errorCode == DcError.DC_OK) {
				host.setHasMore(false);
				if(lvExchangeHistory.getChildCount() < 1)
					host.showNoRecordView();
			} else if (DcError.DC_NET_GENER_ERROR == errorCode) {
				Toast.makeText(getApplicationContext(), R.string.exchange_history_list_loading_error_hint, Toast.LENGTH_SHORT).show();
				host.showErrorView();
			} else if (DcError.DC_NEEDLOGIN == errorCode) {
				retryCount ++;
				Toast.makeText(getApplicationContext(), R.string.exchange_history_list_error_session_invalid, Toast.LENGTH_LONG).show();
				host.showErrorView();
				host.jumpToLogin();
			} else {
				host.showErrorView();
			}
			lvExchangeHistory.onRefreshComplete();
		}

	}

	@Override
	public void onLastItemVisible() {
		if (DeviceUtil.isNetworkAvailable(getApplicationContext()) && !getLoadlingMoreState() && hasMore()) {
			setFooterVisible(true);
			setLoadlingMoreState(true);
			getExchangeListByPage(pageindex + 1);
		}
	}

	private View createFooter() {
		View view = View.inflate(this, R.layout.loading_layout, null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}

	private void setFooterVisible(boolean visible) {
		footer.setVisibility(visible ? View.VISIBLE : View.GONE);
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

}
