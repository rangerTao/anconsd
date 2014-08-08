package com.ranger.bmaterials.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

public final class UIUtil {
	private UIUtil() {
	}

	/**
	 * 以最省内存的方式native读取本地资源的图片 setImageBitmap setimageresource decodefile都是java层
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		InputStream is = context.getResources().openRawResource(resId);
		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);// native实现
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 根据android源码 动态计算samplesize
	 * 设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，
	 * 即opts.width和opts.height
	 */
	public static Bitmap optimizeDecodeFile(String filePath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		opts.inJustDecodeBounds = false;
		try {
			return BitmapFactory.decodeFile(filePath, opts);
		} catch (OutOfMemoryError err) {
		}

		opts.inSampleSize = 2;

		return BitmapFactory.decodeFile(filePath, opts);
	}

//	/**
//	 * 
//	 * @param b
//	 * @param rx
//	 * @param ry
//	 * @param position
//	 *            : 0. all | 1. top | 2. bottom | 3. left | 4. right
//	 * @return
//	 */
//	public static Bitmap transferToRoundCorner(Bitmap bitmap, float rx,
//			float ry, int position) {
//	    if (null == bitmap)
//	    {
//	        return null;
//	    }
//	    
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//				bitmap.getHeight(), Bitmap.Config.RGB_565);
//		
//		if (null == output)
//		{
//		    return null;
//		}
//		
//		Canvas canvas = new Canvas(output);
//
//		final int color = 0xff424242;
//		final Paint paint = new Paint();
//		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		final RectF rectF = new RectF(rect);
//
//		switch (position) {
//		case 2:
//			rectF.top += 30;
//		case 1:
//			rectF.bottom += 30;
//			break;
//
//		case 4:
//			rectF.left += 30;
//		case 3:
//			rectF.right += 30;
//			break;
//
//		default:
//			break;
//		}
//
//		paint.setAntiAlias(true);
//		canvas.drawARGB(0, 0, 0, 0);
//		paint.setColor(color);
//		canvas.drawRoundRect(rectF, rx, ry, paint);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//		
//		return output;
//	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
	 */
	public static int px2sp(float pxValue, Context context) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) FloatMath.ceil(pxValue / fontScale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 sp的单位 转成为 px(像素)
	 */
	public static int sp2px(float spValue, Context context) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) FloatMath.ceil(spValue * fontScale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) FloatMath.ceil(dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) FloatMath.ceil(pxValue / scale + 0.5f);
	}

	/** 获取屏幕高度和宽度 */
	public static int[] getScreenPx(Context cx) {
		WindowManager mWindowManager = (WindowManager) cx
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);

		return new int[] { dm.widthPixels, dm.heightPixels };
	}

	public static int getDeviceDpi(Context cx) {
		WindowManager mWindowManager = (WindowManager) cx
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}

	/** 获取通知栏高度 */
	public static int getStatusBarHeight(Activity act) {
		Rect frame = new Rect();
		act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}


    public static void setTextViewTextHtml(TextView tv, String title)
    {
        if (null != tv)
        {
            if (null != title)
            {
                String s = checkspace(title.trim());

                tv.setText(Html.fromHtml(s));//"\u3000\u3000" +
            }
            else
            {
                tv.setText("");
            }
        }
    }

    public static String checkspace(String content)
    {
        Pattern p = Pattern.compile("　{1,10}");
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();

        while (result)
        {
            m.appendReplacement(sb, "");

            result = m.find();
        }
        m.appendTail(sb);

        Pattern p2 = Pattern.compile("[ | |　]{0,140}\n{1,140}[ | |　]{0,140}");
        Matcher m2 = p2.matcher(sb.toString());
        StringBuffer sb2 = new StringBuffer();
        boolean result2 = m2.find();

        while (result2)
        {
            m2.appendReplacement(sb2, "\n\u3000\u3000");

            result2 = m2.find();
        }
        m2.appendTail(sb2);

        return sb2.toString();
    }
	 
}
