package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class HomeGuideViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> childs;

	public HomeGuideViewPagerAdapter(ArrayList<View> childs) {
		this.childs = childs;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		View v = childs.get(position);

		// Now just add View to ViewPager and return it
		// if (v.getParent() == null)
		container.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(childs.get(position));// view不销毁
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getCount() {
		return childs.size();
	}

}
