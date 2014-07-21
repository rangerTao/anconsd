package com.ranger.bmaterials.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.UpdatableAppInfo;

public class UpdatableAppLoader extends AbstractListLoader<UpdatableAppInfo> {
	public UpdatableAppLoader(Context context) {
		super(context);
	}
	 
	@Override
	public List<UpdatableAppInfo> loadData() {
		AppManager manager = AppManager.getInstance(getContext());
		List<UpdatableAppInfo> apps = manager.getAllUpdatableGames();
		
		if(apps != null && apps.size() > 0){
			checkStatus(apps);
		}
		return apps;
	}
	private void checkStatus(List<UpdatableAppInfo> apps) {
		int size = apps.size();
		List<String> ids = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			ids.add(apps.get(i).getGameId());
		}
		
		Map<String, PackageMode> statusMap = PackageHelper.queryPackageStatusForUpdates(apps);
		for (UpdatableAppInfo app : apps) {
			PackageMode packageMode = statusMap.get(app.getGameId());
			app.setApkStatus(packageMode.status);
			app.setApkReason(packageMode.reason);
			
			app.setDownloadId(packageMode.downloadId);
			app.setSaveDest(packageMode.downloadDest);
			app.setCurrtentSize(packageMode.currentSize);
			app.setTotalSize(packageMode.totalSize);
			
			if(DEBUG){
				Log.d("UpdatableAppLoader", "--------------------------------------");
				Log.d("UpdatableAppLoader", String.format("UpdatableAppLoader status %s for %s",PackageMode.getStatusString(packageMode.status),app.getName()));
				Log.d("UpdatableAppLoader", "UpdatableAppLoader packageMode:"+packageMode);
				Log.d("UpdatableAppLoader", "UpdatableAppLoader app:"+app);
			}
			
		}
		
	}

	@Override
	public boolean isPackageIntentReceiver() {
		return true;
	}
	protected void onReceveAppEvent(boolean addOrRemove){
	    	//mLoader.onContentChanged();   
	    /*if(!addOrRemove)*/forceLoad();
	}
	@Override
	protected void onDownloadChanged(boolean downloadOrOtherWise) {
		super.onDownloadChanged(downloadOrOtherWise);
		if(!downloadOrOtherWise){
			forceLoad();
		}
	}

	protected void onIgnoredStatusChanged(boolean ignored,
			String... packageNames) {
		if (Constants.DEBUG)Log.i("UpdatableAppListFragment","OnAppStatusChangedListener onIgnoredStatusChanged");
		//if (!ignored) {
			// getLoaderManager().restartLoader(0,null,
			// UpdatableAppListFragment.this);
			// handler.sendEmptyMessage(100);
			forceLoad();
		//}
	}

/*	private boolean checkUpdatableList(){
		AppMananager manager = AppMananager.getInstance(getContext());
		Long updateTimeForUpdatableList = manager.getUpdateTimeForUpdatableList();
		if(updateTimeForUpdatableList == null || updateTimeForUpdatableList < 0){
			return false ;
		}
		boolean updatableListInited = manager.isUpdatableListInited();
		if(!updatableListInited){
			manager.saveUpdateTimeForWhiteList(-1L);
		}
		return updatableListInited ;
	}*/
	

	@Override
	protected void onUpdatableListInitialized() {
		super.onUpdatableListInitialized();
		forceLoad();
	}


}
