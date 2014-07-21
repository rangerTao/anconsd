package com.ranger.bmaterials.ui;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.ui.enumeration.StatusLoading;

import android.view.View;
import android.view.View.OnClickListener;

public class GameDetailHolder
{
    private View mParent;
    private View mViewLoading;
    private View mViewError;
    private View mViewContentNone;
    private View mViewGameNotFound;
    private OnClickListener mReloadListener = null;
    
    private OnClickListener mErrorListener = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            if (ConnectManager.isNetworkConnected(v.getContext()))
            {
                if (null != mReloadListener)
                {
                    mReloadListener.onClick(v);
                }
            }
            else
            {
                CustomToast.showToast(v.getContext(), v.getContext().getString(R.string.alert_network_inavailble));
            }            
        }
    };
    
    public GameDetailHolder(View parent)
    {
        mParent = parent;
        mViewLoading = parent.findViewById(R.id.network_loading_pb);
        mViewError = parent.findViewById(R.id.loading_error_layout);
    }
    
    public void setViewGameNotFound(View view)
    {
        mViewGameNotFound = view;
    }
    
    public void setViewContentNone(View none)
    {
        mViewContentNone = none;
    }
    
    private void setNoneVisibility(int visibility)
    {
        if (null != mViewContentNone)
        {
            mViewContentNone.setVisibility(visibility);
        }
    }
    
    private void setGameVisibility(int visibility)
    {
        if (null != mViewGameNotFound)
        {
            mViewGameNotFound.setVisibility(visibility);
        }
    }
    
    public void setReloadListener(View.OnClickListener l)
    {
        mReloadListener = l;
        mViewError.setOnClickListener(mErrorListener);
        mViewError.setClickable(true);
    }
    
    /**
     * 
     * @param status 当前状态
     * 1. INVALID: 无效
     * 2. LOADING：加载中
     * 3. FAILED: 网络请求错误
     * 4. NONE：请求数据为空
     * 5. NOT_FOUND：暂无此游戏
     */
    public void refreshStatus(StatusLoading status)
    {
        switch (status)
        {
            case INVALID:
            {
                // RESERVED
            }
            break;
            
            case SUCCEED:
            {
                mParent.setVisibility(View.GONE);
                setNoneVisibility(View.GONE);
                setGameVisibility(View.GONE);
            }
            break;
            
            case LOADING:
            {
                mViewLoading.setVisibility(View.VISIBLE);
                mViewError.setVisibility(View.GONE);
                
                setNoneVisibility(View.GONE);
                setGameVisibility(View.GONE);
            }
            break;
            
            case FAILED:
            {
                mViewLoading.setVisibility(View.GONE);
                mViewError.setVisibility(View.VISIBLE);
                
                setNoneVisibility(View.GONE);
                setGameVisibility(View.GONE);
            }
            break;
            
            case NONE:
            {
                mViewLoading.setVisibility(View.GONE);
                mViewError.setVisibility(View.GONE);
                
                setNoneVisibility(View.VISIBLE);
                setGameVisibility(View.GONE);
            }
            break;
            
            case NOT_FOUND:
            {
                mViewLoading.setVisibility(View.GONE);
                mViewError.setVisibility(View.GONE);
                
                setNoneVisibility(View.GONE);
                setGameVisibility(View.VISIBLE);
            }
            break;
            
            default:break;
        }
    }
    
}
