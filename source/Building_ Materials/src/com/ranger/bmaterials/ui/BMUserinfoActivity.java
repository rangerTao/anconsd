package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.SpinnerListAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.cropimg.CropImageActivity;
import com.ranger.bmaterials.cropimg.ModifyAvatarDialog;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.listener.onEditUserInfoDialogDismissListener;
import com.ranger.bmaterials.netresponse.BMCityResult;
import com.ranger.bmaterials.netresponse.BMUserInfoResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.CityListResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.DialogFactory;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.work.LoadingTask;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class BMUserinfoActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener, IRequestListener, OnCancelListener, TextWatcher {

    private static final int INPUT_ERROR_USERNAME = 0;
    private static final int INPUT_ERROR_NICKNAME = 1;
    private static final int INPUT_ERROR_PASSWORD = 2;
    private static final int INPUT_ERROR_PHONENUM = 3;
    private static final int INPUT_ERROR_VERIFYCODE = 4;
    private static final int INPUT_ERROR_USERNAME_CANNOT_BE_PHONENUM = 5;

    private CustomProgressDialog progressDialog;
    private int requestId;

    private Handler handler;
    private TimerTask timertask;
    private Timer timer;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bm_layout_user_info);

        findViewById(R.id.btn_back).setOnClickListener(this);

        if (DeviceUtil.isNetworkAvailable(this)) {
            requestId = NetUtil.getInstance().requestForUserinfo(this);
            initView();
        } else {
            Toast.makeText(this, R.string.network_error_hint, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public static final int USERINFO_EDIT_TYPE_NICK = 1;
    public static final int USERINFO_EDIT_TYPE_NAME = 1 << 1;
    public static final int USERINFO_EDIT_TYPE_SEX = 1 << 2;
    public static final int USERINFO_EDIT_TYPE_AREA = 1 << 3;
    public static final int USERINFO_EDIT_TYPE_SIGN = 1 << 4;

    private Dialog mDialog;

    private View viewProvince;
    private Spinner spProvince;
    private Button btnClose;

    private View loading;

    private final int DISMISS_LOADING = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DISMISS_LOADING:
                    loading.setVisibility(View.GONE);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public void showSelectProvince(View v) {

        loading = findViewById(R.id.progress_bar);

        loading.setVisibility(View.VISIBLE);

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences("cache", Context.MODE_PRIVATE);

                String cache_province = sp.getString("citys", "");

                if (!cache_province.equals("")) {

                    BMCityResult citys = JSONParser.parseCityList(cache_province);

                    doShowProvinceDialog(citys);
                } else {

                    NetUtil.getInstance().requestCityList(getApplicationContext(), new IRequestListener() {
                        @Override
                        public void onRequestSuccess(BaseResult responseData) {

                            doShowProvinceDialog((BMCityResult) responseData);

                        }

                        @Override
                        public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                            Message msg2 = new Message();
                            msg2.what = DISMISS_LOADING;
                            mHandler.sendMessage(msg2);

                            CustomToast.showToast(getApplicationContext(), "网络不给力，请重试！");
                        }
                    });

                }
            }
        });


    }

    private Spinner spCitys;
    private Button btnConfirm;

    private int proIndex = -1;
    private int cityIndex = -1;

    private void doShowProvinceDialog(final BMCityResult city) {

        proIndex = -1;
        cityIndex = -1;

        viewProvince = getLayoutInflater().inflate(R.layout.layout_location_select, null);
        spProvince = (Spinner) viewProvince.findViewById(R.id.sp_location);
        spCitys = (Spinner) viewProvince.findViewById(R.id.sp_city);
        btnConfirm = (Button) viewProvince.findViewById(R.id.btn_confirm_dialog);
        btnClose = (Button) viewProvince.findViewById(R.id.btn_close_dialog);

        mDialog = new Dialog(this);

        mDialog.setContentView(viewProvince);
        mDialog.setTitle("选择所在地");

        mDialog.setCancelable(true);

        mDialog.show();

        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        btnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                String cityName = "";

                final String oldPro = MineProfile.getInstance().getArea();
                final String oldCity = MineProfile.getInstance().getCity();

                if (proIndex != -1 && cityIndex != -1) {
                    MineProfile.getInstance().setArea(city.getCitys().get(proIndex).getId() + "");
                    MineProfile.getInstance().setCity(city.getCitys().get(proIndex).getChildren().get(cityIndex).getId() + "");

                    MineProfile.getInstance().setProId(city.getCitys().get(proIndex).getId());
                    MineProfile.getInstance().setCityId(city.getCitys().get(proIndex).getChildren().get(cityIndex).getId());

                    str = city.getCitys().get(proIndex).getName();
                    cityName = city.getCitys().get(proIndex).getChildren().get(cityIndex).getName();
                } else {
                    Toast.makeText(getApplicationContext(), "请选择城市", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String finalStr = str;
                final String finalCityName = cityName;
                LoadingTask task = new LoadingTask(BMUserinfoActivity.this, new LoadingTask.ILoading() {

                    @Override
                    public void loading(IRequestListener listener) {
                        NetUtil.getInstance().updateUserinfo(new IRequestListener() {
                            @Override
                            public void onRequestSuccess(BaseResult responseData) {

                                if (responseData.getErrorCode() == 0) {
                                    MineProfile.getInstance().setArea(finalStr);
                                    MineProfile.getInstance().setCity(finalCityName);
                                    MineProfile.getInstance().Save();

                                    CustomToast.showToast(getApplicationContext(), "修改成功");
                                    initView();
                                    if (mDialog != null && mDialog.isShowing()) {
                                        mDialog.dismiss();
                                        mDialog = null;
                                    }
                                } else {

                                    MineProfile.getInstance().setArea(oldPro);
                                    MineProfile.getInstance().setCity(oldCity);
                                    CustomToast.showToast(getApplicationContext(), "修改失败");
                                }
                            }

                            @Override
                            public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                                CustomToast.showToast(getApplicationContext(), msg);
                            }
                        });
                    }

                    @Override
                    public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
                    }

                    @Override
                    public boolean isShowNoNetWorkView() {
                        return true;
                    }

                    @Override
                    public IRequestListener getRequestListener() {
                        return BMUserinfoActivity.this;
                    }

                    @Override
                    public boolean isAsync() {
                        return false;
                    }
                });

                task.setRootView(getWindow().getDecorView());
                task.loading();
            }
        });

        SharedPreferences sp = getSharedPreferences("cache", Context.MODE_PRIVATE);

        String cache_province = sp.getString("citys", "");

        if (!cache_province.equals("")) {

            String[] provinns = new String[city.getCitys().size()];

            int index = 0;
            int defPos = -1;

            for (CityListResult pi : city.getCitys()) {
                provinns[index] = pi.getName();

                if (pi.getName().equals(MineProfile.getInstance().getArea()) || pi.getName().contains(MineProfile.getInstance().getArea())) {
                    defPos = index;
                    proIndex = defPos;

//                    break;
                }
                index++;
            }

            // 建立Adapter并且绑定数据源
//            SpinnerListAdapter _Adapter = new SpinnerListAdapter(this,provinns);
            ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinns);
            //绑定 Adapter到控件
            spProvince.setAdapter(_Adapter);
            spProvince.setSelection(defPos, true);

            spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try{

                        final String str = parent.getItemAtPosition(position).toString();

                        if (str.equals("")) {
                            return;
                        }

                        proIndex = position;

                        cityIndex = -1;

                        initCitySpinner(position, city);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

//                    MineProfile.getInstance().setArea(blr.getProviceList().get(position).getId() + "");

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(defPos != -1){
                initCitySpinner(defPos,city);
            }
        }

        Message msg = new Message();
        msg.what = DISMISS_LOADING;
        mHandler.sendMessage(msg);
    }

    private void initCitySpinner(int position, BMCityResult city) {
        int cIndex = 0;
        int desPos = 0;

        String[] citys = new String[city.getCitys().get(position).getChildren().size()];
        for (CityListResult.City ci : city.getCitys().get(position).getChildren()) {
            citys[cIndex] = ci.getName();

            if (ci.getName().contains(MineProfile.getInstance().getCity()) || ci.getName().equals(MineProfile.getInstance().getCity())) {
                desPos = cIndex;
                cityIndex = desPos;
//                break;
            }
            cIndex++;
        }

        ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, citys);

        spCitys.setAdapter(city_adapter);

        spCitys.setSelection(desPos, true);

        spCitys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private View sexView;
    private RadioGroup rgSex;

    public void showSelectSex(View v) {

        sexView = getLayoutInflater().inflate(R.layout.layout_sex_select, null);
        rgSex = (RadioGroup) sexView.findViewById(R.id.rg_sex);

        mDialog = new Dialog(this);

        mDialog.setContentView(sexView);
        mDialog.setTitle("设置性别");

        mDialog.setCancelable(true);

        mDialog.show();

        if (MineProfile.getInstance().getUserType() == 1) {
            RadioButton rbMale = (RadioButton) rgSex.findViewById(R.id.rb_male);
            rbMale.setChecked(true);
        } else if (MineProfile.getInstance().getUserType() == 0) {
            RadioButton rbMale = (RadioButton) rgSex.findViewById(R.id.rb_female);
            rbMale.setChecked(true);
        } else {
            RadioButton rbMale = (RadioButton) rgSex.findViewById(R.id.rb_none);
            rbMale.setChecked(true);
        }


        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
                        MineProfile.getInstance().setUserType(1);
                        break;
                    case R.id.rb_female:
                        MineProfile.getInstance().setUserType(0);
                        break;
                    case R.id.rb_none:
                        MineProfile.getInstance().setUserType(2);
                        break;
                }

                initView();

                LoadingTask task = new LoadingTask(BMUserinfoActivity.this, new LoadingTask.ILoading() {

                    @Override
                    public void loading(NetUtil.IRequestListener listener) {
                        NetUtil.getInstance().updateUserinfo(new NetUtil.IRequestListener() {
                            @Override
                            public void onRequestSuccess(BaseResult responseData) {

                                if (responseData.getErrorCode() == 0) {
                                    CustomToast.showToast(getApplicationContext(), "修改成功");
                                    initView();
                                    if (mDialog != null && mDialog.isShowing()) {
                                        mDialog.dismiss();
                                        mDialog = null;
                                    }
                                } else {

                                    if (responseData.getSuccess() == 3) {
                                        MineProfile.getInstance().Reset();
                                        Intent intent = new Intent(getApplicationContext(), BMLoginActivity.class);
                                        startActivity(intent);
                                    }

                                    CustomToast.showToast(getApplicationContext(), "修改失败");
                                }
                            }

                            @Override
                            public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

                                if (errorCode == 3) {
                                    MineProfile.getInstance().Reset();
                                    Intent intent = new Intent(getApplicationContext(), BMLoginActivity.class);
                                    startActivity(intent);
                                }

                                CustomToast.showToast(getApplicationContext(), msg);
                            }
                        });
                    }

                    @Override
                    public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
                    }

                    @Override
                    public boolean isShowNoNetWorkView() {
                        return true;
                    }

                    @Override
                    public NetUtil.IRequestListener getRequestListener() {
                        return BMUserinfoActivity.this;
                    }

                    @Override
                    public boolean isAsync() {
                        return false;
                    }
                });

                task.setRootView(getWindow().getDecorView());
                task.loading();
            }
        });
    }

    public void showedit(View v) {

        if (v.getId() == R.id.bm_rl_userhead) {

            try{
                if (MineProfile.getInstance().getIsLogin()) {
                    showModifyAvatarView();
                } else {
                    Intent loginIntent = new Intent(BMUserinfoActivity.this, BMLoginActivity.class);
                    startActivity(loginIntent);
                }
            }catch (Exception e){
                e.printStackTrace();
            }catch (Error er){
                er.printStackTrace();
            }


            return;
        }

        int type = USERINFO_EDIT_TYPE_NICK;
        String title = "";

        switch (v.getId()) {
            case R.id.bm_rl_user_nick:
                type = USERINFO_EDIT_TYPE_NICK;
                title = "请输入昵称";
                break;
            case R.id.bm_rl_user_name:
                type = USERINFO_EDIT_TYPE_NAME;
                title = "请输入名字";
                break;
            case R.id.bm_rl_user_sign:
                type = USERINFO_EDIT_TYPE_SIGN;
                title = "请输入签名";
                break;
        }

        final int finalType = type;
        mDialog = DialogFactory.createCheckRootDownDialog(this, type, title, new onEditUserInfoDialogDismissListener() {
            @Override
            public void onEditUserInfoDialogDismissed(String res) {

                if (res.trim().equals("")) {
                    switch (finalType) {
                        case USERINFO_EDIT_TYPE_NICK:
                            CustomToast.showToast(getApplicationContext(), "昵称不能为空");
                            break;
                        case USERINFO_EDIT_TYPE_NAME:
                            CustomToast.showToast(getApplicationContext(), "名字不能为空");
                            break;
                        case USERINFO_EDIT_TYPE_SIGN:
                            CustomToast.showToast(getApplicationContext(), "签名不能为空");
                            break;
                    }
                    return;
                }

                initView();

                LoadingTask task = new LoadingTask(BMUserinfoActivity.this, new LoadingTask.ILoading() {

                    @Override
                    public void loading(NetUtil.IRequestListener listener) {
                        NetUtil.getInstance().updateUserinfo(new NetUtil.IRequestListener() {
                            @Override
                            public void onRequestSuccess(BaseResult responseData) {

                                if (responseData.getErrorCode() == 0) {
                                    CustomToast.showToast(getApplicationContext(), "修改成功");
                                    initView();
                                    mDialog.dismiss();
                                } else {
                                    CustomToast.showToast(getApplicationContext(), "修改失败");
                                }
                            }

                            @Override
                            public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

                                if (errorCode == 3) {
                                    MineProfile.getInstance().Reset();

                                    Intent loginIntent = new Intent(BMUserinfoActivity.this, BMLoginActivity.class);
                                    startActivity(loginIntent);
                                }
                                CustomToast.showToast(getApplicationContext(), msg);
                            }
                        });
                    }

                    @Override
                    public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
                    }

                    @Override
                    public boolean isShowNoNetWorkView() {
                        return true;
                    }

                    @Override
                    public NetUtil.IRequestListener getRequestListener() {
                        return BMUserinfoActivity.this;
                    }

                    @Override
                    public boolean isAsync() {
                        return false;
                    }
                });

                task.setRootView(getWindow().getDecorView());
                task.loading();
            }
        });

        mDialog.show();

    }

    /**
     * request code
     */
    public static final int GALLERY_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int RESULT_REQUEST_CODE = 2;

    private void showModifyAvatarView() {
        ModifyAvatarDialog modifyAvatarDialog = new ModifyAvatarDialog(this, R.style.dialog_avatar_bg) {
            @Override
            public void chooseGallery() {
                this.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }

            @Override
            public void useCamera() {
                this.dismiss();
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        File filePath = new File(Constants.IMGCACHE_FOLDER);
                        if (!filePath.exists()) {
                            filePath.mkdirs();
                        }
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(filePath, Constants.TEMPFILE_NAME);
                        Uri uri = Uri.fromFile(f);
                        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    } catch (ActivityNotFoundException e) {
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "SD卡存储空间不足!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlignmentSpan span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        String dTitle = "header";
        spannable.append(dTitle);
        spannable.setSpan(span, 0, dTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_size, 0, dTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        modifyAvatarDialog.setTitle(spannable);
        modifyAvatarDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        NetUtil.getInstance().cancelRequestById(requestId);
    }


    private ImageView ivUserhead;
    private TextView tvUserNick;
    private TextView tvUserName;
    private TextView tvUserSex;
    private TextView tvUserArea;
    private TextView tvUserSign;

    private void init() {
        ivUserhead = (ImageView) findViewById(R.id.img_logo);

        tvUserNick = (TextView) findViewById(R.id.label_user_nick);
        tvUserName = (TextView) findViewById(R.id.label_user_username);
        tvUserSex = (TextView) findViewById(R.id.label_user_sex);
        tvUserArea = (TextView) findViewById(R.id.label_user_area);
        tvUserSign = (TextView) findViewById(R.id.label_user_sign);
    }

    private void initView() {

        init();

        ImageLoaderHelper.displayImage(MineProfile.getInstance().getStrUserHead(), ivUserhead, ImageLoaderHelper.optionUserHead);

        tvUserNick.setText(MineProfile.getInstance().getNickName());
        tvUserName.setText(MineProfile.getInstance().getUserName());
        if (MineProfile.getInstance().getUserType() == 1) {
            tvUserSex.setText("男");
        } else if (MineProfile.getInstance().getUserType() == 0) {
            tvUserSex.setText("女");
        } else {
            tvUserSex.setText("保密");
        }

        tvUserArea.setText(MineProfile.getInstance().getArea());
        tvUserSign.setText(MineProfile.getInstance().getSignture());

    }

    private void initView(BMUserInfoResult result) {

        init();

        ImageLoaderHelper.displayImage(result.getPhoto(), ivUserhead, ImageLoaderHelper.getCustomOption(R.drawable.bm_user_header_unlogin));

        tvUserNick.setText(result.getNickname());
        tvUserName.setText(result.getRealname());
        tvUserSex.setText(result.getSex().equals("1") ? "男" : "女");
        tvUserArea.setText(result.getCity());
        tvUserSign.setText(result.getSignature());

        MineProfile.getInstance().setStrUserHead(result.getPhoto());
        MineProfile.getInstance().setNickName(result.getNickname());
        MineProfile.getInstance().setUserName(result.getRealname());
        MineProfile.getInstance().setUserType(result.getSex().equals("1") ? 1 : 0);
        MineProfile.getInstance().setArea(result.getProvinces());
        MineProfile.getInstance().setCity(result.getCity());
        MineProfile.getInstance().setSignture(result.getSignature());

    }


    @Override
    public void onRequestSuccess(BaseResult responseData) {

        if (responseData.getTag().equals(Constants.NET_TAG_USERINFO + "")) {
            if (responseData.getSuccess() == 0 && responseData.getErrorCode() == DcError.DC_OK)
                initView((BMUserInfoResult) responseData);
            else {
                BMUserInfoResult bir = (BMUserInfoResult) responseData;
                if (bir.getSuccess() == 3) {
                    CustomToast.showToast(getApplicationContext(), "登录已过期！请重新登录");
                    MineProfile.getInstance().setIsLogin(false);
                    MineProfile.getInstance().Save();

                    Intent loginIntent = new Intent(BMUserinfoActivity.this, BMLoginActivity.class);
                    startActivity(loginIntent);

                    finish();

                    MainHallActivity.jumpToTabByChildActivity(this, 0);
                }
            }
        }

        if (responseData.getTag().equals(Constants.NET_TAG_MODIFYUSER)) {

        }

    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

        CustomToast.showToast(getApplicationContext(), msg);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    String phonenum;
    String verifyCode;
    String password;
    String username;

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_commit_login:

                username = ((EditText) findViewById(R.id.edit_username)).getText().toString();
                password = ((EditText) findViewById(R.id.edit_p_pwd)).getText().toString();

                if (!StringUtil.checkValidUserName(username)) {
                    findViewById(R.id.edit_username).requestFocus();
                    return;
                }

                if (!StringUtil.checkValidPassword(password)) {
                    findViewById(R.id.edit_p_pwd).requestFocus();
                    return;
                }

                findViewById(R.id.bm_ll_user_register_hint1).setVisibility(View.GONE);
                findViewById(R.id.bm_ll_user_register_username).setVisibility(View.GONE);

                findViewById(R.id.bm_ll_user_register_hint2).setVisibility(View.VISIBLE);
                findViewById(R.id.bm_ll_user_register_phone).setVisibility(View.VISIBLE);

                break;
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.label_p_agree_protocol:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    if (data != null) {
                        Uri uri = data.getData();
                        progressDialog = CustomProgressDialog.createDialog(BMUserinfoActivity.this);
                        progressDialog.setMessage("截取头像");
                        progressDialog.show();

                        if (!TextUtils.isEmpty(uri.getAuthority())) {
                            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                            if (null == cursor) {
                                Toast.makeText(this, "No photo goted", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            cursor.moveToFirst();
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                            Intent intent = new Intent(this, CropImageActivity.class);
                            intent.putExtra("path", path);
                            startActivityForResult(intent, RESULT_REQUEST_CODE);
                        } else {
                            Intent intent = new Intent(this, CropImageActivity.class);
                            intent.putExtra("path", uri.getPath());
                            startActivityForResult(intent, RESULT_REQUEST_CODE);
                        }
                    }
                    break;
                case CAMERA_REQUEST_CODE:
                    progressDialog = CustomProgressDialog.createDialog(BMUserinfoActivity.this);
                    progressDialog.setMessage("截取头像");
                    progressDialog.show();
                    File f = new File(Constants.IMGCACHE_FOLDER, Constants.TEMPFILE_NAME);
                    Intent intent = new Intent(this, CropImageActivity.class);
                    intent.putExtra("path", f.getAbsolutePath());
                    startActivityForResult(intent, RESULT_REQUEST_CODE);
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        final String path = data.getStringExtra("path");

                        LoadingTask task = new LoadingTask(BMUserinfoActivity.this, new LoadingTask.ILoading() {

                            @Override
                            public void loading(NetUtil.IRequestListener listener) {
                                NetUtil.getInstance().uploadUserHead(path, path, new NetUtil.IRequestListener() {
                                    @Override
                                    public void onRequestSuccess(BaseResult responseData) {

                                        if (responseData.getErrorCode() == 0 && responseData.getSuccess() == 1) {
                                            CustomToast.showToast(getApplicationContext(), "上传成功");
                                            NetUtil.getInstance().requestForUserinfo(BMUserinfoActivity.this);
//                                            initView();
                                        } else {
                                            CustomToast.showToast(getApplicationContext(), responseData.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                                        CustomToast.showToast(getApplicationContext(), msg);
                                    }
                                });
                            }

                            @Override
                            public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
                            }

                            @Override
                            public boolean isShowNoNetWorkView() {
                                return true;
                            }

                            @Override
                            public NetUtil.IRequestListener getRequestListener() {
                                return BMUserinfoActivity.this;
                            }

                            @Override
                            public boolean isAsync() {
                                return false;
                            }
                        });

                        task.setRootView(getWindow().getDecorView());
                        task.loading();
                    }

                    break;
                default:
                    break;
            }
        }
    }
}