package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public abstract class AbstractListAdapter<T /* extends BaseAppInfo */> extends
		BaseAdapter implements OnClickListener {
	public interface OnListItemClickListener {
		void onItemIconClick(View view, int position);

		void onItemButtonClick(View view, int position);
	}

	protected OnListItemClickListener onListItemClickListener;

	public void setOnListItemClickListener(
			OnListItemClickListener onListItemClickListener) {
		this.onListItemClickListener = onListItemClickListener;
	}

	protected Context context;
	protected final LayoutInflater mInflater;

	protected List<T> data;
	private boolean mNotifyOnChange = true;
	private final int[] mLock = new int[0];

	public AbstractListAdapter(Context context) {
		this.context = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = new ArrayList<T>();
	}

	public void setData(List<T> data) {
		this.data = data;
		notifyChanged();
	}

	public List<T> getData() {
		return data;
	}

	@Override
	public int getCount() {
		if (data == null) {
			return 0;
		} else {
			return data.size();
		}
	}

	@Override
	public T getItem(int position) {
		if (data == null || position >= data.size()) {
			return null;
		} else {
			return data.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void add(T item) {

		synchronized (this.mLock) {
			if (this.data != null) {
				this.data.add(item);
				if (this.mNotifyOnChange) {
					notifyChanged();
				}
			}

		}
	}

	private void notifyChanged() {
//		handler.post(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		notifyDataSetChanged();
	}

	private static Handler handler = new Handler();

	public void addAll(Collection<T> collection) {
		synchronized (this.mLock) {
			if (this.data != null) {
				this.data.addAll(collection);
				if (this.mNotifyOnChange)
					notifyChanged();
			}

		}
	}

	public void clear() {
		synchronized (this.mLock) {
			if (this.data != null) {
				this.data.clear();
				if (this.mNotifyOnChange)
					notifyChanged();
			}

		}
	}

	public int getItemViewType(int paramInt) {
		return super.getItemViewType(paramInt);
	}

	public void remove(T mAppList) {
		synchronized (this.mLock) {
			if (this.data != null) {
				this.data.remove(mAppList);
				if (this.mNotifyOnChange)
					notifyChanged();
			}
		}
	}

	public void remove(int position) {
		synchronized (this.mLock) {
			if (this.data != null) {
				this.data.remove(position);
				if (this.mNotifyOnChange)
					notifyChanged();
			}
		}
	}

	public void setNotifyOnChange(boolean paramBoolean) {
		this.mNotifyOnChange = paramBoolean;
	}

	/*
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { return null; }
	 */
	@Override
	public void onClick(View v) {

	}

}
