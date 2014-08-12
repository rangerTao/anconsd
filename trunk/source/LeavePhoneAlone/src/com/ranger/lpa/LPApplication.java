package com.ranger.lpa;

import android.app.Application;

import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.utils.DeviceId;

import java.util.LinkedList;

/**
 * @author taoliang(taoliang@baidu-mgame.com)
 * @version V
 * @Description: TODO
 * @date 2014年5月31日 下午8:33:54
 */
public class LPApplication extends Application {

    private static LPApplication _instants;

    private static boolean selfServer = false;

    public static LPApplication getInstance() {
        return _instants;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instants = this;

        if (MineProfile.getInstance().getUdid().equals("")) {
            MineProfile.getInstance().setUdid(DeviceId.getDeviceID(this));
        }
    }

    public static boolean isSelfServer() {
        return selfServer;
    }

    public static void setSelfServer(boolean selfServer) {
        LPApplication.selfServer = selfServer;
    }

}
