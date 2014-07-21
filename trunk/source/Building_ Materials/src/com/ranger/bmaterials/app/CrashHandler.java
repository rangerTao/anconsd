package com.ranger.bmaterials.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;

import com.ranger.bmaterials.app.CrashHandler;
import com.ranger.bmaterials.tools.MyLogger;
import com.ranger.bmaterials.tools.PhoneHelper;

public class CrashHandler implements UncaughtExceptionHandler {
	private static MyLogger mLogger = MyLogger.getLogger(CrashHandler.class.getName());

	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler INSTANCE = new CrashHandler();
	private Context mContext;
	private Map<String, String> infos = new HashMap<String, String>();

	// private DateFormat formatter = new
	// SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context context) {
		mContext = context;
		mLogger.d("CrashHandler init isEmulator = " + PhoneHelper.isEmulator(mContext));
		if (!PhoneHelper.isEmulator(mContext)) {
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(this);
		}
	}

	public void uncaughtException(Thread thread, Throwable ex) {
		// DownLoadController.cancelAllDownloadTask();

		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private boolean handleException(Throwable ex) {
        if(Constants.DEBUG){

        }else{
            saveCrashInfo2File(ex);
        }
		return false;//false抛出异常
	}

	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			Calendar myDate = Calendar.getInstance();
			StringBuffer date = new StringBuffer();
			date.append(myDate.get(Calendar.YEAR)).append("-").append(myDate.get(Calendar.MONTH)).append("-").append(myDate.get(Calendar.DAY_OF_MONTH)).append("-")
					.append(myDate.get(Calendar.HOUR)).append("-").append(myDate.get(Calendar.MINUTE)).append("-").append(myDate.get(Calendar.SECOND));
			
			String fileName = "crash-" + date.toString()  + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()
						.toString() + Constants.GAME_TING_LOG;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}