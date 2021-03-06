package com.ranger.lpa.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.adapter.LPAWifiUsersAdapter;
import com.ranger.lpa.connectity.wifi.LPAWifiManager;
import com.ranger.lpa.encoding.EncodingHandler;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.NotifyServerInfo;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.pojos.WifiInfo;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.receiver.IOnNotificationReceiver;
import com.ranger.lpa.test.adapter.BtDeviceListAdapter;
import com.ranger.lpa.thread.LPAServerNotifyThread;
import com.ranger.lpa.thread.LPAUdpClientThread;
import com.ranger.lpa.tools.NotifyManager;
import com.ranger.lpa.ui.view.LPAKeyGuardView;
import com.ranger.lpa.utils.StringUtil;
import com.ranger.lpa.utils.WifiUtils;

/**
 * Created by taoliang on 14-8-5.
 */
public class LPAPartyCenter extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, WifiUtils.OnWifiConnected {

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

            //update the users' view
            BaseInfo wUser = (BaseInfo) msg.obj;
            WifiUser wfUser = null;
            if(wUser != null){
                wfUser = new Gson().fromJson(wUser.getMessage(),WifiUser.class);
            }

            switch (type) {
                case SocketMessage.MSG_LOCK_REQUEST:
                    if(LPApplication.getInstance().isSelfServer()){
                        showLockedView();
                    }else{
                        showLockRequestDialog();
                    }
                    break;
                case SocketMessage.MSG_STOPSERVER:
                    showLockRequestDialog();
                    break;
                case SocketMessage.MSG_LOCK_ACCEPT:
                    refreshUserStatus(wfUser);
                    break;
                case SocketMessage.MSG_LOCK_START:
                    dismissWaitingPopup();
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
                    dismissGiveupRequestDialog();
                    break;
                case SocketMessage.MSG_SUBMIT_NAME:
                    if(wfUser != null && !NotifyServerInfo.getInstance().getUsers().contains(wfUser.getUdid())){
                        NotifyServerInfo.getInstance().addUser(wfUser);
                        adapterUsers.notifyDataSetChanged();
                    }

                    break;
                case MSG_CONNECTING_DIALOG_SHOW:
                    break;
                case MSG_CONNECTING_DIALOG_HIDE:
                    break;
                case BaseInfo.MSG_NOTIFY_SERVER:
                    if(!LPApplication.getInstance().isSelfServer()){

                    }
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

    public void initView() {

        initPartyPattern();

    }

    private TextView tvLockPeriodHint;

    private void initPartyPattern() {
        view_find_phone = ((ViewStub) findViewById(R.id.stub_lock_center_party)).inflate();

        view_find_phone.findViewById(R.id.btn_join_party_server).setOnClickListener(this);
        view_find_phone.findViewById(R.id.btn_start_party_server).setOnClickListener(this);
        tvLockPeriodHint = (TextView)view_find_phone.findViewById(R.id.tv_lock_time_period);

        tvLockPeriodHint.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriodParty()));

        view_find_phone.findViewById(R.id.btn_screen_select).setOnClickListener(this);
        view_find_phone.findViewById(R.id.btn_settings).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tvLockPeriodHint.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriod()));
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

    private LPAUdpClientThread clientThread;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_screen_select:
                finish();
                break;
            case R.id.fl_search_btn:

                view_find_phone.setVisibility(View.GONE);

                initFindingView();

                setFindingPhoneView();

                break;
            case R.id.tv_phone_name_found_small:

                break;
            case R.id.btn_join_party_server:
//                startBarcodeScanner();
                WifiUtils.getInstance().setmWifiConnected(this);

                clientThread = new LPAUdpClientThread(getApplicationContext());
                clientThread.start();

                showWaitingPopup();
                break;
            case R.id.btn_start_party_server:
//                LPAWifiManager.getInstance(getApplicationContext()).startWifiAp();
                try {
                    LPApplication.getInstance().setSelfServer(true);

                    serNotifyThread = new LPAServerNotifyThread(getApplicationContext());
                    clientThread = new LPAUdpClientThread(getApplicationContext());

                    serNotifyThread.start();
                    clientThread.start();

                    showJoinedUserPopup();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_btn_start_party:
                if(serNotifyThread != null && NotifyServerInfo.getInstance().getUsers().size() > 0){
                    MineProfile.getInstance().setLocked(true);
                }
                break;
            case R.id.btn_request_accept:
                //show lock waiting view
                showWaitingPopup();
                break;
            case R.id.btn_request_refuse:
                dismissLockRequestDialog();
                
                if(clientThread != null){
                    clientThread.stop();
                }
                
                break;
            case R.id.btn_cancel_waiting_popup:
                dismissWaitingPopup();
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

            initJoinedPopupView(joined_view);

        }

        if(!mJoinedUserPopup.isShowing()){
            mJoinedUserPopup.showAtLocation(findViewById(R.id.ll_found_center_root), Gravity.TOP | Gravity.LEFT,0,0);
        }

    }

    private PopupWindow mWaitingToStartPopup;
    View waitStarting;

    private void dismissWaitingPopup(){
        if(mWaitingToStartPopup != null && mWaitingToStartPopup.isShowing()){
            mWaitingToStartPopup.dismiss();
            mWaitingToStartPopup = null;
        }
    }

    private void showWaitingPopup(){

        if(mWaitingToStartPopup == null){
            mWaitingToStartPopup = new PopupWindow(getApplicationContext());
            mWaitingToStartPopup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            waitStarting = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_user_waiting_start,null);
            mWaitingToStartPopup.setContentView(waitStarting);

            waitStarting.findViewById(R.id.btn_cancel_waiting_popup).setOnClickListener(this);
            gvJoinedUsers = (GridView) waitStarting.findViewById(R.id.gv_joined_users);

            adapterUsers = new LPAWifiUsersAdapter(getApplicationContext());
            gvJoinedUsers.setAdapter(adapterUsers);

        }

        if(!mWaitingToStartPopup.isShowing()){
            mWaitingToStartPopup.showAtLocation(findViewById(R.id.ll_found_center_root), Gravity.TOP | Gravity.LEFT,0,0);
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

        @Override
        public void onNotificated(int type, BaseInfo info) {
            Message msg = new Message();
            msg.what = type;
            msg.obj = info;
            mHandler.sendMessage(msg);
        }
    };

    private View view_giveup_request;

    private void refreshUserStatus(WifiUser wu){

        String udid = wu.getUdid();
        for(WifiUser user : NotifyServerInfo.getInstance().getUsers()){
            if(user.getUdid().equals(udid)){
                user.setAccept(1);
            }
        }

        adapterUsers.notifyDataSetChanged();
    }

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
            try{
                lpa.unlock();
            }catch (Exception ex){
                ex.printStackTrace();
            }
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

    private void dismissLockRequestDialog(){
        if (popup_lock_request_dialog != null && popup_lock_request_dialog.isShowing()) {
            popup_lock_request_dialog.dismiss();
            popup_lock_request_dialog = null;
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

                    try{
                        Gson gson = new Gson();
                        WifiInfo wInfo = gson.fromJson(result, WifiInfo.class);
                        if (wInfo != null) {
                            if (LPAWifiManager.getInstance(getApplicationContext()).connectWifi(wInfo)) {
                                WifiUtils.initWifiSetting(LPAPartyCenter.this,wInfo.getmSSID());
                            } else {
                                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onConnected() {
        clientThread = new LPAUdpClientThread(getApplicationContext());
        clientThread.start();
    }
}