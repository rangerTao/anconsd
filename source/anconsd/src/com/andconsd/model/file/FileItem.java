package com.andconsd.model.file;

import java.net.URLEncoder;

public class FileItem {
	private String name;
	private String pathOnServer;

	private Boolean isFile;
	private String link;
	private int index;

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathOnServer() {
		return pathOnServer;
	}

	public void setPathOnServer(String pathOnServer) {
		this.pathOnServer = pathOnServer;
	}

	public Boolean getIsFile() {
		return isFile;
	}

	public void setIsFile(Boolean isFile) {
		this.isFile = isFile;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {

		StringBuffer stringBuffer = new StringBuffer();
		if (index % 4 == 1) {
			stringBuffer.append("<tr>");
		}
		if(getName().endsWith(".mp4") || getName().endsWith(".3gp")){
			stringBuffer = new StringBuffer("<td><embed src=\" " + URLEncoder.encode(getLink()) +  " \" autostart=false width=\"320\" height=\"240\"></embed></br>");
			stringBuffer.append("<input type=\"checkbox\" name=\"files\" value=\""
					+ getName() + "\"> " + getName() + " </input></td>");
		}else{
			stringBuffer = new StringBuffer("<td><a href=\"" + this.getLink()
					+ "\"><img src =\"" + this.getLink() + "\" alt="
					+ this.getName() + " width=\"320\" height=\"240\"></a><br/>");
			stringBuffer.append("<input type=\"checkbox\" name=\"files\" value=\""
					+ getName() + "\"> " + getName() + " </input></td>");
		}
		
		if (index % 4 == 0) {
			stringBuffer.append("</tr>");
		}
		return stringBuffer.toString();
	}
}
