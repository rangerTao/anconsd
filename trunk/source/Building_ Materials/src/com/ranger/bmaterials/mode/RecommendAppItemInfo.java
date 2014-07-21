package com.ranger.bmaterials.mode;

public class RecommendAppItemInfo {

	private String appName;
	private String appicon_Url;
	private String recommendUrl;
	private int iconID; //当不需要web的Icon时可以调用本地的icon来显示
	
	/**
	 * 使用本地的icon时候调用此构造方法
	 * @param appName
	 * @param iconId
	 * @param appUrl
	 */
	public RecommendAppItemInfo(String appName,int iconId, String appUrl) {
		setAppName(appName);
		setIconID(iconId);
		setRecommendUrl(appUrl);
	}
	
	/**
	 * 使用web的icon时候调用此构造方法
	 * @param appName
	 * @param iconId
	 * @param appUrl
	 */
	public RecommendAppItemInfo(String appName,String iconURL, String appUrl) {
		setAppName(appName);
		setAppicon_Url(iconURL);
		setRecommendUrl(appUrl);
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getAppicon_Url() {
		return appicon_Url;
	}

	public void setAppicon_Url(String appicon_Url) {
		this.appicon_Url = appicon_Url;
	}
	
	public String getRecommendUrl() {
		return recommendUrl;
	}
	public void setRecommendUrl(String recommendUrl) {
		this.recommendUrl = recommendUrl;
	}
	
	public int getIconID() {
		return iconID;
	}

	public void setIconID(int iconID) {
		this.iconID = iconID;
	}
}
