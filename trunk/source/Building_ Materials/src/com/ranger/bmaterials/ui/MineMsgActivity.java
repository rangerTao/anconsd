package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.MineMsgResult;
import com.ranger.bmaterials.sapi.SapiLoginActivity;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase;
import com.ranger.bmaterials.view.pull.PullToRefreshListView;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnLastItemVisibleListener;
import com.ranger.bmaterials.view.pull.PullToRefreshBase.OnRefreshListener2;

//test: skyeye 111111
public class MineMsgActivity extends Activity implements OnClickListener, OnItemClickListener, OnRefreshListener2<ListView>, IRequestListener {

	private CustomProgressDialog progressDialog;
	private static int MSGTYPE_DELETE = 0;
	private static int MSGTYPE_SET_READ = 1;
	private static int MSGTYPE_ALL = 0;
	private static int MSGTYPE_UNREAD = 1;
	private static int MSGTYPE_READ = 2;

	private View btnBack;
	private View layout_edit;
	private ImageView img_edit;
	private boolean isEditing;

	private List<MineMsgItemInfo> mlistMsgInfo;
	private List<MineMsgItemInfo> delListInfo;
	private MineMsgAdapter msgInfoListAdapter = null;

	private PullToRefreshListView plv;

	private ViewGroup viewContainer;
	private ViewGroup editPane;
	private ViewGroup noMsgViewContainer;
	private ViewGroup errorContainer;

	private int pageIndex = 1;
	private boolean noMoreMsg = false;
	private int pageNum = 20;
	private int requestId = 0;
	private int totalCount = 0;

	private View layout_loading_msg;
	boolean msgRequestSend = false;

	private int totalcheckednum = 0;
	TextView selectAll;
	TextView delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine_activity_msg);
		((TextView) findViewById(R.id.label_title)).setText("我的消息");

		btnBack = findViewById(R.id.img_back);
		btnBack.setOnClickListener(this);

		isEditing = false;
		layout_edit = findViewById(R.id.layout_msgedit);
		layout_edit.setVisibility(View.VISIBLE);
		layout_edit.setEnabled(false);
		img_edit = (ImageView) findViewById(R.id.img_msgedit);

		layout_edit.setOnClickListener(this);

		mlistMsgInfo = new ArrayList<MineMsgItemInfo>();
		delListInfo = new ArrayList<MineMsgItemInfo>();
		msgInfoListAdapter = new MineMsgAdapter(this, mlistMsgInfo);

		plv = (PullToRefreshListView) findViewById(R.id.listview_mine_msgs);
		plv.setOnRefreshListener(this);
		plv.setAdapter(msgInfoListAdapter);
		plv.setOnItemClickListener(this);

		findViewById(R.id.btn_msg_select_all).setOnClickListener(this);
		findViewById(R.id.btn_msg_delete).setOnClickListener(this);

		viewContainer = (ViewGroup) findViewById(R.id.layout_mine_msg_view_container);
		editPane = (ViewGroup) findViewById(R.id.layout_mine_msg_editpane);
		noMsgViewContainer = (ViewGroup) findViewById(R.id.label_mine_msg_none_pane);
		noMsgViewContainer.setVisibility(View.INVISIBLE);
		errorContainer = (ViewGroup) findViewById(R.id.error_hint);
		errorContainer.setVisibility(View.GONE);
		errorContainer.setOnClickListener(this);

		isEditing = false;
		img_edit.setImageResource(R.drawable.btn_mine_msg_titlebar_edit_bg);
		editPane.setVisibility(View.GONE);
		layout_loading_msg = findViewById(R.id.layout_loading_msg);

		selectAll = (TextView) findViewById(R.id.btn_msg_select_all);
		delete = (TextView) findViewById(R.id.btn_msg_delete);

		plv.setOnLastItemVisibleListener(OnLastItemVisibleListener);
		footer = createFooter();
	}

	OnLastItemVisibleListener OnLastItemVisibleListener = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if (!isLoadingMore && !noMoreMsg) {
				setFooterVisible(true);
				isLoadingMore = true;
				getMsgInfo();
			} else if (showNoMoreTip && !isLoadingMore) {
				showNoMoreTip = false;
				CustomToast.showLoginRegistErrorToast(MineMsgActivity.this, CustomToast.DC_ERR_NO_MORE_DATA);
			}
		}
	};
	private View footer;
	private boolean isLoadingMore;
	private boolean showNoMoreTip = true;

	private void setFooterVisible(boolean visible ){
		ListView listView = plv.getRefreshableView();
		
		if (visible) {
		    listView.addFooterView(footer);
			footer.setVisibility(View.VISIBLE);
		    listView.setSelection(listView.getBottom());
		} else {
			listView.removeFooterView(footer);
		}
	}

	private View createFooter() {
		View view = View.inflate(this, R.layout.loading_layout, null);
		TextView subView = (TextView) view.findViewById(R.id.loading_text);
		subView.setText(R.string.pull_to_refresh_refreshing_label);
		view.setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (errorContainer.getVisibility() == View.VISIBLE && MineProfile.getInstance().getIsLogin()) {
			refreshMsgInfo();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mlistMsgInfo.size() > 0)
			msgInfoListAdapter.notifyDataSetChanged();

		if (mlistMsgInfo.size() <= 0 && !msgRequestSend) {
			msgRequestSend = true;
			layout_loading_msg.setVisibility(View.VISIBLE);
			getMsgInfo();
		}
	}

	private void getMsgInfo() {
		if (noMoreMsg) {
			requestFinished(true);
		} else {
			String userid = MineProfile.getInstance().getUserID();
			String sessionid = MineProfile.getInstance().getSessionID();

			if (requestId > 0)
				NetUtil.getInstance().cancelRequestById(requestId);
			requestId = NetUtil.getInstance().requestMyMessage(userid, sessionid, MSGTYPE_ALL, pageIndex, pageNum, this);
		}
	}

	private void refreshMsgInfo() {
		showNoMoreTip = true;
		pageIndex = 1;
		noMoreMsg = false;
		getMsgInfo();
	}

	private void setEditState(boolean editing) {

		if (editing) {
			img_edit.setImageResource(R.drawable.btn_mine_msg_titlebar_edit_bg_pressed);
			editPane.setVisibility(View.VISIBLE);
		} else {
			img_edit.setImageResource(R.drawable.btn_mine_msg_titlebar_edit_bg);
			editPane.setVisibility(View.GONE);
			for (MineMsgItemInfo itemInfo : mlistMsgInfo) {
				itemInfo.setChecked(false);
			}
		}

		msgInfoListAdapter.setEditMode(isEditing);

		if (mlistMsgInfo.size() <= 0) {
			layout_edit.setEnabled(false);
		} else {
			layout_edit.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();

		switch (viewID) {
		case R.id.img_back:
			this.finish();
			break;

		case R.id.error_hint:
			noMsgViewContainer.setVisibility(View.GONE);
			viewContainer.setVisibility(View.GONE);
			errorContainer.setVisibility(View.GONE);
			layout_loading_msg.setVisibility(View.VISIBLE);
			refreshMsgInfo();
			break;

		case R.id.layout_msgedit:

			isEditing = !isEditing;
			setEditState(isEditing);
			break;

		case R.id.btn_msg_select_all:

			if (totalcheckednum < mlistMsgInfo.size()) {
				for (MineMsgItemInfo itemInfo : mlistMsgInfo) {
					itemInfo.setChecked(true);
				}
			} else if (totalcheckednum == mlistMsgInfo.size()) {
				for (MineMsgItemInfo itemInfo : mlistMsgInfo) {
					itemInfo.setChecked(false);
				}
			}

			msgInfoListAdapter.notifyDataSetChanged();
			this.refreshButtonState();
			break;

		case R.id.btn_msg_delete:
			delListInfo.clear();

			for (MineMsgItemInfo itemInfo : mlistMsgInfo) {
				if (itemInfo.getChecked())
					delListInfo.add(itemInfo);
			}

			if (delListInfo.size() <= 0) {
				return;
			}

			AlertDialog dialog = new AlertDialog.Builder(MineMsgActivity.this).setTitle("删除消息")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							String userid = MineProfile.getInstance().getUserID();
							String sessionid = MineProfile.getInstance().getSessionID();

							requestId = NetUtil.getInstance().requestDeleteMessage(userid, sessionid, MSGTYPE_DELETE, delListInfo,
									MineMsgActivity.this);

							progressDialog = CustomProgressDialog.createDialog(MineMsgActivity.this);
							progressDialog.setMessage("删除消息中...");
							progressDialog.show();
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int pos = position - 1;
		
		MineMsgItemInfo msgInfo = mlistMsgInfo.get(pos);

		if (isEditing) {
			msgInfo.setChecked(!msgInfo.getChecked());
			msgInfoListAdapter.notifyDataSetChanged();
			return;
		}

		Intent intent = new Intent(this, MineMsgDetailActivity.class);
		intent.putExtra(Constants.JSON_MSGID, msgInfo.msgID);
		intent.putExtra(Constants.JSON_MSGTITLE, msgInfo.msgTitle);
		intent.putExtra(Constants.JSON_MSGTIME, msgInfo.msgTime);
		intent.putExtra("position", pos);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
		super.onActivityResult(requestCode, resultCode, data);
		int position = data.getIntExtra("position", -1);
		boolean ret = data.getBooleanExtra("result", false);

		if (ret && position >= 0 && position < mlistMsgInfo.size()) {
			MineMsgItemInfo msgInfo = mlistMsgInfo.get(position);
			msgInfo.unreadMsg = false;
			msgInfoListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRequestSuccess(BaseResult responseData) {

		int requestTag = StringUtil.parseInt(responseData.getTag());

		if (requestTag == Constants.NET_TAG_GET_MY_MESSAGE) {
			MineMsgResult result = (MineMsgResult) responseData;

			totalCount = result.totalcount;

			if (pageIndex == 1) {
				mlistMsgInfo.clear();
			}

			if (result.msgListInfo.size() > 0) {
				for (MineMsgItemInfo item : result.msgListInfo) {
					item.observer = this;
					mlistMsgInfo.add(item);
				}
				msgInfoListAdapter.notifyDataSetChanged();
				pageIndex++;
			}

			if (mlistMsgInfo.size() >= totalCount) {
				noMoreMsg = true;
				setFooterVisible(false);
			}
		} else if (requestTag == Constants.NET_TAG_DEL_SETREAD_MESSAGE) {
			progressDialog.dismiss();
			mlistMsgInfo.removeAll(delListInfo);

			if (mlistMsgInfo.size() <= 0) {
				refreshMsgInfo();
			} else {
				msgInfoListAdapter.notifyDataSetChanged();
			}

			isEditing = false;
			setEditState(isEditing);
		}

		requestFinished(true);
	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
		requestFinished(false);

		if (requestTag == Constants.NET_TAG_DEL_SETREAD_MESSAGE) {
			progressDialog.dismiss();
		}
		switch (errorCode) {
		case DcError.DC_NEEDLOGIN:// 需要登录
			MineProfile.getInstance().setIsLogin(false);
			Intent intent = new Intent(this, SapiLoginActivity.class);
			startActivity(intent);
			CustomToast.showToast(this, getResources().getString(R.string.need_login_tip));
			finish();
			break;
		default:
			break;
		}
		CustomToast.showLoginRegistErrorToast(this, errorCode);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		refreshMsgInfo();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMsgInfo();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (requestId > 0) {
			NetUtil.getInstance().cancelRequestById(requestId);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (isEditing && keyCode == KeyEvent.KEYCODE_BACK) {
			isEditing = false;
			setEditState(isEditing);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void requestFinished(boolean succeed) {
		plv.onRefreshComplete();
		this.refreshButtonState();
		requestId = 0;
		layout_loading_msg.setVisibility(View.GONE);
		
		isLoadingMore = false;
		setFooterVisible(false);

		if (succeed) {

			if (mlistMsgInfo.size() > 0) {
				noMsgViewContainer.setVisibility(View.GONE);
				viewContainer.setVisibility(View.VISIBLE);
			} else {

				noMsgViewContainer.setVisibility(View.VISIBLE);
				viewContainer.setVisibility(View.GONE);
			}
			errorContainer.setVisibility(View.GONE);
		} else {
			noMsgViewContainer.setVisibility(View.GONE);
			viewContainer.setVisibility(View.GONE);
			errorContainer.setVisibility(View.VISIBLE);
		}

		if (mlistMsgInfo.size() <= 0) {
			layout_edit.setEnabled(false);
		} else {
			layout_edit.setEnabled(true);
		}
	}

	public void itemChecked(boolean checked) {
		this.refreshButtonState();
	}

	private void refreshButtonState() {

		totalcheckednum = 0;

		for (MineMsgItemInfo item : mlistMsgInfo) {
			if (item.getChecked()) {
				totalcheckednum++;
			}
		}

		if (totalcheckednum == mlistMsgInfo.size()) {
			// 全选
			selectAll.setText("取消全选");
			delete.setText("删除(" + totalcheckednum + ")");
			delete.setEnabled(true);
		} else if (totalcheckednum == 0) {
			// 全不选
			selectAll.setText("全选");
			delete.setText("删除");
			delete.setEnabled(false);
		} else {
			// 部分选择
			selectAll.setText("全选");
			delete.setText("删除(" + totalcheckednum + ")");
			delete.setEnabled(true);
		}
	}
}
