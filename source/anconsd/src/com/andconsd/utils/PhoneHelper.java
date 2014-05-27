/**
 * 
 */
package com.andconsd.utils;


import com.andconsd.AndApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public final class PhoneHelper {
	
	private static TelephonyManager mTelephonyManager = null;
	
	public static String getPhoneNumber(){
		Context appcontext = (Context)AndApplication.getAppInstance();
		
		if (mTelephonyManager == null){
			mTelephonyManager = (TelephonyManager)appcontext.getSystemService(Context.TELEPHONY_SERVICE);
		}
		
		String res = mTelephonyManager.getLine1Number();
	    
		if (res != null && res.length()>0){
			return res;
		}
		return "";
	}
	
	public static String getIMEI(){
		Context appcontext = (Context)AndApplication.getAppInstance();
		if (mTelephonyManager == null){
			mTelephonyManager = (TelephonyManager)appcontext.getSystemService(Context.TELEPHONY_SERVICE);
		}
		String res = mTelephonyManager.getDeviceId();
		if (res != null && res.length()>0){
			return res;
		}
		return "";
	}
	
    /** �ж��Ƿ�ģ����������TRUE����ǰ��ģ����
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (imei == null || imei.equals("000000000000000")) {
            return true;
        }
        return false;
    }
    
	public static String getChannelData(String key) {
		try {
			Context appcontext = (Context) AndApplication.getAppInstance();
			ApplicationInfo ai = appcontext.getPackageManager()
					.getApplicationInfo(appcontext.getPackageName(),
							PackageManager.GET_META_DATA);
			Object value = ai.metaData.get(key);
			if (value != null) {
				return value.toString();
			}
		} catch (Exception e) {
			//
		}
		return "-100";
	}

	public static String getAppVersionName() {
		try {
			Context appcontext = (Context) AndApplication.getAppInstance();
			PackageManager packageManager = appcontext.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					appcontext.getPackageName(), 0);
			return packInfo.versionName;
		} catch (Exception e) {
			//
		}
		return "";
	}
	
	public static String getAppPkgName() {
		try {
			Context appcontext = (Context) AndApplication.getAppInstance();
			PackageManager packageManager = appcontext.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					appcontext.getPackageName(), 0);
			return packInfo.packageName;
		} catch (Exception e) {
			//
		}
		return "";
	}
	
	public static String getUdid() {

		return DeviceId.getDeviceID(AndApplication.getAppInstance());
		
	}
	
	/* dp to px*/    
    public static int dip2px(Context context, float dpValue) {    
        final float scale = context.getResources().getDisplayMetrics().density;    
        return (int) (dpValue * scale + 0.5f);    
    }    
    
    /*px to dp*/    
    public static int px2dip(Context context, float pxValue) {    
        final float scale = context.getResources().getDisplayMetrics().density;    
        return (int) (pxValue / scale + 0.5f);    
    }   
    
    
	
}
