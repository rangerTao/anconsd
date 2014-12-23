package com.ranger.lpa.ui.activity;

import java.util.ArrayList;

import com.baidu.mobstat.StatActivity;
import com.ranger.lpa.receiver.IOnNotificationReceiver;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public abstract class BaseActivity extends StatActivity{

    private Handler mHandler = new Handler();
    
    LocalBroadcastManager lbManger;
    
    public void showErrorLog(final String log){

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.e(getLocalClassName(),log);
            }
        });
    }

    public abstract void initView();

}
