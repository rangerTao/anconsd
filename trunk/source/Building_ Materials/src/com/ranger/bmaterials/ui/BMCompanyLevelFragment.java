package com.ranger.bmaterials.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.netresponse.BMCompanyInfoResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.view.PagerSlidingTabStrip;
import com.ranger.bmaterials.work.LoadingTask;

import org.w3c.dom.Text;

public class BMCompanyLevelFragment extends Fragment implements NetUtil.IRequestListener, View.OnClickListener {

    private boolean gameRequestSend = false;
    private int requestId = 0;

    public PagerSlidingTabStrip tabStrip;

    private int userid;

    private TextView bm_tv_company_info_name;
    private TextView bm_tv_company_info_addres;
    private TextView bm_tv_company_info_salary;
    private TextView bm_tv_company_info_range;
    private TextView bm_tv_company_info_number;
    private TextView bm_tv_company_info_type;
    private TextView bm_tv_company_info_legal;
    private TextView bm_tv_company_info_latest;

    private View root;

    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(
                R.layout.bm_layout_company_level_fragmet, null);

        userid = getArguments().getInt(BMCompanyInfoActivity.USER_ID, 0);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bm_tv_company_info_name = (TextView) root.findViewById(R.id.bm_tv_company_info_name);
        bm_tv_company_info_addres = (TextView) root.findViewById(R.id.bm_tv_company_info_addres);
        bm_tv_company_info_salary = (TextView) root.findViewById(R.id.bm_tv_company_info_salary);
        bm_tv_company_info_range = (TextView) root.findViewById(R.id.bm_tv_company_info_range);
        bm_tv_company_info_number = (TextView) root.findViewById(R.id.bm_tv_company_info_number);
        bm_tv_company_info_type = (TextView) root.findViewById(R.id.bm_tv_company_info_type);
        bm_tv_company_info_legal = (TextView) root.findViewById(R.id.bm_tv_company_info_legal);
        bm_tv_company_info_latest = (TextView) root.findViewById(R.id.bm_tv_company_info_latest);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(getActivity() != null ){
            if(BMCompanyInfoActivity.comInfo != null){
                initView(BMCompanyInfoActivity.comInfo);
            }else{
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshGame();
                    }
                },100);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (requestId > 0) {
            NetUtil.getInstance().cancelRequestById(requestId);
        }
    }

    private void initView(BMCompanyInfoResult data){

        bm_tv_company_info_name.setText("公司名称：" + data.getCompanyName());
        bm_tv_company_info_addres.setText("注册地址："+ data.getAddress());
        bm_tv_company_info_salary.setText("注册资本：" + data.getRegistCapital());
        bm_tv_company_info_range.setText("经营范围：" + data.getScope());
        bm_tv_company_info_number.setText("工商注册号："+ data.getLicense());
        bm_tv_company_info_type.setText("公司类型：" +data.getCoType());
        bm_tv_company_info_legal.setText("法人代表：" +data.getLegalPerson());
        bm_tv_company_info_latest.setText("最近年检：" +data.getRecYearlyTime());

    }

    @Override
    public void onRequestSuccess(BaseResult responseData) {

        BMCompanyInfoResult data = (BMCompanyInfoResult) responseData;

        if(getActivity() != null){
            BMCompanyInfoActivity.comInfo = data;
        }

        initView(data);

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

        requestId = NetUtil.getInstance().requestComDetail(userid, this);
    }

    private void requestFinished(boolean succeed) {
        requestId = 0;
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

                            if(responseData.getErrorCode() == 1){
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
                    return BMCompanyLevelFragment.this;
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
