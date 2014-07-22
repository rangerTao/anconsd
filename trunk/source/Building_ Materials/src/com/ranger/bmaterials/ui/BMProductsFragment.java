package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BMSearchResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

public class BMProductsFragment extends Fragment implements OnClickListener, IRequestListener, OnRefreshListener2<ListView>,
		OnItemClickListener {

	private boolean guideRequestSend = false;
	private boolean noMoreGuide;
	private int requestId = 0;

	private List<BMSearchResult.BMSearchData> guideListInfo;
	private BMProductLiteAdapter guideInfoListAdapter = null;
	private PullToRefreshListView plvGuide;

	private ViewGroup guideViewContainer;
	private int pageGuideIndex;
	private int pageGuideNum;
	private int totalNum = 0;

	public PagerSlidingTabStrip tabStrip;
	private boolean update = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mine_activity_collection_subpage_guide, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		pageGuideIndex = 1;
		noMoreGuide = false;
		pageGuideNum = 20;
		guideListInfo = new ArrayList<BMSearchResult.BMSearchData>();
		guideInfoListAdapter = new BMProductLiteAdapter(getActivity(), guideListInfo);
		plvGuide = (PullToRefreshListView) getActivity().findViewById(R.id.listview_mine_collection_guides);
		plvGuide.setOnRefreshListener(this);
		plvGuide.setAdapter(guideInfoListAdapter);
		plvGuide.setOnItemClickListener(this);

		guideViewContainer = (ViewGroup) getActivity().findViewById(R.id.layout_mine_guide_view_container);

		plvGuide.setOnLastItemVisibleListener(OnLastItemVisibleListener);
		footer = createFooter();
	}

	OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if (!isLoadingMore && !noMoreGuide) {
				setFooterVisible(true);
				isLoadingMore = true;
				requestGuide();
			} else if (showNoMoreTip && !isLoadingMore) {
				showNoMoreTip = false;
				CustomToast.showLoginRegistErrorToast(BMProductsFragment.this.getActivity(), CustomToast.DC_ERR_NO_MORE_DATA);
			}
		}
	};
	private View footer;
	private boolean isLoadingMore;
	private boolean showNoMoreTip = true;

	private void setFooterVisible(boolean visible) {
		ListView listView = plvGuide.getRefreshableView();

		if (visible) {
			listView.addFooterView(footer);
			footer.setVisibility(View.VISIBLE);
			listView.setSelection(listView.getBottom());
		} else {
			listView.removeFooterView(footer);
		}
	}

	private View createFooter() {
		View view = View.inflate(this.getActivity(), R.layout.loading_layout, null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onStart() {

		super.onStart();

		if (!guideRequestSend) {
			guideRequestSend = true;
			showLoadingView();
			refreshGuide();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (requestId > 0) {
			NetUtil.getInstance().cancelRequestById(requestId);
		}
	}

	private void showLoadingView() {
		guideViewContainer.setVisibility(View.GONE);
	}

	private void showErrorView() {
		guideViewContainer.setVisibility(View.GONE);
	}

	private void showContentView() {


		if (guideListInfo.size() > 0) {
			guideViewContainer.setVisibility(View.VISIBLE);
		} else {
			guideViewContainer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageGuideIndex = 1;
		noMoreGuide = false;
		requestGuide();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestGuide();
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		BMSearchResult result = (BMSearchResult) responseData;

		totalNum = result.getTotal();

		if (pageGuideIndex == 1) {
			guideListInfo.clear();
		}

		if (result.getDataList().size() > 0) {
			guideListInfo.addAll(result.getDataList());
			guideInfoListAdapter.notifyDataSetChanged();
			pageGuideIndex++;
		}

		if (guideListInfo.size() >= totalNum) {
			noMoreGuide = true;
			setFooterVisible(false);
		}

		requestFinished(true);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		requestFinished(false);

		switch (errorCode) {
		default:
			break;
		}
		CustomToast.showLoginRegistErrorToast(getActivity(), errorCode);
	}

	@Override
	public void onClick(View v) {
	}

	private void refreshGuide() {

		showNoMoreTip = true;
		pageGuideIndex = 1;
		noMoreGuide = false;
		requestGuide();
	}

	private void requestGuide() {

		if (noMoreGuide) {
			CustomToast.showLoginRegistErrorToast(getActivity(), CustomToast.DC_ERR_NO_MORE_DATA);
			requestFinished(true);
		} else {
            int userid = getArguments().getInt(BMCompanyInfoActivity.USER_ID);
			requestId = NetUtil.getInstance().requestForProductsPerCom(userid,pageGuideIndex, this);
		}
	}

	private void requestFinished(boolean succeed) {
		plvGuide.onRefreshComplete();
		isLoadingMore = false;
		setFooterVisible(false);
		requestId = 0;

		if (succeed || guideListInfo.size() > 0) {
			showContentView();
		} else {
			showErrorView();
		}

		updateTitle(totalNum);
	}

	private void updateTitle(int total) {

	}
}