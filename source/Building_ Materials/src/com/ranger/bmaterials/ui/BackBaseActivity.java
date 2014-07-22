package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public abstract class BackBaseActivity extends Activity implements OnClickListener {
	protected Intent intent;
	private String actshare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		setContentView(getLayout());

		String title = getHeaderTitle();
		if (title != null && !title.equals(""))
			((TextView) findViewById(R.id.label_title)).setText(title);
		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.img_msgedit).setOnClickListener(this);
	}

	public abstract int getLayout();

	public abstract String getHeaderTitle();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_msgedit:
			break;
		default:
			break;
		}
	}


	public String getActshare() {
		return actshare;
	}

	public void setActshare(String actshare) {
		this.actshare = actshare;
	}
}