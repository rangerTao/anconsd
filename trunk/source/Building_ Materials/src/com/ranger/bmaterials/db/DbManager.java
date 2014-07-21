package com.ranger.bmaterials.db;

import com.ranger.bmaterials.app.GameTingApplication;

import android.content.Context;
import android.util.Log;

public class DbManager {
	private static final Object mInstanceSync = new Object();
	private	static DbManager mInstance;
	private Context mAppContext; 
	DaoFactory mDaoFactory;
	
	private DbManager(){
		mDaoFactory = new DaoFactory();
		Log.d("GameTingApplication","DbManager constructor "+System.currentTimeMillis() +" context:"+GameTingApplication.getAppInstance());
		mAppContext = GameTingApplication.getAppInstance().getApplicationContext();
	}
	
	private static DbManager _getInstance(){
		
		synchronized(mInstanceSync){
			
			if(mInstance == null){
				mInstance = new DbManager();
			}
		}
		return mInstance;
	}
	
	private IImageCacheDao _getImageCacheDbHandler(){
        return mDaoFactory.getImageCacheDao(mAppContext);
    }
	private AppDao _getAppDbHandler(){
		return mDaoFactory.getAppDao(mAppContext);
	}
	private CommonDao _getSearchDbHandler(){
		return mDaoFactory.getSearchDao(mAppContext);
	}
	
	public static IImageCacheDao getImageCacheDbHandler(){
		return _getInstance()._getImageCacheDbHandler();
	}
	public static AppDao getAppDbHandler(){
		return _getInstance()._getAppDbHandler();
	}
	public static CommonDao getCommonDbHandler(){
		return _getInstance()._getSearchDbHandler();
	}
}
