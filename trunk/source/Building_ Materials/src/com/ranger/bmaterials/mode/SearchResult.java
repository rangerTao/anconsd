package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.app.AppManager.GameStatus;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadReason;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.install.PackageUtils;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

public class SearchResult extends BaseResult {

	public static class SearchItem {
		private GameStatus status;
		private long currentBytes;
		private long totalBytes;
		private DownloadStatus downloadStatus;
		private DownloadReason downloadReason;

		private String gameId;
		private String gameName;
		private String gameNameDes;
		private float star;
		private int downloadTimes;
		private String packageName;
		private String iconUrl;
		private String downloadUrl;
		private long packageSize;
		private String version;
		private int versionInt;
		private long publishDate;

		private String action;

		private boolean needLogin;
		private boolean isPendingOnLine;
		private InstallStatus installeStatus;
		public String labelName;
		public String labelColor;

		// ////////////////////////////////////////////////////
		/** 是否是增量更新 */
		private boolean diffDownload;
		/** 统一状态 */
		private int apkStatus;
		/** 统一原因 */
		private Integer apkReason;
		/** 增量更新地址 */
		private String diffUrl;
		/** 下载文件保存位置 */
		private String saveDest;
		/** 下载文件downloadId */
		private long downloadId;
		/** 更新合并失败次数 */
		private long mergeFailedCount;

		private String localVersion;

		private int localVersionCode;

		private long patchSize;

		// ////////////////////////////////////////////////////

		public SearchItem() {
			super();
			this.gameId = "";
			this.gameName = "";
		}

		public SearchItem(String gameId, String gameName, float star,
				int downloadTimes, String packageName, String iconUrl,
				String downloadUrl, long packageSize, String version,
				int versionInt, long publishDate, String action,
				boolean needLogin, boolean isPendingOnLine) {
			super();
			this.gameId = gameId;
			this.gameName = gameName;
			this.star = star;
			this.downloadTimes = downloadTimes;
			this.packageName = packageName;
			this.iconUrl = iconUrl;
			this.downloadUrl = downloadUrl;
			this.packageSize = packageSize;
			this.version = version;
			this.versionInt = versionInt;
			this.publishDate = publishDate;
			this.action = action;
			this.needLogin = needLogin;
			this.isPendingOnLine = isPendingOnLine;
		}

		public long getCurrentBytes() {
			return currentBytes;
		}

		public void setCurrentBytes(long currentBytes) {
			this.currentBytes = currentBytes;
		}

		public long getTotalBytes() {
			return totalBytes;
		}

		public void setTotalBytes(long totalBytes) {
			this.totalBytes = totalBytes;
		}

		public boolean isNeedLogin() {
			return needLogin;
		}

		public void setNeedLogin(boolean needLogin) {
			this.needLogin = needLogin;
		}

		public long getPublishDate() {
			return publishDate;
		}

		public void setPublishDate(long publishDate) {
			this.publishDate = publishDate;
		}

		public String getGameId() {
			return gameId;
		}

		public void setGameId(String gameId) {
			this.gameId = gameId;
		}

		public String getGameName() {
			return gameName;
		}

		public void setGameName(String gameName) {
			this.gameName = gameName;
		}

		public String getGameNameDes() {
			return gameNameDes;
		}

		public void setGameNameDes(String gameNameDes) {
			this.gameNameDes = gameNameDes;
		}

		public float getStar() {
			return star;
		}

		public void setStar(float star) {
			this.star = star;
		}

		public int getDownloadTimes() {
			return downloadTimes;
		}

		public void setDownloadTimes(int downloadTimes) {
			this.downloadTimes = downloadTimes;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}

		public String getDownloadUrl() {
			return downloadUrl;
		}

		public void setDownloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
		}

		public long getPackageSize() {
			return packageSize;
		}

		public void setPackageSize(long packageSize) {
			this.packageSize = packageSize;
		}

		@Deprecated
		public GameStatus getStatus() {
			return status;
		}

		@Deprecated
		public void setStatus(GameStatus status) {
			this.status = status;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public int getVersionInt() {
			return versionInt;
		}

		public void setVersionInt(int versionInt) {
			this.versionInt = versionInt;
		}

		@Deprecated
		public DownloadStatus getDownloadStatus() {
			return downloadStatus;
		}

		@Deprecated
		public void setDownloadStatus(DownloadStatus downloadStatus) {
			this.downloadStatus = downloadStatus;
		}

		@Deprecated
		public DownloadReason getDownloadReason() {
			return downloadReason;
		}

		@Deprecated
		public void setDownloadReason(DownloadReason downloadReason) {
			this.downloadReason = downloadReason;
		}

		private int installErrorReason = PackageUtils.INSTALL_SUCCEEDED;

		@Deprecated
		public InstallStatus getInstalleStatus() {
			return installeStatus;
		}

		@Deprecated
		public void setInstalleStatus(InstallStatus installeStatus) {
			this.installeStatus = installeStatus;
		}

		public int getInstallErrorReason() {
			return installErrorReason;
		}

		public void setInstallErrorReason(int installErrorReason) {
			this.installErrorReason = installErrorReason;
		}

		public boolean isPendingOnLine() {
			return isPendingOnLine;
		}

		public void setPendingOnLine(boolean isPendingOnLine) {
			this.isPendingOnLine = isPendingOnLine;
		}

		/**
		 * 联运游戏的action
		 * 
		 * @return
		 */
		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		/**
		 * 后加上的
		 * 
		 * @return
		 */
		public boolean isDiffDownload() {
			return diffDownload;
		}

		/**
		 * 后加上的
		 * 
		 * @param diffUpdate
		 */
		public void setDiffDownload(boolean diffUpdate) {
			this.diffDownload = diffUpdate;
		}

		/**
		 * 后加上的
		 * 
		 * @return
		 */
		public int getApkStatus() {
			return apkStatus;
		}

		/**
		 * 后加上的
		 * 
		 * @param apkStatus
		 */
		public void setApkStatus(int apkStatus) {
			this.apkStatus = apkStatus;
		}

		/**
		 * 后加上的
		 * 
		 * @return
		 */
		public Integer getApkReason() {
			return apkReason;
		}

		/**
		 * 后加上的
		 * 
		 * @param apkReason
		 */
		public void setApkReason(Integer apkReason) {
			this.apkReason = apkReason;
		}

		/**
		 * 后加上的
		 * 
		 * @return
		 */
		public String getDiffUrl() {
			return diffUrl;
		}

		/**
		 * 后加上的
		 * 
		 * @param diffUrl
		 */
		public void setDiffUrl(String diffUrl) {
			this.diffUrl = diffUrl;
		}

		/**
		 * 后加上的
		 * 
		 * @return
		 */
		public String getSaveDest() {
			return saveDest;
		}

		/**
		 * 后加上的
		 * 
		 * @param saveDest
		 */
		public void setSaveDest(String saveDest) {
			this.saveDest = saveDest;
		}

		/**
		 * 后加上的
		 * 
		 * @return
		 */
		public long getDownloadId() {
			return downloadId;
		}

		/**
		 * 后加上的
		 * 
		 * @param downloadId
		 */
		public void setDownloadId(long downloadId) {
			this.downloadId = downloadId;
		}

		/**
		 * 后加上的
		 */
		public long getMergeFailedCount() {
			return mergeFailedCount;
		}

		/**
		 * 后加上的
		 */
		public void setMergeFailedCount(long mergeFailedCount) {
			this.mergeFailedCount = mergeFailedCount;
		}

		public String getLocalVersion() {
			return localVersion;
		}

		public void setLocalVersion(String localVersion) {
			this.localVersion = localVersion;
		}

		public int getLocalVersionCode() {
			return localVersionCode;
		}

		public void setLocalVersionCode(int localVersionCode) {
			this.localVersionCode = localVersionCode;
		}

		public long getPatchSize() {
			return patchSize;
		}

		public void setPatchSize(long patchSize) {
			this.patchSize = patchSize;
		}

		@Override
		public String toString() {
			return "SearchItem [status=" + status + ", currentBytes="
					+ currentBytes + ", totalBytes=" + totalBytes
					+ ", downloadStatus=" + downloadStatus
					+ ", downloadReason=" + downloadReason + ", gameId="
					+ gameId + ", gameName=" + gameName + ", star=" + star
					+ ", downloadTimes=" + downloadTimes + ", packageName="
					+ packageName + ", iconUrl=" + iconUrl + ", downloadUrl="
					+ downloadUrl + ", packageSize=" + packageSize
					+ ", version=" + version + ", versionInt=" + versionInt
					+ ", publishDate=" + publishDate + ", action=" + action
					+ ", needLogin=" + needLogin + ", isPendingOnLine="
					+ isPendingOnLine + ", installeStatus=" + installeStatus
					+ ", diffDownload=" + diffDownload + ", apkStatus="
					+ apkStatus + ", apkReason=" + apkReason + ", diffUrl="
					+ diffUrl + ", saveDest=" + saveDest + ", downloadId="
					+ downloadId + ", mergeFailedCount=" + mergeFailedCount
					+ ", installErrorReason=" + installErrorReason + "]";
		}

	}

	private boolean isSearch;

	private List<SearchItem> data;

	public List<SearchItem> getData() {
		return data;
	}

	public void setData(List<SearchItem> data) {
		this.data = data;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	private int totalCount;

	public final int getTotalCount() {
		return totalCount;
	}

	public final void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
