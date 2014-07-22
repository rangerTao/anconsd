package com.ranger.bmaterials.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.ui.CustomToast;

public class GameTingApplication extends Application {

	private static GameTingApplication mInstance = null;

	public static final String ACTION_RECEIVED_ACCOUNT = "com.baidu.intent.action.RECEIVED_ACCOUNT";

	private static final String CUSTOM_THEME_URL = "file:///android_asset/sapi_theme/style.css";

	@Override
	public void onCreate() {
		mInstance = this;

		// NEED CLOSE WHEN RELEASE
		if (Constants.DEBUG) {
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init(getApplicationContext());
		}

		initApp();
	}

	public void initApp() {


		ImageLoaderHelper.config();
		NetUtil.getInstance();

	}

	private Runnable checkSdcard() {
		String status = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(status)) {
			CustomToast.showToast(this, getString(R.string.sdcard_unmounted));
		} else {
			final Handler h = new Handler();
		}
		return null;
	}

	public static GameTingApplication getAppInstance() {
		return mInstance;
	}

}
