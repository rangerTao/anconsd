package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.ui.BMMineFragment;
import com.ranger.bmaterials.ui.BMSearchFragment;

public class CustomFragmentTabHost extends FragmentTabHost {

//	private TextView msgNumTv;
	private View msgNumTv;

	// private ImageView coinTipView;

	public CustomFragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void initTab() {

		// 首页
		TabSpec spec = newTabSpec("首页");
		View v = getIndicatorView(R.layout.tab_item_home_hall);
		spec.setIndicator(v);
		addTab(spec,BMSearchFragment.class, null);

		// 排行
		spec = newTabSpec("排行");
		v = getIndicatorView(R.layout.tab_item_hot_hall);

		spec.setIndicator(v);
		addTab(spec, BMMineFragment.class, null);

	}

	private boolean isMsgNumTvShown;

	public void showMsgNumTv(boolean isShow, String unreadMsg) {
		if (isShow) {
			if (unreadMsg == null || "0".equals(unreadMsg)) {
				msgNumTv.setVisibility(View.INVISIBLE);
				isMsgNumTvShown = false;
			} else {
				isMsgNumTvShown = true;
//				msgNumTv.setText(unreadMsg);
				msgNumTv.setVisibility(View.VISIBLE);
				// dismissCoinTip();
			}
		} else {
		}
	}

	public boolean isMsgNumTvShown() {
//		return !msgNumTv.getText().toString().equals("") && msgNumTv.isShown();
		return msgNumTv.isShown();
	}

	// public void dismissCoinTip() {
	// if (coinTipView != null) {
	// coinTipView.setVisibility(View.INVISIBLE);
	// }
	// }

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Exception e) {

		}
	}

	// public void showCoinTip() {
	// if (!isMsgNumTvShown && HeaderCoinAnimationTask.coinNum.get() !=
	// HeaderCoinAnimationTask.NONE_TIP) {
	// coinTipView.setVisibility(View.VISIBLE);
	// }
	// }

	public View getIndicatorView(int resid) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(getContext()).inflate(resid, null);
		// ImageView iv5 = (ImageView)
		// v.findViewById(R.id.iv_tab_item_game_hall);
		// iv5.setImageResource(resid);
		// TextView tv5 = (TextView) v.findViewById(R.id.tv_tab_item_game_hall);
		// tv5.setText(spec.getTag());
		return v;
	}

	private String current_tab_id;

	public void init(FragmentManager fm) {
		setup(getContext(), fm, R.id.realtabcontent);
	}

	public static final int TAB_HOT_ID = R.string.tab_name_hot_hall;
	public static final int TAB_CLASS_ID = R.string.tab_name_class_hall;
	public static final int TAB_HOME_ID = R.string.tab_name_home_hall;
	public static final int TAB_DISCOVER_ID = R.string.tab_name_discover_hall;
	public static final int TAB_MINE_ID = R.string.mine_title;

	public Fragment getFragment(FragmentManager fm, int tabId) {
		return getFragment(fm, getContext().getString(tabId));
	}

	public Fragment getFragment(FragmentManager fm, String tabId) {
		if (tabId != null && !tabId.equals(""))
			return fm.findFragmentByTag(tabId);
		return null;
	}

	public Fragment getCurrentFragment(FragmentManager fm) {
		if (current_tab_id != null && !current_tab_id.equals(""))
			return fm.findFragmentByTag(current_tab_id);
		return null;
	}
}
