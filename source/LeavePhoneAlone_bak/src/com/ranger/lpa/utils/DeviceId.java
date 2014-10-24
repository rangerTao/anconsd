package com.ranger.lpa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by taoliang on 14-8-11.
 */
public class DeviceId {
    private static final String TAG = "DeviceId";
    private static final String SHARED_NAME = "bids";
    private static final String KEY_IMEI = "i";
    private static final String KEY_ANDROID_ID = "a";
    private static String mDeviceId = null;

    private DeviceId() {
    }

    public static String getDeviceID(Context context) {

        if (mDeviceId == null) {

            SharedPreferences sharedpreferences = context.getSharedPreferences("bids", 0);
            String s = sharedpreferences.getString("i", null);

            if (s == null) {
                s = getIMEI(context);
                android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("i", s);
                editor.commit();
            }
            String s1 = sharedpreferences.getString("a", null);
            if (s1 == null) {
                s1 = getAndroidId(context);
                android.content.SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString("a", s1);
                editor1.commit();
            }

            mDeviceId = Md5Tools.toMd5((new StringBuilder()).append("com.duoku").append(s).append(s1).toString().getBytes(), true);
        }

        return mDeviceId;
    }

    /**
     * For debug only
     *
     * @param context
     * @return
     */
    public static String getDebugDeviceID(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("bids", 0);
        String s = sharedpreferences.getString("i", null);

        if (s == null) {
            s = getIMEI(context);
            android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("i", s);
            editor.commit();
        }
        String s1 = sharedpreferences.getString("a", null);
        if (s1 == null) {
            s1 = getAndroidId(context);
            android.content.SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString("a", s1);
            editor1.commit();
        }

        mDeviceId = Md5Tools.toMd5((new StringBuilder()).append(System.currentTimeMillis()).append(s).append(s1).toString().getBytes(), true);

        return mDeviceId;
    }

    private static String getIMEI(Context context) {
        String s = "";
        TelephonyManager telephonymanager = (TelephonyManager) context.getSystemService("phone");
        if (telephonymanager != null) {
            s = telephonymanager.getDeviceId();
            if (TextUtils.isEmpty(s))
                s = "";
        }
        return s;
    }

    private static String getAndroidId(Context context) {
        String s = "";
        s = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (TextUtils.isEmpty(s))
            s = "";
        return s;
    }

}
