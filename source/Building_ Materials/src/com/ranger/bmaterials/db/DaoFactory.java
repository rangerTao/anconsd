package com.ranger.bmaterials.db;

import android.content.Context;

public class DaoFactory {
	private ImageCacheDao mImageCacheDbHandler;
	private AppDao appDbHandler;
	private CommonDao searchDbHandler;
	
	DaoFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public IImageCacheDao getImageCacheDao(Context context){
		
		synchronized (this) {
			if (mImageCacheDbHandler == null){
				mImageCacheDbHandler = new ImageCacheDao(context);
			}
		}
		return mImageCacheDbHandler;
	}
	
	public AppDao getAppDao(Context context){
		
		synchronized (this) {
			if (appDbHandler == null){
				appDbHandler = new AppDaoImpl(context);
			}
		}
		return appDbHandler;
	}
	public CommonDao getSearchDao(Context context){
		
		synchronized (this) {
			if (searchDbHandler == null){
				searchDbHandler = new CommonDaoImpl(context);
			}
		}
		return searchDbHandler;
	}
	
	public void onDestroy() {

	}
}
