package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.tools.MyLogger;

public class UserNameRegisterResult extends BaseResult {

	private int registertype = 1;
	private String userid = "";
	private String sessionid = "";
	private String username = "";
	private String nickname = "";
	private int coinnum = 0;
	
	public int getCoinnum() {
		return coinnum;
	}

	public void setCoinnum(int coinnum) {
		this.coinnum = coinnum;
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

}
