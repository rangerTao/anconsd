package com.ranger.lpa.ui.activity;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;

/**
 * @author taoliang(taoliang@baidu-mgame.com)
 * @version V
 * @Description: TODO
 * @date 2014年5月31日 下午8:33:46
 */
public class LPAMainActivity extends BaseActivity {

    private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    LPABlueToothManager btManager;
    String blueName = "LPA";
    UUID mUuid;

    TextView tv_log;

    Handler mHandler = new Handler() {

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);

        btManager = LPABlueToothManager.getInstance(getApplicationContext());

    }

    ///
    public void enter_pattern(View view) {

        switch (view.getId()) {
            case R.id.btn_enter_couple:
                startFindingPhoneView(0);
                break;
            case R.id.btn_enter_party:
                startFindingPhoneView(1);
                break;
            case R.id.btn_enter_work:
                startFindingPhoneView(1);
                break;
        }

    }

    public void startFindingPhoneView(int type){
        Intent findingPhone = new Intent(this,LPAFoundPhoneCenter.class);
        findingPhone.putExtra(LPAFoundPhoneCenter.EXTRA_TYPE,type);
        startActivity(findingPhone);
    }

}
