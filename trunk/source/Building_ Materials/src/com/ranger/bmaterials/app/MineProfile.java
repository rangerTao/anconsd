package com.ranger.bmaterials.app;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.MyLogger;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class MineProfile implements IRequestListener {

	private MyLogger logger = MyLogger.getLogger("MineProfile");
	public static int USERTYPE_BINGDINGPHONE = 1;
	public static int USERTYPE_UNBINDINGPHONE = 2;
	public static int USERTYPE_PHONEUSER = 3;

    private String token;
	private String userID;
	private String userName;
	private String nickName;
	private String strUserHead;
	private boolean isLogin;
	private String sessionID;
	private boolean isNewUser;
	private boolean isRootUser;

    private String area;
    private String signture;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSignture() {
        return signture;
    }

    public void setSignture(String signture) {
        this.signture = signture;
    }

    private int userType;
	private String phonenum;

	// settings
	private boolean downloadOnlyWithWiFi;
	private boolean noPicture;
	private int simultaneousDownloadNum;
	private boolean deletePkgAfterInstallation;
	private boolean showInstallTipAfterDownloading;
	private boolean installAutomaticllyAfterDownloading;

	//
	private String gamenum;
	private String totalmsgnum;
	private String messagenum;
	private String collectnum;
	private int coinnum;// 金币总数
	private int checkRootPrompTime;

	private boolean updateAvailable;
	private long lastUpdateTime;
	private long lastUpdateSMSCTime;
	private long lastCheckRootTime;

	private boolean hasShowNotification;
	private long timeShowNotification;

	public static final String MINE_DYNAMIC_DATA_NOTIFICATION = "com.duoku.gamesearch.mydynamicdata";
	public static final String MINE_DYNAMIC_DATA_REFRESH = "com.duoku.gamesearch.refreshdata";

    // use as area;
	private String push_userid;
	private String push_channelid;
	private String appversion;

	private List<String> accountList;

	private MineProfile() {
        token = "";
		userID = "";
		userName = "";
		nickName = "";
		isLogin = false;
		isNewUser = false;
		isRootUser = false;
		sessionID = "";
		userType = USERTYPE_UNBINDINGPHONE;
		phonenum = "";
		downloadOnlyWithWiFi = true;
		noPicture = false;
		simultaneousDownloadNum = 3;
		deletePkgAfterInstallation = false;
		showInstallTipAfterDownloading = true;
		installAutomaticllyAfterDownloading = false;

		gamenum = "0";
		totalmsgnum = "0";
		messagenum = "0";
		collectnum = "0";
		coinnum = 0;
		checkRootPrompTime = 0;
		appversion = "";

		lastUpdateTime = 0;
		lastUpdateSMSCTime = 0;
		lastCheckRootTime = 0;

        area = "";
        signture = "";

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
		Context context = BMApplication.getAppInstance()
				.getApplicationContext();
		Reset(context);
	}

	public void Reset(Context context) {
		userID = "";
		userName = "";
		nickName = "";
		sessionID = "";
        token = "";
		userType = USERTYPE_UNBINDINGPHONE;
		phonenum = "";
		gamenum = "0";
		totalmsgnum = "0";
		messagenum = "0";
		collectnum = "0";
		coinnum = 0;
		checkRootPrompTime = 0;
		appversion = "";
		isNewUser = false;
		isRootUser = false;
		isLogin = false;

        area = "";
        signture = "";

		Save(context);
	}

	// for debug
	public void Print() {

		MyLogger logger = MyLogger.getLogger(this.getClass().getSimpleName());

		logger.d("MineProfile Debug Print --- begin");
        logger.d("TOKEN" + this.token);
		logger.d("userID: " + this.userID);
		logger.d("userName: " + this.userName);
		logger.d("nickName: " + this.nickName);
		logger.d("isNewUser: " + this.isNewUser);
		logger.d("isRootUser: " + this.isRootUser);
		logger.d("isLogin: " + this.isLogin);
		logger.d("sessionID: " + this.sessionID);
		logger.d("userType: " + this.userType);
		logger.d("phonenum: " + this.phonenum);
		logger.d("downloadOnlyWithWiFi: " + this.downloadOnlyWithWiFi);
		logger.d("noPicture: " + this.noPicture);
		logger.d("simultaneousDownloadNum: " + this.simultaneousDownloadNum);
		logger.d("deletePkgAfterInstallation: "
				+ this.deletePkgAfterInstallation);
		logger.d("showInstallTipAfterDownloading: "
				+ this.showInstallTipAfterDownloading);
		logger.d("installAutomaticllyAfterDownloading: "
				+ this.installAutomaticllyAfterDownloading);
		logger.d("gamenum: " + this.gamenum);
		logger.d("totalmsgnum: " + this.totalmsgnum);
		logger.d("messagenum: " + this.messagenum);
		logger.d("collectnum: " + this.collectnum);
		logger.d("coinnum: " + this.coinnum);
		logger.d("checkRootPromptTime:" + this.checkRootPrompTime);
		logger.d("appversion:" + this.appversion);

		logger.d("MineProfile Debug Print --- end");
	}

	private void Load() {
		Context context = BMApplication.getAppInstance()
				.getApplicationContext();
		Load(context);
	}

	private void Load(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Constants.SETTINGS_PREFERENCE, Activity.MODE_MULTI_PROCESS);

        this.token = settings.getString("token","");
		this.userID = settings.getString("userID", "");
		this.userName = settings.getString("userName", "");
		this.nickName = settings.getString("nickName", "");
		this.isNewUser = settings.getBoolean("isNewUser", false);
		this.isRootUser = settings.getBoolean("isRootUser", false);
		this.isLogin = settings.getBoolean("isLogin", false);

		this.sessionID = settings.getString("sessionID", "");
		this.userType = settings.getInt("userType", 1);
		this.phonenum = settings.getString("phonenum", "");

        this.area = settings.getString("city","");
        this.signture = settings.getString("signture","");

		// settings
		this.downloadOnlyWithWiFi = settings.getBoolean("downloadOnlyWithWiFi", true);
		this.noPicture = settings.getBoolean("noPicture", false);
		this.simultaneousDownloadNum = settings.getInt(
				"simultaneousDownloadNum", 3);
		this.deletePkgAfterInstallation = settings.getBoolean(
				"deletePkgAfterInstallation", false);
		this.showInstallTipAfterDownloading = settings.getBoolean(
				"showInstallTipAfterDownloading", true);
		this.installAutomaticllyAfterDownloading = settings.getBoolean(
				"installAutomaticllyAfterDownloading", false);

		this.gamenum = settings.getString("gamenum", "");
		this.totalmsgnum = settings.getString("totalmsgnum", "");
		this.messagenum = settings.getString("messagenum", "");
		this.collectnum = settings.getString("collectnum", "");
		this.coinnum = settings.getInt("coinnum", 0);
		this.checkRootPrompTime = settings.getInt("checkrootPrompTime", 0);
		this.appversion = settings.getString("appversion", "");

		this.updateAvailable = settings.getBoolean("updateavailable", false);
		this.lastUpdateTime = settings.getLong("lastupdatetime", 0);
		this.lastUpdateSMSCTime = settings.getLong("lastUpdateSMSCTime", 0);
		this.lastCheckRootTime = settings.getLong("lastCheckRootTime", 0);

		this.hasShowNotification = settings.getBoolean("hasShowNotification",
				false);
		this.timeShowNotification = settings.getLong("timeShowNotification", 0);

		this.push_channelid = settings.getString("push_channelid", "");
		this.push_userid = settings.getString("push_userid", "");
		this.strUserHead = settings.getString("user_head", "");
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
		Context context = BMApplication.getAppInstance()
				.getApplicationContext();
		return Save(context);
	}

	@SuppressLint("WorldWriteableFiles")
	public boolean Save(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Constants.SETTINGS_PREFERENCE, Activity.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString("token",this.token);
		editor.putString("userID", this.userID);
		editor.putString("userName", this.userName);
		editor.putString("nickName", this.nickName);
		editor.putBoolean("isNewUser", this.isNewUser);
		editor.putBoolean("isRootUser", this.isRootUser);
		editor.putBoolean("isLogin", this.isLogin);

		editor.putString("sessionID", this.sessionID);
		editor.putInt("userType", this.userType);
		editor.putString("phonenum", this.phonenum);

		editor.putBoolean("downloadOnlyWithWiFi", this.downloadOnlyWithWiFi);
		editor.putBoolean("noPicture", this.noPicture);
		editor.putInt("simultaneousDownloadNum", this.simultaneousDownloadNum);
		editor.putBoolean("deletePkgAfterInstallation",
				this.deletePkgAfterInstallation);
		editor.putBoolean("showInstallTipAfterDownloading",
				this.showInstallTipAfterDownloading);
		editor.putBoolean("installAutomaticllyAfterDownloading",
				this.installAutomaticllyAfterDownloading);

		editor.putString("gamenum", this.gamenum);
		editor.putString("totalmsgnum", this.totalmsgnum);
		editor.putString("messagenum", this.messagenum);
		editor.putString("collectnum", this.collectnum);
		editor.putInt("coinnum", this.coinnum);
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

		String accountList = "";
		for (String string : this.accountList) {
			accountList += string;
			accountList += ";";
		}

		editor.putString("accountlist", accountList);
		editor.putString("user_head", strUserHead);

        editor.putString("city",area);
        editor.putString("signture",signture);

		return editor.commit();
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
		logger.d("setHasShowNotification: " + hasShowNotification
				+ "------------");
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

	public boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(boolean isLogin) {
		this.isLogin = isLogin;

		if (this.isLogin != isLogin) {
			this.isLogin = isLogin;
		}
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isNewUser() {
		return isNewUser;
	}

	public void setIsNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	public boolean isRootUser() {
		return isRootUser;
	}

	public void setIsRootUser(boolean isRootUser) {
		this.isRootUser = isRootUser;
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

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {

		if (this.userType != userType) {
			this.userType = userType;
		}
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

	public boolean isDownloadOnlyWithWiFi() {
		return downloadOnlyWithWiFi;
	}

	public void setDownloadOnlyWithWiFi(boolean downloadOnlyWithWiFi) {

		if (this.downloadOnlyWithWiFi != downloadOnlyWithWiFi) {
			this.downloadOnlyWithWiFi = downloadOnlyWithWiFi;
		}
	}

	public boolean isNoPicture() {
		return noPicture;
	}

	public void setNoPicture(boolean noPicture) {

		if (this.noPicture != noPicture) {
			this.noPicture = noPicture;
		}
	}

	public int getSimultaneousDownloadNum() {
		return simultaneousDownloadNum;
	}

	public void setSimultaneousDownloadNum(int simultaneousDownloadNum) {

		if (this.simultaneousDownloadNum != simultaneousDownloadNum) {
			this.simultaneousDownloadNum = simultaneousDownloadNum;
		}
	}

	public boolean isDeletePkgAfterInstallation() {
		return deletePkgAfterInstallation;
	}

	public void setDeletePkgAfterInstallation(boolean deletePkgAfterInstallation) {

		if (this.deletePkgAfterInstallation != deletePkgAfterInstallation) {
			this.deletePkgAfterInstallation = deletePkgAfterInstallation;
		}
	}

	public boolean isShowInstallTipAfterDownloading() {
		return showInstallTipAfterDownloading;
	}

	public void setShowInstallTipAfterDownloading(
			boolean showInstallTipAfterDownloading) {

		if (this.showInstallTipAfterDownloading != showInstallTipAfterDownloading) {
			this.showInstallTipAfterDownloading = showInstallTipAfterDownloading;
		}
	}

	public boolean isInstallAutomaticllyAfterDownloading() {
		return installAutomaticllyAfterDownloading;
	}

	public void setInstallAutomaticllyAfterDownloading(
			boolean installAutomaticllyAfterDownloading) {
		if (this.installAutomaticllyAfterDownloading != installAutomaticllyAfterDownloading) {
			this.installAutomaticllyAfterDownloading = installAutomaticllyAfterDownloading;
		}
		Save(BMApplication.getAppInstance());
	}

	public String getGamenum() {
		if (gamenum.length() <= 0)
			return "0";
		return gamenum;
	}

	public void setGamenum(String gamenum) {
		if (gamenum == null || gamenum.equals("")) {
			this.gamenum = "0";
			return;
		}
		this.gamenum = gamenum;
	}

	public String getTotalmsgnum() {
		if (totalmsgnum.length() <= 0)
			return "0";
		return totalmsgnum;
	}

	public void setTotalmsgnum(String totalmsgnum) {
		if (totalmsgnum == null || totalmsgnum.equals("")) {
			this.totalmsgnum = "0";
			return;
		}
		this.totalmsgnum = totalmsgnum;
	}

	public String getMessagenum() {
		if (messagenum.length() <= 0)
			return "0";

		return messagenum;
	}

	public void setMessagenum(String messagenum) {
		if (messagenum == null || messagenum.equals("")) {
			this.messagenum = "0";
			return;
		}
		this.messagenum = messagenum;
	}

	public String getCollectnum() {
		if (collectnum.length() <= 0)
			return "0";
		return collectnum;
	}

	public void setCollectnum(String collectnum) {
		if (collectnum == null || collectnum.equals("")) {
			this.collectnum = "0";
			return;
		}
		this.collectnum = collectnum;
	}

	public int getCoinnum() {
		return coinnum;
	}

	public void setCoinnum(int coinnum) {
		this.coinnum = coinnum;
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

	public int addCoinnum(int addCoinnum) {
		logger.d("******      addCoin: " + addCoinnum + "       ******");

		if (addCoinnum > 0) {
			this.coinnum += addCoinnum;
			// 发增加金币广播
			broadcastAddCoinEvent(addCoinnum);
		}
		return this.coinnum;
	}

	public int subCoinnum(int coinnum) {
		this.coinnum -= coinnum;
		if (this.coinnum < 0) {
			this.coinnum = 0;
		}
		return this.coinnum;
	}

	public void increaseCollectnum() {
		int num = StringUtil.parseInt(this.collectnum);
		num++;
		this.collectnum = String.valueOf(num);
	}

	public void decreaseCollectnum() {
		int num = StringUtil.parseInt(this.collectnum);
		num--;
		if (num < 0)
			num = 0;
		this.collectnum = String.valueOf(num);
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode,
			String msg) {
	}

	public void broadcastEvent() {
		Intent intent = new Intent(MineProfile.MINE_DYNAMIC_DATA_NOTIFICATION);
		intent.putExtra("gamenum", this.gamenum);
		intent.putExtra("totalmsgnum", this.totalmsgnum);
		intent.putExtra("unreadmsgnum", this.messagenum);
		intent.putExtra("collectnum", this.collectnum);
		intent.putExtra("coinnum", this.coinnum);

		BMApplication.getAppInstance().sendBroadcast(intent);
	}

	public void broadcastRefreshMsgEvent() {
		Intent intent = new Intent(MineProfile.MINE_DYNAMIC_DATA_REFRESH);
		BMApplication.getAppInstance().sendBroadcast(intent);
	}

	public static final String ADD_COIN_NUM_EXTRA = "addCoinnum";
	public static final String TOTAL_COIN_NUM_EXTRA = "coinnum";

	public void broadcastAddCoinEvent(int addCoinnum) {
//		Intent intent = new Intent(MineProfile.MINE_ADD_COIN_NOTIFICATION);
//		intent.putExtra(ADD_COIN_NUM_EXTRA, addCoinnum);
//		intent.putExtra(TOTAL_COIN_NUM_EXTRA, this.coinnum);
//
//		BMApplication.getAppInstance().sendBroadcast(intent);
	}

	public String getStrUserHead() {
		return strUserHead;
	}

	public void setStrUserHead(String strUserHead) {
		this.strUserHead = strUserHead;
	}
	
	
}
