package com.ranger.bmaterials.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.OwnGameAction;

public class AppUtil {
    
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	private static final String TAG = "AppUtil";
	
	private static final String META_KEY_ACTION = "ActionName";
	private static final String META_KEY_HAS_ACCOUNT = "HasAccount";
	
	public static void writeToFile(String data, String filename)
	{
		if (data == null || data.length() == 0)
		{
			return ;
		}
		
		try
		{
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			{
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "duoku" + File.separator + filename + ".txt");
				FileWriter fw = new FileWriter(file);
				
				fw.write(data);
				fw.flush();
				fw.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
    
    public static List<InstalledAppInfo> loadAppInfoList(PackageManager packageManager, boolean hideSystemApps,boolean getmd5) {
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (apps == null) {
            apps = new ArrayList<ApplicationInfo>();
        }

        Set<String> loadLaunchablePackages = loadLaunchablePackages2(packageManager);
        // Create corresponding array of entries and load their labels.
        ArrayList<InstalledAppInfo> entries = new ArrayList<InstalledAppInfo>();
        InstalledAppInfo entry;
        for (ApplicationInfo applicationInfo : apps) {
            if (!hideSystemApps || !isSystemPackage(applicationInfo)) {
            	if(filterNonLauchable(loadLaunchablePackages, applicationInfo.packageName)){
            		PackageInfo packageInfo = loadPackageInfo(packageManager, applicationInfo.packageName);
                    entry = createAppInfoWithoutMD5(packageManager, applicationInfo,packageInfo);
                    entries.add(entry);
            	}else {
            		String metaData = getMetaData(packageManager, applicationInfo.packageName,META_KEY_ACTION);
            		if(metaData != null){
            			PackageInfo packageInfo = loadPackageInfo(packageManager, applicationInfo.packageName);
            			entry = createAppInfoWithoutMD5(packageManager, applicationInfo,packageInfo);
            			entry.setExtra(metaData);
            			boolean flag = false ;
            			String hasAccount = getMetaData(packageManager,applicationInfo.packageName, META_KEY_HAS_ACCOUNT);
            			if(hasAccount != null){
            				flag = (StringUtil.parseInt(hasAccount) == 1);
            			}
            			entry.setNeedLogin(flag);
            			entries.add(entry);
            		}
            		
            	}
            	
            }
        }
        return entries;
    }
    
    
/*    public static filterOwnApp(PackageManager pm,String packageName){
    	String metaData = getMetaData(pm, "ActionName");
		if(metaData != null){
			PackageInfo packageInfo = loadPackageInfo(pm, applicationInfo.packageName);
			entry = createAppInfo(packageManager, applicationInfo,packageInfo);
			entry.setExtra(metaData);
			boolean flag = false ;
			String hasAccount = getMetaData(packageManager, "HasAccount");
			if(hasAccount != null){
				flag = (StringUtil.parseInt(hasAccount) == 1);
			}
			entry.setNeedLogin(flag);
			entries.add(entry);
		}
    }**/
    
    
    private static boolean filterNonLauchable(Set<String> loadLaunchablePackages ,String packageName){
    	int size = loadLaunchablePackages.size();
    	if(loadLaunchablePackages.contains(packageName)){
    		return true ;
    	}
    	return false ;
    	/*for (int i = 0; i < size; i++) {
    		String string = loadLaunchablePackages.get(i);
    		if(string.equals(packageName)){
    			return true ;
    		}
		}
    	return false ;*/
    }
    
    public static List<String> loadLaunchablePackages(PackageManager manager){
    	Set<String> ret = loadLaunchablePackages2(manager);
    	ArrayList<String> arrayList = new ArrayList<String>(ret.size());
    	arrayList.addAll(ret);
        return arrayList ;
    }
    
    public static Set<String> loadLaunchablePackages2(PackageManager manager){
    	
    	Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    	
    	final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
    	//Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
    	final int count = apps.size();
    	Set<String> mApplications = new HashSet<String>();
    	
    	for (int i = 0; i < count; i++) {
    		mApplications.add(apps.get(i).activityInfo.packageName);
    		
    		/*application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);*/
    		//application.icon = info.activityInfo.loadIcon(manager);
    	}
    	return mApplications ;
    }
    
    public static InstalledAppInfo loadAppInfo(PackageManager mPm, String packageName) {
        ApplicationInfo applicationInfo = loadApplicationInfo(mPm, packageName);
        PackageInfo packageInfo = loadPackageInfo(mPm, packageName);
        InstalledAppInfo appInfo = null;
        if (applicationInfo != null) {
            appInfo = createAppInfo(mPm, applicationInfo,packageInfo);
            
            OwnGameAction action = tryLoadOwnGame(mPm, packageName);
            if(action != null){
            	appInfo.setExtra(action.action);
            	appInfo.setNeedLogin(action.hasAccount);
            }
            
        }
        return appInfo;
    }
    
    
    public static ApplicationInfo loadApplicationInfo(PackageManager mPm, String packageName) {
        try {
            return mPm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);            
        } catch (NameNotFoundException e) {
            return null;
        }
    }
    
    public static Drawable loadApplicationIcon(PackageManager mPm, String packageName) {
        ApplicationInfo applicationInfo = loadApplicationInfo(mPm, packageName);
        if (applicationInfo != null) {
            try{
                return applicationInfo.loadIcon(mPm);
            }catch(OutOfMemoryError ex){
                ex.printStackTrace();
                return null;
            }

        } else {
            return null;
        }
    }
    
    public static PackageInfo loadPackageInfo(PackageManager mPm, String packageName) {
        try {
            return mPm.getPackageInfo(packageName, 
                    PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS|PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            return null;
        }
    }
    
    public static boolean isFromGooglePlay(PackageManager mPm, String packageName) {
        String installPM = mPm.getInstallerPackageName(packageName);
        if ( installPM == null ) {
            // Definitely not from Google Play
            return false;
        } else if (installPM.equals("com.google.android.feedback") || installPM.equals("com.android.vending")) {
            // Installed from the Google Play
            return true;
        }
        return false;
    }
    
    public static boolean isRunning(Context context, String packageName) {
        boolean running = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);        
        List<RunningAppProcessInfo> procInfos = am.getRunningAppProcesses();
        for (RunningAppProcessInfo procInfo : procInfos) {
            if (procInfo.processName.equals(packageName)) {
                running = true;
                break;
            }
        }
        return running;
    }
    
//    /**
//     * 在安装数据库中没有action的情况下调用
//     * @param context
//     * @param packageName
//     */
//    public static void startActivity(Context context,String packageName) throws Exception{
//    	PackageManager pm = context.getPackageManager();
//    	Intent intent = null ;
//    	Exception ex ;
//    	Intent launcherIntent = getLauncherIntent(pm, packageName);
//    	intent = launcherIntent ;
//    	try {
//        	context.startActivity(intent);
//        	return ;
//		} catch (Exception e) {
//			ex = e ;
//		}
//		OwnGameAction ownGameActionMode = tryLoadOwnGame(pm,packageName);
//		if(ownGameActionMode != null){
//			Intent ownIntent = getOwnIntent(context, packageName, ownGameActionMode.action);
//			intent = ownIntent ;
//		}
//    	try {
//        	if(intent != null)context.startActivity(intent);
//		} catch (Exception e) {
//			ex = e ;
//		}
//    	throw new RuntimeException(ex);
//    	
//    }


	public static OwnGameAction tryLoadOwnGame( PackageManager pm,String packageName) {
		String action = getMetaData(pm, packageName,META_KEY_ACTION);
		if(action != null){
			boolean hasAccount = false ;
			String hasAccountStr = getMetaData(pm,packageName, META_KEY_HAS_ACCOUNT);
			if(hasAccountStr != null){
				hasAccount = (StringUtil.parseInt(hasAccountStr) == 1);
			}
			OwnGameAction ownGameAction = new OwnGameAction();
			ownGameAction.action = action ;
			ownGameAction.hasAccount = hasAccount ;
			return ownGameAction ;
		}
		return null;
	}
    

    
    
    public static Intent getLauncherIntent(PackageManager pm, String packageName) {
    	/*PackageInfo packageInfo = null;
		try {
			packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        Intent intent = null;
        intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);*/
        return getIntent(packageName, pm);
    }
    
    public static Intent getOwnIntent(Context context ,String packageName,String action){
		Intent intent = new Intent(action);
		//intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setPackage(packageName);
		return intent ;
    }
/*    public static Intent getApplicationIntent(PackageManager pm, String packageName) {
    	PackageInfo packageInfo = null;
    	try {
    		packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
    	} catch (NameNotFoundException e) {
    		e.printStackTrace();
    	}
    	Intent intent = null;
    	intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
    	if (intent != null) {
    		intent = intent.cloneFilter();
    		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
    		return intent;
    	}
    	if (packageInfo.activities.length == 1) {
    		intent = new Intent(Intent.ACTION_MAIN);
    		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
    		intent.setClassName(packageInfo.packageName, packageInfo.activities[0].name);
    		return intent;
    	}
    	intent = getIntent(packageInfo.packageName, pm);
    	if (intent != null) {
    		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
    		return intent;
    	}
    	return null;
    }
*/    
    public static Intent getIntent(String packageName, PackageManager pm) {
    	Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);

        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveInfo = null ;
        if(apps != null && apps.size() == 1){
        	resolveInfo = apps.get(0);
        }else{
        	return null ;
        }
    	ComponentName componentName = new ComponentName(
    			packageName,
    			resolveInfo.activityInfo.name);
    	
    	
    	Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        //intent.setPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent ;
        
        /*List<ResolveInfo> list = getRunableList(pm, false);
        for (ResolveInfo info : list) {
            if (packageName.equals(info.activityInfo.packageName)) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setClassName(packageName, info.activityInfo.name);
                return i;
            }
        }*/
    }
    
    private static synchronized List<ResolveInfo> getRunableList(PackageManager pm, boolean reload) {
        Intent baseIntent = new Intent(Intent.ACTION_MAIN);
        baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(baseIntent, 0);
    }
    
    private static boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM);
    }
    
    public static Boolean isSystemPackage(PackageManager manager,String pkgName) {
    	ApplicationInfo info = loadApplicationInfo(manager, pkgName);
    	if(info != null){
    		return isSystemPackage(info);
    	}
    	return null ;
    }
    
    @SuppressLint("NewApi")
	private static InstalledAppInfo createAppInfo(PackageManager mPm, ApplicationInfo applicationInfo,PackageInfo packageInfo) {
    	InstalledAppInfo entry = new InstalledAppInfo();
        entry.setPackageName(applicationInfo.packageName);
        entry.setUid(applicationInfo.uid);
        String name = applicationInfo.name;
        String name2 = applicationInfo.loadLabel(mPm).toString();
        entry.setName((TextUtils.isEmpty(name)?name2:name));
        
        entry.setVersionInt(packageInfo.versionCode);
        entry.setVersion(packageInfo.versionName);
        entry.setVersionInt(packageInfo.versionCode);
        setInstalledApkDateAndSize(packageInfo, entry);
        //entry.setExtra(parseSignature(packageInfo.signatures));
       
        
        String signMd5 = getSignMd5(packageInfo);
        //String parseSignature = parseSignature(packageInfo.signatures);
        
        
		
		String sourceDir = applicationInfo.sourceDir;
		String publicSourceDir = applicationInfo.publicSourceDir;
		if(publicSourceDir != null && publicSourceDir.equals(sourceDir)){
			entry.setFileMd5(FileHelper.getFileMd5(publicSourceDir));
		}else{
			entry.setFileMd5(FileHelper.getFileMd5(sourceDir));
		}
        entry.setSign(signMd5);
        //Log.i(TAG, "signMd5:"+signMd5+" parseSignature:"+parseSignature);
        
        entry.setDrawable(loadApplicationIcon(mPm, applicationInfo.packageName));
        return entry;
    }
    
    @SuppressLint("NewApi")
	private static InstalledAppInfo createAppInfoWithoutMD5(PackageManager mPm, ApplicationInfo applicationInfo,PackageInfo packageInfo) {
    	InstalledAppInfo entry = new InstalledAppInfo();
        entry.setPackageName(applicationInfo.packageName);
        entry.setUid(applicationInfo.uid);
        String name = applicationInfo.name;
        String name2 = applicationInfo.loadLabel(mPm).toString();
        entry.setName((TextUtils.isEmpty(name)?name2:name));
        
        entry.setVersionInt(packageInfo.versionCode);
        entry.setVersion(packageInfo.versionName);
        entry.setVersionInt(packageInfo.versionCode);
        setInstalledApkDateAndSize(packageInfo, entry);
        //entry.setExtra(parseSignature(packageInfo.signatures));
       
        
//        String signMd5 = getSignMd5(packageInfo);
        //String parseSignature = parseSignature(packageInfo.signatures);
        
        
		
		String sourceDir = applicationInfo.sourceDir;
		String publicSourceDir = applicationInfo.publicSourceDir;
//		if(publicSourceDir != null && publicSourceDir.equals(sourceDir)){
//			entry.setFileMd5(FileHelper.getFileMd5(publicSourceDir));
//		}else{
//			entry.setFileMd5(FileHelper.getFileMd5(sourceDir));
//		}
//        entry.setSign(signMd5);
        //Log.i(TAG, "signMd5:"+signMd5+" parseSignature:"+parseSignature);
        
        entry.setDrawable(loadApplicationIcon(mPm, applicationInfo.packageName));
        return entry;
    }
    
    @SuppressLint("NewApi")
	private static void setInstalledApkDateAndSize(PackageInfo packageInfo, InstalledAppInfo item) {
        String dir = packageInfo.applicationInfo.publicSourceDir;
        //item.setApksize(Formatter.formatFileSize(context, size));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        	item.setDate(packageInfo.lastUpdateTime);
        }else{ 
        	try {
            	long date = new File(dir).lastModified();
            	item.setDate(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        try {
    		long size = new File(dir).length();
        	item.setSize(size);
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }
    
    public static String getMD5(byte[] plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText);
            byte[] b = md.digest();

            return toHexString(b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 16进制数组 */
    static final char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a',
            'b',
            'c', 'd', 'e', 'f' };
    
    /**
     * 将字节数组转换为16进制字符串
     * 
     * @param byt
     *            要转换的字节
     * @return 字符串
     */
    public static String toHexString(byte[] byt) {
        StringBuilder sb = new StringBuilder(byt.length * 2);
        for (int i = 0; i < byt.length; i++) {
            sb.append(HEXCHAR[(byt[i] & 0xf0) >>> 4]);// SUPPRESS CHECKSTYLE :
                                                      // magic number
            sb.append(HEXCHAR[byt[i] & 0x0f]);// SUPPRESS CHECKSTYLE : magic
                                              // number
        }
        return sb.toString();
    }
    
	public static String getSignMd5(PackageInfo packageinfo) {
		String charsString = packageinfo.signatures[0].toCharsString();
		byte[] bytes = charsString
				.getBytes();
		return getMD5(bytes);
	}
    
    private static  String parseSignature(Signature[] signatures) {
    	 Signature sign = signatures[0];
    	 byte[] signature =  sign.toByteArray();
		try {
			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(signature));
			PublicKey publicKey = cert.getPublicKey();
			String pubKey = publicKey.toString();
			String signNumber = cert.getSerialNumber().toString();
			
			//pubKey = pubKey.substring(pubKey.indexOf("modulus: ") + 9,  
			//		pubKey.indexOf("\n", pubKey.indexOf("modulus:")));  

			return pubKey ;
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null ;
	}

    
    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
    
    public static String getMetaData(PackageManager pm,String packageName,String metaDataName) {
		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			String metaDataValue = null ;
			if (info.metaData != null ) {
				Log.i(TAG,"packageName "+packageName+" meta:"+ info.metaData.keySet());
				if(info.metaData.containsKey(metaDataName)){
					Object value = info.metaData.get(metaDataName);
					return value.toString();
				}
				
			}
			return metaDataValue;
		}
		catch (NameNotFoundException e) {
			return null;
		}
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
    
    public static ArrayList<String> getInstalledPackages(PackageManager pm){
    	ArrayList<String> list = new ArrayList<String>();
    	List< PackageInfo> pinfo = pm.getInstalledPackages(0);
    	if(pinfo != null){
    		for(int i = 0; i < pinfo.size(); i++){
    			String packName = pinfo.get(i).packageName;
    			list.add(packName);
    		}
    	}
    	
    	return list;
    }
    
    private static final String gameSearch = "com.duoku.gamesearch";
    
	public static boolean isGameSearchForeground() {
		ActivityManager activityManager = (ActivityManager) GameTingApplication.getAppInstance()
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(gameSearch)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
	
	public static String getApkSignMd5(String apkPath){
		String signMd5 = null ;
		PackageInfo pack = ApkUtil.getPackageForFile(apkPath,GameTingApplication.getAppInstance());
		if(pack != null){
			signMd5 = AppUtil.getSignMd5(pack);
		}
		return signMd5 ;
	}
	
	public static String getAppVersion(Context context) {
		String ver_name = null;
		try {
			ver_name = context.getPackageManager().getPackageInfo(
					context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ver_name;
	}
	
/*	public static String getInstalledApkSignMd5(String packageName){
		PackageInfo info = AppUtil.loadPackageInfo(GameTingApplication.getAppInstance().getPackageManager(), packageName);
		if(info != null){
			String signMd5 = AppUtil.getSignMd5(info);
			return signMd5 ;
		}
		return null ;
	}*/
}
