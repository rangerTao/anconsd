package com.ranger.phonerecorder.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class UploadFileTask extends AsyncTask<String, Void, String> {

	public static final String requestURL = "http://172.16.10.180:8090/comicserver/1/upload.php";

//	private CustomProgressDialog progressDialog;
	private Context context = null;
	private Handler mHandler;
	
	public final static String SUCCESS = "1";
	public final static String FAILURE = "0";	

	public UploadFileTask(Context ctx,Handler handler) {
		this.context = ctx;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
//		progressDialog = CustomProgressDialog.createDialog(context);
//		progressDialog.setMessage(context.getString(R.string.committing_tip));
//		progressDialog.show();
	}

	/**
	 * 现在不需要传入头像类型参数了，只要自定义头像调用此方法。
	 * 当前只传入一个参数:头像文件在本地的全路径
	 */
	@Override
	protected String doInBackground(String... params) {
		File file = new File(params[0]);
		UploadUtils uploadUtils = new UploadUtils();
		return uploadUtils.uploadFile(file, requestURL);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}

	@Override
	protected void onPostExecute(String result) {
//		progressDialog.dismiss();
//		Message uploadResult = mHandler.obtainMessage(HeadPhotoActivity.PHOTO_UPLOAD_MSG);
//		uploadResult.obj = result;
//		uploadResult.sendToTarget();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}


	class UploadUtils {
		private static final int TIME_OUT = 60 * 1000; // 超时时间
		/**
		 * 获取响应码 200=成功 当响应成功，获取响应的流
		 * @param file
		 * @param RequestURL
		 * @return
		 */
		public String uploadFile(File file, String requestURL) {
			String result = FAILURE;
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(requestURL);
			MultipartEntity multipart = new MultipartEntity();
			HttpResponse response = null;
			try {
				multipart.addPart("remind", new FileBody(file));
				post.setEntity(multipart);
				response = client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					response = client.execute(post);
				} catch (Exception ex) {
				}
			}
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream;
				try {
					inputStream = response.getEntity().getContent();
					InputStreamReader isr = new InputStreamReader(inputStream);
					BufferedReader bufferReader = new BufferedReader(isr);
					StringBuffer responsestr = new StringBuffer();
					String temp=null;
					while((temp=bufferReader.readLine())!=null){
						responsestr.append(temp);
					}
					
					Log.d("TAG", responsestr.toString());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					result = SUCCESS;
				}
			}
			return result;
		}
	}
}