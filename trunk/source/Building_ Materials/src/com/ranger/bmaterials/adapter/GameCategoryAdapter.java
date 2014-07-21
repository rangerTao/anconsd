package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.R.id;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.GameTypeInfo;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.ExpandableHeightGridView;

public class GameCategoryAdapter extends BaseAdapter
{
    private Context                     mContext;
    private DataSetObserver             mObserver;
    private volatile ArrayList<GameTypeInfo>     mDataList;
    
    private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false)
            .cacheOnDisc(true).showImageOnLoading(R.drawable.game_ad_pic_rd_game_default)
            .showImageForEmptyUri(R.drawable.game_ad_pic_rd_game_default)
            .imageScaleType(ImageScaleType.EXACTLY)
            .showImageOnFail(R.drawable.game_ad_pic_rd_game_default)
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
            int padding = UIUtil.dip2px(GameTingApplication.getAppInstance(), 6f);
            int width = (GameTingApplication.getAppInstance().getResources().getDisplayMetrics().widthPixels - padding * 3) / 2;
            int bwidth = b.getWidth();
            int bheight = b.getHeight();

            ilp = new LayoutParams(width, ((int)(bheight * (width * 1.0f / bwidth))));
            ImageView iv=((ImageViewAware)arg1).getImageView();
            iv.setLayoutParams(ilp);
            
            iv.setScaleType(ScaleType.FIT_XY);
            
            iv.setImageBitmap(arg0);
            
        }

    }

    public GameCategoryAdapter(Context context)
    {
        mContext = context;
    }
    
    public void setDataList(ArrayList<GameTypeInfo> list)
    {
        mDataList = list;
        notifyDataSetChanged();
    }
    
    public List<GameTypeInfo> getDataList()
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
        return mDataList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	
    	CategoryHolder gh;
    	View view;
    	
    	GameTypeInfo gti = (GameTypeInfo) getItem(position);
    	
    	if(convertView == null){
    		
    		view = View.inflate(mContext, R.layout.game_classes_item, null);
    		gh = new CategoryHolder();
    		
    		gh.rciv = (RoundCornerImageView) view.findViewById(R.id.iv_game_icon);
    		gh.tv_name = (TextView) view.findViewById(R.id.title);
    		gh.recomGames = (ExpandableHeightGridView) view.findViewById(R.id.gv_category_recom_games);
    		
    		view.setTag(gh);
    		
    		convertView = view;
    	}else{
    		gh = (CategoryHolder) convertView.getTag();
    	}
    	
    	ImageLoaderHelper.displayImage(gti.getGametypeicon(), gh.rciv, options);
    	
    	gh.tv_name.setText(gti.getGametypename());
    	
    	GameCategoryRecomGamesAdapter gcrga = new GameCategoryRecomGamesAdapter(mContext, gti ,2);
    	
    	gh.recomGames.setNumColumns(2);
    	gh.recomGames.setAdapter(gcrga);
    	
    	return convertView;
    }
    
    class CategoryHolder{
    	RoundCornerImageView rciv;
    	TextView tv_name;
    	ExpandableHeightGridView recomGames;
    }

}
