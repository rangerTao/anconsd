package com.andconsd.ui;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.MenuItem;
import com.andconsd.R;
import com.baidu.mobads.appoffers.OffersManager;
import com.baidu.mobads.appoffers.OffersView;

public class PromotionActivity extends BaseActivity{

	public static int THEME = R.style.Theme_Sherlock;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(THEME);

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_blank_activity);

		LinearLayout llMain = (LinearLayout) findViewById(R.id.llMain);

		OffersView ov = new OffersView(this, false);
		LinearLayout.LayoutParams rllp = new LinearLayout.LayoutParams(-1, -1);
		llMain.addView(ov, rllp);

//		OffersManager.setAppSec("debug");
//		OffersManager.setAppSid("debug");

		initPopupController();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initPopupController() {

		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
