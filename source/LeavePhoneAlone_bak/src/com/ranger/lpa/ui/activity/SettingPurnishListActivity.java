package com.ranger.lpa.ui.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.lpa.R;
import com.ranger.lpa.adapter.LPAPurnishAdapter;

/**
 * Created by taoliang on 14-8-25.
 */
public class SettingPurnishListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView lvPurnish;
    private LPAPurnishAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_purnish_list);

        initView();
    }

    @Override
    public void initView() {

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.title_purnish);
        ((ImageView) findViewById(R.id.iv_btn_title_right)).setImageResource(R.drawable.punish_add);
        findViewById(R.id.iv_btn_title_right).setOnClickListener(this);
        findViewById(R.id.iv_btn_back).setOnClickListener(this);

        lvPurnish = (ListView) findViewById(R.id.lv_purnish);
        mAdapter = new LPAPurnishAdapter();
        lvPurnish.setAdapter(mAdapter);

        lvPurnish.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_btn_back:
                finish();
                break;
            case R.id.iv_btn_title_right:
                Intent editIntent = new Intent(this,PurnishAddActivity.class);
                startActivity(editIntent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent editIntent = new Intent(this,PurnishEditorActivity.class);
        editIntent.putExtra(PurnishEditorActivity.INDEX_PURNISH,position);
        startActivity(editIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }
}
