package com.andconsd.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class HyImageView extends ImageView {
    private int dWidth;// 屏幕宽度
    private int dHeight;

    private float bWidth;// 图片宽度
    private float bHeight;
    float initScale = 0;
    float mScale = 0;
    float scale = 0;
    /** 保存当前的状�?*/
    Matrix mMatrix;
    /** 初始状�? */
    Matrix initMatrix;
    /** 上一个状�?*/
    Matrix mSavedMatrix;

    float initWidth = 0;// 图片第一次显示时的初始宽�?
    float initHeight = 0;
    float initLeft = 0;// 图片第一次显示时的左边位�?
    float initRight = 0;
    float initTop = 0;
    float initBottom = 0;
    // String mPicPath;
    // boolean isOnClickable=false;
    ImageState mapState = new ImageState();
    float[] values = new float[9];
    PointF mStart = new PointF();
    float bitMapWidth = 0;// 图片的真正宽�?
    float bitMapHeight = 0;
    int maxEnlargeScale = 3;// 图片可放大�?�?
    String zoomMode = null;
    // boolean mBrowseMode = false;// 单次浏览
    // GestureDetector gd;
    float oldDist = 0;
    GestureDetector gd;
    boolean mMark = false; // 控制双击事件，双击恢复初�?
    Bitmap mBitmap = null;
    boolean mIsZoom = false;// 多点触摸
    float modifyValue = 50;
    float mStartX = 0;
    float mStartY = 0;
    public HyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScaleType(ScaleType.MATRIX);
    }
    public HyImageView(Context context) {
        super(context);
        this.setScaleType(ScaleType.MATRIX);
    }

    /**
     * @return the oldDist
     */
    public float getOldDist(MotionEvent event) {
        this.oldDist = this.spacing(event);
        if (oldDist > 10f) {
            mSavedMatrix.set(mMatrix);
        }
        return oldDist;
    }
    private class ImageState {
        private float left;
        private float top;
        private float right;
        private float bottom;
    }
    public void init(MotionEvent event){
        mSavedMatrix.set(mMatrix);
        mStart.set(event.getX(), event.getY());
    }
    /**
     * @param oldDist the oldDist to set
     */
//    public void setOldDist(MotionEvent event) {
//        this.oldDist = this.spacing(event);
//    }
    public void setScreenSize(Context context, int width, int height,Bitmap bitmap) {
        mBitmap =bitmap;
        super.setImageBitmap(mBitmap);
        dWidth = width;
        dHeight = height;
        initScale = scale;
//        gd = new GestureDetector(context, new LearnGestureListener());

        bWidth = mBitmap.getWidth();
        bHeight = mBitmap.getHeight();
        // mView = (ImageView) findViewById(R.id.imageView);
        float xScale = (float) dWidth / bWidth;
        float yScale = (float) dHeight / bHeight;
        mScale = xScale <= yScale ? xScale : yScale;
        scale = mScale < 1 ? mScale : 1;
        initScale = scale;
        mMatrix = new Matrix();
        initMatrix = new Matrix();

        mSavedMatrix = new Matrix();
        // 平移
        mMatrix.postTranslate((dWidth - bWidth) / 2, (dHeight - bHeight) / 2);

        float sX = dWidth / 2;
        float sY = dHeight / 2;
        initWidth = bWidth * scale;
        initHeight = bHeight * scale;

        mMatrix.postScale(scale, scale, sX, sY);
        initMatrix.set(mMatrix);
        mSavedMatrix.set(mMatrix);
        setView();
    }
 // 刷新界面
    public void setView() {
        // UserUtils.log(TAG, "set view", "set view");
        this.setImageMatrix(mMatrix);
        Rect rect = this.getDrawable().getBounds();
        this.getImageMatrix().getValues(values);
        bWidth = rect.width() * values[0];
        bHeight = rect.height() * values[0];

        mapState.left = values[2];
        mapState.top = values[5];
        mapState.right = mapState.left + bWidth;
        mapState.bottom = mapState.top + bHeight;
    }
  //缩放
    public void zoom(MotionEvent event) {
        float newDist = spacing(event);
        if (newDist > 10f) {
            mMatrix.set(mSavedMatrix);
            scale = newDist / oldDist;
            // 缩放模式为缩�?
            if (scale < 1) {
                zoomMode = "small";
                mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);
            } else {// 缩放模式为放�?
                zoomMode = "enlarge";
                mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);

            }

        }
    }
    //拖动
    public void drag(MotionEvent event){
        mMatrix.set(mSavedMatrix);
        // 上下左右都至少有�?��出界时，能随意拖�?
        if ((mapState.left <= 0 || mapState.right >= dWidth)
                && (mapState.top <= 0 || mapState.bottom >= dHeight)) {
            mMatrix.postTranslate(event.getX() - mStart.x, event.getY()
                    - mStart.y);
//            // 当只有上下一方出界，只能上下拖动
        } else if (mapState.top <= 0 || mapState.bottom >= dHeight) {
            mMatrix.postTranslate(0, event.getY() - mStart.y);
            // 当只有左右一方出界时，只能左右拖�?
        } else if (mapState.left <= 0 || mapState.right >= dWidth) {
            mMatrix.postTranslate(event.getX() - mStart.x, 0);
        }
    }
    /** 计算移动距离 */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
    public boolean getNext(){
       return mapState.right <= dWidth && mapState.left <= -2;
    }
    public boolean getBack(){
        return mapState.left >= 0 && mapState.right >= dWidth;
    }
    public void up(int mode){
     // 当图片脱离左边界且图片右边界大于屏幕右边时，则弹到最左边或跳到上�?��
        if (mapState.left >= 0 && mapState.right >= dWidth) {
            if (bWidth > dWidth) {
                mMatrix.postTranslate(0 - mapState.left, 0);
            } else {
                mMatrix.set(initMatrix);
            }
        }
        // 当图片脱离右边界时，则弹到最右边或跳到下�?��
        if (mapState.right <= dWidth && mapState.left <= 0) {
            if (bWidth > dWidth) {
                mMatrix.postTranslate(dWidth - mapState.right, 0);
            } else {
                mMatrix.set(initMatrix);
            }
        }

        // 当图片脱离上边界时，则弹到最上边
        if (mapState.top >= 0 && mapState.bottom >= dHeight) {
            mMatrix.postTranslate(0, 0 - mapState.top);
        }
        // 当图片脱离下边界时，则弹到最下边，增加修正�?50DP
        if (mapState.bottom + modifyValue <= dHeight && mapState.top <= 0) {
            mMatrix.postTranslate(0, dHeight - mapState.bottom
                    - modifyValue);

        }

        // 若为缩放模式
        if (mode == 2) {
            // 当图片长宽都小于屏大�?时，则图片大小弹为初始�?
            setView();
            if ((bWidth < initWidth) && (bHeight < initHeight)) {
                mMatrix.set(initMatrix);
            }
            // 当图片有X\Y两个方向都至少有�?��脱离�?
            if ((mapState.left >= initLeft || mapState.right <= initRight)
                    && (mapState.top >= initTop || mapState.bottom <= initBottom)) {
                // 且为缩小模式时，则图片大小弹为初始�?
                if ("small".equals(zoomMode)) {
                    mMatrix.set(initMatrix);
                }
            }

        }
    }
    public void setInit(){
        mMatrix.set(initMatrix);
        this.setView();
    }
}
