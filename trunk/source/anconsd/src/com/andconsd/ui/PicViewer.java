/*****************Copyright (C), 2010-2015, FORYOU Tech. Co., Ltd.********************/
package com.andconsd.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.andconsd.AndApplication;
import com.andconsd.R;
import com.andconsd.adapter.PicPagerAdapter;
import com.andconsd.utils.AndconsdUtils;
import com.andconsd.utils.Constants;
import com.andconsd.widget.DuoleCountDownTimer;
import com.andconsd.widget.DuoleVideoView;
import com.andconsd.widget.photoview.PhotoView;

public class PicViewer extends BaseActivity implements OnTouchListener, OnPageChangeListener {

	public static int THEME = R.style.Theme_Sherlock;

	PhotoView hy;

	private int mIndex;

	public static PicViewer appref;
	private int mItemwidth;
	private int mItemHerght;

	DuoleVideoView vv;
	private ArrayList<String> pathes;

	DuoleCountDownTimer autoPlayerCountDownTimer;

	int screen_off_timeout = 0;

	private Bitmap zoomBitmap;

	PicPagerAdapter pva;

	private ViewPager mFlingGallery;

	RelativeLayout rlSlidShow;
	ImageView ivSlidShow;
	public Handler handler = new Handler();

	RotateAnimation rotate;
	ScaleAnimation scale;
	AlphaAnimation alpha;
	TranslateAnimation translate;

	boolean slidshow = false;

	ProgressBar pbDealPic;
	TextView tvIndex;

	View controlTIps;

	public int getmIndex() {
		return mIndex;
	}

	public void updateState(int visibility) {
		pbDealPic.setVisibility(visibility);
	}

	public void updateIndex(int index) {
		if (tvIndex != null) {
			tvIndex.setText(index + 1 + "/" + Constants.files.size());
		}

		if (autoPlayerCountDownTimer != null) {
			autoPlayerCountDownTimer.seek(0);
		}

	}

	private boolean isViewIntent() {
		String action = getIntent().getAction();
		return Intent.ACTION_VIEW.equals(action);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setTheme(THEME);

		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		}

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		appref = this;
		Intent intent = getIntent();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mItemwidth = dm.widthPixels;
		mItemHerght = dm.heightPixels;

		if (!isViewIntent()) {
			pathes = intent.getStringArrayListExtra("pathes");
			mIndex = intent.getIntExtra("index", 0);
		} else {
			pathes = new ArrayList<String>();
			pathes.add(intent.getData().getPath());
			mIndex = 0;
		}

		try {
			screen_off_timeout = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 1000 * 60 * 4);

		setContentView(R.layout.picview);

		controlTIps = getLayoutInflater().inflate(R.layout.control_pic_view, null);
		tvIndex = (TextView) controlTIps.findViewById(R.id.tvIndex);
		pbDealPic = (ProgressBar) controlTIps.findViewById(R.id.progress_circular);

		getSupportActionBar().setCustomView(controlTIps);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		mFlingGallery = (ViewPager) findViewById(R.id.horizontalview);

		pva = new PicPagerAdapter(Constants.files, mItemwidth, mItemHerght, ((AndApplication) getApplication()).options, pbDealPic);
		mFlingGallery.setAdapter(pva);
		mFlingGallery.setCurrentItem(mIndex);

		mFlingGallery.setOnPageChangeListener(this);

		mFlingGallery.setOnTouchListener(this);

		initAutoPlayCountDown();

		initPopupController();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu sub = menu.addSubMenu("更多");
		sub.add(0, Constants.OPTION_MENU_SETTING, 0, "设置");
		sub.add(0, Constants.OPTION_MENU_DELETE, 0, "删除");

		MenuItem subMenuItem = sub.getItem();
		subMenuItem.setIcon(R.drawable.ic_title_more_menu);
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);

		// Set file with share history to the provider and set the share intent.
		MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		// Note that you can set/change the intent any time,
		// say when the user has selected an image.
		actionProvider.setShareIntent(createShareIntent());

		return super.onCreateOptionsMenu(menu);
	}

	private void exitPicViewer() {
		if (vv != null && vv.isPlaying()) {
			if (vv != null && vv.isPlaying()) {
				vv.pause();
				vv.setVisibility(View.INVISIBLE);

				mFlingGallery.setVisibility(View.VISIBLE);
				getSupportActionBar().show();

				autoPlayerCountDownTimer.resume();
			}
		} else {
			appref.finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			exitPicViewer();
			break;
		case Constants.OPTION_MENU_DELETE:
			String filepath = Constants.files.get(mFlingGallery.getCurrentItem());
			if (AndconsdUtils.deleteFileByName(filepath)) {
				Constants.files.remove(mFlingGallery.getCurrentItem());
				if (mFlingGallery.getChildAt(mFlingGallery.getCurrentItem()) != null) {
					mFlingGallery.removeViewAt(mFlingGallery.getCurrentItem());
					updateIndex(mFlingGallery.getCurrentItem());
				}
				pva.notifyDataSetChanged();
				Toast.makeText(appref, filepath.substring(filepath.lastIndexOf("/") + 1) + "  " + appref.getString(R.string.deletesuccess), 2000)
						.show();
			}
			break;
		case Constants.OPTION_MENU_SETTING:
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creates a sharing {@link Intent}.
	 * 
	 * @return The sharing intent.
	 */
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Uri uri = Uri.fromFile(new File(Constants.files.get(mFlingGallery.getCurrentItem())));
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_tip));
		return shareIntent;
	}

	private void initAutoPlayCountDown() {

		autoPlayerCountDownTimer = new DuoleCountDownTimer(Constants.apCountDown, 1000) {

			@Override
			public void onTick(long millisUntilFinished, int percent) {
			}

			@Override
			public void onFinish() {
				// Start to auto play

				mFlingGallery.setVisibility(View.INVISIBLE);
				getSupportActionBar().hide();

				new Thread() {

					@Override
					public void run() {
						initAnimations();

						rlSlidShow = (RelativeLayout) findViewById(R.id.rlSlidShow);
						rlSlidShow.setOnTouchListener(appref);

						ivSlidShow = (ImageView) findViewById(R.id.ivSlidShow);
						slidshow = true;

						while (slidshow) {

							handler.post(new Runnable() {

								@Override
								public void run() {
									rlSlidShow.setVisibility(View.VISIBLE);

									try {
										if (ivSlidShow.getDrawingCache() != null)
											ivSlidShow.getDrawingCache().recycle();
										ivSlidShow.setImageBitmap(AndconsdUtils.getDrawable(Constants.files.get(getNextIndex()), 1));
										System.gc();
									} catch (Exception e) {
										System.gc();
										ivSlidShow.setImageURI(Uri.parse(Constants.files.get(getNextIndex())));
									}

									int anIndex = 0;
									anIndex = new Random().nextInt() % 4;
									anIndex = Math.abs(anIndex);
									switch (anIndex) {
									case 0:
										ivSlidShow.startAnimation(rotate);
										break;
									case 1:
										ivSlidShow.startAnimation(scale);
										break;
									case 2:
										ivSlidShow.startAnimation(alpha);
										break;
									case 3:
										ivSlidShow.startAnimation(translate);
										break;
									default:
										break;
									}
								}
							});

							try {
								Thread.sleep(15000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						resumeCountDown();
					}

				}.start();

			}
		};

		autoPlayerCountDownTimer.start();
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {

		if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_SCROLL:

				if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
					selectNext();
				else
					selectPrev();
				return true;
			}
		}
		return super.onGenericMotionEvent(event);
	}

	private void selectNext() {

		getSupportActionBar().hide();
		if (mFlingGallery.getCurrentItem() < Constants.files.size())
			mFlingGallery.setCurrentItem(mFlingGallery.getCurrentItem() + 1);
	}

	private void selectPrev() {
		getSupportActionBar().hide();

		if (mFlingGallery.getCurrentItem() > 0)
			mFlingGallery.setCurrentItem(mFlingGallery.getCurrentItem() - 1);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			exitPicViewer();
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			selectNext();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			selectPrev();
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			selectPrev();
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			selectNext();
			break;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (zoomBitmap != null) {
			zoomBitmap.recycle();
		}
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 1000 * 60 * 4);
		super.onDestroy();
	}

	public void showHideController() {

		if (getSupportActionBar().isShowing()) {
			getSupportActionBar().hide();
		} else {
			getSupportActionBar().show();
		}
	}

	public void playVideo(int position) {

		String pathString = Constants.files.get(mFlingGallery.getCurrentItem());
		Uri uri = Uri.parse(pathString);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setType("video/*");
		intent.setDataAndType(uri, "video/*");

		try {
			PicViewer.appref.startActivity(intent);
		} catch (Exception e) {
			new AlertDialog.Builder(PicViewer.appref).setTitle(R.string.player_notfound_title).setMessage(R.string.player_tip)
					.setNegativeButton(R.string.btnClose, null).create().show();
		}
	}

	private void initPopupController() {

		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvIndex.setTextColor(Color.WHITE);
		tvIndex.setText(mIndex + 1 + "/" + Constants.files.size());
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		autoPlayerCountDownTimer.seek(0);

		if (rlSlidShow != null && rlSlidShow.getVisibility() == View.VISIBLE) {
			mFlingGallery.setVisibility(View.VISIBLE);
			getSupportActionBar().show();
			ivSlidShow.setVisibility(View.INVISIBLE);
			rlSlidShow.setVisibility(View.INVISIBLE);
			slidshow = false;
		}

		return false;
	}

	private void resumeCountDown() {
		autoPlayerCountDownTimer.seek(0);
		autoPlayerCountDownTimer.start();
	}

	private void initAnimations() {

		rotate = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		alpha = new AlphaAnimation(0, 1);

		rotate.setFillAfter(true);
		rotate.setDuration(1000);

		scale.setFillAfter(true);
		scale.setDuration(500);
		scale.setStartOffset(500);

		alpha.setFillAfter(true);
		alpha.setDuration(500);
		alpha.setStartOffset(500);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		translate = new TranslateAnimation(dm.widthPixels, 0, dm.heightPixels, 0);
		translate.setFillAfter(true);
		translate.setDuration(500);
		translate.setStartOffset(500);
	}

	private int getNextIndex() {
		if(Constants.files.size() > 0){
			try{
				int resIndex = new Random().nextInt() % Constants.files.size();
				resIndex = Math.abs(resIndex);
				if (resIndex > Constants.files.size() - 1) {
					resIndex = 0;
				}
				
				return resIndex;
			}catch(Exception e){
				return 0;
			}
		}else{
			return 0;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		updateIndex(arg0);
	}

}
