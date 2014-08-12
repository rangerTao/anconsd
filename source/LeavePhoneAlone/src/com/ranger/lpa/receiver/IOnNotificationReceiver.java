package com.ranger.lpa.receiver;


import com.ranger.lpa.pojos.BaseInfo;

public abstract class IOnNotificationReceiver {

    public abstract void onNotificated(int type);

    public abstract void onNotificated(int type,BaseInfo info);
}
