package com.ranger.lpa;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

public class Constants {

    public static final boolean DEBUG = true;

    public static final String WEIXIN_ID = "wx48a723e78444dfbf";
    public static final String WEIBO_KEY = "455094126";
    public static final String SINA_APP_KEY = "455094126";
    public static final String REDIRECT_URL = "http://www.baidu.com";
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write," + "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog,"
            + "invitation_write";

	public static final String D_UUID = "8ce255c0-200a-11e0-ac64-0800200c9a66";
	public static final UUID mUUID = UUID.fromString(D_UUID);

    public static boolean isBoradcastNeeded = true;
	
	//***************  ACTION ******************//
	public static final String action_bt_scan_finish = "com.ranger.lpa.bluetooth.scan_finish";

    public final static int WIFI_STATUS = 999;
    public final static int WIFI_CONNECTIONINFO = 998;

    public final static int UDP_SOCKET = 8989;
    public final static int UDP_CLIENT = 8990;
    public static DatagramSocket ds_server;

    public static InetAddress temp_address;

    public static final String SETTINGS_PREFERENCE = "settings";

}
