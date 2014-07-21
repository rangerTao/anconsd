package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ranger.bmaterials.utils.ThumbnailUtils;

public class FrameMaskImageView extends ImageView {
	private Paint mPaint;

	private Bitmap mBottom;

	private Bitmap mFrame;

	private Bitmap mMask;

	private RectF mSaveLayerRectF;

	private PorterDuffXfermode mXfermode;

	public FrameMaskImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initView() {
		if (mPaint == null) {
			mPaint = new Paint();
			mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
		}

		if (mMask != null)
			mSaveLayerRectF = new RectF(0, 0, mMask.getWidth(),
					mMask.getHeight());
	}

	public void setBitmap(Bitmap mBottom) {
		if (mBottom == null)
			this.mBottom = null;
		else
			this.mBottom = ThumbnailUtils.extractThumbnail(mBottom, getWidth(),
					getHeight());// 缩略图
	}

	public void setFrame(Bitmap mFrame) {
		this.mFrame = mFrame;
	}

	public void setMask(Bitmap mMask) {
		this.mMask = mMask;
	}

	public final Paint getPaint() {
		return mPaint;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBottom != null && mFrame != null && mMask != null
				&& mPaint != null) {
			canvas.saveLayerAlpha(mSaveLayerRectF, 255, Canvas.MATRIX_SAVE_FLAG
					| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
					| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
					| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
			// 绘制蒙板
			canvas.drawBitmap(mMask, 0, 0, mPaint);
			mPaint.setXfermode(mXfermode);
			// 绘制底部图片
			canvas.drawBitmap(mBottom, 0, 0, mPaint);
			mPaint.setXfermode(null);
			// 绘制边框
			canvas.drawBitmap(mFrame, 0, 0, mPaint);

			canvas.restore();

		} else {
			mFrame = null;
			mMask = null;
			super.onDraw(canvas);
		}

	}

}
