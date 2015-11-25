package com.andconsd;

import com.andconsd.framework.utils.Constants;
import com.andconsd.framework.utils.MyLogger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class MineProfile {

	private MyLogger logger = MyLogger.getLogger("MineProfile");
	public static int DEVICE_LOGIN = 1;
	public static int PHONENUM_LOGIN = 2;
	public static int ACCOUNT_LOGIN = 3;

	private boolean isRootUser;

	private String phonenum;

	private String channelId;//默认渠道号
	private int checkRootPrompTime;

	private boolean updateAvailable;
	private long lastUpdateTime;
	private long lastUpdateSMSCTime;
	private long lastCheckRootTime;

	private boolean hasShowNotification;
	private long timeShowNotification;
	private boolean installAutomaticllyAfterDownloading;

	public static final String MINE_DYNAMIC_DATA_NOTIFICATION = "com.duoku.gamesearch.mydynamicdata";
	public static final String MINE_DYNAMIC_DATA_REFRESH = "com.duoku.gamesearch.refreshdata";
	public static final String MINE_ADD_KUDOU_NOTIFICATION = "com.duoku.gamesearch.addkudou";

	private String push_userid;
	private String push_channelid;
	private String appversion;


	private MineProfile() {
		phonenum = "";

		isRootUser = false;
		channelId = "";
		appversion = "";

		lastUpdateTime = 0;
		lastUpdateSMSCTime = 0;
		lastCheckRootTime = 0;
		installAutomaticllyAfterDownloading = false;

	}

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
		Context context = AndApplication.getAppInstance().getApplicationContext();
		Reset(context);
	}

	public void Reset(Context context) {
		phonenum = "";
		channelId = "";
		checkRootPrompTime = 0;
		appversion = "";
		isRootUser = false;
		Save(context);
	}

	// for debug
	public void Print() {

		MyLogger logger = MyLogger.getLogger(this.getClass().getSimpleName());

		logger.d("MineProfile Debug Print --- begin");
		logger.d("isRootUser: " + this.isRootUser);
		logger.d("phonenum: " + this.phonenum);

		logger.d("channelId:" + this.channelId);
		logger.d("checkRootPromptTime:" + this.checkRootPrompTime);
		logger.d("appversion:" + this.appversion);
		logger.d("installAutomaticllyAfterDownloading: " + this.installAutomaticllyAfterDownloading);

		logger.d("MineProfile Debug Print --- end");
	}

	private void Load() {
		Context context = AndApplication.getAppInstance().getApplicationContext();
		Load(context);
	}

	private void Load(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_PREFERENCE, Activity.MODE_MULTI_PROCESS);

		this.isRootUser = settings.getBoolean("isRootUser", false);

		this.phonenum = settings.getString("phonenum", "");

		this.channelId = settings.getString("channelId", "");
		this.checkRootPrompTime = settings.getInt("checkrootPrompTime", 0);
		this.appversion = settings.getString("appversion", "");

		this.updateAvailable = settings.getBoolean("updateavailable", false);
		this.lastUpdateTime = settings.getLong("lastupdatetime", 0);
		this.lastUpdateSMSCTime = settings.getLong("lastUpdateSMSCTime", 0);
		this.lastCheckRootTime = settings.getLong("lastCheckRootTime", 0);

		this.hasShowNotification = settings.getBoolean("hasShowNotification", false);
		this.timeShowNotification = settings.getLong("timeShowNotification", 0);

		this.push_channelid = settings.getString("push_channelid", "");
		this.push_userid = settings.getString("push_userid", "");
		this.installAutomaticllyAfterDownloading = settings.getBoolean("installAutomaticllyAfterDownloading", false);
		String accountList = settings.getString("accountlist", "");
	}

	public boolean Save() {
		Context context = AndApplication.getAppInstance().getApplicationContext();
		return Save(context);
	}

	@SuppressLint("WorldWriteableFiles")
	public boolean Save(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_PREFERENCE, Activity.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("isRootUser", this.isRootUser);

		editor.putString("phonenum", this.phonenum);

		editor.putString("channelId", this.channelId);
		editor.putInt("checkrootPrompTime", this.checkRootPrompTime);
		editor.putString("appversion", this.appversion);

		editor.putBoolean("updateavailable", this.updateAvailable);
		editor.putLong("lastupdatetime", this.lastUpdateTime);
		editor.putLong("lastUpdateSMSCTime", this.lastUpdateSMSCTime);
		editor.putLong("lastCheckRootTime", this.lastCheckRootTime);

		editor.putBoolean("hasShowNotification", this.hasShowNotification);
		editor.putLong("timeShowNotification", this.timeShowNotification);

		editor.putString("push_channelid", this.push_channelid);
		editor.putString("push_userid", this.push_userid);
		editor.putBoolean("installAutomaticllyAfterDownloading", this.installAutomaticllyAfterDownloading);

		return editor.commit();
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

	public long getTimeShowNotification() {
		return timeShowNotification;
	}

	public void setTimeShowNotification(long timeShowNotification) {
		this.timeShowNotification = timeShowNotification;
	}

	public boolean isHasShowNotification() {
		logger.d("hasShowNotification: " + hasShowNotification + "------------");
		return hasShowNotification;
	}

	public void setHasShowNotification(boolean hasShowNotification) {
		logger.d("setHasShowNotification: " + hasShowNotification + "------------");
		this.hasShowNotification = hasShowNotification;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public long getLastUpdateSMSCTime() {
		return lastUpdateSMSCTime;
	}

	public void setLastUpdateSMSCTime(long lastUpdateSMSCTime) {
		this.lastUpdateSMSCTime = lastUpdateSMSCTime;
	}

	public long getLastCheckrootTime() {
		return lastCheckRootTime;
	}

	public void setLastCheckrootTime(long lastCheckTime) {
		this.lastCheckRootTime = lastCheckTime;
	}

	public boolean isUpdateAvailable() {
		return updateAvailable;
	}

	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {

		if (phonenum == null) {
			this.phonenum = "";
			return;
		}

		if (!this.phonenum.equals(phonenum)) {
			this.phonenum = phonenum;
		}
	}

	public String getChannelId() {
		return channelId;
	}
	
	public void setChannelId(String setChannelId) {
		this.channelId = setChannelId;
	}

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public int getCheckRootPrompTime() {
		return checkRootPrompTime;
	}

	public void setCheckRootPrompTime(int prompTime) {
		this.checkRootPrompTime = prompTime;
	}

	public boolean isRootUser() {
		return isRootUser;
	}

	public void setIsRootUser(boolean isRootUser) {
		this.isRootUser = isRootUser;
	}

	public void broadcastRefreshMsgEvent() {
		Intent intent = new Intent(MineProfile.MINE_DYNAMIC_DATA_REFRESH);
		AndApplication.getAppInstance().sendBroadcast(intent);
	}

	public static final String ADD_kudou_NUM_EXTRA = "addkudounum";
	public static final String TOTAL_kudou_NUM_EXTRA = "kudounum";

	public boolean isInstallAutomaticllyAfterDownloading() {
		return installAutomaticllyAfterDownloading;
	}

	public void setInstallAutomaticllyAfterDownloading(boolean installAutomaticllyAfterDownloading) {
		if (this.installAutomaticllyAfterDownloading != installAutomaticllyAfterDownloading) {
			this.installAutomaticllyAfterDownloading = installAutomaticllyAfterDownloading;
		}
		Save(AndApplication.getAppInstance());
		// BackAppListener backAppListener = BackAppListener.getInstance();
		// backAppListener.changeAutoInstall(installAutomaticllyAfterDownloading);
	}
}
