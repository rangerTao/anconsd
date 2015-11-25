package com.andconsd.ui.widget.picview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.andconsd.AndApplication;
import com.andconsd.R;
import com.andconsd.framework.bitmap.ImageLoaderHelper;
import com.andconsd.model.Picture;
import com.andconsd.framework.utils.UIUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class WaterFallAdapter extends BaseAdapter
{
	private Context                     mContext;
    private DataSetObserver             mObserver;
    private volatile ArrayList<Picture>     mDataList;
    
    //private static DisplayImageOptions  options = ImageLoaderHelper.getCustomOption(R.drawable.icon_default_small_game_class);
    
    private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true).showImageOnLoading(R.drawable.picture_loading)
            .showImageForEmptyUri(R.drawable.picture_loading)
            .imageScaleType(ImageScaleType.EXACTLY)
            .showImageOnFail(R.drawable.picture_loading)
            .bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用 每像素站2byte
            .displayer(new BitmapDisplayerImpl())
            .build(); 
    
    private class BitmapDisplayerImpl implements BitmapDisplayer
    {
        @Override
        public void display(Bitmap arg0, ImageAware arg1, LoadedFrom arg2)
        {
            Bitmap b = arg0;
            LayoutParams ilp;
            int padding = UIUtil.dip2px(AndApplication.getAppInstance(), 6f);
            int width = (AndApplication.getAppInstance().getResources().getDisplayMetrics().widthPixels - padding * 3) / 2;
            int bwidth = b.getWidth();
            int bheight = b.getHeight();

            ilp = new LayoutParams(width, ((int)(bheight * (width * 1.0f / bwidth))));
            ImageView iv=((ImageViewAware)arg1).getImageView();
            iv.setLayoutParams(ilp);
            
            iv.setScaleType(ScaleType.FIT_CENTER);
            
            iv.setImageBitmap(arg0);
            
        }

    }
    


    public WaterFallAdapter(Context context,ArrayList<Picture> list)
    {
        mContext = context;
        mDataList = list;
    }
    
    public void setDataList(ArrayList<Picture> list)
    {
        mDataList = list;
        notifyDataSetChanged();
    }
    
    public List<Picture> getDataList()
    {
        return mDataList;
    }

    @Override
    public void notifyDataSetChanged()
    {
        if (null != mObserver)
        {
            mObserver.onChanged();
        }
    }

    @Override
    public void notifyDataSetInvalidated()
    {
        if (null != mObserver)
        {
            mObserver.onInvalidated();
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {
        mObserver = observer;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {
        mObserver = null;
    }

    @Override
    public int getCount()
    {
        return null != mDataList? mDataList.size() : 0;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LinearLayout item = (LinearLayout)convertView;
        
        try
        {
            if (null == convertView)
            {
                item = (LinearLayout)(LayoutInflater.from(mContext).inflate(R.layout.game_classes_item, null));
            }

            ImageView cover = (ImageView)item.findViewById(R.id.cover);
            TextView title = (TextView)item.findViewById(R.id.title);
            final boolean[] enabled = {true, true};
            
            if (null != mDataList)
            {
                if (position < mDataList.size())
                {
                	Picture info = mDataList.get(position);

                    ImageLoaderHelper.displayImage(info.getUrl(), cover, mContext);
                    
                    if(!info.getDescp().equals("")){
                    	title.setText(info.getDescp());
                        title.setVisibility(View.VISIBLE);
                    }else{
                        title.setVisibility(View.GONE);
                    }
                    
                    item.setTag(Integer.valueOf(position));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return item;
    }

}
