package com.ranger.bmaterials.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.GridView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.GameNewAdapter;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameNewDataResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class GameNewFragement extends Fragment implements IRequestListener {
	private View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);

				return root;
			}
		}
		
		root = inflater.inflate(R.layout.game_new_activity, null);

		gv_game_new = (GridView) root.findViewById(R.id.gv_game_new_activity);
		requestData();
		return root;
	}
	
	private void requestData() {
		LoadingTask task = new LoadingTask(getActivity(), new ILoading() {

			@Override
			public void loading(IRequestListener listener) {
				NetUtil.getInstance().requestGameNewData(listener);
			}

			@Override
			public void preLoading(View network_loading_layout,
					View network_loading_pb, View network_error_loading_tv) {
			}

			@Override
			public boolean isShowNoNetWorkView() {
				return true;
			}

			@Override
			public IRequestListener getRequestListener() {
				return GameNewFragement.this;
			}

			@Override
			public boolean isAsync() {
				return false;
			}
		});
		task.setRootView(root);
		task.loading();
	}

	private GridView gv_game_new;
	private GameNewAdapter adapter;

	private void initGameList(GameNewDataResult mGameNewDataResult) {
		if (mGameNewDataResult != null) {
			ArrayList<GameInfo> game_list = mGameNewDataResult.getList_game();
			WindowManager mWindowManager = (WindowManager) getActivity()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			mWindowManager.getDefaultDisplay().getMetrics(dm);
			// displayWidth = dm.widthPixels;
			int density = dm.densityDpi;

			adapter = new GameNewAdapter(getActivity(), game_list, density);
			// 逐个tab页刷新 防止卡顿
			// GameActivity.handler.post(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// gv_game_new.setAdapter(adapter);
			// }
			// });

			gv_game_new.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		// TODO Auto-generated method stub
		GameNewDataResult mGameNewDataResult = (GameNewDataResult) responseData;
		initGameList(mGameNewDataResult);
		gv_game_new.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode,
			String msg) {
		// TODO Auto-generated method stub
		gv_game_new.setVisibility(View.GONE);

	}

}
