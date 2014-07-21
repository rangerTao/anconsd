package com.ranger.bmaterials.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.tools.UIUtil;

public final class SectionScrollView extends ScrollEventScrollerView {
	private View section_layout, gloss_section_layout;
	private int statusBar_h;
	private Context cx;
	private int top_offset;
	private View home_section_newgames;
	private View home_tab_mustplay;
	private View home_tab_competition;
	private View home_tab_class;

	public SectionScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.cx = context;
	}

	public void setGlossSectionLayout(View gloss_section_layout) {
		this.gloss_section_layout = gloss_section_layout;
		gloss_section_layout.getBackground().setAlpha(242);

		home_section_newgames = gloss_section_layout.findViewById(R.id.home_section_newgames);
		home_tab_mustplay = gloss_section_layout.findViewById(R.id.home_section_mustplay);
		home_tab_competition = gloss_section_layout.findViewById(R.id.home_section_competition);
		home_tab_class = gloss_section_layout.findViewById(R.id.home_section_classgames);

		gloss_section_layout.setVisibility(View.GONE);

	}

	public final View getSection_layout() {
		return section_layout;
	}

	public final void setSection_layout(View section_layout) {
		this.section_layout = section_layout;
	}

	public final View getGloss_section_layout() {
		return gloss_section_layout;
	}

	public void setButtonOnClickListener(OnClickListener clickListener) {
		home_section_newgames.setOnClickListener(clickListener);
		home_tab_mustplay.setOnClickListener(clickListener);
		home_tab_competition.setOnClickListener(clickListener);
		home_tab_class.setOnClickListener(clickListener);
	}

	public void setTopOffset(int top_offset) {
		this.top_offset = top_offset;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);

		// 快速入口section
		if (section_layout != null) {
			int section_layout_y = getViewLocation(section_layout)[1];
			boolean isShow = gloss_section_layout.isShown();
			if (section_layout_y <= 0 && !isShow) {
				gloss_section_layout.setVisibility(View.VISIBLE);
				TextView tv_new_games_count = (TextView) gloss_section_layout.findViewById(R.id.tv_new_games_count);
				SharedPreferences sp = cx.getSharedPreferences("startdata", Context.MODE_PRIVATE);
				String adCount = sp.getString("addedcount", "0");
				if (!adCount.equals("0") && !TextUtils.isEmpty(adCount)) {
					tv_new_games_count.setText(adCount);
					tv_new_games_count.setVisibility(View.VISIBLE);
				} else {
					tv_new_games_count.setVisibility(View.INVISIBLE);
				}
				section_layout.setVisibility(View.INVISIBLE);
			} else if (section_layout_y > 0 && isShow) {
				gloss_section_layout.setVisibility(View.GONE);
				section_layout.setVisibility(View.VISIBLE);
			}
		}
	}

	/** 获取view的屏幕坐标 */
	private int[] getViewLocation(View v) {
		int[] location = new int[2];
		if (v != null)
			v.getLocationOnScreen(location);
		if (statusBar_h == 0)
			statusBar_h = UIUtil.getStatusBarHeight((Activity) cx);
		location[1] -= statusBar_h + top_offset;
		return location;
	}

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	/**
	 * 能够兼容ViewPager的ScrollView
	 * 
	 * @Description: 解决了ViewPager在ScrollView中的滑动反弹问题
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

        try{
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xDistance = yDistance = 0f;
                    xLast = ev.getX();
                    yLast = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float curX = ev.getX();
                    final float curY = ev.getY();

                    xDistance += Math.abs(curX - xLast);
                    yDistance += Math.abs(curY - yLast);
                    xLast = curX;
                    yLast = curY;

                    if (xDistance > yDistance) {
                        return false;
                    }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

		return super.onInterceptTouchEvent(ev);
	}

}
