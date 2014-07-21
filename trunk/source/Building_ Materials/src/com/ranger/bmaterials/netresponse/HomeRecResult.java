package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.SearchResult.SearchItem;

public class HomeRecResult extends BaseResult {
	private ArrayList<SearchItem> recGames = new ArrayList<SearchItem>();
	private ArrayList<Infos> recInfos = new ArrayList<Infos>();

	public ArrayList<SearchItem> getRecGames() {
		return recGames;
	}

	public void setRecGames(ArrayList<SearchItem> recGames) {
		this.recGames = recGames;
	}

	public ArrayList<Infos> getRecInfos() {
		return recInfos;
	}

	public void setRecInfos(ArrayList<Infos> recInfos) {
		this.recInfos = recInfos;
	}

	public class Infos {
		private String infoId;
		private String infoType;
		private String infoContent;
		private String gameid;

		public String getInfoId() {
			return infoId;
		}

		public void setInfoId(String string) {
			this.infoId = string;
		}

		public String getInfoType() {
			return infoType;
		}

		public void setInfoType(String infoType) {
			this.infoType = infoType;
		}

		public String getInfoContent() {
			return infoContent;
		}

		public void setInfoContent(String infoContent) {
			this.infoContent = infoContent;
		}

		public String getGameid() {
			return gameid;
		}

		public void setGameid(String gameid) {
			this.gameid = gameid;
		}

	}
}
