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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.connectity.wifi.LPAWifiManager;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.pojos.WifiInfo;
import com.ranger.lpa.receiver.IOnNotificationReceiver;
import com.ranger.lpa.test.adapter.BtDeviceListAdapter;
import com.ranger.lpa.thread.LPAClientThread;
import com.ranger.lpa.thread.LPAServerNotifyThread;
import com.ranger.lpa.tools.NotifyManager;
import com.ranger.lpa.ui.view.LPAKeyGuardView;
import com.ranger.lpa.utils.WifiUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by taoliang on 14-7-5.
 */
public class LPAFoundPhoneCenter extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static String EXTRA_TYPE = "type";
    public static final int MSG_CONNECTING_DIALOG_SHOW = 3;
    public static final int MSG_CONNECTING_DIALOG_HIDE = 3 << 1;
    public static final int MSG_WIFI_CONNECTED = 4;
    public static final int MSG_WIFI_FAILED = 4 << 1;

    private int TYPE_PATTERN = 0;

    View view_find_phone;
    View view_phone_found;

    RelativeLayout fl_btn_start_lock;
    FrameLayout fl_btn_lock_select_phone;

    String blueName = "LPA";

    private LPAFoundPhoneCenter appref;

    private BluetoothServerSocket bts;
    
    private LPABlueToothManager btManager;
    private DiscoveryFinishReceiver mDiscoveryFinishReceiver;

    private AtomicBoolean isLoadingCancel = new AtomicBoolean(false);

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int type = msg.what;

            switch (type){
                case SocketMessage.MSG_LOCK_REQUEST:
                case SocketMessage.MSG_STOPSERVER:
                    if(bts != null){
                        try{
                            bts.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    showLockRequestDialog();
                    break;
                case SocketMessage.MSG_LOCK_ACCEPT:
                    showLockedView();
                    break;
                case SocketMessage.MSG_LOCK_REFUSE:
                    break;
                case SocketMessage.MSG_GIVEUP_ACCEPT:
                    dismissLockedView();
                    break;
                case SocketMessage.MSG_GIVEUP_REQUEST:
                    showGiveupRequestDialog();
                    break;
                case SocketMessage.MSG_GIVEUP_REFUSE:
//                    dismissGiveupRequestDialog();
                    break;
                case MSG_CONNECTING_DIALOG_SHOW:
                    break;
                case MSG_CONNECTING_DIALOG_HIDE:
                    break;

            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_phone_found_center);

        TYPE_PATTERN = getIntent().getIntExtra(EXTRA_TYPE,0);

        appref = this;

        initView();

        btManager = LPABlueToothManager.getInstance(getApplicationContext());

        NotifyManager.getInstance(this).registerOnNotificationReceiver(notifyReceiver);
    }

    private void initView() {

        switch (TYPE_PATTERN){
            case 0:
                initCouplePattern();
                break;
            case 1:
            case 2:
                initPartyPattern();
                break;
            default:
                initCouplePattern();
                break;
        }

    }

    private void initCouplePattern(){
        view_find_phone = ((ViewStub) findViewById(R.id.stub_lock_center)).inflate();

        fl_btn_start_lock = (RelativeLayout) view_find_phone.findViewById(R.id.fl_search_btn);
        fl_btn_start_lock.setOnClickListener(this);
    }

    private void initPartyPattern(){
        view_find_phone = ((ViewStub) findViewById(R.id.stub_lock_center_party)).inflate();

//        fl_btn_start_lock = (RelativeLayout) view_find_phone.findViewById(R.id.fl_search_btn);
//        fl_btn_start_lock.setOnClickListener(this);
        view_find_phone.findViewById(R.id.btn_join_party_server).setOnClickListener(this);
        view_find_phone.findViewById(R.id.btn_start_party_server).setOnClickListener(this);
    }

    private void initFindingView() {

        if (view_phone_found == null) {
            view_phone_found = ((ViewStub) findViewById(R.id.stub_phone_found)).inflate();

            view_phone_found.findViewById(R.id.btn_cancel_finding).setOnClickListener(this);
        } else {
            view_phone_found.setVisibility(View.VISIBLE);
        }
    }

    private void resetFindingView(){
        if(view_find_phone!=null)
            view_find_phone.setVisibility(View.VISIBLE);

        if(view_phone_found != null)
            view_phone_found.setVisibility(View.GONE);

        finish();
    }

    private LPAKeyGuardView lpa;
    private View view_lock_control;

    private LPAClientThread lpact;

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
                            Socket socket = new Socket("192.168.191.1",8999);

//                            BluetoothSocket socket = dev.createRfcommSocketToServiceRecord(Constants.mUUID);
                            if(socket != null){
//                                socket.connect();

                                if(bts != null){
                                    bts.close();
                                }

                                lpact = new LPAClientThread(getApplicationContext(),socket,true);
                                lpact.start();

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

                if(lpact != null){
                    lpact.sendGiveupRequest();
                }

                break;
            case R.id.btn_request_accept:
                showLockedView();
                //
                lpact.sendLockRequestAccept();
                break;
            case R.id.btn_request_refuse:
                if(popup_lock_request_dialog != null && popup_lock_request_dialog.isShowing()){
                    popup_lock_request_dialog.dismiss();
                }
                lpact.sendLockRequestRefuse();
                break;
            case R.id.btn_giveup_accept:
                dismissGiveupRequestDialog();
                dismissLockedView();
                lpact.sendGiveupRequestAccept();
                break;
            case R.id.btn_giveup_cancel:
                dismissGiveupRequestDialog();
                lpact.sendGiveupRequestRefuse();
                break;
            case R.id.btn_join_party_server:
                startBarcodeScanner();
                break;
            case R.id.btn_start_party_server:
                LPAWifiManager.getInstance(getApplicationContext()).startWifiAp();
                try{
                    ImageView ivBarcode = (ImageView) view_find_phone.findViewById(R.id.iv_barcode);
//                    Bitmap barcode = EncodingHandler.createQRCode(LPAWifiManager.getInstance(getApplicationContext()).getmWifiInfo().getMessageString(), ivBarcode.getWidth());
//                    ivBarcode.setImageBitmap(barcode);
//
//                    serNotifyThread = new LPAServerNotifyThread(getApplicationContext());
//
//                    serNotifyThread.start();

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private LPAServerNotifyThread serNotifyThread;

    private void startBarcodeScanner(){
        Intent intent = new Intent(this,BarcodeScannerActivity.class);
        startActivityForResult(intent,BarcodeScannerActivity.RESULT_BARCODE);
    }

    // 查找蓝牙设备
    private static final int REQUEST_DISCOVERABLE = 0x2;

    // 开始查找蓝牙设备
    private void startBlueToothAndDiscovery() {
        registerFinishReceiver();

        if (!btManager.getBluetoothAdapter().isEnabled()) {
            btManager.startDiscovery();
        }

//        btManager.getBluetoothAdapter().setName("test");
        btManager.setDeviceVisiable(this);

        // 设置蓝牙可见
        Intent enabler = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(enabler, REQUEST_DISCOVERABLE);

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

    private IOnNotificationReceiver notifyReceiver = new IOnNotificationReceiver() {
        @Override
        public void onNotificated(int type) {

            Message msg = new Message();
            msg.what = type;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onNotificated(int type, BaseInfo info) {

        }
    };

    private View view_giveup_request;

    private void showLockedView() {
        View lock_view = View.inflate(this, R.layout.layout_locked_view, null);
        view_giveup_request = lock_view.findViewById(R.id.include_dialog_giveup_confirm);
        lpa = LPAKeyGuardView.getInstance(this);
        lpa.setLockView(lock_view);
        lpa.lock();

        view_lock_control = lock_view.findViewById(R.id.fl_lock_area);
        view_lock_control.setOnClickListener(this);
    }

    private void dismissLockedView(){

        if(lpa != null && lpa.isShowing()){
            lpa.unlock();
        }

        resetFindingView();
    }

    private View view_select_btdevices;
    private Dialog popup_select_btdevices;
    private ListView lv_devices;
    BtDeviceListAdapter btla;

    private View view_lock_request_dialog;
    private Dialog popup_lock_request_dialog;

    //显示锁定请求弹窗
    private void showLockRequestDialog(){

        if(view_lock_request_dialog == null){
            view_lock_request_dialog = getLayoutInflater().inflate(R.layout.dialog_lock_request_received,null);
        }

        if(popup_lock_request_dialog == null){
            popup_lock_request_dialog = new Dialog(appref);
            popup_lock_request_dialog.setContentView(view_lock_request_dialog, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            view_lock_request_dialog.findViewById(R.id.btn_request_accept).setOnClickListener(this);
            view_lock_request_dialog.findViewById(R.id.btn_request_refuse).setOnClickListener(this);
        }

        if(popup_lock_request_dialog != null && !popup_lock_request_dialog.isShowing()){
            popup_lock_request_dialog.show();
        }
    }

    //显示锁定请求弹窗
    private void showGiveupRequestDialog(){

        if(view_giveup_request != null){
            view_giveup_request.setVisibility(View.VISIBLE);
            view_giveup_request.findViewById(R.id.btn_giveup_cancel).setOnClickListener(this);
            view_giveup_request.findViewById(R.id.btn_giveup_accept).setOnClickListener(this);
        }
    }

    private void dismissGiveupRequestDialog(){
        if(view_giveup_request != null){
            view_giveup_request.setVisibility(View.INVISIBLE);
        }
    }

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

        NotifyManager.getInstance(this).unRegisterNotificationReceiver(notifyReceiver);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_DISCOVERABLE){
            if(resultCode > 0){
                new Thread() {

                    public void run() {

                        try {
                            bts = btManager
                                    .getBluetoothAdapter()
                                    .listenUsingRfcommWithServiceRecord(blueName,
                                            Constants.mUUID);

                            BluetoothSocket bs = bts.accept();

                            if(bs != null){
                                lpact = new LPAClientThread(getApplicationContext(),bs,false);
                                lpact.start();

                                if(popup_select_btdevices != null && popup_select_btdevices.isShowing()){
                                    popup_select_btdevices.dismiss();
                                    popup_select_btdevices = null;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else{
                Toast.makeText(getApplicationContext(),"请选择“允许”，才能使用此功能！",Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == BarcodeScannerActivity.RESULT_BARCODE){
            if(data != null){
                Bundle bundle = data.getExtras();
                String result = bundle.getString(BarcodeScannerActivity.RESULT_CONTENT);
                if(result != null && !result.equals("")){
                    Gson gson = new Gson();
                    WifiInfo wInfo = gson.fromJson(result,WifiInfo.class);
                    if(wInfo != null){
                        WifiUtils.initWifiSetting(getApplicationContext());
                        if(LPAWifiManager.getInstance(getApplicationContext()).connectWifi(wInfo)){

                        }else{

                        }
                    }
                }
            }
        }
    }
}