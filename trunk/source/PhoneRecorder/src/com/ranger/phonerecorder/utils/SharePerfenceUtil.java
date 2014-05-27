package com.ranger.phonerecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.ranger.phonerecorder.pojos.EmailServer;

/**
 * Used to operate the SharedPreferences.
 * @author taoliang
 *
 */
public class SharePerfenceUtil {

	private static final String SP_BASE_INFO = "base_info";
	private static final String SP_FILE_INFO = "file_info";
	private static final String SP_BASE_INFO_EMAIL = "email_server";
	
	/**
	 * 
	 * @param context
	 * @param info
	 */
	public static void saveEmaiServerInfo(Context context,String info){
		
		SharedPreferences sp = context.getSharedPreferences(SP_BASE_INFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(SP_BASE_INFO_EMAIL, info);
		editor.commit();
	}
	
	/**
	 * 
	 * @param context
	 * @param info
	 * @return
	 */
	public static EmailServer getEmailServerInfo(Context context){
		SharedPreferences sp = context.getSharedPreferences(SP_BASE_INFO, Context.MODE_PRIVATE);
		String server_info = sp.getString(SP_BASE_INFO_EMAIL, "");
		if(!server_info.equals("")){
			Gson gson = new Gson();
			EmailServer es = gson.fromJson(server_info, EmailServer.class);
			if(es.isValid()){
				return es;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param context
	 * @param path
	 * @param interval
	 */
	public static void saveRecordFileInterval(Context context,String path,int interval){
		SharedPreferences sp = context.getSharedPreferences(SP_FILE_INFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt( path, interval);
		editor.commit();
	}
}
