/**
 * 
 */
package com.ranger.lpa.share;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;


/**
 * @author zhangshoutian
 * @date   2014年9月3日
 * @desc   
 */
public class ShareUtil {
	
	private static ShareUtil util = null;
	private IShareResultListener mShareListener = null;
	
	public static final String SHARE_CONTENT = "share_content";
	public static final String SHARE_TYPE = "share_type";
	
	public static final String SP_SHARE_DATE = "share_date";
	public static final String SP_SHARE_COUNT = "share_count";
	
	public static final int SHARE_TYPE_URL = 1;
	public static final int SHARE_TYPE_IMG = 2;
	
	private ShareUtil() {
		super();
	}

	public static ShareUtil getInstance() {
		if (null == util) {
			util = new ShareUtil();
		}

		return util;
	}
	
	public void startShare(Context context, Bundle b) {
		startShare(context, b, null);
	}

	public void startShare(Context context, Bundle b, IShareResultListener l) {
//		if (null != context) {
//			Intent intent = new Intent(context, ShareEntryActivity.class);
//
//			if (null != b) {
//				intent.putExtras(b);
//			}
//
//			unregisterShareListener();
//			registerShareListener(l);
//
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(intent);
//		}
	}
	

	public void unregisterShareListener() {
		mShareListener = null;
	}

	public void registerShareListener(IShareResultListener l) {
		mShareListener = l;
	}

	public void setShareResult(boolean success) {
		if (null != mShareListener) {
			synchronized (mShareListener) {
				if (success) {
					mShareListener.onShareSucceed();
				} else {
                    mShareListener.onShareFailed();
                }
			}
		}
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
}
