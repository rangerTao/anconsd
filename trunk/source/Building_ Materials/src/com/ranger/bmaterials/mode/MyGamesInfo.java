package com.ranger.bmaterials.mode;

import android.graphics.drawable.Drawable;

public final class MyGamesInfo extends Object {
	private String name;
	private Drawable icon;
	private String pkgName;
	private String action;
	private boolean needLogin;
	private String gameid;
	private long lastStartTime;

	public final long getLastStartTime() {
		return lastStartTime;
	}

	public final void setLastStartTime(long lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public final String getGameid() {
		return gameid;
	}

	public final void setGameid(String gameid) {
		this.gameid = gameid;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final Drawable getIcon() {
		return icon;
	}

	public final void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public final String getPkgName() {
		return pkgName;
	}

	public final void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public final String getAction() {
		return action;
	}

	public final void setAction(String action) {
		this.action = action;
	}

	public final boolean isNeedLogin() {
		return needLogin;
	}

	public final void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

}
