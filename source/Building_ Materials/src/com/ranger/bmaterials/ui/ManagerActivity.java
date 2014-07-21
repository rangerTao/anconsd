package com.ranger.bmaterials.ui;

import java.lang.reflect.Field;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.TabPagerAdapter;
import com.ranger.bmaterials.adapter.TabPagerAdapter.PageCallback;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.broadcast.NotificaionReceiver;
import com.ranger.bmaterials.broadcast.Notifier;
import com.ranger.bmaterials.download.DownloadService;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.view.NewSegmentedLayout;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.view.NewSegmentedLayout.OnCheckedChangeListener;

public class ManagerActivity extends FragmentActivity implements
        OnCheckedChangeListener, /* CallBack, */PageCallback,
        OnPageChangeListener, OnClickListener
{

    private ViewPager pager;

    private NewSegmentedLayout controlView;

    private DownloadAppListFragment downloadAppListFragment;

    private UpdatableAppListFragment updatableAppListFragment;

    private InstalledAppListFragment installedAppListFragment;

    String[] titles;

    private PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        titles = new String[] {
                getString(R.string.tab_download_manager_download),
                getString(R.string.tab_download_manager_update),
                getString(R.string.tab_download_manager_installed) };
        setContentView(R.layout.manager_activity);
        init();
        setupView2();
        // 当前应用的代码执行目录
        // boolean ret = ApkUtil.upgradeRootPermission(getPackageCodePath());
        // Toast.makeText(this, "get root "+ret, 0).show();
        if (Constants.DEBUG)
            Log.i("ManagerActivity", "ManagerActivity onCreate");
        getArgs();
        getCount();
        this.observer = new PackageIntentReceiver(this);
        Notifier.cancleNotifyUpdatableList();
    }

    PackageIntentReceiver observer = null;

    @Override
    protected void onPause()
    {
        GeneralStatistics.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        GeneralStatistics.onResume(this);

        try
        {
            if (null != getIntent() && null != getIntent().getExtras())
            {
                String temp = getIntent().getExtras().getString("from");
                if (temp != null)
                    fromNotifier = true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        super.onResume();
    }

    public static class PackageIntentReceiver extends BroadcastReceiver
    {
        private ManagerActivity host;

        public PackageIntentReceiver(ManagerActivity host)
        {
            this.host = host;
            if (host == null)
            {
                return;
            }
            IntentFilter filter = new IntentFilter(
                    BroadcaseSender.ACTION_PACKAGE_ADDED);
            filter.addAction(BroadcaseSender.ACTION_PACKAGE_REMOVED);
            filter.addDataScheme("package");
            host.registerReceiver(this, filter);

            IntentFilter downloadFilter = new IntentFilter();
            downloadFilter.addAction(BroadcaseSender.ACTION_DOWNLOAD_CHANGED);
            host.registerReceiver(this, downloadFilter);

            IntentFilter installFilter = new IntentFilter();
            installFilter.addAction(BroadcaseSender.ACTION_INSTALL_CHANGED);
            host.registerReceiver(this, installFilter);

            IntentFilter ignoredFilter = new IntentFilter(
                    BroadcaseSender.ACTION_IGNORED_STATE_CHANGED);
            host.registerReceiver(this, ignoredFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (host == null)
            {
                return;
            }
            String action = intent.getAction();
            if (BroadcaseSender.ACTION_PACKAGE_ADDED.equals(action))
            {
                host.onReceveAppEvent(true);
            }
            else if (BroadcaseSender.ACTION_PACKAGE_REMOVED.equals(action))
            {
                host.onReceveAppEvent(false);
            }
            else if (BroadcaseSender.ACTION_DOWNLOAD_CHANGED.equals(action))
            {
                boolean downloadOrOtherwise = intent.getBooleanExtra(
                        BroadcaseSender.DOWNLOAD_CHANGED_ARG, false);
                host.onDownloadChanged(downloadOrOtherwise);
            }
            else if (BroadcaseSender.ACTION_INSTALL_CHANGED.equals(action))
            {
                host.onInstallChanged();
            }
            else if (BroadcaseSender.ACTION_IGNORED_STATE_CHANGED
                    .equals(action))
            {
                boolean status = intent.getBooleanExtra(
                        BroadcaseSender.ARG_IGNORED_STATE, false);
            }
        }
    }

    protected void onReceveAppEvent(boolean addOrRemove)
    {
        onInstallChanged();
    }

    protected void onDownloadChanged(boolean downloadOrOtherWise)
    {
        onInstallChanged();
    }

    protected void onInstallChanged()
    {
        getCount();
    }

    private void getArgs()
    {
        try
        {
            Intent intent = getIntent();
            long notifierId = intent.getLongExtra(
                    NotificaionReceiver.ARG_NOTIFICATION_ID, -1);
            if (notifierId > -1)
            {
                fromNotifier = true;
                if (tabStrip != null)
                {
                    tabStrip.setPage(0);
                }
            }
            else if (intent.getBooleanExtra(
                    NotificaionReceiver.ARG_NOTIFICATION_UPDATE, false))
            {
                fromNotifier = true;
                if (tabStrip != null)
                {
                    tabStrip.setPage(1);
                }
            }
            if (Constants.DEBUG)
                Log.i("ManagerActivity", "ManagerActivity getArgs notifierId "
                        + notifierId);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private void getCount()
    {
        new AsyncTask<Void, Integer, Integer[]>()
        {

            @Override
            protected Integer[] doInBackground(Void... params)
            {
                Integer[] ret = new Integer[3];
                AppManager manager = AppManager
                        .getInstance(getApplicationContext());
                List<DownloadAppInfo> downloadGames = manager
                        .getAndCheckDownloadGames();
                if (downloadGames != null && downloadGames.size() > 0)
                {
                    ret[0] = downloadGames.size();
                }
                else
                {
                    ret[0] = 0;
                }

                List<UpdatableAppInfo> updatableGames = manager
                        .getUpdatableGames(true);
                if (updatableGames != null && updatableGames.size() > 0)
                {
                    ret[1] = updatableGames.size();
                }
                else
                {
                    ret[1] = 0;
                }

                List<InstalledAppInfo> installedGames = manager
                        .getInstalledGames();

                if (installedGames != null && installedGames.size() > 0)
                {
                    ret[2] = installedGames.size();
                }
                else
                {
                    ret[2] = 0;
                }
                return ret;
            }

            @Override
            protected void onPostExecute(Integer[] result)
            {
                updateTitle(result);
            };

        }.execute();
    }

    public void updateTitle(int page, int size)
    {
        String title = titles[page] + (size > 0 ? "(" + size + ")" : "");
        if (tabStrip != null)
            tabStrip.updateTitle(page, title);
    }

    private void updateTitle(Integer[] size)
    {
        if (tabStrip != null)
        {
            String title = "";
            for (int i = 0; i < size.length; i++)
            {
                title = titles[i] + (size[i] > 0 ? "(" + size[i] + ")" : "");
                tabStrip.updateTitle(i, title);

            }
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            if (observer != null)
            {
                unregisterReceiver(observer);
                observer = null;
            }
        }
        catch(Exception e)
        {
            // TODO: handle exception
        }

        try
        {
            ImageLoaderHelper.clearCache();
            finalize();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    boolean fromNotifier = false;

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        // fromNotifier = true ;
        if (Constants.DEBUG)
        {
            // Log.i("ManagerActivity", "ManagerActivity onNewIntent");
        }
        getArgs();
    }

    private boolean checkAppRunning(Context context)
    {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(30);
        String myPkg = context.getPackageName();
        if (!runningTasks.isEmpty())
        {
            int size = runningTasks.size();
            for (int i = 0; i < size; i++)
            {
                RunningTaskInfo task = runningTasks.get(i);
                ComponentName ta = task.topActivity;

                String pkg = ta.getPackageName();
                if (pkg.equals(myPkg) && task.numActivities > 0/**
                 * 
                 * 
                 * 
                 * !ta.getClassName().equals(this.getClass().getName())
                 **/
                )
                {
                    // Logger.d(TAG,"Our app running in backgroud:"+task.topActivity.getClassName());
                    return true;

                }
            }
        }
        // Logger.d(TAG,"Our app not start.");
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (fromNotifier)
            {
                MainHallActivity.jumpToTab(this, 0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @TargetApi(9)
    private void setupView2()
    {
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs_indicator);

        if (DeviceUtil.hasGingerbread())
        {
            pager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        tabStrip.setViewPager(pager);
        tabStrip.setOnPageChangeListener(new OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int index)
            {
                switch (index)
                {
                case 0:
                    ClickNumStatistics
                            .addManageDownloadClickStatis(getApplicationContext());
                    break;
                case 1:
                    ClickNumStatistics
                            .addManageUpdateClickStatis(getApplicationContext());
                    break;
                case 2:
                    ClickNumStatistics
                            .addManageInstalledClickStatis(getApplicationContext());
                    break;
                default:
                    break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {

            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {

            }
        });
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles[position];
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // if (Constants.DEBUG)Log.i("onActivityResult",
        // "ManagerActivity  installApp result"+requestCode+" "+resultCode);
        // requestCode == 1 means the result for package-installer activity
        if (requestCode == 100)
        {
            // resultCode == RESULT_CANCELED means user pressed `Done` button
            if (resultCode == Activity.RESULT_CANCELED)
            {
                // Toast.makeText(this, "User pressed 'Done' button",
                // Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == Activity.RESULT_OK)
            {
                // resultCode == RESULT_OK means user pressed `Open` button
                // Toast.makeText(this, "User pressed 'Open' button",
                // Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init()
    {
        startDownloadService();
        initTitleBar();
        controlView = (NewSegmentedLayout) findViewById(R.id.manager_segment_layout);
        pager = (ViewPager) findViewById(R.id.manager_activity_pager);
        pager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), this));
        setVelocity(pager);
        controlView.setOnCheckedChangeListener(this);
        pager.setOnPageChangeListener(this);
        pager.setOnTouchListener(pagerTouchListener);

        int defaultSegmet = controlView.getDefaultSegmet();
        switch (defaultSegmet)
        {
        case 0:
            pager.setCurrentItem(0, false);
            break;

        case 1:
            pager.setCurrentItem(1, false);
            break;
        case 2:
            pager.setCurrentItem(2, false);
            break;
        }
    }

    private void initTitleBar()
    {
        // TextView tvTitle = (TextView) findViewById(R.id.title_bar_left_text);
        TextView tvTitle = (TextView) findViewById(R.id.label_title);
        tvTitle.setText(R.string.title_manager);

        // View viewBack = findViewById(R.id.title_bar_back);
        View viewBack = findViewById(R.id.img_back);
        viewBack.setOnClickListener(this);
    }

    private void startDownloadService()
    {
        Intent intent = new Intent();
        intent.setClass(this, DownloadService.class);
        startService(intent);
    }

    private void setVelocity(ViewPager pager)
    {
        try
        {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(
                    pager.getContext(), new AccelerateInterpolator());
            // scroller.setFixedDuration(5000);
            mScroller.set(pager, scroller);
        }
        catch(NoSuchFieldException e)
        {
        }
        catch(IllegalArgumentException e)
        {
        }
        catch(IllegalAccessException e)
        {
        }

    }

    private class FixedSpeedScroller extends Scroller
    {

        private int mDuration = 280;

        public FixedSpeedScroller(Context context)
        {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator)
        {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy,
                int duration)
        {
            dismissPopopWindow();
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy)
        {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    // //////////////////////////////////////////////////
    @Override
    public Fragment getFragment(int page)
    {
        switch (page)
        {
        case 0:
            downloadAppListFragment = new DownloadAppListFragment();
            return downloadAppListFragment;
        case 1:
            updatableAppListFragment = new UpdatableAppListFragment();
            return updatableAppListFragment;
        case 2:
            installedAppListFragment = new InstalledAppListFragment();
            return installedAppListFragment;
        default:
            break;
        }
        return null;
    }

    @Override
    public int getPageCount()
    {
        return 3;
    }

    // //////////////////////////////////////////////////
    @Override
    public void onPageScrollStateChanged(int arg0)
    {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
    }

    @Override
    public void onPageSelected(int index)
    {
        controlView.check(index);
    }

    // //////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////
    // OnCheckedChangeListener callback
    // //////////////////////////////////////////////////////////
    @Override
    public void onCheckedChanged(NewSegmentedLayout group, int checkedId)
    {
        switch (checkedId)
        {
        case R.id.manager_segment_download:
            pager.setCurrentItem(0, false);
            break;
        case R.id.manager_segment_update:
            pager.setCurrentItem(1, false);
            break;
        case R.id.manager_segment_installed:
            pager.setCurrentItem(2, false);
            break;
        default:
            break;
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.img_back)
        {
            if (fromNotifier)
            {
                MainHallActivity.jumpToTab(this, 0);
            }
            finish();
        }
    }
	android.view.View.OnTouchListener pagerTouchListener = new android.view.View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, android.view.MotionEvent event) {
			dismissPopopWindow();
			return false;
		}
	};

	private void dismissPopopWindow() {
		if (null != downloadAppListFragment)
			downloadAppListFragment.dismissPopupWindow();
		if (null != updatableAppListFragment)
			updatableAppListFragment.dismissPopupWindow();
		if (null != installedAppListFragment)
			installedAppListFragment.dismissPopupWindow();
	}
}
