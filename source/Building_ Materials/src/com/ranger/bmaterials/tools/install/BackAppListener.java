package com.ranger.bmaterials.tools.install;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.broadcast.NotificaionReceiver;
import com.ranger.bmaterials.broadcast.Notifier;
import com.ranger.bmaterials.download.DownloadService;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadListener;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.PackageMark;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.tools.ApkUtil;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.PackageInstallerCallback;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.ManagerActivity;
import com.ranger.bmaterials.work.DBTaskManager;
import com.ranger.bmaterials.work.FutureTaskManager;

/**
 * 有root权限才能静默安装
 * 
 * @author wangliang
 * 
 */
public class BackAppListener implements DownloadListener, PackageInstallerCallback {

	// private static final String TAG = AppSilentInstaller.TAG;
	private static final String TAG = "BackAppListener";
	Context context;

	private BackAppListener(Context context) {
		this.context = context;
		notifiedIds.clear();

	}

	static BackAppListener INSTANCE;

	public static BackAppListener getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BackAppListener(GameTingApplication.getAppInstance());
		}
		return INSTANCE;
	}

	public void onCreate() {
		startDownloadService();
	}

	/**
	 * 设置是否静默安装
	 * 
	 * @param auto
	 */
	public void changeAutoInstall(boolean auto) {
		if (auto) {
			startDownloadService();
			addListener();
			// 需要将已下载的游戏安装吗？
		} else {
			removeListener();
		}
	}

	@Deprecated
	private void addListener() {
		// DownloadUtil.addAllDownloadsListener(context, BackAppListener.this);
	}

	@Deprecated
	private void removeListener() {
		// DownloadUtil.removeDownloadsListener(context, this);
	}

	/**
	 * 启动下载服务
	 */
	@Deprecated
	private void startDownloadService() {
		Intent intent = new Intent();
		intent.setClass(context, DownloadService.class);
		context.startService(intent);
	}

	public void onDestroy() {
		try {
			// DownloadUtil.removeDownloadsListener(context, this);
			AppSilentInstaller installer = AppSilentInstaller.getInstnce();
			installer.onDestory();
			unregisterNotificationReceiver(context);
			INSTANCE = null;
		} catch (Exception e) {
		}

	}

	@Override
	public void onDownloadProcessing(List<DownloadItemOutput> items) {
		// DO nothing.
	}

	Set<Long> notifiedIds = new HashSet<Long>();

	NotificaionReceiver notificaionReceiver;

	private void registerNotificationReceiver(Context context) {

		if (notificaionReceiver == null) {
			notificaionReceiver = new NotificaionReceiver();
			IntentFilter intentFilter = new IntentFilter(NotificaionReceiver.ACTION_CANCLE_NOTIFICATION);
			context.registerReceiver(notificaionReceiver, intentFilter);
		}
	}

	private void unregisterNotificationReceiver(Context context) {
		if (notificaionReceiver != null) {
			context.unregisterReceiver(notificaionReceiver);
			notificaionReceiver = null;
		}
	}

	@Deprecated
	public void cancleNotification(DownloadAppInfo target) {
		if (target == null) {
			return;
		}
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (target.getDownloadUrl() != null) {
			nm.cancel(target.getDownloadUrl().hashCode());
		} else {
			nm.cancel((int) target.getDownloadId());
		}
	}

	@Deprecated
	public void cancleNotification(String url, long downloadId) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (url == null) {
			nm.cancel((int) downloadId);
		} else {
			nm.cancel(url.hashCode());
		}
	}

	@SuppressWarnings("deprecation")
	private void showNotification(Context context, long id, String title, String text, boolean cancleable, Class<?> clazz) {
		registerNotificationReceiver(context);

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_notifier;
		// String title ="["+output.getTitle()+"]正在下载";
		String caption = text;// "点击查看下载" ;
		// mContext.getResources().getString(R.string.notification_download_failed);

		Intent contentIntent = new Intent(context, clazz/* ManagerActivity.class */);
		contentIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID, id);
		contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		notification.when = System.currentTimeMillis(); // download.mLastMod;
		notification.setLatestEventInfo(context, title, caption, PendingIntent.getActivity(context, 0, contentIntent, 0));

		Intent delIntent = new Intent(NotificaionReceiver.ACTION_CANCLE_NOTIFICATION);
		delIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID, id);
		notification.deleteIntent = PendingIntent.getBroadcast(context, 0, delIntent, 0);
		notification.tickerText = title;// "["+output.getTitle()+"]正在下载";

		if (!cancleable) {
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_ONGOING_EVENT;
		} else {
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify((int) id, notification);

	}

	public static void showInstalledNotification(Context context, DownloadAppInfo app) {
		try {
			String title = null;
			String text = null;
			boolean cancleable = true;
			Class<?> clazz = null;
			title = "安装成功";
			// 暂时注释掉 稍后确定跳转逻辑
			// clazz = MyGamesLocalActivity.class;

			Notification notification = new Notification();

			notification.icon = R.drawable.ic_notifier;
			// String title ="["+output.getTitle()+"]正在下载";
			// mContext.getResources().getString(R.string.notification_download_failed);

			/** 添加通知栏点击启动应用功能。 20130815 **/

			// Intent contentIntent = new
			// Intent(context,clazz/*ManagerActivity.class*/);
			Intent contentIntent = new Intent(Intent.ACTION_MAIN);

			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> infos = pm.queryIntentActivities(contentIntent, 0);

			String intentLauncherName = "";

			for (ResolveInfo ri : infos) {

				contentIntent.addCategory(Intent.CATEGORY_LAUNCHER);

				String packagename = app.getPackageName();
				if (ri.activityInfo.packageName.equals(packagename)) {
					intentLauncherName = packagename;
					contentIntent.setClassName(packagename, ri.activityInfo.name);
					text = "[" + app.getName() + "]安装成功，点击启动";
					break;
				}
			}

			if (intentLauncherName.equals("")) {

				contentIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
				contentIntent.addCategory(Intent.CATEGORY_DEFAULT);

				if (!app.getExtra().equals("")) {
					intentLauncherName = app.getPackageName();
					contentIntent.setAction(app.getExtra());
				}
				text = "[" + app.getName() + "]安装成功，点击启动";
			}

			if (intentLauncherName.equals("")) {
				contentIntent.setClass(context, clazz);
				text = "点击查看游戏";
			}

			String caption = text;// "点击查看下载" ;
			/** 添加通知栏点击启动应用功能。 20130815 **/

			contentIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID, app.getDownloadId());
			// contentIntent.putExtra(name, value)
			contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

			notification.when = System.currentTimeMillis(); // download.mLastMod;
			notification.setLatestEventInfo(context, title, caption, PendingIntent.getActivity(context, 0, contentIntent, 0));

			Intent delIntent = new Intent(NotificaionReceiver.ACTION_CANCLE_NOTIFICATION);
			delIntent.putExtra(NotificaionReceiver.ARG_NOTIFICATION_ID, app.getDownloadId());
			notification.deleteIntent = PendingIntent.getBroadcast(context, 0, delIntent, 0);
			notification.tickerText = title;// "["+output.getTitle()+"]正在下载";

			if (!cancleable) {
				notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_ONGOING_EVENT;
			} else {
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
			}

			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			int id = 0;
			if (app.getDownloadUrl() != null) {
				id = app.getDownloadUrl().hashCode();
			} else {
				id = (int) app.getDownloadId();
			}
			nm.notify(id, notification);
			// showNotification(GameTingApplication.getAppInstance(),
			// app.getDownloadId(), title, text, cancleable,clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void doShow(DownloadItemOutput out,Notification notification) {
	//
	// NotificationManager nm = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	// if (out.getUrl() != null) {
	// nm.notify((int) out.getUrl().hashCode(), notification);
	// } else {
	// nm.notify((int) out.getDownloadId(), notification);
	// }
	// }

	/**
	 * 是否自动静默安装
	 * 
	 * @return
	 */
	private boolean checkAuto() {
		MineProfile profile = MineProfile.getInstance();
		boolean installAutomaticllyAfterDownloading = profile.isInstallAutomaticllyAfterDownloading();
		return installAutomaticllyAfterDownloading;
	}

	/**
	 * 是否自动弹出安装page
	 * 
	 * @return
	 */
	private boolean checkTip() {
		MineProfile profile = MineProfile.getInstance();
		boolean r = profile.isShowInstallTipAfterDownloading();
		return r;
	}

	void installAppNormal(String dest) {
		try {
			Uri fromFile = Uri.fromFile(new File(dest));
			// GameTingApplication.getAppInstance().getContentResolver().openFileDescriptor(Uri.parse(dest),
			// "r").close();
			GameTingApplication.getAppInstance().getContentResolver().openFileDescriptor(fromFile, "r").close();
		} catch (FileNotFoundException exc) {
			return;
		} catch (Exception e) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(dest), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		// com.android.packageinstaller/PackageInstallerActivity
		intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
		try {
			GameTingApplication.getAppInstance().startActivity(intent);
		} catch (ActivityNotFoundException ex) {
		}
	}

	public boolean checkApkValid(DownloadItemOutput out) {
		try {

			PackageInfo pack = ApkUtil.getPackageForFile(Uri.parse(out.getDest()).getPath(), GameTingApplication.getAppInstance());
			if (pack == null || pack.packageName == null) {
				Log.e(TAG, "[checkApk]严重错误，无法获取apk的信息 for " + out.getDest());
			}
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	private String updateData(DownloadItemOutput out, boolean isDiffUpdate, PackageInfo oldPack) {
		PackageMark appData = extraAppData(out);
		String packageName = appData.packageName;
		String gameId = appData.gameId;
		String path = Uri.parse(out.getDest()).getPath();

		PackageInfo newPack = ApkUtil.getPackageForFile(path, GameTingApplication.getAppInstance());
		String newPackageName = newPack.packageName;
		String newVerion = newPack.versionName;
		int newVersionCode = newPack.versionCode;
		// Reset appData
		if (!newPackageName.equals(packageName)) {
			Log.e(TAG, "[checkApk]严重错误，apk信息不一致" + newPackageName + " ");
			String newMark = PackageHelper.formDownloadAppData(newPackageName, newVerion, newVersionCode, gameId, isDiffUpdate);
			out.setAppData(newMark);
		}

		String fileMd5 = null;// 暂时没有用到FileHelper.getFileMd5(path);
		// Get apk sign(只有是更新(就是本地已经安装了这个应用)的情况下才检查apk签名)
		String sign = null;
		if (oldPack != null) {
			try {
				sign = AppUtil.getSignMd5(newPack);
			} catch (Exception e) {
				e.printStackTrace();
				sign = ApkUtil.getApkSignatureByFilePath(GameTingApplication.getAppInstance(), path);
			}
		}
		/**
		 * 更新数据库
		 */
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		manager.updateDownloadRecord(out.getDownloadId(), gameId, packageName, newPackageName, newVerion, newVersionCode, false, sign, fileMd5);

		return sign;

	}

	/**
	 * @param out
	 * @return
	 */
	public boolean checkApkIdentical(DownloadItemOutput out, boolean isDiffUpdate, PackageInfo oldPack) {
		try {
			String newSign = updateData(out, isDiffUpdate, oldPack);
			if (oldPack == null) {
				return true;
			} else {
				String signMd5 = AppUtil.getSignMd5(oldPack);
				if (signMd5 == null) {
					return true;
				} else if (signMd5.equals(newSign)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 检查签名是否一致
	 * 
	 * @param out
	 * @param newSignMd5
	 * @return
	 */
	private PackageMark extraAppData(DownloadItemOutput out) {
		String mark = out.getAppData();
		PackageMark markData = PackageHelper.getAppData(mark);
		return markData;
	}

	/**
	 * 对于普通下载和普通更新，提交到server，并且尝试安装
	 * 
	 * @param out
	 * @param mode
	 */
	public void notifyAndSubmitForNormal(DownloadItemOutput out, PackageMode mode) {
		// notifyForCheck(mode);
		try {
			notifyDownloadFinished(out);

			// boolean valid = checkApkValid(out);
			//
			// if (!valid) {
			// Notifier.showDownloadFailedNotification(out);
			// return;
			// }
			PackageMark appData = extraAppData(out);
			PackageInfo oldPack = AppUtil.loadPackageInfo(GameTingApplication.getAppInstance().getPackageManager(), appData.packageName);

			// boolean identical = checkApkIdentical(out, false, oldPack);
			// if (!identical) {
			// handler.sendEmptyMessage(DIFF_SIGN);
			// notifyForChecked(mode);
			// return;
			// }
			notifyForChecked(mode);
			notifyInstall(out);
			submit(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 正在检查apk签名，通知变化并且保存
	 * 
	 * @param mode
	 */
	private void notifyForCheck(PackageMode mode) {
		Log.i(TAG, "notifyForCheck " + mode);
		mode.status = PackageMode.CHECKING;
		PackageHelper.notifyPackageStatusChanged(mode);
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		manager.saveCheckStatus(mode.gameId, PackageMode.CHECKING);
	}

	/**
	 * 检查apk签名完成，通知变化并且保存
	 * 
	 * @param mode
	 */
	private void notifyForChecked(PackageMode mode) {
		Log.i(TAG, "notifyForChecked " + mode);

		mode.status = PackageMode.CHECKING_FINISHED;
		PackageHelper.notifyPackageStatusChanged(mode);
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		manager.saveCheckStatus(mode.gameId, PackageMode.CHECKING_FINISHED);

	}

	/**
	 * 检查apk签名完成，通知变化并且保存
	 * 
	 * @param mode
	 */
	private void notifyForCheckedWithStatus(PackageMode mode, int status) {
		Log.i(TAG, "notifyForChecked " + mode);

		mode.status = PackageMode.CHECKING_FINISHED;
		PackageHelper.notifyPackageStatusChanged(mode);
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		manager.saveCheckStatus(mode.gameId, status);

	}

	/**
	 * 对于增量更新下载，检查签名
	 * 
	 * @param out
	 * @param mode
	 */
	public void notifyAndSubmitForDiffUpdate(DownloadItemOutput out, PackageMode mode) {
		notifyForCheck(mode);
		try {
			notifyDownloadFinished(out);

			boolean valid = checkApkValid(out);
			if (!valid) {
				return;
			}
			PackageMark appData = extraAppData(out);
			PackageInfo oldPack = AppUtil.loadPackageInfo(GameTingApplication.getAppInstance().getPackageManager(), appData.packageName);
			boolean identical = checkApkIdentical(out, true, oldPack);
			if (!identical) {
				notifyForChecked(mode);
				return;
			}
			notifyForChecked(mode);
			notifyInstall(out);
			submit(out);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用通知栏通知下载完成
	 * 
	 * @param o
	 */
	private void notifyDownloadFinished(DownloadItemOutput o) {
		Notifier.showDownloadFinishedNotification(o);
	}

	private void notifyInstall(DownloadItemOutput o) {
		String mark = o.getAppData();
		PackageMark appData = PackageHelper.getAppData(mark);
		if (appData == null) {
			Log.e(TAG, "Parse Mark data Error,cannot install");
			return;
		}
		String packageName = appData.packageName;
		// 静默安装
		if (checkAuto()) {
			InstalledAppInfo appInfo = AppUtil.loadAppInfo(GameTingApplication.getAppInstance().getPackageManager(), packageName);
			if (appInfo != null) {
				if (com.ranger.bmaterials.app.Constants.DEBUG)
					Log.d(AppSilentInstaller.TAG, "[notifyInstall]已经安装 for " + packageName);
				// return ;
			}
			AppSilentInstaller installer = AppSilentInstaller.getInstnce();
			String dest = o.getDest();
			String filepath = Uri.parse(dest).getPath();

			if (com.ranger.bmaterials.app.Constants.DEBUG)
				Log.d(AppSilentInstaller.TAG, "[notifyInstall]静默安装 for " + packageName);
			installer.sendInstallRequest(context, o, this);

			// 非静默安装，但是下载完成后弹出安装界面
		} else if (checkTip()) {
			if (com.ranger.bmaterials.app.Constants.DEBUG)
				Log.d(TAG, "[notifyInstall]非静默安装 tryAutoInstall for " + mark);
			String dest = o.getDest();
			installAppNormal(dest);

			// NotificationManager nm = (NotificationManager)
			// context.getSystemService(Context.NOTIFICATION_SERVICE);
			// nm.cancel((int) o.getDownloadId());

		} else {

		}
	}

	private void submit(final DownloadItemOutput o) {

		DBTaskManager.submitTask(new Runnable() {

			@Override
			public void run() {
				try {
					AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
					DownloadAppInfo downloadGame = manager.getDownloadGame(/*
																			 * o.
																			 * getExtra
																			 * (
																			 * )
																			 * ,
																			 */o.getDownloadId(), false);

					/**
					 * 删除的不应该提交
					 */
					if (downloadGame == null) {
						Log.e(TAG, "[submit]错误Error for " + o.getAppData() + " " + o.getTitle());
						// 如果为null肯定出错，不应该为null的
						return;
					} else {
						Log.i(TAG, "[submit]没有错误 for " + o.getAppData() + " " + o.getTitle());
					}
					if (com.ranger.bmaterials.app.Constants.DEBUG)
						Log.i(TAG, "[submit] for" + o.getAppData() + " " + o.getTitle());
					FutureTaskManager task = FutureTaskManager.getInstance();
					task.submitGame(downloadGame.getGameId(), downloadGame.getPackageName(), downloadGame.getDownloadId());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void markInstallStatus(final InstallPacket pack) {

		DBTaskManager.submitTask(new Runnable() {

			@Override
			public void run() {
				InstallStatus status = pack.getStatus();
				long downloadId = pack.downloadId;
				String packageName = pack.packageName;
				String downloadUrl = pack.downloadUrl;
				String title = pack.name;
				int errorReason = pack.errorReason;
				try {
					AppManager manager = AppManager.getInstance(context);
					// DownloadItemOutput downloadInfo =
					// DownloadUtil.getDownloadInfo(GameTingApplication.getAppInstance(),
					// downloadId);
					String text = null;
					boolean cancleable = true;
					Class<?> clazz = null;
					int id = -1;
					id = downloadUrl.hashCode();
					if (status == InstallStatus.INSTALLED) {
						String gameId = manager.addInstalledGameRecord(packageName, downloadId);
						manager.removeDownloadRecordIfNecessary(packageName, downloadId);

						/*
						 * AppCache cache = AppCache.getInstance();
						 * cache.refreshDownload(context);
						 * cache.refreshInstall(context);
						 */

						// 在receiver中也会接受到，在这里没有必要
						FutureTaskManager taskManager = FutureTaskManager.getInstance();
						taskManager.registerGame(GameTingApplication.getAppInstance(), gameId);
						title = "[" + title + "]安装成功";
						text = "点击查看游戏";
						// clazz = MyGamesLocalActivity.class;
					} else if (status == InstallStatus.INSTALLING) {
						title = "[" + title + "]正在安装";
						text = "点击查看";
						cancleable = false;
						clazz = ManagerActivity.class;
					} else if (status == InstallStatus.INSTALL_ERROR) {
						title = "[" + title + "]安装失败";
						text = "点击查看";
						clazz = ManagerActivity.class;
					} else {
						NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						nm.cancel(id);
					}
					if (com.ranger.bmaterials.app.Constants.DEBUG)
						Log.e(TAG, "BackAppListener markInstallStatus for " + packageName + " status:" + status);
					AppManager appManager = AppManager.getInstance(context);
					appManager.updateGameInstallStatus(packageName, downloadId, status, errorReason);

					showNotification(GameTingApplication.getAppInstance(), id, title, text, cancleable, clazz);

				} catch (Exception e) {
					if (downloadUrl != null) {
						NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						nm.cancel((int) downloadUrl.hashCode());
					}
					Log.e(TAG, "markInstallStatus Error.", e);

				}

			}
		});
	}

	/**
	 * 监听AppInstaller的结果
	 */
	@Override
	public void onInstallerEvent(InstallPacket pack) {
		// Log.i(TAG,
		// "BackAppListener onInstallerEvent:"+packageName+" "+event);

		InstallStatus status = pack.getStatus();
		String packageName = pack.getPackageName();

		/**
		 * 安装成功之后"删除"下载记录
		 */
		switch (status) {
		case INSTALLED:
			// 不能异步这样，因为先要“删除”下载记录才能刷新download
			// list界面,因此如果这样做可能下载数据库记录删除但是app.db没有删除，此时已经通知界面刷新，
			// 就会出现问题，所以注释这一行,在markInstallStatus中完成
			handleInstalledEvent(packageName);
			markInstallStatus(pack);
			break;
		case INSTALLING:
			markInstallStatus(pack);
			break;
		case INSTALL_ERROR:
			markInstallStatus(pack);
			try {
				handler.sendEmptyMessage(pack.getErrorReason());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void parseError(int error) {
		try {
			switch (error) {
			case com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_ALREADY_EXISTS:
			case com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_DUPLICATE_PACKAGE:
				CustomToast.showToast(context, "此游戏已经安装");
				break;
			case com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_INSUFFICIENT_STORAGE:
				CustomToast.showToast(context, "您的手机空间不足，无法安装此游戏!");
				break;
			case com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_OLDER_SDK:
				CustomToast.showToast(context, "您的手机版本较低，无法安装此游戏!");
				break;
			case com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_MISSING_FEATURE:
				CustomToast.showToast(context, "您的手机无法安装此游戏!");
				break;
			case com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_PERMISSION:
				CustomToast.showToast(context, context.getString(R.string.refuse_root));
				MineProfile.getInstance().setInstallAutomaticllyAfterDownloading(false);
				break;
			default:
				CustomToast.showToast(context, "安装失败");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static final int DIFF_SIGN = -100;
	Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == DIFF_SIGN) {
				CustomToast.showToast(context, "签名不同，无法自动安装!");
			}
			parseError(msg.what);
		};
	};

	private void handleInstalledEvent(final String packageName) {
		BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
		sender.sendPreBroadcastForPackageEvent(true, packageName);

		// 不能异步这样，因为先要“删除”下载记录才能刷新download
		// list界面,因此如果这样做可能下载数据库记录删除但是app.db没有删除，此时已经通知界面刷新，
		// 就会出现问题，所以注释这一行,在markInstallStatus中完成

		/*
		 * DBTaskManager.submitTask(new Runnable() {
		 * 
		 * @Override public void run() { AppMananager manager =
		 * AppMananager.getInstance(context); String gameId =
		 * manager.addInstalledGameRecord(packageName);
		 * manager.removeDownloadRecordIfNecessary(packageName);
		 * 
		 * AppCache cache = AppCache.getInstance();
		 * cache.refreshDownload(context); cache.refreshInstall(context);
		 * 
		 * //在receiver中也会接受到，在这里没有必要 FutureTaskManager taskManager =
		 * FutureTaskManager.getInstance();
		 * taskManager.registerGame(GameTingApplication
		 * .getAppInstance(),gameId);
		 * 
		 * } });
		 */

	}
}
