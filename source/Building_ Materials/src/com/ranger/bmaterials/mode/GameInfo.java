package com.ranger.bmaterials.mode;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.ranger.bmaterials.tools.StringUtil;

public class GameInfo extends GameDownloadInfo implements Serializable {

	private static final long serialVersionUID = -142308406191398350L;
	private String gameId;
	private String gameName;
	private String iconUrl;
	private String iconUrlhome;
	private String iconPath;

    private long currentBytes;
    private long totalBytes;

	private String pkgname;
	private String downloadurl;
	private Bitmap icon;
	private String apkpath;
	private String size;
	private float star;
	private String downloadedtimes;
	private String updatetime;
	private String updatetimedate;
	private String startaction;
	private String gameversion;
	private int gameversioncode;
	private boolean iscollected;
	private String description;
	private boolean needlogin;
	private String displaydownloadtimes;
	private String comingsoon;
	private String gametypename;
	private String labelname;
	private String labelcolor;

	private boolean isShowTime;
	private int showTimeLocation;

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


    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public boolean isDiffDownload() {
        return diffDownload;
    }

    public void setDiffDownload(boolean diffDownload) {
        this.diffDownload = diffDownload;
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

    public String getDiffUrl() {
        return diffUrl;
    }

    public void setDiffUrl(String diffUrl) {
        this.diffUrl = diffUrl;
    }

    public String getSaveDest() {
        return saveDest;
    }

    public void setSaveDest(String saveDest) {
        this.saveDest = saveDest;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public long getMergeFailedCount() {
        return mergeFailedCount;
    }

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

	public String getLabelName() {
		return labelname;
	}

	public void setLabelName(String name) {
		labelname = name;
	}

	public String getLabelColor() {
		return labelcolor;
	}

	public void setLabelColor(String color) {
		labelcolor = color;
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

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public String getApkpath() {
		return apkpath;
	}

	public void setApkpath(String apkpath) {
		this.apkpath = apkpath;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public float getStar() {
		return star;
	}

	public void setStar(float star) {
		this.star = star;
	}

	public String getDownloadedtimes() {
		return downloadedtimes;
	}

	public void setDownloadedtimes(String downloadedtimes) {
		this.downloadedtimes = downloadedtimes;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
		try {
			if (updatetime != null && !"".equals(updatetime))
				this.updatetimedate = updatetime.split(" ")[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getStartaction() {
		return startaction;
	}

	public void setStartaction(String startaction) {
		this.startaction = startaction;
	}

	public String getGameversion() {
		return gameversion;
	}

	public void setGameversion(String gameversion) {
		this.gameversion = gameversion;
	}

	public boolean isIscollected() {
		return iscollected;
	}

	public void setIscollected(boolean iscollected) {
		this.iscollected = iscollected;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isNeedlogin() {
		return needlogin;
	}

	public void setNeedlogin(boolean needlogin) {
		this.needlogin = needlogin;
	}

	public String getUpdatetimedate() {
		return updatetimedate;
	}

	public void setUpdatetimedate(String updatetimedate) {
		this.updatetimedate = updatetimedate;
	}

	public String getDisplaydownloadtimes() {
		if (displaydownloadtimes == null)
			displaydownloadtimes = StringUtil.getDisplayDownloadtimes(downloadedtimes);
		return displaydownloadtimes;
	}

	public void setDisplaydownloadtimes(String displaydownloadtimes) {
		this.displaydownloadtimes = displaydownloadtimes;
	}

	public String getIconUrlhome() {
		return iconUrlhome;
	}

	public void setIconUrlhome(String iconUrlhome) {
		this.iconUrlhome = iconUrlhome;
	}

	public int getGameversioncode() {
		return gameversioncode;
	}

	public void setGameversioncode(int gameversioncode) {
		this.gameversioncode = gameversioncode;
	}

	public String getComingsoon() {
		return comingsoon;
	}

	public void setComingsoon(String comingsoon) {
		this.comingsoon = comingsoon;
	}

	public String getGametypename() {
		return gametypename;
	}

	public void setGametypename(String gametypename) {
		this.gametypename = gametypename;
	}

	public boolean isShowTime() {
		return isShowTime;
	}

	public void setShowTime(boolean isShowTime) {
		this.isShowTime = isShowTime;
	}

	public int getShowTimeLocation() {
		return showTimeLocation;
	}

	public void setShowTimeLocation(int showTimeLocation) {
		this.showTimeLocation = showTimeLocation;
	}

}
