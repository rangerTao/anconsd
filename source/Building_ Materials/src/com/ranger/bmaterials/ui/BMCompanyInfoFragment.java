package com.ranger.bmaterials.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.netresponse.BMCompanyInfoResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.work.LoadingTask;

public class BMCompanyInfoFragment extends Fragment implements NetUtil.IRequestListener, View.OnClickListener {

    private boolean gameRequestSend = false;
    private int requestId = 0;

    public PagerSlidingTabStrip tabStrip;

    private int userid;

    private TextView bm_tv_company_info_name;
    private TextView bm_tv_company_info_contact;
    private TextView bm_tv_company_info_phone;
    private TextView bm_tv_company_info_mphone;
    private TextView bm_tv_company_info_addres;
    private TextView bm_tv_company_info_level;

    private ImageView bm_iv_collect;

    private TextView getBm_tv_company_info_detai;

    private View root;

    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(
                R.layout.bm_layout_company_info_fragmet, null);

        userid = getArguments().getInt(BMCompanyInfoActivity.USER_ID, 0);

        return root;
    }

    private View loading;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = root.findViewById(R.id.progress_bar);

        bm_iv_collect = (ImageView) root.findViewById(R.id.iv_product_collect_star);

        bm_tv_company_info_name = (TextView) root.findViewById(R.id.bm_tv_company_info_name);
        bm_tv_company_info_contact = (TextView) root.findViewById(R.id.bm_tv_company_info_contact);
        bm_tv_company_info_phone = (TextView) root.findViewById(R.id.bm_tv_company_info_phone);
        bm_tv_company_info_mphone = (TextView) root.findViewById(R.id.bm_tv_company_info_mphone);
        bm_tv_company_info_addres = (TextView) root.findViewById(R.id.bm_tv_company_info_addres);
        bm_tv_company_info_level = (TextView) root.findViewById(R.id.bm_tv_company_info_level);
        getBm_tv_company_info_detai = (TextView) root.findViewById(R.id.bm_tv_company_info_detail);

        view.findViewById(R.id.bm_fl_save_product).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getActivity() != null ){
            if(BMCompanyInfoActivity.comInfo != null){
                initView(BMCompanyInfoActivity.comInfo);
            }else{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshGame();
                    }
                });

            }
        }
    }

    private void initView(BMCompanyInfoResult data){
        bm_tv_company_info_name.setText("公司名称：" + data.getCompanyName());
        bm_tv_company_info_contact.setText("联系人：" + data.getLinkName());
        bm_tv_company_info_phone.setText("联系电话：" + data.getPhone());
        bm_tv_company_info_mphone.setText("手机：" + data.getTelephone());
        bm_tv_company_info_level.setText("信用等级：" + data.getIntegralGrade());
        bm_tv_company_info_addres.setText("地址："+ data.getAddress());

        getBm_tv_company_info_detai.setText(Html.fromHtml(data.getCompanyAbout()));
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!gameRequestSend) {
            gameRequestSend = true;
            refreshGame();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (requestId > 0) {
            NetUtil.getInstance().cancelRequestById(requestId);
        }
    }

    @Override
    public void onRequestSuccess(BaseResult responseData) {

        BMCompanyInfoResult data = (BMCompanyInfoResult) responseData;

        if(getActivity() != null){
            BMCompanyInfoActivity.comInfo = data;
        }


        requestFinished(true);
    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode,
                               String msg) {
        requestFinished(false);
        switch (errorCode) {
            case DcError.DC_NEEDLOGIN:// 需要登录
                break;
            default:
                break;
        }
    }

    private void refreshGame() {
        requestGame();
    }

    private void requestGame() {

        loading.setVisibility(View.VISIBLE);

        requestId = NetUtil.getInstance().requestComDetail(userid, this);
    }

    private void requestFinished(boolean succeed) {
        requestId = 0;

        loading.setVisibility(View.GONE);
    }

    private void updateTitle(int total) {
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bm_fl_save_product:
                requestSave();
                break;
        }

    }

    private void requestSave() {

        try{

            LoadingTask task = new LoadingTask(getActivity(), new LoadingTask.ILoading() {

                @Override
                public void loading(NetUtil.IRequestListener listener) {
                    NetUtil.getInstance().requestCollectProduct(userid,2, new NetUtil.IRequestListener() {
                        @Override
                        public void onRequestSuccess(BaseResult responseData) {

                            if(responseData.getErrorCode() == 0){
                                CustomToast.showToast(getActivity(),"收藏成功");
                                bm_iv_collect.setImageResource(R.drawable.bm_start_collected);
                            }else{
                                CustomToast.showToast(getActivity(),"收藏失败");
                            }
                        }

                        @Override
                        public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                            CustomToast.showToast(getActivity(),msg);
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
                    return BMCompanyInfoFragment.this;
                }

                @Override
                public boolean isAsync() {
                    return false;
                }
            });

            task.setRootView(root);
            task.loading();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
