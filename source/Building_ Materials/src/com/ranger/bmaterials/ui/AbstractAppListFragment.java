package com.ranger.bmaterials.ui;

import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.ranger.bmaterials.adapter.AbstractListAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.BaseAppInfo;

public abstract class AbstractAppListFragment<T /*extends BaseAppInfo*/> extends Fragment implements
		LoaderManager.LoaderCallbacks<List<T>>,
		AdapterView.OnItemClickListener ,
		AbstractListAdapter.OnListItemClickListener{
	boolean debug = true ;

	public boolean checkSdCard()
    {
        boolean ret = true;
        
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            ret = false;
            CustomToast.showToast(getActivity(), "请检查您的SD卡");
        }
        
        return ret;
    }

	protected AbstractListAdapter<T> mAdapter;

	//abstract void showAppInfo(String name, String packageName);

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * if
		 * (ApplicationsReceiver.getInstance().isContextChanged
		 * (KEY_LISTENER)) { Bundle args = new Bundle();
		 * getLoaderManager().restartLoader(0, args, this); }
		 */
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			/*if(mAdapter != null){
				mAdapter.clear();
			}*/
			//finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	//LoaderManager.LoaderCallbacks
	@Override
	public abstract Loader<List<T>> onCreateLoader(int id,
			Bundle args);
	//LoaderManager.LoaderCallbacks
	@Override
	public void onLoadFinished(Loader<List<T>> loader,
			List<T> data) {
		//if (Constants.DEBUG)Log.i(this.getClass().getSimpleName(), "## onLoadFinished " +data.size());
		loading(false);
		// Set the new data in the adapter.
		mAdapter.setData(data);
		
	}
	//LoaderManager.LoaderCallbacks
	@Override
	public void onLoaderReset(Loader<List<T>> loader) {
		if (Constants.DEBUG)Log.i(this.getClass().getSimpleName(), "## onLoaderReset "+loader);
		loading(false);
		// Clear the data in the adapter.
		mAdapter.setData(null);
	}

	protected void loading(boolean loading) {
    }
	
	
	//AppListAdapter.OnListItemClickListener callback
	@Override
	public void onItemIconClick(View view,int position) {
		
	}
	//AppListAdapter.OnListItemClickListener callback
	@Override
	public void onItemButtonClick(View view,int position) {
		
	}
}