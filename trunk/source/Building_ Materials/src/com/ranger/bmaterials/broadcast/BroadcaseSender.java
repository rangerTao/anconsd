package com.ranger.bmaterials.broadcast;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.ranger.bmaterials.app.AppCache;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.SnapNumber.SnapNumberStatus;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;

public class BroadcaseSender {
	public static final String ACTION_DOWNLOAD_COMPLETE = com.ranger.bmaterials.download.DownloadManager.ACTION_DOWNLOAD_COMPLETE ;
	public static final String ACTION_DOWNLOAD_PAUSE = com.ranger.bmaterials.download.DownloadManager.ACTION_DOWNLOAD_PAUSE ;
	public static final String ACTION_DOWNLOAD_PAUSE_BY_USER = com.ranger.bmaterials.download.DownloadManager.ACTION_DOWNLOAD_PAUSE_BY_USER ;
	public static final String ACTION_DOWNLOAD_START =  com.ranger.bmaterials.download.DownloadManager.ACTION_DOWNLOAD_START ;
	public static final String ACTION_DOWNLOAD_CANCLE =  com.ranger.bmaterials.download.DownloadManager.ACTION_DOWNLOAD_CANCLE ;
	public static final String ACTION_DOWNLOAD_RUNNING =  com.ranger.bmaterials.download.DownloadManager.ACTION_DOWNLOAD_RUNNING ;
	
	static BroadcaseSender instance ;
	Context context ;
	
	/**下载任务发生改变（增加或删除下载任务）*/
	public static final String ACTION_DOWNLOAD_CHANGED = "duoku.gamesearch.intent.action.DOWNLOAD_CHANGED";
	/**静默安装状态改变*/
	public static final String ACTION_INSTALL_CHANGED = "duoku.gamesearch.intent.action.INSTALL_CHANGED";
	public static final String DOWNLOAD_CHANGED_ARG = "download_arg";
	public static final String DOWNLOAD_CHANGED_PACKAGE = "download_arg_package";
	
	/**白名单列表初始化成功*/
	public static final String ACTION_WHITELIST_INITIALIZED = "duoku.gamesearch.intent.action.WHITELIST_INITIALIZED";
	/**安装列表初始化成功*/
	public static final String ACTION_INSTALLED_LIST_INITIALIZED = "duoku.gamesearch.intent.action.INSTALLEDLIST_INITIALIZED";
	/**更新列表初始化成功*/
	public static final String ACTION_UPDATABLE_LIST_INITIALIZED = "duoku.gamesearch.intent.action.UPDATABLELIST_INITIALIZED";
	
	/**
	 * 下载和更新的app数量变化
	 */
	public static final String ACTION_MANAGER_APPS_CHANGED = "duoku.gamesearch.intent.action.APPS_CHANGED";
	public static final String MANAGER_APPS_CHANGED_ARG = "arg_number";
	
	public static final String ACTION_DELETE_DOWNLOADED_PKG = "duoku.gamesearch.intent.action.DELETEDOWNLOADEDPKG";
	public static final String ACTION_USER_LOGIN = "duoku.gamesearch.intent.action.USER_LOGIN";
	public static final String ACTION_USER_LOGOUT = "duoku.gamesearch.intent.action.USER_LOGOUT";
	public static final String ACTION_SNAP_NUMBER = "duoku.gamesearch.intent.action.SNAP_NUMBER";
	//重复抢号
	public static final String ACTION_SNAP_NUMBER_MUTI_ACTION = "duoku.gamesearch.intent.action.SNAP_NUMBER_MUTI";
	//抢光或结束
	public static final String ACTION_SNAP_NUMBER_OVER = "duoku.gamesearch.intent.action.SNAP_NUMBER_OVER";
	public static final String ACTION_SNAP_NUMBER_NONE = "duoku.gamesearch.intent.action.SNAP_NUMBER_NONE";
	public static final String SNAP_NUMBER_ARG = "snap_number_arg";
	public static final String SNAP_NUMBER_LEFT_COUNT_ARG = "snap_number_left_arg";
	public static final String SNAP_NUMBER_STATUS_ARG = "snap_number_status_arg";
	
	public static final String ACTION_SNAP_DETAIL_RESULT = "duoku.gamesearch.intent.action.SNAP_NUMBER_DETAIL_RESULT";
	/**程序覆盖安装*/
	public static final String ACTION_PACKAGE_REPLACED = "duoku.gamesearch.intent.action.PACKAGE_REPLACED";
	/**程序卸载*/
	public static final String ACTION_PACKAGE_REMOVED = "duoku.gamesearch.intent.action.PACKAGE_REMOVED";
	/**程序安装*/
	public static final String ACTION_PACKAGE_ADDED = "duoku.gamesearch.intent.action.PACKAGE_ADDED";
	
	
	public static final String ACTION_PRE_PACKAGE_EVENT = "duoku.gamesearch.intent.action.PRE_PACKAGE_EVENT";
	public static final String ARG_ORIGIANL_ACTION = "original_action";
	
	/**可更新item的忽略状态改变*/
	public static final String ACTION_IGNORED_STATE_CHANGED = "duoku.gamesearch.intent.action.IGNORED_STATE_CHANGED";
	public static final String ARG_IGNORED_STATE_CHANGED_PACKAGES = "ignored_packages";
	public static final String ARG_IGNORED_STATE = "ignored_state";
	
	public static final String ACTION_COLLECT_GAME_SUCCESS = "duoku.gamesearch.intent.action.COLLECT_GAME_SUCCESS";
	public static final String ACTION_COLLECT_GAME_CANCEL = "duoku.gamesearch.intent.action.COLLECT_GAME_CANCEL";
	public static final String ACTION_COLLECT_GUIDE_SUCCESS = "duoku.gamesearch.intent.action.COLLECT_GUIDE_SUCCESS";
	public static final String ACTION_COLLECT_GUIDE_CANCEL = "duoku.gamesearch.intent.action.COLLECT_GUIDE_CANCEL";
	
	public static final String ACTION_DISMISS_MINECOIN_TIP = "duoku.gamesearch.intent.action.DISMISS_COIN_TIP";
	
	public static final String ACTION_PACKAGE_STATUS_CHANGED = "duoku.gamesearch.intent.action.PACKAGE_STATUS_CHANGED";
	public static final String ARG_PACKAGE_STATUS_CHANGED = "arg_PACKAGE_STATUS_CHANGED";
	public static final String ARG_CURRENT_SIZE = "arg_current_size";
	public static final String ARG_TOTAL_SIZE = "arg_total_size";
	private static final String TAG = BroadcaseSender.class.getSimpleName();
	
	private BroadcaseSender(Context context) {
		this.context = context ;
	}
	public static synchronized BroadcaseSender getInstance(Context context){
		if(instance == null){
			instance = new BroadcaseSender(context);
		}
		return instance ;
	}
	/**
	 * @param downloadOrOtherWise 下载或者取消下载
	 * @param packageName
	 */
	public void notifyDownloadChanged(boolean downloadOrOtherWise,String... packageNams){
		AppCache cache = AppCache.getInstance();
		cache.refreshDownload(context);
		cache.refreshUpdatable(context);
		
		//Log.d("DownloadLog", "[BroadcaseSender]notifyDownloadChanged ACTION_DOWNLOAD_CHANGED");
		//Log.d(AppCache.TAG, "[BroadcaseSender]notifyDownloadChanged downloadOrOtherWise:" +downloadOrOtherWise);
		
		Intent intent = new Intent(ACTION_DOWNLOAD_CHANGED);
		
		intent.putExtra(DOWNLOAD_CHANGED_ARG, downloadOrOtherWise);
		String[] arr = null ;
		
		if(packageNams == null || packageNams.length==0){
			arr = new String[]{};
		}else{
			arr = packageNams ;
		}
		intent.putExtra(DOWNLOAD_CHANGED_PACKAGE, arr);
		if (Constants.DEBUG)Log.d("PopNumber", "[BroadcaseSender#notifyDownloadChanged]:"/*+packageNams[0]*/);
		context.sendBroadcast(intent);
	}
	/**
	 * 静默安装
	 */
	public void notifyInstallChanged(){
		if (Constants.DEBUG)Log.d(AppSilentInstaller.TAG, "notifyInstallChanged "+ACTION_INSTALL_CHANGED);
		Intent intent = new Intent(ACTION_INSTALL_CHANGED);
		context.sendBroadcast(intent);
	}
	
	public synchronized void notifyManagerAppsChanged(int number){
		if (Constants.DEBUG)Log.d("PopNumber", "[BroadcaseSender#notifyManagerAppsChanged] "+ACTION_MANAGER_APPS_CHANGED+"number "+number);
		Intent intent = new Intent(ACTION_MANAGER_APPS_CHANGED);
        String num = number + "a";
		intent.putExtra(MANAGER_APPS_CHANGED_ARG, new String(num));
		context.sendBroadcast(intent);
	}
	
	private void notify(String action){
		if (Constants.DEBUG)Log.i("Refresh", "ScheduledRefreshTask notify action "+action);
		context.sendBroadcast(new Intent(action));
	}
	
	private void notifyWithNum(String action){
		if (Constants.DEBUG)Log.i("Refresh", "ScheduledRefreshTask notify action "+action);
		Intent refresh = new Intent(action);
        String num = AppCache.getInstance().getPopNumber(GameTingApplication.getAppInstance()) + "a";
		refresh.putExtra(BroadcaseSender.MANAGER_APPS_CHANGED_ARG,  num);
		context.sendBroadcast(refresh);
	}
	
	/*notify(ACTION_WHITELIST_INITIALIZED);
	notify(ACTION_INSTALLED_LIST_INITIALIZED);
	notify(ACTION_UPDATABLE_LIST_INITIALIZED);*/
	
	public void notifyWhiteListInitlized(){
		if (Constants.DEBUG)Log.d(AppCache.TAG, "[BroadcaseSender]notifyWhiteListInitlized");
		AppCache cache = AppCache.getInstance();
		cache.refreshInstall(context);
		
		notify(ACTION_WHITELIST_INITIALIZED);
	}
	public void notifyInstalledListInitlized(){
		if (Constants.DEBUG)Log.d(AppCache.TAG, "[BroadcaseSender]notifyInstalledListInitlized");
		AppCache cache = AppCache.getInstance();
		cache.refreshInstall(context);
		
		notify(ACTION_INSTALLED_LIST_INITIALIZED);
		
		/**
		 * 通知界面需要重新查询状态
		 */
		PackageMode packageMode = new PackageMode();
		packageMode.status = PackageMode.RESET_STATUS ;
		PackageHelper.notifyPackageStatusChanged(packageMode);
		
	}
	public void notifyUpdatableInitlized(){
		if (Constants.DEBUG)Log.d(AppCache.TAG, "[BroadcaseSender]notifyUpdatableInitlized");
		AppCache cache = AppCache.getInstance();
		cache.refreshUpdatable(context);
		
		notifyWithNum(ACTION_UPDATABLE_LIST_INITIALIZED);
	}
	
	public void notifyDeleteDownloadedPkg(){
		notify(ACTION_DELETE_DOWNLOADED_PKG);
	}
	public void notifyUserLogin(){
		notify(ACTION_USER_LOGIN);
	}
	public void notifyUserLogout(){
		notify(ACTION_USER_LOGOUT);
	}
	public void notifySnapNumber(String snapId){
		Intent intent = new Intent(ACTION_SNAP_NUMBER);
		intent.putExtra(SNAP_NUMBER_ARG, snapId);
		context.sendBroadcast(intent);
	}
	public void notifySnapNumberDetail(String snapId,SnapNumberStatus status,int leftCount){
		Intent intent = new Intent(ACTION_SNAP_DETAIL_RESULT);
		intent.putExtra(SNAP_NUMBER_ARG, snapId);
		intent.putExtra(SNAP_NUMBER_LEFT_COUNT_ARG, leftCount);
		intent.putExtra(SNAP_NUMBER_STATUS_ARG, status);
		context.sendBroadcast(intent);
	}
	
	public void notifySnapNumberOver(String snapId){
		Intent intent = new Intent(ACTION_SNAP_NUMBER_OVER);
		intent.putExtra(SNAP_NUMBER_ARG, snapId);
		context.sendBroadcast(intent);
	}
	public void notifySnapNumberNone(String snapId){
		Intent intent = new Intent(ACTION_SNAP_NUMBER_NONE);
		intent.putExtra(SNAP_NUMBER_ARG, snapId);
		context.sendBroadcast(intent);
	}
	public void notifySnapNumberMutilple(String snapId){
		Intent intent = new Intent(ACTION_SNAP_NUMBER_MUTI_ACTION);
		intent.putExtra(SNAP_NUMBER_ARG, snapId);
		context.sendBroadcast(intent);
	}
	
	public void sendPreBroadcast(Intent intent){
		
		if (Constants.DEBUG)Log.i("SearchResult", "AppMonitorReceiver send ACTION_PRE_PACKAGE_EVENT ,original:"+intent.getAction());
		Intent resultIntent = new Intent(ACTION_PRE_PACKAGE_EVENT);
		resultIntent.putExtra(ARG_ORIGIANL_ACTION, intent.getAction());
		resultIntent.setData(intent.getData());
        context.sendBroadcast(resultIntent);
	}
	
	public void sendPreBroadcastForPackageEvent(boolean addOrRemove,String packageName){
		Intent resultIntent = new Intent(ACTION_PRE_PACKAGE_EVENT);
		if(addOrRemove){
			resultIntent.putExtra(ARG_ORIGIANL_ACTION, Intent.ACTION_PACKAGE_ADDED);
		}else{
			resultIntent.putExtra(ARG_ORIGIANL_ACTION, Intent.ACTION_PACKAGE_REMOVED);
			GeneralStatistics.uninstallGameCountStatistics(GameTingApplication.getAppInstance(), packageName);
			BroadcaseSender sender = BroadcaseSender.getInstance(context);
			sender.notifyWhiteListInitlized();
		}
		resultIntent.setData(Uri.parse("package:"+packageName));
        context.sendBroadcast(resultIntent);
	}
	
	public void notifyPackageAdded(final Intent intent) {
		Intent resultIntent = new Intent(ACTION_PACKAGE_ADDED);
		 resultIntent.setData(intent.getData());
		 context.sendBroadcast(resultIntent);
	}
	
	public void notifyPackageRemoved(final Intent intent) {
		Intent resultIntent = new Intent(ACTION_PACKAGE_REMOVED);
		 resultIntent.setData(intent.getData());
		 context.sendBroadcast(resultIntent);
	}
	
	
	
	public void notifyIgnoredStatedChanged(boolean ignored,
			String... packageNames) {
		Intent resultIntent = new Intent(ACTION_IGNORED_STATE_CHANGED);
		resultIntent.putExtra(ARG_IGNORED_STATE, ignored);
		resultIntent.putExtra(ARG_IGNORED_STATE_CHANGED_PACKAGES, packageNames);
		context.sendBroadcast(resultIntent);
	}
	
	/**
	 * 
	 * 通知apk状态改变<br/>
	 * 注意，可能一个状态通知多次，所以最好调用着检查一下先前的状态然后来确定是否更新自己的状态
	 * 
	 * @param mode
	 */
	public void notifyPackageStatusChanged2(PackageMode mode/*,Long currentSize,Long totalSize*/) {
		try {
			Intent resultIntent = new Intent(ACTION_PACKAGE_STATUS_CHANGED);
			resultIntent.putExtra(ARG_PACKAGE_STATUS_CHANGED, (Parcelable)mode);
			/*if(mode.status == PackageHelper.DOWNLOAD_RUNNING){
				resultIntent.putExtra(ARG_CURRENT_SIZE, currentSize);
				resultIntent.putExtra(ARG_TOTAL_SIZE, totalSize);
			}*/
			context.sendBroadcast(resultIntent);
		} catch (Exception e) {
			Log.e(TAG, "[notifyPackageStatusChanged] Error",e);
		}
	}
	
	
	
	Handler handler = null  ;
	static final int NOTIFIER_MSG = 9999;
	class MyHandler extends Handler{
		
		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == NOTIFIER_MSG){
				try {
					PackageMode mode = (PackageMode) msg.obj ;
					Set<PackageCallback> packageListners = PackageHelper.getPackageListners();
					if(packageListners != null && packageListners.size() >0){
						for (PackageCallback listener : packageListners) {
							
							if(null != listener){
                                try{
                                    listener.onPackageStatusChanged(mode);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void notifyPackageStatusChanged(PackageMode mode/*,Long currentSize,Long totalSize*/) {
		if(handler == null){
			HandlerThread notifier = new HandlerThread("notifier");
			notifier.setPriority(Thread.NORM_PRIORITY-1);
			notifier.start();
			handler = new MyHandler(notifier.getLooper());
		}
		Message msg = Message.obtain();
		if(msg == null){
			msg = new Message();
		}
		msg.what = NOTIFIER_MSG ;
		msg.obj = mode ;
		handler.sendMessage(msg);
	}
	
	
	
}
