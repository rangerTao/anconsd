package com.ranger.phonerecorder.adapter;

import java.util.ArrayList;

import com.ranger.phonerecorder.R;
import com.ranger.phonerecorder.pojos.PhoneRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReminderRecordAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	ArrayList<PhoneRecord> records;
	Context mContext;

	public ReminderRecordAdapter(Context context, ArrayList<PhoneRecord> incalls) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		records = incalls;
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
			View view = mInflater.inflate(R.layout.item_remind_record_layout, null);
			irh = new IncallRecordHolder();

			irh.tvName = (TextView) view.findViewById(R.id.tv_record_name);

			view.setTag(irh);

			convertView = view;
		} else {
			irh = (IncallRecordHolder) convertView.getTag();
		}

		irh.tvName.setText(ir.name);

		return convertView;
	}

	public class IncallRecordHolder {
		TextView tvName;
	}

}
