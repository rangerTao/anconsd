package com.ranger.bmaterials.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;

public class CustomizedButton extends Button
{
    private volatile boolean mEnabled;
    private int mDisabledDrawableId;
    private int mEnabledDrawableId;
    
    /**
     * @param enabled
     */
    public void setEnabled(boolean enabled)
    {
        mEnabled = enabled;
        
        if (!enabled)
        {
            {
                setBackgroundResource(mDisabledDrawableId);
            }
            
            setTextColor(Color.WHITE);
        }
        else
        {
            {
                setBackgroundResource(mEnabledDrawableId);
            }
            
            setTextColor(Color.WHITE);
        }
    }
    
    /**
     * 
     * @param id
     */
    public void setDisabledDrawableId(int id)
    {
        mDisabledDrawableId = id;
    }
    
    /**
     * 
     * @param id
     */
    public void setEnabledDrawableId(int id)
    {
        mEnabledDrawableId = id;
    }
    
    /**
     * 
     * @param d
     */
    public void setDisabledBackgroundDrawable(Drawable d)
    {
    }
    
    /**
     * 
     * @param d
     */
    public void setEnabledBackgroundDrawable(Drawable d)
    {
    }
    
    /**
     * 
     * @return
     */
    public Drawable getEnabledBackgroundDrawable()
    {
        return null;
    }
    
    /**
     * 
     * @return
     */
    public Drawable getDisabledBackgroundDrawable()
    {
        return null;
    }
    
    /**
     * 
     * @return
     */
    public boolean getEnabled()
    {
        return mEnabled;
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (!mEnabled)
        {
            return true;
        }
        
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (!mEnabled)
        {
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!mEnabled)
        {
            return true;
        }
        
        return super.onTouchEvent(event);
    }

    public CustomizedButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public CustomizedButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomizedButton(Context context)
    {
        super(context);
    }

}
