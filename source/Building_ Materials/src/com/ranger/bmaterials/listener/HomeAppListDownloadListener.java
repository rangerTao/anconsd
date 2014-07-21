package com.ranger.bmaterials.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.HomeAppListAdapter;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.download.DefaultDownLoadCallBack;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListBaseInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.view.NetWorkTipDialog;

public final class HomeAppListDownloadListener implements OnClickListener {
	private HomeAppListBaseInfo homeInfo;
	private boolean isBanner;

	public HomeAppListDownloadListener(HomeAppListBaseInfo homeInfo,
			boolean isBanner) {
		this.homeInfo = homeInfo;
		this.isBanner = isBanner;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		statusOnClick(v.getContext());
	}

	private void statusOnClick(Context context) {
		PackageMode packageMode = homeInfo.packageMode;
		if (packageMode == null)
			return;

		switch (packageMode.status) {
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:
		case PackageMode.UNDOWNLOAD:
            if (ConnectManager.isNetworkConnected(context)) {
                if (HomeAppListAdapter.checkWifiConfig(context)) {
                    showWifiDialog(context);
                    return;
                }
            } else{
                CustomToast.showToast(context,
                        context.getString(R.string.network_error_hint));
                return;
            }


			startDownLoad(context);
			break;

		case PackageMode.DOWNLOADED:
			if (packageMode.isDiffDownload) {
				PackageHelper.sendMergeRequest(packageMode, true);
			}
		case PackageMode.CHECKING_FINISHED:
			PackageHelper.installApp((Activity) context, homeInfo.pkgname,
					homeInfo.gameid, packageMode.downloadDest);
			break;

		case PackageMode.INSTALLED:
			startGame(context);
			break;

		}

	}

	public void showWifiDialog(final Context context) {
		final NetWorkTipDialog resultDialog = new NetWorkTipDialog(context);

		resultDialog.setClickListner(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.dialog_button_right:
					resultDialog.dismiss();
					break;
				case R.id.dialog_button_left:
					startDownLoad(context);
					resultDialog.changeConfig();

					resultDialog.dismiss();
					break;

				default:
					break;
				}
			}
		}).createView().show();
	}

	private void startGame(Context context) {
		StartGame isg = new StartGame(context, homeInfo.pkgname,
				homeInfo.startaction, homeInfo.gameid, false);
		isg.startGame();
		ClickNumStatistics.addHomeStartStatistics(context, homeInfo.gamename);
	}

	private boolean startDownLoad(Context context) {

        DownloadItemInput dInfo = new DownloadItemInput();
        dInfo.setGameId(homeInfo.gameid);
        dInfo.setDownloadUrl(homeInfo.downloadurl);
        dInfo.setDisplayName(homeInfo.gamename);
        dInfo.setPackageName(homeInfo.pkgname);
        dInfo.setIconUrl(homeInfo.gameicon);
        dInfo.setAction(homeInfo.startaction);
        dInfo.setVersion(homeInfo.versionname);
        try {
            dInfo.setVersionInt(Integer.valueOf(homeInfo.versioncode));
            if (homeInfo.pkgsize != null && !homeInfo.pkgsize.equals(""))
                dInfo.setSize(Long.valueOf(homeInfo.pkgsize));

            PackageHelper.download(dInfo, new DownLoadCallBack(
                    (Activity) context, homeInfo));
            // DownloadStatistics.addDownloadGameStatistics(context,
            // homeInfo.gamename);
            if (isBanner)
                DownloadStatistics.addHomeRecommendBannerGameDownload(context,
                        homeInfo.gamename);
            else
                DownloadStatistics.addHomeRecommendListGameDownload(context,
                        homeInfo.gamename);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	private class DownLoadCallBack extends DefaultDownLoadCallBack {
		public HomeAppListBaseInfo info;

		public DownLoadCallBack(Activity context, HomeAppListBaseInfo info) {
			super(context);
			// TODO Auto-generated constructor stub
			this.info = info;
		}

		@Override
		public void onDownloadResult(String downloadUrl, boolean status,
				long downloadId, String saveDest, Integer reason) {
			// TODO Auto-generated method stub
			info.downloadId = downloadId;
			super.onDownloadResult(downloadUrl, status, downloadId, saveDest,
					reason);
		}

	}
}
