package com.ranger.bmaterials.mode;

import java.io.Serializable;

public class ActivityInfo implements Serializable {

	/*
	 * gameid string 游戏id actid string 活动id acttitle string 活动标题 gameicon string
	 * 游戏图标地址 acttime string 活动时间 actcontent string活动内容
	 */

	private static final long serialVersionUID = -2146116805696802819L;
	private String gameId;
	private String id;
	private String title;
	private String gameIcon;
	private long time;
	private String content;

	public ActivityInfo() {
		super();
	}

	public ActivityInfo(String gameId, String id, String title, String gameIcon, long time) {
		super();
		this.gameId = gameId;
		this.id = id;
		this.title = title;
		this.gameIcon = gameIcon;
		this.time = time;
	}

	public ActivityInfo(String gameId, String id, String title, String gameIcon, String content, long time) {
		this(gameId, id, title, gameIcon, time);
		this.content = content;
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "ActivityInfo [gameId=" + gameId + ", id=" + id + ", title=" + title + ", gameIcon=" + gameIcon + ", time=" + time + ", content=" + content + "]";
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
