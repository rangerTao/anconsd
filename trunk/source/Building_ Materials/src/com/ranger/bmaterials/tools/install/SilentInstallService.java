package com.ranger.bmaterials.tools.install;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.broadcast.AutoInstallAppMonitorReceiver;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

/**
 * 静默安装service.
 * 运行在独立进程里，解决4.0上内存不足导致的安装失败的问题。
 * @author chengyifan
 *
 */
public class SilentInstallService extends Service {
	public static final String EXTRA_INSTALLER_PACK = "arg_pack";
	
	/**
	 * TAG.
	 */
	private static final String TAG = AppSilentInstaller.TAG;
	
	/**
	 * DEBUG.
	 */
	private static final boolean DEBUG = Constants.DEBUG & true;
	
	/**
	 * 当前正在运行的安装任务。
	 */
	private int mCount = 0;
	
	/**
	 * 主线程handler。
	 */
	private Handler mHandler;
	
	/**
	 * 安装线程handler。
	 */
	private Handler mInstallHandler;
	
	/**
	 * 安装线程。
	 */
	private HandlerThread mInstallThread;
	
	/**
	 * 安装线程名。
	 */
	//private static final String INSTALL_APK_SU_THREAD_NAME = Constants.APPSEARCH_THREAD_PRENAME+ "InstallAPKBySUThread";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (DEBUG) {
			Log.i(TAG, "destory called");
		}
		Intent intent = new Intent(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_INSTALL_SERVICE_FINISHED);
		sendBroadcast(intent);
		
		mInstallThread.quit();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "[SilentInstallService#onStartCommand]");
		if (intent != null) {
			
			InstallPacket item = intent.getParcelableExtra(EXTRA_INSTALLER_PACK);
			Log.e(TAG, "onStartCommand data:"+item);
			if (mInstallHandler != null) {
				Message msg = mInstallHandler.obtainMessage(0);
				msg.obj = item;
				msg.sendToTarget();
				mCount++;
			} else {
				Log.e(TAG, "install handler is null");
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "[SilentInstallService#onCreate]");
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				mCount--;
				if (mCount <= 0) {
					stopSelf();
					
				}

			};
		};
		mInstallThread = new HandlerThread("");
		mInstallThread.start();
		mInstallHandler = new Handler(mInstallThread.getLooper()) {
			public void handleMessage(android.os.Message msg) {
			    try {
			    	InstallPacket pack = (InstallPacket) msg.obj;
			    	
			    	/**
			    	 * 
			    	 */
			        installSilent(pack);
	                mHandler.obtainMessage().sendToTarget();
	                
	                
			    } catch (ClassCastException e) {
			        if (DEBUG) {
			            Log.d(TAG, e.toString());
			        }
			    }
			};
		};

	}
	
	private void installSilent(InstallPacket item){
		if (item == null) {
			return;
		}
		String path = item.getFilepath();
		
		int result = PackageUtils.installSilent(getApplicationContext(), path);
		//PackageUtils.installSilent(path);
		 //installAPKBySUCommand(pack);
		
		// 通知
		Intent intent = new Intent(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_PACKAGE_ADDED_AUTO);
		
		
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_PACKAGE_AUTO, item.getPackageName());
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_FILE_AUTO, path);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ID_AUTO, item.getDownloadId());
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_DOWNLOAD_URL_AUTO, item.getDownloadUrl());
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_GAME_ID_AUTO, item.getGameId());
		
		
		
		if(result == PackageUtils.INSTALL_SUCCEEDED){
			if(Constants.DEBUG)Log.d(TAG, "[SilentInstallService#installSilent] result INSTALL_SUCCEEDED");
			intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_STATUE_AUTO, true);
			
			item.setStatus(InstallStatus.INSTALLED);
			
			intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ITEM_AUTO, (Parcelable)item);
		}else{
			if(Constants.DEBUG)Log.d(TAG, "[SilentInstallService#installSilent] result INSTALL_ERROR");
			intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_STATUE_AUTO, false);
			intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ERROR_REASON, result);
			
			item.setStatus(InstallStatus.INSTALL_ERROR);
			item.setErrorReason(result);
			
			intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ITEM_AUTO, (Parcelable)item);

            if(result == PackageUtils.INSTALL_FAILED_PERMISSION){
                MineProfile.getInstance().setInstallAutomaticllyAfterDownloading(false);
            }

		}
		sendBroadcast(intent);
		
	}
	
	
	/**
	 * 通过su命令安装安装包。
	 * @param task {@link InstallTaskArgs}
	 */
	private void installAPKBySUCommand(InstallPacket item) {
		Log.i("SilentInstallService", "installAPKBySUCommand");
		Log.e(TAG, "installAPKBySUCommand data:"+item);
		if (item == null) {
			return;
		}
		String path = item.getFilepath();
		String packageName = item.getPackageName();
		boolean installSuccess = false;
		
		
		/*if (AppUtils.isRunningInEclair()) {
			File tempFile = AppUtils.createTempPackageFile(this, new File(path));
	        if (tempFile == null) {
	    		Intent intent = new Intent(MyAppConstants.SILENT_INSTALL_FAILED_BROADCAST);
	        	intent.putExtra(MyAppConstants.EXTRA_APP_KEY, appKey);
	    		intent.putExtra(MyAppConstants.EXTRA_APP_NAME, appName);
	    		intent.putExtra(MyAppConstants.EXTRA_APK_FILE_PATH, path);
	    		sendBroadcast(intent);
	    		return;
	        }
	        path = tempFile.getAbsolutePath();
		}*/
		
		//FIX BUG:APPSEARCH-283 G6自动安装错误的下载包会重启手机
		//安装前检查安装包是否可用。
        /*if (!AppUtils.isAPKFileValid(path, this)) {
            Intent intent = new Intent(
                    MyAppConstants.SILENT_INSTALL_FAILED_BROADCAST);
            intent.putExtra(MyAppConstants.EXTRA_APP_KEY, appKey);
            intent.putExtra(MyAppConstants.EXTRA_APP_NAME, appName);
            intent.putExtra(MyAppConstants.EXTRA_APK_FILE_PATH, originalPath);
            sendBroadcast(intent);
            return;
		}*/
		
		java.lang.Process process = null;
		boolean needDestroyProcess = true;
		try {
			Log.i("SilentInstallService", "SilentInstallService execute 'su'");
			process = Runtime.getRuntime().exec("su");
			//int waitFor = process.waitFor();
			OutputStream os = process.getOutputStream();
			if (os != null) {
				DataOutputStream dos = new DataOutputStream(os);
				// 将文件路径用单引号括起来。
				byte[] command = ("pm install -r \'" + path + "\'\n")
						.getBytes("UTF-8");
				// dos.writeUTF("pm install -r " + path + "\n");
				Log.i("SilentInstallService", "SilentInstallService 'pm install -r "+path+"'");
				dos.write(command);
				dos.flush();

				dos.writeBytes("exit\n");
				Log.i("SilentInstallService", "SilentInstallService execute 'exit',waitting...");
				dos.flush();
				int exitValue = process.waitFor();
				
				needDestroyProcess = false; //waitFor()方法中已将Process teminate
				
				if (DEBUG) {
					Log.i(TAG, "silent install finished, exit value is:"
							+ exitValue);
				}
				Log.i("SilentInstallService", "SilentInstallService waitFor exitValue:"+(exitValue==0));
				if (exitValue == 0) {
					// 只有在exitValue为0时才读输出信息。
					// 因在某些机型上可能出现Segmentation fault,读输出流时会阻塞。
					DataInputStream dis = new DataInputStream(
							process.getInputStream());
					String line;
					while ((line = dis.readLine()) != null) {
						if (DEBUG) {
							Log.i(TAG, "result:" + line);
						}
						Log.i("SilentInstallService", "readLine "+line);
						if (line.toLowerCase().contains("success")) {
							installSuccess = true;
							break;
						}
					}
					dis.close();
					
					if (DEBUG) {
						DataInputStream error = new DataInputStream(process.getErrorStream());
						while ((line = error.readLine()) != null) {
							if (DEBUG) {
								Log.i(TAG, "error:" + line);
							}
							// pm在安装时会用error信息打印出安装包路径
							// 出错情况暂不处理
						}
						error.close();
					}
				}
				dos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("SilentInstallService", "IOException ",e);
			// 一般是没有su命令
			installSuccess = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.i("SilentInstallService", "InterruptedException ",e);
			installSuccess = false;
		} catch (Exception e) {
			e.printStackTrace();
			installSuccess = false;
			Log.i("SilentInstallService", "Other Exception ",e);
		} finally {
			if (process != null && needDestroyProcess) {
				process.destroy();
				Log.i("SilentInstallService", "SilentInstallService process destroy");
			}
		}
		Log.i("SilentInstallService", "SilentInstallService finished. 结果:"+installSuccess);
		Log.i(TAG, "silent install finished, 结果:"
				+ installSuccess);
		// 通知。
		Intent intent = new Intent(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_PACKAGE_ADDED_AUTO);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_PACKAGE_AUTO, packageName);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_FILE_AUTO, path);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_STATUE_AUTO, installSuccess);
		sendBroadcast(intent);
	}
	
	
	 

	/**
	 * 安装任务的封装对象。
	 * @author chengyifan
	 *
	 *//*
	public static class InstallTaskArgs {
		public static final String EXTRA_PACKAGE_AUTO = "package_AUTO";
		public static final String EXTRA_FILE_AUTO = "file_AUTO";
		*//** 路径 *//*
		private String path;
		*//** appkey *//*
		private String appKey;
		*//** 应用名称 *//*
		private String appName;
		
		public InstallTaskArgs(Intent intent) {
			appName = intent.getStringExtra(EXTRA_PACKAGE_AUTO);
			path = intent.getStringExtra(EXTRA_FILE_AUTO);
		}
	}*/
}
