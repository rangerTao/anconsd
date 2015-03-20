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
public class PurnishEditorActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public static final String INDEX_PURNISH = "index";

    private int pIndex;

    PurnishInfo pi;

    private EditText et_purnish_content;
    private EditText et_purnish_title;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_purnish_editor);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        pIndex = getIntent().getIntExtra(INDEX_PURNISH, 0);

        pIndex = MineProfile.getInstance().getPurnish().getPurnishes().size() > pIndex ? pIndex : 0;

        pi = MineProfile.getInstance().getPurnish().getPurnishes().get(pIndex);

        initView();
    }

    @Override
    public void initView() {

        et_purnish_content = (EditText) findViewById(R.id.tv_purnish_content);
        et_purnish_title = (EditText) findViewById(R.id.et_title);

        et_purnish_title.setOnEditorActionListener(this);

        et_purnish_content.setText(pi.getPurnish_content());

        et_purnish_title.setText(pi.getPurnish_title());
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
                MineProfile.getInstance().getPurnish().getPurnishes().get(pIndex).setPurnish_content(et_purnish_content.getText().toString());
                MineProfile.getInstance().Save();
                finish();
                break;
            case R.id.iv_btn_title_right:
                et_purnish_title.setFocusable(true);
                et_purnish_title.setEnabled(true);
                et_purnish_title.setInputType(InputType.TYPE_CLASS_TEXT);
                et_purnish_title.setSelection(et_purnish_title.getText().length());
                imm.showSoftInput(et_purnish_title,0);
                break;
            case R.id.btn_purnish_default:
                if(MineProfile.getInstance().setDefaultPurnish(pIndex)){
                    Toast.makeText(this,getString(R.string.toast_default_saved),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,getString(R.string.toast_save_fail),Toast.LENGTH_LONG).show();
                }
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Purnish_default);
                break;
            case R.id.btn_purnish_delete:
                if(MineProfile.getInstance().removePurnish(pIndex)){
                    Toast.makeText(this,getString(R.string.toast_delete_succ),Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(this,getString(R.string.toast_delete_fail),Toast.LENGTH_LONG).show();
                }

                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Purnish_del);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if(actionId == EditorInfo.IME_ACTION_DONE){

            if(imm.isActive()){
                imm.hideSoftInputFromWindow(et_purnish_title.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }

            if(et_purnish_title.getText().length() > 0){
                MineProfile.getInstance().getPurnish().getPurnishes().get(pIndex).setPurnish_title(et_purnish_title.getText().toString());
                MineProfile.getInstance().Save();
            }else{
                et_purnish_title.setText(MineProfile.getInstance().getPurnish().getPurnishes().get(pIndex).getPurnish_title());
            }
        }

        return true;
    }
}
