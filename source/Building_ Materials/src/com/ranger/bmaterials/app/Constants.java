package com.ranger.bmaterials.app;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.R.integer;
import android.os.Environment;

import com.ranger.bmaterials.mode.KeywordsList;

public final class Constants {

	private static final String FORMATTER_DATE_STRING = "yyyy-MM-dd";
	public static final SimpleDateFormat FORMATER_DATE_FORMAT = new SimpleDateFormat(FORMATTER_DATE_STRING);

	public static int ACT_CANCELLED = 0xFFEE;
	public static int ACT_FAILED = 0xFFFF;
	public static int ACT_SUCCESS = 0xFF00;

	public static final boolean DEBUG = true;

	public static final int SHOW_GUIDE_ONLY_FIRST_INSTALLED = 1;// 只有首次安装有引导页
	public static final int SHOW_GUIDE_NONE = 2;// 首次安装和覆盖安装都没有引导页
	public static final int SHOW_GUIDE_ALL = 3;// 首次安装和覆盖安装都有引导页
	public static final int SHOW_GUIDE_TYPE = SHOW_GUIDE_ALL;// 本次版本是否有新功能引导页
																// 区别于首次安装

	public static boolean isFirstStartWhenVersionChanged;// 当版本号变化时的首次启动
	public static boolean isFirstInstalled;// 应用第一次启动 不包含覆盖安装时的情况
	public static boolean speedServiceStarted = false;// 高速下载服务是否已经启动

	public static final boolean speed_download_enable = false;// 高速下载开关

	public static final boolean EXCLUDE_ONLINE_GAME = false;

	public static final String GAME_TING_LOG = "/gameting/crashlog/";

	public final static String APP_ID = "760313";
	public final static String API_KEY = "NHRy2FIdi1c1psse9pTCe0Ru";
	public final static String SECRIT_KEY = "x9ardLD1ro3PGBZgVkFSYWE7vKQsFisr";

	public static final int keywordCount = 50;
	public static final int IMAGE_LOADER_MAX_MEMCACHE = 512 * 1024;
	public static KeywordsList keywordsListForSearch;
	public static boolean isFirstDownload = true;
	public static HashMap<String, Integer> mergeFailedCountMap = new HashMap<String, Integer>();

	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	public static final String PHOTO_LOCAL_FILE = "headphoto.jpg";
//	public static final String PHOTO_UNCOMMIT_FILE = "unused_photo.jpg";
//	public static final String TEMPFILE_NAME = "headphoto_temp";
	public static final String IMAGE_PATH = SDCARD + Constants.IMAGE_CACHE;
    /**
     * String constants
     */
	/** 程序设置 */
	public static final String SETTINGS_PREFERENCE = "settings_preference";
	/** 设备地址 */
	public static final String MACADDRESS = "macaddress";

	/** 服务器地址 */
//	public static final String GAMESEARCH_SERVER = "http://apitest.duoku.com:9090/gamehall/service2";
//    public static final String GAMESEARCH_KEYWORDS = "http://itest.client.duoku.com:10086/atcmlt/bookNotice.php?channel=game&key=";

	public static final String GAMESEARCH_SERVER =	"http://gamehall.m.duoku.com/gamehall/service2";
    public static final String GAMESEARCH_KEYWORDS = "http://api.m.duoku.com/atcmlt/bookNotice.php?channel=game&key=";
		
	public static final String ONLINE_GAME = "1";
	public static final String OFFLINE_GAME = "2";

    /** tag定义：激活 */
    public static final int NET_TAG_GET_PROVINCE = 100;


	/** tag定义：激活 */
	public static final int NET_TAG_APP_ACTIVE = 100;

	/** tag定义：用户名注册 */
	public static final int NET_TAG_USERNAME_REGISTER = 101;

	/** tag定义：得到手机验证码 */
	public static final int NET_TAG_GET_PHONE_VERIFYCODE = 102;

	/** tag定义：手机号注册 */
	public static final int NET_TAG_PHONENUM__REGISTER = 103;

	/** tag定义：更改昵称 */
	public static final int NET_TAG_CHANGE_NICKNAME = 104;

	/** tag定义：手机号一键注册登陆 */
	public static final int NET_TAG_FAST_PHONE_REGESTER = 105;

	/** tag定义：用户登录 */
	public static final int NET_TAG_USER_LOGIN = 111;

	/** tag定义： 百度SAPI登录 */
	public static final int NET_TAG_BAIDU_SAPI = 112;

	/** tag定义：检查登录状态 */
	public static final int NET_TAG_CHECK_USER_LOGIN = 121;

	/** tag定义：用户注销登录 */
	public static final int NET_TAG_USER_UNLOGIN = 131;

	/** tag定义：修改密码 */
	public static final int NET_TAG_CHANGE_PWD = 303;

	/** tag定义：检查更新 */
	public static final int NET_TAG_CHECK_UPDATE = 304;

	/** tag定义：绑定、验证、更改绑定手机号 */
	public static final int NET_TAG_BIND_PHONE = 301;

	/** tag定义：用户反馈 */
	public static final int NET_TAG_FEEDBACK = 302;

	/** tag定义：忘记密码 */
	public static final int NET_TAG_FORGET_PWD = 141;

	// 首页数据
	public static final int NET_TAG_HOME_PAGE_DATA = 206;

	// 首页加载更多
	public static final int NET_TAG_HOME_MORE = 205;

	// TODO 游戏-推荐
	public static final int NET_TAG_GAME_RECOMMEND_DATA = 220;

	// TODO 游戏-排行
	public static final int NET_TAG_GAME_HOT_DATA = 221;

	// TODO 游戏-最新
	public static final int NET_TAG_GAME_NEW_DATA = 222;

	// TODO 游戏-分类
	public static final int NET_TAG_GAME_CLASS_DATA = 227;

	// TODO 游戏-专题
	public static final int NET_TAG_GAME_TOPICS = 1100;

	// TODO 专题详情
	public static final int NET_TAG_GAME_TOPIC_DETAIL = 1101;

	// TODO 专题详情
	public static final int NET_TAG_GAME_TOPIC_DETAIL_MORE_LIST = 1102;

	// 游戏-更多
	public static final int NET_TAG_GAME_MORE_DATA = 226;

	// 获取网游列表及网游分类
	public static final int NET_TAG_ONLINE_GAMES_AND_TYPES = 224;

	// 获取单个类别的游戏列表
	public static final int NET_TAG_SINGLE_CLASS_GAMES = 228;

	// 获取游戏详情&简介
//	public static final int NET_TAG_GAME_DETAIL_AND_SUMMARY = 230;
	public static final int NET_TAG_GAME_DETAIL_AND_SUMMARY = 231;

	// 获取游戏详情-攻略
	public static final int NET_TAG_GAME_DETAIL_GUIDE = 232;

	// 获取游戏详情-攻略详情
	public static final int NET_TAG_GAME_DETAIL_GUIDE_DETAIL = 236;// 233;

	// 获取游戏详情-评论
	public static final int NET_TAG_GAME_DETAIL_COMMENT = 234;

	// 发表评论评星
	public static final int NET_TAG_PUBLISH_COMMENT_STAR = 235;

	/** tag定义：获取我的动态 */
	public static final int NET_TAG_GET_MY_DYNAMIC_DATA = 251;

	/** tag定义：获取我安装过的游戏 */
	public static final int NET_TAG_GET_INSTALLED_GAME = 252;

	/** tag定义：注册我安装过的游戏 */
	public static final int NET_TAG_REGISTER_INSTALLED_GAME = 253;

	/** tag定义：获取我的消息 */
	public static final int NET_TAG_GET_MY_MESSAGE = 254;

	/** tag定义：删除、设置消息已读 */
	public static final int NET_TAG_DEL_SETREAD_MESSAGE = 255;

	/** tag定义：得到消息详情 */
	public static final int NET_TAG_GET_MESSAGE_DETAIL = 256;

	/** tag定义：获取我收藏的游戏 */
	public static final int NET_TAG_GET_COLLECTION_GAME = 257;

	/** tag定义：收藏、取消收藏游戏和攻略 */
	public static final int NET_TAG_COLLECTION_ACTIONS = 258;

	/** tag定义：获取我收藏的攻略 */
	public static final int NET_TAG_GET_COLLECTION_GUIDE = 259;

	/** tag定义：获取抢号列表 */
	public static final int NET_TAG_SNAP_NUMBER_LIST = 210;
	/** tag定义：抢号详情 */
	public static final int NET_TAG_SNAP_NUMBER_DETAIL = 211;
	/** tag定义：抢号 */
	public static final int NET_TAG_SNAP_NUMBER = 212;
	/** tag213：开服 **/
	public static final int NET_TAG_OPEN_SERVER_LIST = 213;
	/** tag214：开服详情 **/
	public static final int NET_TAG_OPEN_SERVER_DETAIL = 214;
	/** tag215：活动 **/
	public static final int NET_TAG_ACTIVITIES_LIST = 215;
	/** 活动详情 tag = 216 **/
	public static final int NET_TAG_ACTIVITY_DETAIL = 216;
	/** 测评 tag = 217 **/
	public static final int NET_TAG_APPIAISAL_LIST = 217;
	/** 测评详情 tag = 218 **/
	public static final int NET_TAG_APPIAISAL_DETAIL = 218;
	/* 获取搜索关键字 tag = 241 */
	public static final int NET_TAG_KEYWORDS = 241;
	/* 根据关键字搜索游戏 tag = 242 */
	public static final int NET_TAG_SEARCH = 242;
	/* 获取推荐游戏和关键字 tag = 243 */
	public static final int NET_TAG_GET_RECOM_KEYWORDS = 243;
	/**获取游戏相关推荐信息*/
	public static final int NET_TAG_GET_RELATED_GAMEINFO = 261;
	/* 白名单 */
	public static final int NET_TAG_WHITE_LIST = 501;
	/* 游戏更新 */
	public static final int NET_TAG_UPDATE_GAMES = 502;

	/** tag定义：注册启动联运游戏 */
	public static final int NET_TAG_REGISTER_START_GAME = 601;

	/** tag定义：统计开始下载游戏 */
	public static final int NET_TAG_START_DOWNLOAD_GAME = 701;

	/* 获取设备下载游戏列表 tag = 201 */
	public static final int NET_TAG_GET_DOWNLOADED_GAMES = 201;
	/* 上传设备下载游戏信息 tag = 202 */
	public static final int NET_TAG_UPLOAD_DOWNLOADED_GAMES = 202;
	/* 获取游戏登录token tag = 142 */
	public static final int NET_TAG_GET_GAME_LOGIN_TOKEN = 142;
	/* 获取金币 */
	public static final int NET_TAG_GET_COIN = 801;

	/* 获取积分兑换详情 */
	public static final int NET_TAG_GET_EXCHANGE_HISTORY_DETAIL = 804;
	/**
	 * 争霸赛
	 */
	public static final int NET_TAG_COMPETITION = 805;

	/* 必玩 */
	public static final int NET_TAG_GET_MUST_PLAY_GAMES = 806;

	/**
	 * Main content in Coin Center
	 */
	public static final int NET_TAG_MAIN_CONTENT_IN_COIN_CENTER = 802;

	/**
	 * Exchange action in coin center
	 */
	public static final int NET_TAG_EXCHANGE_ACTION_IN_COIN_CENTER = 803;
	/**
	 * 获取推荐应用.
	 */
	public static final int NET_TAG_GET_RECOMMEND_APP = 1200;
	/**
	 * 获取短信中心号.
	 */
	public static final int NET_TAG_GET_SMSCENTERS = 1201;

	// 启动屏广告
	public static final int NET_TAG_SPLASH_AD = 1301;//原1300

	// 首页为您推荐
	public static final int NET_TAG_HOME_RECOMMEND = 1400;

	// 首页启动弹窗
	public static final int NET_TAG_HOME_START_DIALOG = 1500;
	// tag constants
	public static final String JSON_CONTENT_TYPE = "content_type";
	public static final String JSON_TAG = "tag";
	public static final String JSON_VERSION = "version";
	public static final String JSON_UA = "ua";
	public static final String JSON_OS = "os";
	public static final String JSON_SCREENW = "screenw";
	public static final String JSON_SCREENH = "screenh";
	public static final String JSON_TOKEN = "token";
	public static final String JSON_PICTURE_TYPE = "pictype";
	public static final String JSON_CONNECT_TYPE = "connecttype";
	public static final String JSON_IMEI = "imei";
	public static final String JSON_IMSI = "imsi";
	public static final String JSON_PHONE = "phone";
	public static final String JSON_CHANNEL = "channel";
	public static final String JSON_UDID = "udid";
    public static final String JSON_BDCUID = "bdcuid";
	public static final String JSON_PUSH_USERID = "push_userid";
	public static final String JSON_PUSH_CHANNELID = "push_channelid";
	public static final String JSON_ERROR_CODE = "errorcode";
	public static final String JSON_ERROR_MSG = "errormsg";

	public static final String JSON_APP_VERSION = "appversion";
	public static final String JSON_APP_VERSION_CODE = "appversioncode";
	public static final String JSON_APP_UPDATETYPE = "updatetype";
	public static final String JSON_APP_APKURL = "apkurl";
	public static final String JSON_APP_APKVERSION = "apkversion";
	public static final String JSON_APP_APKSIZE = "apksize";
	public static final String JSON_APP_DESCRIPTION = "description";

	public static final String JSON_USERNAME = "username";
	public static final String JSON_PASSWORD = "password";
	public static final String JSON_NICKNAME = "nickname";
	public static final String JSON_MESSAGE = "msgcontent";
	public static final String JSON_USERID = "userid";
	public static final String JSON_SESSIONID = "sessionid";
	public static final String JSON_MSGTYPE = "msgtype";
	public static final String JSON_TARGETID = "targetid";
	public static final String JSON_MSGSUBTYPE = "msgsubtype";
	public static final String JSON_PAGEINDEX = "pageindex";
	public static final String JSON_PAGENUM = "pagenum";
	public static final String JSON_REGISTERTYPE = "registtype";
	public static final String JSON_PHONENUM = "phonenum";
	public static final String JSON_SERVICENUM = "servicenum";
	public static final String JSON_VERIFYCODE = "verifycode";
	public static final String JSON_REQUESTTYPE = "requesttype";
	public static final String JSON_COINTYPE = "type";
	public static final String JSON_CONTENT = "content";
	public static final String JSON_CONTACT = "contact";
	public static final String JSON_GAMENUM = "gamenum";
	public static final String JSON_GAMECOUNT = "gamescount";
	public static final String JSON_TOTALMSGNUM = "totalmsgnum";
	public static final String JSON_MESSAGENUM = "messagenum";
	public static final String JSON_UNREADMSGNUM = "unreadmsgnum";
	public static final String JSON_COLLECTNUM = "collectnum";
	public static final String JSON_COINNUM = "coinnum";
	public static final String JSON_ISLOGINREQ = "isloginreq";

	public static final String JSON_NEWPASSWORD = "newpwd";
	public static final String JSON_OLDPASSWORD = "oldpwd";
	public static final String JSON_FLAG = "flag";
	public static final String JSON_MSGLIST = "msglist";
	public static final String JSON_MSGID = "msgid";
	public static final String JSON_MSGTITLE = "msgtitle";
	public static final String JSON_MSGICONURL = "msgiconurl";
	public static final String JSON_MSGTIME = "msgtime";
	public static final String JSON_MSGSTATE = "msgstate";
	public static final String JSON_MSGTEXT = "msgtext";
	public static final String JSON_GAMELIST = "gamelist";
	public static final String JSON_GAMEID = "gameid";
	public static final String JSON_GAMEIDS = "gameids";

	public static final String JSON_GAME_LISTS = "game";
	public static final String JSON_GAME_CATEGORY = "categorytitle";
	public static final String JSON_GAME_DES = "comment";
	public static final String JSON_GAME_LABLE_NAME = "labelname";
	public static final String JSON_GAME_LABLE_COLOR = "labelcolor";

	public static final String JSON_GAMENAME = "gamename";
	public static final String JSON_STAR = "star";
	public static final String JSON_GAME_STARS = "gamestar";
	public static final String JSON_GAME_DOWNLOADED_COUNT = "gamedownloadcount";
	public static final String JSON_GAME_RECOMMENDATION = "gamerecommenddesc";
	public static final String JSON_DOWNLOADTIMES = "downloadtimes";
	public static final String JSON_PKGNAME = "pkgname";
	public static final String JSON_GAMEURL = "gameurl";
	public static final String JSON_DOWNLOADURL = "downloadurl";
	public static final String JSON_DOWNLOADSTATUS = "downloadstatus";
	public static final String JSON_PKGSIZE = "pkgsize";

	public static final String JSON_GUIDELIST = "guidelist";
	public static final String JSON_GUIDEID = "guideid";
	public static final String JSON_GUIDETITLE = "guidetitle";
	public static final String JSON_GUIDEURL = "guideurl";
	public static final String JSON_GUIDETIME = "time";

	/* 页码 */
	public static final String JSON_PAGE = "page";
	/* 页面大小 */
	public static final String JSON_DATACOUNT = "datacount";
	/* 记录总数 */
	public static final String JSON_TOTALCOUNT = "totalcount";

	/* 游戏id */
	public static final String JSON_GAME_ID = "gameid";
	/* 游戏名称 */
	public static final String JSON_GAME_NAME = "gamename";
	/* 游戏包名 */
	public static final String JSON_GAME_PACKAGE = "pkgname";

	/* 游戏图标地址 */
	public static final String JSON_GAME_ICON = "gameicon";
	/* 游戏星级:0-5 */
	public static final String JSON_GAME_STAR = "star";
	/* 下载次数 */
	public static final String JSON_GAME_DOWNLOAD_TIMES = "downloadtimes";
	/* 下载地址 */
	public static final String JSON_GAME_DOWNLOAD_URL = "downloadurl";
	/*活动来源*/
	public static final String JSON_ACTIVITY_DETAIL_ACTSOURCE = "actsource";
	/* 包大小 */
	public static final String JSON_GAME_PACKAGE_SIZE = "pkgsize";

	/* 抢号列表 */
	public static final String JSON_GRAB_GAMES = "grabgames";
	/* 抢号id */
	public static final String JSON_GRAB_ID = "grabid";
	/* 抢号标题 */
	public static final String JSON_GRAB_TITLE = "grabtitle";
	/* 抢号的号码剩余数量 */
	public static final String JSON_GRAB_NUMBE_RREST = "numberrest";
	/* 抢号的号码总数 */
	public static final String JSON_GRAB_NUMBER_TOTAL = "numbertotal";
	/* 抢号状态 */
	public static final String JSON_GRAB_STATUS = "grabstatus";// 0 未登录 1 未抢 2
																// 已抢 3 已结束
	/* 抢到的号码 */
	public static final String JSON_GRABBED_NUMBER = "grabbednumber";

	/* 开服列表 */
	public static final String JSON_OPEN_SERVERS = "gameservers";//
	/* 开服id */
	public static final String JSON_OPEN_SERVER_ID = "openserverid";
	/* 开服标题 */
	public static final String JSON_OPEN_SERVER_TITLE = "opentitle";
	/* 开服状态 */
	public static final String JSON_OPEN_SERVER_TIME = "opentime";
	/* 开服时间 */
	public static final String JSON_OPEN_SERVER_STATUS = "openstatus";

	// home page
    public static final String JSON_KEYWORD_HOME_PAGE = "keywords";
	public static final String JSON_GAEMADS_HOME_PAGE = "gameads";
	public static final String JSON_GAMEDRID_HOME_PAGE = "gamegrid";
	public static final String JSON_GAMELIST_HOME_PAGE = "gamelist";
	public static final String JSON_ADGAMEID_HOME_PAGE = "adgameid";
	public static final String JSON_ADGAMENAME_HOME_PAGE = "adgamename";
	public static final String JSON_ADGAMESIZE_HOME_PAGE = "adgamesize";
	public static final String JSON_ADGAMESTAR_HOME_PAGE = "adgamestar";
	public static final String JSON_ADPKGNAME_HOME_PAGE = "adpkgname";
	public static final String JSON_ADURL_HOME_PAGE = "adurl";
	public static final String JSON_ADTYPE_HOME_PAGE = "adtype";
	public static final String JSON_ITEMID_HOME_PAGE = "itemid";
	public static final String JSON_GAMETYPE_HOME_PAGE = "gametype";
	public static final String JSON_GAMETYPENUMBER_HOME_PAGE = "gametypenumber";
	public static final String JSON_GAMEID_HOME_PAGE = "gameid";
	public static final String JSON_GAMENAME_HOME_PAGE = "gamename";
	public static final String JSON_PKGNAME_HOME_PAGE = "pkgname";
	public static final String JSON_GAMEICON_HOME_PAGE = "gameicon";
	public static final String JSON_DOWNLOADURL_HOME_PAGE = "downloadurl";

	/** @upload -- @游戏专题 -- 页码 ： 1...: 页码 | 0：全部 **/
	public static final String JSON_PAGE_ID = "pageindex";
	/** @upload -- @游戏专题 -- 每页专题数量 **/
	public static final String JSON_TOPIC_PAGE_COUNT = "pagenum";
	/** @download -- @游戏专题 -- 专题列表 **/
	public static final String JSON_TOPIC_LIST = "subjectlist";
	/** @download -- @游戏专题 -- 专题banner图 **/
	public static final String JSON_BANNER_ICON = "subjecticon";
	/** @download -- @游戏专题 -- 专题名称 **/
	public static final String JSON_TOPIC_NAME = "subjectname";
	/** @download -- @游戏专题 -- 专题描述 **/
	public static final String JSON_TOPIC_DESCRIPTION = "subjectdesc";
	/** @download -- @游戏专题 -- 专题id **/
	public static final String JSON_TOPIC_ID = "subjectid";
	/** @download -- @游戏专题 -- 专题总数 **/
	public static final String JSON_TOPIC_COUNT = "subjectcount";
	/** @download -- @專題詳情 -- 专题詳情信息 **/
	public static final String JSON_TOPIC_DETAIL = "subject";
	public static final String JSON_APPRAISAL_TYPE = "type";
	/*资讯列表 */
	public static final String JSON_APPRAISAL = "infos";
	/*资讯详情列表 */
	public static final String JSON_APPRAISAL_INFOSTIME = "infostime";
	public static final String JSON_APPRAISAL_INFOSSOURCE = "infossource";
	public static final String JSON_APPRAISAL_INFOSCONTENT = "infoscontent";
	/*资讯详情-游戏根标签 */
	public static final String JSON_APPRAISAL_GAMEINFO = "gameinfo";
	/* 资讯id */
	public static final String JSON_APPRAISAL_ID = "infosid";
	/* 资讯标题 */
	public static final String JSON_APPRAISAL_TITLE = "infostitle";
	/* 资讯内容 */
	public static final String JSON_APPRAISAL_CONTENT = "infoscontent";
	/*资讯图标地址 */
	public static final String JSON_APPRAISAL_ICON = "infosicon";
	/* 活动时间 */
	public static final String JSON_APPRAISAL_TIME = "infostime";
	/* 活动列表 */
	public static final String JSON_ACTIVITIES = "activities";
	/* 活动id */
	public static final String JSON_ACTIVITY_ID = "actid";
	/* 活动标题 */
	public static final String JSON_ACTIVITY_TITLE = "acttitle";
	/* 活动时间 */
	public static final String JSON_ACTIVITY_TIME = "acttime";

	/* 开服、活动详情内容列表 */
	public static final String JSON_DETAIL_CONTENTS_LIST = "contents";
	/* 开服、活动详情的一个图片地址 */
	public static final String JSON_DETAIL_ITEM_PIC = "actpic";
	/* 开服、活动详情的一个内容 */
	public static final String JSON_DETAIL_ITEM_CONTENT = "actcontent";

	/* 关键字列表 */
	public static final String JSON_KEYWORDS_LIST = "keywordlist";
	/* 关键字个数 */
	public static final String JSON_KEYWORDS_COUNT = "count";

	/* 游戏列表 */
	public static final String BM_JSON_DATA_LIST = "data";
	/* 关键字 */
	public static final String JSON_KEYWORD = "keyword";
	/* 是否有搜索结果 */
	public static final String JSON_HAS_SEARCH_RESULT = "hassearchresult";
	/*
	 * 搜索结果总数
	 */
	public static final String JSON_SEARCH_TOTAL_COUNT = "total";

	public static final String JSON_PACKAGES = "packages";

	public static final String JSON_GAME_VERSION = "gameversion";
	public static final String JSON_GAME_VERSION_INT = "gameversionint";
	public static final String JSON_VERSION_INT = "versionint";
	/* 发布日期 */
	public static final String JSON_PUBLISH_DATE = "publishdate";
	/* 签名 */
	public static final String JSON_GAME_SIGN = "sign";
	public static final String JSON_GAME_UPDATE_RESULTS = "updateresults";
	public static final String JSON_GAME_ICON_URL = "iconurl";

	/* 联运游戏key */
	public static final String JSON_GAME_KEY = "startaction";
	public static final String JSON_NEED_LOGIN = "needlogin";

	public static final String JSON_UPDATABLE = "updatable";
	public static final String JSON_COMING = "comingsoon";

	/* 抢到的号码 */
	public static final String JSON_GRABBED_RESCODE = "rescode";

	public static final String JSON_PATCH_URL = "patchurl";
	public static final String JSON_PATCH_SIZE = "patchsize";
	public static final String JSON_APKMD5 = "apkMd5";

	/* Exchange history detail */
	public static final String JSON_EXCHANGE_LIST = "exchangelist";
	public static final String JSON_EXCHANGE_ID = "exchangeid";
	public static final String JSON_EXCHANGE_PROP_ID = "propid";
	public static final String JSON_EXCHANGE_PROP_ICON_URL = "propicon";
	public static final String JSON_EXCHANGE_DATE = "date";
	public static final String JSON_EXCHANGE_EXPIRE_DATE = "expire";
	public static final String JSON_EXCHANGE_METADATA = "metadata";
	public static final String JSON_EXCHANGE_METATYPE = "metatype";
	public static final String JSON_EXCHANGE_CARD_NUM = "cardnum";
	public static final String JSON_EXCHANGE_CARD_PWD = "password";
	public static final String JSON_EXCHANGE_CARD_OPERATOR = "operator";

	/**获取游戏相关推荐信息*/
	public static final String JSON_INFOS = "info";
	public static final String JSON_INFO_ID = "infoid";
	public static final String JSON_INFO_TYPE = "infotype";
	public static final String JSON_INFO_CONTENT = "infocontent";
	
	public static final int EXCHANGE_META_TYPE_CARD = 1;
	public static final int FASTREG_REQTYPE_LOGIN = 1;
	public static final int FASTREG_REQTYPE_REGISTER = 2;

	/* Exchange history detail */

	public static class GrabStatus {
		public static final int NOT_LOGIN = 0;
		public static final int SNAPPED = 2;
		public static final int NOT_SNAPPED = 1;
		public static final int OVER = 3;
	}

	public static class OpenServerStatus {
		public static final int PENDING = 1;
		public static final int IN_PROGRESS = 2;
		public static final int OVER = 3;
	}

	public static class CancelReason {
		public static final int CANCEL_UPDATE = 1;
	}

	public static class Operator {
		public static final int OPERATOR_MOBILE = 1;
		public static final int OPERATOR_TELCOM = 3;
		public static final int OPERATOR_UNICOM = 2;
	}

	public static final String IMAGE_CACHE = "/duoku/GameSearch/cache/";
	public static final String DOWNLOAD_FOLDER = "/duoku/GameSearch/downloads";

	public static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;

	public static String APK_MIME_TYPE = "application/vnd.android.package-archive";

	public static final String VERSION_NAME_SP = "ver_name";

	/** 定义高速下载扫描文件夹信息获取地址 */
	public static final String GAMESEARCH_SPEEDDOWNLOAD_DIRSCAN_URL = "http://api.m.duoku.com/client/path.json";
	/** 高速下载apk名字正则表达式 */
	// public static final String SPEED_DOWLOAD_APK_NAME_PATTERN =
	// "^gamesearch_[0-9]+_[0-9]+.apk$";
	/** tag定义： 根据gameid获取下载信息 */
	public static final int NET_TAG_SPEEDDOWNLOAD_INFOS = 260;
	// for speed download
	public static final String JSON_SPEED_DOWNLOAD_URL = "downloadurl";
	public static final String JSON_SPEED_DOWNLOAD_PACKAGENAME = "pkgname";
	public static final String JSON_SPEED_DOWNLOAD_VERSIONNAME = "versionname";
	public static final String JSON_SPEED_DOWNLOAD_VERSIONCODE = "versioncode";
	public static final String JSON_SPEED_DOWNLOAD_ICONURL = "iconurl";
	public static final String JSON_SPEED_DOWNLOAD_APPNAME = "gamename";
	public static final String JSON_SPEED_DOWNLOAD_DOWNLOADINFOS = "downloadinfos";
	public static final String JSON_SPEED_DOWNLOAD_ACTION = "startaction";
	public static final String JSON_SPEED_DOWNLOAD_NEED_LOGIN = "needlogin";
	public static final String JSON_SPEED_DOWNLOAD_GAME_ID = "gameid";

	/** 争霸赛 **/
	public static final String JSON_COMPETITION_DESC = "competitionDes";
	public static final String JSON_COMPETITION_LIST = "competitionlist";
	public static final String JSON_COMPETITION_TITLE = "competitionTitle";
	public static final String JSON_COMPETITION_COINS = "coins";
	public static final String JSON_COMPETITION_MEMEBERS = "members";
	public static final String JSON_COMPETITION_REWARDS = "reward";
	public static final String JSON_COMPETITION_RULE = "rule";

	public static final String JSON_PROP_ID = "propid";
	public static final String JSON_OPERATOR_ID = "operator";

	/** Configuration for notification of update. **/
	public static final String SP_UPDATE_NOTIFICATION_LAST_SHOW = "last_show";
	public static final String UPDATE_AVIABLE = "com.duoku.update.aviable";

	public static final long UPDATE_NOTIFICATION_WAIT_DURATION = 8 * 60 * 60 * 1000;

	public static final String JSON_RECOM_APPLIST = "applist";
	public static final String JSON_RECOMAPP_TITLE = "title";
	public static final String JSON_RECOMAPP_ITEMNAME = "appname";
	public static final String JSON_RECOMAPP_ITEMICON = "appicon";
	public static final String JSON_RECOMAPP_ITEMURL = "appurl";

	/** 获取短信中心相关KEY,用于协议解析的key **/
	public static final String JSON_GET_SMSC_CNMOBILE = "cnmobile";
	public static final String JSON_GET_SMSC_CNUNICOM = "cnunicom";
	public static final String JSON_GET_SMSC_CNTELECOM = "cntelecom";
	public static final String JSON_GET_SMSC_COMMONNUM = "commonnum";
	/** sharedPrefrence存储各个运营商短信号用的key */
	public static final String KEY_SMSC_CNMOBILE = "smsc_cnmobile";
	public static final String KEY_SMSC_CNUNICOM = "smsc_cnunicom";
	public static final String KEY_SMSC_CNTELECOM = "smsc_cntelecom";
	public static final String KEY_SMSC_COMMONNUM = "smsc_commonnum";

	public static final int CHINAMOBILE = 1;
	public static final int CHINAUNICOM = 2;
	public static final int CHINATELECOM = 3;
	public static final int UNKNOW_OPERATE = 0;

	public static final long DURATION_UPDATE_SMSC = 60 * 60 * 24 * 1000;

	public static final int MAXTIME_PROMPT_CHECKROOT_DOWNLOAD = 3;

	public static final String PHONE_BRAND_XIAOMI = "xiaomi";

	public static final String refresh_head_action = "com.duoku.gamesearch.userlogin.refreshheader";
}
