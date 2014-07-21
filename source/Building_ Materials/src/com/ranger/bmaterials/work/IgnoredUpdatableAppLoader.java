package com.ranger.bmaterials.work;

import java.util.List;

import android.content.Context;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.mode.UpdatableAppInfo;

public class IgnoredUpdatableAppLoader extends UpdatableAppLoader {
	public IgnoredUpdatableAppLoader(Context context) {
		super(context);
	}
	 
	@Override
	public List<UpdatableAppInfo> loadData() {
		AppManager manager = AppManager.getInstance(getContext());
		return manager.getIgnoredGames(true);
	}
	@Override
	public boolean isPackageIntentReceiver() {
		return true;
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
