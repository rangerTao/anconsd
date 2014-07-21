package com.ranger.bmaterials.work;
/*
 * package com.duoku.gamesearch.work;
 * 
 * import java.util.concurrent.atomic.AtomicInteger;
 * 
 * import android.app.Activity; import android.content.IntentFilter; import
 * android.os.Handler; import android.support.v4.app.Fragment; import
 * android.view.View; import android.view.View.OnClickListener; import
 * android.view.ViewGroup; import android.view.animation.Animation; import
 * android.widget.TextView;
 * 
 * import com.ranger.bmaterials.R; import
 * com.duoku.gamesearch.animation.HeaderCoinIconAnimation; import
 * com.duoku.gamesearch.animation.HeaderCoinTvAnimation; import
 * com.duoku.gamesearch.animation.SimpleAnimationListener; import
 * com.duoku.gamesearch.app.MineProfile; import
 * com.duoku.gamesearch.broadcast.HeaderCoinReceiver; import
 * com.duoku.gamesearch.broadcast.HeaderCoinReceiver.IHeaderCoinChanged; import
 * com.duoku.gamesearch.statistics.ClickNumStatistics; import
 * com.duoku.gamesearch.ui.GameDetailsActivity; import
 * com.duoku.gamesearch.ui.HeaderHallBaseFragment; import
 * com.duoku.gamesearch.ui.HomeFragment; import
 * com.duoku.gamesearch.ui.MainHallActivity; import
 * com.duoku.gamesearch.ui.coincenter.CoinCenterCallbacks; import
 * com.duoku.gamesearch.ui.coincenter.CoinUtil; import
 * com.duoku.gamesearch.view.CustomFragmentTabHost;
 * 
 * //获取金币动画 public class HeaderCoinAnimationTask implements IHeaderCoinChanged,
 * CoinCenterCallbacks {
 * 
 * private HeaderCoinReceiver header_coin_tip_receiver; public static final int
 * NONE_TIP = -1; public static final int HAS_TIP = -2; public static final
 * AtomicInteger coinNum = new AtomicInteger(NONE_TIP); private Activity act;
 * 
 * private Fragment fragment; private View root;
 * 
 * public HeaderCoinAnimationTask(Activity act) { this.act = act; }
 * 
 * public HeaderCoinAnimationTask(Fragment fragment, View root) { this.fragment
 * = fragment; this.root = root; }
 * 
 * public void initCoinImp() {
 * findView(R.id.hall_header_coin).setOnClickListener( new OnClickListener() {
 * 
 * @Override public void onClick(View arg0) { // TODO Auto-generated method stub
 * CoinUtil.Instance().startCoinActivity(getActivity(), null);
 * 
 * if (getInstance() instanceof HeaderHallBaseFragment) { ClickNumStatistics
 * .addHomeCoinClickStatistics(getActivity()); } else if (getInstance()
 * instanceof GameDetailsActivity) { ClickNumStatistics
 * .addEnterCoinCenterFromDetailStatistis(getActivity()); } } });
 * 
 * CoinUtil.Instance().registerCallback(this); registerHeaderCoinTip();
 * 
 * if (getInstance() instanceof HomeFragment) { // 首页刚进来显示动画
 * showHeaderCoinTip(SplashTask.SPLASH_DELAY_TIME + 1000); }
 * 
 * }
 * 
 * private View findView(int id) { if (act == null) return
 * root.findViewById(id); else return act.findViewById(id); }
 * 
 * private Activity getActivity() { if (act == null) return
 * fragment.getActivity(); else return act; }
 * 
 * private Object getInstance() { if (act == null) return fragment; else return
 * act; }
 * 
 * // 启动金币中心的回调
 * 
 * @Override public void OnCoinCenterActivityLaunched() { // TODO Auto-generated
 * method stub coinNum.set(NONE_TIP); onCoinViewClick(); }
 * 
 * // 金币更新的广播 public void registerHeaderCoinTip() { if (header_coin_tip_receiver
 * == null) { header_coin_tip_receiver = new HeaderCoinReceiver();
 * header_coin_tip_receiver.impl = this; IntentFilter f = new IntentFilter(
 * MineProfile.MINE_ADD_COIN_NOTIFICATION);
 * getActivity().registerReceiver(header_coin_tip_receiver, f); } }
 * 
 * private void unregisterHeaderCoinTip() { try { if (header_coin_tip_receiver
 * != null) { getActivity().unregisterReceiver(header_coin_tip_receiver);
 * header_coin_tip_receiver = null; } } catch (Exception e) {
 * 
 * } }
 * 
 * public void onDestroy() { // TODO Auto-generated method stub
 * CoinUtil.Instance().unregisterCallback(this); unregisterHeaderCoinTip(); if
 * (getInstance() instanceof HomeFragment) { coinNum.set(NONE_TIP); } }
 * 
 * @Override public void onCoinChangedHandler(int add_coin_count) { // TODO
 * Auto-generated method stub coinNum.set(add_coin_count); handlerCoinRefresh();
 * }
 * 
 * private boolean hasFocus;
 * 
 * public void onWindowFocusChanged(boolean hasFocus) { // TODO Auto-generated
 * method stub this.hasFocus = hasFocus; handlerCoinRefresh(); }
 * 
 * public void onResume() { // TODO Auto-generated method stub
 * handlerCoinRefresh(); }
 * 
 * public void onStop() { View headerCoinBg = findView(R.id.iv_header_coin_bg);
 * if (headerCoinBg != null) { headerCoinBg.clearAnimation();
 * headerCoinBg.setVisibility(View.GONE); } }
 * 
 * private void refreshHallBottomTip() { // 金币刷新时 如果底部我的页面tab未显示消息数 则显示气泡 if
 * (getInstance() instanceof HomeFragment) { CustomFragmentTabHost tabHost =
 * ((MainHallActivity) getActivity()) .getTabHost(); tabHost.showCoinTip(); } }
 * 
 * private void handlerCoinRefresh() { if (hasFocus && coinNum.get() > 0) { //
 * 金币更新广播的回调 showHeaderCoinTip(0); coinNum.set(HAS_TIP); } else if
 * (coinNum.get() == HAS_TIP) { View headerCoinTipIv =
 * findView(R.id.img_header_coin_tip); if (null != headerCoinTipIv)
 * headerCoinTipIv.setVisibility(View.VISIBLE); }
 * 
 * refreshHallBottomTip(); }
 * 
 * private void onCoinViewClick() { // TODO Auto-generated method stub //
 * 金币按钮点击的回调 dissmissCoinTip(); }
 * 
 * public void showHeaderCoinTip(int delay_time) { final View headerCoinBg =
 * findView(R.id.iv_header_coin_bg); if (headerCoinBg == null) return;
 * 
 * final int add_coin_count = coinNum.get(); new Handler().postDelayed(new
 * Runnable() {
 * 
 * @Override public void run() { dissmissCoinTip();
 * 
 * HeaderCoinIconAnimation as = new HeaderCoinIconAnimation(true); as.init();
 * 
 * ((ViewGroup) headerCoinBg.getParent()).setClipChildren(false);
 * headerCoinBg.setVisibility(View.VISIBLE); SimpleAnimationListener endListener
 * = new SimpleAnimationListener() {
 * 
 * @Override public void onAnimationEnd(Animation animation) { // TODO
 * Auto-generated method stub dissmissCoinTip(); if (add_coin_count > 0) {
 * findView(R.id.img_header_coin_tip).setVisibility( View.VISIBLE); } } }; if
 * (add_coin_count == NONE_TIP) as.setAnimationListener(endListener);
 * headerCoinBg.startAnimation(as);
 * 
 * if (add_coin_count > 0) { TextView headerCoinTv = (TextView)
 * findView(R.id.header_coin_tv); headerCoinTv.setText("+" + add_coin_count);
 * headerCoinTv.setVisibility(View.VISIBLE); HeaderCoinTvAnimation ha = new
 * HeaderCoinTvAnimation(true); ha.setAnimationListener(endListener);
 * ha.init(headerCoinTv); } } }, delay_time);
 * 
 * }
 * 
 * private void dissmissCoinTip() { View headerCoinBg =
 * findView(R.id.iv_header_coin_bg); if (headerCoinBg != null) {
 * headerCoinBg.clearAnimation(); headerCoinBg.setVisibility(View.GONE); }
 * 
 * View headerCoinTipIv = findView(R.id.img_header_coin_tip); if (null !=
 * headerCoinTipIv) headerCoinTipIv.setVisibility(View.GONE); TextView
 * headerCoinTv = (TextView) findView(R.id.header_coin_tv); if (null !=
 * headerCoinTv) headerCoinTv.setVisibility(View.GONE); }
 * 
 * }
 */