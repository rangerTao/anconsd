package com.ranger.bmaterials.db;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MergeMode;
import com.ranger.bmaterials.mode.MyDownloadedGame;
import com.ranger.bmaterials.mode.MyInstalledAppInfo;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.mode.UpdatableItem;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

public class AppDaoImplProxy implements AppDao {
	private AppDaoImpl  appDaoImpl ;
	private Object mutex = new Object() ;
	
	public AppDaoImplProxy(Context context) {
		appDaoImpl = new AppDaoImpl(context);
	}
	
	@Override
	public int getWhiteListCount() {
		synchronized (mutex) {
			try {
				return appDaoImpl.getWhiteListCount() ;
			} catch (Exception e) {
				
			}finally{
				appDaoImpl.closeConnection();
			}
			return 0 ;
		}
	}

	@Override
	public int getUpdatableCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInstalledListCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateWhiteList(List<BaseAppInfo> apps) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addWhiteListApp(BaseAppInfo whiteApp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAllInstalledApps(List<InstalledAppInfo> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInstalledApp(InstalledAppInfo app) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInstalledApp(InstalledAppInfo app, boolean isGame) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMyInstalledApp(InstalledAppInfo app) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMyInstalledApp(MyInstalledAppInfo app) {
		// TODO Auto-generated method stub

	}

	@Override
	public InstalledAppInfo removeInstalledApp(String packageName) {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public List<InstalledAppInfo> getAllInstalledGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InstalledAppInfo> getAllInstalledApps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UpdatableAppInfo> getAllUpdatableGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUpdatableList(List<UpdatableItem> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DownloadAppInfo> getAllDownloadGames(boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long addDownloadGame(DownloadAppInfo app) {
		// TODO Auto-generated method stub
		return -1 ;

	}


	@Override
	public void addDownloadGames(DownloadAppInfo... app) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateIgnoreState(String packageName, boolean ignore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDownloadGames2(boolean delete, String... gameIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDownloadGames(boolean delete, long... downloadIds) {
		// TODO Auto-generated method stub

	}


	@Override
	public UpdatableAppInfo getUpdatableGame(String packageName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InstalledAppInfo getInstalledGame(String packageName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DownloadAppInfo getDownloadGameForId(String gameId,
			boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseAppInfo getWhiteApp(String packageName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMyDownloadedGames(List<MyDownloadedGame> game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMyDownloadedGame(String gameId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MyDownloadedGame> getAllMyDownloadedGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDownloadNotifyStatus(String downloadUrl, boolean flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getDownloadNotifyStatus(String downloadUrl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateInstalledGameIds(Map<String, String> ids) {
		// TODO Auto-generated method stub

	}




	@Override
	public void updateGameInstallStatus(String packageName, Long downloadId,
			InstallStatus status, int... errorReason) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public DownloadAppInfo getDownloadGame(String packageName, String version,
			String versionInt, boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDownloadGame(boolean delete, String packageName,
			String version, String versionInt) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<InstalledAppInfo> queryInstalledApps(List<String> packageNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDownloadGame(boolean delete, String packageName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDownloadGameRecord(String packageName, boolean showing) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDownloadGame(String downloadUrl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllDownloadGames(boolean delete) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int updateDownloadId(String downloadUrl, long downloadId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public DownloadAppInfo getDownloadGame(String downloadUrl, String gameId,
			boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDownloadGame(boolean delete, long downloadId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DownloadAppInfo getDownloadGame(Long downloadId,
			boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDeletedApp(String packageName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InstalledAppInfo getInstalledApp(String packageName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceAllInstalledApps(List<InstalledAppInfo> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeInstalledApps() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDownload(Long downloadId, String packageName,
			String newPackage, String verion, int versionCode, String sign,
			String fileMd5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int updateMergeFailedCount(MergeMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addMergeRecord(MergeMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MergeMode> queryMergeRecord() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeMergeRecord(MergeMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeMergeRecord(String gameId, String downloadUrl,
			long downloadId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MergeMode queryMergeRecord(String gameId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDownload(Long downloadId, String sign, String fileMd5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DownloadAppInfo getDownloadGame(String fileMd5,
			boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateDownloadGame(String oldUrl, String newUrl,
			boolean diffUpdate, long newSize) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateDownloadGame(long oldId, String newUrl,
			boolean diffUpdate, long newSize) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateMergeStatus(MergeMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeAllMergeRecord() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateApplicationMD5(List<InstalledAppInfo> apps) {
		// TODO Auto-generated method stub
		
	}

}
