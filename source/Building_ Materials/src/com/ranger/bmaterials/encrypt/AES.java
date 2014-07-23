package com.ranger.bmaterials.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java��ES���瑙ｅ�锛���ㄤ�PHP��ES���瑙ｅ�(PHP璇��寮����CRYPT_3DES绠����CRYPT_MODE_ECB妯″�
 * �� PKCS5濉���瑰�)
 * 
 */
public class AES {
	
	static{
		System.loadLibrary("AESKey");
	}
	
	private static AES mInstance;
	private SecretKey sSecretKey = null;// key瀵硅薄
	private Cipher sCipher = null; // 绉�拷���瀵硅薄Cipher
	private String sKeyString = "1934567820bacDQF";// 瀵��

	public synchronized static AES getInstance() {
		if (mInstance == null) {
			mInstance = new AES();
		}

		return mInstance;
	}

	private AES() {
		try {
			/* AES绠�� */
			sSecretKey = new SecretKeySpec(sKeyString.getBytes(), "AES");// �峰�瀵��
			/*
			 * �峰�涓�釜绉�拷���绫�ipher锛�ESede-��ES绠��锛�CB���瀵�ā寮��PKCS5Padding��～���寮
			 * �
			 */
			sCipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // AES/CBC/NoPadding
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���
	 * 
	 * @param message
	 * @return
	 */
	public synchronized String aesEncrypt(String message) {
		String result = ""; // DES���瀛��涓�
		String newResult = "";// �绘��㈣�绗�����瀵��绗�覆
		try {
			sCipher.init(Cipher.ENCRYPT_MODE, sSecretKey); // 璁剧疆宸ヤ�妯″�涓哄�瀵�ā寮��缁��瀵��

			byte[] resultBytes = sCipher.doFinal(message.getBytes("UTF-8")); // 姝ｅ��ц�������
			result = new String(Base64.encode(resultBytes, Base64.DEFAULT));// 杩��BASE64缂��
			newResult = filter(result); // �绘����涓蹭腑���琛��
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newResult;
	}

	/**
	 * 瑙ｅ�
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public synchronized String aesDecrypt(String message) {
		String result = "";
		try {
			byte[] messageBytes = Base64.decode(message, Base64.DEFAULT); // 杩��BASE64缂��
			sCipher.init(Cipher.DECRYPT_MODE, sSecretKey); // 璁剧疆宸ヤ�妯″�涓鸿В瀵�ā寮��缁��瀵��
			byte[] resultBytes = sCipher.doFinal(messageBytes);// 姝ｅ��ц�瑙ｅ����
			result = new String(resultBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * �绘����瀛��涓叉�琛��
	 * 
	 * @param str
	 * @return
	 */
	public synchronized static String filter(String str) {
		String output = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			int asc = str.charAt(i);
			if (asc != 10 && asc != 13) {
				sb.append(str.subSequence(i, i + 1));
			}
		}
		output = new String(sb);
		return output;
	}

}
