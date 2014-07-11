package com.ranger.lpa.ui.activity;

import java.util.ArrayList;

import com.ranger.lpa.receiver.IOnNotificationReceiver;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BaseActivity extends Activity{

    private Handler mHandler = new Handler();
    
    LocalBroadcastManager lbManger;
    
    private static ArrayList<IOnNotificationReceiver> receivers;
    
    public void showErrorLog(final String log){
        
        mHandler.post(new Runnable() {
            
            @Override
            public void run() {
                Log.e(getLocalClassName(),log);
            }
        });
    }
    
    public synchronized void registerOnNotificationReceiver(IOnNotificationReceiver receiver){
        
        if(receivers == null){
            receivers  = new ArrayList<IOnNotificationReceiver>();
        }
        
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
            }
        }
        
    }

}
