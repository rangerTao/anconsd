package com.ranger.bmaterials.mode;

public class ADInfo {
	private String adgameid;
	private String adgamename;
	private String adpkgname;
	private String adpicurl;
	private int adtype = -1; // 0 单个游戏 1 单个攻略 2 单个抢号 3 单个活动 4 单个开服
								// 5分类列表(某一类游戏列表)
	private String itemid; // 根据广告类型可能是攻略id，抢号id，活动id...
	private String gametype; // 单机or网游
	private String gametypenumber; // 游戏小类别号
	private String prizeurl;

	public String getAdgameid() {
		return adgameid;
	}

	public void setAdgameid(String adgameid) {
		this.adgameid = adgameid;
	}

	public String getAdgamename() {
		return adgamename;
	}

	public void setAdgamename(String adgamename) {
		this.adgamename = adgamename;
	}

	public String getAdpkgname() {
		return adpkgname;
	}

	public void setAdpkgname(String adpkgname) {
		this.adpkgname = adpkgname;
	}

	public String getAdpicurl() {
		return adpicurl;
	}

	public void setAdpicurl(String adpicurl) {
		this.adpicurl = adpicurl;
	}

	public int getAdtype() {
		return adtype;
	}

	public void setAdtype(int adtype) {
		this.adtype = adtype;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getGametype() {
		return gametype;
	}

	public void setGametype(String gametype) {
		this.gametype = gametype;
	}

	public String getGametypenumber() {
		return gametypenumber;
	}

	public void setGametypenumber(String gametypenumber) {
		this.gametypenumber = gametypenumber;
	}

	public String getPrizeurl() {
		return prizeurl;
	}

	public void setPrizeurl(String prizeurl) {
		this.prizeurl = prizeurl;
	}

}
