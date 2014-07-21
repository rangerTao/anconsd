package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MyPopupWindows extends PopupWindowCompat {

	public MyPopupWindows(Context cx, View contentView) {
		super(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		setBackgroundDrawable(new BitmapDrawable(cx.getResources()));// fix bug
		setOutsideTouchable(true);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		getContentView().invalidate();//fix bug
		
		super.dismiss();
	}

	public void showAtBottom(View parent, int xoff, int yoff, int animationStyle) {
		// TODO Auto-generated method stub
		setAnimationStyle(animationStyle);
		showAsDropDown(parent, xoff, yoff);
	}

	public void showAtBottom(View parent, int animationStyle) {
		showAtBottom(parent, 0, 0, animationStyle);
	}

}
