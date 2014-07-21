package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.HomeAppGridInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;

public class HomeAppGridAdapter extends BaseAdapter {
	private Activity context;
	public CopyOnWriteArrayList<HomeAppGridInfo> showAppList = new CopyOnWriteArrayList<HomeAppGridInfo>();
	private DisplayImageOptions options;
	private static final int MY_LOCAL_GAME_TYPE = 1000;
	private static final int DOWNLOAD_GAME_TYPE = 1001;

	public HomeAppGridAdapter(Activity c) {
		this.context = c;

		options = ImageLoaderHelper.getDefaultImageOptions(false);
	}

	private class SmallerAnimationListener implements AnimationListener {
		private View v;

		public SmallerAnimationListener(View v) {
			this.v = v;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if (showAppList != null && showAppList.size() != 0) {
				int pos = (Integer) v.getTag(R.id.home_activity_grid);
				HomeAppGridInfo homeInfo = showAppList.get(pos);
				Intent intent = new Intent(context, GameDetailsActivity.class);
				intent.putExtra(GameDetailConstants.KEY_GAME_ID, homeInfo.gameId);
				intent.putExtra(GameDetailConstants.KEY_GAME_NAME, homeInfo.gameName);
				context.startActivity(intent);
				ClickNumStatistics.addHomeGameCoverStatistics(context, homeInfo.gameName);
			}
		}
	};

	@Override
	public int getCount() {
		// return showAppList.isEmpty() ? 1 : Math.min(9, showAppList.size());
		return showAppList.isEmpty() ? 0 : Math.min(8, showAppList.size());
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderView holder;
		// int type = getItemViewType(position);

		if (convertView == null) {
			holder = new HolderView();
			convertView = View.inflate(context, R.layout.item_gv_home_activity, null);

			holder.icon = (RoundCornerImageView) convertView.findViewById(R.id.home_grid_icon);
			holder.game_name = (TextView) convertView.findViewById(R.id.home_grid_game_name);
			holder.home_grid_game_dlcount = (TextView) convertView.findViewById(R.id.home_grid_game_dlcount);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (showAppList != null && showAppList.size() != 0) {
						int pos = (Integer) v.getTag(R.id.home_activity_grid);
						HomeAppGridInfo homeInfo = showAppList.get(pos);
						Intent intent = new Intent(context, GameDetailsActivity.class);
						intent.putExtra(GameDetailConstants.KEY_GAME_ID, homeInfo.gameId);
						intent.putExtra(GameDetailConstants.KEY_GAME_NAME, homeInfo.gameName);
						context.startActivity(intent);
						ClickNumStatistics.addHomeGameCoverStatistics(context, homeInfo.gameName);
					}
				}
			});
			// ItemOnTouchAnimationListener l = new
			// ItemOnTouchAnimationListener(context, new
			// SmallerAnimationListener(convertView));
			// l.setAnimationView(convertView);
			// convertView.setOnTouchListener(l);

			convertView.setTag(holder);

		} else {
			holder = (HolderView) convertView.getTag();
		}

		// if (type == MY_LOCAL_GAME_TYPE) {
		// convertView.setVisibility(View.INVISIBLE);
		// } else {

		convertView.setVisibility(View.VISIBLE);
		if (showAppList != null && showAppList.size() != 0) {
			HomeAppGridInfo homeInfo = showAppList.get(position);
			ImageLoaderHelper.displayImage(homeInfo.iconUrl, holder.icon, ImageLoaderHelper.getCustomOption(R.drawable.game_icon_list_default));
			holder.game_name.setText(homeInfo.gameName);
			holder.game_name.setTextColor(Color.BLACK);

            try{
                holder.home_grid_game_dlcount.setText(StringUtil.formatTimes(Long.valueOf(homeInfo.downloadcount)));
            }catch (Exception ex){
                ex.printStackTrace();
            }
		}
		// }
		convertView.setTag(R.id.home_activity_grid, position);

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		// if (position == 0)
		// return MY_LOCAL_GAME_TYPE;
		return DOWNLOAD_GAME_TYPE;
	}

	public class HolderView {
		RoundCornerImageView icon;
		TextView game_name;
		TextView home_grid_game_dlcount;
	}

}
