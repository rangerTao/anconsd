package com.ranger.bmaterials.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.AbstractListAdapter.OnListItemClickListener;
import com.ranger.bmaterials.adapter.BMProvinceAdapter;
import com.ranger.bmaterials.adapter.BMSearchResultAdapter;
import com.ranger.bmaterials.adapter.ProductBandAdapter;
import com.ranger.bmaterials.adapter.ProductPinpaiAdapter;
import com.ranger.bmaterials.adapter.SuggestAdapter;
import com.ranger.bmaterials.app.BMApplication;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.db.CommonDaoImpl;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.netresponse.BMSearchResult;
import com.ranger.bmaterials.netresponse.BandAndModelResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.ui.gametopic.BMProductDetailActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.work.LoadingTask;

public class BMSearchResultActivity extends Activity implements
/* IRequestListener, */OnClickListener, OnItemClickListener, CompoundButton.OnCheckedChangeListener, IRequestListener, ProductBandAdapter.onCategoryClickListener, ProductBandAdapter.onBandClickListener {

    private static final String TAG = "SearchResultActivity";

    protected static final boolean DEBUG = true;

    private static String keyword;
    private int pid;
    private String pname;

    public static final String ARG_KEYWORD = "keywords";
    public static final String ARG_PID = "pid";
    public static final String ARG_PNAME = "pname";

    private PullToRefreshListView searchResultLayout;
    private ViewGroup searchNoResultLayout;
    private View loadingView;

    private BMSearchResultAdapter searchResultAdapter;
    private int requestId;

    public static SlidingMenu menu;

    private CheckBox cbIdentifyProvider;
    private Button cbGroupProvider;

    private TextView edit_search;

    /**
     * 推荐的GridView
     */
    private GridView recomendGv;

    private View errorView;

    private View footer;

    /**
     * 分页的页码
     */
    private int currentPage = 0;

    private View firstMenu;
    private View secondMenu;
    private ListView lv_province_list;
    private BMProvinceAdapter bpa;

    private android.os.Handler mHandler = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.search_result_activity);

        Intent intent = getIntent();

        if (intent != null) {
            keyword = intent.getStringExtra(ARG_KEYWORD);
            pid = BMApplication.getAppInstance().getSelectedProvince();
            pname = BMApplication.getAppInstance().getSelectedProvinceName();
//            pid = intent.getIntExtra(ARG_PID, 0);
//            pname = intent.getStringExtra(ARG_PNAME);
        }

        restore(savedInstanceState);

        initTitleBar();
        initView();

        tvProvince.setText(pname.equals("") ? "全国" : pname);
        edit_search.setText(keyword);

//        showLoadingProgressView();

        registerListener();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                search(currentPage + 1, false);

                loadingView.setVisibility(View.VISIBLE);
                initSlidingMenu();

                loadingView.setVisibility(View.GONE);


            }
        }, 1000);

    }

    private String newkeyword;
    private int newpid;
    private String newpname;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        newkeyword = intent.getStringExtra(ARG_KEYWORD);
        newpid = intent.getIntExtra(ARG_PID, 0);
        newpname = intent.getStringExtra(ARG_PNAME);

        if(!keyword.equals(newkeyword)){
            keyword = newkeyword;
//            pid = newpid;
//            pname = newpname;
            edit_search.setText(keyword);
//            showLoadingProgressView();
            initParams();
            search();
            loadBrandAndModel();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    List<String> suggestWords = null;

    private void loadHistroyData() {
        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {
                return CommonDaoImpl.getInstance(getApplicationContext()).getKeywords();
            }

            protected void onPostExecute(java.util.List<String> result) {
                if (result == null || result.size() == 0) {
                    initSuggest(new ArrayList<String>());
                } else {
                    initSuggest(result);
                }
            }

        }.execute();
    }

    private SuggestAdapter suggestAdapter;

    public static ListView lvRecom;

    private void initSuggest(List<String> keywords) {

        if (lvRecom == null) {
            lvRecom = (ListView) findViewById(R.id.ll_search_recom);
            suggestWords = keywords;
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int code = event.getKeyCode();
            if (code == KeyEvent.KEYCODE_BACK) {

                if (lvRecom != null && lvRecom.getVisibility() == View.VISIBLE) {
                    lvRecom.setVisibility(View.GONE);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void showDropdown() {
        suggestAdapter = new SuggestAdapter(getApplicationContext(), suggestWords, 5);
        lvRecom.setAdapter(suggestAdapter);
        suggestAdapter.notifyDataSetChanged();

        lvRecom.setVisibility(View.VISIBLE);
        lvRecom.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit_search.setText(suggestWords.get(position));
                search();
            }
        });
    }

    TextView tvProvince;

    public void setCityName(String name) {

        tvProvince.setText(name);
    }

    private void getProvinces() {
        NetUtil.getInstance().requestForProvices(this, new IRequestListener() {
            @Override
            public void onRequestSuccess(BaseResult responseData) {
                BMProvinceListResult blr = (BMProvinceListResult) responseData;

                if (blr.getTag().equals(Constants.NET_TAG_GET_PROVINCE + "")) {
                    bpa = new BMProvinceAdapter(getApplicationContext(), blr.getProviceList());
                    bpa.setProvince(pname);
                    bpa.setOnProvinceSelectedListener(new BMProvinceAdapter.onProvinceSelectedListener() {
                        @Override
                        public void onSelected(int position,String name) {
//                            BMApplication.getAppInstance().setSelectedProvince(position,name);
                        }
                    });
                    lv_province_list.setAdapter(bpa);
                    lv_province_list.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            BMProvinceListResult.ProviceItem pi = (BMProvinceListResult.ProviceItem) parent.getAdapter().getItem(position);

                            if(pi.getId() == BMApplication.getAppInstance().getSelectedProvince()){
                                pid = 0;
                                pname = "全国";
                            }else{

                                if (pi != null) {
                                    try {
                                        pid = pi.getId();
                                        pname = pi.getName();
                                        BMApplication.getAppInstance().setSelectedProvince(pid,pname);

                                        setCityName(pi.getName());

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            setCityName(pname);

                            bpa.setProvince(pname);
                            bpa.notifyDataSetChanged();

                            menu.toggle();

                            search();
                            loadBrandAndModel();
                        }
                    });
                    bpa.setOnListItemClickListener(new OnListItemClickListener() {
                        @Override
                        public void onItemIconClick(View view, int position) {

                        }

                        @Override
                        public void onItemButtonClick(View view, int position) {

                        }
                    });
                }
            }

            @Override
            public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

            }
        });
    }

    ExpandableListView bandList;
    ExpandableListView typeList;

    private OnClickListener cateAndBandClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.rl_cate:
                case R.id.rl_band:
                case R.id.rl_band_main:
                    if(bandList != null && bandList.getVisibility() == View.VISIBLE){
                        bandList.setVisibility(View.GONE);
                        rl_band.setVisibility(View.GONE);
                        ll_band_main.setVisibility(View.VISIBLE);
                        typeList.expandGroup(0);
                    }else{
                        rl_band.setVisibility(View.VISIBLE);
                        ll_band_main.setVisibility(View.GONE);
                        bandList.setVisibility(View.VISIBLE);
                        if(typeList != null){
                            typeList.collapseGroup(0);
                        }
                    }
                    break;
            }
        }
    };

    RelativeLayout rl_band;
    LinearLayout ll_band_main;
    RelativeLayout rl_band_main;

    TextView tv_selected_cate;
    TextView tv_selected_band;
    TextView tv_selected_band_main;

    private void initSlidingMenu() {

        firstMenu = getLayoutInflater().inflate(R.layout.side_menu, null);
        lv_province_list = (ListView) firstMenu.findViewById(R.id.bm_province_list);
        secondMenu = getLayoutInflater().inflate(R.layout.band_menu, null);
        secondMenu.findViewById(R.id.tv_btn_band_ok).setOnClickListener(this);
        bandList = (ExpandableListView) secondMenu.findViewById(R.id.elv_band);
        typeList = (ExpandableListView) secondMenu.findViewById(R.id.elv_cate);
        rl_cate = (RelativeLayout) secondMenu.findViewById(R.id.rl_cate);
        rl_cate.setOnClickListener(cateAndBandClick);
        rl_band = (RelativeLayout) secondMenu.findViewById(R.id.rl_band);
        ll_band_main = (LinearLayout) secondMenu.findViewById(R.id.ll_band_main);
        rl_band_main = (RelativeLayout) secondMenu.findViewById(R.id.rl_band_main);
        rl_band_main.setOnClickListener(cateAndBandClick);
        rl_band.setOnClickListener(cateAndBandClick);

        tv_selected_band = (TextView) secondMenu.findViewById(R.id.tv_band_selected_name);
        tv_selected_cate = (TextView) secondMenu.findViewById(R.id.tv_type_selected_name);
        tv_selected_band_main = (TextView) secondMenu.findViewById(R.id.tv_band_selected_name_main);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT_RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int offset = (dm.widthPixels / 3) * 1;
        menu.setBehindOffset(offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(firstMenu);
        menu.setSecondaryMenu(secondMenu);

        getProvinces();

        loadBrandAndModel();
    }

    private void restore(Bundle savedInstanceState) {
        try {
            if (savedInstanceState != null
                    && savedInstanceState.containsKey(PAGE)) {
                currentPage = savedInstanceState.getInt(PAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTitleBar() {
        TextView backView = (TextView) findViewById(R.id.btn_province);
        backView.setText(pname.equals("") ? "全国" : pname);
//        backView.setOnClickListener(this);
    }

    boolean isLoadingMore = false;
    boolean hasMore = true;

    OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

        @Override
        public void onLastItemVisible() {
            if (Constants.DEBUG)
                Log.i(TAG, "onLastItemVisible");
            if (DeviceUtil.isNetworkAvailable(getApplicationContext())
                    && !getLoadlingMoreState() && hasMore()) {
                setFooterVisible(true);
                setLoadlingMoreState(true);
                search(currentPage + 1, true);
            } else {
                // searchResultLayout.onRefreshComplete();
            }
        }
    };

    private View createFooter() {
        View view = View.inflate(this, R.layout.loading_layout, null);
        TextView subView = (TextView) view.findViewById(R.id.loading_text);
        subView.setText(R.string.pull_to_refresh_refreshing_label);
        view.setVisibility(View.GONE);
        return view;
    }

    private ForegroundColorSpan fcc_keyword = new ForegroundColorSpan(Color.parseColor("#478ddc"));
    private ForegroundColorSpan fcc_no_data = new ForegroundColorSpan(Color.parseColor("#989898"));

    private View footer_view;

    private View createNoMoreDataFooter(final String key) {

        if (footer_view == null) {
            footer_view = View.inflate(getApplicationContext(), R.layout.layout_listview_footer_nomoredata, null);
        }
        final String default_keyword = getString(R.string.search_result_no_more_data);

        SpannableString ss_default = new SpannableString(default_keyword + "  \"" + key + "\"");
        ss_default.setSpan(fcc_no_data, 0, default_keyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss_default.setSpan(fcc_keyword, default_keyword.length(), ss_default.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        TextView tvFooter = (TextView) footer_view.findViewById(R.id.tv_footer_no_data);
        tvFooter.setText(ss_default);

        footer_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                et_search.setText(key);
                showLoadingProgressView();
                search();
            }
        });

        return footer_view;
    }

    public void menuProvinceOnclick(View view){
        if(!menu.isMenuShowing()){
            menu.showMenu(true);
        }
    }

    private void initView() {

        if (tvProvince == null) {
            tvProvince = (TextView) findViewById(R.id.btn_province);
        }

        edit_search = (TextView) findViewById(R.id.edit_search);
        edit_search.setOnClickListener(this);

        findViewById(R.id.btn_search).setOnClickListener(this);

        searchResultLayout = (PullToRefreshListView) findViewById(R.id.layout_search_result_list);
        searchResultLayout.setOnRefreshListener(new MyOnRefreshListener2());
        searchResultLayout.setOnItemClickListener(this);
        searchResultLayout
                .setOnLastItemVisibleListener(OnLastItemVisibleListener);
        footer = createFooter();
        searchResultLayout.getRefreshableView().addFooterView(footer);

        searchNoResultLayout = (ViewGroup) findViewById(R.id.layout_search_subview_no_result);
        recomendGv = (GridView) searchNoResultLayout
                .findViewById(R.id.search_recomend_gv);
        // recomendGv.setScrollContainer(false);
        loadingView = findViewById(R.id.progress_bar);

        errorView = findViewById(R.id.error_hint);
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(this);

        cbIdentifyProvider = (CheckBox) findViewById(R.id.bm_cb_user_identify);
        cbIdentifyProvider.setOnCheckedChangeListener(this);
        cbGroupProvider = (Button) findViewById(R.id.bm_btn_group_provider);
        cbGroupProvider.setOnClickListener(this);

        findViewById(R.id.select_band).setOnClickListener(this);
        findViewById(R.id.btn_back_bottom).setOnClickListener(this);
        findViewById(R.id.search_clear).setOnClickListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        switch (id) {
            case R.id.bm_cb_user_identify:
                isCredit = isChecked ? 1 : 0;
                search();
                break;
            case R.id.bm_btn_group_provider:
                break;
        }

    }

    @Override
    public void onRequestSuccess(BaseResult responseData) {

    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

    }

    @Override
    public void onCategoryClick(View view,int gpos,int cpos) {

        if (view != null) {
            String cate = (String) view.getTag();
            smalltype = cate;
            if(cate.equals(tv_selected_cate.getText().toString())){
                cate ="全部";
                gpos = -1;
                cpos = -1;
                smalltype = "";
            }
            tv_selected_cate.setText(cate);
            pba.notifyModalSelect(cate,gpos,cpos);

            tv_selected_band_main.setText("全部");
            tv_selected_band.setText("全部");
            band = "";

            if(gpos == -1 || cpos == -1){
                productPinpaiAdapter = new ProductPinpaiAdapter(getApplicationContext(),bamr.getBrand());
            }else{
                productPinpaiAdapter = new ProductPinpaiAdapter(getApplicationContext(),bamr.getCategory().get(gpos).getTypes().get(cpos).getBrand());
            }

            productPinpaiAdapter.setOnCategoryClickListener(onCategoryClickListener);

            typeList.setAdapter(productPinpaiAdapter);
            productPinpaiAdapter.notifyChange();
        }

    }

    @Override
    public void onBandClick(View view) {
        if (view != null) {
            String ba = (String) view.getTag();
            band = ba;

            if(ba.equals(tv_selected_band.getText().toString())){
                ba = "全部";
                band = "";
            }

            tv_selected_band.setText(band);
            tv_selected_band_main.setText(band);

            pba.notifyBandSelect(ba);
        }
    }

    public void bandSearch(View v) {
        search();
        if (menu.isSecondaryMenuShowing()) {
            menu.toggle();
        }

    }

    class MyOnRefreshListener2 implements OnRefreshListener2<ListView> {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            if (DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                search(currentPage + 1, true);
            } else {
                CustomToast.showToast(getApplicationContext(),
                        getString(R.string.alert_network_inavailble));
                searchResultLayout.onRefreshComplete();
            }
        }
    }

    private void showLoadingProgressView() {
        loadingView.setVisibility(View.VISIBLE);
        searchResultLayout.setVisibility(View.GONE);
        searchNoResultLayout.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void showErrorView() {
        loadingView.setVisibility(View.GONE);
        searchResultLayout.setVisibility(View.GONE);
        searchNoResultLayout.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showLoadingMoreFooter() {
        if (footer != null) {
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (footer_view != null) {
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer_view);
            } catch (Exception e) {
            }
        }

        searchResultLayout.getRefreshableView().addFooterView(footer);
    }

    //显示没有更多的提示
    private void showNoMoreView() {
        if (footer != null) {
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (footer_view != null) {
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer_view);
            } catch (Exception e) {
            }
        }

    }

    private void showSearchResultView() {
        loadingView.setVisibility(View.GONE);
        searchNoResultLayout.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        searchResultLayout.setVisibility(View.VISIBLE);

    }

    static final String PAGE = "page";
    static final String KEYWORD = "keyword";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(PAGE, currentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean containsKey = savedInstanceState.containsKey(PAGE);
        if (containsKey) {
            currentPage = savedInstanceState.getInt(PAGE);
        }
    }

    private static final int PAGE_SIZE = 20;

    private String band = "";

    private String smalltype = "";

    private int isMerge = 0;

    private int isCredit = 0;

    private void initParams(){
        band = "";
        smalltype = "";
        isMerge = 0;
        isCredit = 0;

        loadBrandAndModel();
    }

    private void search(int targetPage, boolean loadMore) {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (DeviceUtil.isNetworkAvailable(this)) {

            showLoadingProgressView();

            if (loadMore) {
                //String keyword, int area, String smalltype,String brand,int ismerge,int page, int pageSize,String sortField,int isAscSort ,IRequestListener observer
                requestId = NetUtil.getInstance().requestForSearch(keyword, pid, smalltype, band, isMerge,isCredit,
                        targetPage, PAGE_SIZE, "", 1,
                        new LoadMoreContentListener(this));
            } else {
                requestId = NetUtil.getInstance()
                        .requestForSearch(keyword, pid, smalltype, band, isMerge,isCredit,
                                targetPage, PAGE_SIZE, "", 1,
                                new LoadMoreContentListener(this));
            }

            saveKeywords(keyword);

        } else {
            if (!loadMore)
                showErrorView();
        }
    }

    ArrayList<String> groupList = new ArrayList<String>();

    BandAndModelResult bamr;
    ProductBandAdapter pba;
    ProductPinpaiAdapter productPinpaiAdapter;
    RelativeLayout rl_cate;

    private void initBandAndModelMenu(BaseResult responseData) {

        bamr = (BandAndModelResult) responseData;

        band = "";
        smalltype = "";
        tv_selected_band.setText("全部");
        tv_selected_band_main.setText("全部");
        tv_selected_cate.setText("全部");

        if (bamr != null) {
            pba = new ProductBandAdapter(getApplicationContext(), bamr);
            bandList.setAdapter(pba);

            pba.notifyDataSetChanged();

            pba.setOnBandClickListener(this);
            pba.setOnCategoryClickListener(this);

            productPinpaiAdapter = new ProductPinpaiAdapter(getApplicationContext(),bamr.getBrand());
            productPinpaiAdapter.setOnCategoryClickListener(onCategoryClickListener);

            typeList.setAdapter(productPinpaiAdapter);

        }
    }

    private ProductPinpaiAdapter.onCategoryClickListener onCategoryClickListener = new ProductPinpaiAdapter.onCategoryClickListener() {
        @Override
        public void onCategoryClick(View view, int gpos, int cpos) {
            String cate = (String) view.getTag();

            int gp = -1;
            int cp = -1;

            if(band.equals(cate)){
                band = "";
                tv_selected_band.setText("全部");
                tv_selected_band_main.setText("全部");
            }else{
                band = cate;
                gp = gpos;
                cp = cpos;
                tv_selected_band_main.setText(cate);
                tv_selected_band.setText(cate);
            }

            productPinpaiAdapter.notifyModalSelect(cate,gp,cp);
        }
    };

    private void loadBrandAndModel() {
        LoadingTask task = new LoadingTask(BMSearchResultActivity.this, new LoadingTask.ILoading() {

            @Override
            public void loading(NetUtil.IRequestListener listener) {
                NetUtil.getInstance().getMarketTypeAndBrand(keyword, pid + "", new NetUtil.IRequestListener() {
                    @Override
                    public void onRequestSuccess(BaseResult responseData) {

                        if (responseData.getErrorCode() == 0 && responseData.getSuccess() == 0) {
                            initBandAndModelMenu(responseData);
                        } else {
                            CustomToast.showToast(getApplicationContext(), responseData.getMessage());
                        }
                    }

                    @Override
                    public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                        CustomToast.showToast(getApplicationContext(), msg);
                    }
                });
            }

            @Override
            public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
            }

            @Override
            public boolean isShowNoNetWorkView() {
                return true;
            }

            @Override
            public NetUtil.IRequestListener getRequestListener() {
                return BMSearchResultActivity.this;
            }

            @Override
            public boolean isAsync() {
                return false;
            }
        });

        task.setRootView(getWindow().getDecorView());
        task.loading();
    }

    static class LoadMoreContentListener implements IRequestListener {

        WeakReference<BMSearchResultActivity> hostRef;

        public LoadMoreContentListener(BMSearchResultActivity host) {
            hostRef = new WeakReference<BMSearchResultActivity>(host);
        }

        private boolean check() {
            BMSearchResultActivity host = hostRef.get();
            if (host == null) {
                return false;
            }
            if (host.isFinishing()) {
                return false;
            }
            return true;
        }

        @Override
        public void onRequestSuccess(BaseResult responseData) {
            if (!check()) {
                return;
            }
            BMSearchResultActivity host = hostRef.get();
            host.setLoadlingMoreState(false);
            host.setFooterVisible(false);

            if (responseData.getErrorCode() == DcError.DC_OK) {
                // 搜索结果
                BMSearchResult searchResult = (BMSearchResult) responseData;
                List<BMSearchResult.BMSearchData> searchData = searchResult.getDataList();
                if (searchData == null || searchData.size() == 0) {
                    host.setHasMore(false);
                    host.setFooterVisible(false);
                    // Toast.makeText(host.getApplicationContext(), "没有更多内容",
                    // Toast.LENGTH_LONG).show();
                    host.showSearchResultView();
                    host.showNoMoreView();
                } else {

                    host.checkAndFillSearchResult(searchData);
                    host.showLoadingMoreFooter();
                    host.setFooterVisible(false);

                    if (searchResult.getDataList().size() < 20) {
                        host.setHasMore(false);
                        host.showNoMoreView();
                    } else {
                        host.setHasMore(true);
                        host.showLoadingMoreFooter();
                        host.setFooterVisible(false);
                    }
                }

            } else {
                // host.showErrorView();
                CustomToast.showToast(host.getApplicationContext(),
                        host.getString(R.string.get_more_data_failed));
            }

            host.searchResultLayout.onRefreshComplete();
        }

        @Override
        public void onRequestError(int requestTag, int requestId,
                                   int errorCode, String msg) {
            if (!check()) {
                return;
            }

            BMSearchResultActivity host = hostRef.get();
            host.setLoadlingMoreState(false);
            host.setFooterVisible(false);
            host.searchResultLayout.onRefreshComplete();
            CustomToast.showToast(host.getApplicationContext(), msg);
            // Toast.makeText(host.getApplicationContext(), "获取更多内容失败",
            // Toast.LENGTH_LONG).show();

        }

    }

    private void saveKeywords(String key) {
        CommonDaoImpl.getInstance(getApplicationContext()).saveKeywords(key);
    }

    private void setLoadlingMoreState(boolean loading) {
        this.isLoadingMore = loading;
    }

    private void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    private boolean hasMore() {
        return this.hasMore;
    }

    private boolean getLoadlingMoreState() {
        return this.isLoadingMore;
    }

    private void setFooterVisible(boolean visible) {
        footer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private Map<String, Long> observersIds = new HashMap<String, Long>();
    private Map<String, String> downloadedIds = new HashMap<String, String>();

    private void checkAndFillSearchResult(final List<BMSearchResult.BMSearchData> data) {

        doFillSearchResult(data);
    }

    private void doFillSearchResult(List<BMSearchResult.BMSearchData> data) {
        if (data == null) {
            Log.e(TAG, "Fatal Error!");
            return;
        }
        showSearchResultView();
        if (searchResultAdapter == null) {
            searchResultAdapter = new BMSearchResultAdapter(this);
            searchResultLayout.setAdapter(searchResultAdapter);
            searchResultLayout.setVisibility(View.VISIBLE);
            searchResultAdapter
                    .setOnListItemClickListener(searchResultItemClickListener);
        }

        searchResultAdapter.addAll(data);
//		searchResultAdapter.setData(data);
        currentPage += 1;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
    }

    ;


    OnListItemClickListener searchResultItemClickListener = new OnListItemClickListener() {

        @Override
        public void onItemIconClick(View view, int position) {
        }

        @Override
        public void onItemButtonClick(final View view, int position) {

            final BMSearchResult.BMSearchData item = searchResultAdapter.getItem(position);

            Intent detailIntent = new Intent(getApplicationContext(), BMProductDetailActivity.class);
            detailIntent.putExtra(BMProductDetailActivity.SUPPLY_ID, item.getSupplyId());
            startActivity(detailIntent);

        }

    };

    private boolean isGroupProvider = false;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_search:
            case R.id.edit_search:
                Intent intent = new Intent(this, BMSearchActivity.class);
                intent.putExtra("pid", pid);
                intent.putExtra("pname", pname);
                startActivity(intent);
                break;
            case R.id.tv_btn_band_ok:
                search();
                if (menu.isSecondaryMenuShowing())
                    menu.toggle();
                break;
            case R.id.btn_province:
                if (!menu.isShown())
                    menu.showMenu(true);
                break;
            case R.id.btn_back_bottom:
                finish();
                break;
            case R.id.select_band:
                if (!menu.isSecondaryMenuShowing())
                    menu.showSecondaryMenu();
                break;
            case R.id.error_hint:
                if (!DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                    CustomToast.showToast(getApplicationContext(),
                            getString(R.string.alert_network_inavailble));
                    return;
                } else {
//                    showLoadingProgressView();
                    search(1, false);
                }
                break;
            case R.id.tv_no_searchresult_hint:
                if (!DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                    CustomToast.showToast(getApplicationContext(),
                            getString(R.string.alert_network_inavailble));
                    return;
                } else {
//                    showLoadingProgressView();

                    search();
                }

                break;
            case R.id.bm_btn_group_provider:

                if (isMerge == 1) {
                    isMerge = 0;
                    cbGroupProvider.setText("合并相同供应商");
                    search();
                } else {
                    isMerge = 1;
                    search();
                    cbGroupProvider.setText("取消合并供应商");
                }
                break;
            case R.id.search_clear:
                edit_search.setText("");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetUtil.getInstance().cancelRequestById(requestId);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1,
                            int position, long id) {
        BMSearchResult.BMSearchData item = null;
        // final SearchItem item = null ;
        if (adapterView instanceof ListView) {
            Object item2 = adapterView.getAdapter().getItem(position);
            if (item2 != null) {
                item = (BMSearchResult.BMSearchData) item2;
                if (item2 == item) {
                    // Toast.makeText(getApplicationContext(), "same",
                    // 0).show();
                }
            }
        }
        if (item != null) {

            Intent intentDetail = new Intent(this, BMProductDetailActivity.class);
            intentDetail.putExtra(BMProductDetailActivity.SUPPLY_ID, item.getSupplyId());
            startActivity(intentDetail);

        }

    }

    // //////////////////////////////////////////////////////////////////////////////////////
    private boolean needRequery = false;

    private void registerListener() {
    }

    public void search() {

        if (searchResultAdapter != null) {
            searchResultAdapter.clear();
        }

        if (footer_view != null) {
            try {
                searchResultLayout.getRefreshableView().removeFooterView(footer_view);
            } catch (Exception e) {
            }
        }

        search(0, false);
    }

}
