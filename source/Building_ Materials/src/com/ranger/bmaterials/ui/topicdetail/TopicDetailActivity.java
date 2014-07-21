package com.ranger.bmaterials.ui.topicdetail;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.*;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class TopicDetailActivity extends HeaderCoinBackBaseActivity implements OnClickListener, IRequestListener
{
    private RoundCornerImageView mDetailImage = null;
    private TextView mDetailDescription = null;
    private ImageView mDetailDesMore = null;
    private View mDetailBottom = null;
    private ListView mRecommendList = null;
    private View mLoadingFooter = null;

    private RecommendListAdapter mListAdapter = null;

    private String mTopicId = null;
    private boolean mIsOpen = false;
    private int mHeight = 0;
    private int mRequestMoreId = -1;
    private int mPageIndex = 0;

    public static final String KEY_EXTRA_TOPIC_ID = "topic.detail.topic.id";
    public static final String KEY_EXTRA_TOPIC_TITLE = "topic.detail.topic.title";
    private static final DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.ad_default);
    
    private HashMap<String, TopicPackageCallback> mCallbackContainer;

    private void initViewWithData(TopicDetailData data)
    {
        mListAdapter.setTopicDetialData(data);
        mListAdapter.notifyDataSetChanged();

        ImageLoaderHelper.displayImage(data.getDetailIcon(), mDetailImage, options);
        //mDetailDescription.setText(data.getDetailDescription());
        Utils.instance().setTextViewText(mDetailDescription, data.getDetailDescription());
        
        adapt();
        
        mDetailDesMore.setVisibility(View.INVISIBLE);
        mDetailBottom.setVisibility(View.GONE);
        mHandler.sendEmptyMessageDelayed(MSG_CALCULATIING_TEXTVIEW_LINES, 100);
        
        TextView headerTitle = findTitleView();
        
        if (null != headerTitle)
        {
            headerTitle.setText(data.getDetailName());
        }
    }

    private String getExtraString(Intent intent, String key)
    {
        if (null != intent && null != key)
        {
            if (intent.hasExtra(key))
            {
                return intent.getStringExtra(key);
            }
        }

        return null;
    }

    private void requestData()
    {
        LoadingTask task = new LoadingTask(this, new ILoading()
        {

            @Override
            public void loading(IRequestListener listener)
            {
                NetUtil.getInstance().requestGameTopicDetailData(listener, mTopicId, 12);
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
                return TopicDetailActivity.this;
            }

            @Override
            public boolean isAsync()
            {
                return false;
            }
        });

        task.loading();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        
        StatService.onPause(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        
        StatService.onResume(this);
    }

    private View createLoadingFooter()
    {
        mLoadingFooter = View.inflate(this, R.layout.loading_layout, null);

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
            
            tv.setTextColor(getResources().getColor(R.color.no_more_data_text));
            tv.setText(R.string.footer_no_more_see_more);
            tv.setOnClickListener(this);
        }
        
        mPageIndex = -1;
    }

    @SuppressLint("InlinedApi")
    private void initView()
    {
        // 专题详情
        View header = LayoutInflater.from(this).inflate(R.layout.topic_detail_headerview, null);

        mDetailImage = (RoundCornerImageView) header.findViewById(R.id.picture);
        mDetailDescription = (TextView) header.findViewById(R.id.titile);
        mDetailDesMore = (ImageView) header.findViewById(R.id.more);
        mDetailBottom = header.findViewById(R.id.bottom);
        mRecommendList = (ListView) findViewById(R.id.list);
        
        final boolean[] enabled = {true, true};
        
        mDetailImage.setCornersEnabled(enabled);
        mDetailImage.setRadius(UIUtil.dip2px(this, 7f));
        
        mRecommendList.setOnScrollListener(mScrollListener);
        mRecommendList.setOnItemClickListener(mListItemClickListener);
        mRecommendList.addHeaderView(header);
        mRecommendList.addFooterView(createLoadingFooter());

        mHeight = mDetailDescription.getLineHeight() * 2 + mDetailDescription.getPaddingTop() + mDetailDescription.getPaddingBottom() + 1;
        
        mDetailDesMore.setOnClickListener(this);
        mDetailDescription.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        mCallbackContainer = new HashMap<String, TopicDetailActivity.TopicPackageCallback>();

        Intent intent = getIntent();
        mTopicId = getExtraString(intent, KEY_EXTRA_TOPIC_ID);
        mIsOpen = false;

        initView();

        mListAdapter = new RecommendListAdapter(this);
        mRecommendList.setAdapter(mListAdapter);
        mListAdapter.setDelegateView(mRecommendList);
        
        PackageHelper.registerPackageStatusChangeObserver(mDownloadListener);

        requestData();
    }

    private void loadMore()
    {
        if (!ConnectManager.isNetworkConnected(this))
        {
            CustomToast.showToast(this, getString(R.string.alert_network_inavailble));
            return ;
        }
        
        if (mRequestMoreId < 0 && mPageIndex > 0)
        {
            showLoadingFooter();
            mRequestMoreId = NetUtil.getInstance().requestGameTopicDetailMoreListData(mMoreListener, mTopicId, mPageIndex, 12);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        PackageHelper.unregisterPackageStatusChangeObserver(mDownloadListener);
        
        if (null != mCallbackContainer)
        {
            for (String key : mCallbackContainer.keySet())
            {
                PackageHelper.unregisterPackageStatusChangeObserver(mCallbackContainer.get(key));
            }
            
            mCallbackContainer.clear();
            mCallbackContainer = null;
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            case R.id.more:
            case R.id.titile:
            {
                mIsOpen = !mIsOpen;

                adapt();
            }
                break;
            case R.id.loading_text:
            	finish();
            	break;

            default:
                break;
        }
    }

    private void adapt()
    {
        LayoutParams lp = (LayoutParams) mDetailDescription.getLayoutParams();
        Drawable d = mIsOpen ? getResources().getDrawable(R.drawable.icon_up) : getResources().getDrawable(R.drawable.icon_down);
        String title = getText(R.string.more).toString();
        SpannableString spantitle = new SpannableString(title);

        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        spantitle.setSpan(new ImageSpan(d), title.length() - 1, title.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        lp.height = mIsOpen ? LayoutParams.WRAP_CONTENT : mHeight;
        mDetailDescription.setLayoutParams(lp);
        mDetailDescription.invalidate();
        
        if (mDetailDesMore.getVisibility() == View.VISIBLE)
        {
            mDetailDesMore.setImageResource(mIsOpen ? R.drawable.topic_expand_button_unfold_selector : R.drawable.topic_expand_button_fold_selector);
        }
    }

    @Override
    public void onRequestSuccess(BaseResult responseData)
    {
        mPageIndex = 1;
        initViewWithData((TopicDetailData) responseData);
    }

    @Override
    public void onRequestError(int requestTag, int requestId, int errorCode, String msg)
    {
    }

    @Override
    public int getLayout()
    {
        return R.layout.activity_game_topic_detail;
    }

    @Override
    public String getHeaderTitle()
    {
        return getExtraString(getIntent(), KEY_EXTRA_TOPIC_TITLE);
    }

    private IRequestListener mMoreListener = new IRequestListener()
    {

        @Override
        public void onRequestSuccess(BaseResult responseData)
        {
            TopicDetailMoreGamesData data = (TopicDetailMoreGamesData)responseData;
            TopicDetailData detailData = null;
            
            if (null != mListAdapter)
            {
                synchronized (responseData)
                {
                    detailData = mListAdapter.getTopicDetailData();

                    if (null != data && null != data.getDataList() && data.getDataList().size() > 0)
                    {
                        if (null != detailData)
                        {
                            detailData.appendRecommendList(data.getDataList());
                            mListAdapter.notifyDataSetChanged();
                            mPageIndex++;
                        }
                    }
                    else
                    {
                        showNoDataFooter();
                    }
                    
                    if (null != data)
                    {
                        int count = (null != detailData.getRecommendList() ? detailData.getRecommendList().size() : 0);
                        
                        if (count >= data.getTotalCount())
                        {
                            showNoMoreData();
                        }
                    }
                }
            }
            
            mRequestMoreId = -1;
        }

        @Override
        public void onRequestError(int requestTag, int requestId, int errorCode, String msg)
        {
            showNoDataFooter();
            CustomToast.showToast(TopicDetailActivity.this, getString(R.string.network_error_hint));

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
    
    final static int MSG_CALCULATIING_TEXTVIEW_LINES            = 1000;
    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CALCULATIING_TEXTVIEW_LINES:
                {
                    if (mDetailDescription.isShown() == false)
                    {
                        mHandler.sendEmptyMessageDelayed(MSG_CALCULATIING_TEXTVIEW_LINES, 70);
                        break;
                    }
                    
                    int lines = mDetailDescription.getLineCount();
                    
                    if (lines > 2)
                    {
                        mDetailDesMore.setVisibility(View.VISIBLE);
                        mDetailBottom.setVisibility(View.GONE);
                        
                        adapt();
                    }
                    else
                    {
                        mDetailDesMore.setVisibility(View.GONE);
                        mDetailBottom.setVisibility(View.VISIBLE);
                        mIsOpen = true;
                        adapt();
                    }
                }
                break;
                
                default:
                    break;
            }
        }
        
    };
    
    public void registerDownloadInfo(RecommendGameItem item)
    {
        if (null == item)
        {
            return ;
        }
        
        if (null == mCallbackContainer)
        {
            mCallbackContainer = new HashMap<String, TopicDetailActivity.TopicPackageCallback>();
        }
        
        if (!mCallbackContainer.containsKey(item.getGameDownloadUrl()))
        {
            TopicPackageCallback cb = new TopicPackageCallback();
            
            cb.info = item;
            
            mCallbackContainer.put(item.getGameDownloadUrl(), cb);
            PackageHelper.registerPackageStatusChangeObserver(cb);
        }
    }
    
    public void unregisterDownloadInfo(String url)
    {
        if (null != mCallbackContainer && null != url && mCallbackContainer.containsKey(url))
        {
            PackageHelper.unregisterPackageStatusChangeObserver(mCallbackContainer.get(url));
            mCallbackContainer.remove(url);
        }
    }
    
    class TopicPackageCallback implements PackageCallback
    {
        public RecommendGameItem info = null;

        @Override
        public void onPackageStatusChanged(PackageMode mode)
        {
            if (info.getPackageMode().downloadUrl != null && info.getPackageMode().downloadUrl.equals(mode.downloadUrl))
            {
                info.setPackageMode(mode);
            }
            else
            {
                return ;
            }
            
            mHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    TopicDetailData data = mListAdapter.getTopicDetailData();
                    PackageMode tmode = info.getPackageMode();
                    
                    if (Constants.DEBUG)
                    {
                        Log.d("T_DOWNLOAD", "name: " + info.getGameName() + " | download id: " + tmode.downloadId + " | url: " + info.getGameDownloadUrl());
                    }
                    
                    if (null != tmode && tmode.downloadId > 0 && null != data && null != data.getRecommendList())
                    {
                        int id = data.getRecommendList().indexOf(info);
                        int findex = mRecommendList.getFirstVisiblePosition();
                        int lindex = mRecommendList.getLastVisiblePosition();
                        CardHolderView holder = null;

                        if (Constants.DEBUG)
                        {
                            Log.d("T_DOWNLOAD", "list id: " + id + " | first visible: " + findex + " | last visible: " + lindex);
                        }
                        
                        if (id >= findex - 1 && id <= lindex - 1)
                        {
                            View view = mRecommendList.getChildAt(id - findex + 1);
                            
                            if (null != view)
                            {
                                holder = (CardHolderView)view.getTag();
                                if (null != holder)
                                {
                                    view.requestFocus();
                                    mListAdapter.changeDownloadViewStatus(info, holder);
                                    holder.card_download_iv.invalidate();
                                    holder.card_pb_tv.invalidate();
                                    view.invalidate();
                                }
                            }
                        }
                    }
                }
            });
        }
        
    }
    
    private OnItemClickListener mListItemClickListener = new OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (position > 0 && --position < mListAdapter.getCount())
            {
                RecommendGameItem item = (RecommendGameItem)mListAdapter.getItem(position); // because of header view
                
                if (null != item)
                {
                    Intent intent = new Intent(TopicDetailActivity.this, GameDetailsActivity.class);
                    
                    intent.putExtra(GameDetailConstants.KEY_GAME_ID, item.getGameId());
                    intent.putExtra(GameDetailConstants.KEY_GAME_NAME, item.getGameName());
                    
                    TopicDetailActivity.this.startActivity(intent);
                }
            }
        }
        
    };
    
    private PackageCallback mDownloadListener = new PackageCallback()
    {
        
        @Override
        public void onPackageStatusChanged(final PackageMode mode)
        {
            mHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    RecommendGameItem info = null;
                    PackageMode tmode = null;
                    int findex = mRecommendList.getFirstVisiblePosition();
                    int lindex = mRecommendList.getLastVisiblePosition();
                    int id = findex == 0 ? 0 : findex - 1;
                    int datasize = mListAdapter.getCount();
                    
                    if (Constants.DEBUG)
                    {
                        Log.d("T_DOWNLOAD", "onPackageStatusChanged >> mode id: " + mode.downloadId);
                    }
                    
                    if (mode.downloadId < 0)
                    {
                        Log.d("", "pause");
                    }
                    
                    lindex = lindex > datasize ? datasize : lindex;
                    
                    for (; id < lindex; ++id)
                    {
                        info = (RecommendGameItem)mListAdapter.getItem(id);
                        tmode = info.getPackageMode();
                        
                        String localurl = tmode.downloadUrl;
                        String modeurl = mode.downloadUrl;
                        
                        if (Constants.DEBUG)
                        {
                            Log.d("T_DOWNLOAD", "onPackageStatusChanged >> mode == -1 >> local: " + localurl + " | mode: " + modeurl);
                        }
                        
                        //if (tmode.downloadId > 0 && tmode.downloadId == mode.downloadId)
                        if (null != localurl && localurl.equals(modeurl))
                        {
                            info.setPackageMode(mode);
                            tmode = mode;
                            break;
                        }
                        else
                        {
                            info = null;
                            tmode = null;
                        }
                    }
                    
                    //if (null != info && null != tmode && tmode.downloadId > 0)
                    if (null != info && null != tmode)
                    {
                        CardHolderView holder = null;

                        if (Constants.DEBUG)
                        {
                            Log.d("T_DOWNLOAD", "onPackageStatusChanged >> name: " + info.getGameName() + " | download id: " + tmode.downloadId + " | url: " + info.getGameDownloadUrl());
                            Log.d("T_DOWNLOAD", "onPackageStatusChanged >> list id: " + id + " | first visible: " + findex + " | last visible: " + lindex);
                        }
                        
                        View view = mRecommendList.getChildAt(id - findex + 1);
                        
                        if (null != view)
                        {
                            holder = (CardHolderView)view.getTag();
                            if (null != holder)
                            {
                                view.requestFocus();
                                mListAdapter.changeDownloadViewStatus(info, holder);
                                holder.card_download_iv.invalidate();
                                holder.card_pb_tv.invalidate();
                                view.invalidate();
                            }
                        }
                    }
                }
            });
        }
    };

}
