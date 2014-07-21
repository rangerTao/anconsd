package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ranger.bmaterials.tools.UIUtil;

public class AdsPoints extends LinearLayout {
	private int normalBgRes, selectBgRes;
	private Context cx;

	public AdsPoints(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setOrientation(HORIZONTAL);
		this.cx = context;
	}

	public final void setNormalBgRes(int normalBgRes) {
		this.normalBgRes = normalBgRes;
	}

	public final void setSelectBgRes(int selectBgRes) {
		this.selectBgRes = selectBgRes;
	}

	public void setChildCount(int count) {
		removeAllViews();

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(0, 0, UIUtil.dip2px(cx, 5), 0);
		for (int i = 0; i < count; i++) {
			ImageView iv = new ImageView(cx);

			if (i == count - 1) {
				lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				lp.setMargins(0, 0, 0, 0);
			}

			iv.setLayoutParams(lp);
			iv.setImageResource(normalBgRes);

			addView(iv);
		}

		getChildView(0).setImageResource(selectBgRes);

	}

	public void change(int pos) {
		int count = getChildCount();
		if (count == 2)
			pos = pos % count;

		for (int i = 0; i < count; i++) {
			getChildView(i).setImageResource(
					i == pos ? selectBgRes : normalBgRes);
		}
	}

	private ImageView getChildView(int pos) {
		return (ImageView) getChildAt(pos);
	}
}
