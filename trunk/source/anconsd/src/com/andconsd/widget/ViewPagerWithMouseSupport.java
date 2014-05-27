package com.andconsd.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class ViewPagerWithMouseSupport extends ViewPager implements OnGestureListener {

	Context mContext;

	private GestureDetector mGestureDetector;

	public ViewPagerWithMouseSupport(Context context) {
		super(context);
		mContext = context;
		initDetector();
	}

	public ViewPagerWithMouseSupport(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initDetector();
	}

	private void initDetector() {
		mGestureDetector = new GestureDetector(mContext, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		
		Log.d("TAG",  "on down");
		
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.d("TAG",  "on fling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.d("TAG",  "on long press");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		Log.d("TAG",  "on scroll");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}
