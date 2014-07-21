package com.ranger.bmaterials.diff;

import android.content.Context;

import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.mode.DiffInfo;
import com.ranger.bmaterials.mode.PackageMode;

public final class DiffManager {

	private static DiffManager manager = null;

//	private ConcurrentHashMap<String, JarDeltaDiff> diff_map = new ConcurrentHashMap<String, JarDeltaDiff>();
//	private ConcurrentHashMap<String, Object> observer_map = new ConcurrentHashMap<String, Object>();

	public synchronized static DiffManager getInstance() {
		if (manager == null) {
			manager = new DiffManager();
		}
		return manager;
	}

	/**
	 * 异步方法 复制旧包
	 * 
	 * @param cx
	 * @param info
	 */
//	public void preDiff(final Context cx, final DiffInfo info) {
//		// if(diff_map.get(info.packageMode.packageName)!=null)
//		// //如果manager未被回收 则会出现直接返回
//		// return;
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				info.patchPath = info.packageMode.downloadDest;
//				JarDeltaDiff delta = new JarDeltaDiff(cx, info);
//				delta.copyApk2Sd();
//				diff_map.put(info.packageMode.packageName, delta);
//
//				Object observer = observer_map
//						.get(info.packageMode.packageName);
//				if (observer != null) {
//					// postDiff在复制完成前被调用了 复制完成后 唤醒
//					synchronized (observer) {
//						observer.notifyAll();
//					}
//				}
//			}
//		}).start();
//	}

	/**
	 * 非异步方法 合成补丁
	 * 
	 * @param cx
	 * @param info
	 */

	public void postDiff(Context cx, DiffInfo info) {
		info.patchPath = info.packageMode.downloadDest;
		JarDeltaDiff delta = new JarDeltaDiff(cx, info);

		delta.applyDiff();
		notifyResult(delta.info);
	}

//	public void postDiff(String pkgName) {
//		try {
//			// if (observer_map.get(pkgName) != null)
//			// //已有相同包名的等待线程 如果manager未被回收 则会出现直接返回
//			// return;
//			JarDeltaDiff delta = diff_map.get(pkgName);
//			if (delta == null) {
//				// 还未复制完成 先等待
//				Object obj = new Object();
//				observer_map.put(pkgName, obj);
//				synchronized (obj) {
//					obj.wait();
//				}
//				observer_map.remove(obj);
//				delta = diff_map.get(pkgName);
//			}
//
//			delta.applyDiff();
//			diff_map.remove(pkgName);
//			notifyResult(delta.info);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

//	public void reDiff(Context cx, DiffInfo info) {
//		try {
//			// if (observer_map.get(pkgName) != null)
//			// //已有相同包名的等待线程 如果manager未被回收 则会出现直接返回
//			// return;
//			JarDeltaDiff delta = new JarDeltaDiff(cx, info);
//
//			delta.copyApk2Sd();
//			delta.applyDiff();
//			notifyResult(delta.info);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 非异步方法 生成差分补丁
	 * 
	 * @param info
	 */
	public void computeDiff(String sourcePath, String targetPath) {
		JarDeltaDiff delta = new JarDeltaDiff();
		delta.computeDiff(sourcePath, targetPath);
	}

	private void notifyResult(DiffInfo info) {
		PackageMode packageMode = info.packageMode;
		boolean successful = info.success;
		int errorReason = info.failedReason;
		if (packageMode != null) {
			PackageHelper.notifyMergeResult(packageMode, successful,
					errorReason);
			// PackageHelper.notifyMergeResult(packageMode, false,
			// errorReason);
		}

	}

	public interface DiffListener {
		void onFinish(DiffInfo deltaInfo);
	}

}
