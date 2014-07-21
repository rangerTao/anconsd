package com.ranger.bmaterials.db;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.ranger.bmaterials.tools.FileHelper;
import com.ranger.bmaterials.tools.MyLogger;

public class ImageCacheDao implements IImageCacheDao {
	MyLogger mLogger = MyLogger.getLogger("ImageCacheDao");

	// create
	private static final String CREATE_SQL = "create table imagecache " + "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "url TEXT not null, "
			+ "path TEXT not null, " + "date TEXT not null, " + "d1 TEXT,"
			+ "d2 TEXT," + "d3 TEXT" + ")";
	// insert
	private static final String ADD_CACHE = "INSERT INTO imagecache (url, path, date) VALUES (?, ?, ?)";
	// delete all image data
	private static final String DEL_ALL_IMAGE = "delete from imagecache";

	private static final String[] TABLE_COLUMNS;
	private static final String TABLE_NAME = "imagecache";
	private static final String DATABASE_NAME = "imagecache.db";
	private static final int DATABASE_VERSION = 1; // data base version

	private Context mAppContext = null;
	private ImageCacheSqliteHelper mSqliteHelper = null;

	static {
		String[] columnStrings = new String[6];
		columnStrings[0] = "url";
		columnStrings[1] = "path";
		columnStrings[2] = "date";
		columnStrings[3] = "d1";
		columnStrings[4] = "d2";
		columnStrings[5] = "d3";
		TABLE_COLUMNS = columnStrings;
	}

	ImageCacheDao(Context context) {
		this.mAppContext = context;
	}

	public class ImageCacheSqliteHelper extends SQLiteOpenHelper {

		public ImageCacheSqliteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// First read current data into memory
			{
				// need copy old data to memory
			}
			// Second create a new data base table
			// drop old data base table
			onCreate(db);
			// Third copy old data from memory into new data table
			{

			}
		}
	}

	public synchronized String getCacheFile(String url) {
		String path = null;
		SQLiteDatabase dbInstance = null;
		Cursor cursor = null;

		try {
			openConnection();
			dbInstance = mSqliteHelper.getReadableDatabase();
			dbInstance.beginTransaction();
			cursor = dbInstance
					.query(TABLE_NAME, TABLE_COLUMNS, TABLE_COLUMNS[0] + "='"
							+ url + "'", null, null, null, null);
			if (cursor != null && cursor.getCount() != 0) {
				cursor.moveToFirst();
				path = extractAnImageCache(cursor);
				boolean r = FileHelper.fileIfExists(path);

				if (!r) {
					path = null;
					// delete the image cache by url
					dbInstance.execSQL("delete from imagecache where url = '"
							+ url + "'");
					dbInstance.setTransactionSuccessful();
				}
			}
		} catch (Exception e) {
			mLogger.e(e.toString());
			throw new SQLiteException(e.toString());
		} finally {
			// close book data base file
			if (dbInstance != null) {
				dbInstance.endTransaction();
				dbInstance.close();
			}
			if (cursor != null) {
				cursor.close();
			}
			closeConnection();
		}
		return path;

	}

	public synchronized void addCacheFile(String url, String localPath) {
		SQLiteDatabase db = null;
		try {
			openConnection();

			db = mSqliteHelper.getWritableDatabase();
			db.beginTransaction();
			SQLiteStatement statement = db.compileStatement(ADD_CACHE);
			DBTools.bindString(statement, 1, url);
			DBTools.bindString(statement, 2, localPath);
			Date date = new Date();
			DBTools.bindLong(statement, 3, date.getTime());

			long rowid = statement.executeInsert();

			if (rowid == -1) {
				mLogger.v("cache image failed");
			}
			db.setTransactionSuccessful();
			mLogger.d(" addCacheFile end");
		} catch (Exception e) {
			mLogger.e(e.toString());
		} finally {
			if (db != null) {
				db.endTransaction();
				db.close();
			}
			closeConnection();
		}
	}

	public void cleanCache() {
		SQLiteDatabase db = null;
		try {

			openConnection();
			db = mSqliteHelper.getWritableDatabase();
			db.beginTransaction();
			db.execSQL(DEL_ALL_IMAGE);
			db.setTransactionSuccessful();
			mLogger.d("clear all icon cache");
		} catch (Exception e) {

		} finally {
			if (db != null) {
				db.endTransaction();
				db.close();
			}
			
			// clear icon with path
			
			closeConnection();
		}
	}

	
	private void deleteIcons(){
//        ArrayList<String> resArray = new ArrayList<String>();
//        SQLiteDatabase dbInstance = null;
//        Cursor cursor = null;
//        try
//        {
//            // open book database file
//            openConnection();
//            // get the read able data base handler
//            dbInstance = mSqliteHelper.getReadableDatabase();
//            dbInstance.beginTransaction();
//            cursor = dbInstance.query(TABLE_NAME, TABLE_COLUMNS, TABLE_COLUMNS[10] + "=" + Book.TYPE_BOOKSHELF, null, null, null, TABLE_COLUMNS[index] + " "
//                    + sortType);
//            if (cursor != null)
//            {
//                cursor.moveToFirst();
//                while (cursor != null && cursor.getCount() != 0 && !cursor.isAfterLast())
//                {
//                    LocalBook tempBook = extractAnBook(cursor);
//                    resArray.add(tempBook);
//                    cursor.moveToNext();
//                }
//            }
//            dbInstance.setTransactionSuccessful();
//            logger.i("getBookList--end");
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            logger.i("getBookList--exception=", e);
//            // Need Throw a new exception
//            throw new SQLiteException(e.toString());
//
//        }
//        finally
//        {
//            // close book data base file
//            if (dbInstance != null)
//            {
//                dbInstance.endTransaction();
//                dbInstance.close();
//            }
//            if (cursor != null)
//            {
//                cursor.close();
//            }
//            closeConnection();
//        }
	}
	private void openConnection() {
		mSqliteHelper = new ImageCacheSqliteHelper(this.mAppContext);
	}

	private void closeConnection() {
		mSqliteHelper.close();
		mSqliteHelper = null;
	}

	private String extractAnImageCache(Cursor cursor) {

		String res = null;
		res = cursor.getString(cursor.getColumnIndex(TABLE_COLUMNS[1]));
		return res;
	}
}
