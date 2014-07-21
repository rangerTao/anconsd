package com.ranger.bmaterials.ui;

import com.ranger.bmaterials.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint({ "DrawAllocation", "InlinedApi" })
public class CustomizedProgress extends View
{
    /** Max value for progress **/
    private int mMax;
    /** Current progress **/
    private int mProgress;
    /** Padding left value **/
    private int mPaddingLeft;
    /** Padding top value **/
    private int mPaddingTop;
    /** Padding right value **/
    private int mPaddingRight;
    /** Padding bottom value **/
    private int mPaddingBottom;
    /** Width of view **/
    private int mWidth;
    /** Height of view **/
    private int mHeight;
    /** Bitmap for current progress **/
    private Bitmap mBMCurProgress;
    /** Bitmap for current node **/
    private Bitmap mBMNodeProgress;
    /** Bitmap for total progress **/
    private Bitmap mBMTotalProgress;
    /** Bitmap for total node **/
    private Bitmap mBMNodeTotal;
    /** Paint for drawing **/
    private Paint mPaint;
    /** Canvas line rectangle to be drawn **/
    private Rect mRect;
    /** Canvas circle rectangle to be drawn **/
    private Rect mRectCircle;
    /** Bitmap rectangle to be drawn **/
    private Rect mBMRect;
    /** **/
    private volatile boolean mIsDrawing;
    
    private void initView(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomizedProgress);

        mMax = ta.getInt(R.styleable.CustomizedProgress_maxProgress, 50);
        mProgress = ta.getInt(R.styleable.CustomizedProgress_currentProgress, 0);
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mBMCurProgress = BitmapFactory.decodeResource(context.getResources(), R.drawable.progress);
        mBMNodeProgress = BitmapFactory.decodeResource(context.getResources(), R.drawable.node);
        mBMTotalProgress = BitmapFactory.decodeResource(context.getResources(), R.drawable.progress_total);
        mBMNodeTotal = BitmapFactory.decodeResource(context.getResources(), R.drawable.node_total);
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        
        mRect = new Rect();
        mRectCircle = new Rect();
        mBMRect = new Rect();
    }
    
    public CustomizedProgress(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        
        initView(context, attrs);
    }

    public CustomizedProgress(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        initView(context, attrs);
    }

    public CustomizedProgress(Context context)
    {
        super(context);
        
        initView(context, null);
    }

    /**
     * To set the max value of progress
     * @param max
     */
    public void setMaxValue(int max)
    {
        mMax = max < 5 ? 50 : max;
        invalidate();
    }

    /**
     * To get max value of progress
     * @return
     */
    public int getMaxValue()
    {
        return mMax;
    }

    /**
     * To set progress value
     * @param val
     */
    public void setProgressValue(int val)
    {
        mProgress = val;
        invalidate();
    }

    /**
     * To get progress value
     * @return
     */
    public int getProgressValue()
    {
        return mProgress;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = right - left;
        mHeight = bottom - top;
    }
    
    /**
     * Compute the offset line covered circle
     * @param r     -- Radius
     * @param h     -- Line bitmap height
     * @return      -- Offset value
     */
    private int computeOffset(int r, int h)
    {
        int ret = 0;
        
        ret = r - (int)Math.sqrt((r * r * 1.0 - (h * h / 4))) + 1;
        
        return ret;
    }
    
    /**
     * To check if progressed to the end
     * @param progress  -- Need to drawn value
     * @param width     -- Progressed value
     * @return          -- Delta value
     */
    private int checkDelta(int progress, int width)
    {
        int ret = width - progress;;

        if (ret < 0)
        {
//            mRect.right += ret;
//            mBMRect.left -= ret;
        }
        
        return ret;
    }

    private void drawOneProgress(Bitmap c, Bitmap l, int progress, Canvas canvas, Paint paint)
    {
        drawOneProgress(c, l, progress, canvas, paint, 0, 0);
    }
    
    /**
     * 
     * @param c         -- Circle bitmap
     * @param l         -- Line bitmap
     * @param progress  -- Progressed value
     * @param canvas    -- Canvas
     * @param paint     -- Paint
     * @param left      -- Padding left align to total progress
     * @param right     -- Padding right align to total progress
     */
    private void drawOneProgress(Bitmap c, Bitmap l, int progress, Canvas canvas, Paint paint, int left, int right)
    {
        // four margins to make vertical layout to center
        int marginLeft = mPaddingLeft + left;
        int marginRight = mPaddingRight + right;
        int marginTop = mPaddingTop + (mHeight - c.getHeight()) / 2;
        int offset = computeOffset(c.getWidth() / 2, l.getHeight());    // offset to make progress jointing circle
        int voffset = (c.getHeight() - l.getHeight()) / 2;              // offset between circle and progress
        int index = 0;
        int count = 5;
        int progressWidth = (mWidth - c.getWidth() * (count + 1) - marginLeft - marginRight) / count + offset * 2;  // total width of progress
        int currentWidth = (int)((progress * 1.0f / mMax) * (mWidth - marginLeft - marginRight));                   // progressed width
        
        if (progress == 0)
        {
            return ;
        }

        mRectCircle.setEmpty();
        mRect.setEmpty();
        mBMRect.setEmpty();
        
        mRectCircle.top = marginTop;
        mRectCircle.bottom = mRectCircle.top + c.getHeight();
        mRectCircle.left = marginLeft;
        mRectCircle.right = mRectCircle.left + c.getWidth();
        mBMRect.left = 0;
        mBMRect.top = 0;
        mBMRect.right = c.getWidth();
        mBMRect.bottom = c.getHeight();
        
        // progress adapt
        if (checkDelta((mRect.right - marginLeft), currentWidth) < 0)
        {
            count = 0;
        }

        //draw first node
        canvas.drawBitmap(c, mBMRect, mRectCircle, paint);
        
        for (index = 0; index < count; ++index)
        {
            mRect.left = mRectCircle.right - offset;
            mRect.right = mRect.left + progressWidth;
            mRect.top = marginTop + voffset;
            mRect.bottom = mRect.top + l.getHeight();
            mBMRect.left = 0;
            mBMRect.top = 0;
            
            mRectCircle.left = mRect.right - offset;
            mRectCircle.right = mRectCircle.left + c.getWidth();
            mRectCircle.top = marginTop;
            mRectCircle.bottom = mRectCircle.top + c.getHeight();
            
            int lDelta = checkDelta((mRect.right - marginLeft), currentWidth);
            int cDelta = checkDelta((mRectCircle.right - marginLeft), currentWidth);
            boolean needBreak = false;
            
            // to draw circle
            if (lDelta > 0)
            {
                if (cDelta <= 0)
                {
                    mRect.right += cDelta;
                    needBreak = true;
                }

                if (!needBreak)
                {
                    mBMRect.right = c.getWidth();
                    mBMRect.bottom = c.getHeight();
                    canvas.drawBitmap(c, mBMRect, mRectCircle, paint);
                }
            }
            
            if (lDelta <= 0)
            {
                // to draw line only
                mRect.right += lDelta;
                mBMRect.left -= lDelta;
                mBMRect.right = l.getWidth();
                needBreak = true;
            }
            else
            {
                mBMRect.right = l.getWidth() - 4;
            }

            mBMRect.bottom = l.getHeight();
            canvas.drawBitmap(l, mBMRect, mRect, paint);
         
            if (needBreak)
            {
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mIsDrawing == true)
        {
            return ;
        }
        
        mIsDrawing = true;
        super.onDraw(canvas);

        drawOneProgress(mBMNodeTotal, mBMTotalProgress, mMax, canvas, mPaint);
        
        int padding = (mBMNodeTotal.getWidth() - mBMNodeProgress.getWidth()) / 2;
        drawOneProgress(mBMNodeProgress, mBMCurProgress, mProgress, canvas, mPaint, padding, padding);

        mIsDrawing = false;
    }

}
