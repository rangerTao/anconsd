package com.ranger.bmaterials.app;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.ranger.bmaterials.adapter.BMProductCollectionAdapter;
import com.ranger.bmaterials.mode.KeywordsList;

import android.graphics.Bitmap;
import android.os.Environment;


public final class Constants {
	
	private static final String FORMATTER_DATE_STRING = "yyyy-MM-dd";
	public static final SimpleDateFormat FORMATER_DATE_FORMAT = new SimpleDateFormat(FORMATTER_DATE_STRING);

	public static final boolean DEBUG = true;
	
	public static KeywordsList keywordsListForSearch;

	public static final int SHOW_GUIDE_ONLY_FIRST_INSTALLED = 1;// 只有首次安装有引导页
	public static final int SHOW_GUIDE_NONE = 2;// 首次安装和覆盖安装都没有引导页
	public static final int SHOW_GUIDE_ALL = 3;// 首次安装和覆盖安装都有引导页
	public static final int SHOW_GUIDE_TYPE = SHOW_GUIDE_ALL;// 本次版本是否有新功能引导页
																// 区别于首次安装

	public static boolean isFirstStartWhenVersionChanged;// 当版本号变化时的首次启动
	public static boolean isFirstInstalled;// 应用第一次启动 不包含覆盖安装时的情况

	public static final String GAME_TING_LOG = "/bmaterials/crashlog/";

	public static final int keywordCount = 50;

	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    public static final String IMAGE_CACHE = "/bmaterials/cache/";

    public static final String IMAGE_PATH = SDCARD + IMAGE_CACHE;
    public static final String IMGCACHE_FOLDER = IMAGE_PATH + "/cache/";

    public static final String TEMPFILE_NAME = "header_temp";
    public static final String PHOTO_UNCOMMIT_FILE = "uncommit";
    /**
     * String constants
     */
	/** 程序设置 */
	public static final String SETTINGS_PREFERENCE = "settings_preference";
	/** 设备地址 */

	/** 服务器地址 */


    /** tag定义：检查更新 */
    public static final int NET_TAG_CHECK_UPDATE = 304;

    /** tag定义：得到手机验证码 */
    public static final int NET_TAG_GET_PHONE_VERIFYCODE = 102;

    /** tag定义：用户名注册 */
    public static final int NET_TAG_USERNAME_REGISTER = 101;

    /* 获取搜索关键字 tag = 241 */
    public static final int NET_TAG_KEYWORDS = 241;
    /** tag定义：激活 */
    public static final int NET_TAG_GET_PROVINCE = 100;

    /** tag定义：修改密码 */
    public static final int NET_TAG_CHANGE_PWD = 303;

    /* 根据关键字搜索游戏 tag = 242 */
    public static final int NET_TAG_SEARCH = 242;

    /* 获取用户信息 */
    public static final int NET_TAG_USERINFO = 103;

    /* 修改用户信息 */
    public static final int NET_TAG_MODIFYUSER = 104;

    /* 上传头像 */
    public static final int NET_TAG_UPLOAD_HEAD = 105;

    // tag constants
	public static final String JSON_TAG = "tag";
	public static final String JSON_VERSION = "version";
	public static final String JSON_UA = "ua";
	public static final String JSON_OS = "os";
	public static final String JSON_SCREENW = "screenw";
	public static final String JSON_SCREENH = "screenh";
	public static final String JSON_CONNECT_TYPE = "connecttype";
	public static final String JSON_IMEI = "imei";
	public static final String JSON_CHANNEL = "channel";
	public static final String JSON_UDID = "udid";
	public static final String JSON_PUSH_USERID = "push_userid";
	public static final String JSON_PUSH_CHANNELID = "push_channelid";
	public static final String JSON_ERROR_CODE = "errorcode";
	public static final String JSON_ERROR_MSG = "errormsg";
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
	public static final String JSON_VERIFYCODE = "verifycode";
	public static final String JSON_REQUESTTYPE = "requesttype";
	public static final String JSON_COINTYPE = "type";
	public static final String JSON_CONTENT = "content";
	public static final String JSON_CONTACT = "contact";
	public static final String JSON_COINNUM = "coinnum";

	public static final String JSON_NEWPASSWORD = "newpwd";
	public static final String JSON_OLDPASSWORD = "oldpwd";
	public static final String JSON_FLAG = "flag";
	public static final String JSON_GAMEID = "gameid";
	public static final String JSON_GAMEIDS = "gameids";

	public static final String JSON_GAMENAME = "gamename";
	public static final String JSON_PKGNAME = "pkgname";
	public static final String JSON_DOWNLOADSTATUS = "downloadstatus";
	/* 页码 */
	public static final String JSON_PAGE = "page";
	/* 页面大小 */
	public static final String JSON_DATACOUNT = "datacount";

	/* 游戏id */
	public static final String JSON_GAME_ID = "gameid";
	/* 抢号id */
	public static final String JSON_GRAB_ID = "grabid";
	/* 开服id */
	public static final String JSON_OPEN_SERVER_ID = "openserverid";

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
	/* 资讯id */
	public static final String JSON_APPRAISAL_ID = "infosid";
	/* 活动id */
	public static final String JSON_ACTIVITY_ID = "actid";
	/* 关键字个数 */
	public static final String JSON_KEYWORDS_COUNT = "count";

	/* 游戏列表 */
	public static final String BM_JSON_DATA_LIST = "data";
	/* 关键字 */

	/*
	 * 搜索结果总数
	 */
	public static final String JSON_SEARCH_TOTAL_COUNT = "total";

	public static final String JSON_PACKAGES = "packages";

	public static final String VERSION_NAME_SP = "ver_name";

	public static final int NET_TAG_SPEEDDOWNLOAD_INFOS = 260;
	public static final String UPDATE_AVIABLE = "com.duoku.update.aviable";

	public static final String refresh_head_action = "com.duoku.gamesearch.userlogin.refreshheader";
}
