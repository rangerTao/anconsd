package com.ranger.lpa.ui.view;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * Created by taoliang on 14-6-13.
 */
public class LPAKeyGuardView {

    private static LPAKeyGuardView _instance;
    private static Activity mActivity;

    private WindowManager mWindowManager;
    private View mLockView;
    private LayoutParams mLockViewLayoutParams;

    private boolean isLocked;

    public static synchronized LPAKeyGuardView getInstance(Activity context){

        mActivity = context;

        if(_instance == null){
            _instance = new LPAKeyGuardView(context);
        }

        return _instance;
    }

    private LPAKeyGuardView(Activity act) {

        mActivity = act;
        init();

    }

    //初始化界面。将界面设置成异常的界面。屏蔽home键和其他按键。
    private void init(){
        isLocked = false;
        mWindowManager = mActivity.getWindowManager();
        mLockViewLayoutParams = new LayoutParams();
        mLockViewLayoutParams.width = LayoutParams.MATCH_PARENT;
        mLockViewLayoutParams.height = LayoutParams.MATCH_PARENT;
        //实现关键
        mLockViewLayoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        //apktool value，这个值具体是哪个变量还请网友帮忙
        mLockViewLayoutParams.flags = 1280;
    }

    //
    public synchronized void setLockView(View v){
        mLockView = v;
    }

    //显示锁屏
    public synchronized void lock() {
        if(mLockView!=null&&!isLocked){
            mWindowManager.addView(mLockView, mLockViewLayoutParams);
        }
        isLocked = true;
    }

    //隐藏锁屏
    public synchronized void unlock() {
        if(mWindowManager!=null&&isLocked){
            mWindowManager.removeView(mLockView);
        }
        isLocked = false;
    }

}
