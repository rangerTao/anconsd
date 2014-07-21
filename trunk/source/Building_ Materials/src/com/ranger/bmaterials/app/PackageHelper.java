package com.ranger.bmaterials.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants.CancelReason;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.broadcast.Notifier;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.diff.DiffManager;
import com.ranger.bmaterials.download.DownloadHelper;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.Downloads;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadReason;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.download.DownloadHelper.DownloadProgressCallback;
import com.ranger.bmaterials.mode.DiffInfo;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MergeMode;
import com.ranger.bmaterials.mode.PackageMark;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.FileHelper;
import com.ranger.bmaterials.tools.PinyinUtil;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;
import com.ranger.bmaterials.tools.install.BackAppListener;
import com.ranger.bmaterials.tools.install.InstallPacket;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.view.DuokuDialog;

public class PackageHelper {

	public static boolean DEBUG = true;
	public static String DEBUG_TAG = PackageHelper.class.getSimpleName();

	private static String DEFAUL_TDOWNLOAD_DEST = null;

	private static DownloadProgressCallback callback;

	static class OnDownloadProgressCallback implements DownloadProgressCallback {

		private PackageMode parse(long downloadId, String downloadUrl, String saveDest, String title, String appData, long currentSize, long totalSize) {
			PackageMark mark = PackageHelper.getAppData(appData);

			String gameId = mark.gameId;
			String packageName = mark.packageName;
			String version = mark.version;
			int versionCode = mark.versionCode;
			boolean isDiffUpdate = mark.isDiffUpdate;

			int status = PackageMode.DOWNLOAD_RUNNING;

			Integer reason = null;
			// String sign = null ;
			// String fileMd5 = null ;

			PackageMode mode = new PackageMode(gameId, downloadUrl, packageName, version, versionCode, downloadId, saveDest, title, status, reason, currentSize, totalSize,/*
																																											 * sign
																																											 * ,
																																											 * fileMd5
																																											 * ,
																																											 */isDiffUpdate);
			mode.currentSize = currentSize;
			mode.totalSize = totalSize;

			return mode;
		}

		@Override
		public void onDownloading(long downloadId, String downloadUrl, String saveDest, String title, String appData, long currentSize, long totalSize) {
			PackageMode mode = parse(downloadId, downloadUrl, saveDest, title, appData, currentSize, totalSize);
			PackageHelper.notifyPackageStatusChanged(mode);

		}

	}

	public synchronized static void addDownloadProgressListener() {
		if (callback == null) {
			callback = new OnDownloadProgressCallback();
			DownloadHelper.addDownloadListener(callback);
		} else {
			DownloadHelper.addDownloadListener(callback);
		}
	}

	public synchronized static void removeDownloadProgressListener() {
		if (callback != null) {
			callback = null;
		}
		DownloadHelper.removeAllDownloadListener();
	}

	// //////////////////////////////////////////////////////////////////////////////////

	public static int getFinalInstallErrorReason(int reason) {
		switch (reason) {
		case PackageMode.INSTALL_FAILED_ALREADY_EXISTS:
		case PackageMode.INSTALL_FAILED_DUPLICATE_PACKAGE:
		case PackageMode.INSTALL_FAILED_INSUFFICIENT_STORAGE:
		case PackageMode.INSTALL_FAILED_OLDER_SDK:
		case PackageMode.INSTALL_FAILED_MISSING_FEATURE:
		case PackageMode.INSTALL_FAILED_PERMISSION:
			return reason;
		default:
			return PackageMode.INSTALL_FAILED_OTHER;
		}
	}

	public static int getFinalPauseReason(int status) {
		if (status == 0) {
			Log.e(DEBUG_TAG, "getFinalPauseReason error ,reason is 0!");
			return PackageMode.PAUSED_UNKNOWN;
		}
		switch (status) {
		case Downloads.Impl.STATUS_WAITING_TO_RETRY:
			return PackageMode.PAUSED_WAITING_TO_RETRY;
		case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
			return PackageMode.PAUSED_WAITING_FOR_NETWORK;
		case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
			return PackageMode.PAUSED_QUEUED_FOR_WIFI;
		case Downloads.Impl.STATUS_PAUSED_BY_APP:
			return PackageMode.PAUSED_BY_APP;

		default:
			return PackageMode.PAUSED_UNKNOWN;
		}
	}

	public static int getFinalPauseReason(DownloadReason reason) {
		if (reason == null) {
			Log.e(DEBUG_TAG, "getFinalPauseReason error ,reason is null!");
			return PackageMode.PAUSED_UNKNOWN;
		}
		switch (reason) {
		case PAUSED_BY_APP:
			return PackageMode.PAUSED_BY_APP;
		case PAUSED_QUEUED_FOR_WIFI:
			return PackageMode.PAUSED_QUEUED_FOR_WIFI;
		case PAUSED_WAITING_FOR_NETWORK:
			return PackageMode.PAUSED_WAITING_FOR_NETWORK;
		case PAUSED_WAITING_TO_RETRY:
			return PackageMode.PAUSED_WAITING_TO_RETRY;
		default:
			return PackageMode.PAUSED_UNKNOWN;
		}
	}

	public static int getFinalFailReason(DownloadReason reason) {
		if (reason == null) {
			Log.e(DEBUG_TAG, "getFinalFailReason error ,reason is null!");
			return PackageMode.ERROR_UNKNOWN;
		}
		switch (reason) {
		case ERROR_HTTP_CANNOT_RUSUME:
		case ERROR_HTTP_ERROR:
		case ERROR_HTTP_UNKNOWN:
			return PackageMode.ERROR_HTTP_ERROR;
		case ERROR_FILE_ERROR:
		case ERROR_FILE_ALREADY_EXISTS:
			return PackageMode.ERROR_FILE_ERROR;
		case ERROR_INSUFFICIENT_SPACE:
			return PackageMode.ERROR_INSUFFICIENT_SPACE;
		case ERROR_DEVICE_NOT_FOUND:
			return PackageMode.ERROR_DEVICE_NOT_FOUND;
		case ERROR_UNKNOWN:
			return PackageMode.ERROR_UNKNOWN;
		default:
			return PackageMode.ERROR_UNKNOWN;
		}
	}

	public static int getFinalFailReason(int status) {
		if ((400 <= status && status < Downloads.Impl.MIN_ARTIFICIAL_ERROR_STATUS) // SUPPRESS
																					// CHECKSTYLE
				|| (500 <= status && status < 600)) { // SUPPRESS CHECKSTYLE
			return PackageMode.ERROR_HTTP_ERROR;
		}
		switch (status) {
		case Downloads.STATUS_FILE_ERROR:
			return PackageMode.ERROR_FILE_ERROR;

		case Downloads.STATUS_UNHANDLED_HTTP_CODE:
		case Downloads.STATUS_UNHANDLED_REDIRECT:
			return PackageMode.ERROR_FILE_ERROR;

		case Downloads.STATUS_HTTP_DATA_ERROR:
			return PackageMode.ERROR_HTTP_ERROR;

		case Downloads.STATUS_TOO_MANY_REDIRECTS:
			return PackageMode.ERROR_HTTP_ERROR;

		case Downloads.STATUS_INSUFFICIENT_SPACE_ERROR:
			return PackageMode.ERROR_INSUFFICIENT_SPACE;

		case Downloads.STATUS_DEVICE_NOT_FOUND_ERROR:
			return PackageMode.ERROR_DEVICE_NOT_FOUND;

		case Downloads.Impl.STATUS_CANNOT_RESUME:
			return PackageMode.ERROR_HTTP_ERROR;

		case Downloads.Impl.STATUS_FILE_ALREADY_EXISTS_ERROR:
			return PackageMode.ERROR_FILE_ERROR;

		default:
			return PackageMode.ERROR_UNKNOWN;
		}

	}

	public static interface PackageCallback {
		void onPackageStatusChanged(PackageMode mode);
	}

	private static final CopyOnWriteArraySet<PackageCallback> listeners = new CopyOnWriteArraySet<PackageHelper.PackageCallback>();

	public static Set<PackageCallback> getPackageListners() {
		return listeners;
	}

	/**
	 * 注册接收package的状态变化
	 * 
	 * <pre>
	 * 可以这样获得数据
	 * if (BroadcaseSender.ACTION_PACKAGE_STATUS_CHANGED.equals(action)) {
	 * 	PackageMode packageMode = resultIntent.getParcelableExtra(BroadcaseSender.ARG_PACKAGE_STATUS_CHANGED);
	 * 	...
	 * }
	 * </pre>
	 * 
	 * @param context
	 * @param receiver
	 * 
	 * @see #PackageMode
	 */
	public synchronized static void registerPackageStatusChangeObserver(PackageCallback listener) {
		try {
			if (listener != null) {
				listeners.add(listener);
			}
		} catch (Exception e) {
			Log.e(DEBUG_TAG, "[registerPackageStatusChangeObserver] Error", e);
		}

	}

	public synchronized static void unregisterPackageStatusChangeObserver(PackageCallback receiver) {
		try {
			if (listeners != null) {
				listeners.remove(receiver);
			}
		} catch (Exception e) {
			Log.e(DEBUG_TAG, "[unregisterPackageStatusChangeObserver] Error", e);
		}

	}

	/*
	 * public static void registerPackageStatusChangeObserver(Context
	 * context,BroadcastReceiver receiver){
	 * 
	 * try { IntentFilter intentFilter = new IntentFilter();
	 * intentFilter.addAction(BroadcaseSender.ACTION_PACKAGE_STATUS_CHANGED);
	 * context.registerReceiver(receiver, intentFilter); } catch (Exception e) {
	 * Log.e(DEBUG_TAG, "[registerPackageStatusChangeObserver] Error",e); }
	 * 
	 * }
	 */

	/*	*//**
	 * 取消注册
	 * 
	 * @param context
	 * @param receiver
	 * 
	 * @see #registerPackageStatusChangeObserver
	 */
	/*
	 * public static void unregisterPackageStatusChangeObserver(Context
	 * context,BroadcastReceiver receiver){ try {
	 * context.unregisterReceiver(receiver); } catch (Exception e) {
	 * Log.e(DEBUG_TAG, "[unregisterPackageStatusChangeObserver] Error",e); }
	 * 
	 * }
	 */

	/**
	 * Report package status.All broadcast receiver register with proper action
	 * will receive the broadcast.
	 * 
	 * @param mode
	 * 
	 * @see #registerPackageStatusChangeObserver
	 */
	public static void notifyPackageStatusChanged(PackageMode mode) {

		BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
		sender.notifyPackageStatusChanged(mode/* ,currentSize,totalSize */);
	}

	/**
	 * Report package status.All broadcast receiver register with proper action
	 * will receive the broadcast.
	 * 
	 * @param gameId
	 * @param downloadUrl
	 * @param dest
	 * @param packageName
	 * @param version
	 * @param versionCode
	 * @param downloadId
	 * @param status
	 * @param reason
	 * 
	 * @see #registerPackageStatusChangeObserver
	 */
	public static void notifyPackageStatusChanged(String gameId, String downloadUrl, String dest, String packageName, String title, String version, int versionCode, long downloadId, int status,
			Integer reason, Long currentSize, Long totalSize/*
															 * , String sign,
															 * String fileMd5
															 */
	) {

		PackageMode mode = new PackageMode();
		mode.downloadId = downloadId;
		mode.downloadUrl = downloadUrl;
		mode.downloadDest = dest;
		mode.gameId = gameId;
		mode.packageName = packageName;
		mode.reason = reason;
		mode.status = status;
		mode.title = title;
		mode.version = version;
		mode.versionCode = versionCode;
		mode.currentSize = currentSize;
		mode.totalSize = totalSize;
		// mode.apkSign = sign ;
		// mode.apkFileMd5 = fileMd5 ;

		BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
		sender.notifyPackageStatusChanged(mode/* ,currentSize,totalSize */);
	}

	public static String formDownloadAppData(DownloadItemInput app) {
		return formDownloadAppData(app.getPackageName(), app.getVersion(), app.getVersionInt(), app.getGameId(), app.isDiffDownload());
	}

	public static String formDownloadAppData(String packageName, String version, int versionCode, String gameId, boolean isDiffUpdate) {

		StringBuffer sb = new StringBuffer();
		sb.append(packageName).append("@");
		sb.append(version).append("@");
		sb.append(versionCode).append("@");
		sb.append(gameId).append("@");
		sb.append(isDiffUpdate ? 1 : 0);
		return sb.toString();

	}

	public static String[] splitDownloadAppData(String mark) {
		String[] split = mark.split("@");
		return split;
	}

	public static String splitPackageFromAppData(String mark) {
		return splitDownloadAppData(mark)[0];
	}

	public static String splitVerionFromAppData(String mark) {
		return splitDownloadAppData(mark)[1];
	}

	public static String splitVersionCodeFromAppData(String mark) {
		return splitDownloadAppData(mark)[2];
	}

	public static String splitGameIdFromAppData(String mark) {
		return splitDownloadAppData(mark)[3];
	}

	public static String splitIsDiffUpdateFromAppData(String mark) {
		return splitDownloadAppData(mark)[4];
	}

	public static PackageMark getAppData(String mark) {
		try {
			String[] arr = splitDownloadAppData(mark);
			PackageMark packageMark = new PackageMark();
			packageMark.packageName = arr[0];
			packageMark.version = arr[1];
			packageMark.versionCode = Integer.parseInt(arr[2]);
			packageMark.gameId = arr[3];
			packageMark.isDiffUpdate = (Integer.parseInt(arr[4]) == 1);
			return packageMark;
		} catch (Exception e) {
			Log.e(DEBUG_TAG, "Parse Mark data Error", e);
			return null;
		}
	}

	private static CopyOnWriteArraySet<PackageMode> mergingSet = new CopyOnWriteArraySet<PackageMode>();

	/**
	 * 合并请求
	 * 
	 * @param mode
	 * @param reMergeFromUi
	 *            是否用户主动触发
	 */
	public static void sendMergeRequest(final PackageMode mode, final boolean reMergeFromUi) {
		new Thread() {
			@Override
			public void run() {
				if (DEBUG) {
					Log.d(DEBUG_TAG, "sendMergeRequest " + mode);
				}
				MergeMode queryMergeRecord = DbManager.getAppDbHandler().queryMergeRecord(mode.gameId);
				if (queryMergeRecord == null) {
					MergeMode mergeMode = new MergeMode(mode.downloadId, mode.downloadUrl, mode.downloadDest, mode.versionCode, mode.version, mode.gameId, mode.packageName, 0, -1, PackageMode.MERGING);
					DbManager.getAppDbHandler().addMergeRecord(mergeMode);
					mergingSet.add(mode);
				} else {
					// DbManager.getAppDbHandler().updateMergeFaileCount(queryMergeRecord);
				}
				mode.status = PackageMode.MERGING;
				PackageHelper.notifyPackageStatusChanged(mode);

				DiffManager m = DiffManager.getInstance();
				// 注意与sendMergeRequestFromUI的区别
				DiffInfo info = new DiffInfo(mode);
				m.postDiff(GameTingApplication.getAppInstance(), info);
			}
		}.start();

	}

	/**
	 * 合并请求(先前未合并或者合并失败后用户主动触发)
	 * 
	 * @param downloadId
	 */
	public static void sendMergeRequestFromUI(final long downloadId) {
		new Thread() {
			@Override
			public void run() {

				DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(GameTingApplication.getAppInstance(), downloadId);
				if (downloadInfo == null) {
					return;
				}
				if (DEBUG) {
					Log.d(DEBUG_TAG, "sendMergeRequest " + downloadInfo);
				}
				PackageMode mode = formPackageMode(downloadInfo);
				mode.status = PackageMode.MERGING;
				PackageHelper.notifyPackageStatusChanged(mode);

				MergeMode queryMergeRecord = DbManager.getAppDbHandler().queryMergeRecord(mode.gameId);
				if (queryMergeRecord == null) {
					MergeMode mergeMode = new MergeMode(mode.downloadId, mode.downloadUrl, mode.downloadDest, mode.versionCode, mode.version, mode.gameId, mode.packageName, 0, -1, PackageMode.MERGING);
					DbManager.getAppDbHandler().addMergeRecord(mergeMode);
					mergingSet.add(mode);
				} else {
					// DbManager.getAppDbHandler().updateMergeFaileCount(queryMergeRecord);
				}
				DiffManager m = DiffManager.getInstance();
				// 注意与sendMergeRequest的区别
				m.postDiff(GameTingApplication.getAppInstance(), new DiffInfo(mode));
			}
		}.start();
	}

	/**
	 * 合并请求（后台线程自动调用）
	 * 
	 * @param downloadId
	 */
	private static void sendMergeRequestFromBg(final long downloadId) {
		new Thread() {
			@Override
			public void run() {

				DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(GameTingApplication.getAppInstance(), downloadId);
				if (downloadInfo == null) {
					return;
				}
				if (DEBUG) {
					Log.d(DEBUG_TAG, "sendMergeRequest " + downloadInfo);
				}
				PackageMode mode = formPackageMode(downloadInfo);
				mode.status = PackageMode.MERGING;
				PackageHelper.notifyPackageStatusChanged(mode);

				MergeMode queryMergeRecord = DbManager.getAppDbHandler().queryMergeRecord(mode.gameId);
				if (queryMergeRecord == null) {
					MergeMode mergeMode = new MergeMode(mode.downloadId, mode.downloadUrl, mode.downloadDest, mode.versionCode, mode.version, mode.gameId, mode.packageName, 0, -1, PackageMode.MERGING);
					DbManager.getAppDbHandler().addMergeRecord(mergeMode);
					mergingSet.add(mode);
				} else {
					// DbManager.getAppDbHandler().updateMergeFaileCount(queryMergeRecord);
				}

				DiffManager m = DiffManager.getInstance();
				m.postDiff(GameTingApplication.getAppInstance(), new DiffInfo(mode));
			}
		}.start();
	}

	public static void installAppAuto(final Activity context, String gameId, final String packageName, final String apkPath) {
		boolean installAutomaticllyAfterDownloading = MineProfile.getInstance().isInstallAutomaticllyAfterDownloading();
		if (installAutomaticllyAfterDownloading) {
			installApp(context, gameId, packageName, apkPath);
		}
	}

	public static void removeOldAuto(final Activity context, final String packageName) {
		boolean installAutomaticllyAfterDownloading = MineProfile.getInstance().isInstallAutomaticllyAfterDownloading();
		if (installAutomaticllyAfterDownloading) {
			showUninstallOldDialog(context, packageName);
		}
	}

	/**
	 * 安装
	 * 
	 * @param context
	 * @param gameId
	 * @param packageName
	 * @param apkPath
	 */
	public static void installApp(final Activity context, final String gameId, final String packageName, final String apkPath) {

		new HandlerThread("installThread") {
			@Override
			public void run() {
				PackageInfo installedApk = AppUtil.getPacakgeInfo(GameTingApplication.getAppInstance(), packageName);
				// if(installedApk != null){
				// AppManager manager =
				// AppManager.getInstance(GameTingApplication.getAppInstance());
				// DownloadAppInfo downloadedGame =
				// manager.getDownloadGameForId(gameId, false);
				// //java.lang.NullPointerException
				// //at
				// com.duoku.gamesearch.app.PackageHelper$5.run(PackageHelper.java:587)
				// String newSignMd5 = downloadedGame.getSign();
				// String signMd5 = AppUtil.getSignMd5(installedApk);
				// if(signMd5 != null && !signMd5.equals(newSignMd5)){
				// showUninstallOldDialog(context, packageName);
				// return ;
				// }
				// }

				installDirectly(apkPath);

			}
		}.start();
	}

	// public static void installApp(final Activity context,final String
	// gameId,final String packageName,final String apkPath){
	// new HandlerThread("installThread"){
	// @Override
	// public void run() {
	// AppManager manager =
	// AppManager.getInstance(GameTingApplication.getAppInstance());
	// DownloadAppInfo downloadedGame = manager.getDownloadGameForId(gameId,
	// false);
	// String sign = downloadedGame.getSign();
	//
	//
	// PackageInfo installedApk =
	// AppUtil.getPacakgeInfo(GameTingApplication.getAppInstance(),
	// packageName);
	// if(installedApk != null){
	// PackageInfo pack =
	// ApkUtil.getPackageForFile(apkPath,GameTingApplication.getAppInstance());
	// if(pack != null){
	// String newSignMd5 = null ;
	// try {
	// newSignMd5 = AppUtil.getSignMd5(pack);
	// } catch (Exception e) {
	// e.printStackTrace();
	// newSignMd5 =
	// ApkUtil.getApkSignatureByFilePath(GameTingApplication.getAppInstance(),
	// apkPath);
	// //newSignMd5 = ApkCerMgr2.getSign(apkPath);
	// }
	// String signMd5 = AppUtil.getSignMd5(installedApk);
	// if(signMd5 != null && !signMd5.equals(newSignMd5)){
	// showUninstallOldDialog(context, packageName);
	// return ;
	// }
	// }
	// }
	//
	// installDirectly(apkPath);
	//
	// }
	// }.start();
	// }
	//
	private static void uninstallOld(Activity context, String packageName) {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivity(uninstallIntent);
	}

	static class PackBean {
		Activity context;
		String packageName;
	}

	private static void showUninstallOldDialog(final Activity context, final String packageName) {
		Message message = new Message();
		PackBean packBean = new PackBean();
		packBean.context = context;
		packBean.packageName = packageName;
		message.obj = packBean;
		message.what = REMOVE_OLD;
		handler.sendMessage(message);
	}

	static final int FILE_DELETED = 10;
	static final int PARSE_FILE_ERROR = 11;
	static final int REMOVE_OLD = 12;
	static Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			final Context context = GameTingApplication.getAppInstance();
			switch (msg.what) {
			case FILE_DELETED:// "下载文件已经删除,请选择：;
				CustomToast.showToast(context, context.getString(R.string.file_deleted));
				break;
			case PARSE_FILE_ERROR:
				CustomToast.showToast(context, context.getString(R.string.parse_file_error));
				break;
			case REMOVE_OLD:
				final PackBean bean = (PackBean) msg.obj;
				DuokuDialog.showRemoveOldDialog(bean.context, 100, bean.packageName, null, null);
			default:
				break;
			}
		};
	};

	private static void installDirectly(String apkPath) {
		Application context = GameTingApplication.getAppInstance();
		Uri uri;
		try {
			uri = Uri.fromFile(new File(apkPath));
			context.getContentResolver().openFileDescriptor(uri, "r").close();
		} catch (FileNotFoundException exc) {
			handler.sendEmptyMessage(FILE_DELETED);
			return;
		} catch (Exception e) {
			handler.sendEmptyMessage(PARSE_FILE_ERROR);
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, Constants.APK_MIME_TYPE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");

		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
		}
	}

	/**
	 * 签名不同，提示卸载老版本
	 * 
	 * @param out
	 */
	public static void notifyUninstallOld(boolean diffUpdate, DownloadItemOutput out) {
		PackageMode mode = PackageHelper.formPackageMode(out);
		if (!diffUpdate) {
			mode.status = PackageMode.DOWNLOADED_DIFFERENT_SIGN;
		} else {
			mode.status = PackageMode.MERGED_DIFFERENT_SIGN;
		}

		// TODO 这里没有设置sign
		PackageHelper.notifyPackageStatusChanged(mode);
	}

	public static com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput formDownloadOut(PackageMode packageMode) {
		com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput out = new com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput();
		out.setCurrentBytes(packageMode.currentSize);
		out.setDest(packageMode.downloadDest);
		out.setDownloadId(packageMode.downloadId);
		String appData = formDownloadAppData(packageMode.packageName, packageMode.version, packageMode.versionCode, packageMode.gameId, packageMode.isDiffDownload);
		out.setAppData(appData);
		// out.setOriginalStatusCode(packageMode.status);
		out.setMimeType(com.ranger.bmaterials.app.Constants.APK_MIME_TYPE);
		out.setStatus(DownloadStatus.STATUS_SUCCESSFUL);
		out.setTitle(packageMode.title);
		out.setTotalBytes(packageMode.totalSize);
		out.setUrl(packageMode.downloadUrl);
		return out;
	}

	/**
	 * 通知合并失败或者成功
	 * 
	 * @param packageMode
	 * @param successful
	 * @param errorReason
	 */
	public static void notifyMergeResult(PackageMode packageMode, boolean successful, int errorReason) {
		if (packageMode != null) {
			QueryInput queryInput = new QueryInput(packageMode.packageName, packageMode.version, packageMode.versionCode, packageMode.downloadUrl, packageMode.gameId);

			if (DEBUG) {
				Log.d(DEBUG_TAG, "notifyMergeResult packageMode:" + packageMode + " successful:" + successful + "packageMode " + packageMode);
			}
			if (successful) {
				Map<QueryInput, PackageMode> queryPackageStatus = queryPackageStatus(queryInput);
				PackageMode m = queryPackageStatus.get(queryInput);
				if (DEBUG) {
					Log.d(DEBUG_TAG, "notifyMergeResult successful" + m + "");
				}
				m.status = PackageMode.MERGED;
				packageMode.status = PackageMode.MERGED;
				// PackageHelper.notifyMergeStatusChanged(packageMode);
				PackageHelper.notifyMergeStatusChanged(m);
			} else {
				if (DEBUG) {
					Log.d(DEBUG_TAG, "notifyMergeResult fail");
				}

				Map<QueryInput, PackageMode> queryPackageStatus = queryPackageStatus(queryInput);
				PackageMode m = queryPackageStatus.get(queryInput);
				packageMode.status = PackageMode.MERGE_FAILED;
				packageMode.reason = errorReason;
				PackageHelper.notifyMergeStatusChanged(packageMode);
			}
		}
	}

	/**
	 * 广播 合并成功或者失败
	 * 
	 * @param mode
	 */
	private static void notifyMergeStatusChanged(PackageMode mode) {
		if (mode.status == PackageMode.MERGE_FAILED) {
			MergeMode queryMergeRecord = DbManager.getAppDbHandler().queryMergeRecord(mode.gameId);
			if (queryMergeRecord != null) {
				queryMergeRecord.failedCount++;
				queryMergeRecord.failedReason = mode.reason;
				mode.mergeFailedCount = queryMergeRecord.failedCount;

				DbManager.getAppDbHandler().updateMergeFailedCount(queryMergeRecord);
				PackageHelper.notifyPackageStatusChanged(mode);
				mergingSet.remove(mode);
				// 没有超过次数,再次合并
				// sendMergeRequest(mode,false);
				// if(queryMergeRecord.failedCount < 1){
				// }else{
				//
				// //失败之后走普通更新
				// UpdatableAppInfo updatableGame =
				// AppManager.getInstance(GameTingApplication.getAppInstance()).getUpdatableGame(mode.packageName);
				// if(updatableGame == null){
				// //Log.e()出错
				// }
				// DownloadItemInput downloadItemInput = new
				// DownloadItemInput(updatableGame.getIconUrl(),
				// mode.gameId, mode.packageName,
				// mode.title,mode.title, mode.versionCode, mode.version,
				// updatableGame.getDownloadUrl(),
				// null, updatableGame.getNewSize(),
				// null, -1, updatableGame.getExtra(),
				// updatableGame.isNeedLogin(), false);
				//
				// restartDownloadNormally(mode.downloadId, downloadItemInput,
				// null);
				//
				// }

			}
			/*
			 * }else { MergeMode mergeMode = new MergeMode(mode.downloadId,
			 * mode.downloadUrl, mode.downloadDest, mode.versionCode,
			 * mode.version, mode.gameId, mode.packageName, 1, mode.reason,
			 * PackageMode.MERGING);
			 * DbManager.getAppDbHandler().addMergeRecord(mergeMode); }
			 */
		} else if (mode.status == PackageMode.MERGED) {
			// DbManager.getAppDbHandler().removeMergeRecord(mode.gameId,mode.downloadUrl,mode.downloadId);
			MergeMode mergeMode = new MergeMode(mode.downloadId, mode.downloadUrl, null, -1, null, mode.gameId, null, 0, -1, PackageMode.MERGED);
			DbManager.getAppDbHandler().updateMergeStatus(mergeMode);

			mergingSet.remove(mode);

			DownloadItemOutput out = PackageHelper.formDownloadOut(mode);
			checkAndNotifyForDownloadedGame(true, mode, out);

		}
	}

	/**
	 * 下载或者合并完成后检查apk签名以及安装
	 * 
	 * @param diffUpdate
	 * @param mode
	 * @param out
	 */
	public static void checkAndNotifyForDownloadedGame(boolean diffUpdate, PackageMode mode, DownloadItemOutput out) {

		BackAppListener listener = BackAppListener.getInstance();
		if (!diffUpdate) {
			PackageHelper.notifyPackageStatusChanged(mode);
			if (mode.status == PackageMode.DOWNLOADED) {
				listener.notifyAndSubmitForNormal(out, mode);
			}
		} else {
			PackageHelper.notifyPackageStatusChanged(mode);
			if (mode.status == PackageMode.MERGED) {
				BackAppListener.getInstance().notifyAndSubmitForDiffUpdate(out, mode);
			}

		}

	}

	/**
	 * 4 <li>gameid/downloadUrl查询不了安装状态</li> <li>
	 * pakcage/verion/versionCode不能完全查询下载状态</li><br/>
	 * 所以需要多个字段，如果要准确地查询状态，需要提供字段pakcage+verion+versionCode+gameid或者pakcage+
	 * verion+versionCode+downloadUrl <br/>
	 * <br/>
	 * 另外，注意的是，有些广播只会发送一次，所以如果调用者在数据未展示之前已经收到广播，需要再次查询一次，否则可能无法更新最新的状态，
	 * 因为状态是实时变化的。 比如<br/>
	 * <li>从 DOWNLOAD_RUNNING 到 DOWNLOAD_PAUSED，</li> <li>从 DOWNLOAD_RUNNING 到
	 * DOWNLOAD_FAILED，</li> <li>从 DOWNLOAD_RUNNING 到 DOWNLOAD_SUCCESSFUL，</li>
	 * <li>从 INSTALLING 到 INSTALL_FAILED，</li> <li>从 INSTALLING 到
	 * INSTALL_SUCCESSFUL，</li><br/>
	 * 可能查询到前者，但是在数据战时之前又收到了状态改变为后者的广播，此时就无法更新状态。
	 * 
	 * 
	 * 查询状态为 "安装"、"更新"或者"下载完成"或者"合并完成"需要设置文件路径
	 * 
	 * @param targets
	 * @return
	 */
	public static Map<QueryInput, PackageMode> queryPackageStatus(List<QueryInput> targets) {
		try {
			int size = 0;
			if (targets == null || (size = targets.size()) == 0) {
				return null;
			}
			// Itialize.
			HashMap<QueryInput, PackageMode> ret = new HashMap<QueryInput, PackageMode>(size);
			for (QueryInput q : targets) {
				ret.put(q, formDefaultPackageModeFromQueryInput(q));
			}
			long start = System.currentTimeMillis();

			checkInstallingStatus(targets, ret);
			long end = System.currentTimeMillis();
			if (Constants.DEBUG) {
				Log.i(DEBUG_TAG, "[checkInstallingStatus] consume" + (end - start));
			}
			start = end;
			checkInstallStatus(targets, ret);
			end = System.currentTimeMillis();
			if (Constants.DEBUG) {
				Log.i(DEBUG_TAG, "[checkInstallStatus] consume" + (end - start));
			}
			start = end;
			checkDownloadStatus(targets, ret);
			end = System.currentTimeMillis();
			if (Constants.DEBUG) {
				Log.i(DEBUG_TAG, "[checkDownloadStatus] consume:" + (end - start));
			}
			start = end;
			checkMergeStatus(targets, ret);
			// checkCheckingStatus(targets,ret);
			end = System.currentTimeMillis();
			if (Constants.DEBUG) {
				Log.i(DEBUG_TAG, "[checkMergeStatus] consume" + (end - start));
			}
			if (Constants.DEBUG) {
				logStatus(ret);
			}

			return ret;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * <li>gameid/downloadUrl查询不了安装状态</li> <li>
	 * pakcage/verion/versionCode不能完全查询下载状态</li><br/>
	 * 所以需要多个字段，如果要准确地查询状态，需要提供字段pakcage+verion+versionCode+gameid或者pakcage+
	 * verion+versionCode+downloadUrl <br/>
	 * <br/>
	 * 另外，注意的是，有些广播只会发送一次，所以如果调用者在数据未展示之前已经收到广播，需要再次查询一次，否则可能无法更新最新的状态，
	 * 因为状态是实时变化的。 比如<br/>
	 * <li>从 DOWNLOAD_RUNNING 到 DOWNLOAD_PAUSED，</li> <li>从 DOWNLOAD_RUNNING 到
	 * DOWNLOAD_FAILED，</li> <li>从 DOWNLOAD_RUNNING 到 DOWNLOAD_SUCCESSFUL，</li>
	 * <li>从 INSTALLING 到 INSTALL_FAILED，</li> <li>从 INSTALLING 到
	 * INSTALL_SUCCESSFUL，</li><br/>
	 * 可能查询到前者，但是在数据战时之前又收到了状态改变为后者的广播，此时就无法更新状态。
	 * 
	 * @param target
	 * @return
	 */
	public static Map<QueryInput, PackageMode> queryPackageStatus(QueryInput target) {
		int size = 0;
		if (target == null) {
			return null;
		}
		// Itialize.
		HashMap<QueryInput, PackageMode> ret = new HashMap<QueryInput, PackageMode>(size);
		ret.put(target, formDefaultPackageModeFromQueryInput(target));

		List<QueryInput> targets = Arrays.asList(target);
		if (targets == null) {
			return null;
		}
		checkInstallingStatus(targets, ret);
		checkInstallStatus(target, ret);
		checkDownloadStatus(target, ret);
		checkMergeStatus(target, ret);
		// checkCheckingStatus(targets, ret);

		if (Constants.DEBUG) {
			logStatus(ret);
		}

		return ret;
	}

	/**
	 * 3
	 * 
	 * @param targets
	 *            gameId的集合
	 * @return &lt;gameId,PackageMode&gt;
	 */
	public static Map<String, PackageMode> queryPackageStatusForIds(List<String> targets) {
		int size = 0;
		if (targets == null || (size = targets.size()) == 0) {
			return null;
		}
		List<QueryInput> list = new ArrayList<QueryInput>(size);
		// Itialize.
		HashMap<String, PackageMode> ret = new HashMap<String, PackageMode>(size);
		HashMap<QueryInput, PackageMode> ret2 = new HashMap<QueryInput, PackageMode>(size);

		for (String gameId : targets) {
			QueryInput queryInput = new QueryInput(null, null, -1, null, gameId);
			ret2.put(queryInput, formDefaultPackageModeFromQueryInput(queryInput));
			list.add(queryInput);
		}

		checkInstallingStatus(list, ret2);
		// checkInstallStatus(targets, ret);
		checkDownloadStatus(list, ret2);
		checkMergeStatus(list, ret2);
		// checkCheckingStatus(list, ret2);
		Set<QueryInput> keySet = ret2.keySet();
		for (QueryInput queryInput : keySet) {
			ret.put(queryInput.gameId, ret2.get(queryInput));
		}
		return ret;
	}

	/**
	 * 2
	 * 
	 * @param targets
	 *            DownloadAppInfo的集合
	 * @return &lt;gameId,PackageMode&gt;
	 */
	public static Map<String, PackageMode> queryPackageStatusForDownloads(List<DownloadAppInfo> targets) {
		int size = 0;
		if (targets == null || (size = targets.size()) == 0) {
			return null;
		}
		List<QueryInput> list = new ArrayList<QueryInput>(size);
		// Itialize.
		HashMap<String, PackageMode> ret = new HashMap<String, PackageMode>(size);
		HashMap<QueryInput, PackageMode> ret2 = new HashMap<QueryInput, PackageMode>(size);

		for (DownloadAppInfo app : targets) {
			QueryInput queryInput = new QueryInput(null, null, -1, null, app.getGameId());
			ret2.put(queryInput, formDefaultPackageModeFromQueryInput(queryInput));
			list.add(queryInput);
		}

		checkInstallingStatus(list, ret2);
		// checkInstallStatus(targets, ret);
		checkDownloadStatus(targets, list, ret2);
		checkMergeStatus(list, ret2);
		// checkCheckingStatus(list, ret2);
		Set<QueryInput> keySet = ret2.keySet();
		for (QueryInput queryInput : keySet) {
			ret.put(queryInput.gameId, ret2.get(queryInput));
		}
		return ret;
	}

	private static DownloadAppInfo findDownloadGame(List<DownloadAppInfo> downloadGames, QueryInput input) {
		boolean r = determineCheckThrougthGameId(input);

		int size = downloadGames.size();
		if (r) {
			for (int i = 0; i < size; i++) {
				DownloadAppInfo item = downloadGames.get(i);
				if (input.gameId.equals(item.getGameId())) {
					return item;
				}
			}
		}
		boolean r2 = determineCheckThrougthDownloadUrl(input);
		if (r2) {
			for (int i = 0; i < size; i++) {
				DownloadAppInfo item = downloadGames.get(i);
				if (input.downloadUrl.equals(item.getDownloadUrl())) {
					return item;
				}
			}
		}
		return null;
	}

	private static void checkCheckingStatus2(List<DownloadAppInfo> downloadGames, List<QueryInput> targets, Map<QueryInput, PackageMode> ret) {
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		Map<String, ?> checkStatus = manager.getCheckStatus();
		int size = targets.size();
		for (int i = 0; i < size; i++) {
			QueryInput item = targets.get(i);
			DownloadAppInfo findDownloadGame = findDownloadGame(downloadGames, item);
			if (findDownloadGame != null && findDownloadGame.getStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
				if (checkStatus != null && checkStatus.size() != 0) {
					Integer x = (Integer) checkStatus.get(item.gameId);
					if (x != null && (x == PackageMode.CHECKING || x == PackageMode.CHECKING_FINISHED)) {
						PackageMode packageMode = ret.get(item);
						packageMode.status = x;
					}
				}
			} else {
				removeMergeGame(item.gameId, item.downloadUrl, -1);
			}

		}

	}

	// 1
	public static Map<String, PackageMode> queryPackageStatusForUpdates(List<UpdatableAppInfo> targets) {
		int size = 0;
		if (targets == null || (size = targets.size()) == 0) {
			return null;
		}
		List<QueryInput> list = new ArrayList<QueryInput>(size);
		// Itialize.
		HashMap<String, PackageMode> ret = new HashMap<String, PackageMode>(size);
		HashMap<QueryInput, PackageMode> ret2 = new HashMap<QueryInput, PackageMode>(size);

		for (UpdatableAppInfo app : targets) {
			QueryInput queryInput = new QueryInput(null, null, -1, null, app.getGameId());
			PackageMode def = formDefaultPackageModeFromQueryInput(queryInput);
			def.status = PackageMode.UPDATABLE;
			ret2.put(queryInput, def);
			list.add(queryInput);
		}

		checkInstallingStatus(list, ret2);
		checkInstallStatus(list, ret2);
		checkDownloadStatus(list, ret2);
		checkMergeStatus(list, ret2);
		// checkCheckingStatus(list, ret2);

		Set<QueryInput> keySet = ret2.keySet();
		for (QueryInput queryInput : keySet) {
			ret.put(queryInput.gameId, ret2.get(queryInput));
		}
		return ret;
	}

	private static void logStatus(Map<QueryInput, PackageMode> ret) {
		try {
			Set<QueryInput> keySet = ret.keySet();
			for (QueryInput q : keySet) {
				Log.i("getGamesStatus", "status:" + ret.get(q).status + " for " + q.packageName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除合并记录（删除下载时需要调用）
	 * 
	 * @param gameId
	 * @param downloadUrl
	 * @param downloadId
	 */
	public static void removeMergeGame(String gameId, String downloadUrl, long downloadId) {
		DbManager.getAppDbHandler().removeMergeRecord(gameId, downloadUrl, downloadId);
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		manager.removeCheckStatus(gameId);

	}

	public static void removeAllMergeGames() {
		DbManager.getAppDbHandler().removeAllMergeRecord();
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		manager.removeAllCheckStatus();

	}

	public static void removeDownloadGames(final String... urls) {
		if (urls == null) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					Application context = GameTingApplication.getAppInstance();
					List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(context);

					AppDao appDbHandler = DbManager.getAppDbHandler();

					BroadcaseSender sender = BroadcaseSender.getInstance(context);
					// BackAppListener.getInstance().cancleNotification(apps.get(j));
					if (allDownloads != null) {
						for (DownloadItemOutput downloadInfo : allDownloads) {
							PackageMark appData = getAppData(downloadInfo.getAppData());
							for (String url : urls) {
								if (downloadInfo != null && url.equals(downloadInfo.getUrl())) {
									appDbHandler.removeDownloadGames(true, downloadInfo.getDownloadId());
									DownloadUtil.removeDownload(context, true, downloadInfo.getDownloadId());

									removeMergeGame(appData.gameId, url, downloadInfo.getDownloadId());
									notifyForRemoveDownload(downloadInfo);
								}
							}

						}
					}

				} catch (Exception e) {
					Log.e(DEBUG_TAG, "removeDownloadGames Error!", e);
				}
				BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
				sender.notifyDownloadChanged(false);

			};
		}.start();

	}

	public static void removeDownloadGames(final String url) {
		removeDownloadGames(url, true);
	}

	/**
	 * private method
	 * 
	 * @param url
	 * @param notify
	 */
	public static void doRemove(final String url, final boolean notify) {
		Application context = GameTingApplication.getAppInstance();
		try {
			DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, url);
			if (downloadInfo != null) {
				AppDao appDbHandler = DbManager.getAppDbHandler();
				appDbHandler.removeDownloadGames(true, downloadInfo.getDownloadId());
				if (Constants.DEBUG) {
					Log.d(DEBUG_TAG, "removeDownloadGames appDbHandler.removeDownloadGames");
				}
				DownloadUtil.removeDownload(context, true, downloadInfo.getDownloadId());
				if (notify)
					notifyForRemoveDownload(downloadInfo);
				if (Constants.DEBUG) {
					Log.d(DEBUG_TAG, "removeDownloadGames success for:" + url);
				}
				PackageMark appData = getAppData(downloadInfo.getAppData());
				removeMergeGame(appData.gameId, url, downloadInfo.getDownloadId());
			} else {
				if (Constants.DEBUG) {
					Log.d(DEBUG_TAG, "removeDownloadGames failed,cannot find game for:" + url);
				}
			}

		} catch (Exception e) {
			Log.e(DEBUG_TAG, "removeDownloadGames Error!", e);
		}
		BroadcaseSender sender = BroadcaseSender.getInstance(context);
		sender.notifyDownloadChanged(false);
	}

	private static void removeDownloadGames(final String url, final boolean notify) {
		new Thread() {
			@Override
			public void run() {
				doRemove(url, notify);
			}
		}.start();

	}

	/**
	 * 取消或者删除下载
	 * 
	 * @param downloadId
	 */
	public static void removeDownloadGames(final long downloadId) {
		removeDownloadGames(downloadId, true);
	}

	public static void removeDownloadUpdateGames(final long downloadId) {
		removeDownloadUpdateGames(downloadId, true);
	}

	private static void removeDownloadUpdateGames(final long downloadId, final boolean notify) {
		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();
				try {
					DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, downloadId);
					AppDao appDbHandler = DbManager.getAppDbHandler();
					appDbHandler.removeDownloadGames(true, downloadId);
					if (downloadId > 0) {
						int removeDownload = DownloadUtil.removeDownload(context, true, downloadId);
						if (Constants.DEBUG) {
							Log.d(DEBUG_TAG, "removeDownloadGames success for :" + downloadId + " affect:" + removeDownload);
						}
					}

					downloadInfo.setReason(DownloadReason.CANCEL_UPDATE);

					if (downloadInfo != null && notify) {
						notifyForRemoveDownload(downloadInfo);
					}
					if (downloadInfo != null && notify) {
						PackageMark appData = getAppData(downloadInfo.getAppData());
						removeMergeGame(appData.gameId, downloadInfo.getUrl(), downloadId);
					}

				} catch (Exception e) {
					Log.e(DEBUG_TAG, "removeDownloadGames Error!", e);
				}
				BroadcaseSender sender = BroadcaseSender.getInstance(context);
				sender.notifyDownloadChanged(false);
			}
		}.start();

	}

	private static void removeDownloadGames(final long downloadId, final boolean notify) {
		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();
				try {
					DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, downloadId);
					AppDao appDbHandler = DbManager.getAppDbHandler();
					appDbHandler.removeDownloadGames(true, downloadId);
					if (downloadId > 0) {
						int removeDownload = DownloadUtil.removeDownload(context, true, downloadId);
						if (Constants.DEBUG) {
							Log.d(DEBUG_TAG, "removeDownloadGames success for :" + downloadId + " affect:" + removeDownload);
						}
					}

					if (downloadInfo != null && notify) {
						PackageMark appData = getAppData(downloadInfo.getAppData());
						removeMergeGame(appData.gameId, downloadInfo.getUrl(), downloadId);
					}

					if (downloadInfo != null && notify) {
						notifyForRemoveDownload(downloadInfo);
					}

				} catch (Exception e) {
					Log.e(DEBUG_TAG, "removeDownloadGames Error!", e);
				}
				BroadcaseSender sender = BroadcaseSender.getInstance(context);
				sender.notifyDownloadChanged(false);
			}
		}.start();

	}

	/**
	 * 取消或者删除下载
	 * 
	 * @param downloadIds
	 */
	public static void removeDownloadGames(final long... downloadIds) {
		if (downloadIds == null) {
			return;
		}
		new Thread() {
			public void run() {
				Application context = GameTingApplication.getAppInstance();

				try {
					List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(context);

					AppDao appDbHandler = DbManager.getAppDbHandler();
					appDbHandler.removeDownloadGames(true, downloadIds);
					if (downloadIds.length > 0) {
						DownloadUtil.removeDownload(context, true, downloadIds);
					}

					if (allDownloads != null) {
						for (DownloadItemOutput downloadInfo : allDownloads) {
							if (downloadInfo != null) {
								PackageMark appData = getAppData(downloadInfo.getAppData());
								for (long id : downloadIds) {
									if (id == downloadInfo.getDownloadId()) {
										notifyForRemoveDownload(downloadInfo);

										removeMergeGame(appData.gameId, downloadInfo.getUrl(), id);

									}
								}
							}
						}
					}
					BroadcaseSender sender = BroadcaseSender.getInstance(context);
					sender.notifyDownloadChanged(false);

				} catch (Exception e) {
					Log.e(DEBUG_TAG, "removeDownloadGames Error!", e);
				}
			};
		}.start();

	}

	public static void restartDownload(DownloadCallback callback, String... urls) {
		resumeDownload(callback, urls);
		new Thread() {
			public void run() {
				Notifier.updateNotificationForFailedDownload();
			};
		}.start();
	}

	public static void restartDownload(String url, DownloadCallback callback) {
		resumeDownload(url, callback);
		new Thread() {
			public void run() {
				Notifier.updateNotificationForFailedDownload();
			};
		}.start();
	}

	public static void restartDownload(DownloadCallback callback, long... ids) {
		resumeDownload(callback, ids);
		new Thread() {
			public void run() {
				Notifier.updateNotificationForFailedDownload();
			};
		}.start();
	}

	public static void restartDownload(long id, DownloadCallback callback) {
		resumeDownload(id, callback);
		new Thread() {
			public void run() {
				Notifier.updateNotificationForFailedDownload();
			};
		}.start();
	}

	public static PackageMode formPackageMode(DownloadItemOutput out) {
		try {
			String mark = out.getAppData();
			PackageMark markData = getAppData(mark);

			String packageName = markData.packageName;
			String gameId = markData.gameId;
			String version = markData.version;
			int versionCode = markData.versionCode;
			boolean isDiffUpdate = markData.isDiffUpdate;

			String downloadUrl = out.getUrl();
			String dest = out.getDest();
			try {
				dest = Uri.parse(out.getDest()).getPath();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String title = out.getTitle();

			int status = PackageMode.DEFAULT_STATUS;
			Integer reason = null;

			if (out.getReason() == DownloadReason.CANCEL_UPDATE) {
				reason = CancelReason.CANCEL_UPDATE;
			}

			long downloadId = out.getDownloadId();
			long currentSize = out.getCurrentBytes();
			long totalSize = out.getTotalBytes();

			// String sign = null ;
			// String fileMd5 = null ;

			return new PackageMode(gameId, downloadUrl, packageName, version, versionCode, downloadId, dest, title, status, reason, currentSize, totalSize,/*
																																							 * sign
																																							 * ,
																																							 * fileMd5
																																							 * ,
																																							 */isDiffUpdate);

		} catch (Exception e) {
			Log.e(DEBUG_TAG, "formPackageMode error", e);
			return null;
		}
	}

	/**
	 * 恢复暂停的下载
	 * 
	 * @param urls
	 */
	public static void resumeDownload(final DownloadCallback callback, final String... urls) {
		if (urls == null) {
			return;
		}
		new Thread() {
			public void run() {
				int len = urls.length;
				Application context = GameTingApplication.getAppInstance();
				List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(context);
				if (allDownloads != null) {
					for (int i = 0; i < len; i++) {
						String url = urls[i];
						for (DownloadItemOutput downloadInfo : allDownloads) {
							if (downloadInfo != null && url.equals(downloadInfo.getUrl())) {
								try {
									int reason = checkDownload(downloadInfo.getTotalBytes());
									if (reason != -1) {
										if (callback != null) {
											callback.onResumeDownloadResult(url, false, reason);
										}
										continue;
									}

									DownloadUtil.resumeDownload(context, downloadInfo.getDownloadId());

									PackageMode formPackageMode = formPackageMode(downloadInfo);
									if (formPackageMode == null) {
										continue;
									}
									formPackageMode.status = PackageMode.DOWNLOAD_PENDING;
									formPackageMode.reason = null;
									notifyPackageStatusChanged(formPackageMode);

								} catch (Exception e) {
									Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
								}
							}
						}
					}
				}
				Notifier.updateNotificationForDownload();
			};
		}.start();

	}

	public static void doResume(final String url, final DownloadCallback callback) {

		Application context = GameTingApplication.getAppInstance();
		DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, url);
		if (downloadInfo != null) {
			try {
				int reason = checkDownload(downloadInfo.getTotalBytes());
				if (reason != -1) {
					if (callback != null) {
						callback.onResumeDownloadResult(url, false, reason);
					}
					return;
				}

				DownloadUtil.resumeDownload(context, downloadInfo.getDownloadId());

				PackageMode formPackageMode = formPackageMode(downloadInfo);
				if (formPackageMode == null) {
					return;
				}
				formPackageMode.status = PackageMode.DOWNLOAD_PENDING;
				formPackageMode.reason = null;
				notifyPackageStatusChanged(formPackageMode);
				if (Constants.DEBUG) {
					Log.d(DEBUG_TAG, "resumeDownload success  for:" + url);
				}
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
			}

		} else {
			if (Constants.DEBUG) {
				Log.d(DEBUG_TAG, "resumeDownload failed cannot find game for:" + url);
			}
		}
	}

	/**
	 * 恢复暂停的下载
	 * 
	 * @param url
	 */
	public static void resumeDownload(final String url, final DownloadCallback callback) {
		new Thread() {
			@Override
			public void run() {
				doResume(url, callback);
				Notifier.updateNotificationForDownload();
			}
		}.start();

	}

	/**
	 * 恢复暂停的下载
	 * 
	 * @param downloadIds
	 */
	public static void resumeDownload(final DownloadCallback callback, final long... downloadIds) {
		if (downloadIds == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();
				if (downloadIds != null) {
					DownloadUtil.resumeDownload(context, downloadIds);
				}
				List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(context);
				if (allDownloads != null) {
					int len = downloadIds.length;
					for (int i = 0; i < len; i++) {
						long id = downloadIds[i];
						for (DownloadItemOutput downloadInfo : allDownloads) {
							if (downloadInfo != null && id == downloadInfo.getDownloadId()) {
								try {
									int reason = checkDownload(downloadInfo.getTotalBytes());
									if (reason != -1) {
										if (callback != null) {
											callback.onResumeDownloadResult(downloadInfo.getUrl(), false, reason);
										}
										continue;
									}

									PackageMode formPackageMode = formPackageMode(downloadInfo);
									if (formPackageMode == null) {
										continue;
									}
									formPackageMode.status = PackageMode.DOWNLOAD_PENDING;
									formPackageMode.reason = null;
									notifyPackageStatusChanged(formPackageMode);
								} catch (Exception e) {
									Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
								}
							}
						}
					}
				}
				Notifier.updateNotificationForDownload();
			}
		}.start();

	}

	/**
	 * 恢复暂停的下载
	 * 
	 * @param id
	 *            downloadId
	 */
	public static void resumeDownload(final long id, final DownloadCallback callback) {

		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();
				DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, id);
				if (downloadInfo != null) {
					try {
						int reason = checkDownload(downloadInfo.getTotalBytes());
						if (reason != -1) {
							if (callback != null) {
								callback.onResumeDownloadResult(downloadInfo.getUrl(), false, reason);
							}
							return;
						}

						DownloadUtil.resumeDownload(context, id);
						PackageMode formPackageMode = formPackageMode(downloadInfo);
						if (formPackageMode == null) {
							return;
						}
						formPackageMode.status = PackageMode.DOWNLOAD_PENDING;
						formPackageMode.reason = null;
						notifyPackageStatusChanged(formPackageMode);
					} catch (Exception e) {
						Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
					}
				}
				Notifier.updateNotificationForDownload();

			}
		}.start();

	}

	private static void doPause(final String url) {
		Application context = GameTingApplication.getAppInstance();

		DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, url);

		if (downloadInfo != null) {
			try {
				DownloadUtil.pauseDownload(context, downloadInfo.getDownloadId());

				PackageMode formPackageMode = formPackageMode(downloadInfo);
				if (formPackageMode == null) {
					return;
				}
				formPackageMode.status = PackageMode.DOWNLOAD_PAUSED;
				formPackageMode.reason = PackageMode.PAUSED_BY_APP;
				notifyPackageStatusChanged(formPackageMode);
				Notifier.updateNotificationForDownload();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
			}
			if (Constants.DEBUG) {
				Log.d(DEBUG_TAG, "pauseDownloadGames success for:" + url);
			}

		} else {
			if (Constants.DEBUG) {
				Log.d(DEBUG_TAG, "removeDownloadGames faile,cannot find app for:" + url);
			}
		}
	}

	/**
	 * 暂停下载
	 * 
	 * @param url
	 */
	public static void pauseDownloadGames(final String url) {
		new Thread() {
			@Override
			public void run() {
				doPause(url);
			}
		}.start();

	}

	/**
	 * 暂停下载
	 * 
	 * @param urls
	 */
	public static void pauseDownloadGames(final String... urls) {
		if (urls == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();

				List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(context);
				if (allDownloads != null) {
					for (DownloadItemOutput downloadInfo : allDownloads) {
						for (String url : urls) {
							if (downloadInfo != null && url.equals(downloadInfo.getUrl())) {
								try {
									DownloadUtil.pauseDownload(context, downloadInfo.getDownloadId());
									PackageMode formPackageMode = formPackageMode(downloadInfo);
									if (formPackageMode == null) {
										continue;
									}
									formPackageMode.status = PackageMode.DOWNLOAD_PAUSED;
									formPackageMode.reason = PackageMode.PAUSED_BY_APP;
									notifyPackageStatusChanged(formPackageMode);
									Notifier.updateNotificationForDownload();
								} catch (Exception e) {
									Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
								}
							}
						}

					}
				}
			}
		}.start();

	}

	/**
	 * 暂停下载 还需要更新notification
	 * 
	 * @param downloadIds
	 */
	public static void pauseDownloadGames(final long... downloadIds) {
		if (downloadIds == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();
				if (downloadIds != null) {
					DownloadUtil.pauseDownload(context, downloadIds);
				}

				List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(context);

				if (allDownloads != null) {
					for (DownloadItemOutput downloadInfo : allDownloads) {
						for (long id : downloadIds) {
							if (downloadInfo != null && id == downloadInfo.getDownloadId()) {
								try {
									PackageMode formPackageMode = formPackageMode(downloadInfo);
									if (formPackageMode == null) {
										continue;
									}
									formPackageMode.status = PackageMode.DOWNLOAD_PAUSED;
									formPackageMode.reason = PackageMode.PAUSED_BY_APP;
									notifyPackageStatusChanged(formPackageMode);
									Notifier.updateNotificationForDownload();
								} catch (Exception e) {
									Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
								}
							}
						}
					}
				}
			}
		}.start();

	}

	/**
	 * 暂停下载
	 * 
	 * @param downloadId
	 */
	public static void pauseDownloadGames(final long downloadId) {
		new Thread() {
			@Override
			public void run() {
				Application context = GameTingApplication.getAppInstance();
				DownloadUtil.pauseDownload(context, downloadId);
				DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(context, downloadId);

				if (downloadInfo != null) {
					try {
						PackageMode formPackageMode = formPackageMode(downloadInfo);
						if (formPackageMode == null) {
							return;
						}
						formPackageMode.status = PackageMode.DOWNLOAD_PAUSED;
						formPackageMode.reason = PackageMode.PAUSED_BY_APP;
						notifyPackageStatusChanged(formPackageMode);

						Notifier.updateNotificationForDownload();

						if (DEBUG) {
							Log.d(DEBUG_TAG, String.format("[pauseDownloadGames] current size:%s,total size:%s for %s", formPackageMode.currentSize, formPackageMode.totalSize, formPackageMode.title));
						}
					} catch (Exception e) {
						Log.e(DEBUG_TAG, "pauseDownloadGames error", e);
					}
				}
			}
		}.start();

	}

	/**
	 * Download method.This is asynchronous request,you will get notified
	 * through <B>DownloadCallback</B>.
	 * 
	 * @param app
	 * @param callback
	 * 
	 * @see #DownloadCallback
	 */
	public static void download(final DownloadItemInput app, final DownloadCallback callback) {
		download(app, callback, 0, false);
	}

	/**
	 * Download method.This is synchronous request,you will get notified through
	 * <B>DownloadCallback</B>.
	 * 
	 * @param app
	 * @param callback
	 * 
	 * @see #DownloadCallback
	 */
	public synchronized static void download_speed(final DownloadItemInput app, final DownloadCallback callback, final int index, final boolean forceDownload) {
		if (app == null) {
			return;
		}
		long result = doDownload(app, callback, index, forceDownload);
	}

	/**
	 * 增量更新下载失败，普通更新下载
	 * 
	 * @param oldId
	 * @param app
	 * @param callback
	 */
	public static synchronized void restartDownloadNormally(final long oldId, final DownloadItemInput app, final DownloadCallback callback) {
		// download(app, callback, 0,false);
		// removeDownloadGames(oldId,false);
		new Thread() {
			@Override
			public void run() {

				int r = updateDownloadForRestart(oldId, app);
				if (callback != null) {
					if (r == -1) {
						File destinationFile = DownloadUtil.composeDestination(GameTingApplication.getAppInstance(), app.getDownloadUrl(), DEFAUL_TDOWNLOAD_DEST, formSaveName(app));
						if (Constants.DEBUG)
							Log.d("Package Helper : ", "destination file name " + destinationFile.getAbsolutePath());
						callback.onRestartDownloadResult(app.getDownloadUrl(), destinationFile.getAbsolutePath(), true, null);
					} else {
						callback.onRestartDownloadResult(app.getDownloadUrl(), null, false, r);
					}
				}

			}
		}.start();
	}

	/**
	 * 增量更新下载失败，普通更新下载
	 * 
	 * @param oldUrl
	 * @param app
	 * @param callback
	 */
	public static void restartDownloadNormally(final String oldUrl, final DownloadItemInput app, final DownloadCallback callback) {
		// download(app, callback, 0,false);
		// removeDownloadGames(oldUrl,false);
		new Thread() {
			@Override
			public void run() {
				int r = updateDownloadForRestart(oldUrl, app);
				if (callback != null) {
					if (r == -1) {
						File destinationFile = DownloadUtil.composeDestination(GameTingApplication.getAppInstance(), app.getDownloadUrl(), DEFAUL_TDOWNLOAD_DEST, formSaveName(app));

						callback.onRestartDownloadResult(app.getDownloadUrl(), destinationFile.getAbsolutePath(), true, null);
					} else {
						callback.onResumeDownloadResult(app.getDownloadUrl(), false, r);
					}
				}

			}
		}.start();

	}

	/**
	 * 增量更新失败，走普通更新（更新下载地址和一些其他的数据，不是删除记录再重新下载）
	 * 
	 * @param oldUrl
	 * @param app
	 * @return
	 */
	private static int updateDownloadForRestart(String oldUrl, DownloadItemInput app) {
		/*
		 * if(app.getDownloadUrl() == null){ return
		 * PackageMode.ERROR_PARAM_NO_URL ; }
		 */
		int checkDownload = checkDownload(app);

		if (checkDownload != -1) {
			return checkDownload;
		}
		Application context = GameTingApplication.getAppInstance();
		File destinationFile = DownloadUtil.composeDestination(context, app.getDownloadUrl(), DEFAUL_TDOWNLOAD_DEST, formSaveName(app));

		String appData = formDownloadAppData(app);
		int r = DownloadUtil.updateDownload(context, oldUrl, app.getDownloadUrl(), destinationFile.getAbsolutePath(), appData);

		int r2 = AppManager.getInstance(context).updateDownloadRecord(oldUrl, app.getDownloadUrl(), false, app.getSize());

		return -1;
	}

	/**
	 * 增量更新失败，走普通更新（更新下载地址和一些其他的数据，不是删除记录再重新下载）
	 * 
	 * @param oldId
	 * @param app
	 * @return
	 */
	private static int updateDownloadForRestart(long oldId, DownloadItemInput app) {
		/*
		 * if(app.getDownloadUrl() == null){ return
		 * PackageMode.ERROR_PARAM_NO_URL ; }
		 */
		int checkDownload = checkDownload(app);

		if (checkDownload != -1) {
			return checkDownload;
		}
		Application context = GameTingApplication.getAppInstance();
		File destinationFile = DownloadUtil.composeDestination(context, app.getDownloadUrl(), DEFAUL_TDOWNLOAD_DEST, formSaveName(app));

		String appData = formDownloadAppData(app);

		int r = DownloadUtil.updateDownload(context, oldId, app.getDownloadUrl(), destinationFile.getAbsolutePath(), appData);

		int r2 = AppManager.getInstance(context).updateDownloadRecord(oldId, app.getDownloadUrl(), false, app.getSize());

		return -1;
	}

	public static void download(final List<DownloadItemInput> apps, final DownloadCallback callback) {
		int size = 0;
		if (apps == null || (size = apps.size()) == 0) {
			return;
		}
		for (int i = 0; i < size; i++) {
			DownloadItemInput app = apps.get(i);
			download(app, callback, i, false);
		}
	}

	private static List<DownloadItemOutput> getDownloadFiles() {
		List<DownloadItemOutput> allDownloads = DownloadUtil.getAllDownloads(GameTingApplication.getAppInstance());
		return allDownloads;
	}

	/**
	 * 暂停超过数量的下载任务（后来需求改变了，所以目前没有用到）
	 * 
	 * @param targetSize
	 */
	public static void pauseOverDonwloads(int targetSize) {
		List<DownloadItemOutput> downloadFiles = getDownloadFiles();
		if (downloadFiles == null) {
			return;
		}
		TreeSet<Long> treeSet = new TreeSet<Long>();
		for (DownloadItemOutput o : downloadFiles) {
			DownloadStatus status = o.getStatus();
			if (status == DownloadStatus.STATUS_PENDING || status == DownloadStatus.STATUS_RUNNING) {
				long downloadId = o.getDownloadId();
				if (downloadId > 0) {
					boolean add = treeSet.add(downloadId);
				}

			}
		}
		if (treeSet.size() <= targetSize) {
			return;
		}
		long[] ids = new long[treeSet.size() - targetSize];
		int i = 0;
		for (Long id : treeSet) {
			if (i >= targetSize) {
				ids[i - targetSize] = id;
			}
			i++;
		}
		pauseDownloadGames(ids);

	}

	/**
	 * 删除下载完成的任务，同时删除文件（设置里面使用）
	 */
	public static void removeFinishedDownload() {
		if (Constants.DEBUG)
			Log.i("PopNumber", "[AppManager#removeFinishedDownload]");
		List<DownloadItemOutput> downloadFiles = getDownloadFiles();
		if (downloadFiles == null) {
			if (Constants.DEBUG)
				Log.i("PopNumber", "[AppManager#removeFinishedDownload]downloadFiles is null");
			return;
		}
		Set<String> excludeFiles = new HashSet<String>();
		AppSilentInstaller installer = AppSilentInstaller.getInstnce();
		Map<Long, String> map = new HashMap<Long, String>();
		for (DownloadItemOutput o : downloadFiles) {
			DownloadStatus status = o.getStatus();
			if (status == DownloadStatus.STATUS_SUCCESSFUL /*
															 * || status ==
															 * DownloadStatus
															 * .STATUS_FAILED
															 */) {
				long downloadId = o.getDownloadId();
				if (downloadId > 0) {
					String data = o.getAppData();
					String pkg = PackageHelper.splitPackageFromAppData(data);
					if (!installer.getInstallPackage(pkg)) {
						map.put(downloadId, data);
						BackAppListener.getInstance().cancleNotification(o.getUrl(), o.getDownloadId());
						if (Constants.DEBUG)
							Log.i("PopNumber", "[AppManager#removeFinishedDownload]add status:" + status + " pkg:" + data);
					}
				}

				// 要保留的
			} else {
				String dest = o.getDest();
				try {
					Uri uri = Uri.parse(dest);
					// String path = uri.getPath();
					String lastPathSegment = uri.getLastPathSegment();
					excludeFiles.add(lastPathSegment);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (map.size() > 0) {// 空表示删除所有,所以需要判断map
			removeDownloadGames(map);
		}
		try {
			final File cachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.DOWNLOAD_FOLDER);
			FileHelper.removeFile(cachePath, excludeFiles);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * 通知界面需要重新查询状态
		 */
		PackageMode packageMode = new PackageMode();
		packageMode.status = PackageMode.RESET_STATUS;
		PackageHelper.notifyPackageStatusChanged(packageMode);

	}

	/**
	 * 删除(取消)下载
	 * 
	 * @param packages
	 *            downloadId,packageName
	 */
	private static void removeDownloadGames(Map<Long, String> packages) {

		Context context = GameTingApplication.getAppInstance();
		if (Constants.DEBUG)
			Log.i("PopNumber", "[AppManager#removeDownloadGames]");
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			int size = packages.size();
			long[] ids = new long[size];
			String[] ps = new String[size];
			Set<Long> keySet = packages.keySet();
			int i = 0;
			for (Long id : keySet) {
				ids[i] = id;
				ps[i] = packages.get(id);
				i++;
			}
			if (Constants.DEBUG)
				Log.i("PopNumber", "[AppManager#removeDownloadGames]appDbHandler.removeDownloadGames");
			appDbHandler.removeDownloadGames(true, ids);
			if (Constants.DEBUG)
				Log.i("PopNumber", "[AppManager#removeDownloadGames]DownloadUtil.removeDownload");
			DownloadUtil.removeDownload(context, true, ids);
			for (int j = 0; j < size; j++) {
				// 删除合并记录和检查记录
				PackageHelper.removeMergeGame(null, null, ids[j]);
			}
			BroadcaseSender sender = BroadcaseSender.getInstance(context);
			sender.notifyDownloadChanged(false, ps);

		} catch (Exception e) {
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// private static void doDownload(final DownloadItemInput app,
	// final DownloadCallback callback, final int index) {
	// boolean result = true ;
	// Context context = GameTingApplication.getAppInstance() ;
	//
	// int errorReason = checkDownload(app);
	//
	// result = (errorReason == -1 );
	// long downloadId = -1 ;
	//
	//
	// if(result){
	// String appData = formDownloadAppData(app);
	// boolean valid = DownloadUtil.checkDownload(context, app.getDownloadUrl(),
	// Constants.APK_MIME_TYPE, DEFAUL_TDOWNLOAD_DEST, formSaveName(app),
	// appData);
	// if(valid){
	// //current the downloadId is not correct.
	// DownloadAppInfo downloadAppInfo = formDownloadAppInfo(app);
	// AppManager manager = AppManager.getInstance(context);
	//
	// removeOldNotificationForDownload(app.getDownloadUrl(), downloadId);
	// notifyBeforeDownload(context, app,downloadId);
	//
	// try {
	// //Already checked,so don't check any more.
	// downloadId = DownloadUtil.download(context, app.getDownloadUrl(),
	// Constants.APK_MIME_TYPE,
	// DEFAUL_TDOWNLOAD_DEST,formSaveName(app), app.getDisplayName(),
	// appData,false);
	//
	// } catch (Exception e) {
	// Log.e(DEBUG_TAG, "Download Error:", e);
	// errorReason = PackageMode.ERROR_DATABASE_ERROR ;
	// }
	// result = (downloadId > 0) ;
	// if(result){
	// downloadAppInfo.setDownloadId(downloadId);
	// long rowId = manager.addDownloadRecord(downloadAppInfo);
	// result = (rowId > 0);
	// }
	// if(!result){
	// errorReason = PackageMode.ERROR_DATABASE_ERROR ;
	// if(downloadId > 0){
	// DownloadUtil.removeDownload(context, true, downloadId);
	// }
	// DbManager.getAppDbHandler().removeDownloadGames2(true, app.getGameId());
	// notifyInsertDownloadFailed(context, app, downloadId);
	// }
	//
	// } else{
	// //DownloadUtil.removeDownload(context, true, downloadId);
	// result = false ;
	// errorReason = PackageMode.ERROR_DATABASE_ERROR ;
	//
	// }
	// }
	// //Notify the caller that download successful or not .
	// if(callback !=null){
	// callback.onDownloadResult(index,result,downloadId, errorReason);
	// }
	//
	// if(result){
	// notifyForDownload(app, context);
	// }
	// }
	private static long doDownload(final DownloadItemInput app, final DownloadCallback callback, final int index, boolean forceDownload) {

		String downloadUrl = app.getDownloadUrl();
		String formSaveName = formSaveName(app);
		String appData = formDownloadAppData(app);

		boolean result = true;
		final Context context = GameTingApplication.getAppInstance();

		// if(Constants.isFirstDownload ){
		// Constants.isFirstDownload = false;
		// if (!ConnectManager.isWifi(context)) {
		// Looper.prepare();
		// Toast.makeText(context, R.string.toast_download_alert_wifi_only,
		// Toast.LENGTH_LONG).show();
		// Looper.loop();
		// }
		// }

		// Check download
		int errorReason = checkDownload(app);

		result = (errorReason == -1);
		long downloadId = -1;

		if (result) {
			// Check whether this item has been downloaded or is downloading.
			boolean valid = DownloadUtil.checkDownload(context, downloadUrl, Constants.APK_MIME_TYPE, DEFAUL_TDOWNLOAD_DEST, formSaveName, appData);
			if (valid || forceDownload) {

				DownloadAppInfo downloadAppInfo = formDownloadAppInfo(app);
				AppManager manager = AppManager.getInstance(context);
				// current the downloadId is not correct(-1).
				removeOldNotificationForDownload(downloadUrl);
				if (Constants.DEBUG) {
					Log.i("MyTest", "notifyBeforeDownload");
				}
				notifyBeforeDownload(context, app, downloadId);

				try {
					// Already checked,so we don't check any more.
					downloadId = DownloadUtil.download(context, downloadUrl, Constants.APK_MIME_TYPE, DEFAUL_TDOWNLOAD_DEST, formSaveName, app.getDisplayName(), appData, false);

				} catch (Exception e) {
					Log.e(DEBUG_TAG, "Download Error:", e);
					errorReason = PackageMode.ERROR_DATABASE_ERROR;
				}
				result = (downloadId > 0);
				if (result) {
					// Notifier.updateNotificationForDownload();
					Notifier.updateNotificationForFailedDownload();

					downloadAppInfo.setDownloadId(downloadId);
					long rowId = manager.addDownloadRecord(downloadAppInfo);
					result = (rowId > 0);
				}
				if (!result) {
					errorReason = PackageMode.ERROR_DATABASE_ERROR;
					if (downloadId > 0) {
						DownloadUtil.removeDownload(context, true, downloadId);
					}
					DbManager.getAppDbHandler().removeDownloadGames2(true, app.getGameId());
					notifyInsertDownloadFailed(context, app, downloadId);
				}
				if (Constants.DEBUG)
					Log.i(DEBUG_TAG, "[download]checkDownload valid true:downloadId" + downloadId);
			} else {
				if (Constants.DEBUG)
					Log.i(DEBUG_TAG, "[download]checkDownload valid false" + downloadId);
				if (forceDownload) {

				} else {
					result = false;
					errorReason = PackageMode.ERROR_DATABASE_ERROR;
				}
				//
			}
		}
		File saveDest = null;
		if (result) {
			saveDest = DownloadUtil.composeDestination(GameTingApplication.getAppInstance(), downloadUrl, DEFAUL_TDOWNLOAD_DEST, formSaveName);
			if (app.isDiffDownload())
				copyDiffApk(app, downloadId, saveDest.getAbsolutePath());
		}
		// Notify the caller that download successful or not .
		if (callback != null) {
			if (Constants.DEBUG)
				Log.i(DEBUG_TAG, "[download]result:" + result + " errorReason:" + errorReason + " downloadId:" + downloadId);
			if (result) {
				callback.onDownloadResult(downloadUrl, true, downloadId, saveDest.getAbsolutePath(), errorReason);
			} else {
				callback.onDownloadResult(downloadUrl, false, -1, null, errorReason);
			}
		}

		if (result) {
			notifyForDownload(app, context);
		}
		return downloadId;
	}

	private static void copyDiffApk(DownloadItemInput input, long downloadId, String saveDest) {

		PackageMode mode = new PackageMode(input.getGameId(), input.getDownloadUrl(), input.getPackageName(), input.getVersion(), input.getVersionInt(), downloadId, saveDest, input.getDisplayName(),
				PackageMode.DEFAULT_STATUS, -1, 0, -1, true);

		// DiffInfo info = new DiffInfo(mode);
		// DiffManager.getInstance().preDiff(GameTingApplication.getAppInstance(),
		// info);
	}

	private static String formSaveName(DownloadItemInput app) {
		return app.getDownloadUrl().hashCode() + ".apk";
		// return app.getSaveName() ;
	}

	private static DownloadAppInfo formDownloadAppInfo(DownloadItemInput app) {
		// current the downloadId is not correct.
		long downloadId = -1;
		DownloadAppInfo downloadAppInfo = new DownloadAppInfo(app.getPackageName(), app.getDisplayName(), app.getVersion(), app.getVersionInt(), app.getPublishDate(), app.getAction(),
				app.isNeedLogin(), PinyinUtil.getPinyin(app.getDisplayName()), app.getSign(), app.getSize(), downloadId, app.getDownloadUrl(), app.getIconUrl(), new Date().getTime(), app.getGameId(),
				app.isDiffDownload(), null);
		return downloadAppInfo;
	}

	/**
	 * 注意： <li>STATUS_PENDING包括PENDING和非用户暂停 <li>STATUS_PAUSED为用户暂停
	 * 
	 * @return
	 */
	public static Map<DownloadStatus, Integer> loadDownloadTasks() {
		HashMap<DownloadStatus, Integer> ret = new HashMap<DownloadStatus, Integer>();

		int runningSize = 0;
		int waittingSize = 0;
		int failedSize = 0;
		int successfulSize = 0;
		int pausedSize = 0;
		Context context = GameTingApplication.getAppInstance();

		AppManager manager = AppManager.getInstance(context);
		List<DownloadAppInfo> games = manager.getAndCheckDownloadGames();
		if (games != null && games.size() > 0) {

			int size = games.size();
			for (int i = 0; i < size; i++) {
				DownloadAppInfo o = games.get(i);
				DownloadStatus status = o.getStatus();
				if (status == DownloadStatus.STATUS_RUNNING) {
					runningSize++;
				} else if (status == DownloadStatus.STATUS_PENDING) {
					waittingSize++;
				} else if (status == DownloadStatus.STATUS_PAUSED) {
					if (DownloadReason.PAUSED_BY_APP != o.getReason()) {
						waittingSize++;
					} else {
						pausedSize++;
					}
				} else if (status == DownloadStatus.STATUS_FAILED) {
					failedSize++;
				} else if (status == DownloadStatus.STATUS_SUCCESSFUL) {
					successfulSize++;
				}
			}
		}

		ret.put(DownloadStatus.STATUS_PENDING, waittingSize);
		ret.put(DownloadStatus.STATUS_RUNNING, runningSize);
		ret.put(DownloadStatus.STATUS_FAILED, failedSize);
		ret.put(DownloadStatus.STATUS_SUCCESSFUL, successfulSize);
		ret.put(DownloadStatus.STATUS_PAUSED, pausedSize);
		return ret;
	}

	/**
	 * Download method.This is asynchronous request,you will get notified
	 * through <B>DownloadCallback</B>.
	 * 
	 * @param app
	 * @param callback
	 * 
	 * @see #DownloadCallback
	 */
	private static void download(final DownloadItemInput app, final DownloadCallback callback, final int index, final boolean forceDownload) {
		if (app == null) {
			return;
		}
		new HandlerThread("DownloadThread") {
			@Override
			public void run() {
				doDownload(app, callback, index, forceDownload);
			}
		}.start();
	}

	private static void notifyBeforeDownload(Context context, DownloadItemInput app, long downloadId) {

		File destinationFile = DownloadUtil.composeDestination(context, app.getDownloadUrl(), DEFAUL_TDOWNLOAD_DEST, formSaveName(app));
		String dest = Uri.fromFile(destinationFile).getPath();
		// report package status.
		notifyPackageStatusChanged(app.getGameId(), app.getDownloadUrl(), dest, app.getPackageName(), app.getSaveName(), app.getVersion(), app.getVersionInt(), downloadId,
				PackageMode.DOWNLOAD_PENDING, null, /** No reason */
				0L, /** No size info */
				-1L/** No size info */
		// null,/**No sign info*/
		// null /**No file md5 info*/
		);
		// Statics

	}

	private static void notifyInsertDownloadFailed(Context context, DownloadItemInput app, long downloadId) {

		File destinationFile = DownloadUtil.composeDestination(context, app.getDownloadUrl(), DEFAUL_TDOWNLOAD_DEST, formSaveName(app));
		String dest = Uri.fromFile(destinationFile).toString();
		// report package status.
		notifyPackageStatusChanged(app.getGameId(), app.getDownloadUrl(), dest, app.getPackageName(), app.getSaveName(), app.getVersion(), app.getVersionInt(), downloadId,
				PackageMode.DOWNLOAD_FAILED, null, /** No reason */
				0L, /** No size info */
				-1L /** No size info */
		// null,/**No sign info*/
		// null /**No file md5 info*/
		);
		// Statics
	}

	/**
	 * downloadId、currentSize、totalSize、dest、title都没有初始化
	 * 
	 * @param input
	 * @return
	 */
	private static PackageMode formDefaultPackageModeFromQueryInput(QueryInput input) {
		Long downloadId = -1L;
		String dest = null;
		String title = null;
		int status = PackageMode.DEFAULT_STATUS;
		Integer reason = null;
		long currentSize = 0;
		long totalSize = -1;

		// String sign = null ;
		// String fileMd5 = null ;
		boolean isDiffDownload = false;

		PackageMode mode = new PackageMode(input.gameId, input.downloadUrl, input.packageName, input.version, input.versionCode, downloadId, dest, title, status, reason, currentSize, totalSize,
				isDiffDownload);
		return mode;
	}

	private static PackageMode formDefaultPackageModeFromGameId(String gameId) {
		Long downloadId = -1L;
		String dest = null;
		String title = null;
		int status = PackageMode.DEFAULT_STATUS;
		Integer reason = null;
		long currentSize = 0;
		long totalSize = -1;

		// String sign = null ;
		// String fileMd5 = null ;
		boolean isDiffDownload = false;

		PackageMode mode = new PackageMode(gameId, null, null, null, -1, downloadId, dest, title, status, reason, currentSize, totalSize,/*
																																		 * sign
																																		 * ,
																																		 * fileMd5
																																		 * ,
																																		 */isDiffDownload);
		return mode;
	}

	private static InstalledAppInfo checkInstallStatus(List<InstalledAppInfo> installedList, QueryInput input) {
		if (determineCheckThrougthPackage(input)) {
			for (InstalledAppInfo item : installedList) {
				// 安装不再检查版本号
				if (input.packageName != null && input.packageName.equals(item.getPackageName()) /*
																								 * &&
																								 * input
																								 * .
																								 * version
																								 * .
																								 * equals
																								 * (
																								 * item
																								 * .
																								 * getVersion
																								 * (
																								 * )
																								 * )
																								 * &&
																								 * input
																								 * .
																								 * versionCode
																								 * ==
																								 * item
																								 * .
																								 * getVersionInt
																								 * (
																								 * )
																								 */
				) {
					return item;
				}
			}
		}
		return null;
	}

	private static void checkInstallingStatus(List<QueryInput> inputs, Map<QueryInput, PackageMode> ret) {
		for (QueryInput item : inputs) {
			checkInstallingStatus(ret, item);
		}
	}

	private static void checkInstallingStatus(Map<QueryInput, PackageMode> ret, QueryInput input) {
		/**
		 * //如果数据库保存的是正在安装，但是AppInstaller却没有记录，可能是程序被强制结束（可能安装成功也可能失败，
		 * 但是不应该显示正在安装）
		 */
		Set<InstallPacket> installDataSet = AppSilentInstaller.getInstnce().getInstallDataSet();
		if (installDataSet == null) {
			return;
		}

		PackageMode packageMode = ret.get(input);
		if (packageMode == null || packageMode.status != PackageMode.DEFAULT_STATUS) {
			return;
		}

		InstallPacket target = null;
		if (determineCheckThrougthGameId(input)) {
			for (InstallPacket item : installDataSet) {
				if (input.gameId != null && input.gameId.equals(item.getGameId())) {
					target = item;
					break;
				}
			}
		}
		if (target == null && input.packageName != null) {
			for (InstallPacket item : installDataSet) {
				if (input.packageName != null && input.packageName.equals(item.getPackageName())) {
					target = item;
					break;
				}
			}
		}

		if (target != null && packageMode != null) {

			InstallStatus status = target.getStatus();
			if (status == InstallStatus.INSTALLING) {
				packageMode.status = PackageMode.INSTALLING;
				packageMode.downloadDest = target.getFilepath();
				packageMode.downloadId = target.getDownloadId();
				/*
				 * String fileMd5 =
				 * FileHelper.getFileMd5(packageMode.downloadDest);
				 * packageMode.apkFileMd5 = fileMd5 ; PackageInfo pack =
				 * ApkUtil.
				 * getPackageForFile(packageMode.downloadDest,GameTingApplication
				 * .getAppInstance()); if(pack != null){ String newSignMd5 =
				 * AppUtil.getSignMd5(pack); packageMode.apkSign = newSignMd5 ;
				 * }
				 */

			} else if (status == InstallStatus.INSTALL_ERROR) {
				packageMode.status = PackageMode.INSTALL_FAILED;
				packageMode.reason = getFinalInstallErrorReason(target.getErrorReason());

				packageMode.downloadDest = target.getFilepath();
				packageMode.downloadId = target.getDownloadId();
				/*
				 * String fileMd5 =
				 * FileHelper.getFileMd5(packageMode.downloadDest);
				 * packageMode.apkFileMd5 = fileMd5 ;
				 * 
				 * PackageInfo pack =
				 * ApkUtil.getPackageForFile(packageMode.downloadDest
				 * ,GameTingApplication.getAppInstance()); if(pack != null){
				 * String newSignMd5 = AppUtil.getSignMd5(pack);
				 * packageMode.apkSign = newSignMd5 ; }
				 */

			} else if (status == InstallStatus.INSTALLED) {
				packageMode.status = PackageMode.INSTALLED;
				packageMode.downloadDest = target.getFilepath();
				packageMode.downloadId = target.getDownloadId();
				/*
				 * String fileMd5 =
				 * FileHelper.getFileMd5(packageMode.downloadDest);
				 * packageMode.apkFileMd5 = fileMd5 ; PackageInfo pack =
				 * ApkUtil.
				 * getPackageForFile(packageMode.downloadDest,GameTingApplication
				 * .getAppInstance()); if(pack != null){ String newSignMd5 =
				 * AppUtil.getSignMd5(pack); packageMode.apkSign = newSignMd5 ;
				 * }
				 */
			}
		}

	}

	/**
	 * 检查安装状态
	 * 
	 * @param inputs
	 * @param ret
	 */
	private static void checkInstallStatus(List<QueryInput> inputs, Map<QueryInput, PackageMode> ret) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		List<InstalledAppInfo> installedList = appDbHandler.getAllInstalledApps();

		if (installedList == null) {
			PackageManager pm = GameTingApplication.getAppInstance().getPackageManager();
			List<PackageInfo> pInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
			for (PackageInfo info : pInfos) {
				InstalledAppInfo iai = new InstalledAppInfo();
				iai.setPackageName(info.packageName);
				iai.setName(info.applicationInfo.name);
				installedList.add(iai);
			}

			if (null == installedList)
				return;
		}
		for (QueryInput input : inputs) {
			PackageMode packageMode = ret.get(input);
			// 优先使用安装状态
			if (packageMode.status != PackageMode.DEFAULT_STATUS) {
				continue;
			}
			InstalledAppInfo checkInstallStatus = checkInstallStatus(installedList, input);
			boolean installed = (checkInstallStatus != null);
			if (installed && packageMode.status != PackageMode.INSTALLED) {
				packageMode.status = PackageMode.INSTALLED;
				packageMode.title = checkInstallStatus.getName();
			}
		}
		long start = System.currentTimeMillis();
		// TODO
		// checkUpdatableStatus(installedList,inputs,ret);
		checkDiffUpdatableStatus(inputs, ret);
		long end = System.currentTimeMillis();
		if (Constants.DEBUG) {
			Log.i(DEBUG_TAG, "[checkDiffUpdatableStatus] consume" + (end - start));
		}

	}

	private static void checkInstallStatus(QueryInput input, Map<QueryInput, PackageMode> ret) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		InstalledAppInfo installedGame = appDbHandler.getInstalledApp(input.packageName);
		if (installedGame == null) {
			return;
		}
		List<InstalledAppInfo> installedList = Arrays.asList(installedGame);
		// List<QueryInput> inputs = Arrays.asList(input);

		PackageMode packageMode = ret.get(input);
		// 优先使用安装状态
		if (packageMode.status == PackageMode.DEFAULT_STATUS) {
			InstalledAppInfo app = checkInstallStatus(installedList, input);
			boolean installedStatus = (app != null);
			if (installedStatus && packageMode.status != PackageMode.INSTALLED) {
				packageMode.status = PackageMode.INSTALLED;
				packageMode.title = app.getName();
			}
		}
		// checkUpdatableStatus(installedList,inputs,ret);
		checkDiffUpdatableStatus(input, ret);

	}

	private static void checkDownloadStatus(QueryInput input, Map<QueryInput, PackageMode> ret) {
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		DownloadAppInfo downloadGame = null;
		if (input.gameId != null) {
			downloadGame = manager.getDownloadGameForId(input.gameId, false);
		} else if (input.downloadUrl != null) {
			downloadGame = manager.getDownloadGameForUrl(input.downloadUrl, false);
		}

		// DownloadAppInfo downloadGame = DbManager.getAppDbHandler()
		// .getDownloadGame(input.downloadUrl, input.gameId, false);
		if (downloadGame == null) {
			return;
		}
		PackageMode packageMode = ret.get(input);
		/*
		 * if (packageMode.status != DEFAULT_STATUS) { return; }
		 */
		List<DownloadAppInfo> asList = Arrays.asList(downloadGame);
		DownloadAppInfo target = checkDownloadStatus(asList, input, packageMode);
		if (target == null) {
		}

		checkCheckingStatus2(asList, Arrays.asList(input), ret);
	}

	private static void checkMergeStatus(List<QueryInput> inputs, HashMap<QueryInput, PackageMode> ret) {
		List<MergeMode> queryMergeRecordList = DbManager.getAppDbHandler().queryMergeRecord();
		if (queryMergeRecordList == null) {
			return;
		}

		for (QueryInput input : inputs) {
			checkMergeStatus(queryMergeRecordList, input, ret);
		}

	}

	private static void checkMergeStatus(QueryInput input, HashMap<QueryInput, PackageMode> ret) {

		List<MergeMode> queryMergeRecord = DbManager.getAppDbHandler().queryMergeRecord();
		if (queryMergeRecord == null) {
			return;
		}
		checkMergeStatus(queryMergeRecord, input, ret);

	}

	private static PackageMode queryFromSet(QueryInput input) {
		for (PackageMode mode : mergingSet) {
			if (input.gameId != null && input.gameId.equals(mode.gameId)) {
				return mode;
			} else if (input.downloadUrl != null && input.downloadUrl.equals(mode.downloadUrl)) {
				return mode;
			}
		}
		return null;
	}

	private static void checkMergeStatus(List<MergeMode> mergeList, QueryInput input, HashMap<QueryInput, PackageMode> ret) {

		MergeMode target = null;
		for (MergeMode mergeMode : mergeList) {
			if (input.gameId != null && input.gameId.equals(mergeMode.gameId)) {
				target = mergeMode;
				break;
			} else if (input.downloadUrl != null && input.downloadUrl.equals(mergeMode.downloadUrl)) {
				target = mergeMode;
				break;
			}
		}
		if (target != null) {
			PackageMode packageMode = ret.get(input);
			int oldStatus = packageMode.status;
			if (target.status == PackageMode.MERGING) {
				// TODO 意外结束，没有保存记录
				PackageMode queryFromSet = queryFromSet(input);
				if (queryFromSet == null) {
					packageMode.status = PackageMode.MERGE_FAILED;
					// TODO
					packageMode.reason = target.failedReason;
					packageMode.mergeFailedCount = target.failedCount;
				} else {
					packageMode.status = PackageMode.MERGING;
				}

			} else if (target.status == PackageMode.MERGE_FAILED) {
				packageMode.status = PackageMode.MERGE_FAILED;
				packageMode.reason = target.failedReason;
				packageMode.mergeFailedCount = target.failedCount;
			} else if (target.status == PackageMode.MERGED) {
				if (oldStatus == PackageMode.INSTALLED || oldStatus == PackageMode.UPDATABLE || oldStatus == PackageMode.UPDATABLE_DIFF || oldStatus == PackageMode.DEFAULT_STATUS
						|| oldStatus == PackageMode.DOWNLOADED) {
					packageMode.status = PackageMode.MERGED;
				}
			}

		}

	}

	/**
	 * 检查下载状态
	 * 
	 * @param inputs
	 * @param ret
	 */
	private static void checkDownloadStatus(List<QueryInput> inputs, HashMap<QueryInput, PackageMode> ret) {
		long start = System.currentTimeMillis();
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		List<DownloadAppInfo> downloadGames = manager.getAndCheckDownloadGames();
		long end = System.currentTimeMillis();
		if (Constants.DEBUG) {
			Log.d(DEBUG_TAG, "[checkDownloadStatus#getAndCheckDownloadGames] consume" + (end - start));
		}
		start = end;
		if (downloadGames == null) {
			return;
		}
		checkDownloadStatus(downloadGames, inputs, ret);
		// checkCheckingStatus2(downloadGames,inputs, ret);
	}

	/**
	 * 检查下载状态
	 * 
	 * @param downloadGames
	 * @param inputs
	 * @param ret
	 */
	private static void checkDownloadStatus(List<DownloadAppInfo> downloadGames, List<QueryInput> inputs, HashMap<QueryInput, PackageMode> ret) {

		if (downloadGames == null) {
			return;
		}
		for (QueryInput input : inputs) {
			PackageMode packageMode = ret.get(input);
			// 已经安装或者可以更新状态下，但是用户正在更新下载，显示下载
			/*
			 * if (packageMode.status != UNDOWNLOAD) { continue; }
			 */
			DownloadAppInfo target = checkDownloadStatus(downloadGames, input, packageMode);
			if (target == null) {
			}
		}

		checkCheckingStatus2(downloadGames, inputs, ret);

	}

	private static DownloadAppInfo checkDownloadStatus(List<DownloadAppInfo> downloadGames, QueryInput input, PackageMode packageMode) {
		DownloadAppInfo target = null;
		if (determineCheckThrougthGameId(input)) {
			for (DownloadAppInfo item : downloadGames) {
				if (input.gameId != null && input.gameId.equals(item.getGameId())) {
					target = item;
					break;
				}
			}
		}
		if (target == null && determineCheckThrougthDownloadUrl(input)) {
			for (DownloadAppInfo item : downloadGames) {
				if (input.downloadUrl != null && input.downloadUrl.equals(item.getDownloadUrl())) {
					target = item;
					break;
				}
			}
		}
		if (target == null) {
			return null;
		}

		DownloadStatus status = target.getStatus();

		if (status != null) {
			int finalReason = -1;
			DownloadReason reason = null;
			switch (status) {
			case STATUS_PENDING:
				packageMode.status = PackageMode.DOWNLOAD_PENDING;
				packageMode.currentSize = target.getCurrtentSize();
				packageMode.totalSize = target.getTotalSize();
				// TODO
				packageMode.isDiffDownload = target.isDiffUpdate();
				packageMode.downloadId = target.getDownloadId();
				if (target.getSaveDest() != null) {
					if (target.getSaveDest().startsWith("file")) {
						Uri parse = Uri.parse(target.getSaveDest());
						if (parse != null)
							packageMode.downloadDest = parse.getPath();
					} else {
						packageMode.downloadDest = target.getSaveDest();
					}
				}
				packageMode.title = target.getName();
				break;
			case STATUS_RUNNING:
				packageMode.status = PackageMode.DOWNLOAD_RUNNING;
				packageMode.currentSize = target.getCurrtentSize();
				packageMode.totalSize = target.getTotalSize();
				packageMode.isDiffDownload = target.isDiffUpdate();
				packageMode.downloadId = target.getDownloadId();
				if (target.getSaveDest() != null) {
					if (target.getSaveDest().startsWith("file")) {
						Uri parse = Uri.parse(target.getSaveDest());
						if (parse != null)
							packageMode.downloadDest = parse.getPath();
					} else {
						packageMode.downloadDest = target.getSaveDest();
					}
				}
				packageMode.title = target.getName();
				break;
			case STATUS_PAUSED:
				reason = target.getReason();
				finalReason = getFinalPauseReason(reason);
				packageMode.status = PackageMode.DOWNLOAD_PAUSED;
				packageMode.reason = finalReason;
				packageMode.currentSize = target.getCurrtentSize();
				packageMode.totalSize = target.getTotalSize();
				packageMode.isDiffDownload = target.isDiffUpdate();
				packageMode.downloadId = target.getDownloadId();
				if (target.getSaveDest() != null) {
					if (target.getSaveDest().startsWith("file")) {
						Uri parse = Uri.parse(target.getSaveDest());
						if (parse != null)
							packageMode.downloadDest = parse.getPath();
					} else {
						packageMode.downloadDest = target.getSaveDest();
					}
				}
				packageMode.title = target.getName();
				break;
			case STATUS_FAILED:
				reason = target.getReason();
				finalReason = getFinalPauseReason(reason);
				packageMode.status = PackageMode.DOWNLOAD_FAILED;
				packageMode.reason = finalReason;
				packageMode.currentSize = target.getCurrtentSize();
				packageMode.totalSize = target.getTotalSize();
				packageMode.isDiffDownload = target.isDiffUpdate();
				packageMode.downloadId = target.getDownloadId();
				if (target.getSaveDest() != null) {
					if (target.getSaveDest().startsWith("file")) {
						Uri parse = Uri.parse(target.getSaveDest());
						if (parse != null)
							packageMode.downloadDest = parse.getPath();
					} else {
						packageMode.downloadDest = target.getSaveDest();
					}
				}
				packageMode.title = target.getName();
				break;
			case STATUS_SUCCESSFUL:
				// 安装状态优先
				// 安装成功就删除下载记录，所以如果是更新下载并且安装成功，这里“应该”不会出现INSTALL_SUCCESSFUL
				if (packageMode.status == PackageMode.DEFAULT_STATUS || packageMode.status == PackageMode.INSTALLED || packageMode.status == PackageMode.UPDATABLE
						|| packageMode.status == PackageMode.UPDATABLE_DIFF) {
					packageMode.status = PackageMode.DOWNLOADED;
				}

				packageMode.isDiffDownload = target.isDiffUpdate();
				packageMode.currentSize = target.getCurrtentSize();
				packageMode.totalSize = target.getTotalSize();

				if (target.getSaveDest() != null) {
					if (target.getSaveDest().startsWith("file")) {
						Uri parse = Uri.parse(target.getSaveDest());
						if (parse != null)
							packageMode.downloadDest = parse.getPath();
					} else {
						packageMode.downloadDest = target.getSaveDest();
					}

				}
				/*
				 * String fileMd5 =
				 * FileHelper.getFileMd5(packageMode.downloadDest);
				 * packageMode.apkFileMd5 = fileMd5 ;
				 * 
				 * PackageInfo pack =
				 * ApkUtil.getPackageForFile(packageMode.downloadDest
				 * ,GameTingApplication.getAppInstance()); if(pack != null){
				 * String newSignMd5 = AppUtil.getSignMd5(pack);
				 * packageMode.apkSign = newSignMd5 ; }
				 */
				packageMode.downloadId = target.getDownloadId();
				packageMode.title = target.getName();
				break;
			default:
				break;
			}
		}

		return target;

	}

	/**
	 * This verion cannot implement .
	 * 
	 * @param inputs
	 * @param ret
	 */
	private static void checkUpdatableStatus(List<InstalledAppInfo> installedList, List<QueryInput> inputs, Map<QueryInput, PackageMode> ret) {
		// TODO
		for (QueryInput input : inputs) {
			PackageMode packageMode = ret.get(input);
			InstalledAppInfo t = checkUpdatableStatus(installedList, input);
			boolean updatable = (t != null);
			// 安装
			if (updatable && packageMode.status == PackageMode.INSTALLED) {
				packageMode.status = PackageMode.UPDATABLE;
				packageMode.version = t.getVersion();
				packageMode.versionCode = t.getVersionInt();
				// TODO 好像没有用
				// packageMode.apkFileMd5 = t.getFileMd5() ;
			}
		}
	}

	private static void checkDiffUpdatableStatus(QueryInput input, List<UpdatableAppInfo> updatableGames, Map<QueryInput, PackageMode> ret) {
		if (updatableGames == null) {
			return;
		}
		PackageMode packageMode = ret.get(input);
		for (UpdatableAppInfo updatableGame : updatableGames) {
			if (input.gameId != null && input.gameId.equals(updatableGame.getGameId())) {
				boolean diffUpdate = updatableGame.isDiffUpdate();
				if (packageMode.status == PackageMode.UPDATABLE || packageMode.status == PackageMode.DEFAULT_STATUS || packageMode.status == PackageMode.INSTALLED) {
					packageMode.status = diffUpdate ? PackageMode.UPDATABLE_DIFF : PackageMode.UPDATABLE;
				}
				packageMode.localVersion = updatableGame.getVersion();
				packageMode.localVersionCode = updatableGame.getVersionInt();

				packageMode.version = updatableGame.getNewVersion();
				packageMode.versionCode = updatableGame.getNewVersionInt();

				packageMode.totalApkSize = updatableGame.getNewSize();
				packageMode.pacthSize = updatableGame.getPatchSize();
				packageMode.downloadUrl = diffUpdate ? updatableGame.getPatchUrl() : updatableGame.getDownloadUrl();
			}

		}

	}

	private static void checkDiffUpdatableStatus(List<QueryInput> inputs, Map<QueryInput, PackageMode> ret) {
		// TODO
		List<UpdatableAppInfo> allUpdatableGames = DbManager.getAppDbHandler().getAllUpdatableGames();
		if (allUpdatableGames == null) {
			return;
		}
		for (QueryInput input : inputs) {
			checkDiffUpdatableStatus(input, allUpdatableGames, ret);
		}

	}

	private static void checkDiffUpdatableStatus(QueryInput input, Map<QueryInput, PackageMode> ret) {
		if (input.packageName == null) {
			return;
		}
		UpdatableAppInfo updatableGame = DbManager.getAppDbHandler().getUpdatableGame(input.packageName);
		if (updatableGame == null) {
			Log.d(DEBUG_TAG, String.format("checkDiffUpdatableStatus updatableGame is null for %s ", input.packageName));
			return;
		}
		PackageMode packageMode = ret.get(input);
		Log.d(DEBUG_TAG, String.format("checkDiffUpdatableStatus for %s ", updatableGame.getGameId()));
		if (input.gameId != null && input.gameId.equals(updatableGame.getGameId())) {
			boolean diffUpdate = updatableGame.isDiffUpdate();
			if (packageMode.status == PackageMode.UPDATABLE || packageMode.status == PackageMode.DEFAULT_STATUS || packageMode.status == PackageMode.INSTALLED) {
				packageMode.status = diffUpdate ? PackageMode.UPDATABLE_DIFF : PackageMode.UPDATABLE;
			}
			packageMode.localVersion = updatableGame.getVersion();
			packageMode.localVersionCode = updatableGame.getVersionInt();

			packageMode.version = updatableGame.getNewVersion();
			packageMode.versionCode = updatableGame.getNewVersionInt();

			packageMode.totalApkSize = updatableGame.getNewSize();
			packageMode.pacthSize = updatableGame.getPatchSize();
			packageMode.isDiffDownload = diffUpdate;
			packageMode.downloadUrl = diffUpdate ? updatableGame.getPatchUrl() : updatableGame.getDownloadUrl();
		}
	}

	private static InstalledAppInfo checkUpdatableStatus(List<InstalledAppInfo> installedList, QueryInput input) {
		if (determineCheckThrougthPackage(input)) {
			for (InstalledAppInfo item : installedList) {
				if (input.packageName != null && input.packageName.equals(item.getPackageName()) && input.versionCode > item.getVersionInt()) {
					return item;
				}
			}
		}
		return null;

	}

	private static boolean determineCheckThrougthPackage(QueryInput input) {
		if (!TextUtils.isEmpty(input.packageName) || !TextUtils.isEmpty(input.version) && input.versionCode > 0) {
			return true;
		}
		if (DEBUG) {
			Log.d(DEBUG_TAG, String.format("QueryInput's package info is not complete:package:%s,version:%s,versionCode:%s", input.packageName, input.version, input.versionCode));
		}
		return false;
	}

	private static boolean determineCheckThrougthGameId(QueryInput input) {
		if (!TextUtils.isEmpty(input.gameId)) {
			return true;
		}
		if (DEBUG) {
			Log.d(DEBUG_TAG, String.format("QueryInput's game id info is not complete:package:%s,version:%s,versionCode:%s", input.packageName, input.version, input.versionCode));
		}
		return false;
	}

	private static boolean determineCheckThrougthDownloadUrl(QueryInput input) {
		if (!TextUtils.isEmpty(input.downloadUrl)) {
			return true;
		}
		if (DEBUG) {
			Log.d(DEBUG_TAG, String.format("QueryInput's download url info is not complete:package:%s,version:%s,versionCode:%s", input.packageName, input.version, input.versionCode));
		}
		return false;
	}

	private static int checkDownload(DownloadItemInput input) {

		String status = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(status)) {
			// handler.sendEmptyMessage(MEDIA_UN_MOUNTED);
			return PackageMode.ERROR_DEVICE_NOT_FOUND;
		} else {
			File sdcard = Environment.getExternalStorageDirectory();
			File dir = new File(sdcard.getAbsolutePath() + "/" + Constants.DOWNLOAD_FOLDER);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			DEFAUL_TDOWNLOAD_DEST = dir.getAbsolutePath();

			long usableSpace = DeviceUtil.getUsableSpace();
			long targetSize = input.getSize();
			if (targetSize > 0 && usableSpace <= targetSize) {

				showToastOfLackOfSpace();

				return PackageMode.ERROR_INSUFFICIENT_SPACE;
				// handler.sendEmptyMessage(ERROR_INSUFFICIENT_SPACE);
			}
		}

		if (TextUtils.isEmpty(input.getDownloadUrl())) {
			return PackageMode.ERROR_PARAM_NO_URL;
		}
		if (TextUtils.isEmpty(input.getPackageName())) {
			return PackageMode.ERROR_PARAM_NO_PACKAGE_NAME;
		}
		if (TextUtils.isEmpty(input.getGameId())) {
			return PackageMode.ERROR_PARAM_NO_GAME_ID;
		}
		if (TextUtils.isEmpty(input.getVersion()) || input.getVersionInt() <= 0) {
			return PackageMode.ERROR_PARAM_NO_VERSION;
		}
		return -1;
	}

	private static void showToastOfLackOfSpace() {
		Looper.prepare();
		Toast.makeText(GameTingApplication.getAppInstance(), R.string.sdcard_lack_space, Toast.LENGTH_LONG).show();
		Looper.loop();
	}

	private static int checkDownload(long targetSize) {

		String status = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(status)) {
			// handler.sendEmptyMessage(MEDIA_UN_MOUNTED);
			return PackageMode.ERROR_DEVICE_NOT_FOUND;
		} else {
			File sdcard = Environment.getExternalStorageDirectory();
			File dir = new File(sdcard.getAbsolutePath() + "/" + Constants.DOWNLOAD_FOLDER);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			DEFAUL_TDOWNLOAD_DEST = dir.getAbsolutePath();

			long usableSpace = DeviceUtil.getUsableSpace();
			if (targetSize > 0 && usableSpace <= targetSize) {

				showToastOfLackOfSpace();
				return PackageMode.ERROR_INSUFFICIENT_SPACE;
				// handler.sendEmptyMessage(ERROR_INSUFFICIENT_SPACE);
			}
		}
		return -1;
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * 取消老的通知（比如先前下载然后重新下载）
	 * 
	 * @param downloadUrl
	 * @param downloadId
	 */
	private static void removeOldNotificationForDownload(String downloadUrl) {
		Notifier.removeNotificationForDelete(downloadUrl);
	}

	private static void notifyForDownload(final DownloadItemInput app, Context context) {

		BroadcaseSender sender = BroadcaseSender.getInstance(context);
		// TODO 是否需要修改呢?
		sender.notifyDownloadChanged(true, app.getPackageName());

		DownloadStatistics.addDownloadGameStatistics(GameTingApplication.getAppInstance(), app.getDisplayName(), true);
	}

	/**
	 * 取消下载后重新查询状态并且通知
	 * 
	 * @param downloadInfo
	 */
	private static void notifyForRemoveDownload(DownloadItemOutput downloadInfo) {
		PackageMode formPackageMode = formPackageMode(downloadInfo);
		if (formPackageMode == null) {
			return;
		}
		formPackageMode.status = PackageMode.DEFAULT_STATUS;
		// formPackageMode.reason = null;

		QueryInput target = new QueryInput(formPackageMode.packageName, formPackageMode.version, formPackageMode.versionCode, formPackageMode.downloadUrl, formPackageMode.gameId);
		// 重新查询状态
		Map<QueryInput, PackageMode> queryPakckageStatus = queryPackageStatus(target);

		PackageMode packageMode = queryPakckageStatus.get(target);
		if (packageMode.status != PackageMode.DEFAULT_STATUS) {
			formPackageMode.status = packageMode.status;
			Log.d(DEBUG_TAG, String.format("After removeDownloadGames game %S ,status is packageMode.status %s.", formPackageMode.title, formPackageMode.status));
		}

		notifyPackageStatusChanged(formPackageMode);
		//
		DownloadStatus status = downloadInfo.getStatus();
		if (DownloadStatus.STATUS_SUCCESSFUL == status) {
			// 有问题是：如果不更新数量，可能数量不一致，但是这样可能会重新显示通知
			Notifier.updateDownloadFinishedNotification();
		} else if (DownloadStatus.STATUS_FAILED == status) {
			// 有问题是：如果不更新数量，可能数量不一致，但是这样可能会重新显示通知
			Notifier.updateNotificationForFailedDownload();
		}
		// Notifier.removeNotificationForDelete(downloadInfo.getUrl());
		Notifier.updateNotificationForDownload();

		BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
		// TODO 是否需要修改呢?
		sender.notifyDownloadChanged(false, formPackageMode.packageName);
		// Statics?

	}

}
