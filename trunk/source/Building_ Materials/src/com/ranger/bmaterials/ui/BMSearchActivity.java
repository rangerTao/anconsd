package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.SuggestAdapter;
import com.ranger.bmaterials.db.CommonDaoImpl;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.tools.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taoliang on 14/10/27.
 */
public class BMSearchActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private AutoCompleteTextView searchEt;
    private View clearView;

    public static ListView lvRecom;

    List<String> suggestWords = null;

    private SuggestAdapter suggestAdapter;

    private static BMProvinceListResult.ProviceItem mPi;

    private String pId = "";
    private String pName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_activity);

        searchEt = (AutoCompleteTextView) findViewById(R.id.edit_search);

        findViewById(R.id.btn_search).setOnClickListener(this);

        clearView = findViewById(R.id.search_clear);
        clearView.setOnClickListener(this);
        listenInput();

        pId = getIntent().getStringExtra("pid");
        pName = getIntent().getStringExtra("pname");

        lvRecom = (ListView) findViewById(R.id.ll_search_recom);

    }

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        loadHistroyData();
    }

    private void initSuggest(List<String> keywords) {

        suggestWords = keywords;
        showDropdown();

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadHistroyData();

    }

    private void showDropdown(){
        suggestAdapter = new SuggestAdapter(getApplicationContext(), suggestWords, 5);
        lvRecom.setAdapter(suggestAdapter);
        suggestAdapter.notifyDataSetChanged();

        lvRecom.setOnItemClickListener(this);
    }

    private void listenInput() {
        String text = String.format(getString(R.string.search_hint));
        CharSequence styledText = Html.fromHtml(text);

        searchEt.setHint(styledText);

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    boolean networkAvailable = DeviceUtil.isNetworkAvailable(getApplicationContext());
                    if (networkAvailable) {
                        // 搜索
                        search();
                    } else {
                        CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
                        // Toast.makeText(getActivity(), "网络不给力",
                        // Toast.LENGTH_LONG).show();
                    }
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    boolean networkAvailable = DeviceUtil.isNetworkAvailable(getApplicationContext());
                    if (networkAvailable) {
                        // 搜索
                        search();
                    } else {
                        CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
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

    private void search() {

        String keyword = searchEt.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            CustomToast.showToast(getApplicationContext(), getString(R.string.alert_search_cannot_be_null));
        } else if (TextUtils.isEmpty(keyword.trim())) {
            CustomToast.showToast(getApplicationContext(), getString(R.string.alert_search_cannot_be_null));
        } else {
            jumpSearch(keyword);
            // fakeResult();
        }
    }

    private void jumpSearch(String keyword) {
        Intent intent = new Intent(getApplicationContext(), BMSearchResultActivity.class);
        intent.putExtra(BMSearchResultActivity.ARG_KEYWORD, keyword);
        intent.putExtra(BMSearchResultActivity.ARG_PID, pId);
        intent.putExtra(BMSearchResultActivity.ARG_PNAME, pName);

        startActivity(intent);

        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                boolean networkAvailable = DeviceUtil.isNetworkAvailable(getApplicationContext());
                if (networkAvailable) {
                    // 搜索
                    search();
                } else {
                    CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
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
}
