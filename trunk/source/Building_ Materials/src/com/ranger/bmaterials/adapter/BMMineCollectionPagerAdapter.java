package com.ranger.bmaterials.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BMMineCollectionPagerAdapter extends FragmentPagerAdapter {

	public interface PageCallback{
		Fragment getFragment(int page);
		int getPageCount();
		CharSequence getPageTitle(int position) ;
	}
	
	private PageCallback callback ;
	
	public BMMineCollectionPagerAdapter(FragmentManager fm, PageCallback callback) {
		super(fm);
		this.callback = callback ;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return callback.getPageTitle(position);
	}
	
	@Override
	public Fragment getItem(int page) {
		return callback.getFragment(page);
	}

	@Override
	public int getCount() {
		return callback.getPageCount();
	}

}
