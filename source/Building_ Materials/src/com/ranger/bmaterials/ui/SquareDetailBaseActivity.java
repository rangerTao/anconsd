package com.ranger.bmaterials.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public abstract class SquareDetailBaseActivity extends StatActivity implements OnClickListener {
	protected ViewGroup contentLayout;
	protected ImageView iconIv;
	protected TextView titleTv;
	protected TextView timeTv;
	protected View loadingView;
	private View errorView;

	protected String gameId;
    protected String gameName;
	protected String activityId;
	private View pbView;

	public static final String ARG_DETAIL = "detail";
	protected WebView detailLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		check();
		setContentView(getLayout());
		initTitleBar();
		initView();
		loadData();
	}

	protected void initTitleBar() {
		View backView = findViewById(R.id.img_back);
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
	}

	protected void initView() {
		loadingView = findViewById(R.id.square_detail_base_network);
		contentLayout = (ViewGroup) findViewById(R.id.content_global_layout);

		pbView = loadingView.findViewById(R.id.network_loading_pb);
		errorView = loadingView.findViewById(R.id.loading_error_layout);
		errorView.setOnClickListener(this);
		detailLayout = (WebView)findViewById(R.id.square_detail_content);
		detailLayout.getSettings().setJavaScriptEnabled(true);
		iconIv = (ImageView) findViewById(R.id.game_icon);
		titleTv = (TextView) findViewById(R.id.title_text);
		timeTv = (TextView) findViewById(R.id.time_text);
		findViewById(R.id.viewdetail_button).setOnClickListener(this);

		showLoadingView();

	}

	protected abstract void loadData();

	protected abstract void check();


	protected void fillData(BaseResult data) {
		showContentView();
	}

	protected abstract int getLayout();

	protected void onFailToLoadData() {
		showErrorView();
	}

	private void showContentView() {
		if (loadingView.getVisibility() == View.VISIBLE) {
			loadingView.setVisibility(View.GONE);
		}
		if (contentLayout.getVisibility() != View.VISIBLE) {
			contentLayout.setVisibility(View.VISIBLE);
		}
		errorView.setVisibility(View.GONE);

	}

	private void showErrorView() {
		iconIv.setOnClickListener(null);
		if (loadingView.getVisibility() == View.VISIBLE) {
			pbView.setVisibility(View.GONE);
		}
		if (contentLayout.getVisibility() != View.VISIBLE) {
			contentLayout.setVisibility(View.GONE);
		}
		errorView.setVisibility(View.VISIBLE);

	}

	private void showLoadingView() {
		if (loadingView.getVisibility() != View.VISIBLE) {
			loadingView.setVisibility(View.VISIBLE);
		}
		if (contentLayout.getVisibility() == View.VISIBLE) {
			contentLayout.setVisibility(View.INVISIBLE);
		}
		errorView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.loading_error_layout) {
			if (DeviceUtil.isNetworkAvailable(getApplicationContext())) {
				loadData();
				errorView.setVisibility(View.GONE);
				pbView.setVisibility(View.VISIBLE);
			} else {
				CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
			}
		} else if (v.getId() == R.id.viewdetail_button) {
			AppManager manager = AppManager.getInstance(getApplicationContext());
            manager.jumpToDetail(this, gameId, gameName, null, false);
        }
	}

	protected class RequestContentListener implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (responseData.getErrorCode() == DcError.DC_OK) {
				fillData(responseData);
			} else {
				onFailToLoadData();
				// Toast.makeText(this, "获取详细内容失败", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			onFailToLoadData();

		}

	}

	public void fillEmptyData(int resid) {
		findViewById(R.id.no_data_hint_container).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.no_data_hint_textview)).setText(resid);
		CustomToast.showToast(SquareDetailBaseActivity.this, getString(resid));
	}
}
