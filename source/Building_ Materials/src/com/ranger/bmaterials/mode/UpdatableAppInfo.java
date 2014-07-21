package com.ranger.bmaterials.mode;

import com.ranger.bmaterials.app.Constants;

import android.util.Log;


public class UpdatableAppInfo extends InstalledAppInfo {
	private UpdatableItem updatableItem ;
	private boolean ignoreUpdate ;
	
	public UpdatableAppInfo() {
		super();
	}
	/**
	 * 
	 * @param packageName
	 * @param name
	 * @param version
	 * @param versionInt
	 * @param date
	 * @param extra
	 * @param pinyinName
	 * @param sign
	 * @param size
	 * @param newVersion
	 * @param newVersionInt
	 * @param downloadUrl
	 * @param publishDate game的发布日期
	 * @param newSize
	 * @param ignored
	 * @param serverSign
	 */
	public UpdatableAppInfo(String packageName, String name, String version,
			int versionInt, long date, String extra, boolean needLogin,String pinyinName,
			String sign,long size,String newVersion,int newVersionInt,String downloadUrl,long publishDate,
			long newSize,boolean ignored,String serverSign,String gameId,String iconUrl
			,boolean isDiffUpdate,String patchUrl,long pacthSize) {
		
		super(packageName, name, version, versionInt, date, extra, needLogin,pinyinName, sign,size,gameId,true,null,-1);
		this.updatableItem = new UpdatableItem();
		this.ignoreUpdate = ignored ;
		//updatableItem.packageName = packageName ;
		//updatableItem.name = name ;
		updatableItem.newVersion = newVersion ;
		updatableItem.newVersionInt = newVersionInt;
		updatableItem.downloadUrl = downloadUrl ;
		updatableItem.publishDate = publishDate ;
		updatableItem.serverSign = serverSign ;
		updatableItem.newSize = newSize ;
		updatableItem.gameId = gameId ;
		updatableItem.iconUrl = iconUrl ;
		updatableItem.extra = extra ;
		updatableItem.needLogin = needLogin;
		updatableItem.isDiffUpdate = isDiffUpdate ;
		updatableItem.patchUrl = patchUrl ;
		updatableItem.pacthSize = pacthSize ;
		
		
	}

	public String getDownloadUrl() {
		return updatableItem.downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.updatableItem.downloadUrl = downloadUrl;
	}
	/**
	 * game的new version的发布日期
	 * @return
	 */
	public long getPublishDate() {
		return updatableItem.publishDate;
	}
	/**
	 * game的new version的发布日期
	 */
	public void setPublishDate(long publishDate) {
		this.updatableItem.publishDate = publishDate;
	}

	public String getServerSign() {
		return updatableItem.serverSign;
	}

	public void setServerSign(String serverSign) {
		this.updatableItem.serverSign = serverSign;
	}

	public int getNewVersionInt() {
		return updatableItem.newVersionInt;
	}


	public void setNewVersionInt(int newVersionInt) {
		this.updatableItem.newVersionInt = newVersionInt;
	}


	public void setNewVersion(String newVersion) {
		this.updatableItem.newVersion = newVersion;
	}


	public String getNewVersion() {
		return updatableItem.newVersion;
	}

	public long getNewSize() {
		return updatableItem.newSize;
	}

	public void setNewSize(long newSize) {
		this.updatableItem.newSize = newSize;
	}

	public boolean isIgnoreUpdate() {
		return ignoreUpdate;
	}

	public void setIgnoreUpdate(boolean ignoreUpdate) {
		this.ignoreUpdate = ignoreUpdate;
	}
	public String getGameId() {
		return updatableItem.gameId;
	}

	public void setGameId(String gameId) {
		this.updatableItem.gameId = gameId;
	}
	public String getIconUrl() {
		return updatableItem.iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.updatableItem.iconUrl = iconUrl;
	}
	/**
	 * 联运游戏的action
	 */
	@Override
	public String getExtra() {
		return super.getExtra();
	}

	public boolean isDiffUpdate() {
		return updatableItem.isDiffUpdate && getMergeFaildCount(updatableItem.getGameId());
	}
	
	private boolean getMergeFaildCount(String gameid){
		if(null != Constants.mergeFailedCountMap && Constants.mergeFailedCountMap.containsKey(gameid)){
			int count = Constants.mergeFailedCountMap.get(gameid);
			if(count >= 2){
				return false;
			}
		}
		return true;
	}

	public void setDiffUpdate(boolean isDiffUpdate) {
		this.updatableItem.setDiffUpdate(isDiffUpdate);
	}
	
	public long getPatchSize(){
		return this.updatableItem.pacthSize;
	}
	
	public String getPatchUrl(){
		return this.updatableItem.patchUrl;
	}

	public void setPatchUrl(String patchUrl) {
		this.updatableItem.setPatchUrl(patchUrl);
	}


	public void setPacthSize(long pacthSize) {
		this.updatableItem.setPacthSize(pacthSize);
	}
	
	
	/**后加上的*/
	private long currtentSize ;
	/**后加上的*/
	private long totalSize ;
	
	/**后加上的*/
	private String saveDest ;
	
	/**统一状态*/
	private int apkStatus ; 
	/**统一原因*/
	private Integer apkReason ;
	
	private long downloadId ;
	
	private int mergeFailedCount ;

	public long getCurrtentSize() {
		return currtentSize;
	}
	public void setCurrtentSize(long currtentSize) {
		this.currtentSize = currtentSize;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public String getSaveDest() {
		return saveDest;
	}
	public void setSaveDest(String saveDest) {
		this.saveDest = saveDest;
	}
	public int getApkStatus() {
		return apkStatus;
	}
	public void setApkStatus(int apkStatus) {
		this.apkStatus = apkStatus;
	}
	public Integer getApkReason() {
		return apkReason;
	}
	public void setApkReason(Integer apkReason) {
		this.apkReason = apkReason;
	}
	public int getMergeFailedCount() {
		return mergeFailedCount;
	}
	public void setMergeFailedCount(int mergeFailedCount) {
		this.mergeFailedCount = mergeFailedCount;
	}
	public long getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}
	@Override
	public String toString() {
		return "UpdatableAppInfo [updatableItem=" + updatableItem
				+ ", ignoreUpdate=" + ignoreUpdate + ", currtentSize="
				+ currtentSize + ", totalSize=" + totalSize + ", saveDest="
				+ saveDest + ", apkStatus=" + apkStatus + ", apkReason="
				+ apkReason + ", downloadId=" + downloadId
				+ ", mergeFailedCount=" + mergeFailedCount + "]";
	}
	
	
}
