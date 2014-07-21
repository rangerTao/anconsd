package com.ranger.bmaterials.download.tool;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.tools.ConnectManager;

// Referenced classes of package com.baidu.android.common.net:
//			ConnectManager

public class ProxyHttpClient extends DefaultHttpClient
{

	private static final String TAG = "ProxyHttpClient";
	private static final boolean DEBUG = false;
	private String mProxy;
	private String mPort;
	private boolean mUseWap;
	private static final int HTTP_TIMEOUT_MS = 30000;
	private RuntimeException mLeakedException;

	public ProxyHttpClient(Context context)
	{
		this(context, null, null);
	}

	public ProxyHttpClient(Context context, String s)
	{
		this(context, s, null);
	}

	public ProxyHttpClient(Context context, ConnectManager connectmanager)
	{
		this(context, null, connectmanager);
	}

	public ProxyHttpClient(Context context, String s, ConnectManager connectmanager)
	{
		mLeakedException = new IllegalStateException("ProxyHttpClient created and never closed");
		ConnectManager connectmanager1 = connectmanager;
		if (connectmanager1 == null)
			connectmanager1 = new ConnectManager(context);
		mUseWap = connectmanager1.isWapNetwork();
		mProxy = connectmanager1.getProxy();
		mPort = connectmanager1.getProxyPort();
		if (mUseWap)
		{
			HttpHost httphost = new HttpHost(mProxy, Integer.valueOf(mPort).intValue());
			getParams().setParameter("http.route.default-proxy", httphost);
		}
		HttpConnectionParams.setConnectionTimeout(getParams(), 30000);
		HttpConnectionParams.setSoTimeout(getParams(), 30000);
		HttpConnectionParams.setSocketBufferSize(getParams(), 8192);
		if (!TextUtils.isEmpty(s))
			HttpProtocolParams.setUserAgent(getParams(), s);
	}

	protected void finalize()
		throws Throwable
	{
		super.finalize();
		if (mLeakedException != null)
			Log.e(TAG, "Leak found", mLeakedException);
	}

	public void close()
	{
		if (mLeakedException != null)
		{
			getConnectionManager().shutdown();
			mLeakedException = null;
		}
	}

	public boolean isWap()
	{
		return mUseWap;
	}

	protected HttpParams createHttpParams()
	{
		HttpParams httpparams = super.createHttpParams();
		HttpProtocolParams.setUseExpectContinue(httpparams, false);
		return httpparams;
	}

}
