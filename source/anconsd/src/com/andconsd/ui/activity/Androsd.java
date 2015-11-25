package com.andconsd.ui.activity;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.andconsd.framework.actionbarsherlock.view.Menu;
import com.andconsd.framework.actionbarsherlock.view.MenuItem;
import com.andconsd.framework.actionbarsherlock.view.SubMenu;
import com.andconsd.AndApplication;
import com.andconsd.R;
import com.andconsd.adapter.ImageThumbAdapter;
import com.andconsd.framework.barcode.EncodingHandler;
import com.andconsd.framework.http.RequestListenerThread;
import com.andconsd.framework.net.response.BaseResult;
import com.andconsd.framework.statictis.ClickStatictis;
import com.andconsd.framework.template.UiTemplate;
import com.andconsd.ui.widget.SettingPreference;
import com.andconsd.framework.utils.AndconsdUtils;
import com.andconsd.framework.utils.Constants;
import com.andconsd.framework.utils.FileHelper;
import com.andconsd.framework.utils.NetUtil.IRequestListener;
import com.andconsd.framework.utils.SharePreferenceUtil;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobstat.StatService;
import com.google.zxing.WriterException;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class Androsd extends BaseActivity implements AdViewListener, OnClickListener, IRequestListener{

	public static Androsd appref;
	public static int THEME = R.style.Theme_Sherlock;
	public static final int STOP_SERVICE = 999;

	private static String cache_dir;

	RecyclerView pictureListView;

	String ip = "";
	GridView gl;
	ToggleButton btnService;
	TextView tvIp;
	ImageView ivHelp;
	TextView tvHelp;
	
	ImageView ivBarCode;

	// AlertDialog
	Button btnCheck;
	ProgressBar pbCheckIp;
	TextView tvCheckResult;
	EditText etIpEditText;

	AlertDialog ad;

	String wifiLockString = "Androsd";

	ImageThumbAdapter fa;

	WifiManager wifiManager;
	WifiLock wifiLock;

	Thread t;

	PopupWindow popupDelete;
	View controler;

	AdView baiduAD;
	
	SlidingMenu menu;

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case STOP_SERVICE:

				if (t != null) {
					t.interrupt();
				}
				stopServerSafty();

				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		setTheme(THEME);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		getSupportActionBar().setDisplayShowHomeEnabled(true);

		initSlidingMenu();
		
		init();

		baiduAD = (AdView) findViewById(R.id.baiduad);

		baiduAD.setListener(this);
		
		initPushService();
	}
	
	
	/**
	 * Launch the push service.
	 */
	private void initPushService() {

		PushConstants.restartPushService(getApplicationContext());
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Constants.API_KEY);
	}


	private void initSlidingMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int offset = (dm.widthPixels / 3) * 2;
		menu.setBehindOffset(offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.side_menu);
		menu.showMenu();
		
		findViewById(R.id.menu_beauty).setOnClickListener(this);
		findViewById(R.id.menu_relax).setOnClickListener(this);
		findViewById(R.id.menu_game).setOnClickListener(this);
		findViewById(R.id.menu_car).setOnClickListener(this);
		findViewById(R.id.menu_famous).setOnClickListener(this);
		findViewById(R.id.menu_promotion).setOnClickListener(this);
		findViewById(R.id.menu_topics).setOnClickListener(this);
		findViewById(R.id.menu_topics).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		fa.notifyDataSetChanged();
		super.onResume();
	}

	/**
	 * Init the all apk.
	 */
	private void init() {

		controler = getLayoutInflater().inflate(R.layout.control_pic_thumb_list, null);

		getSupportActionBar().setCustomView(controler);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		new UiTemplate(this);
		appref = this;

		// Init the gridview.
		initPicGrid();

		// Init the service controller.
		initController();

		String dir = SharePreferenceUtil.getInstance(getApplicationContext()).getString(SharePreferenceUtil.SP_CACHE_DIR);
		if (dir != null && !dir.equals("")) {
			cache_dir = dir;
		} else {
			cache_dir = Constants.ROOT_DIR;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu sub = menu.addSubMenu("更多");
		sub.add(0, Constants.OPTION_MENU_SETTING, 0, "设置");
		sub.add(0, Constants.OPTION_MENU_HELP, 0, "使用说明");

		MenuItem subMenuItem = sub.getItem();
		subMenuItem.setIcon(R.drawable.ic_title_more_menu);
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case Constants.OPTION_MENU_HELP:

			View view = LayoutInflater.from(appref).inflate(R.layout.howtouse, null);
			ad = new AlertDialog.Builder(appref).setView(view).setNegativeButton(R.string.btnClose, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}

			}).create();

			ad.show();

			break;
		case Constants.OPTION_MENU_SETTING:

			Intent settingIntent = new Intent(this, SettingPreference.class);
			startActivityForResult(settingIntent, 1);

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Init the service controller.
	 */
	private void initController() {

		btnService = (ToggleButton) controler.findViewById(R.id.btnService);
		tvIp = (TextView) controler.findViewById(R.id.tvIpAddress);

		ivBarCode = (ImageView) findViewById(R.id.ivBarcode);
		
		// Set the service button.
		btnService.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {

					StatService.onEvent(getApplicationContext(), Constants.EVENT_HTTP_START, "Server Start");
					try {
						wifiManager = (WifiManager) appref.getSystemService(Context.WIFI_SERVICE);
						if (!wifiManager.isWifiEnabled()) {
							Toast.makeText(appref, "未连接到WIFI", Toast.LENGTH_SHORT).show();
						}
						String ip = getLocalIpAddress();
						Constants.service_running = true;
						if (ip != null) {

							String path = Constants.ROOT_DIR;
							String cache_dir = SharePreferenceUtil.getInstance(Androsd.appref).getString(SharePreferenceUtil.SP_CACHE_DIR);
							if (cache_dir != null && !cache_dir.equals(""))
								path = cache_dir;

							t = new RequestListenerThread(Constants.DEFAULT_PORT, path);
							t.setDaemon(false);

							t.start();

							tvIp.setTextColor(Color.WHITE);
							tvIp.setText(ip + ":" + Constants.DEFAULT_PORT);

						} else {
							Toast.makeText(appref, "network error", Toast.LENGTH_SHORT).show();
						}
						
						ivBarCode.setVisibility(View.VISIBLE);
						try {
							String contentString = "http://" + ip + ":"
									+ Constants.DEFAULT_PORT
									+ "/share/sh.lilith.dgame.DK"
									+ Constants.PREFIX_PACKAGE_DEFAULT;
							if (!contentString.equals("")) {
								Bitmap qrCodeBitmap = EncodingHandler
										.createQRCode(contentString, 350);
								ivBarCode.setImageBitmap(qrCodeBitmap);
							} else {
							}

						} catch (WriterException e) {
							e.printStackTrace();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
						sendStopServiceMessage();
						btnService.setChecked(false);
						Toast.makeText(appref, e.getMessage(), Toast.LENGTH_LONG).show();
					}
				} else {

					sendStopServiceMessage();

					ivBarCode.setVisibility(View.GONE);
				}
			}

		});

	}
	
	private void sendStopServiceMessage() {
		Message msg_stopservice = new Message();
		msg_stopservice.what = STOP_SERVICE;
		handler.sendMessage(msg_stopservice);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Get the local IP address
	 * 
	 * @return IP address
	 */
	public String getLocalIpAddress() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();

					Log.d("TAG","ip :" + inetAddress.getHostAddress().toString());
					
					if (!inetAddress.isLoopbackAddress()
							&& (inetAddress.getHostAddress().toString().startsWith("192")  || inetAddress
									.getHostAddress().toString().startsWith("172"))) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	private void initPicGrid() {
		gl = (GridView) findViewById(R.id.gl);

		fa = new ImageThumbAdapter(Constants.files, gl, ((AndApplication) getApplication()).options);

		gl.setAdapter(fa);

		gl.setPadding(30, 10, 30, 10);

		gl.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String path = Constants.files.get(position).toLowerCase();
				if (path.endsWith(".mp4") || path.endsWith(".rmvb") || path.endsWith(".avi") || path.endsWith(".mkv") || path.endsWith(".rm")) {
					Uri uri = Uri.parse(path);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					String type = "video/" + path.substring(path.lastIndexOf("."));
					intent.setType(type);
					intent.setDataAndType(uri, type);
					try {
						startActivity(intent);
					} catch (Exception e) {
						new AlertDialog.Builder(appref).setTitle(R.string.player_notfound_title).setMessage(R.string.player_tip).setNegativeButton(R.string.btnClose, null)
								.create().show();
					}

				} else {
					Intent intent = new Intent(appref, PicViewer.class);
					intent.putExtra("index", position);
					startActivity(intent);
				}

			}
		});

		gl.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (popupDelete != null && popupDelete.isShowing()) {
					popupDelete.dismiss();
				}
			}
		});

		gl.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

				if (popupDelete != null && popupDelete.isShowing()) {
					popupDelete.dismiss();
				}

				View deleteView = LayoutInflater.from(appref).inflate(R.layout.popupdelete, null);

				popupDelete = new PopupWindow(deleteView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				ImageView ivDelete = (ImageView) deleteView.findViewById(R.id.ivDelete);

				ivDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						File file = new File((String) parent.getAdapter().getItem(position));
						if (AndconsdUtils.deleteFileByName(file.getAbsolutePath())) {

							appref.handler.post(new Runnable() {

								@Override
								public void run() {
									appref.notifyDatasetChanged();
								}

							});
							Toast.makeText(appref, file.getName() + "  " + appref.getString(R.string.deletesuccess), 2000).show();
						}
					}
				});

				popupDelete.showAsDropDown(view, (view.getWidth() / 4) * 3, -view.getHeight());
				return true;
			}
		});
	}

	@Override
	public void onAttachedToWindow() {
		// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		String new_dir = SharePreferenceUtil.getInstance(getApplicationContext()).getString(SharePreferenceUtil.SP_CACHE_DIR);
		if (new_dir != null && !cache_dir.equals(new_dir)) {
			cache_dir = new_dir;
			Constants.ROOT_DIR = cache_dir;
			
			notifyDatasetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			stopServerSafty();
			finish();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void notifyDatasetChanged() {

		setSupportProgressBarIndeterminateVisibility(true);

		Constants.files.clear();
		getAllFilesFromFolder(cache_dir);

		fa.notifyDataSetChanged();
		setSupportProgressBarIndeterminateVisibility(false);
	}
	
	private void getAllFilesFromFolder(String cache) {
		final File rootFile = new File(cache);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}
		File[] filess = rootFile.listFiles();

		if(filess != null){
			for (File temp : filess) {
				if(temp.isDirectory()){
					if(!temp.getName().contains("thumbnail"))
						getAllFilesFromFolder(temp.getAbsolutePath());
				}
				else{
					if(FileHelper.isPicture(temp.getAbsolutePath()))
						Constants.files.add(temp.getAbsolutePath());
				}
			}
		}
	}

	private void stopServerSafty() {

		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {

				try {
					new Socket("127.0.0.1", Constants.DEFAULT_PORT);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();

		tvIp.setText("");
		t = null;

	}

	private boolean checkIp(String ip) {

		String[] ips = ip.split("\\.");
		if (ips.length < 4) {
			return false;
		}

		for (String ipeach : ips) {
			try {
				int ipe = Integer.parseInt(ipeach);
				if (ipe < 0 || ipe > 255) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onAdClick(JSONObject arg0) {
		StatService.onEvent(this, Constants.EVENT_BAIDUAD_CLICK, "ADView onClick");
	}

	@Override
	public void onAdFailed(String arg0) {
		StatService.onEvent(this, Constants.EVENT_BAIDUAD_FAIL, "ADView fail");
	}

	@Override
	public void onAdReady(AdView arg0) {

	}

	@Override
	public void onAdShow(JSONObject arg0) {
		StatService.onEvent(this, Constants.EVENT_BAIDUAD_SHOW, "ADView show");
	}

	@Override
	public void onAdSwitch() {
	}

	@Override
	public void onVideoClickAd() {
	}

	@Override
	public void onVideoClickClose() {
	}

	@Override
	public void onVideoClickReplay() {
	}

	@Override
	public void onVideoError() {
	}

	@Override
	public void onVideoFinish() {
	}

	@Override
	public void onVideoStart() {
	}

	@Override
	public void onClick(View view) {
		
		Intent targetIntent = new Intent(this,PicFlowListActivity.class);
		
		switch (view.getId()) {
		case R.id.menu_beauty:
			ClickStatictis.addMenuItemClick(getApplicationContext(), getString(R.string.menu_beauty));
			targetIntent.putExtra(Constants.INTENT_PICTYPE, Constants.TAG_BEAUTY);
			break;
		case R.id.menu_relax:
			ClickStatictis.addMenuItemClick(getApplicationContext(), getString(R.string.menu_relax));
			targetIntent.putExtra(Constants.INTENT_PICTYPE, Constants.TAG_RELAX);
			break;
		case R.id.menu_game:
			ClickStatictis.addMenuItemClick(getApplicationContext(), getString(R.string.menu_game));
			targetIntent.putExtra(Constants.INTENT_PICTYPE, Constants.TAG_GAME);
			break;
		case R.id.menu_car:
			ClickStatictis.addMenuItemClick(getApplicationContext(), getString(R.string.menu_car));
			targetIntent.putExtra(Constants.INTENT_PICTYPE, Constants.TAG_CAR);
			break;
		case R.id.menu_famous:
			ClickStatictis.addMenuItemClick(getApplicationContext(), getString(R.string.menu_famous));
			targetIntent.putExtra(Constants.INTENT_PICTYPE, Constants.TAG_FAMOUS);
			break;
		case R.id.menu_promotion:
			ClickStatictis.addMenuItemClick(getApplicationContext(), getString(R.string.menu_promotion));
			startActivity(new Intent(this, PromotionActivity.class));
			return;
		case R.id.menu_topics:
			targetIntent.putExtra(Constants.INTENT_PICTYPE, Constants.TAG_BEAUTY_BAIDU);
			break;
		default:
			break;
		}

		menu.toggle();
		startActivity(targetIntent);
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		
	}
}
