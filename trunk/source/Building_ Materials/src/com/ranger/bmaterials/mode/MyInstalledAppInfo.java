package com.ranger.bmaterials.mode;

/**
 * 从duoku安装的游戏
 * @author wangliang
 *
 */
public class MyInstalledAppInfo extends InstalledAppInfo {
	private long installDate;
	private long latestOpenTime;
	private int openTimes ;
	public MyInstalledAppInfo() {
		super();
	}
	public MyInstalledAppInfo(String packageName, String name, String version,
			int versionInt, long date, String extra, boolean needLogin,String pinyinName,
			String sign, long size,String fileMd5 ) {
		
		super(packageName, name, version, versionInt, date, extra, needLogin,pinyinName, sign,
				size,null,true,fileMd5,-1);
		/**
		 * 注意！！！！
		 */
	}
	public long getInstallDate() {
		return installDate;
	}
	public void setInstallDate(long installDate) {
		this.installDate = installDate;
	}
	public int getOpenTimes() {
		return openTimes;
	}
	public void setOpenTimes(int openTimes) {
		this.openTimes = openTimes;
	}
	public long getLatestOpenTime() {
		return latestOpenTime;
	}
	public void setLatestOpenTime(long latestOpenTime) {
		this.latestOpenTime = latestOpenTime;
	}
	
	

}
