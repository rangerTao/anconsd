package com.ranger.lpa.ui.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.pojos.IncomeResult;
import com.ranger.lpa.receiver.BlueToothReceiver;
import com.ranger.lpa.test.act.DiscoveryDevicesActivity;
import com.ranger.lpa.test.act.WifiApTestActivity;

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
            case R.id.btn_enter_party:
            case R.id.btn_enter_work:
                startFindingPhoneView(0);
                break;
        }

    }

    public void startFindingPhoneView(int type){
        Intent findingPhone = new Intent(this,LPAFoundPhoneCenter.class);
        findingPhone.putExtra("type",type);
        startActivity(findingPhone);
    }

}
