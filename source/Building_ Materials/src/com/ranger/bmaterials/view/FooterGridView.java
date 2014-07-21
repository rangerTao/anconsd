package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public class FooterGridView extends GridView {
	public FooterGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private View footer;

	public final void hideFooter() {
		if (footer != null)
			footer.setVisibility(View.GONE);
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

	public void addFooter(String footerStr) {
		footer = View.inflate(getContext(), R.layout.loading_layout, null);
		TextView subView = (TextView) footer.findViewById(R.id.loading_text);
		if (footerStr == null)
			subView.setText(R.string.pull_to_refresh_refreshing_label);
		else
			subView.setText(footerStr);
		footer.setVisibility(View.GONE);
		((ViewGroup) getParent()).addView(footer);
	}
}
