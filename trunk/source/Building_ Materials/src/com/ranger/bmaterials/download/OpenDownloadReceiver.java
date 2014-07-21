package com.ranger.bmaterials.download;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.logging.Logger;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.ranger.bmaterials.download.DownloadConfiguration.OnNotifierClickListener;
import com.ranger.bmaterials.tools.MyLogger;

/*import com.ranger.bmaterials.R;
import com.duoku.gamesearch.activity.MainActivity;
import com.duoku.gamesearch.app.StringConstants;*/

/**
 * This {@link BroadcastReceiver} handles {@link Intent}s to open and delete
 * files downloaded by the Browser.
 */
public class OpenDownloadReceiver extends BroadcastReceiver {
	 private void hideNotification(Context context, long downloadId) {
	            
		 
		new RealSystemFacade(context).cancelNotification(downloadId);
		Uri uri = ContentUris.withAppendedId(
				Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, downloadId);
		/*ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_VISIBILITY,
				Downloads.Impl.VISIBILITY_VISIBLE);
		context.getContentResolver().update(uri, values, null, null);*/
	    }

	 
    @Override
    public void onReceive(Context context, Intent intent) {
    	//Log.i("0000","the onreciver is in    ");
        ContentResolver cr = context.getContentResolver();
        Uri data = intent.getData();
        Cursor cursor = null;
        try {
            cursor = cr.query(data,
                    new String[] { Downloads.Impl._ID, Downloads.Impl.DATA,
                    Downloads.Impl.COLUMN_MIME_TYPE, Downloads.COLUMN_STATUS, Downloads.COLUMN_NOTIFICATION_EXTRAS },
                    null, null, null);
            if (cursor.moveToFirst()) {
                String filename = cursor.getString(
                        cursor.getColumnIndex(Downloads.Impl.DATA));
                String mimetype = cursor.getString(
                        cursor.getColumnIndex(Downloads.Impl.COLUMN_MIME_TYPE));
                String action = intent.getAction();
                if (Downloads.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                	
                	
                	/*DownloadConfiguration.DownloadItem item = new DownloadConfiguration.DownloadItem();
                	item.setId(Long.parseLong(data.getPathSegments().get(1)));
                	item.setFileName(filename);
                	item.setMimeType(mimetype);*/
                    int status = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
                    int downloadId = cursor.getInt(cursor.getColumnIndex(Downloads.Impl._ID));
                    
                    hideNotification(context, downloadId);
                    
                    DownloadConfiguration config = DownloadConfiguration.getInstance(context);
                    boolean autoOpenDownloadedFile = config.isOpenOnClickSuccessfulDownload();
                    MyLogger logger = MyLogger.getLogger(this.getClass().getSimpleName());
                    logger.i("OpenDownloadReceiver ACTION_NOTIFICATION_CLICKED "+status+" autoOpenDownloadedFile "+autoOpenDownloadedFile);
                	OnNotifierClickListener onNotifierClickListener = config.getOnNotifierClickListener();
                    if (Downloads.isStatusCompleted(status)
                            && Downloads.isStatusSuccess(status)) {
                    	
                    	
                    	if(autoOpenDownloadedFile){
                    		Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                            Uri path = Uri.parse(filename);
                            // If there is no scheme, then it must be a file
                            if (path.getScheme() == null) {
                                path = Uri.fromFile(new File(filename));
                            }
                            launchIntent.setDataAndType(path, mimetype);
                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                context.startActivity(launchIntent);
                            } catch (ActivityNotFoundException ex) {
                            	Log.e("OpenDownloadReceiver", "ActivityNotFoundException",ex);
                                /*Toast.makeText(context,
                                        R.string.download_no_application_title,
                                        Toast.LENGTH_LONG).show();*/
                            }
                    	}
                    	if(onNotifierClickListener != null){
                    		//onNotifierClickListener.onDownloadCompleted(true,downloadId);
                    	}
                     //ֻ�з��Զ����ԲŻص�����
                    } else if(Downloads.isStatusCompleted(status) && Downloads.isStatusError(status)){
                    	if(onNotifierClickListener != null){
                    		onNotifierClickListener.onDownloadCompleted(false,downloadId);
                    	}
                        //���������ҳ
                       /* String extras = cursor.getString(
                                cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS));
                        if (extras != null) {*/
                        	/*Intent openIntent = new Intent();
                        	openIntent.setAction(StringConstants.INTENT_ACTION_MANAGER_PAGE_FROM_NOTIFICATION);
                        	openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        	context.startActivity(openIntent);*/
                        	
                        /*}*/
                        
                    }else if(status == Downloads.Impl.STATUS_SUCCESS){
                    	if(onNotifierClickListener != null){
                    		onNotifierClickListener.onDownloadRunning(downloadId);
                    	}
                    }else {
                    	if(onNotifierClickListener != null){
                    		onNotifierClickListener.onDownloadPaused(downloadId);
                    	}
                    }
                } else if (Intent.ACTION_DELETE.equals(action)) {
                    if (deleteFile(cr, filename, mimetype)) {
                        cr.delete(data, null, null);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Remove the file from the SD card.
     * @param cr ContentResolver used to delete the file.
     * @param filename Name of the file to delete.
     * @param mimetype Mimetype of the file to delete.
     * @return boolean True on success, false on failure.
     */
    private boolean deleteFile(ContentResolver cr, String filename,
            String mimetype) {
        Uri uri;
        if (mimetype.startsWith("image")) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (mimetype.startsWith("audio")) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else if (mimetype.startsWith("video")) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = null;
        }
        return (uri != null && cr.delete(uri, MediaStore.MediaColumns.DATA
                + " = " + DatabaseUtils.sqlEscapeString(filename), null) > 0)
                || new File(filename).delete();
    }
}
