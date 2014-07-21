package com.ranger.bmaterials.work;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomToast;

public final class LoadingTask {

	private Activity act;
	private ILoading impl;
	public View network_loading_layout;// loading view root
	public View network_loading_pb;// loading view
	public View loading_error_image;// retry view
	private IRequestListener listener;
	private View rootView;

	public LoadingTask(Activity act, ILoading impl) {
		this.act = act;
		this.impl = impl;
	}

	// for fragment
	public void setRootView(View rootView) {
		this.rootView = rootView;
	}

	public AtomicBoolean isLoading = new AtomicBoolean();

	public synchronized void loading() {
		network_loading_layout = findView(R.id.network_loading_layout);
		network_loading_pb = findView(R.id.network_loading_pb);
		loading_error_image = findView(R.id.loading_error_layout);

		if (impl != null) {
			impl.preLoading(network_loading_layout, network_loading_pb,
					loading_error_image);
			listener = impl.getRequestListener();
			if (impl.isAsync()) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						impl.loading(new RequestListenerImpl());
						isLoading.set(true);
					}
				});
				t.setDaemon(true);
				t.start();
			} else {
				impl.loading(new RequestListenerImpl());
				isLoading.set(true);
			}
		}
	}

	private View findView(int id) {
		if (rootView == null)
			return act.findViewById(id);
		else
			return rootView.findViewById(id);
	}

	private class RequestListenerImpl implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			// TODO Auto-generated method stub
			network_loading_layout.setVisibility(View.GONE);
			if (listener != null)
				listener.onRequestSuccess(responseData);
		}

		@Override
		public void onRequestError(int requestTag, int requestId,
				int errorCode, String msg) {
			// TODO Auto-generated method stub
			if (impl.isShowNoNetWorkView()) {
				loading_error_image.setClickable(true);
				loading_error_image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (ConnectManager.isNetworkConnected(act)) {
							impl.loading(RequestListenerImpl.this);
							isLoading.set(true);
							network_loading_pb.setVisibility(View.VISIBLE);
							loading_error_image.setVisibility(View.GONE);
						} else {
							CustomToast.showToast(
									act,
									act.getString(R.string.alert_network_inavailble));
						}
					}
				});

				loading_error_image.setVisibility(View.VISIBLE);
				network_loading_pb.setVisibility(View.GONE);
			}
			if (listener != null)
				listener.onRequestError(requestTag, requestId, errorCode, msg);
		}

	}

	public interface ILoading {
		void preLoading(View network_loading_layout, View network_loading_pb,
				View loading_error_image);

		IRequestListener getRequestListener();

		void loading(IRequestListener listener);

		boolean isShowNoNetWorkView();

		boolean isAsync();
	}
}
