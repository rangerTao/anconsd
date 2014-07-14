package com.ranger.lpa.connectity.wifi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import com.ranger.lpa.utils.Md5Tools;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LPAWifiManager {

	private String mSSID = "LeavePhoneAlone_";
	private String mPasswd;
	
	private static LPAWifiManager _instance;
	
	private WifiManager wifiManager;
	
	public WifiManager getWifiManager() {
		return wifiManager;
	}

	private LPAWifiManager(Context context){
		
	}
	
	public static LPAWifiManager getInstance(Context context){
		if(_instance == null){
			_instance = new LPAWifiManager(context);
	
			_instance.init(context);
		}
		
		return _instance;
	}
	
	private void init(Context context){
		
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		Long time = System.currentTimeMillis();
//		mPasswd = Md5Tools.toMd5((time + "").getBytes(), true);
		mPasswd = "taoliang1985";
	}
	
	public void enableWifiSpot(){
		
		if (!wifiManager.isWifiEnabled()) {
			startWifiAp();
		}
		
	}
	
	public void startWifiAp() {  
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
            // TODO Auto-generated catch block  
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
	
	public void connectSpecificWIFI(String bssid, String pwd) {

		WifiConfiguration wific = new WifiConfiguration();
		wific.BSSID = mSSID;
		wific.preSharedKey = mPasswd;

		wific.allowedAuthAlgorithms
				.set(WifiConfiguration.AuthAlgorithm.OPEN);
		wific.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wific.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wific.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wific.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.CCMP);
		wific.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.TKIP);
		wific.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wific.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		
		int newWID = wifiManager.addNetwork(wific);
		
		Log.d("TAG", "network id " + newWID);

		Log.d("TAG", "is enabled " + wifiManager.enableNetwork(newWID, true));
		
	}
	
}
