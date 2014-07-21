package com.ranger.bmaterials.work;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.mode.InstalledAppInfo;

public class InstalledAppListLoader extends AbstractListLoader<InstalledAppInfo> {
    
    private static final String TAG = "InstalledAppListLoader";
	private final PackageManager mPm;

    public InstalledAppListLoader(Context context) {
        super(context);
        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        mPm = getContext().getPackageManager();
    }

	/**
     */
    @Override
    public boolean isPackageIntentReceiver() {
        return true;
    }
    protected void onReceveAppEvent(boolean addOrRemove){
    	//mLoader.onContentChanged();   
    	forceLoad();
    }
    @Override
    public List<InstalledAppInfo> loadData() {
    	AppManager manager = AppManager.getInstance(getContext());
    	/*List<InstalledAppInfo> installedGames = manager.getInstalledGames();
    	if(installedGames == null || installedGames.size() == 0){
    		checkInstalledGames();
    		installedGames = manager.getInstalledGames();
    	}
        return installedGames;*/
    	
    	return manager.getInstalledGames(); 
    }
    @Override
    protected void onInstalledListInitialized() {
    	super.onInstalledListInitialized();
    	forceLoad();
    }
}