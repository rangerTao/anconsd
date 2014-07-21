package com.ranger.bmaterials.ui.gameclass;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.GameTypeInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameClassDataResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.HeaderHallBaseFragment;
import com.ranger.bmaterials.ui.MainHallActivity;
import com.ranger.bmaterials.ui.MoreClassGameActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class GameClassFragement extends HeaderHallBaseFragment implements IRequestListener {

	private LinearLayout ll_game_categories;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);

				return root;
			}
		}

		root = inflater.inflate(R.layout.game_class_activity, null);

		initHeader();
		
		ll_game_categories = (LinearLayout) root.findViewById(R.id.sl_game_categories_content);
		
		requestData();
		return root;
	}

	private void requestData() {
		LoadingTask task = new LoadingTask(getActivity(), new ILoading() {

			@Override
			public void loading(IRequestListener listener) {
				NetUtil.getInstance().requestGameClassData(listener);
			}

			@Override
			public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
			}

			@Override
			public boolean isShowNoNetWorkView() {
				return true;
			}

			@Override
			public IRequestListener getRequestListener() {
				return GameClassFragement.this;
			}

			@Override
			public boolean isAsync() {
				return false;
			}
		});

		task.setRootView(root);
		task.loading();
	}

	private ArrayList<GameTypeInfo> game_type_list = new ArrayList<GameTypeInfo>();
	private ArrayList<String> singe_type_and_number_list = new ArrayList<String>();
	
	private ArrayList<GameTypeInfo> banner_game_category = new ArrayList<GameTypeInfo>();
	private ArrayList<GameTypeInfo> item_game_category = new ArrayList<GameTypeInfo>();

	private void initView(GameClassDataResult mGameClassDataResult) {
		if (mGameClassDataResult != null) {
			if (mGameClassDataResult.getList_game().size() > 0) {
				ArrayList<GameTypeInfo> gt_list = mGameClassDataResult.getList_game();

				for (int i = 0; i < gt_list.size(); i++) {
					GameTypeInfo gt_info = gt_list.get(i);

					if (gt_info.getLabel() == null || "".equals(gt_info.getLabel())) {
						game_type_list.add(gt_info);

						singe_type_and_number_list.add(gt_info.getGametypename() + "##" + gt_info.getGametypenumber());
					} else {
						game_type_list.add(0, gt_info);
					}
					
					if(gt_info.getGames().size() > 4){
						banner_game_category.add(gt_info);
					}else{
						item_game_category.add(gt_info);
					}
				}
				
				for(int bci = 0;bci < banner_game_category.size();bci++){
					
					//Init banner category
					initBannerGameCategoryView(bci);
					
					//Init card game category
					if(bci == 0){
						
					}
				}
			}
		}
	}

	public void initBannerGameCategoryView(int bci) {
		GameTypeInfo gi_banner = banner_game_category.get(bci);
		View banner_view = View.inflate(getActivity().getApplicationContext(), R.layout.game_classes_item, null);
		RoundCornerImageView riv_icon = (RoundCornerImageView) banner_view.findViewById(R.id.iv_game_icon);
		TextView tv_cate_name = (TextView) banner_view.findViewById(R.id.title);
		tv_cate_name.setText(gi_banner.getGametypename());
		ImageLoaderHelper.displayImage(gi_banner.getGametypeicon(), riv_icon);
//		GridView gv_banner_game_list = (GridView) banner_view.findViewById(R.id.gv_category_recom_games);
//		GameCategoryRecomGamesAdapter grecomGameAdapter = new GameCategoryRecomGamesAdapter(getActivity().getApplicationContext(), gi_banner);
//		gv_banner_game_list.setAdapter(grecomGameAdapter);
//		grecomGameAdapter.notifyDataSetChanged();
		
		ll_game_categories.addView(banner_view);
	}

	private OnClickListener mItemClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = ((Integer) v.getTag()).intValue();

			if (position < 0) {
				// 搜索
				MainHallActivity.jumpToTabByChildActivity(getActivity(), 3);
				onClickStatics("", "搜索");
			} else {
				GameTypeInfo gtInfo = game_type_list.get(position);

				if (gtInfo.getLabel() == null || "".equals(gtInfo.getLabel())) {
					Intent in = new Intent(getActivity(), MoreClassGameActivity.class);
					in.putExtra("game_type", "0");
					in.putExtra("game_type_number", gtInfo.getGametypenumber());
					in.putExtra("title", gtInfo.getGametypename());
					in.putStringArrayListExtra("singe_type_and_number_list", singe_type_and_number_list);
					startActivity(in);
					onClickStatics(gtInfo.getGametypenumber(), gtInfo.getGametypename());
				} else {
					Intent in = new Intent(getActivity(), MoreClassGameActivity.class);
					in.putExtra("game_type", "1");
					in.putExtra("title", gtInfo.getLabel());
					startActivity(in);
					onClickStatics(gtInfo.getGametypenumber(), gtInfo.getLabel());
				}
			}
		}
	};

	private void onClickStatics(String type, String className) {
		Activity act = getActivity();

		ClickNumStatistics.addGameClassItemStatistics(act, type, className);
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		initView((GameClassDataResult) responseData);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		Log.d("TAG", "error");
	}
}
