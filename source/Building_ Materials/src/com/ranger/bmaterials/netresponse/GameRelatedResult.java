package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.GameRelatedInfo;

public class GameRelatedResult extends BaseResult{

	private String pkgName;
	
	private ArrayList<GameRelatedInfo> infoList = new ArrayList<GameRelatedInfo>();

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public final ArrayList<GameRelatedInfo> getGamesList() {
		return infoList;
	}

	public final void setGamesList(ArrayList<GameRelatedInfo> gamesList) {
		this.infoList = gamesList;
	}
	
}