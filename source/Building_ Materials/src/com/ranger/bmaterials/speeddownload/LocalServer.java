package com.ranger.bmaterials.speeddownload;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;

public class LocalServer extends Thread{
	
	public final static int port = 6791;
	public Context ctx;
	private boolean RUN = false;
	
	public LocalServer(Context context, String threadName){
		this.ctx = context;
		this.setName(threadName);
	}
	
	@Override
	public void run(){
		super.run();
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			RUN = true;
			while(RUN){
				try{
					socket = server.accept();
					socket.setSoTimeout(200);
					Thread workThread = new HttpWorkThread(ctx, socket, "httpWorkThread");
					workThread.start();
				} catch (InterruptedIOException eiox){
					eiox.printStackTrace();
				}
			}
			server.close();			
	    } catch (IOException e) {
	    	e.printStackTrace();
		} finally {
	    	try{
		    	if(socket != null)  {
		    		socket.close();
		    	}
		    	server.close();
	    	} catch(Exception ex){
	    		ex.printStackTrace();
	    	}
		}
	}

	public void stopServer() {
		RUN = false;
	}
}