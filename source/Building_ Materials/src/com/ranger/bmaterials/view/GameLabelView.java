package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ranger.bmaterials.tools.UIUtil;

public final class GameLabelView extends TextView {
	private String labelColor;
	private ShapeDrawable mShapeDrawable;

	public GameLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public final String getLabelColor() {
		return labelColor;
	}

	public final void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
		if (mShapeDrawable == null) {
			mShapeDrawable = new ShapeDrawable();
		}
		mShapeDrawable.setShape(new MyShape(Color.parseColor(labelColor)));
		setBackgroundDrawable(mShapeDrawable);
	}

	private class MyShape extends Shape {

		private int color;
		private float w, h, round;

		public MyShape(int color) {
			Context cx = getContext();
			round = UIUtil.dip2px(cx, 2);
			this.color = color;
		}

		@Override
		public void draw(Canvas canvas, Paint paint) {
			// TODO Auto-generated method stub
			if (color != 0) {
				paint.setColor(color);
				if (w == 0) {
					w = GameLabelView.this.getWidth();
					h = GameLabelView.this.getHeight();
				}
				canvas.drawRoundRect(new RectF(0, 0, w, h), round, round, paint);
			}
		}

	}
}
