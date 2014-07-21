package com.ranger.bmaterials.mode;

import java.io.Serializable;

import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadReason;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.tools.install.PackageUtils;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

/**
 * 
 * @author wangliang
 * 
 */
public class DownloadAppInfo extends InstalledAppInfo implements Serializable{
	
	private static final long serialVersionUID = 410021581464050503L;
	private long downloadDate;// 10
	private String downloadUrl;// 11
	private long publishDate;// 12
	private String iconUrl;// 13
	private long downloadId; // 15
	
	DownloadStatus status ;
	DownloadReason reason ;
	
	InstallStatus installeStatus ;
	private int installErrorReason = PackageUtils.INSTALL_SUCCEEDED ;
	
	
	private boolean markDeleted ;
	
	/**后加上的*/
	private long currtentSize ;
	/**后加上的*/
	private long totalSize ;
	
	/**后加上的*/
	private String saveDest ;
	
	/**是否是增量更新*/
	private boolean diffUpdate ;
	
	/**统一状态*/
	private int apkStatus ; 
	/**统一原因*/
	private Integer apkReason ;
	
	private int mergeFailedCount ;
	
	public DownloadAppInfo() {
		super();
	}
	/**
	 * 
	 * @param packageName
	 * @param name
	 * @param version
	 * @param versionInt
	 * @param publishDate
	 * @param extra
	 * @param pinyinName
	 * @param sign
	 * @param size
	 * @param iconUrl
	 * <br/>
	 * 
	 * @param downloadId
	 * @param gameId
	 * @param downloadUrl
	 * @param downloadDate
	 */
	public DownloadAppInfo(String packageName, String name, String version,
			int versionInt, long publishDate, String extra, boolean needLogin,String pinyinName,
			String sign, long size,long downloadId,String downloadUrl,String iconUrl,long downloadDate,String gameId,boolean diffUpdate
			,String fileMd5) {
		
		super(packageName, name, version, versionInt, -1, extra, needLogin,pinyinName, sign,
				size,gameId,true,fileMd5,-1);
		this.downloadId = downloadId ;
		this.downloadDate = downloadDate ;
//		this.gameId = gameId ;
		
		this.publishDate = publishDate ;
		this.downloadUrl = downloadUrl;
		this.iconUrl = iconUrl ;
		this.diffUpdate = diffUpdate ;
		
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
	/**后加上的*/
	public String getSaveDest() {
		return saveDest;
	}
	/**后加上的*/
	public void setSaveDest(String saveDest) {
		if(saveDest != null){
			try {
				this.saveDest = Uri.parse(saveDest).getPath();
			} catch (Exception e) {
				e.printStackTrace();
				this.saveDest = saveDest;
			}
		}
		
	}

	public DownloadStatus getStatus() {
		return status;
	}


	public void setStatus(DownloadStatus status) {
		this.status = status;
	}


	public DownloadReason getReason() {
		return reason;
	}


	public void setReason(DownloadReason reason) {
		this.reason = reason;
	}


	
	
	/**后加上的*/
	public long getCurrtentSize() {
		return currtentSize;
	}

	/**后加上的*/
	public void setCurrtentSize(long currtentSize) {
		this.currtentSize = currtentSize;
	}

	/**后加上的*/
	public long getTotalSize() {
		return totalSize;
	}

	/**后加上的*/
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}


	public boolean isMarkDeleted() {
		return markDeleted;
	}


	public void setMarkDeleted(boolean markDeleted) {
		this.markDeleted = markDeleted;
	}

	
	
	
	
	public boolean isDiffUpdate() {
		return diffUpdate;
	}

	public void setDiffUpdate(boolean diffUpdate) {
		this.diffUpdate = diffUpdate;
	}

	public long getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}
	public long getDownloadDate() {
		return downloadDate;
	}
	public void setDownloadDate(long downloadDate) {
		this.downloadDate = downloadDate;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public long getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(long publishDate) {
		this.publishDate = publishDate;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getGameId() {
		return super.getGameId();
	}
	
	
	public void setGameId(String gameId) {
		super.setGameId(gameId);
	}

	/**
	 * 从server获取的数据，不一定正确
	 */
	public long getSize() {
		return super.getSize();
	}

	/**
	 * 从server获取的数据，不一定正确
	 */
	public void setSize(long size) {
		super.setSize(size);
	}


	public InstallStatus getInstalleStatus() {
		return installeStatus;
	}


	public void setInstalleStatus(InstallStatus installeStatus) {
		this.installeStatus = installeStatus;
	}


	public int getErrorReason() {
		return installErrorReason;
	}


	public void setErrorReason(int errorReason) {
		this.installErrorReason = errorReason;
	}


	public int getInstallErrorReason() {
		return installErrorReason;
	}


	public void setInstallErrorReason(int installErrorReason) {
		this.installErrorReason = installErrorReason;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMergeFailedCount() {
		return 0;
	}


	public void setMergeFailedCount(int mergeFailedCount) {
		this.mergeFailedCount = mergeFailedCount;
	}
	/**
	 * 联运游戏的action
	 */
	@Override
	public String getExtra() {
		return super.getExtra();
	}

	@Override
	public String toString() {
		return "DownloadAppInfo id:"+downloadId+" [currtentSize=" + currtentSize + ", totalSize="
				+ totalSize + ", status=" + status + ", reason=" + reason
				+ ", installeStatus=" + installeStatus
				+ ", installErrorReason=" + installErrorReason
				+ ", markDeleted=" + markDeleted + "]";
	}
	

	
	


}
