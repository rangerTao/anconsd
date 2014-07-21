package com.ranger.bmaterials.view.slideexpand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MyExpandListView extends ListView{
	
	public MyExpandListView(Context context) {
		super(context);
	}

	public MyExpandListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyExpandListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
		if (isMeasure) {
			if (getMeasuredHeight() != 0) {
				ViewGroup.LayoutParams params = getLayoutParams();
				params.height = getMesuHeight();
			}
			isMeasure = false;
		}
	}
	
	private int getMesuHeight() {
		ListAdapter listAdapter = this.getAdapter();
		if (listAdapter == null) {
			return getMeasuredHeight();
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, this);
			listItem.measure(0, 0);
			listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}
		
		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.height = totalHeight
				+ (this.getDividerHeight() * (listAdapter.getCount() - 1))
				+ 15;
		this.setLayoutParams(params);
		return params.height;
	}
	
	private boolean isMeasure;


	public final void setMeasure(boolean isMeasure) {
		this.isMeasure = isMeasure;
	}
	
	/**
	 * Interface for callback to be invoked whenever an action is clicked in the
	 * expandle area of the list item.
	 */
	public interface OnItemSizeChangeListener {
		public void onItemSizeChange();
	}	
}