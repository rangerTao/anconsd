package com.ranger.bmaterials.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.ranger.bmaterials.work.WeakReferenceHandler;

public class ADsWorkspace extends ViewGroup implements OnTouchListener {

	public static final int TOUCH_STATE_BEGIN = 1;
	public static final int TOUCH_STATE_STOPED = 3;
	public int mTouchState = TOUCH_STATE_STOPED;

	public int diff;
	private float x_move_first;
	private float x_move_second;
	private float x_down;
	private float x_up;
	private long time_x_down;
	private long time_x_up;
	public int current_screen;
	public int page_count;

	private int index;

	private float x;

	private int old_diff;
	private boolean need_trans_view;

	public static final int SCREEN_IS_NOT_MOVING = 0;
	public static final int SCREEN_IS_MOVING = 1;
	public int SCREEN_STATE = SCREEN_IS_NOT_MOVING;

	private static final int SCROLL_TO_DIFF = 1;
	private final static int CHANGE_AD_POINTS_ADD = 1001;
	private final static int CHANGE_AD_POINTS_MINUS = 1002;

	public int displayWidth;
	private OnScrollChangeListener mScrollChangeListener;

	public void reset() {
		index = 0;
	}

	public interface OnScrollChangeListener {
		public void onScrollFinished(int id);
	}

	public void setOnScrollChangeListener(OnScrollChangeListener l) {
		mScrollChangeListener = l;
	}

	private static class WeakHandler extends WeakReferenceHandler<ADsWorkspace> {

		public WeakHandler(ADsWorkspace workspace) {
			super(workspace);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void handleMessage(ADsWorkspace workspace, Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SCROLL_TO_DIFF:
				int f = (Integer) msg.obj;
				if (f == 0)
					break;
				workspace.diff = (workspace.current_screen - 1)
						* workspace.displayWidth;
				if (f > 0) {
					if (workspace.getChildCount() > 0) {
						View ch = workspace.getChildAt(0);
						workspace.removeViewAt(0);
						workspace.addView(ch);
					}
				} else {
					if (workspace.getChildCount() > 0) {
						View ch = workspace.getChildAt(workspace
								.getChildCount() - 1);
						workspace.removeViewAt(workspace.getChildCount() - 1);
						workspace.addView(ch, 0);
					}
				}
				workspace.scrollTo(workspace.diff, 0);

				workspace.SCREEN_STATE = SCREEN_IS_NOT_MOVING;

				if (null != workspace.mScrollChangeListener) {
					workspace.mScrollChangeListener
							.onScrollFinished(workspace.index);
				} else {
				}

				break;
			case CHANGE_AD_POINTS_ADD:
				if (workspace.page_count < 1) {
					workspace.index = 0;
				}else{
					workspace.index = (++workspace.index) % workspace.page_count;
				}
				
				break;
			case CHANGE_AD_POINTS_MINUS:
				if (workspace.page_count < 1) {
					workspace.index = 0;
				}else{
					workspace.index = (--workspace.index + workspace.page_count)
							% workspace.page_count;
				}
				
				break;
			}
		}
	}

	private WeakHandler mHandler = new WeakHandler(this);

	public ADsWorkspace(Context context, AttributeSet attr) {
		super(context, attr);
		setOnTouchListener(this);
	}

	public void setScrollOffset(int offset) {
		this.scrollOffset = offset;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		int childLeft = 0;
		// 横向平铺
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);

			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			if (child.getVisibility() != GONE) {
				child.setVisibility(View.VISIBLE);
				child.layout(childLeft, 0, childLeft + width, height);
				childLeft += width;
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	// int slowly_mode;
	public synchronized void scrollTo(int x, int y, boolean slowly,
			boolean need_trans_view) {
		int slowly_mode = old_diff - x;
		if (slowly_mode == 0)
			return;
		if (slowly) {
			SCREEN_STATE = SCREEN_IS_MOVING;
			if (slowly_mode > 0) {
				// --
				scroll2Left(need_trans_view);
			} else if (slowly_mode < 0) {
				// ++
				scroll2Right(need_trans_view);
			}
			if (!need_trans_view)
				SCREEN_STATE = SCREEN_IS_NOT_MOVING;
		} else {
			scrollTo(x, y);
		}
	}

	private void scroll2Left(final boolean need_trans_view) {
		old_diff -= 50;
		if (old_diff > diff) {
			// scrollTo(old_diff, 0);
			scrollTo(old_diff, 0);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					scroll2Left(need_trans_view);
				}
			}, 5);
		} else {
			Message m = new Message();
			m.what = SCROLL_TO_DIFF;
			if (need_trans_view) {
				m.obj = -1;
				mHandler.sendEmptyMessage(CHANGE_AD_POINTS_MINUS);
			} else
				m.obj = 0;

			mHandler.sendMessage(m);
		}
	}

	private void scroll2Right(final boolean need_trans_view) {
		old_diff += 50;
		if (old_diff < diff) {
			scrollTo(old_diff, 0);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					scroll2Right(need_trans_view);
				}
			}, 5);
		} else {
			Message m = new Message();
			m.what = SCROLL_TO_DIFF;
			if (need_trans_view) {
				m.obj = 1;
				mHandler.sendEmptyMessage(CHANGE_AD_POINTS_ADD);
			} else
				m.obj = 0;
			mHandler.sendMessage(m);
		}
	}

	public void setCurrentScreen(int currentScreen) {
		old_diff = diff;
		diff = currentScreen * displayWidth;
		scrollTo(diff, 0, true, true);
	}

	private int scrollOffset;

	@Override
	public void scrollTo(int x, int y) {
		// TODO Auto-generated method stub
		x -= scrollOffset;
		super.scrollTo(x, y);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mTouchState = TOUCH_STATE_BEGIN;
			x_down = ev.getX();
			x_move_first = ev.getX();
			time_x_down = System.currentTimeMillis();
			need_trans_view = false;

			x = ev.getRawX();

		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			mTouchState = TOUCH_STATE_STOPED;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (mTouchState == TOUCH_STATE_STOPED) {
				mTouchState = TOUCH_STATE_BEGIN;
				x_down = ev.getX();
				x_move_first = ev.getX();
				time_x_down = System.currentTimeMillis();
				need_trans_view = false;

				x = ev.getRawX();
			}

			if (Math.abs(x - ev.getRawX()) > 10) {
				requestDisallowInterceptTouchEvent(true);
				return true;
			}

		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (SCREEN_STATE == SCREEN_IS_MOVING)
			return true;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x_down = event.getX();
			x_move_first = event.getX();
			time_x_down = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_MOVE:
			requestDisallowInterceptTouchEvent(true);
			need_trans_view = false;
			if (mTouchState != TOUCH_STATE_STOPED) {
				x_move_second = event.getX();

				diff += (x_move_first - x_move_second);
				x_move_first = x_move_second;

				old_diff = diff;

				scrollTo(diff, 0);
			}

			break;
		case MotionEvent.ACTION_UP:
			time_x_up = System.currentTimeMillis();
			x_up = event.getX();
			if (mTouchState != TOUCH_STATE_STOPED) {

				if ((Math.abs(x_down - x_up)) / ((time_x_up - time_x_down)) > 0.3f) {
					// to next page
					if (x_down - x_up > 0) {
						// ++

						diff = displayWidth * current_screen;
						need_trans_view = true;
					} else {
						// --

						diff = displayWidth * (current_screen - 2);
						need_trans_view = true;

					}

					scrollTo(diff, 0, true, need_trans_view);

					mTouchState = TOUCH_STATE_STOPED;

					return true;
				}
			}

			if (mTouchState != TOUCH_STATE_STOPED) {

				if (x_down - x_up > (displayWidth >> 1)) {

					diff = displayWidth * current_screen;
					need_trans_view = true;

				} else if (x_down - x_up < -(displayWidth >> 1)) {

					diff = displayWidth * (current_screen - 2);
					need_trans_view = true;

				} else {
					diff = displayWidth * (current_screen - 1);
				}
				scrollTo(diff, 0, true, need_trans_view);

				mTouchState = TOUCH_STATE_STOPED;

			}

			break;
		}
		return true;
	}

}
