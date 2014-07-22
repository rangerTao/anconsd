package com.ranger.bmaterials.tools;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.CheckUpdateResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomProgressDialog;
import com.ranger.bmaterials.ui.CustomToast;

public class UpdateHelper implements IRequestListener, OnClickListener, OnCancelListener {

	private CheckUpdateResult updateResult;
	private Dialog alertDialog;
	private CustomProgressDialog progressDialog;
	private int requestId;
	private Activity context;
	private boolean silent;
	private boolean checking = false;

	public UpdateHelper(Activity context, boolean silent) {
		this.context = context;
		this.silent = silent;
	}
	
	/**
	 * Check GameTing update.
	 * If false, means manually check, then should always check update without any limitation.
	 */
	public void checkGameTingUpdate(boolean autoCheck) {

		if (checking) {
			return;
		}

		if (silent) {
			long time = System.currentTimeMillis();
			if ((time - MineProfile.getInstance().getLastUpdateTime() < 2 * 24 * 3600 * 1000) && autoCheck) {//两天检查一次
				return;
			} else {
				MineProfile.getInstance().setLastUpdateTime(time);
				MineProfile.getInstance().Save();
			}
		}
		
		checking = true;
		if (!silent) {
			progressDialog = CustomProgressDialog.createDialog(this.context);
			progressDialog.setMessage("检查更新...");
			progressDialog.show();
		}
		requestId = NetUtil.getInstance().requestCheckUpdate(this);
	}

	@Override
	public void onCancel(DialogInterface dialog) {

		if (requestId > 0) {
			NetUtil.getInstance().cancelRequestById(requestId);
		}
	}

	@Override
	public void onClick(View v) {
		checking = false;

		switch (v.getId()) {
		case R.id.btn_check_update_cancel: {
			if(null != alertDialog && alertDialog.isShowing())
				alertDialog.dismiss();
			alertDialog = null;
		}
			break;
		case R.id.btn_check_update_commit: {
			if (null != alertDialog && alertDialog.isShowing())
				alertDialog.dismiss();
			alertDialog = null;

			try {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(updateResult.apkurl);
				intent.setData(content_url);
				this.context.startActivity(intent);
			} catch (Exception e) {
				CustomToast.showLoginRegistErrorToast(this.context, DcError.DC_NET_DATA_ERROR);
			}
		}
			break;

		default:
			break;
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (alertDialog != null) {
			alertDialog.dismiss();
			alertDialog = null;
		}
		requestId = 0;
		updateResult = (CheckUpdateResult) responseData;

		// String currentVersion = PhoneHelper.getAppVersionName();
		int updateType = updateResult.updatetype;

		if (updateType == 0) {
			checking = false;
			if (!silent) {
				CustomToast.showLoginRegistSuccessToast(this.context, CustomToast.DC_OK_CHECK_VERSION);
			}
			MineProfile.getInstance().setUpdateAvailable(false);
		} else {// if (updateType == 1) {

			MineProfile.getInstance().setUpdateAvailable(true);

			if (!context.isFinishing()) {
				LayoutInflater factory = LayoutInflater.from(this.context);
				View dialogView = factory.inflate(R.layout.mine_check_update_result_dialog, null);
				
				dialogView.findViewById(R.id.btn_check_update_cancel).setOnClickListener(this);
				dialogView.findViewById(R.id.btn_check_update_commit).setOnClickListener(this);
				
				((TextView) dialogView.findViewById(R.id.label_update_version)).setText(updateResult.apkversion);
				
				int pkgSize = StringUtil.parseInt(updateResult.apksize);
				
				DecimalFormat df = new DecimalFormat("#.##");
				
				((TextView) dialogView.findViewById(R.id.label_update_size)).setText(df.format(pkgSize / 1024.0 / 1024.0) + "M");
				TextView updateDes = (TextView) dialogView.findViewById(R.id.label_update_des);
				updateDes.setMovementMethod(ScrollingMovementMethod.getInstance());
				updateDes.setText(updateResult.description);
				
				alertDialog = new Dialog(this.context, R.style.dialog);
				DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
				int width = dm.widthPixels - PhoneHelper.dip2px(this.context, 13) * 2;
				alertDialog.addContentView(dialogView, new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT));
				alertDialog.setCancelable(true);
				alertDialog.show();				
			}
		}
		
		GameTingApplication.getAppInstance().sendBroadcast(new Intent(Constants.UPDATE_AVIABLE));
		
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		requestId = 0;
		checking = false;

		if (!silent) {
			CustomToast.showLoginRegistErrorToast(this.context, errorCode);
		}
	}
}
