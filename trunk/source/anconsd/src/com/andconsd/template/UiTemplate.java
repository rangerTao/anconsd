package com.andconsd.template;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.andconsd.R;

import android.app.Activity;


public class UiTemplate {
	public static String uiTemplate;
	private Activity activity;

	public UiTemplate(Activity activity) {
		this.activity = activity;
		uiTemplate = initUiTemplate();
	}

	public String initUiTemplate() {
		if (uiTemplate == null) {

			InputStream inputStream = activity.getApplicationContext()
					.getResources().openRawResource(R.raw.uitemplate);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					inputStream);
			byte data[] = new byte[1024];
			int num = 0;
			StringBuffer stringBuffer = new StringBuffer();
			try {
				while ((num = bufferedInputStream.read(data)) > 0) {
					stringBuffer.append(new String(data, 0, num));
				}
				bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			uiTemplate = stringBuffer.toString();
		}
		return uiTemplate;
	}

	public static String generateHtml(String dir, String tds)
			throws URISyntaxException, IOException {
		return uiTemplate.replaceAll("\\$\\{dir\\}", dir).replaceAll(
				"\\$\\{content\\}", tds).replaceAll("\\$\\{target\\}", "test").replaceAll("\\$\\{upload\\}", "\u4e0a\u4f20").replaceAll("\\$\\{delete\\}", "\u5220\u9664\u9009\u4e2d");

	}

}
