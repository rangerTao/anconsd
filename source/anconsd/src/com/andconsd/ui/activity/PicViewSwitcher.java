package com.andconsd.ui.activity;

import java.io.File;

import com.andconsd.R;
import com.andconsd.ui.widget.DuoleCountDownTimer;
import com.andconsd.ui.widget.DuoleVideoView;
import com.andconsd.ui.widget.ScrollLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class PicViewSwitcher extends Activity implements ViewFactory{

	int cacheEnd = 0;
	int cacheStart = 0;
	RelativeLayout.LayoutParams lp;
	ScrollLayout slPic;
	PicViewSwitcher appref;
	int imgIndex = 0;
	TextView tvIndex;
	RelativeLayout rlController;
	RelativeLayout rlSlidShow;
	
	ImageSwitcher iSwitcher;
	
	int screenWidth = 0;
	int screenHeight = 0;
	
	ImageView iv ;
	DuoleVideoView vv;
	ImageView ivPlay;
	
	File tempFile;
	
	ImageView ivSlidShow;
	
	DuoleCountDownTimer autoPlayerCountDownTimer;

	private int ImageViewId = 999998;
	
	Handler handler = new Handler();
	
	RotateAnimation rotate; 
	ScaleAnimation scale; 
	AlphaAnimation alpha; 
	TranslateAnimation translate;
	
	boolean slidshow = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appref = this;
		setContentView(R.layout.picview);
		
		imgIndex = getIntent().getIntExtra("index", 0);
		
		iSwitcher = (ImageSwitcher) findViewById(R.layout.picview);
		
		iSwitcher.setFactory(this);
		
	}

	@Override
	public View makeView() {
		View view = LayoutInflater.from(appref).inflate(R.layout.picviewitem, null);
		
		return null;
	}

	
	
	
}
