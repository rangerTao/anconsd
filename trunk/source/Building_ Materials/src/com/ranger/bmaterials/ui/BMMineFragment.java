package com.ranger.bmaterials.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiscCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.ranger.bmaterials.adapter.MyGamesExpandAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.RecommendAppItemInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.DynamicDataResult;
import com.ranger.bmaterials.netresponse.UserLoginResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.UserStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.Logger;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.PhoneHelper;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UpdateHelper;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.slideexpand.MyExpandListView;

public class BMMineFragment extends Fragment implements
        android.view.View.OnClickListener, IRequestListener,
        OnItemClickListener {

    private static final String TAG = "BMMineFragment";
    private WindowManager mWindowManager;
    private int device_dp;

    boolean isLogin;

    private PopupWindow mPop;
    private View layout_menu;
    private TextView loginMenuBtn;

    private TextView tv_item_exit_pop_menu_home_activity;
    private TextView tv_item_settings_pop_menu_home_activity;
    private TextView tv_item_share_pop_menu_home_activity;
    private LinearLayout ll_checkupdate_pop_item_home;
    private View ll_logout_pop_item_home;
    private TextView tv_item_feedback_pop_menu_home_activity;

    private ImageView ivUserHead;

    private TextView popText;

    private RefreshUserHeadReceiver refreshUserHeadReceiver;

    // Add below code for fast register.
    private SendReceiver smsReceiver;
    // private SMSDeliveryReceiver deliveryReceiver;
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    String DELIVERY_SMS_ACTION = "SENT_SMS_ACTION";
    private String message;
    private CustomProgressDialog progressDialog = null;
    private boolean hasProgressDlg = false;
    private boolean flag = false;
    private Dialog alertDialog;
    private String nickname;

    private List<RecommendAppItemInfo> recomdAppList;

    /**
     * 是否应该重试一键注册请求，如果请求已成功或者请求被用户取消，则不应该再重试，初始状态为true
     */
    boolean ignoreRegResult;
    /**
     * 一键注册请求是否被cancel，用户在按下cancel键时表示请求被cancel
     */
    private boolean requestHasCancel;

    private int requestId;
    ArrayList<Integer> fastRegReqList = new ArrayList<Integer>();

    protected View root;
    private View btn_login;
    private View ucView;

    protected BroadcastReceiver mDynamicDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MineProfile.MINE_DYNAMIC_DATA_NOTIFICATION)) {
                String gamenum = intent.getStringExtra("gamenum");
                String totalmsgnum = intent.getStringExtra("totalmsgnum");
                String unreadmsgnum = intent.getStringExtra("unreadmsgnum");
                String collectnum = intent.getStringExtra("collectnum");
                int coinnum = intent.getIntExtra("coinnum", 0);

                if (isLogin) {

                    MineProfile.getInstance().setGamenum(gamenum);
                    MineProfile.getInstance().setMessagenum(unreadmsgnum);
                    MineProfile.getInstance().setTotalmsgnum(totalmsgnum);
                    MineProfile.getInstance().setCollectnum(collectnum);
                    MineProfile.getInstance().setCoinnum(coinnum);

                    setDynamicData();
                }
            }
        }
    };

    protected BroadcastReceiver mRefreshDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MineProfile.MINE_DYNAMIC_DATA_REFRESH) && isLogin) {
                NetUtil.getInstance().requestDynamicData(
                        MineProfile.getInstance().getUserID(),
                        MineProfile.getInstance().getSessionID(),
                        BMMineFragment.this);
            }
        }
    };

    private void registerReceiver() {
        // IntentFilter intentFilter = new
        // IntentFilter(BroadcaseSender.ACTION_MANAGER_APPS_CHANGED);
        // popNumberReceiver = new PopNumberReceiver();
        // getActivity().registerReceiver(popNumberReceiver, intentFilter);

        // baidu pass 登录后刷新用户头像
        IntentFilter userLoginFilter = new IntentFilter(
                Constants.refresh_head_action);
        refreshUserHeadReceiver = new RefreshUserHeadReceiver();

        LocalBroadcastManager.getInstance(
                GameTingApplication.getAppInstance().getApplicationContext())
                .registerReceiver(refreshUserHeadReceiver, userLoginFilter);
    }

    private void unregisterReceiver() {

        if (refreshUserHeadReceiver != null) {
            LocalBroadcastManager.getInstance(
                    GameTingApplication.getAppInstance()
                            .getApplicationContext()
            ).unregisterReceiver(
                    refreshUserHeadReceiver);
        }
    }

    class RefreshUserHeadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.refresh_head_action)) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // refreshUserHead();
                        refreshHeadPhoto();
                    }
                });
            }

        }

    }

    View view_exit_pop;

    private void initExitPopView() {
        view_exit_pop = View.inflate(getActivity(),
                R.layout.pop_exit_home_activity, null);

        TextView tv_exit_pop_exit_home_activity = (TextView) view_exit_pop
                .findViewById(R.id.tv_exit_pop_exit_home_activity);
        tv_exit_pop_exit_home_activity.setOnClickListener(this);

        TextView tv_cancel_pop_exit_home_activity = (TextView) view_exit_pop
                .findViewById(R.id.tv_cancel_pop_exit_home_activity);
        tv_cancel_pop_exit_home_activity.setOnClickListener(this);

    }

    PopupWindow mExitPop;

    private View ll_iv_menu_home_activity;

    @SuppressWarnings("unused")
    private void initExitPopWindow() {
        if (mExitPop == null) {
            mExitPop = new PopupWindow(view_exit_pop,
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mExitPop.setBackgroundDrawable(new BitmapDrawable(BitmapFactory
                    .decodeResource(getResources(),
                            R.drawable.transparent_drawable)));
            mExitPop.setOutsideTouchable(true);
        }
    }

    private MyGamesExpandAdapter adapter;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (root != null) {
            ViewParent parent = this.root.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.root);
            }
            return root;
        }

        root = inflater.inflate(R.layout.mine_activity_login, null);

        ucView = root.findViewById(R.id.login_view);

        mWindowManager = (WindowManager) getActivity().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        device_dp = dm.densityDpi;

        root.findViewById(R.id.bm_rl_about).setOnClickListener(this);
        root.findViewById(R.id.bm_rl_change_pass).setOnClickListener(this);
        root.findViewById(R.id.bm_rl_feedback).setOnClickListener(this);
        root.findViewById(R.id.bm_rl_my_collect).setOnClickListener(this);
        root.findViewById(R.id.bm_rl_userinfo).setOnClickListener(this);
        root.findViewById(R.id.bm_rl_update).setOnClickListener(this);

        flag = getActivity().getIntent().getBooleanExtra("flag", false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MineProfile.MINE_DYNAMIC_DATA_NOTIFICATION);
        filter.addAction(BroadcaseSender.ACTION_USER_LOGIN);
        filter.addAction(BroadcaseSender.ACTION_USER_LOGOUT);
        getActivity().registerReceiver(mDynamicDataReceiver, filter);

        IntentFilter refreshFilter = new IntentFilter(
                MineProfile.MINE_DYNAMIC_DATA_REFRESH);
        getActivity().registerReceiver(mRefreshDataReceiver, refreshFilter);

        initExitPopView();

        ScrollView scrollview = (ScrollView) root
                .findViewById(R.id.scroll_view_pane);

        if (Build.VERSION.SDK_INT >= 9) {
            scrollview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // root.findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
        // changeData();
        // regPackagechange();
        // registerReceiver();

        return root;
    }

    public AtomicBoolean isRegisterPackage = new AtomicBoolean();

    MyExpandListView listView;
    private ReentrantLock rl = new ReentrantLock();

    @Override
    public void onStart() {
        super.onStart();
        smsReceiver = new SendReceiver();
        // deliveryReceiver = new SMSDeliveryReceiver();
        getActivity().registerReceiver(smsReceiver,
                new IntentFilter(SENT_SMS_ACTION));
        // getActivity().registerReceiver(deliveryReceiver,
        // new IntentFilter(DELIVERY_SMS_ACTION));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();

        // for test
        // BaiduPushTest.pushGame();
        // BaiduPushTest.pushMsgNum();
        // BaiduPushTest.pushStartPage();
        // BaiduPushTest.pushGameList();
    }

    @Override
    public void onStop() {
        super.onStop();
        MineProfile.getInstance().Save();
        getActivity().unregisterReceiver(smsReceiver);
        // getActivity().unregisterReceiver(deliveryReceiver);
    }

    public void refreshView() {
        isLogin = isLogin();

        refreshHeadPhoto();
        if (isLogin) {
            if(btn_login != null)
                btn_login.setVisibility(View.GONE);
            if(ucView != null)
                ucView.setVisibility(View.VISIBLE);

            String nickName = MineProfile.getInstance().getNickName();
            if (TextUtils.isEmpty(nickName)) {
                ((TextView) root.findViewById(R.id.label_user_nickname))
                        .setText(MineProfile.getInstance().getUserName());
            } else {
                ((TextView) root.findViewById(R.id.label_user_nickname))
                        .setText(nickName);
            }

            setDynamicData();

            NetUtil.getInstance().requestDynamicData(
                    MineProfile.getInstance().getUserID(),
                    MineProfile.getInstance().getSessionID(), this);
            if (!(MineProfile.getInstance().getNickName().length() > 0)
                    && MineProfile.getInstance().isNewUser()) {
                checkNickName();
                MineProfile.getInstance().setIsNewUser(false);
            }
            // refreshUserHead();
        } else {
//			if (btn_login != null)
//				btn_login.setVisibility(View.VISIBLE);
//			if (ucView != null)
//				ucView.setVisibility(View.GONE);
        }
    }

    public void refreshHeadPhoto() {
        if (ivUserHead == null) {
            ivUserHead = (ImageView) root.findViewById(R.id.img_logo);
        }
        if (isLogin()) {
            String filepath = Constants.IMAGE_PATH + Constants.PHOTO_LOCAL_FILE;
            Bitmap bitmap = ImageLoaderHelper.decodeBitmap(filepath);
            if (bitmap != null) {
                ivUserHead.setImageBitmap(ImageLoaderHelper
                        .getRoundedBitmap(bitmap));
            } else {
                // ivUserHead.setImageResource(R.drawable.mine_nickname_bk);
                ivUserHead.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.mine_nickname_bk));
            }
        } else {
            // ivUserHead.setImageResource(R.drawable.mine_nickname_bk);
            ivUserHead.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.mine_nickname_bk));
        }
    }

    // pass 登录后，由于是异步操作。需要异步处理头像。
    private void refreshUserHead() {
        if (ivUserHead == null)
            ivUserHead = (ImageView) root.findViewById(R.id.img_logo);
        String user_head = MineProfile.getInstance().getStrUserHead();
        final String imageUri = user_head;
        if (user_head != null && !user_head.equals("")
                && !user_head.equals("null")) {
            // ImageLoaderHelper.displayImage(user_head, ivUserHead,
            // ImageLoaderHelper.getCustomRoundeOption(true,
            // R.drawable.mine_nickname_bk));
            ImageLoader.getInstance().loadImage(user_head,
                    new ImageLoadingListener() {
                        boolean cacheFound;

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            List<String> memCache = MemoryCacheUtils
                                    .findCacheKeysForImageUri(imageUri,
                                            ImageLoader.getInstance()
                                                    .getMemoryCache()
                                    );
                            cacheFound = !memCache.isEmpty();
                            if (!cacheFound) {
                                File discCache = DiscCacheUtils.findInCache(
                                        imageUri, ImageLoader.getInstance()
                                                .getDiscCache()
                                );
                                if (discCache != null) {
                                    cacheFound = discCache.exists();
                                }
                            }
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {

                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1,
                                                      Bitmap bitmap) {
                            if (cacheFound) {
                                MemoryCacheUtils.removeFromCache(imageUri,
                                        ImageLoader.getInstance()
                                                .getMemoryCache()
                                );
                                DiscCacheUtils.removeFromCache(imageUri,
                                        ImageLoader.getInstance()
                                                .getDiscCache()
                                );
                                // ImageLoader.getInstance().loadImageSync(imageUri);
                                // ImageLoader.getInstance().displayImage(imageUri,ivUserHead);
                            }
                            ivUserHead.setImageBitmap(ImageLoaderHelper
                                    .getRoundedBitmap(bitmap));
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                        }
                    }
            );
        }
    }

    @Override
    public void onRequestSuccess(BaseResult responseData) {
        int requestTag = StringUtil.parseInt(responseData.getTag());

        if (requestTag == Constants.NET_TAG_FAST_PHONE_REGESTER) {
            // 如果请求成功，则停止重试
            if (requestHasCancel) {
                return; // 如果请求已经被取消，则直接返回，不处理结果
            } else {
                requestHasCancel = true;// 请求成功则设置状态为取消后续的请求
            }
            UserLoginResult result = (UserLoginResult) responseData;
            UserStatistics.addFastRegSuccessStatistics(getActivity());
            MineProfile.getInstance().setUserID(result.getUserid());
            MineProfile.getInstance().setSessionID(result.getSessionid());
            MineProfile.getInstance().setUserName(result.getUsername());

            MineProfile.getInstance().setNickName(result.getNickname());
            MineProfile.getInstance().setUserType(result.getRegistertype());
            MineProfile.getInstance().setIsLogin(true);
            MineProfile.getInstance().setIsNewUser(true);
            MineProfile.getInstance().setPhonenum(result.getPhonenum());

            MineProfile.getInstance().setGamenum(result.getGamenum());
            MineProfile.getInstance().setTotalmsgnum(result.getTotalmsgnum());
            MineProfile.getInstance().setMessagenum(result.getMessagenum());
            MineProfile.getInstance().setCollectnum(result.getCollectnum());

            MineProfile.getInstance().setCoinnum(0);
            if (result.getIsloginReq() == Constants.FASTREG_REQTYPE_REGISTER) {
                MineProfile.getInstance().addCoinnum(result.getCoinnum());
            }
            MineProfile.getInstance().addAccount(result.getUsername());

            if (MineProfile.getInstance().getNickName().length() > 0) {
                loginSucceed();
            } else {// 没有昵称
                disMissProgressDialog();
                refreshView();
            }
            return;
        }

        DynamicDataResult result = (DynamicDataResult) responseData;

        MineProfile.getInstance().setGamenum(result.gamenum);
        MineProfile.getInstance().setMessagenum(result.unreadmsgnum);
        MineProfile.getInstance().setTotalmsgnum(result.totalmsgnum);
        MineProfile.getInstance().setCollectnum(result.collectnum);
        MineProfile.getInstance().setCoinnum(result.coinnum);

        MineProfile.getInstance().broadcastEvent();
    }

    private void checkNickName() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View dialogView = factory.inflate(R.layout.mine_change_nickname, null);

        ((EditText) dialogView.findViewById(R.id.edit_change_nickname))
                .setText(MineProfile.getInstance().getNickName());
        CharSequence text = ((EditText) dialogView
                .findViewById(R.id.edit_change_nickname)).getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
        dialogView.findViewById(R.id.btn_change_nickname_cancel)
                .setOnClickListener(this);
        dialogView.findViewById(R.id.btn_change_nickname_commit)
                .setOnClickListener(this);
        alertDialog = new Dialog(getActivity(), R.style.dialog);

        DisplayMetrics dm = GameTingApplication.getAppInstance().getResources()
                .getDisplayMetrics();
        int width = dm.widthPixels - PhoneHelper.dip2px(getActivity(), 13) * 2;
        alertDialog.addContentView(dialogView, new ViewGroup.LayoutParams(
                width, LayoutParams.WRAP_CONTENT));
        alertDialog.setCancelable(true);
        disMissProgressDialog();
        alertDialog.show();
    }

    private void loginSucceed() {
        UserStatistics.addLoginNumStatistics(getActivity());
        /*
         * Intent intent = new Intent(getActivity(), DKGameHallActivity.class);
		 * 
		 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 * startActivity(intent);
		 */
        disMissProgressDialog();
        // MineProfile.getInstance().Print();//for debug
        refreshView();
    }

    private void disMissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
            hasProgressDlg = false;
        }
    }

    private void setDynamicData() {
        String msgNum = MineProfile.getInstance().getMessagenum();

        int unreadMsgNum = 0;
        unreadMsgNum = StringUtil.parseInt(msgNum);

    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode,
                               String msg) {

        if (requestTag == Constants.NET_TAG_GET_RECOMMEND_APP) {
            // Toast.makeText(getActivity(), "获取推荐app出错",
            // Toast.LENGTH_SHORT).show();
            disMissProgressDialog();
            return;
        }

        if (requestTag == Constants.NET_TAG_FAST_PHONE_REGESTER) {// 一键注册登陆
            if (requestHasCancel) {
                return;
            }
            /** 如果当前还在持续请求，则不处理本次请求的错误。所以这里只有最后一次尝试仍然出错时候才会处理 */
            if (errorCode == DcError.DC_NET_TIME_OUT
                    || errorCode == DcError.DC_NET_DATA_ERROR
                    || errorCode == DcError.DC_NET_GENER_ERROR) {
                CustomToast.showLoginRegistErrorToast(getActivity(), errorCode);
                enableFastRegBtn();
                cancelRequest();
            } else if (requestHasCancel && !ignoreRegResult) {
                // 手动注册
                jump2ManualReg();
                cancelRequest();
            }
        }
        if (ConnectManager.isNetworkConnected(getActivity())) {
            MineProfile.getInstance().setIsLogin(false);
        }
    }

    private boolean isLogin() {
        return MineProfile.getInstance().getIsLogin();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        getActivity().unregisterReceiver(mDynamicDataReceiver);
        getActivity().unregisterReceiver(mRefreshDataReceiver);
    }

    public final static int REQCODE_LOGIN = 1;

    @Override
    public void onClick(View v) {
        int viewID = v.getId();

        switch (viewID) {
            case R.id.bm_rl_change_pass:
                if (!isLogin) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), BMLoginActivity.class);
                    startActivity(intent);
                    break;
                }else{
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ChangePwdActivity.class);
                    startActivity(intent);
                    break;
                }
            case R.id.bm_rl_userinfo:
                break;
            case R.id.img_logo:
                if (isLogin()) {
                    break;
                }
                break;
            case R.id.bm_rl_my_collect: {
                if (!isLogin) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), BMLoginActivity.class);
                    startActivity(intent);
                    break;
                }else{
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),
                            BMMineCollectionActivity.class);
                    startActivity(intent);
                }

            }
            break;
            case R.id.tv_item_feedback_pop_menu_home_activity: {
                mPop.dismiss();
                ClickNumStatistics.addMenuFeedBackClickStatistis(getActivity());
                Intent intent = new Intent();
                intent.setClass(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.ll_checkupdate_pop_item_home:
                mPop.dismiss();

                ClickNumStatistics.addMenuCheckUpdateClickStatis(getActivity());
                UpdateHelper updateHelper = new UpdateHelper(getActivity(), false);
                updateHelper.checkGameTingUpdate(false);
                break;
            default:
                break;
        }
    }

    private void enableFastRegBtn() {
        root.findViewById(R.id.btn_register).setEnabled(true);
    }

    class SendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int resultCode = getResultCode();

            if (resultCode == Activity.RESULT_OK) {
                return;
            } else {
                // 短信发送失败
                cancelRequest();
                Toast.makeText(getActivity(), R.string.login_sms_send_failed,
                        Toast.LENGTH_SHORT).show();
                CustomToast.showLoginRegistErrorToast(context,
                        CustomToast.DC_Err_NEED_REGISTER_MANUALLY);

                Intent intentManual = new Intent(getActivity(),
                        BMRegisterActivity.class);
                startActivity(intentManual);
            }

        }

    }

    //
    // class SMSDeliveryReceiver extends BroadcastReceiver {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    //
    // int resultCode = getResultCode();
    //
    // if (resultCode == Activity.RESULT_OK && newMessageSend) {
    //
    // newMessageSend = false;
    //
    // new Handler().postDelayed(new Runnable() {
    // public void run() {
    // NetUtil.getInstance().requestFastPhoneRegister(message,
    // MineFragment.this);
    // }
    // }, 5000);// 5秒钟
    //
    // } else {
    // }
    //
    // }
    //
    // }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ClickNumStatistics.addSettingRecomdAppClickStatis(getActivity());
        RecommendAppItemInfo itemInfo = recomdAppList.get(position);
        Intent intent = new Intent();
        intent.setClass(getActivity(), WebviewActivity.class);
        intent.putExtra("title",
                getResources().getString(R.string.down_more_duoku_app));
        intent.putExtra("url", itemInfo.getRecommendUrl());
        startActivity(intent);
    }

    private Runnable fastRegRunable = new Runnable() {
        @Override
        public void run() {
            int RETRY_TIMES = 60 * 1000; // 60s
            int duration = 5000;
            int retry_counts = 0;
            int maxTimes = RETRY_TIMES / duration;
            while (retry_counts < maxTimes && !requestHasCancel) {
                requestId = NetUtil.getInstance().requestFastPhoneRegister(
                        message, BMMineFragment.this);
                fastRegReqList.add(requestId);
                Logger.w(TAG, "retry fast register:" + message);
                try {
                    // 最后一次重试完后不sleep，方便及时处理最后一次请求的response
                    if (retry_counts != maxTimes - 1) {
                        Thread.sleep(duration);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retry_counts++;
            }
            // 如果重试了一分钟依然没成功，则最后一次请求出错时应该跳到手动注册
            Logger.w(TAG, "retry" + retry_counts
                    + " times,abandon fast register.");
            // cancelRequest();
            mHandler.obtainMessage(MSG_CANCEL_TO_MANUAL).sendToTarget();
        }
    };

    protected void cancelRequest() {
        resetFastRegStatus("cancel");
        Logger.d(TAG, "cancel RequestList,size=" + fastRegReqList.size());
        for (int requestId : fastRegReqList) {
            Logger.d(TAG, "cancel requestId= " + requestId);
            NetUtil.getInstance().cancelRequestById(requestId);
        }
        fastRegReqList.clear();
        enableFastRegBtn();
        disMissProgressDialog();
    }

    /**
     * 重置一键注册时状态为初始状态
     */
    private void resetFastRegStatus(String action) {
        if (action.equals("init")) {
            requestHasCancel = false;
            ignoreRegResult = false;
        } else if (action.equals("cancel")) {
            requestHasCancel = true;
            ignoreRegResult = true;
        }
    }

    private void jump2ManualReg() {
        Intent intent = new Intent(getActivity(), BMRegisterActivity.class);
        intent.putExtra("flag", flag);
        getActivity().startActivity(intent);
        CustomToast.showLoginRegistErrorToast(getActivity(),
                CustomToast.DC_Err_NEED_REGISTER_MANUALLY);
    }

    private final static int MSG_CANCEL_TO_MANUAL = 1;
    private final static int MSG_REFRESH_PHOTO = 2;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CANCEL_TO_MANUAL:
                    if (!requestHasCancel) {
                        jump2ManualReg();
                    }
                    cancelRequest();
                    break;
                case MSG_REFRESH_PHOTO:
                    refreshHeadPhoto();
                    break;
                default:
                    break;
            }
        }

        ;
    };
}
