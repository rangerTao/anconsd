package com.andconsd.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.os.Environment;

public class Constants {
	
	private static final String date_format = "yyyy-MM-dd HH:mm:ss";
	public static final SimpleDateFormat date_formater = new SimpleDateFormat(date_format);
	
	public static final String COMIC_SERVER = "http://comicserver.sinaapp.com/";
//	public static final String COMIC_SERVER = "http://192.168.1.6:8090/comicserver/1/";
	
	public static final String SERVER_BEAUTY = COMIC_SERVER + "getbeauty.php";
	public static final String SERVER_RELAX = COMIC_SERVER + "getrelax.php";
	public static final String SERVER_GAME = COMIC_SERVER + "getgame.php";
	public static final String SERVER_CAR = COMIC_SERVER + "getcar.php";
	public static final String SERVER_FAMOUS = COMIC_SERVER + "getfamous.php";
	public static final String SERVER_FEEDBACK = COMIC_SERVER + "feedback.php";
	
	public static final int TAG_BEAUTY = 1;
	public static final int TAG_RELAX = 2;
	public static final int TAG_GAME = 3;
	public static final int TAG_CAR = 4;
	public static final int TAG_FAMOUS = 5;
	public static final int TAG_FEEDBACK = 6;
	public static final int TAG_BEAUTY_BAIDU = 7;
	
	public static boolean DEBUG = false;
	
	public static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;
	
	public static final String SETTINGS_PREFERENCE = "settings_preference";
	
	public final static String APP_ID = "1133625";
	public final static String API_KEY = "tWbkg977Oz2z2kthvzL4ihrg";
	public final static String SECRIT_KEY = "Kx9491PFegMcuixzvkxz1IiS4mWBfRgG";
	
	public static final String WEIBO_KEY = "1330499407";
	public static final String WEIBO_SECRET = "5ddb0013700c0b355f4781d75920a53b";
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	// public static final String REDIRECT_URL = "http://www.sina.com";
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog," + "invitation_write";
	
	public final static String WEIXIN_ID = "wxe725322f54f8f9bd";
	public final static String WEIXIN_KEY = "3c6c026177cc74a51f8dc2badea2d723";
	
	public static final String IMAGE_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Andconsd/cache/";
	public static String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Andconsd/picture";
	public static final String BLANK_LINE = "\r\n\r\n";
	public static final String Short_BLANK_LINE = "\r\n";
	public static final String FILE_NOT_FOUND_TEXT = "File not found!";
	public static final String PREFIX_PACKAGE_DEFAULT = ".duoku_share.apk";
	public static final int DEFAULT_PORT = 8080;
	
	public static final long apCountDown = 1000 * 50;
	
	public static boolean service_running = true;
	
	public static HashMap<String,Bitmap> ImgCache = new HashMap<String,Bitmap>();
	
	public static ArrayList<String> files = new ArrayList<String>();
	
	public static final int OPTION_MENU_SETTING = 998;
	public static final int OPTION_MENU_HELP = 997;
	public static final int OPTION_MENU_SHARE = 996;
	public static final int OPTION_MENU_DELETE = 995;
	
	public static final String EVENT_HTTP_START = "10001";
	public static final String EVENT_UPLOAD_PIC = "20001";
	public static final String EVENT_DELETE_PIC  = "20002";
	public static final String EVENT_UPLOAD_SUCCESS = "20003";
	public static final String EVENT_DELETE_SUCCESS = "20004";
	public static final String EVENT_CHANGE_DIR = "30001";
	public static final String EVENT_CHANGE_TIMEOUT = "30002";
	public static final String EVENT_BAIDUAD_SHOW = "40001";
	public static final String EVENT_BAIDUAD_CLICK = "40002";
	public static final String EVENT_BAIDUAD_FAIL = "40003";
	
	public static final String JSON_TAG = "tag";
	public static final String JSON_ERROR_CODE = "errorcode";
	public static final String JSON_ERROR_MSG = "errormsg";
	
	public static final String JSON_VERSION = "version";
	public static final String JSON_UA = "ua";
	public static final String JSON_OS = "os";
	public static final String JSON_UDID = "udid";
	public static final String JSON_SCREENWH = "screenwh";
	public static final String JSON_CONNECT_TYPE = "connecttype";
	public static final String JSON_IMEI = "imei";
	public static final String JSON_CHANNEL = "channel";
	public static final String JSON_PUSH_USERID = "push_userid";
	public static final String JSON_PUSH_CHANNELID = "push_channelid";
	public static final String JSON_SESSION_ID = "sessionid";
	public static final String JSON_PAGEID = "pageid";
	public static final String JSON_ACTID = "actid";
	public static final String JSON_PROPID = "propid";
	public static final String JSON_GAMEIDS = "gmids";
	
	public static final String INTENT_PICTYPE = "pic_type";
}
