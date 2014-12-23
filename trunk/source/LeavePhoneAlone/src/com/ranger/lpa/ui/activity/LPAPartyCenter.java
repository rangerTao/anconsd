package com.ranger.lpa.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.ranger.lpa.Constants;
import com.ranger.lpa.LPApplication;
import com.ranger.lpa.MineProfile;
import com.ranger.lpa.R;
import com.ranger.lpa.adapter.LPAWifiUsersAdapter;
import com.ranger.lpa.connectity.SocketSessionManager;
import com.ranger.lpa.connectity.wifi.LPAWifiManager;
import com.ranger.lpa.encoding.EncodingHandler;
import com.ranger.lpa.pojos.BaseInfo;
import com.ranger.lpa.pojos.IncomeResult;
import com.ranger.lpa.pojos.NotifyServerInfo;
import com.ranger.lpa.pojos.SocketMessage;
import com.ranger.lpa.pojos.WifiInfo;
import com.ranger.lpa.pojos.WifiUser;
import com.ranger.lpa.receiver.IOnNotificationReceiver;
import com.ranger.lpa.share.ShareUtil;
import com.ranger.lpa.test.adapter.BtDeviceListAdapter;
import com.ranger.lpa.thread.ClientServerHandler;
import com.ranger.lpa.thread.LPAServerNotifyThread;
import com.ranger.lpa.thread.LPAUdpClientThread;
import com.ranger.lpa.tools.NotifyManager;
import com.ranger.lpa.ui.view.LPAKeyGuardView;
import com.ranger.lpa.utils.StringUtil;
import com.ranger.lpa.utils.WifiUtils;
import com.ranger.lpa.wxapi.WXEntryActivity;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by taoliang on 14-8-5.
 */
public class LPAPartyCenter extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, WifiUtils.OnWifiConnected {

    public static final int MSG_CONNECTING_DIALOG_SHOW = 3;
    public static final int MSG_CONNECTING_DIALOG_HIDE = 3 << 1;
    public static final int MSG_WIFI_CONNECTED = 4;
    public static final int MSG_WIFI_FAILED = 4 << 1;

    private long lock_period = 60* 60*1000;

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
            IncomeResult wfUser = null;
            if(wUser != null){
                wfUser = new Gson().fromJson(wUser.getMessage(),IncomeResult.class);
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
                    for(WifiUser wuser : wfUser.getUsers()){
                        if(wfUser != null && !NotifyServerInfo.getInstance().isUserExists(wuser)){

                            NotifyServerInfo.getInstance().addUser(wuser);
                            adapterUsers.notifyDataSetChanged();
                        }
                    }
                    break;
                case MSG_CONNECTING_DIALOG_SHOW:
                    break;
                case MSG_CONNECTING_DIALOG_HIDE:
                    break;
                case BaseInfo.MSG_NOTIFY_SERVER:
                    for(WifiUser wuser : wfUser.getUsers()){
                        if(wfUser != null && !NotifyServerInfo.getInstance().isUserExists(wuser)){
                            NotifyServerInfo.getInstance().addUser(wuser);
                            adapterUsers.notifyDataSetChanged();
                        }
                    }

                    if(wfUser != null){
                        if(tvLockPeriodWaiting != null){
                            tvLockPeriodWaiting.setText(StringUtil.getFormattedTimeByMillseconds(wfUser.getLock_period()));
                        }

                        lock_period = wfUser.getLock_period();
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
    public void onResume() {
        super.onResume();

        tvLockPeriodHint.setText(StringUtil.getFormattedTimeByMillseconds(MineProfile.getInstance().getLockPeriodParty()));
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

    private boolean isWifiServer = false;

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

                isWifiServer = false;

                LPAWifiManager.getInstance(getApplicationContext()).enableWifi();

                SocketSessionManager.init(getApplicationContext());

                startBarcodeScanner();
                WifiUtils.getInstance().setmWifiConnected(this);

                showWaitingPopup();
                break;
            case R.id.btn_start_party_server:

                isWifiServer = true;

                LPAWifiManager.getInstance(getApplicationContext()).enableWifi();

                SocketSessionManager.init(getApplicationContext());

                WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);
                WifiManager.MulticastLock lock = manager.createMulticastLock("lpa");

                lock.acquire();

                LPAWifiManager.getInstance(getApplicationContext()).startWifiAp(new WifiUtils.OnWifiConnected(){
                    @Override
                    public void onConnected() {

                        onWifiApConnected();

                    }
                });

                showJoinedUserPopup();

                break;
            case R.id.btn_cancel_joined_popup:

                dismissJoinedUserPopup();
                break;
            case R.id.tv_btn_start_party:
                if(LPApplication.getInstance().isSelfServer() && NotifyServerInfo.getInstance().getUsers().size() > 0){
                    MineProfile.getInstance().setLocked(true);

                    SocketSessionManager.getInstance().notifyLockStart();

                    showLockedView();
                }
                break;
            case R.id.btn_request_accept:
                showLockedView();
                break;
            case R.id.btn_request_refuse:
                dismissLockRequestDialog();
                
                if(clientThread != null){
                    clientThread.stop();
                }
                
                break;
            case R.id.btn_cancel_waiting_popup:
                if(clientThread != null){
                    clientThread.stopSocket();
                }
                dismissWaitingPopup();
                WifiUtils.dismissWifiReceiver();
                break;
            case R.id.btn_giveup_accept:
                dismissGiveupRequestDialog();
                dismissLockedView();
                break;
            case R.id.btn_giveup_cancel:
                dismissGiveupRequestDialog();
                break;
            case R.id.ll_cancel_lock:
                Bundle bundle = new Bundle();
                bundle.putString(ShareUtil.SHARE_CONTENT,"test");
                bundle.putInt(ShareUtil.SHARE_TYPE,1);

                Intent intentShare = new Intent(this,WXEntryActivity.class);
                startActivity(intentShare);

                break;
        }
    }

    IoAcceptor clientAcceptor;
    InetSocketAddress clientLocalInetAddress;

    private void onWifiApConnected() {
        try {
            if(isWifiServer){
                LPApplication.getInstance().setSelfServer(true);
                clientThread = new LPAUdpClientThread(getApplicationContext());
                clientThread.start();
            }else{

                if(clientAcceptor == null){
                    clientAcceptor = new NioSocketAcceptor();
                    clientAcceptor.getFilterChain().addLast("logger",new LoggingFilter());
                    clientAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
                    clientAcceptor.setHandler(new ClientServerHandler(LPAPartyCenter.this));
                    clientLocalInetAddress = new InetSocketAddress(Constants.UDP_CLIENT);
                    clientAcceptor.setDefaultLocalAddress(clientLocalInetAddress);

                }

                clientAcceptor.bind();

                serNotifyThread = new LPAServerNotifyThread(getApplicationContext());
                serNotifyThread.start();
            }


        } catch (Exception e) {
            e.printStackTrace();
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

    private void dismissJoinedUserPopup(){

        LPAWifiManager.getInstance(getApplicationContext()).stopWifiAP();

        if(mJoinedUserPopup != null && mJoinedUserPopup.isShowing()){
            mJoinedUserPopup.dismiss();

            try{
                if(serNotifyThread != null){

                    Constants.isBoradcastNeeded = false;

                    serNotifyThread = null;
                }

                if(clientThread != null){
                    clientThread.stopSocket();

                    clientThread = null;
                }

                NotifyServerInfo.getInstance().getUsers().clear();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private PopupWindow mWaitingToStartPopup;
    View waitStarting;

    private void dismissWaitingPopup(){
        if(mWaitingToStartPopup != null && mWaitingToStartPopup.isShowing()){
            mWaitingToStartPopup.dismiss();
            mWaitingToStartPopup = null;
        }

        if(clientAcceptor != null){
            try{
                clientAcceptor.unbind(clientLocalInetAddress);
                clientAcceptor.getFilterChain().clear();
                clientAcceptor.dispose();
                clientAcceptor = null;

                if(Constants.DEBUG){
                    Log.e("TAG","unbind acceptor");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(serNotifyThread != null){
            serNotifyThread.closeThread();
            serNotifyThread = null;
        }

        LPAWifiManager.getInstance(getApplicationContext()).disableWifi();

    }

    private TextView tvLockPeriodWaiting;

    private void showWaitingPopup(){

        if(mWaitingToStartPopup == null){
            mWaitingToStartPopup = new PopupWindow(getApplicationContext());
            mWaitingToStartPopup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            waitStarting = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_user_waiting_start,null);
            tvLockPeriodWaiting = (TextView) waitStarting.findViewById(R.id.tv_lock_time_waiting);
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

    public LPAServerNotifyThread serNotifyThread;

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

    private void refreshUserStatus(IncomeResult wu){

        for(WifiUser wuser : wu.getUsers()){
            String udid = wuser.getUdid();
            for(WifiUser user : NotifyServerInfo.getInstance().getUsers()){
                if(user.getUdid().equals(udid)){
                    user.setAccept(1);
                }
            }
        }

        adapterUsers.notifyDataSetChanged();
    }

    private View view_giveup_request;
    private View view_cancel_lock;
    private TextView tvDeviceName;

    private void showLockedView() {
        View lock_view = View.inflate(this, R.layout.layout_locked_view, null);
        view_giveup_request = lock_view.findViewById(R.id.include_dialog_giveup_confirm);
        view_cancel_lock = lock_view.findViewById(R.id.ll_cancel_lock);
        view_giveup_request = lock_view.findViewById(R.id.include_dialog_giveup_confirm);
        tvDeviceName = (TextView) lock_view.findViewById(R.id.tv_device_name);
        lpa = LPAKeyGuardView.getInstance(this);
        tvDeviceName.setText(Build.MODEL);
        lpa.setLockView(lock_view);
        lpa.setLockPeriod(lock_period);
        lpa.lock();

        view_lock_control = lock_view.findViewById(R.id.fl_lock_area);
        view_lock_control.setOnClickListener(this);
        view_cancel_lock.setOnClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(clientThread != null){
            clientThread.stopSocket();
        }

        dismissJoinedUserPopup();

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

        Toast.makeText(this,"Wifi connected",Toast.LENGTH_SHORT).show();

        onWifiApConnected();
    }


}