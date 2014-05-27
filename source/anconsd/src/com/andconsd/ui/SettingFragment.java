package com.andconsd.ui;

import java.io.File;

import com.andconsd.R;
import com.andconsd.listener.DialogFragmentCallback;
import com.andconsd.statictis.ClickStatictis;
import com.andconsd.utils.AndConstants;
import com.andconsd.utils.Constants;
import com.andconsd.utils.SharePreferenceUtil;
import com.baidu.mobstat.StatService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;

public class SettingFragment extends PreferenceFragment implements DialogFragmentCallback {

	Dialog loadingDialog;

	private static final int CLEAR_CACHE = 1;

	private Handler mHandler = new Handler() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLEAR_CACHE:
				File cacheFolder = new File(Constants.IMAGE_CACHE);
				if (!cacheFolder.exists())
					return;

				File[] caches = cacheFolder.listFiles();
				for (File cache : caches) {
					if (cache.exists())
						cache.delete();
				}

				try {
					Thread.sleep(5 * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				loadingDialog.dismiss();
				loadingDialog = null;
				break;

			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.setting_preference);

		Preference preFolder = findPreference("pre_folder");
		String cache_dir = SharePreferenceUtil.getInstance(getActivity().getApplicationContext()).getString(SharePreferenceUtil.SP_CACHE_DIR);
		if (cache_dir != null && !cache_dir.equals(""))
			preFolder.setSummary(cache_dir);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		if (preference.getKey().equals("pre_folder")) {
			PicFolderDialogActivity folder_picker = PicFolderDialogActivity.newInstance(Environment.getExternalStorageDirectory().getAbsolutePath());
			folder_picker.show(getFragmentManager(), "");
			folder_picker.setDialogCallback(this);
		}

		if (preference.getKey().equals("clear_cache")) {
			if (loadingDialog == null) {
				View loading = View.inflate(getActivity(), R.layout.pb_loading, null);
				TextView tvHint = (TextView) loading.findViewById(R.id.tv_loading_hint);
				tvHint.setText("清理中，请稍候....");
				loadingDialog = new AlertDialog.Builder(getActivity()).setView(loading).create();
			}

			loadingDialog.show();

			Message msg = new Message();
			msg.what = CLEAR_CACHE;
			mHandler.sendMessage(msg);
		}

		if (preference.getKey().equals("feedback")) {
			ClickStatictis.addFeedBackClick(getActivity().getApplicationContext());
			Intent feedIntent = new Intent(getActivity(), FeedBackActivity.class);
			startActivity(feedIntent);
		}

		if (preference.getKey().equals("autoplay_timeout")) {
			PicFolderDialogActivity folder_picker = PicFolderDialogActivity.newInstance("/sdcard");
			folder_picker.show(getFragmentManager(), "");
			folder_picker.setDialogCallback(this);
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case AndConstants.REQUEST_FOLDER_PICK:

			if (resultCode != 0) {
				break;
			}

			Preference preFolder = findPreference("pre_folder");

			String folder = data.getStringExtra("folder");
			if (folder != null && !folder.equals("")) {
				preFolder.setSummary(data.getStringExtra("folder"));
			}

			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDialogDismiss() {

	}

	@Override
	public void onDilaogConfirm(String result) {
		if (result != null && !result.equals("")) {

			StatService.onEvent(getActivity().getApplicationContext(), Constants.EVENT_CHANGE_DIR, "Cache dir changed");

			Preference preFolder = findPreference("pre_folder");
			preFolder.setSummary(result);
			SharePreferenceUtil.getInstance(getActivity().getApplicationContext()).saveString(SharePreferenceUtil.SP_CACHE_DIR, result + "/");
		}
	}

}
