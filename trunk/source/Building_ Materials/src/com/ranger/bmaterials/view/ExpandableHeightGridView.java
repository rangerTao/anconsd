package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class ExpandableHeightGridView extends FooterGridView {

	public ExpandableHeightGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean isATMOST = true;

	public final ScrollView getParentScrollView() {
		return parent_sv;
	}

	public final void setParentScrollView(ScrollView parent_sv) {
		this.parent_sv = parent_sv;
	}

	private ScrollView parent_sv;

	// 兼容scollerview
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Calculate entire height by providing a very large height hint.
		// But do not use the highest 2 bits of this integer; those are
		// reserved for the MeasureSpec mode.
		if (isATMOST) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			if (getMeasuredHeight() != 0) {
				ViewGroup.LayoutParams params = getLayoutParams();
				params.height = getMeasuredHeight();
			}
		} else
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		

	}

	public final boolean isATMOST() {
		return isATMOST;
	}

	public final void setATMOST(boolean isATMOST) {
		this.isATMOST = isATMOST;
	}

	//解决某些机型上gridview会被拖动的问题
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return super.onTouchEvent(event);
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			return super.onTouchEvent(event);
		}
		return false;
	}

}
