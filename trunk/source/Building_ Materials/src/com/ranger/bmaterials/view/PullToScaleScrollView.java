package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * @Description: 我的页面，下拉缩放背景
 * 
 * @author taoliang(taoliang@baidu-mgame.com)
 * @date 2014年6月5日 下午4:28:43
 * @version V
 * 
 */
public class PullToScaleScrollView extends ScrollView {

	private static final int LEN = 0xc8;
	private static final int DURATION = 500;
	private static final int MAX_DY = 200;

	private ImageView image_to_scale;

	private Scroller mScroller;

	private float currentX, currentY;
	private float origY;
	private int imgTop;
	private TouchScroll ts;

	private int imageViewH;

	boolean scrollerType;

	public PullToScaleScrollView(Context context) {
		super(context);
	}

	public PullToScaleScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PullToScaleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public void setScaleImageView(ImageView imageView) {
		this.image_to_scale = imageView;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (!mScroller.isFinished()) {
			try {
				return super.onTouchEvent(ev);
			} catch (Exception e) {
				return false;
			}
		}

		currentX = ev.getX();
		currentY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:

			origY = currentY;
			imgTop = image_to_scale.getBottom();
			imageViewH = image_to_scale.getHeight();
			ts = new TouchScroll(image_to_scale.getLeft(), image_to_scale.getBottom(), image_to_scale.getLeft(), image_to_scale.getBottom() + LEN);
			break;
		case MotionEvent.ACTION_MOVE:

			if (image_to_scale.isShown() && image_to_scale.getTop() >= 0) {
				if (ts != null) {
					int scrollY = ts.getScrollY(currentY - origY);
					if (scrollY >= imgTop && scrollY <= image_to_scale.getBottom() + LEN) {
						ViewGroup.LayoutParams lp = image_to_scale.getLayoutParams();
						lp.height = scrollY;
						image_to_scale.setLayoutParams(lp);
						image_to_scale.invalidate();
					}
				}
			}

			scrollerType = false;

			break;
		case MotionEvent.ACTION_UP:

			scrollerType = true;
			mScroller.startScroll(image_to_scale.getLeft(), image_to_scale.getBottom(), 0 - image_to_scale.getLeft(), imageViewH - image_to_scale.getBottom(), DURATION);
			invalidate();

			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			image_to_scale.layout(0, 0, x + image_to_scale.getWidth(), y);
			invalidate();
			if (!mScroller.isFinished() && scrollerType && y > MAX_DY) {
				android.view.ViewGroup.LayoutParams params = image_to_scale.getLayoutParams();
				params.height = y;
				image_to_scale.setLayoutParams(params);
			}
		}
	}

	public class TouchScroll {

		private int startX, startY;

		public TouchScroll(int startX, int startY, int endX, int endY) {
			super();
			this.startX = startX;
			this.startY = startY;
		}

		public int getScrollX(float dx) {
			int xx = (int) (startX + dx / 2.5F);
			return xx;
		}

		public int getScrollY(float dy) {
			int yy = (int) (startY + dy / 2.5F);
			return yy;
		}
	}

}
