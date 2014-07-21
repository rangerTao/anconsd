package com.ranger.bmaterials.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.ADInfo;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.work.WeakReferenceHandler;

public class AdsViewPager extends SlowScrollViewpager {

	public AdsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private AdsPoints adPointsView;

	public final void setAdsPointsView(AdsPoints adPointsView) {
		this.adPointsView = adPointsView;
	}

	private class PageChangeListenerImpl implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			if (adPointsView != null) {
				adPointsView.change(position % ad_page_count);
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// TODO Auto-generated method stub
			isScrolling = positionOffset == 0 && positionOffsetPixels == 0 ? false : true;
		}
	}

	public int ad_page_count;

	// private WeakHashMap<String, Bitmap> imgCache = new WeakHashMap<String,
	// Bitmap>();
	//
	// private void showAdPic(int position) {
	// int pos = position % ad_page_count;
	// View child = ad_ivs.get(pos);
	// if (child.getParent() != null) {
	// // 避免显示占位图
	// ADInfo adInfo = (ADInfo) child.getTag();
	// String downUrl = adInfo.getAdpicurl();
	// ImageView iv = (ImageView) child;
	// if (imgCache.get(downUrl) == null
	// || imgCache.get(downUrl).isRecycled()) {
	// boolean haveFileCache = checkFileCache(downUrl, iv);
	// if (!haveFileCache)
	// downloadImage(downUrl);
	// } else
	// iv.setImageBitmap(imgCache.get(downUrl));
	// }
	// }

	// private boolean checkFileCache(String downUrl, ImageView iv) {
	// File imgFile = ImageLoader.getInstance().getDiscCache().get(downUrl);
	// FileInputStream fis = null;
	// try {
	// fis = new FileInputStream(imgFile);
	//
	// if (fis.available() > 0) {
	// Bitmap bg = BitmapFactory.decodeStream(fis, null, null);
	// Bitmap roundedBitmap = RoundedBitmapDisplayer.roundCorners(bg,
	// iv, UIUtil.dip2px(getContext(), 2f));
	// imgCache.put(downUrl, roundedBitmap);
	// iv.setImageBitmap(roundedBitmap);
	// bg = null;
	// roundedBitmap = null;
	// return true;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (fis != null)
	// try {
	// fis.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return false;
	// }

	// private void downloadImage(final String downUrl) {
	// ImageLoaderHelper.config();
	// ImageLoader.getInstance().loadImage(downUrl, optionsForLargImage,
	// new SimpleImageLoadingListener() {
	// @Override
	// public void onLoadingComplete(String arg0, View iv,
	// Bitmap bm) {
	// // TODO Auto-generated method stub
	// ImageView imageView = (ImageView) iv;
	// Bitmap roundedBitmap = RoundedBitmapDisplayer
	// .roundCorners(bm, imageView,
	// UIUtil.dip2px(getContext(), 2f));
	// imageView.setImageBitmap(roundedBitmap);
	// imgCache.put(downUrl, roundedBitmap);
	// }
	//
	// });
	// }

	private void loadImage(ImageView imageView, String downUrl) {
		ImageLoaderHelper.config();
		ImageLoaderHelper.displayImage(downUrl, imageView, optionsForLargImage);
		// ImageLoader.getInstance().displayImage(downUrl, imageView,
		// optionsForLargImage);
	}

	private DisplayImageOptions optionsForLargImage;

	private ArrayList<View> ad_ivs;// 广告图view

	public void init(ArrayList<View> ad_ivs, final int ad_page_count) {
		removeAllViews();
		this.ad_page_count = ad_page_count;
		this.ad_ivs = ad_ivs;
		int margin = UIUtil.dip2px(getContext(), 6);
		this.optionsForLargImage = ImageLoaderHelper.getCustomOption(R.drawable.ad_default);

		AdsViewPagerAdapter adpter = new AdsViewPagerAdapter();
		setAdapter(adpter);

		int pos = 65535; // 计算初始位置 过大会无效
		if (ad_ivs.size() < 2) {
			setCurrentItem(0);
		} else {
			setCurrentItem(0);
		}
		setOnPageChangeListener(new PageChangeListenerImpl());
	}

	private class AdsViewPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			int pos = position % ad_page_count;
			View view = ad_ivs.get(pos);

			if (view.getParent() == null) {
				container.addView(view, 0);
			} else {
				container.removeView(view);
				container.addView(view, 0);
			}

			ADInfo adInfo = (ADInfo) view.getTag();
			String downUrl = adInfo.getAdpicurl();
			loadImage((RoundCornerImageView) view, downUrl);

			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			int pos = position % ad_page_count;
			View view = ad_ivs.get(pos);
			container.removeView(view);
		}

		@Override
		public int getCount() {
			if (ad_page_count == 1) {
				return 1;
			} else {
				return Integer.MAX_VALUE;// 伪循环
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	private HANDLERTYPE handlerType;

	private enum HANDLERTYPE {
		START, STOP, DESTORY;
	}

	public synchronized void startAD() {
		if (handlerType != HANDLERTYPE.START) {
			handlerType = HANDLERTYPE.START;

			if (mHandler == null) {
				mHandler = new WeakHandler(this);
				mHandler.postTimer();
			}
		}
	}

	public synchronized void stopAD() {
		handlerType = HANDLERTYPE.STOP;
	}

	public boolean isScrolling;

	private static class WeakHandler extends WeakReferenceHandler<AdsViewPager> {

		public WeakHandler(AdsViewPager viewPager) {
			super(viewPager);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void handleMessage(AdsViewPager viewPager, Message msg) {
			// TODO Auto-generated method stub
			if (viewPager.handlerType == HANDLERTYPE.START) {
				if (!viewPager.isScrolling)
					viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
			}
			if (viewPager.handlerType != HANDLERTYPE.DESTORY)
				postTimer();
		}

		public void postTimer() {
			sendEmptyMessageDelayed(0, 5000);
		}
	}

	private WeakHandler mHandler;

	public void recycle() {
		// for (Bitmap bm : imgCache.values()) {
		// bm.recycle();
		// }
		// imgCache.clear();
		handlerType = HANDLERTYPE.DESTORY;
	}

}
