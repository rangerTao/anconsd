package com.ranger.bmaterials.mode;

import java.util.ArrayList;

public class GameGuideDetailInfo extends GameGuideInfo {
	private ArrayList<GameGuideContent> list_guide_content;
	
	public class GameGuideContent{
		public String content;
		public String picurl;
	}

	public ArrayList<GameGuideContent> getList_guide_content() {
		return list_guide_content;
	}

	public void setList_guide_content(ArrayList<GameGuideContent> list_guide_content) {
		this.list_guide_content = list_guide_content;
	}
	
	
}
