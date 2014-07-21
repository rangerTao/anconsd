package com.ranger.bmaterials.speeddownload;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;

public class HomeCommandHandler implements HttpRequestHandler {

	private Context mContext = null;
	private String host = "localhost";
	
	public HomeCommandHandler(Context context) {
		this.mContext = context;
	}
	
	@Override
	public void handle(HttpRequest req, HttpResponse resp, HttpContext arg2)
			throws HttpException, IOException {
		this.host = req.getFirstHeader("Host").getValue();
		HttpEntity entity = new EntityTemplate(new ContentProducer() {
			public void writeTo(final OutputStream outstream) throws IOException {
				OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
				String resp = "<html><head></head>" +
						"<body><center><h1>Duoku GameSearch<h1></center></body></html>";
				writer.write(resp);
				writer.flush();
			}
		});
		resp.setHeader("Content-Type", "text/html");
		resp.setEntity(entity);
	}
}