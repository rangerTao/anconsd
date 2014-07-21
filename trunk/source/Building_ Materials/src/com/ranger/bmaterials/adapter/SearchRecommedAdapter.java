package com.ranger.bmaterials.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.MoreGameActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.ui.SearchResultActivity;

public class SearchRecommedAdapter extends AbstractListAdapter<SearchItem> {
    private int off = 3 ;
	public SearchRecommedAdapter(Context context) {
		super(context);
		initItemSize();
		off = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2, context.getResources().getDisplayMetrics());
	}
	static class Holder {
        RoundCornerImageView icon;
		TextView title;
	}
	
	private void initItemSize(){
		int[] screensize = DeviceUtil.getScreensize(context);
		//列数
		int column = 4 ;
		//gridview的横向padding
		int gVPaddingH = (int) context.getResources().getDimension(R.dimen.gridview_padding_horizontal);// (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, context.getResources().getDisplayMetrics());
		//gridview item的横向padding
		int itemPaddingH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, context.getResources().getDisplayMetrics());
		
		textTopMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4, context.getResources().getDisplayMetrics());
		int totalWidth = screensize[0] - gVPaddingH *2;
		int itemWidth = totalWidth / column ;
		imageWidth = itemWidth - itemPaddingH *2;
		
		itemLp = new GridView.LayoutParams(itemWidth, itemWidth);
		imageLp = new RelativeLayout.LayoutParams(imageWidth, imageWidth);
		imageLp.bottomMargin = textTopMargin ;
		
	}

    private class ViewOnClickListener implements View.OnClickListener{

        private int position;

        public ViewOnClickListener(int pos){
            position = pos;
        }

        @Override
        public void onClick(View v) {
            SearchItem item = getItem(position);
            AppManager manager = AppManager.getInstance(context);
            manager.jumpToDetail((Activity) context, item.getGameId(), item.getGameName(), item.getPackageName(), false);
        }
    };
	
	private void setTouchListener(View view,final Holder holder,final int position){
		final View topView = view.findViewById(R.id.game_icon_top);
//		final View underView = view.findViewById(R.id.game_icon_under);
		
		
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					
					FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) holder.icon.getLayoutParams();
					lp.setMargins(0, 0, 0, 0);
					holder.icon.setLayoutParams(lp);
					topView.setLayoutParams(lp);
//					underView.setLayoutParams(lp);
					break;
				case MotionEvent.ACTION_UP:
					FrameLayout.LayoutParams lp2 = (android.widget.FrameLayout.LayoutParams)  holder.icon.getLayoutParams();
					lp2.setMargins(off, off, off, off);
					holder.icon.setLayoutParams(lp2);
					topView.setLayoutParams(lp2);
//					underView.setLayoutParams(lp2);
					
					SearchItem item = getItem(position);
					AppManager manager = AppManager.getInstance(context);
                    manager.jumpToDetail((Activity) context, item.getGameId(), item.getGameName(), item.getPackageName(), false);
                    /*if(ITEM_TYPE_GAME == type){
						//Toast.makeText(context, "game details", 1).show();
						Intent intent = new Intent(context,GameDetailsActivity.class);
						intent.putExtra("gameid", list.get(pos).getGameId());
						intent.putExtra("gamename", list.get(pos).getGameName());
						context.startActivity(intent);
					}else{
						//Toast.makeText(context, "more games", 1).show();
						Intent in = new Intent(context,MoreRecommendGameActivity.class);
						in.putExtra("more_type", "0");
						context.startActivity(in);
					}*/
					break;
				case MotionEvent.ACTION_CANCEL:
					FrameLayout.LayoutParams lp3 = (android.widget.FrameLayout.LayoutParams)  holder.icon.getLayoutParams();
					lp3.setMargins(off, off, off, off);
					holder.icon.setLayoutParams(lp3);
					topView.setLayoutParams(lp3);
//					underView.setLayoutParams(lp3);
					break;
				}
			return true;
			}
		});
	}
	

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.i("SnapNumberAdapter", "SnapNumberAdapter "+position);
		final View view;
		Holder holder;
		if (convertView == null) {
			
			RelativeLayout rl = (RelativeLayout) mInflater.inflate(R.layout.search_recomend_gv_item_square,
					parent, false);
			view = rl ;
			//view.setLayoutParams(itemLp);
			holder = new Holder();
			holder.title = (TextView) view.findViewById(R.id.game_name);
			holder.icon = (RoundCornerImageView) view.findViewById(R.id.game_icon);
			//RelativeLayout.LayoutParams iLp = (android.widget.RelativeLayout.LayoutParams) holder.icon.getLayoutParams();
			//iLp.bottomMargin = textTopMargin ;
			//iLp.width = imageWidth ;
			//iLp.height = imageWidth ;
			//holder.icon.setLayoutParams(iLp);
			//LayoutParams titleLp = holder.title.getLayoutParams(); 
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}
		bindView(position, holder);

        view.setOnClickListener(new ViewOnClickListener(position));

		return view;
	}
	
	final Animation anim = new ScaleAnimation(1.0F, 1.3F, 1.0F, 1.3F, 1, 0.5F, 1, 0.5F);
	private int textTopMargin;
	private int imageWidth;
	private GridView.LayoutParams itemLp;
	private RelativeLayout.LayoutParams imageLp;  
	
	private void bindView(int position, Holder holder ) {
		SearchItem item = getItem(position);
		String gameName = item.getGameName();
		if(gameName.length() > 5){
			gameName = gameName.substring(0,5);
		}
		holder.title.setText(gameName);
		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
	}
	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}
	
}
