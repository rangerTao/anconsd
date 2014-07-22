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
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.MineGuidesResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

public class MineCollectionGuideSubFragment extends Fragment implements OnClickListener, IRequestListener, OnRefreshListener2<ListView>,
		OnItemClickListener {

	private boolean guideRequestSend = false;
	private boolean noMoreGuide;
	private int requestId = 0;

	private List<MineGuideItemInfo> guideListInfo;
	private MineGuideAdapter guideInfoListAdapter = null;
	private PullToRefreshListView plvGuide;

	private ViewGroup guideViewContainer;
	private int pageGuideIndex;
	private int pageGuideNum;
	private int totalNum = 0;
	private GuideReceiver guideReceiver;

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
		guideListInfo = new ArrayList<MineGuideItemInfo>();
		guideInfoListAdapter = new MineGuideAdapter(getActivity(), guideListInfo);
		plvGuide = (PullToRefreshListView) getActivity().findViewById(R.id.listview_mine_collection_guides);
		plvGuide.setOnRefreshListener(this);
		plvGuide.setAdapter(guideInfoListAdapter);
		plvGuide.setOnItemClickListener(this);

		guideViewContainer = (ViewGroup) getActivity().findViewById(R.id.layout_mine_guide_view_container);

		registerReceiver();

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
				CustomToast.showLoginRegistErrorToast(MineCollectionGuideSubFragment.this.getActivity(), CustomToast.DC_ERR_NO_MORE_DATA);
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

		unregisterReceiver();
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
		MineGuideItemInfo guideInfo = (MineGuideItemInfo) parent.getAdapter().getItem(position);

		Intent in = new Intent(getActivity(), GameGuideDetailActivity2.class);
		//in.putExtra("gamename", guideInfo.guideTitle);
		in.putExtra("guideid", guideInfo.guideID);
		this.startActivity(in);
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
		MineGuidesResult result = (MineGuidesResult) responseData;

		totalNum = result.totalcount;

		if (pageGuideIndex == 1) {
			guideListInfo.clear();
		}

		if (result.guideListInfo.size() > 0) {
			guideListInfo.addAll(result.guideListInfo);
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
			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();
			requestId = NetUtil.getInstance().requestCollectionGuide(userid, sessionid, pageGuideIndex, pageGuideNum, this);
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

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter(BroadcaseSender.ACTION_COLLECT_GUIDE_CANCEL);
		intentFilter.addAction(BroadcaseSender.ACTION_COLLECT_GUIDE_SUCCESS);
		guideReceiver = new GuideReceiver();
		getActivity().registerReceiver(guideReceiver, intentFilter);
	}

	private void unregisterReceiver() {
		if (guideReceiver != null) {
			getActivity().unregisterReceiver(guideReceiver);
			guideReceiver = null;
		}
	}

	class GuideReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcaseSender.ACTION_COLLECT_GUIDE_CANCEL)) {
				String guideID = intent.getStringExtra(Constants.JSON_GUIDEID);

				for (MineGuideItemInfo item : guideListInfo) {
					if (item.guideID.equals(guideID)) {
						guideListInfo.remove(item);
						if (guideListInfo.size() <=0) {
							update = true;
							onResume();
						}
						guideInfoListAdapter.notifyDataSetChanged();
						totalNum--;
						updateTitle(totalNum);
						break;
					}
				}
			} else if (intent.getAction().equals(BroadcaseSender.ACTION_COLLECT_GUIDE_SUCCESS)) {
				update = true;
			}

			showContentView();
		}
	}

	private void updateTitle(int total) {
		if (tabStrip != null) {
			if (total > 0) {
				tabStrip.updateTitle(1, "供应商(" + total + ")");
			} else {
				tabStrip.updateTitle(1, "供应商");
			}			
		}
	}
}