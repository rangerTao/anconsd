package com.ranger.bmaterials.ui;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.BMSearchResultAdapter;
import com.ranger.bmaterials.adapter.SearchRecommedAdapter;
import com.ranger.bmaterials.adapter.AbstractListAdapter.OnListItemClickListener;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.AppManager.GameStatus;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.mode.*;
import com.ranger.bmaterials.netresponse.BMSearchResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.ui.gametopic.BMProductDetailActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

public class BMSearchResultActivity extends Activity implements
/* IRequestListener, */OnClickListener, OnItemClickListener {

    private static final String TAG = "SearchResultActivity";

    protected static final boolean DEBUG = true;

    private String keyword;
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
    private View searchRecomendHintView;

    /**
     * 推荐的GridView
     */
    private GridView recomendGv;

    private View errorView;
    private SearchRecommedAdapter recommedAdapter;

    private View footer;

    /**
     * 分页的页码
     */
    private int currentPage = 0;

    private Dialog rootDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_result_activity);

        Intent intent = getIntent();

        if (intent != null) {
            keyword = intent.getStringExtra(ARG_KEYWORD);
            pid = intent.getIntExtra(ARG_PID, 0);
            pname = intent.getStringExtra(ARG_PNAME);
        }

        restore(savedInstanceState);

        initTitleBar();
        initView();

        showLoadingProgressView();

        registerListener();

        search(currentPage + 1, false);

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

    private static final int MSG_REFRESH_PROGRESS = 300;
    private static final int MSG_REFRESH_TITLE_COUNT = 301;

    /**
     * no use
     *
     * @author wangliang
     */
    /*
     * class AllDownloadListener implements DownloadListener {
	 * 
	 * @Override public void onDownloadProcessing(List<DownloadItemOutput>
	 * items) { List<SearchItem> data = searchResultAdapter.getData(); if (data
	 * != null && items != null) { for (SearchItem item : data) { String
	 * downloadUrl = item.getDownloadUrl(); for (DownloadItemOutput s : items) {
	 * if (downloadUrl.equals(s.getUrl())) { long currentBytes =
	 * s.getCurrentBytes(); long totalBytes = s.getTotalBytes();
	 * item.setCurrentBytes(currentBytes); item.setTotalBytes(totalBytes);
	 * DownloadStatus status = s.getStatus(); //
	 * app.setSaveDest(file.getDest()); switch (status) { case STATUS_FAILED:
	 * break; case STATUS_PAUSED: break; case STATUS_RUNNING: case
	 * STATUS_PENDING: break; case STATUS_SUCCESSFUL: break; }
	 * 
	 * } }
	 * 
	 * } } }
	 * 
	 * }
	 */
    private void initTitleBar() {
        TextView backView = (TextView) findViewById(R.id.btn_back);
        backView.setText(pname);
        backView.setOnClickListener(this);
//		titleLeftText = (TextView) findViewById(R.id.label_title);
//		titleLeftText.setText("搜索结果");
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


    private void initView() {
        searchResultLayout = (PullToRefreshListView) findViewById(R.id.layout_search_result_list);
        searchResultLayout.setOnRefreshListener(new MyOnRefreshListener2());
        searchResultLayout.setOnItemClickListener(this);
        searchResultLayout
                .setOnLastItemVisibleListener(OnLastItemVisibleListener);
        footer = createFooter();
        searchResultLayout.getRefreshableView().addFooterView(footer);

        searchNoResultLayout = (ViewGroup) findViewById(R.id.layout_search_subview_no_result);
        searchRecomendHintView = searchNoResultLayout
                .findViewById(R.id.label_recomend_hint);
        recomendGv = (GridView) searchNoResultLayout
                .findViewById(R.id.search_recomend_gv);
        // recomendGv.setScrollContainer(false);
        loadingView = findViewById(R.id.progress_bar);

        errorView = findViewById(R.id.error_hint);
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(this);
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

        searchResultLayout.getRefreshableView().addFooterView(createNoMoreDataFooter(KeywordsList.getInstance().getRandomRecomKeyword()));

    }

    private void showSearchResultView() {
        loadingView.setVisibility(View.GONE);
        searchNoResultLayout.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        searchResultLayout.setVisibility(View.VISIBLE);

    }

    private void showSearchNoResultView() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        searchResultLayout.setVisibility(View.GONE);
        searchNoResultLayout.setVisibility(View.VISIBLE);

        TextView tv_noresult_hint = (TextView) findViewById(R.id.tv_no_searchresult_hint);
        String default_keyword = getString(R.string.no_keywords);
        String no_data_hint = getString(R.string.string_with_quote);
        SpannableString ss = new SpannableString(default_keyword + no_data_hint);
        ss.setSpan(fcc_no_data, 0, default_keyword.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(fcc_keyword, default_keyword.length(), (default_keyword + no_data_hint).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_noresult_hint.setText(ss);

        tv_noresult_hint.setOnClickListener(this);
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

    private void search(int targetPage, boolean loadMore) {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (DeviceUtil.isNetworkAvailable(this)) {
            if (loadMore) {
                //String keyword, int area, String smalltype,String brand,int ismerge,int page, int pageSize,String sortField,int isAscSort ,IRequestListener observer
                requestId = NetUtil.getInstance().requestForSearch(keyword,pid,"","",0,
                        targetPage, PAGE_SIZE,"",1,
                        new LoadMoreContentListener(this));
            } else {
                requestId = NetUtil.getInstance()
                        .requestForSearch(keyword,pid,"","",0,
                                targetPage, PAGE_SIZE,"",1,
                                new LoadMoreContentListener(this));
            }

        } else {
            if (!loadMore)
                showErrorView();
        }
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
            host.searchResultLayout.onRefreshComplete();
            if (responseData.getErrorCode() == DcError.DC_OK) {
                // 搜索结果
                BMSearchResult searchResult = (BMSearchResult) responseData;
                List<BMSearchResult.BMSearchData> searchData = searchResult.getDataList();
                if (searchData == null || searchData.size() == 0) {
                    host.setHasMore(false);
                    host.setFooterVisible(false);
                    // Toast.makeText(host.getApplicationContext(), "没有更多内容",
                    // Toast.LENGTH_LONG).show();
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
            CustomToast.showToast(host.getApplicationContext(),msg);
            // Toast.makeText(host.getApplicationContext(), "获取更多内容失败",
            // Toast.LENGTH_LONG).show();

        }

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

        // 将搜索设置下载以及安装的状态
        new AsyncTask<Void, Void, Map<String, GameStatus>>() {

            protected void onPreExecute() {
                // registerListener();
            }

            ;

            @Override
            protected Map<String, GameStatus> doInBackground(Void... params) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(
                    java.util.Map<String, GameStatus> result) {
                doFillSearchResult(data);
            }

            ;
        }.execute();
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

            Intent detailIntent = new Intent(getApplicationContext(),BMProductDetailActivity.class);
            detailIntent.putExtra(BMProductDetailActivity.SUPPLY_ID,item.getSupplyId());
            startActivity(detailIntent);

        }

    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.error_hint:
                if (!DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                    CustomToast.showToast(getApplicationContext(),
                            getString(R.string.alert_network_inavailble));
                    return;
                } else {
                    showLoadingProgressView();
                    search(1, false);
                }
                break;
            case R.id.tv_no_searchresult_hint:
                if (!DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                    CustomToast.showToast(getApplicationContext(),
                            getString(R.string.alert_network_inavailble));
                    return;
                } else {
                    showLoadingProgressView();

                    search();
                }

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
        final AppManager manager = AppManager
                .getInstance(getApplicationContext());
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
        if (item != null){

            Intent intentDetail = new Intent(this, BMProductDetailActivity.class);
            intentDetail.putExtra(BMProductDetailActivity.SUPPLY_ID,item.getSupplyId());
            startActivity(intentDetail);

        }

    }

    // //////////////////////////////////////////////////////////////////////////////////////
    private boolean needRequery = false;
    private PackageCallback packageCallback;

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
