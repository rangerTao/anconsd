package com.andconsd.configure;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.andconsd.R;

import android.app.Activity;

public class Configurator {

	private String uiTemplate;

	public String getUiTemplate() {
		return uiTemplate;
	}

	public void setUiTemplate(String uiTemplate) {
		this.uiTemplate = uiTemplate;
	}

	public Configurator(Activity activity) {
		try {
			setUiTemplate(getUiTemplate(activity));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getUiTemplate(Activity activity) throws IOException {
		InputStream inputStream = activity.getApplicationContext()
				.getResources().openRawResource(R.raw.uitemplate);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				inputStream);
		byte data[] = new byte[1024];
		int num = 0;
		StringBuffer stringBuffer = new StringBuffer();
		while ((num = bufferedInputStream.read(data)) > 0) {
			stringBuffer.append(new String(data, 0, num));
		}
		bufferedInputStream.close();
		return stringBuffer.toString();
	}
}
