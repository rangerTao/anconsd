package com.ranger.lpa;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ranger.lpa.pojos.PurnishList;

public class MineProfile{

	public static int USERTYPE_UNBINDINGPHONE = 2;
    private static final long LOCK_PERIOD = 60 * 60 * 1000;

	private String userID;
	private String userName;
	private String nickName;
	private String strUserHead;
	private boolean isLocked;
	private String sessionID;
    private String udid;

    private long lockPeriod;

    private PurnishList purnish;

	// settings
	private String push_userid;
	private String push_channelid;
	private String appversion;

	private List<String> accountList;

	private MineProfile() {
		userID = "";
		userName = "";
		nickName = "";
		isLocked = false;
		sessionID = "";
		appversion = "";
        lockPeriod = LOCK_PERIOD;

		accountList = new ArrayList<String>();
	}

	// ����
	private static MineProfile gInstance;

	public static MineProfile getInstance() {
		if (gInstance == null) {
			synchronized (MineProfile.class) {
				if (gInstance == null) {
					gInstance = new MineProfile();
					gInstance.Load();
				}
			}
		}

		return gInstance;
	}

	public void Reset() {
		Context context = LPApplication.getInstance()
				.getApplicationContext();
		Reset(context);
	}

	public void Reset(Context context) {
		userID = "";
		userName = "";
		nickName = "";
		sessionID = "";
		appversion = "";
		isLocked = false;
        lockPeriod = LOCK_PERIOD;
        Save(context);
	}

	// for debug
	public void Print() {
	}

	private void Load() {
		Context context = LPApplication.getInstance()
				.getApplicationContext();
		Load(context);
	}

    Gson gson = new Gson();

	private void Load(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Constants.SETTINGS_PREFERENCE, Activity.MODE_MULTI_PROCESS);

		this.userID = settings.getString("userID", "");
		this.userName = settings.getString("userName", "");
		this.nickName = settings.getString("nickName", "");
		this.isLocked = settings.getBoolean("isLocked", false);

		this.sessionID = settings.getString("sessionID", "");
		// settings
		this.appversion = settings.getString("appversion", "");

		this.push_channelid = settings.getString("push_channelid", "");
		this.push_userid = settings.getString("push_userid", "");
		this.strUserHead = settings.getString("user_head", "");
        this.udid = settings.getString("udid","");
        this.lockPeriod = settings.getLong("lock_period", LOCK_PERIOD);
        this.purnish = gson.fromJson(settings.getString("purnish",""),PurnishList.class);
        String accountList = settings.getString("accountlist", "");
		// accountList = "aaaaa;bbbbb;dcccc;ddddd;eeeee";

		if (accountList.length() > 0) {
			String accouts[] = accountList.split(";");

			for (String string : accouts) {
				if (string.length() > 0) {
					this.accountList.add(string);
				}
			}
		}
	}

	public boolean Save() {
		Context context = LPApplication.getInstance()
				.getApplicationContext();
		return Save(context);
	}

	@SuppressLint("WorldWriteableFiles")
	public boolean Save(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Constants.SETTINGS_PREFERENCE, Activity.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userID", this.userID);
		editor.putString("userName", this.userName);
		editor.putString("nickName", this.nickName);
        editor.putString("udid",this.udid);

		editor.putString("sessionID", this.sessionID);

		editor.putString("appversion", this.appversion);

		editor.putString("push_channelid", this.push_channelid);
		editor.putString("push_userid", this.push_userid);
        editor.putLong("lock_period", this.lockPeriod);

		String accountList = "";
		for (String string : this.accountList) {
			accountList += string;
			accountList += ";";
		}

		editor.putString("accountlist", accountList);
		editor.putString("user_head", strUserHead);
        String purnish = gson.toJson(getPurnish());
        editor.putString("purnish",purnish);
		return editor.commit();
	}

    public PurnishList getPurnish() {
        return purnish;
    }

    public void setPurnish(PurnishList purnish) {
        this.purnish = purnish;
    }

    public long getLockPeriod() {
        return lockPeriod;
    }

    public void setLockPeriod(long lockPeriod) {
        this.lockPeriod = lockPeriod;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public List<String> getAccountsList() {
		return this.accountList;
	}

	public void addAccount(String accout) {
		boolean found = false;

		for (String string : this.accountList) {
			if (string.equals(accout)) {
				found = true;
				break;
			}
		}

		if (!found) {
			this.accountList.add(accout);
		}
	}

	public void removeAccount(String account) {
		for (int i = 0; i < this.accountList.size(); i++) {
			if (this.accountList.get(i).equals(account)) {
				this.accountList.remove(i);
				break;
			}
		}
	}

	public String getPush_userid() {
		return push_userid;
	}

	public void setPush_userid(String push_userid) {
		this.push_userid = push_userid;
	}

	public String getPush_channelid() {
		return push_channelid;
	}

	public void setPush_channelid(String push_channelid) {
		this.push_channelid = push_channelid;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {

		if (userID == null) {
			this.userID = "";
			return;
		}

		if (!this.userID.equals(userID)) {
			this.userID = userID;
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {

		if (userName == null) {
			this.userName = "";
			return;
		}

		if (!this.userName.equals(userName)) {
			this.userName = userName;
		}
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {

		if (nickName == null || nickName.length() <= 0) {
			this.nickName = "";
			return;
		}
		if (!this.nickName.equals(nickName)) {
			this.nickName = nickName;
		}
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLogin) {
		this.isLocked = isLogin;

		if (this.isLocked != isLogin) {
			this.isLocked = isLogin;
		}

		if (this.isLocked) {
			/**
			 * 通知用户已经登陆
			 */
		} else {

			/**
			 * 通知用户退出登陆
			 */
		}
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {

		if (sessionID == null) {
			this.sessionID = "";
			return;
		}

		if (!this.sessionID.equals(sessionID)) {
			this.sessionID = sessionID;
		}
	}

    public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public String getStrUserHead() {
		return strUserHead;
	}

	public void setStrUserHead(String strUserHead) {
		this.strUserHead = strUserHead;
	}
	
	
}
