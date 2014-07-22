package com.ranger.bmaterials.work;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ranger.bmaterials.adapter.HomeGuideViewPagerAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.view.FixTouchView;
import com.ranger.bmaterials.view.SlowScrollViewpager;

public class SplashTask implements OnPageChangeListener {

	private Activity cx;

	public static int SPLASH_DELAY_TIME;

	public SplashTask(Activity cx) {
		this.cx = cx;
	}

	private boolean isEnterHall;

	public interface IEnterHallCallBack {
		void onEnterHall();
	}

	private IEnterHallCallBack enterHallCallBackImpl;

	public void setEnterHallCallBack(IEnterHallCallBack enterHallCallBackImpl) {
		this.enterHallCallBackImpl = enterHallCallBackImpl;
	}

	public void show() {
		final View splash = getSplashView();
        if(splash != null){
            ((ViewGroup) cx.getWindow().getDecorView()).addView(splash);
        }

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				enterHall(splash);
			}
		}, SPLASH_DELAY_TIME);

	}

	private static final String SPLASH_AD_SP = "splash_ad";
	private static final String SPLASH_AD_PIC_URL_SP = "splash_ad_pic_url";
	private static final String SPLASH_AD_ADTYPE_SP = "splash_ad_ad_type";
	private static final String SPLASH_AD_ITEM_ID_SP = "splash_ad_item_id";
	private static final String SPLASH_AD_PRIZE_URL = "splash_ad_prize_url";

	private View getSplashView() {
		View splash = null;
		SharedPreferences sp = cx.getSharedPreferences(SPLASH_AD_SP, Context.MODE_PRIVATE);
		String adurl = sp.getString(SPLASH_AD_PIC_URL_SP, "");
		ImageLoaderHelper.config();
		File imgFile = ImageLoader.getInstance().getDiscCache().get(adurl);

		SPLASH_DELAY_TIME = 2500;
		splash = new FixTouchView(cx);
		try {
			splash.setBackgroundResource(R.drawable.splash_bg);
		} catch (OutOfMemoryError e) {
            System.gc();
            return null;
        }

		return splash;
	}

	private WeakReference<Bitmap> splashImgCach;

	private synchronized void enterHall(View splash) {
		if (isEnterHall)
			return;

		final boolean isShowGuide = isShowGuide();
		if (isShowGuide) {
			initGuideView();
		} else {
		}

		if (Constants.isFirstInstalled) {
			addShortcut();
			// MineProfile.getInstance().setUpdateAvailable(false);//annoter
			// this line to fix bug 8055.
		}

		if (!isShowGuide && enterHallCallBackImpl != null)
			enterHallCallBackImpl.onEnterHall();

		if (cx.getWindow() != null && splash != null) {
			((ViewGroup) cx.getWindow().getDecorView()).removeView(splash);
		}
		splashImgCach = null;
		isEnterHall = true;
	}

	private SlowScrollViewpager viewPager;
	private int guide_dismiss_limit;

	private void initGuideView() {

		viewPager = new SlowScrollViewpager(cx, 350);

		final ArrayList<View> guide_viewpager_list = new ArrayList<View>();
		final int[] imgRes = new int[] { R.drawable.home_guide_1};

		int[] screenPx = UIUtil.getScreenPx(cx);

		int marginTop = UIUtil.dip2px(cx, 420);// in 480*800

		for (int i = 0; i < imgRes.length; i++) {
			View layout = View.inflate(cx, R.layout.home_guide_layout, null);
			ImageView iv = (ImageView) layout.findViewById(R.id.home_guide_iv);

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;

			Bitmap bm = BitmapFactory.decodeResource(cx.getResources(), imgRes[i], opt);
			int w = bm.getWidth();
			int h = bm.getHeight();

			int newHeight = (h * screenPx[0]) / w;
			try {
//				iv.setImageBitmap(Bitmap.createScaledBitmap(bm, screenPx[0], newHeight = newHeight > screenPx[1] ? screenPx[1] : newHeight, true));
                iv.setImageBitmap(bm);
			} catch (OutOfMemoryError e) {
				ImageLoaderHelper.displayImage("drawable://" + imgRes[i], iv);
			}

			if (i == imgRes.length - 1) {
				View btn = layout.findViewById(R.id.home_guide_btn);
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn.getLayoutParams();
				lp.topMargin = marginTop + (newHeight - h);
				// btn.setVisibility(View.VISIBLE);
				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (viewPager != null) {
							viewPager.setCurrentItem(guide_viewpager_list.size() - 1, true);
						}
					}
				});
			}

			guide_viewpager_list.add(layout);
		}

		View layout = View.inflate(cx, R.layout.home_guide_layout, null);
		layout.setBackgroundColor(Color.TRANSPARENT);
		guide_viewpager_list.add(layout);
		guide_dismiss_limit = UIUtil.dip2px(cx, 50);

		guide_view_pager_count = guide_viewpager_list.size();

		viewPager.setAdapter(new HomeGuideViewPagerAdapter(guide_viewpager_list));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);

		// 显示引导页
		((ViewGroup) cx.getWindow().getDecorView()).addView(viewPager);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	private int guide_view_pager_count;

	/*
	 * 当前页滚动时调用，无论是程序控制的平滑滚动还是用户发起的触摸滚动。
	 * position：第一个页面当前显示的位置索引。如果页面偏移不是0，下一个页面将会可见。
	 * positionOffset：表示第二个页面位置偏移量的比例值，[0, 1)。（右侧页面所占屏幕百分比）
	 * positionOffsetPixels：表示第二个页面位置偏移量的像素值。（右侧页面距右边的像素值）
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (viewPager == null) {
			return;
		}
		if (position == guide_view_pager_count - 2 && positionOffsetPixels >= guide_dismiss_limit) {
			viewPager.setCurrentItem(guide_view_pager_count - 1, true);
		} else if (position == guide_view_pager_count - 1 && positionOffset == 0 && positionOffsetPixels == 0)
			guideViewPagerDismiss();
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
	}

	private void checkUnCompletedDownloadTask() {
	}

	private void guideViewPagerDismiss() {
		((ViewGroup) cx.getWindow().getDecorView()).removeView(viewPager);
		viewPager = null;

		if (enterHallCallBackImpl != null)
			enterHallCallBackImpl.onEnterHall();
	}

	/**
	 * 为程序创建桌面快捷方式
	 */
	private void addShortcut() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				delShortcut();

				Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

				// 快捷方式的名称
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, cx.getString(R.string.app_name));
				shortcut.putExtra("duplicate", false); // 不允许重复创建

				Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
				shortcutIntent.setClassName(cx, cx.getClass().getName());
				shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);// 不加会出现从快捷方式启动进程会重启
																		// 添加后部分手机会重复创建快捷方式
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

				// 快捷方式的图标
				ShortcutIconResource iconRes = ShortcutIconResource.fromContext(cx, R.drawable.ic_launcher);
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

				cx.sendBroadcast(shortcut);
			}
		});
		t.setPriority(Thread.NORM_PRIORITY - 2);
		t.setDaemon(true);
		t.start();

	}

	private void delShortcut() {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, cx.getString(R.string.app_name));
		String appClass = cx.getPackageName() + "." + cx.getLocalClassName();
		ComponentName comp = new ComponentName(cx.getPackageName(), appClass);
		Intent i = new Intent(Intent.ACTION_MAIN).setComponent(comp);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);

		cx.sendBroadcast(shortcut);

	}

	public static boolean isShowGuide() {
		switch (Constants.SHOW_GUIDE_TYPE) {
		case Constants.SHOW_GUIDE_ONLY_FIRST_INSTALLED:

			return Constants.isFirstInstalled;
		case Constants.SHOW_GUIDE_NONE:

			return false;
		case Constants.SHOW_GUIDE_ALL:

			return Constants.isFirstInstalled && Constants.isFirstStartWhenVersionChanged;

		default:
			return true;
		}

	}
}
