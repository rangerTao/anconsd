package com.ranger.bmaterials.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.R.string;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MergeMode;
import com.ranger.bmaterials.mode.MyDownloadedGame;
import com.ranger.bmaterials.mode.MyInstalledAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.mode.UpdatableItem;
import com.ranger.bmaterials.tools.ApkUtil;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.FileHelper;
import com.ranger.bmaterials.tools.install.PackageUtils;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.work.DBTaskManager;
import com.ranger.bmaterials.work.FutureTaskManager;

@SuppressLint("NewApi")
public class AppDaoImpl implements AppDao {

	private Context context;

	private static final String DATABASE_NAME = "app.db";

	private static final int DATABASE_OLD_VERSION = 2;
	private static final int DATABASE_NEW_VERSION = DATABASE_OLD_VERSION + 2;

	/** 白名单 */
	private static final String TABLE_WHITE_LIST = "white_list";
	private static final String INDEX_TABLE_WHITE_LIST = "white_list_index"; // white
																				// list
																				// of
																				// games
	/** 已安装 */
	private static final String TABLE_INSALLED_APP_LIST = "installed_list"; // all
																			// apps
																			// we
																			// installed
	private static final String INDEX_TABLE_INSALLED_APP_LIST = "installed_list_index";
	/** 更新表 */
	private static final String TABLE_UPDATABLE_APP_LIST = "updatable_list"; // information
																				// from
																				// server
																				// about
																				// all
																				// games
																				// that
																				// we
																				// installed
	private static final String INDEX_TABLE_UPDATABLE_APP_LIST = "updatable_list_index";
	/** 下载表 */
	private static final String TABLE_DOWNLOAD_APP_LIST = "download_list";
	/** 合并表（注意：必须以下载表为基础） */
	private static final String TABLE_MERGE_LIST = "merge_list";

	private static final String INDEX_TABLE_DOWNLOAD_APP_LIST = "download_list_index";
	/** 这个表暂时没有用到（之前老版本以为会用到） */
	private static final String TABLE_MY_INSTALLED_LIST = "my_installed_list";
	private static final String INDEX_TABLE_MY_INSTALLED_LIST = "my_installed_list_index";
	/** 首页我的游戏 */
	private static final String TABLE_MY_DOWNLOADED_LIST = "my_downloaded_list";

	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_PKG_NAME = "pkg_name";
	private static final String COLUMN_NAME = "name";

	/* date of download or install */
	private static final String COLUMN_INSTALLED_DATE = "date";
	/* date of publish of new version app */
	private static final String COLUMN_PUBLISH_DATE = "update_date";
	private static final String COLUMN_DOWNLOAD_DATE = "download_date";
	/* signiture of app */
	private static final String COLUMN_SIGN = "sign";
	/* app's signiture from server */
	private static final String COLUMN_SERVER_SIGN = "server_sign";
	private static final String COLUMN_VERSION = "version";
	private static final String COLUMN_VERSION_INT = "version_int";
	private static final String COLUMN_SIZE = "size";
	private static final String COLUMN_NEW_SIZE = "new_size";
	private static final String COLUMN_IGNORE_STATE = "ignore_update";

	private static final String COLUMN_UPDATE_STATE = "update_state";

	private static final int COLUMN_UPDATE_STATE_NONE = 0;
	private static final int COLUMN_UPDATE_STATE_UPDADING = 1;
	private static final int COLUMN_UPDATE_STATE_UPDATED = 2;

	private static final String COLUMN_NEW_VERSION = "new_version";
	private static final String COLUMN_NEW_VERSION_INT = "new_version_int";
	private static final String COLUMN_EXTRA = "extra";
	private static final String COLUMN_NEED_LOGIN = "need_login";

	private static final String COLUMN_DOWNLOAD_URL = "download_url";
	private static final String COLUMN_DOWNLOAD_ID = "download_id";
	private static final String COLUMN_ICON_URL = "icon_url";
	private static final String COLUMN_NAME_PINYIN = "name_pinyin";

	private static final String COLUMN_IS_GAME = "is_game";

	static class WhiteListTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID; // 1
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME;// 2
		static final String COLUMN_NAME = AppDaoImpl.COLUMN_NAME;// 3
		// static final String COLUMN_VERSION = AppDaoImpl.COLUMN_VERSION ;//4
		// static final String COLUMN_VERSION_INT =
		// AppDaoImpl.COLUMN_VERSION_INT ;//5
		// static final String COLUMN_PUBLISH_DATE =
		// AppDaoImpl.COLUMN_PUBLISH_DATE ;//6
		// static final String COLUMN_SIGN = AppDaoImpl.COLUMN_SIGN ;//7
		// static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA ;//8
	}

	static class DownloadTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID; // 1
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME;// 2
		static final String COLUMN_NAME = AppDaoImpl.COLUMN_NAME;// 3
		static final String COLUMN_NAME_PINYIN = AppDaoImpl.COLUMN_NAME_PINYIN;// 4
		static final String COLUMN_VERSION = AppDaoImpl.COLUMN_VERSION;// 5
		static final String COLUMN_VERSION_INT = AppDaoImpl.COLUMN_VERSION_INT;// 6
		static final String COLUMN_PUBLISH_DATE = AppDaoImpl.COLUMN_PUBLISH_DATE;// 7
		static final String COLUMN_SIZE = AppDaoImpl.COLUMN_SIZE;// 8
		static final String COLUMN_ICON_URL = AppDaoImpl.COLUMN_ICON_URL;// 9
		static final String COLUMN_SIGN = AppDaoImpl.COLUMN_SIGN;// 10
		static final String COLUMN_FILEMD5 = InstalledTable.COLUMN_FILEMD5;// 10
		static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA;// 11

		static final String COLUMN_DOWNLOAD_DATE = AppDaoImpl.COLUMN_DOWNLOAD_DATE;// 12
		static final String COLUMN_GAME_ID = "game_id";// 13
		static final String COLUMN_DOWNLOAD_ID = AppDaoImpl.COLUMN_DOWNLOAD_ID;// 14
		static final String COLUMN_DOWNLOAD_URL = AppDaoImpl.COLUMN_DOWNLOAD_URL;// 15

		static final String COLUMN_IS_DELETED = "is_deleted";// 16
		static final String COLUMN_INSTALL_STATUS = "install_status";// 17
																		// (0:未安装，1：正在安装，2：已经安装，其他以后再增加）
		static final String COLUMN_INSTALL_ERROR_REASON = "error_reason";// 17
																			// (0:未安装，1：正在安装，2：已经安装，其他以后再增加）

		static final int INSTALL_STATUS_UNINSTALLED = InstallStatus.UNINSTALLED
				.getIndex();
		static final int INSTALL_STATUS_INSTALLED = InstallStatus.INSTALLED
				.getIndex();
		static final int INSTALL_STATUS_INSTALLING = InstallStatus.INSTALLING
				.getIndex();
		static final int INSTALL_STATUS_INSTALL_ERROR = InstallStatus.INSTALL_ERROR
				.getIndex();

		public static final String COLUMN_NEED_LOGIN = AppDaoImpl.COLUMN_NEED_LOGIN;

		public static final String COLUMN_NOTIFIED = "notified";
		public static final String COLUMN_IS_DIFFUPDATE = "is_diff_update";
		public static final String COLUMN_MERGE_FAILED_COUNT = "merge_failed_count";

		private static DownloadAppInfo extractDownloadAppInfo(
				Cursor resultCursor) {

			int indexPkg = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_NAME);
			int indexPinyin = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_NAME_PINYIN);
			int indexDate = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_PUBLISH_DATE);
			int indexVersion = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_VERSION);
			int indexVersionInt = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_VERSION_INT);
			int indexSign = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_SIGN);
			int indexSize = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_SIZE);
			int indexIconUrl = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_ICON_URL);
			int indexExtra = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_EXTRA);

			int indexDownloadDate = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_DATE);
			int indexDownloadId = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_ID);
			int indexDownloadUrl = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_URL);
			int indexGameId = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_GAME_ID);
			int indexIsDeleted = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_IS_DELETED);
			int indexInstallStatus = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_INSTALL_STATUS);
			int indexInstallError = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_INSTALL_ERROR_REASON);
			int indexNeedLogin = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_NEED_LOGIN);

			int indexIsDiffUpdate = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_IS_DIFFUPDATE);
			int indexFileMd5 = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_FILEMD5);

			String packageName = resultCursor.getString(indexPkg);
			String name = resultCursor.getString(indexName);
			String pinyin = resultCursor.getString(indexPinyin);
			long date = resultCursor.getLong(indexDate);
			String version = resultCursor.getString(indexVersion);
			int versionInt = resultCursor.getInt(indexVersionInt);
			String extra = resultCursor.getString(indexExtra);
			String sign = resultCursor.getString(indexSign);
			String iconUrl = resultCursor.getString(indexIconUrl);
			long size = resultCursor.getLong(indexSize);

			long downloadDate = resultCursor.getLong(indexDownloadDate);
			long downloadId = resultCursor.getLong(indexDownloadId);
			String downloadUrl = resultCursor.getString(indexDownloadUrl);
			String gameId = resultCursor.getString(indexGameId);

			int isDeleted = resultCursor.getInt(indexIsDeleted);
			int installStatusInt = resultCursor.getInt(indexInstallStatus);
			int installError = resultCursor.getInt(indexInstallError);
			boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);

			boolean isDiffUpdate = false;
			if (indexIsDiffUpdate != -1) {
				isDiffUpdate = (resultCursor.getInt(indexIsDiffUpdate) == 1);
			}
			String fileMd5 = null;
			if (indexFileMd5 != -1) {
				fileMd5 = resultCursor.getString(indexFileMd5);
			}

			DownloadAppInfo info = new DownloadAppInfo(packageName, name,
					version, versionInt, date, extra, needLogin, pinyin, sign,
					size, downloadId, downloadUrl, iconUrl, downloadDate,
					gameId, isDiffUpdate, fileMd5);
			info.setMarkDeleted((isDeleted == 1));
			info.setInstalleStatus(InstallStatus.parse(installStatusInt));
			info.setInstallErrorReason(installError);
			return info;
		}

		public static DownloadAppInfo getDownloadGame(Context context,
				String fileMd5, boolean includeDeleted) {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			Cursor resultCursor = null;
			SQLiteDatabase db = helper.getReadableDatabase();
			try {
				String[] columns = new String[] {
						DownloadTable.COLUMN_PKG_NAME,
						DownloadTable.COLUMN_GAME_ID,
						DownloadTable.COLUMN_NAME,
						DownloadTable.COLUMN_NAME_PINYIN,
						DownloadTable.COLUMN_PUBLISH_DATE,
						DownloadTable.COLUMN_VERSION,
						DownloadTable.COLUMN_VERSION_INT,
						DownloadTable.COLUMN_SIGN, DownloadTable.COLUMN_SIZE,
						DownloadTable.COLUMN_DOWNLOAD_URL,
						DownloadTable.COLUMN_ICON_URL,
						DownloadTable.COLUMN_EXTRA,
						DownloadTable.COLUMN_DOWNLOAD_DATE,
						DownloadTable.COLUMN_DOWNLOAD_ID,
						DownloadTable.COLUMN_IS_DELETED,
						DownloadTable.COLUMN_INSTALL_STATUS,
						DownloadTable.COLUMN_INSTALL_ERROR_REASON,
						DownloadTable.COLUMN_NEED_LOGIN,
						DownloadTable.COLUMN_IS_DIFFUPDATE,
						DownloadTable.COLUMN_FILEMD5 };

				String selection = null;
				String selectionArgs[] = null;
				if (includeDeleted) {
					// 包含删除的和未删除的
					selection = DownloadTable.COLUMN_FILEMD5 + "=? ";
					selectionArgs = new String[] { fileMd5 };
				} else {
					// 不包含删除的
					selection = DownloadTable.COLUMN_FILEMD5 + "=?  AND "
							+ DownloadTable.COLUMN_IS_DELETED + " = 0 ";
					;
					selectionArgs = new String[] { fileMd5 };
				}
				resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
						selection, selectionArgs, null, null,
						DownloadTable.COLUMN_DOWNLOAD_DATE + " DESC");
				if (resultCursor.getCount() > 1) {
					// throw new RuntimeException("Error");
				} else if (resultCursor.getCount() == 0) {
					return null;
				}
				resultCursor.moveToFirst();
				return extractDownloadAppInfo(resultCursor);
			} catch (Exception e) {
				// throw new RuntimeException(e);
				e.printStackTrace();
			} finally {
				if (resultCursor != null && !resultCursor.isClosed()) {
					resultCursor.close();
				}
			}
			return null;
		}
	}

	static class UpdateTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID;
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME; // 1
		static final String COLUMN_NEW_VERSION = AppDaoImpl.COLUMN_NEW_VERSION;// 2
		static final String COLUMN_NEW_VERSION_INT = AppDaoImpl.COLUMN_NEW_VERSION_INT;// 3
		static final String COLUMN_DOWNLOAD_URL = AppDaoImpl.COLUMN_DOWNLOAD_URL;// 4
		static final String COLUMN_PUBLISH_DATE = AppDaoImpl.COLUMN_PUBLISH_DATE;// 5
		static final String COLUMN_SERVER_SIGN = AppDaoImpl.COLUMN_SERVER_SIGN;// 6
		static final String COLUMN_NEW_SIZE = AppDaoImpl.COLUMN_NEW_SIZE;// 7
		static final String COLUMN_IGNORE_STATE = AppDaoImpl.COLUMN_IGNORE_STATE;// 8
		static final String COLUMN_UPDATE_STATE = AppDaoImpl.COLUMN_UPDATE_STATE;// 9
		static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA;// 10
		static final String COLUMN_GAME_ID = "game_id";// 11

		static final String COLUMN_ICON_URL = AppDaoImpl.COLUMN_ICON_URL;// 12
		static final String COLUMN_NEED_LOGIN = AppDaoImpl.COLUMN_NEED_LOGIN;

		static final String COLUMN_IS_DIFFUPDATE = DownloadTable.COLUMN_IS_DIFFUPDATE;
		static final String COLUMN_PATCH_URL = "patch_url";
		static final String COLUMN_PATCH_SIZE = "patch_size";
	}

	static class InstalledTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID;
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME;
		static final String COLUMN_NAME = AppDaoImpl.COLUMN_NAME;
		static final String COLUMN_NAME_PINYIN = AppDaoImpl.COLUMN_NAME_PINYIN;
		static final String COLUMN_INSTALLED_DATE = AppDaoImpl.COLUMN_INSTALLED_DATE;
		static final String COLUMN_VERSION = AppDaoImpl.COLUMN_VERSION;
		static final String COLUMN_VERSION_INT = AppDaoImpl.COLUMN_VERSION_INT;
		static final String COLUMN_SIGN = AppDaoImpl.COLUMN_SIGN;
		static final String COLUMN_SIZE = AppDaoImpl.COLUMN_SIZE;
		static final String COLUMN_IS_GAME = AppDaoImpl.COLUMN_IS_GAME;
		static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA;
		static final String COLUMN_NEED_LOGIN = AppDaoImpl.COLUMN_NEED_LOGIN;

		static final String COLUMN_IS_OWN = "is_own";// 是否是从duoku安装
		static final String COLUMN_GAME_ID = "game_id";//

		static final String COLUMN_FILEMD5 = "file_md5";//

		static final String COLUMN_UID = "uid";//
	}

	static class MyDownloadedTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID;
		static final String COLUMN_GAME_ID = "game_id";//
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME;
		static final String COLUMN_NAME = AppDaoImpl.COLUMN_NAME;
		static final String COLUMN_ICON_URL = AppDaoImpl.COLUMN_ICON_URL;
		static final String COLUMN_GAME_KEY = "game_key";//
		static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA;
		static final String COLUMN_NEED_LOGIN = AppDaoImpl.COLUMN_NEED_LOGIN;

	}

	static class MyInstalledTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID;
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME;
		static final String COLUMN_INSTALL_TIME = "install_time";// 安装时间
		static final String COLUMN_LASTEST_OPEN_TIME = "latest_time";// 最近一次打开时间
		static final String COLUMN_OPEN_TIMES = "open_times"; // 打开次数
		static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA;

	}

	static class MergeTable {
		static final String COLUMN_ID = AppDaoImpl.COLUMN_ID;
		static final String COLUMN_GAME_ID = "game_id";//
		static final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME;
		static final String COLUMN_VERSION = AppDaoImpl.COLUMN_VERSION;
		static final String COLUMN_VERSION_INT = AppDaoImpl.COLUMN_VERSION_INT;
		static final String COLUMN_DOWNLOAD_ID = AppDaoImpl.COLUMN_DOWNLOAD_ID;// 14
		static final String COLUMN_DOWNLOAD_URL = AppDaoImpl.COLUMN_DOWNLOAD_URL;// 15

		static final String COLUMN_SAVE_DEST = "save_dest";// 15
		static final String COLUMN_FAILED_COUNT = "failed_count";// 15
		static final String COLUMN_FAILED_REASON = "failed_reason";// 15
		static final String COLUMN_STATUS = "status";// 15

		static final int STATUS_ERROR = PackageMode.MERGE_FAILED;
		static final int STATUS_SUCCESSFUL = PackageMode.MERGED;
		static final int STATUS_MERGING = PackageMode.MERGING;
		static final int STATUS_DEFAULT = 0;

		public static long addMergeRecord(Context context, MergeMode mode) {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			SQLiteDatabase db = null;
			try {
				db = helper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put(MergeTable.COLUMN_GAME_ID, mode.gameId);
				cv.put(MergeTable.COLUMN_DOWNLOAD_ID, mode.downloadId);
				cv.put(MergeTable.COLUMN_DOWNLOAD_URL, mode.downloadUrl);
				cv.put(MergeTable.COLUMN_PKG_NAME, mode.packageName);
				cv.put(MergeTable.COLUMN_VERSION, mode.version);
				cv.put(MergeTable.COLUMN_VERSION_INT, mode.versionInt);
				cv.put(MergeTable.COLUMN_SAVE_DEST, mode.saveDest);
				if (mode.failedCount > 0) {
					cv.put(MergeTable.COLUMN_FAILED_COUNT, mode.failedCount);
					cv.put(MergeTable.COLUMN_FAILED_REASON, mode.failedReason);
				}
				cv.put(MergeTable.COLUMN_STATUS, mode.status);

				long rowId = db.replace(TABLE_MERGE_LIST, null, cv);
				cv.clear();
				return rowId;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			return -1;
		}

		public static List<MergeMode> queryMergeRecord(Context context) {
			return queryMergeRecord(context, null);
		}

		public static List<MergeMode> queryMergeRecord(Context context,
				String gameId) {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			SQLiteDatabase db = helper.getReadableDatabase();
			String[] columns = { MergeTable.COLUMN_GAME_ID,
					MergeTable.COLUMN_PKG_NAME, MergeTable.COLUMN_VERSION,
					MergeTable.COLUMN_VERSION_INT,
					MergeTable.COLUMN_DOWNLOAD_ID,
					MergeTable.COLUMN_DOWNLOAD_URL,
					MergeTable.COLUMN_SAVE_DEST,
					MergeTable.COLUMN_FAILED_COUNT,
					MergeTable.COLUMN_FAILED_REASON, MergeTable.COLUMN_STATUS };

			Cursor resultCursor = null;
			String whereClause = null;
			String whereArgs[] = null;
			if (gameId != null) {
				whereClause = MergeTable.COLUMN_GAME_ID + "=?";
				whereArgs = new String[] { gameId };
			}
			try {
				resultCursor = db.query(TABLE_MERGE_LIST, columns, whereClause,
						whereArgs, null, null, null);
				int indexGameId = resultCursor
						.getColumnIndex(MergeTable.COLUMN_GAME_ID);
				int indexDownloadId = resultCursor
						.getColumnIndex(MergeTable.COLUMN_DOWNLOAD_ID);
				int indexDownloadUrl = resultCursor
						.getColumnIndex(MergeTable.COLUMN_DOWNLOAD_URL);
				int indexPkg = resultCursor
						.getColumnIndex(MergeTable.COLUMN_PKG_NAME);
				int indexVersion = resultCursor
						.getColumnIndex(MergeTable.COLUMN_VERSION);
				int indexVersionInt = resultCursor
						.getColumnIndex(MergeTable.COLUMN_VERSION_INT);
				int indexSaveDest = resultCursor
						.getColumnIndex(MergeTable.COLUMN_SAVE_DEST);
				int indexFailedCount = resultCursor
						.getColumnIndex(MergeTable.COLUMN_FAILED_COUNT);
				int indexFailedReason = resultCursor
						.getColumnIndex(MergeTable.COLUMN_FAILED_REASON);
				int indexStatus = resultCursor
						.getColumnIndex(MergeTable.COLUMN_STATUS);
				int count = resultCursor.getCount();
				if (count == 0) {
					return null;
				}
				ArrayList<MergeMode> list = new ArrayList<MergeMode>(
						resultCursor.getCount());
				while (resultCursor.moveToNext()) {
					gameId = resultCursor.getString(indexGameId);
					long downloadId = resultCursor.getLong(indexDownloadId);
					String downloadUrl = resultCursor
							.getString(indexDownloadUrl);
					String packageName = resultCursor.getString(indexPkg);
					String version = resultCursor.getString(indexVersion);
					int versionInt = resultCursor.getInt(indexVersionInt);

					String saveDest = resultCursor.getString(indexSaveDest);
					int failedCount = resultCursor.getInt(indexFailedCount);
					int failedReason = resultCursor.getInt(indexFailedReason);
					int status = resultCursor.getInt(indexStatus);

					MergeMode mergeMode = new MergeMode(downloadId,
							downloadUrl, saveDest, versionInt, version, gameId,
							packageName, failedCount, failedReason, status);
					list.add(mergeMode);
				}
				return list;

			} catch (SQLiteException e) {
			} catch (Exception ex ){
            } finally {
				if (resultCursor != null && !resultCursor.isClosed()) {
					resultCursor.close();
				}
			}
			return null;
		}

		public static int updateMergeFaileCount(Context context, MergeMode mode) {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			SQLiteDatabase db = null;
			try {
				db = helper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				if (mode.failedCount > 0) {
					cv.put(MergeTable.COLUMN_STATUS, MergeTable.STATUS_ERROR);
					cv.put(MergeTable.COLUMN_FAILED_COUNT, mode.failedCount);
					cv.put(MergeTable.COLUMN_FAILED_REASON, mode.failedReason);
				}

				String whereClause = null;
				String[] whereArgs = new String[1];
				if (mode.gameId != null) {
					whereClause = MergeTable.COLUMN_GAME_ID + "=?";
					whereArgs[0] = mode.gameId;
				} else if (mode.downloadUrl != null) {
					whereClause = MergeTable.COLUMN_DOWNLOAD_URL + "=?";
					whereArgs[0] = mode.downloadUrl;
				} else if (mode.downloadId > 0) {
					whereClause = MergeTable.COLUMN_DOWNLOAD_ID + "=?";
					whereArgs[0] = Long.toString(mode.downloadId);
				}
				if (whereClause != null) {
					int affected = db.update(TABLE_MERGE_LIST, cv, whereClause,
							whereArgs);
					cv.clear();
					return affected;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			return 0;
		}

		public static int updateMergeStatus(Context context, MergeMode mode) {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			SQLiteDatabase db = null;
			try {
				db = helper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				if (mode.status == MergeTable.STATUS_ERROR) {
					cv.put(MergeTable.COLUMN_STATUS, MergeTable.STATUS_ERROR);
					cv.put(MergeTable.COLUMN_FAILED_COUNT, mode.failedCount);
					cv.put(MergeTable.COLUMN_FAILED_REASON, mode.failedReason);
				} else if (mode.status == MergeTable.STATUS_SUCCESSFUL) {
					cv.put(MergeTable.COLUMN_STATUS,
							MergeTable.STATUS_SUCCESSFUL);
				} else if (mode.status == MergeTable.STATUS_MERGING) {
					cv.put(MergeTable.COLUMN_STATUS, MergeTable.STATUS_MERGING);
				}

				String whereClause = null;
				String[] whereArgs = new String[1];
				if (mode.gameId != null) {
					whereClause = MergeTable.COLUMN_GAME_ID + "=?";
					whereArgs[0] = mode.gameId;
				} else if (mode.downloadUrl != null) {
					whereClause = MergeTable.COLUMN_DOWNLOAD_URL + "=?";
					whereArgs[0] = mode.downloadUrl;
				} else if (mode.downloadId > 0) {
					whereClause = MergeTable.COLUMN_DOWNLOAD_ID + "=?";
					whereArgs[0] = Long.toString(mode.downloadId);
				}
				if (whereClause != null) {
					int affected = db.update(TABLE_MERGE_LIST, cv, whereClause,
							whereArgs);
					cv.clear();
					return affected;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			return 0;
		}

		public static int removeMergeRecord(Context context, MergeMode mode,
				boolean deleteAll) {
			return removeMergeRecord(context, mode.gameId, mode.downloadUrl,
					mode.downloadId, deleteAll);
		}

		public static int removeMergeRecord(Context context, String gameId,
				String downloadUrl, long downloadId, boolean deleteAll) {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			SQLiteDatabase db = null;
			try {
				db = helper.getWritableDatabase();

				if (deleteAll) {
					int affected = db.delete(TABLE_MERGE_LIST, null, null);
					return affected;
				}
				String whereClause = null;
				String[] whereArgs = new String[1];
				if (gameId != null) {
					whereClause = MergeTable.COLUMN_GAME_ID + "=?";
					whereArgs[0] = gameId;
				} else if (downloadUrl != null) {
					whereClause = MergeTable.COLUMN_DOWNLOAD_URL + "=?";
					whereArgs[0] = downloadUrl;
				} else if (downloadId > 0) {
					whereClause = MergeTable.COLUMN_DOWNLOAD_ID + "=?";
					whereArgs[0] = Long.toString(downloadId);
				}
				if (whereClause != null) {
					int affected = db.delete(TABLE_MERGE_LIST, whereClause,
							whereArgs);
					return affected;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			return 0;
		}
	}

	private static final String TAG = "AppDaoImpl";

	AppDaoImpl(Context context) {
		this.context = context.getApplicationContext();
	}

	@Override
	public int updateMergeFailedCount(MergeMode mode) {
		return MergeTable.updateMergeFaileCount(context, mode);
	}

	@Override
	public int updateMergeStatus(MergeMode mode) {
		return MergeTable.updateMergeStatus(context, mode);
	}

	@Override
	public long addMergeRecord(MergeMode mode) {
		return MergeTable.addMergeRecord(context, mode);
	}

	@Override
	public List<MergeMode> queryMergeRecord() {
		return MergeTable.queryMergeRecord(context);

	}

	@Override
	public MergeMode queryMergeRecord(String gameId) {
		List<MergeMode> queryMergeRecord = MergeTable.queryMergeRecord(context,
				gameId);
		if (queryMergeRecord == null || queryMergeRecord.size() == 0) {
			return null;
		}
		return queryMergeRecord.get(0);

	}

	@Override
	public int removeMergeRecord(MergeMode mode) {
		return MergeTable.removeMergeRecord(context, mode, false);

	}

	@Override
	public int removeMergeRecord(String gameId, String downloadUrl,
			long downloadId) {
		return MergeTable.removeMergeRecord(context, gameId, downloadUrl,
				downloadId, false);
	}

	@Override
	public int removeAllMergeRecord() {
		return MergeTable.removeMergeRecord(context, null, null, -1, true);
	}

	/*
	 * public boolean initWhiteList(){ return loadWhiteList(); }
	 */
	private boolean loadWhiteList() {
		try {
			doLoadWork();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

		/*
		 * if(Looper.myLooper() == Looper.getMainLooper()){ new Thread(new
		 * Runnable() { public void run() { try { doLoadWork(); } catch
		 * (IOException e) { throw new RuntimeException(e); } } }).start();
		 * }else{ try { doLoadWork(); } catch (IOException e) { throw new
		 * RuntimeException(e); } }
		 */
	}

	private void doLoadWork() throws IOException {
		final Resources resources = context.getResources();
		InputStream inputStream = resources.openRawResource(R.raw.white_list);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] strings = TextUtils.split(line, "\\|");
				if (strings.length < 2)
					continue;
				long id = addEntry(db, strings[0].trim(), strings[1].trim());
				if (id < 0) {
					// Log.e(TAG, "unable to add entry: " + strings[0].trim());
				}
			}
		} finally {
			reader.close();
		}
	}

	private long addEntry(SQLiteDatabase db, String name, String packageName) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_NAME, name);
		initialValues.put(COLUMN_PKG_NAME, packageName);

		return db.insert(TABLE_WHITE_LIST, null, initialValues);
	}

	public void closeConnection() {
		try {
			AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
					.getInstance(context);
			helper.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void updateIgnoreState(String packageName, boolean ignore) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(UpdateTable.COLUMN_IGNORE_STATE, ignore ? 1 : 0);
			if (packageName != null) {
				db.update(TABLE_UPDATABLE_APP_LIST, cv,
						COLUMN_PKG_NAME + "= ?", new String[] { packageName });
			} else {
				db.update(TABLE_UPDATABLE_APP_LIST, cv, null, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (db != null && db.isOpen() && db.inTransaction() && db.isDbLockedByCurrentThread()) {
                try{
                    db.endTransaction();
                }catch(Exception  e){
                }
            }
        }

	}

	// ////////////////////////////////////////////////////////////////////////////

	@SuppressLint("NewApi")
	public void updateApplicationMD5(List<InstalledAppInfo> apps) {
		if (apps == null || apps.size() == 0) {
			return;
		}
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}

			for (InstalledAppInfo whiteApp : apps) {

				StringBuffer whereClause = new StringBuffer(
						InstalledTable.COLUMN_PKG_NAME + " = ( ");

				int size = apps.size();
				String[] whereArgs = new String[1];
				whereClause.append("?)");
				whereArgs[0] = whiteApp.getPackageName();

				ContentValues cv = new ContentValues();
				cv.put(InstalledTable.COLUMN_FILEMD5, whiteApp.getFileMd5());
				cv.put(InstalledTable.COLUMN_SIGN, whiteApp.getSign());
				db.update(TABLE_INSALLED_APP_LIST, cv, whereClause.toString(),
						whereArgs);
			}
			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen() && db.inTransaction() && db.isDbLockedByCurrentThread()) {
				try{
					db.endTransaction();
				}catch(Exception  e){
				}
			}
		}
	}

	@SuppressLint("NewApi")
	public void updateWhiteList(List<BaseAppInfo> apps) {
		if (apps == null || apps.size() == 0) {
			return;
		}
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}

			StringBuffer whereClause = new StringBuffer(
					InstalledTable.COLUMN_PKG_NAME + " IN ( ");

			int size = apps.size();
			String[] whereArgs = new String[size];
			int i = 0;
			for (BaseAppInfo whiteApp : apps) {
				if (i != (size - 1)) {
					whereClause.append("?,");
				} else {
					whereClause.append("?)");
				}
				whereArgs[i] = whiteApp.getPackageName();
				i++;
			}
			ContentValues cv = new ContentValues();
			cv.put(InstalledTable.COLUMN_IS_GAME, 1);
			db.update(TABLE_INSALLED_APP_LIST, cv, whereClause.toString(),
					whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.endTransaction();
			}
		}
		updateInstalledAppState(null);
	}

	public void addWhiteListApp(BaseAppInfo whiteApp) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_PKG_NAME, whiteApp.getPackageName());
			cv.put(COLUMN_NAME, whiteApp.getName());
			// cv.put(COLUMN_VERSION, whiteApp.getVersion());
			// cv.put(COLUMN_DATE, whiteApp.getDate());
			// cv.put(COLUMN_EXTRA, whiteApp.getPackageName());
			long rowId = db.replace(TABLE_WHITE_LIST, null, cv);
			cv.clear();
		} catch (Exception e) {
		} finally {
		}
	}

	/**
	 * set the column "is_game" to 1.
	 */
	private void updateInstalledAppState(String packageName) {
		if (packageName == null) {
			List<MyDownloadedGame> allMyDownloadedGames = getAllMyDownloadedGames();
			if (Constants.DEBUG)
				Log.d("TAG",
						"[AppDaoImpl#updateInstalledAppState]AllMyDownloadedGames size:"
								+ ((allMyDownloadedGames != null) ? allMyDownloadedGames
										.size() : "null"));
			if (allMyDownloadedGames != null && allMyDownloadedGames.size() > 0) {
				HashMap<String, String> hashMap = new HashMap<String, String>(
						allMyDownloadedGames.size());
				for (MyDownloadedGame d : allMyDownloadedGames) {
					hashMap.put(d.getPackageName(), d.getGameId());
				}
				updateInstalledGameIds(hashMap);
			}
		}
	}

	public boolean debug = true;

	@Override
	@SuppressLint("NewApi")
	public void replaceAllInstalledApps(List<InstalledAppInfo> list) {
		saveAllInstalledApps(list, true);
	}

	@Override
	@SuppressLint("NewApi")
	public void saveAllInstalledApps(List<InstalledAppInfo> list) {
		saveAllInstalledApps(list, false);
	}

	@SuppressLint("NewApi")
	private void saveAllInstalledApps(List<InstalledAppInfo> list,
			boolean replace) {
		if (list == null || list.size() == 0) {
			return;
		}
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {

			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}
			ContentValues cv = new ContentValues();
			for (InstalledAppInfo app : list) {
				cv.put(InstalledTable.COLUMN_PKG_NAME, app.getPackageName());
				cv.put(InstalledTable.COLUMN_NAME, app.getName());
				cv.put(InstalledTable.COLUMN_NAME_PINYIN, app.getPinyinName());
				cv.put(InstalledTable.COLUMN_VERSION, app.getVersion());
				cv.put(InstalledTable.COLUMN_VERSION_INT, app.getVersionInt());
				cv.put(InstalledTable.COLUMN_INSTALLED_DATE, app.getDate());
				cv.put(InstalledTable.COLUMN_SIZE, app.getSize());
				if (app.getSign() != null) {
					cv.put(InstalledTable.COLUMN_SIGN, app.getSign());
				}
				cv.put(InstalledTable.COLUMN_EXTRA, app.getExtra());
				cv.put(InstalledTable.COLUMN_NEED_LOGIN, app.isNeedLogin() ? 1
						: 0);
				cv.put(InstalledTable.COLUMN_FILEMD5, app.getFileMd5());
				cv.put(InstalledTable.COLUMN_UID, app.getUid());
				long rowId = -1;
				// if(!replace){
				// rowId = db.insert(TABLE_INSALLED_APP_LIST, null, cv);
				// }else{
				// }
				rowId = db.replace(TABLE_INSALLED_APP_LIST, null, cv);
				if (rowId < 0) {
					Log.d("wangliangtest",
							"[AppDaoImpl#saveAllInstalledApps]app "
									+ app.getName() + " is already saved");
				}
				/*
				 * if(debug){ db.insertOrThrow(TABLE_INSALLED_APP_LIST, null,
				 * cv); }else{ db.replace(TABLE_INSALLED_APP_LIST, null, cv); }
				 */
				if ("com.mas.wawagame.BDDKlord".equals(app.getPackageName())) {
					Log.d("wangliangtest",
							"[AppDaoImpl#saveAllInstalledApps]save com.mas.wawagame.BDDKlord:extra?"
									+ app.getExtra() + ";login?"
									+ app.isNeedLogin());
				}
				cv.clear();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (db != null) {
				db.endTransaction();
			}
		}
		updateInstalledAppState(null);
	}

	@Override
	public void removeDeletedApp(String packageName) {
		if (packageName == null) {
			return;
		}
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			db.delete(TABLE_INSALLED_APP_LIST, InstalledTable.COLUMN_PKG_NAME
					+ "=?", new String[] { packageName });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void addInstalledApp(InstalledAppInfo app) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();

			ContentValues cv = new ContentValues();
			cv.put(InstalledTable.COLUMN_PKG_NAME, app.getPackageName());
			cv.put(InstalledTable.COLUMN_NAME, app.getName());
			cv.put(InstalledTable.COLUMN_NAME_PINYIN, app.getPinyinName());
			cv.put(InstalledTable.COLUMN_VERSION, app.getVersion());
			cv.put(InstalledTable.COLUMN_VERSION_INT, app.getVersionInt());
			cv.put(InstalledTable.COLUMN_INSTALLED_DATE, app.getDate());
			cv.put(InstalledTable.COLUMN_SIZE, app.getSize());
			cv.put(InstalledTable.COLUMN_EXTRA, app.getExtra());
			cv.put(InstalledTable.COLUMN_NEED_LOGIN, app.isNeedLogin() ? 1 : 0);
			cv.put(InstalledTable.COLUMN_FILEMD5, app.getFileMd5());
			if (app.getUid() > 0)
				cv.put(InstalledTable.COLUMN_UID, app.getUid());
			if (app.getSign() != null) {
				cv.put(COLUMN_SIGN, app.getSign());
			}
			long rowId = db.replace(TABLE_INSALLED_APP_LIST, null, cv);
			cv.clear();
			updateInstalledAppState(app.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	private void addMyInstalledAppRecord(InstalledAppInfo app,
			Long installTime, Long lastOpenTime, Integer openTimes) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(MyInstalledTable.COLUMN_PKG_NAME, app.getPackageName());
			if (installTime != null && installTime > 0) {
				cv.put(MyInstalledTable.COLUMN_INSTALL_TIME,
						app.getPackageName());
			}
			if (lastOpenTime != null && lastOpenTime > 0) {
				cv.put(MyInstalledTable.COLUMN_LASTEST_OPEN_TIME, lastOpenTime);
			}
			if (openTimes != null && openTimes > 0) {
				cv.put(MyInstalledTable.COLUMN_OPEN_TIMES, openTimes);
			}
			long rowId = db.insert(TABLE_MY_INSTALLED_LIST, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	@Override
	public void addMyInstalledApp(InstalledAppInfo app) {
		addInstalledApp(app, true);
		addMyInstalledAppRecord(app, new Date().getTime(), null, null);
	}

	@Override
	public void addMyInstalledApp(MyInstalledAppInfo app) {
		addInstalledApp(app, true);
		addMyInstalledAppRecord(app, app.getInstallDate(),
				app.getLatestOpenTime(), app.getOpenTimes());
	}

	public void addInstalledApp(InstalledAppInfo app, boolean isGame) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(InstalledTable.COLUMN_PKG_NAME, app.getPackageName());
			cv.put(InstalledTable.COLUMN_NAME, app.getName());
			cv.put(InstalledTable.COLUMN_NAME_PINYIN, app.getPinyinName());
			cv.put(InstalledTable.COLUMN_VERSION, app.getVersion());
			cv.put(InstalledTable.COLUMN_VERSION_INT, app.getVersionInt());
			cv.put(InstalledTable.COLUMN_INSTALLED_DATE, app.getDate());
			cv.put(InstalledTable.COLUMN_EXTRA, app.getExtra());
			cv.put(InstalledTable.COLUMN_NEED_LOGIN, app.isNeedLogin() ? 1 : 0);
			cv.put(InstalledTable.COLUMN_SIZE, app.getSize());
			cv.put(InstalledTable.COLUMN_IS_GAME, isGame ? 1 : 0);
			cv.put(InstalledTable.COLUMN_GAME_ID, app.getGameId());
			cv.put(InstalledTable.COLUMN_FILEMD5, app.getFileMd5());
			if (app.getUid() > 0)
				cv.put(InstalledTable.COLUMN_UID, app.getUid());
			if (app.getSign() != null) {
				cv.put(COLUMN_SIGN, app.getSign());
			}
			long rowId = db.replace(TABLE_INSALLED_APP_LIST, null, cv);
			cv.clear();
			updateInstalledAppState(app.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	/**
	 * + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
	 * COLUMN_PKG_NAME + " TEXT UNIQUE NOT NULL ," //app attribute 1 +
	 * COLUMN_NAME + " TEXT," //app attribute 2 + COLUMN_NAME_PINYIN + " TEXT,"
	 * //app attribute 3 + COLUMN_VERSION + " TEXT," //app attribute 4 +
	 * COLUMN_VERSION_INT + " INTEGER," //app attribute 5 + COLUMN_ICON_URL +
	 * " TEXT," //app attribute 6 + COLUMN_PUBLISH_DATE + " INTEGER," //app
	 * attribute 7 + COLUMN_SERVER_SIGN + " TEXT," //app attribute 8 +
	 * COLUMN_NEW_SIZE + " INTEGER," //app attribute 9 + COLUMN_EXTRA + " TEXT,"
	 * //app attribute 10
	 * 
	 * + COLUMN_DOWNLOAD_DATE + " INTEGER," //file attribute +
	 * COLUMN_DOWNLOAD_ID + " INTEGER NOT NULL UNIQUE," //file attribute +
	 * COLUMN_DOWNLOAD_URL + " TEXT NOT NULL UNIQUE" //file attribute
	 * 
	 * @param app
	 */

	@Override
	public long addDownloadGame(DownloadAppInfo app) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_GAME_ID, app.getGameId()); // 0 package
																	// name
			cv.put(DownloadTable.COLUMN_PKG_NAME, app.getPackageName()); // 1
																			// package
																			// name
			cv.put(DownloadTable.COLUMN_NAME, app.getName()); // 2 name
			cv.put(DownloadTable.COLUMN_NAME_PINYIN, app.getPinyinName());// 3
																			// pinyin
			cv.put(DownloadTable.COLUMN_VERSION, app.getVersion());// 4 version
			cv.put(DownloadTable.COLUMN_VERSION_INT, app.getVersionInt());// 5
																			// version
																			// int
			cv.put(DownloadTable.COLUMN_PUBLISH_DATE, app.getDate());// 6
																		// publish
																		// date
			cv.put(DownloadTable.COLUMN_SIZE, app.getSize());// 7 size
			cv.put(DownloadTable.COLUMN_SIGN, app.getSign());// 8 sign
			cv.put(DownloadTable.COLUMN_FILEMD5, app.getFileMd5());// 8 sign
			cv.put(DownloadTable.COLUMN_ICON_URL, app.getIconUrl());// 9 icon
																	// url
			cv.put(DownloadTable.COLUMN_EXTRA, app.getExtra());// 10 extra
			cv.put(DownloadTable.COLUMN_NEED_LOGIN, app.isNeedLogin() ? 1 : 0);// 10
																				// extra

			cv.put(DownloadTable.COLUMN_DOWNLOAD_DATE, app.getDownloadDate());// 11
																				// download
																				// date
			cv.put(DownloadTable.COLUMN_DOWNLOAD_ID, app.getDownloadId());// 12
																			// download
																			// id
			cv.put(DownloadTable.COLUMN_DOWNLOAD_URL, app.getDownloadUrl());// 13
																			// download
																			// url
			cv.put(DownloadTable.COLUMN_IS_DIFFUPDATE, app.isDiffUpdate() ? 1
					: 0);// 13 download url

			// long rowId = db.replace(TABLE_DOWNLOAD_APP_LIST, null, cv);
			long rowId = db.insert(TABLE_DOWNLOAD_APP_LIST, null, cv);
			// if(rowId < 0){
			if (Constants.DEBUG)
				Log.i("PopNumber", "[AppDaoImpl#addDownloadGame]rowId:" + rowId);
			// }
			cv.clear();
			if (Constants.DEBUG)
				Log.i("wang", "addDownloadGame db,rowId:" + rowId);
			return rowId;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
		return -1;
	}

	@Override
	public int updateDownloadGame(String oldUrl, String newUrl,
			boolean diffUpdate, long newSize) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put(DownloadTable.COLUMN_DOWNLOAD_URL, newUrl);
			cv.put(DownloadTable.COLUMN_IS_DIFFUPDATE, diffUpdate ? 1 : 0);
			cv.put(DownloadTable.COLUMN_SIZE, newSize);
			int r = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
					DownloadTable.COLUMN_DOWNLOAD_URL + "=?",
					new String[] { oldUrl });
			cv.clear();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return 0;
	}

	@Override
	public int updateDownloadGame(long oldId, String newUrl,
			boolean diffUpdate, long newSize) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put(DownloadTable.COLUMN_DOWNLOAD_URL, newUrl);
			cv.put(DownloadTable.COLUMN_IS_DIFFUPDATE, diffUpdate ? 1 : 0);
			cv.put(DownloadTable.COLUMN_SIZE, newSize);
			int r = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
					DownloadTable.COLUMN_DOWNLOAD_ID + "=?",
					new String[] { String.valueOf(oldId) });
			cv.clear();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return 0;
	}

	@Override
	public void updateDownload(Long downloadId, String packageName,
			String newPackage, String verion, int versionCode, String sign,
			String fileMd5/* ,boolean isDiffUpdate */) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_PKG_NAME, newPackage); // 1 package name
			cv.put(DownloadTable.COLUMN_VERSION, verion); // 1 package name
			cv.put(DownloadTable.COLUMN_VERSION_INT, versionCode); // 1 package
																	// name
			cv.put(DownloadTable.COLUMN_SIGN, sign); // 1 package name
			cv.put(DownloadTable.COLUMN_FILEMD5, fileMd5); // 1 package name
			// cv.put(DownloadTable.COLUMN_IS_DIFFUPDATE, isDiffUpdate?1:0); //1
			// package name

			String whereClause = DownloadTable.COLUMN_PKG_NAME + "=? AND "
					+ DownloadTable.COLUMN_DOWNLOAD_ID + "=?";
			String[] whereArgs = new String[] { packageName,
					String.valueOf(downloadId) };
			int count = db.update(TABLE_DOWNLOAD_APP_LIST, cv, whereClause,
					whereArgs);
			if (count < 0) {

			}
			cv.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	@Override
	public void updateDownload(Long downloadId, String sign, String fileMd5/*
																			 * ,
																			 * boolean
																			 * isDiffUpdate
																			 */) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_SIGN, sign);
			cv.put(DownloadTable.COLUMN_FILEMD5, fileMd5);

			String whereClause = DownloadTable.COLUMN_DOWNLOAD_ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(downloadId) };
			int count = db.update(TABLE_DOWNLOAD_APP_LIST, cv, whereClause,
					whereArgs);
			cv.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public int updateDownloadId(String downloadUrl, long downloadId) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_DOWNLOAD_ID, downloadId); // 1 package
																	// name

			String whereClause = DownloadTable.COLUMN_DOWNLOAD_URL + "=?";
			String[] whereArgs = new String[] { downloadUrl };
			int count = db.update(TABLE_DOWNLOAD_APP_LIST, cv, whereClause,
					whereArgs);
			cv.clear();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
		return 0;
	}

	@Override
	public void updateGameInstallStatus(String packageName, Long downloadId,
			InstallStatus status, int... errorReason) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			/*
			 * int statusInt = DownloadTable.INSTALL_STATUS_UNINSTALLED;
			 * if(status == InstallerPackageEvent.INSTALLED){ statusInt =
			 * DownloadTable.INSTALL_STATUS_INSTALLED ; }else if(status ==
			 * InstallerPackageEvent.INSTALLING){ statusInt =
			 * DownloadTable.INSTALL_STATUS_INSTALLING; }else if(status ==
			 * InstallerPackageEvent.UNINSTALLED){ statusInt =
			 * DownloadTable.INSTALL_STATUS_UNINSTALLED; }
			 */
			cv.put(DownloadTable.COLUMN_INSTALL_STATUS, status.getIndex());
			if (status == InstallStatus.INSTALL_ERROR) {
				cv.put(DownloadTable.COLUMN_INSTALL_ERROR_REASON,
						errorReason[0]);
			}
			String whereClause = DownloadTable.COLUMN_PKG_NAME + "=? AND "
					+ DownloadTable.COLUMN_DOWNLOAD_ID + "=?";
			String[] whereArgs = new String[] { packageName,
					String.valueOf(downloadId) };
			db.update(TABLE_DOWNLOAD_APP_LIST, cv, whereClause, whereArgs);
			cv.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	@Override
	public void addDownloadGames(DownloadAppInfo... apps) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();

			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				ContentValues cv = new ContentValues();
				for (DownloadAppInfo app : apps) {
					cv.put(DownloadTable.COLUMN_GAME_ID, app.getGameId()); // 0
																			// package
																			// name
					cv.put(DownloadTable.COLUMN_PKG_NAME, app.getPackageName()); // 1
																					// package
																					// name
					cv.put(DownloadTable.COLUMN_NAME, app.getName()); // 2 name
					cv.put(DownloadTable.COLUMN_NAME_PINYIN,
							app.getPinyinName());// 3 pinyin
					cv.put(DownloadTable.COLUMN_VERSION, app.getVersion());// 4
																			// version
					cv.put(DownloadTable.COLUMN_VERSION_INT,
							app.getVersionInt());// 5 version int
					cv.put(DownloadTable.COLUMN_PUBLISH_DATE, app.getDate());// 6
																				// publish
																				// date
					cv.put(DownloadTable.COLUMN_SIZE, app.getSize());// 7 size
					cv.put(DownloadTable.COLUMN_SIGN, app.getSign());// 8 sign
					cv.put(DownloadTable.COLUMN_FILEMD5, app.getFileMd5());// 8
																			// sign
					cv.put(DownloadTable.COLUMN_ICON_URL, app.getIconUrl());// 9
																			// icon
																			// url
					cv.put(DownloadTable.COLUMN_EXTRA, app.getExtra());// 10
																		// extra
					cv.put(DownloadTable.COLUMN_NEED_LOGIN,
							app.isNeedLogin() ? 1 : 0);// 10 extra

					cv.put(DownloadTable.COLUMN_DOWNLOAD_DATE,
							app.getDownloadDate());// 11 download date
					cv.put(DownloadTable.COLUMN_DOWNLOAD_ID,
							app.getDownloadId());// 12 download id
					cv.put(DownloadTable.COLUMN_DOWNLOAD_URL,
							app.getDownloadUrl());// 13 download url
					// long rowId = db.replace(TABLE_DOWNLOAD_APP_LIST, null,
					// cv);
					long rowId = db.insert(TABLE_DOWNLOAD_APP_LIST, null, cv);
					cv.clear();
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	public void addMyDownloadedGames(List<MyDownloadedGame> games) {

		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			// new ReentrantLock().isHeldByCurrentThread();
			// new ReentrantLock().lock();
			// new ReentrantLock().isLocked();
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				ContentValues cv = new ContentValues();
				for (MyDownloadedGame app : games) {
					cv.put(MyDownloadedTable.COLUMN_GAME_ID, app.getGameId()); // 0
																				// package
																				// name
					cv.put(MyDownloadedTable.COLUMN_PKG_NAME,
							app.getPackageName()); // 1 package name
					cv.put(MyDownloadedTable.COLUMN_NAME, app.getName()); // 2
																			// name
					cv.put(MyDownloadedTable.COLUMN_ICON_URL, app.getIconUrl());// 9
																				// icon
																				// url
					cv.put(MyDownloadedTable.COLUMN_GAME_KEY, app.getKey());// 3
																			// pinyin
					cv.put(MyDownloadedTable.COLUMN_EXTRA, app.getExtra()); // 7
																			// size
					cv.put(MyDownloadedTable.COLUMN_NEED_LOGIN,
							app.isNeedLogin() ? 1 : 0); // 7 size
					long rowId = db.replace(TABLE_MY_DOWNLOADED_LIST, null, cv);
					if ("com.mas.wawagame.BDDKlord"
							.equals(app.getPackageName())) {
						if (Constants.DEBUG)
							Log.i("wangliangtest",
									"[AppDaoImpl#addMyDownloadedGames]save com.mas.wawagame.BDDKlord:rowid:"
											+ rowId + " gameId:"
											+ app.getGameId());
					}
					cv.clear();
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (db != null && db.isOpen()) { db.close(); }
			 */
		}
	}

	@Override
	public List<MyDownloadedGame> getAllMyDownloadedGames() {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		db = helper.getReadableDatabase();
		String[] columns = new String[] {

		MyDownloadedTable.COLUMN_PKG_NAME, MyDownloadedTable.COLUMN_GAME_ID,
				MyDownloadedTable.COLUMN_NAME,
				MyDownloadedTable.COLUMN_GAME_KEY,
				MyDownloadedTable.COLUMN_ICON_URL,
				MyDownloadedTable.COLUMN_EXTRA,
				MyDownloadedTable.COLUMN_NEED_LOGIN

		};

		String selection = null;
		String selectionArgs[] = null;
		Cursor resultCursor = null;
		try {
			resultCursor = db.query(TABLE_MY_DOWNLOADED_LIST, columns,
					selection, selectionArgs, null, null, null);
			int indexPkg = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_NAME);
			int indexIconUrl = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_ICON_URL);
			int indexExtra = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_EXTRA);
			int indexGameId = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_GAME_ID);
			int indexKey = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_GAME_KEY);

			int indexNeedLogin = resultCursor
					.getColumnIndex(MyDownloadedTable.COLUMN_NEED_LOGIN);

			// List<DownloadAppInfo> apps = new
			// ArrayList<DownloadAppInfo>(resultCursor.getCount());
			List<MyDownloadedGame> apps = new ArrayList<MyDownloadedGame>();
			while (resultCursor.moveToNext()) {
				String packageName = resultCursor.getString(indexPkg);
				String name = resultCursor.getString(indexName);
				String extra = resultCursor.getString(indexExtra);
				String iconUrl = resultCursor.getString(indexIconUrl);
				String gameId = resultCursor.getString(indexGameId);
				String key = resultCursor.getString(indexKey);
				boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);

				MyDownloadedGame info = new MyDownloadedGame(gameId, name,
						iconUrl, packageName, key, extra, needLogin);

				apps.add(info);

			}
			return apps;
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;

	}

	@Override
	public void removeMyDownloadedGame(String gameId) {

		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}
			int id = db.delete(TABLE_MY_DOWNLOADED_LIST,
					MyDownloadedTable.COLUMN_GAME_ID + "= ?",
					new String[] { gameId });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.endTransaction();
			}
		}
	}

	/*
	 * @Override public void removeDownloadGames(boolean delete,String...
	 * packageNames){ if(packageNames == null || packageNames.length ==0){
	 * return ; } AppStorgeSqliteHelper helper =
	 * AppStorgeSqliteHelper.getInstance(context); SQLiteDatabase db = null ;
	 * try { db = helper.getWritableDatabase(); try { if (Build.VERSION.SDK_INT
	 * >= Build.VERSION_CODES.HONEYCOMB){ db.beginTransactionNonExclusive();
	 * }else{ db.beginTransaction(); } ContentValues cv = new ContentValues();
	 * cv.put(DownloadTable.COLUMN_IS_DELETED, 1); for (String packageName :
	 * packageNames) { if(delete){ db.delete(TABLE_DOWNLOAD_APP_LIST,
	 * DownloadTable.COLUMN_PKG_NAME+"=?", new String[]{packageName}); }else{
	 * int update = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
	 * DownloadTable.COLUMN_PKG_NAME+"=?", new String[]{packageName});
	 * //Log.i("DowloadAppsLoadertest",
	 * "[DowloadAppsLoader]removeDownloadGames for "
	 * +packageName+" affect:"+update); } } db.setTransactionSuccessful();
	 * }finally{ db.endTransaction(); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
	@Override
	public void removeDownloadGame(boolean delete, String packageName,
			String version, String versionInt) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_IS_DELETED, 1);
			if (delete) {
				db.delete(TABLE_DOWNLOAD_APP_LIST,
						DownloadTable.COLUMN_PKG_NAME + "=? AND "
								+ DownloadTable.COLUMN_VERSION + "=? AND "
								+ DownloadTable.COLUMN_VERSION_INT + "=?",
						new String[] { packageName, version, versionInt });
			} else {
				int update = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
						DownloadTable.COLUMN_PKG_NAME + "=? AND "
								+ DownloadTable.COLUMN_VERSION + "=?AND "
								+ DownloadTable.COLUMN_VERSION_INT + "=?",
						new String[] { packageName, version, versionInt });
				Log.i("DowloadAppsLoadertest",
						"[DowloadAppsLoader]removeDownloadGames for "
								+ packageName + "version" + version
								+ " affect:" + update);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDownloadGame(boolean delete, String packageName) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_IS_DELETED, 1);
			if (delete) {
				db.delete(TABLE_DOWNLOAD_APP_LIST,
						DownloadTable.COLUMN_PKG_NAME + "=? ",
						new String[] { packageName });
			} else {
				int update = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
						DownloadTable.COLUMN_PKG_NAME + "=? ",
						new String[] { packageName });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDownloadGame(String downloadUrl) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			db.delete(TABLE_DOWNLOAD_APP_LIST,
					DownloadTable.COLUMN_DOWNLOAD_URL + "=? ",
					new String[] { downloadUrl });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDownloadGame(boolean delete,/* String packageName, */
			long downloadId) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_IS_DELETED, 1);
			if (delete) {
				db.delete(TABLE_DOWNLOAD_APP_LIST,/*
												 * DownloadTable.COLUMN_PKG_NAME+
												 * "=? AND "+
												 */
						DownloadTable.COLUMN_DOWNLOAD_ID + "=?",
						new String[] {/* packageName, */String
								.valueOf(downloadId) });
			} else {
				int update = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
						DownloadTable.COLUMN_DOWNLOAD_ID + "=?",
						new String[] {/* packageName, */String
								.valueOf(downloadId) });
				// Log.i(AppSilentInstaller.TAG,
				// "[AppDaoImpl#removeDownloadGame] for "+packageName+"downloadId "+downloadId+" affect:"+update);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateDownloadGameRecord(String packageName, boolean showing) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_IS_DELETED, showing ? 0 : 1);
			if (showing) {
				cv.put(DownloadTable.COLUMN_INSTALL_STATUS,
						DownloadTable.INSTALL_STATUS_UNINSTALLED);
			}
			int update = db.update(TABLE_DOWNLOAD_APP_LIST, cv,
					DownloadTable.COLUMN_PKG_NAME + "=? ",
					new String[] { packageName });
			// Log.i(AppSilentInstaller.TAG,
			// "[AppDaoImpl#removeDownloadGame] for "+packageName+"downloadId "+downloadId+" affect:"+update);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDownloadGames2(boolean delete, String... gameIds) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				ContentValues cv = new ContentValues();
				cv.put(DownloadTable.COLUMN_IS_DELETED, 1);
				for (String id : gameIds) {
					if (delete) {
						db.delete(TABLE_DOWNLOAD_APP_LIST,
								DownloadTable.COLUMN_GAME_ID + "=?",
								new String[] { id });
					} else {
						db.update(TABLE_DOWNLOAD_APP_LIST, cv,
								DownloadTable.COLUMN_GAME_ID + "=?",
								new String[] { id });
					}
				}

				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDownloadGames(boolean delete, long... downloadIds) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				ContentValues cv = new ContentValues();
				cv.put(DownloadTable.COLUMN_IS_DELETED, 1);
				for (long id : downloadIds) {
					if (delete) {
						db.delete(TABLE_DOWNLOAD_APP_LIST,
								DownloadTable.COLUMN_DOWNLOAD_ID + "=?",
								new String[] { String.valueOf(id) });
					} else {
						db.update(TABLE_DOWNLOAD_APP_LIST, cv,
								DownloadTable.COLUMN_DOWNLOAD_ID + "=?",
								new String[] { String.valueOf(id) });
					}
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeAllDownloadGames(boolean delete) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				ContentValues cv = new ContentValues();
				cv.put(DownloadTable.COLUMN_IS_DELETED, 1);
				if (delete) {
					db.delete(TABLE_DOWNLOAD_APP_LIST, null, null);
				} else {
					db.update(TABLE_DOWNLOAD_APP_LIST, cv, null, null);
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public BaseAppInfo getWhiteApp(String packageName) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			String[] s = { WhiteListTable.COLUMN_PKG_NAME,
			// WhiteListTable.COLUMN_NAME,
			// WhiteListTable.COLUMN_PUBLISH_DATE,
			// WhiteListTable.COLUMN_SIGN,WhiteListTable.COLUMN_VERSION,
			// WhiteListTable.COLUMN_VERSION_INT,WhiteListTable.COLUMN_EXTRA,

			};
			Cursor query = db.query(TABLE_WHITE_LIST, s,
					WhiteListTable.COLUMN_PKG_NAME + "=?",
					new String[] { packageName }, null, null, null);
			if (query.moveToFirst()) {
				String pack = query.getString(query
						.getColumnIndex(WhiteListTable.COLUMN_PKG_NAME));
				String name = query.getString(query
						.getColumnIndex(WhiteListTable.COLUMN_NAME));
				// long date =
				// query.getLong(query.getColumnIndex(WhiteListTable.COLUMN_PUBLISH_DATE));
				// String sign =
				// query.getString(query.getColumnIndex(WhiteListTable.COLUMN_SIGN));
				// String version =
				// query.getString(query.getColumnIndex(WhiteListTable.COLUMN_VERSION));
				// int versionInt =
				// query.getInt(query.getColumnIndex(WhiteListTable.COLUMN_VERSION_INT));
				// String extra =
				// query.getString(query.getColumnIndex(WhiteListTable.COLUMN_EXTRA));
				return new BaseAppInfo(pack, name/* , sign */);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<DownloadAppInfo> getAllDownloadGames(boolean includeDeleted) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			return queryDownloadList(db, includeDeleted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public UpdatableAppInfo getUpdatableGame(String packageName) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			UpdatableAppInfo info = (UpdatableAppInfo) queryInstalledOrUpdatableApp(
					db, packageName, false);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstalledAppInfo getInstalledGame(String packageName) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			InstalledAppInfo info = (InstalledAppInfo) queryInstalledOrUpdatableApp(
					db, packageName, true);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstalledAppInfo getInstalledApp(String packageName) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			InstalledAppInfo info = queryInstalleApp(db, packageName);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean getDownloadNotifyStatus(String downloadUrl) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		Cursor resultCursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			String[] columns = new String[] { DownloadTable.COLUMN_NOTIFIED };

			String selection = null;
			String selectionArgs[] = null;
			selection = DownloadTable.COLUMN_DOWNLOAD_URL + "=? ";
			selectionArgs = new String[] { downloadUrl };
			resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
					selection, selectionArgs, null, null, null);
			if (resultCursor.getCount() > 1) {
			} else if (resultCursor.getCount() == 0) {
				return false;
			}
			resultCursor.moveToFirst();
			return resultCursor.getInt(0) == 1;

		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return false;
	}

	@Override
	public void updateDownloadNotifyStatus(String downloadUrl, boolean flag) {
		// COLUMN_NOTIFIED

		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadTable.COLUMN_NOTIFIED, flag ? 1 : 0);
			if (downloadUrl != null) {
				db.update(TABLE_DOWNLOAD_APP_LIST, cv,
						DownloadTable.COLUMN_DOWNLOAD_URL + "= ?",
						new String[] { downloadUrl });
			} else {
				db.update(TABLE_DOWNLOAD_APP_LIST, cv, null, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public DownloadAppInfo getDownloadGame(
			/* String packageName, */Long downloadId, boolean includeDeleted) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		Cursor resultCursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			String[] columns = new String[] { DownloadTable.COLUMN_PKG_NAME,
					DownloadTable.COLUMN_GAME_ID, DownloadTable.COLUMN_NAME,
					DownloadTable.COLUMN_NAME_PINYIN,
					DownloadTable.COLUMN_PUBLISH_DATE,
					DownloadTable.COLUMN_VERSION,
					DownloadTable.COLUMN_VERSION_INT,
					DownloadTable.COLUMN_SIGN, DownloadTable.COLUMN_SIZE,
					DownloadTable.COLUMN_DOWNLOAD_URL,
					DownloadTable.COLUMN_ICON_URL, DownloadTable.COLUMN_EXTRA,
					DownloadTable.COLUMN_DOWNLOAD_DATE,
					DownloadTable.COLUMN_DOWNLOAD_ID,
					DownloadTable.COLUMN_IS_DELETED,
					DownloadTable.COLUMN_INSTALL_STATUS,
					DownloadTable.COLUMN_INSTALL_ERROR_REASON,
					DownloadTable.COLUMN_NEED_LOGIN,
					DownloadTable.COLUMN_IS_DIFFUPDATE,
					DownloadTable.COLUMN_FILEMD5, };

			String selection = null;
			String selectionArgs[] = null;
			if (includeDeleted) {
				// 包含删除的和未删除的
				selection = /* DownloadTable.COLUMN_PKG_NAME+"=? AND "+ */DownloadTable.COLUMN_DOWNLOAD_ID
						+ "=?";
				selectionArgs = new String[] {/* packageName, */String
						.valueOf(downloadId) };
			} else {
				// 不包含删除的
				selection = /* DownloadTable.COLUMN_PKG_NAME+"=? AND "+ */DownloadTable.COLUMN_DOWNLOAD_ID
						+ "=? AND " + DownloadTable.COLUMN_IS_DELETED + " = 0 ";
				;
				selectionArgs = new String[] {/* packageName, */String
						.valueOf(downloadId) };
			}
			resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
					selection, selectionArgs, null, null,
					DownloadTable.COLUMN_DOWNLOAD_DATE + " DESC");
			if (resultCursor.getCount() > 1) {
				// throw new RuntimeException("Error");
			} else if (resultCursor.getCount() == 0) {
				return null;
			}
			resultCursor.moveToFirst();
			return extractDownloadAppInfo(resultCursor);
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	@Override
	public DownloadAppInfo getDownloadGame(String fileMd5,
			boolean includeDeleted) {
		return DownloadTable.getDownloadGame(context, fileMd5, includeDeleted);
	}

	@Override
	public DownloadAppInfo getDownloadGame(String packageName, String version,
			String versionInt, boolean includeDeleted) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		Cursor resultCursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			String[] columns = new String[] { DownloadTable.COLUMN_PKG_NAME,
					DownloadTable.COLUMN_GAME_ID, DownloadTable.COLUMN_NAME,
					DownloadTable.COLUMN_NAME_PINYIN,
					DownloadTable.COLUMN_PUBLISH_DATE,
					DownloadTable.COLUMN_VERSION,
					DownloadTable.COLUMN_VERSION_INT,
					DownloadTable.COLUMN_SIGN, DownloadTable.COLUMN_SIZE,
					DownloadTable.COLUMN_DOWNLOAD_URL,
					DownloadTable.COLUMN_ICON_URL, DownloadTable.COLUMN_EXTRA,
					DownloadTable.COLUMN_DOWNLOAD_DATE,
					DownloadTable.COLUMN_DOWNLOAD_ID,
					DownloadTable.COLUMN_IS_DELETED,
					DownloadTable.COLUMN_INSTALL_STATUS,
					DownloadTable.COLUMN_INSTALL_ERROR_REASON,
					DownloadTable.COLUMN_NEED_LOGIN,
					DownloadTable.COLUMN_IS_DIFFUPDATE,
					DownloadTable.COLUMN_FILEMD5 };

			String selection = null;
			String selectionArgs[] = null;
			if (includeDeleted) {
				// 包含删除的和未删除的
				selection = DownloadTable.COLUMN_PKG_NAME + "=? AND "
						+ DownloadTable.COLUMN_VERSION + "=? AND "
						+ DownloadTable.COLUMN_VERSION_INT + "=?";
				selectionArgs = new String[] { packageName, version, versionInt };
			} else {
				// 不包含删除的
				selection = DownloadTable.COLUMN_PKG_NAME + "=? AND "
						+ DownloadTable.COLUMN_VERSION + "=? AND "
						+ DownloadTable.COLUMN_VERSION_INT + "=? AND "
						+ DownloadTable.COLUMN_IS_DELETED + " = 0 ";
				;
				selectionArgs = new String[] { packageName, version, versionInt };
			}
			resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
					selection, selectionArgs, null, null,
					DownloadTable.COLUMN_DOWNLOAD_DATE + " DESC");
			if (resultCursor.getCount() > 1) {
				// throw new RuntimeException("Error");
			} else if (resultCursor.getCount() == 0) {
				return null;
			}
			resultCursor.moveToFirst();
			return extractDownloadAppInfo(resultCursor);
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	@Override
	public DownloadAppInfo getDownloadGame(String downloadUrl, String gameId,
			boolean includeDeleted) {
		if (downloadUrl == null && gameId == null) {
			return null;
		}
		int selectionSize = 0;
		selectionSize += (downloadUrl != null) ? 1 : 0;
		selectionSize += (gameId != null) ? 1 : 0;

		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		Cursor resultCursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			String[] columns = new String[] { DownloadTable.COLUMN_PKG_NAME,
					DownloadTable.COLUMN_GAME_ID, DownloadTable.COLUMN_NAME,
					DownloadTable.COLUMN_NAME_PINYIN,
					DownloadTable.COLUMN_PUBLISH_DATE,
					DownloadTable.COLUMN_VERSION,
					DownloadTable.COLUMN_VERSION_INT,
					DownloadTable.COLUMN_SIGN, DownloadTable.COLUMN_SIZE,
					DownloadTable.COLUMN_DOWNLOAD_URL,
					DownloadTable.COLUMN_ICON_URL, DownloadTable.COLUMN_EXTRA,
					DownloadTable.COLUMN_DOWNLOAD_DATE,
					DownloadTable.COLUMN_DOWNLOAD_ID,
					DownloadTable.COLUMN_IS_DELETED,
					DownloadTable.COLUMN_INSTALL_STATUS,
					DownloadTable.COLUMN_INSTALL_ERROR_REASON,
					DownloadTable.COLUMN_NEED_LOGIN,
					DownloadTable.COLUMN_IS_DIFFUPDATE,
					DownloadTable.COLUMN_FILEMD5

			};
			StringBuffer selection = new StringBuffer();
			;
			String selectionArgs[] = new String[selectionSize];
			if (includeDeleted) {
				// 包含删除的和未删除的
				if (downloadUrl != null) {
					selection = selection.append("("
							+ DownloadTable.COLUMN_DOWNLOAD_URL + "=? ");
					selectionArgs[0] = downloadUrl;
				}
				if (gameId != null) {
					selection.append((selection.length() > 0) ? " OR " : "(");
					selection.append(DownloadTable.COLUMN_GAME_ID + " =? )");
					selectionArgs[1] = gameId;
				}

			} else {
				// 不包含删除的
				if (downloadUrl != null) {
					selection = selection.append("("
							+ DownloadTable.COLUMN_DOWNLOAD_URL + "=? ");
					selectionArgs[0] = downloadUrl;
				}
				if (gameId != null) {
					selection.append((selection.length() > 0) ? " OR " : "(");
					selection.append(DownloadTable.COLUMN_GAME_ID + "=? )");
					selectionArgs[1] = gameId;
				}
				selection.append(" AND ");
				selection.append(DownloadTable.COLUMN_IS_DELETED + " = 0 ");
			}
			resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
					selection.toString(), selectionArgs, null, null,
					DownloadTable.COLUMN_DOWNLOAD_DATE + " DESC");
			if (resultCursor.getCount() > 1) {
				// throw new RuntimeException("Error");
			} else if (resultCursor.getCount() == 0) {
				return null;
			}
			resultCursor.moveToFirst();
			return extractDownloadAppInfo(resultCursor);
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	@Override
	public DownloadAppInfo getDownloadGameForId(String gameId,
			boolean includeDeleted) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		Cursor resultCursor = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			String[] columns = new String[] { DownloadTable.COLUMN_PKG_NAME,
					DownloadTable.COLUMN_GAME_ID, DownloadTable.COLUMN_NAME,
					DownloadTable.COLUMN_NAME_PINYIN,
					DownloadTable.COLUMN_PUBLISH_DATE,
					DownloadTable.COLUMN_VERSION,
					DownloadTable.COLUMN_VERSION_INT,
					DownloadTable.COLUMN_SIGN, DownloadTable.COLUMN_SIZE,
					DownloadTable.COLUMN_DOWNLOAD_URL,
					DownloadTable.COLUMN_ICON_URL, DownloadTable.COLUMN_EXTRA,
					DownloadTable.COLUMN_DOWNLOAD_DATE,
					DownloadTable.COLUMN_DOWNLOAD_ID,
					DownloadTable.COLUMN_IS_DELETED,
					DownloadTable.COLUMN_INSTALL_STATUS,
					DownloadTable.COLUMN_INSTALL_ERROR_REASON,
					DownloadTable.COLUMN_NEED_LOGIN,
					DownloadTable.COLUMN_IS_DIFFUPDATE,
					DownloadTable.COLUMN_FILEMD5 };

			String selection = null;
			String selectionArgs[] = null;
			if (includeDeleted) {
				// 包含删除的和未删除的
				selection = DownloadTable.COLUMN_GAME_ID + "=? ";
				selectionArgs = new String[] { gameId };
			} else {
				// 不包含删除的
				selection = DownloadTable.COLUMN_GAME_ID + "=? AND "
						+ DownloadTable.COLUMN_IS_DELETED + " = 0 ";
				;
				selectionArgs = new String[] { gameId };
			}
			resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
					selection, selectionArgs, null, null,
					DownloadTable.COLUMN_DOWNLOAD_DATE + " DESC");
			if (resultCursor.getCount() > 1) {
				// throw new RuntimeException("Error");
			} else if (resultCursor.getCount() == 0) {
				return null;
			}
			resultCursor.moveToFirst();
			return extractDownloadAppInfo(resultCursor);
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	private DownloadAppInfo extractDownloadAppInfo(Cursor resultCursor) {

		int indexPkg = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_PKG_NAME);
		int indexName = resultCursor.getColumnIndex(DownloadTable.COLUMN_NAME);
		int indexPinyin = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_NAME_PINYIN);
		int indexDate = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_PUBLISH_DATE);
		int indexVersion = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_VERSION);
		int indexVersionInt = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_VERSION_INT);
		int indexSign = resultCursor.getColumnIndex(DownloadTable.COLUMN_SIGN);
		int indexSize = resultCursor.getColumnIndex(DownloadTable.COLUMN_SIZE);
		int indexIconUrl = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_ICON_URL);
		int indexExtra = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_EXTRA);

		int indexDownloadDate = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_DATE);
		int indexDownloadId = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_ID);
		int indexDownloadUrl = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_URL);
		int indexGameId = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_GAME_ID);
		int indexIsDeleted = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_IS_DELETED);
		int indexInstallStatus = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_INSTALL_STATUS);
		int indexInstallError = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_INSTALL_ERROR_REASON);
		int indexNeedLogin = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_NEED_LOGIN);

		int indexIsDiffUpdate = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_IS_DIFFUPDATE);
		int indexFileMd5 = resultCursor
				.getColumnIndex(DownloadTable.COLUMN_FILEMD5);

		String packageName = resultCursor.getString(indexPkg);
		String name = resultCursor.getString(indexName);
		String pinyin = resultCursor.getString(indexPinyin);
		long date = resultCursor.getLong(indexDate);
		String version = resultCursor.getString(indexVersion);
		int versionInt = resultCursor.getInt(indexVersionInt);
		String extra = resultCursor.getString(indexExtra);
		String sign = resultCursor.getString(indexSign);
		String iconUrl = resultCursor.getString(indexIconUrl);
		long size = resultCursor.getLong(indexSize);

		long downloadDate = resultCursor.getLong(indexDownloadDate);
		long downloadId = resultCursor.getLong(indexDownloadId);
		String downloadUrl = resultCursor.getString(indexDownloadUrl);
		String gameId = resultCursor.getString(indexGameId);

		int isDeleted = resultCursor.getInt(indexIsDeleted);
		int installStatusInt = resultCursor.getInt(indexInstallStatus);
		int installError = resultCursor.getInt(indexInstallError);
		boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);

		boolean isDiffUpdate = false;
		if (indexIsDiffUpdate != -1) {
			isDiffUpdate = (resultCursor.getInt(indexIsDiffUpdate) == 1);
		}
		String fileMd5 = null;
		if (indexFileMd5 != -1) {
			fileMd5 = resultCursor.getString(indexFileMd5);
		}

		DownloadAppInfo info = new DownloadAppInfo(packageName, name, version,
				versionInt, date, extra, needLogin, pinyin, sign, size,
				downloadId, downloadUrl, iconUrl, downloadDate, gameId,
				isDiffUpdate, fileMd5);
		info.setMarkDeleted((isDeleted == 1));
		info.setInstalleStatus(InstallStatus.parse(installStatusInt));
		info.setInstallErrorReason(installError);
		return info;
	}

	/**
	 * 
	 */
	/*
	 * @Override public void updateInstalledApp(InstalledAppInfo app) {
	 * 
	 * 
	 * }
	 */

	@Override
	public InstalledAppInfo removeInstalledApp(String packageName) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}
			InstalledAppInfo installedApp = getInstalledApp(packageName);
			int id = db.delete(TABLE_INSALLED_APP_LIST,
					InstalledTable.COLUMN_PKG_NAME + "= ?",
					new String[] { packageName });
			db.delete(TABLE_MY_INSTALLED_LIST, MyInstalledTable.COLUMN_PKG_NAME
					+ "=?", new String[] { packageName });
			db.setTransactionSuccessful();
			return installedApp;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.endTransaction();
			}
		}
		return null;
	}

	@Override
	public void removeInstalledApps() {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}
			int id = db.delete(TABLE_INSALLED_APP_LIST, null, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.endTransaction();
			}
		}
	}

	private List queryInstalledOrUpdatableList(SQLiteDatabase db, boolean all) {
		StringBuffer rawSql = new StringBuffer("SELECT "
				+ TABLE_INSALLED_APP_LIST + "."
				+ InstalledTable.COLUMN_PKG_NAME + " AS " + COLUMN_PKG_NAME
				+ "," + InstalledTable.COLUMN_NAME + ","
				+ InstalledTable.COLUMN_NAME_PINYIN + ","
				+ InstalledTable.COLUMN_INSTALLED_DATE + ","
				+ InstalledTable.COLUMN_VERSION + ","
				+ InstalledTable.COLUMN_VERSION_INT + ","
				+ InstalledTable.COLUMN_SIGN + "," + InstalledTable.COLUMN_SIZE
				+ "," + TABLE_INSALLED_APP_LIST + "."
				+ InstalledTable.COLUMN_GAME_ID + " AS game_id1,"
				+ TABLE_INSALLED_APP_LIST + "." + InstalledTable.COLUMN_EXTRA
				+ " AS " + " extra1 ," + TABLE_INSALLED_APP_LIST + "."
				+ InstalledTable.COLUMN_NEED_LOGIN + " AS " + " need_login1 ");
		if (!all) {
			rawSql.append("," + UpdateTable.COLUMN_NEW_VERSION + ","
					+ UpdateTable.COLUMN_NEW_VERSION_INT + ","
					+ UpdateTable.COLUMN_DOWNLOAD_URL + ","
					+ UpdateTable.COLUMN_PUBLISH_DATE + ","
					+ UpdateTable.COLUMN_NEW_SIZE + ","
					+ UpdateTable.COLUMN_IGNORE_STATE + ","
					+ TABLE_UPDATABLE_APP_LIST + "."
					+ UpdateTable.COLUMN_GAME_ID + " AS game_id2,"
					+ UpdateTable.COLUMN_ICON_URL + ","
					+ UpdateTable.COLUMN_SERVER_SIGN + ","
					+ TABLE_UPDATABLE_APP_LIST + "." + UpdateTable.COLUMN_EXTRA
					+ " AS " + " extra2," + TABLE_UPDATABLE_APP_LIST + "."
					+ UpdateTable.COLUMN_NEED_LOGIN + " AS " + " need_login2 ,"
					+ UpdateTable.COLUMN_IS_DIFFUPDATE + " ,"
					+ UpdateTable.COLUMN_PATCH_URL + " ,"
					+ UpdateTable.COLUMN_PATCH_SIZE + " ");
			// 不用判断签名
			rawSql.append(" FROM " + TABLE_INSALLED_APP_LIST + ","
					+ TABLE_UPDATABLE_APP_LIST + " WHERE  " + COLUMN_IS_GAME
					+ "=1 AND " + TABLE_INSALLED_APP_LIST + "."
					+ COLUMN_PKG_NAME + "=" + TABLE_UPDATABLE_APP_LIST + "."
					+ COLUMN_PKG_NAME + " AND " + TABLE_INSALLED_APP_LIST + "."
					+ COLUMN_VERSION_INT + "<" + TABLE_UPDATABLE_APP_LIST + "."
					+ COLUMN_NEW_VERSION_INT);
			// rawSql.append(" ORDER BY "+ TABLE_INSALLED_APP_LIST
			// +"."+COLUMN_NAME_PINYIN+ " ASC ");

		} else {
			rawSql.append("," + InstalledTable.COLUMN_FILEMD5);
			rawSql.append("," + InstalledTable.COLUMN_UID);
			// 不用判断签名
			rawSql.append(" FROM " + TABLE_INSALLED_APP_LIST + " WHERE  "
					+ COLUMN_IS_GAME + "=1");
		}
		rawSql.append(" ORDER BY " + COLUMN_NAME_PINYIN + " ASC ");

		Cursor resultCursor = null;
		try {
			resultCursor = db.rawQuery(rawSql.toString(), null);
			int indexPkg = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME);
			int indexPinyin = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME_PINYIN);
			int indexDate = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_INSTALLED_DATE);
			int indexVersion = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION);
			int indexVersionInt = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION_INT);
			int indexSign = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIGN);
			int indexExtra = -1;
			int indexNeedLogin = -1;

			int indexGameId = -1;
			int indexIsDiffUpdate = -1;
			int indexFileMd5 = -1;
			int indexPatchUrl = -1;
			int indexPatchSize = -1;
			int indexUid = -1;
			if (all) {
				indexExtra = resultCursor.getColumnIndex("extra1");
				indexNeedLogin = resultCursor.getColumnIndex("need_login1");
				indexGameId = resultCursor.getColumnIndex("game_id1");
				indexFileMd5 = resultCursor
						.getColumnIndex(InstalledTable.COLUMN_FILEMD5);
				indexUid = resultCursor
						.getColumnIndex(InstalledTable.COLUMN_UID);
			} else {
				indexExtra = resultCursor.getColumnIndex("extra2");
				indexNeedLogin = resultCursor.getColumnIndex("need_login2");
				indexGameId = resultCursor.getColumnIndex("game_id2");
				indexIsDiffUpdate = resultCursor
						.getColumnIndex(UpdateTable.COLUMN_IS_DIFFUPDATE);
				indexPatchUrl = resultCursor
						.getColumnIndex(UpdateTable.COLUMN_PATCH_URL);
				indexPatchSize = resultCursor
						.getColumnIndex(UpdateTable.COLUMN_PATCH_SIZE);
			}
			int indexSize = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIZE);
			int indexNewVersion = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_NEW_VERSION);
			int indexNewVersionInt = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_NEW_VERSION_INT);
			int indexDownloadUrl = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_DOWNLOAD_URL);
			int indexUpdateDate = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_PUBLISH_DATE);
			int indexNewSize = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_NEW_SIZE);
			int indexSIgnoreUpdate = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_IGNORE_STATE);
			int indexServersign = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_SERVER_SIGN);

			int indexIconUrl = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_ICON_URL);

			int count = resultCursor.getCount();
			if (count == 0) {
				return new ArrayList();
			}
			List apps = new ArrayList(count);
			while (resultCursor.moveToNext()) {
				String packageName = resultCursor.getString(indexPkg);
				String name = resultCursor.getString(indexName);
				String pinyin = resultCursor.getString(indexPinyin);
				long date = resultCursor.getLong(indexDate);
				String version = resultCursor.getString(indexVersion);
				int versionInt = resultCursor.getInt(indexVersionInt);
				String extra = resultCursor.getString(indexExtra);
				String sign = resultCursor.getString(indexSign);
				long size = resultCursor.getLong(indexSize);
				boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);
				String gameId = resultCursor.getString(indexGameId);

				if (!all) {
					String newVersion = resultCursor.getString(indexNewVersion);
					int newVersionInt = resultCursor.getInt(indexNewVersionInt);
					String downloadUrl = resultCursor
							.getString(indexDownloadUrl);
					long updateDate = resultCursor.getLong(indexUpdateDate);
					long newSize = resultCursor.getLong(indexNewSize);
					boolean ignoreUpdate = (resultCursor
							.getInt(indexSIgnoreUpdate) == 1);
					String serverSign = resultCursor.getString(indexServersign);

					String iconUrl = resultCursor.getString(indexIconUrl);
					boolean isDiffUpdate = false;
					if (indexIsDiffUpdate != -1) {
						isDiffUpdate = (resultCursor.getInt(indexIsDiffUpdate) == 1);
					}
					long patchSize = resultCursor.getLong(indexPatchSize);
					;
					String patchUrl = resultCursor.getString(indexPatchUrl);
					;

					apps.add(new UpdatableAppInfo(packageName, name, version,
							versionInt, date, extra, needLogin, pinyin, sign,
							size, newVersion, newVersionInt, downloadUrl,
							updateDate, newSize, ignoreUpdate, serverSign,
							gameId, iconUrl, isDiffUpdate, patchUrl, patchSize));
				} else {
					String fileMd5 = null;
					if (indexFileMd5 != -1) {
						fileMd5 = resultCursor.getString(indexFileMd5);
					}
					int uid = -1;
					if (indexUid != -1) {
						uid = resultCursor.getInt(indexUid);
					}
					apps.add(new InstalledAppInfo(packageName, name, version,
							versionInt, date, extra, needLogin, pinyin, sign,
							size, gameId, true, fileMd5, uid));
				}

			}
			return apps;
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	private List queryInstalledApps(SQLiteDatabase db, List<String> packageNames) {

		// StringBuffer rawSql = new StringBuffer("SELECT "+
		// InstalledTable.COLUMN_PKG_NAME +","+
		// InstalledTable.COLUMN_NAME +","+
		// InstalledTable.COLUMN_NAME_PINYIN +","+
		// InstalledTable.COLUMN_INSTALLED_DATE+","+
		// InstalledTable.COLUMN_VERSION +","+
		// InstalledTable.COLUMN_VERSION_INT +","+
		// InstalledTable.COLUMN_SIGN +","+
		// InstalledTable.COLUMN_SIZE +","+
		// InstalledTable.COLUMN_NEED_LOGIN +","+
		// InstalledTable.COLUMN_GAME_ID +","+
		// InstalledTable.COLUMN_EXTRA +" "
		// );
		String[] col = { InstalledTable.COLUMN_PKG_NAME,
				InstalledTable.COLUMN_NAME, InstalledTable.COLUMN_NAME_PINYIN,
				InstalledTable.COLUMN_INSTALLED_DATE,
				InstalledTable.COLUMN_VERSION,
				InstalledTable.COLUMN_VERSION_INT, InstalledTable.COLUMN_SIGN,
				InstalledTable.COLUMN_SIZE, InstalledTable.COLUMN_NEED_LOGIN,
				InstalledTable.COLUMN_GAME_ID, InstalledTable.COLUMN_EXTRA,
				InstalledTable.COLUMN_IS_GAME, InstalledTable.COLUMN_FILEMD5,
				InstalledTable.COLUMN_UID };
		StringBuffer selection = null;
		String[] selectionArgs = null;
		// 不用判断签名
		if (packageNames == null || packageNames.size() == 0) {
			// rawSql.append(" FROM "+TABLE_INSALLED_APP_LIST);
			// rawSql.append(" ORDER BY "+ InstalledTable.COLUMN_NAME_PINYIN+
			// " ASC ");
		} else {
			// rawSql.append(" FROM "+TABLE_INSALLED_APP_LIST);
			selection = new StringBuffer();
			selection.append(InstalledTable.COLUMN_PKG_NAME + " IN (");
			int size = packageNames.size();
			selectionArgs = new String[size];
			for (int i = 0; i < size; i++) {
				if (i == size - 1) {
					selection.append("?)");
				} else {
					selection.append("?,");
				}
				selectionArgs[i] = packageNames.get(i);
			}
		}

		Cursor resultCursor = null;
		try {
			if (packageNames == null || packageNames.size() == 0) {
				resultCursor = db.query(TABLE_INSALLED_APP_LIST, col, null,
						null, null, null, InstalledTable.COLUMN_NAME_PINYIN
								+ " ASC", null);
			} else {
				resultCursor = db.query(TABLE_INSALLED_APP_LIST, col,
						selection.toString(), selectionArgs, null, null,
						InstalledTable.COLUMN_NAME_PINYIN + " ASC", null);
			}
			// resultCursor = db.rawQuery(rawSql.toString(), null);
			int indexPkg = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME);
			int indexPinyin = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME_PINYIN);
			int indexDate = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_INSTALLED_DATE);
			int indexVersion = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION);
			int indexVersionInt = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION_INT);
			int indexSign = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIGN);
			int indexExtra = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_EXTRA);
			int indexSize = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIZE);
			int indexNeedLogin = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NEED_LOGIN);
			int indexGameId = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_GAME_ID);
			int indexIsGame = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_IS_GAME);
			int indexFileMd5 = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_FILEMD5);
			int indexUid = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_UID);

			int count = resultCursor.getCount();
			if (count == 0) {
				return new ArrayList();
			}
			List apps = new ArrayList(count);
			while (resultCursor.moveToNext()) {
				String packageName = resultCursor.getString(indexPkg);
				String name = resultCursor.getString(indexName);
				String pinyin = resultCursor.getString(indexPinyin);
				long date = resultCursor.getLong(indexDate);
				String version = resultCursor.getString(indexVersion);
				int versionInt = resultCursor.getInt(indexVersionInt);
				String extra = resultCursor.getString(indexExtra);
				String sign = resultCursor.getString(indexSign);
				String gameId = resultCursor.getString(indexGameId);
				long size = resultCursor.getLong(indexSize);
				boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);
				boolean isGame = (resultCursor.getInt(indexIsGame) == 1);
				String fileMd5 = null;
				if (indexFileMd5 != -1) {
					fileMd5 = resultCursor.getString(indexFileMd5);
				}
				int uid = -1;
				if (indexUid != -1) {
					uid = resultCursor.getInt(indexUid);
				}
				apps.add(new InstalledAppInfo(packageName, name, version,
						versionInt, date, extra, needLogin, pinyin, sign, size,
						gameId, isGame, fileMd5, uid));

			}
			return apps;
		} catch (Exception e) {
			// throw new RuntimeException(e);
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	private InstalledAppInfo queryInstalleApp(SQLiteDatabase db,
			String packageName) {
		StringBuffer rawSql = new StringBuffer("SELECT "
				+ TABLE_INSALLED_APP_LIST + "." + COLUMN_PKG_NAME + " AS "
				+ COLUMN_PKG_NAME + "," + InstalledTable.COLUMN_NAME + ","
				+ InstalledTable.COLUMN_NAME_PINYIN + ","
				+ InstalledTable.COLUMN_INSTALLED_DATE + ","
				+ InstalledTable.COLUMN_VERSION + ","
				+ InstalledTable.COLUMN_VERSION_INT + ","
				+ InstalledTable.COLUMN_SIGN + "," + InstalledTable.COLUMN_SIZE
				+ ","
				+ InstalledTable.COLUMN_GAME_ID
				+ " AS game_id1,"
				+
				// TABLE_INSALLED_APP_LIST+"."+COLUMN_EXTRA
				// +" AS "+COLUMN_EXTRA);
				InstalledTable.COLUMN_EXTRA + " AS " + " extra1, "
				+ InstalledTable.COLUMN_NEED_LOGIN + " AS " + " need_login1 ,"
				+ InstalledTable.COLUMN_FILEMD5 + ","
				+ InstalledTable.COLUMN_UID + "");
		// 不用判断签名
		rawSql.append(" FROM " + TABLE_INSALLED_APP_LIST + " WHERE  "
				+ InstalledTable.COLUMN_PKG_NAME + "='" + packageName + "'");

		Cursor resultCursor = null;
		try {
			resultCursor = db.rawQuery(rawSql.toString(), null);
			int indexPkg = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME);
			int indexPinyin = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME_PINYIN);
			int indexDate = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_INSTALLED_DATE);
			int indexVersion = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION);
			int indexVersionInt = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION_INT);
			int indexSign = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIGN);
			int indexFileMd5 = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_FILEMD5);
			int indexUid = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_UID);

			int indexExtra = -1;
			int indexNeedLogin = -1;
			int indexGameId = -1;
			indexExtra = resultCursor.getColumnIndex("extra1");
			indexNeedLogin = resultCursor.getColumnIndex("need_login1");
			indexGameId = resultCursor.getColumnIndex("game_id1");

			int indexSize = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIZE);

			int count = resultCursor.getCount();
			if (count == 0) {
				return null;
			} else if (count > 1) {
				// throw new RuntimeException("Error");
			}
			InstalledAppInfo ret;
			resultCursor.moveToFirst();
			String name = resultCursor.getString(indexName);
			String pinyin = resultCursor.getString(indexPinyin);
			long date = resultCursor.getLong(indexDate);
			String version = resultCursor.getString(indexVersion);
			int versionInt = resultCursor.getInt(indexVersionInt);
			String extra = resultCursor.getString(indexExtra);
			String sign = resultCursor.getString(indexSign);
			long size = resultCursor.getLong(indexSize);
			boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);
			String gameId = resultCursor.getString(indexGameId);
			String fileMd5 = null;
			if (indexFileMd5 != -1) {
				fileMd5 = resultCursor.getString(indexFileMd5);
			}
			int uid = -1;
			if (indexUid != -1) {
				uid = resultCursor.getInt(indexUid);
			}
			ret = new InstalledAppInfo(packageName, name, version, versionInt,
					date, extra, needLogin, pinyin, sign, size, gameId, true,
					fileMd5, uid);

			return ret;
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	private Object queryInstalledOrUpdatableApp(SQLiteDatabase db,
			String packageName, boolean all) {
		StringBuffer rawSql = new StringBuffer("SELECT "
				+ TABLE_INSALLED_APP_LIST + "." + COLUMN_PKG_NAME + " AS "
				+ COLUMN_PKG_NAME + "," + InstalledTable.COLUMN_NAME + ","
				+ InstalledTable.COLUMN_NAME_PINYIN + ","
				+ InstalledTable.COLUMN_INSTALLED_DATE + ","
				+ InstalledTable.COLUMN_VERSION + ","
				+ InstalledTable.COLUMN_VERSION_INT + ","
				+ InstalledTable.COLUMN_SIGN + "," + InstalledTable.COLUMN_SIZE
				+ "," + TABLE_INSALLED_APP_LIST + "."
				+ InstalledTable.COLUMN_GAME_ID
				+ " AS game_id1,"
				+
				// TABLE_INSALLED_APP_LIST+"."+COLUMN_EXTRA
				// +" AS "+COLUMN_EXTRA);
				TABLE_INSALLED_APP_LIST + "." + InstalledTable.COLUMN_EXTRA
				+ " AS " + " extra1, " + TABLE_INSALLED_APP_LIST + "."
				+ InstalledTable.COLUMN_NEED_LOGIN + " AS " + " need_login1 ");
		if (!all) {
			rawSql.append("," + UpdateTable.COLUMN_NEW_VERSION + ","
					+ UpdateTable.COLUMN_NEW_VERSION_INT + ","
					+ UpdateTable.COLUMN_DOWNLOAD_URL + ","
					+ UpdateTable.COLUMN_PUBLISH_DATE + ","
					+ UpdateTable.COLUMN_NEW_SIZE + ","
					+ UpdateTable.COLUMN_IGNORE_STATE + ","
					+ TABLE_UPDATABLE_APP_LIST + "."
					+ UpdateTable.COLUMN_GAME_ID + " AS game_id2,"
					+ UpdateTable.COLUMN_ICON_URL + ","
					+ UpdateTable.COLUMN_SERVER_SIGN + ","
					+ TABLE_UPDATABLE_APP_LIST + "." + UpdateTable.COLUMN_EXTRA
					+ " AS " + " extra2 ," + TABLE_UPDATABLE_APP_LIST + "."
					+ UpdateTable.COLUMN_NEED_LOGIN + " AS " + " need_login2 ,"
					+ UpdateTable.COLUMN_IS_DIFFUPDATE + ","
					+ UpdateTable.COLUMN_PATCH_URL + ","
					+ UpdateTable.COLUMN_PATCH_SIZE);
			// 不用判断签名
			rawSql.append(" FROM " + TABLE_INSALLED_APP_LIST + ","
					+ TABLE_UPDATABLE_APP_LIST + " WHERE  "
					+ InstalledTable.COLUMN_IS_GAME + "=1 AND "
					+ TABLE_INSALLED_APP_LIST + "."
					+ InstalledTable.COLUMN_PKG_NAME + "='" + packageName
					+ "' AND " + TABLE_INSALLED_APP_LIST + "."
					+ COLUMN_PKG_NAME + "=" + TABLE_UPDATABLE_APP_LIST + "."
					+ COLUMN_PKG_NAME + " AND " + TABLE_INSALLED_APP_LIST + "."
					+ COLUMN_VERSION_INT + "<" + TABLE_UPDATABLE_APP_LIST + "."
					+ COLUMN_NEW_VERSION_INT);
			// rawSql.append(" ORDER BY "+ TABLE_INSALLED_APP_LIST
			// +"."+COLUMN_NAME_PINYIN+ " ASC ");

		} else {
			rawSql.append("," + InstalledTable.COLUMN_FILEMD5);
			rawSql.append("," + InstalledTable.COLUMN_UID);
			// 不用判断签名
			rawSql.append(" FROM " + TABLE_INSALLED_APP_LIST + " WHERE  "
					+ InstalledTable.COLUMN_IS_GAME + "=1 AND "
					+ InstalledTable.COLUMN_PKG_NAME + "='" + packageName + "'");
		}
		rawSql.append(" ORDER BY " + COLUMN_NAME_PINYIN + " ASC ");

		Cursor resultCursor = null;
		try {
			resultCursor = db.rawQuery(rawSql.toString(), null);
			int indexPkg = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME);
			int indexPinyin = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_NAME_PINYIN);
			int indexDate = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_INSTALLED_DATE);
			int indexVersion = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION);
			int indexVersionInt = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_VERSION_INT);
			int indexSign = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIGN);
			int indexExtra = -1;
			int indexNeedLogin = -1;
			int indexGameId = -1;
			int indexIsDiffUpdate = -1;
			int indexFileMd5 = -1;

			int indexPatchUrl = -1;
			int indexPatchSize = -1;
			int indexUid = -1;
			if (all) {
				indexExtra = resultCursor.getColumnIndex("extra1");
				indexNeedLogin = resultCursor.getColumnIndex("need_login1");
				indexGameId = resultCursor.getColumnIndex("game_id1");
				indexFileMd5 = resultCursor
						.getColumnIndex(InstalledTable.COLUMN_FILEMD5);
				indexUid = resultCursor
						.getColumnIndex(InstalledTable.COLUMN_UID);
			} else {
				indexExtra = resultCursor.getColumnIndex("extra2");
				indexNeedLogin = resultCursor.getColumnIndex("need_login2");
				indexGameId = resultCursor.getColumnIndex("game_id2");
				indexIsDiffUpdate = resultCursor
						.getColumnIndex(UpdateTable.COLUMN_IS_DIFFUPDATE);
				indexPatchUrl = resultCursor
						.getColumnIndex(UpdateTable.COLUMN_PATCH_URL);
				indexPatchSize = resultCursor
						.getColumnIndex(UpdateTable.COLUMN_PATCH_SIZE);
			}
			int indexSize = resultCursor
					.getColumnIndex(InstalledTable.COLUMN_SIZE);

			int indexNewVersion = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_NEW_VERSION);
			int indexNewVersionInt = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_NEW_VERSION_INT);
			int indexDownloadUrl = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_DOWNLOAD_URL);
			int indexUpdateDate = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_PUBLISH_DATE);
			int indexNewSize = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_NEW_SIZE);
			int indexSIgnoreUpdate = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_IGNORE_STATE);
			int indexServersign = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_SERVER_SIGN);

			int indexIconUrl = resultCursor
					.getColumnIndex(UpdateTable.COLUMN_ICON_URL);
			int count = resultCursor.getCount();
			if (count == 0) {
				return null;
			} else if (count > 1) {
				// throw new RuntimeException("Error");
			}
			Object ret;
			resultCursor.moveToFirst();
			String p = resultCursor.getString(indexPkg);
			String name = resultCursor.getString(indexName);
			String pinyin = resultCursor.getString(indexPinyin);
			long date = resultCursor.getLong(indexDate);
			String version = resultCursor.getString(indexVersion);
			int versionInt = resultCursor.getInt(indexVersionInt);
			String extra = resultCursor.getString(indexExtra);
			String sign = resultCursor.getString(indexSign);
			long size = resultCursor.getLong(indexSize);
			boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);
			String gameId = resultCursor.getString(indexGameId);
			if (!all) {
				String newVersion = resultCursor.getString(indexNewVersion);
				int newVersionInt = resultCursor.getInt(indexNewVersionInt);
				String downloadUrl = resultCursor.getString(indexDownloadUrl);
				long updateDate = resultCursor.getLong(indexUpdateDate);
				long newSize = resultCursor.getLong(indexNewSize);
				boolean ignoreUpdate = (resultCursor.getInt(indexSIgnoreUpdate) == 1);
				String serverSign = resultCursor.getString(indexServersign);

				String iconUrl = resultCursor.getString(indexIconUrl);
				boolean isDiffUpdate = false;
				if (indexIsDiffUpdate != -1) {
					isDiffUpdate = (1 == resultCursor.getInt(indexIsDiffUpdate));
				}
				String patchUrl = resultCursor.getString(indexPatchUrl);
				long patchSize = resultCursor.getLong(indexPatchSize);

				ret = new UpdatableAppInfo(packageName, name, version,
						versionInt, date, extra, needLogin, pinyin, sign, size,
						newVersion, newVersionInt, downloadUrl, updateDate,
						newSize, ignoreUpdate, serverSign, gameId, iconUrl,
						isDiffUpdate, patchUrl, patchSize);
			} else {
				String fileMd5 = null;
				if (indexFileMd5 != -1) {
					fileMd5 = resultCursor.getString(indexFileMd5);
				}
				int uid = -1;
				if (indexUid != -1) {
					uid = resultCursor.getInt(indexUid);
				}
				ret = new InstalledAppInfo(packageName, name, version,
						versionInt, date, extra, needLogin, pinyin, sign, size,
						gameId, true, fileMd5, uid);
			}

			return ret;
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;
	}

	/**
	 * + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PKG_NAME +
	 * " TEXT UNIQUE NOT NULL ," //app attribute 1 + COLUMN_NAME + " TEXT,"
	 * //app attribute 2 + COLUMN_NAME_PINYIN + " TEXT," //app attribute 3 +
	 * COLUMN_VERSION + " TEXT," //app attribute 4 + COLUMN_VERSION_INT +
	 * " INTEGER," //app attribute 5 + COLUMN_ICON_URL + " TEXT," //app
	 * attribute 6 + COLUMN_PUBLISH_DATE + " INTEGER," //app attribute 7 +
	 * COLUMN_SIGN + " TEXT," //app attribute 8 + COLUMN_SIZE + " INTEGER,"
	 * //app attribute 9 + COLUMN_EXTRA + " TEXT," //app attribute 10
	 * 
	 * + COLUMN_DOWNLOAD_DATE + " INTEGER," //file attribute +
	 * COLUMN_DOWNLOAD_ID + " INTEGER NOT NULL UNIQUE," //file attribute +
	 * COLUMN_DOWNLOAD_URL + " TEXT NOT NULL UNIQUE" //file attribute
	 * 
	 * @param db
	 * @return
	 */

	private List<DownloadAppInfo> queryDownloadList(SQLiteDatabase db,
			boolean includeDeleted) {
		String[] columns = new String[] { DownloadTable.COLUMN_PKG_NAME,
				DownloadTable.COLUMN_GAME_ID, DownloadTable.COLUMN_NAME,
				DownloadTable.COLUMN_NAME_PINYIN,
				DownloadTable.COLUMN_PUBLISH_DATE,
				DownloadTable.COLUMN_VERSION, DownloadTable.COLUMN_VERSION_INT,
				DownloadTable.COLUMN_SIGN, DownloadTable.COLUMN_ICON_URL,
				DownloadTable.COLUMN_EXTRA, DownloadTable.COLUMN_DOWNLOAD_DATE,
				DownloadTable.COLUMN_DOWNLOAD_ID,
				DownloadTable.COLUMN_DOWNLOAD_URL, DownloadTable.COLUMN_SIZE,
				DownloadTable.COLUMN_IS_DELETED,
				DownloadTable.COLUMN_INSTALL_STATUS,
				DownloadTable.COLUMN_INSTALL_ERROR_REASON,
				DownloadTable.COLUMN_NEED_LOGIN,
				DownloadTable.COLUMN_IS_DIFFUPDATE,
				DownloadTable.COLUMN_FILEMD5 };

		String selection = null;
		String selectionArgs[] = null;
		if (includeDeleted) {
			// 包含删除的和未删除的
		} else {
			// 不包含删除的
			selection = DownloadTable.COLUMN_IS_DELETED + " = 0 ";
			;
		}

		Cursor resultCursor = null;

		try {
			resultCursor = db.query(TABLE_DOWNLOAD_APP_LIST, columns,
					selection, selectionArgs, null, null,
					DownloadTable.COLUMN_DOWNLOAD_DATE + " DESC");
			int indexPkg = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_PKG_NAME);
			int indexName = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_NAME);
			int indexPinyin = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_NAME_PINYIN);
			int indexDate = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_PUBLISH_DATE);
			int indexVersion = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_VERSION);
			int indexVersionInt = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_VERSION_INT);
			int indexSign = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_SIGN);
			int indexSize = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_SIZE);
			int indexIconUrl = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_ICON_URL);
			int indexExtra = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_EXTRA);

			int indexDownloadDate = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_DATE);
			int indexDownloadId = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_ID);
			int indexDownloadUrl = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_DOWNLOAD_URL);
			int indexGameId = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_GAME_ID);
			int indexIsDeleted = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_IS_DELETED);
			int indexInstallStatus = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_INSTALL_STATUS);
			int indexInstallError = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_INSTALL_ERROR_REASON);
			int indexNeedLogin = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_NEED_LOGIN);

			int indexIsDiffUpdate = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_IS_DIFFUPDATE);
			int indexFileMd5 = resultCursor
					.getColumnIndex(DownloadTable.COLUMN_FILEMD5);

			// List<DownloadAppInfo> apps = new
			// ArrayList<DownloadAppInfo>(resultCursor.getCount());
			// List<DownloadAppInfo> apps = new LinkedList<DownloadAppInfo>();
			List<DownloadAppInfo> apps = new ArrayList<DownloadAppInfo>(
					resultCursor.getCount());
			while (resultCursor.moveToNext()) {
				String packageName = resultCursor.getString(indexPkg);
				String name = resultCursor.getString(indexName);
				String pinyin = resultCursor.getString(indexPinyin);
				long date = resultCursor.getLong(indexDate);
				String version = resultCursor.getString(indexVersion);
				int versionInt = resultCursor.getInt(indexVersionInt);
				String extra = resultCursor.getString(indexExtra);
				String sign = resultCursor.getString(indexSign);
				String iconUrl = resultCursor.getString(indexIconUrl);
				long size = resultCursor.getLong(indexSize);

				long downloadDate = resultCursor.getLong(indexDownloadDate);
				long downloadId = resultCursor.getLong(indexDownloadId);
				String downloadUrl = resultCursor.getString(indexDownloadUrl);
				String gameId = resultCursor.getString(indexGameId);

				int isDeleted = resultCursor.getInt(indexIsDeleted);
				int installStatusInt = resultCursor.getInt(indexInstallStatus);
				int installErrorReason = resultCursor.getInt(indexInstallError);

				boolean needLogin = (resultCursor.getInt(indexNeedLogin) == 1);

				boolean isDiffUpdate = false;
				if (indexIsDiffUpdate != -1) {
					isDiffUpdate = (resultCursor.getInt(indexIsDiffUpdate) == 1);
				}

				String fileMd5 = null;
				if (indexFileMd5 != -1) {
					fileMd5 = (resultCursor.getString(indexFileMd5));
				}

				DownloadAppInfo info = new DownloadAppInfo(packageName, name,
						version, versionInt, date, extra, needLogin, pinyin,
						sign, size, downloadId, downloadUrl, iconUrl,
						downloadDate, gameId, isDiffUpdate, fileMd5);

				info.setMarkDeleted((isDeleted == 1));
				info.setInstalleStatus(InstallStatus.parse(installStatusInt));
				info.setInstallErrorReason(installErrorReason);
				apps.add(info);

			}
			return apps;
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
		} finally {
			if (resultCursor != null && !resultCursor.isClosed()) {
				resultCursor.close();
			}
		}
		return null;

	}

	@Override
	public List<InstalledAppInfo> getAllInstalledGames() {
		// Log.i("wang", "getAllInstalledGames");
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			List ret = queryInstalledOrUpdatableList(db, true);
			// Log.i("wang",
			// "getAllInstalledGames "+ret+" db:"+db+" file exist?"+new
			// File("/data/data/com.duoku.gamesearch/databases/app.db").exists());
			// Log.i("wang", "getAllInstalledGames db isopen"+db.isOpen());
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<InstalledAppInfo> getAllInstalledApps() {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			return queryInstalledApps(db, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<InstalledAppInfo> queryInstalledApps(List<String> packageNames) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			return queryInstalledApps(db, packageNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getWhiteListCount() {
		int tableRecordCount = getTableRecordCount(TABLE_WHITE_LIST);
		return tableRecordCount;
	}

	@Override
	public int getUpdatableCount() {
		int tableRecordCount = getTableRecordCount(TABLE_UPDATABLE_APP_LIST);
		return tableRecordCount;
	}

	@Override
	public int getInstalledListCount() {
		int tableRecordCount = getTableRecordCount(TABLE_INSALLED_APP_LIST);
		return tableRecordCount;
	}

	public int getTableRecordCount(String table) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			Cursor query = db.query(table, new String[] { "count(*)" }, null,
					null, null, null, null);
			query.moveToFirst();
			return query.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// ////////////////////////////////////////////////////////////////////////////

	@Override
	public List<UpdatableAppInfo> getAllUpdatableGames() {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
			return queryInstalledOrUpdatableList(db, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param ids
	 */
	@Override
	public void updateInstalledGameIds(Map<String, String> ids) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				Set<String> keySet = ids.keySet();
				ContentValues cv = new ContentValues();
				for (String pkg : keySet) {
					String gameId = ids.get(pkg);
					if (gameId != null) {
						cv.put(InstalledTable.COLUMN_GAME_ID, gameId);
					}
					cv.put(InstalledTable.COLUMN_IS_GAME, 1);
					int update = db.update(TABLE_INSALLED_APP_LIST, cv,
							InstalledTable.COLUMN_PKG_NAME + "= ?",
							new String[] { pkg });
					if ("com.mas.wawagame.BDDKlord".equals(pkg)) {
						if (Constants.DEBUG)
							Log.i("wangliangtest",
									"[AppDaoImpl#updateInstalledGameIds]update com.mas.wawagame.BDDKlord:affect lines:"
											+ update + " gameId:" + gameId);
					}
					cv.clear();
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void updateUpdatableList(List<UpdatableItem> list) {
		AppStorgeSqliteHelper helper = AppStorgeSqliteHelper
				.getInstance(context);
		SQLiteDatabase db = null;

		try {
			db = helper.getWritableDatabase();

			HashMap<String, String> hashMap2 = new HashMap<String, String>();

			List<UpdatableAppInfo> allUpdatableGames = getAllUpdatableGames();
			HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
			if (allUpdatableGames != null) {
				for (UpdatableAppInfo info : allUpdatableGames) {
					hashMap.put(info.getPackageName(), info.isIgnoreUpdate());
				}
			}
			ContentValues cv = new ContentValues();
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					db.beginTransactionNonExclusive();
				} else {
					db.beginTransaction();
				}
				if (list.size() > 0) {
					db.delete(TABLE_UPDATABLE_APP_LIST, null, null);
				}
				for (UpdatableItem app : list) {
					hashMap2.put(app.getPackageName(), app.getGameId());
					/*
					 * if(!app.isUpdatable()){ continue; }
					 */
					cv.put(UpdateTable.COLUMN_PKG_NAME, app.getPackageName()); // 1
																				// package
																				// name
					cv.put(UpdateTable.COLUMN_GAME_ID, app.getGameId()); // 2
																			// game
																			// id
					cv.put(UpdateTable.COLUMN_NEW_VERSION, app.getNewVersion());// 3
																				// version
					cv.put(UpdateTable.COLUMN_NEW_VERSION_INT,
							app.getNewVersionInt());// 4 version int
					cv.put(UpdateTable.COLUMN_DOWNLOAD_URL,
							app.getDownloadUrl()); // 5 download url
					cv.put(UpdateTable.COLUMN_PUBLISH_DATE,
							app.getPublishDate());// 6 publish date,optional
					cv.put(UpdateTable.COLUMN_SERVER_SIGN, app.getServerSign()); // 7
																					// server
																					// sign
					cv.put(UpdateTable.COLUMN_NEW_SIZE, app.getNewSize()); // 8
																			// apk
																			// size
					cv.put(UpdateTable.COLUMN_ICON_URL, app.getIconUrl()); // 9
																			// icon
																			// url
					cv.put(UpdateTable.COLUMN_EXTRA, app.getExtra()); // 10
					cv.put(UpdateTable.COLUMN_NEED_LOGIN, app.isNeedLogin() ? 1
							: 0); // 11
					cv.put(UpdateTable.COLUMN_IS_DIFFUPDATE,
							app.isDiffUpdate() ? 1 : 0); // 12
					cv.put(UpdateTable.COLUMN_PATCH_URL, app.getPatchUrl()); // 13
					cv.put(UpdateTable.COLUMN_PATCH_SIZE, app.getPacthSize()); // 14
					Boolean ignored = hashMap.get(app.getPackageName());// 15
					cv.put(UpdateTable.COLUMN_IGNORE_STATE,
							(ignored == null) ? 0 : (ignored ? 1 : 0));// save
																		// ignored
																		// state
																		// 16
					db.replace(TABLE_UPDATABLE_APP_LIST, null, cv); // 10 是否忽略更新
					if (Constants.DEBUG)
						if (Constants.DEBUG)
							Log.i(TAG,
									"updateUpdatableList "
											+ cv.getAsString(COLUMN_PKG_NAME)
											+ ":"
											+ cv.getAsInteger(COLUMN_IGNORE_STATE));
					cv.clear();
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
			updateInstalledGameIds(hashMap2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {

			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////

	/*
	 * public List<InstalledAppInfo> getAllInstalledApps(){
	 * AppStorgeSqliteHelper helper =
	 * AppStorgeSqliteHelper.getInstance(context); SQLiteDatabase db = null ;
	 * Cursor resultCursor = null ; try { db = helper.getReadableDatabase();
	 * String rawSql = "SELECT "+ TABLE_INSALLED_APP_LIST+"."+COLUMN_PKG_NAME
	 * +" AS "+COLUMN_PKG_NAME +","+
	 * TABLE_INSALLED_APP_LIST+"."+COLUMN_NAME+" AS "+COLUMN_NAME +","+
	 * TABLE_INSALLED_APP_LIST+"."+COLUMN_DATE+" AS "+COLUMN_DATE +","+
	 * TABLE_INSALLED_APP_LIST+"."+COLUMN_VERSION+" AS "+COLUMN_VERSION +","+
	 * TABLE_INSALLED_APP_LIST+"."+COLUMN_VERSION_INT+" AS "+COLUMN_VERSION_INT
	 * +","+ TABLE_INSALLED_APP_LIST+"."+COLUMN_NAME_PINYIN
	 * +" AS "+COLUMN_NAME_PINYIN+","+ TABLE_WHITE_LIST+"."+COLUMN_VERSION
	 * +" AS new_version"+","+ TABLE_WHITE_LIST+"."+COLUMN_VERSION_INT
	 * +" AS new_version_int"+","+ TABLE_INSALLED_APP_LIST+"."+COLUMN_EXTRA
	 * +" AS "+COLUMN_EXTRA+ " FROM "+ TABLE_INSALLED_APP_LIST
	 * +","+TABLE_WHITE_LIST+ " WHERE "+
	 * TABLE_INSALLED_APP_LIST+"."+COLUMN_PKG_NAME
	 * +"="+TABLE_WHITE_LIST+"."+COLUMN_PKG_NAME+ " ORDER By "+
	 * TABLE_INSALLED_APP_LIST +"."+COLUMN_NAME_PINYIN+ " ASC ";
	 * 
	 * 
	 * resultCursor = db.rawQuery(rawSql, null); int indexPkg =
	 * resultCursor.getColumnIndex(COLUMN_PKG_NAME); int indexName =
	 * resultCursor.getColumnIndex(COLUMN_NAME); int indexDate =
	 * resultCursor.getColumnIndex(COLUMN_DATE); int indexVersion =
	 * resultCursor.getColumnIndex(COLUMN_VERSION); int indexVersionInt =
	 * resultCursor.getColumnIndex(COLUMN_VERSION_INT); int indexPinyin =
	 * resultCursor.getColumnIndex(COLUMN_NAME_PINYIN); int indexExtra =
	 * resultCursor.getColumnIndex(COLUMN_EXTRA); int indexNewVersion =
	 * resultCursor.getColumnIndex("new_version"); int indexNewVersionInt =
	 * resultCursor.getColumnIndex("new_version_int"); List<InstalledAppInfo>
	 * apps = new ArrayList<InstalledAppInfo>(resultCursor.getCount());
	 * while(resultCursor.moveToNext()){ String packageName =
	 * resultCursor.getString(indexPkg); String name =
	 * resultCursor.getString(indexName); long date =
	 * resultCursor.getLong(indexDate); String version =
	 * resultCursor.getString(indexVersion); int versionInt =
	 * resultCursor.getInt(indexVersionInt); String pinyin =
	 * resultCursor.getString(indexPinyin); String extra =
	 * resultCursor.getString(indexExtra); String newVersion =
	 * resultCursor.getString(indexNewVersion); int newVersionInt =
	 * resultCursor.getInt(indexNewVersionInt);
	 * 
	 * newVersion = TextUtils.isEmpty(newVersion)?version:newVersion;
	 * newVersionInt = (newVersionInt==0)?versionInt:newVersionInt; new
	 * InstalledAppInfo(packageName, name, newVersion, newVersionInt, date,
	 * extra, pinyinName) //apps.add(new InstalledAppInfo(packageName, name,
	 * version, versionInt, date, extra, pinyin,newVersion,newVersionInt));
	 * 
	 * } return apps ; } catch (Exception e) { Log.e(TAG,
	 * "getInstalledApp error:",e); }finally{ if(resultCursor != null){
	 * resultCursor.close(); } } return null ; }
	 */

	public static class AppStorgeSqliteHelper extends SQLiteOpenHelper {
		private Context context;
		private SQLiteDatabase database;
		static AppStorgeSqliteHelper instance;

		/**
		 * static final String COLUMN_ID = AppDaoImpl.COLUMN_ID ; //1 static
		 * final String COLUMN_PKG_NAME = AppDaoImpl.COLUMN_PKG_NAME ;//2 static
		 * final String COLUMN_NAME = AppDaoImpl.COLUMN_NAME ;//3 static final
		 * String COLUMN_VERSION = AppDaoImpl.COLUMN_VERSION ;//4 static final
		 * String COLUMN_VERSION_INT = AppDaoImpl.COLUMN_VERSION_INT ;//5 static
		 * final String COLUMN_PUBLISH_DATE = AppDaoImpl.COLUMN_PUBLISH_DATE
		 * ;//6 static final String COLUMN_SIGN = AppDaoImpl.COLUMN_SIGN ;//7
		 * static final String COLUMN_EXTRA = AppDaoImpl.COLUMN_EXTRA ;//8
		 */
		private static final String CREATE_WHITE_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_WHITE_LIST
				+ " ("
				+ WhiteListTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ WhiteListTable.COLUMN_PKG_NAME
				+ " TEXT UNIQUE NOT NULL,"
				+ WhiteListTable.COLUMN_NAME + " TEXT"
				//
				// + WhiteListTable.COLUMN_PUBLISH_DATE + " INTEGER,"
				// + WhiteListTable.COLUMN_VERSION + " TEXT,"
				// + WhiteListTable.COLUMN_VERSION_INT + " INTEGER,"
				// + WhiteListTable.COLUMN_SIGN + " TEXT,"
				// + WhiteListTable.COLUMN_EXTRA + " TEXT"
				+ " )";

		private static final String CREATE_WHITE_LIST_INDEX = "CREATE INDEX IF NOT EXISTS "
				+ INDEX_TABLE_WHITE_LIST
				+ " ON "
				+ TABLE_WHITE_LIST
				+ "("
				+ COLUMN_PKG_NAME + " ASC)";

		private static final String CREATE_INSTALLED_APP_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_INSALLED_APP_LIST
				+ " ("
				+ InstalledTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 1
				+ InstalledTable.COLUMN_PKG_NAME
				+ " TEXT UNIQUE NOT NULL," // 2
				+ InstalledTable.COLUMN_NAME
				+ " TEXT," // 3
				+ InstalledTable.COLUMN_NAME_PINYIN
				+ " TEXT," // 4
				+ InstalledTable.COLUMN_INSTALLED_DATE
				+ " INTEGER," // 5
				+ InstalledTable.COLUMN_VERSION
				+ " TEXT," // 6
				+ InstalledTable.COLUMN_VERSION_INT
				+ " INTEGER," // 7
				+ InstalledTable.COLUMN_SIGN
				+ " TEXT," // 8
				+ InstalledTable.COLUMN_SIZE
				+ " INTEGER," // 8.1

				// + COLUMN_NEW_VERSION + " TEXT," //9
				// + COLUMN_NEW_VERSION_INT + " INTEGER," //10
				// + COLUMN_DOWNLOAD_URL + " TEXT," //11
				// + COLUMN_UPDATE_DATE + " INTEGER," //12
				// + COLUMN_SERVER_SIGN + " TEXT," //13

				+ InstalledTable.COLUMN_IS_GAME
				+ " INTEGER DEFAULT 0," // 14
				+ InstalledTable.COLUMN_IS_OWN
				+ " INTEGER DEFAULT 0," // 14.2 +是否是从duoku安装

				+ InstalledTable.COLUMN_NEED_LOGIN
				+ " INTEGER DEFAULT 0,"
				+ InstalledTable.COLUMN_GAME_ID
				+ " TEXT,"
				+ COLUMN_EXTRA
				+ " TEXT" // 15
				+ " )";

		private static final String CREATE_INSTALLED_APP_LIST_INDEX = "CREATE INDEX IF NOT EXISTS "
				+ INDEX_TABLE_INSALLED_APP_LIST
				+ " ON "
				+ TABLE_INSALLED_APP_LIST + "(" + COLUMN_PKG_NAME + " ASC)";

		private static final String CREATE_MYINSTALLED_APP_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_MY_INSTALLED_LIST
				+ " ("
				+ MyInstalledTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 1
				+ MyInstalledTable.COLUMN_PKG_NAME + " TEXT UNIQUE NOT NULL," // 2
				+ MyInstalledTable.COLUMN_INSTALL_TIME + " INTEGER ," // 3
				+ MyInstalledTable.COLUMN_LASTEST_OPEN_TIME + " INTEGER ," // 4
				+ MyInstalledTable.COLUMN_OPEN_TIMES + " INTEGER DEFAULT 0 ," // 5
				+ MyInstalledTable.COLUMN_EXTRA + " TEXT" // 6
				+ " )";

		private static final String CREATE_MYINSTALLED_APP_LIST_INDEX = "CREATE INDEX IF NOT EXISTS "
				+ INDEX_TABLE_MY_INSTALLED_LIST
				+ " ON "
				+ TABLE_INSALLED_APP_LIST + "(" + COLUMN_PKG_NAME + " ASC)";

		private static final String CREATE_MYDOWNLOADED_APP_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_MY_DOWNLOADED_LIST
				+ " ("
				+ MyDownloadedTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 1
				+ MyDownloadedTable.COLUMN_PKG_NAME + " TEXT UNIQUE NOT NULL," // 2
				+ MyDownloadedTable.COLUMN_GAME_ID + " TEXT ," // 3
				+ MyDownloadedTable.COLUMN_NAME + " TEXT ," // 4
				+ MyDownloadedTable.COLUMN_ICON_URL + " TEXT ," // 5
				+ MyDownloadedTable.COLUMN_GAME_KEY + " TEXT ," // 5
				+ MyDownloadedTable.COLUMN_NEED_LOGIN + " INTEGER DEFAULT 0," // 6
				+ MyDownloadedTable.COLUMN_EXTRA + " TEXT" // 6
				+ " )";

		private static final String CREATE_UPDATABLE_APP_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_UPDATABLE_APP_LIST
				+ " ("
				+ UpdateTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 1
				+ UpdateTable.COLUMN_PKG_NAME
				+ " TEXT UNIQUE NOT NULL," // 2
				+ UpdateTable.COLUMN_NEW_VERSION
				+ " TEXT," // 3
				+ UpdateTable.COLUMN_NEW_VERSION_INT
				+ " INTEGER," // 4
				+ UpdateTable.COLUMN_DOWNLOAD_URL
				+ " TEXT," // 5
				+ UpdateTable.COLUMN_PUBLISH_DATE
				+ " INTEGER," // 6
				+ UpdateTable.COLUMN_SERVER_SIGN
				+ " TEXT," // 7
				+ UpdateTable.COLUMN_NEW_SIZE
				+ " INTEGER," // 8
				+ UpdateTable.COLUMN_IGNORE_STATE
				+ " INTEGER DEFAULT 0," // 9
				+ UpdateTable.COLUMN_ICON_URL
				+ " TEXT ," // 9

				+ UpdateTable.COLUMN_UPDATE_STATE
				+ " INTEGER DEFAULT "
				+ COLUMN_UPDATE_STATE + "," // 9.1
				+ UpdateTable.COLUMN_GAME_ID + " TEXT ," // 9.2
				+ UpdateTable.COLUMN_NEED_LOGIN + " INTEGER DEFAULT 0," // 6
				+ UpdateTable.COLUMN_EXTRA + " TEXT" // 10
				+ " )";

		private static final String CREATE_UPDATABLE_APP_LIST_INDEX = "CREATE INDEX IF NOT EXISTS "
				+ INDEX_TABLE_UPDATABLE_APP_LIST
				+ " ON "
				+ TABLE_UPDATABLE_APP_LIST + "(" + COLUMN_PKG_NAME + " ASC)";

		private static final String CREATE_DOWNLOAD_APP_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_DOWNLOAD_APP_LIST
				+ " ("
				+ DownloadTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 0
				+ DownloadTable.COLUMN_PKG_NAME
				+ " TEXT ," // app attribute 1
				+ DownloadTable.COLUMN_NAME
				+ " TEXT," // app attribute 2
				+ DownloadTable.COLUMN_NAME_PINYIN
				+ " TEXT," // app attribute 3
				+ DownloadTable.COLUMN_VERSION
				+ " TEXT," // app attribute 4
				+ DownloadTable.COLUMN_VERSION_INT
				+ " INTEGER," // app attribute 5
				+ DownloadTable.COLUMN_ICON_URL
				+ " TEXT," // app attribute 6
				+ DownloadTable.COLUMN_PUBLISH_DATE
				+ " INTEGER," // app attribute 7
				+ DownloadTable.COLUMN_SIGN
				+ " TEXT," // app attribute 8
				+ DownloadTable.COLUMN_SIZE
				+ " INTEGER," // app attribute 9
				+ DownloadTable.COLUMN_EXTRA
				+ " TEXT," // app attribute 10
				+ DownloadTable.COLUMN_NEED_LOGIN
				+ " INTEGER DEFAULT 0," // 6
				+ DownloadTable.COLUMN_GAME_ID
				+ " TEXT," // app attribute 11

				+ DownloadTable.COLUMN_DOWNLOAD_DATE
				+ " INTEGER," // file attribute 12
				+ DownloadTable.COLUMN_DOWNLOAD_ID
				+ " INTEGER NOT NULL UNIQUE," // file attribute 13
				+ DownloadTable.COLUMN_DOWNLOAD_URL
				+ " TEXT UNIQUE NOT NULL," // file attribute 14

				+ DownloadTable.COLUMN_IS_DELETED
				+ " INTEGER DEFAULT 0,"
				+ DownloadTable.COLUMN_INSTALL_STATUS
				+ " INTEGER DEFAULT "
				+ DownloadTable.INSTALL_STATUS_UNINSTALLED
				+ ","
				+ DownloadTable.COLUMN_NOTIFIED
				+ " INTEGER DEFAULT 0"
				+ "," // added by wangliang
				+ DownloadTable.COLUMN_INSTALL_ERROR_REASON
				+ " INTEGER DEFAULT  " + PackageUtils.INSTALL_SUCCEEDED

				+ " )";
		private static final String CREATE_DOWNLOAD_APP_LIST_INDEX = "CREATE INDEX IF NOT EXISTS "
				+ INDEX_TABLE_DOWNLOAD_APP_LIST
				+ " ON "
				+ TABLE_DOWNLOAD_APP_LIST + "(" + COLUMN_PKG_NAME + " ASC)";

		private static final String CREATE_MERGE_LIST_SQL = "CREATE TABLE IF NOT EXISTS "
				+ AppDaoImpl.TABLE_MERGE_LIST
				+ " ("
				+ MergeTable.COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," // 0
				+ MergeTable.COLUMN_PKG_NAME
				+ " TEXT ," // app attribute 1
				+ MergeTable.COLUMN_VERSION
				+ " TEXT," // app attribute 4
				+ MergeTable.COLUMN_VERSION_INT
				+ " INTEGER," // app attribute 5
				+ MergeTable.COLUMN_GAME_ID
				+ " TEXT UNIQUE NOT NULL," // app attribute 11

				+ MergeTable.COLUMN_DOWNLOAD_ID
				+ " INTEGER ," // file attribute 13
				+ MergeTable.COLUMN_DOWNLOAD_URL
				+ " TEXT ," // file attribute 14
				+ MergeTable.COLUMN_SAVE_DEST
				+ " TEXT ," // file attribute 14
				+ MergeTable.COLUMN_FAILED_COUNT
				+ " INTEGER DEFAULT 0," // file attribute 14
				+ MergeTable.COLUMN_FAILED_REASON
				+ " INTEGER DEFAULT 0," // file attribute 14
				+ MergeTable.COLUMN_STATUS
				+ " INTEGER DEFAULT "
				+ MergeTable.STATUS_DEFAULT // file attribute 14

				+ " )";

		public static synchronized AppStorgeSqliteHelper getInstance(
				Context context) {
			/*
			 * File databasePath = context.getDatabasePath("app.db");
			 * if(databasePath == null || !databasePath.exists()){
			 * rebuild(context); }
			 */
			if (instance == null) {
				instance = new AppStorgeSqliteHelper(context);
			}
			return instance;
		}

		static synchronized AppStorgeSqliteHelper rebuild(Context context) {
			// if(instance == null){
			instance = new AppStorgeSqliteHelper(context);
			// }
			return instance;
		}

		public AppStorgeSqliteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_NEW_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			this.database = db;
			// Log.i("wang", "AppStorgeSqliteHelper onCreate");
			if (Constants.DEBUG)
				if (Constants.DEBUG)
					Log.i(TAG, "AppStorgeSqliteHelper onCreate:"
							+ Thread.currentThread().getName() + " "
							+ Thread.currentThread().getId());
			// db.execSQL(CREATE_WHITE_LIST_SQL);
			// db.execSQL(CREATE_INSTALLED_APP_LIST_SQL);
			// db.execSQL(CREATE_DOWNLOAD_APP_LIST_SQL);
			// db.execSQL(CREATE_UPDATABLE_APP_LIST_SQL);
			// db.execSQL(CREATE_MYINSTALLED_APP_LIST_SQL);
			// db.execSQL(CREATE_MYDOWNLOADED_APP_LIST_SQL);
			//
			// db.execSQL(CREATE_WHITE_LIST_INDEX);
			// db.execSQL(CREATE_INSTALLED_APP_LIST_INDEX);
			// db.execSQL(CREATE_DOWNLOAD_APP_LIST_INDEX);
			// db.execSQL(CREATE_UPDATABLE_APP_LIST_INDEX);
			// db.execSQL(CREATE_MYINSTALLED_APP_LIST_INDEX);

			onUpgrade(db, DATABASE_OLD_VERSION, DATABASE_NEW_VERSION);
			// loadInitWhiteList();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			/*
			 * db.execSQL("DROP INDEX IF EXISTS "+INDEX_TABLE_WHITE_LIST);
			 * db.execSQL
			 * ("DROP INDEX IF EXISTS "+INDEX_TABLE_INSALLED_APP_LIST);
			 * db.execSQL
			 * ("DROP INDEX IF EXISTS "+INDEX_TABLE_DOWNLOAD_APP_LIST);
			 * db.execSQL
			 * ("DROP INDEX IF EXISTS "+INDEX_TABLE_UPDATABLE_APP_LIST);
			 * db.execSQL
			 * ("DROP INDEX IF EXISTS "+INDEX_TABLE_MY_INSTALLED_LIST);
			 * 
			 * db.execSQL("DROP TABLE IF EXISTS "+TABLE_DOWNLOAD_APP_LIST);
			 * db.execSQL("DROP TABLE IF EXISTS "+TABLE_INSALLED_APP_LIST);
			 * db.execSQL("DROP TABLE IF EXISTS "+TABLE_WHITE_LIST);
			 * db.execSQL("DROP TABLE IF EXISTS "+TABLE_UPDATABLE_APP_LIST);
			 * db.execSQL("DROP TABLE IF EXISTS "+TABLE_MY_INSTALLED_LIST);
			 * db.execSQL("DROP TABLE IF EXISTS "+TABLE_MY_DOWNLOADED_LIST);
			 */

			for (int version = oldVersion; version <= newVersion; version++) {
				upgradeTo(db, version);
			}
			afterDbCreated();

		}

		private void addColumn(SQLiteDatabase db, String dbTable,
				String columnName, String columnDefinition) {
			try {
				db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN "
						+ columnName + " " + columnDefinition);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void createTable(SQLiteDatabase db) {
			if (Constants.DEBUG)
				if (Constants.DEBUG)
					Log.i(TAG, "AppStorgeSqliteHelper onCreate:"
							+ Thread.currentThread().getName() + " "
							+ Thread.currentThread().getId());
			db.execSQL(CREATE_WHITE_LIST_SQL);
			db.execSQL(CREATE_INSTALLED_APP_LIST_SQL);
			db.execSQL(CREATE_DOWNLOAD_APP_LIST_SQL);
			db.execSQL(CREATE_UPDATABLE_APP_LIST_SQL);
			db.execSQL(CREATE_MYINSTALLED_APP_LIST_SQL);
			db.execSQL(CREATE_MYDOWNLOADED_APP_LIST_SQL);

			db.execSQL(CREATE_WHITE_LIST_INDEX);
			db.execSQL(CREATE_INSTALLED_APP_LIST_INDEX);
			db.execSQL(CREATE_DOWNLOAD_APP_LIST_INDEX);
			db.execSQL(CREATE_UPDATABLE_APP_LIST_INDEX);
			db.execSQL(CREATE_MYINSTALLED_APP_LIST_INDEX);
		}

		private void upgradeTo(SQLiteDatabase db, int version) {
			switch (version) {
			case 2:
				createTable(db);
				break;
			case 3:
				db.execSQL(CREATE_MERGE_LIST_SQL);
				addColumn(db, TABLE_INSALLED_APP_LIST,
						InstalledTable.COLUMN_FILEMD5, "TEXT");
				addColumn(db, TABLE_DOWNLOAD_APP_LIST,
						DownloadTable.COLUMN_FILEMD5, "TEXT");
				addColumn(db, TABLE_DOWNLOAD_APP_LIST,
						DownloadTable.COLUMN_IS_DIFFUPDATE, "INTEGER DEFAULT 0");
				// 合并失败次数
				/*
				 * addColumn(db,TABLE_DOWNLOAD_APP_LIST,
				 * DownloadTable.COLUMN_MERGE_FAILED_COUNT,
				 * "INTEGER DEFAULT 0");
				 */
				addColumn(db, TABLE_UPDATABLE_APP_LIST,
						UpdateTable.COLUMN_IS_DIFFUPDATE, "INTEGER DEFAULT 0");
				addColumn(db, TABLE_UPDATABLE_APP_LIST,
						UpdateTable.COLUMN_PATCH_URL, "TEXT");
				addColumn(db, TABLE_UPDATABLE_APP_LIST,
						UpdateTable.COLUMN_PATCH_SIZE, "INTEGER DEFAULT 0");
				break;
			case 4:
				addColumn(db, TABLE_INSALLED_APP_LIST,
						InstalledTable.COLUMN_UID, "INTEGER");
				updateFor();
				break;
			default:
				throw new IllegalStateException("Don't know how to upgrade to "
						+ version);
			}
		}

		private void updateFor() {
			DBTaskManager.submitTask(new Runnable() {

				@Override
				public void run() {
					updateAppDataForDownloadDatabase();
					updateFileMd5ForInstallTable();
					updateDownloadSign();
				}
			});
		}

		private void updateAppDataForDownloadDatabase() {

			AppDao appDbHandler = DbManager.getAppDbHandler();
			List<DownloadAppInfo> allDownloadGames = appDbHandler
					.getAllDownloadGames(true);
			if (allDownloadGames != null) {
				for (DownloadAppInfo d : allDownloadGames) {
					long downloadId = d.getDownloadId();
					int updateCount = 0;
					try {
						String appData = PackageHelper.formDownloadAppData(
								d.getPackageName(), d.getVersion(),
								d.getVersionInt(), d.getGameId(), false);
						updateCount = DownloadUtil.updateDownload(context,
								downloadId, appData);
					} catch (Exception e) {
						Log.e(TAG, "updateDownloadTable Error,", e);
					}
					try {
						if (updateCount == 0) {
							appDbHandler.removeDownloadGames(true, downloadId);
							DownloadUtil.removeDownload(
									GameTingApplication.getAppInstance(), true,
									downloadId);
						}
					} catch (Exception e) {
						Log.e(TAG, "updateDownloadTable Error,", e);
					}

				}
			}
		}

		private void updateFileMd5ForInstallTable() {
			try {
				AppDao appDbHandler = DbManager.getAppDbHandler();
				AppManager manager = AppManager.getInstance(GameTingApplication
						.getAppInstance());
				List<InstalledAppInfo> news = manager.loadInstalledList(true);
				List<InstalledAppInfo> olds = appDbHandler
						.getAllInstalledApps();

				if (olds != null && news != null) {
					for (InstalledAppInfo o : olds) {
						for (InstalledAppInfo n : news) {
							if (o.getPackageName().equals(n.getPackageName())) {
								n.setGame(o.isGame());
								n.setGameId(o.getGameId());
							}
						}
					}
					if (news.size() > 0) {
						appDbHandler.removeInstalledApps();
						appDbHandler.replaceAllInstalledApps(news);
					}
				} else {
					appDbHandler.replaceAllInstalledApps(news);
				}
			} catch (Exception e) {
			}

		}

		private void updateDownloadSign() {
			try {
				AppManager manager = AppManager.getInstance(GameTingApplication
						.getAppInstance());
				List<DownloadAppInfo> downloadGames = manager
						.getAndCheckDownloadGames(true);
				if (downloadGames == null) {
					return;
				}
				if (downloadGames == null || downloadGames.size() == 0) {
					return;
				}
				AppDao appDbHandler = DbManager.getAppDbHandler();
				for (DownloadAppInfo d : downloadGames) {
					if (d.getStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
						String path = Uri.parse(d.getSaveDest()).getPath();
						String fileMd5 = FileHelper.getFileMd5(path);
						PackageInfo pack = ApkUtil.getPackageForFile(path,
								GameTingApplication.getAppInstance());
						String sign = AppUtil.getSignMd5(pack);
						if (sign != null && fileMd5 != null) {
							appDbHandler.updateDownload(d.getDownloadId(),
									sign, fileMd5);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void afterDbCreated() {
			// Log.i("wang", "AppStorgeSqliteHelper afterDbCreated");
			// Log.d(TAG, "[requestDownloadedGames]");
			DBTaskManager.submitTask(new Runnable() {

				@Override
				public void run() {
					FutureTaskManager task = FutureTaskManager.getInstance();
					if (Constants.DEBUG)
						Log.i("wangliangtest",
								"[AppDaoImpl#afterDbCreated]requestDownloadedGames");
					task.requestDownloadedGames();
				}
			});
		}
	}

}
