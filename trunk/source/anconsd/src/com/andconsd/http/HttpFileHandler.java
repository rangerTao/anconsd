package com.andconsd.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.drm.DrmStore.Action;
import android.nfc.Tag;
import android.util.Log;

import com.andconsd.AndApplication;
import com.andconsd.file.FileItem;
import com.andconsd.file.FileItemService;
import com.andconsd.template.UiTemplate;
import com.andconsd.ui.Androsd;
import com.andconsd.utils.Constants;
import com.andconsd.utils.SharePreferenceUtil;
import com.baidu.mobstat.StatService;

public class HttpFileHandler implements HttpRequestHandler {

	private final String docRoot;

	public HttpFileHandler(final String docRoot) {
		super();
		this.docRoot = docRoot;
	}

	public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST") && !method.equals("DELETE") && !method.equals("SHARE")) {
			throw new MethodNotSupportedException(method + " method not supported");
		}
		String target = request.getRequestLine().getUri();
		
		if (request instanceof HttpEntityEnclosingRequest) {
			String action = request.getRequestLine().getUri();
			if (action.equals("/upload")) {
				storeFile((HttpEntityEnclosingRequest) request);
			} else if (action.equals("/delete")) {
				deleteFile((HttpEntityEnclosingRequest) request);
			} 

			target = "";

		} else if (target.contains("/share/")) {
			try {
				shareFile(request,response);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			return;
		}

		final File file = new File(this.docRoot, URLDecoder.decode(target));
		if (!file.exists()) {
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {

				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
					writer.write("<html><body><h1>");
					writer.write("File ");
					writer.write(file.getPath());
					writer.write(" not found");
					writer.write("</h1></body></html>");
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);

		} else if (file.isDirectory()) {
			response.setStatusCode(HttpStatus.SC_OK);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
					List<FileItem> fileItems = new FileItemService().getFileItems(file.getAbsolutePath(),docRoot);

					StringBuffer tds = new StringBuffer();
					for (FileItem fileItem : fileItems) {
						tds.append(fileItem.toString());
					}
					String html = null;
					try {
						html = UiTemplate.generateHtml(file.getAbsolutePath(), tds.toString());
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					writer.write(html);
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);

		} else if (!file.canRead()) {
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {

				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
					writer.write("<html><body><h1>");
					writer.write("Access denied");
					writer.write("</h1></body></html>");
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);
			System.out.println("Cannot read file " + file.getPath());

		} else {
			response.setStatusCode(HttpStatus.SC_OK);
			FileEntity body = new FileEntity(file, "APPLICATION/OCTET-STREAM");
			response.setEntity(body);
			System.out.println("Serving file " + file.getPath());
		}
	}
	
	private void shareFile(HttpRequest request,HttpResponse response) throws IllegalStateException, IOException, NameNotFoundException {
//		StatService.onEvent(Androsd.appref, Constants.EVENT_DELETE_PIC, "Delete Start");
		
		String packageName = request.getRequestLine().getUri().replace("/share/", "");
		packageName = packageName.replace(Constants.PREFIX_PACKAGE_DEFAULT, "");
		
		PackageManager pm = AndApplication.getAppInstance().getPackageManager();
		ApplicationInfo aiPackage = pm.getApplicationInfo(packageName,0);
		
		if(aiPackage != null){
			
			String filePath = aiPackage.sourceDir;
			File apkFile = new File(filePath);
			
			if(apkFile.exists()){
				response.setStatusCode(HttpStatus.SC_OK);
				FileEntity body = new FileEntity(apkFile, "APPLICATION/OCTET-STREAM");

				response.setEntity(body);
				
				Log.d("TAG", "Serving file " + apkFile.getPath());
			}else{
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			}
		}
		
	}

	private void deleteFile(HttpEntityEnclosingRequest request) throws IllegalStateException, IOException {

		StatService.onEvent(Androsd.appref, Constants.EVENT_DELETE_PIC, "Delete Start");
		
		StringBuffer headerBuffer = new StringBuffer();
		HttpEntity entity = request.getEntity();

		int data = 0;
		InputStream inputStream = entity.getContent();
		while ((data = inputStream.read()) != -1) {
			char ch = (char) data;
			headerBuffer.append((char) data);
			if (ch == 10) {
				if (headerBuffer.substring(headerBuffer.length() - 4).equals(Constants.BLANK_LINE)) {
					break;
				}
			}
		}
		String path = this.docRoot;
		
		String filename = headerBuffer.toString();
		if (!filename.equals("")) {
			String[] input = filename.split("&");
			for (String test : input) {
				File file = new File(path + "/" + URLDecoder.decode(test.split("=")[1]));
				if (file.exists()) {
					file.delete();
				}
			}
			
			StatService.onEvent(Androsd.appref, Constants.EVENT_DELETE_SUCCESS, "Delete Success");
			
			notifyDatasetChanged();
		}

	}

	private boolean storeFile(HttpEntityEnclosingRequest request) throws IllegalStateException, IOException {

		StatService.onEvent(Androsd.appref, Constants.EVENT_UPLOAD_PIC, "Upload Start");
		int data = 0;
		long readed = 0;
		StringBuffer headerBuffer = new StringBuffer();
		HttpEntity entity = request.getEntity();
		InputStream inputStream = entity.getContent();
		while ((data = inputStream.read()) != -1) {
			readed++;
			char ch = (char) data;
			headerBuffer.append((char) data);
			if (ch == 10) {
				if (headerBuffer.substring(headerBuffer.length() - 4).equals(Constants.BLANK_LINE)) {
					break;
				}
			}
		}
		long contentLength = entity.getContentLength();
		long boundaryLength = getBoundaryLength(headerBuffer.toString());
		
		String path = this.docRoot;

		String filename = headerBuffer.substring(headerBuffer.indexOf("filename=\"") + 10, headerBuffer.lastIndexOf("\""));
		if (filename.equals("")) {
			return false;
		}
		if (filename.contains("\\")) {
			filename = filename.substring(filename.lastIndexOf("\\") + 1);
		}
		filename = path + "/" + new String(filename.getBytes("ISO-8859-1"), "UTF-8");
		File file = new File(filename);
		OutputStream outputStream = new FileOutputStream(file);
		int tempSize = 1024 * 12;
		byte[] temp = new byte[tempSize];
		long fileByteCount = contentLength - readed - (boundaryLength + 6);
		long fileByteLoaded = 0;
		int count = 0;
		while (fileByteLoaded <= fileByteCount) {
			long restByteCount = fileByteCount - fileByteLoaded;
			if (restByteCount < temp.length) {
				temp = new byte[(int) restByteCount];
				count = inputStream.read(temp, 0, temp.length);
				fileByteLoaded += count;
				outputStream.write(temp, 0, count);
				outputStream.flush();
				if (fileByteLoaded == fileByteCount) {
					break;
				}
				continue;
			}
			if ((count = inputStream.read(temp, 0, temp.length)) > 0) {
				fileByteLoaded += count;
				outputStream.write(temp, 0, count);
				outputStream.flush();
			}
		}
		outputStream.close();
		
		StatService.onEvent(Androsd.appref, Constants.EVENT_UPLOAD_SUCCESS, "Upload Success");
		
		notifyDatasetChanged();
		
		return true;
	}

	private long getBoundaryLength(String string) {
		String[] lines = string.split("\r\n");
		return Long.valueOf(lines[0].length());
	}

	private void notifyDatasetChanged() {
		Androsd.appref.handler.post(new Runnable() {

			@Override
			public void run() {
				Androsd.appref.notifyDatasetChanged();
			}

		});
	}
}
