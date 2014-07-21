package com.ranger.bmaterials.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;

/** 此类用于显示HTML页面，(必须参数：title，url,arg1_param)如果作为抽奖页，则arg1_param参数，对应值为sweepstakes */
public class WebviewActivity extends BackBaseActivity implements DownloadListener {

	ProgressBar progressBar;
	TextView mTextView;
	String sourceParam;
	private static final String PARAM = "arg1_param";
	private static final String PARAM1 = "sweepstakes";
	private WebView myWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String url = intent.getStringExtra("url");
		if (TextUtils.isEmpty(url)) {
			finish();
			return;
		}
		sourceParam = intent.getStringExtra(PARAM);
		if (PARAM1.equals(sourceParam)) {
			// 抽奖活动,需显示分享功能
			viewSetting();
		}
		progressBar = (ProgressBar) findViewById(R.id.network_loading_pb);
		mTextView = (TextView) findViewById(R.id.id_tv_loadingmsg);
		myWebView = (WebView) findViewById(R.id.webview);

		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.setWebChromeClient(webChromeClient);
		myWebView.setWebViewClient(webViewClient);

		myWebView.setDownloadListener(this);

		myWebView.loadUrl(url);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.img_back:
			onBackPressed();
			break;
		default:
			super.onClick(v);
		}
	}

	@Override
	public int getLayout() {
		return R.layout.layout_webview;
	}

	@Override
	public String getHeaderTitle() {
		return intent.getStringExtra("title");
	}

	void viewSetting() {
		findViewById(R.id.layout_msgedit).setVisibility(View.VISIBLE);
		ImageView img_msgedit = (ImageView) findViewById(R.id.img_msgedit);
		img_msgedit.setImageResource(R.drawable.bt_share_game_detail_selector);
		SharedPreferences sPreferences = getSharedPreferences("startdata", 0);
		setActshare(sPreferences.getString("actshare", getString(R.string.share_content)));
	}

	void jumpToSystemExplorer(String url) {

		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		if (myWebView.canGoBack())
			myWebView.goBack();
		else
			super.onBackPressed();
	}

	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
		jumpToSystemExplorer(url);
	}

	WebChromeClient webChromeClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			// mTextView.setText(newProgress+"");
			progressBar.setProgress(newProgress);
		}
	};
	WebViewClient webViewClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			myWebView.loadUrl(url);
			// return super.shouldOverrideUrlLoading(view, url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			mTextView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			mTextView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
		}
	};
}
