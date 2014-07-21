package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;

import com.ranger.bmaterials.R;

public class LargeTouchableAreasViewSearch extends LargeTouchableAreasViewBase {

	public LargeTouchableAreasViewSearch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected int getLayoutViewId() {
		return R.layout.search_result_list_item_view;
	}

	@Override
	protected int getButtonId() {
		return R.id.search_item_button;
	}

	
}
