package com.andconsd.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.andconsd.utils.Constants;

public class FileItemService {
	/**
	 * 
	 * @param pathOnServer
	 * @return
	 */
	public List<FileItem> getFileItems(String pathOnServer, String root_dir) {
		List<FileItem> fileItems = new ArrayList<FileItem>();
		File root = new File(pathOnServer);
		int index = 1;
		for (File file : root.listFiles()) {
			FileItem fileItem = new FileItem();
			fileItem.setIsFile(file.isFile());
			fileItem.setName(file.getName());
			fileItem.setIndex(index++);
			if (pathOnServer.equals("/")) {
				pathOnServer = "";
			}
			fileItem.setLink(file.getAbsolutePath().replaceAll(root_dir, ""));
			fileItems.add(fileItem);
		}
		return fileItems;
	}
}
