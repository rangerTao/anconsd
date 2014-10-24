package com.ranger.lpa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Formatter;

public class StringUtil {

	public static String formatTimes(long times) {
		int d = (int) (times / 10000);
		if (d > 0) {
			return (d + "万+次下载");
		} else {
			return times + "次下载";
		}
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String convertEscapeString(String string) {
		String r = string.replaceAll("\\[BR\\]", "\n").replaceAll("\\[&nbsp;\\]", "    ").replace("&nbsp;", "    ");
		return r;
	}

	/**
	 * 
	 * @param old
	 * @return
	 */
	public static String trim(String old) {
		String replaceAll = old.replaceAll(" ", " ").replaceAll("　", " ");
		return replaceAll.trim();
	}

	/**
	 * 去除字符串前后的空格，包括全角与半角
	 * 
	 * @param str
	 *            要去掉的空格的内容
	 * @return 去掉空格后的内容
	 */
	public static String trimAllSpace(String str) {
		if (str == null) {
			return str;
		}
		return str.replaceAll("^[\\s　]*|[\\s　]*$", "");
	}

	/**
	 * compress data(gzip)
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] gzip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/***
	 * unzip data from GZip
	 * 
	 * @param data
	 */
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
		return b;
	}

	public static String InputStreamToString(InputStream is, boolean isGzip) {
		String s = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// os = new BufferedOutputStream();
			byte[] buff = new byte[1024];
			int readed = -1;
			while ((readed = is.read(buff)) != -1) {
				baos.write(buff, 0, readed);
			}
			byte[] result = null;
			if (isGzip) {
				result = unGZip(baos.toByteArray());
			} else {
				result = baos.toByteArray();
			}
			if (result == null) {
				return null;
			}
			s = new String(result, "UTF-8");
			is.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static int getColor(String colorStr) {
		return Color.rgb(Integer.valueOf(colorStr.substring(0, 2), 16),//
				Integer.valueOf(colorStr.substring(2, 4), 16),//
				Integer.valueOf(colorStr.substring(4, 6), 16));
	}

	public static boolean checkValidUserName(String username) {
		// 4-14位字母或数字
		String usernamePattern = "^[a-z0-9A-Z]{4,14}$";
		Pattern pattern = Pattern.compile(usernamePattern);
		boolean ret = pattern.matcher(username).matches();

		return ret;
	}

	public static boolean checkValidUserName2(String username) {
		// 用户名不能1开头11位数字

		String phonenumPattern = "^1\\d{10}$";
		Pattern pattern = Pattern.compile(phonenumPattern);
		boolean ret = pattern.matcher(username).matches();

		return !ret;
	}

	public static boolean checkValidPassword(String password) {
		// 6-16位字母或数字
		String pwdPattern = "^[a-z0-9A-Z]{6,16}$";
		Pattern pattern = Pattern.compile(pwdPattern);
		boolean ret = pattern.matcher(password).matches();

		return ret;
	}

	public static boolean checkValidNickName(String nickname) {
		// 2-12位字母、数字或汉字，不包含非法字符#&*！!、/\"@,，。.%<>:：【】空格

		// String chinese = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
		String nicknamePattern = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2Da-z0-9A-Z]{2,12}$";
		Pattern pattern = Pattern.compile(nicknamePattern);
		boolean ret = pattern.matcher(nickname).matches();

		if (!ret) {
			return ret;
		}

		String invalidCharsPattern = "[#&*！!、/\"@,，。.%<>:：【】 ]";
		pattern = Pattern.compile(invalidCharsPattern);
		ret = pattern.matcher(nickname).matches();

		return !ret;
	}

	public static boolean checkValidPhoneNum(String phonenum) {
		// 13,14,15,18开头11位数字

		String phonenumPattern = "^1[3458]\\d{9}$";
		Pattern pattern = Pattern.compile(phonenumPattern);
		boolean ret = pattern.matcher(phonenum).matches();

		return ret;
	}

	public static boolean checkValidMailaddress(String mailaddress) {

		String mailPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern pattern = Pattern.compile(mailPattern);
		boolean ret = pattern.matcher(mailaddress).matches();

		return ret;
	}

	public static boolean checkValidVerifyCode(String verifyCode) {
		// 4位数字

		String verifyCodePattern = "^\\d{4}$";
		Pattern pattern = Pattern.compile(verifyCodePattern);
		boolean ret = pattern.matcher(verifyCode).matches();

		return ret;
	}

	public static boolean checkValidFeedbackContent(String content) {

		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(content);
		String tmp = m.replaceAll("");

		boolean ret = true;

		if (tmp.length() <= 0) {
			ret = false;
		}
		return ret;
	}

	public static String getDisplaySize(String sizelong) {
		String result = sizelong;
		try {
			long size_byte = Long.parseLong(sizelong);
			if (size_byte < 1024) {
				result = sizelong + "B";
			} else if (size_byte < 1024 * 1024) {
				result = ((int) ((size_byte / 1024.0f) * 100)) / 100.0f + "KB";
			} else if (size_byte < 1024 * 1024 * 1024) {
				result = ((int) ((size_byte / (1024 * 1024.0f)) * 100)) / 100.0f + "MB";
			} else if (size_byte < 1024 * 1024 * 1024 * 1024) {
				result = ((int) ((size_byte / (1024 * 1024 * 1024.0f)) * 100)) / 100.0f + "GB";
			}
		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
		return result;
	}

	public static String getDisplaySize(long sizelong) {
		String result = null;

		if (sizelong < 1024) {
			result = sizelong + "B";
		} else if (sizelong < 1024 * 1024) {
			result = ((int) ((sizelong / 1024.0f) * 100)) / 100.0f + "KB";
		} else if (sizelong < 1024 * 1024 * 1024) {
			result = ((int) ((sizelong / (1024 * 1024.0f)) * 100)) / 100.0f + "MB";
		} else if (sizelong < 1024 * 1024 * 1024 * 1024) {
			result = ((int) ((sizelong / (1024 * 1024 * 1024.0f)) * 100)) / 100.0f + "GB";
		}

		return result;
	}

	public static String getDisplayDownloadtimes(String downloadtimes) {
		String result = downloadtimes;

		try {
			long times = Long.parseLong(downloadtimes);
			if (times < 10000) {

			} else if (times < 100000000) {
				result = (times / 10000) + "万+";
			} else {
				result = (times / 100000000) + "亿+";
			}

		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}

		return result;
	}

	public static int parseInt(String value) {
		int ret = 0;
		try {
			ret = Integer.parseInt(value);
		} catch (Exception e) {
		}

		return ret;
	}

	public static Integer valueOf(String value) {
		Integer ret = 0;
		try {
			ret = Integer.valueOf(value);
		} catch (Exception e) {
		}

		return ret;
	}

	/***
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288 || c[i] == 9 || c[i] == 32) {
				// c[i] = (char) 32;
				c[i] = '\u3000';
				continue;
			}
			if (c[i] > 32 && c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * * 去除特殊字符或将所有中文标号替换为英文标号
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static boolean isEmpty(String string) {
		if (string == null || string.length() <= 0) {
			return true;
		}

		return false;
	}

	/**
	 * 根据毫秒值，获取时间格式
	 * 
	 * @param time
	 * @return
	 */
	public static String getFormattedTimeByMillseconds(long time) {

		String pattern = "00";
		DecimalFormat df = new DecimalFormat(pattern);
		String res = "00:00:00";

		if (time < 1) {
			return res;
		}

		long totalSeconds = time / 1000;
		long seconds = totalSeconds % 60;

		totalSeconds /= 60;

		long mins = totalSeconds % 60;

		totalSeconds /= 60;

		long hours = totalSeconds % 60;

		return df.format(hours) + ":" + df.format(mins) + ":" + df.format(seconds);
	}

	/**
	 * 根据Uri获取文件绝对路径
	 * 
	 * @param act
	 * @param suri
	 * @return
	 */
	public static String uriToUrl(Activity act, String suri) {

		Uri uri = Uri.parse(suri);

		String[] proj = { MediaStore.Images.Media.DATA };

		ContentResolver cr = act.getContentResolver();

		Cursor actualimagecursor = cr.query(uri, proj, null, null, null);

		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		actualimagecursor.moveToFirst();

		String img_path = actualimagecursor.getString(actual_image_column_index);

		File file = new File(img_path);

		if (file.exists()) {
			return file.getAbsolutePath();
		}

		return "";

	}
	
    public static String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>24)&0xff)));
        return sb.toString();
    }

}
