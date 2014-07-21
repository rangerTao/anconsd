package com.ranger.bmaterials.sapi;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mobstat.StatActivity;
import com.baidu.sapi2.SapiAccount;
import com.baidu.sapi2.SapiAccountManager;
import com.baidu.sapi2.SapiWebView;
import com.baidu.sapi2.SapiWebView.OnBackCallback;
import com.baidu.sapi2.SapiWebView.OnFinishCallback;
import com.baidu.sapi2.shell.callback.SapiCallBack;
import com.baidu.sapi2.shell.listener.AuthorizationListener;
import com.baidu.sapi2.shell.response.GetPortraitResponse;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.UserLoginResult;
import com.ranger.bmaterials.sapi.util.SapiWebViewUtil;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomToast;

/**
 * 
* @Description: 百度pass接入
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年6月3日 上午10:47:50 
* @version V3.0.0
*
 */
public class SapiLoginActivity extends StatActivity implements IRequestListener{

	private SapiAccount mAccount;
	
	private SapiWebView mSapiWebView;
	private AuthorizationListener authorizationListener = new AuthorizationListener() {
		
		@Override
		public void onSuccess() {
			CustomToast.showLoginRegistSuccessToast(getApplicationContext(), 0);
			mAccount = SapiAccountManager.getInstance().getSession();
			NetUtil.getInstance().requestBaiduSAPI(
					SapiAccountManager.getInstance().getSession(
							SapiAccountManager.SESSION_DISPLAYNAME),
					SapiAccountManager.getInstance().getSession(
							SapiAccountManager.SESSION_UID),
							SapiLoginActivity.this);
		}
		
		@Override
		public void onFailed(int arg0, String arg1) {
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sapi_login_activity);
		
		ClickNumStatistics.addLoginBdPassportClickStatis(this);
		mSapiWebView = (SapiWebView) findViewById(R.id.sapi_webview);
		SapiWebViewUtil.addCustomView(this, mSapiWebView);
		mSapiWebView.setOnFinishCallback(new OnFinishCallback() {
			
			@Override
			public void onFinish() {
				
			}
		});

        try{
            mSapiWebView.setAuthorizationListener(authorizationListener);

            mSapiWebView.loadLogin();

            mSapiWebView.setOnBackCallback(new OnBackCallback() {

                @Override
                public void onBack() {
                    finish();
                }
            });

            mSapiWebView.setOnFinishCallback(new OnFinishCallback() {

                @Override
                public void onFinish() {
                    finish();
                }
            });

        }catch (OutOfMemoryError oom){

        }

	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		// TODO Auto-generated method stub
		UserLoginResult result = (UserLoginResult) responseData;

		MineProfile.getInstance().setUserID(result.getUserid());
		MineProfile.getInstance().setSessionID(result.getSessionid());
		MineProfile.getInstance().setUserName(result.getUsername());

        String nickName = mAccount.displayname.trim();

        if(!nickName.equals("") && result.getUsername().toLowerCase().startsWith("baidu") || result.getNickname().equals("")){
            NetUtil.getInstance().requestChangeNickname(result.getUserid(), result.getSessionid(), nickName, new IRequestListener() {
                @Override
                public void onRequestSuccess(BaseResult responseData) {
                    if(Constants.DEBUG){
                        Log.e("TAG","nick changed ");
                    }
                }

                @Override
                public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
                }
            });
        }

        if(!result.getNickname().equals("") && !result.getNickname().toLowerCase().startsWith("baidu")){
            MineProfile.getInstance().setNickName(result.getNickname());
        }else{
            MineProfile.getInstance().setNickName(mAccount.displayname);
        }

		MineProfile.getInstance().setUserType(result.getRegistertype());
		MineProfile.getInstance().setIsLogin(true);

		if (!TextUtils.isEmpty(result.getPhonenum())) {
			MineProfile.getInstance().setUserType(
					MineProfile.USERTYPE_BINGDINGPHONE);
			MineProfile.getInstance().setPhonenum(result.getPhonenum());
		} else {
			MineProfile.getInstance().setUserType(
					MineProfile.USERTYPE_UNBINDINGPHONE);
			MineProfile.getInstance().setPhonenum("");
		}

		MineProfile.getInstance().setGamenum(result.getGamenum());
		MineProfile.getInstance().setTotalmsgnum(result.getTotalmsgnum());
		MineProfile.getInstance().setMessagenum(result.getMessagenum());
		MineProfile.getInstance().setCollectnum(result.getCollectnum());

		MineProfile.getInstance().setCoinnum(result.getCoinnum());
		MineProfile.getInstance().addAccount(result.getUsername());

		SapiAccountManager.getInstance().getAccountService().getPortrait(new SapiCallBack<GetPortraitResponse>() {
			
			@Override
			public void onSystemError(int arg0) {
				
			}
			
			@Override
			public void onSuccess(GetPortraitResponse res) {
				MineProfile.getInstance().setStrUserHead(res.portrait);
				
				//每次登陆成功，缓存头像到指定目录文件
				File localFile = new File(Constants.IMAGE_PATH, Constants.PHOTO_LOCAL_FILE);
				if (localFile.exists()) {
					localFile.delete();
				}
				ImageLoaderHelper.saveImg2Local(res.portrait, Constants.IMAGE_PATH, Constants.PHOTO_LOCAL_FILE, null);
				
				MineProfile.getInstance().Save();
				Intent refreshUserHead = new Intent(Constants.refresh_head_action);
				LocalBroadcastManager.getInstance(GameTingApplication.getAppInstance().getApplicationContext()).sendBroadcast(refreshUserHead);
				setResult(RESULT_OK);
			}
			
			@Override
			public void onNetworkFailed() {
				
			}
		}, mAccount.bduss, mAccount.ptoken, mAccount.stoken);

		CustomToast.showToast(SapiLoginActivity.this, "登录成功");
		
		MineProfile.getInstance().Save();

		finish();
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode,
			String msg) {
		loginError();
	}

	private void loginError() {
		CustomToast.showToast(SapiLoginActivity.this, "登录失败");
		MineProfile.getInstance().setIsLogin(false);
	}
	
}
