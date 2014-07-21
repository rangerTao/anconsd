package com.ranger.bmaterials.view;

import com.ranger.bmaterials.tools.UIUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TextImageView extends ImageView
{
    private String mText;
    private Bitmap mBitmap;
    
    public TextImageView(Context context)
    {
        super(context);
    }

    public TextImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    public void setText(String text)
    {
        mText = text;
    }
    
    public String getText()
    {
        return mText;
    }

    @Override
    public void setImageResource(int resId)
    {
        Drawable d = getContext().getResources().getDrawable(resId);
        
        setImageDrawable(d);
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        mBitmap = bm;
        
        Bitmap output = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());  
        final Rect rectDraw = new Rect(0, 0, (int)(rect.width() * 0.36), (int)(rect.height() * 0.73));
        
        rectDraw.left = canvas.getWidth() - rectDraw.width();

        paint.setAntiAlias(true);  
        canvas.drawRect(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        
        if (null != mText)
        {
            Rect rectString = new Rect();
            
            paint.setTextSize(UIUtil.sp2px(10, getContext()));
            paint.setColor(Color.WHITE);  
            paint.getTextBounds(mText, 0, mText.length(), rectString);
            
            int x = (int)rectDraw.left - rectString.width() / 2;//rectDraw.left + 
            int y = (int)rectDraw.exactCenterY() + rectString.height() / 2;//rectDraw.top +  + (int)rectString.exactCenterY();
            
            canvas.translate(x, y);
            canvas.translate(-x, -y);
            
            canvas.rotate(43, x + rectString.exactCenterX(), y + rectString.exactCenterY());
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(mText, x, y, paint);
        }
        
        super.setImageBitmap(output);
    }

    
}
