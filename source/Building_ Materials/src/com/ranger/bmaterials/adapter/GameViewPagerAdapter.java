package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class GameViewPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragmentList;

	public GameViewPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragmentList) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragmentList = fragmentList;
	}

	private String[] titles = { "推荐", "排行", "分类", "专题" };

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titles[position];
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return (fragmentList == null || fragmentList.size() == 0) ? null
				: fragmentList.get(arg0);
	}
}
