package com.andconsd.ui.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andconsd.framework.actionbarsherlock.view.MenuItem;
import com.andconsd.R;
import com.andconsd.framework.bitmap.ImageLoaderHelper;
import com.andconsd.framework.json.JSONParser;
import com.andconsd.framework.net.response.BaseResult;
import com.andconsd.model.Picture;
import com.andconsd.model.PictureList;
import com.andconsd.framework.share.ShareUtil;
import com.andconsd.framework.statictis.ClickStatictis;
import com.andconsd.framework.utils.AppUtil;
import com.andconsd.framework.utils.Constants;
import com.andconsd.framework.utils.NetUtil;
import com.andconsd.framework.utils.NetUtil.IRequestListener;
import com.andconsd.framework.pull.PullToRefreshBase;
import com.andconsd.framework.pull.PullToRefreshListView;
import com.andconsd.ui.widget.picview.WaterFallAdapter;
import com.andconsd.ui.widget.photoview.PhotoView;
import com.andconsd.ui.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobstat.StatService;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.BaseResp.ErrCode;

public class PicFlowListActivity extends BaseActivity implements
        IRequestListener, OnClickListener, AbsListView.OnScrollListener, OnViewTapListener,
        AdapterView.OnItemClickListener, IWXAPIEventHandler, Response, AdViewListener {
    AdView baiduAD;

    private static final int DISMISS_DIALOG = 1;

    public static int THEME = R.style.Theme_Sherlock;

    private View footView;
    private View loading_footview;
    PullToRefreshListView wfv;

    private RelativeLayout fl_view;
    private PhotoView ivFullScreen;

    private RelativeLayout rl_Sendto;
    private View pbLoading;

    private View controlTIps;
    private TextView tvTitle;

    WaterFallAdapter wfa;

    SlidingMenu menu;

    private static int PIC_TYPE = 1;

    ArrayList<Picture> picList = new ArrayList<Picture>();
    private int page = 0;

    private boolean hasMore = true;
    private boolean isLoading = false;

    private Handler mHandler = new Handler() {

        /*
         * (non-Javadoc)
         *
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DISMISS_DIALOG:

                    wfv.onRefreshComplete();

//				wfv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

                    wfa.setDataList(picList);

                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    break;

                default:
                    break;
            }
        }

    };

    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(THEME);

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.pic_flow_list_view);

        try {
            int type = getIntent().getExtras().getInt(Constants.INTENT_PICTYPE);
            if (type == 0) {
                type = Constants.TAG_BEAUTY;
            }

            PIC_TYPE = type;
        } catch (Exception e) {
            e.printStackTrace();
        }

        fl_view = (RelativeLayout) findViewById(R.id.fl_view);
        ivFullScreen = (PhotoView) findViewById(R.id.ivFullScreen);
        ivFullScreen.setOnViewTapListener(this);

        rl_Sendto = (RelativeLayout) findViewById(R.id.rlSendto);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnMore).setOnClickListener(this);
        findViewById(R.id.btn_share_sina).setOnClickListener(this);
        findViewById(R.id.btn_share_weixin_friend).setOnClickListener(this);
        findViewById(R.id.btn_share_weixin_zone).setOnClickListener(this);
        findViewById(R.id.btn_save_to_local).setOnClickListener(this);
        findViewById(R.id.btn_set_wallpaper).setOnClickListener(this);

        pbLoading = findViewById(R.id.pb_loading);

        initPopupController();

        baiduAD = (AdView) findViewById(R.id.baiduad);

        baiduAD.setListener(this);

        wfv = (PullToRefreshListView) findViewById(R.id.waterfallview);

        wfv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getPicListData();
            }
        });

        initSlidingMenu();

        getPicListData();

        wfv.setOnScrollListener(this);

        wfa = new WaterFallAdapter(getApplicationContext(), picList);
        wfv.setAdapter(wfa);

        wfv.setOnItemClickListener(this);

        showLoadingDialog();
    }

    private void showLoadingDialog() {

        if (loadingDialog == null) {
            View loading = View.inflate(this, R.layout.pb_loading, null);
            TextView tvHint = (TextView) loading
                    .findViewById(R.id.tv_loading_hint);
            tvHint.setText(R.string.loding_tip_waiting);
            loadingDialog = new AlertDialog.Builder(this).setView(loading)
                    .create();
        }

        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    private void initPopupController() {

        controlTIps = getLayoutInflater().inflate(R.layout.control_pic_view,
                null);

        tvTitle = (TextView) controlTIps.findViewById(R.id.tvIndex);

        controlTIps.findViewById(R.id.progress_circular).setVisibility(
                View.INVISIBLE);

        getSupportActionBar().setCustomView(controlTIps);

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getPicListData() {

        isLoading = true;
        picList = new ArrayList<Picture>();
        switch (PIC_TYPE) {
            case Constants.TAG_BEAUTY:
                tvTitle.setText(R.string.menu_beauty);
                NetUtil.getInstance().requestForPicList(Constants.SERVER_BEAUTY,
                        this);
                break;
            case Constants.TAG_RELAX:
                tvTitle.setText(R.string.menu_relax);
                NetUtil.getInstance().requestForRelaxList(Constants.SERVER_RELAX,
                        this);
                break;
            case Constants.TAG_GAME:
                tvTitle.setText(R.string.menu_game);
                NetUtil.getInstance().requestForGameList(Constants.SERVER_GAME,
                        this);
                break;
            case Constants.TAG_CAR:
                tvTitle.setText(R.string.menu_car);
                NetUtil.getInstance().requestForCarList(Constants.SERVER_CAR, this);
                break;
            case Constants.TAG_FAMOUS:
                tvTitle.setText(R.string.menu_famous);
                NetUtil.getInstance().requestForFamousList(Constants.SERVER_FAMOUS,
                        this);
                break;
            case Constants.TAG_BEAUTY_BAIDU:
                tvTitle.setText(R.string.menu_beauty_baidu);
                run(getBaiduBeautyUrl());
                break;
            default:
                break;
        }

    }

    String getBaiduBeautyUrl() {
        String baidu_beauty = "http://pic.baidu.com/data/imgs?col=%E7%BE%8E%E5%A5%B3&tag=%E5%85%A8%E9%83%A8&sort=0&tag3=&pn=";
        String append_url = "&rn=60&p=channel&from=1";
        String res = baidu_beauty + (60 * page) + append_url;

        return res;
    }

    void run(final String url) {

        new Thread() {

            @Override
            public void run() {

                try {

                    URL getUrl = new URL("http://127.0.0.1:6179/sendintent?callback=jsonp4&intent=http%3A%2F%2Fdl.m.duoku.com%2FGameSearch18_67744_60.apk%23Intent%3Baction%3D%3BlaunchFlags%3D%3Bcomponent%3D%3Bend");
                    // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
                    // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) getUrl
                            .openConnection();
                    // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
                    // 服务器
                    connection.setRequestProperty("Accept-Charset", "utf-8");
                    connection.setRequestProperty("contentType", "GBK");
                    connection.setRequestMethod("GET");
                    connection.connect();
                    // 取得输入流，并使用Reader读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));

                    StringBuffer lines = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.append(line);
                    }
                    reader.close();
                    // 断开连接
                    connection.disconnect();

                    isLoading = false;
                    PictureList pl = JSONParser.parseBaiduBeautyList(page, lines.toString());

                    initPicList(pl);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        ;
    }

    private void initSlidingMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int offset = (dm.widthPixels / 3) * 2;
        menu.setBehindOffset(offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.side_menu);

        findViewById(R.id.menu_beauty).setOnClickListener(menuOnClickListener);
        findViewById(R.id.menu_relax).setOnClickListener(menuOnClickListener);
        findViewById(R.id.menu_game).setOnClickListener(menuOnClickListener);
        findViewById(R.id.menu_car).setOnClickListener(menuOnClickListener);
        findViewById(R.id.menu_famous).setOnClickListener(menuOnClickListener);
        findViewById(R.id.menu_topics).setOnClickListener(this);
        findViewById(R.id.menu_promotion).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case Constants.OPTION_MENU_SETTING:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestSuccess(BaseResult responseData) {

        isLoading = false;
        PictureList pl = (PictureList) responseData;

        initPicList(pl);
    }

    public void initPicList(PictureList pl) {
        if (pl.getPics().size() < 20) {
            hasMore = false;
            footView = View.inflate(getApplicationContext(),
                    R.layout.footview_nomoredata, null);
        } else {
            hasMore = true;
        }

        picList.addAll(pl.getPics());

        try {
            page = Integer.parseInt(pl.getPage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        wfv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                page = 1;
                getPicListData();
            }
        });

        Message msg = new Message();
        msg.what = DISMISS_DIALOG;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode,
                               String msg) {

        isLoading = false;

        Toast.makeText(getApplicationContext(),
                getString(R.string.network_error_tip), Toast.LENGTH_SHORT)
                .show();

        wfa.setDataList(picList);

        Message msg_dismiss = new Message();
        msg_dismiss.what = DISMISS_DIALOG;
        mHandler.sendMessage(msg_dismiss);
    }

    private IWXAPI wxApi;

    AlertDialog dialogWXInstall;

    private void regToWX() {
        wxApi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_ID, true);
        if (!wxApi.isWXAppInstalled()) {
            if (dialogWXInstall == null) {
                dialogWXInstall = new AlertDialog.Builder(this)
                        .setMessage(R.string.weixin_not_installed)
                        .setTitle(R.string.tip_warning)
                        .setPositiveButton(R.string.btnConfirm,
                                new AlertDialog.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        AppUtil.browseWithDefaultBrowser(
                                                getApplicationContext(),
                                                "http://weixin.qq.com");
                                    }
                                }).create();
            }

            dialogWXInstall.show();
        } else {
            wxApi.registerApp(Constants.WEIXIN_ID);
            wxApi.handleIntent(getIntent(), this);
        }

    }

    private WeiboAuth mWeiboAuth;
    private IWeiboShareAPI mWeiboShareAPI = null;

    private void regToWB() {
        // Sina Weibo
        mWeiboAuth = new WeiboAuth(this, Constants.WEIBO_KEY,
                Constants.REDIRECT_URL, Constants.SCOPE);

        if (mWeiboShareAPI != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        WeiboShareSDK.createWeiboAPI(this, Constants.WEIBO_KEY)
                .handleWeiboResponse(getIntent(), this);

        if (mSsoHandler == null) {
            mSsoHandler = new SsoHandler(this, mWeiboAuth);
        }
    }

    private SsoHandler mSsoHandler;

    OnClickListener menuOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            page = 1;
            showLoadingDialog();

            switch (v.getId()) {
                case R.id.menu_beauty:
                    ClickStatictis.addMenuItemClick(getApplicationContext(),
                            getString(R.string.menu_beauty));
                    PIC_TYPE = Constants.TAG_BEAUTY;
                    break;
                case R.id.menu_relax:
                    ClickStatictis.addMenuItemClick(getApplicationContext(),
                            getString(R.string.menu_relax));
                    PIC_TYPE = Constants.TAG_RELAX;
                    break;
                case R.id.menu_game:
                    ClickStatictis.addMenuItemClick(getApplicationContext(),
                            getString(R.string.menu_game));
                    PIC_TYPE = Constants.TAG_GAME;
                    break;
                case R.id.menu_car:
                    ClickStatictis.addMenuItemClick(getApplicationContext(),
                            getString(R.string.menu_car));
                    PIC_TYPE = Constants.TAG_CAR;
                    break;
                case R.id.menu_famous:
                    ClickStatictis.addMenuItemClick(getApplicationContext(),
                            getString(R.string.menu_famous));
                    PIC_TYPE = Constants.TAG_FAMOUS;
                    break;
                case R.id.menu_topics:
                    PIC_TYPE = Constants.TAG_BEAUTY_BAIDU;
                    break;
                default:
                    break;
            }

            if (menu.isShown())
                menu.toggle();

            getPicListData();
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivFullScreen:
                break;
            case R.id.btnCancel:
                hideSendToView();
                break;
            case R.id.btnMore:
                ClickStatictis.addSendToMore(getApplicationContext());
                callSystemShare();
                break;
            case R.id.btn_share_weixin_friend:
                ClickStatictis.addSendToSinaClick(getApplicationContext());
                regToWX();
                ShareUtil.getInstance(getApplicationContext()).shareImageToWX(
                        wxApi, ivFullScreen.getDrawingCache(), true);
                break;
            case R.id.btn_share_weixin_zone:
                ClickStatictis.addSendToWeixin(getApplicationContext());
                regToWX();
                ShareUtil.getInstance(getApplicationContext()).shareImageToWX(
                        wxApi, ivFullScreen.getDrawingCache(), false);
                break;
            case R.id.btn_share_sina:
                ClickStatictis.addSendToWXZone(getApplicationContext());
                regToWB();
                ShareUtil.getInstance(this).shareSinaWeibo(
                        getString(R.string.share_tip),
                        ivFullScreen.getDrawingCache(), mSsoHandler);
                break;
            case R.id.menu_promotion:
                ClickStatictis.addMenuItemClick(getApplicationContext(),
                        getString(R.string.menu_promotion));
                startActivity(new Intent(this, PromotionActivity.class));
                break;
            case R.id.btn_set_wallpaper:
                WallpaperManager wallpaperManager = WallpaperManager
                        .getInstance(this);
                try {
                    wallpaperManager.setBitmap(ivFullScreen.getDrawingCache());
                    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();

                    if (rl_Sendto.getVisibility() == View.VISIBLE) {
                        hideSendToView();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_save_to_local:
                ContentResolver cr = getContentResolver();
                String url = MediaStore.Images.Media.insertImage(
                        cr,
                        ivFullScreen.getDrawingCache(),
                        "IMG_"
                                + Constants.date_formater.format(Calendar
                                .getInstance().getTime()), "");
                if (null != url) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_save_succ), Toast.LENGTH_SHORT)
                            .show();
                    if (rl_Sendto.getVisibility() == View.VISIBLE) {
                        hideSendToView();
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void hideSendToView() {
        Animation animOut = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.sendto_transout);
        animOut.setFillAfter(false);
        rl_Sendto.startAnimation(animOut);
        rl_Sendto.setVisibility(View.GONE);
    }

    /**
     * 获取分页数据
     */
    private void requestMoreData() {

        if (hasMore && !isLoading) {
            isLoading = true;
            loading_footview = View.inflate(getApplicationContext(),
                    R.layout.footview_loading, null);
//			wfv.addFooterView(loading_footview);
            switch (PIC_TYPE) {
                case Constants.TAG_BEAUTY:
                    ClickStatictis.addGetMoreStatictis(getApplicationContext(),
                            getString(R.string.menu_beauty));
                    NetUtil.getInstance().requestForPicListOfPage(
                            Constants.SERVER_BEAUTY, page + 1, this);
                    break;
                case Constants.TAG_RELAX:
                    ClickStatictis.addGetMoreStatictis(getApplicationContext(),
                            getString(R.string.menu_relax));
                    NetUtil.getInstance().requestForMoreRelaxList(
                            Constants.SERVER_RELAX, page + 1, this);
                    break;
                case Constants.TAG_GAME:
                    ClickStatictis.addGetMoreStatictis(getApplicationContext(),
                            getString(R.string.menu_game));
                    NetUtil.getInstance().requestForMoreGameList(
                            Constants.SERVER_GAME, page + 1, this);
                    break;
                case Constants.TAG_CAR:
                    ClickStatictis.addGetMoreStatictis(getApplicationContext(),
                            getString(R.string.menu_car));
                    NetUtil.getInstance().requestForMoreCarList(
                            Constants.SERVER_CAR, page + 1, this);
                    break;
                case Constants.TAG_FAMOUS:
                    ClickStatictis.addGetMoreStatictis(getApplicationContext(),
                            getString(R.string.menu_famous));
                    NetUtil.getInstance().requestForMoreFamousList(
                            Constants.SERVER_FAMOUS, page + 1, this);
                    break;
                case Constants.TAG_BEAUTY_BAIDU:
                    run(getBaiduBeautyUrl());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (fl_view != null && fl_view.getVisibility() == View.VISIBLE) {
            hideFullScreenImageView();
        }
    }

    private void hideFullScreenImageView() {
        fl_view.setVisibility(View.GONE);
        showHideController();
        ivFullScreen.setDrawingCacheEnabled(false);
        ivFullScreen.setDrawingCacheEnabled(true);
    }

    public void showHideController() {

        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }

    public void sendto(View view) {
        ClickStatictis.addSendToButtonOnClick(getApplicationContext());
        Animation animIn = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.sendto_transin);
        animIn.setFillAfter(false);
        rl_Sendto.startAnimation(animIn);
        rl_Sendto.setVisibility(View.VISIBLE);
    }

    private String mShareImagePath;

//	@Override
//	public void onItemClick(PLA_AdapterView<?> parent, View view, int position,
//			long id) {
//
//		try {
//			Picture picture = picList.get(position - 1);
//
//			if (null == picture)
//				return;
//
//			ClickStatictis.addPicListOnItemClick(getApplicationContext(),
//					picture.getCategory());
//
//			String url = picture.getUrl();
//
//			mShareImagePath = url;
//
//			if (url == null || url.equals(""))
//				return;
//
//			showHideController();
//			fl_view.setVisibility(View.VISIBLE);
//			ivFullScreen.setDrawingCacheEnabled(true);
//			ImageLoaderHelper.displayImageWithoutCache(url, ivFullScreen);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

    /**
     * Creates a sharing {@link Intent}.
     *
     * @return The sharing intent.
     */
    private void callSystemShare() {

        pbLoading.setVisibility(View.VISIBLE);

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                try {
                    shareIntent.putExtra(Intent.EXTRA_STREAM,
                            saveMyBitmap(ivFullScreen.getDrawingCache()));
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.share_tip));

                    startActivity(shareIntent);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "failed",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                pbLoading.setVisibility(View.GONE);
            }
        });

    }

    public Uri saveMyBitmap(Bitmap mBitmap) throws IOException {

        File folder = new File(Constants.IMAGE_CACHE);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File f = new File(Constants.IMAGE_CACHE + "/temp.png");
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            Uri uri = Uri.fromFile(f);

            if (mBitmap != null && !mBitmap.isRecycled())
                mBitmap.recycle();
            return uri;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
        return null;
    }

    @Override
    public void onReq(BaseReq arg0) {

    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.errCode == ErrCode.ERR_OK) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.share_success), Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.share_fail), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(BaseResponse res) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Sina Weibo
        if (null != mSsoHandler) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (rl_Sendto.getVisibility() == View.VISIBLE) {
                    hideSendToView();
                    return true;
                }

                if (fl_view.getVisibility() == View.VISIBLE) {
                    hideFullScreenImageView();
                    return true;
                }
                break;

            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAdClick(JSONObject arg0) {
        StatService.onEvent(this, Constants.EVENT_BAIDUAD_CLICK, "图片列表页面-广告点击");
    }

    @Override
    public void onAdFailed(String arg0) {
        StatService
                .onEvent(this, Constants.EVENT_BAIDUAD_FAIL, "图片列表页面-广告加载失败");
    }

    @Override
    public void onAdReady(AdView arg0) {

    }

    @Override
    public void onAdShow(JSONObject arg0) {
        StatService.onEvent(this, Constants.EVENT_BAIDUAD_SHOW, "图片列表页面-广告展现");
    }

    @Override
    public void onAdSwitch() {
    }

    @Override
    public void onVideoClickAd() {
    }

    @Override
    public void onVideoClickClose() {
    }

    @Override
    public void onVideoClickReplay() {
    }

    @Override
    public void onVideoError() {
    }

    @Override
    public void onVideoFinish() {
    }

    @Override
    public void onVideoStart() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Picture picture = picList.get(position);

            if (null == picture)
                return;

            ClickStatictis.addPicListOnItemClick(getApplicationContext(),
                    picture.getCategory());

            String url = picture.getUrl();

            mShareImagePath = url;

            if (url == null || url.equals(""))
                return;

            showHideController();
            fl_view.setVisibility(View.VISIBLE);
            ivFullScreen.setDrawingCacheEnabled(true);
            ImageLoaderHelper.displayImageWithoutCache(url, ivFullScreen);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {

            if (view.getLastVisiblePosition() + 5 > wfa.getCount()) {
                requestMoreData();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
