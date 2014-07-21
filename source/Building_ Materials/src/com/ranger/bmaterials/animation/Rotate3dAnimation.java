/**
 * 
 */
package com.ranger.bmaterials.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 
 * @author hcq
 * @version 2012-12-19 ����4:59:53
 */

public final class Rotate3dAnimation extends Animation {
	private Camera camera = new Camera();
	private float fromDegree, toDegree, centerY, centerX;
	public static final int CENTER_HORI_MODE = 1;
	public static final int CENTER_VERI_MODE = 2;
	private boolean custom;
	private int mode = -1;

	public Rotate3dAnimation(float fromDegree, float toDegree, int mode) {
		this.toDegree = toDegree;
		this.fromDegree = fromDegree;
		this.mode = mode;

		setDuration(250);
		setFillAfter(true);
	}

	public Rotate3dAnimation(float fromDegree, float toDegree, int centerX,
			int centerY, int mode) {
		this.toDegree = toDegree;
		this.fromDegree = fromDegree;
		this.centerX = centerX;
		this.centerY = centerY;
		this.mode = mode;
		custom = true;

		setDuration(250);
		setFillAfter(true);
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// TODO Auto-generated method stub
		super.initialize(width, height, parentWidth, parentHeight);
		if (!custom) {
			centerY = height >> 1;
			centerX = width >> 1;
		}
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		float degrees = fromDegree + (toDegree - fromDegree) * interpolatedTime;
		Camera c = camera;

		Matrix matrix = t.getMatrix();
		c.save();
		if (mode == CENTER_VERI_MODE)
			c.rotateY(degrees);
		else if (mode == CENTER_HORI_MODE)
			c.rotateX(degrees);
		c.getMatrix(matrix);
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
		c.restore();
	}

}
