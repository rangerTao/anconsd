package com.ranger.bmaterials.view;

import com.ranger.bmaterials.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View{
	
	private Paint mPaint;
	private int mRadius;
	
	public CircleView(Context context) {
		super(context);
		mPaint = new Paint();
	}
	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CircleView);
        setFilledColor(a.getColor(R.styleable.CircleView_circlecolor, 0xFF0000));
        int radius = a.getDimensionPixelOffset(R.styleable.CircleView_radisSize, 0);
        if (radius > 0) {
            setRadius(radius);
        }
        a.recycle();

	}
	
	public CircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	private void setRadius(int radius) {
		mRadius = radius;
	}
	
	private void setFilledColor(int color) {
		mPaint.setColor(color);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int centre = getWidth() / 2; // 获取圆心的x坐标
		canvas.drawCircle(centre, centre, mRadius, mPaint);
	}
}