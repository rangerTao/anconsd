package com.ranger.bmaterials.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;

/**
 */
public abstract class LargeTouchableAreasViewBase extends LinearLayout {

    private static final int TOUCH_ADDITION = 20;
    private static final int COLOR_SELECT_AREA = Color.argb(50, 255, 0, 0);
    private static final int COLOR_STAR_AREA = Color.argb(50, 0, 0, 255);

//    /**
//     */
    public interface OnLargeTouchableAreasListener {
        /**
         * Called when the selection state changed
         * 
         * @param view The {@link LargeTouchableAreasViewBase} whose selection state
         *            changed
         * @param selected The new selection state
         */
        void onItemIconClick(LargeTouchableAreasViewBase view);

        /**
         * Called when the selection starred changed
         * 
         * @param view The {@link LargeTouchableAreasViewBase} whose starred state
         *            changed
         * @param selected The new starred state
         */
        void onItemButtonClick(LargeTouchableAreasViewBase view);
    }

    /**
     * An association of a color and a Rect that will be used only for debug
     * purposes. This will let us draw a color over the area that forward
     * MotionEvent to a delegate View.
     * 
     */
    private static class TouchDelegateRecord {
        public Rect rect;
        public int color;

        public TouchDelegateRecord(Rect _rect, int _color) {
            rect = _rect;
            color = _color;
        }
    }

    private final ArrayList<TouchDelegateRecord> mTouchDelegateRecords = new ArrayList<LargeTouchableAreasViewBase.TouchDelegateRecord>();
    //private final Paint mPaint = new Paint();

    private View endView;

    private TouchDelegateGroup mTouchDelegateGroup;

    private int mTouchAddition;


    private int mPreviousWidth = -1;
    private int mPreviousHeight = -1;

    public LargeTouchableAreasViewBase(Context context) {
        super(context);
        init(context);
    }

    public LargeTouchableAreasViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected abstract int getLayoutViewId();
    protected abstract int getButtonId();
    	
    
    private void init(Context context) {

        setOrientation(LinearLayout.HORIZONTAL);
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        mTouchDelegateGroup = new TouchDelegateGroup(this);
        //mPaint.setStyle(Style.FILL);

        final float density = context.getResources().getDisplayMetrics().density;
        mTouchAddition = (int) (density * TOUCH_ADDITION + 0.5f);

        LayoutInflater.from(context).inflate(getLayoutViewId(), this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int buttonId = getButtonId();
        endView =  findViewById(buttonId);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        final int width = r - l;
        final int height = b - t;

        /*
         * We can't use onSizeChanged here as this is called before the layout
         * of child View is actually done ... Because we need the size of some
         * child children we need to check for View size change manually
         */
        if (width != mPreviousWidth || height != mPreviousHeight) {

            mPreviousWidth = width;
            mPreviousHeight = height;

            mTouchDelegateGroup.clearTouchDelegates();

            //@formatter:off
            /*addTouchDelegate(
                    new Rect(0, 0, startView.getWidth() + mTouchAddition, height),
                    COLOR_SELECT_AREA,
                    startView);*/
            
            addTouchDelegate(
                    new Rect(width - endView.getWidth() - mTouchAddition, 0, width, height),
                    COLOR_STAR_AREA,
                    endView);
            //@formatter:on

            setTouchDelegate(mTouchDelegateGroup);
        }
    }

    private void addTouchDelegate(Rect rect, int color, View delegateView) {
        mTouchDelegateGroup.addTouchDelegate(new TouchDelegate(rect, delegateView));
        mTouchDelegateRecords.add(new TouchDelegateRecord(rect, color));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

}
