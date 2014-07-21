package com.ranger.bmaterials.mode;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class InstalledAppInfo extends BaseAppInfo {
	
	private int versionInt; // 3
	private String version;// 4
	private long size;// 5
	private String sizeString;
	private long date;// 6

	private String extra;// 8

	private String pinyinName;// 9
	private String sign;// 7
	
	private Drawable drawable ;
	
	private String gameId; 	// 14
	private boolean isGame ;

	private String fileMd5 ;
	private int uid ;
	// private int newVersionInt ;
	// private String newVersion ;
	
	public Drawable getDrawable() {
		return drawable;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public InstalledAppInfo() {
		super();
	}

	private boolean needLogin;

	public InstalledAppInfo(String packageName, String name, String version,
			int versionInt, long date, String extra, boolean needLogin,
			String pinyinName, String sign, long size,/*
													 * ,String newVersion,int
													 * newVersionInt
													 */
			
			String gameId,boolean isGame,String fileMd5,int uid) {
		super(packageName, name/* sign, version, versionInt, date, extra */);
		this.version = version;
		this.versionInt = versionInt;
		this.date = date;
		this.extra = extra;
		this.pinyinName = pinyinName;
		this.size = size;
		this.sign = sign;
		this.needLogin = needLogin;
		this.gameId = gameId ;
		this.isGame = isGame ;
		this.fileMd5 = fileMd5 ;
		this.uid = uid ;
		// this.newVersion = newVersion ;
		// this.newVersionInt = newVersionInt ;
	}

	public boolean isNeedLogin() {
		return needLogin;
	}

	/**
	 * 联运游戏是否需要登陆
	 * 
	 * @param needLogin
	 */
	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSizeString() {
		return sizeString;
	}

	public void setSizeString(String sizeString) {
		this.sizeString = sizeString;
	}

	public String getPinyinName() {
		return pinyinName;
	}

	public void setPinyinName(String pinyinName) {
		this.pinyinName = pinyinName;
	}

	public int getVersionInt() {
		return versionInt;
	}

	public void setVersionInt(int versionInt) {
		this.versionInt = versionInt;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 安装或者更新安装日期
	 * 
	 * @return
	 */
	public long getDate() {
		return date;
	}

	/**
	 * 安装或者更新安装日期
	 */
	public void setDate(long date) {
		this.date = date;
	}

	/**
	 * 联运游戏的action
	 */
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	/*
	 * public int getNewVersionInt() { return newVersionInt; }
	 * 
	 * 
	 * public void setNewVersionInt(int newVersionInt) { this.newVersionInt =
	 * newVersionInt; }
	 * 
	 * 
	 * public void setNewVersion(String newVersion) { this.newVersion =
	 * newVersion; }
	 * 
	 * 
	 * 
	 * 
	 * public String getNewVersion() { return newVersion; }
	 */

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	/*
	 * public String getSign() { return sign; }
	 * 
	 * 
	 * public void setSign(String sign) { this.sign = sign; }
	 */

	@Override
	public int compareTo(BaseAppInfo another) {
		// sCollator.compare(getName(), another.getName())
		if (another instanceof InstalledAppInfo) {
			String pinyinName2 = ((InstalledAppInfo) another).getPinyinName();
			if (!TextUtils.isEmpty(pinyinName)
					&& !TextUtils.isEmpty(pinyinName2)) {
				return pinyinName.compareTo(pinyinName2);
			}
		}
		return super.compareTo(another);

	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public boolean isGame() {
		return isGame;
	}

	public void setGame(boolean isGame) {
		this.isGame = isGame;
	}

	@Override
	public String toString() {
		return "InstalledAppInfo [versionInt=" + versionInt + ", version="
				+ version + ", size=" + size + ", sizeString=" + sizeString
				+ ", date=" + date + ", extra=" + extra + ", pinyinName="
				+ pinyinName + ", sign=" + sign + "]";
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public void updateMD5(){
	}

}
