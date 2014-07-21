package com.ranger.bmaterials.mode;

import java.io.Serializable;

public class OpenServer implements Serializable {

	/*
	 * gameid string 游戏id openserverid string 开服id gamename string 游戏名称
	 * opentitle string 开服标题 gameicon string 游戏图标地址 openstatus string 开服状态
	 * opentime string 开服时间
	 */

	private static final long serialVersionUID = 3763219374238271380L;
	/*
	 * public static enum OpenServerStatus{ PENDING(
	 * com.duoku.gamesearch.app.Constants.OpenServerStatus.PENDING),
	 * IN_PROGRESS(
	 * com.duoku.gamesearch.app.Constants.OpenServerStatus.IN_PROGRESS),
	 * OVER(com.duoku.gamesearch.app.Constants.OpenServerStatus.OVER);
	 * 
	 * private int code ; private OpenServerStatus(int code){ this.code = code ;
	 * } public static OpenServerStatus getStatus(int statusInt){
	 * OpenServerStatus[] values = OpenServerStatus.values(); for
	 * (OpenServerStatus s : values) { if(s.code == statusInt){ return s ; } }
	 * return null ; }
	 * 
	 * }
	 */
	private String gameId;
	private String id;
	private String gameName;
	private String title;
	private String gameIcon;
	private String pkgsize;
	private String pkgname;
	public String getVersionname() {
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}

	public String getVersioncode() {
		return versioncode;
	}

	public void setVersioncode(String versioncode) {
		this.versioncode = versioncode;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public String getActsource() {
		return actsource;
	}

	public void setActsource(String actsource) {
		this.actsource = actsource;
	}

	private String versionname;
	private String versioncode;
	private String downloadurl;
	private String actsource;
	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	// private OpenServerStatus status;
	private long time;

	public OpenServer() {
		super();
	}

	public OpenServer(String gameId, String id, String gameName, String title,
			String gameIcon, /* OpenServerStatus status, */long time) {
		super();
		this.gameId = gameId;
		this.id = id;
		this.gameName = gameName;
		this.title = title;
		this.gameIcon = gameIcon;
		// this.status = status;
		this.time = time;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGameIcon() {
		return gameIcon;
	}

	public void setGameIcon(String gameIcon) {
		this.gameIcon = gameIcon;
	}

	/*
	 * public OpenServerStatus getStatus() { return status; } public void
	 * setStatus(OpenServerStatus status) { this.status = status; }
	 */
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "OpenServer [gameId=" + gameId + ", id=" + id + ", gameName="
				+ gameName + ", title=" + title + ", gameIcon=" + gameIcon
				+ ", time=" + time + "]";
	}

	public String getPkgsize() {
		return pkgsize;
	}

	public void setPkgsize(String pkgsize) {
		this.pkgsize = pkgsize;
	}

}
