package com.ranger.lpa.test.act;

import java.io.IOException;

import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.test.adapter.BtDeviceListAdapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DiscoveryDevicesActivity extends Activity implements OnItemClickListener{

	ProgressBar pb_discovering;
	ListView lv_devices;
	
	BtDeviceListAdapter btla;
	
	Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_bt_discovery_activity);
		
		setupViews();
		
		IntentFilter intentDiscoveryFinish = new IntentFilter(Constants.action_bt_scan_finish);
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new DiscoveryFinishReceiver(), intentDiscoveryFinish);
	}

	public void setupViews() {
		pb_discovering = (ProgressBar) findViewById(R.id.pb_discovery_bt);
		lv_devices = (ListView) findViewById(R.id.lv_bt_devices);
		
		btla = new BtDeviceListAdapter(getApplicationContext());
		lv_devices.setAdapter(btla);
		
		lv_devices.setOnItemClickListener(this);
	}
	
	
	class DiscoveryFinishReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(Constants.action_bt_scan_finish)){
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {

						btla.notifyDataSetChanged();
						pb_discovering.setVisibility(View.GONE);
					}
				});
			}
			
		}
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BluetoothDevice dev = LPABlueToothManager.getInstance(getApplicationContext()).getBluetoothDevices().get(position);
		//connect to dev's service
		try {
			BluetoothSocket socket = dev.createRfcommSocketToServiceRecord(Constants.mUUID);
			if(socket != null){
				Toast.makeText(getApplicationContext(), socket.getRemoteDevice().getName(), 1000).show();
				socket.connect();
				socket.getOutputStream().write("test".getBytes());
				socket.getOutputStream().flush();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
