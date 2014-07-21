package com.ranger.bmaterials.diff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.duoku.patch.app.DKDeltaUpdateManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.DiffInfo;

public final class JarDeltaDiff {

	private Context cx;
	public DiffInfo info;

	private String diff_folder;
	private String sourcePath;

	public JarDeltaDiff() {
		diff_folder = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Constants.DOWNLOAD_FOLDER + "/";
	}

	public JarDeltaDiff(Context cx, DiffInfo info) {
		this.cx = cx;
		this.info = info;
		diff_folder = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Constants.DOWNLOAD_FOLDER + "/";
	}

	// 拷贝apk 返回是否会复制
	public boolean copyApk2Sd() {
		try {
			PackageManager pm = cx.getPackageManager();

			String apkPath = pm.getApplicationInfo(
					info.packageMode.packageName, 0).publicSourceDir;
			String apkName = parseApkName(apkPath);
			sourcePath = diff_folder + apkName;

			return copyFile(new File(apkPath), new File(sourcePath));
		} catch (Exception e) {
			e.printStackTrace();
			sourcePath = null;
			return false;
		}
	}

	public void getSourcePath() {
		try {
			PackageManager pm = cx.getPackageManager();

			String apkPath = pm.getApplicationInfo(
					info.packageMode.packageName, 0).publicSourceDir;
			sourcePath = apkPath;
		} catch (Exception e) {
			e.printStackTrace();
			sourcePath = null;
		}
	}

	// 返回是否会复制
	private boolean copyFile(File sourceFile, File targetFile)
			throws IOException {
		if (targetFile.exists()) {
			if (sourceFile.length() == targetFile.length())
				return false;
			else
				targetFile.delete();
		}

		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
		return true;
	}

	/**
	 * 生成补丁 要先判断sd卡是否存在 要判断返回值是否为空
	 * 
	 * @param sourcePath
	 *            源apk路径
	 * @param targetPath
	 *            目标apk路径
	 * @return 输出路径
	 */
	protected String computeDiff(String sourcePath, String targetPath) {

		String outPath = null;
		try {
			outPath = diff_folder
					+ targetPath.substring(targetPath.lastIndexOf("/") + 1,
							targetPath.lastIndexOf(".")) + "_patch.apk";
			DKDeltaUpdateManager.calculateAppDelta(sourcePath, targetPath,
					outPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outPath;
	}

	/**
	 * 合并补丁 要先判断sd卡是否存在
	 * 
	 * @param sourcePath
	 *            下载目录中源包路径 若不存在则传null
	 * @param patch
	 *            补丁路径
	 * @param outPath
	 *            输出路径
	 */
	protected void applyDiff() {
		try {
			getSourcePath();
			
			if (sourcePath == null || sourcePath.equals(""))
				return;

			String patch = info.patchPath;
			// 临时新包输出路径
			String temp_outPath = diff_folder + System.currentTimeMillis()
					+ "_diff.apk";

			// 合并补丁
			boolean result = DKDeltaUpdateManager.createPatchNewApp(sourcePath,
					temp_outPath, patch);

			File temp_outFile = new File(temp_outPath);
			if (!result || temp_outFile.length() == 0) {
				info.success = false;
				return;
			}
			// 重命名补丁文件
			File patchFile = new File(patch);
			String temp_patchPath = diff_folder + System.currentTimeMillis()
					+ "_patch.apk";
			File temp_patchFile = new File(temp_patchPath);
			patchFile.renameTo(temp_patchFile);

			// 重命名新包为补丁文件名
			temp_outFile.renameTo(patchFile);

			// 删除补丁包和copy的源包
			temp_patchFile.delete();
//			new File(sourcePath).delete();
		} catch (Exception e) {
			e.printStackTrace();
			info.success = false;
			return;
		}
		info.success = true;
	}

	private String parseApkName(String apkPath) {
		return apkPath
				.substring(apkPath.lastIndexOf("/") + 1, apkPath.length());
	}

}
