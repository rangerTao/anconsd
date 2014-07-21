package com.ranger.bmaterials.work;
/*
 * package com.duoku.gamesearch.work;
 * 
 * import java.util.ArrayList; import java.util.Random; import
 * java.util.concurrent.CopyOnWriteArrayList; import
 * java.util.concurrent.atomic.AtomicInteger;
 * 
 * import android.app.Activity; import android.content.Intent; import
 * android.content.SharedPreferences; import android.content.res.Resources;
 * import android.graphics.Bitmap; import android.graphics.BitmapFactory; import
 * android.graphics.drawable.BitmapDrawable; import android.os.AsyncTask; import
 * android.view.View; import android.view.ViewGroup; import
 * android.view.animation.Animation; import
 * android.view.animation.Animation.AnimationListener; import
 * android.widget.RelativeLayout; import android.widget.TextView;
 * 
 * import com.ranger.bmaterials.R; import
 * com.duoku.gamesearch.animation.Rotate3dAnimation; import
 * com.duoku.gamesearch.app.AppManager; import
 * com.duoku.gamesearch.app.InternalGames.InternalInstalledGames; import
 * com.duoku.gamesearch.app.InternalGames.InternalStartGames; import
 * com.duoku.gamesearch.listener.ItemOnTouchAnimationListener; import
 * com.duoku.gamesearch.mode.InstalledAppInfo; import
 * com.duoku.gamesearch.statistics.ClickNumStatistics; import
 * com.duoku.gamesearch.tools.UIUtil; import
 * com.duoku.gamesearch.ui.MyGamesLocalActivity; import
 * com.duoku.gamesearch.view.FrameMaskImageView;
 * 
 * public class HomeMyLocalGameAnimationTask { private Activity cx; private
 * TextView local_game_count_tip; private AtomicInteger installedGamesCount =
 * new AtomicInteger(); private AtomicInteger imageViewIndex = new
 * AtomicInteger(), rotateCount = new AtomicInteger(); private Rotate3dAnimation
 * ra; private CopyOnWriteArrayList<InstalledAppInfo> installedAppList; private
 * ArrayList<InstalledAppInfo> randomIndexs = new ArrayList<InstalledAppInfo>(
 * 4);// 随机过的游戏 private ArrayList<FrameMaskImageView> ivs = new
 * ArrayList<FrameMaskImageView>();
 * 
 * public long startAnimationDelay;
 * 
 * private View root;
 * 
 * public HomeMyLocalGameAnimationTask(Activity cx, View root) { this.cx = cx;
 * this.root = root; }
 * 
 * // 刷新我的游戏视图 public synchronized void refreshMyLocalGames(final boolean init)
 * { new AsyncTask<Void, Void, Integer>() {
 * 
 * @Override protected Integer doInBackground(Void... params) { // TODO
 * Auto-generated method stub return refreshMyLocalGamesViews(); };
 * 
 * protected void onPostExecute(final Integer count) { setLocalGameViews();
 * 
 * if (local_game_count_tip != null) { if (count > 0) { if (count > 99)
 * local_game_count_tip.setText("n"); else local_game_count_tip.setText(count +
 * ""); local_game_count_tip.setVisibility(View.VISIBLE); } else if (count == 0)
 * local_game_count_tip.setVisibility(View.GONE); }
 * 
 * // 首页刚进来才显示动画效果 if (init) { startAnimation(); }
 * 
 * } }.execute(); }
 * 
 * public void init() { // item layout移动到指定位置 ViewGroup
 * local_games_layout_parent = (ViewGroup) root
 * .findViewById(R.id.home_grid_item_local_game_layout_parent);
 * RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams)
 * local_games_layout_parent .getChildAt(0).getLayoutParams(); int[] screenwh =
 * UIUtil.getScreenPx(cx); lp2.width = (screenwh[0] - UIUtil.dip2px(cx, 8) * 2)
 * / 3;// marginleft+marginright
 * local_games_layout_parent.getChildAt(0).setLayoutParams(lp2);
 * 
 * local_games_layout_parent.getChildAt(0).setOnTouchListener( new
 * ItemOnTouchAnimationListener(cx, new OnMyLocalGamesLayoutListener()));
 * 
 * TextView game_name = (TextView) root .findViewById(R.id.home_grid_game_name);
 * game_name.setText(R.string.title_mygames_local);
 * game_name.setTextColor(cx.getResources().getColor(
 * R.color.home_grid_local_game_name_tv_color));
 * 
 * // 提示个数移动到特定位置 local_game_count_tip = (TextView) root
 * .findViewById(R.id.home_grid_item_local_game_tip); ViewGroup
 * local_game_count_tip_parent = (ViewGroup) root
 * .findViewById(R.id.home_grid_item_local_game_tip_parent); int offset =
 * UIUtil.dip2px(cx, 5); int x = -UIUtil.dip2px(cx, 90) + offset;
 * RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
 * local_game_count_tip_parent .getLayoutParams();
 * local_game_count_tip.measure(0, 0); lp.height =
 * local_game_count_tip.getMeasuredHeight() + offset;
 * local_game_count_tip_parent.setLayoutParams(lp);
 * local_game_count_tip_parent.scrollTo(x, -offset);
 * 
 * ra = new Rotate3dAnimation(0, 360, Rotate3dAnimation.CENTER_VERI_MODE);
 * ra.setDuration(700); ra.setStartOffset(500);
 * 
 * for (int i = 0; i < 4; i++) { FrameMaskImageView iv = null; switch (i) { case
 * 0: iv = (FrameMaskImageView) root .findViewById(R.id.home_grid_local_bg1);
 * break; case 1: iv = (FrameMaskImageView) root
 * .findViewById(R.id.home_grid_local_bg2); break; case 2: iv =
 * (FrameMaskImageView) root .findViewById(R.id.home_grid_local_bg3); break;
 * case 3: iv = (FrameMaskImageView) root
 * .findViewById(R.id.home_grid_local_bg4); break; } if (iv != null)
 * ivs.add(iv); } }
 * 
 * private class OnMyLocalGamesLayoutListener implements AnimationListener {
 * 
 * @Override public void onAnimationStart(Animation animation) { // TODO
 * Auto-generated method stub
 * 
 * }
 * 
 * @Override public void onAnimationRepeat(Animation animation) { // TODO
 * Auto-generated method stub
 * 
 * }
 * 
 * @Override public void onAnimationEnd(Animation animation) { // TODO
 * Auto-generated method stub Intent in_mg = new Intent(cx,
 * MyGamesLocalActivity.class); cx.startActivity(in_mg);
 * ClickNumStatistics.addHomeTabMyGamesStatistics(cx); } };
 * 
 * private synchronized void setLocalGameViews() { Resources r =
 * cx.getResources(); Bitmap frame = BitmapFactory.decodeResource(r,
 * R.drawable.home_grid_local_item_frame); Bitmap mask =
 * BitmapFactory.decodeResource(r, R.drawable.home_grid_local_item_mask);
 * 
 * int count = installedGamesCount.get(); for (int i = 0; i < 4; i++) {
 * 
 * if (count == 0) { FrameMaskImageView iv = ivs.get(i); localGameSetNone(iv,
 * View.VISIBLE); } else if (i < count) { InstalledAppInfo info =
 * getRandomInstalledGames(i, i); setIconBitMap(i, info, frame, mask); } else {
 * FrameMaskImageView iv = ivs.get(i); localGameSetNone(iv, View.INVISIBLE); } }
 * 
 * }
 * 
 * private synchronized int refreshMyLocalGamesViews() { AppManager am =
 * AppManager.getInstance(cx);
 * 
 * installedAppList = new CopyOnWriteArrayList<InstalledAppInfo>(
 * am.getInstalledGames());
 * 
 * installedGamesCount.set(installedAppList.size()); int newGamesCount = 0; if
 * (installedAppList.size() > 0) { SharedPreferences started_app_sp =
 * InternalStartGames .getSharedPreferences(cx);
 * 
 * ArrayList<String> internalInstalledAppList = InternalInstalledGames
 * .getInternalInstalledGames(cx);// 应用内安装的游戏 for (String pkgName :
 * internalInstalledAppList) { if (started_app_sp.getLong(pkgName, 0) == 0) { //
 * 未启动过的游戏 newGamesCount++; } } }
 * 
 * return newGamesCount; }
 * 
 * private void setIconBitMap(int pos, InstalledAppInfo info, Bitmap frame,
 * Bitmap mask) {
 * 
 * try { FrameMaskImageView iv = ivs.get(pos); if (iv == null || info == null)
 * return; iv.setBitmap(((BitmapDrawable) info.getDrawable()).getBitmap());
 * iv.setFrame(frame); iv.setMask(mask); iv.initView(); iv.invalidate();
 * iv.setVisibility(View.VISIBLE); } catch (Exception e) { e.printStackTrace();
 * }
 * 
 * }
 * 
 * private void localGameSetNone(FrameMaskImageView iv, int visible) { // TODO
 * Auto-generated method stub if (iv == null) return; iv.setBitmap(null);
 * iv.setVisibility(visible); }
 * 
 * private synchronized void startAnimation() { if (installedGamesCount.get() >
 * 0) { // 可能当前缓存的数据 在显示动画 reset(); cx.runOnUiThread(new Runnable() {
 * 
 * @Override public void run() { // TODO Auto-generated method stub if
 * (installedGamesCount.get() > 1) ra.setAnimationListener(new
 * ImageViewAnimationListener()); FrameMaskImageView iv =
 * ivs.get(imageViewIndex.get()); if (iv != null) { iv.startAnimation(ra); } }
 * });
 * 
 * } }
 * 
 * private class ImageViewAnimationListener implements AnimationListener {
 * private int rotateMaxCount;// 最大翻转次数 减去第一次
 * 
 * public ImageViewAnimationListener() { if (installedGamesCount.get() <= 4) {
 * rotateMaxCount = installedGamesCount.get() - 1; } else { rotateMaxCount = 3;
 * } }
 * 
 * @Override public void onAnimationEnd(Animation animation) { // TODO
 * Auto-generated method stub // 当前光盘 int current_view_index =
 * getAnimationIndex(imageViewIndex.get()); FrameMaskImageView preIv =
 * ivs.get(current_view_index); if (preIv != null) preIv.clearAnimation();
 * 
 * Resources r = cx.getResources(); Bitmap frame =
 * BitmapFactory.decodeResource(r, R.drawable.home_grid_local_item_frame);
 * Bitmap mask = BitmapFactory.decodeResource(r,
 * R.drawable.home_grid_local_item_mask);
 * 
 * rotateCount.incrementAndGet(); imageViewIndex.incrementAndGet(); if
 * (rotateCount.get() > rotateMaxCount) { // 结束 setIconBitMap(
 * current_view_index, getRandomInstalledGames(rotateCount.get(),
 * current_view_index), frame, mask); reset(); } else { setIconBitMap(
 * current_view_index, getRandomInstalledGames(rotateCount.get(),
 * current_view_index), frame, mask); // 下一个光盘翻转 FrameMaskImageView iv = ivs
 * .get(getAnimationIndex(imageViewIndex.get())); if (iv != null)
 * iv.startAnimation(ra); } }
 * 
 * @Override public void onAnimationRepeat(Animation animation) { // TODO
 * Auto-generated method stub
 * 
 * }
 * 
 * @Override public void onAnimationStart(Animation animation) { // TODO
 * Auto-generated method stub
 * 
 * }
 * 
 * }
 * 
 * private void reset() { // 可能当前缓存的数据 在显示动画 for (int i = 0; i < 4; i++) {
 * FrameMaskImageView iv = ivs.get(i); if (iv != null) iv.clearAnimation(); }
 * 
 * if (ra != null) ra.setAnimationListener(null); imageViewIndex.set(0);
 * rotateCount.set(0); randomIndexs.clear(); }
 * 
 * // 交叉翻转的顺序 private int getAnimationIndex(int imageViewIndex) { int[] i =
 * null; if (installedGamesCount.get() <= 2) i = new int[] { 0, 1 }; else if
 * (installedGamesCount.get() == 3) i = new int[] { 0, 1, 2 }; else i = new
 * int[] { 0, 3, 1, 2 }; if (imageViewIndex >= i.length) return imageViewIndex;
 * return i[imageViewIndex]; }
 * 
 * // 随机的游戏 private synchronized InstalledAppInfo getRandomInstalledGames( int
 * rotateCount, int current_view_index) {
 * 
 * if (installedAppList.isEmpty()) return null;
 * 
 * if (installedGamesCount.get() <= 4) { if (rotateCount >=
 * installedGamesCount.get()) rotateCount = 0;
 * 
 * return installedAppList.get(rotateCount); }
 * 
 * // 过滤 for (InstalledAppInfo info : randomIndexs) {
 * installedAppList.remove(info); }
 * 
 * InstalledAppInfo result = null; Random random = new Random(); int size =
 * installedAppList.size(); if (size > 0) {
 * 
 * int index = random.nextInt(size); result = installedAppList.get(index);
 * 
 * // 覆盖当前位置的游戏 if (randomIndexs.size() == 4)
 * randomIndexs.remove(current_view_index); if (current_view_index >=
 * randomIndexs.size()) randomIndexs.add(result); else
 * randomIndexs.add(current_view_index, result);
 * 
 * } return result; } }
 */