package com.ranger.bmaterials.download;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.ui.CustomToast;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class DefaultDownLoadCallBack implements DownloadCallback {
	private Activity cx;

	public DefaultDownLoadCallBack(Activity cx) {
		this.cx = cx;
	}

	@Override
	public void onDownloadResult(String downloadUrl, boolean status,
			long downloadId, String saveDest, Integer reason) {
		toast(status, reason);
	}

	@Override
	public void onResumeDownloadResult(String downloadUrl, boolean successful,
			Integer reason) {
		toast(successful, reason);
	}

	private void toast(boolean status, final int reason) {
		//in async thread
		if (!status) {
			cx.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					switch (reason) {
					case PackageMode.ERROR_HTTP_ERROR:
						CustomToast.showToast(cx, "网络错误");
						break;
					case PackageMode.ERROR_DEVICE_NOT_FOUND:
						CustomToast.showToast(cx,
								cx.getString(R.string.sdcard_unmounted));
						break;
					case PackageMode.ERROR_INSUFFICIENT_SPACE:
						CustomToast.showToast(cx,
								cx.getString(R.string.sdcard_lack_space));
						break;
					default:
						break;
					}
				}
			});
		}
	}

	@Override
	public void onRestartDownloadResult(String downloadUrl, String saveDest,
			boolean successful, Integer reason) {
		try {
			toast(successful, reason);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
