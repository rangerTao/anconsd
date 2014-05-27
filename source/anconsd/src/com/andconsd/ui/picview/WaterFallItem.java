package com.andconsd.ui.picview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class WaterFallItem extends RelativeLayout
{
    private int         mWidth;
    private int         mHeight;
    
    private void initView(Context context)
    {
    }

    public WaterFallItem(Context context)
    {
        super(context);
        initView(context);
    }

    public WaterFallItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    public WaterFallItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        
        mWidth = r - l;
        mHeight = b - t;
    }
    
    public int height()
    {
        return mHeight;
    }
    
    public int width()
    {
        return mWidth;
    }
    
}
