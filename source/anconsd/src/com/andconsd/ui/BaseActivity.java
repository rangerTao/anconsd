package com.andconsd.ui;

import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.mobstat.StatService;

public class BaseActivity extends SherlockActivity {

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		StatService.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		StatService.onResume(this);
		super.onResume();
	}

}