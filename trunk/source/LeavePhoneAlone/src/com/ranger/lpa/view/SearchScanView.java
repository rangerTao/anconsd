package com.ranger.lpa.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ranger.lpa.R;

/**
 * Created by taoliang on 14-10-11.
 */
public class SearchScanView extends View {

    private int degree = 0;
    private int r = 0;

    private Bitmap bmp_search;


    public SearchScanView(Context context) {
        super(context);
    }

    public SearchScanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        bmp_search = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_search);
//        setBackgroundResource(R.drawable.btn_red_bg_selector);
    }

    public SearchScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bmp_search = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_search);
//        setBackgroundResource(R.drawable.btn_red_bg_selector);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        r = 50;

        double x = getWidth() / 2 + r * Math.cos(degree * 3.14 / 180) - (bmp_search.getWidth() / 2);
        double y = getHeight() / 2 + r * Math.sin(degree * 3.14 / 180) - (bmp_search.getHeight() / 2);

        Paint newpaint = new Paint();
        newpaint.setColor(Color.BLACK);

        canvas.drawBitmap(bmp_search, (float) x, (float) y, newpaint);

        degree += 1;

        degree = degree % 360;

        postInvalidateDelayed(10);

    }
}
