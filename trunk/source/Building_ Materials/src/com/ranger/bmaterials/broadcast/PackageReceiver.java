package com.ranger.bmaterials.broadcast;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.download.DownloadInfo;
import com.ranger.bmaterials.download.Downloads;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.PackageMark;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.utils.NetUtil;

public class PackageReceiver extends BroadcastReceiver {
	private static final String TAG = "PackageReceiver";
	private Context context;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		new Thread(){
			@Override
			public void run() {
				try {
					PackageReceiver.this.context = context;
					String action = intent.getAction();
					if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_COMPLETE)) {
						onDownloadCompleted(intent);
					} else if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_PAUSE)) {
						onDownloadPaused(intent);
					} else if (action
							.equals(BroadcaseSender.ACTION_DOWNLOAD_PAUSE_BY_USER)) {
						onDownloadPausedByUser(intent);
					} else if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_START)) {
						onDownloadStart(intent);
					} else if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_CANCLE)) {
						onDownloadCancled(intent);
					} else if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_RUNNING)) {
						onDownloading(intent);
					}

					if (com.ranger.bmaterials.app.Constants.DEBUG)
						Log.v("MyReceiver", "Received broadcast intent for " + action);

				} catch (Exception e) {
				}
			}
		}.start();
		
	}
	
	private PackageMode parsePackageMode(Intent intent){
		try {
			String url = intent.getStringExtra(DownloadInfo.EXTRA_URL);
			String dest = intent.getStringExtra(DownloadInfo.EXTRA_DEST);
			try {
				if(dest != null){
					dest = Uri.parse(dest).getPath();
				}
			} catch (Exception e) {
			}
			
			String title = intent.getStringExtra(DownloadInfo.EXTRA_TITLE);
			long downloadId = intent.getLongExtra(DownloadInfo.EXTRA_ID, -1);
			String markData = intent.getStringExtra(DownloadInfo.EXTRA_MARK);
			PackageMark mark = PackageHelper.getAppData(markData);

			String gameId = mark.gameId;
			String downloadUrl = url;
			String packageName = mark.packageName;
			String version = mark.version;
			int versionCode = mark.versionCode;
			
			int status = PackageMode.DEFAULT_STATUS ;
			Integer reason = null ;
			long currentSize = 0L ;
			long totalSize = -1L ;
			//String sign = null ;
			//String fileMd5 = null ;
			boolean isDiffUpdate = mark.isDiffUpdate ;

			PackageMode mode = new PackageMode(gameId, downloadUrl, packageName, version, versionCode,
					downloadId, dest, title, status, reason, currentSize, totalSize/*, sign, fileMd5*/,isDiffUpdate);
			if(intent.hasExtra(DownloadInfo.EXTRA_CURRENT_SIZE) && intent.hasExtra(DownloadInfo.EXTRA_TOTAL_SIZE)){
				mode.currentSize = intent.getLongExtra(DownloadInfo.EXTRA_CURRENT_SIZE, 0);
				mode.totalSize = intent.getLongExtra(DownloadInfo.EXTRA_TOTAL_SIZE, -1);
			}

			return mode ;
		} catch (Exception e) {
			Log.e(TAG, "parsePackageMode error",e);
			return null ;
			
		}
	}
	
	
	/**
	 * 正在下载
	 * @param intent
	 */
	private void onDownloading(Intent intent) {
		PackageMode packageMode = parsePackageMode(intent);
		if(packageMode == null){
			return ;
		}
		packageMode.status = PackageMode.DOWNLOAD_RUNNING;
		packageMode.reason = null;
		
		long currentSize = intent.getLongExtra(DownloadInfo.EXTRA_CURRENT_SIZE, -1);
		long totalSize = intent.getLongExtra(DownloadInfo.EXTRA_TOTAL_SIZE, -1);
		packageMode.currentSize = currentSize ;
		packageMode.totalSize = totalSize ;
		
		PackageHelper.notifyPackageStatusChanged(packageMode);
	}

	
	
	/**
	 * 取消下载
	 * @param intent
	 */
	private void onDownloadCancled(Intent intent) {
		PackageMode mode = parsePackageMode(intent);
		
		QueryInput queryInput = new QueryInput(mode.packageName, mode.version, mode.versionCode, mode.downloadUrl, mode.gameId);
		Map<QueryInput, PackageMode> queryPackageStatus = PackageHelper.queryPackageStatus(queryInput);
		PackageMode packageMode = queryPackageStatus.get(queryInput);
		PackageHelper.notifyPackageStatusChanged(packageMode);
		
		Log.d("PackageHelper", String.format("onDownloadCancled  game %S ,status is  %s.",mode.title,PackageMode.getStatusString(packageMode.status)));
		
		
	}
	
	/**
	 * 下载开始
	 * @param intent
	 */
	private void onDownloadStart(Intent intent) {
		PackageMode mode = parsePackageMode(intent);
		mode.status = PackageMode.DOWNLOAD_RUNNING;
		com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput out = PackageHelper.formDownloadOut(
				mode);
		out.setStatus(DownloadStatus.STATUS_RUNNING);
		Notifier.showDownloadStartNotification(out);
	}

	/**
	 * 手动暂停
	 * @param intent
	 */
	private void onDownloadPausedByUser(Intent intent) {
		PackageMode mode = parsePackageMode(intent);
		mode.status = PackageMode.DOWNLOAD_PAUSED;
		mode.reason = PackageMode.PAUSED_BY_APP ;
		PackageHelper.notifyPackageStatusChanged(mode);
		
		Notifier.updateNotificationForDownload();
	}

	/**
	 * 下载暂停（非手动暂停）
	 * @param intent
	 */
	private void onDownloadPaused(Intent intent) {
		PackageMode mode = parsePackageMode(intent);
		mode.status = PackageMode.DOWNLOAD_PAUSED;
		int reason = intent.getIntExtra(DownloadInfo.EXTRA_REASON, -1);
		int finalPauseReason = PackageHelper.getFinalPauseReason(reason);
		mode.reason = finalPauseReason ;
		PackageHelper.notifyPackageStatusChanged(mode);
		
		Notifier.updateNotificationForDownload();
		
	}
	


	/**
	 * @param intent
	 */
	private void onDownloadCompleted(Intent intent) {
		PackageMode packageMode = parsePackageMode(intent);
		if(packageMode == null){
			return ;
		}
		boolean successful = intent.getBooleanExtra(DownloadInfo.EXTRA_SUCCESSFUL, false);
		if(successful){
			packageMode.status = PackageMode.DOWNLOADED;
			packageMode.reason = null;
			DownloadStatistics.addDownloadGameSucceedStatistics(GameTingApplication.getAppInstance(), packageMode.title);
			NetUtil.getInstance().requestFinishDownloadGame(packageMode.gameId, packageMode.title, null);
		}else{
			packageMode.status = PackageMode.DOWNLOAD_FAILED;
			int reason = intent.getIntExtra(DownloadInfo.EXTRA_REASON, -1);
			packageMode.reason = PackageHelper.getFinalFailReason(reason);
			PackageHelper.notifyPackageStatusChanged(packageMode);
			
			DownloadStatistics.addDownloadGameFailedStatistics(GameTingApplication.getAppInstance(), packageMode.title);
			if(isCausedByNet(packageMode.reason)){
				DownloadStatistics.addDownloadGameFailedNetStatistics(GameTingApplication.getAppInstance(), packageMode.title);
			}
		}
		if(successful){
			String markData = intent.getStringExtra(DownloadInfo.EXTRA_MARK);
			PackageMark mark = PackageHelper.getAppData(markData);
			if(mark.isDiffUpdate){
				//TODO
				packageMode.status = PackageMode.DOWNLOADED ;
				PackageHelper.notifyPackageStatusChanged(packageMode);
				PackageHelper.sendMergeRequest(packageMode,false);
				
			}else{
				//packageMode.apkSign = PackageHelper.getApkSignMd5(packageMode.downloadDest);
				com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput out = PackageHelper.formDownloadOut(
						packageMode);
				out.setStatus(DownloadStatus.STATUS_SUCCESSFUL);
				
				PackageHelper.checkAndNotifyForDownloadedGame(false, packageMode, out);
//				checkAndNotifyForDownloadedGame(packageMode,out);
				
			}
			
		}else {
			com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput out = PackageHelper.formDownloadOut(
					packageMode);
			out.setStatus(DownloadStatus.STATUS_FAILED);
			
			PackageHelper.checkAndNotifyForDownloadedGame(false, packageMode, out);
			Notifier.showDownloadFailedNotification(out);
		}
	}
	
/*	private void checkAndNotifyForDownloadedGame(PackageMode mode,DownloadItemOutput out) {
		boolean notifyForDifferentSign = false ;
		BackAppListener listener = BackAppListener.getInstance();
		boolean valid = listener.checkApkValid(out);
		if(!valid){
			notifyForDifferentSign =  false;
		}
		if(valid){
			PackageInfo newPack = listener.checkApkIdentical(out,false);
			if(newPack == null){
				notifyForDifferentSign = false;
			}else {
				boolean identical = listener.checkExistsSignature(out,newPack);
				if(!identical){
					notifyForDifferentSign = true ;;
				}
			}
		}
		if(notifyForDifferentSign){
			PackageHelper.notifyUninstallOld(out);
		}else{
			PackageHelper.notifyPackageStatusChanged(mode);
			listener.notifyAndSubmitForNormal(out);
		}
	}*/
	
	public static boolean isCausedByNet(int status) {
		 if ((400 <= status && status < Downloads.Impl.MIN_ARTIFICIAL_ERROR_STATUS) // SUPPRESS CHECKSTYLE
               || (500 <= status && status < 600)) { // SUPPRESS CHECKSTYLE
           return true;
       }
       switch (status) {
           case Downloads.STATUS_FILE_ERROR:
               return false;

           case Downloads.STATUS_UNHANDLED_HTTP_CODE:
           case Downloads.STATUS_UNHANDLED_REDIRECT:
          	 return true;

           case Downloads.STATUS_HTTP_DATA_ERROR:
           case Downloads.STATUS_TOO_MANY_REDIRECTS:

           case Downloads.STATUS_INSUFFICIENT_SPACE_ERROR:
               return false ;

           case Downloads.STATUS_DEVICE_NOT_FOUND_ERROR:
           	 return false ;

           case Downloads.Impl.STATUS_CANNOT_RESUME:
          	 	return true ;

           case Downloads.Impl.STATUS_FILE_ALREADY_EXISTS_ERROR:
               return false ;
         
           default:
               return true ;
       }

	}

}
