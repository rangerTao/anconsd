package com.andconsd.utils;

import com.andconsd.entry.DkKeyString;
import com.andconsd.entry.DkValueString;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private static SharePreferenceUtil mInstance;
	private final int MODE = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
	private final SharedPreferences sharedpreferences;
	private static final String FILE_NAME = "andconsd_shared_preferences";
	
	public static final String SP_CACHE_DIR = "andconsd_cache_dir";

	/** 定义SharedPreference key的常量值 */

	private SharePreferenceUtil(Context context, String fileName) {
		sharedpreferences = context.getSharedPreferences(fileName, MODE);
	}

	public static SharePreferenceUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SharePreferenceUtil(context, FILE_NAME);
		}

		return mInstance;
	}

	public boolean saveString(DkKeyString key, DkValueString dkValue) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		String outValue = dkValue.getValue();

		editor.putString(key.getKey(), outValue);
		return editor.commit();
	}

	public boolean saveString(String key, String value) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString(key, value);

		return editor.commit();
	}

	public boolean saveLong(String key, Long value) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putLong(key, value);

		return editor.commit();
	}

	public String getString(DkKeyString key) {
		String value = null;
		value = sharedpreferences.getString(key.getKey(), "");
		return value;
	}

	public String getString(String key) {

		return sharedpreferences.getString(key, "");
	}

	public Long getLong(String key) {
		return sharedpreferences.getLong(key, 0);
	}

	public boolean saveBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public boolean getBoolean(String key) {

		return sharedpreferences.getBoolean(key, false);
	}

	public boolean removeKey(String key) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.remove(key);
		return editor.commit();
	}

	public boolean removeAllKey() {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.clear();
		return editor.commit();
	}

	/**
	 * 将key值映射的value置为空串
	 * 
	 * @param key
	 * @return
	 */
	public boolean clearString(String key) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString(key, "");
		return editor.commit();
	}

	/**
	 * 将key值映射的value置为false
	 * 
	 * @param key
	 * @return
	 */
	public boolean clearBoolean(String key) {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putBoolean(key, false);
		return editor.commit();
	}
}
