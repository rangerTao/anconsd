package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ExpandablePullUpListView extends PullUpListView {

	public ExpandablePullUpListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private boolean isMeasure;

	// 兼容scollerview
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Calculate entire height by providing a very large height hint.
		// But do not use the highest 2 bits of this integer; those are
		// reserved for the MeasureSpec mode.
		if (isMeasure) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			if (getMeasuredHeight() != 0) {
				ViewGroup.LayoutParams params = getLayoutParams();
				params.height = getMeasuredHeight();
			}

			isMeasure = false;
		} else
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	public final void setMeasure(boolean isMeasure) {
		this.isMeasure = isMeasure;
	}

}
