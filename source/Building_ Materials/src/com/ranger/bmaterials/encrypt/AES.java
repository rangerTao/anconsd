package com.ranger.bmaterials.encrypt;

import com.ranger.bmaterials.app.MineProfile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java��ES���瑙ｅ�锛���ㄤ�PHP��ES���瑙ｅ�(PHP璇��寮����CRYPT_3DES绠����CRYPT_MODE_ECB妯″�
 * �� PKCS5濉���瑰�)
 * 
 */
public class AES {

    public static void main(String args[]){
        String test = "{\"username\":\"bilige\",\"password\":\"123456\"}";

        System.out.println(AES.getInstance().decrypt("WaSSzC5vZ4uE5UtSC8R+Ey7SVi2+NLTd2YVuIIUoNDsUZY8tbPx3Xr1hYgRA9tc2"));
    }

	private static AES2 mInstance;
	private SecretKey sSecretKey = null;// key瀵硅薄
	private Cipher sCipher = null; // 绉�拷���瀵硅薄Cipher
	private String sKeyString = "1934567820bacDQF";// 瀵��

	public synchronized static AES2 getInstance() {
		if (mInstance == null) {
			mInstance = new AES2();
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
			sCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // AES/CBC/NoPadding
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

		String newResult = "";

        try{
            newResult = mInstance.encrypt(message);
        }catch (Exception e){
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
			result = mInstance.decrypt(message);
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
