package com.ranger.lpa;

import android.app.Application;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.utils.DeviceId;
import com.ranger.lpa.utils.StringUtil;

import java.util.LinkedList;

/**
 * @author taoliang(taoliang@baidu-mgame.com)
 * @version V
 * @Description: TODO
 * @date 2014年5月31日 下午8:33:54
 */
public class LPApplication extends Application {
    
    private String localIP;

    private static LPApplication _instants;

    private static boolean selfServer = false;

    public static LPApplication getInstance() {
        return _instants;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(_instants == null){
            
            _instants = this;
            if (MineProfile.getInstance().getUdid().equals("")) {
                MineProfile.getInstance().setUdid(DeviceId.getDeviceID(this));
            }
            
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            
            localIP = String.valueOf(StringUtil.long2ip(dhcpInfo.ipAddress));
            
            localIP = localIP.substring(0,localIP.lastIndexOf(".")) + ".255";
            
        }
        
    }

    public static boolean isSelfServer() {
        return selfServer;
    }

    public static void setSelfServer(boolean selfServer) {
        LPApplication.selfServer = selfServer;
    }

    
    public String getLocalIP() {
        return localIP;
    }

    
    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

}
