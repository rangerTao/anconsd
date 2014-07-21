package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.SearchResult;

/**
 * 
* @Description: TODO
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年6月9日 下午2:39:11 
* @version V
*
 */
public class RecommandKeyword extends BaseResult{

	public SearchResult.SearchItem recomGame;
	
	private ArrayList<String> keywords = new ArrayList<String>();
	
	public void addKeyword(String key){
		keywords.add(key);
	}
	
	public ArrayList<String> getKeywords(){
		return keywords;
	}
	
	public void setGameInfo(String json){
		recomGame = new SearchResult.SearchItem();
	}
	
	public SearchResult.SearchItem getGameInfo(){
		return recomGame;
	}
}
