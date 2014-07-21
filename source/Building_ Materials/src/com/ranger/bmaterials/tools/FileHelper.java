/**

 * @author huzexin@duoku.com

 * @version CreateData��2012-5-10 3:46:54 PM

 */
package com.ranger.bmaterials.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import android.graphics.AvoidXfermode.Mode;
import android.os.Environment;

import com.ranger.bmaterials.*;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.tools.MyLogger;


public final class FileHelper {
	
	/*
	 * @Judge whether the file exist with specify path
	 */
	public static boolean fileIfExists(String filepath){
        File file = new File(filepath);
        if (file.exists())
            return true;
        return false;
	}
	
	/*
	 * @Remove a file or directory According to file path
	 */
	public static boolean removeFile(String filepath){
		
		File file = new File(filepath);
		if (file.exists()){
			return removeFile(file);
		}
		return false;
	}
	
	
	public static String getJsonStringFromJsonFile(String filePath) {

		String result = "";
		if (fileIfExists(filePath)) {
			FileInputStream fis = null;
			ByteArrayOutputStream outBa = null;
			DataOutputStream dos = null;
			try {
				fis = new FileInputStream(new File(filePath));
				outBa = new ByteArrayOutputStream();
				dos = new DataOutputStream(outBa);
				int currentCount = 0;
				byte[] tempBuffer = new byte[1024];
				while ((currentCount = fis.read(tempBuffer)) != -1) {
					dos.write(tempBuffer, 0, currentCount);
				}
				dos.flush();
				result = outBa.toString();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

			}

		}
		return result;
	}
	
	
	/*
	 * @Remove a file or directory According to an file instance
	 */
	public static boolean removeFile(File file){
		if (file != null && file.isDirectory()){
			
			String[] childlist = file.list();
			
			for(int i =0; i < childlist.length; i++){
				boolean success = removeFile(new File(file, childlist[i]));
				
				if (!success){
					return false;
				}
			}
		}else if (file != null){
			return file.delete();
		}
		return false;
	}
	
	public static boolean removeFile(File folder,Set<String> excludedFiles){
		if (folder != null &&folder.isDirectory()){
			File[] childList = folder.listFiles(new MyFilenameFilter(excludedFiles));
			if(childList == null){
				return true;
			}
			for(int i =0; i < childList.length; i++){
				File file = childList[i];
				if(file != null){
					
					try{
						boolean r = file.delete();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			return true ;
			
		}else if (folder != null){
			return folder.delete();
		}
		return false;
	}
	
	static class MyFilenameFilter implements FilenameFilter{
		Set<String> excludedFiles ;
		public MyFilenameFilter(Set<String> excludedFiles) {
			this.excludedFiles= excludedFiles ;
		}
		@Override
		public boolean accept(File dir, String filename) {
			if(excludedFiles.contains(filename)){
				return false ;
			}
			return true;
		}
	}
	
	/*
	 * @Get file size in byte with specify file path
	 */
	public static long getFileSize(String filepath){
		File f = null;
		FileInputStream fs = null;
		byte[] buffer = new byte[512];
		int totalCount = 0;
		
		try{
			f = new File(filepath);
			fs = new FileInputStream(f);
			
			int currentReadCount = 0;
			
			while ((currentReadCount = fs.read(buffer)) != -1 ){
				totalCount += currentReadCount;
			}
			
			fs.close();
			
		}catch (Exception e){
			
		}
		
		return totalCount;
	}
	
	/*
	 * @Check whether support sd card
	 */
	public static boolean isSupportSDCard(){ 
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * @Create directory with folder and the filepath should
	 * be like ""
	 */
	public static boolean createDirectory(String filepath){
		
		boolean r = isSupportSDCard();
		File file = null;
		if (r){
			file = Environment.getExternalStorageDirectory();
		}else{
			//file = ReaderApplication.instance().getFilesDir();
		}
		
		MyLogger.getLogger("FileHelper").v(file.toString());
		
		File newfile = new File(file,filepath);
		
		if (!newfile.exists())
			r = newfile.mkdirs();
			
		return r;
	}
	
	public static String getAppDataDirectoryPath(){
		boolean r = isSupportSDCard();
		File file = null;
		if (r){
			file = Environment.getExternalStorageDirectory();
		}else{
			file = GameTingApplication.getAppInstance().getFilesDir();
		}
		
		return file.getPath();
	}
	
	public static String getFileMd5(String path) {
		 MessageDigest messageDigest = null;  
		    try {  
		        messageDigest = MessageDigest.getInstance("MD5");  
		        FileInputStream in = new FileInputStream(path);  
		        byte[] buffer = new byte[2048];  
		        int len = 0;  
		        while ((len = in.read(buffer)) > 0) {  
		            messageDigest.update(buffer, 0, len);  
		        }  
		        in.close();  
		    } catch (Exception e) {  
		    	e.printStackTrace();
		        return "";  
		    }  
		    return AppUtil.toHexString(messageDigest.digest());  
	}

	public static void saveResultToLogFile(String res , String filename,boolean isappend){
		
		if(!Constants.DEBUG)
			return;
		
		try {
			File file = new File(Environment.getExternalStorageDirectory()  + File.separator + filename);
			if(!file.exists()){
				file.createNewFile();
			}
			
			FileWriter fWriter = new FileWriter(file,isappend);
			fWriter.write(res);
			fWriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
