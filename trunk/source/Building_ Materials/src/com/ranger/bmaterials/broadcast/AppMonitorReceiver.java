package com.ranger.bmaterials.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppCache;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.download.Constants;
import com.ranger.bmaterials.download.DownloadInfo;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.BindPhoneResult;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;
import com.ranger.bmaterials.tools.install.BackAppListener;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.work.FutureTaskManager;

public class AppMonitorReceiver extends BroadcastReceiver {
	static String TAG = AppSilentInstaller.TAG;//"AppMonitorReceiver" ;
	
	
	private Context context ;
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			this.context = context ;
			String action = intent.getAction();
			
			if(action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				if(com.ranger.bmaterials.app.Constants.DEBUG)Log.v(Constants.TAG, "Received broadcast intent for " +
	                        Intent.ACTION_MEDIA_MOUNTED);
//	              initIfNessary();
	              
			}else if(action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
				CustomToast.showToast(context, context.getString(R.string.sdcard_unmounted));
				//Toast.makeText(context, "SDcard已经拔出或者不可访问，无法下载!", Toast.LENGTH_LONG).show();
			}else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION) && DeviceUtil.isNetworkAvailable(context)) {
				submitIncompleteTasks();
	        }else if(action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
					action.equals(Intent.ACTION_PACKAGE_REPLACED)){
	        	updateDataIfNecessary(intent);
	        	BroadcaseSender sender = BroadcaseSender.getInstance(context);
	    		sender.sendPreBroadcast(intent);
	        }else if(action.equals(BroadcaseSender.ACTION_DOWNLOAD_COMPLETE)){
	        	//onDownloadCompleted(intent);
	        }else if(action.equals(BroadcaseSender.ACTION_DOWNLOAD_PAUSE)){
	        	//onDownloadPaused(intent);
	        }else if(action.equals(BroadcaseSender.ACTION_DOWNLOAD_PAUSE_BY_USER)){
	        	//onDownloadPausedByUser(intent);
	        }else if(action.equals(BroadcaseSender.ACTION_DOWNLOAD_START)){
	        	//onDownloadStart(intent);
	        }else if(action.equals(BroadcaseSender.ACTION_DOWNLOAD_CANCLE)){
	        	//onDownloadCancle(intent);
	        }
			
			if(com.ranger.bmaterials.app.Constants.DEBUG)Log.v("MyReceiver", "Received broadcast intent for " +action);
			
		} catch (Exception e) {
		}
	}
	
//	private void onDownloadCompleted(final Intent intent){
//		try {
//			new Thread(){
//				@Override
//				public void run() {
//					DownloadItemOutput output = queryDownload(intent);
//					if(output != null){
//						BackAppListener listener = BackAppListener.getInstance();
//						listener.notifyAndSubmit(output);
//					}
//					
//					String url = intent.getStringExtra(DownloadInfo.EXTRA_URL);
//					String title = intent.getStringExtra(DownloadInfo.EXTRA_TITLE);
//					long downloadId = intent.getLongExtra(DownloadInfo.EXTRA_ID, -1);
//					boolean successful = intent.getBooleanExtra(DownloadInfo.EXTRA_SUCCESSFUL, false);
//					if(downloadId > 0 && !TextUtils.isEmpty(url)){
//						if(!successful){
//							DownloadStatistics.addDownloadGameFailedStatistics(context, title);
//						}else {
//							DownloadStatistics.addDownloadGameSucceedStatistics(GameTingApplication.getAppInstance(), title);
//						}
//					}
//				}
//			}.start();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
//	private void onDownloadPaused(final Intent intent){
//		new Thread(){
//			@Override
//			public void run() {
//				try {
//					
//					DownloadItemOutput output = queryDownload(intent);
//					if(output != null){
//						BackAppListener listener = BackAppListener.getInstance();
//						listener.notifyAndSubmit(output);
//					}
//					if(com.duoku.gamesearch.app.Constants.DEBUG)Log.i(TAG, "onDownloadPaused output:"+output);
//					
//					String url = intent.getStringExtra(DownloadInfo.EXTRA_URL);
//					String title = intent.getStringExtra(DownloadInfo.EXTRA_TITLE);
//					long downloadId = intent.getLongExtra(DownloadInfo.EXTRA_ID, -1);
//					if(downloadId > 0 && !TextUtils.isEmpty(url)){
//						DownloadStatistics.addPauseDownloadGameStatistics(GameTingApplication.getAppInstance(), title);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();
//		
//	}
//	private void onDownloadCancle(final Intent intent){
//		new Thread(){
//			@Override  
//			public void run() {
//				String url = intent.getStringExtra(DownloadInfo.EXTRA_URL);
//				String title = intent.getStringExtra(DownloadInfo.EXTRA_TITLE);
//				long downloadId = intent.getLongExtra(DownloadInfo.EXTRA_ID, -1);
//				BackAppListener listener = BackAppListener.getInstance();
//				listener.cancleNotification(url, downloadId);
//			}
//		}.start();
//	}
//	private void onDownloadStart(final Intent intent){
//		new Thread(){
//			@Override
//			public void run() {
//				DownloadItemOutput output = queryDownload(intent);
//				if(output != null){
//					BackAppListener listener = BackAppListener.getInstance();
//					listener.notifyAndSubmit(output);
//				}
//			}
//		}.start();
//	}
	
//	private void onDownloadPausedByUser(final Intent intent){
//		onDownloadStart(intent);
//	}
	
	private DownloadItemOutput queryDownload( Intent intent){
		try {
			String url = intent.getStringExtra(DownloadInfo.EXTRA_URL);
			String title = intent.getStringExtra(DownloadInfo.EXTRA_TITLE);
			long downloadId = intent.getLongExtra(DownloadInfo.EXTRA_ID, -1);
			if(downloadId > 0 && !TextUtils.isEmpty(url)){
				DownloadItemOutput output = DownloadUtil.getDownloadInfo(GameTingApplication.getAppInstance(), downloadId);
				return output ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "queryDownload return null",e);
			
		}
		Log.e(TAG, "queryDownload return null");
		return null ;
		
	}
	
	private void initIfNessary(){
		new AsyncTask<Void, Void, Long>(){

			@Override
			protected Long doInBackground(Void... params) {
				AppManager.getInstance(context).init();
				long maxMemory = DeviceUtil.getMaxMemory();
				long usableSpace = DeviceUtil.getUsableSpace();
				return usableSpace;
			}
			protected void onPostExecute(Long result) {
				if(result < 20*1024 *1024){
					CustomToast.showToast(context, context.getString(R.string.sdcard_lack_space));
					//Toast.makeText(GameTingApplication.getAppInstance(), "SD卡存储空间不足!", Toast.LENGTH_LONG).show();
				}
			};
			
		}.execute();
	}
	
	private void submitIncompleteTasks(){
		//ScheduledRefreshTask scheduledRefreshTask = new ScheduledRefreshTask(GameTingApplication.getAppInstance());
		//scheduledRefreshTask.execute();
		
		new Thread(){
			public void run() {
				FutureTaskManager taskManager = FutureTaskManager.getInstance();
				taskManager.submitIncompleteIfNecessary();
			};
		}.start();
	}
	
	
	
	
	
	private void updateDataIfNecessary(final Intent intent){
		new Thread(){
			public void run() {
				BroadcaseSender sender = BroadcaseSender.getInstance(context);
				String action = intent.getAction();
		         if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
		        	 if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i("MyLogcatObserver", "receiver:ACTION_PACKAGE_ADDED");
		             onPackageAdded(context, intent);
		             if(com.ranger.bmaterials.app.Constants.DEBUG) Log.i(TAG, "onPackageAdded notifyPackageAdded");
		             sender.notifyPackageAdded(intent);
		         } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
		        	 if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i("MyLogcatObserver", "receiver ACTION_PACKAGE_REMOVED");
		             if(!isApkReplaced(intent)){
		            	 onPackageRemoved(context, intent);
			             sender.notifyPackageRemoved(intent);
		             }
		         } else if(action.equals(Intent.ACTION_PACKAGE_REPLACED)){
		        	 if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i("MyLogcatObserver", "receiver ACTION_PACKAGE_REPLACED");
		        	 onPackageReplaced(context, intent);
		         }
			}

			

			
			;
		}.start();
	}
	
	
	
	
	private void onPackageReplaced(Context context, Intent intent) {
		if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i(TAG, "onPackageReplaced:"+intent.getData());
	}



	private void onPackageAdded(Context context, Intent intent) {
		try {
			String packageName = intent.getData().getSchemeSpecificPart();
			
			//boolean booleanExtra = intent.getBooleanExtra(Intent.EXTRA_REPLACING , false);
			//boolean booleanExtra2 = intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED  , false);
			//int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
			
			//Log.i("AppMonitorReceiver", "onPackageAdded EXTRA_REPLACING "+booleanExtra+" EXTRA_DATA_REMOVED:"+booleanExtra2+" EXTRA_UID:"+uid);
			/*File dataFolder = new File("/data/data/"+packageName);
			if(dataFolder.exists() && dataFolder.isDirectory() ){
				if(com.duoku.gamesearch.app.Constants.DEBUG){
					Log.i("AppMonitorReceiver", "onPackageAdded /data/data/"+packageName+"存在");
				}
			}else {
				if(com.duoku.gamesearch.app.Constants.DEBUG){
					Log.i("AppMonitorReceiver", "onPackageAdded /data/data/"+packageName+"不存在");
				}
			}*/
			Boolean systemPackage = AppUtil.isSystemPackage(
					context.getPackageManager(), packageName);
			if (systemPackage == null || systemPackage) {
				return;
			}
			AppManager manager = AppManager.getInstance(context);
			
			InstalledAppInfo loadAppInfo = AppUtil.loadAppInfo(context.getPackageManager(), packageName);
			if(!packageName.equals("com.duoku.gamesearch") && loadAppInfo != null){
				DownloadAppInfo downloadApp = manager.addInstalledAppRecord(loadAppInfo);
				FutureTaskManager taskManager = FutureTaskManager.getInstance();
				if(downloadApp == null){
					//不能确定是否为游戏，需要上传服务器确定
					if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i(TAG, "onPackageAdded packageName:"+packageName+" verifing...");
					taskManager.verifyGame(context, packageName);
					checkAnother(packageName);
				} else {
					manager.removeDownloadRecordIfNecessary(packageName, downloadApp.getDownloadId());

					BackAppListener.showInstalledNotification(GameTingApplication.getAppInstance(), downloadApp);
					taskManager.registerGame(context, downloadApp.getGameId());

					DownloadStatistics.addInstallGameStatistics(GameTingApplication.getAppInstance(), downloadApp.getName());

					if (MineProfile.getInstance().getIsLogin()) {
						NetUtil.getInstance().requestGetCoin(MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), 0, 3, downloadApp.getGameId(),
								new IRequestListener() {

									@Override
									public void onRequestSuccess(BaseResult responseData) {
										BindPhoneResult result = (BindPhoneResult) responseData;

										MineProfile.getInstance().addCoinnum(result.getCoinnum());
									}

									@Override
									public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
									}
								});
					}
				}
				if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i(TAG, "onPackageAdded packageName:"+packageName+" query from download list:"+downloadApp);
				if(com.ranger.bmaterials.app.Constants.DEBUG)Log.i(TAG, "onPackageAdded packageName:"+packageName+" version:"+loadAppInfo.getVersion()+" version int:"+loadAppInfo.getVersionInt());
				
				
				// 如果安装的是已下载的，则删除?
				// 安装成功，删除下载记录?
				// 如果安装的是已下载更新，则从更新列表删除?
				
				AppCache cache = AppCache.getInstance();
				cache.refreshDownload(context);
				cache.refreshInstall(context);
				//如果是从更新中安装，则需要刷新缓存
				cache.refreshUpdatable(context);
			}else{
				manager.removeDownloadRecordIfNecessary(packageName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "onPackageAdded Error",e);
		}
	}
	
	
	
	
	private void checkAnother(String packageName){
		
		AppManager manager = AppManager.getInstance(context);
		List<DownloadAppInfo> downloadFiles = manager.getAndCheckDownloadGames();
		if(downloadFiles != null){
			int count = 0 ;
			DownloadAppInfo target = null ;
			for (DownloadAppInfo d : downloadFiles) {
				if(packageName.equals(d.getPackageName())){
					count++;
					target = d ;
				}
			}
			if(count == 1 && target != null){
				BackAppListener.showInstalledNotification(GameTingApplication.getAppInstance(), target);
				DownloadStatistics.addInstallGameStatistics(GameTingApplication.getAppInstance(), target.getName());
				AppDao appDbHandler = DbManager.getAppDbHandler();
				BaseAppInfo baseAppInfo = new BaseAppInfo(packageName, "");
				ArrayList<BaseAppInfo> list = new ArrayList<BaseAppInfo>();
				list.add(baseAppInfo);
				appDbHandler.updateWhiteList(list);
				
				BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
				sender.notifyWhiteListInitlized();
				
				manager.removeDownloadRecordIfNecessary(packageName);
				
				
				FutureTaskManager taskManager = FutureTaskManager.getInstance();
				taskManager.registerGame(context,target.getGameId());
			}
		}
	}
	
	
	private void onPackageRemoved(Context context, Intent intent) {
		try {
			String packageName = intent.getData().getSchemeSpecificPart();
			AppManager manager = AppManager.getInstance(context);
			InstalledAppInfo removedApp = manager.removeInstallAppRecord(packageName);
			
			
			//InstalledAppInfo loadAppInfo = AppUtil.loadAppInfo(context.getPackageManager(), packageName);
			manager.updateDownloadRecordIfNecessary(packageName);
			if(removedApp != null){
				manager.notifyForAppRemoved(removedApp);
			}
			try {
				List<DownloadAppInfo> downloadFiles = manager.getAndCheckDownloadGames();
				if(downloadFiles != null){
					int count = 0 ;
					DownloadAppInfo target = null ;
					for (DownloadAppInfo d : downloadFiles) {
						if(packageName.equals(d.getPackageName())){
							count++;
							target = d ;
						}
					}
					if(count == 1 && target != null){
						 BackAppListener.getInstance().cancleNotification(target);
					    
					}
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		        
			AppCache cache = AppCache.getInstance();
			cache.refreshDownload(context);
			cache.refreshInstall(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private boolean isApkReplaced(Intent intent) {
		boolean booleanExtra = intent.getBooleanExtra(Intent.EXTRA_REPLACING , false);
//		boolean booleanExtra2 = intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED  , false);
//		int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
//		
//		Log.i("AppMonitorReceiver", "onPackageRemoved EXTRA_REPLACING "+booleanExtra+" EXTRA_DATA_REMOVED:"+booleanExtra2
//				+" EXTRA_UID:"+uid);
//		boolean replace  = false ;
//		File dataFolder = new File("/data/data/"+packageName);
//		if(dataFolder.exists() && dataFolder.isDirectory() ){
//			if(com.duoku.gamesearch.app.Constants.DEBUG){
//				Log.i("AppMonitorReceiver", "onPackageRemoved /data/data/"+packageName+"存在");
//			}
//			replace = true ;
//		}else {
//			if(com.duoku.gamesearch.app.Constants.DEBUG){
//				Log.i("AppMonitorReceiver", "onPackageRemoved /data/data/"+packageName+"不存在");
//			}
//			replace = false ;
//		}
		return booleanExtra ;
	}
	

}
