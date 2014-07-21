package com.ranger.bmaterials.adapter;

import java.util.List;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.ui.MineGameItemInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MineGamesAdapter extends BaseAdapter {

	public enum ListType {
		LISTTYPE_DOWNLOADED_GAMES, LISTTYPE_COLLECTED_GAMES
	}

	private List<MineGameItemInfo> mListGameInfo = null;
	private LayoutInflater inflater = null;
	private ListType mListType = ListType.LISTTYPE_DOWNLOADED_GAMES;

	public MineGamesAdapter(Context context, List<MineGameItemInfo> gameList,
			ListType listType) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mListGameInfo = gameList;
		this.mListType = listType;
	}

	@Override
	public int getCount() {
		return mListGameInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mListGameInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (mListType == ListType.LISTTYPE_DOWNLOADED_GAMES) {
			if (convertView == null) {
                convertView = inflater.inflate(R.layout.mine_downloaded_game_listview_item, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.img_mine_listview_icon);
                holder.title = (TextView)convertView.findViewById(R.id.label_listview_item_title);
                holder.subtitle = (TextView)convertView.findViewById(R.id.label_listview_item_subtitle);
                holder.mark = (ImageView)convertView.findViewById(R.id.img_mine_listview_mark);
                
                convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
		} else if (mListType == ListType.LISTTYPE_COLLECTED_GAMES) {

		}

		return convertView;
	}

	static class ViewHolder {
		ImageView icon;
		TextView title;
		TextView subtitle;
		ImageView mark;
	}
}
