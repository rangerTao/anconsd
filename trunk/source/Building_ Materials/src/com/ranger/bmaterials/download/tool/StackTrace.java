package com.ranger.bmaterials.download.tool;

import android.util.Log;

public class StackTrace {
	/*public static void debugWhere(String tag, String msg) {
		Log.d(tag, msg + " --- stack trace begins: ");
		StackTraceElement elements[] = Thread.currentThread().getStackTrace();
		// skip first 3 element, they are not related to the caller
		for (int i = 3, n = elements.length; i < n; ++i) {
			StackTraceElement st = elements[i];
			String message = String.format("    at %s.%s(%s:%s)",
					st.getClassName(), st.getMethodName(), st.getFileName(),
					st.getLineNumber());
			Log.d(tag, message);
		}
		Log.d(tag, msg + " --- stack trace ends.");
	}*/

	public static String getTrace() {
		String trace = null ;
		if(trace == null || "".equals(trace)){
			StackTraceElement st = Thread.currentThread().getStackTrace()[5];
			String fullClassName = st.getClassName();
			String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
			int lineNumber = st.getLineNumber();
			trace =   "(" + className + ":" + lineNumber + ")";;
		}
		if(trace == null || "".equals(trace)){
			try {
				trace = new Throwable().getStackTrace()[2].toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return trace;
	}

	public static String getTrace(Throwable tr) {
		return Log.getStackTraceString(tr);
	}
}
