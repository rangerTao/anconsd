package com.ranger.bmaterials.ui;
/*
 * package com.duoku.gamesearch.ui;
 * 
 * import java.util.concurrent.locks.ReentrantLock;
 * 
 * import android.content.Intent; import android.content.SharedPreferences;
 * import android.os.Bundle; import android.preference.PreferenceManager; import
 * android.view.Gravity; import android.view.View; import
 * android.view.View.OnClickListener; import android.widget.GridView; import
 * android.widget.TextView;
 * 
 * import com.ranger.bmaterials.R; import
 * com.duoku.gamesearch.adapter.MyGamesLocalAdapter; import
 * com.duoku.gamesearch.app.Constants; import
 * com.duoku.gamesearch.app.StartGame; import
 * com.duoku.gamesearch.broadcast.NotificaionReceiver; import
 * com.duoku.gamesearch.tools.NetUtil.IRequestListener; import
 * com.duoku.gamesearch.view.MyPopupWindows; import
 * com.duoku.gamesearch.work.LoadingTask; import
 * com.duoku.gamesearch.work.LoadingTask.ILoading;
 * 
 * public class MyGamesLocalActivity extends HeaderCoinBackBaseActivity {
 * private GridView gv_mygames_local; private MyGamesLocalAdapter adapter;
 * 
 * private static final String MY_GAMES_LOCAL_GUIDE_TIP_SP =
 * "my_games_local_guide_tip";
 * 
 * // 可能从通知栏跳进来
 * 
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * 
 * findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) {
 * MainHallActivity.jumpToTab(MyGamesLocalActivity.this, 2); } });
 * 
 * gv_mygames_local = (GridView) findViewById(R.id.gv_mygames_local_activity);
 * adapter = new MyGamesLocalAdapter(this); getArgs(); changeData(); }
 * 
 * @Override protected void onNewIntent(Intent intent) {
 * super.onNewIntent(intent); fromNotifier = true; }
 * 
 * boolean fromNotifier = false;
 * 
 * private void getArgs() { Intent intent = getIntent(); long notifierId =
 * intent.getLongExtra( NotificaionReceiver.ARG_NOTIFICATION_ID, -1); if
 * (notifierId > -1) { fromNotifier = true; } }
 * 
 * @Override protected void onActivityResult(int requestCode, int resultCode,
 * Intent data) { // TODO Auto-generated method stub if (requestCode ==
 * StartGame.START_GAME_REQUEST_CODE) { changeData(); }
 * super.onActivityResult(requestCode, resultCode, data); }
 * 
 * private ReentrantLock rl = new ReentrantLock();
 * 
 * private void changeData() { LoadingTask task = new LoadingTask(this, new
 * ILoading() {
 * 
 * @Override public void preLoading(View network_loading_layout, View
 * network_loading_pb, View network_error_loading_tv) { // TODO Auto-generated
 * method stub
 * 
 * }
 * 
 * @Override public void loading(final IRequestListener listener) { // TODO
 * Auto-generated method stub rl.lock(); final boolean isEmpty =
 * adapter.lastStartGameChange();
 * 
 * runOnUiThread(new Runnable() {
 * 
 * @Override public void run() { // TODO Auto-generated method stub
 * listener.onRequestSuccess(null);
 * 
 * if (gv_mygames_local.getAdapter() == null) { SharedPreferences sp =
 * PreferenceManager .getDefaultSharedPreferences(MyGamesLocalActivity.this);
 * 
 * boolean showGuidePop = (Constants.isFirstStartWhenVersionChanged ||
 * Constants.isFirstInstalled) && sp.getBoolean( MY_GAMES_LOCAL_GUIDE_TIP_SP,
 * true);
 * 
 * sp.edit() .putBoolean(MY_GAMES_LOCAL_GUIDE_TIP_SP, false).commit();
 * gv_mygames_local.setAdapter(adapter); if (isEmpty && showGuidePop) {
 * gv_mygames_local.post(new Runnable() {
 * 
 * @Override public void run() { // TODO Auto-generated method stub
 * showGuidePop(gv_mygames_local.getChildAt(gv_mygames_local .getCount() - 1 -
 * gv_mygames_local .getFirstVisiblePosition())); } }); } } else {
 * adapter.notifyDataSetChanged(); } } }); rl.unlock(); }
 * 
 * @Override public boolean isShowNoNetWorkView() { // TODO Auto-generated
 * method stub return false; }
 * 
 * @Override public boolean isAsync() { // TODO Auto-generated method stub
 * return true; }
 * 
 * @Override public IRequestListener getRequestListener() { // TODO
 * Auto-generated method stub return null; } });
 * 
 * task.loading(); }
 * 
 * @Override protected void onDestroy() { // TODO Auto-generated method stub if
 * (pop != null) { pop.dismiss(); pop = null; } super.onDestroy(); }
 * 
 * private MyPopupWindows pop;
 * 
 * public void showGuidePop(final View v) { if (pop != null && pop.isShowing())
 * return;
 * 
 * if (pop == null) { View myLocalGamePop = View.inflate(this,
 * R.layout.home_guide_pop_layout, null); TextView tv = (TextView)
 * myLocalGamePop .findViewById(R.id.home_guide_pop_tv);
 * tv.setText(getString(R.string.my_local_games_guide_tip));
 * tv.setGravity(Gravity.LEFT);
 * tv.setBackgroundResource(R.drawable.my_local_game_guide_bg); pop = new
 * MyPopupWindows(this, myLocalGamePop); }
 * 
 * pop.showAtBottom(v, R.style.home_guide_popup_animation);
 * 
 * // // 9秒后自动消失 // Handler handler = new Handler(); // handler.postDelayed(new
 * Runnable() { // // @Override // public void run() { // // TODO Auto-generated
 * method stub // if (!isFinishing()) { // pop.dismiss(); // } // // } // },
 * 9000); }
 * 
 * @Override public int getLayout() { // TODO Auto-generated method stub return
 * R.layout.mygames_local_activity; }
 * 
 * @Override public String getHeaderTitle() { // TODO Auto-generated method stub
 * return getString(R.string.title_mygames_local); }
 * 
 * }
 */