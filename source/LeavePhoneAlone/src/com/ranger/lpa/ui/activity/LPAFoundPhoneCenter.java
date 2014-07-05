package com.ranger.lpa.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.pojos.IncomeResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by taoliang on 14-7-5.
 */
public class LPAFoundPhoneCenter extends BaseActivity implements View.OnClickListener {

    View view_find_phone;
    View view_phone_found;

    FrameLayout fl_btn_start_lock;
    TextView tv_cancel_finding;

    String blueName = "LPA";
    UUID mUuid;

    private LPABlueToothManager btManager;
    private DiscoveryFinishReceiver mDiscoveryFinishReceiver;

    private AtomicBoolean isLoadingCancel = new AtomicBoolean(false);

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_phone_found_center);

        initView();

        btManager = LPABlueToothManager.getInstance(getApplicationContext());
    }

    private void initView() {
        view_find_phone = ((ViewStub) findViewById(R.id.stub_lock_center)).inflate();
//

        fl_btn_start_lock = (FrameLayout) view_find_phone.findViewById(R.id.fl_search_btn);
        fl_btn_start_lock.setOnClickListener(this);
    }

    private void initFindingView() {

        if (view_phone_found == null) {
            view_phone_found = ((ViewStub) findViewById(R.id.stub_phone_found)).inflate();

            view_phone_found.findViewById(R.id.btn_cancel_finding).setOnClickListener(this);
        } else {
            view_phone_found.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_search_btn:

                view_find_phone.setVisibility(View.GONE);

                initFindingView();

                startBlueToothAndDiscovery();

                break;
            case R.id.btn_cancel_finding:

                if (isLoadingCancel.get()) {
                    view_phone_found.setVisibility(View.GONE);
                    view_find_phone.setVisibility(View.VISIBLE);

                    if (mDiscoveryFinishReceiver != null) {
                        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mDiscoveryFinishReceiver);
                    }
                }
                break;
        }
    }

    //开始查找蓝牙设备
    private void startBlueToothAndDiscovery() {
        registerFinishReceiver();

        if (!btManager.getBluetoothAdapter().isEnabled()) {
            btManager.startDiscovery();
        }

        btManager.getBluetoothAdapter().setName("test");
        btManager.setDeviceVisiable(this);
        new Thread() {
            public void run() {

                try {
                    BluetoothServerSocket bts = btManager
                            .getBluetoothAdapter()
                            .listenUsingRfcommWithServiceRecord(blueName,
                                    Constants.mUUID);

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            showErrorLog("waiting client \n");
                        }
                    });
                    final BluetoothSocket bs = bts.accept();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            showErrorLog(bs.getRemoteDevice().getName() + "\n");
                        }
                    });

                    if (bs != null) {
                        InputStream inputStream = bs.getInputStream();

                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                        String strIncome = br.readLine();

                        Gson gsonIncome = new Gson();
                        IncomeResult ir = gsonIncome.fromJson(strIncome, IncomeResult.class);

                        while (ir.getErrcode() != 10000) {

                            showErrorLog(ir.getErrmsg());

                            strIncome = br.readLine();

                            ir = gsonIncome.fromJson(strIncome, IncomeResult.class);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void registerFinishReceiver() {
        IntentFilter intentDiscoveryFinish = new IntentFilter(Constants.action_bt_scan_finish);
        mDiscoveryFinishReceiver = new DiscoveryFinishReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mDiscoveryFinishReceiver, intentDiscoveryFinish);
    }

    class DiscoveryFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.action_bt_scan_finish)) {
                if (!isLoadingCancel.get()) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mDiscoveryFinishReceiver);

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            //dismiss loading view. and set the founded view.

                        }
                    });

                    isLoadingCancel.set(true);
                }

            }

        }

    }
}