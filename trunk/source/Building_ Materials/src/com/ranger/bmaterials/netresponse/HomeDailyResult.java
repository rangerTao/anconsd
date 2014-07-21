package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.GameInfo;

public class HomeDailyResult extends BaseResult {
	private String occnumber;
	private String interval;
	private String dialogtype;
	private String title;
	private String picurl;
	private String content;
	private String skiptype;
	private String actid;
	private String gameid;

	private ArrayList<GameInfo> dailyGameInfos = new ArrayList<GameInfo>();

	public String getOccnumber() {
		return occnumber;
	}

	public void setOccnumber(String occnumber) {
		this.occnumber = occnumber;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getDialogtype() {
		return dialogtype;
	}

	public void setDialogtype(String dialogtype) {
		this.dialogtype = dialogtype;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSkiptype() {
		return skiptype;
	}

	public void setSkiptype(String skiptype) {
		this.skiptype = skiptype;
	}

	public String getActid() {
		return actid;
	}

	public void setActid(String actid) {
		this.actid = actid;
	}

	public ArrayList<GameInfo> getDailyGameInfos() {
		return dailyGameInfos;
	}

	public void setDailyGameInfos(ArrayList<GameInfo> dailyGameInfos) {
		this.dailyGameInfos = dailyGameInfos;
	}

	public String getGameid() {
		return gameid;
	}

	public void setGameid(String gameid) {
		this.gameid = gameid;
	}
}
