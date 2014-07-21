package com.ranger.bmaterials.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;

public class GridViewWithDivider extends GridView {

	public GridViewWithDivider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean isATMOST = true;

	public final ScrollView getParentScrollView() {
		return parent_sv;
	}

	public final void setParentScrollView(ScrollView parent_sv) {
		this.parent_sv = parent_sv;
	}

	private ScrollView parent_sv;

	// 兼容scollerview
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Calculate entire height by providing a very large height hint.
		// But do not use the highest 2 bits of this integer; those are
		// reserved for the MeasureSpec mode.
		if (isATMOST) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			if (getMeasuredHeight() != 0) {
				ViewGroup.LayoutParams params = getLayoutParams();
				params.height = getMeasuredHeight();
			}
		} else
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		

	}

	public final boolean isATMOST() {
		return isATMOST;
	}

	public final void setATMOST(boolean isATMOST) {
		this.isATMOST = isATMOST;
	}

	//解决某些机型上gridview会被拖动的问题
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return super.onTouchEvent(event);
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			return super.onTouchEvent(event);
		}
		return false;
	}

    @Override
    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        View localView1 = getChildAt(0);
        int column = localView1 != null ? getWidth() / localView1.getWidth() : 4;
        int childCount = getChildCount();
        Paint localPaint;
        localPaint = new Paint();
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setColor(Color.parseColor("#50d2d2d2"));
        for(int i = 0;i < childCount;i++){
            View cellView = getChildAt(i);
            if((i + 1) % column == 0){
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
            }else if((i + 1) > (childCount - (childCount % column))){
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
            }else{
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
            }
        }
        if(childCount % column != 0){
            for(int j = 0 ;j < (column-childCount % column) ; j++){
                View lastView = getChildAt(childCount - 1);
                canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth()* j, lastView.getBottom(), localPaint);
            }
        }
    }

}
