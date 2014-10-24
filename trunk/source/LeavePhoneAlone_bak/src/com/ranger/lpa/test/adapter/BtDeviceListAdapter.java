package com.ranger.lpa.test.adapter;

import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BtDeviceListAdapter extends BaseAdapter{

	Context mContext;
	LPABlueToothManager lpabtm;
	
	public BtDeviceListAdapter(Context context){
		mContext = context;
		lpabtm = LPABlueToothManager.getInstance(mContext);
	}
	
	@Override
	public int getCount() {
		return lpabtm.getBluetoothDevices().size();
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
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView tvName = new TextView(mContext);
		tvName.setText(lpabtm.getBluetoothDevices().get(position).getName());
		
		return tvName;
	}

}
