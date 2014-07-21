package com.ranger.bmaterials.ui;

import com.ranger.bmaterials.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsSwitchButton extends LinearLayout {
	private ImageView imgButton;
	//private TextView labelOn;
	//private TextView labelOff;
	
	private boolean mIsOn;
	
    public SettingsSwitchButton(Context context) {
    	super(context);
    }
    
    public SettingsSwitchButton(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	
    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	inflater.inflate(R.layout.settings_custom_switch_button, this);
    	imgButton = (ImageView)findViewById(R.id.settings_switch_image);
    	//labelOn = (TextView)findViewById(R.id.settings_switch_label_on);
    	//labelOff = (TextView)findViewById(R.id.settings_switch_label_off);
    	
    	mIsOn = false;
    	resetState();
    }
    
    public void setOn(boolean on) {
    	mIsOn = on;
    	resetState();
    }
    
    public boolean isOn() {
    	return mIsOn;
    }
    
    private void resetState() {
    	if (mIsOn) {
    		//labelOn.setVisibility(View.VISIBLE);
    		//labelOff.setVisibility(View.INVISIBLE);
    		
    		imgButton.setImageResource(R.drawable.settings_on);
    	} else {
    		//labelOn.setVisibility(View.INVISIBLE);
    		//labelOff.setVisibility(View.VISIBLE);
    		
    		imgButton.setImageResource(R.drawable.settings_off);
    	}
    }
}
