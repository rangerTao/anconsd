package com.ranger.bmaterials.ui;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.BMMineCollectionPagerAdapter;
import com.ranger.bmaterials.adapter.BMMineCollectionPagerAdapter.PageCallback;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;

public class BMMineCollectionActivity extends FragmentActivity implements PageCallback, OnClickListener {

	private static final String TAG = "mineCollectionActivity";
	private ViewPager pager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine_activity_collection2);
		
		findViewById(R.id.btn_back).setOnClickListener(this);
		
		setupViews();
	}

	@TargetApi(9)
	private void setupViews(){
		pager = (ViewPager) findViewById(R.id.mine_activity_pager);
		pager.setAdapter(new BMMineCollectionPagerAdapter(getSupportFragmentManager(), this));
		
		tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs_indicator);

		if (DeviceUtil.hasGingerbread()) {
		    pager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		tabStrip.setViewPager(pager);
	}

	@Override
	public Fragment getFragment(int page) {
		Fragment fragment = null;
		
		switch (page) {
		case 0:
			BMCompanyInfoFragment tmp = new BMCompanyInfoFragment();
			tmp.tabStrip = this.tabStrip;
			fragment = tmp;
			
			break;
		case 1:
			BMProductsFragment tmp2 = new BMProductsFragment();
			tmp2.tabStrip = this.tabStrip;
			fragment = tmp2;
		default:
			break;
		}
		
		return fragment;
	}
	
	String[] titles = {"材料","供应商"};
	private PagerSlidingTabStrip tabStrip;
	
	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
	
	@Override
	public int getPageCount() {
		return 2;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.img_back) {
			this.finish();
		}
	}
}
