package com.ranger.bmaterials.tools;

import java.util.ArrayList;

import android.text.TextUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.ranger.bmaterials.bitmap.ImageLoaderHelper;

import android.util.Log;
//Could not find class 'net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat', referenced from method com.guoku.util.PinyinUtil.<clinit>

public class PinyinUtil {

//	static HanyuPinyinOutputFormat format ;
//	static  {
//		format = new HanyuPinyinOutputFormat();
//		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
//		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 没有音调数字
//		format.setVCharType(HanyuPinyinVCharType.WITH_V);// u显示
//	}
	public static String getPingYin(String inputString) {

		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();

		String output = "";

		try {

			for (int i = 0; i < input.length; i++) {

				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {

					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);

					output += temp[0];

				} else

					output += java.lang.Character.toString(input[i]);

			}

		} catch (BadHanyuPinyinOutputFormatCombination e) {

			e.printStackTrace();

		}

		return output;

	}


	    /**
	     * 获取汉字对应的拼音，如果是英文字母则直接返回原内容。
	     * 
	     * @param input
	     *            输入的内容
	     * @return 返回对应的拼音
	     */
	    private static String getPinYin(String input) {
	        StringBuilder sb = new StringBuilder();
	        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(input);

	        if (tokens != null && tokens.size() > 0) {
	            for (HanziToPinyin.Token token : tokens) {
	                if (HanziToPinyin.Token.PINYIN == token.type) {
	                	//有些汉字通过HanziToPinyin类无法得到拼音，这里判断条件是如果通过HanziToPinyin无法得到拼音，就通过pinyin4j-2.5.0.jar再次获取拼音
	                	if(TextUtils.isEmpty(token.target)){
	                		sb.append(getPingYin(token.source));
	                	}else{
	                		sb.append(token.target);
	                	}
	                } else {
	                    sb.append(token.source);
	                }
	            }
	        }else{
	        	sb.append(getPingYin(input));
	        }
	        return sb.toString().toLowerCase();
	    }

	    
	/**
	 * 获得拼音
	 * 
	 * @param chinese
	 * @param format
	 *            为空时，使用默认格式
	 * @return
	 * @throws Exception
	 */
	public static String getPinyin(String chinese) {
		StringBuffer sb = new StringBuffer();
		int length = chinese.length();
		if(length > 3){
			chinese = chinese.substring(0, 3);
		}
		length = chinese.length();
		for (int i = 0; i < length; i++) {
			try {
				String c = getPinYin(String.valueOf(chinese.charAt(i)));
				char charAt = c.charAt(0);
				if(charAt>'z' || charAt < 'a'){
					sb.append("#");
				}else{
					sb.append(charAt);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		if(sb.length() == 0){
			sb.append("#");
		}
		
		//Log.e("MYPINYIN", "Get pinyin: chinese "+chinese+" pinyin:"+sb.toString());
		return sb.toString();
		
		/*//clearCache();
		if(chinese.length() >3){
			chinese = chinese.substring(0, 3);
		}
		String pinyin = "";
		try {
			pinyin = get(chinese);
		} catch (Exception e) {
			clearCache();
			e.printStackTrace();
			Log.e("MYPINYIN", "Get pinyin Error chinese "+chinese);
			pinyin = "#";
		}
		//clearCache();
		return pinyin;*/
	}
	
	private static void clearCache(){
		ImageLoaderHelper.clearCache();
		System.gc();
	}
	
	
	/*private static String get(String chinese){
		String pinyin = "";
		if(chinese.length() >3){
			chinese = chinese.substring(0, 3);
		}
		char[] chars = chinese.toCharArray();
		try {
			for (int i = 0; i < chars.length; i++) {
				String[] pinYin;
				
					pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],format);
					// 当转换不是中文字符时,返回null
					if (pinYin != null) {
						pinyin = pinyin + pinYin[0].charAt(0);//汉字首字母
					} else {
						pinyin += chars[i];
					}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
			
		}
		try {
			pinyin = pinyin.toLowerCase();
			if(pinyin.charAt(0) < 'a' || pinyin.charAt(0) >'z'){
				pinyin = "#"+pinyin ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			pinyin =  "#";
		}
		if("#".equals(pinyin)){
			Log.e("MYPINYIN", "chinese "+chinese+"'s pinyin:"+pinyin +" "+Thread.currentThread().getName());
		}else{
			Log.i("MYPINYIN", "chinese "+chinese+"'s pinyin:"+pinyin+" "+Thread.currentThread().getName());
		}
		return pinyin;
	}
	*/

//	public static void main(String[] args) throws Exception {
//		String str = "绿色单田�?;
//		System.out.println(getPinyin(str, null));
//	}
}
