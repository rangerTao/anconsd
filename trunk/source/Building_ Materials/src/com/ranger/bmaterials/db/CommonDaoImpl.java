package com.ranger.bmaterials.db;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.ranger.bmaterials.download.Constants;
import com.ranger.bmaterials.download.Downloads;
import com.ranger.bmaterials.work.FutureTaskManager.TaskMode;

public class CommonDaoImpl implements CommonDao {
	private static final String DATABASE_NAME = "keyword.db";
	
	private static final String TABLE_KEYWORDS = "keywords";
	private static final String TABLE_TASKS = "tasks";
	static final String COLUMN_ID = "_id" ;			//1
	static final String COLUMN_KEYWORD = "keyword" ;	//2
	
	static final String COLUMN_TASK_TAG = "task_tag" ;	
	static final String COLUMN_TASK_EXTRA = "task_extra" ;	
	Context context ;
	
	public CommonDaoImpl(Context context) {
		this.context = context.getApplicationContext();
	}
	
	public static class SearchSqlHelper extends SQLiteOpenHelper {
		static SearchSqlHelper instance ;
		
		private static final String CREATE_KEYWORDS_SQL = "CREATE TABLE IF NOT EXISTS " + CommonDaoImpl.TABLE_KEYWORDS 
	            + " (" 
	            + CommonDaoImpl.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	            + CommonDaoImpl.COLUMN_KEYWORD + " TEXT UNIQUE" 
	        + " )";
		private static final String CREATE_TASK_SQL = "CREATE TABLE IF NOT EXISTS " + CommonDaoImpl.TABLE_TASKS 
				+ " (" 
				+ CommonDaoImpl.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ CommonDaoImpl.COLUMN_TASK_TAG + " TEXT," 
				+ CommonDaoImpl.COLUMN_TASK_EXTRA + " TEXT ," 
				+ "UNIQUE ("+CommonDaoImpl.COLUMN_TASK_TAG+","+CommonDaoImpl.COLUMN_TASK_EXTRA+")"//唯一
				+ " )";
		
		public static  SearchSqlHelper getInstance(Context context){
			if(instance == null){
				instance = new SearchSqlHelper(context) ;
			}
			return instance ;
		}
		
		public SearchSqlHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_KEYWORDS_SQL);
			db.execSQL(CREATE_TASK_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_KEYWORDS);
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASKS);
			onCreate(db);
		}
		
		
		 
	}

	@SuppressLint("NewApi")
	@Override
	public void saveKeywords(List<String> keywords) {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}
			for (String keyword : keywords) {
				cv.put(COLUMN_KEYWORD, keyword);
				db.insert(TABLE_KEYWORDS, null, cv);
				cv.clear();
			}
			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
                try{
                    db.endTransaction();
                }catch (Exception e){
                    e.printStackTrace();
                }

			}
		}
	}
	@SuppressLint("NewApi")
	@Override
	public void saveKeywords(String... keywords) {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db = helper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				db.beginTransactionNonExclusive();
			} else {
				db.beginTransaction();
			}
			for (String keyword : keywords) {
				cv.put(COLUMN_KEYWORD, keyword);
				db.replace(TABLE_KEYWORDS, null, cv);
				cv.clear();
			}
			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
                try{
                    db.endTransaction();
                }catch (Exception e){
                    e.printStackTrace();
                }
			}
		}
		trimDatabase();
	}
	
	 private void trimDatabase() {
		 SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
			SQLiteDatabase db = null ;
			Cursor cursor = null ;
			try {
				db = helper.getReadableDatabase();
				cursor = db.query(TABLE_KEYWORDS, 
						new String[]{
							CommonDaoImpl.COLUMN_ID
						} ,
						null, null ,null, null, 
						COLUMN_ID +" ASC");
				
				  if (cursor == null) {
			            // This isn't good - if we can't do basic queries in our database, nothing's gonna work
			            Log.e(Constants.TAG, "null cursor in trimDatabase");
			            return;
			        }
			        if (cursor.moveToFirst()) {
			            int numDelete = cursor.getCount() - 100;
			            int columnId = cursor.getColumnIndex(CommonDaoImpl.COLUMN_ID);
			            ArrayList<Long> removed = new ArrayList<Long>();
			            while (numDelete > 0) {
			                //Uri downloadUri = ContentUris.withAppendedId(
			                //        Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, cursor.getLong(columnId));
			            	removed.add(cursor.getLong(columnId));
			            	//db.delete(TABLE_KEYWORDS, CommonDaoImpl.COLUMN_ID+"=?", whereArgs)
			                if (!cursor.moveToNext()) {
			                    break;
			                }
			                numDelete--;
			            }
			            if(removed.size()>0){
			            	removeKeywords(removed);
			            }
			        }
			        cursor.close();
			}catch (Exception e) {
			}finally{
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
			}
	      
	    }

	
	@Override
	public List<String> getKeywords() {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = null ;
		Cursor query = null ;
		try {
			db = helper.getReadableDatabase();
			query = db.query(TABLE_KEYWORDS, new String[]{COLUMN_KEYWORD} , null, null ,null, null, null);
			List<String> keywords = new ArrayList<String>(query.getCount());
			while(query.moveToNext()){
				String keyword = query.getString(0);
				keywords.add(keyword);
			}
			return keywords ;
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}finally{
			if(query != null && !query.isClosed()){
				query.close();
			}
		}
		return null ;
	}

	@Override
	public void removeKeywords() {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db = helper.getWritableDatabase();
			db.delete(TABLE_KEYWORDS, null,null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
		}
	}
	private void removeKeywords(List<Long> ids) {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
				db.beginTransactionNonExclusive();
			}else{
				db.beginTransaction();
			}
			StringBuffer whereClause = new StringBuffer(COLUMN_ID + " IN ( ");
    		int size = ids.size();
    		String[] whereArgs = new String[size];
			int i = 0 ;
			for (Long id : ids) {
				if(i != (size-1)){
					whereClause.append("?,");
				}else{
					whereClause.append("?)");
				}
				whereArgs[i] = String.valueOf(id);
				i++;
			}
			db.delete(TABLE_KEYWORDS, whereClause.toString(), whereArgs);
			db.setTransactionSuccessful();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			
		}
	}


	@Override
	public void saveTask(int taskTag,String extra) {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_TASK_TAG, taskTag);
			cv.put(COLUMN_TASK_EXTRA, extra);
			long id = db.insert(TABLE_TASKS, null, cv);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
		}
	}


	@Override
	public List<TaskMode> getTasks() {
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = null ;
		Cursor query = null ;
		try {
			db = helper.getReadableDatabase();
			query = db.query(TABLE_TASKS, new String[]{
					CommonDaoImpl.COLUMN_ID,
					CommonDaoImpl.COLUMN_TASK_TAG,
					CommonDaoImpl.COLUMN_TASK_EXTRA} 
			, null, null ,null, null, null);
			int count = query.getCount();
			if(count == 0){
				return null ;
			}
			List<TaskMode> tasks = new ArrayList<TaskMode>(count);
			while(query.moveToNext()){
				long taskId = query.getLong(0);
				int taskTag = query.getInt(1);
				String taskExtra = query.getString(2);
				
				tasks.add(new TaskMode(taskId, taskTag, taskExtra));
			}
			return tasks ;
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}finally{
			if(query != null && !query.isClosed()){
				query.close();
			}
		}
		return null ;
	}
	@Override
	public List<TaskMode> getTasks(int tag){
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = null ;
		Cursor query = null ;
		try {
			db = helper.getReadableDatabase();
			query = db.query(TABLE_TASKS, new String[]{
					CommonDaoImpl.COLUMN_ID,
					CommonDaoImpl.COLUMN_TASK_TAG,
					CommonDaoImpl.COLUMN_TASK_EXTRA} 
			, COLUMN_TASK_TAG+"=?", new String[]{String.valueOf(tag)} ,null, null, null);
			int count = query.getCount();
			if(count == 0){
				return null ;
			}
			List<TaskMode> tasks = new ArrayList<TaskMode>(count);
			while(query.moveToNext()){
				long taskId = query.getLong(0);
				int taskTag = query.getInt(1);
				String taskExtra = query.getString(2);
				
				tasks.add(new TaskMode(taskId, taskTag, taskExtra));
			}
			return tasks ;
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}finally{
			if(query != null && !query.isClosed()){
				query.close();
			}
		}
		return null ;
	}


	@SuppressLint("NewApi")
	@Override
	public void removeTask(List<Long> taskIds) {
		if(taskIds == null || taskIds.size() ==0){
			return ;
		}
		
		SearchSqlHelper helper = SearchSqlHelper.getInstance(context);
		SQLiteDatabase db = null ;
		try {
			db = helper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
				db.beginTransactionNonExclusive();
			}else{
				db.beginTransaction();
			}
			
			StringBuffer whereClause = new StringBuffer(COLUMN_ID + " IN ( ");
    		
    		int size = taskIds.size();
    		String[] whereArgs = new String[size];
			int i = 0 ;
			for (Long id : taskIds) {
				if(i != (size-1)){
					whereClause.append("?,");
				}else{
					whereClause.append("?)");
				}
				whereArgs[i] = String.valueOf(id);
				i++;
			}
			db.delete(TABLE_TASKS, whereClause.toString(), whereArgs);
			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}finally{
			if(db != null && db.isOpen()){
                try{
                    db.endTransaction();
                }catch (Exception e){
                    e.printStackTrace();
                }
			}
		}
	}

}
