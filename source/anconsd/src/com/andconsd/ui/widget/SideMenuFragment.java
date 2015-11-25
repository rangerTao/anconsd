package com.andconsd.ui.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andconsd.framework.actionbarsherlock.app.SherlockFragment;
import com.andconsd.R;
import com.andconsd.framework.net.response.BaseResult;
import com.andconsd.framework.utils.NetUtil.IRequestListener;

public class SideMenuFragment extends SherlockFragment implements IRequestListener {

	private View menu;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		menu = inflater.inflate(R.layout.side_menu, null);

		return menu;
	}

	public void side_menu_on_click(View view) {

	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		// TODO Auto-generated method stub
		
	}

}
