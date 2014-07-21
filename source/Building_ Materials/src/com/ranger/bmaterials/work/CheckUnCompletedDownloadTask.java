package com.ranger.bmaterials.work;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.download.DefaultDownLoadCallBack;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.view.CustomDialog;
import com.ranger.bmaterials.view.NetWorkTipDialog;
import com.ranger.bmaterials.view.CustomDialog.ICustomDialog;

public class CheckUnCompletedDownloadTask {
	private Activity cx;

	public CheckUnCompletedDownloadTask(Activity cx) {
		this.cx = cx;
	}

	// 检查未完成的下载任务
	public void checkPauseDownloadTask() {
		new AsyncTask<Void, Void, List<DownloadAppInfo>>() {
			@Override
			protected List<DownloadAppInfo> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				AppManager manager = AppManager.getInstance(cx);
				List<DownloadAppInfo> list = manager.getAndCheckDownloadGames();
				List<DownloadAppInfo> result = new ArrayList<DownloadAppInfo>();
				if (null != list) // added by lsh Caused by:
									// java.lang.NullPointerException
				{
					for (DownloadAppInfo info : list) {
						if (info.getStatus() == DownloadStatus.STATUS_PAUSED) {
							result.add(info);
						}
					}
				}
				return result;
			}

			@Override
			protected void onPostExecute(List<DownloadAppInfo> result) {
				// TODO Auto-generated method stub
				if (!result.isEmpty())
					showContinueDownLoadDialog(result);
			}
		}.execute();

	}

	private void showContinueDownLoadDialog(final List<DownloadAppInfo> result) {
		CustomDialog resultDialog = new CustomDialog(cx);
		resultDialog.setTitle(cx
				.getString(R.string.home_continue_download_dialog_title));

		ICustomDialog impl = new ICustomDialog() {

			@Override
			public void preAddView(CustomDialog dialog) {
				// TODO Auto-generated method stub
				dialog.buttonLeft
						.setText(cx
								.getString(R.string.home_continue_download_dialog_cancel));
				dialog.buttonRight
						.setText(cx
								.getString(R.string.home_continue_download_dialog_confirm));

				dialog.setMessage(String.format(cx
						.getString(R.string.home_continue_download_dialog_msg),
						result.size()));
			}

			@Override
			public View initOtherView(CustomDialog dialog) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		resultDialog
				.setImpl(impl)
				.setClickListner(
						new ContinueDownloadDialogListener(resultDialog, result))
				.createView().show();
	}

	private class ContinueDownloadDialogListener implements OnClickListener {
		private Dialog dialog;
		private List<DownloadAppInfo> result;

		public ContinueDownloadDialogListener(Dialog dialog,
				List<DownloadAppInfo> result) {
			this.dialog = dialog;
			this.result = result;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.dialog_button_left:
				dialogDismiss();
				break;
			case R.id.dialog_button_right:
				ArrayList<DownloadAppInfo> result_list = new ArrayList<DownloadAppInfo>(
						result);

				if (checkWifiConfig(cx)) {
					showWifiDialog(result_list);
				} else {
					resumeDownload(result_list);
				}
				dialogDismiss();
				break;

			default:
				break;
			}
		}
		
		private void dialogDismiss(){
			if(null !=dialog && dialog.isShowing())
				dialog.dismiss();
			dialog = null;
		}
	}

	private void showWifiDialog(final ArrayList<DownloadAppInfo> result) {
		final NetWorkTipDialog resultDialog = new NetWorkTipDialog(cx);

		resultDialog.setClickListner(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.dialog_button_right:
					resultDialog.dismiss();
					break;
				case R.id.dialog_button_left:
					resumeDownload(result);
					resultDialog.changeConfig();

					resultDialog.dismiss();
					break;

				default:
					break;
				}
			}
		}).createView().show();
	}

	private void resumeDownload(ArrayList<DownloadAppInfo> result) {

        if(!ConnectManager.isNetworkConnected(GameTingApplication.getAppInstance().getApplicationContext())){
            CustomToast.showToast(cx,cx.getString(R.string.alert_network_inavailble));
            return;
        }

		DefaultDownLoadCallBack callBack = new DefaultDownLoadCallBack(cx);
		for (DownloadAppInfo info : result) {
			PackageHelper.resumeDownload(info.getDownloadId(), callBack);
			DownloadStatistics.addResumeDownloadGameStatistics(cx,
					info.getName());
		}
	}

	private static boolean checkWifiConfig(Context context) {
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (!ConnectManager.isWifi(context))
				return true;
		}
		return false;
	}
}
