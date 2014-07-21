package com.ranger.bmaterials.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.GameTingApplication;

public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
	
	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static CustomProgressDialog createDialog(Context context){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.mine_progress_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCancelable(true);
		
		return customProgressDialog;
	}
	
	public static CustomProgressDialog createDialogForLoadingMessageDetail(Context context){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.network_loading);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCancelable(true);
		
		return customProgressDialog;
	}
	
	/**
	 * Create a dialog without the mask layer, and
	 * show at the below of screen.
	 */
	public static CustomProgressDialog createCommonDialog(Context context, OnCancelListener cancelListener){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.mine_progress_dialog);
		WindowManager.LayoutParams params = customProgressDialog.getWindow().getAttributes();
		params.x = 0;
		DisplayMetrics displayMetrics = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
		int height = displayMetrics.heightPixels;
//		Log.e("CustomProgress", "params.y="+params.y+",height="+height);
		params.y = height/4;
		params.dimAmount = 0.0f;
		customProgressDialog.setCancelable(true);
		customProgressDialog.setOnCancelListener(cancelListener);
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    }
 
    public CustomProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
    public CustomProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}
