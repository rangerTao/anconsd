package com.ranger.lpa.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by taoliang on 14/12/23.
 */
public class RippleView extends View {

    private int baseRadius = 100;
    private int secondRadius = 120;
    private int thirdRadius = 150;

    private float firstCircleWidth = 1f;
    private float secondCircleWidth = 1f;
    private float thirdCircleWidth = 1f;

    Paint circlePaint;

    public RippleView(Context context) {
        super(context);

        init();
    }

    private void init(){

        secondRadius = Math.abs(new Random(System.currentTimeMillis()).nextInt() % 50) + baseRadius;
        thirdRadius = Math.abs(new Random(System.currentTimeMillis()).nextInt() % 110) + secondRadius;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);

        setCircleWidth(firstCircleWidth);
        setCircleWidth(secondCircleWidth);
        setCircleWidth(thirdCircleWidth);
    }

    private void setCircleWidth(float circle){
        circle = Math.abs(new Random(System.currentTimeMillis()).nextFloat() % 20);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(firstCircleWidth);
        canvas.drawCircle((getRight() - getLeft()) / 2,(getBottom() - getTop() )/ 2,baseRadius, circlePaint);
        baseRadius += 2;

        circlePaint.setColor(Color.YELLOW);
        circlePaint.setStrokeWidth(secondCircleWidth);
        canvas.drawCircle((getRight() - getLeft()) / 2,(getBottom() - getTop() )/ 2,secondRadius, circlePaint);
        secondRadius +=2;

        circlePaint.setColor(Color.BLUE);
        circlePaint.setStrokeWidth(thirdCircleWidth);
        canvas.drawCircle((getRight() - getLeft()) / 2,(getBottom() - getTop() )/ 2,thirdRadius, circlePaint);
        thirdRadius += 2;

        if(baseRadius > getWidth() / 2){
            baseRadius = baseRadius % (getWidth() / 2);
            setCircleWidth(firstCircleWidth);
        }

        if(secondRadius > getWidth() / 2){
            secondRadius = secondRadius % (getWidth() /2 );
            secondRadius += Math.abs(new Random(System.currentTimeMillis()).nextInt() % 100);
            setCircleWidth(secondCircleWidth);
        }

        if(thirdRadius > getWidth() / 2){
            thirdRadius = thirdRadius % (getWidth() /2);
            thirdRadius += Math.abs(new Random(System.currentTimeMillis()).nextInt() % 150);
            setCircleWidth(thirdCircleWidth);
        }

        postInvalidateDelayed(30);
    }
}
