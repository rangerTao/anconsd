package com.ranger.bmaterials.view.slideexpand;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ExpandableListView extends ListView {

	private OnActionClickListener listener;

	private int[] buttonIds = null;

	private ExpandableListAdapter adapter;

	public ExpandableListView(Context context) {
		super(context);
	}

	public ExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setItemActionListener(OnActionClickListener listener,
			int... buttonIds) {
		this.listener = listener;
		this.buttonIds = buttonIds;
	}

	/**
	 * Collapses the currently open view.
	 * 
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if (adapter != null) {
			return adapter.collapseLastOpen();
		}
		return false;
	}

	/**
	 * Registers a OnItemClickListener for this listview which will expand the
	 * item by default. Any other OnItemClickListener will be overriden.
	 * 
	 * To undo call setOnItemClickListener(null)
	 * 
	 * Important: This method call setOnItemClickListener, so the value will be
	 * reset
	 */
	// public void enableExpandOnItemClick() {
	// this.setOnItemClickListener(new OnItemClickListener() {
	// @Override
	// public void onItemClick(AdapterView<?> adapterView, View view, int i,
	// long l) {
	// ExpandableListAdapter adapter = (ExpandableListAdapter)getAdapter();
	// adapter.getExpandToggleButton(view).performClick();
	// }
	// });
	// }

	@Override
	public Parcelable onSaveInstanceState() {
		return adapter.onSaveInstanceState(super.onSaveInstanceState());
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof ExpandableListAdapter.SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		ExpandableListAdapter.SavedState ss = (ExpandableListAdapter.SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		adapter.onRestoreInstanceState(ss);
	}

	@Override
	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);		
	}	
	
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		// TODO Auto-generated method stub
//		super.onLayout(changed, l, t, r, b);
//		
//
//		ListAdapter listAdapter = this.getAdapter();
//		if (listAdapter == null) {
//			return;
//		}
//		int totalHeight = 0;
//		for (int i = 0; i < listAdapter.getCount(); i++) {
//			View listItem = listAdapter.getView(i, null, this);
//			listItem.measure(0, 0);
//			totalHeight += listItem.getMeasuredHeight();
//		}
//		ViewGroup.LayoutParams params = this.getLayoutParams();
//		params.height = totalHeight
//				+ (this.getDividerHeight() * (listAdapter.getCount() - 1))
//				+ 15;
//		this.setLayoutParams(params);
//	}

	/**
	 * Interface for callback to be invoked whenever an action is clicked in the
	 * expandle area of the list item.
	 */
	public interface OnActionClickListener {
		/**
		 * Called when an action item is clicked.
		 * 
		 * @param itemView
		 *            the view of the list item
		 * @param clickedView
		 *            the view clicked
		 * @param position
		 *            the position in the listview
		 */
		public void onClick(View itemView, View clickedView, int position);
	}

	public void setAdapter(ListAdapter adapter) {

		ListAdapter myAdapter = new WrapperListAdapterImpl(adapter) {

			@Override
			public View getView(final int position, View view,ViewGroup viewGroup) {
				final View listView = wrapped.getView(position, view, viewGroup);
				// add the action listeners
				if (buttonIds != null && listView != null) {
					for (int id : buttonIds) {
						View buttonView = listView.findViewById(id);
						if (buttonView != null) {
							buttonView.findViewById(id).setOnClickListener(
									new OnClickListener() {
										@Override
										public void onClick(View view) {
											if (listener != null) {
												listener.onClick(listView,view, position);
											}
										}
									});
						}
					}
				}
				return listView;
			}
		};
		this.adapter = new ExpandableListAdapter(myAdapter);
		// this.adapter = new ExpandableListAdapter(adapter);
		super.setAdapter(this.adapter);
	}
}
