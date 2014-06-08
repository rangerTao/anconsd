package com.ranger.lpa.ui.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.pojos.IncomeResult;
import com.ranger.lpa.receiver.BlueToothReceiver;
import com.ranger.lpa.test.act.DiscoveryDevicesActivity;

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

	TextView tv_log;

	Handler mHandler = new Handler() {

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main_activity);

		btManager = LPABlueToothManager.getInstance(getApplicationContext());

		tv_log = (TextView) findViewById(R.id.tv_log);
	}

	public void bluetoothservice(View view) {
		switch (view.getId()) {
		case R.id.btn_start_service:

			if (!btManager.getBluetoothAdapter().isEnabled()) {
				// btManager.startDiscovery();

			}
			btManager.getBluetoothAdapter().setName("test");
			btManager.setDeviceVisiable(this);
			new Thread() {
				public void run() {

					try {
						BluetoothServerSocket bts = btManager
								.getBluetoothAdapter()
								.listenUsingRfcommWithServiceRecord(blueName,
										Constants.mUUID);

						mHandler.post(new Runnable() {

							@Override
							public void run() {

								tv_log.append("waiting client \n");
							}
						});
						final BluetoothSocket bs = bts.accept();
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								tv_log.append(bs.getRemoteDevice().getName()
										+ "\n");
							}
						});
						
						if(bs!=null){
							InputStream inputStream = bs.getInputStream();
							
							BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
							
							String strIncome = br.readLine();
							
							Gson gsonIncome = new Gson();
							IncomeResult ir = gsonIncome.fromJson(strIncome, IncomeResult.class);
							
							while (ir.getErrcode() != 10000) {
								
								Log.d("TAG", ir.getErrmsg());
								
								strIncome = br.readLine();
								
								ir = gsonIncome.fromJson(strIncome, IncomeResult.class);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

			break;
		case R.id.btn_connect_service:
			if (!btManager.getBluetoothAdapter().isEnabled()) {
				btManager.getBluetoothAdapter().enable();
			}

			btManager.getBluetoothAdapter().startDiscovery();

			startActivity(new Intent(this, DiscoveryDevicesActivity.class));

			break;
		case R.id.btn_stop_service:
			break;

		default:
			break;
		}
	}

}
