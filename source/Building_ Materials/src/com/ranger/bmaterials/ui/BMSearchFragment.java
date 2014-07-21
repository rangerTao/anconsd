package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.SuggestAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.db.CommonDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.listener.onTagCloudViewLayoutListener;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.Tag;
import com.ranger.bmaterials.view.TagCloudView;
import com.ranger.bmaterials.view.TagCloudView.TagClickListener;
import com.ranger.bmaterials.work.DBTaskManager;

public class BMSearchFragment extends Fragment implements OnClickListener, OnItemClickListener, TagClickListener, onTagCloudViewLayoutListener {

    private static final int KEYWORDS_COUNT = 50;
    private ViewGroup tagLayout;
    private TagCloudView tagView;
    private Button replaceKeywordsButton;

    private ListView searchResultLayout;

    private ViewGroup searchNoResultLayout;

    private KeywordsList keywordsWrapper;

    private View root;

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
        initView();
        initTagCloudViewData();

        return root;
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
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        View searchBtn = root.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);

        searchEt = (AutoCompleteTextView) root.findViewById(R.id.edit_search);

        tagLayout = (ViewGroup) root.findViewById(R.id.layout_search_view);
        tagView = (TagCloudView) tagLayout.findViewById(R.id.tagclouview);
        tagView.setTagClickListener(this);
        tagView.setEditText(searchEt);

        searchResultLayout = (ListView) root.findViewById(R.id.layout_search_result_list);
        searchResultLayout.setVisibility(View.INVISIBLE);

        searchNoResultLayout = (ViewGroup) root.findViewById(R.id.layout_search_subview_no_result);
        searchNoResultLayout.setVisibility(View.INVISIBLE);

        clearView = root.findViewById(R.id.search_clear);
        clearView.setOnClickListener(this);
        listenInput();
        // setViewMode(ViewMode.VIEWMODE_KEYWORDS);

        searchEt.setThreshold(0);

        // 设置自动提示文本高度 与软键盘兼容
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);

                int screenHeight = root.getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);// keyboard
                // size

                int[] location = new int[2];
                searchEt.getLocationOnScreen(location);

                int[] screenwh = UIUtil.getScreenPx(getActivity());

                searchEt.setDropDownHeight(screenwh[1] - location[1] - searchEt.getHeight() - heightDifference - UIUtil.getStatusBarHeight(getActivity()) * 2);
            }
        });

        loadHistroyData();
    }

    List<String> suggestWords = null;

    private void initSuggest(List<String> keywords) {
        suggestWords = keywords;
        suggestAdapter = new SuggestAdapter(getActivity(), suggestWords, 10);
        searchEt.setDropDownBackgroundResource(R.drawable.transparent_drawable);
        searchEt.setAdapter(suggestAdapter);
        searchEt.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });

    }

    private void addSuggest(String key) {
        if (suggestWords == null) {
            return;
        }
        boolean find = false;
        for (String k : suggestWords) {
            if (k.startsWith(key)) {
                find = true;
                break;
            }
        }
        List<String> allItems = suggestAdapter.getAllItems();
        if (find) {
            allItems.remove(key);
            // suggestAdapter.notifyDataSetChanged();
        } else {
            saveHistroy(key);
        }
        allItems.add(0, key);

    }

    private void loadHistroyData() {
        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {
                CommonDao handler = DbManager.getCommonDbHandler();
                return handler.getKeywords();
            }

            protected void onPostExecute(java.util.List<String> result) {
                if (result == null || result.size() == 0) {
                    initSuggest(new ArrayList<String>());
                } else {
                    initSuggest(result);
                }
            }

            ;

        }.execute();
    }

    private void saveHistroy(final String keyword) {
        // save keywords
        DBTaskManager.submitTask(new Runnable() {
            @Override
            public void run() {
                CommonDao handler = DbManager.getCommonDbHandler();
                handler.saveKeywords(keyword);
            }
        });
    }

    private void listenInput() {
        String text = String.format(getString(R.string.search_hint));
        CharSequence styledText = Html.fromHtml(text);

        searchEt.setHint(styledText);

        searchEt.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
                    if (networkAvailable) {
                        // 搜索
                        search();
                    } else {
                        CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
                        // Toast.makeText(getActivity(), "网络不给力",
                        // Toast.LENGTH_LONG).show();
                    }
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
                    if (networkAvailable) {
                        // 搜索
                        search();
                    } else {
                        CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
                        // Toast.makeText(getActivity(), "网络不给力",
                        // Toast.LENGTH_LONG).show();
                    }
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_SPACE) {
                    // Toast.makeText(getActivity(),
                    // "OnEditorActionListener spaceback", 1).show();
                }

                return false;
            }
        });

        searchEt.setFilters(ll);

        searchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    clearView.setVisibility(View.VISIBLE);
                } else {
                    clearView.setVisibility(View.GONE);
                }

            }
        });
    }

    private InputFilter[] ll = new InputFilter[]{new InputFilter.LengthFilter(30)};

    private List<String> getKeywords() {
        final List<String> keys = this.keywordsWrapper.getKeywords();
        Collections.shuffle(keys);
        ArrayList<String> ret = new ArrayList<String>(10);
        for (int i = 0; i < 10; i++) {
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
        NetUtil.getInstance().requestForKeywords(KEYWORDS_COUNT, new KeywordsRequestListener(this));
    }

    private void jumpSearch(String keyword) {
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.ARG_KEYWORD, keyword);
        startActivity(intent);
    }

    private void changeKeyWord() {
        if (keywordsWrapper != null && keywordsWrapper.getKeywords() != null) {
            fillKeywords();
        } else {
            boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
            if (networkAvailable) {
                searchKeywords();
            } else {
                CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
                // Toast.makeText(getActivity(), "网络不给力",
                // Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    ;

    @Override
    public void onResume() {

        super.onResume();
    }

    private void searchKeywords() {
        if (DeviceUtil.isNetworkAvailable(getActivity())) {
            loadKeywords();
        }

    }

    private int popularity = 5;
    private AutoCompleteTextView searchEt;
    private SuggestAdapter suggestAdapter;
    private View clearView;

    private void search() {
        String keyword = searchEt.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            CustomToast.showToast(getActivity(), getString(R.string.alert_search_cannot_be_null));
        } else if (TextUtils.isEmpty(keyword.trim())) {
            CustomToast.showToast(getActivity(), getString(R.string.alert_search_cannot_be_null));
        } else {
            jumpSearch(keyword);
            // fakeResult();
            addSuggest(keyword);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if(MainHallActivity.menu !=null){
                    MainHallActivity.menu.toggle();
                }
                break;
            case R.id.btn_search:
                boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
                if (networkAvailable) {
                    // 搜索
                    search();
                } else {
                    CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
                    // Toast.makeText(getActivity(), "网络不给力",
                    // Toast.LENGTH_LONG).show();
                }
                ClickNumStatistics.addSearchButtonClickStatis(getActivity());

                break;
            case R.id.search_clear:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        }

        @Override
        public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

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
            searchEt.setSelection(text.length());
        } catch (Exception e) {
        }

    }

    @Override
    public void onTagCloudViewLayoutInitialize() {

        dealWithPreloadedCloudViewData();
    }

}
