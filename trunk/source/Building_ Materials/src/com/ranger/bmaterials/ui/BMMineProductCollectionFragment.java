package com.ranger.bmaterials.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.BMProductCollectionAdapter;
import com.ranger.bmaterials.adapter.BMProductLiteAdapter;
import com.ranger.bmaterials.netresponse.BMCollectionResult;
import com.ranger.bmaterials.netresponse.BMSearchResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class BMMineProductCollectionFragment extends Fragment implements OnClickListener, IRequestListener, OnRefreshListener2<ListView>,
		OnItemClickListener {

	private boolean guideRequestSend = false;
	private boolean noMoreGuide;
	private int requestId = 0;

	private ArrayList<BMCollectionResult.Collection> guideListInfo;
	private BMProductCollectionAdapter guideInfoListAdapter = null;
	private PullToRefreshListView plvGuide;

	private int pageGuideIndex;
	private int totalNum = 0;

	public PagerSlidingTabStrip tabStrip;

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
		guideListInfo = new ArrayList<BMCollectionResult.Collection>();
		guideInfoListAdapter = new BMProductCollectionAdapter(getActivity(), guideListInfo);
		plvGuide = (PullToRefreshListView) getActivity().findViewById(R.id.listview_mine_collection_guides);
		plvGuide.setOnRefreshListener(this);
		plvGuide.setAdapter(guideInfoListAdapter);
		plvGuide.setOnItemClickListener(this);

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
				CustomToast.showLoginRegistErrorToast(BMMineProductCollectionFragment.this.getActivity(), CustomToast.DC_ERR_NO_MORE_DATA);
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
	}

	private void showErrorView() {
	}

	private void showContentView() {

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
		BMCollectionResult result = (BMCollectionResult) responseData;

		if (pageGuideIndex == 1) {
			guideListInfo.clear();
		}

		if (result.getData().size() > 0) {
			guideListInfo.addAll(result.getData());
            guideInfoListAdapter.notifyDataSetChanged();
			pageGuideIndex++;
		}

		if (guideListInfo.size() >= result.getData().size()) {
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
			requestId = NetUtil.getInstance().requestGetCollection(1,pageGuideIndex, this);
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