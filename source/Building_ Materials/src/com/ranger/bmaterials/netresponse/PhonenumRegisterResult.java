package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.tools.MyLogger;

public class PhonenumRegisterResult extends BaseResult {

	private int registertype = 1;
	private String userid = "";
	private String sessionid = "";
	private String username = "";
	private String nickname = "";
	
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

	public void parse(String resData) {
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			this.setTag(tag);
			this.setErrorCode(errorcode);
			this.setErrorString(errorStr);
			
            String username = jsonObj.getString(Constants.JSON_USERNAME);
            this.setUsername(username);
            
            String userid = jsonObj.getString(Constants.JSON_USERID);
            this.setUserid(userid);

            int registertype = jsonObj.getInt(Constants.JSON_REGISTERTYPE);
            this.setRegistertype(registertype);
            
            String sessionid = jsonObj.getString(Constants.JSON_SESSIONID);
            this.setSessionid(sessionid);
            
            String nickname = jsonObj.getString(Constants.JSON_NICKNAME);
            this.setNickname(nickname);
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}
}
