package com.ranger.bmaterials.ui;

public class MineMsgItemInfo {
    public String msgID;
    public String msgTitle;
    public String msgTime;
    public boolean unreadMsg;
    public String iconUrl;
    private boolean checked;
    public MineMsgActivity observer = null;
    
    public void setChecked(boolean checked) {
    	this.checked = checked;
    	
    	if (observer != null) {
    	    observer.itemChecked(checked);
    	}
    }
    
    public boolean getChecked() {
    	return this.checked;
    }
    
	public MineMsgItemInfo() {
        this.msgID = "";
        this.msgTitle = ""; 
        this.msgTime = "";
        this.unreadMsg = true;
        this.iconUrl = "";
        this.checked = false;
	}
}
