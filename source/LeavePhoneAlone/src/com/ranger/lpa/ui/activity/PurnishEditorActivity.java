package com.ranger.lpa.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.pojos.PurnishInfo;


/**
 * Created by taoliang on 14-8-25.
 */
public class PurnishEditorActivity extends BaseActivity implements View.OnClickListener {

    public static final String INDEX_PURNISH = "index";

    PurnishInfo pi;

    private TextView tv_purnish_content;
    private EditText et_purnish_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_purnish_editor);

        int index = getIntent().getIntExtra(INDEX_PURNISH, 0);

        pi = MineProfile.getInstance().getPurnish().getPurnishes().get(MineProfile.getInstance().getPurnish().getPurnishes().size() > index ? index : 0);

        initView();
    }

    @Override
    public void initView() {

        tv_purnish_content = (TextView) findViewById(R.id.tv_purnish_content);

        tv_purnish_content.setText(pi.getPurnish_content());

        ((TextView) findViewById(R.id.tv_title)).setText(pi.getPurnish_title());
        ((ImageView) findViewById(R.id.iv_btn_title_right)).setImageResource(R.drawable.purnish_edit);
        findViewById(R.id.iv_btn_title_right).setOnClickListener(this);
        findViewById(R.id.iv_btn_back).setOnClickListener(this);

        findViewById(R.id.btn_purnish_delete).setOnClickListener(this);
        findViewById(R.id.btn_purnish_default).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_back:
                finish();
                break;
            case R.id.iv_btn_title_right:
                break;
            case R.id.btn_purnish_default:
                break;
            case R.id.btn_purnish_delete:
                break;
        }
    }
}
