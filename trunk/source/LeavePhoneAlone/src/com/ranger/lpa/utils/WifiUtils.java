package com.ranger.lpa.utils;

import java.util.List;

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

import com.ranger.lpa.Constants;
import com.ranger.lpa.ui.activity.LPAFoundPhoneCenter;

public class WifiUtils {

	static WifiInfo wifiInfo;
	static WifiManager wifiManager;
	static LPAFoundPhoneCenter mContext;
	static BroadcastReceiver wifiReceiver;

	public static void unRegisterWifiReceiver() {
		mContext.unregisterReceiver(wifiReceiver);
	}

	public static void initWifiSetting(Context context) {

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

		mContext = (LPAFoundPhoneCenter) context;
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

	public static void detectWifiStatus(WifiManager wifiManager) {

		Message detail = new Message();
		detail.what = Constants.WIFI_CONNECTIONINFO;

		switch (wifiManager.getWifiState()) {

		case WifiManager.WIFI_STATE_DISABLED:
			break;
		case WifiManager.WIFI_STATE_DISABLING:

			break;
		case WifiManager.WIFI_STATE_ENABLED:

			wifiInfo = wifiManager.getConnectionInfo();
			if (wifiInfo.getNetworkId() != -1) {
                detail.what = LPAFoundPhoneCenter.MSG_WIFI_CONNECTED;
			} else {
                detail.what = LPAFoundPhoneCenter.MSG_WIFI_FAILED;
			}
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			break;
		}

		mContext.mHandler.sendMessage(detail);
	}

	public static int getCalculatedWifiLevel(WifiInfo info) {

		return WifiManager.calculateSignalLevel(info.getRssi(), 5);

	}

	private static void handleStateChanged(DetailedState state) {
		// WifiInfo is valid if and only if Wi-Fi is enabled.
		// Here we use the state of the check box as an optimization.
		if (state != null) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (info != null) {
				Message detail = new Message();
				detail.what = Constants.WIFI_CONNECTIONINFO;
				mContext.mHandler.sendMessage(detail);
			}
		}
	}

	public static List<ScanResult> getWifiScanResult() {
		wifiManager.startScan();
		return wifiManager.getScanResults();
	}

}