package com.ranger.bmaterials.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public class AboutUsActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.about_us_activity);
		getWindow().setBackgroundDrawableResource(R.drawable.bkgroud_aboutus_gray);
		((TextView)findViewById(R.id.label_title)).setText(getResources().getString(R.string.about_us_title));
		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.label_servicenum).setOnClickListener(this);
		TextView appversion = (TextView) findViewById(R.id.about_app_version);
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();
		
		if (viewID == R.id.img_back) {
			this.finish();
		} else if (viewID == R.id.label_servicenum) {
			String servicenum = ((TextView)findViewById(R.id.label_servicenum)).getText().toString();
			if (servicenum.trim().length() != 0) {
				Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + servicenum));
				startActivity(phoneIntent);
			}
		}
	}
}
