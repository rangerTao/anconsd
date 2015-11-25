package com.andconsd.framework.utils;

import java.util.Locale;

import com.andconsd.AndApplication;

import android.content.Context;
import android.database.Cursor;
import android.net.*;
import android.telephony.TelephonyManager;

public class ConnectManager {
	// Connect type
	public static final int Net_ConnType_2G = 1;
	public static final int Net_ConnType_3G = 2;
	public static final int Net_ConnType_WIFI = 3;

	public static final String MOBILE_UNI_PROXY_IP = "10.0.0.172";
	public static final String TELCOM_PROXY_IP = "10.0.0.200";
	public static final String PROXY_PORT = "80";

	public static final String CHINA_MOBILE_WAP = "CMWAP";
	public static final String CHINA_UNI_WAP = "UNIWAP";
	public static final String CHINA_UNI_3G = "3GWAP";
	public static final String CHINA_TELCOM = "CTWAP";

	private static final String TAG = "ConnectManager";
	private static final boolean DEBUG = false;
	private String mApn;
	private String mProxy;
	private String mPort;
	private boolean mUseWap;
	public static final Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	
	private static ConnectManager manager = null;
	
	public static ConnectManager Instance()
	{
	    if (null == manager)
	    {
	        manager = new ConnectManager(AndApplication.getAppInstance());
	    }
	    
	    return manager;
	}

	public ConnectManager(Context context) {
		try {
			checkNetworkType(context);
		} catch (SecurityException e) {
			checkConnectType(context);
		}

	}

	private void checkApn(Context context) {
		Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI,
				new String[] { "_id", "apn", "proxy", "port" }, null, null,
				null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int i = cursor.getColumnIndex("apn");
				int j = cursor.getColumnIndex("proxy");
				int k = cursor.getColumnIndex("port");
				mApn = cursor.getString(i);
				mProxy = cursor.getString(j);
				mPort = cursor.getString(k);
				if (mProxy != null && mProxy.length() > 0) {
					if ("10.0.0.172".equals(mProxy.trim())) {
						mUseWap = true;
						mPort = "80";
					} else if ("10.0.0.200".equals(mProxy.trim())) {
						mUseWap = true;
						mPort = "80";
					} else {
						mUseWap = false;
					}
				} else if (mApn != null) {
					String s = mApn.toUpperCase(Locale.getDefault());
					if (s.equals("CMWAP") || s.equals("UNIWAP")
							|| s.equals("3GWAP")) {
						mUseWap = true;
						mProxy = "10.0.0.172";
						mPort = "80";
					} else if (s.equals("CTWAP")) {
						mUseWap = true;
						mProxy = "10.0.0.200";
						mPort = "80";
					}
				} else {
					mUseWap = false;
				}
			}
			cursor.close();
		}
	}

	/*
	 * Get Current connection type
	 * 
	 * @Return ConnectType have three kind of type
	 */
	public void checkConnectType(Context context) {

		final ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conn != null) {

			NetworkInfo info = conn.getActiveNetworkInfo();

			if (info != null) {
				String connStr = info.getTypeName();

				if (connStr.equalsIgnoreCase("WIFI")) {

					// set member param
					mUseWap = false;

				} else if (connStr.equalsIgnoreCase("MOBILE")) {

					String apn = info.getExtraInfo();

					if (apn.indexOf("wap") > -1) {

						if (apn.equals("cmwap") || apn.equals("uniwap")
								|| apn.equals("3gwap")) {

							mUseWap = true;
							mProxy = "10.0.0.172";
							mPort = "80";

						} else if (apn.equals("ctwap")) {

							mUseWap = true;
							mProxy = "10.0.0.200";
							mPort = "80";

						} else {
							// not use wap
							mUseWap = false;
						}

					} else {
						// not use wap
						mUseWap = false;
					}
				}
			}
		}
	}

	private void checkNetworkType(Context context) {
		ConnectivityManager connectivitymanager = (ConnectivityManager) context
				.getApplicationContext().getSystemService("connectivity");
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		if (networkinfo != null)
			if ("wifi".equals(networkinfo.getTypeName().toLowerCase(Locale.getDefault())))
				mUseWap = false;
			else
				checkApn(context);
	}

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivitymanager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (connectivitymanager == null)
			return false;
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		if (networkinfo != null)
			return networkinfo.isAvailable()
					|| networkinfo.isConnectedOrConnecting();
		else
			return false;
	}

	/**
	 * make true current connect service is wifi
	 * 
	 * @param context
	 *            Application context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null)
			return false;
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * Judge whether fast connection
	 * 
	 * @param context
	 *            Application context true if fast connection, otherwise false
	 */
	public static boolean isConnectionFast(Context context) {
		int type;
		int subType;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null) {
			return false;
		}
		type = ConnectivityManager.TYPE_WIFI;

		TelephonyManager telephoneManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (telephoneManager == null) {
			return false;
		}

		subType = telephoneManager.getNetworkType();

		if (type == ConnectivityManager.TYPE_WIFI) {
			if (Constants.DEBUG)
				System.out.println("CONNECTED VIA WIFI");
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
				// case TelephonyManager.NETWORK_TYPE_HSDPA:
				// return true; // ~ 2-14 Mbps
				// case TelephonyManager.NETWORK_TYPE_HSPA:
				// return true; // ~ 700-1700 kbps
				// case TelephonyManager.NETWORK_TYPE_HSUPA:
				// return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
				// NOT AVAILABLE YET IN API LEVEL 7
				// case Connectivity.NETWORK_TYPE_EHRPD:
				// return true; // ~ 1-2 Mbps
				// case Connectivity.NETWORK_TYPE_EVDO_B:
				// return true; // ~ 5 Mbps
				// case Connectivity.NETWORK_TYPE_HSPAP:
				// return true; // ~ 10-20 Mbps
				// case Connectivity.NETWORK_TYPE_IDEN:
				// return false; // ~25 kbps
				// case Connectivity.NETWORK_TYPE_LTE:
				// return true; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return false;
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isWapNetwork() {
		return mUseWap;
	}

	// public String getApn()
	// {
	// return mApn;
	// }

	public String getProxy() {
		return mProxy;
	}

	public String getProxyPort() {
		return mPort;
	}

	public String getConnectionString(Context context) {

		if (this.isWifi(context)) {
			return "3";
		}

		try {

			checkConnectType(context);
		} catch (Exception e) {
			return "1";
		}

		if (this.isWapNetwork()) {
			return "2";
		}

		return "1";
	}
}