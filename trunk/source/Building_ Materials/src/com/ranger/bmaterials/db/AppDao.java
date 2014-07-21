package com.ranger.bmaterials.db;

import java.util.List;
import java.util.Map;

import com.ranger.bmaterials.db.AppDaoImpl.MergeTable;
import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MergeMode;
import com.ranger.bmaterials.mode.MyDownloadedGame;
import com.ranger.bmaterials.mode.MyInstalledAppInfo;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.mode.UpdatableItem;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

public interface AppDao {
	
	//public boolean initWhiteList();
	public int getWhiteListCount();
	public int getUpdatableCount();
	public int getInstalledListCount();
	
	// //////////////////////////////////////////////////////////////
	/**
	 * 根据白名单确定是否为游戏
	 * @param apps
	 */
	public void updateWhiteList(List<BaseAppInfo> apps);
	/**
	 * 添加一个白名单游戏
	 * @param whiteApp
	 */
	public void addWhiteListApp(BaseAppInfo whiteApp);

	// //////////////////////////////////////////////////////////////
	
	
	// //////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	
	
	/**
	 * 保存所有安装记录
	 * @param list
	 */
	public void saveAllInstalledApps(List<InstalledAppInfo> list);
	
	/**
	 * 添加安装记录
	 * @param app
	 */
	public void addInstalledApp(InstalledAppInfo app);
	/**
	 * 添加安装记录
	 * @param app
	 * @param isGame 是否是游戏(如果不确定则调用addInstalledApp(InstalledAppInfo app))
	 */
	public void addInstalledApp(InstalledAppInfo app,boolean isGame);
	/**
	 * 添加安装记录（是游戏并且从duoku安装）
	 * @param app
	 */
	public void addMyInstalledApp(InstalledAppInfo app);
	/**
	 * 添加安装记录（是游戏并且从duoku安装）
	 * @param app
	 */
	public void addMyInstalledApp(MyInstalledAppInfo app);

	/**
	 * @param app
	 */
	//public void updateInstalledApp(InstalledAppInfo app); 

	/**
	 * 删除安装记录
	 * @param packageName
	 */
	InstalledAppInfo  removeInstalledApp(String packageName);
	/**
	 * 获取安装游戏
	 * @return
	 */
	public List<InstalledAppInfo> getAllInstalledGames();
	/**
	 * 获取安装的app列表
	 * @return
	 */
	public List<InstalledAppInfo> getAllInstalledApps();
	// //////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////
	/**
	 * 添加更新记录
	 * @return
	 */
	public List<UpdatableAppInfo> getAllUpdatableGames();
	// //////////////////////////////////////////////////////////////
	/**
	 * 刷新可更新列表
	 * @param list
	 */
	void updateUpdatableList(List<UpdatableItem> list);
	/**
	 * 获取下载记录
	 * @return
	 */
	List<DownloadAppInfo> getAllDownloadGames(boolean includeDeleted);
	/**
	 * 添加下载记录
	 * @param app
	 */
	long addDownloadGame(DownloadAppInfo app);
	
	/**
	 * 更新应用列表的MD5信息
	 * @param apps
	 */
	void updateApplicationMD5(List<InstalledAppInfo> apps);
	
	/**
	 * 更新游戏的安装状态
	 * @param packageName
	 * @param status
	 * @param errorReason
	 */
	void updateGameInstallStatus(String packageName,Long downloadId,InstallStatus status,int...errorReason);
	/**
	 *  添加下载记录
	 * @param app
	 */
	void addDownloadGames(DownloadAppInfo... app);
	//void removeDownloadApp(String packageName,String sign);
	/**
	 * 更改忽略状态
	 * @param packageName 被更改的app包，null表示所有
	 * @param ignore
	 */
	void updateIgnoreState(String packageName, boolean ignore);
	/**
	 * 删除下载记录
	 * @param gameId
	 */
	void removeDownloadGames2(boolean delete,String... gameIds);
	/**
	 *  删除下载记录
	 * @param downloadId
	 */
	void removeDownloadGames(boolean delete,long... downloadIds);
//	/**
//	 *  删除下载记录
//	 * @param delete
//	 * @param packageName
//	 */
//	void removeDownloadGames(boolean delete,String... packageName);
	/**
	 * 查询可更新的游戏
	 * @param packageName
	 * @return
	 */
	UpdatableAppInfo getUpdatableGame(String packageName);
	/**
	 * 查询已安装的游戏（不一定是从duoku下载安装）
	 * @param packageName
	 * @return
	 */
	InstalledAppInfo getInstalledGame(String packageName);
	/**
	 * 查询下载或者已经下载但是没有安装的游戏
	 * @param packageName
	 * @return
	 *//*
	DownloadAppInfo getDownloadGame(String packageName,boolean includeDeleted);*/
	/**
	 * 过去下载的游戏
	 * @param gameId
	 * @param includeDeleted
	 * @return
	 */
	DownloadAppInfo getDownloadGameForId(String gameId,boolean includeDeleted);
	/**
	 * 查询白名单游戏的详情
	 * @param packageName
	 * @return
	 */
	BaseAppInfo getWhiteApp(String packageName);
	/**
	 * 保存当前设备下载过的游戏
	 * @param game
	 */
	void addMyDownloadedGames(List<MyDownloadedGame> game);
	
	/**
	 * 删除下载过的游戏
	 * @param gameId
	 */
	void removeMyDownloadedGame(String gameId);
	/**
	 * 获取当前设备下载过的游戏
	 * @return
	 */
	List<MyDownloadedGame> getAllMyDownloadedGames();
	/**
	 * 更新下载游戏的通知状态
	 * @param downloadUrl
	 * @param flag
	 */
	void updateDownloadNotifyStatus(String downloadUrl, boolean flag);
	/**
	 * 获取下载游戏是否通知过（下载完成通知安装）
	 * @param downloadUrl
	 * @return
	 */
	boolean getDownloadNotifyStatus(String downloadUrl);
	DownloadAppInfo getDownloadGame(String fileMd5, boolean includeDeleted);
	/**
	 * 更新game的gameid
	 * @param ids
	 */
	void updateInstalledGameIds(Map<String, String> ids);
	/**
	 * 查询安装的apps
	 * @param packageNames
	 * @return
	 */
	List<InstalledAppInfo> queryInstalledApps(List<String> packageNames);
	void removeDownloadGame(boolean delete, String packageName, String version,String versionInt);
	void removeDownloadGame(boolean delete, /*String packageName,*/ long downloadId);
	DownloadAppInfo getDownloadGame(Long downloadId,
			boolean includeDeleted);
	DownloadAppInfo getDownloadGame(String packageName, String version,String versionInt,
			boolean includeDeleted);
	void removeDownloadGame(boolean delete, String packageName);
	void updateDownloadGameRecord(String packageName, boolean showing);
	void removeDownloadGame(String downloadUrl);
	void removeAllDownloadGames(boolean delete);
	int updateDownloadId(String downloadUrl, long downloadId);
	DownloadAppInfo getDownloadGame(String downloadUrl, String gameId,
			boolean includeDeleted);
	void removeDeletedApp(String packageName);
	InstalledAppInfo getInstalledApp(String packageName);
	void replaceAllInstalledApps(List<InstalledAppInfo> list);
	void removeInstalledApps();
	void updateDownload(Long downloadId, String packageName, String newPackage,
			String verion, int versionCode, String sign, String fileMd5);
	void updateDownload(Long downloadId, String sign, String fileMd5);
	
	
	//////////////////////////////////////////////////////////
	public int updateMergeFailedCount(MergeMode mode);
	public long addMergeRecord(MergeMode mode);
	public List<MergeMode> queryMergeRecord();
	public int removeMergeRecord(MergeMode mode);
	int removeMergeRecord(String gameId, String downloadUrl, long downloadId);
	MergeMode queryMergeRecord(String gameId);
	//////////////////////////////////////////////////////////
	int updateDownloadGame(String oldUrl, String newUrl, boolean diffUpdate,
			long newSize);
	int updateDownloadGame(long oldId, String newUrl, boolean diffUpdate,
			long newSize);
	int updateMergeStatus(MergeMode mode);
	int removeAllMergeRecord();
	
	
	
	
}
