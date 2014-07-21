package com.ranger.bmaterials.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class SlowScrollViewpager extends ViewPager {
	private int default_duration = 600;

	public SlowScrollViewpager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
//		reflectScroller(default_duration);
	}

	public SlowScrollViewpager(Context context, int duration) {
		super(context);
		// TODO Auto-generated constructor stub
//		reflectScroller(duration);
	}

	private void reflectScroller(int duration) {
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			FixedSpeedScroller mScroller = new FixedSpeedScroller(getContext(),
					new LinearInterpolator(), duration);
			mField.set(this, mScroller);
		} catch (Exception e) {
		}
	}

	public SlowScrollViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		reflectScroller(default_duration);
	}

	private class FixedSpeedScroller extends Scroller {
		private int mDuration;// viewpager切换动画速度

		public FixedSpeedScroller(Context context, Interpolator interpolator,
				int duration) {
			super(context, interpolator);
			this.mDuration = duration;
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}
	}
}
