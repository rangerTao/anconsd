package com.ranger.bmaterials.ui.gametopic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.topicdetail.TopicDetailActivity;
import com.ranger.bmaterials.ui.topicdetail.Utils;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class GameTopicFragment extends Fragment implements IRequestListener, OnClickListener
{
    private int mPage = 0;
    private int mRequestMoreId = -1;
    
    private static final String TAG = "GameTopics";
    
    private ListView            mListView;
    private GameTopicsAdapter   mDataAdapter;
    private View                mLoadingFooter = null;
    
    private static final int    PAGE_COUNT = 12;
    
    private IRequestListener mMoreListener = new IRequestListener()
    {

        @Override
        public void onRequestSuccess(BaseResult responseData)
        {
        	if(getActivity()==null){
        		return;
        	}
            GameTopicsData data = (GameTopicsData)responseData;
            
            if (null != data && null != data.getDataList() && data.getDataList().size() > 0)
            {
                mDataAdapter.appendDataList(data.getDataList());
                
                ++mPage;
            }
            else
            {
                showNoDataFooter();
            }
            
            if (null != data)
            {
                int count = (null != mDataAdapter.getDataList() ? mDataAdapter.getDataList().size() : 0);
                
                if (count >= data.getTotalCount())
                {
                    showNoMoreData();
                }
            }
            
            mRequestMoreId = -1;
        }

        @Override
        public void onRequestError(int requestTag, int requestId, int errorCode, String msg)
        {
        	if(getActivity()==null){
        		return;
        	}
            showNoDataFooter();
            if(isAdded()){
            	CustomToast.showToast(getActivity(), getString(R.string.network_error_hint));
            }

            mRequestMoreId = -1;
        }
    };
    
    private OnScrollListener mScrollListener = new OnScrollListener()
    {
        private int preState = SCROLL_STATE_IDLE;
        
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            if (scrollState == SCROLL_STATE_IDLE && scrollState != preState)
            {
                if (null != mLoadingFooter && mLoadingFooter.isShown())
                {
                    loadMore();
                }
            }
            
            preState = scrollState;
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            
        }
    };
    
    private OnItemClickListener mItemClickListener = new OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3)
        {
            GameTopicsDataItem item = (GameTopicsDataItem)mDataAdapter.getItem(position);
            
            if (null != item)
            {
                Bundle b = new Bundle();
                
                b.putString(TopicDetailActivity.KEY_EXTRA_TOPIC_ID, item.getId());
                b.putString(TopicDetailActivity.KEY_EXTRA_TOPIC_TITLE, item.getName());
                
                Utils.instance().tripToTopicDetail(GameTopicFragment.this.getActivity(), b);
                
                ClickNumStatistics.addGameTopicClickStatistics(getActivity(), item.getName());
            }
            else
            {
                if (Constants.DEBUG)
                {
                    Log.e(TAG, ">> item selected is null...");
                }
            }
        }
        
    };
    
    private void loadMore()
    {
        if (!ConnectManager.isNetworkConnected(getActivity()))
        {
            CustomToast.showToast(getActivity(), getActivity().getString(R.string.alert_network_inavailble));
            return ;
        }
        
        if (mRequestMoreId < 0 && mPage > 0)
        {
            showLoadingFooter();
            NetUtil.getInstance().requestGameTopicsData(mMoreListener, mPage, PAGE_COUNT);
        }
    }
    
    public String getString(Intent intent, String key)
    {
        if (null != intent && null != key && intent.hasExtra(key))
        {
            return intent.getStringExtra(key);
        }
        
        return null;
    }

    private View root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);

				return root;
			}
		}
    	
    	mPage=1;
    	root = inflater.inflate(R.layout.game_topic_fragment, null);
        mListView = (ListView)root.findViewById(R.id.list);
        mDataAdapter = new GameTopicsAdapter(getActivity());
        
        mListView.addFooterView(createLoadingFooter());
        mListView.setOnScrollListener(mScrollListener);
        mListView.setOnItemClickListener(mItemClickListener);

        mListView.setAdapter(mDataAdapter);
    	requestData();
        return root;
    }
    
    private void initViewWithData(GameTopicsData data)
    {
        mDataAdapter.setData(data);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }
    
    private View createLoadingFooter()
    {
        mLoadingFooter = View.inflate(getActivity(), R.layout.loading_layout, null);
        
        mLoadingFooter.setVisibility(View.VISIBLE);

        showNoDataFooter();
        
        return mLoadingFooter;
    }
    
    private void showLoadingFooter()
    {
        if (null != mLoadingFooter)
        {
            TextView subView = (TextView)mLoadingFooter.findViewById(R.id.loading_text);
            View progress = mLoadingFooter.findViewById(R.id.loading_progressbar);
            
            subView.setText(R.string.pull_to_refresh_refreshing_label);
            progress.setVisibility(View.VISIBLE);
        }
    }
    
    private void showNoDataFooter()
    {
        if (null != mLoadingFooter)
        {
            TextView tv = (TextView)mLoadingFooter.findViewById(R.id.loading_text);
            View progress = mLoadingFooter.findViewById(R.id.loading_progressbar);
            
            progress.setVisibility(View.GONE);
            
            tv.setText(R.string.pull_to_refresh_from_bottom_pull_label);
        }
    }

    private void showNoMoreData()
    {
        if (null != mLoadingFooter)
        {
            TextView tv = (TextView)mLoadingFooter.findViewById(R.id.loading_text);
            View progress = mLoadingFooter.findViewById(R.id.loading_progressbar);
            
            progress.setVisibility(View.GONE);
            
            tv.setText(R.string.footer_no_more_see_more);
            if (isAdded()) {
            	tv.setTextColor(getResources().getColor(R.color.no_more_data_text));				
			}
            tv.setOnClickListener(this);
        }
        
        mPage = -1;
    }
    
    private void requestData()
    {
        LoadingTask task = new LoadingTask(getActivity(), new ILoading()
        {

            @Override
            public void loading(IRequestListener listener)
            {
                NetUtil.getInstance().requestGameTopicsData(listener, 0, PAGE_COUNT);
            }

            @Override
            public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv)
            {
            }

            @Override
            public boolean isShowNoNetWorkView()
            {
                return true;
            }

            @Override
            public IRequestListener getRequestListener()
            {
                return GameTopicFragment.this;
            }

            @Override
            public boolean isAsync()
            {
                return false;
            }
        });
        
        task.setRootView(root);
        task.loading();
    }

    @Override
    public void onRequestSuccess(BaseResult responseData)
    {
        ++mPage;
        initViewWithData((GameTopicsData)responseData);
    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode, String msg)
    {
        CustomToast.showToast(getActivity(), msg);
    }

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.loading_text:
			break;

		default:
			break;
		}
		
	}
    
}
