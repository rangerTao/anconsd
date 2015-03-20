package com.ranger.lpa.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.pojos.PurnishInfo;
import com.ranger.lpa.statics.ClickStats;


/**
 * Created by taoliang on 14-8-25.
 */
public class PurnishAddActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public static final String INDEX_PURNISH = "index";

    private int pIndex;

    PurnishInfo pi;

    private EditText et_purnish_content;
    private EditText et_purnish_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_purnish_add);

        pi = new PurnishInfo();

        initView();
    }

    @Override
    public void initView() {

        et_purnish_content = (EditText) findViewById(R.id.tv_purnish_content);
        et_purnish_title = (EditText) findViewById(R.id.et_title);

        et_purnish_title.setOnEditorActionListener(this);

        ((ImageView) findViewById(R.id.iv_btn_title_right)).setImageResource(R.drawable.purnish_edit);
        findViewById(R.id.iv_btn_title_right).setOnClickListener(this);
        findViewById(R.id.iv_btn_back).setOnClickListener(this);

        findViewById(R.id.btn_purnish_save).setOnClickListener(this);
        findViewById(R.id.btn_purnish_cancel).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_back:
                finish();
                break;
            case R.id.iv_btn_title_right:
                et_purnish_title.setEnabled(true);
                et_purnish_title.setInputType(InputType.TYPE_CLASS_TEXT);
                et_purnish_title.setSelection(et_purnish_title.getText().length());
                et_purnish_title.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_purnish_title,InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.btn_purnish_cancel:
                finish();
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Purnish_Cancel);
                break;
            case R.id.btn_purnish_save:
                pi.setPurnish_content(et_purnish_content.getText().toString());
                MineProfile.getInstance().getPurnish().getPurnishes().add(pi);
                MineProfile.getInstance().Save();
                finish();
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Purnish_Save);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if(actionId == EditorInfo.IME_ACTION_DONE){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm.isActive()){
                imm.hideSoftInputFromWindow(et_purnish_title.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        return true;
    }
}
