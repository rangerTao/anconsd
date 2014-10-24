/**

 * @author huzexin@duoku.com

 * @version CreateData��2012-5-10 3:46:54 PM

 */

package com.andconsd.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.FutureTask;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import com.andconsd.AndApplication;
import com.andconsd.DcError;
import com.andconsd.R;
import com.andconsd.json.JSONHelper;
import com.andconsd.net.NetMessage.NetMessageType;
import com.andconsd.net.response.BaseResult;
import com.andconsd.utils.FileHelper;
import com.andconsd.utils.MyLogger;

import android.os.Handler;
import android.os.Message;

// modify by wenzutong, modify Runnable to Callable
public class ASIHttpRequest implements Runnable {

	private MyLogger mLogger = MyLogger.getLogger(ASIHttpRequest.class.getName());
	private FutureTask<Object> mTask = new FutureTask<Object>(this, null);
	private static final int BUFFER_SIZE = 1024 * 10; // 10k byte
	// default support resume broken transfer
	private String mUrl;
	private boolean mStop = false;
	private String mDownloadDstFilePath;
	private int mTimeOutSeconds = 60 * 1000;
	private String mAccpetEcoding;
	private boolean mIsDownLoad = false;
	private String mContentType;
	private String mRequestMethod;
	private String mRequestData;
	private Handler mCbkHandler;
	private int mRequestTag;
	// indicate whether support Resume broken transfer
	private boolean mIsSupportBt = true;
	private int mRepeatCount = 0;

	public Handler getCbkHandler() {
		return this.mCbkHandler;
	}

	public void setCbkHandler(Handler cbkhandler) {
		this.mCbkHandler = cbkhandler;

	}

	public int getRequestTag() {
		return this.mRequestTag;
	}

	public void setRequestTag(int requestTag) {
		this.mRequestTag = requestTag;
	}

	public String getRequestData() {
		return mRequestData;
	}

	public void setRequestData(String requestdata) {
		this.mRequestData = requestdata;
	}

	public String getRequestMethod() {

		return mRequestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.mRequestMethod = requestMethod;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public boolean getStop() {
		return mStop;
	}

	public void cancelRequest() {
		this.mStop = true;
	}

	public String getDownloadDstFilePath() {
		return mDownloadDstFilePath;
	}

	public void setDownloadDstFilePath(String downloadDstFilePath) {
		this.mDownloadDstFilePath = downloadDstFilePath;
		this.mIsDownLoad = true;
	}

	public int getTimeOutSeconds() {
		return mTimeOutSeconds;
	}

	public void setTimeOutSeconds(int mTimeOutSeconds) {
		this.mTimeOutSeconds = mTimeOutSeconds;
	}

	public String getAccpetEcoding() {
		return mAccpetEcoding;
	}

	public void setAccpetEcoding(String mAccpetEcoding) {
		this.mAccpetEcoding = mAccpetEcoding;
	}

	public boolean isIsDownLoad() {
		return mIsDownLoad;
	}

	public void setIsDownLoad(boolean isDownLoad) {
		this.mIsDownLoad = isDownLoad;
	}

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		this.mContentType = contentType;
	}

	public void sendCbkMessage(Message msg) {

		if (this.mCbkHandler != null) {

			if (msg == null) {
				Message _msg = new Message();
				_msg.what = 9;

				mCbkHandler.sendMessage(_msg);
			} else {
				mCbkHandler.sendMessage(msg);
			}
		}
	}

	private OutputStream initOutPutIO() throws IOException {
		OutputStream res = null;

		if (this.mIsDownLoad) {
			// create the folder
			new File(mDownloadDstFilePath.substring(0, mDownloadDstFilePath.lastIndexOf("/"))).mkdirs();

			// use a temp file and rename after finish download
			if (mIsSupportBt) {
				res = new FileOutputStream(mDownloadDstFilePath + ".temp", true);
			} else {
				res = new FileOutputStream(mDownloadDstFilePath + ".temp");
			}
		} else {
			res = new ByteArrayOutputStream();
		}
		return res;
	}

	private void handleNetRequest() {
		// add by wenzutong, 2012-08-24
		HttpClient client = HttpClientHelper.getHttpClient();
		HttpPost postRequest = null;
		HttpGet getRequest = null;
		HttpResponse response = null;
		// end
		OutputStream outputio = null;
		InputStream inio = null;

		try {
			// do {
			byte[] bs = null;
			if (mDownloadDstFilePath != null) {
				setIsDownLoad(true);
				getRequest = HttpClientHelper.getHttpGetRequest(mUrl);
				if (this.mIsSupportBt) {
					long tempfileSize = FileHelper.getFileSize(mDownloadDstFilePath + ".temp");
					getRequest.setHeader("RANGE", "bytes=" + tempfileSize + "-");
				}
				try {

					response = client.execute(getRequest);
				} catch (Exception e) {
					client = HttpClientHelper.getHttpClient();
					try {
						response = client.execute(getRequest);
					} catch (Exception ex) {
						client = HttpClientHelper.getHttpClient();
						try {
							response = client.execute(getRequest);
						} catch (Exception ex1) {
							handleErrorEvent(AndApplication.getAppInstance().getString(R.string.alert_network_inavailble), DcError.DC_NET_GENER_ERROR);
							mTask.cancel(true);
							return;
						}
					}
				}
			} else {

				postRequest = HttpClientHelper.getHttpPostRequest(mUrl);

				setIsDownLoad(false);
				StringEntity reqEntity = new StringEntity(mRequestData, "UTF-8");
				postRequest.setEntity(reqEntity);
				if (this.mStop) {
					mTask.cancel(true);
					handleCancelEvent("cancel before write data to pipe");
					return;
				}
				try {
					response = client.execute(postRequest);
				} catch (Exception e) {
					client = HttpClientHelper.getHttpClient();
					try {
						response = client.execute(postRequest);
					} catch (Exception ex) {
						client = HttpClientHelper.getHttpClient();
						try {
							response = client.execute(postRequest);
						} catch (Exception ex1) {
							handleErrorEvent(AndApplication.getAppInstance().getString(R.string.alert_network_inavailble), DcError.DC_NET_GENER_ERROR);
							mTask.cancel(true);
							return;
						}
					}
				}
			}

			outputio = this.initOutPutIO();

			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
				int currentReadbyteCount = 0;
				long responseDataLen = response.getEntity().getContentLength();
				long havedownDataSize = 0;
				inio = response.getEntity().getContent();

				// 2.3.1以上版本支持gzip
				BufferedInputStream bis = new BufferedInputStream(inio);
				// 兼容wap网关的方式判断是否为gzip格式
				bis.mark(2);
				// 取前两个字节
				byte[] header = new byte[2];
				int result = bis.read(header);
				// reset输入流到开始位置
				bis.reset();
				// 判断是否是GZIP格式
				int headerData = (int) ((header[0] << 8) | header[1] & 0xFF);
				// Gzip 流 的前两个字节是 0x1f8b
				if (result != -1 && headerData == 0x1f8b) {
					// 一般判断方式
					// if
					// (response.getEntity().getContentEncoding().getValue()
					// .contains("gzip")) {
					inio = new GZIPInputStream(bis);
				} else {
					inio = bis;
				}

				byte[] tempBytes = new byte[BUFFER_SIZE];

				if (this.mIsDownLoad && this.mIsSupportBt) {
					havedownDataSize = FileHelper.getFileSize(mDownloadDstFilePath + ".temp");
					responseDataLen += havedownDataSize;
				}
				while ((currentReadbyteCount = inio.read(tempBytes)) != -1) {
					if (this.mStop) {
						mTask.cancel(true);
						break;
					}
					havedownDataSize += currentReadbyteCount;
					// if is download request
					// then need create the progress event and
					// handle the event to UI layer
					if (this.mIsDownLoad) {
						handleDownLoadingEvent(responseDataLen, havedownDataSize);
					}
					outputio.write(tempBytes, 0, currentReadbyteCount);
				}
				if (this.mStop) {
					mTask.cancel(true);
					handleCancelEvent("cancel after read data from pipe");
					return;
				}

//				if (responseDataLen != -1) {
					// 压缩后的字节长度会不一致
					// if (havedownDataSize != responseDataLen) {
					// error happen
					// handleErrorEvent(
					// GameTingApplication
					// .getAppInstance()
					// .getString(
					// R.string.alert_network_inavailble),
					// DcError.DC_NET_DATA_ERROR);
					// break;
					// } else if (havedownDataSize == responseDataLen) {

					if (!this.mIsDownLoad) {
						bs = ((ByteArrayOutputStream) outputio).toByteArray();
					} else {
						File file = new File(mDownloadDstFilePath + ".temp");
						file.renameTo(new File(mDownloadDstFilePath));
					}
					handleSuccessEvent(mUrl,bs); 
					// }
//				} else {
//					MyLogger.getLogger(this.getClass().getName()).v("response data length is -1");
//					{
//						// When download file
//						handleErrorEvent("content len is error", DcError.DC_NET_GENER_ERROR);
//					}
//
//				}
			} else if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY || responseCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				String redirectUrl = response.getFirstHeader("location").getValue();
				if (redirectUrl != null && redirectUrl.length() > 0) {
					setUrl(redirectUrl);
					handleNetRequest();
				}
			} else {
				if (responseCode == HttpStatus.SC_GATEWAY_TIMEOUT) {
					handleErrorEvent("connect time out", DcError.DC_NET_TIME_OUT);
				} else if (responseCode == -1 && mRepeatCount == 0) {
					mRepeatCount = 1;
					handleNetRequest();
				} else {
					String codestr = String.format("Net Error Code: %d", responseCode);
					String msgstr = String.format("Net Error Msg: %s", response.getStatusLine().getReasonPhrase());

					MyLogger.getLogger(this.getClass().getName()).v(codestr);
					MyLogger.getLogger(this.getClass().getName()).v(msgstr);

					handleErrorEvent("net error", DcError.DC_NET_GENER_ERROR);
				}
			}
			//
			// } while (false);

		} catch (Exception e) {
			e.printStackTrace();
			MyLogger.getLogger(this.getClass().getName()).v(e.toString());
			handleErrorEvent("exception happen", DcError.DC_NET_GENER_ERROR);
		} finally {

			// close the io pipe
			if (inio != null) {
				try {
					inio.close();
				} catch (Exception e) {
				}
				inio = null;
			}

			if (outputio != null) {
				try {
					outputio.close();
				} catch (Exception e) {
				}
				outputio = null;
			}
		}
	}

	private void handleDownLoadingEvent(long totalSize, long currentSize) {
		// create custom message instance and
		// set parameter
		NetMessage msg = new NetMessage();
		msg.setMessageType(NetMessage.NetMessageType.NetDownloadling);
		msg.setTotalSize(totalSize);
		msg.setCurentSize(currentSize);
		msg.setRequestId(this.hashCode());

		// create system message instance
		Message sysmsg = new Message();
		sysmsg.obj = msg;

		sendCbkMessage(sysmsg);
	}

	private void handleErrorEvent(String netError, int errorCode) {

		NetMessage msg = new NetMessage();
		if (this.mIsDownLoad) {
			msg.setMessageType(NetMessage.NetMessageType.NetDownloadFailure);
			File file = new File(mDownloadDstFilePath);
			file.delete();
		} else {
			msg.setMessageType(NetMessageType.NetFailure);
			msg.setErrorCode(errorCode);
		}
		msg.setErrorString(netError);
		msg.setRequestId(this.hashCode());

		// create system message instance
		Message sysmsg = new Message();
		sysmsg.obj = msg;

		sendCbkMessage(sysmsg);
	}

	private void handleSuccessEvent(String url,byte[] responseStr) {
		NetMessage msg = new NetMessage();
		if (this.mIsDownLoad) {
			msg.setMessageType(NetMessage.NetMessageType.NetDownloadSuccess);

		} else {
			msg.setMessageType(NetMessageType.NetSuccess);

			String resString = "";
			try {
				
				resString = new String(responseStr, "UTF-8");
				mLogger.v("response data is" + resString);
				BaseResult res = JSONHelper.parserWithTag(this.mRequestTag, resString);
				// BaseResult res = JSONHelper.parserWithTag(this.mRequestTag,
				// new String(responseStr, "UTF-8"));
				msg.setResponseData(res);

			} catch (JSONException e) {
				// 捕获JSON异常
				e.printStackTrace();
				msg.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				msg.setErrorString("parse json error");
			} catch (Exception ex) {
				// net request failed
				ex.printStackTrace();
				msg.setErrorCode(DcError.DC_NET_DATA_ERROR);
				msg.setErrorString("receive data error");
			} finally {

			}
		}
		msg.setRequestId(this.hashCode());

		// create system message instance
		Message sysmsg = new Message();
		sysmsg.obj = msg;

		sendCbkMessage(sysmsg);
	}

	/*
	 * @Transfer request cancel event to UI layer
	 * 
	 * @Param cancelStr specify the cancel reason and not use yet
	 */
	private void handleCancelEvent(String cancelStr) {

		MyLogger.getLogger(this.getClass().getName()).v(cancelStr);

		NetMessage msg = new NetMessage();
		msg.setMessageType(NetMessageType.NetCancel);
		msg.setRequestId(this.hashCode());

		// create system message instance
		Message sysmsg = new Message();
		sysmsg.obj = msg;

		sendCbkMessage(sysmsg);
	}

	// public String call() throws Exception {
	// handleNetRequest();
	// return null;
	// }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		handleNetRequest();
	}
}
