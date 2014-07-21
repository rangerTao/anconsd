package com.ranger.bmaterials.tools;

import java.io.DataOutputStream;
import java.io.File;

/**
 * 
 * @author HuangChangQiang
 *
 */
public final class RootUtil {
	private RootUtil() {
	}

	/** 通过是否存在su文件 判断是否root */
	public static boolean checkRoot() {
		boolean isRoot = false;

		File f = null;
		String sys = System.getenv("PATH");
		String kSuSearchPaths[] = sys.split(":");
		for (int i = 0; i < kSuSearchPaths.length; i++) {
			f = new File(kSuSearchPaths[i] + "/su");
			if (f != null && f.exists()) {
				isRoot = true;
				break;
			}
		}

		return isRoot;
	}

	/** 申请root 该方法也能判断是否root 但会弹窗或弹出提示文字 需要异步否则线程阻塞 */
	public static boolean requestRoot() throws Exception {
		Process p = null;
		DataOutputStream dos = null;
		try {
			p = Runtime.getRuntime().exec("su");

			dos = new DataOutputStream(p.getOutputStream());

			dos.writeBytes("exit\n");
			dos.flush();

			return p.waitFor() == 0;
		} finally {
			if (dos != null)
				dos.close();

			if (p != null)
				p.destroy();
		}
	}
}
