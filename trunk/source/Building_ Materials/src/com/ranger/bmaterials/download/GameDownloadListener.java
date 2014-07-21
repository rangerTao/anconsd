package com.ranger.bmaterials.download;

public interface GameDownloadListener {
	void onDownloadStart();
	
	void onDownloadProgress(int progress);
	
	void onDownloadEnd();
	
	void refreshGameState(String state);
}
