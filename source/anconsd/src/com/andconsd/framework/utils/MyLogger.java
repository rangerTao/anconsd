package com.andconsd.framework.utils;

import android.util.Log;

import java.util.Hashtable;

/**
 * Utility log tool.
 * 
 * @author GuoZhen
 */
public class MyLogger {

    public boolean mIsLoggerEnable = true&Constants.DEBUG;
    private final static String LOG_TAG = "[GameHall]";
    private static Hashtable<String, MyLogger> sLoggerTable;
    private String mClassName;
    
    static {
    	sLoggerTable = new Hashtable<String, MyLogger>();
    }

    public static MyLogger getLogger(String className) {
        MyLogger classLogger = (MyLogger) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new MyLogger(className);
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    private MyLogger(String name) {
        mClassName = name;
    }

    public void v(String log) {
        if (mIsLoggerEnable) {
            Log.v(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log);
        }
    }

    public void d(String log) {
        if (mIsLoggerEnable) {
            Log.d(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log);
        }
    }

    public void i(String log) {
        if (mIsLoggerEnable) {
            Log.i(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log);
        }
    }

    public void i(String log, Throwable tr) {
        if (mIsLoggerEnable) {
            Log.i(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log + "\n" + Log.getStackTraceString(tr));
        }
    }

    public void w(String log) {
        if (mIsLoggerEnable) {
            Log.w(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log);
        }
    }

    public void w(String log, Throwable tr) {
        if (mIsLoggerEnable) {
            Log.w(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log + "\n" + Log.getStackTraceString(tr));
        }
    }

    public void e(String log) {
        if (mIsLoggerEnable) {
            Log.e(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log);
        }
    }

    public void e(String log, Throwable tr) {
        if (mIsLoggerEnable) {
            Log.e(LOG_TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName
                    + ":] " + log + "\n" + Log.getStackTraceString(tr));
        }
    }
}