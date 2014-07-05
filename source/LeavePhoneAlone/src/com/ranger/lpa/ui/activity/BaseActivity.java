package com.ranger.lpa.ui.activity;

import android.app.Activity;
import android.util.Log;

public class BaseActivity extends Activity{

    public void showErrorLog(String log){
        Log.e(getLocalClassName(),log);
    }

}
