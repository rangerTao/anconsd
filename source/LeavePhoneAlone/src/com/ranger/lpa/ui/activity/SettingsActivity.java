package com.ranger.lpa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.statics.ClickStats;

import org.w3c.dom.Text;

/**
 * Created by taoliang on 14-8-21.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    TextView tv_purnish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_setting);

        initView();
    }

    @Override
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.title_setting);
        findViewById(R.id.iv_btn_back).setOnClickListener(this);
        findViewById(R.id.rl_purnish_setting).setOnClickListener(this);
        findViewById(R.id.rl_pattern_setting).setOnClickListener(this);

        tv_purnish = (TextView) findViewById(R.id.tv_purnish_content);

        tv_purnish.setText(MineProfile.getInstance().getDefaultPurnishContent());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_btn_back:
                finish();
                break;
            case R.id.rl_purnish_setting:
                Intent intent = new Intent(this,SettingPurnishListActivity.class);
                startActivity(intent);
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.ENTER_SETTING_Purnish);
                break;
            case R.id.rl_pattern_setting:
                Intent pIntent = new Intent(this,SettingPatternActivity.class);
                startActivity(pIntent);
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Enter_setting_pattern);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.ENTER_SETTING);
    }
}
