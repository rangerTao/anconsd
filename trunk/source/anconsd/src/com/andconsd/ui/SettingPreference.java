package com.andconsd.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.andconsd.R;
import com.andconsd.adapter.SimpleFolderListAdapter;
import com.andconsd.statictis.ClickStatictis;
import com.andconsd.utils.AndConstants;
import com.andconsd.utils.Constants;
import com.andconsd.utils.SharePreferenceUtil;
import com.baidu.mobstat.StatService;

public class SettingPreference extends SherlockPreferenceActivity implements OnItemClickListener {

	public static int THEME = R.style.Theme_Sherlock;

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
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		addPreferencesFromResource(R.xml.setting_preference);

		Preference preFolder = findPreference("pre_folder");
		String cache_dir = SharePreferenceUtil.getInstance(getApplicationContext()).getString(SharePreferenceUtil.SP_CACHE_DIR);
		if (cache_dir != null && !cache_dir.equals(""))
			preFolder.setSummary(cache_dir);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 显示目录现则窗口
	 */
	private View content;
	private ListView lvfolder;
	private static String base_path = Environment.getExternalStorageDirectory().getAbsolutePath();
	AlertDialog folderDialog;
	
	private void showPicFolderDialog(String path) {

		String title = path;

		content = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_folderpicker, null);

		lvfolder = (ListView) content.findViewById(R.id.lvFolders);

		initFolderList(base_path);

		lvfolder.setOnItemClickListener(this);

		if (folderDialog == null) {
			folderDialog = new AlertDialog.Builder(this).setView(content).setTitle(title)
					.setNegativeButton(R.string.button_negative, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).setPositiveButton(R.string.button_folder_pick_positive, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							onDilaogConfirm(base_path);
						}
					}).create();
		}
		
		folderDialog.show();

	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == 0) {
			folderDialog.setTitle("/");
			initFolderList("/");
		} else if (arg2 == 1) {
			File parent = new File(base_path).getParentFile();
			if (parent != null) {
				folderDialog.setTitle(parent.getAbsolutePath());
				base_path = parent.getAbsolutePath();
				initFolderList(parent.getAbsolutePath());
			}

		} else {
			base_path = files.get(arg2).path;
			folderDialog.setTitle(files.get(arg2).path);
			initFolderList(base_path);
		}

	}

	/**
	 * 获取目录列表
	 * @param base
	 */
	ArrayList<FolderItem> files;
	SimpleFolderListAdapter sfla;
	
	private void initFolderList(String base) {

		files = new ArrayList<FolderItem>();
		File basePath = new File(base);

		files.add(new FolderItem(".", "."));
		if (basePath.getParentFile() != null)
			files.add(new FolderItem("..", ".."));
		if (basePath != null && basePath.exists()) {
			try {
				for (File file : basePath.listFiles()) {
					if (file.isDirectory()) {
						FolderItem fi = new FolderItem();
						fi.name = file.getName();
						fi.path = file.getAbsolutePath();
						files.add(fi);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			return;
		}

		sfla = new SimpleFolderListAdapter(files, this);

		lvfolder.setAdapter(sfla);

		sfla.notifyDataSetChanged();
	}
	
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		if (preference.getKey().equals("pre_folder")) {
			showPicFolderDialog(Environment.getExternalStorageDirectory().getAbsolutePath());
		}

		if (preference.getKey().equals("clear_cache")) {
			if (loadingDialog == null) {
				View loading = View.inflate(this, R.layout.pb_loading, null);
				TextView tvHint = (TextView) loading.findViewById(R.id.tv_loading_hint);
				tvHint.setText("清理中，请稍候....");
				loadingDialog = new AlertDialog.Builder(this).setView(loading).create();
			}

			loadingDialog.show();

			Message msg = new Message();
			msg.what = CLEAR_CACHE;
			mHandler.sendMessage(msg);
		}

		if (preference.getKey().equals("feedback")) {
			ClickStatictis.addFeedBackClick(getApplicationContext());
			Intent feedIntent = new Intent(getApplicationContext(), FeedBackActivity.class);
			startActivity(feedIntent);
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

	@SuppressWarnings("deprecation")
	private void onDilaogConfirm(String result) {
		if (result != null && !result.equals("")) {

			StatService.onEvent(getApplicationContext(), Constants.EVENT_CHANGE_DIR, "Cache dir changed");

			Preference preFolder = findPreference("pre_folder");
			preFolder.setSummary(result);
			SharePreferenceUtil.getInstance(getApplicationContext()).saveString(SharePreferenceUtil.SP_CACHE_DIR, result + "/");
		}
	}

	public class FolderItem {

		public FolderItem() {
		}

		public FolderItem(String name, String path) {
			this.name = name;
			this.path = path;
		}

		public String name;
		public String path;
	}
}
