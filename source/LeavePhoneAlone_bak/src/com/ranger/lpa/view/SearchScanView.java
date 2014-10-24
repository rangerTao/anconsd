package com.ranger.lpa.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by taoliang on 14-10-11.
 */
public class SearchScanView extends View {

    private int degree = 0;
    private int r = 0;


    public SearchScanView(Context context) {
        super(context);
    }

    public SearchScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        r = getWidth() / 4;

        double x = r * Math.cos(degree);
        double y = r * Math.sin(degree);

        Paint newpaint = new Paint();
        newpaint.setColor(Color.BLACK);

        canvas.drawCircle((float)x,(float)y,5,newpaint);

        Log.e("TAG","position of search : x: " + x + "    " + y);

        degree += 10;

        degree = degree % 360;

        postInvalidateDelayed(200);

    }
}
