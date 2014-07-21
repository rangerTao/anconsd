package com.ranger.bmaterials.mode;

import java.io.Serializable;

import com.ranger.bmaterials.app.Constants.GrabStatus;

public class SnapNumber implements Serializable{
	private static final long serialVersionUID = 602098168842525685L;

	public static enum SnapNumberStatus{
		// 0 未登录 1 未抢 2 已抢 3 已结束
		NOT_LOGIN(GrabStatus.NOT_LOGIN), SNAPPED(GrabStatus.SNAPPED), NOT_SNAPPED(
				GrabStatus.NOT_SNAPPED), NONE(-1), // 协议上没有
		OVER(GrabStatus.OVER);
		public int code;

		private SnapNumberStatus(int code) {
			this.code = code;
		}

		public static SnapNumberStatus getStatus(int statusInt) {
			SnapNumberStatus[] values = SnapNumberStatus.values();
			for (SnapNumberStatus s : values) {
				if (s.code == statusInt) {
					return s;
				}
			}
			return null;
		}
	}

	/*
	 * grabgames array 抢号列表 gameid string 游戏id grabid string 抢号id grabtitle
	 * string 标题 gameicon string 游戏图标地址 numberrest string 抢号剩余 grabstatus string
	 * 抢号状态 0 未登录 1 未抢 2 已抢 3 已结束 totalcount string 记录总数
	 */
	private String gameId;
	private String id;
	String packageName;
	private String title;
	private String iconUrl;
	private int leftCount;
	private int totalCount;
	private SnapNumberStatus status;
	private long time;
	private String number;
	public PackageMode mode;
	
	public String downloadUrl;
	public String startAction;
	public boolean isNeedLogin;
	public String version;
	public int verCode = -1;
	public String pkgSize;
	public String gameName;

	public SnapNumber(String gameId, String id, String title, String iconUrl,
			int leftCount, int totalCount, SnapNumberStatus status, long time,
			String number, String pkgName) {
		super();
		this.gameId = gameId;
		this.id = id;
		this.title = title;
		this.iconUrl = iconUrl;
		this.leftCount = leftCount;
		this.totalCount = totalCount;
		this.status = status;
		this.time = time;
		this.number = number;
		this.packageName = pkgName;
	}

	public SnapNumber() {
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

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public SnapNumberStatus getStatus() {
		return status;
	}

	public void setStatus(SnapNumberStatus status) {
		this.status = status;
	}

	public int getLeftCount() {
		return leftCount;
	}

	public void setLeftCount(int leftCount) {
		this.leftCount = leftCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "SnapNumber [gameId=" + gameId + ", id=" + id + ", title="
				+ title + ", iconUrl=" + iconUrl + ", leftCount=" + leftCount
				+ ", totalCount=" + totalCount + ", status=" + status + "]";
	}

}
