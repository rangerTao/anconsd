package com.ranger.bmaterials.view;

import java.util.ArrayList;
import java.util.List;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MyTextView2 extends View {
	
	private WindowManager mWindowManager;
	private int displayWidth;
	private int displayHeight;
	private int density;
	
	//public static Bitmap mBitmap;
	
	Context mContext;
	
	Paint mPaint;
	//String mText;
	public int mTextViewHeight;
	
	float paddingLeft;
	float paddingRight;
	float paddingTop;
	float paddingBottom;
	float linespace;
	
	float mVisibleWidth;
	float mTextSize;
	public int line_count;
	
	int current_line = 1;
	
	Canvas mCanvas;
	
	int bg_color;
	
	boolean isDrawn;
	
	public boolean isOpen;
	
	public float current_y_pos;
	
	//Rect rect;
	
	int auto_move_step = 8;
	
	ArrayList<String> lines;
	//ArrayList<ArrayList<String>> prograph = new ArrayList<ArrayList<String>>();
	
	private final static int VIEW_INVALIDATE_CALL = 1000;
	

	public MyTextView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyTextView2(Context context, AttributeSet attrs) {
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
		bg_color = array.getColor(R.styleable.MyTextView_backgroundColor, 0XFFFFFFFF);
        float textSize = array.getDimension(R.styleable.MyTextView_textSize, 36);   
        mPaint.setColor(textColor);   
        mPaint.setTextSize(textSize);
        mTextSize = textSize;
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setAntiAlias(true);
        
        linespace = UIUtil.dip2px(mContext, array.getDimension(R.styleable.MyTextView_linespace, 0));
        paddingLeft = UIUtil.dip2px(mContext, array.getDimension(R.styleable.MyTextView_paddingLeft, 0));
        paddingRight = UIUtil.dip2px(mContext, array.getDimension(R.styleable.MyTextView_paddingRight, 0));
        paddingTop = UIUtil.dip2px(mContext, array.getDimension(R.styleable.MyTextView_paddingTop, 0));
        paddingBottom = UIUtil.dip2px(mContext, array.getDimension(R.styleable.MyTextView_paddingBottom, 0));
        
        mVisibleWidth = displayWidth - paddingLeft - paddingRight;
           
        array.recycle();
	}

	public MyTextView2(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	
		if(mCanvas == null)
			mCanvas = canvas;
		super.onDraw(canvas);
		current_line = 1;
		
		canvas.drawColor(bg_color);
		
		//rect = new Rect(0,(int)(-current_y_pos),displayWidth,(int)(displayHeight+(-current_y_pos)));
	
		//Log.i("WWWWW", "onDraw.."+"...c_y_pos:"+current_y_pos);
				
				for(int i = 0; i< lines.size();i++){
					
					if(!isOpen && current_line > 4)
						break;
					
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
		
		line_count = 0;
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
				    line_count++;
				}
				
			}
			
			mTextViewHeight -= linespace;
			mTextViewHeight = (int) (mTextViewHeight + paddingTop + paddingBottom);
			
		}
		
	}
	
	public int getCustomHeight(int line_num){
		return (int)((mTextSize+linespace)*4 - linespace + paddingTop + paddingBottom);
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
	
		/*if(!rect.contains((int)x, (int)(y)))
			return;*/
		//Log.i("WWWWW", "============line:"+line.length()+"...y:"+y+"...."+line);
        for (int i = 0; i < line.length(); i++)
        {
        	mCanvas.drawText(line, i, i+1, x, y+current_y_pos, mPaint);
        	
        	charWidth = mPaint.measureText(line, i, i+1);
            x += space + charWidth;
        }
	}
	
	
	
}
