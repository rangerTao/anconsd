package com.ranger.bmaterials.mode;

public class MyDownloadedGame {
	/**
	 * 
	 * 	gameid  string  游戏id   
		gamename  string  游戏名称   
		gameicon  string  游戏icon下载地址   
		pkgname  string  游戏包名   
		startaction  string  联运游戏启动的key  

	 */
	
	private String gameId ;
	private String name ;
	private String iconUrl ;
	private String packageName ;
	private String key ;
	private String extra ;
	private boolean needLogin ;
	
	public MyDownloadedGame(String gameId, String gamename, String iconUrl,
			String packageName, String key, String extra,boolean needLogin) {
		super();
		this.gameId = gameId;
		this.name = gamename;
		this.iconUrl = iconUrl;
		this.packageName = packageName;
		this.key = key;
		this.extra = extra;
		this.needLogin = needLogin;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}


	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}
	
	
}
