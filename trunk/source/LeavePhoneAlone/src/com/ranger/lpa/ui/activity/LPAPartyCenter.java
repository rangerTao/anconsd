package com.ranger.lpa.ui.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.adapter.LPAWifiUsersAdapter;
import com.ranger.lpa.connectity.bluetooth.LPABlueToothManager;
import com.ranger.lpa.connectity.wifi.LPAWifiManager;
import com.ranger.lpa.encoding.EncodingHandler;
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
 * Created by taoliang on 14-8-5.
 */
public class LPAPartyCenter extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int MSG_CONNECTING_DIALOG_SHOW = 3;
    public static final int MSG_CONNECTING_DIALOG_HIDE = 3 << 1;
    public static final int MSG_WIFI_CONNECTED = 4;
    public static final int MSG_WIFI_FAILED = 4 << 1;

    View view_find_phone;
    View view_phone_found;

    private LPAPartyCenter appref;

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int type = msg.what;

            switch (type) {
                case SocketMessage.MSG_LOCK_REQUEST:
                case SocketMessage.MSG_STOPSERVER:
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
                case SocketMessage.MSG_SUBMIT_NAME:

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

        appref = this;

        initView();

        NotifyManager.getInstance(this).registerOnNotificationReceiver(notifyReceiver);
    }

    private void initView() {

        initPartyPattern();

    }

    private void initPartyPattern() {
        view_find_phone = ((ViewStub) findViewById(R.id.stub_lock_center_party)).inflate();

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

    private void resetFindingView() {
        if (view_find_phone != null)
            view_find_phone.setVisibility(View.VISIBLE);

        if (view_phone_found != null)
            view_phone_found.setVisibility(View.GONE);

        finish();
    }

    private LPAKeyGuardView lpa;
    private View view_lock_control;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_search_btn:

                view_find_phone.setVisibility(View.GONE);

                initFindingView();

                setFindingPhoneView();

                break;
            case R.id.tv_phone_name_found_small:

                break;
            case R.id.btn_join_party_server:
                startBarcodeScanner();
                break;
            case R.id.btn_start_party_server:
                LPAWifiManager.getInstance(getApplicationContext()).startWifiAp();
                try {

                    serNotifyThread = new LPAServerNotifyThread();

                    serNotifyThread.start();

                    showJoinedUserPopup();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private PopupWindow mJoinedUserPopup;
    View joined_view;


    private void showJoinedUserPopup(){

        if(mJoinedUserPopup == null){
            mJoinedUserPopup = new PopupWindow(getApplicationContext());
            mJoinedUserPopup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            joined_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_wifi_barcode_users,null);
            mJoinedUserPopup.setContentView(joined_view);

        }

        if(!mJoinedUserPopup.isShowing()){
            mJoinedUserPopup.showAtLocation(getWindow().getDecorView(), Gravity.TOP | Gravity.LEFT,0,0);
        }

    }

    private ImageView barcodeShow;
    private GridView gvJoinedUsers;
    private LPAWifiUsersAdapter adapterUsers;

    private void initJoinedPopupView(View root){

        root.findViewById(R.id.btn_cancel_joined_popup).setOnClickListener(this);
        root.findViewById(R.id.tv_btn_start_party).setOnClickListener(this);

        barcodeShow = (ImageView) root.findViewById(R.id.iv_barcode);
        gvJoinedUsers = (GridView) root.findViewById(R.id.gv_joined_users);

        adapterUsers = new LPAWifiUsersAdapter(getApplicationContext());
        gvJoinedUsers.setAdapter(adapterUsers);

        try{
            Bitmap barcode = EncodingHandler.createQRCode(LPAWifiManager.getInstance(getApplicationContext()).getmWifiInfo().getMessageString(), barcodeShow.getWidth());
            barcodeShow.setImageBitmap(barcode);
        }catch (Exception e){
            barcodeShow.setImageResource(R.drawable.barcode_tip);
            e.printStackTrace();
        }

    }

    private LPAServerNotifyThread serNotifyThread;

    private void startBarcodeScanner() {
        Intent intent = new Intent(this, BarcodeScannerActivity.class);
        startActivityForResult(intent, BarcodeScannerActivity.RESULT_BARCODE);
    }

    private TextView tv_phone_name_founded;

    private void setFindingPhoneView() {

        if (tv_phone_name_founded == null) {
            tv_phone_name_founded = (TextView) view_phone_found.findViewById(R.id.tv_phone_name_found);
        }

        tv_phone_name_founded.setText(R.string.text_finding_phone_hint);

    }

    private IOnNotificationReceiver notifyReceiver = new IOnNotificationReceiver() {
        @Override
        public void onNotificated(int type) {

            Message msg = new Message();
            msg.what = type;
            mHandler.sendMessage(msg);
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

    private void dismissLockedView() {

        if (lpa != null && lpa.isShowing()) {
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
    private void showLockRequestDialog() {

        if (view_lock_request_dialog == null) {
            view_lock_request_dialog = getLayoutInflater().inflate(R.layout.dialog_lock_request_received, null);
        }

        if (popup_lock_request_dialog == null) {
            popup_lock_request_dialog = new Dialog(appref);
            popup_lock_request_dialog.setContentView(view_lock_request_dialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            view_lock_request_dialog.findViewById(R.id.btn_request_accept).setOnClickListener(this);
            view_lock_request_dialog.findViewById(R.id.btn_request_refuse).setOnClickListener(this);
        }

        if (popup_lock_request_dialog != null && !popup_lock_request_dialog.isShowing()) {
            popup_lock_request_dialog.show();
        }
    }

    //显示锁定请求弹窗
    private void showGiveupRequestDialog() {

        if (view_giveup_request != null) {
            view_giveup_request.setVisibility(View.VISIBLE);
            view_giveup_request.findViewById(R.id.btn_giveup_cancel).setOnClickListener(this);
            view_giveup_request.findViewById(R.id.btn_giveup_accept).setOnClickListener(this);
        }
    }

    private void dismissGiveupRequestDialog() {
        if (view_giveup_request != null) {
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
            popup_select_btdevices.setContentView(view_select_btdevices, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NotifyManager.getInstance(this).unRegisterNotificationReceiver(notifyReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BarcodeScannerActivity.RESULT_BARCODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString(BarcodeScannerActivity.RESULT_CONTENT);
                if (result != null && !result.equals("")) {
                    Gson gson = new Gson();
                    WifiInfo wInfo = gson.fromJson(result, WifiInfo.class);
                    if (wInfo != null) {
                        WifiUtils.initWifiSetting(getApplicationContext());
                        if (LPAWifiManager.getInstance(getApplicationContext()).connectWifi(wInfo)) {

                        } else {

                        }
                    }
                }
            }
        }
    }
}