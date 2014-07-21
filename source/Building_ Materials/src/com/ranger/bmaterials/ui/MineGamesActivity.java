package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.MineGamesResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

public class MineGamesActivity extends StatActivity implements OnClickListener,
		OnItemClickListener, OnRefreshListener2<ListView>, IRequestListener {

	private View btnBack;

	private List<MineGameItemInfo> mlistGameInfo;
	private MineGamesAdapter gameInfoListAdapter = null;

	private PullToRefreshListView plv;

	private ViewGroup errorContainer;
	private ViewGroup viewContainer;
	private ViewGroup noGameViewContainer;

	private int pageIndex = 1;
	private boolean noMoreGame = false;
	private int pageNum = 20;
	private int requestId = 0;
	private int totalCount = 0;

	private boolean gameRequestSend = false;

	private View layout_loading_game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine_activity_games);
		((TextView) findViewById(R.id.label_title)).setText("我的游戏");

		btnBack = findViewById(R.id.img_back);
		btnBack.setOnClickListener(this);
		findViewById(R.id.btn_mine_games_goto_gamehall)
				.setOnClickListener(this);

		mlistGameInfo = new ArrayList<MineGameItemInfo>();
		gameInfoListAdapter = new MineGamesAdapter(this, mlistGameInfo,
				MineGamesAdapter.LISTTYPE_DOWNLOADED_GAMES);

		plv = (PullToRefreshListView) findViewById(R.id.listview_downloaded_games);
		plv.setOnRefreshListener(this);
		plv.setAdapter(gameInfoListAdapter);
		plv.setOnItemClickListener(this);

		viewContainer = (ViewGroup) findViewById(R.id.layout_mine_game_view_container);
		noGameViewContainer = (ViewGroup) findViewById(R.id.layout_mine_game_none_pane);
		noGameViewContainer.setVisibility(View.INVISIBLE);
		errorContainer = (ViewGroup) findViewById(R.id.error_hint);
		errorContainer.setVisibility(View.INVISIBLE);
		errorContainer.setOnClickListener(this);

		layout_loading_game = findViewById(R.id.layout_loading_game);

		plv.setOnLastItemVisibleListener(OnLastItemVisibleListener);
		footer = createFooter();
	}

	OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if (!isLoadingMore && !noMoreGame) {
				setFooterVisible(true);
				isLoadingMore = true;
				getGameInfo();
			} else if (showNoMoreTip && !isLoadingMore) {
				showNoMoreTip = false;
				CustomToast.showLoginRegistErrorToast(MineGamesActivity.this,
						CustomToast.DC_ERR_NO_MORE_DATA);
			}
		}
	};
	private View footer;
	private boolean isLoadingMore;
	private boolean showNoMoreTip = true;

	private void setFooterVisible(boolean visible) {
		ListView listView = plv.getRefreshableView();

		if (visible) {
			listView.addFooterView(footer);
			footer.setVisibility(View.VISIBLE);
			listView.setSelection(listView.getBottom());
		} else {
			listView.removeFooterView(footer);
		}
	}

	private View createFooter() {
		View view = View.inflate(this, R.layout.loading_layout, null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (errorContainer.getVisibility() == View.VISIBLE
				&& MineProfile.getInstance().getIsLogin()) {
			refreshGameInfo();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mlistGameInfo.size() <= 0 && !gameRequestSend) {
			gameRequestSend = true;
			getGameInfo();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (requestId > 0) {
			NetUtil.getInstance().cancelRequestById(requestId);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnBack) {
			this.finish();
		} else if (v.getId() == R.id.btn_mine_games_goto_gamehall) {
			MainHallActivity.jumpToTab(this, 0);
		} else if (v.getId() == R.id.error_hint) {
			layout_loading_game.setVisibility(View.VISIBLE);
			noGameViewContainer.setVisibility(View.INVISIBLE);
			viewContainer.setVisibility(View.INVISIBLE);
			errorContainer.setVisibility(View.INVISIBLE);
			getGameInfo();
		}
	}

	private void getGameInfo() {

		if (noMoreGame) {
			CustomToast.showLoginRegistErrorToast(this,
					CustomToast.DC_ERR_NO_MORE_DATA);
			requestFinished(true);
		} else {
			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();

			requestId = NetUtil.getInstance().requestInstalledGames(userid,
					sessionid, pageIndex, pageNum, this);
		}
	}

	private void refreshGameInfo() {
		showNoMoreTip = true;
		pageIndex = 1;
		noMoreGame = false;
		getGameInfo();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MineGameItemInfo gameInfo = (MineGameItemInfo) parent.getAdapter()
				.getItem(position);

		Intent intent = new Intent(this, GameDetailsActivity.class);
		intent.putExtra("gameid", gameInfo.gameID);
		intent.putExtra("gamename", gameInfo.gameName);
		startActivity(intent);
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {

		MineGamesResult result = (MineGamesResult) responseData;
		totalCount = result.totalcount;

		if (pageIndex == 1) {
			mlistGameInfo.clear();
		}

		if (result.gameListInfo.size() > 0) {
			mlistGameInfo.addAll(result.gameListInfo);
			gameInfoListAdapter.notifyDataSetChanged();
			pageIndex++;
		}

		if (mlistGameInfo.size() >= totalCount) {
			noMoreGame = true;
			setFooterVisible(false);
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
			Intent intent = new Intent(MineGamesActivity.this,
                    SapiLoginActivity.class);
			startActivity(intent);
			CustomToast.showToast(this,
					getResources().getString(R.string.need_login_tip));
			finish();
			break;
		default:
			break;
		}

		CustomToast.showLoginRegistErrorToast(this, errorCode);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		refreshGameInfo();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getGameInfo();
	}

	private void requestFinished(boolean succeed) {
		plv.onRefreshComplete();
		layout_loading_game.setVisibility(View.INVISIBLE);

		isLoadingMore = false;
		setFooterVisible(false);

		if (succeed) {
			if (mlistGameInfo.size() > 0) {
				noGameViewContainer.setVisibility(View.INVISIBLE);
				viewContainer.setVisibility(View.VISIBLE);
				errorContainer.setVisibility(View.INVISIBLE);
			} else {
				noGameViewContainer.setVisibility(View.VISIBLE);
				viewContainer.setVisibility(View.INVISIBLE);
				errorContainer.setVisibility(View.INVISIBLE);
			}
		} else {// 请求失败
			if (mlistGameInfo.size() > 0) {
				noGameViewContainer.setVisibility(View.INVISIBLE);
				viewContainer.setVisibility(View.VISIBLE);
				errorContainer.setVisibility(View.INVISIBLE);
			} else {
				noGameViewContainer.setVisibility(View.INVISIBLE);
				viewContainer.setVisibility(View.INVISIBLE);
				errorContainer.setVisibility(View.VISIBLE);
			}
		}
	}
}
