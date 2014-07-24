package com.ranger.lpa.test.act;

import com.ranger.lpa.R;
import com.ranger.lpa.connectity.wifi.LPAWifiManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class WifiApTestActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_wifi_test_activity);
	}
	
	public void wifi_action(View v){
		switch (v.getId()) {
		case R.id.btn_start_ap:
			LPAWifiManager.getInstance(getApplicationContext()).startWifiAp();
			break;
		case R.id.btn_stop_ap:
			LPAWifiManager.getInstance(getApplicationContext()).stopWifiAP();
			break;
		case R.id.btn_enable_wifi:
			LPAWifiManager.getInstance(getApplicationContext()).enableWIFIConnection();
		case R.id.btn_connect_ap:
//			LPAWifiManager.getInstance(getApplicationContext()).connectSpecificWIFI("", "");
		default:
			break;
		}
	}

}
