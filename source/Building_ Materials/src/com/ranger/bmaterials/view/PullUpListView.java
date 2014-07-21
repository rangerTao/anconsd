package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class PullUpListView extends ListView implements OnScrollListener {

	public PullUpListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private View footer;

	public void setFooter(View footer) {
		if (footer == null)
			return;

		if (this.footer != null)
			removeFooter();

		footer.setVisibility(View.INVISIBLE);
		this.footer = footer;

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout f = new RelativeLayout(getContext());
		f.addView(footer, lp);
		addFooterView(f, null, false);
	}

	public void removeFooter() {
		ViewGroup parent = (ViewGroup) this.footer.getParent();
		if (parent != null)
			removeFooterView(parent);
	}

	public final void hideFooter() {
		if (footer != null) {
			footer.setVisibility(View.GONE);
		}
	}

	public final void showFooter() {
		if (footer != null)
			footer.setVisibility(View.VISIBLE);
	}

	public final boolean isFooterVisible() {
		if (footer != null)
			return footer.isShown();
		return false;
	}

	private ScrollBottomListener scrollBottomListener;
	private boolean mLastItemVisible;

	public final ScrollBottomListener getScrollBottomListener() {
		return scrollBottomListener;
	}

	public final void setScrollBottomListener(
			ScrollBottomListener scrollBottomListener) {
		this.scrollBottomListener = scrollBottomListener;
		setOnScrollListener(this);
	}

	public interface ScrollBottomListener {
		void OnScrollBottom();
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (null != scrollBottomListener) {
			mLastItemVisible = (totalItemCount > 0)
					&& (firstVisibleItem + visibleItemCount >= totalItemCount);
		}
	}

	public final void onScrollStateChanged(AbsListView view, int state) {
		if (null != scrollBottomListener && mLastItemVisible
				&& state == SCROLL_STATE_IDLE) {
			scrollBottomListener.OnScrollBottom();
		}

	}

}
