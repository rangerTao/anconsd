package com.ranger.lpa;

import java.util.UUID;

public class Constants {

	public static final String D_UUID = "A9B54FA6-6749-FF1D-D104-C7CC15305A6C";
	public static final UUID mUUID = UUID.fromString(D_UUID);

    public static boolean isBoradcastNeeded = true;
	
	//***************  ACTION ******************//
	public static final String action_bt_scan_finish = "com.ranger.lpa.bluetooth.scan_finish";

    public final static int WIFI_STATUS = 999;
    public final static int WIFI_CONNECTIONINFO = 998;

    public final static int UDP_SOCKET = 21999;
}
