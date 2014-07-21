package com.ranger.bmaterials.work;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;

public class HtmlGetter implements Html.ImageGetter {
	private Context cx;
	private TextView tv;

	public HtmlGetter(Context cx, TextView tv) {
		this.cx = cx;
		this.tv = tv;
	}

	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
		final HtmlGetterDrawable urlDrawable = new HtmlGetterDrawable();

		ImageLoader.getInstance().loadImage(source, ImageLoaderHelper.getDefaultImageOptions(true), new SimpleImageLoadingListener() {

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bm) {
				// TODO Auto-generated method stub
				urlDrawable.setDrawable(new BitmapDrawable(cx.getResources(), bm));
				tv.setText(tv.getText());
				// or
				// tv.invalidate();
			}
		});
		return urlDrawable;
	}

	public class HtmlGetterDrawable extends BitmapDrawable {

		private Drawable drawable;

		private void setDrawable(Drawable ndrawable) {
			drawable = ndrawable;
			if (drawable == null)
				return;
			int width = tv.getMeasuredWidth();
			int height = (int) (1.0 * width / drawable.getIntrinsicWidth() * drawable.getIntrinsicHeight());
			drawable.setBounds(0, 0, width, height);
			setBounds(0, 0, width, height);

			// Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
			// int w = bm.getWidth();
			// int h = bm.getHeight();
			// WindowManager mWindowManager = (WindowManager) cx
			// .getSystemService(Context.WINDOW_SERVICE);
			//
			// DisplayMetrics dm = new DisplayMetrics();
			// mWindowManager.getDefaultDisplay().getMetrics(dm);
			//
			// int right = 0, bottom = 0;
			// if (w > dm.widthPixels || h > dm.heightPixels) {
			// // 进行等比例缩放
			// right = dm.widthPixels;
			// bottom = (int) (dm.widthPixels * h / w);
			//
			// } else {
			// right = w;
			// bottom = h;
			// }
			//
			// drawable.setBounds(0, 0, right, bottom);
			// setBounds(0, 0, right, bottom);
		}

		@Override
		public void draw(Canvas canvas) {
			if (drawable != null)
				drawable.draw(canvas);
		}
	}
}
