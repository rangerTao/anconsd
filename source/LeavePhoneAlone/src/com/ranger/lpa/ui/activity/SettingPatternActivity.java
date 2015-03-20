package com.ranger.lpa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.statics.ClickStats;
import com.ranger.lpa.utils.StringUtil;

/**
 * Created by taoliang on 14-8-25.
 */
public class SettingPatternActivity extends BaseActivity implements View.OnClickListener {

    TextView tvCouplePeriod;
    TextView tvPartyPeriod;
    TextView tvWorkPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_pattern_list);

        initView();
    }

    @Override
    public void initView() {

        ((TextView) findViewById(R.id.tv_title)).setText("情景模式");

        findViewById(R.id.rl_pattern_couple).setOnClickListener(this);
        findViewById(R.id.rl_pattern_party).setOnClickListener(this);
        findViewById(R.id.rl_pattern_work).setOnClickListener(this);

        tvCouplePeriod = (TextView) findViewById(R.id.tv_couple_period);
        tvPartyPeriod = (TextView) findViewById(R.id.tv_party_period);
        tvWorkPeriod = (TextView) findViewById(R.id.tv_work_period);

        initValue();

    }

    private void initValue(){

        tvCouplePeriod.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriodCouple()));
        tvPartyPeriod.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriodParty()));
        tvWorkPeriod.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriodWork()));

    }


    @Override
    public void onClick(View v) {

        Intent editIntent = new Intent(this,PatternPeriodEditActivity.class);

        switch (v.getId()){
            case R.id.iv_btn_back:
                finish();
                return;
            case R.id.rl_pattern_couple:
                editIntent.putExtra(PatternPeriodEditActivity.PATTER_TYPE,0);
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Enter_Patter_Couple);
                break;
            case R.id.rl_pattern_party:
                editIntent.putExtra(PatternPeriodEditActivity.PATTER_TYPE,1);
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Enter_Patter_Party);
                break;
            case R.id.rl_pattern_work:
                editIntent.putExtra(PatternPeriodEditActivity.PATTER_TYPE,2);
                ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Enter_Patter_Work);
                break;
        }

        startActivity(editIntent);
    }

    @Override
    public void onResume() {
        super.onResume();

        initView();
    }
}
