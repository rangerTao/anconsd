package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;


public class NewSegmentedLayout extends RelativeLayout implements OnCheckedChangeListener {

	private static final String TAG = "NewSegmentedLayout";
	private boolean debug = false ;
	private com.ranger.bmaterials.view.NewSegmentedLayout.OnCheckedChangeListener mOnCheckedChangeListener;
	public NewSegmentedLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		initIndicatorLayout();
		log("onLayout");
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initIndicatorWidth();
		log("onMeasure");
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		log("onFinishInflate");
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		log("onAttachedToWindow");
	}
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		log("onWindowFocusChanged");
	}
	
	
	public interface OnCheckedChangeListener {
		/**
		 * <p>
		 * Called when the checked radio button has changed. When the selection
		 * is cleared, checkedId is -1.
		 * </p>
		 * 
		 * @param group
		 *            the group in which the checked radio button has changed
		 * @param checkedId
		 *            the unique identifier of the newly checked radio button
		 */
		public void onCheckedChanged(NewSegmentedLayout group, int checkedId);
	}
	
	 public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
	        mOnCheckedChangeListener = listener;
	    }
	 
	 public void check(int index){
		 RadioGroup radioGroup = getRadioGroup();
		 if(this.mOnCheckedChangeListener != null){
			 OnCheckedChangeListener listener = this.mOnCheckedChangeListener;
			 setOnCheckedChangeListener(null);
			 RadioButton child = (RadioButton) radioGroup.getChildAt(index);
			 radioGroup.check(child.getId());
			 setOnCheckedChangeListener(listener);
		 }else{
			 radioGroup.check(index);
		 }
		 
		 
	 }

	//////////////////////////////////////////////////////////////////
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(mOnCheckedChangeListener != null){
			mOnCheckedChangeListener.onCheckedChanged(this, checkedId);
		}
		RadioGroup radioGroup =  getRadioGroup();
		int childCount = radioGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			if(checkedId == radioGroup.getChildAt(i).getId()){
				if(indicatorAnimation){
					//1
					translate(currentSegment, i);
				}else{
					////2
					layoutIndicator(offSet(i));
				}
				
				currentSegment = i ;
				break;
			}
		}
		
		
	}
	//////////////////////////////////////////////////////////////////
	
	
	//////////////////////////////////////////////////////////////////
	private boolean initlized = false ;
	private int currentSegment = 0 ;
	private int segmentCount = 0 ;
	private int defaultSegmet = 0 ;
	public int getCurrentSegment() {
		return currentSegment;
	}
	public int getSegmentCount() {
		return segmentCount;
	}
	
	public int getDefaultSegmet() {
		return defaultSegmet;
	}

	//////////////////////////////////////////////////////////////////
	
	
	private boolean measured = false ;
	private void initIndicatorWidth(){
		if(measured){
			return ;
		}
		ImageView indicator = getIndicator();
		RelativeLayout.LayoutParams lp  = (LayoutParams) indicator.getLayoutParams() ;
		lp.width = getMeasuredWidth() / childCount ;
		lp.bottomMargin = 0 ;
//		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		indicator.setLayoutParams(lp) ;
		measured = true; 
	}
	private void initIndicatorLayout(){
		if(initlized ){
			return ;
		}
		int totalWidth = getMeasuredWidth();
		if(totalWidth <= 0){
			return ;
		}
		
		RadioGroup radioGroup = getRadioGroup();
		segmentCount = radioGroup.getChildCount();
		this.defaultSegmet = getDefaultSegmet(radioGroup);
		if(segmentCount == 0){
			return ;
		}
		if("fill".equals(indicatorWidth)){
			
		}else {
			int offSet = offSet(defaultSegmet);
			layoutIndicator(offSet);
		}
		if (Constants.DEBUG)Log.i(TAG, " offSet(0):"+offSet(0)+" offset(1):"+offSet(1)+" offSet(2):"+offSet(2));
		currentSegment = defaultSegmet ;
		initlized = true ;
	}
	
	public int getDefaultSegmet(RadioGroup radioGroup){
		int checkedId = radioGroup.getCheckedRadioButtonId();
		if(checkedId <= 0){
			return 0 ;
		}
		int ret = 0 ;
		int childCount = radioGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			if(checkedId == radioGroup.getChildAt(i).getId()){
				ret = i;
			}
			if (Constants.DEBUG)Log.i(TAG, "checkedId:"+checkedId+" child "+i+" id:"+radioGroup.getChildAt(i).getId());
		}
		if (Constants.DEBUG)Log.i(TAG, "checkedId:"+checkedId+" child return:"+ret);
		return ret ;
	}
	private ImageView getIndicator(){
		ImageView iv = (ImageView) findViewById(ID_INDICATOR);
		return iv ;
	}
	private RadioGroup getRadioGroup(){
		RadioGroup view = (RadioGroup) findViewById(ID_RADIOGROUP);
		return view ;
	}
	private void layoutIndicator(int offset){
		ImageView indicator = getIndicator();
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) indicator
				.getLayoutParams();
		//int offSet = offSet(0);
		if(debug)Log.d(TAG, "lp.leftMargin:"+lp.leftMargin+",offSet:"+offset+",lp.topMargin:"+lp.topMargin);
		lp.leftMargin = offset ;
		
		indicator.setLayoutParams(lp);
	}
	/**
	 * 锟斤拷锟絧arent锟斤拷偏锟斤拷
	 * @param childIndex
	 * @return
	 */
	private int offSet(int childIndex){
		RadioGroup radioGroup = getRadioGroup();
		View child = radioGroup.getChildAt(childIndex);
		ImageView indicator = getIndicator();
		int o = child.getLeft() ;
		if (Constants.DEBUG)Log.i(TAG, "child left:"+child.getLeft());
		if(o == 0){
			for (int i = 0; i < childIndex; i++) {
				o += radioGroup.getChildAt(i).getMeasuredWidth();
			}
		}
		/**
		 * MeasuredWidths锟斤拷锟斤拷onMeasure锟阶讹拷锟斤拷桑锟斤拷锟絞etLeft锟斤拷锟斤拷onLayout锟阶讹拷锟斤拷傻模锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷onMeasure锟斤拷锟矫此凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷getLeft
		 */
		//return child.getLeft()+ (child.getMeasuredWidth() - indicator.getMeasuredWidth()) / 2 ;
		return o+ (child.getMeasuredWidth() - indicator.getMeasuredWidth()) / 2 ;
	}
	/**
	 * 锟斤拷锟斤拷锟斤拷view锟侥撅拷跃锟斤拷锟�
	 * @param seg1
	 * @param seg2
	 * @return
	 */
	private int distanceOf(int seg1,int seg2){
		RadioGroup radioGroup =getRadioGroup();
		
		View child1 = radioGroup.getChildAt(seg1);
		View child2 = radioGroup.getChildAt(seg2);
		int dis = 0 ;
		if(seg2 > seg1){
			dis = child2.getLeft() - child1.getRight() +(child1.getMeasuredWidth() + child2.getMeasuredWidth())/2;
		}else if(seg2 < seg1){
			dis = child1.getLeft() - child2.getRight() +(child1.getMeasuredWidth() + child2.getMeasuredWidth())/2;
		}
		return dis ;
	}
	
	/**
	 * 锟角凤拷锟斤拷锟斤拷谋锟絭iew锟斤拷位锟斤拷
	 */
	private boolean layoutAfter = false ;
	
	private void translate(int from,int to){
		int fromX = 0;
		int toX = 0 ;
		if(!layoutAfter){
			if(from < to){
				fromX = distanceOf(0, from);
				toX = distanceOf(0, to);
			}else if(from > to){
				fromX = distanceOf(0, from);
				toX = distanceOf(0, to);
			}else{
				return ;
			}
		}else {
			fromX = 0;
			if(from < to){
				toX = distanceOf(from, to);
			}else if(from > to){
				toX = -distanceOf(from, to);
			}else{
				return ;
			}
			
		}
		if(debug)if (Constants.DEBUG)Log.i(TAG, "translate:"+from+"-->"+to+";"+" "+fromX +"-->"+toX);
		TranslateAnimation anim = new TranslateAnimation(fromX, toX, 0.0F, 0.0F);
		//localTranslateAnimation.setFillEnabled(false);
		if(!layoutAfter)anim.setFillAfter(true);
		anim.setDuration(160L);
		if(layoutAfter)anim.setAnimationListener(new MyAnimationListener(to));
		
		ImageView indicator = getIndicator();
		indicator.startAnimation(anim);
	}
	

	class MyAnimationListener implements Animation.AnimationListener {
		private int finalLocation = 0 ;
		public MyAnimationListener(int finalIndex) {
			this.finalLocation = offSet(finalIndex) ;
		}
		public void onAnimationEnd(Animation paramAnimation) {
			//clearAnimation();
			layoutIndicator(finalLocation);
		}

		public void onAnimationRepeat(Animation paramAnimation) {
		}

		public void onAnimationStart(Animation paramAnimation) {
		}
	}
	private int childCount ;
	private int ID_RADIOGROUP = 0x22222;
	private int ID_INDICATOR = 0x11111;
	private void init(Context context,AttributeSet attrs){
		parseParams(attrs);
		RadioGroup rg = (RadioGroup) View.inflate(getContext(), layoutId, null);
		
		this.childCount = rg.getChildCount();
		rg.setOnCheckedChangeListener(this);
		RelativeLayout.LayoutParams rgLp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		addView(rg,rgLp);
		int rgId = rg.getId();
		if(rgId <= 0){
			rgId = ID_RADIOGROUP ;
			rg.setId(rgId);
		}else {
			ID_RADIOGROUP = rgId ;
		}
		
		ImageView imageView = new ImageView(context);
		imageView.setId(ID_INDICATOR);
		
		
		//imageView.setImageResource(R.drawable.segment_indicator);
		//imageView.setImageResource(IndicatorId);
		RelativeLayout.LayoutParams ivLp = null ;
		ivLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		/*if("wrap".equals(indicatorWidth)){
			ivLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		}else if("fill".equals(indicatorWidth)){
			ivLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		}*/
		if("bottom".equals(indicatorGravity)){
			ivLp.addRule(RelativeLayout.BELOW, rgId);
			ivLp.topMargin = (int) (5 * context.getResources().getDisplayMetrics().density) ;
			imageView.setImageResource(IndicatorId);
			
		}else if("overlap".equals(indicatorGravity)){
			this.setGravity(Gravity.BOTTOM);
			imageView.setBackgroundResource(IndicatorId);
		}
		addView(imageView, ivLp);
		log("init");
	}
	
	private int layoutId ;
	private int IndicatorId ;
	private String indicatorWidth ;
	private String indicatorGravity ;
	private boolean indicatorAnimation ;
	/** 
	 * custom_segment:segment_layout="@layout/square_segment_control"
	 * custom_segment:segment_indicator="@drawable/topbar_select"
       custom_segment:indicator_grivaty="overlap"
       custom_segment:indicator_width="fill"
       custom_segment:indicator_animation="false"
       custom_segment:indicator_animation="false"
	 * @param attrs
	 * @return
	 */
	private void parseParams(AttributeSet attrs) {
		String namespace = "http://duoku.com/apk/res/android";
		this.layoutId = attrs.getAttributeResourceValue(namespace, "segment_layout", 0);
		this.IndicatorId = attrs.getAttributeResourceValue(namespace, "segment_indicator", 0);
		this.indicatorGravity = attrs.getAttributeValue(namespace,"indicator_grivaty");
		this.indicatorWidth = attrs.getAttributeValue(namespace,"indicator_width");
		this.indicatorAnimation = attrs.getAttributeBooleanValue(namespace, "indicator_animation", false);
	}
	
	private void log(String prefix){
		if(!debug){
			return ;
		}
		View son = this;
		if (Constants.DEBUG)Log.i(TAG, "["+prefix+"]son:"+son);
		View child1 = ((ViewGroup)son).getChildAt(0);
		View child2 = ((ViewGroup)son).getChildAt(1);
		if (Constants.DEBUG)Log.i(TAG, "["+prefix+"]child1:"+child1+";child2:"+child2);
		
		if(child1 == null || child2 == null){
			return ;
		}
		int measuredWidth1 = child1.getMeasuredWidth();
		int measuredWidth2 = child2.getMeasuredWidth();
		
		int width1 = child1.getWidth();
		int width2 = child2.getWidth();
		if (Constants.DEBUG)Log.i(TAG, "["+prefix+"]measuredWidth1:"+measuredWidth1+";width1:"+width1);
		if (Constants.DEBUG)Log.i(TAG, "["+prefix+"]measuredWidth2:"+measuredWidth2+";width2:"+width2);
	}

	
}
