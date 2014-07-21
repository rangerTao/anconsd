package com.ranger.bmaterials.db;

import java.util.List;

import com.ranger.bmaterials.work.FutureTaskManager.TaskMode;

public interface CommonDao {
	public void saveKeywords(List<String> keywords);
	public void saveKeywords(String... keywords);
	public List<String> getKeywords();
	public void removeKeywords();
	
	public void saveTask(int taskTag,String extra);
	public List<TaskMode> getTasks();
	public List<TaskMode> getTasks(int tag);
	public void removeTask(List<Long> taskIds);
	
//	public void saveNotified(String packageName,String donwloadUrl);
//	public void getNotifieds();
//	public boolean queryNotifieds(String packageName);
}
