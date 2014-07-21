package com.ranger.bmaterials.mode;

import android.text.TextUtils;

/**
 * 
 * @author wangliang
 *
 */
public class DownloadItemInput {
	String iconUrl ; //1
	String gameId ;//2
	String packageName;//3
	String displayName;	//4
	String saveName;	//5
	int versionInt;	//5		
	String version;	//6
	String downloadUrl;//7
	String sign;	//8 	optional
	long size;		//9 	
	String sizeString ;//11	optional
	long publishDate ;//10	optional
	
	String action ;
	boolean needLogin ;
	
	boolean isDiffDownload ;
	
	public DownloadItemInput() {
		super();
	}
	
	/**
	 * 
	 * @param iconUrl
	 * @param gameId
	 * @param packageName
	 * @param displayName
	 * @param saveName
	 * @param versionInt
	 * @param version
	 * @param downloadUrl
	 * @param sign
	 * @param size
	 * @param sizeString
	 * @param publishDate
	 * @param action 联运游戏的action
	 * @param needLogin 联运游戏是否需要登陆
	 */
	public DownloadItemInput(String iconUrl, String gameId, String packageName,
			String displayName, String saveName, int versionInt,
			String version, String downloadUrl, String sign, long size,
			String sizeString, long publishDate,String action,boolean needLogin,boolean isDiffDownload) {
		super();
		this.iconUrl = iconUrl;
		this.gameId = gameId;
		this.packageName = packageName;
		this.displayName = displayName;
		this.saveName = saveName;
		this.versionInt = versionInt;
		this.version = version;
		this.downloadUrl = downloadUrl;
		this.sign = sign;
		this.size = size;
		this.sizeString = sizeString;
		this.publishDate = publishDate;
		this.action = action ;
		this.needLogin = needLogin ;
		this.isDiffDownload = isDiffDownload ;
	}

	public boolean isNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	/**http://stackoverflow.com/questions/893977/java-how-to-find-out-whether-a-file-name-is-valid
	 * private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
	 * @return
	 */
	public String getSaveName() {
		if(TextUtils.isEmpty(saveName)){
			//return getPackageName();
		}
		return saveName;
	}
	public void setSaveName(String saveName) {
		this.saveName = saveName;
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
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(long publishDate) {
		this.publishDate = publishDate;
	}

	public String getSizeString() {
		return sizeString;
	}

	public void setSizeString(String sizeString) {
		this.sizeString = sizeString;
	}
	
	/**
	 * 联运游戏的action
	 * @return
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * 联运游戏的action
	 * @param extra
	 */
	public void setAction(String extra) {
		this.action = extra;
	}

	public boolean isDiffDownload() {
		return isDiffDownload;
	}

	public void setDiffDownload(boolean isDiffUpdate) {
		this.isDiffDownload = isDiffUpdate;
	}
	

	

}
