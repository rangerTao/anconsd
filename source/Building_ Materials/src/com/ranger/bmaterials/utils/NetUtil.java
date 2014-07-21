package com.ranger.bmaterials.utils;

import java.util.List;

import android.util.Log;
import android.util.SparseArray;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONManager;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.net.IHttpInterface;
import com.ranger.bmaterials.net.INetListener;
import com.ranger.bmaterials.net.NetManager;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.ui.MineMsgItemInfo;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class NetUtil implements INetListener {
    /**
     * 单例
     */
    private static NetUtil mInstance;
    private IHttpInterface mHttpIml;

    /**
     * 存储Listener
     */
    private SparseArray<IRequestListener> mObservers = new SparseArray<IRequestListener>();

    /**
     * 当前requestId
     */
    private int mCurrentRequestId;

    /**
     * 构造器
     */
    private NetUtil() {
        mHttpIml = NetManager.getHttpConnect();
    }

    public static NetUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetUtil();
        }

        return mInstance;
    }

    // 取消请求
    public void cancelRequestById(int requestId) {
        mHttpIml.cancelRequestById(requestId);
    }

    /**
     * ` 激活
     */
    public int requestActive(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_APP_ACTIVE, JSONManager.getJsonBuilder().buildActiveString(), this);

        // addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * ` 检查更新
     */
    public int requestCheckUpdate(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_CHECK_UPDATE, JSONManager.getJsonBuilder().buildCheckUpdateString(), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * ` 用户反馈
     */
    public int requestFeedback(String userid, String sessionid, String content, String contact, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_FEEDBACK, JSONManager.getJsonBuilder().buildFeedbackString(userid, sessionid, content, contact), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * ` 修改昵称
     */
    public int requestChangeNickname(String userid, String sessionid, String nickname, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_CHANGE_NICKNAME, JSONManager.getJsonBuilder().buildChangeNicknameString(userid, sessionid, nickname),
                this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * ` 手机号一键注册登陆
     */
    public int requestFastPhoneRegister(String message, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_FAST_PHONE_REGESTER, JSONManager.getJsonBuilder().buildFastPhoneRegisterString(message), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * ` 用户名注册
     */
    public int requestUserNameRegister(String username, String password, String nickname, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_USERNAME_REGISTER,
                JSONManager.getJsonBuilder().buildUserNameRegisterString(username, password, nickname), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 百度SAPI登录
     */
    public int requestBaiduSAPI(String username, String bduid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_BAIDU_SAPI, JSONManager.getJsonBuilder().buildBaiduSAPI(username, bduid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 用户登录
     */
    public int requestCheckUserLogin(String userid, String sessionid, IRequestListener observer) {

        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_CHECK_USER_LOGIN, JSONManager.getJsonBuilder().buildCheckUserLoginString(userid, sessionid), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 用户注销登录
     */
    public int requestUserUnlogin(String userid, String sessionid, IRequestListener observer) {

        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_USER_UNLOGIN, JSONManager.getJsonBuilder().buildUserUnloginString(userid, sessionid), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 更改密码
     */
    public int requestChangePwd(String oldpwd, String newpwd, String userid, String sessionid, IRequestListener observer) {

        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_CHANGE_PWD, JSONManager.getJsonBuilder().buildChangePwdString(oldpwd, newpwd, userid, sessionid), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    private void addObserver(int key, IRequestListener observer) {
        mObservers.put(key, observer);
    }

    private void removeObserver(int key) {
        mObservers.remove(key);
    }

    /**
     * 绑定、验证、更改绑定手机号
     */
    public int requestBindPhone(String phonenum, String verifyCode, int requestType, String userid, String sessionid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_BIND_PHONE,
                JSONManager.getJsonBuilder().buildBindPhoneString(phonenum, verifyCode, requestType, userid, sessionid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取金币
     */
    public int requestGetCoin(String userid, String sessionid, int coinnum, int requestType, String gameid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_COIN,
                JSONManager.getJsonBuilder().buildGetCoinString(userid, sessionid, coinnum, requestType, gameid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 忘记密码
     */
    public int requestForgetPwd(String username, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_FORGET_PWD, JSONManager.getJsonBuilder().buildForgetPwd(username), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏-推荐-数据
     */
    public int requestGameRecommendData(IRequestListener observer) {
        // TODO
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_RECOMMEND_DATA, JSONManager.getJsonBuilder().buildGameRecommendRequestBody(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏-排行-数据
     */
    public int requestGameHotData(IRequestListener observer) {
        // TODO
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_HOT_DATA, JSONManager.getJsonBuilder().buildGameHotRequestBody(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏-最新-数据
     */
    public int requestGameNewData(IRequestListener observer) {
        // TODO
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_NEW_DATA, JSONManager.getJsonBuilder().buildGameNewRequestBody(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏-分类-数据
     */
    public int requestGameClassData(IRequestListener observer) {
        // TODO
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_CLASS_DATA, JSONManager.getJsonBuilder().buildGameClassRequestBody(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * TAG: 1100 -- 获取专题列表
     *
     * @param observer
     * @param page      当前页码 >> 从1起始。 0代表全部
     * @param pagecount 每页请求下行的数据数目
     * @return
     * @author liushuohui
     */
    public int requestGameTopicsData(IRequestListener observer, int page, int pagecount) {
        String body = JSONManager.getJsonBuilder().buildGameTopicRequestBody(page, pagecount);

        if (Constants.DEBUG) {
            Log.d("TAG_1100", "REQUEST BODY >> " + body);
        }

        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_TOPICS, body, this);

        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    /**
     * TAG: 1101 -- 获取专题详情，包含了专题详情及游戏列表
     *
     * @param observer
     * @param id
     * @return
     */
    public int requestGameTopicDetailData(IRequestListener observer, String id, int count) {
        String body = JSONManager.getJsonBuilder().buildGameTopicDetailRequestBody(id, count);

        if (Constants.DEBUG) {
            Log.d("TAG_1101", "REQUEST BODY >> " + body);
        }

        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_TOPIC_DETAIL, body, this);

        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    /**
     * TAG: 1102 -- 获取专题详情更多游戏列表
     *
     * @param observer
     * @param id
     * @return
     */
    public int requestGameTopicDetailMoreListData(IRequestListener observer, String id, int page, int count) {
        String body = JSONManager.getJsonBuilder().buildGameTopicDetailMoreListBody(id, page, count);

        if (Constants.DEBUG) {
            Log.d("TAG_1102", "REQUEST BODY >> " + body);
        }

        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_TOPIC_DETAIL_MORE_LIST, body, this);

        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    /**
     * 获取游戏-游戏-更多
     */
    public int requestGameMoreData(String moretype, int page, int count, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_MORE_DATA, JSONManager.getJsonBuilder().builderGameMoreRequestBody(moretype, page, count), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取获取全部网游列表及网游分类
     */
    public int requestOnlineGamesAndTypes(int page, int count, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_ONLINE_GAMES_AND_TYPES,
                JSONManager.getJsonBuilder().builderOnlineGamesAndTypesRequestBody(page, count), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取单个分类(网游或单机)游戏列表
     */

    public int requestSingleClassGames(String gametype, String gametypenumber, int page, int count, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SINGLE_CLASS_GAMES,
                JSONManager.getJsonBuilder().builderSingleClassGamesRequestBody(gametype, gametypenumber, page, count), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏详情
     */

    public int requestGameDetailSummary(String userid, String session, String gameid, String pkgname, String versionCode, String versionName, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_DETAIL_AND_SUMMARY,
                JSONManager.getJsonBuilder().buildGameDetailAndSummaryRequestBody(userid, session, gameid, pkgname, versionCode, versionName), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏详情-攻略列表
     */
    public int requestGameDetailGuide(String gameid, String userid, String sessionid, String pkgname, String versionname, String versioncode, int page, int count, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_DETAIL_GUIDE,
                JSONManager.getJsonBuilder().buildGameDetailGuideRequestBody(gameid, userid, sessionid, pkgname, versionname, versioncode, page, count), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏详情-攻略详情
     */

    public int requestGameGuideDetail(String userid, String sessionid, String guideid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_DETAIL_GUIDE_DETAIL,
                JSONManager.getJsonBuilder().buildGameGuideDetailRequestBody(userid, sessionid, guideid), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    /**
     * 获取游戏详情-评论列表
     */
    public int requestGameDetailComment(String gameid, String pkgname, String versionname, String versioncode, int page, int count, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GAME_DETAIL_COMMENT,
                JSONManager.getJsonBuilder().buildGameDetailCommentRequestBody(gameid, pkgname, versionname, versioncode, page, count), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    public int requestPublishCommentStar(String gameid, String userid, String sessionid, String cmtcontent, float star, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_PUBLISH_COMMENT_STAR,
                JSONManager.getJsonBuilder().buildPublishCommentStarRequestBody(gameid, userid, sessionid, cmtcontent, star), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    /**
     * 获取我的消息
     */
    public int requestMyMessage(String userid, String sessionid, int msgtype, int pageindex, int pagenum, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_MY_MESSAGE,
                JSONManager.getJsonBuilder().buildMyMessage(userid, sessionid, msgtype, pageindex, pagenum), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取我收藏的游戏
     */
    public int requestCollectionGame(String userid, String sessionid, int pageindex, int pagenum, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_COLLECTION_GAME,
                JSONManager.getJsonBuilder().buildGameCollection(userid, sessionid, pageindex, pagenum), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取我收藏的攻略
     */
    public int requestCollectionGuide(String userid, String sessionid, int pageindex, int pagenum, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_COLLECTION_GUIDE,
                JSONManager.getJsonBuilder().buildGuideCollection(userid, sessionid, pageindex, pagenum), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 删除我的消息
     */
    public int requestDeleteMessage(String userid, String sessionid, int msgtype, List<MineMsgItemInfo> msgarray, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_DEL_SETREAD_MESSAGE,
                JSONManager.getJsonBuilder().buildDeleteMessage(userid, sessionid, msgtype, msgarray), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取消息详情
     */
    public int requestMessageDetail(String userid, String sessionid, String msgid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_MESSAGE_DETAIL, JSONManager.getJsonBuilder().buildMessageDetail(userid, sessionid, msgid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取我的动态
     */
    public int requestDynamicData(String userid, String sessionid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_MY_DYNAMIC_DATA, JSONManager.getJsonBuilder().buildDynamicData(userid, sessionid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取我安装过的游戏
     */
    public int requestInstalledGames(String userid, String sessionid, int pageindex, int pagenum, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_INSTALLED_GAME,
                JSONManager.getJsonBuilder().buildInstalledGames(userid, sessionid, pageindex, pagenum), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 注册我安装过的游戏
     */
    public int requestRegisterGames(String userid, String sessionid, List<String> gameids, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_REGISTER_INSTALLED_GAME, JSONManager.getJsonBuilder().buildRegisterGames(userid, sessionid, gameids),
                this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 注册启动联运游戏
     */
    public int requestRegisterStartGame(String gameid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_REGISTER_START_GAME, JSONManager.getJsonBuilder().buildRegisterStartGame(gameid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 统计开始下载游戏
     */
    public int requestStartDownloadGame(String gameid, String gamename, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_START_DOWNLOAD_GAME, JSONManager.getJsonBuilder().buildStartDownloadGame(gameid, gamename), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 统计成功下载游戏
     */
    public int requestFinishDownloadGame(String gameid, String gamename, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_START_DOWNLOAD_GAME, JSONManager.getJsonBuilder().buildFinishDownloadGame(gameid, gamename), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 收藏、取消收藏游戏和攻略
     */
    public int requestCollectionAction(String userid, String sessionid, int msgtype, int msgsubtype, String targetid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_COLLECTION_ACTIONS,
                JSONManager.getJsonBuilder().buildCollectionAction(userid, sessionid, msgtype, msgsubtype, targetid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * tag 213:获取开服列表
     *
     * @param userId    用户id
     * @param sessionId 登录sessionid
     * @param page      请求第几页
     * @param pageSize  每页多少条数据
     * @param observer
     * @return
     */
    public int requestForSnapNumberList(String userId, String sessionId, int page, int pageSize, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SNAP_NUMBER_LIST, JSONManager.getJsonBuilder().buildSnapNumberList(userId, sessionId, page, pageSize),
                this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 抢号详情 tag = 211
     *
     * @param userId
     * @param sessionId
     * @param gameId
     * @param grabId
     * @param observer
     * @return
     */
    public int requestForSnapNumberDetail(String userId, String sessionId, String gameId, String grabId, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SNAP_NUMBER_DETAIL,
                JSONManager.getJsonBuilder().buildSnapNumberDetail(userId, sessionId, gameId, grabId), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 抢号 tag = 212
     *
     * @param userId
     * @param sessionId
     * @param gameId
     * @param grabId
     * @param observer
     * @return
     */
    public int requestForSnapNumber(String userId, String sessionId, String gameId, String grabId, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SNAP_NUMBER, JSONManager.getJsonBuilder().buildSnapNumber(userId, sessionId, gameId, grabId), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 开服 tag = 213
     *
     * @param page
     * @param pageSize
     * @param observer
     * @return
     */
    public int requestForOpenServersList(int page, int pageSize, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_OPEN_SERVER_LIST, JSONManager.getJsonBuilder().buildOpenServerList(page, pageSize), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 开服详情 tag = 214
     *
     * @param gameId
     * @param openServerId
     * @param observer
     * @return
     */
    public int requestForOpenServerDetail(String gameId, String openServerId, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_OPEN_SERVER_DETAIL, JSONManager.getJsonBuilder().buildOpenServerDetail(gameId, openServerId), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 活动 tag = 215
     *
     * @param page
     * @param pageSize
     * @param observer
     * @return
     */
    public int requestForActivitiesList(int page, int pageSize, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_ACTIVITIES_LIST, JSONManager.getJsonBuilder().buildActivitiesList(page, pageSize), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 测评 tag = 217
     *
     * @param page
     * @param pageSize
     * @param observer
     * @return
     */
    public int requestForAppraisalList(int page, int pageSize, int appiaisaltype, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_APPIAISAL_LIST, JSONManager.getJsonBuilder().buildAppiaisalList(page, pageSize, appiaisaltype), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 活动详情 tag = 216
     *
     * @param gameId
     * @param activityId
     * @param observer
     * @return
     */
    public int requestForActivityDetail(String gameId, String activityId, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_ACTIVITY_DETAIL, JSONManager.getJsonBuilder().buildActivityDetail(gameId, activityId), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 测评详情 tag = 218
     *
     * @param infosid
     * @param observer
     * @return
     */
    public int requestForAppraisalDetail(String infosid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_APPIAISAL_DETAIL, JSONManager.getJsonBuilder().buildAppiaisalDetail(infosid), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取搜索关键字 tag = 241
     *
     * @param observer
     * @return
     */
    public int requestForKeywords(int count, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_KEYWORDS, JSONManager.getJsonBuilder().buildKeywords(count), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 根据关键字搜索游戏 tag = 242
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @param observer
     * @return
     */
    public int requestForSearch(String keyword, int page, int pageSize, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SEARCH,
                JSONManager.getJsonBuilder().buildSearch(keyword, String.valueOf(page), String.valueOf(pageSize)), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * @param packages
     * @param observer
     * @return
     */
    public int requestForWhiteList(List<String> packages, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_WHITE_LIST, JSONManager.getJsonBuilder().buildWhiteList(packages), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * @param apps
     * @param observer
     * @return
     */
    public int requestForUpdateGames(List<InstalledAppInfo> apps, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_UPDATE_GAMES, JSONManager.getJsonBuilder().buildUpdateGames(apps), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取设备下载游戏列表 tag = 201
     *
     * @param deviceId
     * @param observer
     * @return
     */
    public int requestForDownloadedGames(String deviceId, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_DOWNLOADED_GAMES, JSONManager.getJsonBuilder().buildGetDownloadedGames(deviceId), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏登录token tag = 142
     *
     * @param deviceId
     * @param gameIds
     * @param observer
     * @return
     */
    public int requestForUploadDownloadedGames(String deviceId, List<String> gameIds, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, JSONManager.getJsonBuilder().buildUploadDownloadedGames(deviceId, gameIds),
                this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取游戏登陆token
     *
     * @param userid
     * @param session_id
     * @param gameid
     * @param observer
     * @return
     */
    public int requestForGamesLoginToken(String userid, String session_id, String gameid, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_GAME_LOGIN_TOKEN, JSONManager.getJsonBuilder().buildGetGameLoginToken(userid, session_id, gameid),
                this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;

    }

    /**
     * 获取高速下载信息
     *
     * @param
     * @param observer
     * @return
     */
    public int requestSpeedDownloadInfos(String gameids, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SPEEDDOWNLOAD_INFOS, JSONManager.getJsonBuilder().buildSpeedDownloadGameids(gameids), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;

    }

    /**
     * @param
     * @param observer
     * @return
     */
    public int requestExchangeHistoryDetail(String userid, String sessionid, int index, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_EXCHANGE_HISTORY_DETAIL,
                JSONManager.getJsonBuilder().buildGetExchangeHistoryDetailString(userid, sessionid, index), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;

    }

    // 争霸赛
    public int requestCompetition(int pageindex, int pagenum, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_COMPETITION, JSONManager.getJsonBuilder().buildCompetition(pageindex, pagenum), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;

    }

    // 必玩
    public int requestMustPlayGames(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_MUST_PLAY_GAMES, JSONManager.getJsonBuilder().buildMustPlayGamesString(), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;
    }

    /**
     * 获取首页数据
     */
    public int requestHomePageData(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_HOME_PAGE_DATA, JSONManager.getJsonBuilder().buildHomePageRequestBody(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    // 首页加载更多
    public int requestHomeMoreData(int pageindex, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_HOME_MORE, JSONManager.getJsonBuilder().buildHomeMore(pageindex), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;

    }

    /**
     * 获取推荐应用
     */
    public int requestRecommendApp(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_RECOMMEND_APP, JSONManager.getJsonBuilder().buildRecommendApp(), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取短信中心号码
     */
    public int requestSmscentersNumber(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_SMSCENTERS, JSONManager.getJsonBuilder().buildSmscentersReq(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    // 启动页广告
    public int requestSplashAdData(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_SPLASH_AD, JSONManager.getJsonBuilder().buildSplashAd(), this);
        addObserver(mCurrentRequestId, observer);

        return mCurrentRequestId;

    }

    /**
     * 获取首页为您推荐 1400
     */
    public int requestHomeRecommend(IRequestListener observer, List<String> gameIds) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_HOME_RECOMMEND, JSONManager.getJsonBuilder().buildHomeRecommend(gameIds), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取首页启动弹窗
     */
    public int requestHomeStartDialog(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_HOME_START_DIALOG, JSONManager.getJsonBuilder().buildHomeStartDialog(), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    /**
     * 获取搜索推荐游戏和关键字
     *
     * @param key
     * @param observer
     * @return
     */
    public int requestRecomKeywords(String key, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_KEYWORDS + key, Constants.NET_TAG_GET_RECOM_KEYWORDS, JSONManager.getJsonBuilder().buildRecomGameAndKeywords(key), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    public int requestRelatedGameInfo(String pkgname, IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest(Constants.GAMESEARCH_SERVER, Constants.NET_TAG_GET_RELATED_GAMEINFO, JSONManager.getJsonBuilder().buildRelatedGameInfo(pkgname), this);
        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    // ----------------------------------INetListener接口实现---------------------------------------------
    @Override
    public void onNetResponse(int requestTag, BaseResult responseData, int requestId) {
        // TODO Auto-generated method stub
        IRequestListener _listener = mObservers.get(requestId);
        if (_listener != null) {
            responseData.setRequestID(requestId);
            _listener.onRequestSuccess(responseData);
        }
        removeObserver(requestId);
    }

    @Override
    public void onDownLoadStatus(DownLoadStatus status, int requestId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDownLoadProgressCurSize(long curSize, long totalSize, int requestId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNetResponseErr(int requestTag, int requestId, int errorCode, String msg) {
        // TODO Auto-generated method stub
        IRequestListener _listener = (IRequestListener) mObservers.get(requestId);
        if (_listener != null) {
            _listener.onRequestError(requestTag, requestId, errorCode, msg);
        }
        removeObserver(requestId);
    }

    // ----------------------------------------END----------------------------------------------

    /**
     * 请求回调接口
     */
    public interface IRequestListener {

        void onRequestSuccess(BaseResult responseData);

        void onRequestError(int requestTag, int requestId, int errorCode, String msg);

    }

    /*****************************************************************************************************/

    // 命名空间
    String nameSpace = "http://admin.jc.net.cn:8092";

    // EndPoint
    String endPoint = "http://admin.jc.net.cn:8092/JcMobileService";
    // SOAP Action
    String soapAction = "http://admin.jc.net.cn:8092/";

    /**
     * 用户登录
     */
    public int requestUserLogin(String username, String password, IRequestListener observer) {

        // 调用的方法名称
        String methodName = "checkLogin";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("info", "{\"username\":\"" + username + "\",password:\"" + password + "\"}");

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(endPoint);


        mCurrentRequestId = transport.hashCode();

        doWebService(Constants.NET_TAG_USER_LOGIN, mCurrentRequestId, transport, soapAction, envelope, observer);

        return mCurrentRequestId;
    }

    /**
     * 手机号验证码
     */
    public int requestPhoneVerifyCode(String phonenum, int flag, IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getSmsCode";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("info", "{\"telephone\":\"" + phonenum + "\"}");

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(endPoint);


        mCurrentRequestId = transport.hashCode();

        doWebService(Constants.NET_TAG_GET_PHONE_VERIFYCODE, mCurrentRequestId, transport, soapAction, envelope, observer);

        return mCurrentRequestId;
    }

    /**
     * 手机号注册
     */
    public int requestPhoneumRegister(String username, String password, String telephone, String verifyCode, IRequestListener observer) {
        // 调用的方法名称
        String methodName = "addUser";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("info", "{\"username\":\"" + username + ",\"password:\"" + password + ",\"telephone:\"" + telephone + "\",\"code:\"" + verifyCode + "\"}");

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(endPoint);


        mCurrentRequestId = transport.hashCode();

        doWebService(Constants.NET_TAG_GET_PHONE_VERIFYCODE, mCurrentRequestId, transport, soapAction, envelope, observer);

        return mCurrentRequestId;
    }

    public void doWebService(int tag, int mCurrentRequestId, HttpTransportSE transport, String action, SoapSerializationEnvelope envelope, IRequestListener observer) {

        try {
            // 调用WebService
            transport.call(action, envelope);

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            String result = object.getProperty(0).toString();

            BaseResult baseResult = JSONParser.parserBMUserLoginResult(result);
            baseResult.setTag(tag + "");

            Log.e("TAG","webservice result " + result);

            observer.onRequestSuccess(baseResult);

        } catch (Exception e) {
            e.printStackTrace();
            observer.onRequestError(tag, mCurrentRequestId, 1001, "error");
        }

    }

}
