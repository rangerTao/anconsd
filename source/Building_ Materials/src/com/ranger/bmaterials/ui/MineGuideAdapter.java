package com.ranger.bmaterials.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public class MineGuideAdapter extends BaseAdapter {

	private List<MineGuideItemInfo> listGuideInfo = null;
	private LayoutInflater inflater = null;
	
	public MineGuideAdapter(Context context, List<MineGuideItemInfo> guideList) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listGuideInfo = guideList;
	}
	
	@Override
	public int getCount() {
		return listGuideInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return listGuideInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mine_collected_guide_listview_item, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.label_listview_item_title);
			holder.subtitle = (TextView) convertView.findViewById(R.id.label_listview_item_subtitle);
			holder.mark = (ImageView) convertView.findViewById(R.id.img_mine_listview_mark);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MineGuideItemInfo itemInfo = listGuideInfo.get(position);
		holder.title.setText(itemInfo.guideTitle);
		holder.subtitle.setText(itemInfo.guideTime);
		
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		TextView subtitle;
		ImageView mark;
	}
}
