package com.ranger.lpa.connectity.bluetooth;

import java.io.IOException;
import java.util.UUID;

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
	
	private LPABlueToothManager(Context context) {
		
		mContext = context;
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(mBluetoothAdapter == null){
			
		}
		
		mBluetoothManager = (BluetoothManager) mContext.getSystemService(Service.BLUETOOTH_SERVICE);

	};
	
	public static synchronized LPABlueToothManager getInstance(Context context) {
		
		if(_instance == null){
			_instance = new LPABlueToothManager(context);
		}
		
		return _instance;
	}

	public BluetoothManager getmBluetoothManager() {
		return mBluetoothManager;
	}

	public BluetoothAdapter getmBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	public BluetoothDevice getmBluetoothDevice() {
		return mBluetoothDevice;
	}
	
	
}
