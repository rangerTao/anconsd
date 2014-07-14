package com.ranger.lpa.tools;

import android.app.Notification;
import android.content.Context;

import com.ranger.lpa.receiver.IOnNotificationReceiver;

import java.util.ArrayList;

/**
 * Created by taoliang on 14-7-12.
 */
public class NotifyManager {

    private Context mContext;

    private static NotifyManager _instance;

    private static ArrayList<IOnNotificationReceiver> receivers;

    private NotifyManager(Context context){
        mContext = context;
        receivers = new ArrayList<IOnNotificationReceiver>();
    }

    public static synchronized NotifyManager getInstance(Context con){

        if(_instance == null){
            _instance = new NotifyManager(con);
        }

        return _instance;
    }

    public synchronized void registerOnNotificationReceiver(IOnNotificationReceiver receiver){

        if(!receivers.contains(receiver)){
            receivers.add(receiver);
        }
    }

    public void unRegisterNotificationReceiver(IOnNotificationReceiver receiver){
        if(receivers.contains(receiver)){
            try {
                receivers.remove(receiver);
            } catch (Exception e) {
            }
        }
    }

    public void notifyStateChanged(int type){

        for(IOnNotificationReceiver rec : receivers){
            try {

                if(rec != null){
                    rec.onNotificated(type);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
