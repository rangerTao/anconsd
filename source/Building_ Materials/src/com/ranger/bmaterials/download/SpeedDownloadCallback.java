package com.ranger.bmaterials.download;

import android.content.Context;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.ui.CustomToast;

public class SpeedDownloadCallback implements DownloadCallback{
	
	private Context mContext;
	
	public SpeedDownloadCallback(Context context) {
		mContext = context;
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

	@Override
	public void onRestartDownloadResult(String downloadUrl, String saveDest,
			boolean successful, Integer reason) {
		try {
			toast(successful, reason);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private void toast(boolean status, final int reason) {
		if (!status) {
			switch (reason) {
			case PackageMode.ERROR_HTTP_ERROR:
				CustomToast.showToast(mContext, "网络错误");
				break;
			case PackageMode.ERROR_DEVICE_NOT_FOUND:
				CustomToast.showToast(mContext,
						mContext.getString(R.string.sdcard_unmounted));
				break;
			case PackageMode.ERROR_INSUFFICIENT_SPACE:
				CustomToast.showToast(mContext,
						mContext.getString(R.string.sdcard_lack_space));
				break;
			default:
				break;
			}
		
//			cx.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					switch (reason) {
//					case PackageMode.ERROR_HTTP_ERROR:
//						CustomToast.showToast(mContext, "网络错误");
//						break;
//					case PackageMode.ERROR_DEVICE_NOT_FOUND:
//						CustomToast.showToast(mContext,
//								mContext.getString(R.string.sdcard_unmounted));
//						break;
//					case PackageMode.ERROR_INSUFFICIENT_SPACE:
//						CustomToast.showToast(mContext,
//								mContext.getString(R.string.sdcard_lack_space));
//						break;
//					default:
//						break;
//					}
//				}
//			});
		}
	}
}
