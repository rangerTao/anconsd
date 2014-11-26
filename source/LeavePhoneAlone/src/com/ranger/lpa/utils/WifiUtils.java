package com.ranger.lpa.utils;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.ranger.lpa.Constants;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.ui.activity.LPAFoundPhoneCenter;

public class WifiUtils {

    private WifiUtils(){

    }

    private static WifiUtils _instance;

    public static WifiUtils getInstance(){
        if(_instance == null){
            _instance = new WifiUtils();
        }

        return _instance;
    }

    private static String mSSID;

    public interface OnWifiConnected{
        public void onConnected();
    }

    private static OnWifiConnected mWifiConnected;

    public void setmWifiConnected(OnWifiConnected mWifiConnected) {
        this.mWifiConnected = mWifiConnected;
    }

    static WifiInfo wifiInfo;
	static WifiManager wifiManager;
	static Activity mContext;
	static BroadcastReceiver wifiReceiver;

	public static void unRegisterWifiReceiver() {
		mContext.unregisterReceiver(wifiReceiver);
	}

	public static void initWifiSetting(Context context,String ssid) {

        mSSID = ssid;

		wifiReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
					handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
				} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
					detectWifiStatus(wifiManager);
				} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
					handleStateChanged(((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState());
				}
			}

		};

		mContext = (Activity) context;
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		detectWifiStatus(wifiManager);

		connectSavedWifi(wifiReceiver);

	}

	public static boolean isWifiEnabled() {
		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		} else {
			return false;
		}
	}

	public static void connectSavedWifi(BroadcastReceiver wifiReceiver) {

		IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
		intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.registerReceiver(wifiReceiver, intentFilter);

	}

    public static void dismissWifiReceiver(){
        if(wifiReceiver!= null)
            mContext.unregisterReceiver(wifiReceiver);
    }

	public static void detectWifiStatus(WifiManager wifiManager) {

		switch (wifiManager.getWifiState()) {

		case WifiManager.WIFI_STATE_DISABLED:
			break;
		case WifiManager.WIFI_STATE_DISABLING:

			break;
		case WifiManager.WIFI_STATE_ENABLED:

			break;
		case WifiManager.WIFI_STATE_ENABLING:
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			break;
		}
	}

	public static void handleStateChanged(DetailedState state) {
		// WifiInfo is valid if and only if Wi-Fi is enabled.
		// Here we use the state of the check box as an optimization.

		if (state != null && (state.compareTo(DetailedState.CONNECTED) == 0)) {
			WifiInfo info = wifiManager.getConnectionInfo();

            wifiInfo = wifiManager.getConnectionInfo();

            if(wifiInfo.getSSID().equals(mSSID)){
                if(mWifiConnected != null){
                    mWifiConnected.onConnected();
                }
            }

			if (info != null) {
			}
		}
	}

	public static List<ScanResult> getWifiScanResult() {
		wifiManager.startScan();
		return wifiManager.getScanResults();
	}

}