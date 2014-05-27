package com.andconsd.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.andconsd.ui.Androsd;

public class AndconsdUtils {

	/**
	 * Delete a file
	 */
	public static boolean deleteFileByName(String filepath) {

		try {
			if (!filepath.equals("")) {
				File file = new File(filepath);
				if (file.exists()) {
					file.delete();
				}else {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Execute a command as root.
	 * 
	 * @param command
	 * @return
	 */
	public static boolean execAsRoot(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				// nothing
			}
		}
		return true;
	}

	public static Bitmap getDrawable(String path, int level) {
		if (path == null || path.length() < 1)
			return null;
		File file = new File(path);
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();

		if (level == 1) {
			if (file.length() < 131072) { // 0-1m
				opts.inSampleSize = 1;
			} else if (file.length() < 262144) { // 1-2m
				opts.inSampleSize = 1;
			} else if (file.length() < 524288) { // 2-4m
				opts.inSampleSize = 1;
			} else if (file.length() < 1048576) { // 4-8m
				opts.inSampleSize = 2;
			} else {
				opts.inSampleSize = 4;
			}
		} else if (level == 2) {
			if (file.length() < 131072) { // 0-1m
				opts.inSampleSize = 1;
			} else if (file.length() < 262144) { // 1-2m
				opts.inSampleSize = 2;
			} else if (file.length() < 524288) { // 2-4m
				opts.inSampleSize = 8;
			} else if (file.length() < 1048576) { // 4-8m
				opts.inSampleSize = 16;
			} else {
				opts.inSampleSize = 32;
			}
		}

		try {
			InputStream is = new DataInputStream(new FileInputStream(file));

			// resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
			resizeBmp = BitmapFactory.decodeStream(is, null, opts);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
		}

		return resizeBmp;
	}

	/**
	 * Query the package manager for MAIN/LAUNCHER activities in the supplied
	 * package.
	 */
	public static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packageName);

		final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
		return apps != null ? apps : new ArrayList<ResolveInfo>();
	}
}
