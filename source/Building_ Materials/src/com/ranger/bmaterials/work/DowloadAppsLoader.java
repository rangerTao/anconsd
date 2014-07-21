package com.ranger.bmaterials.work;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadListener;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;

public class DowloadAppsLoader extends AbstractListLoader<DownloadAppInfo> /*implements DownloadListener*/ {
	boolean started = false ;
	public DowloadAppsLoader(Context context) {
		super(context);
	}
	
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
	}
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
	}
	@Override
	public void onCanceled(List<DownloadAppInfo> apps) {
		super.onCanceled(apps);
	}
	 protected void onReceveAppEvent(boolean addOrRemove){
	    	//mLoader.onContentChanged();   
		 if(DEBUG)Log.i(AppSilentInstaller.TAG, "DowloadAppsLoader onReceveAppEvent "+addOrRemove);
		 //onContentChanged(); 
	    /*if(addOrRemove)*/forceLoad();
	 }
	 
	 @Override
	protected void onInstallChanged() {
		 if(DEBUG)Log.e(AppSilentInstaller.TAG, "onInstallChanged forceLoad");
		 forceLoad();
	}
	 
	
	@Override
	public List<DownloadAppInfo> loadData() {
		
		AppManager manager = AppManager.getInstance(getContext());
		//List<DownloadAppInfo> apps = manager.getDownloadGames(false);
		List<DownloadAppInfo> apps = null ;
		apps = manager.getAndCheckDownloadGames();
		if(apps != null && apps.size() > 0){
			checkStatus(apps);
		}
		return apps;
	}

	private void checkStatus(List<DownloadAppInfo> apps) {
		int size = apps.size();
		List<String> ids = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			ids.add(apps.get(i).getGameId());
		}
		
		Map<String, PackageMode> statusMap = PackageHelper.queryPackageStatusForDownloads(apps);
		for (DownloadAppInfo app : apps) {
			PackageMode packageMode = statusMap.get(app.getGameId());
			app.setApkStatus(packageMode.status);
			app.setApkReason(packageMode.reason);
			//app.setCurrtentSize(packageMode.currentSize);
			if(DEBUG){
				Log.d("DowloadAppsLoader", "--------------------------------------");
				Log.d("DowloadAppsLoader", String.format("DowloadAppsLoader status %s for %s",PackageMode.getStatusString(packageMode.status),app.getName()));
				Log.d("DowloadAppsLoader", "DowloadAppsLoader packageMode:"+packageMode);
				Log.d("DowloadAppsLoader", "DowloadAppsLoader app:"+app);
			}
			
		}
	}
	
	private void log(List<DownloadAppInfo> apps){
		if(apps == null){
			if(DEBUG)Log.i("DowloadAppsLoadertest", "DowloadAppsLoader load data null");
		}
		for (DownloadAppInfo downloadAppInfo : apps) {
			if(DEBUG)Log.i("DowloadAppsLoadertest", "[DowloadAppsLoader]"+downloadAppInfo.getPackageName()+";"+downloadAppInfo.getName()+";"+downloadAppInfo.isMarkDeleted());
		}
	}
	@Override
	public boolean isPackageIntentReceiver() {
		return true;
	}
	
	protected void onReceveAppEvent(){
	
	}
	@Override
	protected void onDownloadChanged(boolean downloadOrOtherWise) {
		super.onDownloadChanged(downloadOrOtherWise);
		if(downloadOrOtherWise){
			if(DEBUG)Log.e("DownloadLog", "Loader onDownloadChanged");
	   		forceLoad();
			   
		}
	}
   
    private void checkUpdate() {
    	AppDao appDbHandler = DbManager.getAppDbHandler();
    }
    
    
    private void parseData(InputStream in){
    	String content = StringUtil.InputStreamToString(in,true);
        
    
    }
    	
    private void doCheckJob(){
    	/*byte[] postconent = buildPostContent();
        
        String deviceinfo = null ;
        String passid = null;
        StringBuilder sb = new StringBuilder();
        sb.append("url").append("&deviceinfo=").append(deviceinfo);
        String url = sb.toString();*/
    }
    
    private byte[] buildPostContent() {
		/*List<InstalledAppInfo> loadInstalledApps = loadInstalledApps();
		JSONArray jsnarray = new JSONArray();
		for (InstalledAppInfo ai : loadInstalledApps) {
			gzipvalue = AppUtils.gZip(value);
		}

		return gzipvalue;*/
		return null ;
	}

	/*@Override
	public void onDownloadProcessing(List<DownloadItemOutput> items) {
		Log.d("DownloadLog", "onDownloadProcessing "+((items != null)?items.size():0));
		forceLoad();
	}*/




}
