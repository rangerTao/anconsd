package com.ranger.bmaterials.view;


import com.ranger.bmaterials.app.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class IndicatorWorkspace extends ViewGroup {
	Context mContext;
	
	public static final int TOUCH_STATE_BEGIN = 1;
	public static final int TOUCH_STATE_SCROLLING = 2;
	public static final int TOUCH_STATE_STOPED = 3;
	public static int mTouchState = TOUCH_STATE_STOPED;
	
	public int diff;
	public float x_move_first;
	public float x_move_second;
	public float x_down;
	public float x_up;
	public long time_x_down;
	public long time_x_up;
	public int current_screen = 1;
	public int page_count = 4;
	
	float x;
	float y;
	
	public int old_diff;
	
	public static final int SCREEN_IS_NOT_MOVING = 0;
	public static final int SCREEN_IS_MOVING = 1;
	public static int SCREEN_STATE = SCREEN_IS_NOT_MOVING;
	
	private static final int SCROLL_TO_OLD_DIFF = 0;
	private static final int SCROLL_TO_DIFF = 1;
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCROLL_TO_OLD_DIFF:
				scrollTo(old_diff, 0);
				break;
			case SCROLL_TO_DIFF:
				scrollTo(diff, 0);
				break;

			}
			super.handleMessage(msg);
		}
		
	};

	public IndicatorWorkspace(Context context) {
		super(context);
		mContext = context;
	}
	public IndicatorWorkspace(Context context,AttributeSet attr){
		super(context,attr);
		mContext = context;
		//System.out.println("----------------"+context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();  
	    int childLeft = 0;  
	    //横向平铺CellLayout   
	    for(int i=0; i<count; i++){  
	        View child = getChildAt(i);  
	        
	         int width = child.getMeasuredWidth(); 
	         int height = child.getMeasuredHeight();
	       
	        if(child.getVisibility() != GONE){
	        	child.setVisibility(View.VISIBLE);
				//child.measure(r-l, b-t);
	            child.layout(childLeft, 0, childLeft+width, height);  
	            childLeft += width;  
	        }  
	    }  

	}
	
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		for (int i = 0; i < getChildCount(); i++)
		{
		View child = getChildAt(i);
		child.measure(widthMeasureSpec,heightMeasureSpec);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}
	
	//int slowly_mode;
	public void scrollTo(int x,int y,boolean slowly){
		final int slowly_mode = old_diff - x;
		if(slowly_mode == 0){
			scrollTo(x,y);
			return;
		}
		if(slowly){
			new Thread(new Runnable(){

				@Override
				public void run() {
					SCREEN_STATE = SCREEN_IS_MOVING;
					if(slowly_mode > 0){
						//--
						//Log.i("whb", "modle ---");
						while(true){
							old_diff-=10;
							if(old_diff > diff){
								//scrollTo(old_diff, 0);
								mHandler.sendEmptyMessage(SCROLL_TO_OLD_DIFF);
							}else{
								//scrollTo(diff, 0);
								mHandler.sendEmptyMessage(SCROLL_TO_DIFF);
								break;
							}
							try {
								Thread.sleep(4);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}else if(slowly_mode < 0){
						//++
						//Log.i("whb", "modle +++");
						while(true){
							old_diff+=10;
							if(old_diff < diff)
								//scrollTo(old_diff, 0);
								mHandler.sendEmptyMessage(SCROLL_TO_OLD_DIFF);
							else{
								//scrollTo(diff, 0);
								mHandler.sendEmptyMessage(SCROLL_TO_DIFF);
								break;
							}
							try {
								Thread.sleep(4);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					SCREEN_STATE = SCREEN_IS_NOT_MOVING;
					
				}}){}.start();
			
			
			
			
		}else{
			scrollTo(x, y);
		}
	}
	
	public void setCurrentScreen(int currentScreen){
		if(Constants.DEBUG)System.out.println("myWorkspace getWidth:"+getWidth());
		//scrollTo(currentScreen*getWidth(),0);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//System.out.println("on workspace intercept touch "+ev.getAction()+".."+this);
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			mTouchState = TOUCH_STATE_BEGIN;
			x_down = ev.getX();
			x_move_first = ev.getX();
			time_x_down = System.currentTimeMillis();
			
			x = ev.getRawX();
			y = ev.getRawY();
			//Log.i("wwwww", "GameWorkspace intercept down");
			
		}else if(ev.getAction() == MotionEvent.ACTION_UP){
			mTouchState = TOUCH_STATE_STOPED;
		}else if(ev.getAction() == MotionEvent.ACTION_MOVE && mTouchState != TOUCH_STATE_STOPED){ 
			//System.out.println(".."+this);
			/*Log.i("wwwww", "GameWorkspace intercept move out");
			if(Math.abs(x - ev.getRawX()) > 20 && Math.abs(y - ev.getRawY()) < 20 && GameRecommendWorkspace.mTouchState == GameRecommendWorkspace.TOUCH_STATE_STOPED){
				//Log.i("wwwww", "GameWorkspace intercept move");
			return true;  
			}*/
		} 
		return super.onInterceptTouchEvent(ev);
	}
	
}
