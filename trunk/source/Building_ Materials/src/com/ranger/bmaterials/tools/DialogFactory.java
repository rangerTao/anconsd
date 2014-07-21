package com.ranger.bmaterials.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.ui.CustomProgressDialog;
import com.ranger.bmaterials.ui.ShellCommand;

public class DialogFactory {

	private static Context mContext;
	private static CustomProgressDialog progressDialog;
	private static Handler mHandler;
	private static boolean rootResult = false;

	public static Dialog createCheckRootDownDialog(Context context) {
		mContext = context;
		final Dialog dialog = new Dialog(context, R.style.dialog);
		
		LayoutInflater factory = LayoutInflater.from(context);
		View dialogView = factory.inflate(R.layout.rootuser_download_dialog, null);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int btnId = v.getId();
				switch (btnId) {
				case R.id.btn_rootuser_cancel:
					dialog.dismiss();
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					break;
				case R.id.btn_rootuser_commit:
					dialog.dismiss();
					
					progressDialog = CustomProgressDialog.createDialog(mContext);
					progressDialog.setMessage("获取root权限...");
					progressDialog.show();

					mHandler = new Handler() {

						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);

							if (mContext == null) {
								progressDialog.hide();
								return;
							}
							if (progressDialog != null) {
								progressDialog.dismiss();
								progressDialog = null;							
							}
							if (rootResult) {
								MineProfile.getInstance().setInstallAutomaticllyAfterDownloading(true);	
							} else if (mContext != null) {
								Toast.makeText(mContext, R.string.get_root_failed_tip, Toast.LENGTH_SHORT).show();							
							}
						}
					};

					new Thread(new Runnable() {
						@Override
						public void run() {
							rootResult = new ShellCommand().canSU(true);
							mHandler.sendMessage(mHandler.obtainMessage());
						}
					}).start();
					break;
				default:
					break;
				}
			}
		};		
		
		dialogView.findViewById(R.id.btn_rootuser_cancel).setOnClickListener(clickListener);
		dialogView.findViewById(R.id.btn_rootuser_commit).setOnClickListener(clickListener);		
		
		DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
		int width = dm.widthPixels - PhoneHelper.dip2px(mContext, 13) * 2;
		
		dialog.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
		dialog.setCancelable(true);
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;							
				}
			}
		});
		
		MineProfile profile = MineProfile.getInstance();
		profile.setLastCheckrootTime(System.currentTimeMillis());
		profile.setCheckRootPrompTime(profile.getCheckRootPrompTime()+1);
		
		return dialog;
	}
}
