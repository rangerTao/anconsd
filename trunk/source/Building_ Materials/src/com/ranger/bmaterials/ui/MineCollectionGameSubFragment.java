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
import com.ranger.bmaterials.netresponse.MineGamesResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

public class MineCollectionGameSubFragment extends Fragment implements
		OnClickListener, IRequestListener, OnRefreshListener2<ListView>,
		OnItemClickListener {

	private boolean gameRequestSend = false;
	private boolean noMoreGame;
	private int requestId = 0;

	private List<MineGameItemInfo> gameListInfo;
	private MineGamesAdapter gameInfoListAdapter = null;
	private PullToRefreshListView plvGame;

	private View layout_loading_game;
	private ViewGroup gameViewContainer;
	private ViewGroup noGameViewContainer;
	private ViewGroup errorContainer;

	private int pageGameIndex;
	private int pageGameNum;
	private int totalNum;

	private GameReceiver gameReceiver;
	public PagerSlidingTabStrip tabStrip;

	private boolean update = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.mine_activity_collection_subpage_game, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		pageGameIndex = 1;
		noMoreGame = false;
		pageGameNum = 20;
		gameListInfo = new ArrayList<MineGameItemInfo>();
		getActivity().findViewById(R.id.btn_collection_game_goto_gamehall)
				.setOnClickListener(this);
		gameInfoListAdapter = new MineGamesAdapter(getActivity(), gameListInfo,
				MineGamesAdapter.LISTTYPE_COLLECTED_GAMES);
		plvGame = (PullToRefreshListView) getActivity().findViewById(
				R.id.listview_mine_collection_games);
		plvGame.setOnRefreshListener(this);
		plvGame.setAdapter(gameInfoListAdapter);
		plvGame.setOnItemClickListener(this);

		gameViewContainer = (ViewGroup) getActivity().findViewById(
				R.id.layout_mine_game_view_container);
		noGameViewContainer = (ViewGroup) getActivity().findViewById(
				R.id.layout_mine_game_none_pane);
		layout_loading_game = getActivity().findViewById(
				R.id.layout_loading_game);
		errorContainer = (ViewGroup) getActivity()
				.findViewById(R.id.error_hint);
		errorContainer.setOnClickListener(this);

		registerReceiver();

		plvGame.setOnLastItemVisibleListener(OnLastItemVisibleListener);
		footer = createFooter();
	}

	OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if (!isLoadingMore && !noMoreGame) {
				setFooterVisible(true);
				isLoadingMore = true;
				requestGame();
				
			} else if (showNoMoreTip && !isLoadingMore) {
				showNoMoreTip = false;

				CustomToast.showLoginRegistErrorToast(
						MineCollectionGameSubFragment.this.getActivity(),
						CustomToast.DC_ERR_NO_MORE_DATA);
			}
		}
	};

	private View footer;
	private boolean isLoadingMore;
	private boolean showNoMoreTip = true;

	private void setFooterVisible(boolean visible) {
		if (plvGame == null)
			return;
		ListView listView = plvGame.getRefreshableView();
		if (footer != null)
			if (visible) {
				listView.addFooterView(footer);
				footer.setVisibility(View.VISIBLE);
				listView.setSelection(listView.getBottom());
			} else {
				listView.removeFooterView(footer);
			}
	}

	private View createFooter() {
		if (getActivity() == null)
			return null;
		View view = View.inflate(this.getActivity(), R.layout.loading_layout,
				null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if ((errorContainer.getVisibility() == View.VISIBLE || update)
				&& MineProfile.getInstance().getIsLogin()) {

			gameListInfo.clear();
			pageGameIndex = 1;
			noMoreGame = false;
			refreshGame();
			update = false;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (!gameRequestSend) {
			gameRequestSend = true;
			showNoMoreTip = true;
			showLoadingView();
			refreshGame();
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
		gameViewContainer.setVisibility(View.GONE);
		noGameViewContainer.setVisibility(View.GONE);
		layout_loading_game.setVisibility(View.VISIBLE);
		errorContainer.setVisibility(View.GONE);
	}

	private void showErrorView() {
		gameViewContainer.setVisibility(View.GONE);
		noGameViewContainer.setVisibility(View.GONE);
		layout_loading_game.setVisibility(View.GONE);
		errorContainer.setVisibility(View.VISIBLE);
	}

	private void showContentView() {

		errorContainer.setVisibility(View.GONE);
		layout_loading_game.setVisibility(View.GONE);

		if (gameListInfo.size() > 0) {
			gameViewContainer.setVisibility(View.VISIBLE);
			noGameViewContainer.setVisibility(View.GONE);
		} else {
			gameViewContainer.setVisibility(View.GONE);
			noGameViewContainer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MineGameItemInfo gameInfo = (MineGameItemInfo) parent.getAdapter()
				.getItem(position);

		Intent intent = new Intent(getActivity(), GameDetailsActivity.class);
		intent.putExtra("gameid", gameInfo.gameID);
		intent.putExtra("gamename", gameInfo.gameName);
		startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageGameIndex = 1;
		noMoreGame = false;
		requestGame();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestGame();
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {

		MineGamesResult result = (MineGamesResult) responseData;

		totalNum = result.totalcount;

		if (gameListInfo != null) {
			if (pageGameIndex == 1) {
				gameListInfo.clear();
			}

			if (result.gameListInfo.size() > 0) {
				gameListInfo.addAll(result.gameListInfo);
				gameInfoListAdapter.notifyDataSetChanged();
				pageGameIndex++;
			}

			if (gameListInfo.size() >= totalNum) {
				noMoreGame = true;
				setFooterVisible(false);
			}
		}
		requestFinished(true);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode,
			String msg) {
		requestFinished(false);
		switch (errorCode) {
		case DcError.DC_NEEDLOGIN:// 需要登录
			MineProfile.getInstance().setIsLogin(false);
			if(getActivity()!=null){
			Intent intent = new Intent(getActivity(), SapiLoginActivity.class);
			startActivity(intent);
			CustomToast.showToast(getActivity(), getActivity().getResources()
					.getString(R.string.need_login_tip));
			getActivity().finish();
			}
			break;
		default:
			break;
		}
		CustomToast.showLoginRegistErrorToast(getActivity(), errorCode);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_collection_game_goto_gamehall) {
			MainHallActivity.jumpToTab(getActivity(), 0);
		} else if (v.getId() == R.id.error_hint) {
			showLoadingView();
			refreshGame();
		}
	}

	private void refreshGame() {
		pageGameIndex = 1;
		noMoreGame = false;
		requestGame();
	}

	private void requestGame() {

		if (noMoreGame) {
			CustomToast.showLoginRegistErrorToast(getActivity(),
					CustomToast.DC_ERR_NO_MORE_DATA);
			requestFinished(true);
		} else {
			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();
			requestId = NetUtil.getInstance().requestCollectionGame(userid,
					sessionid, pageGameIndex, pageGameNum, this);
		}
	}

	private void requestFinished(boolean succeed) {
		requestId = 0;
		plvGame.onRefreshComplete();

		isLoadingMore = false;
		setFooterVisible(false);

		if (succeed || gameListInfo.size() > 0) {
			showContentView();
		} else {
			showErrorView();
		}

		updateTitle(totalNum);
	}

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter(
				BroadcaseSender.ACTION_COLLECT_GAME_CANCEL);
		intentFilter.addAction(BroadcaseSender.ACTION_COLLECT_GAME_SUCCESS);
		gameReceiver = new GameReceiver();
		getActivity().registerReceiver(gameReceiver, intentFilter);
	}

	private void unregisterReceiver() {
		if (gameReceiver != null) {
			getActivity().unregisterReceiver(gameReceiver);
			gameReceiver = null;
		}
	}

	class GameReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					BroadcaseSender.ACTION_COLLECT_GAME_CANCEL)) {
				String gameID = intent.getStringExtra(Constants.JSON_GAMEID);

				for (MineGameItemInfo item : gameListInfo) {
					if (item.gameID.equals(gameID)) {
						gameListInfo.remove(item);

						if (gameListInfo.size() <= 0) {
							update = true;
							onResume();
						}
						gameInfoListAdapter.notifyDataSetChanged();
						totalNum--;
						updateTitle(totalNum);
						break;
					}
				}
			} else if (intent.getAction().equals(
					BroadcaseSender.ACTION_COLLECT_GAME_SUCCESS)) {
				update = true;
			}

			showContentView();
		}
	}

	private void updateTitle(int total) {
		if(tabStrip!=null)
		if (total > 0) {
			tabStrip.updateTitle(0, "游戏(" + total + ")");
		} else {
			tabStrip.updateTitle(0, "游戏");
		}
	}
}
