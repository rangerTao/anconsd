package com.ranger.bmaterials.download;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public class DownloadHelper {
	
	   
    public static interface DownloadProgressCallback{
//    	intent.putExtra(EXTRA_URL, mUri);
//		intent.putExtra(EXTRA_ID, mId);
//		intent.putExtra(EXTRA_MARK, mMarkData);
//		intent.putExtra(EXTRA_DEST, mHint);
//		if(TextUtils.isEmpty(mTitle)){
//			intent.putExtra(EXTRA_TITLE,mDescription);
//		}else{
//			intent.putExtra(EXTRA_TITLE,mTitle);
//		}
//    	intent.putExtra(EXTRA_TOTAL_SIZE, mTotalBytes);
//		intent.putExtra(EXTRA_CURRENT_SIZE, mCurrentBytes);
    	
    	void onDownloading(long downloadId,String downloadUrl,String saveDest,String title,String appData,long currentSize,long totalSize);
    }
    static CopyOnWriteArraySet<DownloadProgressCallback> callbacks = new CopyOnWriteArraySet<DownloadProgressCallback>();
    public synchronized static  void addDownloadListener(DownloadProgressCallback callback){
    	if(callback != null){
    		callbacks.add(callback);
    	}
    }
    
    public synchronized static void removeDownloadListener(DownloadProgressCallback callback){
    	callbacks.remove(callback);
    }
    
    public synchronized static void removeAllDownloadListener(){
    	if(callbacks != null){
    		callbacks.clear();
    	}
    }
    
    public static void notifyDownloadProgress(long downloadId,String downloadUrl,String saveDest,String title,String appData,long currentSize,long totalSize){
    	if(callbacks.size() > 0){
    		for(DownloadProgressCallback c :callbacks){
    			if(c != null){
    				c.onDownloading(downloadId, downloadUrl, saveDest, title, appData, currentSize, totalSize);
    			}
    		}
    	}
    }
    

}
