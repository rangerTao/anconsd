package com.ranger.bmaterials.speeddownload;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.broadcast.SpeedDownloadReceiver;

public class SpeedDownCommandHandler implements HttpRequestHandler{

	private Context mContext = null;
	
	public SpeedDownCommandHandler(Context context) {
		this.mContext = context;
	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext httpcontext) throws HttpException, IOException {
//		HttpParams params = request.getParams();
//		Object callObj = params.getParameter("callback");
//		Object valueObj = params.getParameter("intent");

		Map<String, String> params = getParamsFromUrl(request.getRequestLine().getUri());
		Object callObj = params.get("callback");
		Object valueObj = params.get("intent");
		
		final String responseContent;
		if (callObj == null || valueObj == null) {
			responseContent = buildResponse(1,"fail:callback or intent is null.","");
		} else {
			String callbackName = callObj.toString();
			String valueStr = valueObj.toString();
			String gameId = parseGameIdFromURL(parseURLFromIntentStr(valueStr));
			if (TextUtils.isEmpty(gameId) || (!isValidGameId(gameId))) {
				responseContent = buildResponse(2, "fail:invalid gameID.","");
			} else {
				Intent intent = new Intent(SpeedDownloadReceiver.SPEEDDOWN_ACTION);
				intent.putExtra("callbackName", callbackName);
				intent.putExtra("gameId", gameId);
				mContext.sendBroadcast(intent);
				responseContent = buildResponse(0, "success.",callbackName);
			}
		}		
		HttpEntity entity = new EntityTemplate(new ContentProducer() {
			public void writeTo(final OutputStream outstream) throws IOException {
				OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
				writer.write(responseContent);
				writer.flush();
			}
		});
		response.setHeader("Content-Type", "application/jsonp");
//		response.setHeader("Content-Type", "text/html");
		response.setEntity(entity);
	}
	
	private String parseURLFromIntentStr(String intentStr) {
		String url = null;
		if (!TextUtils.isEmpty(intentStr)) {
			String division = ".apk";
//			intentStr = "http://dl.m.duoku.com/GameSearch20_"+generateGameId()+"_60.apk#Intent;"
//					+ "action=com.baidu.appsearch.action.HIGHSPEED;launchFlags="
//					+ "0x10000000;component=com.baidu.appsearch/.UrlHandlerActivity;end";
			int index = intentStr.indexOf(division);
			url = intentStr.substring(0, index+division.length());			
		}
		return url;
	}

	private String parseGameIdFromURL(String downloadURL) {
		String gameId = null;
		if (!TextUtils.isEmpty(downloadURL)) {
			String regex = "GameSearch\\d+_(\\d+)_60.apk$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(downloadURL);
			if (matcher.find()) {
				gameId = matcher.group(1);
			}
		}
		return gameId;
	}

	/**
	 * build response content.
	 * @param errorCode errorcode, 0=success.
	 * @param errorMsg errorMessage.
	 * @param callbackName callbackName.
	 * @return
	 */
	private String buildResponse(int errorCode,String errorMsg,String callbackName) {
		String responseContent = callbackName+"({\"error\":"+ errorCode
				+",\"message\":\""+ errorMsg +"\"})";
		return responseContent;
	}

	private boolean isValidGameId(String gameId) {
		return Pattern.compile("\\d+").matcher(gameId).matches();
	}
	
	private int generateGameId() {
		Random random = new Random();
		return (int)Math.floor(random.nextFloat()*10000 + 50000);
	}
	
	private static Map<String, String> getParamsFromUrl(String url) {
		Map<String, String> map = null;
		if (url != null && url.indexOf('?') != -1) {
			map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
		}
		if (map == null) {
			map = new HashMap<String, String>();
		}
		return map;
	}

	/**
	 * 从URL中提取所有的参数。
	 * 
	 * @param query URL地址
	 * @return 参数映射
	 */
	public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();
		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}
		return result;
	}
}