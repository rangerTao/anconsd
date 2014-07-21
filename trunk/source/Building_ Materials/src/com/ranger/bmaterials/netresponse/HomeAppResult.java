package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.HomeAppListInfoArray;

public class HomeAppResult extends BaseResult {
	private ArrayList<HomeAppListInfoArray> gamesList = new ArrayList<HomeAppListInfoArray>();
	public int gamescount;

	public final ArrayList<HomeAppListInfoArray> getGamesList() {
		return gamesList;
	}

	public final void setGamesList(ArrayList<HomeAppListInfoArray> gamesList) {
		this.gamesList = gamesList;
	}

}
