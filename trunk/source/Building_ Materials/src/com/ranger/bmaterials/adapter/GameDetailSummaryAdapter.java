package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;

public class GameDetailSummaryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<GameInfo> list;
	private GameDetailsActivity context;
	private DisplayImageOptions options = ImageLoaderHelper
			.getCustomOption(true,R.drawable.game_icon_games_default);
	private boolean openRecommendAndCloseThis;

	public GameDetailSummaryAdapter(GameDetailsActivity context,
			ArrayList<GameInfo> list, int density,boolean openRecommendAndCloseThis) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.openRecommendAndCloseThis =  openRecommendAndCloseThis;
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyHolder holder = null;
		if (convertView == null) {
			holder = new MyHolder();

			convertView = mInflater.inflate(
					R.layout.item_gv_game_recommend_activity, null);

			holder.iv = (RoundCornerImageView) convertView
					.findViewById(R.id.item_gv_game_recommend_iv);
			holder.tv = (TextView) convertView
					.findViewById(R.id.item_gv_game_recommend_tv);
			boolean[] enabled = { true, true ,true,true};
			holder.iv.setCornersEnabled(enabled);
			holder.iv.setRadius(UIUtil.dip2px(context, 8f));
			holder.iv.setDisplayImageOptions(options);
			convertView.setTag(holder);
			holder.iv.setDisplayImageOptions(options);
		} else {
			holder = (MyHolder) convertView.getTag();
		}

        convertView.setOnClickListener(new ViewOnClickListener(position));

		GameInfo gameInfo = list.get(position);
		holder.iv.setImageUrl(gameInfo.getIconUrl());
		holder.tv.setText(gameInfo.getGameName());

		return convertView;
	}

    private class ViewOnClickListener implements View.OnClickListener{

        private int position;

        public ViewOnClickListener(int pos){
            position = pos;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, GameDetailsActivity.class);
            intent.putExtra("gameid", list.get(position).getGameId());
            intent.putExtra("gamename", list.get(position).getGameName());
            intent.putExtra("openRecommendAndCloseThis", true);
            context.startActivity(intent);
            ClickNumStatistics.addGameRecomClickStatisDetail(context,list.get(position).getGameId());
            if(openRecommendAndCloseThis){
                context.finish();
            }
        }
    };

//	private class SmallerAnimationListener implements AnimationListener {
//		private int position;
//
//		public SmallerAnimationListener(int position) {
//			this.position = position;
//		}
//
//		@Override
//		public void onAnimationStart(Animation animation) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onAnimationRepeat(Animation animation) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onAnimationEnd(Animation animation) {
//			// TODO Auto-generated method stub
//			Intent intent = new Intent(context, GameDetailsActivity.class);
//			intent.putExtra("gameid", list.get(position).getGameId());
//			intent.putExtra("gamename", list.get(position).getGameName());
//			intent.putExtra("openRecommendAndCloseThis", true);
//			context.startActivity(intent);
//			ClickNumStatistics.addGameDetailRdGamesClickStatistics(context);
//			if(openRecommendAndCloseThis){
//				context.finish();
//			}
//		}
//	};

	class MyHolder {
		RoundCornerImageView iv;
		TextView tv;
		ImageView front_shade;
	}

}
