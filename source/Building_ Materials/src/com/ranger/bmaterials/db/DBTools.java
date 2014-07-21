/**

 * @author huzexin@duoku.com

 * @version CreateData��2012-5-10 3:46:54 PM

 */
package com.ranger.bmaterials.db;

import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

public abstract class DBTools {

	public static void bindString(SQLiteStatement statement, int index, String value) {
		
		if (value != null) {
			statement.bindString(index, value);
		} else {
			statement.bindNull(index);
		}
	}
	
	public static void bindDate(SQLiteStatement statement, int index, Date value) {
		
		if (value != null) {
			statement.bindLong(index, value.getTime());
		} else {
			statement.bindNull(index);
		}
	}
	
	public static void bindLong(SQLiteStatement statement, int index, long value) {
		
		statement.bindLong(index, value);
	}

	public static Date getDate(Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			return null;
		}
		return new Date(cursor.getLong(index));
	}
	
}
