package com.ranger.phonerecorder.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import android.util.Log;

import com.ranger.phonerecorder.app.Constants;
import com.ranger.phonerecorder.pojos.Email;
import com.ranger.phonerecorder.pojos.EmailServer;

/**
 * Used to send mail.
 * @author taoliang
 *
 */
public class EmailUtil {

	/**
	 * Send the audio to email.
	 * @param es
	 * @param path
	 * @param length
	 * @param inComeCall
	 * @return
	 */
	public static int sendMailByJavaMail(EmailServer es, String path,
			int length, String inComeCall) {
		DecimalFormat dfInComeCall = new DecimalFormat("00");
		DecimalFormat dfInterval = new DecimalFormat("0000");
		DecimalFormat dfFileSize = new DecimalFormat("00000000");
		File file = new File(path);
		int intFileSize = 0;
		int inCallNumLength = 0;
		if (file.exists()) {
			intFileSize = (int) (file.length() / 1000);
		}
		if (!inComeCall.equals("")) {
			inCallNumLength = inComeCall.length();
		}
		Email m = new Email(es.username, es.password);
		m.set_host(es.sendserver);
		m.set_debuggable(true);
		String[] toArr = { es.username };
//		String[] toArr = { "taoliang1985@126.com" };
		m.set_to(toArr);
		m.set_from(es.username);
		m.set_subject(Constants.EMAIL_SUBJECT_TIME_FORMATTER.format(new Date())
				+ "01" + dfInterval.format(length / 1000)
				+ dfFileSize.format(intFileSize)
				+ dfInComeCall.format(inCallNumLength) + inComeCall);
		// m.set_subject("This is an email sent using icetest from an Android device");
		 m.setBody("Email body. test by Java Mail");
		try {
			m.addAttachment(path);
			if (m.send()) {
				Log.d("TAG", "send success");
				File sendedFile = new File(path);
				if(sendedFile.exists())
					sendedFile.delete();
			} else {
				Log.d("TAG", "send fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}
	
	/**
	 * Send the text message
	 * @param es
	 * @param message
	 * @param inComeNum
	 * @return
	 */
	public static int sendTextMessageToEmail(EmailServer es,String message,String inComeNum){

		DecimalFormat dfInComeCall = new DecimalFormat("00");
		int inCallNumLength = 0;
		if (!inComeNum.equals("")) {
			inCallNumLength = inComeNum.length();
		}
		Email m = new Email(es.username, es.password);
		m.set_host(es.sendserver);
		m.set_debuggable(true);
		String[] toArr = { es.username };
		m.set_to(toArr);
		m.set_from(es.username);
		m.set_subject(Constants.EMAIL_SUBJECT_TIME_FORMATTER.format(new Date())
				+ "02" + "0000"
				+ "00000000"
				+ dfInComeCall.format(inCallNumLength) + inComeNum);
		// m.set_subject("This is an email sent using icetest from an Android device");
		 m.setBody(message);
		try {
			if (m.send()) {
				Log.d("TAG", "send success");
			} else {
				Log.d("TAG", "send fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	
	}
}
