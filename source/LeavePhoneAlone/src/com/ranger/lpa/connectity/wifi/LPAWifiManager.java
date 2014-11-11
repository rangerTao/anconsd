package com.ranger.lpa.connectity.wifi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import com.ranger.lpa.LPApplication;
import com.ranger.lpa.pojos.WifiInfo;
import com.ranger.lpa.tools.DeviceUtil;
import com.ranger.lpa.utils.Md5Tools;
import com.ranger.lpa.utils.NetworkUtil;
import com.ranger.lpa.utils.WifiUtils;
import com.tencent.mm.sdk.platformtools.PhoneUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LPAWifiManager {

	private static String mSSID = "LeavePhoneAlone_";
	private String mPasswd;

    private Context mContext;

    private WifiInfo mWifiInfo;
	
	private static LPAWifiManager _instance;
	
	private static WifiManager wifiManager;
	
	public WifiManager getWifiManager() {
		return wifiManager;
	}

	private LPAWifiManager(Context context){
		 mContext = context;
	}

    private static WifiUtils.OnWifiConnected mWifiConnected;

    public void setOnWifiConnected(WifiUtils.OnWifiConnected WifiConnected){
        mWifiConnected = WifiConnected;
    }
	
	public static LPAWifiManager getInstance(Context context){
		if(_instance == null){
			_instance = new LPAWifiManager(context);
	
			_instance.init(context);
		}
		
		return _instance;
	}
	
	private void init(Context context){

        mContext = context;
		
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		Long time = System.currentTimeMillis();
		mPasswd = Md5Tools.toMd5((time + "").getBytes(), true);
	}
	
	public void enableWifiSpot(){
		
		if (!wifiManager.isWifiEnabled()) {
			startWifiAp();
		}
		
	}

    private BroadcastReceiver wifiReceiver;

    public void startWifiAp(WifiUtils.OnWifiConnected wifi){

        mWifiConnected = wifi;
        startWifiAp();

    }
	
	public void startWifiAp() {

        mSSID += Md5Tools.toMd5(DeviceUtil.getImei(mContext).getBytes(),true);

        if(wifiManager.getConnectionInfo().getSSID().equals(mSSID)){
            doWifiPotInited();
            return;
        }

        Method method1 = null;
        try {

            method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();


            netConfig.SSID = mSSID;
            netConfig.preSharedKey = mPasswd;  
  
            netConfig.allowedAuthAlgorithms  
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);  
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);  
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);  
            netConfig.allowedPairwiseCiphers  
                    .set(WifiConfiguration.PairwiseCipher.CCMP);  
            netConfig.allowedPairwiseCiphers  
                    .set(WifiConfiguration.PairwiseCipher.TKIP);  
            netConfig.allowedGroupCiphers  
                    .set(WifiConfiguration.GroupCipher.CCMP);  
            netConfig.allowedGroupCiphers  
                    .set(WifiConfiguration.GroupCipher.TKIP);  
  
            method1.invoke(wifiManager, netConfig, true);  

            String mip = getLocalIpAddress();

            wifiReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                        handleStateChanged(android.net.wifi.WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
                    } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                        detectWifiStatus(wifiManager);
                    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                        handleStateChanged(((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState());
                    }
                }

            };

            connectSavedWifi(wifiReceiver);

            mWifiInfo = new WifiInfo(mSSID,mPasswd,mip);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            e.printStackTrace();  
        } catch (InvocationTargetException e) {  
            e.printStackTrace();  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (NoSuchMethodException e) {  
            e.printStackTrace();  
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connectSavedWifi(BroadcastReceiver wifiReceiver) {

        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(wifiReceiver, intentFilter);

    }

    public static void detectWifiStatus(WifiManager wifiManager) {

        switch (wifiManager.getWifiState()) {

            case WifiManager.WIFI_STATE_DISABLED:
                break;
            case WifiManager.WIFI_STATE_DISABLING:

                break;
            case WifiManager.WIFI_STATE_ENABLED:

                android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                if(wifiInfo.getSSID().equals("\"" + mSSID + "\"")){
                    doWifiPotInited();
                }
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
        }
    }

    private static void doWifiPotInited() {
        if(mWifiConnected != null){

            LPApplication.getInstance().setLocalIP(getLocalIpAddress());

            mWifiConnected.onConnected();
        }
    }

    public static void handleStateChanged(NetworkInfo.DetailedState state) {
        // WifiInfo is valid if and only if Wi-Fi is enabled.
        // Here we use the state of the check box as an optimization.
    }

    /**
     * Get the wifi host's ip address.
     * @return
     */
    public WifiInfo getmWifiInfo() {
        return mWifiInfo;
    }

    /**
	 * 关闭wifi热点。连接到可用网络
	 */
	public void stopWifiAP(){

		if (isWifiApEnabled(wifiManager)) {  
            try {  
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");  
                method.setAccessible(true);  
  
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);  
  
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);  
                method2.invoke(wifiManager, config, false);  
            } catch (NoSuchMethodException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IllegalArgumentException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IllegalAccessException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (InvocationTargetException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
		
	}
	
	/**
	 * 判断当前热点是否可用
	 * @param wifiManager
	 * @return
	 */
	private static boolean isWifiApEnabled(WifiManager wifiManager) {  
        try {  
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");  
            method.setAccessible(true);  
            return (Boolean) method.invoke(wifiManager);  
  
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();  
        }  
  
        return false;  
    }  
	
	/**
	 * 激活wifi
	 * @return
	 */
	public boolean enableWIFIConnection(){

		wifiManager.setWifiEnabled(true);
		
		return true;
	}
	
//	public void connectSpecificWIFI(String bssid, String pwd) {
//
//		WifiConfiguration wific = new WifiConfiguration();
//		wific.BSSID = mSSID;
//		wific.preSharedKey = mPasswd;
//
//		wific.allowedAuthAlgorithms
//				.set(WifiConfiguration.AuthAlgorithm.OPEN);
//		wific.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//		wific.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//		wific.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//		wific.allowedPairwiseCiphers
//				.set(WifiConfiguration.PairwiseCipher.CCMP);
//		wific.allowedPairwiseCiphers
//				.set(WifiConfiguration.PairwiseCipher.TKIP);
//		wific.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//		wific.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//
//		int newWID = wifiManager.addNetwork(wific);
//
//		Log.d("TAG", "network id " + newWID);
//
//		Log.d("TAG", "is enabled " + wifiManager.enableNetwork(newWID, true));
//
//	}

    /**
     * Get the local IP address
     *
     * @return IP address
     */
    public static String getLocalIpAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()
                            && (inetAddress.getHostAddress().toString().startsWith("192")  || inetAddress
                            .getHostAddress().toString().startsWith("172"))) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    public boolean connectWifi(WifiInfo wInfo){
        if(wInfo != null){

            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"" + wInfo.getmSSID() + "\"";

            wc.hiddenSSID = true;

            wc.status = WifiConfiguration.Status.ENABLED;

            NetworkUtil.setWifiConfigurationSettings(wc, wInfo.getmPWD());

            int resId = wifiManager.addNetwork(wc);

            if(resId != -1){
                wifiManager.enableNetwork(resId, true);
                return true;
            }else{
                return false;
            }
        }

        return false;
    }
	
}
