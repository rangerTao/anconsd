package com.ranger.bmaterials.mode;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

public class GameTypeInfo implements Serializable{
	private String label;
	private String gametypeicon;
	private String gametype;
	private String gametypenumber;
	private String gametypename;
	private String totalcount;
	private ArrayList<GameBriefInfo> games = new ArrayList<GameBriefInfo>();
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getGametypeicon() {
		return gametypeicon;
	}
	public void setGametypeicon(String gametypeicon) {
		this.gametypeicon = gametypeicon;
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
	public String getGametypename() {
		return gametypename;
	}
	public void setGametypename(String gametypename) {
		this.gametypename = gametypename;
	}
	public String getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(String totalcount) {
		this.totalcount = totalcount;
	}
	public ArrayList<GameBriefInfo> getGames() {
		return games;
	}
	public void setGame(GameBriefInfo games) {
		this.games.add(games);
	}
	
	public class GameBriefInfo implements  Serializable{
		
		public GameBriefInfo(JSONObject json){
			
			try {
				gameid = json.getString("gameid");
				gamename = json.getString("gamename");
				pkgname = json.getString("pkgname");
			} catch (Exception e) {
				if(gameid == null)
					gameid = "";
				
				if(gamename == null)
					gamename = "";
				
				if(pkgname == null)
					pkgname = "";
			}
			
		}
		
		public String gameid;
		public String gamename;
		public String pkgname;
	}
}
