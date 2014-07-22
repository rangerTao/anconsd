package com.ranger.bmaterials.tools;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.ranger.bmaterials.app.GameTingApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class DeviceUtil {
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= 9 ;//Build.VERSION_CODES.GINGERBREAD;
	}

	public static long getMaxMemory() {
		Context context = GameTingApplication.getAppInstance();
		int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass(); 
		long maxMemory = Runtime.getRuntime().maxMemory();
		String size1 = Formatter.formatFileSize(context, 1024 * 1024 * memoryClass);
		String size2 = Formatter.formatFileSize(context, maxMemory);
		//Log.i("DeviceUtil", "maxMemory()"+size2+";getMemoryClass():"+size1);
		return maxMemory ;
	}
	
	@SuppressLint("NewApi")
	private static long getUsableSpace(File path) {
		try {
			long ret = -1;
			if (hasGingerbread()) {
				return path.getUsableSpace();
			}
			final StatFs stats = new StatFs(path.getPath());
			ret = (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
			return ret;
		} catch (Exception e) {
			return -1 ;
		}
		
	}
    

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static  void copyText(Context context ,String plainText) {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				clipboard.setText(plainText);
			}
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				android.content.ClipData clip = android.content.ClipData
						.newPlainText("text", plainText);
				clipboard.setPrimaryClip(clip);
			}
		}
	}


	@SuppressLint("NewApi")
	public  static int[] getScreensize(Context ctx) {
		int wh[] = new int[2];
		WindowManager manager = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			manager.getDefaultDisplay().getSize(size);
			wh[0] = size.x;
			wh[1] = size.y;
		} else {
			Display d = manager.getDefaultDisplay();
			wh[0] = d.getWidth();
			wh[1] = d.getHeight();
		}
		return wh;
	}
	@SuppressLint("NewApi")
	public  static float getScreenDensity(Context ctx) {
		return ctx.getResources().getDisplayMetrics().scaledDensity;
	}

	/**
	 * 设备品牌（制造商）
	 * @return
	 */
	public static String getPhoneBrand(){
		 return android.os.Build.BRAND ;
	}
	/**
	 * 设备型号
	 * @return
	 */
	public static String getPhoneModel(){
		return android.os.Build.MODEL;
	}
	
	/**
	 *  Unreliable for CDMA phones
	 * @param context
	 * @return
	 */
	public static String getNetworkOperator(Context context) {
		String ret = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (null != telephonyManager)
			ret = telephonyManager.getNetworkOperatorName();
		
		/*int simState = telephonyManager.getSimState();
		if(TelephonyManager.SIM_STATE_READY == simState){
			String simOperator = telephonyManager.getSimOperator();
			telephonyManager.getSimCountryIso()
		}*/
		return ret;
	}
	
	/**
	 * context.getPackageManager().checkPermission(paramString, context.getPackageName()) == 0
	 * android.permission.READ_PHONE_STATE
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService("phone");
		StringBuffer ImsiStr = new StringBuffer();
		try {
			ImsiStr.append(tm.getSubscriberId() == null ? "" : tm
					.getSubscriberId());
			while (ImsiStr.length() < 15)
				ImsiStr.append("0");
		} catch (Exception e) {
			ImsiStr.append("000000000000000");
			e.printStackTrace();
		}
		return ImsiStr.toString();
	}
	/**
	 * context.getPackageManager().checkPermission(paramString, context.getPackageName()) == 0
	 * android.permission.READ_PHONE_STATE
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService("phone");
		StringBuffer tmDevice = new StringBuffer();
		try {
			tmDevice.append(tm.getDeviceId());
			while (tmDevice.length() < 15)
				tmDevice.append("0");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tmDevice.toString().replace("null", "0000");
	}
	
	
	// 设备id
	public static String getAndroidId(Context ctx) {
		String str = null;
		try {
			str = Settings.Secure.getString(ctx.getContentResolver(),
					"android_id");
		} catch (Exception e1) {
		}
		if (str == null) {
			try {
				str = Settings.System.getString(ctx.getContentResolver(),
						"android_id");
			} catch (Exception e2) {
			}
		}
		if (str == null) {
			TelephonyManager tm = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			str = tm.getDeviceId();
		}
		return str;
	}
	//MAC Address
	public static String getMACAddress(Context ctx) {
		try {
			WifiManager wifiMan = (WifiManager) ctx.getSystemService("wifi");
			String mac = wifiMan.getConnectionInfo().getMacAddress();
			/*
			 * if ((mac == null) || (mac.equals(""))) { return null; } String[]
			 * macParts = mac.split(":"); byte[] macAddress = new byte[6]; for
			 * (int i = 0; i < macParts.length; ++i) { Integer hex = Integer
			 * .valueOf(Integer.parseInt(macParts[i], 16)); macAddress[i] =
			 * hex.byteValue(); } return macAddress;
			 */
			return mac;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static String getOsVersion(Context ctx) {
		return android.os.Build.VERSION.RELEASE+"_"+android.os.Build.VERSION.SDK_INT ;
	}
	
	public static String getTimeZone(Context ctx) {
		Configuration configuration = ctx.getResources().getConfiguration();
		Calendar calendar = Calendar.getInstance(configuration.locale);
		TimeZone timeZone = calendar.getTimeZone();
		if(timeZone == null){
			timeZone = TimeZone.getDefault();
		}
		String name = timeZone.getID()+"/"+timeZone.getDisplayName();
		return name;
		
	}
	
	public static int getTimeZoneId(Context ctx) {
		Configuration configuration = ctx.getResources().getConfiguration();
		Calendar calendar = Calendar.getInstance(configuration.locale);
		TimeZone timeZone = calendar.getTimeZone();
		if(timeZone == null){
			timeZone = TimeZone.getDefault();
		}
		String id = timeZone.getID();
		return timeZone.getRawOffset() / (60 * 60 * 1000);
		
	}
	public static String getLanguage(Context ctx) {
		Configuration configuration = ctx.getResources().getConfiguration();
		String language = configuration.locale.getLanguage();
		if(language == null){
			language = Locale.getDefault().getDisplayLanguage();
		}
		return language ;
	}
	public static String getAppName(Context ctx) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return  packageInfo.packageName ;
	}
	
	
	
	public static boolean isNetworkAvailable(Context context) {
//		boolean ret = false;
//		ConnectivityManager conMgr = (ConnectivityManager) context
//				.getApplicationContext().getSystemService(
//						Context.CONNECTIVITY_SERVICE);
//		if (conMgr != null) {
//			NetworkInfo i = conMgr.getActiveNetworkInfo();
//			if (i != null && i.isConnected() && i.isAvailable()) {
//				ret = true;
//			}
//		}
		return checkConnection(context);
		//return ret;

	}
	 private static boolean checkConnection(Context context) {
	        final ConnectivityManager cm =
	                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
	        	//Toast.makeText(context, R.string.no_network_connection_toast, Toast.LENGTH_LONG).show();
	            //Log.e(TAG, "checkConnection - no connection found");
	        	return false ;
	        }
	        
	        NetworkInfo[] allNetworkInfo = cm.getAllNetworkInfo();
	        return true ;
	    }
	
//	 switch (networkType) {
//     case ConnectivityManager.TYPE_MOBILE:
//         return DownloadManager.Request.NETWORK_MOBILE;
//     case ConnectivityManager.TYPE_WIFI:
//         return DownloadManager.Request.NETWORK_WIFI;
//
//     default:
//         return 0;
	 
	
    public static Integer getActiveNetworkType(Context context) {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }
        
        NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
        //NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            return activeNetworkInfo.getType();
        }
        return null;
    }


}
