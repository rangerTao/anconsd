package com.ranger.lpa.ui.view;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.utils.ACountTimer;
import com.ranger.lpa.utils.StringUtil;

import java.security.PublicKey;

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

    private TextView tvEllipsedTime;

    public interface IKeyGuardViewRemoveListener{
        public void onRemoved();
    }

    private IKeyGuardViewRemoveListener removeListener;

    public void setRemoveListener(IKeyGuardViewRemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public static synchronized LPAKeyGuardView getInstance(Activity context) {

        mActivity = context;

        if (_instance == null) {
            _instance = new LPAKeyGuardView(context);
        }

        return _instance;
    }

    private LPAKeyGuardView(Activity act) {

        mActivity = act;
        init();

    }

    //初始化界面。将界面设置成异常的界面。屏蔽home键和其他按键。
    private void init() {
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

    private long lock_period = 1 * 60 * 60 * 1000;

    //
    public synchronized void setLockView(View v) {
        mLockView = v;
    }

    public void setLockPeriod(long period){
        lock_period = period;
    }

    private ACountTimer timer;

    //显示锁屏
    public synchronized void lock() {
        if (mLockView != null && !isLocked) {
            mWindowManager.addView(mLockView, mLockViewLayoutParams);
        }
        isLocked = true;

        assert mLockView != null;
        tvEllipsedTime = (TextView) mLockView.findViewById(R.id.tv_eclipsed_time);

        tvEllipsedTime.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriod()));

        timer = new ACountTimer(lock_period) {
            @Override
            public void onTick(long millisUntilFinished, int percent) {
                tvEllipsedTime.setText(StringUtil.getFormattedTimeByMillseconds(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                unlock();
            }
        };

        timer.start();
    }

    //隐藏锁屏
    public synchronized void unlock() {
        if (mWindowManager != null && isLocked) {
            mWindowManager.removeViewImmediate(mLockView);
        }
        isLocked = false;

        if(timer != null){
            timer.stop();
        }
        timer = null;

        if(removeListener != null){
            removeListener.onRemoved();
        }
    }

    public boolean isShowing(){
        return isLocked;
    }

}
