package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.StringUtil;
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
                preLoadSearchKeywords();
            }
        },500);

    }

    private void preLoadSearchKeywords() {

        final SharedPreferences sp = getSharedPreferences("cache", Context.MODE_PRIVATE);

        final String cache_province = sp.getString("keywords", "");

        if(!cache_province.equals("")){
            BaseResult baseResult = JSONParser.parseBMKeywords(cache_province);
            baseResult.setTag(Constants.NET_TAG_KEYWORDS + "");
            baseResult.setErrorCode(DcError.DC_OK);

            int tag = StringUtil.parseInt(baseResult.getTag());
            if (tag == Constants.NET_TAG_KEYWORDS) {
                Constants.keywordsListForSearch = (KeywordsList) baseResult;
            } else if (tag == Constants.NET_TAG_SEARCH) {
            }
        }

    }
}
