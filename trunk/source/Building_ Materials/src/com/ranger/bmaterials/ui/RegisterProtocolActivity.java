package com.ranger.bmaterials.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.ranger.bmaterials.R;

public class RegisterProtocolActivity extends StatActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_protocol_activity);
		((TextView)findViewById(R.id.label_title)).setText(getResources().getString(R.string.register_protocol_title));
		
		findViewById(R.id.img_back).setOnClickListener(this);
		((TextView)findViewById(R.id.label_protocol_content)).setMovementMethod(ScrollingMovementMethod.getInstance());
		
		 final Resources resources = this.getResources();
         InputStream inputStream = resources.openRawResource(R.raw.register_protocol);
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
         
         String protocol = "";
         String line;
         try {
             while ((line = reader.readLine()) != null) {
                 protocol += line;
                 protocol += "\r\n";
              }
		} catch (Exception e) {
		}
         
         ((TextView)findViewById(R.id.label_protocol_content)).setText(protocol);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.img_back){
			this.finish();
		}
	}
}
