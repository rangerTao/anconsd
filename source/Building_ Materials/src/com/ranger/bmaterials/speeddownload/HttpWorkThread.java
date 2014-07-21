package com.ranger.bmaterials.speeddownload;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import android.content.Context;
import android.util.Log;

public class HttpWorkThread extends Thread {
	
	private static final String TAG = "SpeedDownWorkTH";
	private static final String ALL_PATTERN = "*";
	private static final String SPEEDDOWN_PATTERN = "/sendintent*";
	
	private Context context = null;
	
	public BasicHttpProcessor httproc;
	public HttpService httpserv = null;
	private BasicHttpContext httpContext = null;
	private HttpRequestHandlerRegistry reg = null;
	
	Socket soket = null;
	
	public HttpWorkThread(Context ctx, Socket soket, String threadName) {
		
		this.setContext(ctx);
		this.soket = soket;
		this.setName(threadName);
		httproc = new BasicHttpProcessor();
		httpContext = new BasicHttpContext();
		
		httproc.addInterceptor(new ResponseDate());
	    httproc.addInterceptor(new ResponseServer());
	    httproc.addInterceptor(new ResponseContent());
	    httproc.addInterceptor(new ResponseConnControl());
	    
	    httpserv = new HttpService(httproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
	    
	    reg = new HttpRequestHandlerRegistry();
		reg.register(ALL_PATTERN, new HomeCommandHandler(ctx));
		reg.register(SPEEDDOWN_PATTERN, new SpeedDownCommandHandler(ctx));
		httpserv.setHandlerResolver(reg);
	}

	public void run(){
		DefaultHttpServerConnection httpserver = new DefaultHttpServerConnection();
		try {
			httpserver.bind(this.soket, new BasicHttpParams());
			httpserv.handleRequest(httpserver, httpContext);
		} catch (IOException e) {
			Log.e(TAG,"Exception in HttpWorkThread.java:can't bind");
			e.printStackTrace();
		} catch (HttpException e) {
			Log.e(TAG,"Exception in HttpWorkThread.java:handle request");
			e.printStackTrace();
		} catch (Exception exce){
			Log.e(TAG,"debug : error again !");
			exce.printStackTrace();
		} finally {
			try {
				soket.close();
				httpserver.close();
			} catch (IOException e) {
				Log.e(TAG,"Excetion in HttpWorkThread.java:can't shutdown");
				e.printStackTrace();
			}
		}
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
}