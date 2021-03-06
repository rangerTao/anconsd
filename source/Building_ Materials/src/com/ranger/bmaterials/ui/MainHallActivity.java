package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.AbstractListAdapter;
import com.ranger.bmaterials.adapter.BMProvinceAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.listener.SearchKeywordsPreloadListener;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.net.NetManager;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.view.CustomFragmentTabHost;

public class MainHallActivity extends FragmentActivity implements NetUtil.IRequestListener, AdapterView.OnItemClickListener, AbstractListAdapter.OnListItemClickListener {

    private CustomFragmentTabHost mTabHost;

    public Intent intentNotification = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dk_game_hall_activity);

        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        //找不到具体的解决办法，希望能catch住吧！！
        try {
            mTabHost = (CustomFragmentTabHost) findViewById(android.R.id.tabhost);
            mTabHost.init(getSupportFragmentManager());
            mTabHost.initTab();
            mTabHost.setCurrentTab(0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }


        // 为了让程序快速启动 不会出现黑屏 延迟初始化 初始化操作都加这个方法里
        delayInit();

        intentNotification = getIntent();

    }

    public CustomFragmentTabHost getTabHost() {
        return mTabHost;
    }

    // 在主界面中跳到某个tab
    public static void jumpToTabByChildActivity(Activity cx, int tab_num) {
        CustomFragmentTabHost tabhost = ((MainHallActivity) cx).getTabHost();
        if (tabhost != null)
            tabhost.setCurrentTab(tab_num);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        intentNotification = intent;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        Fragment f = mTabHost.getCurrentFragment(getSupportFragmentManager());
        if (f instanceof BMMineFragment) {
            BMMineFragment mineFragment = (BMMineFragment) f;
            mineFragment.refreshHeadPhoto();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recycleGarbage();

        Constants.isFirstInstalled = false;
        Constants.isFirstStartWhenVersionChanged = false;
        NetManager.getHttpConnect().cancelAllRequest();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    private void recycleGarbage() {
            ImageLoaderHelper.onDestroy();
    }

    private long touchTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int code = event.getKeyCode();
            if (code == KeyEvent.KEYCODE_BACK) {

                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= 2000) {
                    CustomToast.showToast(this, getString(R.string.exit_app_hint));
                    touchTime = currentTime;
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    public void delayInit() {
//        preLoadSearchKeywords();

    }

    TextView tvProvince;

    public void setCityName(BMProvinceListResult.ProviceItem name) {

        BMSearchFragment.setCityName(name);
    }

    @Override
    public void onRequestSuccess(BaseResult responseData) {

    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        BMProvinceListResult.ProviceItem pi = (BMProvinceListResult.ProviceItem) parent.getAdapter().getItem(position);
//
//        if(pi != null){
//            try{
//                BMSearchFragment.setCityName(pi);
//
//                bpa.setProvince(pi.getName());
//                bpa.notifyDataSetChanged();
//
//                menu.toggle();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public void onItemIconClick(View view, int position) {
    }

    @Override
    public void onItemButtonClick(View view, int position) {

    }
}
