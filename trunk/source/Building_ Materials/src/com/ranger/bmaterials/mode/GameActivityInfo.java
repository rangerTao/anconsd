package com.ranger.bmaterials.mode;

public class GameActivityInfo {
	/*activities	array	活动列表	
	gameid	string	游戏id	
	actid	string	活动id	
	acttitle	string	活动标题	
	gameicon	string	游戏图标地址	
	acttime	string	活动时间	
	totalcount	string	记录总数*/
	
	private long gameId ;
	private long id;
	private String title ;
	private String iconUrl ;
	private long time ;
	private int totalCount ;
	
	
	public GameActivityInfo() {
		super();
		// TODO Auto-generated constructor stub
	}


	public GameActivityInfo(long gameId, long id, String title, String iconUrl,
			long time, int totalCount) {
		super();
		this.gameId = gameId;
		this.id = id;
		this.title = title;
		this.iconUrl = iconUrl;
		this.time = time;
		this.totalCount = totalCount;
	}


	public long getGameId() {
		return gameId;
	}


	public void setGameId(long gameId) {
		this.gameId = gameId;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getIconUrl() {
		return iconUrl;
	}


	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}


	public long getTime() {
		return time;
	}


	public void setTime(long time) {
		this.time = time;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}


}
