package com.ranger.bmaterials.view;

import com.ranger.bmaterials.tools.MyLogger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class AnimationDrawableView extends ImageView {

	public AnimationDrawableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		Drawable drawable = getDrawable();
		if(drawable != null && drawable instanceof AnimationDrawable){
			((AnimationDrawable) drawable).start();
		}
		super.onFinishInflate();
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
	}
	
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
	}

    @SuppressLint("NewApi")
	@Override
    protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);
        
        MyLogger.getLogger("").d("AnimationDrawableView --> onVisibilityChanged: " + visibility);
        
        if (visibility == View.VISIBLE)
        {
            Drawable drawable = getDrawable();
            if (drawable != null && drawable instanceof AnimationDrawable)
            {
                ((AnimationDrawable) drawable).start();
            }
        }
        else
        {
            Drawable drawable = getDrawable();
            if (drawable != null && drawable instanceof AnimationDrawable)
            {
                ((AnimationDrawable) drawable).stop();
            }
        }
    }
	
}
