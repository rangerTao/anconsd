package com.ranger.bmaterials.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.sapi2.SapiAccountManager;
import com.baidu.sapi2.SapiConfiguration;
import com.baidu.sapi2.utils.enums.BindType;
import com.baidu.sapi2.utils.enums.Domain;
import com.baidu.sapi2.utils.enums.LoginShareStrategy;
import com.baidu.sapi2.utils.enums.Switch;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.install.BackAppListener;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.work.FutureTaskManager;

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

		ExecutorService threadPool = Executors.newCachedThreadPool();
		Runnable ar = AppCache.getInstance().onCreate();
		threadPool.execute(ar);
		PackageHelper.addDownloadProgressListener();

		ImageLoaderHelper.config();
		NetUtil.getInstance();
		BackAppListener.getInstance().onCreate();

		Runnable csr = checkSdcard();
		if (csr != null)
			threadPool.execute(csr);
		Runnable stlr = submitIncompletedTasks();
		if (stlr != null)
			threadPool.execute(stlr);

		FrontiaApplication.initFrontiaApplication(this);
	}

	private Runnable submitIncompletedTasks() {
		if (DeviceUtil.isNetworkAvailable(this)) {
			return new Runnable() {
				@Override
				public void run() {
					FutureTaskManager.getInstance()
							.submitIncompleteIfNecessary();
				}
			};
		}
		return null;
	}

	private Runnable checkSdcard() {
		String status = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(status)) {
			CustomToast.showToast(this, getString(R.string.sdcard_unmounted));
		} else {
			final Handler h = new Handler();
			return new Runnable() {
				@Override
				public void run() {
					final long usableSpace = DeviceUtil.getUsableSpace();

					h.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (usableSpace < 20 * 1024 * 1024) {
								CustomToast.showToast(GameTingApplication.this,
										getString(R.string.sdcard_lack_space));
							}
						}
					});
				}
			};
		}
		return null;
	}

	public static GameTingApplication getAppInstance() {
		return mInstance;
	}

}
