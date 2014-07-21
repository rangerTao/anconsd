package com.ranger.bmaterials.view;

import java.util.ArrayList;
import java.util.List;

import com.ranger.bmaterials.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class MultiThumbnailBundler {
	private Context cx;
	private int count = 4; // 可显示的缩略图数
	private int num_col = 2; // 每行显示的个数
	private int padding = 1; // 内边距 为了适配单位用dp
	private int margin = 2; // 外边距 为了适配单位用dp

	public final void setCount(int count) {
		this.count = count;
	}

	public final void setnum_col(int num_col) {
		this.num_col = num_col;
	}

	public final void setpadding(int padding) {
		this.padding = padding;
	}

	public final void setmargin(int margin) {
		this.margin = margin;
	}

	private Bitmap bgBmp;// 背景图
	private ArrayList<Bitmap> contents_bm;// 要显示的缩略图标

	public MultiThumbnailBundler(Context cx, int resBg,
			ArrayList<Bitmap> contents_bm) {
		this.cx = cx;
		this.bgBmp = BitmapFactory.decodeResource(cx.getResources(), resBg);
		this.contents_bm = contents_bm;
	}

	public MultiThumbnailBundler(Context cx, Bitmap bg,
			ArrayList<Bitmap> contents_bm) {
		this.cx = cx;
		this.bgBmp = bg;
		this.contents_bm = contents_bm;
	}

	public Bitmap getMultiThumbnailBundler() {
		float x, y;
		int iconWidth = bgBmp.getWidth(); // icon的宽度
		int iconHeight = bgBmp.getHeight();
		Bitmap bg = Bitmap.createBitmap(iconWidth, iconHeight,
				Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bg);
		canvas.drawBitmap(bgBmp, 0, 0, null); // 绘制背景
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象

		padding = dip2px(padding);
		margin = dip2px(margin);

		float scaleWidth = (iconWidth - margin * 2) / num_col - 2 * padding; // 计算缩略图的宽(高与宽相同)
		float scale = (scaleWidth / iconWidth); // 计算缩放比例
		matrix.postScale(scale, scale); // 设置缩放比例
		for (int i = 0; i < count; i++) {
			if (i < contents_bm.size()) {
				x = margin + padding * (2 * (i % num_col) + 1) + scaleWidth
						* (i % num_col);
				y = margin + padding * (2 * (i / num_col) + 1) + scaleWidth
						* (i / num_col);
				Bitmap scalebmp = Bitmap.createBitmap(contents_bm.get(i), 0, 0,
						iconWidth, iconHeight, matrix, true);
				canvas.drawBitmap(scalebmp, x, y, null);
				scalebmp.recycle();
			} else
				break;
		}
		bgBmp.recycle();
		return bg;
	}

	private int dip2px(float dpValue) {
		final float scale = cx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	
	private void test() {
		long t1 = System.currentTimeMillis();

		PackageManager pm = cx.getPackageManager();
		List<ApplicationInfo> infos = pm.getInstalledApplications(0);
		// 快捷方式的图标
		ArrayList<Bitmap> info_bms = new ArrayList<Bitmap>();
		int count = 4, index = 1;// 显示个数
		for (ApplicationInfo info : infos) {
			if (index > count)
				break;
			BitmapDrawable bd = (BitmapDrawable) info.loadIcon(pm);
			info_bms.add(bd.getBitmap());
			index++;
		}
		MultiThumbnailBundler mt = new MultiThumbnailBundler(cx,
				R.drawable.ic_launcher, info_bms);
		mt.setCount(count);

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				cx.getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName(cx, cx.getClass().getName());
		shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);// 不加会出现从快捷方式启动进程会重启
																// 添加后部分手机会重复创建快捷方式
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, mt.getMultiThumbnailBundler());

		cx.sendBroadcast(shortcut);

		Log.e("123", (System.currentTimeMillis() - t1) + "");
	}
}
