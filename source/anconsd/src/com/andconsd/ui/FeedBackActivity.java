package com.andconsd.ui;

import java.net.URLEncoder;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.andconsd.R;
import com.andconsd.net.response.BaseResult;
import com.andconsd.utils.Constants;
import com.andconsd.utils.NetUtil;
import com.andconsd.utils.NetUtil.IRequestListener;

public class FeedBackActivity extends SherlockActivity implements OnClickListener, IRequestListener {

	public static int THEME = R.style.Theme_Sherlock;

	private View controlTIps;
	private TextView tvTitle;

	private EditText etContent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(THEME);

		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.layout_feedback_activity);

		initPopupController();

		etContent = (EditText) findViewById(R.id.et_feedback_content);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_commit).setOnClickListener(this);
	}

	private void initPopupController() {

		controlTIps = getLayoutInflater().inflate(R.layout.control_pic_view, null);

		tvTitle = (TextView) controlTIps.findViewById(R.id.tvIndex);
		controlTIps.findViewById(R.id.progress_circular).setVisibility(View.INVISIBLE);

		getSupportActionBar().setCustomView(controlTIps);

		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvTitle.setText("意见与建议");
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_commit:
			String content = etContent.getText().toString();
			if (content.equals("")) {
				Toast.makeText(getApplicationContext(), "空的内容可不行哟~~", Toast.LENGTH_SHORT).show();
				return;
			}
			NetUtil.getInstance().uploadFeedBack(etContent.getText().toString(), this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		Toast.makeText(getApplicationContext(), "提交失败,请重试", Toast.LENGTH_SHORT).show();
	}

}
