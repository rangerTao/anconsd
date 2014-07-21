package com.ranger.bmaterials.mode;

import java.util.List;
import java.util.Random;

import com.ranger.bmaterials.netresponse.BaseResult;

public class KeywordsList extends BaseResult {

    private static int index = 0;

    private static KeywordsList _instance;

    private KeywordsList(){

    }

    public static synchronized KeywordsList getInstance(){
        if(_instance == null){
            _instance = new KeywordsList();
        }

        return _instance;
    }

    private List<String> recomKeywords;

	private List<String> keywords ;

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

    public List<String> getRecomKeywords() {
        return recomKeywords;
    }

    public void setRecomKeywords(List<String> recomKeywords) {
        this.recomKeywords = recomKeywords;
    }

    //返回一个随机的推荐key
    public String getRandomRecomKeyword(){
        if(recomKeywords != null && recomKeywords.size() > 0) {
            return recomKeywords.get(index ++ % recomKeywords.size());
        }

        return "";
    }
}
