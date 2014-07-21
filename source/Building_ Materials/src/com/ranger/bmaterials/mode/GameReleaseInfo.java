package com.ranger.bmaterials.mode;

public class GameReleaseInfo {
	public static enum ReleaseStatus{
		NOT_LOGIN ,
		SNAPPED,
		NOT_SNAPPED,
		OVER;
	}
/*	gameid	string	游戏id	
	openserverid	string	开服id	
	gamename	string	游戏名称	
	opentitle	string	开服标题	
	gameicon	string	游戏图标地址	
	openstatus	string	开服状态	
	opentime	string	开服时间	
	totalcount	string	记录总数
	*/
	private long gameId ;
	private long id;
	private String name ;
	private String title ;
	private String iconUrl ;
	private long openTime ;
	private int totalCount ;
	private ReleaseStatus status ;
	
	
	public GameReleaseInfo() {
		super();
		// TODO Auto-generated constructor stub
	}


	public GameReleaseInfo(long gameId, long id, String name, String title,
			String iconUrl, long openTime, int totalCount, ReleaseStatus status) {
		super();
		this.gameId = gameId;
		this.id = id;
		this.name = name;
		this.title = title;
		this.iconUrl = iconUrl;
		this.openTime = openTime;
		this.totalCount = totalCount;
		this.status = status;
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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


	public long getOpenTime() {
		return openTime;
	}


	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}


	public ReleaseStatus getStatus() {
		return status;
	}


	public void setStatus(ReleaseStatus status) {
		this.status = status;
	}
	
	
}
