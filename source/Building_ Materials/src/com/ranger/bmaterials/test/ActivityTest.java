package com.ranger.bmaterials.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.InstrumentationTestCase;

public class ActivityTest extends InstrumentationTestCase{

	private Instrumentation mInstrumentation;
	private Activity mCurrentActivity;
	private Activity mSessionActivity;
	private ActivityMonitor mActivityMonitor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if(mInstrumentation == null){
			mInstrumentation = getInstrumentation();
		}
		
		mSessionActivity = null;
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
}
