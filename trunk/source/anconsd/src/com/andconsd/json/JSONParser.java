package com.andconsd.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.andconsd.net.response.BaseResult;
import com.andconsd.pojos.Picture;
import com.andconsd.pojos.PictureList;
import com.andconsd.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.os.Environment;
import android.util.Log;

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
	
	public static PictureList parseBaiduBeautyList(int page,String res) {
		
		PictureList pl = new PictureList();
		
		pl.setPage(++page +"");
		
		ArrayList<Picture> pics = new ArrayList<Picture>();
		try {
			JSONObject json = new JSONObject(res);
			
			
			JSONArray imgs = json.getJSONArray("imgs");
			
			for(int i = 0;i< imgs.length();i++){
				JSONObject img = imgs.getJSONObject(i);
				
				Picture pic = new Picture();
				
				if(img.has("thumbnailWidth"))
					pic.setWidth(img.getString("thumbnailWidth"));
				if(img.has("thumbnailHeight"))
					pic.setHeight(img.getString("thumbnailHeight"));
				if(img.has("title"))
					pic.setDescp(img.getString("title"));
				if(img.has("objUrl"))
					pic.setUrl(img.getString("objUrl"));
				
				pics.add(pic);
			}
			
			pl.setPics(pics);
			
		} catch (Exception e) {
			pl.setPics(pics);
			e.printStackTrace();
		}
		
		return pl;
	}
	
	public static <T extends BaseResult> T parseJson(String resData, Class<T> cls) {
		Gson gs = new Gson();
		return gs.fromJson(resData, cls);
	}
}
