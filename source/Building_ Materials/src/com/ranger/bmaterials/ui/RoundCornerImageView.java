package com.ranger.bmaterials.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.ranger.bmaterials.view.NetImageView;

public class RoundCornerImageView extends NetImageView {
	private boolean[] mCorners = new boolean[]{true,true,true,true};
	private float mRadius = 15f;
	private int width, height;
	private Path clipPath;

	private void setCompatibility() {
		// 4.0以上 用户在开发者选项中强制开了硬件加速 canvas.clippath方法可能会报错
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public RoundCornerImageView(Context context) {
		super(context);
		setCompatibility();
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCompatibility();
	}

	public RoundCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setCompatibility();
	}

	public void setRadius(float radius) {
		mRadius = radius;
		width = 0;
		height = 0;
	}

	public float getRadius() {
		return mRadius;
	}

	public void setCornersEnabled(boolean[] enabled) {

		for (int id = 0; id < 4; ++id) {
			if (id < enabled.length)
				mCorners[id] = enabled[id];
			else
				mCorners[id] = false;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int w = getWidth();
		int h = getHeight();

		if (width != w || height != h) {
			width = w;
			height = h;

			clipPath = new Path();

			RectF rect = new RectF(0, 0, w, h);
			float[] ii = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };

			for (int id = 0; id < mCorners.length; ++id) {
				if (mCorners[id]) {
					ii[id * 2] = mRadius;
					ii[id * 2 + 1] = mRadius;
				}
			}

			clipPath.addRoundRect(rect, ii, Path.Direction.CW);
		}

		canvas.clipPath(clipPath);
		
		super.onDraw(canvas);
	}
}
