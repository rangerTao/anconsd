package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.content.Context;
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
import com.ranger.bmaterials.listener.ItemOnTouchAnimationListener;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.MoreGameActivity;
import com.ranger.bmaterials.view.NetImageView;

public class GameHotAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<GameInfo> list;
	private Context context;

	private DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.game_icon_games_default);
//	private DisplayImageOptions options = new DisplayImageOptions.Builder()
//	.cacheInMemory().cacheOnDisc()
//	.showStubImage(R.drawable.game_icon_games_default)
//	.showImageForEmptyUri(R.drawable.game_icon_games_default)
//	.showImageOnFail(R.drawable.game_icon_games_default)
//	.bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用 每像素站2byte 默认888占4byte
//	.imageScaleType(ImageScaleType.EXACTLY).build();

	private static final int ITEM_TYPE_GAME = 1;
	private static final int ITEM_TYPE_MORE_GAME = 2;

	public GameHotAdapter(Context context, ArrayList<GameInfo> list, int density) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size() + 1;
		else
			return 0;
	}

	@Override
	public int getItemViewType(int position) {
		// return super.getItemViewType(position);
		if (list.size() == 0) {
			return ITEM_TYPE_MORE_GAME;
		}
		if (position < list.size())
			return ITEM_TYPE_GAME;
		else
			return ITEM_TYPE_MORE_GAME;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MyHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null)
        {
            holder = new MyHolder();

            switch (type)
            {
                case ITEM_TYPE_MORE_GAME:
                case ITEM_TYPE_GAME:
                    convertView = mInflater.inflate(R.layout.item_gv_game_hot_activity, null);

                    holder.iv = (NetImageView) convertView.findViewById(R.id.iv_item_gv_game_hot_activity);
                    holder.iv.setDisplayImageOptions(options);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_item_gv_game_hot_activity);
                    holder.tv2 = (TextView) convertView.findViewById(R.id.tv_top_label_item_gv_game_hot_act);

                    break;
            }

            convertView.setTag(holder);
        }
        else
        {
            holder = (MyHolder) convertView.getTag();
        }

        ((ViewGroup) holder.iv.getParent()).setOnTouchListener(new ItemOnTouchAnimationListener(context, new SmallerAnimationListener(position)));

        switch (type)
        {
            case ITEM_TYPE_GAME:
                GameInfo gameInfo = list.get(position);
                //ImageLoaderHelper.displayImage(gameInfo.getIconUrl(), holder.iv, options);
                holder.iv.setImageUrl(gameInfo.getIconUrl());

                holder.tv.setText(gameInfo.getGameName());
                if (position < 4)
                    holder.tv2.setVisibility(View.VISIBLE);
                else
                    holder.tv2.setVisibility(View.INVISIBLE);
                if (position == 0)
                {
                    holder.tv2.setText("Top 1");
                    holder.tv2.setBackgroundResource(R.drawable.bg_tv_item_hot_games);
                }
                else if (position == 1)
                {
                    holder.tv2.setText("Top 2");
                    holder.tv2.setBackgroundResource(R.drawable.bg_tv_item_hot2_games);
                }
                else if (position == 2)
                {
                    holder.tv2.setText("Top 3");
                    holder.tv2.setBackgroundResource(R.drawable.bg_tv_item_hot3_games);
                }
                else if (position == 3)
                {
                    holder.tv2.setText("Top 4");
                    holder.tv2.setBackgroundResource(R.drawable.bg_tv_item_hot4_games);
                }
                break;
            case ITEM_TYPE_MORE_GAME:
                holder.iv.setImageResource(R.drawable.icon_more_games);
                holder.tv.setText(R.string.label_more_games);
                holder.tv2.setVisibility(View.INVISIBLE);
                break;
        }

        return convertView;
    }

	private class SmallerAnimationListener implements AnimationListener {
		private int position;

		public SmallerAnimationListener(int position) {
			this.position = position;
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
			if (ITEM_TYPE_GAME == getItemViewType(position)) {
				// Toast.makeText(context, "game details", 1).show();
				Intent intent = new Intent(context, GameDetailsActivity.class);
				intent.putExtra("gameid", list.get(position).getGameId());
				intent.putExtra("gamename", list.get(position).getGameName());
				context.startActivity(intent);
				ClickNumStatistics.addGameTabRankClickStatistis(context, list
						.get(position).getGameName());
			} else {
				// Toast.makeText(context, "more games", 1).show();
				Intent in = new Intent(context, MoreGameActivity.class);
				in.putExtra("more_type", "1");
				in.putExtra(MoreGameActivity.EXTRA_KEY_COUNT_FILTERED, null != list ? list.size() : 0);
				context.startActivity(in);
				ClickNumStatistics.addGameHotMoreStatistics(context);
			}
		}
	};

	class MyHolder {
	    NetImageView iv;
		TextView tv;
		TextView tv2;
	}

}
