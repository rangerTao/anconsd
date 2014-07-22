package com.ranger.bmaterials.ui;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.BMMineCollectionPagerAdapter;
import com.ranger.bmaterials.adapter.BMMineCollectionPagerAdapter.PageCallback;
import com.ranger.bmaterials.netresponse.BMCompanyInfoResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;

public class BMCompanyInfoActivity extends FragmentActivity implements PageCallback, OnClickListener {

    private static final String TAG = "mineCollectionActivity";
    public static final String USER_ID = "userid";
    public static final String USER_NAME = "name";
    private ViewPager pager;

    static BMCompanyInfoResult comInfo;

    private TextView comName;

    private int userid;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        userid = getIntent().getIntExtra(USER_ID, 0);
        name = getIntent().getStringExtra(USER_NAME);

        setContentView(R.layout.mine_activity_collection2);

        findViewById(R.id.btn_back).setOnClickListener(this);

        comName = (TextView) findViewById(R.id.bm_tv_company_name);
        comName.setText(name);

        setupViews();
    }

    @TargetApi(9)
    private void setupViews() {
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

        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID,userid);

        switch (page) {
            case 0:
                BMCompanyInfoFragment tmp = new BMCompanyInfoFragment();
                tmp.setArguments(bundle);
                tmp.tabStrip = this.tabStrip;
                fragment = tmp;
                break;
            case 1:
                BMProductsFragment tmp2 = new BMProductsFragment();
                tmp2.tabStrip = this.tabStrip;
                tmp2.setArguments(bundle);
                fragment = tmp2;
                break;
            case 2:
                BMCompanyLevelFragment tmp3 = new BMCompanyLevelFragment();
                tmp3.tabStrip = this.tabStrip;
                tmp3.setArguments(bundle);
                fragment = tmp3;
            default:
                break;
        }

        return fragment;
    }

    String[] titles = {"公司简介", "供应产品", "诚信档案"};
    private PagerSlidingTabStrip tabStrip;

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getPageCount() {
        return 3;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.img_back) {
            this.finish();
        }
    }
}
