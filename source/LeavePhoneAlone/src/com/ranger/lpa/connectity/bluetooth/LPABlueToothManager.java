package com.ranger.lpa.connectity.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @Description: TODO
 * 
 * @author taoliang(taoliang@baidu-mgame.com)
 * @date 2014年6月3日 下午7:19:36
 * @version V
 * 
 */
public class LPABlueToothManager {

	private static LPABlueToothManager _instance;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private Context mContext;

	private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

	private LPABlueToothManager(Context context) {

		mContext = context;

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {

		}

//		mBluetoothManager = (BluetoothManager) mContext
//				.getSystemService(Service.BLUETOOTH_SERVICE);

	};

	public static synchronized LPABlueToothManager getInstance(Context context) {

		if (_instance == null) {
			_instance = new LPABlueToothManager(context);
		}

		return _instance;
	}

	public BluetoothManager getBluetoothManager() {
		return mBluetoothManager;
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	public BluetoothDevice getBluetoothDevice() {
		return mBluetoothDevice;
	}

	public ArrayList<BluetoothDevice> getBluetoothDevices() {
		if (devices == null)
			devices = new ArrayList<BluetoothDevice>();

		return devices;
	}

	// 查找蓝牙设备
	private static final int REQUEST_DISCOVERABLE = 0x2;

	public boolean setDeviceVisiable(Activity act) {

		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}

		// 设置蓝牙可见
		Intent enabler = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		act.startActivityForResult(enabler, REQUEST_DISCOVERABLE);

		return false;
	}

	public void startDiscovery() {
		mBluetoothAdapter.startDiscovery();
	}

	public void addBlueToothDevice(BluetoothDevice device) {
		if (devices == null) {
			devices = new ArrayList<BluetoothDevice>();
		}

		devices.add(device);
	}

}
