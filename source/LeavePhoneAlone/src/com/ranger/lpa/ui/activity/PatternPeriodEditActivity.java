package com.ranger.lpa.ui.activity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.statics.ClickStats;
import com.ranger.lpa.utils.StringUtil;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

/**
 * Created by taoliang on 14-8-25.
 */
public class PatternPeriodEditActivity extends BaseActivity implements View.OnClickListener {

    public static final String PATTER_TYPE = "pattern_type";

    private int type = 0;

    AbstractWheel hours;
    AbstractWheel mins;
    AbstractWheel second;

    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_pattern_editor);

        type = getIntent().getIntExtra(PATTER_TYPE,0);

        initView();

    }

    @Override
    public void initView() {

        findViewById(R.id.iv_btn_back).setOnClickListener(this);
        ((ImageView) findViewById(R.id.iv_btn_title_right)).setImageResource(R.drawable.icon_btn_confirm);
        findViewById(R.id.iv_btn_title_right).setOnClickListener(this);

        hours = (AbstractWheel) findViewById(R.id.hour);

        mins = (AbstractWheel) findViewById(R.id.min);

        second = (AbstractWheel) findViewById(R.id.second);

        int hour = 1;
        int min = 0;

        switch (type){
            case 0:
                ((TextView) findViewById(R.id.tv_title)).setText("情侣模式");
                hour = getHour(MineProfile.getInstance().getLockPeriodCouple());
                min = getMins(MineProfile.getInstance().getLockPeriodCouple());
                break;
            case 1:
                ((TextView) findViewById(R.id.tv_title)).setText("聚会模式");
                hour = getHour(MineProfile.getInstance().getLockPeriodParty());
                min = getMins(MineProfile.getInstance().getLockPeriodParty());
                break;
            case 2:
                ((TextView) findViewById(R.id.tv_title)).setText("工作模式");
                hour = getHour(MineProfile.getInstance().getLockPeriodWork());
                min = getMins(MineProfile.getInstance().getLockPeriodWork());
                break;
        }

        hours.setViewAdapter(new NumericWheelAdapter(this, 0, 23));
        mins.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
        second.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));

        hours.setCyclic(true);
        mins.setCyclic(true);
        second.setCyclic(true);

        hours.setCurrentItem(hour);
        mins.setCurrentItem(min);
        second.setCurrentItem(0);

    }

    private int getMins(long period){

        String time = StringUtil.getFormattedTimeByMillseconds(period);

        try{
            int min = Integer.parseInt(time.split(":")[1]);
            return min;
        }catch (Exception ex){

        }

        return 0;

    }

    private int getHour(long period){

        String time = StringUtil.getFormattedTimeByMillseconds(period);

        try{
            int min = Integer.parseInt(time.split(":")[0]);
            return min;
        }catch (Exception ex){

        }

        return 0;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_btn_back:
                finish();
                break;
            case R.id.iv_btn_title_right:

                long period = (hours.getCurrentItem() * 3600 + mins.getCurrentItem() * 60) * 1000;

                if(type == 0){
                    MineProfile.getInstance().setLockPeriodCouple(period);
                    ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Pattern_Period_C,period + "");
                }else if (type == 1){
                    MineProfile.getInstance().setLockPeriodParty(period);
                    ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Pattern_Period_P,period + "");
                }else if (type == 2){
                    MineProfile.getInstance().setLockPeriodWork(period);
                    ClickStats.onClickStats(getApplicationContext(), ClickStats.CLICK_TYPE.Click_Pattern_Period_W,period + "");
                }

                Toast.makeText(this,getString(R.string.toast_default_saved),Toast.LENGTH_LONG).show();

                MineProfile.getInstance().Save();

                finish();

                break;
        }
    }
}
