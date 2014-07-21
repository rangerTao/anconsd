package com.ranger.bmaterials.view;

import java.util.List;

import com.ranger.bmaterials.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 此类用于对下拉刷机listview的简单封装，在setScrollBottomCallBack中加載下一頁
 * 
 * @author zhangxiaofeng
 * 
 * @param <T>
 */
public class SimpleListView<T> extends ListView implements android.widget.AbsListView.OnScrollListener

{
	private boolean mLastItemVisible;
	private loadMoreDataCallBack callbackInterface;
	private LayoutInflater inflater;
	private View mFooterView;
	public static final byte STATUS_LOADING = 0;
	public static final byte STATUS_NO_DATA = 1;
	public static final byte STATUS_NO_MORE = 2;
	public static final byte STATUS_NO_GONE = 3;
	private SimpleAdapter simpleAdapter;
	private BindViewCallBack<T> bindViewCallBack;
	private OnNoneMoreDataCallBack onNoneMoreDataCallBack;
	private int itemId;

	public static class SimpleHolder {
		public View v1, v2, v3, v4, v5, v6, v7, v8;
	}

	public SimpleListView(Context context) {
		super(context);
		init(context);
	}

	public SimpleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SimpleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		inflater = LayoutInflater.from(context);
		setOnScrollListener(this);
	}

	public void setList(int itemId, List<T> list, BindViewCallBack<T> bindViewCallBack) {
		this.itemId = itemId;
		simpleAdapter = new SimpleAdapter(list);
		this.bindViewCallBack = bindViewCallBack;
		addFooterView();
		setAdapter(simpleAdapter);
		setFooterStatus(STATUS_NO_GONE);
	}

	class SimpleAdapter extends BaseAdapter {
		private List<T> list;

		public SimpleAdapter(List<T> list) {
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public T getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SimpleHolder tag = null;
			if (convertView == null) {
				convertView = inflater.inflate(itemId, null);
				if (bindViewCallBack != null) {
					convertView.setTag(bindViewCallBack.onBindViewTag(convertView));
				}
			}
			tag = (SimpleHolder) convertView.getTag();
			if (bindViewCallBack != null && tag != null) {
				bindViewCallBack.onBindViewCallBack(convertView, getItem(position), tag);
			}
			return convertView;
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mLastItemVisible && scrollState == SCROLL_STATE_IDLE) {
			if (callbackInterface != null) {
				setFooterStatus(STATUS_LOADING);
				callbackInterface.onScrollBottomCallBack();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
	}

	public interface loadMoreDataCallBack {
		void onScrollBottomCallBack();
	}
/**
 * 当滑动至底部时的回调，重写onScrollBottomCallBack方法，用于加载下一页数据
 * @param callbackInterface
 */
	public void setScrollBottomCallBack(loadMoreDataCallBack callbackInterface) {
		this.callbackInterface = callbackInterface;

	}

	private void addFooterView() {
		mFooterView = inflater.inflate(R.layout.item_loading_bottom_game_list, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout footerParent = new RelativeLayout(getContext());
		footerParent.addView(mFooterView, lp);
		addFooterView(footerParent);
	}

	public void setFooterStatus(byte status) {
		if (STATUS_NO_GONE == status) {
			mFooterView.setVisibility(View.GONE);
			return;
		}

		mFooterView.setVisibility(View.VISIBLE);
		TextView tv = (TextView) mFooterView.findViewById(R.id.loading_text);
		ProgressBar progress = (ProgressBar) mFooterView.findViewById(R.id.loading_progress);

		if (STATUS_NO_MORE == status) {
			if (null != tv) {
				if(onNoneMoreDataCallBack!=null){
					tv.setText(onNoneMoreDataCallBack.onNoreMoreDataPrompt());
				}else{
					tv.setText(R.string.no_more_data_tip);
				}
			}

			if (null != progress) {
				progress.setVisibility(View.GONE);
			}

			tv.setTextColor(getResources().getColor(R.color.no_more_data_text));
			
			if(onNoneMoreDataCallBack!=null){
				tv.setOnClickListener(onNoneMoreDataCallBack.onClickListener());
			}
		} else if (STATUS_LOADING == status) {
			if (null != tv) {
				tv.setText(R.string.pull_to_refresh_refreshing_label);
			}

			if (null != progress) {
				progress.setVisibility(View.VISIBLE);
			}

			 tv.setOnClickListener(null);
		} else if (STATUS_NO_DATA == status) {
			if (null != tv) {
				tv.setText(R.string.pull_to_refresh_from_bottom_pull_label);
			}

			if (null != progress) {
				progress.setVisibility(View.GONE);
			}
			 tv.setOnClickListener(null);
		} else {
			// RESERVED
		}
	}

	public void notifyDataSetChanged() {
		if (simpleAdapter != null) {
			simpleAdapter.notifyDataSetChanged();
		}
	}

	public interface BindViewCallBack<T> {
		/**
		 * 绑定数据
		 * @param convertView
		 * @param item
		 * @param tag
		 */
		void onBindViewCallBack(View convertView, T item, SimpleHolder tag);
		/**
		 * 绑定convertView与SimpleHolder
		 * @param convertView
		 * @return
		 */
		SimpleHolder onBindViewTag(View convertView);
	}
	public interface OnNoneMoreDataCallBack{
		/**
		 * 当没有更多数据时的提示语对应的点击事件
		 * @return
		 */
		OnClickListener onClickListener();
		/**
		 * 当没有跟多数据时的提示语
		 * @return
		 */
		String onNoreMoreDataPrompt();
		
	}
	/**
	 * 可选方法，不调用则会使用默认值
	 * @param onNoneMoreDataCallBack
	 */
	public void setOnNoneMoreDataCallBack(OnNoneMoreDataCallBack onNoneMoreDataCallBack){
		this.onNoneMoreDataCallBack=onNoneMoreDataCallBack;
	}
}
