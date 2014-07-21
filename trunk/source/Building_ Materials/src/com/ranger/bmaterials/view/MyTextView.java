package com.ranger.bmaterials.view;

import java.util.ArrayList;
import java.util.List;

import com.ranger.bmaterials.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MyTextView extends View {
	
	private WindowManager mWindowManager;
	private int displayWidth;
	private int displayHeight;
	private int density;
	
	//public static Bitmap mBitmap;
	
	Context mContext;
	
	Paint mPaint;
	String mText;
	public int mTextViewHeight;
	
	float paddingLeft;
	float paddingRight;
	float paddingTop;
	float paddingBottom;
	float linespace;
	
	float mVisibleWidth;
	float mTextSize;
	
	int current_line = 1;
	
	Canvas mCanvas;
	
	boolean isDrawn;
	
	public float current_y_pos;
	
	Rect rect;
	
	int auto_move_step = 8;
	
	ArrayList<String> lines;
	//ArrayList<ArrayList<String>> prograph = new ArrayList<ArrayList<String>>();
	
	private final static int VIEW_INVALIDATE_CALL = 1000;
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIEW_INVALIDATE_CALL:
				
				if(mStep > 70)
					auto_move_step = 9;
				else if(mStep > 65)
					auto_move_step = 8;
				else if(mStep > 60)
					auto_move_step = 7;
				else if(mStep > 55)
					auto_move_step = 6;
				else if(mStep > 50)
					auto_move_step = 6;
				else if(mStep > 45)
					auto_move_step = 5;
				else if(mStep > 40)
					auto_move_step = 5;
				else if(mStep > 35)
					auto_move_step = 4;
				else if(mStep > 30)
					auto_move_step = 4;
				else if(mStep > 25)
					auto_move_step = 3;
				else if(mStep > 20)
					auto_move_step = 3;
				else if(mStep > 15)
					auto_move_step = 2;
				else if(mStep > 10)
					auto_move_step = 2;
				else if(mStep > 5)
					auto_move_step = 1;
				else if(mStep > 0)
					auto_move_step = 1;
				
				int direct = msg.arg1;
				if(direct > 0){
					mStep--;
					current_y_pos -= auto_move_step;
				}else if(direct < 0){
					mStep--;
					current_y_pos += auto_move_step;
				}
				MyTextView.this.invalidate();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();   
		mWindowManager.getDefaultDisplay().getMetrics(dm);   
        displayWidth = dm.widthPixels;   
        displayHeight = dm.heightPixels;
        density = dm.densityDpi;
		
        mContext = context;
		mPaint = new Paint();
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyTextView);
		
		int textColor = array.getColor(R.styleable.MyTextView_textColor, 0XFFFF0000);
        float textSize = array.getDimension(R.styleable.MyTextView_textSize, 36);   
        mPaint.setColor(textColor);   
        mPaint.setTextSize(textSize);
        mTextSize = textSize;
        
        linespace = array.getDimension(R.styleable.MyTextView_linespace, 0);
        paddingLeft = array.getDimension(R.styleable.MyTextView_paddingLeft, 0);
        paddingRight = array.getDimension(R.styleable.MyTextView_paddingRight, 0);
        paddingTop = array.getDimension(R.styleable.MyTextView_paddingTop, 0);
        paddingBottom = array.getDimension(R.styleable.MyTextView_paddingBottom, 0);
        
        mVisibleWidth = displayWidth - paddingLeft - paddingRight;
           
        array.recycle();
	}

	public MyTextView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	
		if(mCanvas == null)
			mCanvas = canvas;
		super.onDraw(canvas);
		current_line = 1;
		
		canvas.drawColor(Color.WHITE);
		
		rect = new Rect(0,(int)(-current_y_pos),displayWidth,(int)(displayHeight+(-current_y_pos)));
	
		//Log.i("WWWWW", "onDraw.."+"...c_y_pos:"+current_y_pos);
				for(int i = 0; i< lines.size();i++){
					
					if(lines.size() == 1){
						if(current_line == 1){
							drawTextLine(lines.get(i),paddingLeft,paddingTop+mTextSize);
						}else{
							drawTextLine(lines.get(i),paddingLeft,paddingTop+(current_line -1)*(linespace)+current_line*mTextSize);
						}
						
					}else{
						/*if(i == lines.size()-1){
							drawTextLine(lines.get(i),paddingLeft,paddingTop+(current_line -1)*(linespace)+current_line*mTextSize,true);
						}else{*/
							if(current_line == 1){
								drawTextLine(lines.get(i),paddingLeft,paddingTop+mTextSize);
							}else{
								drawTextLine(lines.get(i),paddingLeft,paddingTop+(current_line -1)*(linespace)+current_line*mTextSize);
							}
						//}
					}
				}
	
	}

	public void setText(String text){
		//mText = text;
		if(text != null && !"".equals(text.trim())){
			String [] texts = text.split("\n");
			
			lines = new ArrayList<String>();
			for(String s:texts){
				StringBuilder sb = new StringBuilder();
				sb.append(s);
				while(sb.length() > 0){
					int nSize = mPaint.breakText(sb.toString(), true, mVisibleWidth, null);
					String line = sb.substring(0, nSize);
					sb.delete(0, nSize);
					if(sb.length() == 0)
						line = line+"\n";
				    lines.add(line);
				    mTextViewHeight += (mTextSize+linespace);
				}
				//prograph.add(lines);
			}
			
			mTextViewHeight -= linespace;
			mTextViewHeight = (int) (mTextViewHeight + paddingTop + paddingBottom);
			
		}
		//Log.i("WWWWW", "prograph:"+prograph.size());
	}
	
	private void drawTextLine(String line, float x, float y){
		/*if(mBitmap == null){
			mBitmap = Bitmap.createBitmap(displayWidth, mTextViewHeight,
                    Bitmap.Config.ARGB_8888);
			
			mCanvas = new Canvas(mBitmap);
		}*/
		
		current_line++;
		boolean isLastLine = line.endsWith("\n");
		
		if(isLastLine)
			line = line.replace("\n", "");
	
		float space = mVisibleWidth - mPaint.measureText(line);
		
		if(isLastLine){
			space = 0;
		}else{
			space = space / line.length();
		}
		
		float charWidth = 0.0f;
		//float[] w_character = new float[1];
		if(!rect.contains((int)x, (int)(y)))
			return;
		//Log.i("WWWWW", "============line:"+line.length()+"...y:"+y+"...."+line);
        for (int i = 0; i < line.length(); i++)
        {
        	//mPaint.drawString(x, y, line, i, 1);
            //charWidth = mPaint.getStringWidth(line, i, 1);
        	//Log.i("WWWWW", "i....."+i+"....x:"+x);
        	
        	
        	
        	mCanvas.drawText(line, i, i+1, x, y+current_y_pos, mPaint);
        	
        	charWidth = mPaint.measureText(line, i, i+1);
            x += space + charWidth;
        }
	}
	
	int mStep;
	public void autoScroll(final float fromY,final float toY,int step){
		
		final float direct = fromY - toY;
		//Log.i("WWWWW", "direct..."+direct);
		if(step > 90)
			mStep = 90;
		else
			mStep = step;
		
		
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				Log.i("WWWWW", "step:"+mStep);
				if(direct > 0){
					
					while(mStep > 0){
						
						//Log.i("WWWWW", "mStep......111:"+mStep);
						
						Message m = new Message();
						m.what = VIEW_INVALIDATE_CALL;
						m.arg1 = (int)direct;
						mHandler.sendMessage(m);
						
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}else if(direct < 0){
					
					while(mStep > 0){
						
						//Log.i("WWWWW", "mStep......222:"+mStep);
						
						Message m = new Message();
						m.what = VIEW_INVALIDATE_CALL;
						m.arg1 = (int)direct;
						mHandler.sendMessage(m);
						
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}}){}.start();
		
	}
	
}
