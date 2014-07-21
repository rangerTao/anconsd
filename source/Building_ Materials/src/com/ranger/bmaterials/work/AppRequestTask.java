package com.ranger.bmaterials.work;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.db.AppDao;
import com.ranger.bmaterials.db.DbManager;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class AppRequestTask {

	private static final String TAG = "AppRequestTask";


	// 是否是忽略的更新
	// 如果本地已安装的应用的versioncode比服务器下发的新的还要大，则不需要更新
	// 如果是已下载更新，则也不更新
	// 如果本地可更新数据已经是新的，且签名一致，则不需要再更新。
	// 如果本地可更新应用的签名不一致，但是服务器的数据签名一致，则需要更新
	public static /*List<UpdatableItem>*/void  requestUpdatableList(/*List<InstalledAppInfo> list,*/IRequestListener listener){
		AppDao appDbHandler = DbManager.getAppDbHandler();
		List<InstalledAppInfo> allInstalledGames = appDbHandler.getAllInstalledGames();
		NetUtil netUtil = NetUtil.getInstance();
		netUtil.requestForUpdateGames(allInstalledGames, listener);
		
		
		
//		//TODO
//		Context context = GameTingApplication.getAppInstance().getApplicationContext();
//		List<String> load = null;
//		try {
//			load = load(context);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Random random = new Random();
//		List<UpdatableItem> ret = new ArrayList<UpdatableItem>();
//		for (int i = 0; i < load.size(); i++) {
//			UpdatableItem updatableItem = new UpdatableItem();
//			updatableItem.setNewVersion("10");
//			updatableItem.setNewVersionInt(200);
//			updatableItem.setPublishDate(new Date().getTime());
//			updatableItem.setNewSize(20000);
//			updatableItem.setIconUrl(imageThumbUrls[random.nextInt(imageThumbUrls.length)]);
//			updatableItem.setServerSign("sign");
//			updatableItem.setDownloadUrl(url[random.nextInt(url.length)]);
//			updatableItem.setPackageName(load.get(i));
//			ret.add(updatableItem);
//		}
//		return ret ;
	}
	
	
	
	private static List<String> load(Context context) throws IOException {
		ArrayList<String> arrayList = new ArrayList<String>();
		final Resources resources = context.getResources();
		InputStream inputStream = resources.openRawResource(R.raw.white_list);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] strings = TextUtils.split(line, "\\|");
				if (strings.length < 2)
					continue;
				arrayList.add(strings[1].trim());
			}
		} finally {
			reader.close();
		}
		return arrayList;
	}
}
