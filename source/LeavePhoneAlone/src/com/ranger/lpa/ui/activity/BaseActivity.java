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
    
    public void showErrorLog(final String log){

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.e(getLocalClassName(),log);
            }
        });
    }

}
