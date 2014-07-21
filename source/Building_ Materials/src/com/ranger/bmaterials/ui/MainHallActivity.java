package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.baidu.mobstat.StatService;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppCache;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.listener.SearchKeywordsPreloadListener;
import com.ranger.bmaterials.net.NetManager;
import com.ranger.bmaterials.tools.MyLogger;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.install.BackAppListener;
import com.ranger.bmaterials.view.CustomFragmentTabHost;
import com.ranger.bmaterials.work.SplashTask;
import com.ranger.bmaterials.work.SplashTask.IEnterHallCallBack;

public class MainHallActivity extends FragmentActivity {

	private CustomFragmentTabHost mTabHost;

	public Intent intentNotification = null;

	private final String TAG = "DKGameHallActivity";

	private MyLogger logger = MyLogger.getLogger(TAG);
	
	public static SlidingMenu menu;

	public CustomFragmentTabHost getTabHost() {
		return mTabHost;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        checkVersion();
		SplashTask splashTask = new SplashTask(this);
		splashTask.setEnterHallCallBack(new IEnterHallCallBack() {
			@Override
			public void onEnterHall() {
			}
		});
		splashTask.show();
		setContentView(R.layout.dk_game_hall_activity);

        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        //找不到具体的解决办法，希望能catch住吧！！
        try{
            mTabHost = (CustomFragmentTabHost) findViewById(android.R.id.tabhost);
            mTabHost.init(getSupportFragmentManager());
            mTabHost.initTab();
            mTabHost.setCurrentTab(0);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }


		// 为了让程序快速启动 不会出现黑屏 延迟初始化 初始化操作都加这个方法里
		delayInit();

        preLoadSearchKeywords();

		intentNotification = getIntent();

	}

	private void preLoadSearchKeywords() {
		NetUtil.getInstance().requestForKeywords(Constants.keywordCount, new SearchKeywordsPreloadListener());
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
		if (JUMP_TO_TAB_EXTRA != -1) {
			mTabHost.setCurrentTab(JUMP_TO_TAB_EXTRA);
			JUMP_TO_TAB_EXTRA = -1;
		}

		StatService.onResume(this);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
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
		// new Thread() {
		// @Override
		// public void run() {
		try {
			PackageHelper.removeDownloadProgressListener();

			ImageLoaderHelper.onDestroy();
			BackAppListener.getInstance().onDestroy();
			AppCache.getInstance().onDestroy();
			AppManager manager = AppManager.getInstance(getApplicationContext());

			// manager.pauseDownloadGames();
			manager.onDestroy();
		} catch (Exception e) {
		}
		//
		// }
		// }.start();
	}

	private long touchTime = 0;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			int code = event.getKeyCode();
			if (code == KeyEvent.KEYCODE_BACK) {
				// 按返回键返回首页
				if (mTabHost != null && mTabHost.getCurrentTabTag() != null && mTabHost.getCurrentTabTag().equals(getString(CustomFragmentTabHost.TAB_HOME_ID))) {
					if (HomeFragment.turn2TabFourTag) {
						mTabHost.setCurrentTab(2);
						HomeFragment.turn2TabFourTag = false;
						return true;
					}
				}

				long currentTime = System.currentTimeMillis();
				if ((currentTime - touchTime) >= 2000) {
					CustomToast.showToast(this, getString(R.string.exit_app_hint));
					touchTime = currentTime;
				} else {
					HomeFragment homefragment = (HomeFragment) MainHallActivity.getHallFragment(this, CustomFragmentTabHost.TAB_HOME_ID);
					if (homefragment != null)
						homefragment.dismissGuidePop();

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

	public static int JUMP_TO_TAB_EXTRA = -1;

	// 回到主界面的某个tab
	public static void jumpToTab(Activity cx, int tab_num) {
		JUMP_TO_TAB_EXTRA = tab_num;
		Intent intent = new Intent(cx, MainHallActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cx.startActivity(intent);
		cx.finish();
	}

	// 在主界面中跳到某个tab
	public static void jumpToTabByChildActivity(Activity cx, int tab_num) {
		CustomFragmentTabHost tabhost = ((MainHallActivity) cx).getTabHost();
		if (tabhost != null)
			tabhost.setCurrentTab(tab_num);
	}

	public static Fragment getHallFragment(Activity cx, int tabId) {
		MainHallActivity main = (MainHallActivity) cx;
		FragmentManager fm = main.getSupportFragmentManager();
		CustomFragmentTabHost tabhost = main.getTabHost();
		if (tabhost != null)
			return tabhost.getFragment(fm, tabId);
		return null;
	}

	public void delayInit() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		}, 300);

		initSlidingMenu();
	}

    private String checkVersion() {
        String ver_name = "";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            ver_name = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = sp.getString(Constants.VERSION_NAME_SP, "");
        if (version.equals(""))
            Constants.isFirstInstalled = true;

        if (!version.equals(ver_name))
            Constants.isFirstStartWhenVersionChanged = true;

        sp.edit().putString(Constants.VERSION_NAME_SP, ver_name).commit();
        return ver_name;
    }
	
	private void initSlidingMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT_RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int offset = (dm.widthPixels / 3) * 2;
		menu.setBehindOffset(offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.side_menu);
        menu.setSecondaryMenu(R.layout.side_menu);
	}
}
