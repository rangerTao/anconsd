package com.andconsd.framework.utils;

import android.util.SparseArray;

import com.andconsd.framework.json.JSONManager;
import com.andconsd.framework.net.IHttpInterface;
import com.andconsd.framework.net.INetListener;
import com.andconsd.framework.net.NetManager;
import com.andconsd.framework.net.response.BaseResult;

public class NetUtil implements INetListener {
	/** 单例 */
	private static NetUtil mInstance;
	private IHttpInterface mHttpIml;

	/** 存储Listener */
	private SparseArray<IRequestListener> mObservers = new SparseArray<IRequestListener>();

	/** 当前requestId */
	private int mCurrentRequestId;

	/**
	 * 构造器
	 */
	private NetUtil() {
		mHttpIml = NetManager.getHttpConnect();
	}

	public static NetUtil getInstance() {
		if (mInstance == null) {
			mInstance = new NetUtil();
		}

		return mInstance;
	}

	private void removeObserver(int key) {
		mObservers.remove(key);
	}

	private void addObserver(int key, IRequestListener observer) {
		mObservers.put(key, observer);
	}

	// 取消请求
	public void cancelRequestById(int requestId) {
		mHttpIml.cancelRequestById(requestId);
	}

	public int uploadFeedBack(String feedback, IRequestListener observer) {
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_FEEDBACK, Constants.SERVER_FEEDBACK, JSONManager.getJsonBuilder()
					.createFeedBackJson(feedback).toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}

	public int requestForPicList(String url, IRequestListener observer) {
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_BEAUTY, url, JSONManager.getJsonBuilder().createJsonObject().toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}
	
	public int requestForPicListOfPage(String url, int page, IRequestListener observer) {
		
		if (page > 1) {
			String newUrl = url + "?page=" + page;
			return requestForPicList(newUrl, observer);
		} else {
			return 0;
		}
	}
	
	public int requestForRelaxList(String url, IRequestListener observer) {
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_RELAX, url, JSONManager.getJsonBuilder().createJsonObject().toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}
	
	public int requestForMoreRelaxList(String url, int page, IRequestListener observer) {

		if (page > 1) {
			String newUrl = url + "?page=" + page;
			return requestForRelaxList(newUrl, observer);
		} else {
			return 0;
		}
	}
	
	public int requestForGameList(String url, IRequestListener observer) {
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_GAME, url, JSONManager.getJsonBuilder().createJsonObject().toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}
	
	public int requestForMoreGameList(String url, int page, IRequestListener observer) {

		if (page > 1) {
			String newUrl = url + "?page=" + page;
			return requestForRelaxList(newUrl, observer);
		} else {
			return 0;
		}
	}
	
	public int requestForCarList(String url, IRequestListener observer) {
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_CAR, url, JSONManager.getJsonBuilder().createJsonObject().toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}
	
	public int requestForMoreCarList(String url, int page, IRequestListener observer) {

		if (page > 1) {
			String newUrl = url + "?page=" + page;
			return requestForCarList(newUrl, observer);
		} else {
			return 0;
		}
	}
	
	public int requestForFamousList(String url, IRequestListener observer) {
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_FAMOUS, url, JSONManager.getJsonBuilder().createJsonObject().toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}
	
	public int requestForBaiduBeauty(String url,int page, IRequestListener observer){
		
		String baidu_beauty = "http://pic.baidu.com/data/imgs?col=美女&tag=全部&sort=0&tag3=&pn=";
		String append_url = "&rn=60&p=channel&from=1";
		try {
			mCurrentRequestId = mHttpIml.sendRequest(Constants.TAG_FAMOUS, baidu_beauty + (60 * page) + append_url, JSONManager.getJsonBuilder().createJsonObject().toString(), this);
			addObserver(mCurrentRequestId, observer);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCurrentRequestId;
	}
	
	public int requestForMoreFamousList(String url, int page, IRequestListener observer) {

		if (page > 1) {
			String newUrl = url + "?page=" + page;
			return requestForFamousList(newUrl, observer);
		} else {
			return 0;
		}
	}

	// ----------------------------------INetListener接口实现---------------------------------------------
	@Override
	public void onNetResponse(int requestTag, BaseResult responseData, int requestId) {
		// TODO Auto-generated method stub
		IRequestListener _listener = mObservers.get(requestId);
		if (_listener != null) {
			responseData.setRequestID(requestId);
			_listener.onRequestSuccess(responseData);
		}
		removeObserver(requestId);
	}

	@Override
	public void onDownLoadStatus(DownLoadStatus status, int requestId) {
	}

	@Override
	public void onDownLoadProgressCurSize(long curSize, long totalSize, int requestId) {
	}

	@Override
	public void onNetResponseErr(int requestTag, int requestId, int errorCode, String msg) {
		IRequestListener _listener = (IRequestListener) mObservers.get(requestId);
		if (_listener != null) {
			_listener.onRequestError(requestTag, requestId, errorCode, msg);
		}
		removeObserver(requestId);
	}

	// ----------------------------------------END----------------------------------------------

	/**
	 * 请求回调接口
	 */
	public interface IRequestListener {

		void onRequestSuccess(BaseResult responseData);

		void onRequestError(int requestTag, int requestId, int errorCode, String msg);

	}
}
