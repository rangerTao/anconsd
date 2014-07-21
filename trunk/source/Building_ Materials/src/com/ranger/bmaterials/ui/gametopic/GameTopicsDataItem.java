package com.ranger.bmaterials.ui.gametopic;

import java.io.Serializable;

public class GameTopicsDataItem implements Serializable
{

    private static final long serialVersionUID = 1L;
    
    private String mBannerIcon;
    private String mName;
    private String mDescription;
    private String mId;
    
    public void setId(String id)
    {
        mId = id;
    }
    
    public String getId()
    {
        return mId;
    }
    
    public void setDescription(String des)
    {
        mDescription = des;
    }
    
    public String getDescription()
    {
        return mDescription;
    }
    
    public void setName(String name)
    {
        mName = name;
    }
    
    public String getName()
    {
        return mName;
    }
    
    public void setBannerIcon(String icon)
    {
        mBannerIcon = icon;
    }
    
    public String getBannerIcon()
    {
        return mBannerIcon;
    }
    
    public GameTopicsDataItem()
    {
    }

}
