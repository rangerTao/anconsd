package com.ranger.bmaterials.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.GridView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.GameHotAdapter;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameHotDataResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class GameHotFragement extends HeaderHallBaseFragment implements IRequestListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);

				return root;
			}
		}

		root = inflater.inflate(R.layout.game_hot_activity, null);
		initHeader();
		gv_game_hot = (GridView) root.findViewById(R.id.gv_game_hot_activity);
//		TextView titleText = (TextView) root.findViewById(R.id.tv_item_title_hall);
//		titleText.setText("排行");
		requestData();
		return root;
	}

	private void requestData() {
		LoadingTask task = new LoadingTask(getActivity(), new ILoading() {

			@Override
			public void loading(IRequestListener listener) {
				// TODO Auto-generated method stub
				NetUtil.getInstance().requestGameHotData(listener);
			}

			@Override
			public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isShowNoNetWorkView() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public IRequestListener getRequestListener() {
				// TODO Auto-generated method stub
				return GameHotFragement.this;
			}

			@Override
			public boolean isAsync() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		task.setRootView(root);
		task.loading();
	}

	private GridView gv_game_hot;
	private GameHotAdapter adapter;

	private void initGameList(GameHotDataResult mGameHotDataResult) {

		if (mGameHotDataResult != null) {
			ArrayList<GameInfo> game_list = mGameHotDataResult.getList_game();

			WindowManager mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			mWindowManager.getDefaultDisplay().getMetrics(dm);
			// displayWidth = dm.widthPixels;
			int density = dm.densityDpi;

			adapter = new GameHotAdapter(getActivity(), game_list, density);
			gv_game_hot.setAdapter(adapter);
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		// TODO Auto-generated method stub
		initGameList((GameHotDataResult) responseData);
		gv_game_hot.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		// TODO Auto-generated method stub
		gv_game_hot.setVisibility(View.GONE);

	}

}
