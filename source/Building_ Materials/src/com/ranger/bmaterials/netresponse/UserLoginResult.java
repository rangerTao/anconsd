package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;

public class UserLoginResult extends BaseResult {
	private int registertype = 1;
	private String userid = "";
	private String sessionid = "";
	private String username = "";
	private String nickname = "";
	private String phonenum = "";
	private String gamenum = "";
	private String totalmsgnum = "";
	private String messagenum = "";
	private String collectnum = "";
	private int coinnum = 0;
	private int isloginReq;
	
	public int getIsloginReq() {
		return isloginReq;
	}
	public void setIsloginReq(int isloginReq) {
		this.isloginReq = isloginReq;
	}
	public int getRegistertype() {
		return registertype;
	}
	public void setRegistertype(int registertype) {
		this.registertype = registertype;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public String getGamenum() {
		return gamenum;
	}
	public void setGamenum(String gamenum) {
		this.gamenum = gamenum;
	}
	public String getTotalmsgnum() {
		return totalmsgnum;
	}
	public void setTotalmsgnum(String totalmsgnum) {
		this.totalmsgnum = totalmsgnum;
	}
	public String getMessagenum() {
		return messagenum;
	}
	public void setMessagenum(String messagenum) {
		this.messagenum = messagenum;
	}
	public String getCollectnum() {
		return collectnum;
	}
	public void setCollectnum(String collectnum) {
		this.collectnum = collectnum;
	}
	public int getCoinnum() {
		return coinnum;
	}
	
	public void setCoinnum(int coinnum) {
		this.coinnum = coinnum;
	}
}
