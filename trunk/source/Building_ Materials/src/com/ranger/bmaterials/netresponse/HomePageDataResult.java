package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.ADInfo;
import com.ranger.bmaterials.mode.HomeAppGridInfo;

public class HomePageDataResult extends HomeAppResult {

	private ArrayList<ADInfo> adsList = new ArrayList<ADInfo>();
	private ArrayList<HomeAppGridInfo> gamesGrid = new ArrayList<HomeAppGridInfo>();

	private String resData;

	private String gameListTitle;

	public final String getGameListTitle() {
		return gameListTitle;
	}

	public final void setGameListTitle(String gameListTitle) {
		this.gameListTitle = gameListTitle;
	}

	public final String getResData() {
		return resData;
	}

	public final void setResData(String resData) {
		this.resData = resData;
	}

	public final ArrayList<ADInfo> getAdsList() {
		return adsList;
	}

	public final void setAdsList(ArrayList<ADInfo> adsList) {
		this.adsList = adsList;
	}

	public final ArrayList<HomeAppGridInfo> getGamesGrid() {
		return gamesGrid;
	}

	public final void setGamesGrid(ArrayList<HomeAppGridInfo> gamesGrid) {
		this.gamesGrid = gamesGrid;
	}

	public String getJsonRes() {
		return resData;
	}
}
