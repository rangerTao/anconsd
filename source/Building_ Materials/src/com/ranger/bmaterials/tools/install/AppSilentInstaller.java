package com.ranger.bmaterials.tools.install;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.broadcast.AutoInstallAppMonitorReceiver;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.mode.PackageMark;

public class AppSilentInstaller {
	
	public static final String TAG = "AppInstaller";
	private CopyOnWriteArraySet<InstallPacket> dataSet = new CopyOnWriteArraySet<InstallPacket>() ;
	

	public enum InstallStatus{
		UNINSTALLED(0),
		INSTALLING(1),
		INSTALLED(2),
		INSTALL_ERROR(3),
		UNINSTALLING(4);
		
		private int index ;
		private InstallStatus(int index){
			this.index = index ;
		}
		
		public int getIndex(){
			return this.index ;
		}
		
		public static InstallStatus parse(int index){
			InstallStatus[] values = values();
			for (InstallStatus installerPackageEvent : values) {
				if(index == installerPackageEvent.getIndex()){
					return installerPackageEvent ;
				}
			}
			return null ;
		}
	}
	
	public static interface PackageInstallerCallback{
		//void onInstallerEvent(String packageName,long downloadId,InstallerPackageEvent event,int...errorReason);
		void onInstallerEvent(InstallPacket pack);
	}
	
	
	private static AppSilentInstaller INSTANCE ; 
	
	private AppSilentInstaller() {
		// TODO Auto-generated constructor stub
	}
	
	
	public synchronized static AppSilentInstaller getInstnce(){
		if(INSTANCE == null){
			INSTANCE = new AppSilentInstaller();
		}
		return INSTANCE ;
	}
	
	private boolean filter(String packageName){
		for (InstallPacket item : dataSet) {
			if(packageName.equals(item.getPackageName())){
				return false;
			}
		}
		return true ;
	}
	
	public Set<InstallPacket> getInstallDataSet() {
		return dataSet;
	}
	
	public void onDestory(){
		if(dataSet != null){
			dataSet.clear();
		}
		INSTANCE = null ;
	}
	
	public boolean getInstallPackage(String packageName) {
		for (InstallPacket p : dataSet) {
			if(packageName.equals(p.getPackageName())){
				return true ;
			}
		}
		return false ;
	}
	private InstallPacket formInstallPacket(DownloadItemOutput o){
		try {
			String mark = o.getAppData();
			PackageMark appData = PackageHelper.getAppData(mark);
			String packageName = appData.packageName ;
			String gameId = appData.gameId ;
			
			String dest = o.getDest();
			String filepath = Uri.parse(dest).getPath();
			long downloadId = o.getDownloadId() ;
			String downloadUrl = o.getUrl() ;
			String name = o.getTitle() ;
			
			InstallPacket installPacket = new InstallPacket(name,packageName, filepath, downloadId, gameId, downloadUrl);
			return installPacket ;
		} catch (Exception e) {
			Log.e(TAG, "formInstallPacket error",e);
			return null ;
		}
		
	}
	
	
	private boolean checkInstall(InstallPacket pack ,PackageInstallerCallback callback){
		if(!filter(pack.getPackageName())){
			if (Constants.DEBUG)Log.d(TAG, "[AppSilentInstaller#sendInstallRequest]"+"重复安装,不再继续:"+pack.getPackageName());
			return false;
		}
		
		pack.setCallback(callback);
		dataSet.add(pack);
		
		return true ;
	}

	public void sendInstallRequest(Context context,DownloadItemOutput o,PackageInstallerCallback callback){
		InstallPacket pack = formInstallPacket(o);
		if(pack == null){
			return ;
		}
		if(!checkInstall(pack, callback)){
			return ;
		}
		
		if (Constants.DEBUG)Log.d(TAG, "[AppSilentInstaller#sendInstallRequest]"+pack.getPackageName()+" dataSet:"+dataSet);
		for (InstallPacket item : dataSet) {
			if(!item.isStarted()){
				registerReceiver(context);
				
				item.setStarted(true);
				item.setStatus(InstallStatus.INSTALLING);
				
				notifyInstallStatus(item);
				startInstall(context, item);
			}
		}
	}
	
	
	/**
	 * Intent intent = new Intent(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_PACKAGE_ADDED_AUTO);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_PACKAGE_AUTO, packageName);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_FILE_AUTO, path);
		intent.putExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_STATUE_AUTO, installSuccess);
	 * @param intent
	 */
	private void notifyInstallStatus(Intent intent){
		boolean success = intent.getBooleanExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_STATUE_AUTO, false);
		String file = intent.getStringExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_FILE_AUTO);
		String packageName = intent.getStringExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_PACKAGE_AUTO);
		int errorReason = intent.getIntExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ERROR_REASON,0);
		long downloadId = intent.getLongExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ID_AUTO, -1);
		InstallPacket item = intent.getParcelableExtra(AutoInstallAppMonitorReceiver.AutoInstall.EXTRA_ITEM_AUTO);
		
		if(success){
			notifyInstallStatus(item);
		}else {
			//安装失败
			notifyInstallStatus(item);
		}
	}
	
	
/*	private void notifyInstallStatus(String packageName,long downloadId,InstallerPackageEvent event,int...errorReason){
		InstallPacket target = null ;
		for (InstallPacket item : dataSet) {
			if(item.getPackageName().equals(packageName)){
				target = item ;
				break;
			}
		}
		if(target == null){
			//Log.e(TAG, "Cannot find "+packageName +" in dataset:"+dataSet);
			return ;
		}
		PackageInstallerCallback callback = target.getCallback();
		if(callback == null){
			Log.e(TAG, "Cannot find callback for "+packageName);
			return ;
		}
		Log.e(TAG, "[AppInstaller#notifyInstallStatus] for "+packageName+" status:"+event);
		callback.onInstallerEvent(packageName,downloadId, event,errorReason);
		target.setStatus(event);
		if(errorReason != null && errorReason.length == 1){
			target.setErrorReason(errorReason[0]);
		}
		//安装出错不再继续
		if(event == InstallerPackageEvent.INSTALLED || event == InstallerPackageEvent.INSTALL_ERROR){
			//dataSet.remove(target);
		}
	}*/
	
	private InstallPacket findTarget(InstallPacket pack){
		InstallPacket target = null ;
		for (InstallPacket item : dataSet) {
			if(item.getPackageName() != null && item.getPackageName().equals(pack.getPackageName())){
				target = item ;
				break;
			}
			if(item.getGameId() != null && item.getGameId().equals(pack.getGameId())){
				target = item ;
				break;
			}
		}
		return target ;
	}
	
	private void removeOldPackIfNecessary(InstallPacket target){
		InstallStatus status = target.getStatus();
		//安装出错或者成功不再继续
		if(status == InstallStatus.INSTALLED || status == InstallStatus.INSTALL_ERROR ){
			dataSet.remove(target);
		}
	}
	
	private void notifyInstallStatus(InstallPacket pack){
		InstallPacket target = findTarget(pack) ;
		if(target == null){
			Log.e(TAG, "Cannot find "+pack.getPackageName() +" in dataset:"+dataSet);
			return ;
		}
		InstallStatus status = pack.getStatus();
		int errorReason = pack.getErrorReason();
		target.setStatus(status);
		target.setErrorReason(errorReason);
		
		removeOldPackIfNecessary(target);
		
				
		PackageInstallerCallback callback = target.getCallback();
		if(callback == null){
			Log.e(TAG, "Cannot find callback for "+target.getPackageName());
			return ;
		}
		callback.onInstallerEvent(target);
		
	}
	
	/**
	 * 监听SilentInstallService的结果
	 * @author wangliang
	 *
	 */
	class MyAutoInstallAppMonitorReceiver extends AutoInstallAppMonitorReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//super.onReceive(context, intent);
			String action = intent.getAction();
			if (Constants.DEBUG)Log.i(AppSilentInstaller.TAG, "MyAutoInstallAppMonitorReceiver receive broadcast,action:"+action+" data:"+intent.getData()+" intent:"+intent);
			if (action != null && action
					.equals(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_PACKAGE_ADDED_AUTO)) {
				//安装结束
				notifyInstallStatus(intent);
			} else if (action.equals(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_INSTALL_SERVICE_FINISHED)) {
				//service结束，不再监听
				unregisterReceiver(context);
			}
		}
	}
	private AutoInstallAppMonitorReceiver receiver ;
	private void registerReceiver(Context context){
		if(receiver == null){
			receiver = new MyAutoInstallAppMonitorReceiver();
			IntentFilter filter = new IntentFilter(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_PACKAGE_ADDED_AUTO);
			filter.addAction(AutoInstallAppMonitorReceiver.AutoInstall.ACTION_INSTALL_SERVICE_FINISHED);
			context.registerReceiver(receiver, filter);
		}
	}
	private void unregisterReceiver(Context context){
		if(receiver != null){
			context.unregisterReceiver(receiver);
			receiver = null ;
		}
	}
	
	/**
	 * 开始安装
	 * @param context
	 * @param item
	 */
	private void startInstall(Context context,InstallPacket item){
		if (Constants.DEBUG)Log.d(TAG, "[AppInstaller#startInstall] startService for "+item);
		Intent intent = new Intent(context,SilentInstallService.class);
		//intent.setClass(context, SilentInstallService.class);
		intent.putExtra(SilentInstallService.EXTRA_INSTALLER_PACK,(Parcelable)item);
		context.startService(intent);
	}

}
