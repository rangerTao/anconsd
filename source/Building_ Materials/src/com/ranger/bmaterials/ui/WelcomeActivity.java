package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ranger.bmaterials.work.SplashTask;

/**
 * Created by taoliang on 14/10/28.
 */
public class WelcomeActivity extends Activity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashTask splashTask = new SplashTask(this);
        splashTask.setEnterHallCallBack(new SplashTask.IEnterHallCallBack() {
            @Override
            public void onEnterHall() {
                Intent mainIntent = new Intent(WelcomeActivity.this,MainHallActivity.class);
                startActivity(mainIntent);

                finish();
            }
        });
        splashTask.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },2000);

    }
}
