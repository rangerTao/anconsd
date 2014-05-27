package com.ranger.phonerecorder.adapter;

import java.io.File;
import java.util.ArrayList;

import com.ranger.phonerecorder.R;
import com.ranger.phonerecorder.pojos.PhoneRecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class IncallRecordAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	ArrayList<PhoneRecord> records;
	Context mContext;
	SharedPreferences spUpload;

	public IncallRecordAdapter(Context context, ArrayList<PhoneRecord> incalls) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		records = incalls;
		spUpload = context.getSharedPreferences("upload",Context.MODE_WORLD_READABLE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return records.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return records.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PhoneRecord ir = (PhoneRecord) getItem(position);
		IncallRecordHolder irh;

		if (convertView == null) {
			View view = mInflater.inflate(R.layout.item_incall_record_layout, null);
			irh = new IncallRecordHolder();

			irh.tvName = (TextView) view.findViewById(R.id.tv_record_name);
			irh.tvTime = (TextView) view.findViewById(R.id.tvFileTime);
			irh.tvUpload = (TextView) view.findViewById(R.id.tvFileUploadStatus);

			view.setTag(irh);

			convertView = view;
		} else {
			irh = (IncallRecordHolder) convertView.getTag();
		}

		String filename = ir.name;
		if(filename.contains("_")){
			filename = filename.substring(0,filename.indexOf("_"));
		}
		irh.tvName.setText(filename);
		irh.tvTime.setText(ir.create_time);
		if(spUpload.getBoolean(ir.name, false)){
			irh.tvUpload.setText("已上传");
		}else{
			irh.tvUpload.setText("未上传");
		}

		return convertView;
	}

	public class IncallRecordHolder {
		TextView tvName;
		TextView tvTime;
		TextView tvUpload;
	}

}
