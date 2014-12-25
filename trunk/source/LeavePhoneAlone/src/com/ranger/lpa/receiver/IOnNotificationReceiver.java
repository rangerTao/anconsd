package com.ranger.lpa.receiver;


import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.SocketMessage;

public abstract class IOnNotificationReceiver {

    public abstract void onNotificated(int type);

    public abstract void onNotificated(SocketMessage sm);

    public abstract void onNotificated(int type,BaseInfo info);
}
