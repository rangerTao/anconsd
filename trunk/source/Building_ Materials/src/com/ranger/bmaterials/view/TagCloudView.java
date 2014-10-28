package com.ranger.bmaterials.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.listener.onTagCloudViewLayoutListener;
import com.ranger.bmaterials.tools.DeviceUtil;

public class TagCloudView extends RelativeLayout implements
		OnGlobalLayoutListener {
	private static final String TAG = "TagCloudView";

	private TextView mTextView1;

	private AlphaAnimation animAlpha2Opaque;

	private AlphaAnimation animAlpha2Transparent;

	private Interpolator interpolator;

	private ScaleAnimation animScaleLarge2Normal, animScaleNormal2Large,
			animScaleZero2Normal, animScaleNormal2Zero;

	private AutoCompleteTextView editText;

	private InputMethodManager imm;

	private onTagCloudViewLayoutListener tagCloudViewLayoutListener;

	public TagCloudView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
		initGesture();
		getViewTreeObserver().addOnGlobalLayoutListener(this);

		imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public TagCloudView(Context mContext, int width, int height,
			List<Tag> tagList) {
		this(mContext, width, height, tagList, 6, 34, 5); // default for min/max
	}

	public void setEditText(AutoCompleteTextView editText) {
		this.editText = editText;
	}

	public TagCloudView(Context context, int width, int height,
			List<Tag> tagList, int textSizeMin, int textSizeMax, int scrollSpeed) {
		super(context);
		this.mContext = context;
		init();
		initGesture();
		locateCenter(width, height);
		addAndSetTags(tagList);
	}

	/**
	 * 确定球的中心
	 * 
	 * @param w
	 * @param h
	 */
	private void locateCenter(int w, int h) {
		float yOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, getResources().getDisplayMetrics());
		this.centerX = (float) (w / 2);
		this.centerY = (float) (h / 2);
		// set the center of the sphere on center of our screen:
		radius = Math.min(centerX * 0.75f, centerY * 0.75f); // use 95% of
																// screen
		this.centerY -= yOffset;
		// since we set tag margins from left of screen, we shift the whole tags
		// to left so that
		// it looks more realistic and symmetric relative to center of screen in
		// X direction
		shiftLeft = (int) (Math.min(centerX * 0.1f, centerY * 0.1f));
	}

	boolean hasInited = false;

	/**
	 * 确定当前view的大小
	 */
	@Override
	public void onGlobalLayout() {
		// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG,
		// "onGlobalLayout width:"+getWidth()+"height:"+ getHeight());

		if (!hasInited) {
			locateCenter(getWidth(), getHeight());
			if (tagList != null) {
				addAndSetTags(tagList);
				if (null != tagCloudViewLayoutListener) {
					tagCloudViewLayoutListener.onTagCloudViewLayoutInitialize();
				}
				hasInited = true;
			}

		}

	}

	private List<Tag> tagList;

	public void addTags(List<Tag> tagList) {
		this.tagList = Filter(tagList);
	}

	public void setOnTagCloudViewLayoutInitializedListener(
			onTagCloudViewLayoutListener listener) {
		tagCloudViewLayoutListener = listener;
	}

	public void addTagsWithPreload(List<Tag> tags) {
		this.tagList = Filter(tags);

		disappear();

		mAngleX = 0;
		mAngleY = 0;

		if (textViews != null) {
			textViews.clear();
		}

		if (params != null)
			params.clear();
		
		if (mTagCloud != null) {
			mTagCloud.clear();
			mTagCloud.rebuild(tags);
			this.tagList = tags;
			setMaxTextCount();
			Iterator it = mTagCloud.iterator();
			Tag tempTag;
			int i = 0;
			while (it.hasNext()) {
				tempTag = (Tag) it.next();
				addTag(tempTag);
				tempTag.setParamNo(i);
				i++;
			}
			show();
		}
	}

	private void addAndSetTags(List<Tag> tagList) {

		if (Constants.DEBUG)
			if (Constants.DEBUG)
				Log.i(TAG, "addTags centerX " + centerX + "centerY:" + centerY
						+ " ");

		// initialize the TagCloud from a list of tags
		// Filter() func. screens tagList and ignores Tags with same text (Case
		// Insensitive)
		mTagCloud = new TagCloud(Filter(tagList), (int) radius, textSizeMin,
				textSizeMax);
		float[] tempColor1 = { 0.9412f, 0.7686f, 0.2f, 1 }; // rgb Alpha
		// {1f,0f,0f,1} red {0.3882f,0.21568f,0.0f,1} orange
		// {0.9412f,0.7686f,0.2f,1} light orange
		float[] tempColor2 = { 1f, 0f, 0f, 1 }; // rgb Alpha
		mTagCloud.setTagColor1(tempColor1);// higher color
		mTagCloud.setTagColor2(tempColor2);// lower color
		mTagCloud.setRadius((int) radius);
		mTagCloud.create(true); // to put each Tag at its correct initial

		// update the transparency/scale of tags
		mTagCloud.setAngleX(mAngleX);
		mTagCloud.setAngleY(mAngleY);
		mTagCloud.update();

		textViews = new ArrayList<MyTextView>();
		params = new ArrayList<RelativeLayout.LayoutParams>();
		// Now Draw the 3D objects: for all the tags in the TagCloud
		Iterator it = mTagCloud.iterator();
		Tag tempTag;
		int i = 0;

		while (it.hasNext()) {
			tempTag = (Tag) it.next();
			tempTag.setParamNo(i); // store the parameter No. related to this
									// tag

			textViews.add(new MyTextView(this.mContext));
			TextView textView = textViews.get(i);
			textView.setText(tempTag.getText());
			params.add(new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			LayoutParams param = params.get(i);
			param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			/*
			 * mParams.get(i).setMargins( (int) (centerX -shiftLeft +
			 * tempTag.getLoc2DX()), (int) (centerY + tempTag.getLoc2DY()), 0,
			 * 0);
			 */
			textView.setBackgroundResource(tempTag.getBackgroudRes());
			layoutTag(tempTag);
			textView.setLayoutParams(param);

			textView.setSingleLine(true);
			/*
			 * int mergedColor = Color.argb( (int) (tempTag.getAlpha() * 255),
			 * (int) (tempTag.getColorR() * 255), (int) (tempTag.getColorG() *
			 * 255), (int) (tempTag.getColorB() * 255));
			 * mTextView.get(i).setTextColor(mergedColor);
			 */
			// int textSize = (int)(tempTag.getTextSize() * tempTag.getScale());
			// textView.setTextSize(textSize);

			textView.setGravity(Gravity.CENTER);
			addView(textView);
			// textView.setOnClickListener(OnTagClickListener2(tempTag.getText()));
			registerClickEvent(textView, tempTag);
			i++;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private void addTag(Tag newTag) {
		// mTagCloud.add(newTag);

		int i = textViews.size();
		newTag.setParamNo(i);

		textViews.add(new MyTextView(this.mContext));
		TextView textView = textViews.get(i);
		textView.setText(newTag.getText());

		params.add(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		LayoutParams param = params.get(i);
		param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		/*
		 * mParams.get(i).setMargins( (int) (centerX -shiftLeft +
		 * newTag.getLoc2DX()), (int) (centerY + newTag.getLoc2DY()), 0, 0);
		 */
		textView.setBackgroundResource(newTag.getBackgroudRes());
		// textView.setBackgroundColor(Color.argb(0x77, 0xa6, 0x96, 0x56));
		textView.setSingleLine(true);
//		textView.setTextColor(Color.rgb(0xff, 0xff, 0xff));
        textView.setTextColor(newTag.getColor());
		textView.setGravity(Gravity.CENTER_VERTICAL);
		layoutTag(newTag);

		textView.setLayoutParams(param);
		addView(textView);

		registerClickEvent(textView, newTag);
	}

	int maxTextCount;

	private void setMaxTextCount() {
		int max = 1;
		for (Tag tag : tagList) {
			int length = tag.getText().length();
			if (max < length) {
				max = length;
			}
		}
		this.maxTextCount = max;
	}

	private int getMaxTextCount() {
		return maxTextCount;
	}

	private int getMinTextCount() {
		int min = 0;
		for (Tag tag : tagList) {
			int length = tag.getText().length();
			if (min > length) {
				min = length;
			}
		}
		return min;
	}

	private int getMinMaxRatio() {
		int maxTextCount = getMaxTextCount();
		if (maxTextCount > 0) {
			return getMinTextCount() / maxTextCount;
		}
		return 1;
	}

	private float getRatio(Tag tag) {
		int maxTextCount = getMaxTextCount();
		if (maxTextCount > 0) {
			float r = (float) (1.0 * (tag.getText().length() + 1) / (maxTextCount + 1));
			if (r < 0.3) {
				r = 0.3f;
			}
			return r;
		}
		return 1;
	}

    private int lastPositionX = 0;
    private int lastPositionY = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    }

    private int[] getTagPadding(Tag tempTag) {
		float scale = tempTag.getScale();
		// int textSize = tempTag.getTextSize();
		int pH = (int) (paddingH * scale);
		// int pV = (int) (paddingV*scale*scale);
		float ratio = getRatio(tempTag);
		int pV = (int) (paddingV * scale * ratio);
		int[] ret = new int[2];
		ret[0] = pH;
		ret[1] = pV;
		// if
		// (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG,String.format("scale:%s,padding left:%s,padding top:%s,ratio:%s for %s",scale,pH,pV,ratio,tempTag.getText()));
		return ret;
	}

	private int[] getTagSize(Tag tag) {
		int length = tag.getText().length();
		float C = tag.getTextSize();// * tag.getScale();
		float w = length * C + (length - 1) * 3;
		float h = C;
		int[] tagPadding = getTagPadding(tag);
		w += 2 * tagPadding[0];
		h += 2 * tagPadding[1];
		int[] ret = new int[2];
		ret[0] = (int) w;
		ret[1] = (int) h;
		return ret;
	}

	private long lastTime = -1;

    private List<Tag> mList;

	public void replace(List<Tag> tagList) {
		try {

            lastPositionX = 0;
            lastPositionY = 0;

            mList = tagList;

			if (System.currentTimeMillis() - lastTime < 600) {
				return;
			}
			txtAnimInType = OUTSIDE_TO_LOCATION;
			txtAnimOutType = LOCATION_TO_CENTER;
			disappear();

			mAngleX = 0;
			mAngleY = 0;
            if(mTagCloud != null)
			    mTagCloud.clear();
            if(textViews != null)
			    textViews.clear();

			params.clear();

			// List<Tag> filter = Filter(tagList);
			mTagCloud.rebuild(tagList);
			this.tagList = tagList;
			setMaxTextCount();
			Iterator it = mTagCloud.iterator();
			Tag tempTag;
			int i = 0;
			while (it.hasNext()) {
				tempTag = (Tag) it.next();
				addTag(tempTag);
				tempTag.setParamNo(i);
				i++;
			}

			// ImageView iv = new ImageView(getContext());
			// RelativeLayout.LayoutParams layoutParams = new
			// RelativeLayout.LayoutParams(
			// 80,
			// 40
			// );
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			// iv.setBackgroundResource(R.drawable.search_bg_red);
			// layoutParams.setMargins(
			// 450,
			// 200,
			// 0,
			// 0);
			// iv.setLayoutParams(layoutParams);
			// addView(iv);

			show();

			lastTime = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int[] textColors = { Color.argb(0xff, 0xE0, 0x66, 0x2F),
			Color.argb(0xff, 0x8B, 0x0A, 0x50),
			Color.argb(0xff, 0x1C, 0x86, 0xEE),
			Color.argb(0xff, 0x2B, 0x2B, 0x2B),
			Color.argb(0xff, 0x7A, 0x67, 0xEE),// #7A67EE
			Color.argb(0xff, 0x00, 0xEE, 0x76),// #7A67EE
			Color.argb(0xff, 0xCA, 0xFF, 0x76),// #CAFF70
			Color.argb(0xff, 0xFF, 0x45, 0x10),// #FF4500
			Color.argb(0xff, 0x7C, 0xFC, 0x10),// #7CFC00
	};

	Random random = new Random();

	void layoutTag(Tag tempTag) {
		TextView textView = textViews.get(tempTag.getParamNo());
		// int mh = textView.getMeasuredHeight();
		// int mw = textView.getMeasuredWidth();
		// //////////////////
		float scale = tempTag.getScale();
		int textSize = tempTag.getTextSize();
		textView.setTextSize((int) (textSize * scale));

		if (Constants.DEBUG)
			if (Constants.DEBUG)
				Log.i(TAG, "[layoutTag]textSize" + textSize + " scale:" + scale
						+ " for " + tempTag.getText());
		int[] tagPadding = getTagPadding(tempTag);

		// ////////////////////
		int w = textView.getWidth();
		int h = textView.getHeight();
		int shiftLeft = 0;
		int shiftTop = 0;
		if (h == 0 || w == 0) {
			/*
			 * textView.measure(MeasureSpec.makeMeasureSpec(0,
			 * MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0,
			 * MeasureSpec.AT_MOST)); int mh2 = textView.getMeasuredHeight();
			 * int mw2 = textView.getMeasuredWidth();
			 */
			// if
			// (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG,String.format("Again MeasuredWidth:%s,MeasuredHeight:%s for %s",mw2,mh2,textView.getText().toString()));
			int[] tagSize = getTagSize(tempTag);
			shiftLeft = tagSize[0] / 2;
			shiftTop = tagSize[1] / 2;
		} else {
			shiftLeft = w / 2;
			shiftTop = h / 2;
		}
		// shiftLeft = 0;
		// shiftTop = 0 ;
		// Paint paint = textView.getPaint();
		// int strWidth = (int) Math.ceil(paint.measureText(tempTag.getText()));

		// if
		// (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG,String.format("MeasuredWidth:%s,MeasuredHeight:%s,width:%s,height:%s,strWidth:%s,shiftLeft:%s,shiftTop:%s for %s",mw,mh,w,h,strWidth,shiftLeft,shiftTop,textView.getText().toString()));

        int marginTop = (int) (centerY - shiftTop + tempTag.getLoc2DY());
        int marginLeft = (int) (centerX - shiftLeft + tempTag.getLoc2DX());

        if(lastPositionX == 0){
            marginTop = 200;
            lastPositionX = marginTop;
            lastPositionY = marginLeft;
        }else{
//            if(marginTop - lastPositionX < 30){
//                marginTop += 30 ;
//            }
            marginTop = lastPositionX + 80;

            if((marginLeft + textSize * textView.getText().length()) > getWidth()){
                marginLeft = 50;
            }
        }

        params.get(tempTag.getParamNo()).setMargins(
				marginLeft,
				/* (int) (centerX -shiftLeft+ tempTag.getLoc2DX()), */
				marginTop, 0, 0);

        lastPositionX = marginTop;
        lastPositionY = marginLeft;

		// textView.setTextColor(tempTag.getColor());
		/*
		 * if((centerX - shiftLeft + tempTag.getLoc2DX()) + textView.getWidth()
		 * >= getWidth() || textView.getRight()+2 >= getWidth()){
		 * textView.setPadding(tagPadding[0], tagPadding[1], 0, tagPadding[1]);
		 * //textView.setBackgroundResource(tempTag.getBackgroudRes()); }else{
		 */

		textView.setPadding(tagPadding[0], tagPadding[1], tagPadding[0],
				tagPadding[1]);
		// }
		textView.bringToFront();

	}

	private void setTextColor() {

	}

	public static final int ANIMATION_IN = 1;

	public static final int ANIMATION_OUT = 2;

	public static final int OUTSIDE_TO_LOCATION = 1;

	public static final int LOCATION_TO_OUTSIDE = 2;

	public static final int CENTER_TO_LOCATION = 3;

	public static final int LOCATION_TO_CENTER = 4;

	private int txtAnimInType, txtAnimOutType;

	private void disappear() {

		if (null == mTagCloud)
			return;

		List<Tag> tagList = mTagCloud.getTagList();
		int size = getChildCount();
		List<TextView> pendingRemovedViews = new ArrayList<TextView>(size / 2);
		for (int i = 0; i < size; i++) {
			final TextView pendingRemovedView = (TextView) getChildAt(i);

			if (Constants.DEBUG)
				if (Constants.DEBUG)
					Log.i(TAG,
							"before removing ,child count:" + size + ",child "
									+ i + "=" + pendingRemovedView
									+ " visibility:"
									+ pendingRemovedView.getVisibility());
			if (pendingRemovedView.getVisibility() == View.GONE) {
				if (Constants.DEBUG)
					if (Constants.DEBUG)
						Log.d(TAG, "before removing,child count:" + size
								+ ",child" + i + "=" + pendingRemovedView
								+ ",GONE,remove " + i);
				pendingRemovedViews.add(pendingRemovedView);
				// removeView(pendingRemovedView);
			} else {
				Boolean tag = (pendingRemovedView.getTag() == null) ? false
						: (Boolean) pendingRemovedView.getTag();
				if (tag) {
					if (Constants.DEBUG)
						if (Constants.DEBUG)
							Log.d(TAG, "before removing ,child count:" + size
									+ ",child " + i + "=" + pendingRemovedView
									+ ",tag is true,remove " + i);
					pendingRemovedView.clearAnimation();
					pendingRemovedViews.add(pendingRemovedView);
				}
			}
		}
		for (TextView textView : pendingRemovedViews) {
			try {
				removeView(textView);
			} catch (Exception e) {
			}

		}

		size = getChildCount();
		for (int i = 0; i < size; i++) {
			final TextView pendingDisappearedView = (TextView) getChildAt(i);
			if (pendingDisappearedView.getVisibility() == View.GONE) {
				removeView(pendingDisappearedView);
				continue;
			}
			Boolean tag = (pendingDisappearedView.getTag() == null) ? false
					: (Boolean) pendingDisappearedView.getTag();
			if (tag) {
				pendingDisappearedView.clearAnimation();
				pendingDisappearedView.setVisibility(View.GONE);
				// 删除可能有问题
				continue;
			}
			pendingDisappearedView.setTag(true);
			// RelativeLayout.LayoutParams lp = (LayoutParams)
			// pendingDisappearedView.getLayoutParams();
			// int[] xy = new int[] { layParams.leftMargin, layParams.topMargin,
			// txt.getWidth() };
			AnimationSet animSet = getAnimationSet(tagList.get(i),
					pendingDisappearedView.getWidth(),
					pendingDisappearedView.getHeight(), (int) centerX,
					(int) centerY, txtAnimOutType);
			pendingDisappearedView.startAnimation(animSet);

			animSet.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					pendingDisappearedView.setOnClickListener(null);
					pendingDisappearedView.setClickable(false);
					pendingDisappearedView.setVisibility(View.GONE);
					// removeView(txt);
				}
			});
		}
	}

	private void show() {
		int size = textViews.size();
		List<Tag> tagList = mTagCloud.getTagList();

		for (int i = size - 1; i >= 0; i--) {
			Tag tag = tagList.get(i);
			TextView textView = textViews.get(i);
			Paint paint = textView.getPaint();
			int strWidth = (int) Math.ceil(paint.measureText(tag.getText()));
			AnimationSet anim = getAnimationSet(tag, strWidth, 0,
					(int) centerX, (int) centerY, txtAnimInType);
			textView.startAnimation(anim);
		}
	}

	private void init() {
		this.textSizeMin = 6;
		this.textSizeMax = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 15, getResources()
						.getDisplayMetrics());

		float screenDensity = DeviceUtil.getScreenDensity(getContext());
		if (screenDensity >= 3) {
			this.textSizeMax = (int) (screenDensity * 9);
		} else if (screenDensity >= 2) {
			this.textSizeMax = (int) (screenDensity * 12);
		} else if (screenDensity >= 1.5) {
			this.textSizeMax = (int) (screenDensity * 15);
		} else {
			this.textSizeMax = (int) (screenDensity * 20);
		}

		this.paddingH = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 13, getResources()
						.getDisplayMetrics());

		this.paddingV = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 6, getResources()
						.getDisplayMetrics());
		this.tspeed = 5;

		interpolator = AnimationUtils.loadInterpolator(getContext(),
				android.R.anim.decelerate_interpolator);
		animAlpha2Opaque = new AlphaAnimation(0.0f, 1.0f);
		animAlpha2Transparent = new AlphaAnimation(1.0f, 0.0f);
		animScaleLarge2Normal = new ScaleAnimation(2, 1, 2, 1);
		animScaleNormal2Large = new ScaleAnimation(1, 2, 1, 2);
		animScaleZero2Normal = new ScaleAnimation(0, 1, 0, 1);
		animScaleNormal2Zero = new ScaleAnimation(1, 0.9f, 1, 0.9f);

		initClickEvent();

		int[] screensize = DeviceUtil.getScreensize(getContext());
		// if(Constants.DEBUG)Log.i(TAG,
		// "textSizeMin:"+textSizeMin+" textSizeMax:"+textSizeMax+" screenDensity :"+screenDensity+"screensize:"+screensize[0]+","+screensize[1]);
	}

	public AnimationSet getAnimationSet(Tag tag, int textWidth, int textHeight,
			int xCenter, int yCenter, int type) {

		AnimationSet animSet = new AnimationSet(true);
		animSet.setInterpolator(interpolator);
		if (type == OUTSIDE_TO_LOCATION) {
			animSet.addAnimation(animAlpha2Opaque);
			// animSet.addAnimation(animScaleLarge2Normal);

			int width = textWidth;
			int height = textHeight;
			float tX = tag.getLoc2DX();
			if (tag.getLoc2DX() > 0) {
				tX += (width >> 1);
			} else {
				tX -= width >> 1;
			}
			float tY = tag.getLoc2DY();
			if (tag.getLoc2DY() > 0) {
				tY += (height >> 1);
			} else {
				tY -= (height >> 1);
			}

			TranslateAnimation translate = new TranslateAnimation(
					((int) tX) << 1, 0, ((int) tY) << 1, 0);
			animSet.addAnimation(translate);
		} else if (type == LOCATION_TO_OUTSIDE) {
			animSet.addAnimation(animAlpha2Transparent);
			animSet.addAnimation(animScaleNormal2Large);
			TranslateAnimation translate = new TranslateAnimation(
					0,
					((int) tag.getLocX() + (tag.getTextSize() >> 1) - xCenter) << 1,
					0, ((int) tag.getLocY() - yCenter) << 1);
			animSet.addAnimation(translate);
		} else if (type == LOCATION_TO_CENTER) {
			int x = (int) (centerX - shiftLeft + tag.getLoc2DX());
			int y = (int) (centerY + tag.getLoc2DY());

			animSet.addAnimation(animAlpha2Transparent);
			// animSet.addAnimation(animScaleNormal2Zero);
			int width = textWidth;
			int height = textHeight;

			float tX = -tag.getLoc2DX();
			if (tag.getLoc2DX() > 0) {
				tX -= (width >> 1);
			} else {
				tX += width >> 1;
			}
			float tY = -tag.getLoc2DY();
			if (tag.getLoc2DY() > 0) {
				tY -= (height >> 1);
			} else {
				tY += (height >> 1);
			}
			TranslateAnimation translate = new TranslateAnimation(0, tX, 0, tY);
			if (Constants.DEBUG)
				if (Constants.DEBUG)
					Log.i(TAG, tag.getText() + ">>cX:" + centerX + " cY:"
							+ centerY + " (Loc2DX,Loc2DY)=" + tag.getLoc2DX()
							+ " " + tag.getLoc2DY() + " (x,y)=" + x + " " + y
							+ " tag size " + tag.getTextSize() + "(tX,tY)="
							+ tX + "," + tY);
			// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG, tag.getText()+
			// "textWidth "+width+" left:"+text.getLeft()
			// +" top:"+text.getTop());
			animSet.addAnimation(translate);
		} else if (type == CENTER_TO_LOCATION) {
			animSet.addAnimation(animAlpha2Opaque);
			animSet.addAnimation(animScaleZero2Normal);
			TranslateAnimation translate = new TranslateAnimation(
					(-(int) tag.getLocX() + xCenter), 0,
					(-(int) tag.getLocY() + yCenter), 0);
			animSet.addAnimation(translate);
		}
		animSet.setDuration(600L);
		return animSet;
	}

	public void reset() {
		mTagCloud.reset();

		Iterator it = mTagCloud.iterator();
		Tag tempTag;
		while (it.hasNext()) {
			tempTag = (Tag) it.next();
			/*
			 * mParams.get(tempTag.getParamNo()).setMargins( (int) (centerX
			 * -shiftLeft+ tempTag.getLoc2DX()), (int) (centerY +
			 * tempTag.getLoc2DY()), 0, 0);
			 */
			layoutTag(tempTag);
			textViews.get(tempTag.getParamNo()).setTextSize(
					(int) (tempTag.getTextSize() * tempTag.getScale()));
			/*
			 * int mergedColor = Color.argb( (int) (tempTag.getAlpha() * 255),
			 * (int) (tempTag.getColorR() * 255), (int) (tempTag.getColorG() *
			 * 255), (int) (tempTag.getColorB() * 255)); setTextColor();
			 */
			tempTag.setBackgroudRes(tempTag.getBackgroudRes());
			textViews.get(tempTag.getParamNo()).bringToFront();
		}
	}

	@Override
	public boolean onTrackballEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		if (Constants.DEBUG)
			if (Constants.DEBUG)
				Log.i(TAG, "onTrackballEvent x:" + x + " y:" + y);
		mAngleX = (y) * tspeed * TRACKBALL_SCALE_FACTOR;
		mAngleY = (-x) * tspeed * TRACKBALL_SCALE_FACTOR;

		mTagCloud.setAngleX(mAngleX);
		mTagCloud.setAngleY(mAngleY);
		mTagCloud.update();

		Iterator it = mTagCloud.iterator();
		Tag tempTag;
		while (it.hasNext()) {
			tempTag = (Tag) it.next();
			/*
			 * mParams.get(tempTag.getParamNo()).setMargins( (int) (centerX
			 * -shiftLeft+ tempTag.getLoc2DX()), (int) (centerY +
			 * tempTag.getLoc2DY()), 0, 0);
			 */
			layoutTag(tempTag);
			textViews.get(tempTag.getParamNo()).setTextSize(
					(int) (tempTag.getTextSize() * tempTag.getScale()));
			/*
			 * int mergedColor = Color.argb( (int) (tempTag.getAlpha() * 255),
			 * (int) (tempTag.getColorR() * 255), (int) (tempTag.getColorG() *
			 * 255), (int) (tempTag.getColorB() * 255)); setTextColor();
			 */
			textViews.get(tempTag.getParamNo()).bringToFront();
		}
		return true;
	}

	GestureDetector dector = null;

	final private int SWIPE_MIN_DISTANCE = 100;

	final private int SWIPE_MIN_VELOCITY = 100;

	private void handleFling(float vX, float vY) {
		mAngleX = (vY / radius) * tspeed * TOUCH_SCALE_FACTOR;
		mAngleY = (-vX / radius) * tspeed * TOUCH_SCALE_FACTOR;

		mTagCloud.setAngleX(mAngleX);
		mTagCloud.setAngleY(mAngleY);
		mTagCloud.update();

		Iterator it = mTagCloud.iterator();
		Tag tempTag;
		while (it.hasNext()) {
			tempTag = (Tag) it.next();
			/*
			 * mParams.get(tempTag.getParamNo()).setMargins( (int) (centerX
			 * -shiftLeft + tempTag.getLoc2DX()), (int) (centerY +
			 * tempTag.getLoc2DY()), 0, 0);
			 */
			layoutTag(tempTag);

			// textView.setBackgroundResource(tempTag.getBackgroudRes());
			// TextView textView = mTextViews.get(tempTag.getParamNo());
			// textView.setTextSize((int)(tempTag.getTextSize() *
			// tempTag.getScale()));
			/*
			 * int mergedColor = Color.argb( (int) (tempTag.getAlpha() * 255),
			 * (int) (tempTag.getColorR() * 255), (int) (tempTag.getColorG() *
			 * 255), (int) (tempTag.getColorB() * 255)); setTextColor();
			 */

		}
	}

	static interface OnSmoothScrollFinishedListener {
		void onSmoothScrollFinished();
	}

	final class SmoothScrollRunnable implements Runnable {
		private final Interpolator mInterpolator;

		private final float mVX;

		private final float mVY;

		private final long mDuration;

		private OnSmoothScrollFinishedListener mListener;

		private boolean mContinueRunning = true;

		private long mStartTime = -1;

		private float mCurrentVX = -1;

		private float mCurrentVY = -1;

		public SmoothScrollRunnable(float vX, float vY, long duration,
				Interpolator mScrollAnimationInterpolator,
				OnSmoothScrollFinishedListener listener) {
			mVX = vX;
			mVY = vY;
			mInterpolator = mScrollAnimationInterpolator;
			mDuration = duration;
			mListener = listener;
		}

		@Override
		public void run() {
			float totalV = 0;
			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
				totalV = (float) getTotalV(mVX, mVY);
				mCurrentVX = mVX;
				mCurrentVY = mVY;
			} else {
				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (10000 * (System.currentTimeMillis() - mStartTime))
						/ mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, 10000), 0);
				float interpolationVal = mInterpolator
						.getInterpolation(normalizedTime / 10000f);

				mCurrentVX = (mVX * (1 - interpolationVal));
				mCurrentVY = (mVY * (1 - interpolationVal));
				handleFling(mCurrentVX, mCurrentVY);
				totalV = (float) getTotalV(mCurrentVX, mCurrentVY);
				if (Constants.DEBUG)
					Log.d(TAG, "handleFling =" + mCurrentVX + " " + mCurrentVY);
				// setHeaderScroll(mCurrentY);
			}
			if (Constants.DEBUG)
				Log.d(TAG, "SmoothScrollRunnable run =" + mCurrentVX + " "
						+ mCurrentVY + "totalV " + totalV);

			// If we're not at the target Y, keep going...
			if (mContinueRunning && totalV > 5) {
				postOnAnimation(TagCloudView.this, this);
			} else {
				if (null != mListener) {
					mListener.onSmoothScrollFinished();
				}
			}

		}

		private double getTotalV(float vx, float vy) {
			return Math.sqrt(vx * vx + vy * vy);
		}

		public void stop() {
			mContinueRunning = false;
			removeCallbacks(this);
		}
	}

	public static final int SMOOTH_SCROLL_DURATION_MS = 800;

	@SuppressLint("NewApi")
	public void postOnAnimation(View view, Runnable runnable) {
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			view.postOnAnimation(runnable);
		} else {
			view.postDelayed(runnable, 16);
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("unused")
	private final void smoothScroll(float vx, float vy) {
		smoothScroll(vx, vy, SMOOTH_SCROLL_DURATION_MS, 0,
				new OnSmoothScrollFinishedListener() {

					@Override
					public void onSmoothScrollFinished() {
						// smoothScrollTo(0, SMOOTH_SCROLL_DURATION_MS,
						// DEMO_SCROLL_INTERVAL, null);
					}
				});
	}

	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	private final void smoothScroll(float vx, float vy, long duration,
			long delayMillis, OnSmoothScrollFinishedListener listener) {
		if (null != mCurrentSmoothScrollRunnable) {
			mCurrentSmoothScrollRunnable.stop();
		}

		Interpolator mScrollAnimationInterpolator = null;
		if (null == mScrollAnimationInterpolator) {
			// Default interpolator is a Decelerate Interpolator
			// mScrollAnimationInterpolator = new DecelerateInterpolator();
			mScrollAnimationInterpolator = new LinearInterpolator();
		}
		mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(vx, vy,
				duration, mScrollAnimationInterpolator, listener);
		if (delayMillis > 0) {
			postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
		} else {
			post(mCurrentSmoothScrollRunnable);
		}
	}

	/*
	 * private void scroll(){ mTagCloud.setAngleX(mAngleX);
	 * mTagCloud.setAngleY(mAngleY); mTagCloud.update();
	 * 
	 * Iterator it=mTagCloud.iterator(); Tag tempTag; while (it.hasNext()){
	 * tempTag= (Tag) it.next(); mParams.get(tempTag.getParamNo()).setMargins(
	 * (int) (centerX -shiftLeft + tempTag.getLoc2DX()), (int) (centerY +
	 * tempTag.getLoc2DY()), 0, 0); layoutTag(tempTag);
	 * mTextViews.get(tempTag.getParamNo
	 * ()).setTextSize((int)(tempTag.getTextSize() * tempTag.getScale())); int
	 * mergedColor = Color.argb( (int) (tempTag.getAlpha() * 255), (int)
	 * (tempTag.getColorR() * 255), (int) (tempTag.getColorG() * 255), (int)
	 * (tempTag.getColorB() * 255)); setTextColor();
	 * mTextViews.get(tempTag.getParamNo()).bringToFront(); } }
	 */

	// OnTouchListener onTouchListener = new OnTouchListener() {
	//
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// String keyword = ((TextView)v).getText().toString();
	// boolean onTouchEvent = clickDector.onTouchEvent(event);
	// // if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG+"test",
	// "OnTouchListener onTouchEvent "+onTouchEvent);
	// // if(onTouchEvent && event.getAction() != MotionEvent.ACTION_DOWN){
	// // if(tagClickListener != null){
	// // tagClickListener.onTagClick(keyword);
	// // }
	// //
	// // }
	// return true;
	// }
	// };

	OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String keyword = ((TextView) v).getText().toString();
			notifyEvent(keyword);
		}
	};

	long lastNotify = -1;

	private void notifyEvent(String keyword) {
		int tapTimeout = ViewConfiguration.getTapTimeout();
		long d = System.currentTimeMillis() - lastNotify;
		if (d < 500) {
			return;
		}
		if (Constants.DEBUG)
			if (Constants.DEBUG)
				Log.i(TAG + "test", "notifyEvent tapTimeout " + tapTimeout
						+ " d " + d);
		if (tagClickListener != null) {
			tagClickListener.onTagClick(keyword);
		}
		lastNotify = System.currentTimeMillis();
	}

	private void registerClickEvent(View view, Tag tag) {
		// view.setOnClickListener(OnTagClickListener2(tag.getText()));
		// view.setOnClickListener(onClickListener);
		// view.setOnTouchListener(onTouchListener);
	}

	class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		View source;

		public MyOnGestureListener(View source) {
			this.source = source;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// if (Constants.DEBUG)
			// if(Constants.DEBUG)Log.i(TAG + "test",
			// "GestureDetector onSingleTapConfirmed " + e.getAction()
			// + " " + e.getActionIndex());
			if (source instanceof TextView) {
				String keyword = ((TextView) source).getText().toString();
				notifyEvent(keyword);
			}
			return true;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// if (Constants.DEBUG)
			// if(Constants.DEBUG)Log.i(TAG + "test",
			// "GestureDetector onSingleTapUp " + e.getAction() + "("
			// + MotionEvent.ACTION_UP + " "
			// + e.getActionIndex());
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (Constants.DEBUG)
				if (Constants.DEBUG)
					Log.i(TAG + "test", "GestureDetector onDoubleTap ");
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			if (Constants.DEBUG)
				if (Constants.DEBUG)
					Log.i(TAG + "test", "GestureDetector onDoubleTapEvent ");
			return super.onDoubleTapEvent(e);
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onDown(MotionEvent e) {
			// if (Constants.DEBUG)
			// if(Constants.DEBUG)Log.i(TAG + "test",
			// "GestureDetector onDown " + e.getAction() + " ("
			// + MotionEvent.ACTION_DOWN + " "
			// + e.getActionIndex());
			return true;
		}

	};

	private void initClickEvent() {
		// new GestureDetector
		// Detects various gestures and events using the supplied MotionEvents.
		// The GestureDetector.OnGestureListener callback will notify users when
		// a particular motion event has occurred. This class should only be
		// used with MotionEvents reported via touch (don't use for trackball
		// events). To use this class:
		// •Create an instance of the GestureDetector for your View
		// •In the onTouchEvent(MotionEvent) method ensure you call
		// onTouchEvent(MotionEvent). The methods defined in your callback will
		// be executed when the events occur.

		// clickDector = new GestureDetector(getContext(), new
		// GestureDetector.SimpleOnGestureListener(){
		//
		// @Override
		// public boolean onSingleTapConfirmed(MotionEvent e) {
		// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG+"test",
		// "GestureDetector onSingleTapConfirmed "+e.getAction()+" "+e.getActionIndex());
		//
		// return true ;
		// }
		//
		// @Override
		// public boolean onSingleTapUp(MotionEvent e) {
		// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG+"test",
		// "GestureDetector onSingleTapUp "+e.getAction()+"("+MotionEvent.ACTION_UP+" "+e.getActionIndex());
		// return super.onSingleTapUp(e);
		// }
		//
		// @Override
		// public boolean onDoubleTap(MotionEvent e) {
		// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG+"test",
		// "GestureDetector onDoubleTap ");
		// return super.onDoubleTap(e);
		// }
		//
		// @Override
		// public boolean onDoubleTapEvent(MotionEvent e) {
		// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG+"test",
		// "GestureDetector onDoubleTapEvent ");
		// return super.onDoubleTapEvent(e);
		// }
		//
		// @Override
		// public boolean onDown(MotionEvent e) {
		// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG+"test",
		// "GestureDetector onDown "+e.getAction()
		// +" ("+MotionEvent.ACTION_DOWN+" "+e.getActionIndex());
		// return true;
		// }
		//
		// });
	}

    public interface OnTagFilngListener{

        public void onFling();
    }

    private OnTagFilngListener mFlingListener;

    public void setOnTagFlingListener(OnTagFilngListener listener){
        mFlingListener = listener;
    }

	private void initGesture() {
		this.dector = new GestureDetector(mContext,

		new GestureDetector.SimpleOnGestureListener() {

			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				final float ev1x = e1.getX();
				final float ev1y = e1.getY();
				final float ev2x = e2.getX();
				final float ev2y = e2.getY();
				final float xdiff = Math.abs(ev1x - ev2x);
				final float ydiff = Math.abs(ev1y - ev2y);
				final float xvelocity = Math.abs(velocityX);
				final float yvelocity = Math.abs(velocityY);

				if (Constants.DEBUG)
					if (Constants.DEBUG)
						Log.i(TAG, "onFling vx " + velocityX + " vy "
								+ velocityY + " dx " + xdiff + " dy " + ydiff);
				if ((xvelocity > SWIPE_MIN_VELOCITY && xdiff > SWIPE_MIN_DISTANCE)
						|| (xvelocity > SWIPE_MIN_VELOCITY && ydiff > SWIPE_MIN_DISTANCE)) {
					// rotate elements depending on how far the selection point
					// is from center of cloud
					float dx = ev2x - centerX;
					float dy = ev2y - centerY;

					mAngleX = mAngleX * velocityY; // ( dy/radius) *tspeed *
													// TOUCH_SCALE_FACTOR;
					mAngleY = mAngleY * velocityY;// (-dx/radius) *tspeed *
													// TOUCH_SCALE_FACTOR;

                    if(xdiff > 20){
                        if(mFlingListener != null){
                            mFlingListener.onFling();
                        }
                    }

//					smoothScroll(velocityX, velocityY);
					if (true)
						return true;

					// ////////////////////////////////////
//					mTagCloud.setAngleX(mAngleX);
//					mTagCloud.setAngleY(mAngleY);
//					mTagCloud.update();

					Iterator it = mTagCloud.iterator();
					Tag tempTag;
					while (it.hasNext()) {
						tempTag = (Tag) it.next();
						params.get(tempTag.getParamNo()).setMargins(
								(int) (centerX - shiftLeft + tempTag
										.getLoc2DX()),
								(int) (centerY + tempTag.getLoc2DY()), 0, 0);
						textViews.get(tempTag.getParamNo()).setTextSize(
								(int) (tempTag.getTextSize() * tempTag
										.getScale()));
						/*
						 * int mergedColor = Color.argb( (int)
						 * (tempTag.getAlpha() * 255), (int)
						 * (tempTag.getColorR() * 255), (int)
						 * (tempTag.getColorG() * 255), (int)
						 * (tempTag.getColorB() * 255)); setTextColor();
						 */
						textViews.get(tempTag.getParamNo()).bringToFront();
					}
					String s = "(xdiff,ydiff)=(%s,%s),e1(x,y)=(%s,%s),e2(x,y)=(%s,%s),(velocityX,velocityY)=(%s,%s),"
							+ "(angleX,angleY)=(%s,%s),(ev2x - centerX,ev2y - centerY)=(%s,%s)";
					if (Constants.DEBUG)
						Log.d(TAG,
								"[onFling]"
										+ String.format(s, xdiff, ydiff,
												e1.getX(), e1.getY(),
												e2.getX(), e2.getY(),
												velocityX, velocityY, mAngleX,
												mAngleY, dx, dy));
					return true;

				}
				return false;

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				if (Constants.DEBUG)
					if (Constants.DEBUG)
						Log.i(TAG, "onScroll distanceX " + distanceX
								+ " distanceY " + distanceY);
				if ((Math.abs(distanceX) > 2 || Math.abs(distanceY) > 2)) {

					float dx = -distanceX * 20;// ev2x - centerX;
					float dy = -distanceY * 20;// ev2y - centerY;
					mAngleX = (dy / radius) * tspeed * TOUCH_SCALE_FACTOR;
					mAngleY = (-dx / radius) * tspeed * TOUCH_SCALE_FACTOR;

					mTagCloud.setAngleX(mAngleX);
					mTagCloud.setAngleY(mAngleY);
					mTagCloud.update();

					Iterator it = mTagCloud.iterator();
					Tag tempTag;
					while (null != it && it.hasNext()) {

						tempTag = (Tag) it.next();
//						layoutTag(tempTag);
					}
					return true;
				}
				return super.onScroll(e1, e2, distanceX, distanceY);

			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
//				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				return super.onDown(e);
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		boolean flag = dector.onTouchEvent(e);
		if (!flag) {
			flag = super.dispatchTouchEvent(e);
		}
		return flag;

	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (true) {
			return true;
		}
		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// rotate elements depending on how far the selection point is from
			// center of cloud
			float dx = x - centerX;
			float dy = y - centerY;
			mAngleX = (dy / radius) * tspeed * TOUCH_SCALE_FACTOR;
			mAngleY = (-dx / radius) * tspeed * TOUCH_SCALE_FACTOR;

			mTagCloud.setAngleX(mAngleX);
			mTagCloud.setAngleY(mAngleY);
			mTagCloud.update();

			Iterator it = mTagCloud.iterator();
			Tag tempTag;
			while (it.hasNext()) {
				tempTag = (Tag) it.next();

                if(dy > 20 && mFlingListener != null){
                    mFlingListener.onFling();
                }

				/*
				 * mParams.get(tempTag.getParamNo()).setMargins( (int) (centerX
				 * -shiftLeft + tempTag.getLoc2DX()), (int) (centerY +
				 * tempTag.getLoc2DY()), 0, 0);
				 */
//				layoutTag(tempTag);
//				textViews.get(tempTag.getParamNo()).setTextSize(
//						(int) (tempTag.getTextSize() * tempTag.getScale()));
//				/*
//				 * int mergedColor = Color.argb( (int) (tempTag.getAlpha() *
//				 * 255), (int) (tempTag.getColorR() * 255), (int)
//				 * (tempTag.getColorG() * 255), (int) (tempTag.getColorB() *
//				 * 255)); setTextColor();
//				 */
//				textViews.get(tempTag.getParamNo()).bringToFront();
			}
			// if (Constants.DEBUG)if(Constants.DEBUG)Log.i(TAG,
			// "onTouchEvent x:"+x+" y:"+y
			// +" dx:"+dx+" dy:"+dy +" mAngleX:"+mAngleX+" mAngleY:"+mAngleY);

			break;
		case MotionEvent.ACTION_UP: // now it is clicked!!!!
			dx = x - centerX;
			dy = y - centerY;
			break;

		}

		return true;
	}

	String urlMaker(String url) {
		if ((url.substring(0, 7).equalsIgnoreCase("http://"))
				|| (url.substring(0, 8).equalsIgnoreCase("https://")))
			return url;
		else
			return "http://" + url;
	}

	// the filter function makes sure that there all elements are having unique
	// Text field:
	List<Tag> Filter(List<Tag> tagList) {
		// current implementation is O(n^2) but since the number of tags are not
		// that many,
		// it is acceptable.
		List<Tag> tempTagList = new ArrayList();
		Iterator itr = tagList.iterator();
		Iterator itrInternal;
		Tag tempTag1, tempTag2;
		// for all elements of TagList
		while (itr.hasNext()) {
			tempTag1 = (Tag) (itr.next());
			boolean found = false;
			// go over all elements of temoTagList
			itrInternal = tempTagList.iterator();
			while (itrInternal.hasNext()) {
				tempTag2 = (Tag) (itrInternal.next());
				if (tempTag2.getText().equalsIgnoreCase(tempTag1.getText())) {
					found = true;
					break;
				}
			}
			if (found == false)
				tempTagList.add(tempTag1);
		}
		return tempTagList;
	}

	// for handling the click on the tags
	// onclick open the tag url in a new window. Back button will bring you back
	// to TagCloud
	View.OnClickListener OnTagClickListener2(final String text) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tagClickListener != null) {
					tagClickListener.onTagClick(text);
				}
			}
		};
	}

	private final float TOUCH_SCALE_FACTOR = .8f;

	private final float TRACKBALL_SCALE_FACTOR = 10;

	private float tspeed;

	private TagCloud mTagCloud;

	private float mAngleX = 0;

	private float mAngleY = 0;

	private float centerX, centerY;

	private float radius;

	private Context mContext;

	private int textSizeMin, textSizeMax, paddingV, paddingH;

	private List<MyTextView> textViews;

	private List<RelativeLayout.LayoutParams> params;

	private int shiftLeft;

	float f = tspeed * TOUCH_SCALE_FACTOR;

	class MyTextView extends TextView {
		GestureDetector detector = null;

		public MyTextView(Context context) {
			super(context);

			this.detector = new GestureDetector(getContext(),
					new MyOnGestureListener(this));
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// ViewGroup view = (ViewGroup) getParent();
			// view.requestDisallowInterceptTouchEvent(true);
			detector.onTouchEvent(event);
			return true;
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			return super.dispatchTouchEvent(event);
		}

	}

	private TagClickListener tagClickListener;

	private GestureDetector clickDector;

	public void setTagClickListener(TagClickListener tagClickListener) {
		this.tagClickListener = tagClickListener;
	}

	public static interface TagClickListener {
		void onTagClick(String tag);
	}

}
