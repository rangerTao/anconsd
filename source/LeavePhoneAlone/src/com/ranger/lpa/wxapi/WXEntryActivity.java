/**
 * 
 */
package com.ranger.lpa.wxapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ranger.lpa.Constants;
import com.ranger.lpa.R;
import com.ranger.lpa.share.AccessTokenKeeper;
import com.ranger.lpa.share.ShareUtil;
import com.ranger.lpa.tools.ConnectManager;
import com.ranger.lpa.ui.activity.BaseActivity;
import com.ranger.lpa.ui.view.CustomToast;
import com.ranger.lpa.ui.view.LPAKeyGuardView;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.BaseResp.ErrCode;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * @author taoliang
 * @date 2014年9月3日
 * @desc 分享
 */
public class WXEntryActivity extends BaseActivity implements
		OnClickListener, IWeiboHandler.Response, IWXAPIEventHandler {

	private WeiboAuth mWeiboAuth = null;
	private SsoHandler mSsoHandler = null;
	private Oauth2AccessToken mAccessToken = null;
	private IWeiboShareAPI mWeiboShareAPI = null;

	private String mShareText = null;
	private int mType = 1;

	private static final String URL_HEAD = "http://mgame.baidu.com/hall/?fpt=r1";
	private static final String SINA_URL = URL_HEAD + "&fr=app_qs01"; // sina微博
	private static final String WEIXIN_URL = URL_HEAD + "&fr=app_qs02"; // 好友分享
	private static final String FRIENDS_URL = URL_HEAD + "&fr=app_qs03"; // 朋友圈

	// weixin
	private IWXAPI mWXApi = null;
	private boolean mIsShareFriend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_activity);

		findViewById(R.id.layout_dialog_share).setVisibility(View.VISIBLE);
		mShareText = getIntent().getStringExtra(ShareUtil.SHARE_CONTENT);
		mType = getIntent().getIntExtra(ShareUtil.SHARE_TYPE, 1);

		if (null == mShareText || "".equals(mShareText)) {
			mShareText = getString(R.string.share_text_default);
		}

		findViewById(R.id.weibo_share).setOnClickListener(this);
		findViewById(R.id.weixin_friend_share).setOnClickListener(this);
		findViewById(R.id.weixin_share).setOnClickListener(this);
		findViewById(R.id.dialog_close).setOnClickListener(this);

		// 初始化新浪微博参数
		mWeiboAuth = new WeiboAuth(this, Constants.SINA_APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		if (null != savedInstanceState) {
			if (mWeiboShareAPI != null) {
				mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
			}
		}

		// 初始化微信参数
		mWXApi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_ID, false);
		mWXApi.handleIntent(getIntent(), this);
		mWXApi.registerApp(Constants.WEIXIN_ID);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Sina Weibo
		if (null != mSsoHandler) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);

		// Sina Weibo
		if (null != mWeiboShareAPI) {
			// 当前应用唤起微博分享后，返回当前应用
			mWeiboShareAPI.handleWeiboResponse(intent, this);
		}

		// Weixin
		mWXApi.handleIntent(intent, this);
	}

	// Sina Weibo
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();

		textObject.text = mShareText + getString(R.string.share_click_text)
				+ SINA_URL;

		return textObject;
	}

	// Sina Weibo
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();

		try {
			Bitmap bitmap = null;

			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);

			imageObject.setImageObject(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			System.gc();
		}

		return imageObject;
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

		Log.e("tim", "SHARE >> weibomessage: " + weiboMessage.toString());

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

        Log.e("tim", "SHARE >> weibomessage: " + weiboMessage.toString());

		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();

		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		mWeiboShareAPI.sendRequest(request);
	}

	// Sina Weibo
	private void sendSinaMessage(boolean hasText, boolean hasImage) {
		if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
			int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();

            Log.e("tim", "SHARE >> hastext: " + hasText + " | hasimage: "
					+ hasImage + " | support api: " + supportApi);

			if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
				sendMultiMessage(hasText, hasImage);
			} else {
				sendSingleMessage(hasText, hasImage);
			}
		} else {
//			CustomToast.showToast(this,
//					getString(R.string.weibosdk_demo_not_support_api_hint));
		}
	}

	// Sina Weibo
	private void shareSinaWeibo() {
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			Log.e("tim", "已授权， 进行sso 授权......");
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
					Constants.SINA_APP_KEY);

			try {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					mWeiboShareAPI.registerApp();
					sendSinaMessage(null != mShareText, true);
				}
			} catch (WeiboShareException e) {
				e.printStackTrace();
			}
		} else {
			//sso 授权
            Log.e("tim", "未授权， 进行sso 授权......");
			mSsoHandler = new SsoHandler(WXEntryActivity.this, mWeiboAuth);
			mSsoHandler.authorize(new AuthListener());
		}
	}

	/**
	 * Share to weixin
	 * 
	 * @param friend
	 *            true: to friend | false: to share
	 */
	private void shareToWeixin(boolean friend) {
		mIsShareFriend = friend;

		if (null != mWXApi && !mWXApi.isWXAppInstalled()) {
			CustomToast.showToast(this,
                    getString(R.string.weixin_notinstalled_prompt));
			finish();
			return;
		}

		// 根据分享类型处理操作
		if (1 == mType) {
			shareWeixinUrl(mIsShareFriend);
		}

//		finish();
	}

	// 发送微信分享请求
	private void shareWeixinUrl(boolean isFriend) {

		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = isFriend == false ? FRIENDS_URL : WEIXIN_URL;

		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.description = isFriend == false ? "" : mShareText;
		msg.title = isFriend == false ? mShareText
				: getString(R.string.app_name);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

        int byteCount = bitmap.getRowBytes() * bitmap.getHeight() / 4;
        float rate = (30f * 1024) / byteCount;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * rate), (int) (bitmap.getHeight() * rate), true);

		msg.thumbData = ShareUtil.bmpToByteArray(thumbBmp, true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = ShareUtil.buildTransaction("webpage");
		req.message = msg;
		req.scene = isFriend == false ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;

		mWXApi.sendReq(req);

        finish();
	}

	private int mCurrentView = 0;

	@Override
	public void onClick(View v) {
		if (mCurrentView == v.hashCode()) {
			return;
		}

		mCurrentView = v.hashCode();

		boolean isNetworkOk = ConnectManager
				.isNetworkConnected(WXEntryActivity.this);

		switch (v.getId()) {
		case R.id.dialog_close: {
			finish();
		}
			break;
		case R.id.weibo_share: {
			if (isNetworkOk) {
				shareSinaWeibo();
			} else {
				CustomToast.showToast(WXEntryActivity.this,
						getString(R.string.alert_network_inavailble));
			}
		}
			break;
		// Weixin share
		case R.id.weixin_share: {
			if (isNetworkOk) {
				try {
					shareToWeixin(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				CustomToast.showToast(WXEntryActivity.this,
                        getString(R.string.alert_network_inavailble));
			}
		}
			break;
		// share to weixin friend
		case R.id.weixin_friend_share: {
			if (isNetworkOk) {
				shareToWeixin(false);
			} else {
				CustomToast.showToast(WXEntryActivity.this,
                        getString(R.string.alert_network_inavailble));
			}
		}
			break;
		default:
			break;
		}
	}

    @Override
    public void initView() {

    }


    class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			CustomToast.showToast(WXEntryActivity.this,
                    getString(R.string.weibo_cancel_auth));
		}

		@Override
		public void onComplete(Bundle b) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(b);
			Log.e("tim", "新浪微博授权onCompelete");
			if (mAccessToken.isSessionValid()) {
                Log.e("tim", "新浪微博授权成功");
				AccessTokenKeeper.writeAccessToken(WXEntryActivity.this,
						mAccessToken);
				CustomToast.showToast(WXEntryActivity.this,
						getString(R.string.auth_success));
			} else {
                Log.e("tim", "新浪微博授权失败");
				CustomToast.showToast(WXEntryActivity.this,
						getString(R.string.weibosdk_demo_toast_auth_failed));
			}

			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(
					WXEntryActivity.this, Constants.SINA_APP_KEY);
			mWeiboShareAPI.registerApp();

			if (mWeiboShareAPI.isWeiboAppInstalled() == false) {
                Log.e("tim", "没有安装微博");
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {

							@Override
							public void onCancel() {
								CustomToast
										.showToast(
												WXEntryActivity.this,
												getString(R.string.weibo_download_cancelled_prompt));
							}
						});
			}

			shareSinaWeibo();
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			CustomToast.showToast(WXEntryActivity.this, arg0.getMessage());
		}

	}

	// Sina weibo 接收微博客户端请求的数据
	@Override
	public void onResponse(BaseResponse rsp) {
		if (rsp.errCode == WBConstants.ErrorCode.ERR_OK) {
			CustomToast.showToast(WXEntryActivity.this,
					getString(R.string.share_success));
            LPAKeyGuardView.getInstance(WXEntryActivity.this).unlock();
		} else {
            Log.e("tim", "错误码： " + rsp.errCode + " " + rsp.errMsg);
			CustomToast.showToast(WXEntryActivity.this,
					getString(R.string.share_fail));
		}

		WXEntryActivity.this.finish();
	}

	// Weixin
	@Override
	public void onReq(BaseReq arg0) {
		// RESERVED
	}

	// Weixin
	@Override
	public void onResp(BaseResp resp) {
		if (resp.errCode == ErrCode.ERR_OK) {
            Toast.makeText(this,getString(R.string.share_success),Toast.LENGTH_LONG).show();
            LPAKeyGuardView.getInstance(WXEntryActivity.this).unlock();
		} else {
            Toast.makeText(this,getString(R.string.share_fail),Toast.LENGTH_LONG).show();
		}
		WXEntryActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}

		return super.onKeyDown(keyCode, event);
	}
}
