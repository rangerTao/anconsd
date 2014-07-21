package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.TextView;

public class CircleTextview extends TextView{


	private String labelColor;
	private ShapeDrawable mShapeDrawable;

	public CircleTextview(Context context, AttributeSet attrs) {
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
	
	public void setRanking(int ranking) {
		setText(String.valueOf(ranking));
		if (ranking < 100) {
			setTextSize(12);
		} else {
			setTextSize(10);
		}
	}

	private class MyShape extends OvalShape {

		private int color;
		private float w, h;

		public MyShape(int color) {
			this.color = color;
		}

		@Override
		public void draw(Canvas canvas, Paint paint) {
			if (color != 0) {
				paint.setColor(color);
				if (w == 0) {
					w = CircleTextview.this.getWidth();
					h = CircleTextview.this.getHeight();
				}
				canvas.drawOval(new RectF(0, 0, w, h), paint);
			}
		}
	}
}