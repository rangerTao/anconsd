package com.andconsd.adapter;

import java.util.ArrayList;

import com.andconsd.ui.widget.SettingPreference.FolderItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleFolderListAdapter extends BaseAdapter {

	ArrayList<FolderItem> mFiles;
	Context mContext;
	
	public SimpleFolderListAdapter(ArrayList<FolderItem> files,Context context){
		mFiles = files;
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return mFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return mFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView tv = new TextView(mContext);
		tv.setText(mFiles.get(position).name);
		tv.setTextSize(15.0f);
		
		return tv;
	}

}
