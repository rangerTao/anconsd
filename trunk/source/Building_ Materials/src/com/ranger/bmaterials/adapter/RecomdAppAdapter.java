package com.ranger.bmaterials.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.RecommendAppItemInfo;
import com.ranger.bmaterials.view.ImageViewForList;

public class RecomdAppAdapter extends BaseAdapter{

	private List<RecommendAppItemInfo> mGist;
	private Context mContext;
	
	public RecomdAppAdapter(Context context,List<RecommendAppItemInfo> gist){

		this.mContext = context;
		this.mGist = gist;
	}
	
	@Override
	public int getCount() {
		return mGist.size();
	}

	@Override
	public Object getItem(int position) {
		return mGist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView ==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.recommendapp_item, null, false);
			holder.iconView = (ImageViewForList) convertView.findViewById(R.id.recommend_game_icon);
			holder.titleTv = (TextView) convertView.findViewById(R.id.recommend_game_subtitle);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		bindView(position, holder);
		return convertView;
	}
	
	private void bindView(int position, ViewHolder holder) {
		RecommendAppItemInfo itemInfo = mGist.get(position);
		holder.titleTv.setText(itemInfo.getAppName());
		if (TextUtils.isEmpty(itemInfo.getAppicon_Url())) {
			holder.iconView.setImageResource(itemInfo.getIconID());
		} else {
//			ImageLoaderHelper.displayImage(itemInfo.getAppicon_Url(), holder.iconView);	
			holder.iconView.displayImage(itemInfo.getAppicon_Url());
		}
	}
	
	public static class ViewHolder{
		ImageViewForList iconView;
		TextView titleTv;
	}
}