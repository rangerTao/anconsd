package com.ranger.lpa.receiver;

import com.ranger.lpa.Constants;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BlueToothReceiver extends BroadcastReceiver{


	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// 找到设备
		BluetoothDevice device = null;
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			
			device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

				if (device != null) {
					LPABlueToothManager.getInstance(context).addBlueToothDevice(device);
				}
			}else{
				LPABlueToothManager.getInstance(context).addBlueToothDevice(device);
			}
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
				.equals(action)) {
			LocalBroadcastManager.getInstance(LPApplication.getInstance().getApplicationContext()).sendBroadcast(new Intent(Constants.action_bt_scan_finish));
		}
	}

}
