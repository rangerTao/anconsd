package com.ranger.bmaterials.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.CommonDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.mode.MyDownloadedGame;
import com.ranger.bmaterials.mode.MyDownloadedGames;
import com.ranger.bmaterials.mode.WhiteList;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.DeviceId;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;

public class FutureTaskManager  {
	
	public static class TaskMode {
		private long taskId ;
		private int taskTag ;
		private String taskExtra ;
		public TaskMode(long taskId, int taskTag, String taskExtra) {
			super();
			this.taskId = taskId;
			this.taskTag = taskTag;
			this.taskExtra = taskExtra;
		}
		public long getTaskId() {
			return taskId;
		}
		public void setTaskId(long taskId) {
			this.taskId = taskId;
		}
		public int getTaskTag() {
			return taskTag;
		}
		public void setTaskTag(int taskTag) {
			this.taskTag = taskTag;
		}
		public String getTaskExtra() {
			return taskExtra;
		}
		public void setTaskExtra(String taskExtra) {
			this.taskExtra = taskExtra;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((taskExtra == null) ? 0 : taskExtra.hashCode());
			result = prime * result + taskTag;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TaskMode other = (TaskMode) obj;
			if (taskExtra == null) {
				if (other.taskExtra != null)
					return false;
			} else if (!taskExtra.equals(other.taskExtra))
				return false;
			if (taskTag != other.taskTag)
				return false;
			return true;
		}
		
		
	}



	private static final String TAG = "FutureTaskManager";
	
	
	
	static FutureTaskManager INSTANCE ;
	
	private FutureTaskManager() {
	}
	public synchronized static FutureTaskManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new FutureTaskManager();
		}
		return INSTANCE ;
	}
	
	
	public void submitIncompleteIfNecessary(){
		
		List<TaskMode> tasks = getTasks();
		if(tasks != null && tasks.size() > 0){
			Map<Integer, List<TaskMode>> taskMap = spliteTasks(tasks);
			List<TaskMode> list = taskMap.get(Constants.NET_TAG_WHITE_LIST);
			if(list != null && list.size() >0){
				verifyGames(list);
			}
			List<TaskMode> list2 = taskMap.get(Constants.NET_TAG_REGISTER_INSTALLED_GAME);
			if(list2 != null && list2.size() >0){
				registerGames(list2);
			}
			List<TaskMode> list3 = taskMap.get(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES);
			if(list3 != null && list3.size() > 0 ){
				submitGames(list3);
			}
			List<TaskMode> list4 = taskMap.get(Constants.NET_TAG_GET_DOWNLOADED_GAMES);
			if(list4 != null && list4.size() > 0 ){
				requestDownloadedGames(list4);
			}
		}
	}
	
	private void verifyGames(List<TaskMode> list){
		NetUtil netUtil = NetUtil.getInstance();
		ArrayList<String> packages = new ArrayList<String>(list.size());
		ArrayList<Long> taskIds = new ArrayList<Long>(list.size());
		for (TaskMode t : list) {
			packages.add(t.getTaskExtra());
			taskIds.add(t.getTaskId());
		}
		netUtil.requestForWhiteList(packages, new CommonListener(taskIds));
	}
	/**
	 * 确定是否为游戏
	 * @param packageName
	 */
	public void verifyGame(Context context,String packageName){
		
		boolean networkAvailable = DeviceUtil.isNetworkAvailable(context);
		if(!networkAvailable){
			saveTask(Constants.NET_TAG_WHITE_LIST, packageName);
		}else {
			ArrayList<String> packages = new ArrayList<String>();
			packages.add(packageName);
		}
	}
	
	private void registerGames(List<TaskMode> list){
		
		MineProfile profile = MineProfile.getInstance();
		if(!profile.getIsLogin()){
			return ;
		}
		String userId = profile.getUserID();
		String sessionId = profile.getSessionID();
		//没有gameid
		if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(sessionId)){
			return ;
		}
		
		NetUtil netUtil = NetUtil.getInstance();
		ArrayList<String> ids = new ArrayList<String>(list.size());
		ArrayList<Long> taskIds = new ArrayList<Long>(list.size());
		for (TaskMode t : list) {
			ids.add(t.getTaskExtra());
			taskIds.add(t.getTaskId());
		}
		netUtil.requestRegisterGames(userId, sessionId, ids, new CommonListener(taskIds));
		
	}
	private void submitGames(List<TaskMode> list){
		NetUtil netUtil = NetUtil.getInstance();
		ArrayList<String> ids = new ArrayList<String>(list.size());
		ArrayList<Long> taskIds = new ArrayList<Long>(list.size());
		for (TaskMode t : list) {
			ids.add(t.getTaskExtra());
			taskIds.add(t.getTaskId());
		}
		netUtil.requestForUploadDownloadedGames(DeviceId.getDeviceID(GameTingApplication.getAppInstance()),
				ids, new CommonListener(taskIds));
		
	}

	/**
	 * 注册我安装的游戏 tag = 253 
	 * @param gameId
	 */
	public void registerGame(Context context,String gameId){
		MineProfile profile = MineProfile.getInstance();
		if(!profile.getIsLogin()){
			return ;
		}
		String userId = profile.getUserID();
		String sessionId = profile.getSessionID();
		//没有gameid
		if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(sessionId)){
			return ;
		}
		boolean networkAvailable = DeviceUtil.isNetworkAvailable(context);
		if(!networkAvailable){
			saveTask(Constants.NET_TAG_REGISTER_INSTALLED_GAME, gameId);
		}else {
			NetUtil netUtil = NetUtil.getInstance();
			ArrayList<String> ids = new ArrayList<String>(1);
			ids.add(gameId);
			netUtil.requestRegisterGames(userId, sessionId, ids, new RegisterGameListener(gameId));
		}
		
	}
	
	/**
	 * 上传设备下载游戏信息 tag = 202 
	 * @param context
	 * @param gameId
	 */
	public void submitGame(String gameId,String packageName,Long downloadId){
		/*if(!checkTask(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, gameId)){
			return ;
		}*/
		
		try {
			if (Constants.DEBUG)Log.i("wangliangtest", "[FutureTaskManager#submitGame]gameId:"+gameId+" packageName:"+packageName);
			if(packageName != null && downloadId >0){
				AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
				manager.saveMyDownloadedGame(gameId, packageName,downloadId);
			}
			
			boolean networkAvailable = DeviceUtil.isNetworkAvailable(GameTingApplication.getAppInstance());
			if(!networkAvailable){
				saveTask(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, gameId);
			}else {
				cacheTask(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, gameId);
				
				NetUtil netUtil = NetUtil.getInstance();
				ArrayList<String> ids = new ArrayList<String>(1);
				ids.add(gameId);
				netUtil.requestForUploadDownloadedGames(DeviceId.getDeviceID(GameTingApplication.getAppInstance()),
						ids, new UploadGameListener(gameId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 获取已经下载的游戏
	 */
	public void requestDownloadedGames(){
		Log.d(TAG, "[requestDownloadedGames]");
		boolean networkAvailable = DeviceUtil.isNetworkAvailable(GameTingApplication.getAppInstance());
		if(!networkAvailable){
			saveTask(Constants.NET_TAG_GET_DOWNLOADED_GAMES, DeviceId.getDeviceID(GameTingApplication.getAppInstance()));
		}else {
			NetUtil netUtil = NetUtil.getInstance();
			netUtil.requestForDownloadedGames(DeviceId.getDeviceID(GameTingApplication.getAppInstance()), new RequestDownloadedGamesListener());
		}
	}
	
	/**
	 * 获取下载过的游戏
	 * @param list
	 */
	private void requestDownloadedGames(List<TaskMode> list){
		List<Long> taskIds = new ArrayList<Long>(1);
		try {
			taskIds.add(list.get(0).getTaskId());
			//不应该出错的
			NetUtil netUtil = NetUtil.getInstance();
			netUtil.requestForDownloadedGames(DeviceId.getDeviceID(GameTingApplication.getAppInstance()), new CommonListener(taskIds));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private CopyOnWriteArraySet<TaskMode> tasks = new CopyOnWriteArraySet<TaskMode>();
	
	private boolean checkTask(int tag,String extra){
		for (TaskMode t : tasks) {
			if(t.getTaskTag() == tag && extra.equals(t.getTaskExtra())){
				return false ;
			}
		}
		return true ;
	}
	
	private void removeCacheTask(int tag,String extra){
	}
	
	private void cacheTask(int tag,String extra){
		tasks.add(new TaskMode(-1, tag, extra));
	}
	
	
	
	private void saveTask(int taskTag,String extra){
		CommonDao dbHandler = DbManager.getCommonDbHandler();
		dbHandler.saveTask(taskTag, extra);
	}
	
	private void removeTasks(List<Long> taskIds){
		CommonDao dbHandler = DbManager.getCommonDbHandler();
		dbHandler.removeTask(taskIds);
	}
	private List<TaskMode>  getTasks(){
		CommonDao dbHandler = DbManager.getCommonDbHandler();
		List<TaskMode> tasks = dbHandler.getTasks();
		return  tasks ;
	}
	private Map<Integer, List<TaskMode>>  spliteTasks(List<TaskMode> tasks){
		HashMap<Integer, List<TaskMode>> taskMap = new HashMap<Integer, List<TaskMode>>();
		for (TaskMode taskMode : tasks) {
			int taskTag = taskMode.getTaskTag();
			if(taskTag == Constants.NET_TAG_WHITE_LIST){
				List<TaskMode> list = taskMap.get(Constants.NET_TAG_WHITE_LIST);
				if(list == null){
					list = new ArrayList<FutureTaskManager.TaskMode>();
					taskMap.put(Constants.NET_TAG_WHITE_LIST, list);
				}
				list.add(taskMode);
			}else if(taskTag == Constants.NET_TAG_REGISTER_INSTALLED_GAME){
				List<TaskMode> list = taskMap.get(Constants.NET_TAG_REGISTER_INSTALLED_GAME);
				if(list == null){
					list = new ArrayList<FutureTaskManager.TaskMode>();
					taskMap.put(Constants.NET_TAG_REGISTER_INSTALLED_GAME, list);
				}
				list.add(taskMode);
			}else if(taskTag == Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES){
				List<TaskMode> list = taskMap.get(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES);
				if(list == null){
					list = new ArrayList<FutureTaskManager.TaskMode>();
					taskMap.put(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, list);
				}
				list.add(taskMode);
			}else if(taskTag == Constants.NET_TAG_GET_DOWNLOADED_GAMES){
				List<TaskMode> list = taskMap.get(Constants.NET_TAG_GET_DOWNLOADED_GAMES);
				if(list == null){
					list = new ArrayList<FutureTaskManager.TaskMode>();
					taskMap.put(Constants.NET_TAG_GET_DOWNLOADED_GAMES, list);
				}
				list.add(taskMode);
			}
		}
		return taskMap ;
	}
	
	
	
	//////////////////////////////////////////////////////
	class CommonListener implements IRequestListener{
		private List<Long> taskIds ;
		public CommonListener(List<Long> taskIds) {
			this.taskIds = taskIds ;
		}
		@Override
		public void onRequestSuccess(final BaseResult responseData) {
			new Thread(){
				@Override
				public void run() {
					if(responseData.getErrorCode() == DcError.DC_OK){
						//请求成功
						int tag = StringUtil.parseInt(responseData.getTag());
						if(tag == Constants.NET_TAG_WHITE_LIST){
							WhiteList whiteList = (WhiteList) responseData ;
							List<BaseAppInfo> data = whiteList.getData();
							if(data != null){
								AppDao appDbHandler = DbManager.getAppDbHandler();
								appDbHandler.updateWhiteList(data);
							}
						}else if(tag == Constants.NET_TAG_REGISTER_INSTALLED_GAME){
							
						}else if(tag == Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES){
							
						}else if(tag == Constants.NET_TAG_GET_DOWNLOADED_GAMES){
							MyDownloadedGames list = (MyDownloadedGames) responseData ;
							List<MyDownloadedGame> data = list.getData();
							if(data != null){
								saveMyDownloadedGames(data);
							}
							
						}
						//删除任务
						removeTasks(taskIds);
					}
				}
			}.start();
			
		}
		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode,
				String msg) {
			Log.e(TAG, "onRequestError for tag:"+requestTag+" error message:"+msg);
			
		}
	}
	
	
	class VerifyGameListener implements IRequestListener{
		private String packageName ;
		public VerifyGameListener(String packageName) {
			this.packageName = packageName ;
		}
		@Override
		public void onRequestSuccess(final BaseResult responseData) {
			new Thread(){
				@Override
				public void run() {
					try {
						Log.i( AppSilentInstaller.TAG, "VerifyGameListener onRequestSuccess");
						if(responseData.getErrorCode() == DcError.DC_OK){
							//请求成功
							WhiteList whiteList = (WhiteList) responseData ;
							List<BaseAppInfo> data = whiteList.getData();
							if(data != null){
								AppDao appDbHandler = DbManager.getAppDbHandler();
								appDbHandler.updateWhiteList(data);
								//AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
								//manager.removeDownloadRecordIfNecessary(packageName, downloadId)
								BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
								sender.notifyWhiteListInitlized();
							}
						}else{
							saveTask(Constants.NET_TAG_WHITE_LIST, packageName);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			
			
			
		}
		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode,
				String msg) {
			new Thread(){
				@Override
				public void run() {
					Log.e( AppSilentInstaller.TAG, "VerifyGameListener onRequestError");
					saveTask(Constants.NET_TAG_WHITE_LIST, packageName);
				}
			}.start();
			
			
		}
	}
	
	class RegisterGameListener implements IRequestListener{
		private String gameId ;
		
		public RegisterGameListener(String gameId) {
			this.gameId = gameId ;
		}
		@Override
		public void onRequestSuccess(final BaseResult responseData) {
			new Thread(){
				@Override
				public void run() {
					if(responseData.getErrorCode() == DcError.DC_OK){
						//请求成功
					}else{
						saveTask(Constants.NET_TAG_REGISTER_INSTALLED_GAME, gameId);
					}
					
				}
			}.start();
			
		}
		@Override
		public void onRequestError(final int requestTag, int requestId, int errorCode,
				final String msg) {
			new Thread(){
				@Override
				public void run() {
					Log.e(TAG, "onRequestError for tag:"+requestTag+" error message:"+msg);
					saveTask(Constants.NET_TAG_REGISTER_INSTALLED_GAME, gameId);
				}
			}.start();
			
			
		}
	}
	
	class UploadGameListener implements IRequestListener{
		private String gameId ;
		
		public UploadGameListener(String gameId) {
			this.gameId = gameId ;
		}
		@Override
		public void onRequestSuccess(final BaseResult responseData) {
			new Thread(){
				@Override
				public void run() {
					if (Constants.DEBUG)Log.i("wangliangtest", "[UploadGameListener#onRequestSuccess]"+responseData);
					if(responseData.getErrorCode() == DcError.DC_OK){
						//请求成功
					}else{
						saveTask(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, gameId);
					}
					
				}
			}.start();
			
		}
		@Override
		public void onRequestError(final int requestTag, int requestId, int errorCode,
				final String msg) {
			new Thread(){
				@Override
				public void run() {
					if (Constants.DEBUG)Log.i("wangliangtest", "[UploadGameListener#onRequestError]msg:"+msg);
					Log.e(TAG, "onRequestError for tag:"+requestTag+" error message:"+msg);
					saveTask(Constants.NET_TAG_UPLOAD_DOWNLOADED_GAMES, gameId);
				}
			}.start();
			
			
			
		}
	}
	
	private void saveMyDownloadedGames(List<MyDownloadedGame> data){
		try {
			if (Constants.DEBUG)Log.i("wangliangtest", "[FutureTaskManager#saveMyDownloadedGames]requestDownloadedGames");
			AppDao appDbHandler = DbManager.getAppDbHandler();
			try {
				appDbHandler.addMyDownloadedGames(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//TODO:这是有问题的 
			HashMap<String, String> hashMap = new HashMap<String,String>();
			for (MyDownloadedGame d : data) {
				hashMap.put(d.getPackageName(), d.getGameId());
			}
			//Log.i(TAG, "Upload Downloaded Games success,updateInstalledGameIds()");
			appDbHandler.updateInstalledGameIds(hashMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	class RequestDownloadedGamesListener implements IRequestListener{
		
		public RequestDownloadedGamesListener() {
		}
		@Override
		public void onRequestSuccess(final BaseResult responseData) {
			new Thread(){
				@Override
				public void run() {
					if(responseData.getErrorCode() == DcError.DC_OK){
						try {
							Log.d(TAG, "Request Downloaded Games success...");
							//请求成功
							MyDownloadedGames list = (MyDownloadedGames) responseData ;
							List<MyDownloadedGame> data = list.getData();
							if(data != null){
								saveMyDownloadedGames(data);
							}
						} catch (Exception e) {
						}
						
						
					}else{
						saveTask(Constants.NET_TAG_GET_DOWNLOADED_GAMES, DeviceId.getDeviceID(GameTingApplication.getAppInstance()));
					}
				}
			}.start();
			
			
		}
		@Override
		public void onRequestError(final int requestTag, int requestId, int errorCode,
				final String msg) {
			new Thread(){
				@Override
				public void run() {
					Log.e(TAG, "Request Downloaded Games for tag:"+requestTag+" error message:"+msg);
					saveTask(Constants.NET_TAG_GET_DOWNLOADED_GAMES, DeviceId.getDeviceID(GameTingApplication.getAppInstance()));
				}
			}.start();
			
			
		}
	}
	
	
	

}
