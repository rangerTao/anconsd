package com.ranger.bmaterials.ui;

import java.util.List;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public class MineMsgAdapter extends BaseAdapter {

	private boolean editMode = false;
	private List<MineMsgItemInfo> listMsgInfo = null;
	private LayoutInflater inflater = null;

	public MineMsgAdapter(Context context, List<MineMsgItemInfo> msgList) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listMsgInfo = msgList;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listMsgInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return listMsgInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mine_msg_listview_item, parent, false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.img_mine_listview_icon);
			holder.title = (TextView) convertView.findViewById(R.id.label_listview_item_title);
			holder.subtitle = (TextView) convertView.findViewById(R.id.label_listview_item_subtitle);
			holder.mark = (ImageView) convertView.findViewById(R.id.img_mine_listview_mark);
			holder.unreadmark = (TextView) convertView.findViewById(R.id.label_mine_listview_unread);
			holder.checkmark = (CheckBox) convertView.findViewById(R.id.checkbox_mine_listview_select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.checkmark.setOnCheckedChangeListener(new CheckListener(position));

		MineMsgItemInfo itemInfo = listMsgInfo.get(position);
		holder.title.setText(itemInfo.msgTitle);
		//add a space before the msgTime to fix bug 5523.
		holder.subtitle.setText(" "+itemInfo.msgTime);
		
		holder.checkmark.setChecked(itemInfo.getChecked());

		if (editMode) {
			holder.checkmark.setVisibility(View.VISIBLE);
			holder.mark.setVisibility(View.INVISIBLE);
		} else {
			holder.checkmark.setVisibility(View.INVISIBLE);
			holder.mark.setVisibility(View.VISIBLE);
		}

		if (itemInfo.unreadMsg) {
			holder.unreadmark.setVisibility(View.VISIBLE);
		} else {
			holder.unreadmark.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView subtitle;
		ImageView mark;
		TextView unreadmark;
		CheckBox checkmark;
	}

	private class CheckListener extends Object implements OnCheckedChangeListener {
		private int position;

		CheckListener(int pos) {
			position = pos;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			MineMsgItemInfo itemInfo = MineMsgAdapter.this.listMsgInfo.get(position);
			itemInfo.setChecked(isChecked);
		}
	}
}
