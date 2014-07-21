package com.ranger.bmaterials.work;

public class DBTaskManager {

	public interface TaskCallback {
		void onTaskFinished();
	}

	public static void submitTask(Runnable runnable) {
		Thread t = new Thread(runnable);
		t.setDaemon(true);
		t.start();
	}

}
