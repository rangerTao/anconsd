package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ScrollEventScrollerView extends ScrollView {

	private ScrollListener scrollListener;
	private boolean isScroll2Bottom;

	public final ScrollListener getScrollListener() {
		return scrollListener;
	}

	public final void setScrollListener(ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	public ScrollEventScrollerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		if (t + getHeight() >= computeVerticalScrollRange()) {
			// 滚动到最下方
			if (scrollListener != null && !isScroll2Bottom) {
				isScroll2Bottom = true;
				scrollListener.OnScrollBottom();
			}
		} else
			isScroll2Bottom = false;
	}

	public interface ScrollListener {
		void OnScrollBottom();
	}
}
