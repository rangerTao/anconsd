package com.andconsd.framework.utils;

import java.util.Iterator;
import java.util.List;

import com.andconsd.AndApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class AppUtil {

	private static final String kkgame = "com.duoku.kkgame";
	
	public static boolean isInstalled(Context context, String pkg)
	{
	    PackageInfo info = null;
	    
	    if (null != context && null != pkg)
	    {
	        try
            {
                info = context.getApplicationContext().getPackageManager().getPackageInfo(pkg, 0);
            }
            catch (NameNotFoundException e)
            {
                info = null;
                if(Constants.DEBUG)
                	Log.e("TAG", "App not installed " + pkg);
            }
	    }
	    
	    return null != info;
	}
	
	public static void browseWithDefaultBrowser(Context context, String url) {

		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(content_url);
		intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
		// 浏览器信息
		String str = "";
		Intent intenta = new Intent("android.intent.action.VIEW", Uri.parse("http://api.m.duoku.com"));
		ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
		Iterator<ResolveInfo> iterator = null;
		if ((resolveInfo != null) && (resolveInfo.activityInfo != null) && (resolveInfo.activityInfo.packageName.equals("android"))
				&& (resolveInfo.activityInfo.name.equals("com.android.internal.app.ResolverActivity")))
			iterator = context.getPackageManager().queryIntentActivities(intenta, PackageManager.MATCH_DEFAULT_ONLY).iterator();
		if (iterator != null) {
			while (iterator.hasNext()) {
				ResolveInfo ri = (ResolveInfo) iterator.next();
				intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
				str = ri.activityInfo.packageName + "/" + ri.activityInfo.name;
				if ((0x1 & ri.activityInfo.applicationInfo.flags) == 1) {
					break;
				}
			}
		}

		context.startActivity(intent);
	}

	public static boolean iskkgameForeground() {
		ActivityManager activityManager = (ActivityManager) AndApplication
				.getAppInstance().getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(kkgame)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	public static String getAppVersion(Context context) {
		String ver_name = null;
		try {
			ver_name = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ver_name;
	}

	/**
	 * 读manifest中meta-data属性的channID
	 * @param context
	 * @return
	 */
	public static String getAppChannelId(Context context) {
		String channelId = null;
		try {  
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),  
                            PackageManager.GET_META_DATA);
            Object object = appInfo.metaData.get("BaiduMobAd_CHANNEL");
            if (object != null && object instanceof Integer) {
				channelId = object.toString();
			}
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }
		return channelId;
	}	
	
	public static void createShortCut(Context cx) {
		if (!hasShortcut(cx, cx.getString(cx.getApplicationInfo().labelRes))) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(0x10200000);
			String clsName = cx.getClass().getName();

			intent.setComponent(new ComponentName(cx.getPackageName(), clsName));

			Intent shortcutIntent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			ApplicationInfo ai = cx.getApplicationInfo();
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					cx.getString(ai.labelRes));
			shortcutIntent.putExtra("duplicate", false);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(cx, ai.icon));
			shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
			
			cx.sendBroadcast(shortcutIntent);
		}
	}

	private static boolean hasShortcut(Context cx, String title) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = cx.getContentResolver();
		String AUTHORITY = getAuthorityFromPermission(cx,
				"com.android.launcher.permission.INSTALL_SHORTCUT");

		if (AUTHORITY == null)
			if (Build.VERSION.SDK_INT < 8) {
				AUTHORITY = "com.android.launcher.settings";
			} else {
				AUTHORITY = "com.android.launcher2.settings";
			}
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { title }, null);

		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
			c.close();
		}
		return isInstallShortcut;
	}

	private static String getAuthorityFromPermission(Context context,
			String permission) {
		if (permission == null)
			return null;
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packs != null) {
			for (PackageInfo pack : packs) {
				ProviderInfo[] providers = pack.providers;
				if (providers != null) {
					for (ProviderInfo provider : providers) {
						if (permission.equals(provider.readPermission))
							return provider.authority;
						if (permission.equals(provider.writePermission))
							return provider.authority;
					}
				}
			}
		}
		return null;
	}

}
