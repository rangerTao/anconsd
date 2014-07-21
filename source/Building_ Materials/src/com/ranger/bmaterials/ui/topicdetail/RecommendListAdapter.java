package com.ranger.bmaterials.ui.topicdetail;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.HomeAppListAdapter;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.download.DefaultDownLoadCallBack;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.CircleProgressBar;
import com.ranger.bmaterials.view.GameLabelView;
import com.ranger.bmaterials.view.ImageViewForList;
import com.ranger.bmaterials.view.NetWorkTipDialog;

public class RecommendListAdapter extends BaseAdapter
{
    private Context mContext = null;
    private volatile TopicDetailData mData = null;

    private volatile List<RecommendGameItem> mRecommendList;

    public RecommendListAdapter(Context context)
    {
        mContext = context;
    }
    
    public void setTopicDetialData(TopicDetailData data)
    {
        mData = data;

        if (null != data)
        {
            mRecommendList = data.getRecommendList();
        }
        
        notifyDataSetChanged();
    }

    public TopicDetailData getTopicDetailData()
    {
        return mData;
    }

    @Override
    public int getCount()
    {
        int count = 0;

        if (null != mRecommendList && mRecommendList.size() > 0)
        {
            count = mRecommendList.size();
        }

        return count;
    }

    @Override
    public Object getItem(int position)
    {
        if (null != mRecommendList && position < mRecommendList.size())
        {
            return mRecommendList.get(position);
        }
        else
        {
            return null;
        }
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        CardHolderView cardHv = null;
        final RecommendGameItem item = (null != mRecommendList && position < mRecommendList.size()) ? mRecommendList.get(position) : null;

        if (null == convertView)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.home_app_list_card_item_layout, null);
            cardHv = new CardHolderView();

            cardHv.card_name = (TextView) convertView.findViewById(R.id.home_app_item_card_name);
            cardHv.card_download_times = (TextView) convertView.findViewById(R.id.home_app_item_card_download_times);
            cardHv.card_size = (TextView) convertView.findViewById(R.id.home_app_item_card_size);
            cardHv.card_download_tv = (TextView) convertView.findViewById(R.id.home_app_item_card_download_tv);
            cardHv.card_recommend_tv = (TextView) convertView.findViewById(R.id.home_app_item_card_recommend_tv);
            cardHv.card_icon = (RoundCornerImageView) convertView.findViewById(R.id.home_app_item_card_iv);
            cardHv.card_download_iv = (CircleProgressBar) convertView.findViewById(R.id.home_app_item_card_download_iv);
            cardHv.card_rating = (RatingBar) convertView.findViewById(R.id.home_app_item_card_rating);
            cardHv.card_pb_tv = (TextView) convertView.findViewById(R.id.home_app_item_card_download_pb_tv);
            cardHv.card_game_label = (GameLabelView) convertView.findViewById(R.id.home_app_item_card_label_name);
            cardHv.card_icon.setDisplayImageOptions(ImageLoaderHelper.getCustomOption(R.drawable.game_icon_list_default));
            convertView.setTag(cardHv);
        }
        else
        {
            cardHv = (CardHolderView) convertView.getTag();
        }

        setTouchDelegate((ViewGroup) cardHv.card_download_iv.getParent());
        
        View view = (ViewGroup) cardHv.card_download_iv.getParent();
        
        view.setTag(item);
        view.setOnClickListener(mStatusClickListener);
        
        if (null != item)
        {
            cardHv.card_name.setText(item.getGameName());
            cardHv.card_download_times.setText(StringUtil.formatTimes(item.getGameDownloadTimes()));
            cardHv.card_size.setText(Formatter.formatFileSize(mContext, item.getPackageSize()));
            //cardHv.card_recommend_tv.setText(item.getGameRecommendation());
            Utils.instance().setTextViewText(cardHv.card_recommend_tv, item.getGameRecommendation());
            cardHv.card_icon.setImageUrl(item.getGameIcon());
            cardHv.card_rating.setRating(item.getGameStar());

            if (item.getGameLabelName() != null && !item.getGameLabelName().equals(""))
            {
                cardHv.card_game_label.setText(item.getGameLabelName());
                cardHv.card_game_label.setLabelColor(item.getGameLabelColor());
                cardHv.card_game_label.setVisibility(View.VISIBLE);
            }
            else
            {
                cardHv.card_game_label.setVisibility(View.GONE);
            }
            
//            if (item.getPackageMode().downloadId > 0)
//            {
//                ((TopicDetailActivity)mContext).registerDownloadInfo(item);
//            }

            changeDownloadViewStatus(item, cardHv);
        }
        else
        {
        }

        return convertView;
    }

    private int calPercent(long currentBytes, long totalBytes)
    {
        return (int) ((currentBytes / (1.0f * totalBytes)) * 100);
    }

    private void showDownloading(RecommendGameItem item, CardHolderView cardHv)
    {
        int percent = 0;

        if (null != item && null != item.getPackageMode())
        {
            percent = calPercent(item.getPackageMode().currentSize, item.getPackageMode().totalSize);
        }

        cardHv.card_pb_tv.setVisibility(View.VISIBLE);
        cardHv.card_pb_tv.setText(percent + "%");
        cardHv.card_download_iv.setCurrentPercent(percent);
        cardHv.card_download_iv.setCustomMode(true);
        cardHv.card_download_iv.setEnabled(false);
        cardHv.card_download_tv.setText(R.string.downloading);
    }

    private void showInstalling(CardHolderView cardHv)
    {
        cardHv.card_pb_tv.setVisibility(View.GONE);
        cardHv.card_download_iv.setCustomMode(false);
        cardHv.card_download_iv.setImageResource(R.drawable.btn_download_install_selector);
        cardHv.card_download_tv.setText(R.string.installing);
        cardHv.card_download_iv.setEnabled(false);
    }

    private void showDown(CardHolderView cardHv)
    {
        cardHv.card_pb_tv.setVisibility(View.GONE);
        cardHv.card_download_iv.setCustomMode(false);
        cardHv.card_download_iv.setImageResource(R.drawable.btn_download_selector);
        cardHv.card_download_tv.setText(R.string.download);
        cardHv.card_download_iv.setEnabled(true);
    }

    private void showUpdate(CardHolderView cardHv)
    {
        cardHv.card_pb_tv.setVisibility(View.GONE);
        cardHv.card_download_iv.setCustomMode(false);
        cardHv.card_download_iv.setImageResource(R.drawable.btn_download_update_selector);
        cardHv.card_download_tv.setText(R.string.update);
        cardHv.card_download_iv.setEnabled(true);
    }

    private void showUpdateDiff(CardHolderView cardHv)
    {
        cardHv.card_pb_tv.setVisibility(View.GONE);
        cardHv.card_download_iv.setCustomMode(false);
        cardHv.card_download_iv.setImageResource(R.drawable.btn_download_diff_update_selector);
        cardHv.card_download_tv.setText(R.string.update);
        cardHv.card_download_iv.setEnabled(true);
    }

    private void showInstall(CardHolderView cardHv)
    {
        cardHv.card_pb_tv.setVisibility(View.GONE);
        cardHv.card_download_iv.setCustomMode(false);
        cardHv.card_download_iv.setImageResource(R.drawable.btn_download_install_selector);
        cardHv.card_download_tv.setText(R.string.install);
        cardHv.card_download_iv.setEnabled(true);
    }

    private void showUnCompeleted(RecommendGameItem item, CardHolderView cardHv)
    {
        int percent = 0;

        if (null != item && null != item.getPackageMode())
        {
            percent = calPercent(item.getPackageMode().currentSize, item.getPackageMode().totalSize);
        }

        cardHv.card_pb_tv.setVisibility(View.VISIBLE);
        cardHv.card_pb_tv.setText(percent + "%");
        cardHv.card_download_iv.setCurrentPercent(percent);
        cardHv.card_download_iv.setCustomMode(true);
        cardHv.card_download_iv.setEnabled(false);
        cardHv.card_download_tv.setText(R.string.uncompeleted);
    }

    private void showStart(CardHolderView cardHv)
    {
        cardHv.card_pb_tv.setVisibility(View.GONE);
        cardHv.card_download_iv.setCustomMode(false);
        cardHv.card_download_iv.setImageResource(R.drawable.icon_start_list);
        cardHv.card_download_tv.setText(R.string.open);
        cardHv.card_download_iv.setEnabled(true);
    }

    public boolean startDownLoad(RecommendGameItem info)
    {
        if (ConnectManager.isNetworkConnected(mContext))
        {
            DownloadItemInput dInfo = new DownloadItemInput();
            
            dInfo.setGameId(info.getGameId());
            dInfo.setDownloadUrl(info.getGameDownloadUrl());
            dInfo.setDisplayName(info.getGameName());
            dInfo.setPackageName(info.getPackageName());
            dInfo.setIconUrl(info.getGameIcon());
            dInfo.setAction(info.getGameStartAction());
            dInfo.setVersion(info.getGameVersionName());
            dInfo.setVersionInt(info.getGameVersionCode());
            dInfo.setSize(info.getPackageSize());
            
            if (Constants.DEBUG)
            {
                Log.d("T_DOWNLOAD", "game id: " + info.getGameId());
                Log.d("T_DOWNLOAD", "download url: " + info.getGameDownloadUrl());
                Log.d("T_DOWNLOAD", "display name: " + info.getGameName());
                Log.d("T_DOWNLOAD", "package name: " + info.getPackageName());
                Log.d("T_DOWNLOAD", "icon url: " + info.getGameIcon());
                Log.d("T_DOWNLOAD", "action: " + info.getGameStartAction());
                Log.d("T_DOWNLOAD", "version: " + info.getGameVersionName());
                Log.d("T_DOWNLOAD", "version int: " + info.getGameVersionCode());
                Log.d("T_DOWNLOAD", "size: " + info.getPackageSize());
            }

            PackageHelper.download(dInfo, new DownLoadCallBack((Activity) mContext, info));
//            DownloadStatistics.addDownloadGameStatistics(mContext, info.getGameName());
            DownloadStatistics.addHomeRecommendListGameDownload(mContext, info.getGameName());
            return true;
        }
        else
        {
            CustomToast.showToast(mContext, mContext.getString(R.string.network_error_hint));
        }
        return false;
    }

    private void statusOnClick(RecommendGameItem info)
    {
        PackageMode packageMode = info.getPackageMode();

        if (packageMode == null)
        {
            return;
        }

        switch (packageMode.status)
        {
            case PackageMode.UPDATABLE:
            case PackageMode.UPDATABLE_DIFF:
            case PackageMode.UNDOWNLOAD:
                if (HomeAppListAdapter.checkWifiConfig(mContext))
                {
//                    DuokuDialog.showNetworkAlertDialog((Activity) mContext, HomeAppListAdapter.SHOW_WIFI_DIALOG_DOWNLOAD_REQUEST, "false", null, info);
                	showWifiDialog(info);
                	return;
                }
                startDownLoad(info);

                break;

            case PackageMode.DOWNLOADED:
                if (packageMode.isDiffDownload)
                {
                    PackageHelper.sendMergeRequest(packageMode, true);
                }
            case PackageMode.CHECKING_FINISHED:
                PackageHelper.installApp((Activity) mContext, info.getPackageName(), info.getGameId(), packageMode.downloadDest);
                break;

            case PackageMode.INSTALLED:
                startGame(info);
                break;

            default:
                break;
        }
    }

    private void startGame(RecommendGameItem info)
    {
        StartGame isg = new StartGame(mContext, info.getPackageName(), info.getGameStartAction(), info.getGameId(), false);

        isg.startGame();
    }

    public void changeDownloadViewStatus(RecommendGameItem item, CardHolderView cardHv)
    {
        if (item == null)
        {
            return;
        }

        PackageMode packageMode = item.getPackageMode();
        if (packageMode == null)
        {
            return;
        }
        
        if (Constants.DEBUG)
        {
            Log.d("TOPIC_DETAIL_DOWNLOAD", "name: " + item.getGameName() + "| url: " + packageMode.downloadUrl + " | status: " + packageMode.status);
        }
        
        switch (packageMode.status)
        {
            case PackageMode.CHECKING_FINISHED:
            case PackageMode.DOWNLOADED:
                showInstall(cardHv);
                break;
            case PackageMode.UPDATABLE:
                showUpdate(cardHv);
                break;
            case PackageMode.UPDATABLE_DIFF:
                showUpdateDiff(cardHv);
                break;
            case PackageMode.UNDOWNLOAD:
                showDown(cardHv);
                break;

            case PackageMode.DOWNLOAD_PENDING:
            case PackageMode.DOWNLOAD_RUNNING:
                showDownloading(item, cardHv);
                break;

            case PackageMode.DOWNLOAD_PAUSED:
            case PackageMode.DOWNLOAD_FAILED:
                showUnCompeleted(item, cardHv);
                break;

            case PackageMode.INSTALLED:
                showStart(cardHv);
                break;

            case PackageMode.CHECKING:
            case PackageMode.MERGING:
            case PackageMode.INSTALLING:
                showInstalling(cardHv);
                break;

            case PackageMode.MERGE_FAILED:
                showUnCompeleted(item, cardHv);
                break;

            default:
                break;
        }
    }

    // 自定义触控区域
    private void setTouchDelegate(final View v)
    {
        final View parent = (View) v.getParent();
        parent.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect delegateArea = new Rect();
                v.getHitRect(delegateArea);

                int newW = 72;// dp=108px in 480*800
                int newh = 72;// dp=108px in 480*800
                int wDp = UIUtil.px2dip(mContext, v.getWidth());
                int hDp = UIUtil.px2dip(mContext, v.getHeight());

                int offsetW = UIUtil.dip2px(mContext, newW - wDp);
                int offsetH = UIUtil.dip2px(mContext, newh - hDp);
                delegateArea.top -= offsetH / 2;
                delegateArea.bottom += offsetH / 2;
                delegateArea.left -= offsetW / 2;
                delegateArea.right += offsetW / 2;

                TouchDelegate expandedArea = new TouchDelegate(delegateArea, v);
                parent.setTouchDelegate(expandedArea);
            }
        });
    }
    
    ListView mListView;
    public void setDelegateView(ListView view)
    {
        mListView = view;
    }

    private class DownLoadCallBack extends DefaultDownLoadCallBack
    {
        RecommendGameItem item = null;
        
        public DownLoadCallBack(Activity context, RecommendGameItem info)
        {
            super(context);
            item = info;
        }

        @Override
        public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason)
        {
            super.onDownloadResult(downloadUrl, status, downloadId, saveDest, reason);
            item.getPackageMode().downloadId = downloadId;
            item.getPackageMode().downloadUrl = downloadUrl;
//            
//            if (Constants.DEBUG)
//            {
//                Log.d("T_DOWNLOAD", "name: " + item.getGameName() + " | download id: " + downloadId + " | url: " + downloadUrl);
//            }
            
//            ((TopicDetailActivity)mContext).registerDownloadInfo(item);
        }
    }
    
    public void showWifiDialog(final RecommendGameItem info)
    {
        final NetWorkTipDialog resultDialog = new NetWorkTipDialog(mContext);

        resultDialog.setClickListner(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                switch (v.getId())
                {
                    case R.id.dialog_button_right:
                        resultDialog.dismiss();
                        break;
                    case R.id.dialog_button_left:
                        startDownLoad(info);
                        resultDialog.changeConfig();

                        resultDialog.dismiss();
                        break;

                    default:
                        break;
                }
            }
        }).createView().show();
    }
    
    private OnClickListener mStatusClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            RecommendGameItem item = (RecommendGameItem)v.getTag();
            
            if (null != item)
            {
                statusOnClick(item);
            }
        }
    };

}
