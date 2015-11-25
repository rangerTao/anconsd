package com.andconsd.framework.share;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.andconsd.R;
import com.andconsd.framework.utils.Constants;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

public class ShareUtil {

	private static ShareUtil instance;
	private static Context mContext;
	private Oauth2AccessToken mAccessToken = null;

	private SsoHandler mSsoHandler;

	private String mShareText;
	private String mShareImagePath;

	private boolean mIsSareFriend;

	private ShareUtil() {

	}

	public synchronized static ShareUtil getInstance(Context context) {

		mContext = context;
		if (instance == null) {
			return new ShareUtil();
		}
		return instance;
	}

	private IWXAPI wxApi;

	private void regToWX() {
		wxApi = WXAPIFactory.createWXAPI(mContext, Constants.WEIXIN_ID, true);
		wxApi.registerApp(Constants.WEIXIN_ID);
	}

	public void shareImageToWX(IWXAPI wxapi, String path) {
		shareWeixinImageEx(wxapi, path);
	}

	public void shareImageToWX(IWXAPI wxapi, Bitmap bmp, boolean isFriend) {

		mIsSareFriend = isFriend;

		shareWeiXinImageBitmap(wxapi, bmp);
	}

	private void shareWeixinImageEx(IWXAPI wxapi, String path) {
		Bitmap bitmap = null;

		if (null == path || "".equals(path) || !new File(path).exists()) {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
		} else {
			bitmap = getBitmapFromFile(path);
		}

		shareWeiXinImageBitmap(wxapi, bitmap);
	}

	private void shareWeiXinImageBitmap(IWXAPI wxapi, Bitmap bitmap) {
		WXImageObject imgObj = new WXImageObject(bitmap);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		int byteCount = bitmap.getRowBytes() * bitmap.getHeight() / 4;
		float rate = (30f * 1024) / byteCount;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * rate), (int) (bitmap.getHeight() * rate), true);
		// bitmap.recycle();
		msg.thumbData = bmpToByteArray(thumbBmp, true); // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = mIsSareFriend == false ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		wxapi.sendReq(req);
	}

	private IWeiboShareAPI mWeiboShareAPI = null;
	private Bitmap mWeiboShareBitmap;

	// Sina Weibo
	public void shareSinaWeibo(String shareText,Bitmap imagepath, SsoHandler soHandler) {

		if (!TextUtils.isEmpty(shareText)) {
			mShareText = shareText;
		}
		
		if(imagepath != null)
			mWeiboShareBitmap = imagepath;

		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);

		if (soHandler != null) {
			this.mSsoHandler = soHandler;
		}

		if (mAccessToken.isSessionValid()) {
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, Constants.WEIBO_KEY);

			try {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					mWeiboShareAPI.registerApp();

					// sendSinaMessage(null != mShareText, null != mShareImage);
					sendSinaMessage(null != mShareText, true);
				}
			} catch (WeiboShareException e) {
				e.printStackTrace();
			}
		} else {
			mSsoHandler.authorize(new AuthListener(mShareText));
		}
	}

	// Sina Weibo
	private void shareSinaWeibo(String mShareText) {
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);

		if (mAccessToken.isSessionValid()) {
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, Constants.WEIBO_KEY);

			try {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					mWeiboShareAPI.registerApp();

					// sendSinaMessage(null != mShareText, null != mShareImage);
					sendSinaMessage(null != mShareText, true);
				}
			} catch (WeiboShareException e) {
				e.printStackTrace();
			}
		} else {
			// mSsoHandler = new SsoHandler(WXEntryActivity.this, mWeiboAuth);
			mSsoHandler.authorize(new AuthListener(mShareText));
		}
	}

	// Sina Weibo
	private void sendSinaMessage(boolean hasText, boolean hasImage) {
		if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
			int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();

			if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
				sendMultiMessage(hasText, hasImage);
			} else {
				sendSingleMessage(hasText, hasImage);
			}
		} else {
		}
	}

	// Sina Weibo
	private void sendMultiMessage(boolean hasText, boolean hasImage) {
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}

		if (hasImage) {
			weiboMessage.imageObject = getImageObj();
		}

		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();

		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		mWeiboShareAPI.sendRequest(request);
	}

	// Sina Weibo
	private void sendSingleMessage(boolean hasText, boolean hasImage) {
		WeiboMessage weiboMessage = new WeiboMessage();

		if (hasText) {
			weiboMessage.mediaObject = getTextObj();
		}

		if (hasImage) {
			weiboMessage.mediaObject = getImageObj();
		}

		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();

		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		mWeiboShareAPI.sendRequest(request);
	}

	// Sina Weibo
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = mShareText;
		return textObject;
	}

	// Sina Weibo
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();

		try {
			Bitmap bitmap = null;

			if (mWeiboShareBitmap == null) {
				bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
				// bitmap = ImageUtil.addTextInfoImage(mShareText, bitmap);
			} else {
				bitmap = mWeiboShareBitmap;
			}

			imageObject.setImageObject(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imageObject;
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

	public static Bitmap getBitmapFromFile(String path) {
		FileInputStream fs;
		BufferedInputStream bs;
		Bitmap btp;

		try {
			fs = new FileInputStream(path);
			bs = new BufferedInputStream(fs);
			btp = BitmapFactory.decodeStream(bs);

			bs.close();
			fs.close();

			return btp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	class AuthListener implements WeiboAuthListener {

		private String shareText;

		public AuthListener(String mShareText) {
			shareText = mShareText;
		}

		@Override
		public void onCancel() {
			Log.d("TAG", "on cancel");
		}

		@Override
		public void onComplete(Bundle b) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(b);
			if (mAccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
			} else {
			}

			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, Constants.WEIBO_KEY);
			mWeiboShareAPI.registerApp();
			if (mWeiboShareAPI.isWeiboAppInstalled() == false) {
				mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {

					@Override
					public void onCancel() {
					}
				});
			}

			shareSinaWeibo(shareText);
		}

		@Override
		public void onWeiboException(WeiboException ex) {
			Log.d("TAG", "on exception" + ex.getMessage());
			ex.printStackTrace();
		}

	}
}
