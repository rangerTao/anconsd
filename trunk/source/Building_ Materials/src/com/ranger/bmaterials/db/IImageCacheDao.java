/**
 * ����ҳ��ͼƬ����
 */
package com.ranger.bmaterials.db;

public interface IImageCacheDao {

	/*
	 * Get image cache file path 
	 * return cache file path, null if cache file not exist
	 */
	public abstract String getCacheFile(String url);
	
	/*
	 * Add an image cache to local
	 */
	public abstract void addCacheFile(String url, String localPath);
	
	/*
	 * Clean image cache
	 * TODO
	 */
	public abstract void cleanCache();
	
}
