package com.ranger.bmaterials.ui.gametopic;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GameTopicsAdapter extends BaseAdapter
{
    private GameTopicsData                  mData;
    
    private Context                 mContext;             
    
    private static DisplayImageOptions  options = ImageLoaderHelper.getCustomOption(R.drawable.ad_default);
    
    public void setData(GameTopicsData data)
    {
        mData = data;
        notifyDataSetChanged();
    }
    
    public GameTopicsData getData()
    {
        return mData;
    }
    
    public void appendDataList(List<GameTopicsDataItem> list)
    {
        mData.getDataList().addAll(list);
        notifyDataSetChanged();
    }
    
    public List<GameTopicsDataItem> getDataList()
    {
        return null != mData ? mData.getDataList() : null;
    }
    
    public GameTopicsAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount()
    {
        int count = 0;
        
        if (null != mData && null != mData.getDataList())
        {
            count = mData.getDataList().size();
        }
        
        return count;
    }

    @Override
    public Object getItem(int position)
    {
        return (null != mData && null != mData.getDataList() && position < mData.getDataList().size()) ? mData.getDataList().get(position) : null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder vh;
        boolean more = position >= mData.getDataList().size();
        
        if (null == convertView)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_list_item, null);
            vh = new ViewHolder(convertView);

            final boolean[] enabled = {true, true};
            
            vh.mPicture.setCornersEnabled(enabled);
            vh.mPicture.setRadius(UIUtil.dip2px(mContext, 7f));
            
            convertView.setTag(vh);
        }
        else
        {
            vh = (ViewHolder)convertView.getTag();
        }
        
        vh.setMore(more);
        
        if (!more)
        {
            GameTopicsDataItem item = mData.getDataList().get(position);
            
            vh.setName(item.getName());
            vh.setPicture(item.getBannerIcon());
            vh.setDescription(item.getDescription());
        }
        else
        {
            vh.setName("加载更多...");
        }
        
        return convertView;
    }
    
    public static class ViewHolder
    {
        private RoundCornerImageView    mPicture;
        private TextView    mName;
        private TextView    mDescription;
        private View        mDivider;
        private static final float  moreSize = 12f;
        private static final float  titleSize = 16f;
        
        public ViewHolder(View parent)
        {
            if (null != parent)
            {
                mPicture = (RoundCornerImageView)parent.findViewById(R.id.picture);
                mPicture.setDisplayImageOptions(options);
                mName = (TextView)parent.findViewById(R.id.titile);
                mDescription = (TextView)parent.findViewById(R.id.description);
                mDivider = parent.findViewById(R.id.divider);
            }
        }

        private static String checkspace(String content)
        {
            Pattern p = Pattern.compile("　{1,10}");
            Matcher m = p.matcher(content);
            StringBuffer sb = new StringBuffer();
            boolean result = m.find();
            
            while (result)
            {
                m.appendReplacement(sb, "");

                result = m.find();
            }
            m.appendTail(sb);

            Pattern p2 = Pattern.compile("[ | |　]{0,140}\n{1,140}[ | |　]{0,140}");
            Matcher m2 = p2.matcher(sb.toString());
            StringBuffer sb2 = new StringBuffer();
            boolean result2 = m2.find();
            
            while (result2)
            {
                m2.appendReplacement(sb2, "\n\u3000\u3000");

                result2 = m2.find();
            }
            m2.appendTail(sb2);
            
            return sb2.toString();
        }
        
        public void setPicture(String url)
        {
        	mPicture.setImageUrl(url);
        }
        
        public void setName(String name)
        {
            mName.setText(name);
        }
        
        public void setDescription(String des)
        {
            String s = checkspace(des.trim());
            
            mDescription.setText(s); //"\u3000\u3000" + 
        }
        
        public void setMore(boolean more)
        {
            if (more)
            {
                mPicture.setVisibility(View.GONE);
                mDescription.setVisibility(View.GONE);
                mDivider.setVisibility(View.GONE);
                mName.setGravity(Gravity.CENTER);
                mName.setTextSize(moreSize);
            }
            else
            {
                mPicture.setVisibility(View.VISIBLE);
                mDescription.setVisibility(View.VISIBLE);
                mDivider.setVisibility(View.VISIBLE);
                mName.setGravity(Gravity.LEFT);
                mName.setTextSize(titleSize);
            }
        }
    }

}
