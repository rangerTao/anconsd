package com.andconsd.ui;

import java.io.File;
import java.util.ArrayList;

import com.andconsd.R;
import com.andconsd.utils.Constants;
import com.andconsd.utils.FileHelper;
import com.andconsd.utils.SharePreferenceUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class WelcomeActivity extends Activity {

	private String cache_dir;
	Handler handler = new Handler();
	WelcomeActivity appref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appref = this;
		setContentView(R.layout.welcome);

		Constants.files = new ArrayList<String>();
		
		String dir = SharePreferenceUtil.getInstance(getApplicationContext()).getString(SharePreferenceUtil.SP_CACHE_DIR);
		if(dir != null && !dir.equals("")){
			cache_dir = dir;
		}else{
			if(Build.VERSION.SDK_INT > 7){
				cache_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DCIM;
				Constants.ROOT_DIR = cache_dir;
			}else
				cache_dir = Constants.ROOT_DIR;
		}
		
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				getAllFilesFromFolder(cache_dir);

				startActivity(new Intent(appref, Androsd.class));
				finish();
			}

			private void getAllFilesFromFolder(String cache) {
				final File rootFile = new File(cache);
				if (!rootFile.exists()) {
					rootFile.mkdirs();
				}
				File[] filess = rootFile.listFiles();

				if(filess != null){
					for (File temp : filess) {
						if(temp.isDirectory()){
							if(!temp.getName().contains("thumbnail"))
								getAllFilesFromFolder(temp.getAbsolutePath());
						}
						else{
							if(FileHelper.isPicture(temp.getAbsolutePath()))
								Constants.files.add(temp.getAbsolutePath());
						}
					}
				}
			}
			
		}, 1000);
	}
}
