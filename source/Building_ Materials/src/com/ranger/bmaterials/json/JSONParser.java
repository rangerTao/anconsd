package com.ranger.bmaterials.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.mode.ADInfo;
import com.ranger.bmaterials.mode.ActivityDetail;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.mode.ActivityInfoList;
import com.ranger.bmaterials.mode.AppiaisalDetail;
import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.mode.CompetitionResult;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.GameRelatedInfo;
import com.ranger.bmaterials.mode.HomeAppGridInfo;
import com.ranger.bmaterials.mode.HomeAppListInfoArray;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.mode.MyDownloadedGame;
import com.ranger.bmaterials.mode.MyDownloadedGames;
import com.ranger.bmaterials.mode.OpenServer;
import com.ranger.bmaterials.mode.OpenServerDetail;
import com.ranger.bmaterials.mode.OpenServerList;
import com.ranger.bmaterials.mode.RecommendAppItemInfo;
import com.ranger.bmaterials.mode.SnapNumber;
import com.ranger.bmaterials.mode.SnapNumberDetail;
import com.ranger.bmaterials.mode.SnapNumberList;
import com.ranger.bmaterials.mode.SnappedNumber;
import com.ranger.bmaterials.mode.SpeedDownLoadInfo;
import com.ranger.bmaterials.mode.UpdatableItem;
import com.ranger.bmaterials.mode.UpdatableList;
import com.ranger.bmaterials.mode.WhiteList;
import com.ranger.bmaterials.mode.ActivityDetail.ActivityItem;
import com.ranger.bmaterials.mode.CompetitionResult.CompetitionInfo;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListBannerInfo;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListItemInfo;
import com.ranger.bmaterials.mode.OpenServerDetail.OpenServerItem;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.mode.SnapNumber.SnapNumberStatus;
import com.ranger.bmaterials.mode.SnapNumberDetail.SnapNumberItem;
import com.ranger.bmaterials.netresponse.BMProductInfoResult;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.netresponse.BMSearchResult;
import com.ranger.bmaterials.netresponse.BMUserLoginResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.BindPhoneResult;
import com.ranger.bmaterials.netresponse.CheckUpdateResult;
import com.ranger.bmaterials.netresponse.CollectionActionResult;
import com.ranger.bmaterials.netresponse.DynamicDataResult;
import com.ranger.bmaterials.netresponse.ExchangeHistoryDetailResult;
import com.ranger.bmaterials.netresponse.ForgetPasswordResult;
import com.ranger.bmaterials.netresponse.GameClassDataResult;
import com.ranger.bmaterials.netresponse.GameDetailCommentResult;
import com.ranger.bmaterials.netresponse.GameDetailGuideResult;
import com.ranger.bmaterials.netresponse.GameDetailSummaryResult;
import com.ranger.bmaterials.netresponse.GameGuideDetailResult;
import com.ranger.bmaterials.netresponse.GameHotDataResult;
import com.ranger.bmaterials.netresponse.GameLoginTokenResult;
import com.ranger.bmaterials.netresponse.GameMoreDataResult;
import com.ranger.bmaterials.netresponse.GameNewDataResult;
import com.ranger.bmaterials.netresponse.GameRecommendDataResult;
import com.ranger.bmaterials.netresponse.GameRelatedResult;
import com.ranger.bmaterials.netresponse.HomeAppResult;
import com.ranger.bmaterials.netresponse.HomeDailyResult;
import com.ranger.bmaterials.netresponse.HomePageDataResult;
import com.ranger.bmaterials.netresponse.HomeRecResult;
import com.ranger.bmaterials.netresponse.MineGamesResult;
import com.ranger.bmaterials.netresponse.MineGuidesResult;
import com.ranger.bmaterials.netresponse.MineMsgDetailResult;
import com.ranger.bmaterials.netresponse.MineMsgResult;
import com.ranger.bmaterials.netresponse.MustPlayGames;
import com.ranger.bmaterials.netresponse.OnlineGamesAndTypesResult;
import com.ranger.bmaterials.netresponse.PublishCommentStarResult;
import com.ranger.bmaterials.netresponse.RecommandKeyword;
import com.ranger.bmaterials.netresponse.RecommendAppResult;
import com.ranger.bmaterials.netresponse.SingleClassGamesResult;
import com.ranger.bmaterials.netresponse.SmscentersResult;
import com.ranger.bmaterials.netresponse.SpeedDownloadResult;
import com.ranger.bmaterials.netresponse.SplashAdResult;
import com.ranger.bmaterials.netresponse.UserLoginResult;
import com.ranger.bmaterials.netresponse.UserNameRegisterResult;
import com.ranger.bmaterials.netresponse.ExchangeHistoryDetailResult.ExchangeItem;
import com.ranger.bmaterials.netresponse.HomeRecResult.Infos;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.DateUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.ui.MineGameItemInfo;
import com.ranger.bmaterials.ui.MineGuideItemInfo;
import com.ranger.bmaterials.ui.MineMsgItemInfo;
import com.ranger.bmaterials.ui.gametopic.GameTopicsData;
import com.ranger.bmaterials.ui.topicdetail.TopicDetailData;
import com.ranger.bmaterials.ui.topicdetail.TopicDetailMoreGamesData;

/**
 * 
 * @author wenzutong
 * 
 */
public class JSONParser {

	// 用户注册
	public static BaseResult parseBMUserNameRegister(String resData) {

		UserNameRegisterResult result = new UserNameRegisterResult();

		do {
			try {

				JSONObject jsonObj = new JSONObject(resData);

				String tag = jsonObj.getString(Constants.JSON_TAG);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				String username = jsonObj.getString(Constants.JSON_USERNAME);
				String userid = jsonObj.getString(Constants.JSON_USERID);
				int registertype = jsonObj.getInt(Constants.JSON_REGISTERTYPE);
				String sessionid = jsonObj.getString(Constants.JSON_SESSIONID);
				String nickname = jsonObj.getString(Constants.JSON_NICKNAME);

				result.setUsername(username);
				result.setUserid(userid);
				result.setRegistertype(registertype);
				result.setSessionid(sessionid);
				result.setNickname(nickname);

			} catch (Exception e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);

		return result;

	}

	// 手机号注册
	public static BaseResult parsePhonenumRegister(String resData) {
		UserNameRegisterResult result = new UserNameRegisterResult();

		do {
			try {

				JSONObject jsonObj = new JSONObject(resData);

				String tag = jsonObj.getString(Constants.JSON_TAG);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				String username = jsonObj.getString(Constants.JSON_USERNAME);
				String userid = jsonObj.getString(Constants.JSON_USERID);
				int registertype = jsonObj.getInt(Constants.JSON_REGISTERTYPE);
				String sessionid = jsonObj.getString(Constants.JSON_SESSIONID);
				String nickname = jsonObj.getString(Constants.JSON_NICKNAME);
				int coinnum = jsonObj.getInt(Constants.JSON_COINNUM);

				result.setUsername(username);
				result.setUserid(userid);
				result.setRegistertype(registertype);
				result.setSessionid(sessionid);
				result.setNickname(nickname);
				result.setCoinnum(coinnum);

			} catch (Exception e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);

		return result;
	}

	// 用户登录
	public static BaseResult parseUserLogin(String resData) {
		UserLoginResult result = new UserLoginResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				String username = jsonObj.getString(Constants.JSON_USERNAME);
				result.setUsername(username);

				String userid = jsonObj.getString(Constants.JSON_USERID);
				result.setUserid(userid);

				int registertype = jsonObj.getInt(Constants.JSON_REGISTERTYPE);
				result.setRegistertype(registertype);

				if (registertype == MineProfile.USERTYPE_BINGDINGPHONE) {
					String phonenum = jsonObj.getString(Constants.JSON_PHONENUM);
					result.setPhonenum(phonenum);
				} else if (registertype == MineProfile.USERTYPE_PHONEUSER) {
					String phonenum = jsonObj.getString(Constants.JSON_PHONENUM);

					if (phonenum.length() > 0) {
						result.setPhonenum(phonenum);
						result.setUsername(phonenum);
					} else {
						result.setPhonenum(username);
					}
				}

				String sessionid = jsonObj.getString(Constants.JSON_SESSIONID);
				result.setSessionid(sessionid);

				result.setGamenum(jsonObj.getString(Constants.JSON_GAMENUM));

				result.setTotalmsgnum(jsonObj.getString(Constants.JSON_TOTALMSGNUM));

				result.setMessagenum(jsonObj.getString(Constants.JSON_MESSAGENUM));

				result.setCollectnum(jsonObj.getString(Constants.JSON_COLLECTNUM));

				result.setCoinnum(jsonObj.getInt(Constants.JSON_COINNUM));

				String nickname = jsonObj.getString(Constants.JSON_NICKNAME);
				result.setNickname(nickname);
				if (jsonObj.has(Constants.JSON_ISLOGINREQ)) {
					result.setIsloginReq(jsonObj.getInt(Constants.JSON_ISLOGINREQ));
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// baidu pass 登录
	public static BaseResult parseBaiduSAPI(String resData) {
		return parseUserLogin(resData);
	}

	// 得到手机验证码
	public static BaseResult parseBMPhoneVerifyCode(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 用户注销登录
	public static BaseResult parseUserUnlogin(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 启动联运游戏
	public static BaseResult parseStartGame(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 统计开始下载游戏
	public static BaseResult parseStartDownloadGame(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 用户登录
	public static BaseResult parseCheckUserLogin(String resData) {
		UserLoginResult result = new UserLoginResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 修改密码
	public static BaseResult parseChangePwd(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 绑定手机
	public static BaseResult parseBindPhone(String resData) {
		BindPhoneResult result = new BindPhoneResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);
				boolean hasCoinNum = jsonObj.has(Constants.JSON_COINNUM);
				if (hasCoinNum) {
					int coinnum = jsonObj.getInt(Constants.JSON_COINNUM);
					result.setCoinnum(coinnum);
				}
				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 获取金币
	public static BaseResult parseGetCoin(String resData) {
		BindPhoneResult result = new BindPhoneResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);
				int coinnum = jsonObj.getInt(Constants.JSON_COINNUM);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);
				result.setCoinnum(coinnum);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 忘记密码
	public static BaseResult parseForgetPwd(String resData) {
		ForgetPasswordResult result = new ForgetPasswordResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				int flag = jsonObj.getInt(Constants.JSON_FLAG);
				result.setFlag(flag);

				if (flag == 2) {
					String servicenum = jsonObj.getString(Constants.JSON_SERVICENUM);
					result.setServicenum(servicenum);
				} else {
					String phonenum = jsonObj.getString(Constants.JSON_PHONENUM);
					result.setPhonenum(phonenum);
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 游戏-推荐-数据
	public static BaseResult parseGameRecommendData(String resData) {
		GameRecommendDataResult result = new GameRecommendDataResult();
		result.parse(resData);
		return result;
	}

	// 游戏-排行-数据
	public static BaseResult parseGameHotData(String resData) {
		GameHotDataResult result = new GameHotDataResult();
		result.parse(resData);
		return result;
	}

	// 游戏-最新-数据
	public static BaseResult parseGameNewData(String resData) {
		GameNewDataResult result = new GameNewDataResult();
		result.parse(resData);
		return result;
	}

	// 游戏-分类-数据
	public static BaseResult parseGameClassData(String resData) {
		// resData =
		// "{\"tag\":\"223\",\"errorcode\":\"0\",\"errormsg\":\"\",\"gametypes\":[{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]},{\"label\":\"test1\",\"gametypeicon\":\"http://www.iyi8.com/uploadfile/2014/0422/20140422123735434.jpg\",\"gametype\":\"1\",\"gametypenumber\":\"1\",\"gametypename\":\"cate1\",\"totalcount\":\"10000\",\"gamelist\":[{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"},{\"gameid\":\"1234\",\"gamename\":\"test1\",\"pkgname\":\"com.baidu.test\"}]}]}";
		GameClassDataResult result = new GameClassDataResult();
		result.parse(resData);
		return result;
	}

	// 游戏-更多
	public static BaseResult parseGameMoreData(String resData) {
		GameMoreDataResult result = new GameMoreDataResult();
		result.parse(resData);
		return result;
	}

	// 获取全部网游列表及网游分类

	public static BaseResult parseOnlineGamesAndTypes(String resData) {
		OnlineGamesAndTypesResult result = new OnlineGamesAndTypesResult();
		result.parse(resData);
		return result;
	}

	public static BaseResult parseSingleClassGames(String resData) {
		SingleClassGamesResult result = new SingleClassGamesResult();
		result.parse(resData);
		return result;
	}

	public static BaseResult parseGameDetailAndSummary(String resData) {
		GameDetailSummaryResult result = new GameDetailSummaryResult();
		result.parse(resData);
		return result;
	}

	public static BaseResult parseGameDetailGuide(String resData) {
		GameDetailGuideResult result = new GameDetailGuideResult();
		result.parse(resData);
		return result;
	}

	public static BaseResult parseGameDetailComment(String resData) {
		GameDetailCommentResult result = new GameDetailCommentResult();
		result.parse(resData);
		return result;
	}

	public static BaseResult parseGameGuideDetail(String resData) {
		GameGuideDetailResult result = new GameGuideDetailResult();
		result.parse(resData);
		return result;
	}

	public static BaseResult parsePublishCommentStar(String resData) {
		PublishCommentStarResult result = new PublishCommentStarResult();
		result.parse(resData);
		return result;
	}

	// 得到我的消息
	public static BaseResult parseMyMessage(String resData) {
		MineMsgResult result = new MineMsgResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				String totalnum = jsonObj.getString(Constants.JSON_TOTALCOUNT);
				result.totalcount = StringUtil.parseInt(totalnum);

				JSONArray msglist = jsonObj.getJSONArray(Constants.JSON_MSGLIST);

				for (int i = 0; i < msglist.length(); i++) {
					JSONObject object = msglist.getJSONObject(i);
					MineMsgItemInfo itemInfo = new MineMsgItemInfo();
					itemInfo.msgID = object.getString(Constants.JSON_MSGID);
					itemInfo.msgTitle = object.getString(Constants.JSON_MSGTITLE);
					itemInfo.msgTime = object.getString(Constants.JSON_MSGTIME);

					int flag = StringUtil.parseInt(object.getString(Constants.JSON_MSGSTATE));

					if (flag <= 0)
						itemInfo.unreadMsg = true;
					else
						itemInfo.unreadMsg = false;

					result.msgListInfo.add(itemInfo);
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}
		} while (false);
		return result;
	}

	// 删除消息、设置为已读
	public static BaseResult parseDeleteMessage(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}
		} while (false);
		return result;
	}

	// 消息详情
	public static BaseResult parseMessageDetail(String resData) {
		MineMsgDetailResult result = new MineMsgDetailResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				result.msgText = jsonObj.getString(Constants.JSON_MSGTEXT);

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);

		return result;
	}

	// 收藏的游戏
	public static BaseResult parseCollectionGame(String resData) {
		MineGamesResult result = new MineGamesResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
				String totalnum = jsonObj.getString(Constants.JSON_TOTALCOUNT);
				result.totalcount = StringUtil.parseInt(totalnum);

				JSONArray msglist = jsonObj.getJSONArray(Constants.JSON_GAMELIST);

				for (int i = 0; i < msglist.length(); i++) {
					JSONObject object = msglist.getJSONObject(i);
					MineGameItemInfo itemInfo = new MineGameItemInfo();

					itemInfo.gameID = object.getString(Constants.JSON_GAMEID);
					itemInfo.gameName = object.getString(Constants.JSON_GAMENAME);
					itemInfo.pkgName = object.getString(Constants.JSON_PKGNAME);
					itemInfo.gameurl = object.getString(Constants.JSON_GAMEURL);
					itemInfo.downloadurl = object.getString(Constants.JSON_DOWNLOADURL);
					itemInfo.pkgsize = object.getString(Constants.JSON_PKGSIZE);
					itemInfo.star = object.getString(Constants.JSON_STAR);
					itemInfo.downloadTimes = object.getString(Constants.JSON_DOWNLOADTIMES);
					result.gameListInfo.add(itemInfo);
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 收藏的攻略
	public static BaseResult parseCollectionGuide(String resData) {
		MineGuidesResult result = new MineGuidesResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
				String totalnum = jsonObj.getString(Constants.JSON_TOTALCOUNT);
				result.totalcount = StringUtil.parseInt(totalnum);

				JSONArray msglist = jsonObj.getJSONArray(Constants.JSON_GUIDELIST);

				for (int i = 0; i < msglist.length(); i++) {
					JSONObject object = msglist.getJSONObject(i);
					MineGuideItemInfo itemInfo = new MineGuideItemInfo();

					itemInfo.guideID = object.getString(Constants.JSON_GUIDEID);
					itemInfo.guideTitle = object.getString(Constants.JSON_GUIDETITLE);
					itemInfo.guideTime = object.getString(Constants.JSON_GUIDETIME);
					itemInfo.guideUrl = object.getString(Constants.JSON_GUIDEURL);

					result.guideListInfo.add(itemInfo);
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 我安装过的游戏
	public static BaseResult parseInstalledGame(String resData) {
		MineGamesResult result = new MineGamesResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
				String totalnum = jsonObj.getString(Constants.JSON_TOTALCOUNT);
				result.totalcount = StringUtil.parseInt(totalnum);

				JSONArray msglist = jsonObj.getJSONArray(Constants.JSON_GAMELIST);

				for (int i = 0; i < msglist.length(); i++) {
					JSONObject object = msglist.getJSONObject(i);
					MineGameItemInfo itemInfo = new MineGameItemInfo();

					itemInfo.gameID = object.getString(Constants.JSON_GAMEID);
					itemInfo.gameName = object.getString(Constants.JSON_GAMENAME);
					itemInfo.pkgName = object.getString(Constants.JSON_PKGNAME);
					itemInfo.gameurl = object.getString(Constants.JSON_GAMEURL);
					itemInfo.downloadurl = object.getString(Constants.JSON_DOWNLOADURL);
					itemInfo.pkgsize = object.getString(Constants.JSON_PKGSIZE);
					result.gameListInfo.add(itemInfo);
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 我的动态
	public static BaseResult parseMyDynamicData(String resData) {
		DynamicDataResult result = new DynamicDataResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				result.gamenum = jsonObj.getString(Constants.JSON_GAMENUM);
				result.totalmsgnum = jsonObj.getString(Constants.JSON_TOTALMSGNUM);
				result.unreadmsgnum = jsonObj.getString(Constants.JSON_UNREADMSGNUM);
				result.collectnum = jsonObj.getString(Constants.JSON_COLLECTNUM);
				result.coinnum = jsonObj.getInt(Constants.JSON_COINNUM);

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}
		} while (false);
		return result;
	}

	// 收藏动作
	public static BaseResult parseCollectionActions(String resData) {
		CollectionActionResult result = new CollectionActionResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
				result.msgtype = jsonObj.getInt(Constants.JSON_MSGTYPE);
				result.msgsubtype = jsonObj.getInt(Constants.JSON_MSGSUBTYPE);

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 注册安装的游戏
	public static BaseResult parseRegisterGame(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 检查更新
	public static BaseResult parseCheckUpdate(String resData) {
		CheckUpdateResult result = new CheckUpdateResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				result.updatetype = jsonObj.getInt(Constants.JSON_APP_UPDATETYPE);
				result.apkurl = jsonObj.getString(Constants.JSON_APP_APKURL);
				result.apkversion = jsonObj.getString(Constants.JSON_APP_APKVERSION);
				result.apksize = jsonObj.getString(Constants.JSON_APP_APKSIZE);
				result.description = jsonObj.getString(Constants.JSON_APP_DESCRIPTION);

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 用户反馈
	public static BaseResult parseFeedback(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	/**
	 * tag 210 抢号列表
	 */
	public static BaseResult parseSnapNumberList(String resData) {
		SnapNumberList snapNumberObj = new SnapNumberList();
		try {
			JSONObject outterObj = new JSONObject(resData);
			int errorcode = outterObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = outterObj.getString(Constants.JSON_ERROR_MSG);
			String tag = outterObj.getString(Constants.JSON_TAG);
			snapNumberObj.setTag(tag);
			snapNumberObj.setErrorCode(errorcode);
			snapNumberObj.setErrorString(errorStr);

			if (errorcode == DcError.DC_OK) {
				int totalCount = outterObj.getInt(Constants.JSON_TOTALCOUNT);
				snapNumberObj.setTotalCount(totalCount);

				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_GRAB_GAMES);
				int length = jsonList.length();
				List<SnapNumber> numberList = new ArrayList<SnapNumber>(length);
				for (int i = 0; i < length; i++) {
					try {
						JSONObject innerObject = jsonList.getJSONObject(i);
						String gameId = innerObject.getString(Constants.JSON_GAME_ID);
						String pkgName = innerObject.getString(Constants.JSON_GAME_PACKAGE);

						String grabId = innerObject.getString(Constants.JSON_GRAB_ID);

						String title = innerObject.getString(Constants.JSON_GRAB_TITLE);
						String gameIcon = innerObject.optString(Constants.JSON_GAME_ICON);// maybe
																							// null
						int numberRest = StringUtil.parseInt(innerObject.getString(Constants.JSON_GRAB_NUMBE_RREST));
						int numberTotal = StringUtil.parseInt(innerObject.getString(Constants.JSON_GRAB_NUMBER_TOTAL));
						int grabStatus = StringUtil.parseInt(innerObject.getString(Constants.JSON_GRAB_STATUS));

						Date time = DateUtil.pareseDate(innerObject.getString(Constants.JSON_GUIDETIME));
						String n = outterObj.optString(Constants.JSON_GRABBED_NUMBER);
						SnapNumber number = new SnapNumber(gameId, grabId, title, gameIcon, numberRest, numberTotal, SnapNumberStatus.getStatus(grabStatus), ((time == null) ? 0 : time.getTime()), n,
								pkgName);
						number.version = innerObject.getString(Constants.JSON_GAME_VERSION);
						number.downloadUrl = innerObject.getString(Constants.JSON_SPEED_DOWNLOAD_URL);
						number.startAction = innerObject.getString(Constants.JSON_GAME_KEY);
						number.isNeedLogin = innerObject.getString(Constants.JSON_NEED_LOGIN).equals("1");
						number.gameName = innerObject.getString(Constants.JSON_GAMENAME);
						number.verCode = Integer.parseInt(innerObject.getString(Constants.JSON_GAME_VERSION_INT));
						number.pkgSize = innerObject.getString(Constants.JSON_PKGSIZE);
						numberList.add(number);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				snapNumberObj.setData(numberList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return snapNumberObj;
	}

	/**
	 * tag 211抢号详情
	 */
	public static BaseResult parseSnapNumberDetail(String resData) {
		SnapNumberDetail snapNumberObj = new SnapNumberDetail();
		try {
			JSONObject outterObj = new JSONObject(resData);
			int errorcode = outterObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = outterObj.getString(Constants.JSON_ERROR_MSG);
			String tag = outterObj.getString(Constants.JSON_TAG);
			snapNumberObj.setTag(tag);
			snapNumberObj.setErrorCode(errorcode);
			snapNumberObj.setErrorString(errorStr);

			if (errorcode == DcError.DC_OK) {
				String gameId = outterObj.getString(Constants.JSON_GAME_ID);
				String grabId = outterObj.getString(Constants.JSON_GRAB_ID);

				if (TextUtils.isEmpty(grabId)) {
					return snapNumberObj;
				}
				snapNumberObj.setId(grabId);
				String title = outterObj.getString(Constants.JSON_GRAB_TITLE);
				String gameIcon = outterObj.getString(Constants.JSON_GAME_ICON);
				int numberRest = StringUtil.parseInt(outterObj.getString(Constants.JSON_GRAB_NUMBE_RREST));
				int numberTotal = StringUtil.parseInt(outterObj.getString(Constants.JSON_GRAB_NUMBER_TOTAL));

				String gameName = outterObj.getString(Constants.JSON_GAMENAME);

				Date time = DateUtil.pareseDate(outterObj.getString(Constants.JSON_GUIDETIME));
				String n = null;
				try {
					n = outterObj.getString(Constants.JSON_GRABBED_NUMBER);
				} catch (Exception e) {
					// TODO: handle exception
				}

				int grabStatus = StringUtil.parseInt(outterObj.getString(Constants.JSON_GRAB_STATUS));

				snapNumberObj.setGameName(gameName);

				String packageName = outterObj.getString(Constants.JSON_GAME_PACKAGE);
				String downloadUrl = outterObj.getString(Constants.JSON_GAME_DOWNLOAD_URL);
				snapNumberObj.setPackageName(packageName);
				snapNumberObj.setDownloadurl(downloadUrl);

				SnapNumber snapNumber = new SnapNumber(gameId, grabId, title, gameIcon, numberRest, numberTotal, SnapNumberStatus.getStatus(grabStatus), ((time == null) ? 0 : time.getTime()), n,
						packageName);

				snapNumberObj.setSnapNumber(snapNumber);
				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_DETAIL_CONTENTS_LIST);
				int length = jsonList.length();
				List<SnapNumberItem> dataList = new ArrayList<SnapNumberItem>(length);
				for (int i = 0; i < length; i++) {
					JSONObject innerObject = jsonList.getJSONObject(i);
					String content = innerObject.getString(Constants.JSON_DETAIL_ITEM_CONTENT);
					String picUrl = innerObject.getString(Constants.JSON_DETAIL_ITEM_PIC);
					com.ranger.bmaterials.mode.SnapNumberDetail.SnapNumberItem item = new com.ranger.bmaterials.mode.SnapNumberDetail.SnapNumberItem(picUrl, content);
					dataList.add(item);
				}
				snapNumberObj.setData(dataList);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			snapNumberObj.setErrorCode(DcError.DC_Error);
			return snapNumberObj;
		}
		return snapNumberObj;
	}

	/**
	 * tag 212抢号
	 * 结果：0:抢号成功;1:没中号;2:被抢光了;3:重复抢号(需要将grabid和grabbednumber返回);4:还没开始;
	 * 5:已经结束;其他数字:其他错误
	 */
	public static BaseResult parseSnapNumberAction(String resData) {
		SnappedNumber snapNumberObj = new SnappedNumber();
		try {
			JSONObject outterObj = new JSONObject(resData);
			int errorcode = outterObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = outterObj.getString(Constants.JSON_ERROR_MSG);
			String tag = outterObj.getString(Constants.JSON_TAG);
			snapNumberObj.setTag(tag);
			snapNumberObj.setErrorCode(errorcode);
			snapNumberObj.setErrorString(errorStr);

			if (errorcode == DcError.DC_OK) {
				String number = outterObj.getString(Constants.JSON_GRABBED_NUMBER);
				String gameId = null;
				if (outterObj.has(Constants.JSON_GAME_ID)) {
					gameId = outterObj.getString(Constants.JSON_GAME_ID);
				}
				String grabId = outterObj.getString(Constants.JSON_GRAB_ID);
				int resCode = SnappedNumber.ResCode.OTHER_ERROR;
				resCode = StringUtil.parseInt(outterObj.getString(Constants.JSON_GRABBED_RESCODE));
				snapNumberObj.setNumber(number);
				snapNumberObj.setGameId(gameId);
				snapNumberObj.setGrabId(grabId);
				snapNumberObj.setResCode(resCode);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return snapNumberObj;
	}

	static class CommonResp {
		int errorCode;
		String errorString;
		String tag;
	}

	static CommonResp pareseCommonResp(String resData) throws JSONException {
		JSONObject outterObj = new JSONObject(resData);
		CommonResp commonResp = new CommonResp();
		commonResp.tag = outterObj.getString(Constants.JSON_TAG);
		try {
			commonResp.errorCode = outterObj.getInt(Constants.JSON_ERROR_CODE);
			commonResp.errorString = outterObj.getString(Constants.JSON_ERROR_MSG);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commonResp;
	}

	/**
	 * tag 213 开服列表
	 */
	public static BaseResult parseOpenServerList(String resData) {
		OpenServerList openServers = new OpenServerList();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			openServers.setTag(commonResp.tag);
			openServers.setErrorCode(commonResp.errorCode);
			openServers.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				int totalCount = outterObj.getInt(Constants.JSON_TOTALCOUNT);
				openServers.setTotalCount(totalCount);

				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_OPEN_SERVERS);
				int length = jsonList.length();
				List<OpenServer> dataList = new ArrayList<OpenServer>(length);
				for (int i = 0; i < length; i++) {
					try {
						JSONObject innerObject = jsonList.getJSONObject(i);
						String gameId = innerObject.getString(Constants.JSON_GAME_ID);
						String gameName = innerObject.getString(Constants.JSON_GAME_NAME);

						String openServerId = innerObject.getString(Constants.JSON_OPEN_SERVER_ID);
						String title = innerObject.getString(Constants.JSON_OPEN_SERVER_TITLE);
						String gameIcon = innerObject.getString(Constants.JSON_GAME_ICON);
						// int openStatus =
						// StringUtil.parseInt(innerObject.getString(Constants.JSON_OPEN_SERVER_STATUS));
						String timeString = innerObject.getString(Constants.JSON_OPEN_SERVER_TIME);

						// OpenServerStatus status =
						// OpenServerStatus.getStatus(openStatus);
						Date date = DateUtil.pareseDate(timeString);

						OpenServer item = new OpenServer(gameId, openServerId, gameName, title, gameIcon, /*
																										 * status
																										 * ,
																										 */
						(date != null) ? date.getTime() : 0);
						dataList.add(item);
					} catch (Exception e) {
					}
				}
				openServers.setData(dataList);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return openServers;
	}

	/**
	 * tag 214 开服详情
	 */
	public static BaseResult parseOpenServerDetail(String resData) {
		OpenServerDetail openServerDetail = new OpenServerDetail();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			openServerDetail.setTag(commonResp.tag);
			openServerDetail.setErrorCode(commonResp.errorCode);
			openServerDetail.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);

				String openserverid = outterObj.getString(Constants.JSON_OPEN_SERVER_ID);
				if (TextUtils.isEmpty(openserverid)) {
					return openServerDetail;
				}
				openServerDetail.setOpenid(openserverid);
				openServerDetail.setInfossource(outterObj.getString(Constants.JSON_ACTIVITY_DETAIL_ACTSOURCE));
				Date date = DateUtil.pareseDate(outterObj.getString(Constants.JSON_OPEN_SERVER_TIME));
				openServerDetail.setInfostime(date == null ? 0 : date.getTime());
				openServerDetail.setOpentitle("【" + outterObj.getString(Constants.JSON_GAME_NAME) + "】" + outterObj.getString(Constants.JSON_OPEN_SERVER_TITLE));
				if (outterObj.has(Constants.JSON_GAME_ID) && !outterObj.isNull(Constants.JSON_GAME_ID)) {
					openServerDetail.item = new SearchItem(outterObj.getString(Constants.JSON_GAME_ID), outterObj.getString(Constants.JSON_GAME_NAME), Float.valueOf("0"), StringUtil.valueOf("0"),
							outterObj.getString(Constants.JSON_GAME_PACKAGE), outterObj.getString(Constants.JSON_GAME_ICON), outterObj.getString(Constants.JSON_GAME_DOWNLOAD_URL),
							Long.valueOf(outterObj.getString(Constants.JSON_GAME_PACKAGE_SIZE)), outterObj.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONNAME), StringUtil.valueOf(outterObj
									.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONCODE)), 0, "", false, false);
				}
				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_DETAIL_CONTENTS_LIST);
				int length = jsonList.length();
				List<OpenServerItem> dataList = new ArrayList<OpenServerItem>(length);
				for (int i = 0; i < length; i++) {
					JSONObject innerObject = jsonList.getJSONObject(i);
					String content = innerObject.getString(Constants.JSON_DETAIL_ITEM_CONTENT);
					String pic = innerObject.getString(Constants.JSON_DETAIL_ITEM_PIC);
					OpenServerItem item = new OpenServerItem(pic, content);
					dataList.add(item);
				}
				openServerDetail.setData(dataList);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			openServerDetail.setErrorCode(DcError.DC_Error);
		}
		return openServerDetail;
	}

	/**
	 * tag 217资讯列表
	 */
	public static BaseResult parseAppiaisal(String resData) {
		ActivityInfoList acitivities = new ActivityInfoList();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			acitivities.setTag(commonResp.tag);
			acitivities.setErrorCode(commonResp.errorCode);
			acitivities.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				int totalCount = outterObj.getInt(Constants.JSON_TOTALCOUNT);
				acitivities.setTotalCount(totalCount);

				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_APPRAISAL);
				int length = jsonList.length();
				List<ActivityInfo> dataList = new ArrayList<ActivityInfo>(length);
				for (int i = 0; i < length; i++) {
					try {
						JSONObject innerObject = jsonList.getJSONObject(i);

						String infosid = innerObject.getString(Constants.JSON_APPRAISAL_ID);
						String infostitle = innerObject.getString(Constants.JSON_APPRAISAL_TITLE);
						String infoscontent = innerObject.getString(Constants.JSON_APPRAISAL_CONTENT);
						String infosicon = innerObject.getString(Constants.JSON_APPRAISAL_ICON);
						String infostime = innerObject.getString(Constants.JSON_APPRAISAL_TIME);

						Date date = DateUtil.pareseDate(infostime);
						ActivityInfo activityInfo = new ActivityInfo(null, infosid, infostitle, infosicon, infoscontent, (date != null) ? date.getTime() : 0);
						dataList.add(activityInfo);
					} catch (Exception e) {
					}
				}
				acitivities.setData(dataList);

			}

		} catch (Exception e) {
			acitivities.setErrorCode(DcError.DC_Error);
			e.printStackTrace();
		}
		return acitivities;
	}

	/**
	 * tag 215 活动列表
	 */
	public static BaseResult parseActivitis(String resData) {
		ActivityInfoList acitivities = new ActivityInfoList();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			acitivities.setTag(commonResp.tag);
			acitivities.setErrorCode(commonResp.errorCode);
			acitivities.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				int totalCount = outterObj.getInt(Constants.JSON_TOTALCOUNT);
				acitivities.setTotalCount(totalCount);

				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_ACTIVITIES);
				int length = jsonList.length();
				List<ActivityInfo> dataList = new ArrayList<ActivityInfo>(length);
				for (int i = 0; i < length; i++) {
					try {
						JSONObject innerObject = jsonList.getJSONObject(i);
						String gameId = innerObject.getString(Constants.JSON_GAME_ID);

						String activityId = innerObject.getString(Constants.JSON_ACTIVITY_ID);
						String title = innerObject.getString(Constants.JSON_ACTIVITY_TITLE);
						String gameIcon = innerObject.getString(Constants.JSON_GAME_ICON);
						String timeString = innerObject.getString(Constants.JSON_ACTIVITY_TIME);

						Date date = DateUtil.pareseDate(timeString);
						ActivityInfo activityInfo = new ActivityInfo(gameId, activityId, title, gameIcon, (date != null) ? date.getTime() : 0);
						dataList.add(activityInfo);
					} catch (Exception e) {
					}
				}
				acitivities.setData(dataList);

			}

		} catch (Exception e) {
			acitivities.setErrorCode(DcError.DC_Error);
			e.printStackTrace();
		}
		return acitivities;
	}

	/**
	 * tag 216 活动详情
	 */
	public static BaseResult parseActivityDetail(String resData) {
		ActivityDetail acitivityDetail = new ActivityDetail();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			acitivityDetail.setTag(commonResp.tag);
			acitivityDetail.setErrorCode(commonResp.errorCode);
			acitivityDetail.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				String activityId = outterObj.getString(Constants.JSON_ACTIVITY_ID);
				if (TextUtils.isEmpty(activityId)) {
					return acitivityDetail;
				}
				acitivityDetail.setId(activityId);
				acitivityDetail.setActtitle(outterObj.getString(Constants.JSON_ACTIVITY_TITLE));
				Date date = DateUtil.pareseDate(outterObj.getString(Constants.JSON_ACTIVITY_TIME));
				acitivityDetail.setTime(date == null ? 0 : date.getTime());
				acitivityDetail.setActsource(outterObj.getString(Constants.JSON_ACTIVITY_DETAIL_ACTSOURCE));
				if (outterObj.has(Constants.JSON_GAME_ID) && !outterObj.isNull(Constants.JSON_GAME_ID)) {
					acitivityDetail.item = new SearchItem(outterObj.getString(Constants.JSON_GAME_ID), outterObj.getString(Constants.JSON_GAME_NAME), Float.valueOf("0"), StringUtil.valueOf("0"),
							outterObj.getString(Constants.JSON_GAME_PACKAGE), outterObj.getString(Constants.JSON_GAME_ICON), outterObj.getString(Constants.JSON_GAME_DOWNLOAD_URL),
							Long.valueOf(outterObj.getString(Constants.JSON_GAME_PACKAGE_SIZE)), outterObj.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONNAME), StringUtil.valueOf(outterObj
									.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONCODE)), 0, "", false, false);
				}
				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_DETAIL_CONTENTS_LIST);
				int length = jsonList.length();
				List<ActivityItem> dataList = new ArrayList<ActivityItem>(length);
				for (int i = 0; i < length; i++) {
					JSONObject innerObject = jsonList.getJSONObject(i);
					String content = innerObject.getString(Constants.JSON_DETAIL_ITEM_CONTENT);
					String picUrl = innerObject.getString(Constants.JSON_DETAIL_ITEM_PIC);
					ActivityItem activityItem = new ActivityItem(picUrl, content);
					dataList.add(activityItem);
				}
				acitivityDetail.setData(dataList);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			acitivityDetail.setErrorCode(DcError.DC_Error);
			return acitivityDetail;
		}
		return acitivityDetail;
	}

	/**
	 * tag 218 测评详情
	 */
	public static BaseResult parseAppiaisalDetail(String resData) {
		AppiaisalDetail acitivityDetail = new AppiaisalDetail();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			acitivityDetail.setTag(commonResp.tag);
			acitivityDetail.setErrorCode(commonResp.errorCode);
			acitivityDetail.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				JSONObject infosObj = outterObj.getJSONObject(Constants.JSON_APPRAISAL);
				JSONObject gameinfoObj = outterObj.getJSONObject(Constants.JSON_APPRAISAL_GAMEINFO);
				Date date = DateUtil.pareseDate(infosObj.getString(Constants.JSON_APPRAISAL_INFOSTIME));
				acitivityDetail.setInfostime(date == null ? 0 : date.getTime());
				acitivityDetail.setInfossource(infosObj.getString(Constants.JSON_APPRAISAL_INFOSSOURCE));
				acitivityDetail.setInfoscontent(infosObj.getString(Constants.JSON_APPRAISAL_INFOSCONTENT));
				if (infosObj.has(Constants.JSON_APPRAISAL_TITLE)) {
					acitivityDetail.setInfostitle(infosObj.getString(Constants.JSON_APPRAISAL_TITLE));
				}

				try {
					if (gameinfoObj.has(Constants.JSON_GAME_ID) && !gameinfoObj.isNull(Constants.JSON_GAME_ID)) {
						boolean needlogin = "1".equals(gameinfoObj.getString(Constants.JSON_SPEED_DOWNLOAD_NEED_LOGIN)) ? true : false;
						acitivityDetail.item = new SearchItem(gameinfoObj.getString(Constants.JSON_GAME_ID), gameinfoObj.getString(Constants.JSON_GAME_NAME), Float.valueOf("0"),
								StringUtil.valueOf("0"), gameinfoObj.getString(Constants.JSON_GAME_PACKAGE), gameinfoObj.getString(Constants.JSON_GAME_ICON),
								gameinfoObj.getString(Constants.JSON_GAME_DOWNLOAD_URL), Long.valueOf(gameinfoObj.getString(Constants.JSON_GAME_PACKAGE_SIZE)),
								gameinfoObj.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONNAME), StringUtil.valueOf(gameinfoObj.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONCODE)), 0, "",
								needlogin, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
			acitivityDetail.setErrorCode(DcError.DC_Error);
			return acitivityDetail;
		}
		return acitivityDetail;
	}

	// 手机号一键注册登陆
	public static BaseResult parseChangeNickname(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 手机号一键注册登陆
	public static BaseResult parseFastPhoneRegister(String resData) {
		return parseUserLogin(resData);
	}

	/**
	 * 白名单 tag = 501
	 * 
	 * @param resData
	 * @return
	 */
	public static BaseResult parseWhiteList(String resData) {
		WhiteList whiteList = new WhiteList();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			whiteList.setTag(commonResp.tag);
			whiteList.setErrorCode(commonResp.errorCode);
			whiteList.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_PACKAGES);

				int length = jsonList.length();
				List<BaseAppInfo> dataList = new ArrayList<BaseAppInfo>(length);
				for (int i = 0; i < length; i++) {
					String pack = jsonList.getString(i);
					dataList.add(new BaseAppInfo(pack, null));
				}
				whiteList.setData(dataList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return whiteList;
	}

	/**
	 * 游戏更新 tag = 502
	 * 
	 * @param resData
	 * @return
	 */
	public static BaseResult parseUpdateGames(String resData) {
		UpdatableList updatableList = new UpdatableList();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			updatableList.setTag(commonResp.tag);
			updatableList.setErrorCode(commonResp.errorCode);
			updatableList.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_GAME_UPDATE_RESULTS);

				int length = jsonList.length();
				List<UpdatableItem> dataList = new ArrayList<UpdatableItem>(length);
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = jsonList.getJSONObject(i);

					String version = jsonObject.getString(Constants.JSON_VERSION);
					int versionInt = StringUtil.parseInt(jsonObject.getString(Constants.JSON_VERSION_INT));
					String downloadUrl = jsonObject.getString(Constants.JSON_DOWNLOADURL);
					String iconUrl = jsonObject.getString(Constants.JSON_GAME_ICON_URL);
					long packageSize = 0;
					try {
						packageSize = Long.parseLong(jsonObject.getString(Constants.JSON_GAME_PACKAGE_SIZE));
					} catch (Exception e) {
					}
					String dateString = jsonObject.getString(Constants.JSON_PUBLISH_DATE);
					Date date = DateUtil.pareseDate(dateString);
					long publishDate = ((date == null) ? 0 : date.getTime());
					String packageName = jsonObject.getString(Constants.JSON_PKGNAME);
					String gameId = null;
					if (jsonObject.has(Constants.JSON_GAME_ID)) {
						gameId = jsonObject.getString(Constants.JSON_GAME_ID);
					}
					String key = null;
					if (jsonObject.has(Constants.JSON_GAME_KEY)) {
						key = jsonObject.getString(Constants.JSON_GAME_KEY);
					}
					boolean needLogin = false;
					if (jsonObject.has(Constants.JSON_NEED_LOGIN)) {
						int parseInt = StringUtil.parseInt(jsonObject.getString(Constants.JSON_NEED_LOGIN));
						needLogin = (parseInt == 1);
					}
					boolean updatable = true;
					int updatableInt = StringUtil.parseInt(jsonObject.getString(Constants.JSON_UPDATABLE));
					updatable = (updatableInt == 1);

					// TODO
					String patchUrl = null;
					if (jsonObject.has(Constants.JSON_PATCH_URL)) {
						patchUrl = jsonObject.getString(Constants.JSON_PATCH_URL);
						patchUrl = TextUtils.isEmpty(patchUrl) ? null : patchUrl;
					}
					long patchSize = 0;
					if (jsonObject.has(Constants.JSON_PATCH_SIZE)) {
						String patchSizeString = jsonObject.getString(Constants.JSON_PATCH_SIZE);
						try {
							patchSize = Long.parseLong(patchSizeString);
						} catch (Exception e) {
						}
					}
					boolean diffUpdate = (patchUrl != null && patchSize > 0);

					UpdatableItem updatableItem = new UpdatableItem(gameId, packageName, null, versionInt, version, downloadUrl, publishDate, null, packageSize, iconUrl, key, needLogin, updatable,
							diffUpdate, patchUrl, patchSize);
					dataList.add(updatableItem);

				}
				updatableList.setData(dataList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return updatableList;
	}

	/**
	 * 
	 * @param resData
	 * @return
	 */
	public static BaseResult parseGetDownloadedGames(String resData) {
		MyDownloadedGames list = new MyDownloadedGames();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			list.setTag(commonResp.tag);
			list.setErrorCode(commonResp.errorCode);
			list.setErrorString(commonResp.errorString);

			if (commonResp.errorCode == DcError.DC_OK) {
				JSONObject outterObj = new JSONObject(resData);
				JSONArray jsonList = outterObj.getJSONArray(Constants.JSON_GAMELIST);

				int length = jsonList.length();
				List<MyDownloadedGame> dataList = new ArrayList<MyDownloadedGame>(length);
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = jsonList.getJSONObject(i);
					String packageName = jsonObject.getString(Constants.JSON_PKGNAME);
					String gameId = jsonObject.getString(Constants.JSON_GAME_ID);
					String gameName = jsonObject.getString(Constants.JSON_GAME_NAME);
					String iconUrl = jsonObject.getString(Constants.JSON_GAME_ICON);
					String key = null;
					if (jsonObject.has(Constants.JSON_GAME_KEY)) {
						key = jsonObject.getString(Constants.JSON_GAME_KEY);
					}
					boolean needLogin = false;
					if (jsonObject.has(Constants.JSON_NEED_LOGIN)) {
						int parseInt = StringUtil.parseInt(jsonObject.getString(Constants.JSON_NEED_LOGIN));
						needLogin = (parseInt == 1);
					}
					MyDownloadedGame myDownloadedGame = new MyDownloadedGame(gameId, gameName, iconUrl, packageName, key, null, needLogin);
					dataList.add(myDownloadedGame);
				}
				list.setData(dataList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 
	 * @param resData
	 * @return
	 */
	public static BaseResult parseUploadDownloadedGames(String resData) {
		BaseResult result = new BaseResult();
		try {
			CommonResp commonResp = pareseCommonResp(resData);
			result.setTag(commonResp.tag);
			result.setErrorCode(commonResp.errorCode);
			result.setErrorString(commonResp.errorString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 刷新token tag=142
	 * 
	 * @param resData
	 * @return
	 */
	public static BaseResult parseLoginToken(String resData) {
		GameLoginTokenResult result = new GameLoginTokenResult();
		result.parse(resData);
		return result;
	}

	/**
	 * 高速下载 tag=260
	 * 
	 * @param resData
	 * @return
	 */
	public static SpeedDownloadResult parseSpeedDownload(String resData) {
		ArrayList<SpeedDownLoadInfo> instance = new ArrayList<SpeedDownLoadInfo>();
		SpeedDownloadResult infoList = new SpeedDownloadResult();
		try {
			JSONObject jobj = new JSONObject(resData);
			int errorcode = jobj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jobj.getString(Constants.JSON_ERROR_MSG);
			String tag = jobj.getString(Constants.JSON_TAG);

			infoList.setTag(tag);
			infoList.setErrorCode(errorcode);
			infoList.setErrorString(errorStr);

			if (errorcode != 0)
				return infoList;

			JSONArray jsonArray = jobj.getJSONArray(Constants.JSON_SPEED_DOWNLOAD_DOWNLOADINFOS);
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.opt(i);

				SpeedDownLoadInfo info = new SpeedDownLoadInfo();
				info.setUrl(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_URL));
				info.setPackagename(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_PACKAGENAME));
				info.setVersionname(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONNAME));
				info.setVersioncode(StringUtil.parseInt(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONCODE)));
				info.setIconurl(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_ICONURL));
				info.setAppname(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_APPNAME));
				info.setGameid(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_GAME_ID));
				info.setStartaction(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_ACTION));
				info.setNeedLogin(jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_NEED_LOGIN).equals("1"));
				info.setApkSize(jsonObject.getString(Constants.JSON_GAME_PACKAGE_SIZE));

				instance.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		infoList.setContentList(instance);
		return infoList;
	}

	// 用户注册
	public static BaseResult parseExchangeHistoryDetail(String resData) {

		ExchangeHistoryDetailResult result = new ExchangeHistoryDetailResult();
		ArrayList<ExchangeItem> exchangeList = new ArrayList<ExchangeItem>();

		do {
			try {

				JSONObject jsonObj = new JSONObject(resData);

				String tag = jsonObj.getString(Constants.JSON_TAG);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				JSONArray exchangelist = jsonObj.getJSONArray(Constants.JSON_EXCHANGE_LIST);
				if (exchangelist.length() > 0) {

					for (int i = 0; i < exchangelist.length(); i++) {

						JSONObject exchangeItem = exchangelist.getJSONObject(i);
						if (null != exchangeItem) {
							if (!exchangeItem.has(Constants.JSON_EXCHANGE_ID) || !exchangeItem.has(Constants.JSON_EXCHANGE_PROP_ID) || !exchangeItem.has(Constants.JSON_EXCHANGE_METADATA)) {
								continue;
							}

							String exchangeid = exchangeItem.getString(Constants.JSON_EXCHANGE_ID);
							String propid = exchangeItem.getString(Constants.JSON_EXCHANGE_PROP_ID);
							String propIcon = exchangeItem.getString(Constants.JSON_EXCHANGE_PROP_ICON_URL);

							String exchange_date = exchangeItem.getString(Constants.JSON_EXCHANGE_DATE);

							String expire_date = "";
							if (exchangeItem.has(Constants.JSON_EXCHANGE_EXPIRE_DATE)) {
								expire_date = exchangeItem.getString(Constants.JSON_EXCHANGE_EXPIRE_DATE);
							}

							JSONObject metadata = exchangeItem.getJSONObject(Constants.JSON_EXCHANGE_METADATA);
							int metatype = metadata.getInt(Constants.JSON_EXCHANGE_METATYPE);

							String cardnum = "";
							String cardpwd = "";
							int operator = 0;

							if (metatype == Constants.EXCHANGE_META_TYPE_CARD) {
								cardnum = metadata.getString(Constants.JSON_EXCHANGE_CARD_NUM);
								cardpwd = metadata.getString(Constants.JSON_EXCHANGE_CARD_PWD);
								operator = metadata.getInt(Constants.JSON_EXCHANGE_CARD_OPERATOR);
							}

							ExchangeItem ei = new ExchangeItem(exchangeid, propid, propIcon, exchange_date.equals("") ? "1970-01-01" : exchange_date, expire_date.equals("") ? "2999-12-31"
									: expire_date, metatype, cardnum, cardpwd, operator);
							exchangeList.add(ei);
						}
					}

					result.setData(exchangeList);
				}

			} catch (Exception e) {
				e.printStackTrace();
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);

		return result;

	}

	// 争霸赛 tag=805
	public static CompetitionResult parseCompetition(String resData) {
		ArrayList<CompetitionInfo> instance = new ArrayList<CompetitionInfo>();
		CompetitionResult infoList = new CompetitionResult();
		try {
			JSONObject jobj = new JSONObject(resData);
			int errorcode = jobj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jobj.getString(Constants.JSON_ERROR_MSG);
			String tag = jobj.getString(Constants.JSON_TAG);

			infoList.setTag(tag);
			infoList.setErrorCode(errorcode);
			infoList.setErrorString(errorStr);

			if (errorcode != 0)
				return infoList;

			JSONArray jsonArray = jobj.getJSONArray(Constants.JSON_COMPETITION_LIST);
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				try {
					JSONObject jsonObject = (JSONObject) jsonArray.opt(i);

					CompetitionInfo info = new CompetitionInfo();
					info.coins = jsonObject.getString(Constants.JSON_COMPETITION_COINS);
					info.date = jsonObject.getString(Constants.JSON_EXCHANGE_DATE);
					info.end_date = jsonObject.getString("end_date");
					info.downloadUrl = jsonObject.getString(Constants.JSON_DOWNLOADURL);
					info.gameId = jsonObject.getString(Constants.JSON_GAME_ID);
					info.gameName = jsonObject.getString(Constants.JSON_GAME_NAME);
					info.isNeedLogin = Boolean.valueOf(jsonObject.getString(Constants.JSON_NEED_LOGIN));
					info.memebers = jsonObject.getString(Constants.JSON_COMPETITION_MEMEBERS);
					info.picUrl = jsonObject.getString(Constants.JSON_GAME_ICON_URL);
					info.pkgName = jsonObject.getString(Constants.JSON_PKGNAME);
					info.rewards = jsonObject.getString(Constants.JSON_COMPETITION_REWARDS);
					info.rule = jsonObject.getString(Constants.JSON_COMPETITION_RULE);
					info.startAction = jsonObject.getString(Constants.JSON_GAME_KEY);
					info.title = jsonObject.getString(Constants.JSON_COMPETITION_TITLE);
					info.version = jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONNAME);
					info.versionInt = jsonObject.getString(Constants.JSON_SPEED_DOWNLOAD_VERSIONCODE);
					String rescode = jsonObject.getString("rescode");
					info.rescode = Integer.parseInt(rescode);
					info.topreward = jsonObject.getString("topreward");
					instance.add(info);
				} catch (Exception e) {
				}
			}

			infoList.desc = jobj.getString(Constants.JSON_COMPETITION_DESC);
			infoList.gamesCount = Integer.valueOf(jobj.getString("gamescount"));
			infoList.bannerIconUrl = jobj.getString("competitionBannerIcon");
		} catch (Exception e) {
			e.printStackTrace();
		}
		infoList.competitions_list = instance;
		return infoList;
	}

	// 必玩 tag=806
	public static MustPlayGames parseMustPlayGames(String resData) {
		MustPlayGames result = new MustPlayGames();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
				JSONArray gamelist = jsonObj.getJSONArray(Constants.JSON_GAMELIST);

				for (int i = 0; i < gamelist.length(); i++) {
					JSONObject object = gamelist.getJSONObject(i);
					SearchItem itemInfo = new SearchItem();
					itemInfo.setGameName(object.getString(Constants.JSON_GAME_CATEGORY));
					result.gameListInfo.add(itemInfo);

					JSONArray games = object.getJSONArray(Constants.JSON_GAME_LISTS);

					for (int j = 0; j < games.length(); j++) {
						JSONObject obj = games.getJSONObject(j);
						SearchItem gameInfo = new SearchItem(obj.getString("gameid"), obj.getString("gamename"), Float.valueOf("0"), StringUtil.valueOf("0"), obj.getString("pkgname"),
								obj.getString("gameicon"), obj.getString("downloadurl"), Long.valueOf("0"), obj.getString("versionname"), StringUtil.valueOf(obj.getString("versioncode")), 0, "",
								false, false);
						if (obj.has("labelcolor")) {
							gameInfo.labelColor = obj.getString("labelcolor");
						}
						if (obj.has("labelname")) {
							gameInfo.labelName = obj.getString("labelname");
						}
						gameInfo.setGameNameDes(obj.getString("comment"));
						result.gameListInfo.add(gameInfo);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}
		} while (false);
		return result;
	}

	// 首页数据
	public static BaseResult parseHomePageData(String resData) {
		HomePageDataResult result = new HomePageDataResult();

		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (errorcode != 0)
				return result;

			JSONArray keywords = jsonObj.getJSONArray(Constants.JSON_KEYWORD_HOME_PAGE);

			ArrayList<String> keys = new ArrayList<String>();
			for (int ki = 0; ki < keywords.length(); ki++) {
				keys.add(keywords.getString(ki));
			}
			KeywordsList.getInstance().setRecomKeywords(keys);

			JSONArray ads = jsonObj.getJSONArray(Constants.JSON_GAEMADS_HOME_PAGE);

			ArrayList<ADInfo> list_ad = result.getAdsList();
			int len = ads.length();
			for (int i = 0; i < len; i++) {
				try {
					JSONObject obj = ads.getJSONObject(i);
					ADInfo adInfo = new ADInfo();
					if (!obj.isNull(Constants.JSON_ADGAMEID_HOME_PAGE)) {
						adInfo.setAdgameid(obj.getString(Constants.JSON_ADGAMEID_HOME_PAGE));
					}
					if (!obj.isNull(Constants.JSON_ADGAMENAME_HOME_PAGE)) {
						adInfo.setAdgamename(obj.getString(Constants.JSON_ADGAMENAME_HOME_PAGE));
					}
					if (!obj.isNull(Constants.JSON_ADURL_HOME_PAGE)) {
						adInfo.setAdpicurl(obj.getString(Constants.JSON_ADURL_HOME_PAGE));
					}
					if (!obj.isNull(Constants.JSON_ADPKGNAME_HOME_PAGE)) {
						adInfo.setAdpkgname(obj.getString(Constants.JSON_ADPKGNAME_HOME_PAGE));
					}

					adInfo.setAdtype(StringUtil.parseInt(obj.getString(Constants.JSON_ADTYPE_HOME_PAGE)));
					if (!obj.isNull(Constants.JSON_ITEMID_HOME_PAGE)) {
						adInfo.setItemid(obj.getString(Constants.JSON_ITEMID_HOME_PAGE));
					}
					if (!obj.isNull(Constants.JSON_GAMETYPE_HOME_PAGE)) {
						adInfo.setGametype(obj.getString(Constants.JSON_GAMETYPE_HOME_PAGE));
					}

					if (!obj.isNull(Constants.JSON_GAMETYPENUMBER_HOME_PAGE)) {
						adInfo.setGametypenumber(obj.getString(Constants.JSON_GAMETYPENUMBER_HOME_PAGE));
					}

					if (!obj.isNull("prizeurl")) {
						adInfo.setPrizeurl(obj.getString("prizeurl"));
					}
					list_ad.add(adInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			JSONArray gamesGridJsonArray = jsonObj.getJSONArray(Constants.JSON_GAMEDRID_HOME_PAGE);
			ArrayList<HomeAppGridInfo> gamesGrid = result.getGamesGrid();
			len = gamesGridJsonArray.length();
			for (int i = 0; i < len; i++) {
				try {
					JSONObject obj = gamesGridJsonArray.getJSONObject(i);
					HomeAppGridInfo hai = new HomeAppGridInfo();
					hai.gameId = obj.getString(Constants.JSON_GAMEID_HOME_PAGE);
					hai.gameName = obj.getString(Constants.JSON_GAMENAME_HOME_PAGE);
					hai.pkgName = obj.getString(Constants.JSON_PKGNAME_HOME_PAGE);
					hai.iconUrl = obj.getString("gameicon");
					hai.downloadUrl = obj.getString(Constants.JSON_DOWNLOADURL_HOME_PAGE);
					hai.startAction = obj.getString("startaction");
					if (!obj.isNull("gamedownloadcount")) {
						hai.downloadcount = obj.getString("gamedownloadcount");
					}
					gamesGrid.add(hai);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			result.setGameListTitle(jsonObj.getString("gamelisttitle"));

			parseHomeAppList(jsonObj, result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		result.setResData(resData);

		return result;
	}

	private static void parseHomeAppList(JSONObject jsonObj, HomeAppResult result) throws JSONException {
		JSONArray gamesListJsonArray = jsonObj.getJSONArray(Constants.JSON_GAMELIST_HOME_PAGE);
		ArrayList<HomeAppListInfoArray> gamesList = result.getGamesList();
		int len = gamesListJsonArray.length();
		for (int i = 0; i < len; i++) {
			// 一组
			JSONArray childArray = gamesListJsonArray.getJSONArray(i);
			int childLen = childArray.length();
			HomeAppListInfoArray infoArray = new HomeAppListInfoArray();
			for (int j = 0; j < childLen; j++) {
				try {
					JSONObject obj = childArray.getJSONObject(j);
					if (obj.getString("isbannergame").equals("1")) {
						// banner
						HomeAppListBannerInfo bannerInfo = new HomeAppListBannerInfo();
						bannerInfo.gameid = obj.getString(Constants.JSON_GAMEID_HOME_PAGE);
						bannerInfo.gamename = obj.getString(Constants.JSON_GAMENAME_HOME_PAGE);
						bannerInfo.pkgname = obj.getString(Constants.JSON_PKGNAME_HOME_PAGE);
						bannerInfo.gameicon = obj.getString("gameicon");
						bannerInfo.downloadurl = obj.getString(Constants.JSON_DOWNLOADURL_HOME_PAGE);
						bannerInfo.startaction = obj.getString("startaction");
						bannerInfo.pkgsize = obj.getString("pkgsize");
						bannerInfo.versionname = obj.getString("versionname");
						bannerInfo.versioncode = obj.getString("versioncode");
						bannerInfo.bannericon = obj.getString("bannericon");

						infoArray.bannerInfo = bannerInfo;
					} else if (obj.getString("isbannergame").equals("0")) {
						HomeAppListItemInfo info = new HomeAppListItemInfo();
						info.gameid = obj.getString(Constants.JSON_GAMEID_HOME_PAGE);
						info.gamename = obj.getString(Constants.JSON_GAMENAME_HOME_PAGE);
						info.pkgname = obj.getString(Constants.JSON_PKGNAME_HOME_PAGE);
						info.gameicon = obj.getString("gameicon");
						info.downloadurl = obj.getString(Constants.JSON_DOWNLOADURL_HOME_PAGE);
						info.startaction = obj.getString("startaction");
						info.pkgsize = obj.getString("pkgsize");
						info.versionname = obj.getString("versionname");
						info.versioncode = obj.getString("versioncode");
						info.gamestar = obj.getString("gamestar");
						info.gamedownloadcount = obj.getString("gamedownloadcount");
						info.gamerecommenddesc = obj.getString("gamerecommenddesc");
						info.labelName = obj.getString("labelname");
						info.labelColor = obj.getString("labelcolor");

						infoArray.homeAppListInfos.add(info);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			gamesList.add(infoArray);
		}
	}

	// 首页加载更多数据
	public static HomeAppResult parseHomeMoreData(String resData) {
		HomeAppResult result = new HomeAppResult();

		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (errorcode != 0)
				return result;

			parseHomeAppList(jsonObj, result);
			result.gamescount = Integer.valueOf(jsonObj.getString("gamescount"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Tag 1100 下行
	 * 
	 * @author liushuohui
	 * @param resData
	 * @return
	 */
	public static GameTopicsData parseGameTopicsData(final String resData) throws JSONException {
		JSONObject obj = new JSONObject(resData);

		if (Constants.DEBUG) {
			new Thread() {

				@Override
				public void run() {
					AppUtil.writeToFile(resData, "1100");
				}

			}.start();
		}

		return new GameTopicsData(obj);
	}

	/**
	 * TAG 1102 下行
	 * 
	 * @author liushuohui
	 * @param resData
	 * @return
	 * @throws JSONException
	 */
	public static TopicDetailMoreGamesData parseGameTopicDetailMoreListData(final String resData) throws JSONException {
		JSONObject obj = new JSONObject(resData);

		if (Constants.DEBUG) {
			new Thread() {

				@Override
				public void run() {
					AppUtil.writeToFile(resData, "1102");
				}

			}.start();
		}

		return new TopicDetailMoreGamesData(obj);
	}

	/**
	 * TAG 1101 下行
	 * 
	 * @author liushuohui
	 * @param resData
	 * @return
	 * @throws JSONException
	 */
	public static TopicDetailData parseGameTopicDetailData(final String resData) throws JSONException {
		JSONObject obj = new JSONObject(resData);

		if (Constants.DEBUG) {
			new Thread() {

				@Override
				public void run() {
					AppUtil.writeToFile(resData, "1101");
				}

			}.start();
		}

		return new TopicDetailData(obj);
	}

	// 启动页广告图
	public static SplashAdResult parseSplashAdData(String resData) {
		SplashAdResult result = new SplashAdResult();

		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (errorcode != 0)
				return result;

			if (!jsonObj.isNull("splashimg")) {
				String splashimg = jsonObj.getString("splashimg");
				result.splashimg = splashimg.equals("") ? null : splashimg;
			}

			if (!jsonObj.isNull("adtype")) {
				String adtype = jsonObj.getString("adtype");
				result.adtype = adtype.equals("") ? null : adtype;
			}
			if (!jsonObj.isNull("actshare")) {
				String actshare = jsonObj.getString("actshare");
				result.actshare = actshare.equals("") ? null : actshare;
			}

			String itemid = jsonObj.getString("itemid");
			result.itemid = itemid.equals("") ? null : itemid;

			result.addedcount = jsonObj.getString("addedcount");
			result.lotteryurl = jsonObj.getString("lotteryurl");
			result.startdialog = jsonObj.getString("startdialog");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static RecommendAppResult parseRecommendAppData(String resData) {

		RecommendAppResult result = new RecommendAppResult();
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (DcError.DC_OK != errorcode) {
				return result;
			}
			result.setTitle(jsonObj.getString(Constants.JSON_RECOMAPP_TITLE));
			JSONArray gamelist = jsonObj.getJSONArray(Constants.JSON_RECOM_APPLIST);
			if (gamelist != null) {
				result.setTotalcount(gamelist.length());
			}
			for (int i = 0; i < gamelist.length(); i++) {
				JSONObject object = gamelist.getJSONObject(i);
				RecommendAppItemInfo itemInfo = new RecommendAppItemInfo(object.getString(Constants.JSON_RECOMAPP_ITEMNAME), object.getString(Constants.JSON_RECOMAPP_ITEMICON),
						object.getString(Constants.JSON_RECOMAPP_ITEMURL));
				result.getAppList().add(itemInfo);
			}
		} catch (JSONException e) {
			result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
			result.setErrorString("Json Parser Error");
		}
		return result;
	}

	public static SmscentersResult parseSmscenters(String resData) {
		SmscentersResult result = new SmscentersResult();
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (DcError.DC_OK != errorcode) {
				return result;
			}
			if (jsonObj.has(Constants.JSON_GET_SMSC_CNMOBILE)) {
				result.setCnMobileNum(jsonObj.getString(Constants.JSON_GET_SMSC_CNMOBILE));
			}
			if (jsonObj.has(Constants.JSON_GET_SMSC_CNUNICOM)) {
				result.setCnUnicomNum(jsonObj.getString(Constants.JSON_GET_SMSC_CNUNICOM));
			}
			if (jsonObj.has(Constants.JSON_GET_SMSC_CNTELECOM)) {
				result.setCnTelecomNum(jsonObj.getString(Constants.JSON_GET_SMSC_CNTELECOM));
			}
			if (jsonObj.has(Constants.JSON_GET_SMSC_COMMONNUM)) {
				result.setCommonNum(jsonObj.getString(Constants.JSON_GET_SMSC_COMMONNUM));
			}
		} catch (JSONException e) {
			result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
			result.setErrorString("Json Parser Error");
		}
		return result;
	}

	// 1400
	public static HomeRecResult parseHomeRecData(String resData) {

		HomeRecResult result = new HomeRecResult();
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (DcError.DC_OK != errorcode) {
				return result;
			}
			JSONArray gamelist = jsonObj.getJSONArray("gamelist");
			for (int i = 0; i < gamelist.length(); i++) {
				JSONObject obj = gamelist.getJSONObject(i);
				String gameid = "";
				String gamename = "";
				String pkgname = "";
				String iconurl = "";
				String downloadurl = "";
				long pkgsize = 0;
				String versionname = "";
				int versioncode = 0;
				String action = "";
				boolean needlogin = false;

				if (!obj.isNull("gameid")) {
					gameid = obj.getString("gameid");
				}
				if (!obj.isNull("gamename")) {
					gamename = obj.getString("gamename");
				}
				if (!obj.isNull("pkgname")) {
					pkgname = obj.getString("pkgname");
				}
				if (!obj.isNull("pkgsize")) {
					pkgsize = obj.getLong("pkgsize");
				}
				if (!obj.isNull("gameicon")) {
					iconurl = obj.getString("gameicon");
				}
				if (!obj.isNull("startaction")) {
					action = obj.getString("startaction");
				}
				if (!obj.isNull("needlogin")) {
					needlogin = obj.getString("needlogin").equals("0") ? false : true;
				}
				if (!obj.isNull("downloadurl")) {
					downloadurl = obj.getString("downloadurl");
				}
				if (!obj.isNull("versionname")) {
					versionname = obj.getString("versionname");
				}
				if (!obj.isNull("versioncode")) {
					versioncode = obj.getInt("versioncode");
				}
				SearchItem gameInfo = new SearchItem(gameid, gamename, Float.valueOf("0"), StringUtil.valueOf("0"), pkgname, iconurl, downloadurl, pkgsize, versionname, versioncode, 0, action,
						needlogin, false);

				result.getRecGames().add(gameInfo);
			}

			JSONArray infolist = jsonObj.getJSONArray("infolist");
			for (int i = 0; i < infolist.length(); i++) {
				JSONObject object = infolist.getJSONObject(i);
				Infos infos = result.new Infos();
				infos.setInfoId(object.getString("infoid"));
				infos.setInfoType(object.getString("infotype"));
				infos.setInfoContent(object.getString("infocontent"));
				if (!object.isNull("gameid")) {
					infos.setGameid(object.getString("gameid"));
				}
				result.getRecInfos().add(infos);
			}
		} catch (JSONException e) {
			result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
			result.setErrorString("Json Parser Error");
		}
		return result;
	}

	public static HomeDailyResult parseHomeDailyData(String resData) {

		HomeDailyResult result = new HomeDailyResult();
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (DcError.DC_OK != errorcode) {
				return result;
			}
			result.setOccnumber(jsonObj.getString("occnumber"));
			result.setInterval(jsonObj.getString("interval"));
			result.setDialogtype(jsonObj.getString("dialogtype"));
			result.setTitle(jsonObj.getString("title"));
			result.setPicurl(jsonObj.getString("picurl"));
			result.setContent(jsonObj.getString("content"));
			result.setSkiptype(jsonObj.getString("skiptype"));
			if (!jsonObj.isNull("actid")) {
				result.setActid(jsonObj.getString("actid"));
			}
			if (!jsonObj.isNull("gameid")) {
				result.setGameid(jsonObj.getString("gameid"));
			}
			JSONArray gamelist = jsonObj.getJSONArray("gamelist");
			for (int i = 0; i < gamelist.length(); i++) {
				JSONObject object = gamelist.getJSONObject(i);
				GameInfo gameInfo = new GameInfo();
				gameInfo.setGameId(object.getString("gameid"));
				gameInfo.setGameName(object.getString("gamename"));
				gameInfo.setPkgname(object.getString("pkgname"));
				gameInfo.setSize(object.getString("pkgsize"));
				gameInfo.setIconUrl(object.getString("gameicon"));
				gameInfo.setDownloadurl(object.getString("downloadurl"));
				gameInfo.setGameversion(object.getString("versionname"));
				gameInfo.setGameversioncode(Integer.valueOf(object.getString("versioncode")));
				result.getDailyGameInfos().add(gameInfo);
			}
		} catch (JSONException e) {
			result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
			result.setErrorString("Json Parser Error");
		}
		return result;
	}

	/**
	 * 
	 * @param res
	 * @return
	 */
	public static RecommandKeyword parseRecomGameAndKeyword(String res) {

		RecommandKeyword rk = new RecommandKeyword();

		try {
			JSONObject json = new JSONObject(res);

			int errorcode = json.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = json.getString(Constants.JSON_ERROR_MSG);
			String tag = json.getString(Constants.JSON_TAG);

			rk.setTag(tag);
			rk.setErrorCode(errorcode);
			rk.setErrorString(errorStr);

			if (json.has("gameinfo")) {
				try {
					JSONObject gameInfo = json.getJSONObject("gameinfo");

					rk.recomGame = new SearchItem();

					rk.recomGame.setGameId(gameInfo.getString("gameid"));
					rk.recomGame.setGameName(gameInfo.getString("gamename"));
					rk.recomGame.setIconUrl(gameInfo.getString("gameicon"));

					rk.recomGame.setPackageName(gameInfo.getString("pkgname"));
					rk.recomGame.setPackageSize(Long.parseLong(gameInfo.getString("pkgsize")));
					rk.recomGame.setDownloadUrl(gameInfo.getString("downloadurl"));
					rk.recomGame.setAction(gameInfo.getString("startaction"));
					String needLogin = gameInfo.getString("needlogin");
					rk.recomGame.setNeedLogin("1".equals(needLogin) ? true : false);
					rk.recomGame.setVersion(gameInfo.getString("gameversion"));
					rk.recomGame.setVersionInt(StringUtil.parseInt(gameInfo.getString("gameversionint")));
					rk.recomGame.setDownloadTimes(Integer.parseInt(gameInfo.getString("downloadtimes")));

					String coming = gameInfo.getString("comingsoon");
					if (coming.equals("1")) {
						rk.recomGame.setPendingOnLine(false);
					} else if (coming.equals("2")) {
						rk.recomGame.setPendingOnLine(true);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			JSONArray jsonKeywords = json.getJSONArray("keywords");
			for (int i = 0; i < jsonKeywords.length(); i++) {
				rk.addKeyword(jsonKeywords.getString(i));
			}

		} catch (Exception e) {
			e.printStackTrace();

			rk.setErrorCode(DcError.DC_Error);
		}

		return rk;
	}

	/**
	 * 
	 * @param res
	 * @return
	 */
	public static GameRelatedResult parseGameRelatedInfo(String res) {

		GameRelatedResult result = new GameRelatedResult();

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(res);

			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
			String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
			String tag = jsonObj.getString(Constants.JSON_TAG);

			result.setTag(tag);
			result.setErrorCode(errorcode);
			result.setErrorString(errorStr);

			if (DcError.DC_OK != errorcode) {
				return result;
			}

			JSONArray jsonArray = jsonObj.getJSONArray(Constants.JSON_INFOS);
			ArrayList<GameRelatedInfo> infolist = new ArrayList<GameRelatedInfo>(jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject itemObj = jsonArray.getJSONObject(i);
				GameRelatedInfo itemInfo = new GameRelatedInfo();
				itemInfo.setInfoId(itemObj.getString(Constants.JSON_INFO_ID));
				itemInfo.setInfoType(itemObj.getString(Constants.JSON_INFO_TYPE));
				itemInfo.setInfocontent(itemObj.getString(Constants.JSON_INFO_CONTENT));
				infolist.add(itemInfo);
			}
			if (jsonObj.has(Constants.JSON_PKGNAME)) {
				result.setPkgName(jsonObj.getString(Constants.JSON_PKGNAME));
			}
			result.setGamesList(infolist);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

    /************************************************************************************************/

    /**
     * BmUser
     */
    public static BMUserLoginResult parserBMUserLoginResult(String res){

        BMUserLoginResult result = null;
        Gson gson = new Gson();
        try{
            result = gson.fromJson(res,BMUserLoginResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    /**
     * tag 241 获取搜索关键字
     */
    public static BaseResult parseBMKeywords(String resData) {
        KeywordsList keywordsList = KeywordsList.getInstance();
        try {

            JSONArray outter = new JSONArray(resData);
            int length = outter.length();
            List<String> dataList = new ArrayList<String>(length);
            for (int i = 0; i < length; i++) {
                String keyword = outter.getString(i);
                dataList.add(keyword);
            }
            keywordsList.setKeywords(dataList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keywordsList;
    }

    public static BaseResult parseBMProvinceList(String res){
        BMProvinceListResult result = new BMProvinceListResult();
        try{
            JSONArray jsonArray = new JSONArray(res);
            for(int i=0;i<jsonArray.length();i++){
                String item = jsonArray.getString(i);
                result.addItem(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public static BaseResult parseBMProductInfo(String res){
        BMProductInfoResult result = new BMProductInfoResult();
        try{
            Gson gson = new Gson();

            result = gson.fromJson(res,BMProductInfoResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    /**
     * tag 242 根据关键字搜索游戏
     */
    public static BaseResult parseBMSearchProducts(String resData) {
        BMSearchResult searchResult = new BMSearchResult();
        try {


                JSONObject outterObj = new JSONObject(resData);
                JSONArray jsonList = outterObj.getJSONArray(Constants.BM_JSON_DATA_LIST);
                int length = jsonList.length();
                ArrayList<BMSearchResult.BMSearchData> dataList = new ArrayList<BMSearchResult.BMSearchData>(length);
                for (int i = 0; i < length; i++) {
                    try{

                        Gson gson = new Gson();
                        BMSearchResult.BMSearchData bmsd = gson.fromJson(jsonList.getJSONObject(i).toString(),BMSearchResult.BMSearchData.class);

                        dataList.add(bmsd);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }

            searchResult.setDataList(dataList);
            searchResult.setTotal(outterObj.getInt(Constants.JSON_SEARCH_TOTAL_COUNT));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }
}
