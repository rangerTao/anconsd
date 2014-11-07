package com.ranger.bmaterials.ui.gametopic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.netresponse.BMProductInfoResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.BMCompanyInfoActivity;
import com.ranger.bmaterials.ui.BMLoginActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class BMProductDetailFragment extends Fragment implements IRequestListener, OnClickListener {

    private TextView bm_tv_product_title;
    private TextView bm_tv_product_name;
    private TextView bm_tv_product_brand;
    private TextView bm_tv_product_model;
    private TextView bm_tv_product_material;
    private TextView bm_tv_product_price;
    private TextView bm_tv_product_detail;

    private ImageView bm_iv_product_logo;
    private ImageView bm_iv_collect;

    private TextView bm_tv_company_info_name;
    private TextView bm_tv_company_info_contact;
    private TextView bm_tv_company_info_phone;
    private TextView bm_tv_company_info_mphone;

    private TextView bm_tv_company_info_level;

    private FrameLayout bm_fl_save_product;

    private Handler mHandler = new Handler();

    public String getString(Intent intent, String key) {
        if (null != intent && null != key && intent.hasExtra(key)) {
            return intent.getStringExtra(key);
        }

        return null;
    }

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root != null) {
            ViewParent parent = this.root.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.root);

                return root;
            }
        }

        root = inflater.inflate(R.layout.game_topic_fragment, null);

        supplyid = getActivity().getIntent().getStringExtra(BMProductDetailActivity.SUPPLY_ID);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
            }
        },1000);


        return root;
    }

    private String supplyid;

    private int mHeight = 0;

    private void initViewWithData(BMProductInfoResult data) {

        bm_iv_collect = (ImageView) root.findViewById(R.id.iv_product_collect_star);

        bm_tv_product_title = (TextView) root.findViewById(R.id.bm_tv_product_title);
        bm_tv_product_title.setText("产品信息--" + data.getMypro().getProductName());

        bm_tv_product_name = (TextView) root.findViewById(R.id.bm_tv_product_name);
        bm_tv_product_name.setText(data.getMypro().getProductName());

        bm_tv_product_brand = (TextView) root.findViewById(R.id.bm_tv_product_brand);
        bm_tv_product_brand.setText(data.getMypro().getBrand());

        bm_tv_product_model = (TextView) root.findViewById(R.id.bm_tv_product_model);
        bm_tv_product_model.setText(data.getMypro().getStandard());

        bm_tv_product_material = (TextView) root.findViewById(R.id.bm_tv_product_material);
        bm_tv_product_material.setText(data.getMypro().getMaterial());

        bm_tv_product_price = (TextView) root.findViewById(R.id.bm_tv_product_price);
        if(!data.getMypro().getPrice().equals(""))
            bm_tv_product_price.setText("￥" + data.getMypro().getPrice() +"/" + data.getMypro().getUnit());

        bm_tv_product_detail = (TextView) root.findViewById(R.id.bm_tv_product_detail);
        mHeight = bm_tv_product_detail.getLineHeight() * 4 + bm_tv_product_detail.getPaddingTop() + bm_tv_product_detail.getPaddingBottom() + 1;
        adapt();
        UIUtil.setTextViewTextHtml(bm_tv_product_detail, data.getMypro().getDetail());
//        bm_tv_product_detail.setText(data.getMypro().getDetail());


        bm_tv_company_info_name = (TextView) root.findViewById(R.id.bm_tv_company_info_name);
        bm_tv_company_info_name.setText("公司名称：" + data.getmCom().getCompanyName());

        bm_tv_company_info_contact = (TextView) root.findViewById(R.id.bm_tv_company_info_contact);
        bm_tv_company_info_contact.setText("联系人：" + data.getmCom().getLinkName());

        bm_tv_company_info_phone = (TextView) root.findViewById(R.id.bm_tv_company_info_phone);
        bm_tv_company_info_phone.setText("联系电话：" + data.getmCom().getPhone());

        bm_tv_company_info_mphone = (TextView) root.findViewById(R.id.bm_tv_company_info_mphone);
        bm_tv_company_info_mphone.setText("手机：" + data.getmCom().getTelephone());

        bm_tv_company_info_level = (TextView) root.findViewById(R.id.bm_tv_company_info_level);
        bm_tv_company_info_level.setText("信用等级：" + data.getmCom().getIntegralGrade());

        bm_iv_product_logo = (ImageView) root.findViewById(R.id.bm_iv_product_logo);
        ImageLoaderHelper.displayImage(data.getMypro().getProductImage(), bm_iv_product_logo);

        bm_fl_save_product = (FrameLayout) root.findViewById(R.id.bm_fl_save_product);
        bm_fl_save_product.setOnClickListener(this);

        root.findViewById(R.id.bm_btn_view_com_detail).setOnClickListener(this);
        root.findViewById(R.id.bm_rl_product_detail).setOnClickListener(this);
    }

    private boolean mIsOpen = false;

    private ImageView ivAdapt;

    private void adapt()
    {

        ivAdapt = (ImageView) root.findViewById(R.id.iv_detail_dictor);

        if(mIsOpen){
            ivAdapt.setImageResource(R.drawable.arrow_hide_detail);
        }else{
            ivAdapt.setImageResource(R.drawable.arrow_show_detail);
        }

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bm_tv_product_detail.getLayoutParams();

        lp.height = mIsOpen ? RelativeLayout.LayoutParams.WRAP_CONTENT : mHeight;
        bm_tv_product_detail.setLayoutParams(lp);
        bm_tv_product_detail.invalidate();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void requestData() {
        LoadingTask task = new LoadingTask(getActivity(), new ILoading() {

            @Override
            public void loading(IRequestListener listener) {
                NetUtil.getInstance().requestProductInfo(supplyid, listener);
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
                return BMProductDetailFragment.this;
            }

            @Override
            public boolean isAsync() {
                return false;
            }
        });

        task.setRootView(root);
        task.loading();
    }

    private void requestSave() {

        try {
            final int mid = Integer.parseInt(supplyid);

            LoadingTask task = new LoadingTask(getActivity(), new ILoading() {

                @Override
                public void loading(IRequestListener listener) {
                    NetUtil.getInstance().requestCollectProduct(mid, 1, new IRequestListener() {
                        @Override
                        public void onRequestSuccess(BaseResult responseData) {

                            if (responseData.getSuccess() == 1) {
//                                CustomToast.showToast(getActivity(), "收藏成功");
                                Toast.makeText(getActivity().getApplicationContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                                bm_iv_collect.setImageResource(R.drawable.bm_start_collected);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), responseData.getMessage(), Toast.LENGTH_SHORT).show();
//                                CustomToast.showToast(getActivity(), responseData.getMessage());
                            }

                        }

                        @Override
                        public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                            if(errorCode == 3){
                                Intent loginIntent = new Intent(getActivity().getApplicationContext(),BMLoginActivity.class);
                                startActivity(loginIntent);
                            }
                            Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
                    return BMProductDetailFragment.this;
                }

                @Override
                public boolean isAsync() {
                    return false;
                }
            });

            task.setRootView(root);
            task.loading();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private BMProductInfoResult data;

    @Override
    public void onRequestSuccess(BaseResult responseData) {
        data = (BMProductInfoResult) responseData;
        initViewWithData(data);
    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
        if(!msg.trim().equals("")){
            CustomToast.showToast(getActivity(), msg);
        }

        if(errorCode == 3){
            MineProfile.getInstance().setIsLogin(false);
            MineProfile.getInstance().Reset();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.loading_text:
                break;
            case R.id.bm_fl_save_product:
                requestSave();
                break;
            case R.id.bm_btn_view_com_detail:

                Intent intent = new Intent(getActivity(), BMCompanyInfoActivity.class);
                intent.putExtra(BMCompanyInfoActivity.USER_ID, data.getmCom().getUserid());
                intent.putExtra(BMCompanyInfoActivity.USER_NAME,data.getmCom().getCompanyName());
                startActivity(intent);

                break;
            case R.id.bm_rl_product_detail:
                mIsOpen = mIsOpen ? false:true;
                adapt();
                break;
            default:
                break;
        }

    }

}
