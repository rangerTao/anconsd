package com.andconsd.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;

import com.andconsd.net.response.BaseResult;
import com.andconsd.pojos.PictureList;
import com.andconsd.utils.Constants;
import com.google.gson.Gson;

import android.os.Environment;

/**
 * @author wenzutong
 */
public class JSONParser {

	public static void saveLogToFile(final String data, final String name) {
		if (Constants.DEBUG) {
			new Thread() {

				@Override
				public void run() {
					String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name + ".txt";
					File file = new File(path);
					FileWriter fw = null;

					try {
						if (file.exists() == false) {
							file.createNewFile();
						}

						fw = new FileWriter(file);

						fw.write(data);
						fw.flush();
					} catch (Exception e) {
					} finally {
						if (null != fw) {
							try {
								fw.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

			}.start();
		}
	}

	/**
	 * 我的道具 tag = 305
	 * 
	 * @param resData
	 * @return
	 */
	public static BaseResult parseBeautyList(String resData) throws JSONException {
		
		return parseJson(resData, PictureList.class);
	}
	
	public static <T extends BaseResult> T parseJson(String resData, Class<T> cls) {
		Gson gs = new Gson();
		return gs.fromJson(resData, cls);
	}
}
