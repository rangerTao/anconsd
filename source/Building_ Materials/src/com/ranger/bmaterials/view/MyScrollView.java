package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	public boolean eventover;
	public static boolean ismoving;
	
	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		//Log.i("WWWWW", "ccccccccccccc"+eventover);
		if(!eventover)
			ismoving = true;
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	

}
