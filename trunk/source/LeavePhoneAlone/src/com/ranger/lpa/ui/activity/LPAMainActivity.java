package com.ranger.lpa.ui.activity;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;

/**
 * 
 * @Description: TODO
 * 
 * @author taoliang(taoliang@baidu-mgame.com)
 * @date 2014年5月31日 下午8:33:46
 * @version V
 * 
 */
public class LPAMainActivity extends BaseActivity {

	LPABlueToothManager btManager;
	String blueName = "LPA";
	UUID mUuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main_activity);

		btManager = LPABlueToothManager.getInstance(getApplicationContext());

		mUuid = new UUID(13245768, 1234);

		BlueToothReceiver mReceiver = new BlueToothReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
	}

	public void bluetoothservice(View view) {
		switch (view.getId()) {
		case R.id.btn_start_service:

			if (!btManager.getmBluetoothAdapter().isEnabled()) {
				btManager.getmBluetoothAdapter().enable();
				btManager.getmBluetoothAdapter().startDiscovery();
			}
			new Thread() {
				public void run() {

					try {
						BluetoothServerSocket bts = btManager
								.getmBluetoothAdapter()
								.listenUsingRfcommWithServiceRecord(blueName,
										mUuid);

						bts.accept();
						Log.d("TAG", "connection from bluetooth");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();

			break;
		case R.id.btn_connect_service:
			if(!btManager.getmBluetoothAdapter().isEnabled()){
				btManager.getmBluetoothAdapter().enable();
			}
			try {
				btManager.getmBluetoothAdapter().startDiscovery();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case R.id.btn_stop_service:
			break;

		default:
			break;
		}
	}

	class BlueToothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 找到设备
			BluetoothDevice device = null;
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				
				Log.d("TAG", "find device");

				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

					Log.v("TAG",
							"find device:" + device.getName()
									+ device.getAddress());
					if(device.createBond()){
					}
					
					if (device != null) {
						try {
							BluetoothSocket bs = device
									.createRfcommSocketToServiceRecord(mUuid);
						} catch (Exception e) {

						}

					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				Log.d("TAG", "discovery finish");
			}

		}
	}
}
