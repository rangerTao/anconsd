package com.ranger.bmaterials.app;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.ui.CustomToast;

public class BMApplication extends Application {

	private static BMApplication mInstance = null;

	public static final String ACTION_RECEIVED_ACCOUNT = "com.baidu.intent.action.RECEIVED_ACCOUNT";

	private static final String CUSTOM_THEME_URL = "file:///android_asset/sapi_theme/style.css";

    private static int lastProvinceID = 0;
    private static String lastProvinceName = "全国";

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

	public static BMApplication getAppInstance() {
		return mInstance;
	}

    public int getSelectedProvince(){
        return lastProvinceID;
    }

    public String getSelectedProvinceName() {
        return lastProvinceName;
    }

    public void setSelectedProvince(int pid,String name){
        lastProvinceID = pid;
        lastProvinceName = name;
    }
}
