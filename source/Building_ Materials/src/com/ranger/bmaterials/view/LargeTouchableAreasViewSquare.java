package com.ranger.bmaterials.view;

import com.ranger.bmaterials.R;

import android.content.Context;
import android.util.AttributeSet;

public class LargeTouchableAreasViewSquare extends LargeTouchableAreasViewBase {

	public LargeTouchableAreasViewSquare(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected int getLayoutViewId() {
		return R.layout.square_activity_snapnumber_list_item_view;
	}

	@Override
	protected int getButtonId() {
		return R.id.square_activity_snapnumber_button;
	}

}
