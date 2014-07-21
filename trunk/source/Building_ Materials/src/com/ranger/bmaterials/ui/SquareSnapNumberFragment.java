package com.ranger.bmaterials.ui;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.SnapNumberAdapter;
import com.ranger.bmaterials.adapter.AbstractListAdapter.OnListItemClickListener;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.download.DefaultDownLoadCallBack;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.SnapNumber;
import com.ranger.bmaterials.mode.SnappedNumber;
import com.ranger.bmaterials.mode.SnapNumber.SnapNumberStatus;
import com.ranger.bmaterials.mode.SnappedNumber.ResCode;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.CustomDialog;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.NetWorkTipDialog;
import com.ranger.bmaterials.view.CustomDialog.ICustomDialog;
import com.ranger.bmaterials.work.DBTaskManager;

public class SquareSnapNumberFragment extends SquareSubPageFragment implements
		OnListItemClickListener {
	private Dialog progressDialog;

	private class PackageIntentReceiver extends BroadcastReceiver {

		public PackageIntentReceiver() {
			IntentFilter filter = new IntentFilter(
					BroadcaseSender.ACTION_SNAP_NUMBER);
			filter.addAction(BroadcaseSender.ACTION_SNAP_NUMBER_MUTI_ACTION);
			filter.addAction(BroadcaseSender.ACTION_SNAP_NUMBER_OVER);
			filter.addAction(BroadcaseSender.ACTION_SNAP_DETAIL_RESULT);
			filter.addAction(BroadcaseSender.ACTION_SNAP_NUMBER_NONE);
			filter.addAction(BroadcaseSender.ACTION_USER_LOGIN);
			filter.addAction(BroadcaseSender.ACTION_USER_LOGOUT);
			getActivity().registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (BroadcaseSender.ACTION_SNAP_NUMBER.equals(action)) {
				String id = intent
						.getStringExtra(BroadcaseSender.SNAP_NUMBER_ARG);
				refreshStateFromSendData(SnapNumberStatus.SNAPPED, id);
			} else if (BroadcaseSender.ACTION_USER_LOGIN.equals(action)) {
				// 刷新数据
				refresh(true);
			} else if (BroadcaseSender.ACTION_USER_LOGOUT.equals(action)) {
				// 刷新数据
				refresh(false);
			} else if (BroadcaseSender.ACTION_SNAP_NUMBER_MUTI_ACTION
					.equals(action)) {
				String id = intent
						.getStringExtra(BroadcaseSender.SNAP_NUMBER_ARG);
				refreshStateFromSendData(SnapNumberStatus.SNAPPED, id);
			} else if (BroadcaseSender.ACTION_SNAP_NUMBER_OVER.equals(action)) {
				String id = intent
						.getStringExtra(BroadcaseSender.SNAP_NUMBER_ARG);
				refreshStateFromSendData(SnapNumberStatus.OVER, id);
			} else if (BroadcaseSender.ACTION_SNAP_NUMBER_NONE.equals(action)) {
				String id = intent
						.getStringExtra(BroadcaseSender.SNAP_NUMBER_ARG);
				refreshStateFromSendData(SnapNumberStatus.NONE, id);
			} else if (BroadcaseSender.ACTION_SNAP_DETAIL_RESULT.equals(action)) {
				try {
					String id = intent
							.getStringExtra(BroadcaseSender.SNAP_NUMBER_ARG);
					int left = intent.getIntExtra(
							BroadcaseSender.SNAP_NUMBER_LEFT_COUNT_ARG, -1);
					SnapNumberStatus status = (SnapNumberStatus) intent
							.getSerializableExtra(BroadcaseSender.SNAP_NUMBER_STATUS_ARG);
					refreshStateFromSendData(status, left, id);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	protected void refresh(boolean flag) {
		if (adapter == null)
			return;
		adapter.setNotifyOnChange(false);
		// Toast.makeText(getActivity(), "用户"+(flag?"登陆":"登出"), 1).show();
		List<SnapNumber> data = adapter.getData();
		if (!flag) {
			if (data != null) {
				for (SnapNumber o : data) {
					if (o.getStatus() == SnapNumberStatus.NOT_SNAPPED) {
						o.setStatus(SnapNumberStatus.NOT_LOGIN);
					}
				}
			}
		}
		adapter.clear();
		setDataPage(true, false);
		adapter.setNotifyOnChange(true);
		adapter.notifyDataSetChanged();
//		super.triggerRefresh();
		showLoadingView();
		refreshTriggered();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (observer != null) {
			getActivity().unregisterReceiver(observer);
			observer = null;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		observer = new PackageIntentReceiver();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initClickEvent();

	}

	private void initClickEvent() {
		SnapNumberAdapter a = (SnapNumberAdapter) adapter;
		a.setOnListItemClickListener(this);
	}

	@Override
	public void onItemIconClick(View view, int position) {
		// do nothing
	}

	/**
	 * 抢号
	 */
	@Override
	public void onItemButtonClick(View view, int position) {
		SnapNumber item = (SnapNumber) adapter.getItem(position);
		String gameId = item.getGameId();
		String id = item.getId();

		MineProfile profile = MineProfile.getInstance();
		boolean isLogin = profile.getIsLogin();
		if (!isLogin) {
			CustomToast.showToast(getActivity(),
					getString(R.string.login_to_snap));
			Intent intent = new Intent(getActivity(), SapiLoginActivity.class);
			startActivity(intent);
		} else {
			if (DeviceUtil.isNetworkAvailable(getActivity())) {
				if (progressDialog == null) {
					NetUtil.getInstance().requestForSnapNumber(
							profile.getUserID(), profile.getSessionID(),
							String.valueOf(gameId), String.valueOf(id),
							new SnapNumberRequestListener());
					showProgressDialog();
				}
			} else {
				CustomToast.showToast(getActivity(),
						getString(R.string.alert_network_inavailble));
			}

		}
		if (getActivity() != null)
			ClickNumStatistics.addSnapNumberClickStatis(getActivity());
	}

	private class SnapNumberRequestListener implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			dismissProgressDialog();
			if (responseData.getErrorCode() == DcError.DC_OK) {
				SnappedNumber number = (SnappedNumber) responseData;
				int resCode = number.getResCode();
				switch (resCode) {
				case ResCode.SUCCESS:
					showResultDialog(number.getGameId(), number.getGrabId(),
							number.getNumber());
					refreshState(ResCode.SUCCESS, number);
					break;
				case ResCode.MULTIPLE_ACTION:
					// Toast.makeText(getActivity(), "您已经抢过了！",
					// Toast.LENGTH_LONG).show();
					showResultDialog(number.getGameId(), number.getGrabId(),
							number.getNumber(), getString(R.string.snapped));
					refreshState(ResCode.MULTIPLE_ACTION, number);
					break;
				case ResCode.BAD_LUCK:
					CustomToast.showToast(getActivity(),
							getString(R.string.snap_bad_luck));
					// Toast.makeText(getActivity(),
					// "对不起，您未抢中，您可以重新尝试！", Toast.LENGTH_LONG).show();
					break;
				case ResCode.NONE:
					CustomToast.showToast(getActivity(),
							getString(R.string.snap_null));
					// Toast.makeText(getActivity(), "对不起，号码已被抢光！",
					// Toast.LENGTH_LONG).show();
					refreshState(ResCode.NONE, number);
					break;
				case ResCode.OVER:
					CustomToast.showToast(getActivity(),
							getString(R.string.snap_over));
					// Toast.makeText(getActivity(), "对不起，活动已结束！",
					// Toast.LENGTH_LONG).show();
					refreshState(ResCode.OVER, number);
					break;
				case ResCode.PENDING:
					CustomToast.showToast(getActivity(),
							getString(R.string.snap_pending));
					// Toast.makeText(getActivity(), "对不起，活动还没开始！",
					// Toast.LENGTH_LONG).show();
					// refreshState(ResCode.OVER,number);
					break;
				default:
					CustomToast.showToast(getActivity(),
							getString(R.string.snap_failed));
					// Toast.makeText(getActivity(), "抢号失败",
					// Toast.LENGTH_LONG).show();
					break;
				}

			} else if (responseData.getErrorCode() == DcError.DC_NEEDLOGIN) {
				CustomToast.showToast(getActivity(),
						getString(R.string.need_login_tip));

				// session失效
				// Toast.makeText(getActivity(), "请重新登录",
				// Toast.LENGTH_LONG).show();
				MineProfile profile = MineProfile.getInstance();
				profile.setIsLogin(false);
				Intent intent = new Intent(getActivity(), SapiLoginActivity.class);
				startActivity(intent);

			} else {
				CustomToast.showToast(getActivity(),
						getString(R.string.snap_failed));
			}
		}

		@Override
		public void onRequestError(int requestTag, int requestId,
				int errorCode, String msg) {
			dismissProgressDialog();
			if (errorCode == DcError.DC_NEEDLOGIN) {
				CustomToast.showToast(getActivity(),
						getString(R.string.need_login_tip));

				// session失效
				MineProfile profile = MineProfile.getInstance();
				profile.setIsLogin(false);
				Intent intent = new Intent(getActivity(), SapiLoginActivity.class);
				startActivity(intent);
			} else {
				CustomToast.showToast(getActivity(),
						getString(R.string.snap_failed));
				// Toast.makeText(getActivity(), "抢号失败",
				// Toast.LENGTH_LONG).show();
			}

		}

	}

	private void showProgressDialog() {
		progressDialog = DuokuDialog.showProgressDialog(getActivity(), true,
				null);
	}

	private void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private PackageIntentReceiver observer;
	private TextView buttonLeft;

	/**
	 * 
	 * @param gameId
	 * @param grabId
	 * @param number
	 *            抢到的号码
	 * @param title
	 *            dialog的自定义标题
	 */
	private void showResultDialog(final String gameId, final String grabId,
			final String number, final String... title) {

		new AsyncTask<Void, Void, SnapNumber>() {

			@Override
			protected SnapNumber doInBackground(Void... params) {
				SnapNumber snapNumber = null;

				List<SnapNumber> data = adapter.getData();
				if (data == null) {
					return null;
				}
				for (SnapNumber number : data) {
					if (number.getGameId().equals(gameId)
							|| number.getId().equals(grabId)) {
						QueryInput qi = new QueryInput(number.getPackageName(),
								number.version, number.verCode,
								number.downloadUrl, number.getGameId());

						Map<QueryInput, PackageMode> status = PackageHelper
								.queryPackageStatus(qi);
						number.mode = status.get(qi);
						snapNumber = number;
						break;
					}
				}

				return snapNumber;
			}

			@Override
			protected void onPostExecute(SnapNumber snapNumber) {
				if (snapNumber != null) {
					doShowResultDialog(snapNumber, number, title);
				}

			};

		}.execute();

	}

	private void doShowResultDialog(final SnapNumber snapNumber,
			final String number, String... title) {

		CustomDialog resultDialog = new CustomDialog(getActivity());
		if (title != null && title.length > 0) {
			resultDialog.setTitle(title[0]);
		} else
			resultDialog.setTitle(getString(R.string.square_snap_num_success));

		ResultDialogClickListener resultDialogClickListener = new ResultDialogClickListener(
				resultDialog);
		ICustomDialog impl = new ICustomDialog() {

			@Override
			public void preAddView(CustomDialog dialog) {
				// TODO Auto-generated method stub
				PackageMode mode = snapNumber.mode;
				if (mode.status == PackageMode.UNDOWNLOAD) {
					buttonLeft.setText(R.string.download_game);
				} else if (mode.status == PackageMode.INSTALLED) {
					buttonLeft.setText(R.string.open_game);
				} else {
					buttonLeft.setText(R.string.view_detail);
				}

				buttonLeft.setTag(snapNumber);
			}

			@Override
			public View initOtherView(CustomDialog dialog) {
				// TODO Auto-generated method stub
				buttonLeft = dialog.buttonLeft;
				dialog.buttonRight.setText(R.string.close);

				View contentView = View.inflate(getActivity(),
						R.layout.snap_num_dialog_layout, null);

				TextView tvBody = (TextView) contentView
						.findViewById(R.id.custom_dialog_body);
				tvBody.setText(number);
				Button copyButton = (Button) contentView
						.findViewById(R.id.custom_dialog_body_button);
				copyButton.setTag(number);
				copyButton.setOnClickListener(dialog.getClickListner());
				return contentView;
			}
		};
		resultDialog.setImpl(impl).setClickListner(resultDialogClickListener)
				.createView().show();
	}

	class ResultDialogClickListener implements OnClickListener {
		private Dialog dialog;

		public ResultDialogClickListener(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 取消
			case R.id.dialog_button_right:
				dismissResultDialog(dialog);
				break;
			// 复制
			case R.id.custom_dialog_body_button:
				String number = (String) v.getTag();
				DeviceUtil.copyText(getActivity(), number);
				CustomToast.showToast(getActivity(),
						getActivity().getString(R.string.copy_successfully));
				break;
			// 下载游戏
			case R.id.dialog_button_left:
				SnapNumber snapNumber = (SnapNumber) v.getTag();
				PackageMode mode = snapNumber.mode;

				if (mode.status != PackageMode.UNDOWNLOAD) {
					dismissResultDialog(dialog);
					checkGame(mode);
				} else {
					// 变成查看详情 弹出toast 并开始下载任务
					if (checkWifiConfig(getActivity())) {
						showWifiDialog(snapNumber);
						return;
					}
					startDownLoad(snapNumber);
				}
				break;
			}
		}
	}

	private boolean startDownLoad(SnapNumber snapNumber) {
		if (ConnectManager.isNetworkConnected(getActivity())) {
			DownloadItemInput dInfo = new DownloadItemInput();
			dInfo.setGameId(snapNumber.getGameId());
			dInfo.setDownloadUrl(snapNumber.downloadUrl);
			dInfo.setDisplayName(snapNumber.gameName);
			dInfo.setPackageName(snapNumber.getPackageName());
			dInfo.setIconUrl(snapNumber.getIconUrl());
			dInfo.setAction(snapNumber.startAction);
			dInfo.setNeedLogin(snapNumber.isNeedLogin);
			dInfo.setVersion(snapNumber.version);
			dInfo.setVersionInt(snapNumber.verCode);
			dInfo.setSize(Long.valueOf(snapNumber.pkgSize));

			PackageHelper.download(dInfo, new DownLoadCallBack(getActivity(),
					snapNumber));

			return true;
		} else
			CustomToast.showToast(getActivity(),
					getString(R.string.network_error_hint));
		return false;
	}

	private static boolean checkWifiConfig(Context context) {
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (!ConnectManager.isWifi(context))
				return true;
		}
		return false;
	}

	private void showWifiDialog(final SnapNumber snapNumber) {
		final NetWorkTipDialog resultDialog = new NetWorkTipDialog(
				getActivity());

		resultDialog.setClickListner(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.dialog_button_right:
					resultDialog.dismiss();
					break;
				case R.id.dialog_button_left:
					startDownLoad(snapNumber);
					resultDialog.changeConfig();

					resultDialog.dismiss();
					break;

				default:
					break;
				}
			}
		}).createView().show();
	}

	private class DownLoadCallBack extends DefaultDownLoadCallBack {
		private SnapNumber snapNumber;

		public DownLoadCallBack(Activity cx, SnapNumber snapNumber) {
			super(cx);
			this.snapNumber = snapNumber;
		}

		@Override
		public void onDownloadResult(String downloadUrl, boolean status,
				long downloadId, String saveDest, final Integer reason) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					switch (reason) {
					case PackageMode.ERROR_HTTP_ERROR:
					case PackageMode.ERROR_DEVICE_NOT_FOUND:
					case PackageMode.ERROR_INSUFFICIENT_SPACE:
						break;
					default:
						buttonLeft.setText(R.string.view_detail);
						CustomToast.showToast(getActivity(),
								snapNumber.gameName
										+ getString(R.string.start_download));
						// DownloadStatistics.addDownloadGameStatistics(
						// getActivity(), snapNumber.gameName);
						snapNumber.mode.status = PackageMode.DOWNLOAD_RUNNING;
						break;
					}
				}
			});

			super.onDownloadResult(downloadUrl, status, downloadId, saveDest,
					reason);
		}

	}

	private void checkGame(final PackageMode mode) {
		DBTaskManager.submitTask(new Runnable() {
			@Override
			public void run() {
				String gameId = mode.gameId;

				if (mode.status == PackageMode.INSTALLED) {
					InstalledAppInfo info = AppManager.getInstance(
							getActivity()).getInstalledGame(mode.packageName);
					StartGame isg = null;

					if (info == null)
						isg = new StartGame(getActivity(), mode.packageName,
								null, gameId);
					else
						isg = new StartGame(getActivity(), mode.packageName,
								info.getExtra(), gameId, info.isNeedLogin());
					isg.startGame();
				} else {
					AppManager manager = AppManager.getInstance(getActivity());
                    manager.jumpToDetail(getActivity(), gameId, "", null, false);
                }
			}
		});
	}

	private void dismissResultDialog(final Dialog resultDialog) {
		if (resultDialog != null && resultDialog.isShowing()) {
			resultDialog.dismiss();
		}
	}

	private void refreshState(int rescode, SnappedNumber number) {
		List<SnapNumber> data = adapter.getData();
		if (data == null) {
			return;
		}
		adapter.setNotifyOnChange(false);
		for (SnapNumber snapNumber : data) {
			if (number.getGrabId().equals(snapNumber.getId())) {
				if (rescode == ResCode.SUCCESS
						|| rescode == ResCode.MULTIPLE_ACTION) {
					if (rescode == ResCode.SUCCESS) {
						int leftCount = snapNumber.getLeftCount();
						if (leftCount > 0) {
							snapNumber.setLeftCount(leftCount - 1);
						}
					}
					snapNumber.setStatus(SnapNumberStatus.SNAPPED);
				} else if (rescode == ResCode.NONE) {
					// snapNumber.setStatus(SnapNumberStatus.OVER);
					snapNumber.setLeftCount(0);
				} else if (rescode == ResCode.OVER) {
					snapNumber.setStatus(SnapNumberStatus.OVER);
				}
				break;
			}
		}
		adapter.setNotifyOnChange(true);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @param grabId
	 */
	private void refreshStateFromSendData(SnapNumberStatus status, String grabId) {
		List<SnapNumber> data = adapter.getData();
		if (data == null) {
			return;
		}
		for (SnapNumber snapNumber : data) {
			if (grabId.equals(snapNumber.getId())) {
				if (status != null) {
					if (status != SnapNumberStatus.NONE) {
						snapNumber.setStatus(status);
					}
					if (/* status == SnapNumberStatus.OVER || */status == SnapNumberStatus.NONE) {
						snapNumber.setLeftCount(0);
					}
					if (status == SnapNumberStatus.SNAPPED) {// 号码-1
						int leftCount = snapNumber.getLeftCount();
						snapNumber
								.setLeftCount((leftCount > 0) ? (leftCount - 1)
										: 0);
					}
				} else {

				}
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}

	private void refreshStateFromSendData(SnapNumberStatus status,
			int leftCount, String grabId) {
		List<SnapNumber> data = adapter.getData();
		if (data == null) {
			return;
		}
		for (SnapNumber snapNumber : data) {
			if (grabId != null && grabId.equals(snapNumber.getId())) {
				if (status != null) {
					snapNumber.setStatus(status);
					int totalCount = snapNumber.getTotalCount();
					if (leftCount <= totalCount && leftCount >= 0) {
						snapNumber.setLeftCount(leftCount);
					}
				}
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}

}