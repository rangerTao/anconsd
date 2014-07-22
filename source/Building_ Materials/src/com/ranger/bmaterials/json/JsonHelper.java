package com.ranger.bmaterials.json;

import org.json.JSONException;

import android.util.Log;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.netresponse.BaseResult;

public final class JsonHelper {

	public static BaseResult parserWithTag(int requestTag, String resData) throws JSONException {
		BaseResult res = null;
		if (Constants.DEBUG)
			Log.i("JsonHelper", "[parserWithTag] request Tag: " + requestTag + " resData: " + resData);
		switch (requestTag) {
		case Constants.NET_TAG_USERNAME_REGISTER: {
			res = JSONParser.parseBMUserNameRegister(resData);
			break;
		}
		case Constants.NET_TAG_PHONENUM__REGISTER: {
			res = JSONParser.parsePhonenumRegister(resData);
			break;
		}
		case Constants.NET_TAG_USER_LOGIN: {
			res = JSONParser.parseUserLogin(resData);
			break;
		}
		case Constants.NET_TAG_GET_PHONE_VERIFYCODE: {
			res = JSONParser.parseBMPhoneVerifyCode(resData);
			break;
		}
		case Constants.NET_TAG_CHECK_USER_LOGIN: {
			res = JSONParser.parseCheckUserLogin(resData);
			break;
		}

		case Constants.NET_TAG_USER_UNLOGIN: {
			res = JSONParser.parseUserUnlogin(resData);
			break;
		}

		case Constants.NET_TAG_REGISTER_START_GAME: {
			res = JSONParser.parseStartGame(resData);
			break;
		}

		case Constants.NET_TAG_START_DOWNLOAD_GAME: {
			res = JSONParser.parseStartDownloadGame(resData);
			break;
		}

		case Constants.NET_TAG_CHANGE_PWD: {
			res = JSONParser.parseChangePwd(resData);
			break;
		}
		case Constants.NET_TAG_BIND_PHONE: {
			res = JSONParser.parseBindPhone(resData);
			break;
		}
		case Constants.NET_TAG_GET_COIN: {
			res = JSONParser.parseGetCoin(resData);
			break;
		}
		case Constants.NET_TAG_FORGET_PWD: {
			res = JSONParser.parseForgetPwd(resData);
			break;
		}

		case Constants.NET_TAG_HOME_PAGE_DATA:
			res = JSONParser.parseHomePageData(resData);
			break;

		case Constants.NET_TAG_SNAP_NUMBER_LIST: {
			res = JSONParser.parseSnapNumberList(resData);
			break;
		}
		case Constants.NET_TAG_SNAP_NUMBER: {
			res = JSONParser.parseSnapNumberAction(resData);
			break;
		}
		case Constants.NET_TAG_SNAP_NUMBER_DETAIL: {
			res = JSONParser.parseSnapNumberDetail(resData);
			break;
		}
		case Constants.NET_TAG_OPEN_SERVER_LIST: {
			res = JSONParser.parseOpenServerList(resData);
			break;
		}
		case Constants.NET_TAG_OPEN_SERVER_DETAIL: {
			res = JSONParser.parseOpenServerDetail(resData);
			break;
		}
		case Constants.NET_TAG_ACTIVITIES_LIST: {
			res = JSONParser.parseActivitis(resData);
			break;
		}
		case Constants.NET_TAG_APPIAISAL_LIST:
			res = JSONParser.parseAppiaisal(resData);
			break;
		case Constants.NET_TAG_ACTIVITY_DETAIL: {
			res = JSONParser.parseActivityDetail(resData);
			break;
		}
		case Constants.NET_TAG_APPIAISAL_DETAIL:
			res = JSONParser.parseAppiaisalDetail(resData);
			break;
		case Constants.NET_TAG_GET_MY_MESSAGE: {
			res = JSONParser.parseMyMessage(resData);
			break;
		}
		case Constants.NET_TAG_DEL_SETREAD_MESSAGE: {
			res = JSONParser.parseDeleteMessage(resData);
			break;
		}
		case Constants.NET_TAG_GET_MESSAGE_DETAIL: {
			res = JSONParser.parseMessageDetail(resData);
			break;
		}
		case Constants.NET_TAG_GET_COLLECTION_GAME: {
			res = JSONParser.parseCollectionGame(resData);
			break;
		}
		case Constants.NET_TAG_GET_COLLECTION_GUIDE: {
			res = JSONParser.parseCollectionGuide(resData);
			break;
		}
		case Constants.NET_TAG_GET_INSTALLED_GAME: {
			res = JSONParser.parseInstalledGame(resData);
			break;
		}

		case Constants.NET_TAG_GET_MY_DYNAMIC_DATA: {
			res = JSONParser.parseMyDynamicData(resData);
			break;
		}
		case Constants.NET_TAG_COLLECTION_ACTIONS: {
			res = JSONParser.parseCollectionActions(resData);
			break;
		}
		case Constants.NET_TAG_REGISTER_INSTALLED_GAME: {
			res = JSONParser.parseRegisterGame(resData);
			break;
		}
		case Constants.NET_TAG_CHECK_UPDATE: {
			res = JSONParser.parseCheckUpdate(resData);
			break;
		}
		case Constants.NET_TAG_FEEDBACK: {
			res = JSONParser.parseFeedback(resData);
			break;
		}
		case Constants.NET_TAG_CHANGE_NICKNAME: {
			res = JSONParser.parseChangeNickname(resData);
			break;
		}
		case Constants.NET_TAG_FAST_PHONE_REGESTER: {
			res = JSONParser.parseFastPhoneRegister(resData);
			break;
		}
		case Constants.NET_TAG_KEYWORDS: {
			res = JSONParser.parseBMKeywords(resData);
			break;
		}
		case Constants.NET_TAG_SEARCH: {
			res = JSONParser.parseBMSearchProducts(resData);
			break;
		}
		case Constants.NET_TAG_BAIDU_SAPI:
			res = JSONParser.parseBaiduSAPI(resData);
			break;
		case Constants.NET_TAG_GAME_RECOMMEND_DATA:
			res = JSONParser.parseGameRecommendData(resData);
			break;
		case Constants.NET_TAG_GAME_HOT_DATA:
			res = JSONParser.parseGameHotData(resData);
			break;
		case Constants.NET_TAG_GAME_NEW_DATA:
			res = JSONParser.parseGameNewData(resData);
			break;
		case Constants.NET_TAG_GAME_CLASS_DATA:
			res = JSONParser.parseGameClassData(resData);
			break;
		case Constants.NET_TAG_GAME_MORE_DATA:
			res = JSONParser.parseGameMoreData(resData);
			break;
		case Constants.NET_TAG_ONLINE_GAMES_AND_TYPES:
			res = JSONParser.parseOnlineGamesAndTypes(resData);
			break;
		case Constants.NET_TAG_SINGLE_CLASS_GAMES:
			res = JSONParser.parseSingleClassGames(resData);
			break;
		case Constants.NET_TAG_GAME_DETAIL_AND_SUMMARY:
			res = JSONParser.parseGameDetailAndSummary(resData);
			break;
		case Constants.NET_TAG_GAME_DETAIL_GUIDE:
			res = JSONParser.parseGameDetailGuide(resData);
			break;
		case Constants.NET_TAG_GAME_DETAIL_COMMENT:
			res = JSONParser.parseGameDetailComment(resData);
			break;
		case Constants.NET_TAG_GAME_DETAIL_GUIDE_DETAIL:
			res = JSONParser.parseGameGuideDetail(resData);
			break;
		case Constants.NET_TAG_PUBLISH_COMMENT_STAR:
			res = JSONParser.parsePublishCommentStar(resData);
			break;

		case Constants.NET_TAG_WHITE_LIST: {
			res = JSONParser.parseWhiteList(resData);
			break;
		}
		case Constants.NET_TAG_UPDATE_GAMES: {
			res = JSONParser.parseUpdateGames(resData);
			break;
		}
		case Constants.NET_TAG_GET_DOWNLOADED_GAMES: {
			res = JSONParser.parseGetDownloadedGames(resData);
			break;
		}
		case Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES: {
			res = JSONParser.parseUploadDownloadedGames(resData);
			break;
		}

		case Constants.NET_TAG_GET_GAME_LOGIN_TOKEN: {
			res = JSONParser.parseLoginToken(resData);
			break;
		}
		case Constants.NET_TAG_SPEEDDOWNLOAD_INFOS: {
			res = JSONParser.parseSpeedDownload(resData);
			break;
		}

		case Constants.NET_TAG_COMPETITION:
			res = JSONParser.parseCompetition(resData);
			break;
		case Constants.NET_TAG_GET_MUST_PLAY_GAMES: {
			res = JSONParser.parseMustPlayGames(resData);
			break;
		}

		case Constants.NET_TAG_GET_EXCHANGE_HISTORY_DETAIL:
			res = JSONParser.parseExchangeHistoryDetail(resData);
			break;

		case Constants.NET_TAG_HOME_MORE:
			res = JSONParser.parseHomeMoreData(resData);
			break;

		/**
		 * 解析tag 1100，获取专题列表
		 * 
		 * @author liushuohui
		 */
		case Constants.NET_TAG_GAME_TOPICS: {
			res = JSONParser.parseGameTopicsData(resData);
		}

			break;

		/**
		 * 解析tag 1101，获取专题详情
		 * 
		 * @author liushuohui
		 */
		case Constants.NET_TAG_GAME_TOPIC_DETAIL: {
			res = JSONParser.parseGameTopicDetailData(resData);
		}

			break;

		/**
		 * 解析tag 1102，获取专题详情更多游戏列表
		 * 
		 * @author liushuohui
		 */
		case Constants.NET_TAG_GAME_TOPIC_DETAIL_MORE_LIST: {
			res = JSONParser.parseGameTopicDetailMoreListData(resData);
		}

			break;

		case Constants.NET_TAG_SPLASH_AD:
			res = JSONParser.parseSplashAdData(resData);
			break;

		case Constants.NET_TAG_GET_RECOMMEND_APP:
			res = JSONParser.parseRecommendAppData(resData);
			break;
		case Constants.NET_TAG_GET_SMSCENTERS:
			res = JSONParser.parseSmscenters(resData);
			break;
		case Constants.NET_TAG_HOME_RECOMMEND:
			res = JSONParser.parseHomeRecData(resData);
			break;
		case Constants.NET_TAG_HOME_START_DIALOG:
			res = JSONParser.parseHomeDailyData(resData);
			break;
		case Constants.NET_TAG_GET_RECOM_KEYWORDS:
			res = JSONParser.parseRecomGameAndKeyword(resData);
			break;
		case Constants.NET_TAG_GET_RELATED_GAMEINFO:
			res = JSONParser.parseGameRelatedInfo(resData);
			break;
		default:
			break;
		}

		return res;
	}
}
