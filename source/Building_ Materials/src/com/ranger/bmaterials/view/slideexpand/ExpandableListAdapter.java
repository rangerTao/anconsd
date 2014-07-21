package com.ranger.bmaterials.view.slideexpand;

import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;
import java.util.BitSet;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.mode.GameRelatedInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameRelatedResult;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class ExpandableListAdapter extends BaseAdapter implements WrapperListAdapter {
	/**
	 * 最后一次展开的item
	 */
	private View lastOpen = null;
	/**
	 * 最后一次打开的item的position
	 * -1表示当前没有打开的item，否则就是指向当前打开的item
	 */
	private int lastOpenPosition = -1;
	
	/**
	 * 动画默认的执行时间
	 */
	private int animationDuration = 330;
	
	/**
	 * A list of positions of all list items that are expanded.
	 * Normally only one is expanded. But a mode to expand
	 * multiple will be added soon.
	 *
	 * If an item onj position x is open, its bit is set
	 */
	private BitSet openItems = new BitSet();
	/**
	 * 记录每个折叠view的高度。该高度在绘制前就已经计算好，不用自己重新计算。
	 * 所以这里在单项item里不要用gone，用invisible就可以了。
	 */
	private final SparseIntArray viewHeights = new SparseIntArray(10);

	/**
	 * 展开或者折叠按钮的Id
	 */
	private int toggle_button_id;
	/**
	 * 展开后显示的view的id
	 */
	private int expandable_view_id;
	
	protected ListAdapter wrapped;
	
	public ExpandableListAdapter(ListAdapter wrapped) {
		this(wrapped, R.id.expandable_toggle_button, R.id.expandable);
	}

	public ExpandableListAdapter(ListAdapter wrapped, int toggle_button_id, int expandable_view_id) {
		this.wrapped = wrapped;
		this.toggle_button_id = toggle_button_id;
		this.expandable_view_id = expandable_view_id;
	}

	/**
	 * 获得点击按钮的view。用来在getview方法中为它设置onclicklistener
	 * @param parent the list view item
	 */
	public View getExpandToggleButton(View parent) {
		return parent.findViewById(toggle_button_id);
	}

	/**
	 * @see #getExpandToggleButton(View)
	 * @param parent the list view item
	 * @ensure return!=null
	 * @return a child of parent which is a view (or often ViewGroup)
	 *  that can be collapsed and expanded
	 */
	public View getExpandableView(View parent) {
		return parent.findViewById(expandable_view_id);
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return wrapped;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return wrapped.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int i) {
		return wrapped.isEnabled(i);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) {
		wrapped.registerDataSetObserver(dataSetObserver);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
		wrapped.unregisterDataSetObserver(dataSetObserver);
	}

	@Override
	public int getCount() {
		return wrapped.getCount();
	}

	@Override
	public Object getItem(int i) {
		return wrapped.getItem(i);
	}

	@Override
	public long getItemId(int i) {
		return wrapped.getItemId(i);
	}

	@Override
	public boolean hasStableIds() {
		return wrapped.hasStableIds();
	}

	@Override
	public int getItemViewType(int i) {
		return wrapped.getItemViewType(i);
	}

	@Override
	public int getViewTypeCount() {
		return wrapped.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = wrapped.getView(position, view, viewGroup);
		enableFor(view, position,viewGroup);
		return view;
	}

	public int getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(int duration) {
		if(duration < 0) {
			throw new IllegalArgumentException("Duration is less than zero");
		}		
		animationDuration = duration;
	}

	/**
	 * Check's if any position is currently Expanded
	 * To collapse the open item @see collapseLastOpen
	 * 
	 * @return boolean True if there is currently an item expanded, otherwise false
	 */
	public boolean isAnyItemExpanded() {
		Log.e("xxxx", "lastOpenPositon="+lastOpenPosition);
		return (lastOpenPosition != -1) ? true : false;
	}

	public void enableFor(View parent, int position,ViewGroup viewGroup) {
		View more = getExpandToggleButton(parent);
		View itemToolbar = getExpandableView(parent);
		itemToolbar.measure(parent.getWidth(), parent.getHeight());
		enableFor(more, itemToolbar, position,viewGroup);
	}

	private void enableFor(final View button, final View target, final int position,final ViewGroup viewGroup) {
//		if(target == lastOpen && position!=lastOpenPosition) {
//			// lastOpen is recycled, so its reference is false
//			lastOpen = null;
//		}
		if(position == lastOpenPosition) {
			// re reference to the last view
			// so when can animate it when collapsed
			lastOpen = target;
		}
		int height = viewHeights.get(position, -1);
		if(height == -1) {
			viewHeights.put(position, target.getMeasuredHeight());
			updateExpandable(target,position);
		} else {
			updateExpandable(target, position);
		}
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				
				collapseLastOpen();
				
				
//				lastOpenPosition = position;
//				target.getParent().requestLayout();
				target.findViewById(R.id.network_loading).setVisibility(View.VISIBLE);
				String pkgname = (String) button.getTag();
				NetUtil.getInstance().requestRelatedGameInfo(pkgname, new IRequestListener() {
					
					@Override
					public void onRequestSuccess(BaseResult resData) {
						
						
						target.findViewById(R.id.network_loading).setVisibility(View.GONE);
						target.findViewById(R.id.item_related_info).setVisibility(View.VISIBLE);
						GameRelatedResult gameInfoResult = (GameRelatedResult) resData;
						ArrayList<GameRelatedInfo> infoList = gameInfoResult.getGamesList();
						for (int i = 0; i < infoList.size() && i<4; i++) {//最多显示4条
							GameRelatedInfo info = infoList.get(i);
//							boolean isWhite = i%2 == 0 ? true : false;
							int resId = getResIdByType(info.getInfoType());
							switch (i) {
							case 0:
								ImageView iView1 = (ImageView) target.findViewById(R.id.mine_item1_icon);
								TextView tView1 = (TextView) target.findViewById(R.id.mine_item1_desc);								
								iView1.setImageResource(resId);
								tView1.setText(info.getInfocontent());								
								break;
							case 1:
								View view2 = target.findViewById(R.id.mine_item2);
								view2.setBackgroundResource(R.drawable.mine_item_expand_gray_middle);
								ImageView iView2 = (ImageView) target.findViewById(R.id.mine_item2_icon);
								TextView tView2 = (TextView) target.findViewById(R.id.mine_item2_desc);								
								iView2.setImageResource(resId);
								tView2.setText(info.getInfocontent());
								break;
							case 2:
								View view3 = target.findViewById(R.id.mine_item3);
								view3.setBackgroundResource(R.drawable.mine_item_expand_white_middle);
								ImageView iView3 = (ImageView) target.findViewById(R.id.mine_item3_icon);
								TextView tView3 = (TextView) target.findViewById(R.id.mine_item3_desc);								
								iView3.setImageResource(resId);
								tView3.setText(info.getInfocontent());
								break;
							case 3:
								View view4 = target.findViewById(R.id.mine_item4);
								view4.setBackgroundResource(R.drawable.mine_item_expand_gray_middle);
								ImageView iView4 = (ImageView) target.findViewById(R.id.mine_item4_icon);
								TextView tView4 = (TextView) target.findViewById(R.id.mine_item4_desc);								
								iView4.setImageResource(resId);
								tView4.setText(info.getInfocontent());
								break;
							default:
								break;
							}
						}
					}
					
					@Override
					public void onRequestError(int requestTag, int requestId, int errorCode,
							String msg) {
					}
				});
				target.setAnimation(null);

				int type = target.getVisibility() == View.VISIBLE
						? ExpandCollapseAnimation.COLLAPSE
						: ExpandCollapseAnimation.EXPAND;

				// remember the state
				if (type == ExpandCollapseAnimation.EXPAND) {
					openItems.set(position, true);
				} else {
					openItems.set(position, false);
				}
				// check if we need to collapse a different view
				if (type == ExpandCollapseAnimation.EXPAND) {
					if (lastOpenPosition != -1 && lastOpenPosition != position) {
						if (lastOpen != null) {
							animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
						} else {
							Log.e("xxxx", "lastopen is null...");
						}
//						else {
//							lastOpen = wrapped.getView(position, view, viewGroup);
//							animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
//						}
						openItems.set(lastOpenPosition, false);
					}
					lastOpen = target;
					lastOpenPosition = position;
				} else if (lastOpenPosition == position) {
					lastOpenPosition = -1;
				}
				animateView(target, type);
			}
		});
	}

	private int getResIdByType(String infoType) {
		int type = Integer.parseInt(infoType);
		int resId = 0;
		switch (type) {
		case 0:
			resId = R.drawable.mine_item_icon_gonglve;
			break;
		case 1:
			resId = R.drawable.mine_item_icon_pingce;
			break;
		case 2:
			resId = R.drawable.mine_item_icon_zixun;
			break;
		case 3:
			resId = R.drawable.mine_item_icon_huodong;
			break;
		case 4:
			resId = R.drawable.mine_item_icon_qianghao;
			break;
		case 5:
			resId = R.drawable.mine_item_icon_kaifu;
			break;
		default:
			break;
		}
		return resId;
	}
	
	private void updateExpandable(View target, int position) {

		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)target.getLayoutParams();
		if(openItems.get(position)) {
			target.setVisibility(View.VISIBLE);
			params.bottomMargin = 0;
		} else {
			target.setVisibility(View.GONE);
			params.bottomMargin = 0-viewHeights.get(position);
		}
	}

	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * @param target the view to animate
	 * @param type the animation type, either ExpandCollapseAnimation.COLLAPSE
	 *			 or ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type) {
		Animation anim = new ExpandCollapseAnimation(target,type);
		anim.setDuration(getAnimationDuration());
		target.startAnimation(anim);
	}

	/**
	 * Closes the current open item.
	 * If it is current visible it will be closed with an animation.
	 *
	 * @return true if an item was closed, false otherwise
	 */
	public boolean collapseLastOpen() {
		if(isAnyItemExpanded()) {
			// if visible animate it out
			if(lastOpen != null) {
				animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
			} else {
				Log.e("xxxx", "lastOpen is null in collapseLastOpen.");
//				lastOpen = wrapped.getView(position, view, viewGroup);
//				animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
			
			}
			openItems.set(lastOpenPosition, false);
			lastOpenPosition = -1;
			return true;
		}
		return false;
	}

	public Parcelable onSaveInstanceState(Parcelable parcelable) {

		SavedState ss = new SavedState(parcelable);
		ss.lastOpenPosition = this.lastOpenPosition;
		ss.openItems = this.openItems;
		return ss;
	}

	public void onRestoreInstanceState(SavedState state) {
		this.lastOpenPosition = state.lastOpenPosition;
		this.openItems = state.openItems;
	}

	/**
	 * Utility methods to read and write a bitset from and to a Parcel
	 */
	private static BitSet readBitSet(Parcel src) {
		int cardinality = src.readInt();

		BitSet set = new BitSet();
		for (int i = 0; i < cardinality; i++) {
			set.set(src.readInt());
		}
		return set;
	}

	private static void writeBitSet(Parcel dest, BitSet set) {
		int nextSetBit = -1;
		dest.writeInt(set.cardinality());
		while ((nextSetBit = set.nextSetBit(nextSetBit + 1)) != -1) {
			dest.writeInt(nextSetBit);
		}
	}

	/**
	 * The actual state class
	 */
	static class SavedState extends View.BaseSavedState {
		public BitSet openItems = null;
		public int lastOpenPosition = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			in.writeInt(lastOpenPosition);
			writeBitSet(in, openItems);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			lastOpenPosition = out.readInt();
			 openItems = readBitSet(out);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}