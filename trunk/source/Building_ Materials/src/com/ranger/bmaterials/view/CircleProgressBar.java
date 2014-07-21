package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ranger.bmaterials.tools.UIUtil;

public class CircleProgressBar extends ImageView {
	private Paint bgPaint = new Paint(), percentPaint = new Paint();
	private int currentPercent = -1;
	private boolean isCustomMode = true;
	private int strokeWidth;
	private RectF rf;
	private int bgColor = Color.parseColor("#dfe3e6");
	private int percentColor = Color.parseColor("#92baed");

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		strokeWidth = UIUtil.dip2px(getContext(), 1.15f);
		initCirclePaint(bgColor, bgPaint);
		initCirclePaint(percentColor, percentPaint);
	}

	public final void setBgColor(int bgColor) {
		this.bgColor = bgColor;
		initCirclePaint(bgColor, bgPaint);
	}

	public final void setPercentColor(int percentColor) {
		this.percentColor = percentColor;
		initCirclePaint(percentColor, percentPaint);
	}

	private void initCirclePaint(int color, Paint paint) {
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (isCustomMode) {
			drawPercent(canvas);
		} else
			super.onDraw(canvas);
	}

	public interface OnDrawCallBack {
		void onDraw(Canvas canvas);
	}

	public void drawPercent(Canvas canvas) {

		if (rf == null) {
			rf = new RectF();
			int offset = strokeWidth >> 1;
			rf.left = getLeft() + offset;
			rf.right = getRight() - offset;
			rf.bottom = getBottom() - offset;
			rf.top = getTop() + offset;
		}

		canvas.drawArc(rf, 0, 360, false, bgPaint);
		if (currentPercent > 0) {
			canvas.drawArc(rf, -90, (currentPercent * 360) / 100, false,
					percentPaint);
		}

	}

	public final void setCurrentPercent(int currentPercent) {
		this.currentPercent = currentPercent;
	}

	public final void setCustomMode(boolean isCustomMode) {
		this.isCustomMode = isCustomMode;
		if (isCustomMode) {
			setImageResource(0);
			setBackgroundResource(0);
		}
	}

	public final int getCurrentPercent() {
		// TODO Auto-generated method stub
		return currentPercent;
	}

}
