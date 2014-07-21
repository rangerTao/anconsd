package com.ranger.bmaterials.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.tools.install.SilentInstallService;

public class ApkUtil {
    private static final String TAG = "ApkUtil";

	/**
     * 通过解析APk文件包，获取AndroidManifest.xml，来判断是否是正常的APK文件。如果找到则认为是正常的，否则认为是错误的。
     * 
     * @param filename
     *            文件名字
     * @return true表示正常,false 表示不正常。
     */
    public static boolean isAPK(String filename) {
        boolean retVal = false;
        if (TextUtils.isEmpty(filename) || !(new File(filename).exists())) {
            return false;
        }
        ZipFile zipfile = null ;
        try {
            // 使用ZipFile判断下载的包里是否包含Manifest文件
            zipfile = new ZipFile(filename);
            if (zipfile.getEntry("AndroidManifest.xml") != null) {
                retVal = true;
            }

        } catch (IOException e) {
        	android.util.Log.e(TAG, "Parse file failure!",e);
            retVal = false;
        }finally{
        	try {
        		 zipfile.close();
			} catch (Exception ex) {
			}
        }

        return retVal;
    }
    
    
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * 
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
    /**
     * 检查一个APK文件是否是可用的APK。
     * @param path
     *            apk file path
     * @param context
     *            context
     * @return true文件有效，false文件无效
     */
    public static boolean isAPKFileValid(String path, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, 0);

        return pi != null;
    }
    
    public static String getPackageNameForFile(String path, Context context) {
    	try {
    		PackageManager pm = context.getPackageManager();
        	PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
        	return pi.packageName;
		} catch (Exception e) {
		}
    	return null ;
    	
    }
    
    public static PackageInfo getPackageForFile(String path, Context context) {
    	try {
    		PackageManager pm = context.getPackageManager();
    		
    		PackageInfo pi;
    		
    		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
    			pi = pm.getPackageArchiveInfo(path, /*PackageManager.GET_PERMISSIONS|*/PackageManager.GET_ACTIVITIES);
    		}else{
    			pi = pm.getPackageArchiveInfo(path, /*PackageManager.GET_PERMISSIONS|*/PackageManager.GET_SIGNATURES);
    		}
        	
        	return pi ;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null ;
    	
    }
    
	public static String getApkSignatureByFilePath(Context context, String apkFile) {
		PackageInfo newInfo = getPackageArchiveInfo(apkFile,
				/*PackageManager.GET_ACTIVITIES |*/ PackageManager.GET_SIGNATURES);
		if (newInfo != null) {
			if (newInfo.signatures != null && newInfo.signatures.length > 0) {
				return newInfo.signatures[0].toString();
			}
		}
		return null;
	}
	
	  private String getSignatures(String archiveFilePath) {
	        //必须使用反射机制才能获取未安装apk的签名信息
	        final String PACKAGEPARSER_CLASS_NAME = "android.content.pm.PackageParser";
	        final String PARSEPACKAGE_METHOD_NAME = "parsePackage";
	        final String SUBCLASS_PACKAGE_CLASS_NAME = "Package";
	        final String COLLECTCERTIFICATES_METHOD_NAME = "collectCertificates";
	        final String GENERATEPACKAGEINFO_METHOD_NAME = "generatePackageInfo";
	        try {
	            //根据反射获取隐藏类
	            Class<?> parserClass = Class.forName(PACKAGEPARSER_CLASS_NAME);

	            //获取类的构造方法
	            Constructor<?> cons = parserClass.getConstructor(String.class);

	            //调用构造方法，构造类实例
	            Object packageParser = cons.newInstance(archiveFilePath);
	            
	            //获取名为"parsePackage"的方法
	            Method parseMethod = parserClass.getMethod(PARSEPACKAGE_METHOD_NAME, File.class, String.class, DisplayMetrics.class, int.class);

	            //遍历类的子类集，找到Package内部类
	            Class<?>[] subClasses = parserClass.getClasses();
	            Class<?> packageClass = null;
	            for(int i=0; i<subClasses.length; i++) {
	                if(subClasses[i].getName().equals(PACKAGEPARSER_CLASS_NAME+"$"+SUBCLASS_PACKAGE_CLASS_NAME)) {
	                    packageClass = subClasses[i];
	                }
	            }
	            
	            DisplayMetrics metrics = new DisplayMetrics();
	            metrics.setToDefaults();
	            
	            //调用parseMethod方法，获取Package的实例
	            Object pkg = parseMethod.invoke(packageParser, new File(archiveFilePath), archiveFilePath, metrics, 0);
	            if(pkg == null) {
	                //Log.e("解析apk包失败");
	                return null;
	            }
	            
	            //获取名为"collectCertificates"的方法
	            Method certMethod = parserClass.getMethod(COLLECTCERTIFICATES_METHOD_NAME, packageClass, int.class);

	            //调用collectCertificates方法，收集证书
	            certMethod.invoke(packageParser, pkg, 0);
	            
	            //获取名为"generatePackageInfo"的方法
	            Method genMethod = parserClass.getMethod(GENERATEPACKAGEINFO_METHOD_NAME, packageClass, int[].class, int.class, long.class, long.class);

	            //最后调用generatePackageInfo方法，得到签名信息
	            PackageInfo pi = (PackageInfo)genMethod.invoke(packageParser, pkg, null, PackageManager.GET_SIGNATURES, 0, 0);
	            return pi.signatures[0].toString();
	            
	        } catch (ClassNotFoundException e) {
	            // TODO Auto-generated catch block
	        } catch (SecurityException e) {
	            // TODO Auto-generated catch block
	        } catch (NoSuchMethodException e) {
	            // TODO Auto-generated catch block
	        } catch (IllegalArgumentException e) {
	            // TODO Auto-generated catch block
	        } catch (InstantiationException e) {
	            // TODO Auto-generated catch block
	        } catch (IllegalAccessException e) {
	            // TODO Auto-generated catch block
	        } catch (InvocationTargetException e) {
	            // TODO Auto-generated catch block
	        }
	        return null;
	    }
    
//	https://android-review.googlesource.com/#/c/18769/1/core/java/android/content/pm/PackageManager.java
//	public PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags) {	1726
//		1727	PackageParser packageParser = new PackageParser(archiveFilePath);	PackageParser packageParser = new PackageParser(archiveFilePath);	1727
//		1728	DisplayMetrics metrics = new DisplayMetrics();	DisplayMetrics metrics = new DisplayMetrics();	1728
//		1729	metrics.setToDefaults();	metrics.setToDefaults();	1729
//		1730	final File sourceFile = new File(archiveFilePath);	final File sourceFile = new File(archiveFilePath);	1730
//		1731	PackageParser.Package pkg = packageParser.parsePackage(	PackageParser.Package pkg = packageParser.parsePackage(	1731
//		1732	sourceFile, archiveFilePath, metrics, 0);	sourceFile, archiveFilePath, metrics, 0);	1732
//		1733	if (pkg == null) {	if (pkg == null) {	1733
//		1734	return null;	return null;	1734
//		1735	}	}	1735
//				if ((flags & GET_SIGNATURES) != 0)	1736
//				packageParser.collectCertificates(pkg, 0);	1737
//		1736	return PackageParser.generatePackageInfo(pkg, null, flags);	return PackageParser.generatePackageInfo(pkg, null, flags);	1738
//		1737	}	}
    
    private static PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags) {
		// Workaround for
		// https://code.google.com/p/android/issues/detail?id=9151#c8
		try {
			Class packageParserClass = Class
					.forName("android.content.pm.PackageParser");
			Class[] innerClasses = packageParserClass.getDeclaredClasses();
			Class packageParserPackageClass = null;
			for (Class innerClass : innerClasses) {
				if (0 == innerClass.getName().compareTo(
						"android.content.pm.PackageParser$Package")) {
					packageParserPackageClass = innerClass;
					break;
				}
			}
			Constructor packageParserConstructor = packageParserClass	.getConstructor(String.class);
			Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, String.class,DisplayMetrics.class, int.class);
			Method collectCertificatesMethod = packageParserClass
					.getDeclaredMethod("collectCertificates",
							packageParserPackageClass, int.class);
			Method generatePackageInfoMethod = packageParserClass
					.getDeclaredMethod("generatePackageInfo",
							packageParserPackageClass, int[].class, int.class,
							long.class, long.class);
			packageParserConstructor.setAccessible(true);
			parsePackageMethod.setAccessible(true);
			collectCertificatesMethod.setAccessible(true);
			generatePackageInfoMethod.setAccessible(true);

			Object packageParser = packageParserConstructor
					.newInstance(archiveFilePath);

			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();

			final File sourceFile = new File(archiveFilePath);

			Object pkg = parsePackageMethod.invoke(packageParser, sourceFile,
					archiveFilePath, metrics, 0);
			if (pkg == null) {
				return null;
			}

			if ((flags & android.content.pm.PackageManager.GET_SIGNATURES) != 0) {
				collectCertificatesMethod.invoke(packageParser, pkg, 0);
			}

			return (PackageInfo) generatePackageInfoMethod.invoke(null, pkg,
					null, flags, 0, 0);
		} catch (Exception e) {
			Log.e("Signature Monitor",
					"android.content.pm.PackageParser reflection failed: "
							+ e.toString());
		}

		return null;
	}
    
  
    
    private static boolean isTestKeys(){
    	/**
		 * getprop |grep build 
		 * 包含test-keys表示rooted
		 */
		// get from build info
		String buildTags = android.os.Build.TAGS;
		if (buildTags != null && buildTags.contains("test-keys")) {
			Log.i(TAG, "该设备有contains test-keys ");
			return true ;
		}
		return false ;

    }
    
    
    private static boolean isSuperuserInstalled(){
    	// check if /system/app/Superuser.apk is present
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				Log.i(TAG, "该设备有/system/app/Superuser.apk ");
				return true ;
			}
		} catch (Throwable e1) {
			// ignore
		}
		return false ;
    }
    
    private static boolean isCommonRootAppInstalled(Context context) {
        if (AppUtil.getPacakgeInfo(context, "com.noshufou.android.su") != null) {
        	Log.i(TAG, "com.noshufou.android.su存在，该设备有root权限。");
            return true;
        }else if(AppUtil.getPacakgeInfo(context, "com.miui.uac") != null){
        	Log.i(TAG, "com.miui.uac存在，该设备有root权限。");
        	 return true;
        }else if(AppUtil.getPacakgeInfo(context, "com.koushikdutta.superuser") != null){
        	//MultitaskSuRequestActivity
        	Log.i(TAG, "com.koushikdutta.superuser存在，该设备有root权限。");
        	return true;
        }
        return false ;
    }
    private static boolean canMakeFileAsRoot() {
    	File sufilebin = new File("/data/data/root");
        try {
            sufilebin.createNewFile();
            if (sufilebin.exists()) {
                sufilebin.delete();
            }
            Log.i(TAG,"/data/data/root可以创建成功，该设备有root权限。");
            return true;
        } catch (IOException e) {
        	Log.i(TAG,"/data/data/root可以创建失败，没有权限。");
        }
    	return false ;
    }
    
    
    public static boolean checkRooted1() {
    	if(canMakeFileAsRoot()){
    		return true;
    	}
		if(isTestKeys()){
			return true ;
		}
		if(isSuperuserInstalled()){
			return true ;
		}
		/*if(isCommonRootAppInstalled(context)){
			return true ;
		}*/
		
		/*
		###此方法superuesr会提示用户###
		String commandToExecute = "su";
		Process process = null;            
	    try{
	        process = Runtime.getRuntime().exec(commandToExecute);
	        Log.i(TAG, "exec su success:");
	    } catch (Exception e) {
	    	Log.e(TAG, "su exception:",e);
	    } finally{
	        if(process != null){
	            try{
	                process.destroy();
	            }catch (Exception e) {
	            }
	        }
	    }
	    */
		return false ;

	}
    
    public static boolean checkRooted2() {
        if (new ExecShell().executeCommand(SHELL_CMD.CHECK_SU_BINARY) != null){
        	 Log.d(TAG, "checkRootMethod3: true" );
            return true;
        }else{
        	Log.d(TAG, "checkRootMethod3: false" );
            return false;
        }
    }
    
    
	static enum SHELL_CMD {
		CHECK_SU_BINARY(new String[] { "/system/xbin/which", "su" }), ;
		String[] command;

		SHELL_CMD(String[] command) {
			this.command = command;
		}
	}

	static class ExecShell {
		private static String LOG_TAG = ExecShell.class.getName();
		// ###此方法superuesr不会提示用户###
		public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
			
			String line = null;
			ArrayList<String> fullResponse = new ArrayList<String>();
			Process localProcess = null;

			try {
				localProcess = Runtime.getRuntime().exec(shellCmd.command);
			} catch (Exception e) {
				return null;
			}

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					localProcess.getOutputStream()));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					localProcess.getInputStream()));

			try {
				while ((line = in.readLine()) != null) {
					Log.d(TAG, shellCmd+"--> Line received: " + line);
					fullResponse.add(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d(TAG, shellCmd+"--> Full response was: "
					+ fullResponse);

			return fullResponse;
		}

	}
    
    /**
     * 支持自动安装的安装方法，为方便修改将其从原方法中抽出，增加AppItem参数。
     * 预计在所有修改完成后，替换原来的installApk方法。
     * @param ctx context
     * @param filepath filepath
     * @param item AppItem
     */
    public static void installApk(Context ctx, String packageName,String filepath) {
    	realInstallApk(ctx,packageName,filepath);
    }
    
    private static void realInstallApk( final Context ctx,final String packageName, final String apkfile) {
    	new Thread(){
    		public void run() {
    			boolean rooted = checkRooted1();
    	    	 if (!rooted) {
    	             //notifyStartSilentInstall(ctx, item);
    	             installAPKBySUCommand(ctx,packageName,apkfile);
    	         } else {
    	             startSystemInstallUI(ctx, apkfile);
    	         }
    		};
    	}.start();
    	
    }
    
    /**
     * 卸载应用
     * 
     * @param ctx
     *            Cotext
     * @param packageName
     *            应用package Name
     */
    public static void uninstallApk(Context ctx, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(uninstallIntent);
    }

	/**
	 * 使用InstallService通过SU命令安装APK。
	 * 
	 * @param apkFile
	 *            apk文件。
	 * @param context
	 *            context.
	 * @param ai
	 *            AppItem
	 */
	public static void installAPKBySUCommand(Context context,
			String packageName, String filepath) {
		Intent intent = new Intent();
		/*intent.setClass(context, SilentInstallService.class);
		intent.putExtra(
				SilentInstallService.InstallTaskArgs.EXTRA_PACKAGE_AUTO,
				packageName);
		intent.putExtra(SilentInstallService.InstallTaskArgs.EXTRA_FILE_AUTO,
				filepath);
		context.startService(intent);*/
	}
    	
    /**
     * 启动系统的安装界面
     * 
     * @param ctx
     *            Context
     * @param apkfile
     *            apk的文件对象
     */
    private static void startSystemInstallUI(Context ctx, String apkfile) {
        try {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType((Uri.fromFile(new File(apkfile))),
                    "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(installIntent);
        } catch (Exception e) {
        	
        }
    }
        

	
	public static boolean isFromGooglePlay(PackageManager mPm,
			String packageName) {
		String installPM = mPm.getInstallerPackageName(packageName);
		if (installPM == null) {
			// Definitely not from Google Play
			return false;
		} else if (installPM.equals("com.google.android.feedback")
				|| installPM.equals("com.android.vending")) {
			// Installed from the Google Play
			return true;
		}
		return false;
	}

	interface RootRequestCallBack{
		void onFinished(boolean sucessful);
	}
	/**
	 * 在新线程中请求root权限。
	 * @param context
	 * @param callback
	 */
	public static void requestRootPrivilege(final Context context,
			final RootRequestCallBack callback) {

		new Thread() {
			@Override
			public void run() {
				boolean requestRoot = requestRoot();
				if(callback != null ){
					callback.onFinished(requestRoot);
				}
			}

		}.start();
	}

	 
	private static boolean requestRoot(){
		boolean isRootGained = false;

		java.lang.Process process = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		try {
			process = Runtime.getRuntime().exec("su");// //"request su";
			OutputStream os = process.getOutputStream();
			InputStream is = process.getInputStream();
			if (os != null && is != null) {
				dos = new DataOutputStream(process.getOutputStream());
				dis = new DataInputStream(process.getInputStream());

				dos.writeBytes("id\n");
				dos.flush();// request flush
				/**
				   * mi-one的结果：
				   * uid=0(root) gid=0(root) groups=1003(graphics),1004(input),1007(log),1009(mount),
				   * 1011(adb),1015(sdcard_rw),3001(net_bt_admin),3002(net_bt),3003(inet)
				   */
				String result = dis.readLine();// request readLine
				boolean needExit = false;
				if (result == null) {
					needExit = false;
					isRootGained = false;
				} else if (result.toLowerCase().contains("uid=0")) {
					isRootGained = true;
					needExit = true;
				} else {
					isRootGained = false;
					needExit = true;
				}
				if (needExit) {
					dos.writeBytes("exit\n");
					os.flush();
				}
			}
		} catch (IOException e) {
			android.util.Log.e(TAG, "Request Root Error", e);
		} finally {
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
				if (process != null)
					process.destroy();
			} catch (Exception e2) {
			}
		}
		return isRootGained ;
	}

	/**
	 * 判断当前下载任务是不是需要弹出root用户下载提示。
	 * 条件：
	 * 1.当前是root用户;
	 * 2.当前设置项的下载后自动安装没打开;
	 * 3.问了少于3次;
	 * 4.每天最多检查一次；
	 * @return
	 */
	public static boolean shouldCheckRootUserDownload() {
		MineProfile mineProfile = MineProfile.getInstance();
		long currTime = System.currentTimeMillis();
		long lastCheckrootTime = mineProfile.getLastCheckrootTime();
		if ((lastCheckrootTime == 0 || (currTime - lastCheckrootTime)>24*60*60*1000) 
				&& mineProfile.isRootUser()
				&& !mineProfile.isInstallAutomaticllyAfterDownloading()
				&& mineProfile.getCheckRootPrompTime() < Constants.MAXTIME_PROMPT_CHECKROOT_DOWNLOAD) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取当前手机品牌名称。加这个因为小米手机上有su文件但是用户没有root权限。
	 * @return
	 */
	public static String getPhoneBrand() {
		String brand = android.os.Build.BRAND;
		if (TextUtils.isEmpty(brand)) {
			return "";
		} else {
			return brand.toLowerCase();
		}
	}
	
	/**
	 * 获取当前用户所属的短信中心号码
	 * @param context
	 * @return
	 */
	public static String getdetinationNum(Context context) {
		SharedPreferences defaulSp = PreferenceManager.getDefaultSharedPreferences(context);
		int operatorType = getNetworkType(context);
		String destination;
		switch (operatorType) {
		case Constants.CHINAMOBILE:
			destination = getDefaultValue(defaulSp, Constants.KEY_SMSC_CNMOBILE, R.string.smsc_cnmobile, context);
			break;
		case Constants.CHINAUNICOM:
			destination = getDefaultValue(defaulSp, Constants.KEY_SMSC_CNUNICOM, R.string.smsc_cnunicom, context);
			break;
		case Constants.CHINATELECOM:
			destination = getDefaultValue(defaulSp, Constants.KEY_SMSC_CNTELECOM, R.string.smsc_cntelecom, context);
			break;
		case Constants.UNKNOW_OPERATE:
		default:
			destination = getDefaultValue(defaulSp, Constants.KEY_SMSC_COMMONNUM, R.string.smsc_common, context);
			break;
		}
		return destination;
	}

	/**
	 * 根据MCC+MNC判断当前用户所在运营商网络
	 * @return
	 */
	private static int getNetworkType(Context context) {
		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = Constants.UNKNOW_OPERATE;
		String mccMnc = mTelephonyManager.getSimOperator();
		if (!TextUtils.isEmpty(mccMnc)) {
			if (mccMnc.equals("46000")||mccMnc.equals("46002")||mccMnc.equals("46007")) {
				networkType = Constants.CHINAMOBILE;
			} else if (mccMnc.equals("46001")) {
				networkType = Constants.CHINAUNICOM;
			} else if (mccMnc.equals("46003")) {
				networkType = Constants.CHINATELECOM;
			}
		}
		return networkType;
	}
	
	/**
	 * 从sharedPrefence中取指定key的值，如果为空字符串则返回配置文件中的内容.
	 * @param defaulSp
	 * @param key
	 * @param defResId
	 * @param context
	 * @return
	 */
	private static String getDefaultValue(SharedPreferences defaulSp,String key,int defResId,Context context) {
		String result = defaulSp.getString(key, "");
		if (TextUtils.isEmpty(result)) {
			result = context.getString(defResId);
		}
		return result;
	}
	/**
	 * 分享
	 */
	public static void shareApp(Context context,String shareContext) {
		Intent intentItem = new Intent(Intent.ACTION_SEND);
		intentItem.setType("text/plain");
		intentItem.putExtra(Intent.EXTRA_SUBJECT, "subject");
		intentItem.putExtra(Intent.EXTRA_TEXT, shareContext);
		intentItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intentItem, "分享"));
	}

}