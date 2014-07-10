package com.ranger.lpa.ui.activity;

import android.app.Dialog;
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
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.pojos.IncomeResult;
import com.ranger.lpa.test.adapter.BtDeviceListAdapter;
import com.ranger.lpa.ui.view.LPAKeyGuardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by taoliang on 14-7-5.
 */
public class LPAFoundPhoneCenter extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    View view_find_phone;
    View view_phone_found;

    FrameLayout fl_btn_start_lock;
    FrameLayout fl_btn_lock_select_phone;
    private FrameLayout fl_start_to_lock;
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

    private LPAKeyGuardView lpa;
    private View view_lock_control;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_search_btn:

                view_find_phone.setVisibility(View.GONE);

                initFindingView();

                startBlueToothAndDiscovery();

                setFindingPhoneView();

                break;
            case R.id.fl_btn_lock_selected_phone:

                new Thread(){

                    @Override
                    public void run() {
                        super.run();
                        
                        try {
                            Socket socket = new Socket("172.16.10.141",8999);

//                            BluetoothSocket socket = dev.createRfcommSocketToServiceRecord(Constants.mUUID);
                            if(socket != null){
//                                Toast.makeText(getApplicationContext(), socket.getRemoteDevice().getName(), 1000).show();
//                                socket.connect();
                                socket.getOutputStream().write("test\n".getBytes());
                                socket.getOutputStream().flush();
                                Log.e("TAG", "message sended");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    
                }.start();
               

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
            case R.id.tv_phone_name_found_small:

                break;
            case R.id.fl_lock_area:
                if (lpa != null) {
                    lpa.unlock();
                }
        }
    }

    private void showLockedView() {
        View lock_view = View.inflate(this, R.layout.layout_locked_view, null);
        lpa = LPAKeyGuardView.getInstance(this);
        lpa.setLockView(lock_view);
        lpa.lock();

        view_lock_control = lock_view.findViewById(R.id.fl_lock_area);
        view_lock_control.setOnClickListener(this);
    }

    // 查找蓝牙设备
    private static final int REQUEST_DISCOVERABLE = 0x2;

    // 开始查找蓝牙设备
    private void startBlueToothAndDiscovery() {
        registerFinishReceiver();

        if (!btManager.getBluetoothAdapter().isEnabled()) {
            btManager.startDiscovery();
        }

        btManager.getBluetoothAdapter().setName("test");
        btManager.setDeviceVisiable(this);

        // 设置蓝牙可见
        Intent enabler = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(enabler, REQUEST_DISCOVERABLE);
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

                        while (ir.getErrcode() != 10000 || !strIncome.equals("end")) {

                            showErrorLog(ir.getErrmsg());

                            strIncome = br.readLine();

                            ir = gsonIncome.fromJson(strIncome, IncomeResult.class);
                        }
                        
                        Log.e("TAG", "server finished");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        btManager.startDiscovery();
    }

    private void registerFinishReceiver() {
        IntentFilter intentDiscoveryFinish = new IntentFilter(Constants.action_bt_scan_finish);
        mDiscoveryFinishReceiver = new DiscoveryFinishReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mDiscoveryFinishReceiver, intentDiscoveryFinish);
    }

    private TextView tv_phone_name_founded;
    private TextView tv_phone_name_small;

    private void setFindingPhoneView() {

        if (tv_phone_name_founded == null) {
            tv_phone_name_founded = (TextView) view_phone_found.findViewById(R.id.tv_phone_name_found);
        }

        tv_phone_name_founded.setText(R.string.text_finding_phone_hint);

    }

    private FrameLayout fl_finding_phone_progress;

    //当搜索蓝牙设备结束时，更新界面
    private void setFoundedPhoneView() {

        if (fl_btn_lock_select_phone == null) {
            fl_btn_lock_select_phone = (FrameLayout) view_phone_found.findViewById(R.id.fl_btn_lock_selected_phone);
        }

        if (fl_finding_phone_progress == null) {
            fl_finding_phone_progress = (FrameLayout) view_phone_found.findViewById(R.id.fl_bind_finding_progress);
        }

        ArrayList<BluetoothDevice> devFounded = btManager.getBluetoothDevices();
        if (devFounded.size() > 1) {
            showDeviceSelectDialog();
        } else {
            setFoundedViewByBlueDevice(devFounded.get(0));
        }
    }

    private View view_select_btdevices;
    private Dialog popup_select_btdevices;
    private ListView lv_devices;
    BtDeviceListAdapter btla;

    //显示选择设备弹窗
    private void showDeviceSelectDialog() {

        if (view_select_btdevices == null) {
            view_select_btdevices = getLayoutInflater().inflate(R.layout.layout_bt_discovery_activity, null);
        }

        if (popup_select_btdevices == null) {
            popup_select_btdevices = new Dialog(this);
            popup_select_btdevices.setContentView(view_select_btdevices, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            lv_devices = (ListView) view_select_btdevices.findViewById(R.id.lv_bt_devices);

            btla = new BtDeviceListAdapter(getApplicationContext());
            lv_devices.setAdapter(btla);

            lv_devices.setOnItemClickListener(this);
        }

        if (popup_select_btdevices != null && !popup_select_btdevices.isShowing()) {
            popup_select_btdevices.show();

            if (btla != null) {
                btla.notifyDataSetChanged();
            }
        }

    }

    //根据获取到的蓝牙设备信息，更新界面状态
    private void setFoundedViewByBlueDevice(BluetoothDevice btDeviceFounded) {

        fl_finding_phone_progress.setVisibility(View.GONE);
        fl_btn_lock_select_phone.setVisibility(View.VISIBLE);
        fl_btn_lock_select_phone.setOnClickListener(this);

        tv_phone_name_founded = (TextView) view_phone_found.findViewById(R.id.tv_phone_name_found);
        tv_phone_name_small = (TextView) view_phone_found.findViewById(R.id.tv_phone_name_found_small);

        tv_phone_name_small.setVisibility(View.VISIBLE);
//        tv_phone_name_small.setOnClickListener(this);

        tv_phone_name_small.setText(btDeviceFounded.getName());
        tv_phone_name_founded.setText(getString(R.string.text_phone_found, btDeviceFounded.getName()));
    }

    private BluetoothDevice dev;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dev = LPABlueToothManager.getInstance(getApplicationContext()).getBluetoothDevices().get(position);
        //connect to dev's service
        setFoundedViewByBlueDevice(dev);

        dismissSelectPhonePopup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissSelectPhonePopup();

    }

    private void dismissSelectPhonePopup() {
        if(popup_select_btdevices!= null && popup_select_btdevices.isShowing()){
            popup_select_btdevices.dismiss();
        }
    }

    class DiscoveryFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.action_bt_scan_finish)) {

                showErrorLog("finding finish");

                if (!isLoadingCancel.get()) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mDiscoveryFinishReceiver);

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // dismiss loading view. and set the founded view.
                            if (btManager.getBluetoothDevices().size() < 1) {
                                Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_LONG).show();
                            } else {
                                // set the found view
                                setFoundedPhoneView();
                            }
                        }
                    });

                    isLoadingCancel.set(true);
                }

            }

        }

    }
}