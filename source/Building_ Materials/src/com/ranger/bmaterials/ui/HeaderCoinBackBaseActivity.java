package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.statistics.GeneralStatistics;

//带返回和金币入口的界面基类 布局上无法直接复用的界面：更多分类页 首页
public abstract class HeaderCoinBackBaseActivity extends Activity {
	protected TextView header_title;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		GeneralStatistics.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		GeneralStatistics.onPause(this);
		super.onPause();
	}

	protected TextView findTitleView() {
		return (TextView) findViewById(R.id.header_title);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayout());

		header_title = (TextView) findViewById(R.id.header_title);
		updateHeaderTitle(getHeaderTitle());
		findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void updateHeaderTitle(String title) {
		if (title != null && !title.equals("")) {
			header_title.setText(title);
		}
	}

	public abstract int getLayout();

	public abstract String getHeaderTitle();

}
