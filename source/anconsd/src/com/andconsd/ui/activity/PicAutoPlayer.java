package com.andconsd.ui.activity;

import java.util.Random;

import com.andconsd.R;
import com.andconsd.framework.utils.Constants;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class PicAutoPlayer extends Activity{

	ViewSwitcher vSwitcher;
	ImageView iViewTop;
	ImageView iViewSecond;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picautoplayer);
		
		int index = getIntent().getIntExtra("index", 0);
		
		vSwitcher = (ViewSwitcher) findViewById(R.id.autoplayer);
		
		iViewTop = new ImageView(this);
		iViewSecond = new ImageView(this);
		
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		iViewSecond.setLayoutParams(lp);
		iViewTop.setLayoutParams(lp);
		
		iViewTop.setImageURI(Uri.parse(Constants.files.get(index)));
		
		vSwitcher.addView(iViewTop);
		
		iViewSecond.setImageURI(Uri.parse(Constants.files.get(index)));
		
		new Thread() {
			
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(15000);
						
						int current = vSwitcher.getDisplayedChild();
						
						if(current == 0){
							iViewSecond = (ImageView) vSwitcher.getChildAt(1);
							iViewSecond.setImageURI(Uri.parse(Constants.files.get(getNextIndex())));
							vSwitcher.removeViewAt(1);
							vSwitcher.addView(iViewSecond);
							vSwitcher.showNext();
						}else{
							iViewTop = (ImageView) vSwitcher.getChildAt(0);
							iViewTop.setImageURI(Uri.parse(Constants.files.get(getNextIndex())));
							vSwitcher.removeViewAt(0);
							vSwitcher.addView(iViewTop);
							vSwitcher.showPrevious();
						}
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}
	
	private int getNextIndex(){
		return new Random().nextInt() % Constants.files.size();
	}

	
	
}
