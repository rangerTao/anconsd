package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.AbstractListAdapter;
import com.ranger.bmaterials.adapter.BMProvinceAdapter;
import com.ranger.bmaterials.adapter.SuggestAdapter;
import com.ranger.bmaterials.app.BMApplication;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.db.CommonDaoImpl;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.listener.onTagCloudViewLayoutListener;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.Tag;
import com.ranger.bmaterials.view.TagCloudView;
import com.ranger.bmaterials.view.TagCloudView.TagClickListener;

public class BMSearchFragment extends Fragment implements OnClickListener, OnItemClickListener, TagClickListener, onTagCloudViewLayoutListener, TagCloudView.OnTagFilngListener {

    private static final int KEYWORDS_COUNT = 50;
    private ViewGroup tagLayout;
    private TagCloudView tagView;

    private ListView searchResultLayout;

    private ViewGroup searchNoResultLayout;

    private KeywordsList keywordsWrapper;

    private View root;

    private View loadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        if (root != null) {
            ViewParent parent = this.root.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.root);
            }
            return root;
        }

        root = inflater.inflate(R.layout.search_activity_new3d, null);
        root.findViewById(R.id.btn_back).setOnClickListener(this);

        loadingView = root.findViewById(R.id.loadingView);

        tv_back = (TextView) root.findViewById(R.id.btn_back);
        initView();
        initTagCloudViewData();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initSlidingMenu();
            }
        },1000);

        return root;
    }

    private Handler mHandler = new Handler();

    public static SlidingMenu menu;
    private View firstMenu;
    private ListView lv_province_list;
    private BMProvinceAdapter bpa;

    private void initSlidingMenu() {

        firstMenu = getActivity().getLayoutInflater().inflate(R.layout.side_menu, null);
        lv_province_list = (ListView) firstMenu.findViewById(R.id.bm_province_list);

        menu = new SlidingMenu(getActivity());
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int offset = (dm.widthPixels / 3) * 1;
        menu.setBehindOffset(offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(firstMenu);

        getProvinces();

    }

    private void getProvinces() {
        NetUtil.getInstance().requestForProvices(getActivity(),new NetUtil.IRequestListener() {
            @Override
            public void onRequestSuccess(BaseResult responseData) {
                BMProvinceListResult blr = (BMProvinceListResult) responseData;

                if (blr.getTag().equals(Constants.NET_TAG_GET_PROVINCE + "")) {
                    bpa = new BMProvinceAdapter(getActivity().getApplicationContext(), blr.getProviceList());
                    lv_province_list.setAdapter(bpa);
                    lv_province_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            BMProvinceListResult.ProviceItem pi = (BMProvinceListResult.ProviceItem) parent.getAdapter().getItem(position);

                            if (pi != null) {
                                try {
                                    setCityName(pi);

                                    bpa.setProvince(pi.getName());
                                    bpa.notifyDataSetChanged();

                                    BMApplication.getAppInstance().setSelectedProvince(pi.getId(),pi.getName());

                                    menu.toggle();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    bpa.setOnListItemClickListener(new AbstractListAdapter.OnListItemClickListener() {
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

    /**
     * Init the data of tagcloudview.
     */
    private void initTagCloudViewData() {

        tagView.addTags(new ArrayList<Tag>());

        if (KeywordsList.getInstance().getKeywords() == null) {
            loadKeywords();
        } else {
            tagView.setOnTagCloudViewLayoutInitializedListener(this);
        }
    }

    private void dealWithPreloadedCloudViewData() {
        keywordsWrapper = KeywordsList.getInstance();
        List<String> keywords = getKeywords();
        List<Tag> tags = new ArrayList<Tag>(keywords.size());
        for (int i = 0; i < keywords.size(); i++) {
            tags.add(new Tag(keywords.get(i), popularity));
        }

        // tagView.addTags(tags);
        tagView.addTagsWithPreload(tags);

        loadingView.setVisibility(View.GONE);
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        View searchBtn = root.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);

        searchEt = (TextView) root.findViewById(R.id.edit_search);

        searchEt.setOnClickListener(this);

        tagLayout = (ViewGroup) root.findViewById(R.id.layout_search_view);
        tagView = (TagCloudView) tagLayout.findViewById(R.id.tagclouview);
        tagView.setTagClickListener(this);
        tagView.setOnTagFlingListener(this);

        searchResultLayout = (ListView) root.findViewById(R.id.layout_search_result_list);
        searchResultLayout.setVisibility(View.INVISIBLE);

        searchNoResultLayout = (ViewGroup) root.findViewById(R.id.layout_search_subview_no_result);
        searchNoResultLayout.setVisibility(View.INVISIBLE);

        clearView = root.findViewById(R.id.search_clear);
        clearView.setOnClickListener(this);
    }

    List<String> suggestWords = null;

    private SuggestAdapter suggestAdapter;

    PopupWindow mSearchRecomPopup;

    @Override
    public void onStop() {
        super.onStop();

        if(mSearchRecomPopup!= null && mSearchRecomPopup.isShowing()){
            mSearchRecomPopup.dismiss();
        }
    }

    private List<String> getKeywords() {
        final List<String> keys = this.keywordsWrapper.getKeywords();
        Collections.shuffle(keys);
        ArrayList<String> ret = new ArrayList<String>(8);
        for (int i = 0; i < 8; i++) {
            ret.add(keys.get(i));
        }
        return ret;
    }

    private void fillKeywords() {
        if (keywordsWrapper != null) {
            List<String> keywords = getKeywords();
            List<Tag> tags = new ArrayList<Tag>(keywords.size());
            for (int i = 0; i < keywords.size(); i++) {
                tags.add(new Tag(keywords.get(i), popularity));
            }
            tagView.replace(tags);
        }

    }

    /**
     * 获取关键字
     */
    private void loadKeywords() {

        SharedPreferences sp = getActivity().getSharedPreferences("cache", Context.MODE_WORLD_WRITEABLE);

        String cache_province = sp.getString("keywords", "");

        Log.e("TAG","keywords : " + cache_province);

        if(!cache_province.trim().equals("")){
            BaseResult baseResult = JSONParser.parseBMKeywords(cache_province);
            baseResult.setTag(Constants.NET_TAG_KEYWORDS + "");
            baseResult.setErrorCode(DcError.DC_OK);

            keywordsWrapper = (KeywordsList) baseResult;
            dealWithPreloadedCloudViewData();
            loadingView.setVisibility(View.GONE);
        }else{
            loadingView.setVisibility(View.VISIBLE);
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                NetUtil.getInstance().requestForKeywords(KEYWORDS_COUNT, new KeywordsRequestListener(BMSearchFragment.this),getActivity().getApplicationContext());
            }
        });

    }

    private void jumpSearch(String keyword) {
        Intent intent = new Intent(getActivity(), BMSearchResultActivity.class);
        intent.putExtra(BMSearchResultActivity.ARG_KEYWORD, keyword);
        intent.putExtra(BMSearchResultActivity.ARG_PID, mPi==null? "":mPi.getId());
        intent.putExtra(BMSearchResultActivity.ARG_PNAME, mPi==null? "":mPi.getName());

        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {

        String pname = BMApplication.getAppInstance().getSelectedProvinceName();

        if(bpa != null){
            for(int i = 0;i<bpa.getCount();i++){

                BMProvinceListResult.ProviceItem pi = (BMProvinceListResult.ProviceItem) bpa.getItem(i);

                if(pname.equals(pi.getName())){
                    try {
                        setCityName(pi);

                        bpa.setProvince(pi.getName());
                        bpa.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        super.onResume();
    }

    private void searchKeywords() {
        if (DeviceUtil.isNetworkAvailable(getActivity())) {
            loadKeywords();
        }

    }

    private int popularity = 5;
    private TextView searchEt;
    private View clearView;

    private void search() {

        Intent intent = new Intent(getActivity(),BMSearchActivity.class);
        intent.putExtra("pid",mPi == null ? "":mPi.getId());
        intent.putExtra("pname",mPi == null ? "":mPi.getName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if(menu !=null){
                    menu.toggle();
                }
            break;
            case R.id.btn_search:
            case R.id.edit_search:
                boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
                if (networkAvailable) {
                    // 搜索
                    search();
                } else {
                    CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
                    // Toast.makeText(getActivity(), "网络不给力",
                    // Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.search_clear:
                searchEt.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String key = suggestWords.get(position);
        searchEt.setText(key);
        search();

    }

    @Override
    public void onFling() {
        fillKeywords();
    }

    private class KeywordsRequestListener implements IRequestListener {

        private BMSearchFragment host;

        public KeywordsRequestListener(BMSearchFragment host) {
            this.host = host;
        }

        @Override
        public void onRequestSuccess(BaseResult responseData) {
            if (responseData.getErrorCode() == DcError.DC_OK) {
                int tag = StringUtil.parseInt(responseData.getTag());
                if (tag == Constants.NET_TAG_KEYWORDS) {
                    host.keywordsWrapper = (KeywordsList) responseData;
                    host.fillKeywords();
                }

            }

            host.loadingView.setVisibility(View.GONE);
        }

        @Override
        public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
            host.loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTagClick(String tag) {
        jumpSearch(tag);
        fillSearchText(tag);
    }

    private void fillSearchText(String text) {
        try {
            searchEt.setText(text);
        } catch (Exception e) {
        }

    }

    @Override
    public void onTagCloudViewLayoutInitialize() {

        dealWithPreloadedCloudViewData();
    }

    private static BMProvinceListResult.ProviceItem mPi;
    private static TextView tv_back;

    public static void setCityName(BMProvinceListResult.ProviceItem pi){

        mPi = pi;
        tv_back.setText(pi.getName());


    }

}
