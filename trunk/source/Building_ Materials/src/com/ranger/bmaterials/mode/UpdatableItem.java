package com.ranger.bmaterials.mode;

import com.ranger.bmaterials.netresponse.BaseResult;

/**
 * 需要更新的game的更新信息，因为是在已安装的基础上更新，所以只包含了更新部分的信息
 * @author wangliang
 *
 */
public class UpdatableItem extends BaseResult{
	
	String gameId ;
	String packageName;
	String name ;
	int newVersionInt;
	String newVersion;
	String downloadUrl;
	long publishDate;
	String serverSign;
	long newSize;
	String iconUrl ;
	String extra ;
	boolean needLogin;
	boolean updatable ;
	
	boolean isDiffUpdate ;
	String patchUrl;
	long pacthSize;
	
	
	
	public UpdatableItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UpdatableItem(String gameId, String packageName, String name,
			int newVersionInt, String newVersion, String downloadUrl,
			long publishDate, String serverSign, long newSize, 
			String iconUrl,String extra,boolean needLogin,boolean updatable
			,boolean isDiffUpdate,String patchUrl,long pacthSize) {
		super();
		this.gameId = gameId;
		this.packageName = packageName;
		this.name = name;
		this.newVersionInt = newVersionInt;
		this.newVersion = newVersion;
		this.downloadUrl = downloadUrl;
		this.publishDate = publishDate;
		this.serverSign = serverSign;
		this.newSize = newSize;
		this.iconUrl = iconUrl;
		this.extra = extra ;
		this.needLogin = needLogin ;
		this.updatable = updatable ;
		this.isDiffUpdate = isDiffUpdate ;
		this.patchUrl = patchUrl ;
		this.pacthSize = pacthSize ;
	}

	
	
	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getNewVersionInt() {
		return newVersionInt;
	}

	public void setNewVersionInt(int newVersionInt) {
		this.newVersionInt = newVersionInt;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
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

	public long getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(long publishDate) {
		this.publishDate = publishDate;
	}

	public String getServerSign() {
		return serverSign;
	}

	public void setServerSign(String serverSign) {
		this.serverSign = serverSign;
	}

	public long getNewSize() {
		return newSize;
	}

	public void setNewSize(long newSize) {
		this.newSize = newSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 联运的action
	 * @return
	 */
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	/**
	 * 联运游戏是否需要登陆
	 * @return
	 */
	public boolean isNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public boolean isDiffUpdate() {
		return isDiffUpdate;
	}

	public void setDiffUpdate(boolean isDiffUpdate) {
		this.isDiffUpdate = isDiffUpdate;
	}

	public String getPatchUrl() {
		return patchUrl;
	}

	public void setPatchUrl(String patchUrl) {
		this.patchUrl = patchUrl;
	}

	public long getPacthSize() {
		return pacthSize;
	}

	public void setPacthSize(long pacthSize) {
		this.pacthSize = pacthSize;
	}

	@Override
	public String toString() {
		return "UpdatableItem [gameId=" + gameId + ", packageName="
				+ packageName + ", name=" + name + ", newVersionInt="
				+ newVersionInt + ", newVersion=" + newVersion
				+ ", downloadUrl=" + downloadUrl + ", publishDate="
				+ publishDate + ", serverSign=" + serverSign + ", newSize="
				+ newSize + ", iconUrl=" + iconUrl + "]";
	}

}
