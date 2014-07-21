package com.ranger.bmaterials.tools;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

//自定义触控区域
public abstract class MyTouchDelegate {
	public abstract void postDelegateArea(Rect delegateArea, View v);

	public void setTouchDelegate(final View v) {
		final View parent = (View) v.getParent();
		parent.post(new Runnable() {
			@Override
			public void run() {
				Rect delegateArea = new Rect();
				v.getHitRect(delegateArea);

				postDelegateArea(delegateArea, v);

				TouchDelegate expandedArea = new TouchDelegate(delegateArea, v);
				parent.setTouchDelegate(expandedArea);
			}
		});
	}
}
