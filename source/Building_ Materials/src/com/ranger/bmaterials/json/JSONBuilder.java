package com.ranger.bmaterials.json;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.PhoneHelper;
/**
 * 
 * @author wenzutong
 * 
 */
public final class JSONBuilder {

	private static JSONObject createJsonObject() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		Context context = GameTingApplication.getAppInstance().getApplicationContext();

		try {
			jsonObject.put(Constants.JSON_VERSION, PhoneHelper.getAppVersionName());
			jsonObject.put(Constants.JSON_UA, Build.MODEL);
			jsonObject.put(Constants.JSON_OS, Build.VERSION.RELEASE);
			jsonObject.put(Constants.JSON_IMEI, PhoneHelper.getIMEI());
			jsonObject.put(Constants.JSON_UDID, PhoneHelper.getUdid());

			jsonObject.put(Constants.JSON_CHANNEL, PhoneHelper.getChannelData(context.getString(R.string.channel_name)));

			DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
			jsonObject.put(Constants.JSON_SCREENH, String.valueOf(dm.heightPixels));
			jsonObject.put(Constants.JSON_SCREENW, String.valueOf(dm.widthPixels));

			jsonObject.put(Constants.JSON_PUSH_CHANNELID, MineProfile.getInstance().getPush_channelid());
			jsonObject.put(Constants.JSON_PUSH_USERID, MineProfile.getInstance().getPush_userid());

		} catch (Exception e) {
			e.printStackTrace();
		}

		ConnectManager connectManager = new ConnectManager(context);
		String connectString = connectManager.getConnectionString(context);
		jsonObject.put(Constants.JSON_CONNECT_TYPE, connectString);

		return jsonObject;
	}

	public String buildGetGameDetailString(String gameid, String packageName, int versionCode, String gamename, String versionName) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			// jsonObj.put(StringConstants.JSON_TAG,
			// String.valueOf(StringConstants.NET_TAG_GET_GAME_DETAIL));
			// jsonObj.put(StringConstants.JSON_GAMEID, gameid);
			// jsonObj.put(StringConstants.JSON_PACKAGENAME, packageName);
			// jsonObj.put(StringConstants.JSON_VERSIONCODE,
			// String.valueOf(versionCode));
			// jsonObj.put(StringConstants.JSON_GAMENAME, gamename);
			// jsonObj.put(StringConstants.JSONG_VERSIONNAME, versionName);
			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildActiveString() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_APP_ACTIVE));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildCheckUpdateString() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_CHECK_UPDATE));
			// jsonObj.put(Constants.JSON_APP_VERSION, appversion);
			// jsonObj.put(Constants.JSON_APP_VERSION_CODE, appversioncode);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildFeedbackString(String userid, String sessionid, String content, String contact) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_FEEDBACK));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_CONTENT, content);
			jsonObj.put(Constants.JSON_CONTACT, contact);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildUserNameRegisterString(String username, String password, String nickname) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_USERNAME_REGISTER));
			jsonObj.put(Constants.JSON_USERNAME, username);
			jsonObj.put(Constants.JSON_PASSWORD, password);
			jsonObj.put(Constants.JSON_NICKNAME, nickname);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildChangeNicknameString(String userid, String sessionid, String nickname) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_CHANGE_NICKNAME));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_NICKNAME, nickname);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildFastPhoneRegisterString(String message) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_FAST_PHONE_REGESTER));
			jsonObj.put(Constants.JSON_MESSAGE, message);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildPhoneNumRegisterString(String username, String password, String nickname, String verifyCode) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_PHONENUM__REGISTER));
			jsonObj.put(Constants.JSON_USERNAME, username);
			jsonObj.put(Constants.JSON_PASSWORD, password);
			jsonObj.put(Constants.JSON_NICKNAME, nickname);
			jsonObj.put(Constants.JSON_VERIFYCODE, verifyCode);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGetPhoneVerifyCodeString(String phonenum, int flag) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_PHONE_VERIFYCODE));
			jsonObj.put(Constants.JSON_PHONENUM, phonenum);
			jsonObj.put(Constants.JSON_FLAG, flag);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildUserLoginString(String username, String password) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_USER_LOGIN));
			jsonObj.put(Constants.JSON_USERNAME, username);
			jsonObj.put(Constants.JSON_PASSWORD, password);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildCheckUserLoginString(String userid, String sessionid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_CHECK_USER_LOGIN));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildUserUnloginString(String userid, String sessionid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_USER_UNLOGIN));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildChangePwdString(String oldpwd, String newpwd, String userid, String sessionid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_CHANGE_PWD));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_NEWPASSWORD, newpwd);
			jsonObj.put(Constants.JSON_OLDPASSWORD, oldpwd);
			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildBindPhoneString(String phonenum, String verifyCode, int requestType, String userid, String sessionid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_BIND_PHONE));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_PHONENUM, phonenum);
			jsonObj.put(Constants.JSON_VERIFYCODE, verifyCode);
			jsonObj.put(Constants.JSON_REQUESTTYPE, requestType);
			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGetCoinString(String userid, String sessionid, int coinnum, int requestType, String gameid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_COIN));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_COINNUM, coinnum);
			jsonObj.put(Constants.JSON_COINTYPE, requestType);
			jsonObj.put(Constants.JSON_GAME_ID, gameid);
			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildForgetPwd(String username) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_FORGET_PWD));
			jsonObj.put(Constants.JSON_USERNAME, username);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameRecommendRequestBody() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_RECOMMEND_DATA));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameHotRequestBody() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_HOT_DATA));

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameNewRequestBody() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_NEW_DATA));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameClassRequestBody() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_CLASS_DATA));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	/** liushuohui **/
	public String buildGameTopicRequestBody(int page, int pagecount) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();

			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_TOPICS));
			jsonObj.put(Constants.JSON_PAGE_ID, page);
			jsonObj.put(Constants.JSON_TOPIC_PAGE_COUNT, pagecount);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	/** liushuohui **/
	public String buildGameTopicDetailRequestBody(String id, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();

			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_TOPIC_DETAIL));
			jsonObj.put(Constants.JSON_TOPIC_ID, id);
			jsonObj.put(Constants.JSON_TOPIC_PAGE_COUNT, count);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	/** liushuohui tag 1102 **/
	public String buildGameTopicDetailMoreListBody(String id, int page, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();

			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_TOPIC_DETAIL_MORE_LIST));
			jsonObj.put(Constants.JSON_TOPIC_ID, id);
			jsonObj.put(Constants.JSON_TOPIC_PAGE_COUNT, count);
			jsonObj.put(Constants.JSON_PAGEINDEX, page);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String builderGameMoreRequestBody(String moretype, int page, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_MORE_DATA));

			jsonObj.put("moretype", moretype);
			jsonObj.put("page", String.valueOf(page));
			jsonObj.put("datacount", String.valueOf(count));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	public String builderOnlineGamesAndTypesRequestBody(int page, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_ONLINE_GAMES_AND_TYPES));

			jsonObj.put("page", String.valueOf(page));
			jsonObj.put("datacount", String.valueOf(count));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	public String builderSingleClassGamesRequestBody(String gametype, String gametypenumber, int page, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SINGLE_CLASS_GAMES));

			jsonObj.put("gametype", gametype);
			jsonObj.put("gametypenumber", gametypenumber);
			jsonObj.put("page", String.valueOf(page));
			jsonObj.put("datacount", String.valueOf(count));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	public String buildGameDetailAndSummaryRequestBody(String userid, String session, String gameid, String pkgname, String versionCode, String versionName) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_DETAIL_AND_SUMMARY));

			jsonObj.put("userid", userid);
			jsonObj.put("sessionid", session);
			jsonObj.put("gameid", gameid);
			jsonObj.put("pkgname", pkgname);
			jsonObj.put("versioncode", versionCode);
			jsonObj.put("versionname", versionName);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameDetailGuideRequestBody(String gameid, String userid, String sessionid, String pkgname, String versionname, String versioncode, int page, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_DETAIL_GUIDE));

			jsonObj.put("userid", userid);
			jsonObj.put("sessionid", sessionid);
			jsonObj.put("gameid", gameid);
			jsonObj.put("page", String.valueOf(page));
			jsonObj.put("datacount", String.valueOf(count));
			jsonObj.put("pkgname", pkgname);
			jsonObj.put("versionname", versionname);
			jsonObj.put("versioncode", versioncode);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameGuideDetailRequestBody(String userid, String sessionid, String guideid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_DETAIL_GUIDE_DETAIL));

			jsonObj.put("userid", userid);
			jsonObj.put("sessionid", sessionid);
			jsonObj.put("guideid", guideid);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameDetailCommentRequestBody(String gameid, String pkgname, String versionname, String versioncode, int page, int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GAME_DETAIL_COMMENT));

			jsonObj.put("gameid", gameid);
			jsonObj.put("page", String.valueOf(page));
			jsonObj.put("datacount", String.valueOf(count));
			jsonObj.put("pkgname", pkgname);
			jsonObj.put("versionname", versionname);
			jsonObj.put("versioncode", versioncode);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildPublishCommentStarRequestBody(String gameid, String userid, String sessionid, String cmtcontent, float star) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_PUBLISH_COMMENT_STAR));

			jsonObj.put("gameid", gameid);
			jsonObj.put("userid", userid);
			jsonObj.put("sessionid", sessionid);
			jsonObj.put("cmtcontent", cmtcontent);
			jsonObj.put("star", String.valueOf(star));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String buildMyMessage(String userid, String sessionid, int msgtype, int pageindex, int pagenum) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_MY_MESSAGE));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_MSGTYPE, msgtype);
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);
			jsonObj.put(Constants.JSON_PAGENUM, pagenum);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildGameCollection(String userid, String sessionid, int pageindex, int pagenum) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_COLLECTION_GAME));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);
			jsonObj.put(Constants.JSON_PAGENUM, pagenum);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildGuideCollection(String userid, String sessionid, int pageindex, int pagenum) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_COLLECTION_GUIDE));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);
			jsonObj.put(Constants.JSON_PAGENUM, pagenum);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildDynamicData(String userid, String sessionid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_MY_DYNAMIC_DATA));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildInstalledGames(String userid, String sessionid, int pageindex, int pagenum) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_INSTALLED_GAME));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);
			jsonObj.put(Constants.JSON_PAGENUM, pagenum);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildRegisterGames(String userid, String sessionid, List<String> gameids) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_REGISTER_INSTALLED_GAME));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			JSONArray jsonArray = new JSONArray();
			for (String string : gameids) {
				jsonArray.put(string);
			}
			jsonObj.put(Constants.JSON_GAMEIDS, jsonArray);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildRegisterStartGame(String gameid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_REGISTER_START_GAME));
			jsonObj.put(Constants.JSON_GAMEID, gameid);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildStartDownloadGame(String gameid, String gamename) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_START_DOWNLOAD_GAME));
			jsonObj.put(Constants.JSON_GAMEID, gameid);
			jsonObj.put(Constants.JSON_GAMENAME, gamename);
			jsonObj.put(Constants.JSON_DOWNLOADSTATUS, "0");
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildFinishDownloadGame(String gameid, String gamename) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_START_DOWNLOAD_GAME));
			jsonObj.put(Constants.JSON_GAMEID, gameid);
			jsonObj.put(Constants.JSON_GAMENAME, gamename);
			jsonObj.put(Constants.JSON_DOWNLOADSTATUS, "1");
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildCollectionAction(String userid, String sessionid, int msgtype, int msgsubtype, String targetid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_COLLECTION_ACTIONS));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_TARGETID, targetid);
			jsonObj.put(Constants.JSON_MSGTYPE, msgtype);
			jsonObj.put(Constants.JSON_MSGSUBTYPE, msgsubtype);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 抢号列表 tag = 210
	 * 
	 * @param
     *
     * * @return
	 */
	public String buildSnapNumberList(String userId, String sessionId, int page, int pageSize) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SNAP_NUMBER_LIST));
			if (userId != null) {
				jsonObj.put(Constants.JSON_USERID, userId);
			} else {
				jsonObj.put(Constants.JSON_USERID, "");
			}
			if (sessionId != null) {
				jsonObj.put(Constants.JSON_SESSIONID, sessionId);
			} else {
				jsonObj.put(Constants.JSON_SESSIONID, "");
			}
			jsonObj.put(Constants.JSON_PAGE, String.valueOf(page));
			jsonObj.put(Constants.JSON_DATACOUNT, String.valueOf(pageSize));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildSnapNumberDetail(String userId, String sessionId, String gameId, String grabId) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SNAP_NUMBER_DETAIL));

			if (userId != null) {
				jsonObj.put(Constants.JSON_USERID, userId);
			} else {
				jsonObj.put(Constants.JSON_USERID, "");
			}
			if (sessionId != null) {
				jsonObj.put(Constants.JSON_SESSIONID, sessionId);
			} else {
				jsonObj.put(Constants.JSON_SESSIONID, "");
			}
			if (gameId != null) {
				jsonObj.put(Constants.JSON_GAME_ID, gameId);
			} else {
				jsonObj.put(Constants.JSON_GAME_ID, "");
			}
			jsonObj.put(Constants.JSON_GRAB_ID, grabId);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildSnapNumber(String userId, String sessionId, String gameId, String grabId) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SNAP_NUMBER));
			if (userId != null)
				jsonObj.put(Constants.JSON_USERID, userId);
			if (sessionId != null)
				jsonObj.put(Constants.JSON_SESSIONID, sessionId);
			jsonObj.put(Constants.JSON_GAME_ID, gameId);
			jsonObj.put(Constants.JSON_GRAB_ID, grabId);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildOpenServerList(int page, int pageSize) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_OPEN_SERVER_LIST));
			jsonObj.put(Constants.JSON_PAGE, String.valueOf(page));
			jsonObj.put(Constants.JSON_DATACOUNT, String.valueOf(pageSize));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildOpenServerDetail(String gameId, String openServerId) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_OPEN_SERVER_DETAIL));
			if (gameId != null) {
				jsonObj.put(Constants.JSON_GAME_ID, gameId);
			} else {
				jsonObj.put(Constants.JSON_GAME_ID, "");
			}
			jsonObj.put(Constants.JSON_OPEN_SERVER_ID, openServerId);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildActivitiesList(int page, int pageSize) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_ACTIVITIES_LIST));
			jsonObj.put(Constants.JSON_PAGE, String.valueOf(page));
			jsonObj.put(Constants.JSON_DATACOUNT, String.valueOf(pageSize));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	public String buildAppiaisalList(int page, int pageSize,int type) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_APPIAISAL_LIST));
			jsonObj.put(Constants.JSON_PAGE, String.valueOf(page));
			jsonObj.put(Constants.JSON_DATACOUNT, String.valueOf(pageSize));
			jsonObj.put(Constants.JSON_APPRAISAL_TYPE, String.valueOf(type));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	public String buildActivityDetail(String gameId, String activityId) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_ACTIVITY_DETAIL));
			if (gameId != null) {
				jsonObj.put(Constants.JSON_GAME_ID, gameId);
			} else {
				jsonObj.put(Constants.JSON_GAME_ID, "");
			}
			jsonObj.put(Constants.JSON_ACTIVITY_ID, activityId);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}
	public String buildAppiaisalDetail(String infosid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_APPIAISAL_DETAIL));
			jsonObj.put(Constants.JSON_APPRAISAL_ID, infosid);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	public String buildKeywords(int count) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_KEYWORDS));
			jsonObj.put(Constants.JSON_KEYWORDS_COUNT, String.valueOf(count));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildSearch(String keyword, String page, String pageSize) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SEARCH));
			jsonObj.put(Constants.JSON_KEYWORD, keyword);
			jsonObj.put(Constants.JSON_PAGE, page);
			jsonObj.put(Constants.JSON_DATACOUNT, pageSize);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildWhiteList(List<String> packages) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_WHITE_LIST));

			int size = packages.size();
			JSONArray ja = new JSONArray();
			for (int i = 0; i < size; i++) {
				String p = packages.get(i);
				// Just for test
				/*
				 * if ("com.mas.wawagame.BDDKlord".equals(p)) {
				 * Log.e("wangliangtest",
				 * "buildWhiteList Has com.mas.wawagame.BDDKlord"); } else if
				 * ("com.gamebox.kingbaiduy".equals(p)) { Log.e("wangliangtest",
				 * "buildWhiteList Has com.gamebox.kingbaiduy"); }
				 */
				ja.put(p);
			}
			jsonObj.put(Constants.JSON_PACKAGES, ja);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildGetDownloadedGames(String deviceId) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_DOWNLOADED_GAMES));
			// jsonObj.put(Constants.JSON_UDID, deviceId);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildUploadDownloadedGames(String deviceId, List<String> gameIds) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES));
			// jsonObj.put(Constants.JSON_UDID, deviceId);
			JSONArray jsonArray = new JSONArray();
			for (String string : gameIds) {
				jsonArray.put(string);
				if (Constants.DEBUG)
					Log.i("wangliangtest", "[JSONBuilder#buildUploadDownloadedGames]gameId:" + string);
			}
			jsonObj.put(Constants.JSON_GAMEIDS, jsonArray);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildGetGameLoginToken(String userid, String session_id, String gameid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_GAME_LOGIN_TOKEN));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, session_id);
			jsonObj.put(Constants.JSON_GAMEID, gameid);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 高速下载
	 * 
	 * @param gameids
	 * @return
	 */
	public String buildSpeedDownloadGameids(String gameids) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SPEEDDOWNLOAD_INFOS));
			jsonObj.put("gameids", gameids);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Tag 804
	 * 
	 * @param userid
	 * @param sessionid
	 * @param pageindex
	 * @return
	 */
	public String buildGetExchangeHistoryDetailString(String userid, String sessionid, int pageindex) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_EXCHANGE_HISTORY_DETAIL));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);
			jsonObj.put(Constants.JSON_PAGENUM, 15);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * tag=805 争霸赛
	 */
	public String buildCompetition(int pageindex, int pagenum) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_COMPETITION));
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);
			jsonObj.put(Constants.JSON_PAGENUM, pagenum);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * tag=806 必玩
	 */
	public String buildMustPlayGamesString() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_MUST_PLAY_GAMES));

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;

	}

	// 首页 tag=204
	public String buildHomePageRequestBody() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_HOME_PAGE_DATA));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * tag=205 首页加载更多
	 */
	public String buildHomeMore(int pageindex) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_HOME_MORE));
			jsonObj.put(Constants.JSON_PAGEINDEX, pageindex);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * tag=1200 获取推荐应用
	 */
	public String buildRecommendApp() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_RECOMMEND_APP));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * tag=1201 获取短信中心号码
	 */
	public String buildSmscentersReq() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_SMSCENTERS));
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 启动页tag=1300
	 */
	public String buildSplashAd() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_SPLASH_AD));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * 首页推荐tag=1400
	 */
	public String buildHomeRecommend(List<String> gameIds) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_HOME_RECOMMEND));
			JSONArray jsonArray = new JSONArray();
			for (String string : gameIds) {
				jsonArray.put(string);
			}
			jsonObj.put(Constants.JSON_GAMEIDS, jsonArray);
			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * 每日弹窗tag=1500
	 */
	public String buildHomeStartDialog() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_HOME_START_DIALOG));

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * TAG 112
	 * 
	 * @param username
	 * @param bduid
	 * @return
	 */
	public String buildBaiduSAPI(String username, String bduid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_BAIDU_SAPI));
			jsonObj.put(Constants.JSON_USERNAME, username);
			jsonObj.put("bduid", bduid);

			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Tag 243
	 * 
	 * @param key
	 * @return
	 */
	public String buildRecomGameAndKeywords(String key) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_RECOM_KEYWORDS));
			jsonObj.put(Constants.JSON_KEYWORD, key);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Tag 261
	 * 
	 * @param pkgname
	 * @return
	 */
	public String buildRelatedGameInfo(String pkgname) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_GET_RELATED_GAMEINFO));
			jsonObj.put(Constants.JSON_PKGNAME, pkgname);

			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}
}
