package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.ranger.bmaterials.R;

public class FixTouchView extends FrameLayout {

	public FixTouchView(Context context) {
		super(context);
	}

	public FixTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		try {
			return true;// MTJ BUG FIX
		} catch (Exception e) {
			return false;
		}
	}
}
