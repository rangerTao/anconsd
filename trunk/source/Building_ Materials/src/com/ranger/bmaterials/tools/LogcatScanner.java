package com.ranger.bmaterials.tools;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
public class LogcatScanner {
	
	private static AndroidLogcatScanner scannerThead;
	public final static void startScanLogcatInfo(LogcatObserver observer) {
		if (scannerThead == null) {
			scannerThead = new AndroidLogcatScanner(observer);
			scannerThead.start();
			//LogOut.out(LogcatScanner.class, "scannerThread.start()");
		}
	}
	public static interface LogcatObserver {  
		    public void handleNewLine(String line);  
		}  

	
	/*private void test(){
		btnScannerLogcat.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				// 开启Logcat流监听
				LogcatScanner.startScanLogcatInfo(MainActivity.this);
			}
		});
		Button btnUninstallMe = (Button) findViewById(R.id.btnUninstallMe);
		btnUninstallMe.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				// 调用应用程序信息
				Intent intent = new Intent(Intent.ACTION_VIEW);
				// com.android.settings/.InstalledAppDetails
				intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
				intent.putExtra("pkg", "lab.sodino.uninstallhint");
				startActivity(intent);
			}
		});
		txtInfo = (TextView) findViewById(R.id.txtInfo);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				txtInfo.append(String.valueOf(msg.obj) + "/n");
			}
		};

	}*/
	
/*	public void handleNewLine(String info) {
		Message msg = new Message();
		msg.obj = info;
		handler.sendMessage(msg);
		if (info.contains("android.intent.action.DELETE") && info.contains(getPackageName())) {
			// 启动删除提示
			Intent intent = new Intent();
			intent.setClass(this, UninstallWarningActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}*/

	
	static class AndroidLogcatScanner extends Thread {
		
		private LogcatObserver observer;
		public AndroidLogcatScanner(LogcatObserver observer) {
			this.observer = observer;
		}
		public void run() {
			String[] cmds = { "logcat", "-c" };
			String shellCmd = "logcat";
			Process process = null;
			InputStream is = null;
			DataInputStream dis = null;
			String line = "";
			Runtime runtime = Runtime.getRuntime();
			try {
				observer.handleNewLine(line);
				int waitValue;
				waitValue = runtime.exec(cmds).waitFor();
				observer.handleNewLine("waitValue=" + waitValue + "/n Has do Clear logcat cache.");
				process = runtime.exec(shellCmd);
				is = process.getInputStream();
				dis = new DataInputStream(is);
				while ((line = dis.readLine()) != null) {
					observer.handleNewLine(line);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException ie) {
				ie.printStackTrace();
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
					if (is != null) {
						is.close();
					}
					if (process != null) {
						process.destroy();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}