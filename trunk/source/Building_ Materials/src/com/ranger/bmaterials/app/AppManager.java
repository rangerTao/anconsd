package com.ranger.bmaterials.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MyDownloadedGame;
import com.ranger.bmaterials.mode.OwnGameAction;
import com.ranger.bmaterials.mode.PackageMark;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.mode.UpdatableItem;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.Md5Tools;
import com.ranger.bmaterials.tools.MyLogger;
import com.ranger.bmaterials.tools.PinyinUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;
import com.ranger.bmaterials.tools.install.BackAppListener;
import com.ranger.bmaterials.tools.install.InstallPacket;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.MainHallActivity;
import com.ranger.bmaterials.view.CustomFragmentTabHost;

public class AppManager {
	
	private static final String TAG = "AppMananager";
	private static AppManager INSTANCE;
	private MyLogger logger = MyLogger.getLogger(this.getClass().getSimpleName());
	private Context context;
	public static String fileMimeType = Constants.APK_MIME_TYPE;
	public static final String DOWNLOAD_FOLDER = Constants.DOWNLOAD_FOLDER;
	private String defaultDownloadDest = null ;
	
	public synchronized void onDestroy(){
		INSTANCE = null ;
	}
	
	private AppManager(Context context) {
		this.context = GameTingApplication.getAppInstance();
		init();
	}

	public void init(){
		if(defaultDownloadDest == null){
			 String state = Environment.getExternalStorageState();
		        if (Environment.MEDIA_MOUNTED.equals(state)) {
		        	File sdcard = Environment.getExternalStorageDirectory();
		            File dir = new File(sdcard.getAbsolutePath()+"/"+DOWNLOAD_FOLDER);
		            if (!dir.exists()) {
		            	dir.mkdirs();
		            }
		            defaultDownloadDest = dir.getAbsolutePath();
		        }
		}
	}
	
	
	public synchronized static AppManager getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new AppManager(context);
		}
		if(INSTANCE.context == null){
			INSTANCE.context = GameTingApplication.getAppInstance();
		}
		return INSTANCE;
	}
	
	/**
	 * 保存下载文件名字
	 * @param url
	 * @return
	 */
	@Deprecated
	private String buildSaveName(String url){
		String saveName = Md5Tools.toMd5(url.getBytes(),true);
		
		return saveName ;//+".apk";
	}
	
	
	/**
	 *  在安装数据库中没有action的情况下调用
	 * @param context
	 * @param packageName
	 * @throws Exception
	 */
    public void startActivity(Context context,String packageName) throws Exception{
    	
    	PackageManager pm = context.getPackageManager();
    	Intent intent = null ;
    	Exception ex ;
    	Intent launcherIntent = AppUtil.getLauncherIntent(pm, packageName);
    	intent = launcherIntent ;
    	try {
        	context.startActivity(intent);
        	return ;
		} catch (Exception e) {
			ex = e ;
		}
		OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(pm,packageName);
		if(ownGameActionMode != null){
			Intent ownIntent = AppUtil.getOwnIntent(context, packageName, ownGameActionMode.action);
			passData(ownIntent);
			intent = ownIntent ;
		}
    	try {
        	if(intent != null){
        		context.startActivity(intent);
        		return ;
        	}
		} catch (Exception e) {
			ex = e ;
		}
    	throw new RuntimeException(ex);
    	
    }
    
    private void passData(Intent intent){
    	//TODO
		//ownIntent.putExtra(name, value);
    }
    /**
     * 能确定是联运的游戏，调用此方法启动
     * @param context
     * @param packageName
     * @param action
     * @param needLogin
     * @throws Exception
     */
    @Deprecated
	public void startOwnActivity(Context context, String gameId,
			String packageName, String action, boolean needLogin)
			throws Exception {
		StartGame internalStartGame = new StartGame(context,packageName, action, gameId, needLogin);
		internalStartGame.startGame();
	}
    
    
    private static final String CHECK_STATUS_FILE = "check_status_file";
    private static final String CHECK_STATUS = "check_status";
    
    /**
     * 保存检查签名的状态
     * @param gameId
     * @param status
     */
    public void saveCheckStatus(String gameId,int status){
    	Context context = GameTingApplication.getAppInstance();
    	SharedPreferences sp = context.getSharedPreferences(CHECK_STATUS_FILE, Context.MODE_PRIVATE);
    	sp.edit().putInt(gameId, status).commit();
    }
    /**
     * 删除检查签名的状态 
     * @param gameId
     */
    public void removeCheckStatus(String gameId){
    	Context context = GameTingApplication.getAppInstance();
    	SharedPreferences sp = context.getSharedPreferences(CHECK_STATUS_FILE, Context.MODE_PRIVATE);
    	sp.edit().remove(gameId).commit();
    }
    
    public void removeAllCheckStatus(){
    	Context context = GameTingApplication.getAppInstance();
    	SharedPreferences sp = context.getSharedPreferences(CHECK_STATUS_FILE, Context.MODE_PRIVATE);
    	sp.edit().clear().commit();
    }
    
    /**
     * 获取检查签名的状态 
     * @param gameId
     * @return
     */
    public int getCheckStatus(String gameId){
    	Context context = GameTingApplication.getAppInstance();
    	SharedPreferences sp = context.getSharedPreferences(CHECK_STATUS_FILE, Context.MODE_PRIVATE);
    	int ret = sp.getInt(gameId, -1);
    	return ret ;
    }
    public Map<String, ?> getCheckStatus(){
    	Context context = GameTingApplication.getAppInstance();
    	SharedPreferences sp = context.getSharedPreferences(CHECK_STATUS_FILE, Context.MODE_PRIVATE);
    	Map<String, ?> all = sp.getAll();
    	return all ;
    }
    
	/**
	 * 加上UPDATABLE更新吗？有个问题，根据package确定更新可能会出现游戏可以下载但是无法安装的情况
	 * @author wangliang
	 *
	 */
    @Deprecated
	public enum GameStatus{
		UPDATABLE,
		/**
		 * 已安装
		 */
		INSTALLED,
		/**
		 * 已经添加到下载（正在下载、暂停或者失败）
		 */
		DOWNLOADING,
		
		/**
		 * 下载完成
		 */
		DONWLOADED,
		/**
		 * 未下载
		 */
		UNDOWNLOAD;
	}
//	/**
//	 * 获取游戏当前状态
//	 * @param packageName
//	 * @return
//	 */
    @Deprecated
	public GameStatus getGameStatus(String packageName,String gameId) {
		try {
			InstalledAppInfo installedGame = getInstalledGame(packageName);
			if (installedGame != null) {
				return GameStatus.INSTALLED;
			}else {
				InstalledAppInfo ai = AppUtil.loadAppInfo(context.getPackageManager(), packageName);
				if(ai != null){
					Log.e(TAG, "[getGameStatus]Warn:数据库没有但是安装了"+packageName);
					//addInstalledGameRecord(packageName);
					updateInstalledGameRecord(ai);
					return GameStatus.INSTALLED;
				}
			}
			
			DownloadAppInfo downloadGame = getDownloadGameForId(gameId, false);
			//DownloadAppInfo downloadGame = getDownloadGame(packageName,version,false);
			if (downloadGame != null) {
				try {
					boolean delete = false ;
					String saveDest = downloadGame.getSaveDest();
					if(saveDest == null){
						delete = true ;
					}else {
						 DownloadStatus s = downloadGame.getStatus();
						 if(s == DownloadStatus.STATUS_SUCCESSFUL){
							 String path = Uri.parse(saveDest).getPath();
							 File file = new File(path);
							 delete = !file.exists();
						 }
						 
					}
					if(delete){
						BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
						sender.notifyDownloadChanged(false, downloadGame.getPackageName());
						AppDao appDbHandler = DbManager.getAppDbHandler();
						if(downloadGame.getDownloadId() > 0){
							DownloadUtil.removeDownload(context,true, downloadGame.getDownloadId());
						}
						appDbHandler.removeDownloadGame(true, /*downloadGame.getPackageName(),*/ downloadGame.getDownloadId());
						return GameStatus.UNDOWNLOAD;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				DownloadStatus status = downloadGame.getStatus();
				if (status == DownloadStatus.STATUS_SUCCESSFUL) {
					return GameStatus.DONWLOADED;
				}
				return GameStatus.DOWNLOADING;
			}
			return GameStatus.UNDOWNLOAD;
		} catch (Exception e) {
			return GameStatus.UNDOWNLOAD;
		}
		
	}
	
	static final int MEDIA_UN_MOUNTED = 300 ;
	static final int ERROR_INSUFFICIENT_SPACE = 301;
	
	@Deprecated
	Handler handler  = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MEDIA_UN_MOUNTED:
					CustomToast.showToast(context, context.getString(R.string.sdcard_unmounted));
					//Toast.makeText(GameTingApplication.getAppInstance(), "SDcard已经拔出或者不可访问，无法下载!", Toast.LENGTH_LONG).show();
					break;
	
				case ERROR_INSUFFICIENT_SPACE:
					CustomToast.showToast(context, context.getString(R.string.sdcard_lack_space));
					//Toast.makeText(GameTingApplication.getAppInstance(), "SD卡存储空间不足!", Toast.LENGTH_LONG).show();
					break;
			}
		};
	};
	
	private void checkDownloadSpace(final long targetSize){
		new Thread(){
			public void run() {
				try {
					String status = Environment.getExternalStorageState();
					if(!Environment.MEDIA_MOUNTED.equals(status)){
						handler.sendEmptyMessage(MEDIA_UN_MOUNTED);
					}else{
						long usableSpace = DeviceUtil.getUsableSpace();
						if(targetSize > 0 && usableSpace < targetSize){
							handler.sendEmptyMessage(ERROR_INSUFFICIENT_SPACE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			};
		}.start();
		
		
	}
	
	/**
	 * 请使用PackageHelper的downloadGame
	 * @param app
	 * @return
	 */
	@Deprecated
	public long downloadGame(DownloadItemInput app){
		if (Constants.DEBUG)Log.i("PopNumber", "downloadGame");
		if(TextUtils.isEmpty(app.getDownloadUrl()) || TextUtils.isEmpty(app.getPackageName()) || TextUtils.isEmpty(defaultDownloadDest) || TextUtils.isEmpty(app.getGameId())){
			return -1;
		}
		
		checkDownloadSpace(app.getSize());
		long downloadId = -1 ;
		try {
			BackAppListener.getInstance().cancleNotification(app.getDownloadUrl(), downloadId);
			downloadId = DownloadUtil.download(context, app.getDownloadUrl(), fileMimeType, 
					defaultDownloadDest,app.getSaveName(), app.getDisplayName(), app.getPackageName());
		
		} catch (Exception e) {
			logger.e("Download Error!", e);
		}
		if(downloadId > 0){
			//AppDao appDbHandler = DbManager.getAppDbHandler();
			//appDbHandler.removeDownloadGame(app.getDownloadUrl());
			
			DownloadAppInfo downloadAppInfo = new DownloadAppInfo(app.getPackageName(), app.getDisplayName(), app.getVersion(), app.getVersionInt(), 
					app.getPublishDate(), app.getAction(), app.isNeedLogin(),
					PinyinUtil.getPinyin(app.getDisplayName()), app.getSign(), app.getSize(), downloadId, app.getDownloadUrl(), app.getIconUrl(), 
					new Date().getTime(),app.getGameId(),app.isDiffDownload(),null);
			addDownloadRecord(downloadAppInfo);
			BroadcaseSender sender = BroadcaseSender.getInstance(context);
			sender.notifyDownloadChanged(true,app.getPackageName());
			
			DownloadStatistics.addDownloadGameStatistics(GameTingApplication.getAppInstance(), app.getDisplayName(),true);
			
			
		}else {
			
		}
		return downloadId ;
		
	}
	

	/**
	 * 更新静默安装状态
	 * @param packageName
	 * @param status
	 */
	public void updateGameInstallStatus(String packageName,long downloadId,InstallStatus status,int ...errorReason){
		
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.updateGameInstallStatus(packageName,downloadId, status,errorReason);
		
		/**
		 * 刷新
		 */
		AppCache cache = AppCache.getInstance();
		cache.refreshDownload(context);
		cache.refreshInstall(context);
		
		BroadcaseSender sender = BroadcaseSender.getInstance(context);
		sender.notifyInstallChanged();
		
		notifyPakcageStatusForInstall(downloadId, status, errorReason);
	}
	
	/**
	 * 收到安装状态改变，然后通知个界面
	 * @param downloadId
	 * @param status
	 * @param errorReason
	 */
	private void notifyPakcageStatusForInstall(long downloadId,InstallStatus status,int...errorReason){
		DownloadItemOutput app = DownloadUtil.getDownloadInfo(context, downloadId);
		
		if(app != null ){
			PackageMode mode = PackageHelper.formPackageMode(app);
			QueryInput queryInput = new QueryInput(mode.packageName, mode.version, mode.versionCode, mode.downloadUrl, mode.gameId);
			Map<QueryInput, PackageMode> queryPakckageStatus = PackageHelper.queryPackageStatus(queryInput);
			PackageMode packageMode = queryPakckageStatus.get(queryInput);
			PackageHelper.notifyPackageStatusChanged(packageMode);
			
			//TODO 
			if(status == InstallStatus.INSTALL_ERROR){
				mode.status = PackageMode.INSTALL_FAILED ;
				mode.reason = PackageHelper.getFinalInstallErrorReason(errorReason[0]) ;
				if(packageMode.status != mode.status){
					PackageHelper.notifyPackageStatusChanged(mode);
				}else {
					PackageHelper.notifyPackageStatusChanged(packageMode);
				}
			}else if(status == InstallStatus.INSTALLED){
				mode.status = PackageMode.INSTALLED ;
				if(packageMode.status != mode.status){
					PackageHelper.notifyPackageStatusChanged(mode);
				}else {
					PackageHelper.notifyPackageStatusChanged(packageMode);
				}
			}else if(status == InstallStatus.INSTALLING){
				mode.status = PackageMode.INSTALLING ;
				if(packageMode.status != mode.status){
					PackageHelper.notifyPackageStatusChanged(mode);
				}else {
					PackageHelper.notifyPackageStatusChanged(packageMode);
				}
			}
			
		}
		
	}

	
	/**
	 * 请使用PackageHelper的removeDownload
	 * @param apps
	 */
	@Deprecated
	public void removeDownloadGames(List<DownloadAppInfo> apps){
		if (Constants.DEBUG)Log.i("PopNumber", "[AppManager#removeDownloadGames]");
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			long[] ids = new long[apps.size()];
			String[] ps = new String[apps.size()];
			int i = 0;
			for (DownloadAppInfo app  : apps) {
				ids[i] = app.getDownloadId() ;
				ps[i] = app.getPackageName();
				i++;
			}
			if(Constants.DEBUG)Log.i("PopNumber", "[AppManager#removeDownloadGames]appDbHandler.removeDownloadGames");
			appDbHandler.removeDownloadGames(true,ids);
			if (Constants.DEBUG)Log.i("PopNumber", "[AppManager#removeDownloadGames]DownloadUtil.removeDownload");
			DownloadUtil.removeDownload(context,true, ids);
			
			BroadcaseSender sender = BroadcaseSender.getInstance(context);
			sender.notifyDownloadChanged(false,ps);
			int length = ps.length;
			for (int j = 0; j < length; j++) {
				BackAppListener.getInstance().cancleNotification(apps.get(j));
			}
		} catch (Exception e) {
			logger.e("Download Error!", e);
		}
	}
	
	
	
	/**请使用PackageHelper的pauseDownload
	 * Pause Download
	 * @param downloadIds
	 */
	@Deprecated
	public void pauseDownloadGames(long...downloadIds){
		DownloadUtil.pauseDownload(context, downloadIds);
	}
	
	/**
	 * 请使用PackageHelper的resumeDownload
	 * @param downloadIds
	 */
	@Deprecated
	public void resumeDownload(long...downloadIds){
		DownloadUtil.resumeDownload(context, downloadIds);
	}
	
	/**
	 * 请使用PackageHelper的restartDownload
	 * @param downloadIds
	 */
	@Deprecated
	public void restartDownload(long...downloadIds){
		DownloadUtil.restartDownload(context, downloadIds);
	}
	
	
	//private Map<String,Long> downloadeds = new HashMap<String, Long>();
	//private Set<String> pendingDownloadeds = new HashSet<String>();
	
	
	/**
	 * 请使用PackageHelper的downloadGame
	 * @param app
	 * @return
	 */
	@Deprecated
	public long downloadGameForUpdate(UpdatableAppInfo app){
		if(TextUtils.isEmpty(app.getDownloadUrl()) || TextUtils.isEmpty(app.getPackageName()) ||TextUtils.isEmpty(defaultDownloadDest)){
			return -1;
		}
		checkDownloadSpace(app.getNewSize());
		/*if(pendingDownloadeds.contains(app.getDownloadUrl())){
			return -1 ;
		}
		Long id = downloadeds.get(app.getDownloadUrl());*/
		/*if(id != null && id > 0){
			return id ;
		}*/
		
		long downloadId = -1 ;
		try {
			BackAppListener.getInstance().cancleNotification(app.getDownloadUrl(), downloadId);
			downloadId = DownloadUtil.download(context, app.getDownloadUrl(), fileMimeType, 
					defaultDownloadDest, buildSaveName(app.getDownloadUrl()), app.getName(), app.getPackageName());
		} catch (Exception e) {
			logger.e("Download Error!", e);
		}
		if(downloadId > 0){
			
			//AppDao appDbHandler = DbManager.getAppDbHandler();
			//appDbHandler.removeDownloadGame(app.getDownloadUrl());
			
			DownloadAppInfo downloadAppInfo = new DownloadAppInfo(app.getPackageName(), 
					app.getName(), app.getNewVersion(), app.getNewVersionInt(), 
					new Date().getTime(), app.getExtra(), app.isNeedLogin(),
					PinyinUtil.getPinyin(app.getName()), 
					app.getServerSign(), app.getNewSize(), 
					downloadId, app.getDownloadUrl(), app.getIconUrl(), 
					new Date().getTime(),app.getGameId(),app.isDiffUpdate(),null);
			addDownloadRecord(downloadAppInfo);
			BroadcaseSender sender = BroadcaseSender.getInstance(context);
			sender.notifyDownloadChanged(true,app.getPackageName());
			//downloadeds.put(app.getDownloadUrl(), downloadId);
			
			DownloadStatistics.addUpdateGameStatistics(GameTingApplication.getAppInstance(), app.getName());
		}else {
			
		}
		return downloadId ;
	}
	
	/**
	 * 老版本使用的方法（更新）,请使用PackageHelper的downloadGame
	 * @param apps
	 * @return 成功的package数组
	 */
	@Deprecated
	public String[] downloadGameForUpdate(List<UpdatableAppInfo> apps){
		//Log.d("DownloadLog", "downloadGameForUpdate");
		if(apps == null ||apps.size() == 0){
			return null ;
		}
		long spaceSize = 0 ;
		for (UpdatableAppInfo updatableAppInfo : apps) {
			spaceSize += updatableAppInfo.getNewSize();
		}
		checkDownloadSpace(spaceSize);
		
		long downloadId = -1 ;
		try {
			int size = apps.size();
			ArrayList<DownloadAppInfo> arrayList = new ArrayList<DownloadAppInfo>(size);
			ArrayList<String> arrayList2 = new ArrayList<String>(size);
			int i=0;
			for (UpdatableAppInfo app : apps) {
				if(TextUtils.isEmpty(app.getDownloadUrl()) || TextUtils.isEmpty(app.getPackageName())){
					continue ;
					//return false ;
				}
				BackAppListener.getInstance().cancleNotification(app.getDownloadUrl(), downloadId);
				downloadId = DownloadUtil.download(context, app.getDownloadUrl(), fileMimeType, 
						defaultDownloadDest, buildSaveName(app.getDownloadUrl()), app.getName(), app.getPackageName());
				if(downloadId >0){
					//AppDao appDbHandler = DbManager.getAppDbHandler();
					//appDbHandler.removeDownloadGame(app.getDownloadUrl());
					
					DownloadAppInfo downloadAppInfo = new DownloadAppInfo(app.getPackageName(), 
							app.getName(), app.getNewVersion(), app.getNewVersionInt(), 
							new Date().getTime(), app.getExtra(), app.isNeedLogin(),
							PinyinUtil.getPinyin(app.getName()), 
							app.getServerSign(), app.getNewSize(), 
							downloadId, app.getDownloadUrl(),app.getIconUrl(), 
							new Date().getTime(),app.getGameId(),app.isDiffUpdate(),null);
					arrayList.add(downloadAppInfo);
					arrayList2.add(app.getPackageName());
					//ps[i] = app.getPackageName();
					i++ ;
					DownloadStatistics.addUpdateGameStatistics(GameTingApplication.getAppInstance(), app.getName());
				}else {
					
				}
				
			}
			if(arrayList.size() > 0){
				DownloadAppInfo[] array = new DownloadAppInfo[arrayList.size()];
				String[] ps = new String[arrayList.size()];
				arrayList.toArray(array);
				arrayList2.toArray(ps);
				
				addDownloadRecords(array);
				BroadcaseSender sender = BroadcaseSender.getInstance(context);
				sender.notifyDownloadChanged(true,ps);
				return ps ;
			}
			
		} catch (Exception e) {
			logger.e("Download Error!", e);
		}
		return null ;
		
	}
	
	
	long addDownloadRecord(DownloadAppInfo info){
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			return appDbHandler.addDownloadGame(info);
		} catch (Exception e) {
			Log.e(TAG, "addDownloadRecord Error",e);
		}
		return -1 ;
		
	}
	int updateDownloadRecord(String oldUrl, String newUrl, boolean diffUpdate,
			long newSize){
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			return appDbHandler.updateDownloadGame(oldUrl, newUrl, diffUpdate, newSize);
		} catch (Exception e) {
			Log.e(TAG, "updateDownloadRecord Error",e);
		}
		return 0 ;
		
	}
	int updateDownloadRecord(long oldId, String newUrl, boolean diffUpdate,
			long newSize){
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			return appDbHandler.updateDownloadGame(oldId, newUrl, diffUpdate, newSize);
		} catch (Exception e) {
			Log.e(TAG, "updateDownloadRecord Error",e);
		}
		return 0 ;
		
	}
	
	
	
	/**
	 * 更新下载的数据(下载完成后调用)
	 * 1、更新下载包的packageName/version（曾经出现过包名和下载的包名不一致的情形）/fileMd5和签名
	 * 
	 * @param downloadId
	 * @param gameId
	 * @param packageName
	 * @param newPackage
	 * @param version
	 * @param versionCode
	 * @param isDiffUpdate
	 * @param sign
	 * @param fileMd5
	 */
	public void updateDownloadRecord(long downloadId,String gameId,String packageName,String newPackage,
			String version,int versionCode,boolean isDiffUpdate,
			String sign,String fileMd5){
		
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.updateDownload(downloadId,packageName,newPackage,version,versionCode,sign,fileMd5);
		String mark = PackageHelper.formDownloadAppData(newPackage, version, versionCode, gameId,isDiffUpdate);
		DownloadUtil.updateDownload(context, downloadId, mark);
	}
	
	private void addDownloadRecords(DownloadAppInfo... infos){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.addDownloadGames(infos);
	}
	
	public void updateNotifyStatus(String downloadUrl,boolean flag){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.updateDownloadNotifyStatus(downloadUrl, flag);
	}
	
	public boolean getNotifyStatus(String downloadUrl){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		return appDbHandler.getDownloadNotifyStatus(downloadUrl);
	}
	
	private void remove(long downloadId){
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			appDbHandler.removeDownloadGame(true, /*packageName,*/ downloadId);
			if(downloadId >0){
				DownloadUtil.removeDownload(context, true, downloadId);
			}
			//删除下载记录必须同时删除合并记录和检查记录
			PackageHelper.removeMergeGame(null, null, downloadId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private DownloadAppInfo checkDownload(DownloadAppInfo app,DownloadItemOutput file){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		if(app != null && !TextUtils.isEmpty( app.getPackageName())){
			long downloadId = app.getDownloadId();
			if(file == null ){
				appDbHandler.removeDownloadGame(true, /*packageName,*/ downloadId);
				//删除下载记录必须同时删除合并记录和检查记录
				PackageHelper.removeMergeGame(app.getGameId(), app.getDownloadUrl(), app.getDownloadId());
				return null ;
			}
			String appData = file.getAppData();
			String pkg = PackageHelper.splitPackageFromAppData(appData);
			
			if((downloadId == file.getDownloadId()) && app.getPackageName().equals(pkg)){
				if (Constants.DEBUG)Log.i(TAG, String.format("app package name:%s file extra:%s, app download id:%s,file download id:%s,",
						app.getPackageName(),pkg,downloadId,file.getDownloadId()));
				setDownloadData(app, file);
				return app;
			}
			
			if((downloadId == file.getDownloadId()) && !app.getPackageName().equals(pkg)){
				if(Constants.DEBUG)Log.e(TAG, downloadId+" Extra:"+pkg +" app package:"+app.getPackageName());
				remove(downloadId);
			}else if(app.getPackageName().equals(pkg) && (downloadId != file.getDownloadId())){
				if(Constants.DEBUG)Log.e(TAG, downloadId+" "+file.getDownloadId()+" Extra:"+pkg +" app package:"+app.getPackageName());
				
			}
		}else if(file != null){
			remove(file.getDownloadId());
		}
		return null ;
	}
	
	/**
	 * 
	 * @param downloadId
	 * @param includeDeleted
	 * @return
	 */
	public DownloadAppInfo getDownloadGame(/*String packageName,*/Long downloadId,boolean includeDeleted){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		DownloadAppInfo app = appDbHandler.getDownloadGame(/*packageName,*/downloadId,includeDeleted);
		DownloadItemOutput file = DownloadUtil.getDownloadInfo(context, downloadId);
		return checkDownload(app, file);
	}
	/**
	 * 
	 * @param fileMd5
	 * @param includeDeleted
	 * @return
	 */
	public DownloadAppInfo getDownloadGame(String fileMd5,boolean includeDeleted){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		DownloadAppInfo app = appDbHandler.getDownloadGame(/*packageName,*/fileMd5,includeDeleted);
		DownloadItemOutput file = null ;
		if(app != null){
			file = DownloadUtil.getDownloadInfo(context, app.getDownloadId());
		}
		return checkDownload(app, file);
		
	}
	/**
	 * 
	 * @param packageName
	 * @param version
	 * @param versionInt
	 * @param includeDeleted
	 * @return
	 */
	public DownloadAppInfo getDownloadGame(String packageName,String version,String versionInt,boolean includeDeleted){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		DownloadAppInfo app = appDbHandler.getDownloadGame(packageName,version,versionInt,includeDeleted);
		DownloadItemOutput file = null ;
		if(app != null){
			file = DownloadUtil.getDownloadInfo(context, app.getDownloadId());
		}
		return checkDownload(app, file); 
	}
	/**
	 * 根据gameid获取下载的game
	 * @param gameId
	 * @param includeDeleted
	 * @return
	 */
	public DownloadAppInfo getDownloadGameForId(String gameId,boolean includeDeleted){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		DownloadAppInfo app = appDbHandler.getDownloadGameForId(gameId,includeDeleted);
		DownloadItemOutput file = null ;
		if(app != null){
			file = DownloadUtil.getDownloadInfo(context, app.getDownloadId());
		}
		return checkDownload(app, file); 
	}
	/**
	 * 
	 * @param downloadUrl
	 * @param includeDeleted
	 * @return
	 */
	public DownloadAppInfo getDownloadGameForUrl(String downloadUrl,boolean includeDeleted){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		DownloadAppInfo app = appDbHandler.getDownloadGame(downloadUrl, null, includeDeleted);
		DownloadItemOutput file = null ;
		if(app != null){
			file = DownloadUtil.getDownloadInfo(context, app.getDownloadId());
		}
		return checkDownload(app, file); 
	}
	
	
	/**
	 * 获取下载的games
	 * @param includeDeleted
	 * @return
	 */
	List<DownloadAppInfo>  getDownloadGames(boolean includeDeleted){
		AppCache cache = AppCache.getInstance();
		if(cache.isInitialize()){
			List<DownloadAppInfo> download = cache.getDownloads();
			return download ;
		}else {
			List<DownloadAppInfo> downloadGamesFromDB = getDownloadGamesFromDB(includeDeleted);
			return downloadGamesFromDB ;
		}
	}
	/**
	 * 获取下载的并且未删除的games
	 * @return
	 */
	public List<DownloadAppInfo>  getAndCheckDownloadGames(){
		Log.i("MyTest", "getAndCheckDownloadGames");
		try {
			List<DownloadAppInfo> downloadGames = getDownloadGames(false);
			if(downloadGames != null){
				checkDownloads(downloadGames);
			}
			return downloadGames ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	/**
	 * 获取下载的games
	 * @param includeDeleted
	 * @return
	 */
	public List<DownloadAppInfo>  getAndCheckDownloadGames(boolean includeDeleted){
		try {
			List<DownloadAppInfo> downloadGames = getDownloadGames(includeDeleted);
			if(downloadGames != null){
				checkDownloads(downloadGames);
			}
			return downloadGames ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}

	/**
	 * 检查下载任务（两个下载表的数据一致是否、下载完成的任务是否文件已经删除）
	 * @param sender
	 * @param downloadGames
	 */
	private void checkDownloads(List<DownloadAppInfo> downloadGames) {
		for (Iterator iterator = downloadGames.iterator(); iterator
				.hasNext();) {
			DownloadAppInfo d = (DownloadAppInfo) iterator.next();
				try {
					boolean delete = false ;
					String saveDest = d.getSaveDest();
					if(saveDest == null){
						delete = true ;
					}else {
						 DownloadStatus status = d.getStatus();
						 if(status == DownloadStatus.STATUS_SUCCESSFUL){
							 String path = Uri.parse(saveDest).getPath();
							 File file = new File(path);
							 delete = !file.exists();
						 }
					}
					if(delete){
						Log.i(TAG, "checkDownloads remove "+d);
						iterator.remove();
						AppDao appDbHandler = DbManager.getAppDbHandler();
						if(d.getDownloadId() > 0){
							DownloadUtil.removeDownload(context,true, d.getDownloadId());
						}
						appDbHandler.removeDownloadGame(true, /*d.getPackageName(),*/ d.getDownloadId());
						BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
						sender.notifyDownloadChanged(false, d.getPackageName());
						//删除下载记录必须同时删除合并记录和检查记录
						PackageHelper.removeMergeGame(d.getGameId(), d.getDownloadUrl(), d.getDownloadId());
						//TODO
						notifyForFileMissed(d);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}
	}
	
	
	private void notifyForFileMissed(DownloadAppInfo d){
		QueryInput queryInput = new QueryInput(d.getPackageName(), d.getVersion(), d.getVersionInt(), d.getDownloadUrl(), d.getGameId());
		
		Map<QueryInput, PackageMode> map = PackageHelper.queryPackageStatus(queryInput);
		PackageMode packageMode = map.get(queryInput);
		
		PackageHelper.notifyPackageStatusChanged(d.getGameId(),
				d.getDownloadUrl(), d.getSaveDest(), d.getPackageName(), d.getName(),
				d.getVersion(), d.getVersionInt(), d.getDownloadId(), packageMode.status,
				null, -1L, 0L/*,packageMode.apkSign,packageMode.apkFileMd5*/);
		
	}
	
	/*public List<DownloadAppInfo>  getShowingDownloadGames(boolean deleteFile){
		BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
		try {
			List<DownloadAppInfo> downloadGames = getDownloadGames(false);
			if(downloadGames != null){
				for (Iterator iterator = downloadGames.iterator(); iterator
						.hasNext();) {
					DownloadAppInfo d = (DownloadAppInfo) iterator.next();
					//if(d.getStatus() == DownloadStatus.STATUS_SUCCESSFUL){
					try {
						boolean delete = false ;
						String saveDest = d.getSaveDest();
						if(saveDest == null){
							delete = true ;
						}else if(deleteFile){
							if(d.getStatus() == DownloadStatus.STATUS_SUCCESSFUL){
								String path = Uri.parse(saveDest).getPath();
								File file = new File(path);
								delete = !file.exists();
							}
							
						}
						if(delete){
							iterator.remove();
							AppDao appDbHandler = DbManager.getAppDbHandler();
							if(d.getDownloadId() > 0){
								DownloadUtil.removeDownload(context,true, d.getDownloadId());
							}
							appDbHandler.removeDownloadGame(true, d.getPackageName(), d.getDownloadId());
							sender.notifyDownloadChanged(false, d.getPackageName());
						}
					} catch (Exception e) {
						e.printStackTrace();
						
					}
					
				}
				//}
			}
			return downloadGames ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}*/
	
//	private void checkInstallStatus(List<DownloadAppInfo> games){
//		if(games == null){
//			return ;
//		}
//		AppSilentInstaller installer = AppSilentInstaller.getInstnce();
//		
//		Set<InstallPacket> installDataSet = installer.getInstallDataSet();
//		for (DownloadAppInfo app : games) {
//			InstallStatus installeStatus = app.getInstalleStatus();
//			//Log.i(TAG, app.getName()+"安装状态:"+installeStatus+" AppInstaller的数据："+installDataSet);
//			//如果数据库保存的是正在安装，但是AppInstaller却没有记录，可能是程序被强制结束（可能安装成功也可能失败，但是不应该显示正在安装）
//			if(installeStatus == InstallStatus.INSTALLING && !installer.getInstallPackage(app.getPackageName())){
//				//Log.e(TAG, app.getName()+"安装状态:"+installeStatus+",但是没有在AppInstaller");
//				app.setInstalleStatus(InstallStatus.INSTALL_ERROR);
//			}
//		}
//	}
	
	/**
	 * 获取正在静默安装的games
	 * @return
	 */
	public Set<InstallPacket> getSilentInstallList(){
		AppSilentInstaller installer = AppSilentInstaller.getInstnce();
		Set<InstallPacket> installDataSet = installer.getInstallDataSet();
		return installDataSet ;
	}
	
	public int getPopNumber(){
		AppCache cahce = AppCache.getInstance();
		if(cahce.isInitialize()){
			int popNumber = cahce.getPopNumber(context);
			return popNumber ;
		}else {
			return getPopNumberFromDB() ;
		}
	}
	
	protected int getPopNumberFromDB(){
		ArrayList<String> set = new ArrayList<String>();
		List<UpdatableAppInfo> allUpdatableGames = getAllUpdatableGames(true);
		int count = 0 ;
		if(allUpdatableGames!=null){
			for (UpdatableAppInfo updatableAppInfo : allUpdatableGames) {
				if(!updatableAppInfo.isIgnoreUpdate()){
					if(updatableAppInfo.getGameId() != null){
						set.add(updatableAppInfo.getGameId());
					}
					count++;
				}
			}
		}
			
		List<DownloadAppInfo> downloadGames = getAndCheckDownloadGames();
		int size = 0 ;
		
		if(downloadGames!=null){
			for (DownloadAppInfo d : downloadGames) {
				if(d.getGameId() != null){
					set.add(d.getGameId());
				}
			}
			size = downloadGames.size();
			count += size ;
		
		}
		
		if (Constants.DEBUG)Log.i("PopNumber", "[AppManager]Get PopNumber from DB:"+set.size()+" download size:"+size);
		return set.size() ;
	}
	
	
	List<DownloadAppInfo>  getDownloadGamesFromDB(boolean includeDeleted){
		
		AppDao appDbHandler = DbManager.getAppDbHandler();
		//List<DownloadAppInfo> allDownloadApps = appDbHandler.getAllDownloadGames(includeDeleted);
		
		List<DownloadAppInfo> allDownloadApps = appDbHandler.getAllDownloadGames(true);
		if(allDownloadApps == null || allDownloadApps.size() == 0){
			if(Constants.DEBUG){
				Log.i("MyTest", "getDownloadGamesFromDB return null");
			}
			//删除所有
			//DownloadUtil.removeDownload(context, true, null);
			//删除下载记录必须同时删除合并记录和检查记录
			///PackageHelper.removeAllMergeGames();
			return null ;
		}
		for (Iterator iterator = allDownloadApps.iterator(); iterator.hasNext();) {
			DownloadAppInfo info = (DownloadAppInfo) iterator.next();
			if(!includeDeleted && info.isMarkDeleted()){
				iterator.remove();
			}
		}
		
		if(Constants.DEBUG)Log.i(AppSilentInstaller.TAG, "getDownloadGamesFromDB "+includeDeleted+" allDownloadApps"+((allDownloadApps!=null)?allDownloadApps.size():0));
		int size ;
		if(allDownloadApps == null || (size = allDownloadApps.size()) == 0){
			return null ;
		}
		
		List<DownloadItemOutput> allDownloadFiles = DownloadUtil.getAllDownloads(context);
		if(allDownloadFiles == null){
			try {
				long[] ids = new long[size];
				for (int i = 0; i < size; i++) {
					ids[i] = allDownloadApps.get(i).getDownloadId();
				}
				appDbHandler.removeDownloadGames(true, ids);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null ;
		}
		
		List<DownloadAppInfo> notFoundList = new ArrayList<DownloadAppInfo>();
		boolean allFound = true; ;
		for (DownloadAppInfo app : allDownloadApps) {
			boolean itemFound = false ;
			long downloadId = app.getDownloadId();
			//如果allDownloadFiles为null，那么必定有个地方写错，这里不应该为null(不过可能数据库被delete)
			for (DownloadItemOutput file : allDownloadFiles) {
				if(/**(!TextUtils.isEmpty(app.getPackageName()) && app.getPackageName().equals(pkg)) && **/ downloadId == file.getDownloadId()){
					setDownloadData(app, file);
					itemFound = true ;
				}
			}
			if(!itemFound){
				allFound = false ;
				if(!includeDeleted){
					notFoundList.add(app);
				}
			}
		}
		for (DownloadAppInfo deleted : notFoundList) {
			try {
				allDownloadApps.remove(deleted);
				appDbHandler.removeDownloadGame(deleted.getDownloadUrl());
				if(deleted.getDownloadId() >0){
					DownloadUtil.removeDownload(context, true, deleted.getDownloadId());
				}
				//删除下载记录必须同时删除合并记录和检查记录
				PackageHelper.removeMergeGame(deleted.getGameId(), deleted.getDownloadUrl(), deleted.getDownloadId());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		if(Constants.DEBUG)Log.i(AppSilentInstaller.TAG, "getDownloadGamesFromDB "+includeDeleted+" finally return :allDownloadApps"+((allDownloadApps!=null)?size:0));
		return allDownloadApps ;
		
	}


	private void setDownloadData(DownloadAppInfo app, DownloadItemOutput file
			) {
		String data = file.getAppData();
		PackageMark appData = PackageHelper.getAppData(data);
		if(appData != null){
			String pkg = appData.packageName;
			boolean isDiffUpdate = appData.isDiffUpdate ;
			app.setDiffUpdate(isDiffUpdate);
		}else{
			//TODO
			//如果是老数据，那么可能就字段不全，也就不是增量更新
			app.setDiffUpdate(false);
		}
		app.setCurrtentSize(file.getCurrentBytes());
		app.setTotalSize(file.getTotalBytes());
		app.setSaveDest(file.getDest());
		app.setStatus(file.getStatus());
		app.setReason(file.getReason());
		
	}

	
	
	
	/**
	 * 移除app记录.remove app record(may not be game app).
	 * @param packageName
	 */
	public InstalledAppInfo removeInstallAppRecord(String packageName) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		InstalledAppInfo removeInstalledApp = appDbHandler.removeInstalledApp(packageName);
		return removeInstalledApp ;
	}

	/**
	 * 添加app安装记录（不一定是游戏）,当有应用安装时调用
	 * @param packageName
	 * @return 非null,表示能够确定是游戏，null表示不能确定
	 */
	public DownloadAppInfo addInstalledAppRecord(String packageName,String version,String versionInt) {
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			InstalledAppInfo app = AppUtil.loadAppInfo(context.getPackageManager(),packageName);
			String trimedName = StringUtil.trim(app.getName());
			app.setName(trimedName);
			app.setPinyinName(PinyinUtil.getPinyin(trimedName));
			
			if(app.getFileMd5() != null){
				DownloadAppInfo downloadGame = appDbHandler.getDownloadGame(app.getFileMd5(), true);
				if(downloadGame != null){
					//在下载数据库中存在说明是游戏（并且是在duoku安装）
					//appDbHandler.addInstalledApp(app,true);
					app.setNeedLogin(downloadGame.isNeedLogin());
					app.setExtra(downloadGame.getExtra());
					app.setGameId(downloadGame.getGameId());
					
					OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(context.getPackageManager(),packageName);
					if(ownGameActionMode != null){
						//app.setNeedLogin(ownGameActionMode.hasAccount);
						//app.setExtra(ownGameActionMode.action);
						//Log.i("","ownGameActionMode:"+ownGameActionMode.action+" downloadGame action:"+downloadGame.getExtra()) ;
					}
					appDbHandler.addMyInstalledApp(app);
					return downloadGame ;
				}
				
			}
			
			DownloadAppInfo downloadGame = appDbHandler.getDownloadGame(packageName,version,versionInt,true);
			if(downloadGame != null){
				//在下载数据库中存在说明是游戏（并且是在duoku安装）
				//appDbHandler.addInstalledApp(app,true);
				app.setNeedLogin(downloadGame.isNeedLogin());
				app.setExtra(downloadGame.getExtra());
				app.setGameId(downloadGame.getGameId());
				
				OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(context.getPackageManager(),packageName);
				if(ownGameActionMode != null){
					//app.setNeedLogin(ownGameActionMode.hasAccount);
					//app.setExtra(ownGameActionMode.action);
					//Log.i("","ownGameActionMode:"+ownGameActionMode.action+" downloadGame action:"+downloadGame.getExtra()) ;
				}
				
				appDbHandler.addMyInstalledApp(app);
				return downloadGame ;
			}else{
				//不在下载数据库中存在说明也 可能是游戏
				OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(context.getPackageManager(),packageName);
				if(ownGameActionMode != null){
					app.setNeedLogin(ownGameActionMode.hasAccount);
					app.setExtra(ownGameActionMode.action);
				}
				appDbHandler.addInstalledApp(app);
				
			}
			//Log.i(AppSilentInstaller.TAG, "addInstalledAppRecord "+app);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
		
	}
	
	/**
	 * 应用安装时通知各个界面状态改变
	 * @param app
	 */
	private void notifyPackageAdded(DownloadAppInfo app){
		
		PackageHelper.notifyPackageStatusChanged(app.getGameId(), 
				app.getDownloadUrl(),
				app.getSaveDest(),
				app.getPackageName(), 
				app.getName(), 
				app.getVersion(), 
				app.getVersionInt(), 
				app.getDownloadId(), 
				PackageMode.INSTALLED, 
				null,
				0L, 
				-1L 
				//null, 
				//null
				);
	}
	
	
	/**
	 * 添加app安装记录
	 * @param app
	 * @return
	 */
	public DownloadAppInfo addInstalledAppRecord(InstalledAppInfo app) {
		try {
			AppDao appDbHandler = DbManager.getAppDbHandler();
			String trimedName = StringUtil.trim(app.getName());
			app.setName(trimedName);
			app.setPinyinName(PinyinUtil.getPinyin(trimedName));
			
			if(app.getFileMd5() != null){
				DownloadAppInfo downloadGame = appDbHandler.getDownloadGame(app.getFileMd5(), true);
				if(downloadGame != null){
					//在下载数据库中存在说明是游戏（并且是在duoku安装）
					//appDbHandler.addInstalledApp(app,true);
					app.setNeedLogin(downloadGame.isNeedLogin());
					app.setExtra(downloadGame.getExtra());
					app.setGameId(downloadGame.getGameId());
					
					OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(context.getPackageManager(),app.getPackageName());
					if(ownGameActionMode != null){
					}
					appDbHandler.addMyInstalledApp(app);
					notifyPackageAdded(downloadGame);
					return downloadGame ;
				}
				
			}
			
			DownloadAppInfo downloadGame = appDbHandler.getDownloadGame(app.getPackageName(),app.getVersion(),String.valueOf(app.getVersionInt()),true);
			if(downloadGame != null){
				//在下载数据库中存在说明是游戏（并且是在duoku安装）
				//appDbHandler.addInstalledApp(app,true);
				app.setNeedLogin(downloadGame.isNeedLogin());
				app.setExtra(downloadGame.getExtra());
				app.setGameId(downloadGame.getGameId());
				
				OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(context.getPackageManager(),app.getPackageName());
				if(ownGameActionMode != null){
					//app.setNeedLogin(ownGameActionMode.hasAccount);
					//app.setExtra(ownGameActionMode.action);
					//Log.i("","ownGameActionMode:"+ownGameActionMode.action+" downloadGame action:"+downloadGame.getExtra()) ;
				}
				
				appDbHandler.addMyInstalledApp(app);
				notifyPackageAdded(downloadGame);
				return downloadGame ;
			}else{
				//不在下载数据库中存在说明也 可能是游戏
				OwnGameAction ownGameActionMode = AppUtil.tryLoadOwnGame(context.getPackageManager(),app.getPackageName());
				if(ownGameActionMode != null){
					app.setNeedLogin(ownGameActionMode.hasAccount);
					app.setExtra(ownGameActionMode.action);
				}
				appDbHandler.addInstalledApp(app);
				
			}
			//Log.i(AppSilentInstaller.TAG, "addInstalledAppRecord "+app);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
		
	}
	
	
	/**
	 * 添加app安装记录（一定是游戏,并且是从duoku安装）,当有应用静默安装时调用
	 * @param packageName
	 * @return
	 */
	public String addInstalledGameRecord(String packageName,Long downloadId) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		InstalledAppInfo app = AppUtil.loadAppInfo(context.getPackageManager(),packageName);
		String trimedName = StringUtil.trim(app.getName());
		app.setName(trimedName);
		app.setPinyinName(PinyinUtil.getPinyin(trimedName));
		//appDbHandler.addInstalledApp(app,true);
		
		//Log.i(AppSilentInstaller.TAG, "addInstalledGameRecord "+app);
		DownloadAppInfo downloadGame = appDbHandler.getDownloadGame(/*packageName,*/downloadId,true);
		if(downloadGame == null){
			//if(debug)throw new RuntimeException("Error!");
		}else{
			app.setNeedLogin(downloadGame.isNeedLogin());
			app.setExtra(downloadGame.getExtra());
			app.setGameId(downloadGame.getGameId());
		}
		appDbHandler.addMyInstalledApp(app);
		return (downloadGame==null)?null:downloadGame.getGameId() ;
	}
	
	public void updateInstalledGameRecord(InstalledAppInfo appInfo) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.addInstalledApp(appInfo, true);
	}
	
	
	/**
	 * 安装程序之后删除下载记录(非常小心，只有当唯一确定只有一个相同的package是调用)
	 * @param packageName
	 */
	public void removeDownloadRecordIfNecessary(String packageName) {
		
		AppDao appDbHandler = DbManager.getAppDbHandler();
		MineProfile profile = MineProfile.getInstance();
		boolean deletePkgAfterInstallation = profile.isDeletePkgAfterInstallation();
		if(deletePkgAfterInstallation){
			if(packageName != null){
				DownloadUtil.removeDownload(context,packageName,true);
			}
			appDbHandler.removeDownloadGame(true, packageName);
		}else{
			appDbHandler.removeDownloadGame(false, packageName);
		}
	}
	/**
	 * 安装程序之后删除下载记录
	 * @param packageName
	 */
	public void removeDownloadRecordIfNecessary(String packageName,Long downloadId) {
		
		AppDao appDbHandler = DbManager.getAppDbHandler();
		
		MineProfile profile = MineProfile.getInstance();
		boolean deletePkgAfterInstallation = profile.isDeletePkgAfterInstallation();
		//应该删除下载数据库中的记录
		if(downloadId >0  && deletePkgAfterInstallation){
			DownloadUtil.removeDownload(context,true, downloadId);
			//但是app数据库中的记录应该保留（添加删除标记）
			appDbHandler.removeDownloadGame(true,/* packageName,*/ downloadId);
		}else {
			appDbHandler.removeDownloadGame(false,/* packageName,*/ downloadId);
		}
		
		DbManager.getAppDbHandler().removeMergeRecord(null,null,downloadId);
		
	}
	
	/**
	 * 更新游戏的安装状态
	 * @param packageName
	 */
	@Deprecated
	public void updateDownloadRecordIfNecessary(String packageName) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		/**
		 * 更新游戏的安装状态
		 */
		appDbHandler.updateDownloadGameRecord(packageName, true);
	}
	
	/**
	 * 当有app卸载时通知状态变化
	 * @param app
	 */
	public void notifyForAppRemoved(InstalledAppInfo app) {
		DownloadAppInfo downloadGame = null;
		if(app.getFileMd5() != null){
			downloadGame = getDownloadGame(app.getFileMd5(), true);
		}
		if(downloadGame == null){
			downloadGame = getDownloadGame(app.getPackageName(), app.getVersion(), String.valueOf(app.getVersionInt()), true);
		}
		
		if(downloadGame != null){
			QueryInput queryInput = new QueryInput(app.getPackageName(), app.getVersion(), app.getVersionInt(), downloadGame.getDownloadUrl(),
					app.getGameId());
			Map<QueryInput, PackageMode> queryPackageStatus = PackageHelper.queryPackageStatus(queryInput);
			PackageHelper.notifyPackageStatusChanged(queryPackageStatus.get(queryInput));
		}else {
			QueryInput queryInput = new QueryInput(app.getPackageName(), app.getVersion(), app.getVersionInt(), 
					null,
					null);
			Map<QueryInput, PackageMode> queryPackageStatus = PackageHelper.queryPackageStatus(queryInput);
			PackageHelper.notifyPackageStatusChanged(queryPackageStatus.get(queryInput));
		}
	}
	
	/**
	 * 获取可更新的游戏
	 * @param excludeIgnored 是否排除忽略的
	 * @return
	 */
	public List<UpdatableAppInfo> getAllUpdatableGames(boolean excludeIgnored){
		AppCache cache = AppCache.getInstance();
		if(cache.isInitialize()){
			List<UpdatableAppInfo> list = cache.getUnpdatable();
			
			if(excludeIgnored && list != null){
				filterData(list,true);
			}
			return list ;
		}else {
			return getAllUpdatableGamesFromDB(excludeIgnored);
		}
	}
	/**
	 * 获取可更新的游戏
	 * @param excludeIgnored 是否排除忽略的
	 * @return
	 */
	 protected List<UpdatableAppInfo> getAllUpdatableGamesFromDB(boolean excludeIgnored){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		List<UpdatableAppInfo> allUpdatableGames = appDbHandler
				.getAllUpdatableGames();
		if(excludeIgnored && allUpdatableGames != null){
			filterData(allUpdatableGames,true);
		}
		return allUpdatableGames ;
	}
	
	/**
	 * 获取可更新的游戏
	 * @param packageName
	 * @return
	 */
	public UpdatableAppInfo getUpdatableGame(String packageName){
		AppCache cache = AppCache.getInstance();
		List<UpdatableAppInfo> unpdatable = cache.getUnpdatable();
		if(unpdatable != null){
			for (UpdatableAppInfo updatableAppInfo : unpdatable) {
				if(packageName.equals(updatableAppInfo.getPackageName())){
					return updatableAppInfo ;
				}
			}
		}
		AppDao appDbHandler = DbManager.getAppDbHandler();
		return appDbHandler.getUpdatableGame(packageName);
	}
	
	
	/**
	 * 获取忽略更新的游戏
	 * @param excludeDownload 是否排除已经下载或者正在下载
	 * @return
	 */
	public List<UpdatableAppInfo> getIgnoredGames(boolean excludeDownload){
		List<UpdatableAppInfo> allApps = getAllUpdatableGames(false);
		filterData(allApps, false);
		if(excludeDownload && allApps!=null){
			List<DownloadAppInfo> downloadGames = getDownloadGames(false);
			if(downloadGames != null){
				Iterator<UpdatableAppInfo> iterator = allApps.iterator();
				while (iterator.hasNext()) {
					UpdatableAppInfo info = (UpdatableAppInfo) iterator
							.next();
					for (DownloadAppInfo file : downloadGames) {
						try {
							//不用判断签名，因为能够下载肯定下载数据库签名正确，能够更新，已经在更新表中判断了
							if(file.getPackageName().equals(info.getPackageName()) && file.getGameId().equals(info.getGameId())){
								iterator.remove();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			}
			
		}
		return allApps ;
	}
	
	/**
	 *  获取可更新的并且没有忽略更新的游戏
	 * @param excludeDownload 是否排除已经下载或者正在下载
	 * @return
	 */
	public List<UpdatableAppInfo> getUpdatableGames(boolean excludeDownload){
		List<UpdatableAppInfo> updatableGames = getAllUpdatableGames(true);
		
		if(excludeDownload && updatableGames!= null){
			List<DownloadAppInfo> downloadGames = getDownloadGames(false);
			if(downloadGames != null){
				Iterator<UpdatableAppInfo> iterator = updatableGames.iterator();
				while (iterator.hasNext()) {
					UpdatableAppInfo info = (UpdatableAppInfo) iterator
							.next();
					for (DownloadAppInfo file : downloadGames) {
						try {
							//不用判断签名，因为能够下载肯定下载数据库签名正确，能够更新，已经在更新表中判断了
							if(file.getPackageName().equals(info.getPackageName()) && file.getGameId().equals(info.getGameId())){
//								iterator.remove();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		}
		return updatableGames ;
	}
	/**
	 * 获取所有可更新的游戏（包括可以更新的和已经忽略的）
	 * @return
	 */
	public List<UpdatableAppInfo> getAllUpdatableGames(){
		List<UpdatableAppInfo> updatableGames = getAllUpdatableGames(false);
		/*if(updatableGames!= null){
			List<DownloadAppInfo> downloadGames = getDownloadGames(false);
			if(downloadGames != null){
				Iterator<UpdatableAppInfo> iterator = updatableGames.iterator();
				while (iterator.hasNext()) {
					UpdatableAppInfo info = (UpdatableAppInfo) iterator
							.next();
					for (DownloadAppInfo file : downloadGames) {
						try {
							if(file.getPackageName().equals(info.getPackageName()) && file.getGameId().equals(info.getGameId())){
								iterator.remove();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		}*/
		return updatableGames ;
	}
	
	/**
	 * 过滤数据
	 * @param data
	 * @param excludeIgnoredOrOtherWise true排除忽略的，false排除未忽略的
	 */
	private void filterData(List<UpdatableAppInfo> data,boolean excludeIgnoredOrOtherWise){
		Iterator<UpdatableAppInfo> iterator = data.iterator();
		while (iterator.hasNext()) {
			UpdatableAppInfo updatableAppInfo = (UpdatableAppInfo) iterator
					.next();
			boolean ignoreUpdate = updatableAppInfo.isIgnoreUpdate();
			if(excludeIgnoredOrOtherWise && ignoreUpdate){
				iterator.remove();
			}else if(!excludeIgnoredOrOtherWise && !ignoreUpdate){
				iterator.remove();
			}else {
				try {
					Drawable icon = AppUtil.loadApplicationIcon(context.getPackageManager(), updatableAppInfo.getPackageName());
					updatableAppInfo.setDrawable(icon);
					
					if(TextUtils.isEmpty(updatableAppInfo.getPinyinName())){
						String trimedName = StringUtil.trim(updatableAppInfo.getName());
						updatableAppInfo.setPinyinName(PinyinUtil.getPinyin(trimedName));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	@Deprecated
	public interface OnAppStatusChangedListener{
		void onIgnoredStatusChanged(boolean ignored,String...packageNames);
	}
	
	private List<OnAppStatusChangedListener> observers ;
	
	public synchronized void addOnAppStatusChangedListener(OnAppStatusChangedListener o){
		if(observers == null){
			observers = new ArrayList<AppManager.OnAppStatusChangedListener>();
		}
		observers.add(o);
	}
	
	public synchronized void removeOnAppStatusChangedListener(OnAppStatusChangedListener o){
		if(observers != null){
			observers.remove(o);
		}
	}

	/**
	 * 更改可更新的app忽略的忽略状态（忽略或者取消忽略）
	 * @param ignored
	 * @param packageName
	 */
	public void updateIgnoreState(boolean ignored,String packageName){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.updateIgnoreState(packageName, ignored);
		notifyAppIgnoreStatusChanged(ignored, packageName);
	}

	/**
	 * 更改可更新的所有app忽略的忽略状态（忽略或者取消忽略）
	 * @param ignored
	 * @param packageNames 这些包的名字
	 */
	public void updateAllIgnoreState(boolean ignored,String...packageNames){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.updateIgnoreState(null, ignored);
		notifyAppIgnoreStatusChanged(ignored, packageNames);
	}
	
	/**
	 * 通知可更新的游戏忽略状态发生改变
	 * @param ignored
	 * @param packageNames
	 */
	private synchronized void notifyAppIgnoreStatusChanged(boolean ignored,String...packageNames){
		AppCache cache = AppCache.getInstance();
		cache.refreshUpdatable(context);
		
		BroadcaseSender sender = BroadcaseSender.getInstance(context);
		sender.notifyIgnoredStatedChanged(ignored, packageNames);
		
	}
	
	
	/**
	 * 获取本地已经安装的应用并且保存到数据库
	 * @return
	 */
	public /*List<InstalledAppInfo>*/void  loadAndSaveInstalledApps(boolean getmd5) {
		List<InstalledAppInfo> installedApps = loadInstalledList(getmd5);
		
		//Save to the database.
		AppDao appDbHandler = DbManager.getAppDbHandler();
		
		List<InstalledAppInfo> olds = appDbHandler.getAllInstalledApps();
		//Find which is in database but is deleted,
		//and delete them from database.
		appDbHandler.saveAllInstalledApps(installedApps);
		if(olds != null){
			for (InstalledAppInfo o : olds) {
				boolean found = find(installedApps, o);
				if(!found && o != null){
					appDbHandler.removeDeletedApp(o.getPackageName());
					//Log.i(TAG, "loadAndSaveInstalledApps remove old:"+o.getPackageName());
				}
			}
		}
		
		/*return installedApps;*/
	}

	/**
	 * 获取本地已经安装的应用
	 * @return
	 */
	public List<InstalledAppInfo> loadInstalledList(boolean getmd5) {
		List<InstalledAppInfo> installedApps = AppUtil.loadAppInfoList(
				context.getPackageManager(), true,getmd5);
		for (InstalledAppInfo installedAppInfo : installedApps) {
			String trimedName = StringUtil.trim(installedAppInfo.getName());
			installedAppInfo.setName(trimedName);
			installedAppInfo.setPinyinName(PinyinUtil.getPinyin(trimedName));
		}
		Collections.sort(installedApps);
		return installedApps;
	}
	
	private boolean find(List<InstalledAppInfo> newList,InstalledAppInfo target){
		if(target == null){
			return false ;
		}
		for (InstalledAppInfo n : newList) {
			if(target.getPackageName().equals(n.getPackageName())){
				return true ;
			}
		}
		return false ;
	}
	
	/**
	 * 获取本地已经安装的游戏
	 * @return
	 */
	public List<InstalledAppInfo> getInstalledGames() {
		AppCache cache = AppCache.getInstance();
		List<InstalledAppInfo> ret = null ;
		PackageManager pm = context.getPackageManager();
		List<InstalledAppInfo> retTemp = new ArrayList<InstalledAppInfo>();
		if(cache.isInitialize()){
			ret = cache.getInstalleds();
		}else {
			ret = getInstalledGamesFromDB();
		}
		if(ret != null){
			InstalledAppInfo t = null ;
			for (InstalledAppInfo installedAppInfo : ret) {
				if(context.getPackageName().equals(installedAppInfo.getPackageName())){
					t = installedAppInfo ;
					continue;
				}
				Drawable icon = AppUtil.loadApplicationIcon(context.getPackageManager(), installedAppInfo.getPackageName());
				installedAppInfo.setDrawable(icon);
				
				if(TextUtils.isEmpty(installedAppInfo.getPinyinName())){
					String trimedName = StringUtil.trim(installedAppInfo.getName());
					installedAppInfo.setPinyinName(PinyinUtil.getPinyin(trimedName));
				}
			}
			
			for(InstalledAppInfo iai : ret){
				try{
						pm.getPackageInfo(iai.getPackageName(), 0);
						retTemp.add(iai);
				}catch(Exception e){
				}
			}
			
			if(t != null){
				retTemp.remove(t);
			}
		}
		
		return retTemp ;
	}
	
	protected List<InstalledAppInfo> getInstalledGamesFromDB() {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		//
		List<InstalledAppInfo> installedApps = appDbHandler
				.getAllInstalledGames();
		return installedApps ;
	}
	/**
	 * 获取本地已经安装的游戏
	 * @param packageName
	 * @return
	 */
	public InstalledAppInfo getInstalledGame(String packageName) {
		AppDao appDbHandler = DbManager.getAppDbHandler();
		//
		return appDbHandler
				.getInstalledGame(packageName);
	}
	
	
	/**
	 * 跳转到游戏详情。前只有在搜索、抢号、管理界面使用
	 * <BR/>
	 * TODO 放到这里不太合适，需要修改
	 */
	public void jumpToDetail(final Activity context,final String gameId,final String gameName,final String packageName,boolean download,String...extra){
		
		Intent intent = new Intent(context, GameDetailsActivity.class);
		boolean flag = false ;
		if(!TextUtils.isEmpty(gameId)){
			intent.putExtra("gameid", gameId);
			flag = true ;
		}
		if(!TextUtils.isEmpty(packageName)){
			intent.putExtra("pkgname", packageName);
			flag = true ;
		}
		intent.putExtra("auto_download", download);
		if(extra != null && extra.length >= 2){
			intent.putExtra("versioncode", extra[0]);
			intent.putExtra("versionname", extra[1]);
		}

        intent.putExtra(GameDetailConstants.KEY_GAME_NAME, gameName == null ? "":gameName);

		if(!flag){
			return ;
		}
		context.startActivity(intent);
		
		
	}
	
	/**
     * 跳转到游戏详情。前只有在金幣中心使用
     * <BR/>
     * TODO 放到这里不太合适，需要修改
     */
    public void jumpToDetail(final Activity context, final int tabid, final String gameId,final String packageName,boolean download,String...extra){
        
        Intent intent = new Intent(context, GameDetailsActivity.class);
        boolean flag = false ;
        if(!TextUtils.isEmpty(gameId)){
            intent.putExtra("gameid", gameId);
            flag = true ;
        }
        if(!TextUtils.isEmpty(packageName)){
            intent.putExtra("pkgname", packageName);
            flag = true ;
        }
        intent.putExtra("auto_download", download);
        if(extra != null && extra.length >= 2){
            intent.putExtra("versioncode", extra[0]);
            intent.putExtra("versionname", extra[1]);
        }
        if(!flag){
            return ;
        }
        
        intent.putExtra(GameDetailConstants.KEY_GAME_TAB_ID, tabid);
        
        context.startActivity(intent);
    }

	/**
	 * 保存更新的游戏列表
	 * @param list
	 */
	public void saveUpdatableList(List<UpdatableItem> list){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		appDbHandler.updateUpdatableList(list);
	}

	/**
	 * 保存下载完成的游戏
	 * @param gameId
	 * @param packageName
	 */
	public void saveMyDownloadedGame(String gameId,String packageName,Long downloadId){
		try {
			DownloadAppInfo downloadGame = getDownloadGame(/*packageName,*/downloadId,true);
			if(downloadGame == null){
				//不应该出现这种情况的,但是缓存被删除就有可能
				Log.e(TAG, "Error");
				//throw new RuntimeException("Error");
				return ;
			}
			AppDao appDbHandler = DbManager.getAppDbHandler();
			
			MyDownloadedGame myDownloadedGame = new MyDownloadedGame(gameId, downloadGame.getName(), downloadGame.getIconUrl(), packageName, 
					downloadGame.getExtra(), null,downloadGame.isNeedLogin());
			
			ArrayList<MyDownloadedGame> list = new ArrayList<MyDownloadedGame>(1);
			list.add(myDownloadedGame);
			appDbHandler.addMyDownloadedGames(list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 获取下载完成的列表
	 * @return
	 */
	public List<MyDownloadedGame> getMyDownloadedGames(){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		return appDbHandler.getAllMyDownloadedGames();
	}
	/**
	 * 获取可更新的(包括忽略的)游戏的数量
	 * @return
	 */
	public int getUpdatableCount(){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		return appDbHandler.getUpdatableCount();
	}
	@Deprecated
	public int getWhiteListCount(){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		return appDbHandler.getWhiteListCount();
	}
	@Deprecated
	public int getInstalledListCount(){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		return appDbHandler.getInstalledListCount();
	}
	
	
	
	
}
