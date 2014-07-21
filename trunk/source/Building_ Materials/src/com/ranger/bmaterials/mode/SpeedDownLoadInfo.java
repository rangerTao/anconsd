package com.ranger.bmaterials.mode;

public class SpeedDownLoadInfo {
	private String url;
	private String packagename;
	private String versionname;
	private int versioncode;
	private String iconurl;
	private String appname;
	private String gameid;
	private boolean needLogin;
	private String startaction;
	private String filePath;
	private String apkSize;

	public final String getApkSize() {
		return apkSize;
	}

	public final void setApkSize(String apkSize) {
		this.apkSize = apkSize;
	}

	public final String getFilePath() {
		return filePath;
	}

	public final void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public final String getGameid() {
		return gameid;
	}

	public final void setGameid(String gameid) {
		this.gameid = gameid;
	}


	public final String getStartaction() {
		return startaction;
	}

	public final void setStartaction(String startaction) {
		this.startaction = startaction;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getVersionname() {
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}

	public final int getVersioncode() {
		return versioncode;
	}

	public final void setVersioncode(int versioncode) {
		this.versioncode = versioncode;
	}

	public final boolean isNeedLogin() {
		return needLogin;
	}

	public final void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}
}
