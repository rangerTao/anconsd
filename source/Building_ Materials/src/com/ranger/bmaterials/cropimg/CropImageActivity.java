package com.ranger.bmaterials.cropimg;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ranger.bmaterials.R;


/**
 * 裁剪界面
 *
 */
public class CropImageActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "CropImageActivity";
	private CropImageView mImageView;
	private Bitmap mBitmap;
	
	private CropImage mCrop;
	
	private Button mSave;
	private Button mCancel,btn_rotate;
	private String mPath = "CropImageActivity";
	public int screenWidth = 0;
	public int screenHeight = 0;
	
	private ProgressBar mProgressBar;
	
	public static final int SHOW_PROGRESS = 2000;

	public static final int REMOVE_PROGRESS = 2001;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SHOW_PROGRESS:
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case REMOVE_PROGRESS:
				mHandler.removeMessages(SHOW_PROGRESS);
				mProgressBar.setVisibility(View.INVISIBLE);
				break;
			}

		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gl_modify_avatar);
        init();
    }
    @Override
    protected void onStop(){
    	super.onStop();
    	if(mBitmap!=null){
    		mBitmap.recycle();
    		mBitmap=null;
    	}
    }
    
    private void init()
    {
    	getWindowWH();
    	mPath = getIntent().getStringExtra("path");
        mImageView = (CropImageView) findViewById(R.id.gl_modify_avatar_image);
        mSave = (Button) this.findViewById(R.id.gl_modify_avatar_save);
        mCancel = (Button) this.findViewById(R.id.gl_modify_avatar_cancel);
        btn_rotate = (Button) this.findViewById(R.id.gl_modify_avatar_rotate_right);
        mSave.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        btn_rotate.setOnClickListener(this);
        try{
            mBitmap = createBitmap(mPath,screenWidth,screenHeight);
            if(mBitmap==null){
            	Toast.makeText(CropImageActivity.this, "没有找到图片", Toast.LENGTH_LONG).show();
    			finish();
            }else{
            	resetImageView(mBitmap);
            }
        }catch (Exception e) {
        	Toast.makeText(CropImageActivity.this, "没有找到图片", Toast.LENGTH_LONG).show();
			finish();
		}catch (OutOfMemoryError e) {
        	Toast.makeText(CropImageActivity.this, "获取图片失败", Toast.LENGTH_LONG).show();
        	System.gc();
			finish();
			e.printStackTrace();
		}
        addProgressbar();       
    }
    /**
     * 获取屏幕的高和宽
     */
    private void getWindowWH(){
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth=dm.widthPixels;
		screenHeight=dm.heightPixels;
	}
    private void resetImageView(Bitmap bitmap){
    	 mImageView.clear();
    	 mImageView.setImageBitmap(bitmap);
         mImageView.setImageBitmapResetBase(bitmap, true);
         mCrop = new CropImage(this, mImageView,mHandler);
         mCrop.crop(bitmap);
    }
    
    public void onClick(View v)
    {
    	int resultId = v.getId();
    	if (resultId == R.id.gl_modify_avatar_cancel) {
    		finish();
		}else if (resultId == R.id.gl_modify_avatar_save) {
			String path = mCrop.saveToLocal(mCrop.cropAndSave());
    		Intent intent = new Intent();
    		intent.putExtra("path", path);
    		setResult(RESULT_OK, intent);
    		finish();
		}else if (resultId == R.id.gl_modify_avatar_rotate_right) {
			mCrop.startRotate(90.f);
		}
    }
    protected void addProgressbar() {
		mProgressBar = new ProgressBar(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addContentView(mProgressBar, params);
		mProgressBar.setVisibility(View.INVISIBLE);
	}
    
    /**
     * 
     * @param path 图片路径
     * @param wantWidth 目标宽度
     * @param wantHeight 目标高度
     * @return
     */
    public Bitmap createBitmap(String path,int wantWidth,int wantHeight){
    	try{
//    		Log.e("xxxx", "cropImageActivity createBitmap path="+path);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < wantWidth || srcHeight < wantHeight) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / wantWidth;
				destWidth = wantWidth;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / wantHeight;
				destHeight = wantHeight;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}catch (OutOfMemoryError e) {
			System.gc();
			return null;
		}
    }
   
}