package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/*详情页游戏标题如果过长显示跑马灯*/
public class AlwaysMarqueeTextView extends TextView {

	public AlwaysMarqueeTextView(Context context) {
		super(context);
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
