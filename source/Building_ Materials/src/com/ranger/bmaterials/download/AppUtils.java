/**
 * Copyright (c) 2011 Baidu Inc.
 * 
 * @author 		Qingbiao Liu <liuqingbiao@baidu.com>
 * 
 * @date 2011-9-7
 */
package com.ranger.bmaterials.download;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import android.text.TextUtils;
import android.util.Log;

/**
 */

public final class AppUtils {
	private static final String TAG = "AppUtils";
	public static boolean isAPK(String filename) {
		boolean relt = false;

		if (TextUtils.isEmpty(filename) || !(new File(filename).exists())) {
			return false;
		}
		try {
			ZipFile zipfile = new ZipFile(filename);
			if (zipfile.getEntry("AndroidManifest.xml") != null) {
				relt = true;
			}
			zipfile.close();
		} catch (IOException e) {
			relt = false;
		}

		return relt;
	}
}
