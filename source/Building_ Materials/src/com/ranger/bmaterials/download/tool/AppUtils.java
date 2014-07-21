/**
 * Copyright (c) 2011 Baidu Inc.
 * 
 * @author 		Qingbiao Liu <liuqingbiao@baidu.com>
 * 
 * @date 2011-9-7
 */
package com.ranger.bmaterials.download.tool;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

/**
 * ���������ļ���
 */

public final class AppUtils {
	private static final String TAG = "AppUtils";

	/**
	 * ͨ�����APk�ļ����ȡAndroidManifest.xml�����ж��Ƿ������APK�ļ�������ҵ�����Ϊ����ģ�������Ϊ�Ǵ���ġ�
	 * 
	 * @param filename
	 *            �ļ�����
	 * @return true��ʾ��,false ��ʾ����
	 */
	public static boolean isAPK2(String filename) {
		boolean relt = false;

		if (TextUtils.isEmpty(filename) || !(new File(filename).exists())) {
			Log.e(TAG, "apk�ļ��Ҳ���");
			return false;
		}
		try {
			// ʹ��ZipFile�ж����صİ����Ƿ��Manifest�ļ�
			ZipFile zipfile = new ZipFile(filename);
			if (zipfile.getEntry("AndroidManifest.xml") != null) {
				relt = true;
			}
			zipfile.close();
		} catch (IOException e) {
			Log.e(TAG, "����APK����:" + e.getMessage());
			relt = false;
		}

		return relt;
	}
	
    /**
     * 获取packageName 关联的PacakgeInfo
     * 
     * @param context
     *            Context
     * @param packageName
     *            应用包名
     * @return PackageInfo
     */
    public static PackageInfo getPacakgeInfo(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
                return pi;
        } catch (NameNotFoundException e) {

            return null;
        }
    }
}
