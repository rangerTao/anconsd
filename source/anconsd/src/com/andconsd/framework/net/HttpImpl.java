/**

 * @author huzexin@duoku.com

 * @version CreateData��2012-5-10 3:46:54 PM

 */
package com.andconsd.framework.net;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.andconsd.AndApplication;
import com.andconsd.DcError;
import com.andconsd.R;
import com.andconsd.framework.net.INetListener.DownLoadStatus;
import com.andconsd.framework.net.NetMessage.NetMessageType;
import com.andconsd.framework.net.response.BaseResult;
import com.andconsd.framework.utils.ConnectManager;
import com.andconsd.framework.utils.MyLogger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HttpImpl implements IHttpInterface {

	private MyLogger mLogger = MyLogger.getLogger(HttpImpl.class.getName());
	private ConcurrentHashMap<ASIHttpRequest, INetListener> mRequestQuene;
	private volatile Handler mHandler;

	HttpImpl() {
		mRequestQuene = new ConcurrentHashMap<ASIHttpRequest, INetListener>(10);
		mHandler = new HttpMsgHandler(Looper.getMainLooper());
	}

	public int sendRequest(int requestTag,String url, String bodydata, INetListener listener) {
		
		if (!ConnectManager.isNetworkConnected(AndApplication.getAppInstance())) {
			if (null != listener) {
				listener.onNetResponseErr(0, bodydata.hashCode(), DcError.DC_NETWORK_NOT_AVAILABLE, AndApplication.getAppInstance()
						.getString(R.string.alert_network_inavailble));
			}

			return bodydata.hashCode();
		}

		mLogger.v("requestBody == " + bodydata);
		ASIHttpRequest request = new ASIHttpRequest();
		request.setUrl(url);

		// encrypt
		request.setRequestData(bodydata);
		request.setRequestTag(requestTag);
		request.setTimeOutSeconds(10 * 1000);
		request.setCbkHandler(mHandler);

		// add to task queue
		addTaskToQuene(request, listener);

		return request.hashCode();
	}

	public int sendDownLoadRequest(String url, String filepath, INetListener listener) {

		// create a new request and set request param
		// and then add the request to quene
		ASIHttpRequest request = new ASIHttpRequest();
		request.setUrl(url);
		request.setDownloadDstFilePath(filepath);
		request.setTimeOutSeconds(10 * 1000);
		request.setCbkHandler(mHandler);

		// add to task quene
		addTaskToQuene(request, listener);

		return request.hashCode();
	}

	public void cancelRequestById(int requestId) {

		Iterator<Entry<ASIHttpRequest, INetListener>> iter = mRequestQuene.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<ASIHttpRequest, INetListener> entry = (Entry<ASIHttpRequest, INetListener>) iter.next();

			ASIHttpRequest request = (ASIHttpRequest) entry.getKey();

			// check if current request match specify request id
			if (requestId == request.hashCode()) {

				// set end flag to specify request
				request.cancelRequest();
				// add by wenzutong, 2012-09-04,��mRequestQueue���Ƴ��request����
				mRequestQuene.remove(request);
				// end
				break;
			}
		}
	}

	public void cancelAllRequest() {
		Iterator<Entry<ASIHttpRequest, INetListener>> iter = mRequestQuene.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<ASIHttpRequest, INetListener> entry = (Entry<ASIHttpRequest, INetListener>) iter.next();

			ASIHttpRequest request = (ASIHttpRequest) entry.getKey();

			request.cancelRequest();
		}

		// remove all request in the quene
		mRequestQuene.clear();
	}

	/**
	 * Add task to the task quene
	 * 
	 * @param request
	 * @param listener
	 */
	private void addTaskToQuene(ASIHttpRequest request, INetListener listener) {

		mRequestQuene.put(request, listener);
		// HttpClientHelper.submit(request);
		HttpClientHelper.execute(request);
	}

	/**
	 * Remove task from the task quene
	 * 
	 * @param request
	 */
	private void removeTask(ASIHttpRequest request) {

		boolean _exist = mRequestQuene.containsKey(request);

		if (_exist) {
			mRequestQuene.remove(request);
		}
	}

	/*
	 * @ get net listener by net request id
	 */
	private Entry<ASIHttpRequest, INetListener> findRequestListenerById(int requestid) {

		Entry<ASIHttpRequest, INetListener> resEntry = null;
		Iterator<Entry<ASIHttpRequest, INetListener>> iter = mRequestQuene.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<ASIHttpRequest, INetListener> entry = (Entry<ASIHttpRequest, INetListener>) iter.next();

			ASIHttpRequest request = (ASIHttpRequest) entry.getKey();

			// check if current request match specify request id
			if (requestid == request.hashCode()) {

				resEntry = entry;
				break;
			}
		}

		return resEntry;
	}

	private final class HttpMsgHandler extends Handler {
		public HttpMsgHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			boolean res = msg.obj.getClass().equals(NetMessage.class);
			if (res) {
				NetMessage netmsg = (NetMessage) msg.obj;
				NetMessageType netmsgtype = netmsg.getMessageType();

				switch (netmsgtype) {
				case NetSuccess: {
					doNetSucessResponse(netmsg);
				}
					break;

				case NetFailure: {
					doNetFailureResponse(netmsg);
				}
					break;

				case NetDownloadling: {
					doDownloadProgressResponse(netmsg);
				}
					break;

				case NetDownloadFailure: {
					doDownLoadFailureResponse(netmsg);
				}
					break;

				case NetDownloadSuccess: {
					doDownloadSuccessResponse(netmsg);
				}
					break;

				case NetCancel: {
					doCancelResponse(netmsg);
				}
					break;

				default:
					break;

				}
			}
		}

		private void doNetSucessResponse(NetMessage message) {
			int requestid = message.getRequestId();
			Entry<ASIHttpRequest, INetListener> entry = HttpImpl.this.findRequestListenerById(requestid);

			if (entry != null) {

				INetListener _listener = entry.getValue();
				ASIHttpRequest _request = entry.getKey();
				int _requestid = entry.getKey().hashCode();

				BaseResult res = message.getResponseData();

				if (res == null) {
					int errcode = message.getErrorCode();
					_listener.onNetResponseErr(_request.getRequestTag(), _requestid, errcode, message.getErrorString());

				} else {

					int errcode = res.getErrorcode();
					if (errcode == DcError.DC_OK) {

						_listener.onNetResponse(_request.getRequestTag(), res, _requestid);

					} else {

						_listener.onNetResponseErr(_request.getRequestTag(), _requestid, errcode, res.getErrormsg());
					}
				}
				removeTask(entry.getKey());
			}
		}

		private void doNetFailureResponse(NetMessage message) {
			int requestid = message.getRequestId();
			Entry<ASIHttpRequest, INetListener> entry = HttpImpl.this.findRequestListenerById(requestid);

			if (entry != null) {
				INetListener _listener = entry.getValue();
				ASIHttpRequest _request = entry.getKey();

				// need handle the net error for more detail
				_listener.onNetResponseErr(_request.getRequestTag(), requestid, message.getErrorCode(), message.getErrorString());

				removeTask(entry.getKey());
			}

		}

		private void doDownLoadFailureResponse(NetMessage message) {
			int requestid = message.getRequestId();
			Entry<ASIHttpRequest, INetListener> entry = HttpImpl.this.findRequestListenerById(requestid);

			if (entry != null) {
				INetListener _listener = entry.getValue();
				_listener.onDownLoadStatus(DownLoadStatus.EDlsDownLoadErr, requestid);
				removeTask(entry.getKey());
			}

		}

		private void doDownloadProgressResponse(NetMessage message) {
			int requestid = message.getRequestId();
			Entry<ASIHttpRequest, INetListener> entry = HttpImpl.this.findRequestListenerById(requestid);
			if (entry != null) {
				INetListener _listener = entry.getValue();
				_listener.onDownLoadProgressCurSize(message.getCurentSize(), message.getTotalSize(), requestid);

			}
		}

		private void doDownloadSuccessResponse(NetMessage message) {
			int requestid = message.getRequestId();
			Entry<ASIHttpRequest, INetListener> entry = HttpImpl.this.findRequestListenerById(requestid);

			if (entry != null) {
				INetListener _listener = entry.getValue();
				_listener.onDownLoadStatus(DownLoadStatus.EDlsDownLoadComplete, requestid);
				removeTask(entry.getKey());
			}

		}

		private void doCancelResponse(NetMessage message) {
			int requestid = message.getRequestId();
			Entry<ASIHttpRequest, INetListener> entry = HttpImpl.this.findRequestListenerById(requestid);

			if (entry != null) {

				// do nothing for now
				removeTask(entry.getKey());
			}
		}

	}

}
