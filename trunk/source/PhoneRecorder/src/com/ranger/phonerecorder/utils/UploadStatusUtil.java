package com.ranger.phonerecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;

public class UploadStatusUtil {

	/**
	 * 检查某文件是否已经上传过
	 * @param context
	 * @param path
	 * @return
	 */
	public static boolean checkFileUpload(Context context, String path) {
		SharedPreferences sp = context.getSharedPreferences("upload", Context.MODE_WORLD_READABLE);
		boolean isUpload = sp.getBoolean(path, false);
		return isUpload;
	}
	
	/**
	 * 设置某文件状态为已上传
	 * @param context
	 * @param path
	 */
	public static void setFileUpload(Context context,String path){
		SharedPreferences sp = context.getSharedPreferences("upload", Context.MODE_WORLD_READABLE);
		Editor editor = sp.edit();
		editor.putBoolean(path, true);
		editor.commit();
	}
}
